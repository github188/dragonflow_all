package com.siteview.leveltop.draw2d.pojo;

public class NodeInfo {
	
	private String resource_id;
	
	private String resource_alias;
	
	public NodeInfo(){
		
	}
	
	public NodeInfo(String resource_id,String resource_alias){
		this.resource_id = resource_id;
		this.resource_alias = resource_alias;
	}
	
	public String getResource_id() {
		return resource_id;
	}

	public void setResource_id(String resource_id) {
		this.resource_id = resource_id;
	}

	public String getResource_alias() {
		return resource_alias;
	}

	public void setResource_alias(String resource_alias) {
		this.resource_alias = resource_alias;
	}

	public boolean isChange(String resource_id,String resource_alias){
		if(resource_alias!=null&&!this.resource_alias.equals(resource_alias)){
			return true;
		}
		return false;
	}
	
}
