package com.siteview.NNM.modles;


public class SvgManagementModle {
	public final String name="设备管理";
	public SvgTypeModle svgtype;
	public SvgManagementModle(){
		if(svgtype==null)
			svgtype=new SvgTypeModle();
	}
	public SvgTypeModle getSvgtype() {
		return svgtype;
	}
	public void setSvgtype(SvgTypeModle svgtype) {
		this.svgtype = svgtype;
	}
	public String getName() {
		return name;
	}
	
}
