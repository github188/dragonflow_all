package com.siteview.NNM.modles;

import java.util.List;

public class SubChartModle {
	
	private String name="子图";
	
	
	public SubChartModle(){}
	
	private List<SubChartModle> list;
	public List<SubChartModle> getList() {
		return list;
	}
	public void setList(List<SubChartModle> list) {
		this.list = list;
	}
	public String getName() {
		return name;
	}
	public void setName(String name)
	{
		this.name=name;
		
	}
	

}
