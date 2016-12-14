package com.siteview.svgvisio;

public class svMonitor {

	String monitorId;//monitor id
	String state;//
	String des;
	/**
	 * 
	 * @param monitorId monitor id
	 * @param state (ok /warning/  error/  disable/ bad)
	 * @param des  monitor description as :Service：ClipBook 
	 */
	svMonitor(String monitorId,String state,String des)
	{
		this.monitorId=monitorId;
		this.state=state;
		this.des=des;
	}
	public String getMonitorId() {
		return monitorId;
	}
	public void setMonitorId(String monitorId) {
		this.monitorId = monitorId;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getDes() {
		return des;
	}
	public void setDes(String des) {
		this.des = des;
	}
	
}
