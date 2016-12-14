package com.siteview.nnm.data.model;

public class svPoint {
	int px;
	public void setPx(int px) {
		this.px = px;
	}
	public void setPy(int py) {
		this.py = py;
	}
	int py;
    public svPoint(int px,int py){
	 this.px=px;
	 this.py=py;
	}
	public int getPx() {
		return px;
	}
	public int getPy() {
		return py;
	}

}
