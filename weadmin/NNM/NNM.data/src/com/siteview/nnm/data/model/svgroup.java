package com.siteview.nnm.data.model;

public class svgroup extends entity{
	/**
	 * 子图名称
	 */
	private String name;
	/**
	 * 创建时间
	 */
	private String createdate;
	/**
	 * ip地址列表
	 */
	private String iplist ;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCreatedate() {
		return createdate;
	}
	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}
	public String getIplist() {
		return iplist;
	}
	public void setIplist(String iplist) {
		this.iplist = iplist;
	}

}
