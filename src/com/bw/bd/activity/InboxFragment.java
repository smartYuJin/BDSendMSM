package com.bw.bd.activity;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bw.bd.provider.BdsMessage;
import com.bw.bd.provider.BdsMessage.ShortMessage;
import com.bw.bd.send.R;
import com.bw.bd.send.bean.BDConstants;

public class InboxFragment extends Fragment implements View.OnClickListener, Observer{
    public static final String TAG = InboxFragment.class.getSimpleName();

    public Context mContext;
    private OutputStream mOutputStream;
    public View mView;
    public TextView textTotal;
    public ListView mListView;
    public ListViewAdapter mListViewAdapter;
    private List<MessageEntity> mlistInfo = new ArrayList<MessageEntity>();
    public ContentResolver mContentResolver;
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case BDConstants.CONSTANT_UDDATEMESSAGE:
				refreshInbox();
				mListViewAdapter.notifyDataSetChanged();
				break;
			}
		}
	};
	
    private InboxFragment() {}

    public InboxFragment(Context context, View view) {
        mContext = context;
    }

    public InboxFragment(Context context, OutputStream outputStream) {
        mContext = context;
        mOutputStream = outputStream;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "---onActivityCreated---");
        textTotal = (TextView)mView.findViewById(R.id.text_total);
        Log.i(TAG, "textTotal: " + textTotal);
        mListView = (ListView)mView.findViewById(R.id.listview_message);
        Log.i(TAG, "mListView: " + mListView);
        mListViewAdapter = new ListViewAdapter(mContext, mlistInfo);
        mListView.setAdapter(mListViewAdapter);
        refreshInbox();
        mListView.setOnItemClickListener(new OnItemClickListener(){  
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {  
                MessageEntity entity = mlistInfo.get(position);
                int SndAddre = entity.getSndAddre(); 
                String infoTitle = entity.getMsg();
                showReadActivity(entity);
            }  
        });  
        //长按菜单显示  
        mListView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {  
            public void onCreateContextMenu(ContextMenu conMenu, View view , ContextMenuInfo info) {  
                conMenu.setHeaderTitle("菜单");  
                conMenu.add(0, 0, 0, "删除");  
                conMenu.add(0, 1, 1, "全部删除");  
                //conMenu.add(0, 2, 2, "待定");  
            }  
        });  
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch (item.getItemId()) {  
        case 0:
            String where = ShortMessage._ID + " =?";
            String[] selectionArgs = new String[]{String.valueOf(info.id)};
            mContentResolver.delete(BdsMessage.ShortMessage.getUri(), where, selectionArgs);
            Toast.makeText(mContext, "删除 id: " + info.id,Toast.LENGTH_SHORT).show();
            refreshInbox();
            textTotal.setText("总共: " + mlistInfo.size() + " 条");
            return true;  
        case 1:
            mContentResolver.delete(BdsMessage.ShortMessage.getUri(), null, null);
            refreshInbox();
            textTotal.setText("总共: " + mlistInfo.size());
            return true;  
        case 2:
            Toast.makeText(mContext, "待定",Toast.LENGTH_SHORT).show();  
            return true;  
        }  
        return false;  
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "---onCreate---");
        SmsDatabaseObserver content = new SmsDatabaseObserver(mHandler, this);
		// 注册短报文数据库变化监听
        mContext.getContentResolver().registerContentObserver(BdsMessage.ShortMessage.getUri(), true, content);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "---onCreateView---");
        Log.i(TAG, "container: " + container);
        //View view = getActivity().getLayoutInflater().inflate(R.layout.layout_message, container, false); 
        mView = inflater.inflate(R.layout.layout_inbox, container, false);
        Log.i(TAG, "mView: " + mView);
        
        return mView;
    }
    
    int num = 0;
    @Override
    public void update(Observable observable, Object data) {
        Log.i(TAG, "---update---");
        Message msg = (Message)data;
        String str = (String)msg.obj;
//        String str = null;
//        if (num%2==0) {
//        	str = "$BDRev,type,"+ (num++) +",五王王一五顶替械肛花样百出花样百出东奔西走花样百出肛三十分梆枯";
//        } else {
//        	str = "$BDRev,type,"+ (num++) +",asdasd";
//        }
        Log.i(TAG, "str: " + str);
        Log.i(TAG, "msg.what: " + msg.what);
        String[] argsItems = str.split(",");
        for(int i = 0; i < argsItems.length; i++) {
            //Log.i(TAG, "argsItems["+ i +"]: " + argsItems[i]);
        }
        switch(msg.what) {
        case BDConstants.CONSTANT_BDSND:
        	Toast.makeText(mContext, "成功发送了短信: " + str, Toast.LENGTH_SHORT).show();
            break;
        case BDConstants.CONSTANT_BDREC:
        	if (argsItems.length > 1) {
        		MessageEntity mReceivedMessage = new MessageEntity();
        		mReceivedMessage.type = argsItems[1].trim();
        		mReceivedMessage.SndAddre = Integer.valueOf(argsItems[2].trim());
        		mReceivedMessage.Msg = argsItems[3].trim();
        		mReceivedMessage.date = mReceivedMessage.getDate();
        		mReceivedMessage.isRead = 0;
        		ContentValues values = new ContentValues();
        		values.put(BdsMessage.ShortMessage.FROM_ID, mReceivedMessage.SndAddre);
        		values.put(BdsMessage.ShortMessage.FROM_NAME, mReceivedMessage.fromName);
        		values.put(BdsMessage.ShortMessage.TO_ID, mReceivedMessage.RecAddre);
        		values.put(BdsMessage.ShortMessage.TO_NAME, mReceivedMessage.ToName);
        		values.put(BdsMessage.ShortMessage.CONTENT, mReceivedMessage.Msg);
        		values.put(BdsMessage.ShortMessage.DATE, mReceivedMessage.date);
        		values.put(BdsMessage.ShortMessage.IS_READ, mReceivedMessage.isRead);
        		mContentResolver.insert(BdsMessage.ShortMessage.getUri(), values);
        		showInNotificationBar(mReceivedMessage);
        	}
        	Toast.makeText(mContext, "成功接收到短信: " + str, Toast.LENGTH_SHORT).show();
        	Vibrator vib = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE); 
        	vib.vibrate(500);
            break;
            default:
        }
    }
    
    @Override
    public void onClick(View v) {
        
    }
    
    public void refreshInbox() {
        Log.i("yujin", "---refreshListView---");
        mlistInfo.clear();
        mContentResolver = mContext.getContentResolver();
        String[] columns = new String[] { ShortMessage._ID, ShortMessage.FROM_ID, ShortMessage.CONTENT, ShortMessage.IS_READ,
                ShortMessage.DATE};
        Cursor mCursor = mContentResolver.query(BdsMessage.ShortMessage.getUri(), columns, null, null, null);
        int total = mCursor.getCount();
        int i = 0;
		while (mCursor.moveToNext()) {
			MessageEntity[] mReceivedMessage = new MessageEntity[total];
			mReceivedMessage[i] = new MessageEntity();
			mReceivedMessage[i].SndAddre = mCursor.getInt(mCursor.getColumnIndex(ShortMessage.FROM_ID));
			mReceivedMessage[i].Msg = mCursor.getString(mCursor.getColumnIndex(ShortMessage.CONTENT));
			mReceivedMessage[i].date = mCursor.getString(mCursor.getColumnIndex(ShortMessage.DATE));
			mReceivedMessage[i].isRead = mCursor.getInt(mCursor.getColumnIndex(ShortMessage.IS_READ));
			mReceivedMessage[i]._id = mCursor.getInt(mCursor.getColumnIndex(ShortMessage._ID));
			mlistInfo.add(mReceivedMessage[i]);
			i++;
		}
        mCursor.close();
        Log.i(TAG, " mCursor.getCount(): " + mCursor.getCount());
        Log.i(TAG, "mlistInfo.size: " + mlistInfo.size());
        mListView.requestLayout();
        mListViewAdapter.notifyDataSetChanged();
        textTotal.setText("总共: " + mlistInfo.size() + " 条信息");
    }
    
    public void showInNotificationBar(MessageEntity mMessageEntity) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager)mContext.getSystemService(ns);
        int icon = R.drawable.icon;
        CharSequence tickerText = mMessageEntity.Msg;
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, tickerText, when);
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.defaults = Notification.DEFAULT_SOUND;
        notification.defaults = Notification.DEFAULT_LIGHTS;
        CharSequence contentTitle = mMessageEntity.Msg;
        CharSequence contentText = mMessageEntity.Msg;
        Intent intent = new Intent("com.bw.bd.read.message");
        Bundle bundle = new Bundle();
        bundle.putSerializable("entity", mMessageEntity);
        intent.putExtras(bundle);
        PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
        notification.setLatestEventInfo(mContext, contentTitle, contentText,pIntent);
        mNotificationManager.notify(1, notification);
    }

    public void showReadActivity(MessageEntity mMessageEntity) {
    	Intent intent = new Intent("com.bw.bd.read.message");
        Bundle bundle = new Bundle();
        bundle.putSerializable("entity", mMessageEntity);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }
}
