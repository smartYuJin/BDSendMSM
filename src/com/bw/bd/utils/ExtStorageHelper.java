package com.bw.bd.utils;

import java.io.File;
import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

/**
 * 通过监听储存卡变化，在广播事件中获取SD卡状态。 SD卡热插拔过程中，也能获取到SD卡正确的状态
 */

public final class ExtStorageHelper {
	private final String TAG = "ExtStorageHelper";
	private final boolean DBG = true;

	private static ExtStorageHelper sInstance;
	private Context mContext;
	private boolean mAvailable;
	private BroadcastReceiver mExtStorageReceiver;
	private ArrayList<ExtStorageNotifyListener> mNotifyListeners;

	/**
	 * 状态变化通知Listener
	 */
	public interface ExtStorageNotifyListener {
		public void onChangeStorageState(boolean bAvailable);
	}

	public static synchronized ExtStorageHelper getInstance() {
		if (sInstance == null) {
			sInstance = new ExtStorageHelper();
		}
		return sInstance;
	}

	private ExtStorageHelper() {
		mNotifyListeners = new ArrayList<ExtStorageNotifyListener>();
	}

	/**
	 * 初始化，启动SD卡监听
	 */
	public void init(Context context) {
		mContext = context;
		startMonitor();
	}

	/**
	 * SD卡状态变化通知设定函数
	 */
	public void setNotifyListener(ExtStorageNotifyListener listener) {
		if (listener != null && !mNotifyListeners.contains(listener)) {
			mNotifyListeners.add(listener);
		}
	}

	public void removeNotifyListener(ExtStorageNotifyListener listener) {
		if (listener != null && mNotifyListeners.contains(listener)) {
			mNotifyListeners.remove(listener);
		}
	}

	/**
	 * SD卡是否存在
	 */
	public boolean isAvailable() {
		return mAvailable;
	}

	/**
	 * 获取SD根目录
	 */
	public File getRootDirectory() {
		if (mAvailable) {
			return Environment.getExternalStorageDirectory();
		} else {
			return null;
		}
	}

	public String getRootPath() {
		if (mAvailable) {
			return Environment.getExternalStorageDirectory().getPath();
		} else {
			return null;
		}
	}

	/**
	 * SD卡剩余空间
	 */
	public long getAvailableSpaceSize() {
		long size = 0L;
		String path = getRootPath();
		if (path != null) {
			StatFs statfs = new StatFs(path);
			long blockSize = statfs.getBlockSize();
			long availableBlocks = statfs.getAvailableBlocks();
			size = availableBlocks * blockSize;
		}
		return size;
	}

	/**
	 * 监听SD变化
	 */
	private void startMonitor() {
		if (mExtStorageReceiver != null) {
			return;
		}

		if (DBG) {
			Log.w(TAG, "Start storage monitor.");
		}

		mExtStorageReceiver = new ExtStorageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addDataScheme("file");
		filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		mContext.registerReceiver(mExtStorageReceiver, filter);

		updateExtStorageState();
	}

	private void stopMonitor() {
		if (mExtStorageReceiver != null) {
			mContext.unregisterReceiver(mExtStorageReceiver);
			mExtStorageReceiver = null;
		}
	}

	private void updateExtStorageState() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			mAvailable = true;
		} else {
			mAvailable = false;
		}

		if (DBG) {
			Log.w(TAG, "Storage available: " + mAvailable);
		}
	}

	private class ExtStorageReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (DBG) {
				Log.w(TAG, "Storage state changed: action=" + intent.getAction() + ", data=" + intent.getData());
			}
			String action = intent.getAction();
			if (action == Intent.ACTION_MEDIA_MOUNTED) {
				mAvailable = true;
			} else if (action == Intent.ACTION_MEDIA_UNMOUNTED) {
				mAvailable = false;
			}
			updateExtStorageState();
			notifyAllListeners();
		}
	}

	private void notifyAllListeners() {
		for (ExtStorageNotifyListener listener : mNotifyListeners) {
			if (listener != null) {
				listener.onChangeStorageState(mAvailable);
			}
		}
	}
}
