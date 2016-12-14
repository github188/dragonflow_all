package com.siteview.ecc.yft.bean;

import java.util.Date;

public class WorkOrderReport {

	private long newtotal;// 新建工单总数
	private long completecount;// 完成工单数
	private double completerate;// 完成工单率
	private double avgduration;// 平均维护时长
	private double outcompleterate;// 超时完成率
	private double outnocompleterate;// 超时未完成率
	private Date date;
	private String area;// 地区
	private String areacode;// 地区编码
	private String parentarea;
	private String parentareacode;
	private String type;
	
	public long getNewtotal() {
		return newtotal;
	}

	public void setNewtotal(long newtotal) {
		this.newtotal = newtotal;
	}

	public long getCompletecount() {
		return completecount;
	}

	public void setCompletecount(long completecount) {
		this.completecount = completecount;
	}

	public double getCompleterate() {
		return completerate;
	}

	public void setCompleterate(double completerate) {
		this.completerate = completerate;
	}

	public double getAvgduration() {
		return avgduration;
	}

	public void setAvgduration(double avgduration) {
		this.avgduration = avgduration;
	}

	public double getOutcompleterate() {
		return outcompleterate;
	}

	public void setOutcompleterate(double outcompleterate) {
		this.outcompleterate = outcompleterate;
	}

	public double getOutnocompleterate() {
		return outnocompleterate;
	}

	public void setOutnocompleterate(double outnocompleterate) {
		this.outnocompleterate = outnocompleterate;
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
