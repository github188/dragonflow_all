package com.siteview.ecc.yft.odata.duty;

import java.util.List;

import com.siteview.ecc.yft.bean.CommonObject;

/**
 * 值班组
 * 
 * @author Administrator
 *
 */
public class DutyTable {

	private String id; // 值班表ID
	private String name; // 值班表名称
	private String dutyRule; //值班规则,加入或排除
	private String dutyForm; // 形式,排班时间,保存每天或每周或每月
	private String dayNumber; // 值班日,选每周保存结束日期周一到周日
	private String weekField;// 值班星期,选每周保存0,1,2,3,4,5,6字符串
	private String startTime;
	private String endTime;
	private String startDateTime;
	private String endDateTime;
	private List<CommonObject> users; // 用户集合

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDutyForm() {
		return dutyForm;
	}

	public void setDutyForm(String dutyForm) {
		this.dutyForm = dutyForm;
	}

	public String getDayNumber() {
		return dayNumber;
	}

	public void setDayNumber(String dayNumber) {
		this.dayNumber = dayNumber;
	}

	public String getWeekField() {
		return weekField;
	}

	public void setWeekField(String weekField) {
		this.weekField = weekField;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(String startDateTime) {
		this.startDateTime = startDateTime;
	}

	public String getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(String endDateTime) {
		this.endDateTime = endDateTime;
	}

	public List<CommonObject> getUsers() {
		return users;
	}

	public void setUsers(List<CommonObject> users) {
		this.users = users;
	}

	public String getDutyRule() {
		return dutyRule;
	}

	public void setDutyRule(String dutyRule) {
		this.dutyRule = dutyRule;
	}

}
