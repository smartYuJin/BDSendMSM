package com.bw.bd.activity;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bw.bd.send.R;
import com.bw.bd.send.bean.BDConstants;
import com.bw.bd.utils.SerialPortOption;
import com.bw.bd.utils.SerialPortUtils;

public class MainActivity extends FragmentActivity {
    public static final String TAG = "MainActivity";
    /*********************************************************/
    public File device = new File("/dev/ttyMSM0");
    public int baudrate = 9600;
    public int flags = 0;
    /*********************************************************/
    // 发送$BDPOW的间隔时间：s
    private static int sendPowCommandInterval = 5;
    private ViewPager viewPager;//页卡内容
    private ImageView imageView;// 动画图片
    private TextView MessageTextView, InboxTextView, SingleTextView;
    private ArrayList<Fragment> fragmentList=new ArrayList<Fragment>();// Tab页面列表
    private int offset = 0;// 动画图片偏移量
    private int currIndex = 0;// 当前页卡编号
    private int bmpW; // 动画图片宽度
//    private View Messageview, Inboxview, Singleview;//各个页卡
    public Context mContext;
    public LayoutInflater minflater;
    
    public InboxFragment mInboxFragment;
    public SendMessageFragment mSendMessageFragment;
    public SingleFragment mSingleFragment;
    
    public SerialPortOption mSerialPortOption;
    public SerialPortObserver mSerialPortObserver;
    public boolean isSerialClosed = false;
    public InputStream mInputStream;
    public OutputStream mOutputStream;
    private boolean isToStopReadData = false;
    public boolean isOpenDeviceSuccess = false;
    public SerialPortUtils mSerialPortUtils = null;
    public ReadSerialPortThread mReadThread;
    public CheckSingleThread mCheckSingleThread;
    public FragmentManager mFragmentManager;
    public FragmentTransaction transaction;
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what) {
            case BDConstants.CONSTANT_SERIALPORTCHANGED:
                mSerialPortObserver.notifyObservers(msg);
                break;
            case BDConstants.CONSTANT_STOPREADSERIALPROT:
                Toast.makeText(mContext, "读串口线程已停止", Toast.LENGTH_SHORT).show();
                break;
            case BDConstants.CONSTANT_BDSIM:
            	mSerialPortObserver.notifyObservers(msg);
            	break;
            case BDConstants.CONSTANT_BDPOW:
            	mSerialPortObserver.notifyObservers(msg);
            	break;
            case BDConstants.CONSTANT_BDSND:
            	mSerialPortObserver.notifyObservers(msg);
            	break;
            case BDConstants.CONSTANT_BDREC:
            	mSerialPortObserver.notifyObservers(msg);
            	break;
            case BDConstants.CONSTANT_COUNTTIME:
            	break;
                default:
            }
        }
        
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        InitImageView();
        InitTextView();
        try {
        	mSerialPortOption = SerialPortOption.getInstance(device, baudrate, flags);
        	isOpenDeviceSuccess = mSerialPortOption.openSerialPort();
        } catch (SecurityException e) {
        	Toast.makeText(mContext, "打开串口失败", Toast.LENGTH_SHORT).show();
        	Log.i(TAG, "---> 串口打开失败");
        	e.printStackTrace();
        }
        if (isOpenDeviceSuccess) {
        	isSerialClosed = false;
        	//clearUI();
        	initIOStream();
        	//switchWorkPattern(BDConstants.MIX_WORK_PATTERN, true);
        	// SerialPortUtil.sendCommand(mOutputStream,
        	// "$RDOPEN\r\n");
        	MainActivity.this.startReadData();
        	MainActivity.this.startCheckSingle();
        }
        InitViewPager();
        mSerialPortObserver = new SerialPortObserver();
        mSerialPortObserver.addObserver(mSendMessageFragment);
        mSerialPortObserver.addObserver(mInboxFragment);
        mSerialPortObserver.addObserver(mSingleFragment);
        mSerialPortUtils = SerialPortUtils.getInstance();
        /*firt open app, check SIM card information*/
        SerialPortOption.sendCommand(getmOutputStream(), BDConstants.CMD_QUERY_SIM);
    }

    private void InitViewPager() {
        viewPager=(ViewPager) findViewById(R.id.vPager);
        mFragmentManager = getSupportFragmentManager();
        mSendMessageFragment = new SendMessageFragment(mContext, mOutputStream);
        mInboxFragment = new InboxFragment(mContext, mOutputStream);
        mSingleFragment = new SingleFragment(mContext, mOutputStream);
        fragmentList.add(mSendMessageFragment);
        fragmentList.add(mInboxFragment);
        fragmentList.add(mSingleFragment);
        viewPager.setAdapter(new MyFragmentPagerAdapter(mFragmentManager, fragmentList));
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }
     /**
      *  初始化头标
      */

    private void InitTextView() {
        MessageTextView = (TextView) findViewById(R.id.text1);
        InboxTextView = (TextView) findViewById(R.id.text2);
        SingleTextView = (TextView) findViewById(R.id.text3);

        MessageTextView.setOnClickListener(new MyOnClickListener(0));
        InboxTextView.setOnClickListener(new MyOnClickListener(1));
        SingleTextView.setOnClickListener(new MyOnClickListener(2));
    }

    /**
     * 初始化动画，这个就是页卡滑动时，下面的横线也滑动的效果，在这里需要计算一些数据
     */

    private void InitImageView() {
        imageView= (ImageView) findViewById(R.id.cursor);
        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.reader_item_divider).getWidth();// 获取图片宽度
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;// 获取分辨率宽度
        offset = (screenW / 3 - bmpW) / 2;// 计算偏移量
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        imageView.setImageMatrix(matrix);// 设置动画初始位置
    }
    /** 
     *头标点击监听 3     
     */
    private class MyOnClickListener implements OnClickListener{
        private int index=0;
        public MyOnClickListener(int i){
            index=i;
        }
        public void onClick(View v) {
            viewPager.setCurrentItem(index);
        }
        
    }
    
    public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragmentsList;

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
            super(fm);
            this.fragmentsList = fragments;
        }

        @Override
        public int getCount() {
            return fragmentsList.size();
        }

        @Override
        public Fragment getItem(int arg0) {
            return fragmentsList.get(arg0);
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

    }

    public class MyOnPageChangeListener implements OnPageChangeListener{
        int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
        int two = one * 2;          // 页卡1 -> 页卡3 偏移量
        public void onPageScrollStateChanged(int arg0) {
            Log.i(TAG, "---onPageScrollStateChanged---");
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
            Log.i(TAG, "---onPageScrolled---");
        }

        public void onPageSelected(int arg0) {
            Log.i(TAG, "---onPageSelected---");
            Animation animation = new TranslateAnimation(one*currIndex, one*arg0, 0, 0);//显然这个比较简洁，只有一行代码。
            currIndex = arg0;
            animation.setFillAfter(true);// True:图片停在动画结束位置
            animation.setDuration(300);
            imageView.startAnimation(animation);
            //Toast.makeText(WeiBoActivity.this, "您选择了"+ viewPager.getCurrentItem()+"页卡", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void initIOStream() {
        mOutputStream = mSerialPortOption.getOutputStream();
        mInputStream = mSerialPortOption.getInputStream();
    }

    public void startReadData() {
        Log.i(TAG, "---startReadData---");
        isToStopReadData = false;
        mReadThread = new ReadSerialPortThread();
        mReadThread.start();
    }

    public void stopReadData() {
        Log.i(TAG, "---stopReadData---");
        mInputStream = null;
        mOutputStream = null;
        isToStopReadData = true;
    }

    public void startCheckSingle() {
        Log.i(TAG, "---startCheckSingle----");
        mCheckSingleThread = new CheckSingleThread();
        mCheckSingleThread.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "---onStop---");
        //stopReadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "---onDestroy---");
        stopReadData();
    }

    public InputStream getmInputStream() {
        return mInputStream;
    }

    public void setmInputStream(InputStream mInputStream) {
        this.mInputStream = mInputStream;
    }

    public OutputStream getmOutputStream() {
        return mOutputStream;
    }

    public void setmOutputStream(OutputStream mOutputStream) {
        this.mOutputStream = mOutputStream;
    }
    
    class ReadSerialPortThread extends Thread {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            Log.i(TAG, "isToStopReadData: " + isToStopReadData);
            while (!isToStopReadData) {
                int size;
                try {
                    // byte[] buffer = new byte[64];
                    byte[] buffer = new byte[256];
                    Log.i(TAG, "mInputStream: " + mInputStream == null ? "跳出": "读字节");
                    if (mInputStream == null) {
                        return;
                    }
                    Log.i(TAG, "start read...");
                    size = mInputStream.read(buffer);
                    Log.i(TAG, "stop read...");
                    Log.d(TAG,"Java buffer = "+ SerialPortUtils.bytesToHexString2(buffer));
                    int[] OutLgt = new int[1];
                    ByteBuffer OutMsg = ByteBuffer.allocate(256);
                    for (byte b : buffer) {
                        if (0 == com.bw.bd.jni.SerialPort.BDRecvNative(b,OutMsg.array(), OutLgt)) {
                            Log.d(TAG, "read byte cmd finish.");
                            break;
                        } else {
                            Log.d(TAG, "continue read byte cmd.");
                        }
                    }
                    byte[] temp = OutMsg.array();
                    byte[] temp2 = new byte[OutLgt[0] + 2];
                    for (int i = 0; i < OutLgt[0]; i++) {
                        temp2[i] = temp[i];
                    }
                    String str = String.valueOf(SerialPortUtils.byteToChars(temp2));
                    String[] str2 = str.split("\n");
                    Log.i(TAG, "str2[0]: " + str2[0]);
                    Message msg = mHandler.obtainMessage();
                    if (str2[0].contains("BDSIM")) {
                        msg.what = BDConstants.CONSTANT_BDSIM;
                    }
                    if (str2[0].contains("BDSnd")) {
                        msg.what = BDConstants.CONSTANT_BDSND;
                    }
                    if (str2[0].contains("BDPOW")) {
                        msg.what = BDConstants.CONSTANT_BDPOW;
                    }
                    if (str2[0].contains("BDRev")) {
                        msg.what = BDConstants.CONSTANT_BDREC;
                    }
                    msg.obj = str2[0];
                    mHandler.sendMessage(msg);
                    Log.d(TAG, "Java OutMsg = " + str);
                    Log.d(TAG, "Java OutMsg = " + String.valueOf(SerialPortUtils.bytesToHexString2(OutMsg.array())));
                    Log.d(TAG, "Java OutMsg = " + String.valueOf(SerialPortUtils.byteToChars(OutMsg.array())));
                    Log.d(TAG, "Java OutLgt = " + OutLgt[0]);
                    Log.i(TAG, "\n--------------------------------------------\n");
                    // Log.e("stream::::::", "::" + new String(buffer, 0, 10));
                    byte[] buf = OutMsg.array();
                    Log.i(TAG, "size: " + size);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }

    }
    /**
     * 持续发送检测通讯信号强度和电源打开状态命令的线程
     * @author Administrator
     *
     */
    class CheckSingleThread extends Thread {

        @Override
        public void run() {
            while (mOutputStream != null) {
                Log.i(TAG, "---持续发送检测通讯信号强度态命令的线程--- ");
                //SerialPortOption.sendCommand(getmOutputStream(), BDConstants.CMD_QUERY_SIM);
                SerialPortOption.sendCommand(mOutputStream, BDConstants.REQUEST_POWER_INFO);
                try {
                    Thread.sleep(sendPowCommandInterval * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            super.run();
        }
    }
    
    class SerialPortObserver extends Observable {
        List<Observer> list = new ArrayList<Observer>();

        @Override
        public void addObserver(Observer observer) {
            Log.i(TAG, "---addObserver---");
            list.add(observer);
        }

        @Override
        public void notifyObservers(Object data) {
            Log.i(TAG, "---notifyObservers-Object --");
            for (Observer observer : list) {
                observer.update(this, data);
            }
        }
    }

}
