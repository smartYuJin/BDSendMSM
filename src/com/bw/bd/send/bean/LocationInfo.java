package com.bw.bd.send.bean;

public class LocationInfo {

	/**
	 * 纬度
	 */
	public String lat;
	/**
	 * 经度
	 */
	public String lon;
	/**
	 * 高度
	 */
	public String hta;
	/**
	 * 高度变化
	 */
	public String delth;
	
	public String getLat() {
    	return lat;
    }
	public void setLat(String lat) {
    	this.lat = lat;
    }
	public String getLon() {
    	return lon;
    }
	public void setLon(String lon) {
    	this.lon = lon;
    }
	public String getHta() {
    	return hta;
    }
	public void setHta(String hta) {
    	this.hta = hta;
    }
	public String getDelth() {
    	return delth;
    }
	public void setDelth(String delth) {
    	this.delth = delth;
    }
	
	
}
