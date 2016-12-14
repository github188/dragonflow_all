package com.siteview.ecc.yft.bean;

/**
 * 通用对象
 * @author Administrator
 *
 */
public class CommonObject {
	private String id;
	private String name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(!(obj instanceof CommonObject))
			return false;
		CommonObject commonObject = (CommonObject) obj;
		if(!commonObject.getId().equals(getId()))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		return 37 * id.hashCode() + 37 * getName().hashCode();
	}
}
