package com.bw.bd.jni;

import java.io.FileDescriptor;

public class SerialPort {
	static {
		System.loadLibrary("BDMode");
	}
	/**
	 * 
	 * @param data  [in] 上位机串口输入消息
	 * @param InMsg [out] 用于串口发送至BD模块的BD输入协议指针
	 * @param InLgt [out] 用于串口发送至BD模块的BD输入协议长度指针
	 * @return 返回输入消息状态信息（0:本条消息完成，1:本条消息未完成）
	 * 
	 * 注：上位机串口输入串口消息，按byte读取，然后调用UARTRecv函数, 上位机输入一条消息以回车换行(\r\n)结束。
	 */
	/*extern int UARTRecv(char data, char *InMsg, int *InLgt);*/
	public native static int UARTRecvNative(byte data, byte[] InMsg, int[] InLgt);
	/**
	 * 
	 * @param data    [in]BD模块串口输出
	 * @param OutMsg  [out]封装后的BD输出协议指针
	 * @param OutLgt  [out]封装后的BD输出协议长度指针
	 * @return 返回输出消息状态信息(0:本条消息完成，1：本条消息未完成)
	 */
	/*extern int BDRecv(char data, char *OutMsg, int *OutLgt);*/
	public native static int BDRecvNative(byte data, byte[] OutMsg, int[] OutLgt);
	
	/**
	 * 
	 * @param path 打开串口的路径，dev/ttyMSM0
	 * @param baudrate 波特率
	 * @param flags O_RDONLY 以只读方式打开文件，O_WRONLY 以只写方式打开文件，O_RDWR 以可读写方式打开文件
	 * @return FileDescriptor
	 */
	public native static FileDescriptor open(String path, int baudrate, int flags);
	
	/**
	 * 关闭串口
	 */
	public native static void close();
}
