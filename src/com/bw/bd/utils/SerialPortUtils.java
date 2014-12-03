package com.bw.bd.utils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Calendar;

import android.util.Log;

public class SerialPortUtils {
    public static boolean DEBUG = false;
    public static final String TAG = "SerialPortUtils";
    private static SerialPortUtils mSerialPortUtils = null;
    
    private SerialPortUtils() {}
    
    public static SerialPortUtils getInstance() {
        if (null != mSerialPortUtils) {
            mSerialPortUtils = new SerialPortUtils();
        }
        return mSerialPortUtils;
    }
   
    /**
     * 只转换字节数组的一部分
     * @param bytes 要转换的字节数组
     * @param len   字节数组的一部分，用长度表示。
     * @return
     */
    public static char[] byteToChars(byte[] bytes, int len) {
        Charset cs = Charset.forName("UTF-8");
        byte[] temp = new byte[len + 1];
        System.arraycopy(bytes, 0, temp, 0, len);
        ByteBuffer bb = ByteBuffer.allocate(temp.length);
        bb.put(temp);
        bb.flip();
        CharBuffer cb = cs.decode(bb);
        return cb.array();
    }
    
    /**
     * 
     * @param bytes
     * @return
     */
    public static char[] byteToChars(byte[] bytes) {
        Charset cs = Charset.forName("UTF-8");
    	//Charset cs = Charset.forName("Unicode");
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put(bytes);
        bb.flip();
        CharBuffer cb = cs.decode(bb);
        return cb.array();
    }
    
    /**
     * 
     * @param chars
     * @return
     */
    public static byte[] charToBytes(char[] chars) {
        Charset cs = Charset.forName("UTF-8");
        CharBuffer cb = CharBuffer.allocate(chars.length);
        cb.put(chars);
        cb.flip();
        ByteBuffer bb = cs.encode(cb);
        return bb.array();
    }
    
    /**
     * 
     * @param src
     * @return
     */
    public static String bytesToHexString2(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString().toUpperCase();
    }
    
    public static String getDate() {
    	Calendar calendar = Calendar.getInstance();  
        String created = (calendar.get(Calendar.MONTH)+1) + "月"//从0计算  
                + calendar.get(Calendar.DAY_OF_MONTH) + "日"  
                + calendar.get(Calendar.HOUR_OF_DAY) + "点";
        return created;
    }
}
