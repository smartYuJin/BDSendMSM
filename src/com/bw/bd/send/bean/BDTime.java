package com.bw.bd.send.bean;

public class BDTime {

	private int year;
	private int mon;
	private int day;
	private int week;

	private int hour;
	private int min;
	private int sec;
	private int ms;

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMon() {
		return mon;
	}

	public void setMon(int mon) {
		this.mon = mon;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getWeek() {
		return week;
	}

	public void setWeek(int week) {
		this.week = week;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getSec() {
		return sec;
	}

	public void setSec(int sec) {
		this.sec = sec;
	}

	public int getMs() {
		return ms;
	}

	public void setMs(int ms) {
		this.ms = ms;
	}

	public String getTime() {
		StringBuilder sb = new StringBuilder();
		try {
			if (hour + 8 >= 24) {
				sb.append((hour + 8 - 24));
			} else {
				sb.append((hour + 8));
			}
			sb.append(":");
			sb.append(min);
			sb.append(":");
			sb.append(sec);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

	public String getDate() {
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(year);
			sb.append(".");
			sb.append(mon);
			sb.append(".");
			sb.append(day);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sb.toString();
	}
}
