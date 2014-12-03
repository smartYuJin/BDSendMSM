package com.bw.bd.activity;

import java.io.OutputStream;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bw.bd.send.R;
import com.bw.bd.send.bean.BDConstants;

public class SingleFragment extends Fragment implements View.OnClickListener, Observer{
    public static final String TAG = SingleFragment.class.getSimpleName();

    public View mView;
    public Context mContext;
    private FrameLayout imgSing01, imgSing02, imgSing03, imgSing04, imgSing05, imgSing06;
    private TextView textSing01,textSing02,textSing03,textSing04,textSing05,textSing06;
    private OutputStream mOutputStream;
    private LinearLayout.LayoutParams params01, params02, params03, params04, params05, params06;
    private SingleFragment() {
    }

    public SingleFragment(Context context) {
//        mView = view;
        mContext = context;
    }

    public SingleFragment(Context context, OutputStream outputStream) {
//        mView = view;
        mContext = context;
        mOutputStream = outputStream;
    }

    private void initView() {
    	imgSing01 = (FrameLayout)mView.findViewById(R.id.image_single01);
        imgSing02 = (FrameLayout)mView.findViewById(R.id.image_single02);
        imgSing03 = (FrameLayout)mView.findViewById(R.id.image_single03);
        imgSing04 = (FrameLayout)mView.findViewById(R.id.image_single04);
        imgSing05 = (FrameLayout)mView.findViewById(R.id.image_single05);
        imgSing06 = (FrameLayout)mView.findViewById(R.id.image_single06);
        textSing01 = (TextView)mView.findViewById(R.id.text_single01_semaphore);
        textSing02 = (TextView)mView.findViewById(R.id.text_single02_semaphore);
        textSing03 = (TextView)mView.findViewById(R.id.text_single03_semaphore);
        textSing04 = (TextView)mView.findViewById(R.id.text_single04_semaphore);
        textSing05 = (TextView)mView.findViewById(R.id.text_single05_semaphore);
        textSing06 = (TextView)mView.findViewById(R.id.text_single06_semaphore);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	Log.i(TAG, "---onCreateView---");
    	mView = inflater.inflate(R.layout.layout_single, container, false);
        return mView;
    }
    

    @Override
    public void update(Observable observable, Object data) {
        Log.i(TAG, "---update---");
        Message msg = (Message)data;
        String str = (String)msg.obj;
        Log.i(TAG, "semaphore str: " + str);
        String[] splite = str.split(",");
        for (int i = 0; i < splite.length; i++) {
            Log.i(TAG, "splite["+i+"]"+splite[i]);
            splite[i].trim();
        }
        switch(msg.what) {
            case BDConstants.CONSTANT_BDPOW:
            	if(this.isVisible()) {
            		//Toast.makeText(mContext, "成功接收到信号束" + str, Toast.LENGTH_SHORT).show();
            	}
            	if (null != imgSing01 && null != imgSing02 && null != imgSing03 
            			&& null != imgSing04 && null != imgSing05 && null != imgSing06) {
            		int height01 = Integer.valueOf(splite[1].trim());
            		params01 = new LinearLayout.LayoutParams(100, height01 * 25);
            		textSing01.setText( "" + height01);
            		imgSing01.setLayoutParams(params01);
            		int height02 = Integer.valueOf(splite[2].trim());
            		params02 = new LinearLayout.LayoutParams(100, height02 * 25);
            		textSing02.setText("" + height02);
            		imgSing02.setLayoutParams(params02);
            		int height03 = Integer.valueOf(splite[3].trim());
            		params03 = new LinearLayout.LayoutParams(100, height03 * 25);
            		textSing03.setText("" + height03);
            		imgSing03.setLayoutParams(params03);
            		int height04 = Integer.valueOf(splite[4].trim());
            		params04 = new LinearLayout.LayoutParams(100, height04 * 25);
            		textSing04.setText("" + height04);
            		imgSing04.setLayoutParams(params04);
            		int height05 = Integer.valueOf(splite[5].trim());
            		params05 = new LinearLayout.LayoutParams(100, height05 * 25);
            		textSing05.setText("" + height05);
            		imgSing05.setLayoutParams(params05);
            		int height06 = Integer.valueOf(splite[6].trim());
            		params06 = new LinearLayout.LayoutParams(100, height06 * 25);
            		textSing06.setText("" + height06);
            		imgSing06.setLayoutParams(params06);
            }
            break;
        }
    }

    @Override
    public void onClick(View v) {
        
    }
}
