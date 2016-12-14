package com.siteview.ecc.yft.odata.duty;

import java.util.List;

import com.siteview.ecc.yft.bean.CommonObject;

/**
 * 值班组
 * 
 * @author Administrator
 *
 */
public class DutyGroup {

	private String id; // 值班组ID
	private String name; // 值班组名称
	private String desc; // 值班组描述
	private String dutyNum; // 值班组人数
	private String uId; // 用户ID
	private String uName; // 用户名称
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

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getDutyNum() {
		return dutyNum;
	}

	public void setDutyNum(String dutyNum) {
		this.dutyNum = dutyNum;
	}

	public String getuId() {
		return uId;
	}

	public void setuId(String uId) {
		this.uId = uId;
	}

	public String getuName() {
		return uName;
	}

	public void setuName(String uName) {
		this.uName = uName;
	}

	public List<CommonObject> getUsers() {
		return users;
	}

	public void setUsers(List<CommonObject> users) {
		this.users = users;
	}

}
