package com.siteview.ecc.yft.bean;

import java.util.ArrayList;
import java.util.List;

public class OrganizationCode {
	private String o_code;
	private String o_name;
	private String o_parent_code;
	private List<OrganizationCode> list=new ArrayList<OrganizationCode>();
	public String getO_code() {
		return o_code;
	}
	public void setO_code(String o_code) {
		this.o_code = o_code;
	}
	public String getO_name() {
		return o_name;
	}
	public void setO_name(String o_name) {
		this.o_name = o_name;
	}
	public String getO_parent_code() {
		return o_parent_code;
	}
	public void setO_parent_code(String o_parent_code) {
		this.o_parent_code = o_parent_code;
	}
	public List<OrganizationCode> getList() {
		return list;
	}
	public void setList(List<OrganizationCode> list) {
		this.list = list;
	}
}
