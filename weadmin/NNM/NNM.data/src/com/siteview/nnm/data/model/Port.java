package com.siteview.nnm.data.model;

public class Port {

	 String ID;
	 String descr;
	 String index;
	 String portnum;
	 String porttype;
	 String mac;
	 String speed;
	 
	 Port(String id,String desc,String index,String portnum,String portype,String mac,String speed){
		this.ID=id;
		this.descr=desc;
		this.index=index;
		this.portnum=portnum;
		this.porttype=portype;
		this.mac=mac;
		this.speed=speed;
	 }
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public String getPortnum() {
		return portnum;
	}
	public void setPortnum(String portnum) {
		this.portnum = portnum;
	}
	public String getPorttype() {
		return porttype;
	}
	public void setPorttype(String porttype) {
		this.porttype = porttype;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getSpeed() {
		return speed;
	}
	public void setSpeed(String speed) {
		this.speed = speed;
	}
	@Override
	public String toString() {
		return "ID "+ID;
	}
	
}
