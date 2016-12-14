package com.siteview.ecc.yft.bean;
/*
 * 摄像机丢失数据对象（断线，录像丢失，视频丢失）
 */
public class LoseVideorecord {
	private String time;
	private String videoname;
	private String videoflag;
	private String ipaddress;
	private String iplace;
	private long longtime;
	private long losttimes;
	private String date;
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getVideoname() {
		return videoname;
	}
	public void setVideoname(String videoname) {
		this.videoname = videoname;
	}
	public String getVideoflag() {
		return videoflag;
	}
	public void setVideoflag(String videoflag) {
		this.videoflag = videoflag;
	}
	public String getIpaddress() {
		return ipaddress;
	}
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}
	public String getIplace() {
		return iplace;
	}
	public void setIplace(String iplace) {
		this.iplace = iplace;
	}
	public long getLongtime() {
		return longtime;
	}
	public void setLongtime(long longtime) {
		this.longtime = longtime;
	}
	public long getLosttimes() {
		return losttimes;
	}
	public void setLosttimes(long losttimes) {
		this.losttimes = losttimes;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
}
