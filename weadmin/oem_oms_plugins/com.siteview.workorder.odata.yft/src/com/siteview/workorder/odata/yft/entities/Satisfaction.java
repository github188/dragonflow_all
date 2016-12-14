package com.siteview.workorder.odata.yft.entities;

/**
 * 满意度
 * 
 * @author Administrator
 *
 */
public class Satisfaction {
	private String suggestions;// 要求提议
	private String servicesAging; // 满意度

	public String getSuggestions() {
		return suggestions;
	}

	public void setSuggestions(String suggestions) {
		this.suggestions = suggestions;
	}

	public String getServicesAging() {
		return servicesAging;
	}

	public void setServicesAging(String servicesAging) {
		this.servicesAging = servicesAging;
	}

}
