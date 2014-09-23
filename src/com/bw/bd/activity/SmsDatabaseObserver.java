package com.bw.bd.activity;

import com.bw.bd.send.bean.BDConstants;

import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class SmsDatabaseObserver extends ContentObserver{
	public static final String TAG = "SmsContentObserver";
	private Cursor cursor = null;
	private InboxFragment mInboxFragment;
	private Handler mHandler;

	public SmsDatabaseObserver(Handler handler, InboxFragment inboxFragment) {
		super(handler);
		mHandler = handler;
		mInboxFragment = inboxFragment;
	}
	
	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
		Log.i(TAG, "---onChange---");
		Message msg = mHandler.obtainMessage();
		msg.what = BDConstants.CONSTANT_UDDATEMESSAGE;
		mHandler.sendMessage(msg);
	}
}
