package com.bw.bd.activity;

import java.io.OutputStream;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bw.bd.send.R;
import com.bw.bd.send.bean.BDConstants;
import com.bw.bd.send.bean.SIMInfo;
import com.bw.bd.utils.SerialPortOption;

public class SendMessageFragment extends Fragment implements View.OnClickListener, Observer {
    public static final String TAG = "SendMessageFragment";
    
    public View mView;
    public Context mContext;
    private TextView simInfoText, coutTimeText, inputWords;
    private EditText simIdEditText, msgContentEditText;
    private Button sendButton, contactButton, checkSIMButton,button;
    private SIMInfo mSIMInfo = new SIMInfo();
    private static int time = 60;
    public static final int PICK_CONTACT = 0x100;
    private static OutputStream mOutputStream;
    
    private SendMessageFragment () {}
    
    public SendMessageFragment(Context context, View view) {
//        mView = view;
        mContext = context;
    }
    
    
    public SendMessageFragment(Context context, OutputStream outputStream) {
//        mView = view;
        mContext = context;
        mOutputStream = outputStream;
    }
    
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch(msg.what) {
            case BDConstants.CONSTANT_BDSIM:
                simInfoText.setText("");
                simInfoText.setText(String.valueOf(msg.obj));
                simInfoText.setEnabled(false);
                break;
            case BDConstants.CONSTANT_COUNTTIME:
                if (time == 0) {
                    time = 60;
                    coutTimeText.setText("60秒计时");
                    coutTimeText.setFocusable(false);
                    coutTimeText.setEnabled(false);
                    SerialPortOption.isEnableSend = true;
                    sendButton.setEnabled(true);
                }
                coutTimeText.setText("时间: " + time);
                coutTimeText.setFocusable(false);
                break;
            }
        } 
    };
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "---onActivityCreated---");
        simInfoText = (TextView)mView.findViewById(R.id.sim_info_text);
        coutTimeText = (TextView)mView.findViewById(R.id.count_time);
        inputWords = (TextView)mView.findViewById(R.id.text_count_words);
        simIdEditText = (EditText)mView.findViewById(R.id.input_sim_id);
        msgContentEditText = (EditText)mView.findViewById(R.id.input_message);
        msgContentEditText.addTextChangedListener(mTextWatcher);
        sendButton = (Button)mView.findViewById(R.id.button_send);
        button = (Button)mView.findViewById(R.id.button2);
        button.setVisibility(View.INVISIBLE);//先暂时隐藏
        contactButton = (Button)mView.findViewById(R.id.btn_contact);
        checkSIMButton = (Button)mView.findViewById(R.id.btn_check_siminfo);
        sendButton.setOnClickListener(this);
        contactButton.setOnClickListener(this);
        checkSIMButton.setOnClickListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(TAG, "---onAttach---");
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "---onCreate---");
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "---onCreateView---");
        mView = inflater.inflate(R.layout.layout_message, container, false);
        
        return mView;
    }
    
    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "---onStop---");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "---onDestroy---");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (PICK_CONTACT) {
        case PICK_CONTACT:
            if (resultCode == Activity.RESULT_OK) {
            	String phoneNumber = getSystemContact(data);
            	phoneNumber.replace(" ", "");//remove space key
            	simIdEditText.setText(phoneNumber);
            }
            break;
            default:
        }
    }
    
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Log.i(TAG, "---onClick---");
        switch(v.getId()) {
        case R.id.btn_check_siminfo:
            String cmdCheckSim = BDConstants.CMD_QUERY_SIM;
            Log.i(TAG, "cmdCheckSim: " + cmdCheckSim);
            Toast.makeText(mContext, "mOutputStream: " + mOutputStream, Toast.LENGTH_SHORT).show();
            SerialPortOption.sendCommand(mOutputStream, cmdCheckSim);
            break;
        case R.id.btn_contact:
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            intent.setType(ContactsContract.Contacts.CONTENT_TYPE);//vnd.android.cursor.dir/contact
            startActivityForResult(intent, PICK_CONTACT);
            break;
        case R.id.button_send:
            //$BDSnd,DestID,DataLen,Msg
            String message = new String(); //send message content;
            int msgLength = 0; //send message content's length
            int simId = 0; //receiver id
            message = msgContentEditText.getText().toString();
            msgLength = message.length();
            if (!TextUtils.isEmpty(simIdEditText.getText().toString())) {
                simId = Integer.valueOf(simIdEditText.getText().toString());
            } else {
                Toast.makeText(mContext, "请输入接收人的SIM卡号", Toast.LENGTH_SHORT).show();
                return;
            }
            if (message.equals("")) {
                Toast.makeText(mContext, "请输入要发送的内容", Toast.LENGTH_SHORT).show();
                return;
            }
            SerialPortOption.isEnableSend = false;
            String cmdSendMsg = BDConstants.CMD_MESSAGE_SEND + "," + simId + "," + msgLength + "," +message + BDConstants.END;
            Log.i(TAG, "simId: " + simId);
            Log.i(TAG, "msgLength: " + msgLength);
            Log.i(TAG, "message: " + message);
            Log.i(TAG, "cmdSendMsg: " + cmdSendMsg);
            if (SerialPortOption.sendCommand(mOutputStream, cmdSendMsg)) {
                msgContentEditText.setText("");//send success, clear the editText 
                sendButton.setEnabled(false);
                CountTime time = new CountTime();
                time.start();
            } else {
            	Toast.makeText(mContext, "发送失败，请检查串口", Toast.LENGTH_SHORT).show();
            	sendButton.setEnabled(true);
            }
            break;
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        Log.i(TAG, "---update---");
        Message msg = (Message)data;
        String str = (String)msg.obj;
        Log.i(TAG, "str: " + str);
        switch(msg.what) {
        case BDConstants.CONSTANT_BDSIM:
            String[] argsItems = str.split(",");
            for(int i = 0; i < argsItems.length; i++) {
                Log.i(TAG, "argsItems["+ i +"]: " + argsItems[i]);
            }
            if (argsItems.length > 1) {
                mSIMInfo.ID = Integer.valueOf(argsItems[1].replaceAll(" ", ""));
                mSIMInfo.Frame = Integer.valueOf(argsItems[2].replaceAll(" ", ""));
                mSIMInfo.BID = Integer.valueOf(argsItems[3].replaceAll(" ", ""));
                mSIMInfo.Feature = Integer.valueOf(argsItems[4].replaceAll(" ", ""));
                mSIMInfo.Freq = Integer.valueOf(argsItems[5].replaceAll(" ", ""));
                mSIMInfo.Level = Integer.valueOf(argsItems[6].replaceAll(" ", ""));
                mSIMInfo.Flag = Integer.valueOf(argsItems[7].replaceAll(" ", ""));
            }
            Message message = mHandler.obtainMessage();
            message.what = BDConstants.CONSTANT_BDSIM;
            message.obj = "本机SIM卡号: " + String.valueOf(mSIMInfo.ID);
            mHandler.sendMessage(message);
            //simInfoText.setText("本机SIM卡号: " + String.valueOf(mSIMInfo.ID));
            break;
        case BDConstants.CONSTANT_BDSND:
        	Toast.makeText(mContext, "成功发送了短信", Toast.LENGTH_SHORT).show();
            break;
        case BDConstants.CONSTANT_BDREC:
        	Toast.makeText(mContext, "成功接收到短信", Toast.LENGTH_SHORT).show();
            break;
        }
    }
    
	private String getSystemContact(Intent intent) {
		Uri uri = intent.getData();
		String PhoneNumber = null;
		// 得到ContentResolver对象
		ContentResolver cr = getActivity().getContentResolver();
		// 取得电话本中开始一项的光标
		Cursor cursor = cr.query(uri, null, null, null, null);
		// 向下移动光标
		while (cursor.moveToNext()) {
			// 取得联系人名字
			int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
			String contact = cursor.getString(nameFieldColumnIndex);
			// 取得电话号码
			String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
			Cursor phone = cr.query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
							+ ContactId, null, null);
			// 不只一个电话号码
			while (phone.moveToNext()) {
				PhoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			}
			if(Integer.parseInt(Build.VERSION.SDK) < 14) {
                cursor.close();  
            }  
		}
		return PhoneNumber;
	}
	
    class CountTime extends Thread {

        @Override
        public void run() {
            super.run();
            while(!SerialPortOption.isEnableSend) {
                try {
                    Message msg = mHandler.obtainMessage();
                    time--;
                    msg.what = BDConstants.CONSTANT_COUNTTIME;
                    msg.arg1 = time;
                    mHandler.sendMessage(msg);
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        
    }
    
    TextWatcher mTextWatcher = new TextWatcher() {  
        private CharSequence temp;
        private int editStart ;
        private int editEnd ;
        @Override  
        public void onTextChanged(CharSequence s, int start, int before, int count) {  
        	Log.i(TAG, "---onTextChanged---");
             temp = s;
             Log.i(TAG, "s: " + s);
             
        }
        
        @Override  
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            Log.i(TAG, "---beforeTextChanged---");
            
        }  
          
        @Override  
        public void afterTextChanged(Editable s) {  
        	Log.i(TAG, "---afterTextChanged---");
            editStart = msgContentEditText.getSelectionStart();
            editEnd = msgContentEditText.getSelectionEnd();
            //msgContentEditText.setText("您输入了" + temp.length() + "个字符");  
            if (temp.length() > 30) {  
                Toast.makeText(mContext,"你输入的字数已经超过了限制！", Toast.LENGTH_SHORT).show();
                s.delete(editStart-1, editEnd);  
                int tempSelection = editStart;  
                msgContentEditText.setText(s);  
                msgContentEditText.setSelection(tempSelection);  
            } else {
            	inputWords.setText(temp.length() + "/120bit");//将输入的内容实时显示 
            }
        }

    };  

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

}
