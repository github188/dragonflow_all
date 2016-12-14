package com.siteview.ecc.view.apptopu.odata;
/**
 * 封装监测器的状态字符串和对应的状态码。
 * @author sharklee
 *
 */
public enum MonitorState {
	
	GOOD("GOOD",1),
	WARN("WARNING",2),
	ERROR("ERROR",3),
	DISABLED("DISABLED",4);
	
	MonitorState(String stateString,int stateCode){
		this.stateString = stateString;
		this.stateCode = stateCode;
	}
	
	private String stateString;
	private int stateCode;
	
	public String getStateString() {
		return stateString;
	}
	public int getStateCode() {
		return stateCode;
	}
	
	

}
