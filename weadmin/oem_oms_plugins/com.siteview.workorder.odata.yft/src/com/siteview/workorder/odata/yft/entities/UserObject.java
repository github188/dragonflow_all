package com.siteview.workorder.odata.yft.entities;

public class UserObject {
	private String loginId;
	private String flag; //0删除异常信息,1用户只有一个角色

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

}
