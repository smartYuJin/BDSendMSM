package com.bw.bd.utils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import android.util.Log;
import android.widget.Toast;

import com.bw.bd.jni.SerialPort;

public class SerialPortOption {
	private static final String TAG = SerialPortOption.class.getSimpleName();
	private static final boolean DBG = true;
	/**
	 * Do not remove or rename the field mFd: it is used by native method
	 * close()
	 */
	private FileDescriptor mFd;
	private FileInputStream mFileInputStream;
	private FileOutputStream mFileOutputStream;
	private boolean isSerialPortOpened;

	private File mDevice;
	private int mBaudrate;
	private int mFlags;
	public static boolean isEnableSend = false;
	static SerialPortOption instance;

	public static SerialPortOption getInstance(File device, int baudrate, int flags) {
		if (instance == null) {
			instance = new SerialPortOption(device, baudrate, flags);
		}

		return instance;
	}

	private SerialPortOption(File device, int baudrate, int flags) {
		this.mDevice = device;
		this.mBaudrate = baudrate;
		this.mFlags = flags;
	}

	public File getmDevice() {
		return mDevice;
	}

	public void setmDevice(File mDevice) {
		this.mDevice = mDevice;
	}

	public int getmBaudrate() {
		return mBaudrate;
	}

	public void setmBaudrate(String mBaudrate) {
		this.mBaudrate = Integer.valueOf(mBaudrate);
	}

	public boolean openSerialPort() throws SecurityException {

		if (mDevice == null) {
			return false;
		}

		if (!mDevice.canRead() || !mDevice.canWrite()) {
			Log.i(TAG, "SerialPort can not read or write");
			throw new SecurityException();
						// try {
			// Process su;
			// su = Runtime.getRuntime().exec("/system/bin/su");
			// String cmd = "chmod 666" + mDevice.getAbsolutePath() + "\n" +
			// "exit\n";
			// su.getOutputStream().write(cmd.getBytes());
			// if ((su.waitFor() != 0) || !mDevice.canRead() ||
			// !mDevice.canWrite()) {
			// throw new SecurityException();
			// }
			// } catch (Exception e) {
			// e.printStackTrace();
			// throw new SecurityException();
			// }
		}
		Log.i(TAG, "mDevice.getAbsolutePath(): " + mDevice.getAbsolutePath());
		Log.e(TAG, "baudrate: " + mBaudrate);
		Log.e(TAG, "mFlags: " + mFlags);
		mFd = SerialPort.open(mDevice.getAbsolutePath(), mBaudrate, mFlags);
		if (mFd == null) {
			// log.e(TAG, "Native fail to open device:" +
			// mDevice.getAbsolutePath());
			setSerialPortOpened(false);
			return false;
		}
		setSerialPortOpened(true);

		mFileInputStream = new FileInputStream(mFd);
		mFileOutputStream = new FileOutputStream(mFd);
		if (null == mFileInputStream) {
			Log.i(TAG, "\n\nmFileInputStream is null\n\n");
		}
		if (null == mFileOutputStream) {
			Log.i(TAG, "\n\nmFileOutputStream is null\n\n");
		}
		return true;
	}

	public void closeSerialPort() {
		setSerialPortOpened(false);
		mFileInputStream = null;
		mFileOutputStream = null;
		SerialPort.close();
	}

	 /**
     * 
     * @param outStream
     * @param cmd
     * @return
     */
    public static boolean sendCommand(OutputStream outStream, String cmd) {
        try {
            Log.d(TAG, "send command::" + cmd);
            int[] InLgt = new int[1];
            //String strBdSim = "$BDSIM\r\n";
            //byte[] byteCmd = strBdSim.getBytes();
            byte[] byteCmd = cmd.getBytes();
            ByteBuffer InMsg = ByteBuffer.allocate(128);
            for (byte b : byteCmd) {
                if (0 == SerialPort.UARTRecvNative(b, InMsg.array(), InLgt)) {
                    Log.d(TAG, "send byte cmd finish.");
                    break;
                } else {
                    Log.d(TAG, "continue send byte cmd.");
                }
            }
            byte[] mInMsg = new byte[InLgt[0]];
            InMsg.get(mInMsg, 0, InLgt[0]);
            Log.d(TAG, "Java mInMsg = " + SerialPortUtils.bytesToHexString2(mInMsg));
            Log.d(TAG, "Java InMsg = " + String.valueOf(SerialPortUtils.byteToChars(mInMsg)));
            Log.d(TAG, "Java InLgt = " + InLgt[0]);
            Log.i(TAG, "\n--------------------------------------------\n");
            outStream.write(mInMsg);
            outStream.flush();
            return true;
        } catch (Exception e) {
        	Log.i(TAG, "--------> 串口写入异常");
            e.printStackTrace();
            return false;
        }
    }
    
	public InputStream getInputStream() {
		return mFileInputStream;
	}

	public OutputStream getOutputStream() {
		return mFileOutputStream;
	}

	public boolean isSerialPortOpened() {
		return isSerialPortOpened;
	}

	public void setSerialPortOpened(boolean isSerialPortOpened) {
		this.isSerialPortOpened = isSerialPortOpened;
	}

//	// JNI
//	private native static FileDescriptor open(String path, int baudrate, int flags);
//
//	public native static void close();
//
//	static {
//		System.loadLibrary("serial_port");
//	}
}
