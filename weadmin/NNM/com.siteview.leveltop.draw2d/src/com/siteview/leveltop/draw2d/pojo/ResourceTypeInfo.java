package com.siteview.leveltop.draw2d.pojo;

public class ResourceTypeInfo {
	
	private String typeName;
	
	private String category;
	
	private int    level;
	
	private String level_id;

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getLevel_id() {
		return level_id;
	}

	public void setLevel_id(String level_id) {
		this.level_id = level_id;
	}

	public ResourceTypeInfo(String typeName, String category, int level, String level_id) {
		this.typeName = typeName;
		this.category = category;
		this.level = level;
		this.level_id = level_id;
	}
	
}
