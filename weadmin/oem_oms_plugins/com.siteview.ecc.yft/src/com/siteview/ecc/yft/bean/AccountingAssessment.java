package com.siteview.ecc.yft.bean;

import java.util.Date;

/**
 * 绩效考核实体类
 * 
 * @author Administrator
 *
 */
public class AccountingAssessment {
	private String organization;// 区域,组织机构
	private String createdate;// 工单创建时间
	private String workordernum;// 工单号
	private double longdelay;// 延长时长
	private double costdeduction;// 所扣费用
	private String isp; //服务提供商
	
	private Date date;

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getCreatedate() {
		return createdate;
	}

	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}

	public String getWorkordernum() {
		return workordernum;
	}

	public void setWorkordernum(String workordernum) {
		this.workordernum = workordernum;
	}

	public double getLongdelay() {
		return longdelay;
	}

	public void setLongdelay(double longdelay) {
		this.longdelay = longdelay;
	}

	public double getCostdeduction() {
		return costdeduction;
	}

	public void setCostdeduction(double costdeduction) {
		this.costdeduction = costdeduction;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getIsp() {
		return isp;
	}

	public void setIsp(String isp) {
		this.isp = isp;
	}

}
