package com.siteview.ecc.yft.bean;
/*
 * 英飞拓报警下发对象
 */
public class AlarmIssued {
	private String id;
	private String machineType;
	private String machineTypeId;
	private String whetherSend;
	private String cycle;
	private String alarmNumber;
	private String responseTime;
	private String alarmIssuedRel;
	private String updatetime;
	public String getMachineType() {
		return machineType;
	}
	public void setMachineType(String machineType) {
		this.machineType = machineType;
	}
	public String getMachineTypeId() {
		return machineTypeId;
	}
	public void setMachineTypeId(String machineTypeId) {
		this.machineTypeId = machineTypeId;
	}
	public String getWhetherSend() {
		return whetherSend;
	}
	public void setWhetherSend(String whetherSend) {
		this.whetherSend = whetherSend;
	}
	public String getCycle() {
		return cycle;
	}
	public void setCycle(String cycle) {
		this.cycle = cycle;
	}
	public String getAlarmNumber() {
		return alarmNumber;
	}
	public void setAlarmNumber(String alarmNumber) {
		this.alarmNumber = alarmNumber;
	}
	public String getResponseTime() {
		return responseTime;
	}
	public void setResponseTime(String responseTime) {
		this.responseTime = responseTime;
	}
	public String getAlarmIssuedRel() {
		return alarmIssuedRel;
	}
	public void setAlarmIssuedRel(String alarmIssuedRel) {
		this.alarmIssuedRel = alarmIssuedRel;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}
}
