package com.siteview.workorder.odata.yft.entities;

/**
 * 工单关联资产的信息
 * 
 * @author Administrator
 *
 */
public class HardWareAsset {
	private String id; // 设备id
	private String name; // 设备名称
	private String code; // 资产编号
	private String maintenanceMan; // 维护人
	private String maintenanceManUnit; // 维护人单位
	private String GBCode; // 国际编码
	private String toward; // 朝向
	private String installAddress; // 安装位置
	private String productTypeId; //产品类型ID
	private String productTypeName; //产品类型名称
	private String productCodeName; //产品类型CODE
	private String deviceCode;//资产编码
	private String ipAddress;//ip地址
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMaintenanceMan() {
		return maintenanceMan;
	}

	public void setMaintenanceMan(String maintenanceMan) {
		this.maintenanceMan = maintenanceMan;
	}

	public String getMaintenanceManUnit() {
		return maintenanceManUnit;
	}

	public void setMaintenanceManUnit(String maintenanceManUnit) {
		this.maintenanceManUnit = maintenanceManUnit;
	}

	public String getGBCode() {
		return GBCode;
	}

	public void setGBCode(String gBCode) {
		GBCode = gBCode;
	}

	public String getToward() {
		return toward;
	}

	public void setToward(String toward) {
		this.toward = toward;
	}

	public String getInstallAddress() {
		return installAddress;
	}

	public void setInstallAddress(String installAddress) {
		this.installAddress = installAddress;
	}

	public String getProductTypeId() {
		return productTypeId;
	}

	public void setProductTypeId(String productTypeId) {
		this.productTypeId = productTypeId;
	}

	public String getProductTypeName() {
		return productTypeName;
	}

	public void setProductTypeName(String productTypeName) {
		this.productTypeName = productTypeName;
	}

	public String getProductCodeName() {
		return productCodeName;
	}

	public void setProductCodeName(String productCodeName) {
		this.productCodeName = productCodeName;
	}

	public String getDeviceCode() {
		return deviceCode;
	}

	public void setDeviceCode(String deviceCode) {
		this.deviceCode = deviceCode;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

}
