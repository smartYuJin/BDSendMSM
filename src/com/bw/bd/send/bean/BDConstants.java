package com.bw.bd.send.bean;

/**
 * Defines constant values.
 * 
 */
public final class BDConstants {
	public static final String NMEA_FIELD_SEPERATOR = ",";
	public static final String NMEA_CHECKNUM_SEPERATOR = "\\*";

	public static final int GPS_NMEA = 1;
	public static final int BDS_NMEA = 2;
	public static final int GLO_NMEA = 3;

	public static final int BDS_WORK_PATTERN = 1;
	public static final int GPS_WORK_PATTERN = 2;
	public static final int MIX_WORK_PATTERN = 3;

	public static final int CONSTANT_BDSIM = 0x01;
	public static final int CONSTANT_BDSND = 0x02;
	public static final int CONSTANT_BDPOW = 0x03;
	public static final int CONSTANT_BDREC = 0x04;
	public static final int CONSTANT_COUNTTIME = 0x05;
	public static final int CONSTANT_UDDATEMESSAGE = 0x06;
	public static final int CONSTANT_SERIALPORTCHANGED = 0x07;
	public static final int CONSTANT_STOPREADSERIALPROT = 0x08;
	
	public static final int NMEA_INF = 1;
	public static final int NMEA_GGA = 2;
	public static final int NMEA_GSA = 3;
	public static final int NMEA_GSV = 4;
	public static final int NMEA_RMC = 5;
	public static final int NMEA_GLL = 6;
	public static final int NMEA_VTG = 7;
	public static final int NMEA_WAV = 8;

	public static final String SET_GPS_MODE = "$cfgsys,h01\r\n";
	public static final String SET_BDS_MODE = "$cfgsys,h10\r\n";
	public static final String SET_MIX_MODE = "$cfgsys,h11\r\n";
//	public static final String REQUEST_POWER_INFO = "$RDPOW\r\n";
	public static final String REQUEST_POWER_INFO = "$BDPOW\r\n";
	public static final String SAVE_SELECTED_MODE = "$cfgsave\r\n";
	public static final String SEND_COMMAND_SUCCESS = "$OK";
	public static final String SEND_COMMAND_FAIL = "$FAIL";
//	public static final String CMD_CHECK_POWER = "$RDOPEN\r\n";
//	public static final String CMD_POWER_ON = "$RDOPEN,1\r\n";
//	public static final String CMD_POWER_OFF = "$RDOPEN,0\r\n";
//	public static final String CMD_CHECK_POWER = "$BDOPEN\r\n"; 北一中没有此条命令
//	public static final String CMD_POWER_ON = "$BDOPEN,1\r\n";
//	public static final String CMD_POWER_OFF = "$BDOPEN,0\r\n";
	
	/*北斗SIM卡查询*/
	public static final String CMD_QUERY_SIM = "$BDSIM\r\n";
	/*北斗功率查询*/
	public static final String CMD_QUERY_POWER = "$BDPOW\r\n";
	/*北斗短信发送 $BDSnd,DestID,DataLen,Msg*/
	public static final String CMD_MESSAGE_SEND = "$BDSnd";
	/*北斗系统自检 $BDSCK, freq*/
	public static final String CMD_BD_SYSTEM_CHECK = "$BDSCK,1\r\n";
	/*北斗定位申请 $BDPos,freq*/
	public static final String CMD_BD_LOCATION_ASK = "$BDPos,1\r\n";
	/*北斗时间查询 $BDTIM,freq*/
	public static final String CMD_DB_TIME_QUERY = "$BDTIM,1\r\n";
	/*北斗结束指令 $BDEnd,para*/
	public static final String CMD_DB_STOP = "$BDEnd,0\r\n";
	/*命令结束符*/
	public static final String END = "\r\n";
	
	static class MsgACK {
		public static final String Ok = "OK!";
		public static final String Fail= "Fail!";
		public static final String SignalUnLocked= "Signal unlockled!";
		public static final String PowerLimited= "Power limited!";
		public static final String ServiceUnreachable= "Service unreachable!";
		public static final String EncryptionError= "Encryption error!";
		public static final String CRCError= "CRC error!";
		public static final String Unknown= "unknown!";
	}
}
