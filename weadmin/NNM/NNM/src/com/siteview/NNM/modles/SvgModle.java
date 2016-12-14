package com.siteview.NNM.modles;

public class SvgModle {
	private  String name;
	private int id;
	public SvgModle(String name,int count,int id){
		this.name=name;
		if(count>0)
			this.name+="("+count+")";
		this.id=id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
