package com.siteview.ecc.view.apptopu.odata;
/**
 * 封装设备的状态字符串和对应的状态码
 * @author sharklee
 *
 */
public enum EquipmentState {
	
	GOOD("GOOD",100),
	WARN("WARNING",101),
	ERROR("ERROR",102),
	DISABLED("DISABLED",102);
	
	private String stateString;
	private int stateCode;
	
	private EquipmentState(String stateString,int stateCode) {
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
