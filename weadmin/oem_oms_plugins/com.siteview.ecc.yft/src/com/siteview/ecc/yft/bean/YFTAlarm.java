package com.siteview.ecc.yft.bean;
/*
 * 英飞拓设备告警配置
 */
public class YFTAlarm {
	private String isor_error;
	private String isor_warning;
	private String isor_good;
	private String errorAlarm;
	private String warningAlarm;
	private String goodAlarm;
	private String type;
	private String alarmconfig;
	private String alarmname;
	public String getErrorAlarm() {
		return errorAlarm;
	}
	public void setErrorAlarm(String errorAlarm) {
		this.errorAlarm = errorAlarm;
	}
	public String getWarningAlarm() {
		return warningAlarm;
	}
	public void setWarningAlarm(String warningAlarm) {
		this.warningAlarm = warningAlarm;
	}
	public String getGoodAlarm() {
		return goodAlarm;
	}
	public void setGoodAlarm(String goodAlarm) {
		this.goodAlarm = goodAlarm;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getIsor_error() {
		return isor_error;
	}
	public void setIsor_error(String isor_error) {
		this.isor_error = isor_error;
	}
	public String getIsor_warning() {
		return isor_warning;
	}
	public void setIsor_warning(String isor_warning) {
		this.isor_warning = isor_warning;
	}
	public String getIsor_good() {
		return isor_good;
	}
	public void setIsor_good(String isor_good) {
		this.isor_good = isor_good;
	}
	public String getAlarmconfig() {
		return alarmconfig;
	}
	public void setAlarmconfig(String alarmconfig) {
		this.alarmconfig = alarmconfig;
	}
	public String getAlarmname() {
		return alarmname;
	}
	public void setAlarmname(String alarmname) {
		this.alarmname = alarmname;
	}
	
}
