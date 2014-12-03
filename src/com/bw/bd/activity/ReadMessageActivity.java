package com.bw.bd.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.bw.bd.provider.BdsMessage;
import com.bw.bd.provider.BdsMessage.ShortMessage;
import com.bw.bd.send.R;
import com.bw.bd.utils.SerialPortOption;
/**
 * 
 * @author yujin
 *
 */
public class ReadMessageActivity extends Activity implements OnClickListener{
	public static final String TAG = ReadMessageActivity.class.getSimpleName();
	
	private Button mBtnSend;
	private Button mBtnBack;
	private EditText mEditTextContent;
	private ListView mListView;
	private ChatMsgViewAdapter mAdapter;
	private List<MessageEntity> mDataArrays = new ArrayList<MessageEntity>();
	private ContentResolver mContentResolver;
	private MessageEntity mMessageEntity;
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Intent intent = this.getIntent(); 
		mMessageEntity = (MessageEntity)intent.getSerializableExtra("entity");
		Log.i(TAG, "mMessageEntity.SndAddre: " + mMessageEntity.SndAddre);
		Log.i(TAG, "mMessageEntity.date: " + mMessageEntity.date);
		Log.i(TAG, "mMessageEntity.RecAddre: " + mMessageEntity.RecAddre);
		initView();
		initData();
		mAdapter = new ChatMsgViewAdapter(this, mDataArrays);
		mListView.setAdapter(mAdapter);
	}

	public void initView() {
		mListView = (ListView) findViewById(R.id.listview);
		mBtnSend = (Button) findViewById(R.id.btn_send);
		mBtnSend.setOnClickListener(this);
		mBtnBack = (Button) findViewById(R.id.btn_back);
		mBtnBack.setOnClickListener(this);
		mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
	}
	
	public void initData() {
		mDataArrays.clear();
		mContentResolver = getContentResolver();
		String[] columns = new String[] { ShortMessage.FROM_ID, ShortMessage.FROM_NAME,
				ShortMessage.IS_READ, ShortMessage.FROM_ID, ShortMessage.CONTENT,
				ShortMessage.TO_ID, ShortMessage.TO_NAME, ShortMessage.DATE};
		Cursor mCursor = mContentResolver.query(BdsMessage.ShortMessage.getUri(), columns, null, null, null);
		int total = mCursor.getCount();
		int i=0;
		while (mCursor.moveToNext()) {
			MessageEntity[] mReceivedMessage = new MessageEntity[total];
			mReceivedMessage[i] = new MessageEntity();
			mReceivedMessage[i].SndAddre = mCursor.getInt(mCursor.getColumnIndex(ShortMessage.FROM_ID));
			mReceivedMessage[i].Msg = mCursor.getString(mCursor.getColumnIndex(ShortMessage.CONTENT));
			mReceivedMessage[i].date = mCursor.getString(mCursor.getColumnIndex(ShortMessage.DATE));
			mReceivedMessage[i].isRead = mCursor.getInt(mCursor.getColumnIndex(ShortMessage.IS_READ));
			mDataArrays.add(mReceivedMessage[i]);
			i++;
		}
		Log.i(TAG, " mCursor.getCount(): " + mCursor.getCount());
		Log.i(TAG, "mDataArrays.size: " + mDataArrays.size());
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_send:
			if (SerialPortOption.isEnableSend)
				send();
			else
				Toast.makeText(this, "离下一次发送时间周期还没到，请稍等后再发送", Toast.LENGTH_SHORT).show();
			break;
		case R.id.btn_back:
			finish();
			break;
		}
	}

	private void send() {
		String contString = mEditTextContent.getText().toString();
		if (contString.length() > 0) {
//			MessageEntity entity = new MessageEntity();
//			entity.setDate(getDate());
//			entity.setName("人马");
//			entity.setMsgType(false);
//			entity.setText(contString);
//			mDataArrays.add(entity);
			mAdapter.notifyDataSetChanged();
			mEditTextContent.setText("");
			mListView.setSelection(mListView.getCount() - 1);
		}
	}
}
