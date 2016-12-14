package com.siteview.NNM.modles;

import java.util.List;



public class RootNNM {
	public static final String name="网络设备节点";
	private List<Object> list;
	//private BusinessObject bo;
	public RootNNM(){}
	public RootNNM(List<Object> list) {
		super();
		this.list = list;
	}
	
	public String getName() {
		return name;
	}
	public List<Object> getList() {
		return list;
	}
	public void setList(List<Object> list) {
		this.list = list;
	}
//	public BusinessObject getBo() {
//		return bo;
//	}
//	public void setBo(BusinessObject bo) {
//		this.bo = bo;
//	}
}
