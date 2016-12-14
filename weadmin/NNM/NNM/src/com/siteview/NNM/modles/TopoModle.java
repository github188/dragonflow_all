package com.siteview.NNM.modles;

import java.util.List;

public class TopoModle extends Modle{
	public static final String name="拓扑管理";
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
}
