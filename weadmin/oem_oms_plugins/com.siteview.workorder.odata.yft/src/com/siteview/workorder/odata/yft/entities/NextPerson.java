package com.siteview.workorder.odata.yft.entities;

/**
 * 流程下级人员信息
 * 
 * @author Administrator
 *
 */
public class NextPerson {
	private String recId;
	private String name; // 用户名
	private String loginId; // 登陆ID
	private String roleName; // 用户角色
	private String email; //邮箱
	
	public String getRecId() {
		return recId;
	}

	public void setRecId(String recId) {
		this.recId = recId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
