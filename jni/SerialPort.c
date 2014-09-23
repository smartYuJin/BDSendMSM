#include "BDMsg.h"
#include "SerialPort.h"
#include <jni.h>

#include <android/log.h>
#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>

#define  LOG_TAG    "BDRDSS"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,  LOG_TAG, __VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

#define MAX_MEM_SIZE 512

static speed_t getBaudrate(jint baudrate)
{
	switch(baudrate) {
	case 0: return B0;
	case 50: return B50;
	case 75: return B75;
	case 110: return B110;
	case 134: return B134;
	case 150: return B150;
	case 200: return B200;
	case 300: return B300;
	case 600: return B600;
	case 1200: return B1200;
	case 1800: return B1800;
	case 2400: return B2400;
	case 4800: return B4800;
	case 9600: return B9600;
	case 19200: return B19200;
	case 38400: return B38400;
	case 57600: return B57600;
	case 115200: return B115200;
	case 230400: return B230400;
	case 460800: return B460800;
	case 500000: return B500000;
	case 576000: return B576000;
	case 921600: return B921600;
	case 1000000: return B1000000;
	case 1152000: return B1152000;
	case 1500000: return B1500000;
	case 2000000: return B2000000;
	case 2500000: return B2500000;
	case 3000000: return B3000000;
	case 3500000: return B3500000;
	case 4000000: return B4000000;
	default: return -1;
	}
}

/*
 * Class:     com_bw_bd_jni_SerialPort
 * Method:    UARTRecvNative
 * Signature: (B[B[I)I
 */
JNIEXPORT jint JNICALL Java_com_bw_bd_jni_SerialPort_UARTRecvNative (JNIEnv *env, jclass obj, jbyte data, jbyteArray InMsg, jintArray InLgt){
	LOGI("---com in Java_com_bw_bd_jni_SerialPort_UARTRecvNative---");
//	char* _InMsg = (char*)(*env)->GetByteArrayElements(env, InMsg, 0);
//	LOGI("C++ _InMsg: %s\n", _InMsg);
	jsize _InMsgSize = (*env)->GetArrayLength(env, InMsg);
	LOGI("C++ _InMsgSize: %d\n", _InMsgSize);
	jint size = 0;
	if (_InMsgSize > MAX_MEM_SIZE) {
		size = MAX_MEM_SIZE;
	} else {
		size = _InMsgSize;
	}
	LOGI("C++ size: %d\n", size);
	int* _InLgt = (int*)(*env)->GetIntArrayElements(env, InLgt, 0);
	//LOGI("C++ _InLgt: %d\n", _InLgt[0]);
	char* tmpIn = (char*) malloc(size * sizeof(char));
	memset(tmpIn, 0, size*sizeof(char));
	//int UARTRecv(char data, char *InMsg, int *InLgt);
	int UART = UARTRecv(data, tmpIn, _InLgt);
	LOGI("C++ _InLgt[0]: %d\n", _InLgt[0]);
	LOGI("C++ tmpIn: %s\n", tmpIn);
	LOGI("C++ UART: %d\n", UART);
	LOGI("SetByteArrayRegion start");
	(*env)->SetByteArrayRegion(env, InMsg, 0, _InLgt[0], (jbyte*)tmpIn);
	LOGI("SetByteArrayRegion end");
	(*env)->ReleaseIntArrayElements(env, InLgt, _InLgt, 0);
	(*env)->ReleaseByteArrayElements(env, InMsg, tmpIn, 0);
	free(tmpIn);
	tmpIn = NULL;
	return UART;
}

/*
 * Class:     com_bw_bd_jni_SerialPort
 * Method:    BDRecvNative
 * Signature: (B[B[I)I
 */
JNIEXPORT jint JNICALL Java_com_bw_bd_jni_SerialPort_BDRecvNative (JNIEnv *env, jclass obj, jbyte data, jbyteArray OutMsg, jintArray OutLgt) {
	LOGI("---com in Java_com_bw_bd_jni_SerialPort_BDRecvNative---");
	int* _OutLgt = (int*)(*env)->GetIntArrayElements(env, OutLgt, 0);
	LOGI("C++ OutLgt: %d\n", _OutLgt);
	jsize _OutMsgSize = (*env)->GetArrayLength(env, OutMsg);
	LOGI("C++ _OutMsgSize: %d\n", _OutMsgSize);
	jint size = 0;
	if (_OutMsgSize > MAX_MEM_SIZE) {
		size = MAX_MEM_SIZE;
	} else {
		size = _OutMsgSize;
	}
	LOGI("C++ size: %d\n", size);
	char* tmpOut = (char*) malloc(size * sizeof(char));
	memset(tmpOut, 0, size * sizeof(char));
	//int BDRecv(char data, char *OutMsg, int *OutLgt);
	int bd = BDRecv(data, tmpOut, _OutLgt);
	LOGI("C++ _OutLgt[0]: %d\n", _OutLgt[0]);
	LOGI("C++ tmpOut: %s\n", tmpOut);
	LOGI("SetByteArrayRegion start");
	(*env)->SetByteArrayRegion(env, OutMsg, 0, _OutLgt[0], (jbyte*)tmpOut);
	LOGI("SetByteArrayRegion end");
	(*env)->ReleaseIntArrayElements(env, OutLgt, _OutLgt, 0);
	(*env)->ReleaseByteArrayElements(env, OutMsg, tmpOut, 0);
	free(tmpOut);
	tmpOut = NULL;
	return bd;
}
/*
 * Class:     com_bw_bd_jni_SerialPort
 * Method:    open
 * Signature: (Ljava/lang/String;II)Ljava/io/FileDescriptor;
 */
JNIEXPORT jobject JNICALL Java_com_bw_bd_jni_SerialPort_open (JNIEnv *env, jclass thiz, jstring path, jint baudrate, jint flags)
{
	LOGI("---com in Java_com_bw_bd_jni_SerialPort_open---");
	int fd;
	speed_t speed;
	jobject mFileDescriptor;

	/* Check arguments */
	{
		speed = getBaudrate(baudrate);
		if (speed == -1) {
			/* TODO: throw an exception */
			LOGE("Invalid baudrate");
			return NULL;
		}
	}

	/* Opening device */
	{
		jboolean iscopy;
		const char *path_utf = (*env)->GetStringUTFChars(env, path, &iscopy);
		LOGD("Opening serial port %s with flags 0x%x", path_utf, O_RDWR | flags);
		fd = open(path_utf, O_RDWR | flags);
		LOGD("open() fd = %d", fd);
		(*env)->ReleaseStringUTFChars(env, path, path_utf);
		if (fd == -1)
		{
			/* Throw an exception */
			LOGE("Cannot open port");
			/* TODO: throw an exception */
			return NULL;
		}
	}

	/* Configure device */
	{
		struct termios cfg;
		LOGD("Configuring serial port");
		if (tcgetattr(fd, &cfg))
		{
			LOGE("tcgetattr() failed");
			close(fd);
			/* TODO: throw an exception */
			return NULL;
		}

		LOGD("Configuring serial port step 1...");
		cfmakeraw(&cfg);
		cfsetispeed(&cfg, speed);
		cfsetospeed(&cfg, speed);

		LOGD("Configuring serial port step 2...");
		if (tcsetattr(fd, TCSANOW, &cfg))
		{
			LOGE("tcsetattr() failed");
			close(fd);
			/* TODO: throw an exception */
			return NULL;
		}
	}

	/* Create a corresponding file descriptor */
	{
		jclass cFileDescriptor = (*env)->FindClass(env, "java/io/FileDescriptor");
		jmethodID iFileDescriptor = (*env)->GetMethodID(env, cFileDescriptor, "<init>", "()V");
		jfieldID descriptorID = (*env)->GetFieldID(env, cFileDescriptor, "descriptor", "I");
		mFileDescriptor = (*env)->NewObject(env, cFileDescriptor, iFileDescriptor);
		(*env)->SetIntField(env, mFileDescriptor, descriptorID, (jint)fd);
	}
	LOGD("Configuring serial port done!");
	return mFileDescriptor;
}
/*
 * Class:     com_bw_bd_jni_SerialPort
 * Method:    close
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_bw_bd_jni_SerialPort_close (JNIEnv *env, jclass thiz)
{
	LOGI("---com in Java_com_bw_bd_jni_SerialPort_close---");
	jclass SerialPortClass = (*env)->GetObjectClass(env, thiz);
	jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");

	jfieldID mFdID = (*env)->GetFieldID(env, SerialPortClass, "mFd", "Ljava/io/FileDescriptor;");
	jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass, "descriptor", "I");

	jobject mFd = (*env)->GetObjectField(env, thiz, mFdID);
	jint descriptor = (*env)->GetIntField(env, mFd, descriptorID);

	LOGD("close(fd = %d)", descriptor);
	close(descriptor);
}


