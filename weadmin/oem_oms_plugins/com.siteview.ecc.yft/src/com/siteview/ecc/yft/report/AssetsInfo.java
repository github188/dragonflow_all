package com.siteview.ecc.yft.report;

import java.util.Date;

public class AssetsInfo {
	private String AssetsCode;
	private String AssetsName;
	private String AssetsType;
	private String AssetsModel;
	private String AssetsStatus;
	private String AssetsBrand;
	private String CreatedBy;
	private Date CreatedDateTime;
	private String MaintenancePeople;
	private Date WarrantyPeriod;
	private int MaintenanceNumber;
	private boolean wpflag;
	private Date endDate;

	public String getAssetsCode() {
		return AssetsCode;
	}

	public void setAssetsCode(String assetsCode) {
		AssetsCode = assetsCode;
	}

	public String getAssetsName() {
		return AssetsName;
	}

	public void setAssetsName(String assetsName) {
		AssetsName = assetsName;
	}

	public String getAssetsType() {
		return AssetsType;
	}

	public void setAssetsType(String assetsType) {
		AssetsType = assetsType;
	}

	public String getAssetsModel() {
		return AssetsModel;
	}

	public void setAssetsModel(String assetsModel) {
		AssetsModel = assetsModel;
	}

	public String getAssetsStatus() {
		return AssetsStatus;
	}

	public void setAssetsStatus(String assetsStatus) {
		AssetsStatus = assetsStatus;
	}

	public String getAssetsBrand() {
		return AssetsBrand;
	}

	public void setAssetsBrand(String assetsBrand) {
		AssetsBrand = assetsBrand;
	}

	public String getCreatedBy() {
		return CreatedBy;
	}

	public void setCreatedBy(String createdBy) {
		CreatedBy = createdBy;
	}

	public String getMaintenancePeople() {
		return MaintenancePeople;
	}

	public void setMaintenancePeople(String maintenancePeople) {
		MaintenancePeople = maintenancePeople;
	}

	public int getMaintenanceNumber() {
		return MaintenanceNumber;
	}

	public void setMaintenanceNumber(int maintenanceNumber) {
		MaintenanceNumber = maintenanceNumber;
	}

	public boolean isWpflag() {
		return wpflag;
	}

	public void setWpflag(boolean wpflag) {
		this.wpflag = wpflag;
	}

	public Date getCreatedDateTime() {
		return CreatedDateTime;
	}

	public void setCreatedDateTime(Date createdDateTime) {
		CreatedDateTime = createdDateTime;
	}

	public Date getWarrantyPeriod() {
		return WarrantyPeriod;
	}

	public void setWarrantyPeriod(Date warrantyPeriod) {
		WarrantyPeriod = warrantyPeriod;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

}
