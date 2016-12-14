package com.siteview.nnm.data.model;

public class svEdge {
	private String lid;
	private String lsource;//原点
	private String ltarget;//目标
	private String sinterface;
	private String tinterface;
	private String flowfrom;//取哪个的端口 left or right
	public String getFlowfrom() {
		return flowfrom;
	}
	public void setFlowfrom(String flowfrom) {
		this.flowfrom = flowfrom;
	}
	public String getLid() {
		return lid;
	}
	public void setLid(String lid) {
		this.lid = lid;
	}
	public String getLsource() {
		return lsource;
	}
	public void setLsource(String lsource) {
		this.lsource = lsource;
	}
	public String getLtarget() {
		return ltarget;
	}
	public void setLtarget(String ltarget) {
		this.ltarget = ltarget;
	}
	public String getSinterface() {
		return sinterface;
	}
	public void setSinterface(String sinterface) {
		this.sinterface = sinterface;
	}
	public String getTinterface() {
		return tinterface;
	}
	public void setTinterface(String tinterface) {
		this.tinterface = tinterface;
	}
	public svEdge() {
		// TODO Auto-generated constructor stub
	}

}
