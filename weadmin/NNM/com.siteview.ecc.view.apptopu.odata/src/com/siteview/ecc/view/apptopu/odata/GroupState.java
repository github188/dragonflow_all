package com.siteview.ecc.view.apptopu.odata;
/**
 * 封装group的状态字符串和对应的状态码
 * @author sharklee
 *
 */
public enum GroupState {
	
	GOOD("GOOD",100),
	WARN("WARNING",101),
	ERROR("ERROR",102);
	
	private String stateString;
	private int stateCode;
	
	private GroupState(String stateString,int stateCode) {
		this.stateString = stateString;
		this.stateCode = stateCode;
	}

	public String getStateString() {
		return stateString;
	}

	public int getStateCode() {
		return stateCode;
	}
	
	

}
