package com.siteview.NNM.modles;

public class SnmpModle {
	private String communtity;
	private int type;
	private String user;
	private String pwd;
	private String auth;
	private int port;
	private String prau;
	private String prpwd;
	public String getCommuntity() {
		return communtity;
	}
	public void setCommuntity(String communtity) {
		this.communtity = communtity;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getAuth() {
		return auth;
	}
	public void setAuth(String auth) {
		this.auth = auth;
	}
	public SnmpModle(){
		communtity="public";
		type=1;
		auth="MD5";
		user="";
		pwd="";
		port=161;
		prau="DES";
		prpwd="";
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getPrau() {
		return prau;
	}
	public void setPrau(String prau) {
		this.prau = prau;
	}
	public String getPrpwd() {
		return prpwd;
	}
	public void setPrpwd(String prpwd) {
		this.prpwd = prpwd;
	}
}
