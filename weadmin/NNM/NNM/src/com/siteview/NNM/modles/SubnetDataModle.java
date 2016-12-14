package com.siteview.NNM.modles;

import java.util.List;

import com.siteview.nnm.data.model.svNode;

public class SubnetDataModle {
	private String subnetname;
	private int count;
	private List<svNode> svnodes;
	public String getSubnetname() {
		return subnetname;
	}
	public void setSubnetname(String subnetname) {
		this.subnetname = subnetname;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public List<svNode> getSvnodes() {
		return svnodes;
	}
	public void setSvnodes(List<svNode> svnodes) {
		this.svnodes = svnodes;
	}
}
