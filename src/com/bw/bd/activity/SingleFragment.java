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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bw.bd.send.R;
import com.bw.bd.send.bean.BDConstants;

public class SingleFragment extends Fragment implements View.OnClickListener, Observer{
    public static final String TAG = SingleFragment.class.getSimpleName();

    public View mView;
    public Context mContext;
    private ImageView imgSing01, imgSing02, imgSing03, imgSing04, imgSing05, imgSing06;
    private OutputStream mOutputStream;
    private LinearLayout.LayoutParams params01, params02, params03, params04, params05, params06;
    private String[] singleLevel = new String[6];
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
    	imgSing01 = (ImageView)mView.findViewById(R.id.image_single01);
        imgSing02 = (ImageView)mView.findViewById(R.id.image_single02);
        imgSing03 = (ImageView)mView.findViewById(R.id.image_single03);
        imgSing04 = (ImageView)mView.findViewById(R.id.image_single04);
        imgSing05 = (ImageView)mView.findViewById(R.id.image_single05);
        imgSing06 = (ImageView)mView.findViewById(R.id.image_single06);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "---onActivityCreated---");
        initView();
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
    	mView = inflater.inflate(R.layout.layout_single, container, false);
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
    public void update(Observable observable, Object data) {
        Log.i(TAG, "---update---");
        Message msg = (Message)data;
        String str = (String)msg.obj;
        Log.i(TAG, "str: " + str);
        String[] splite = str.split(",");
        for (int i = 0; i < splite.length; i++) {
            Log.i(TAG, splite[i]);
        }
        switch(msg.what) {
            case BDConstants.CONSTANT_BDPOW:
            Toast.makeText(mContext, "成功接收到信号束" + str, Toast.LENGTH_SHORT).show();
            if (null != imgSing01 && null != imgSing02 && null != imgSing03 
            	&& null != imgSing04 && null != imgSing05 && null != imgSing06) {
            	int height01 = Integer.valueOf(splite[1].replace(" ", ""));
            	params01 = new LinearLayout.LayoutParams(100, height01 * 22);
            	imgSing01.setLayoutParams(params01);
            	int height02 = Integer.valueOf(splite[2].replace(" ", ""));
            	params02 = new LinearLayout.LayoutParams(100, height02 * 22);
            	imgSing02.setLayoutParams(params02);
            	int height03 = Integer.valueOf(splite[3].replace(" ", ""));
            	params03 = new LinearLayout.LayoutParams(100, height03 * 22);
            	imgSing03.setLayoutParams(params03);
            	int height04 = Integer.valueOf(splite[4].replace(" ", ""));
            	params04 = new LinearLayout.LayoutParams(100, height04 * 22);
            	imgSing04.setLayoutParams(params04);
            	int height05 = Integer.valueOf(splite[5].replace(" ", ""));
            	params05 = new LinearLayout.LayoutParams(100, height05 * 22);
            	imgSing05.setLayoutParams(params05);
            	int height06 = Integer.valueOf(splite[6].replace(" ", ""));
            	params06 = new LinearLayout.LayoutParams(100, height06 * 22);
            	imgSing06.setLayoutParams(params06);
            }
            break;
        }
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, "---onClick---");
        
    }
}
