package com.siteview.leveltop.draw2d.pojo;

public class EdgeInfo {
	
	private String sourceResourceId;
	
	private String targetResourceId;
	
	private String linkId;
	
	public EdgeInfo(){
		
	}
	
	public EdgeInfo(String sourceResourceId,String targetResourceId){
		this.sourceResourceId = sourceResourceId;
		this.targetResourceId = targetResourceId;
	}

	public String getSourceResourceId() {
		return sourceResourceId;
	}

	public void setSourceResourceId(String sourceResourceId) {
		this.sourceResourceId = sourceResourceId;
	}

	public String getTargetResourceId() {
		return targetResourceId;
	}

	public void setTargetResourceId(String targetResourceId) {
		this.targetResourceId = targetResourceId;
	}
	
	public String getLinkId(){
		if(linkId==null&&sourceResourceId!=null&&targetResourceId!=null){
			StringBuffer sb = new StringBuffer();
			sb.append(sourceResourceId).append("-").append(targetResourceId);
			linkId = sb.toString();
		}
		return linkId;
	}
	
}
