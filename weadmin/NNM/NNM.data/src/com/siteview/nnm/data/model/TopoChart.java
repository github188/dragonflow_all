package com.siteview.nnm.data.model;

import java.util.HashMap;
import java.util.Map;

public class TopoChart {
	/**
	 * 拓扑图name
	 */
	private String Name;
	/**
	 * 拓扑图创建时间
	 */
	private String CreateTime;
	/**
	 * 拓扑边集合
	 */
	private  Map<String, svEdge> Edges ;
	/**
	 * 设备集合
	 */
	private Map<String, entity> Nodes;
	
	public Map<String, entity> getNodes() {
		return Nodes;
	}
	public void setNodes(Map<String, entity> nodes) {
		Nodes = nodes;
	}
	public String getCreateTime() {
		return CreateTime;
	}
	public void setCreateTime(String createTime) {
		CreateTime = createTime;
	}
	public Map<String, svEdge> getEdges() {
		return Edges;
	}
	public void setEdges(Map<String, svEdge> edges) {
		Edges = edges;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	
	
}
