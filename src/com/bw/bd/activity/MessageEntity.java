package com.bw.bd.activity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageEntity implements Serializable {
	public int _id;
	public String type;
	public int SndAddre;
	public String fromName;//发送人
	public int RecAddre;
	public String ToName;//接收人
	public String Msg;
	public int isRead;//0 is not read, 1 is readed.
	public String date;
	public boolean isComMeg = true;
    public MessageEntity(){}
    public MessageEntity(String Fname, String Tname,String date, int sndAddre, boolean isComMsg) {
        super();
        this.fromName = Fname;
        this.ToName = Tname;
        this.date = date;
        this.SndAddre = sndAddre;
        this.isComMeg = isComMsg;
    }
    public String getFromName() {
        return fromName;
    }
    public void setFromName(String fromName) {
        this.fromName = fromName;
    }
    public String getToName() {
        return ToName;
    }
    public void setToName(String toName) {
        ToName = toName;
    }
    public int getIsRead() {
        return isRead;
    }
    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }
    public String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");  
        String date=format.format(new Date());  
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public int isRead() {
        return isRead;
    }
    public void setRead(int isRead) {
        this.isRead = isRead;
    }
    public int getSndAddre() {
        return SndAddre;
    }
    public void setSndAddre(int sndAddre) {
        SndAddre = sndAddre;
    }
    public String getMsg() {
        return Msg;
    }
    public void setMsg(String msg) {
        Msg = msg;
    }
    
    public boolean getMsgType() {
        return isComMeg;
    }

}
