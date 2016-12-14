package com.siteview.workorder.odata.yft.entities;

public class ServiceLevelAgreement {
	private String serviceCatalogName;//服务大类
	private String serviceCatalogRecId;//服务大类id
	private String serviceLevelAgreementRecId;//服务级别协议id
	private String serviceLevelAgreementName;//服务级别协议名称
	private String status;//状态，启用，禁用
	private String responseTime;//响应时间
	private String solveTime;//解决时间
	private String timeoutCharges;//超时收费  元/小时 TotalCount
	private String serviceProvider;//服务提供商
	
	private String createdby;//创建人
	private String createddatetime;//创建时间
	public String getServiceCatalogName() {
		return serviceCatalogName;
	}
	public void setServiceCatalogName(String serviceCatalogName) {
		this.serviceCatalogName = serviceCatalogName;
	}
	public String getServiceCatalogRecId() {
		return serviceCatalogRecId;
	}
	public void setServiceCatalogRecId(String serviceCatalogRecId) {
		this.serviceCatalogRecId = serviceCatalogRecId;
	}
	public String getServiceLevelAgreementRecId() {
		return serviceLevelAgreementRecId;
	}
	public void setServiceLevelAgreementRecId(String serviceLevelAgreementRecId) {
		this.serviceLevelAgreementRecId = serviceLevelAgreementRecId;
	}
	public String getServiceLevelAgreementName() {
		return serviceLevelAgreementName;
	}
	public void setServiceLevelAgreementName(String serviceLevelAgreementName) {
		this.serviceLevelAgreementName = serviceLevelAgreementName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getResponseTime() {
		return responseTime;
	}
	public void setResponseTime(String responseTime) {
		this.responseTime = responseTime;
	}
	public String getSolveTime() {
		return solveTime;
	}
	public void setSolveTime(String solveTime) {
		this.solveTime = solveTime;
	}
	public String getTimeoutCharges() {
		return timeoutCharges;
	}
	public void setTimeoutCharges(String timeoutCharges) {
		this.timeoutCharges = timeoutCharges;
	}
	public String getServiceProvider() {
		return serviceProvider;
	}
	public void setServiceProvider(String serviceProvider) {
		this.serviceProvider = serviceProvider;
	}
	public String getCreatedby() {
		return createdby;
	}
	public void setCreatedby(String createdby) {
		this.createdby = createdby;
	}
	public String getCreateddatetime() {
		return createddatetime;
	}
	public void setCreateddatetime(String createddatetime) {
		this.createddatetime = createddatetime;
	}
}
