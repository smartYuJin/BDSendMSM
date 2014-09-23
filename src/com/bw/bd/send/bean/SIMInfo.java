package com.bw.bd.send.bean;

public class SIMInfo {

	/*本机SIM卡地址*/
	public int ID;
	/*帧号*/
	public int Frame;
	/*通播ID*/
	public int BID;
	/*用户特征*/
	public int Feature;
	/*服务频度*/
	public int Freq;
	/*通信等级*/
	public int Level;
	/*加密标志*/
	public int Flag;
	/*下属用户总数*/
	public int Sum;
	
	public SIMInfo() {
		ID = 0;
		Frame = 0;
		BID = 0;
		Feature = 0;
		Freq = 0;
		Level = 0;
		Flag = 0;
		Sum = 0;
	}
	
	public int getID() {
    	return ID;
    }
	public void setID(int iD) {
    	ID = iD;
    }
	public int getFrame() {
    	return Frame;
    }
	public void setFrame(int frame) {
    	Frame = frame;
    }
	public int getBID() {
    	return BID;
    }
	public void setBID(int bID) {
    	BID = bID;
    }
	public int getFeature() {
    	return Feature;
    }
	public void setFeature(int feature) {
    	Feature = feature;
    }
	public int getFreq() {
    	return Freq;
    }
	public void setFreq(int freq) {
    	Freq = freq;
    }
	public int getLevel() {
    	return Level;
    }
	public void setLevel(int level) {
    	Level = level;
    }
	public int getFlag() {
    	return Flag;
    }
	public void setFlag(int flag) {
    	Flag = flag;
    }
	public int getSum() {
    	return Sum;
    }
	public void setSum(int sum) {
    	Sum = sum;
    }
	
	
}
