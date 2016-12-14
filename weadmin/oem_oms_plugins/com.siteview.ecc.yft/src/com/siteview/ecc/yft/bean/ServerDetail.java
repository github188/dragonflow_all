package com.siteview.ecc.yft.bean;

import java.util.Date;

/**
 * 非视频类设备
 * 
 * @author Administrator
 *
 */
public class ServerDetail {
	private long total;// 总数
	private long onlinecount;// 在线数
	private double onlinerate;// 在线率
	private double intactrate;// 完好率
	private long intactcount;// 完好数
	private long unavailablecount;// 不可用总时长
	private String servertype;// 设备类型
	private Date date;
	private String area;// 地区
	private String areacode;// 地区编码
	private String parentarea;
	private String parentareacode;
	private String type;

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public long getOnlinecount() {
		return onlinecount;
	}

	public void setOnlinecount(long onlinecount) {
		this.onlinecount = onlinecount;
	}

	public double getOnlinerate() {
		return onlinerate;
	}

	public void setOnlinerate(double onlinerate) {
		this.onlinerate = onlinerate;
	}

	public double getIntactrate() {
		return intactrate;
	}

	public void setIntactrate(double intactrate) {
		this.intactrate = intactrate;
	}

	public long getIntactcount() {
		return intactcount;
	}

	public void setIntactcount(long intactcount) {
		this.intactcount = intactcount;
	}

	public long getUnavailablecount() {
		return unavailablecount;
	}

	public void setUnavailablecount(long unavailablecount) {
		this.unavailablecount = unavailablecount;
	}

	public String getServertype() {
		return servertype;
	}

	public void setServertype(String servertype) {
		this.servertype = servertype;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getAreacode() {
		return areacode;
	}

	public void setAreacode(String areacode) {
		this.areacode = areacode;
	}

	public String getParentarea() {
		return parentarea;
	}

	public void setParentarea(String parentarea) {
		this.parentarea = parentarea;
	}

	public String getParentareacode() {
		return parentareacode;
	}

	public void setParentareacode(String parentareacode) {
		this.parentareacode = parentareacode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
