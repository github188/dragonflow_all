package com.siteview.NNM.modles;

import java.util.ArrayList;
import java.util.List;

public class TableMibModle {
	private String name;
	private String oid;
	private String status;
	private String type;
	private List<TableMibModle> tablemib=new ArrayList<TableMibModle>();
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<TableMibModle> getTablemib() {
		return tablemib;
	}
	public void setTablemib(List<TableMibModle> tablemib) {
		this.tablemib = tablemib;
	} 
}
