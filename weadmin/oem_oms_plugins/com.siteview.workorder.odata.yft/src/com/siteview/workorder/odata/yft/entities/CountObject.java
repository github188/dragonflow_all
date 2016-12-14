package com.siteview.workorder.odata.yft.entities;

/**
 * 统计对象
 * @author Administrator
 *
 */
public class CountObject {
	private String name; //统计名称
	private String count; // 统计个数

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}
}
