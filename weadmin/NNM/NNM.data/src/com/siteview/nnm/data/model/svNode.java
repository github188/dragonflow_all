package com.siteview.nnm.data.model;

import java.util.Map;

/**
 * 
 * @author Administrator
 * svgtype 0 line node{ 1 switch 2 Routing 3 switch-rout 4 firewall 5 server 6 pc 7hub 99 group}
 * svid 唯一标示
 * nx  node x 坐标
 * ny  node y 坐标
 * localip ip地址
 * mac mac地址
 * devicename 设备名称
 * customname 自定义名称
 * factory    单位
 * model      类型
 *
 */

public class svNode extends entity{
//	private int svgtype;
//	private String svid;
//	private float nx;
//	private float ny;
	private Map<String,String> properys;
	public Map<String, String> getProperys() {
		return properys;
	}
	public void setProperys(Map<String, String> properys) {
		this.properys = properys;
	}
	private String localip;
	private String mac;
	private String devicename;
	private String customname;
	private String factory;
	private String model;
	private String uptime;
	private String desc;
	private String memo;
    public String getUptime() {
		return uptime;
	}
	public void setUptime(String uptime) {
		this.uptime = uptime;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	//	public int getSvgtype() {
//		return svgtype;
//	}
//	public void setSvgtype(int svgtype) {
//		this.svgtype = svgtype;
//	}
//	public String getSvid() {
//		return svid;
//	}
//	public void setSvid(String svid) {
//		this.svid = svid;
//	}
//	public float getNx() {
//		return nx;
//	}
//	public void setNx(float nx) {
//		this.nx = nx;
//	}
//	public float getNy() {
//		return ny;
//	}
//	public void setNy(float ny) {
//		this.ny = ny;
//	}
	public String getLocalip() {
		return localip;
	}
	public void setLocalip(String localip) {
		this.localip = localip;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getDevicename() {
		return devicename;
	}
	public void setDevicename(String devicename) {
		this.devicename = devicename;
	}
	public String getCustomname() {
		return customname;
	}
	public void setCustomname(String customname) {
		this.customname = customname;
	}
	public String getFactory() {
		return factory;
	}
	public void setFactory(String factory) {
		this.factory = factory;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public svNode() {
		// TODO Auto-generated constructor stub
	}

}
