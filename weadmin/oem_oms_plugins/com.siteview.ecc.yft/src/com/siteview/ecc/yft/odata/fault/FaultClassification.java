package com.siteview.ecc.yft.odata.fault;

public class FaultClassification {
	private String pId; // 故障大类ID
	private String cId; // 故障细类ID
	private String pName; // 故障大类名称
	private String cName; // 故障细类名称

	public String getpId() {
		return pId;
	}

	public void setpId(String pId) {
		this.pId = pId;
	}

	public String getcId() {
		return cId;
	}

	public void setcId(String cId) {
		this.cId = cId;
	}

	public String getpName() {
		return pName;
	}

	public void setpName(String pName) {
		this.pName = pName;
	}

	public String getcName() {
		return cName;
	}

	public void setcName(String cName) {
		this.cName = cName;
	}

}
