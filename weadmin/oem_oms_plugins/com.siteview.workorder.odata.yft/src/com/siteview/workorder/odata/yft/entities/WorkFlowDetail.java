package com.siteview.workorder.odata.yft.entities;

/**
 * 工单详情记录
 * 
 * @author Administrator
 *
 */
public class WorkFlowDetail {
	private String id;
	private String from;
	private String to;
	private String state;
	private String des;
	private String flag;
	private String nextstatus;
	private int Startcx;
	private int Startcy;
	private int Endcx;
	private int Endcy;
	private int MidPointx;
	private int MidPointy;
	private String templateId;
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getNextstatus() {
		return nextstatus;
	}

	public void setNextstatus(String nextstatus) {
		this.nextstatus = nextstatus;
	}

	public int getStartcx() {
		return Startcx;
	}

	public void setStartcx(int startcx) {
		Startcx = startcx;
	}

	public int getStartcy() {
		return Startcy;
	}

	public void setStartcy(int startcy) {
		Startcy = startcy;
	}

	public int getEndcx() {
		return Endcx;
	}

	public void setEndcx(int endcx) {
		Endcx = endcx;
	}

	public int getEndcy() {
		return Endcy;
	}

	public void setEndcy(int endcy) {
		Endcy = endcy;
	}

	public int getMidPointx() {
		return MidPointx;
	}

	public void setMidPointx(int midPointx) {
		MidPointx = midPointx;
	}

	public int getMidPointy() {
		return MidPointy;
	}

	public void setMidPointy(int midPointy) {
		MidPointy = midPointy;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	
}
