package com.siteview.ecc.view.apptopu.odata;

import org.eclipse.rap.json.JsonObject;

import com.siteview.utils.international.InternationalUtils;

import Siteview.DataRow;
import Siteview.DataRowCollection;
import Siteview.DataTable;
import Siteview.DatabaseEngine;
import Siteview.SiteviewException;
import Siteview.Api.ISiteviewApi;
import Siteview.Database.IDbDataParameterCollection;
import Siteview.Database.SqlParameterCollection;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MonitorDB {

	private List<String> Ips;
	private List<String> Devices;
	private List<String> Groups;
	private List<String> Lines;
	private ISiteviewApi siteApi;
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
	public static final String SVG_ATTRIBUTE_IP = "ip";
	public static final String SVG_ATTRIBUTE_MID = "mid";
	public static final String SVG_ATTRIBUTE_STATE = "state";
	public static final String SVG_ATTRIBUTE_DESC = "desc";
	public static final String SVG_ATTRIBUTE_LINE = "line";

	public static final String EQUIPMENT_RECID_GET_BY_IP = "select RecId from " + "Equipment where serveraddress=?";
	public static final String EQUIPMENT_GET_BY_IP = "select EquipmentStatus as Status,RecId from " + "Equipment where serveraddress='%s'";
	public static final String EQUIPMENT_IP_GET_BY_TITLE = "select EquipmentStatus as Status,SERVERADDRESS,RECID from " + "Equipment where title='%s'";
	public static final String MONITOR_GET_BY_DEVICE_TITLE = "select m.TITLE,m.MonitorValue as Message,m.RECID,m.GROUPId as Groups,m.EquipmentId as Machine,m.MonitorSTATUS as Status,m.MONITORTYPE as EccMonitorType from "+ "Monitor m where m.EquipmentId in (select recid from Equipment where title='%s')";
	public static final String MONITOR_GET = "select * from Monitor where equipmentid=?";
	public static final String CHILDGROUP_GET_BY_GROUP = "with cte_child(groupname,recid,parentgroupid) as (select groupname,recid,parentgroupid from EccGroup where RecID = '%s' union all select a.groupname,a.recid,a.parentgroupid from EccGroup a, cte_child b WHERE a.parentgroupid=b.recid)select * from cte_child";
	public static final String MONITOR_GET_BY_GROUP = "select RECID,monitorSTATUS as Status,GROUPid as Groups,equipmentid as Machine,MONITORTYPE as EccMonitorType,TITLE,monitorvalue as Message from Monitor where groupid='%s'";
	public static final String MONITOR_GET_BY_IP_MONITORTITLE = "select m.MonitorStatus as Status,m.monitorvalue as Message,m.RecId,m.MONITORTYPE as EccMonitorType from Monitor m,Equipment e where m.title = '%s' and m.equipmentid = e.RecId and e.ServerAddress='%s'";
	public static final String MONITOR_GET_BY_IP = "select m.TITLE,m.monitorvalue as Message,m.RECID,m.GROUPid as Groups,m.equipmentid as Machine,m.MONITORTYPE as EccMonitorType,m.monitorSTATUS as Status from Monitor m where m.equipmentid in(select recid from Equipment where serveraddress='%s') ";
	public static final String GROUP_GET_BY_NAME = "select RECID,STATUS from EccGroup where GroupName='%s'";
	public static final String EQUIPMENT_ID = "RECID";
	public static final String GROUP_ID = "RECID";
	public static final String EQUIPMENT_IP = "SERVERADDRESS";
	public static final String EQUIPMENT_GROUP = "GROUPNAME";
	public static final String EQUIPMENT_STATUS = "STATUS";

	public static final String GROUP_STATUS = "STATUS";

	public static final String MONITOR_TITLE = "TITLE";
	public static final String MONITOR_MESSAGE = "MESSAGE";
	public static final String MONITOR_STATUS = "STATUS";
	public static final String MONITOR_ID = "RECID";

	public static final String MONITOR_ECCMONITORTYPE = "MONITORTYPE";

	public static final String GET_GROUPID_BY_GROUPNAME = "select RecId from EccGroup where GroupName='%s'";
	public static final String MYSQL_CHILDGROUP_GET_BY_GROUP = "SELECT * FROM EccGroup WHERE FIND_IN_SET(RecID, find_eccgroup_childrens_func('%s'))";

	public static final String LOAD_ASSET_BY_IP = "select * from ASSETSLIST where IPADDRESS = '%s'";
	public static final String LOAD_ASSET_BY_NAME = "select * from ASSETSLIST where ASSETSNAME = '%s'";

	public static final String CUSTOM_SQL_IP = "select asset.RecId,asset.EquipmentTitle,asset.AssetsName,asset.AssetsCode,asset.MaintenancePersonnel," + "asset.AssetsStatus,asset.ConfigDetails,asset.Contact,asset.WarrantyStartDate,asset.WarrantyEndDate,asset.AssetsUsed,depar.DepartmentName,"
			+ "loc.LocationName,business.BusProcessName,equipBrand.BrandName,DepartmentID as departmentID,LocationID as locationID," + "BusinessID as businessID,asset.Brand " + "from AssetsList asset " + "left join Department depar on departmentID = depar.RecId " + "left join EquipmentLocation loc on locationID = loc.RecId " + "left join EquipmentBrand equipBrand on Brand = equipBrand.RecId "
			+ "left join EquipmentBusProcess business on businessID = business.RecId " + "where asset.IPAddress='%s'";

	public static final String CUSTOM_SQL_NAME = "select asset.RecId,asset.EquipmentTitle,asset.AssetsName,asset.AssetsCode,asset.MaintenancePersonnel," + "asset.AssetsStatus,asset.ConfigDetails,asset.Contact,asset.WarrantyStartDate,asset.WarrantyEndDate,asset.AssetsUsed,depar.DepartmentName,"
			+ "loc.LocationName,business.BusProcessName,equipBrand.BrandName,DepartmentID as departmentID,LocationID as locationID," + "BusinessID as businessID,asset.Brand " + "from AssetsList asset " + "left join Department depar on departmentID = depar.RecId " + "left join EquipmentLocation loc on locationID = loc.RecId " + "left join EquipmentBrand equipBrand on Brand = equipBrand.RecId "
			+ "left join EquipmentBusProcess business on businessID = business.RecId " + "where asset.AssetsName='%s'";

	public MonitorDB() {

	}

	public MonitorDB(List<String> Ips, List<String> Devices, List<String> Groups, List<String> Lines, ISiteviewApi siteApi) {
		this.Ips = Ips;
		this.Devices = Devices;
		this.Groups = Groups;
		this.Lines = Lines;
		this.siteApi = siteApi;

	}

	private KeyValue<List<JsonObject>, Integer> buildMonitorInfoFromIP(String ip) {
		Integer monitorHightestState = -1;
		List<JsonObject> jsons = new ArrayList<JsonObject>();
		for (Map.Entry<String, Map<String, Object>> entry : loadMonitorByIp(ip).entrySet()) {
			JsonObject json = new JsonObject();
			json.add(SVG_ATTRIBUTE_IP, ip);
			// 将Monitor的recid，组id，machineid传到页面，当点击监测器时，页面会将这些信息回传过来，可以依据这些信息打开监测器的页面。
			json.add(SVG_ATTRIBUTE_MID, entry.getKey() + "&&" + entry.getValue().get("GROUPID") + "&&" + entry.getValue().get("EQUIPMENTID"));
			int monitorStateCode = getMonitorStateCode((String) (entry.getValue().get(MONITOR_STATUS)));
			if ((monitorStateCode > monitorHightestState) && (monitorStateCode != MonitorState.DISABLED.getStateCode()))
				monitorHightestState = monitorStateCode;
			json.add(SVG_ATTRIBUTE_STATE, monitorStateCode);
			json.add(SVG_ATTRIBUTE_DESC, buildDesc(entry.getValue(), (String) entry.getValue().get("MONITORTYPE")));
			jsons.add(json);
		}
		return new KeyValue(jsons, monitorHightestState);
	}

	// 根据group获取监测器信息
	private KeyValue<List<JsonObject>, Integer> buildMonitorInfoFromGroup(String group) {
		Integer hightestState = -1;
		List<JsonObject> jsons = new ArrayList<JsonObject>();
		if (group == null || group.trim().equals(""))
			return new KeyValue(jsons, hightestState);
		for (Map.Entry<String, Map<String, Object>> entry : loadMonitorByGroup(group).entrySet()) {
			JsonObject json = new JsonObject();
			json.add(SVG_ATTRIBUTE_IP, group);
			// 将Monitor的recid，组id，machineid传到页面，当点击监测器时，页面会将这些信息回传过来，可以依据这些信息打开监测器的页面。
			json.add(SVG_ATTRIBUTE_MID, entry.getKey() + "&&" + entry.getValue().get("GROUPID") + "&&" + entry.getValue().get("EQUIPMENTID"));
			int monitorStateCode = getMonitorStateCode((String) (entry.getValue().get(MONITOR_STATUS)));
			if ((monitorStateCode > hightestState) && (monitorStateCode != MonitorState.DISABLED.getStateCode())) {
				hightestState = monitorStateCode;
			}
			json.add(SVG_ATTRIBUTE_STATE, monitorStateCode);
			json.add(SVG_ATTRIBUTE_DESC, buildDesc(entry.getValue(), (String) entry.getValue().get("MONITORTYPE")));
			jsons.add(json);
		}
		return new KeyValue(jsons, hightestState);
	}

	private JsonObject buildGroupInfo(String group, int groupState) {
		JsonObject json = new JsonObject();
		json.add(SVG_ATTRIBUTE_IP, group);
		json.add(SVG_ATTRIBUTE_DESC, "GROUP_NAME" + group);
		for (Map.Entry<String, Map<String, Object>> entry : loadGroupByName(group).entrySet()) {
			if (groupState != -1) {
				json.add(SVG_ATTRIBUTE_STATE, groupState);
			} else {
				json.add(SVG_ATTRIBUTE_STATE, getGroupStateCode((String) entry.getValue().get(GROUP_STATUS)));
			}
			// 只取一个监测器数据
			break;
		}
		return json;
	}

	// 根据设备的title获取监测器信息
	private KeyValue<List<JsonObject>, Integer> buildMonitorInfoFromDeviceTitle(String deviceTitle) {
		Integer hightestState = -1;
		List<JsonObject> jsons = new ArrayList<JsonObject>();
		if (deviceTitle == null || deviceTitle.trim().equals(""))
			return new KeyValue(jsons, hightestState);
		for (Map.Entry<String, Map<String, Object>> entry : loadMonitorByDeviceTitle(deviceTitle).entrySet()) {
			JsonObject json = new JsonObject();
			json.add(SVG_ATTRIBUTE_IP, deviceTitle);
			// 将Monitor的recid，组id，machineid传到页面，当点击监测器时，页面会将这些信息回传过来，可以依据这些信息打开监测器的页面。
			json.add(SVG_ATTRIBUTE_MID, entry.getKey() + "&&" + entry.getValue().get("GROUPID") + "&&" + entry.getValue().get("EQUIPMENTID"));
			int monitorStateCode = getMonitorStateCode((String) (entry.getValue().get(MONITOR_STATUS)));
			if ((monitorStateCode > hightestState) && (monitorStateCode != MonitorState.DISABLED.getStateCode())) {
				hightestState = monitorStateCode;
			}
			json.add(SVG_ATTRIBUTE_STATE, getMonitorStateCode((String) (entry.getValue().get(MONITOR_STATUS))));
			json.add(SVG_ATTRIBUTE_DESC, buildDesc(entry.getValue(), (String) entry.getValue().get("MONITORTYPE")));
			jsons.add(json);
		}
		return new KeyValue(jsons, hightestState);
	}

	// 根据设备的title获取设备信息
	private JsonObject buildEquipmentInfoFromDeviceTitle(String deviceTitle, int deviceState) {
		JsonObject json = new JsonObject();
		if (deviceTitle == null || deviceTitle.trim().equals(""))
			return json;
		json.add(SVG_ATTRIBUTE_IP, deviceTitle);
		for (Map.Entry<String, Map<String, Object>> entry : loadEquipmentByTitle(deviceTitle).entrySet()) {
			if (deviceState != -1) {
				json.add(SVG_ATTRIBUTE_STATE, getDeviceStateByMonitorState(deviceState));
			} else {
				json.add(SVG_ATTRIBUTE_STATE, getEquipmentStateCode((String) (entry.getValue().get(EQUIPMENT_STATUS))));
			}
			json.add(SVG_ATTRIBUTE_DESC, "DEVICE_IP" + entry.getValue().get(EQUIPMENT_IP));
			// 只取一个设备信息
			break;
		}
		return json;
	}

	/*
	 * 构建monitor信息
	 */
	public List<JsonObject> BuildMonitorInfo() {
		List<JsonObject> jsons = new ArrayList<JsonObject>();
		for (String ip : this.Ips) {
			ip = ip.trim();
			if (ip.length() == 0) {
				continue;
			}
			KeyValue<List<JsonObject>, Integer> buildMonitorInfoFromIP = buildMonitorInfoFromIP(ip);
			List<JsonObject> temp = buildMonitorInfoFromIP.first;
			jsons.add(buildEquipmentInfoFromIP(ip, getDeviceStateByMonitorState(buildMonitorInfoFromIP.second)));
			Collections.sort(temp, new StateComparator(SortType.ASC));
			jsons.addAll(temp);
		}
		for (String group : this.Groups) {
			group = group.trim();
			if (group.length() == 0) {
				continue;
			}
			// 根据组查询组下所有设备的监测器
			KeyValue<List<JsonObject>, Integer> buildMonitorInfoFromGroup = buildMonitorInfoFromGroup(group);
			List<JsonObject> temp = buildMonitorInfoFromGroup.first;
			jsons.add(buildGroupInfo(group, getGroupStateByMonitorState(buildMonitorInfoFromGroup.second)));
			Collections.sort(temp, new StateComparator(SortType.ASC));
			jsons.addAll(temp);
			// jsons.add(emptyAsset(group));
		}
		for (String device : this.Devices) {
			device = device.trim();
			if (device.length() == 0) {
				continue;
			}
			// 根据设备title获取设备信息和监测器信息
			KeyValue<List<JsonObject>, Integer> buildMonitorInfoFromDeviceTitle = buildMonitorInfoFromDeviceTitle(device);
			jsons.add(buildEquipmentInfoFromDeviceTitle(device, buildMonitorInfoFromDeviceTitle.second));
			List<JsonObject> temp = buildMonitorInfoFromDeviceTitle.first;
			Collections.sort(temp, new StateComparator(SortType.ASC));
			jsons.addAll(temp);
		}
		return jsons;
	}

	private JsonObject buildLineInfoFromDeviceAddressAndMonitorTitle(String deviceAddress, String monitorTitle) {
		JsonObject json = new JsonObject();
		for (Entry<String, Map<String, Object>> entry : loadMonitorByDeviceAddressAndMonitorTitle(deviceAddress, monitorTitle).entrySet()) {
			json.add("line", deviceAddress + "&&" + monitorTitle);
			json.add(SVG_ATTRIBUTE_STATE, getLineStateCode((String) entry.getValue().get(MONITOR_STATUS)));
			json.add("interf", buildLineDesc(entry.getValue()));
			break;
		}
		return json;
	}

	private String buildLineDesc(Map<String, Object> info) {
		String message = (String) info.get("MESSAGE");
		if (message == null)
			return "";
		StringBuilder desc = new StringBuilder();
		String[] terms = message.split(",");
		for (String term : terms) {
			String[] keyValues = term.split("=");
			if (keyValues.length != 2)
				continue;
			String key = keyValues[0];
			String value = keyValues[1];
			if ("IF_INOCTET_SPEED".equals(key) || "NETWORK_IN_BYTES_PER_SECOND".equals(key)) {
				desc.append("IN_FLOW");
				desc.append(value);
				desc.append("kb/s");
				desc.append(",");
			}
			if ("IF_OUTOCTET_SPEED".equals(key) || "NETWORK_OUT_BYTES_PER_SECOND".equals(key)) {
				desc.append("OUT_FLOW");
				desc.append(value);
				desc.append("kb/s");
			}
		}
		return desc.toString();
	}

	public List<JsonObject> buildLineInfo() {
		List<JsonObject> jsons = new ArrayList<JsonObject>();
		for (String line : this.Lines) {
			if (line != null || line.trim().length() != 0) {
				if (line.indexOf("&&") != -1) {
					int first = line.indexOf("&&");
					String deviceAddress = line.split("&&")[0];
					String monitorTitle = line.substring(first + 2);
					jsons.add(buildLineInfoFromDeviceAddressAndMonitorTitle(deviceAddress, monitorTitle));
				} else {
				}
			}
		}
		return jsons;
	}

	private String buildAssetDesc(Map<String, Object> value) {

		String assetsCode = "资产编码";
		String maintenancePersonnel = "维保人员";
		String assetsStatus = "资产状态";
		String department = "部门";
		String location = "位置";
		String business = "业务流程";
		String brand = "品牌";
		String purpose = "资产用途";
		String startDate = "维保开始时间";
		String stopDate = "维保截止时间";
		String contact = "联系方式";
		String configure = "配置详情";

		String title = (String) value.get("ASSETSNAME");
		if (title == null || title.trim().length() == 0) {
			title = value.get("EQUIPMENTTITLE") == null || value.get("EQUIPMENTTITLE").toString().trim().length() == 0 ? "无" : value.get("EQUIPMENTTITLE").toString();
		}
		StringBuilder desc = new StringBuilder();
		desc.append("资产名称:");
		desc.append(title);
		desc.append("`");

		desc.append(assetsCode);
		desc.append(":");
		desc.append(value.get("ASSETSCODE") == null || value.get("ASSETSCODE").toString().trim().length() == 0 ? "无" : value.get("ASSETSCODE").toString());
		desc.append("`");

		desc.append(maintenancePersonnel);
		desc.append(":");
		desc.append(value.get("MAINTENANCEPERSONNEL") == null || value.get("MAINTENANCEPERSONNEL").toString().trim().length() == 0 ? "无" : value.get("MAINTENANCEPERSONNEL").toString());
		desc.append("`");

		desc.append(assetsStatus);
		desc.append(":");
		desc.append(value.get("ASSETSSTATUS") == null || value.get("ASSETSSTATUS").toString().trim().length() == 0 ? "无" : value.get("ASSETSSTATUS").toString());
		desc.append("`");

		desc.append(department);
		desc.append(":");
		desc.append(value.get("DEPARTMENTNAME") == null || value.get("DEPARTMENTNAME").toString().trim().length() == 0 ? "无" : value.get("DEPARTMENTNAME").toString());
		desc.append("`");

		desc.append(location);
		desc.append(":");
		desc.append(value.get("LOCATIONNAME") == null || value.get("LOCATIONNAME").toString().trim().length() == 0 ? "无" : value.get("LOCATIONNAME").toString());
		desc.append("`");

		desc.append(business);
		desc.append(":");
		desc.append(value.get("BUSPROCESSNAME") == null || value.get("BUSPROCESSNAME").toString().trim().length() == 0 ? "无" : value.get("BUSPROCESSNAME").toString());
		desc.append("`");

		desc.append(brand);
		desc.append(":");
		desc.append(value.get("BRANDNAME") == null || value.get("BRANDNAME").toString().trim().length() == 0 ? "无" : value.get("BRANDNAME").toString());
		desc.append("`");

		desc.append(purpose);
		desc.append(":");
		desc.append(value.get("ASSETSUSED") == null || value.get("ASSETSUSED").toString().trim().length() == 0 ? "无" : value.get("ASSETSUSED").toString());
		desc.append("`");

		desc.append(startDate);
		desc.append(":");
		desc.append(value.get("WARRANTYSTARTDATE") == null || value.get("WARRANTYSTARTDATE").toString().trim().length() == 0 ? "无" : dateFormat.format(value.get("WARRANTYSTARTDATE")));
		desc.append("`");

		desc.append(stopDate);
		desc.append(":");
		desc.append(value.get("WARRANTYENDDATE") == null || value.get("WARRANTYENDDATE").toString().trim().length() == 0 ? "无" : dateFormat.format(value.get("WARRANTYENDDATE")));
		desc.append("`");

		desc.append(contact);
		desc.append(":");
		desc.append(value.get("CONTACT") == null || value.get("CONTACT").toString().trim().length() == 0 ? "无" : value.get("CONTACT").toString());
		desc.append("`");

		desc.append(configure);
		desc.append(":");
		desc.append(value.get("CONFIGDETAILS") == null || value.get("CONFIGDETAILS").toString().trim().length() == 0 ? "无" : value.get("CONFIGDETAILS").toString());

		return desc.toString();
	}

	private static JsonObject emptyAsset(String name) {
		JsonObject json = new JsonObject();
		json.add(SVG_ATTRIBUTE_IP, name);
		json.add(SVG_ATTRIBUTE_STATE, "999");
		json.add(SVG_ATTRIBUTE_DESC, "无对应的资产");
		return json;
	}

	private Map<String, Map<String, Object>> customQueryByIP(String ip) {
		// System.out.println("Info:asset's ip is "+ip);
		ISiteviewApi api = getSiteviewApi();
		Map<String, Map<String, Object>> results = new HashMap<String, Map<String, Object>>();
		try {
			IDbDataParameterCollection parameters = new SqlParameterCollection();
			DataTable dataTable = api.get_NativeSQLSupportService().ExecuteNativeSQLQuery(String.format(CUSTOM_SQL_IP, ip), parameters);
			DataRowCollection rows = dataTable.get_Rows();
			for (DataRow row : rows) {
				Map<String, Object> result = new HashMap<String, Object>();
				for (Map.Entry<String, Object> entry : row.entrySet()) {
					result.put(entry.getKey().toUpperCase(), entry.getValue());
				}
				results.put((String) row.get("RECID"), result);
			}
			return results;
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
		return results;
	}

	private Map<String, Map<String, Object>> customQueryByName(String name) {
		// System.out.println("Info:asset's name is "+name);
		ISiteviewApi api = getSiteviewApi();
		Map<String, Map<String, Object>> results = new HashMap<String, Map<String, Object>>();
		try {
			IDbDataParameterCollection parameters = new SqlParameterCollection();
			DataTable dataTable = api.get_NativeSQLSupportService().ExecuteNativeSQLQuery(String.format(CUSTOM_SQL_NAME, name), parameters);
			DataRowCollection rows = dataTable.get_Rows();
			for (DataRow row : rows) {
				Map<String, Object> result = new HashMap<String, Object>();
				for (Map.Entry<String, Object> entry : row.entrySet()) {
					result.put(entry.getKey().toUpperCase(), entry.getValue());
				}
				results.put((String) row.get("RECID"), result);
			}
			return results;
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
		return results;
	}

	private List<JsonObject> buildAssetsInfoFromIp(String ip) {
		List<JsonObject> result = new ArrayList<JsonObject>();
		if (ip == null || ip.trim().length() == 0) {
			return result;
		}
		for (Map.Entry<String, Map<String, Object>> entry : customQueryByIP(ip).entrySet()) {
			for (String desc : buildAssetDesc(entry.getValue()).split("`")) {
				JsonObject json = new JsonObject();
				json.add(SVG_ATTRIBUTE_IP, ip);
				json.add(SVG_ATTRIBUTE_STATE, "999");
				json.add(SVG_ATTRIBUTE_DESC, desc);
				result.add(json);
			}
		}
		if (result.isEmpty()) {
			result.add(emptyAsset(ip));
		}
		return result;
	}

	private List<JsonObject> buildAssetsInfoFromName(String name) {
		List<JsonObject> result = new ArrayList<JsonObject>();
		if (name == null || name.trim().length() == 0) {
			return result;
		}
		for (Map.Entry<String, Map<String, Object>> entry : customQueryByName(name).entrySet()) {
			for (String desc : buildAssetDesc(entry.getValue()).split("`")) {
				JsonObject json = new JsonObject();
				json.add(SVG_ATTRIBUTE_IP, name);
				json.add(SVG_ATTRIBUTE_STATE, "999");
				json.add(SVG_ATTRIBUTE_DESC, desc);
				result.add(json);
			}
		}
		if (result.isEmpty()) {
			result.add(emptyAsset(name));
		}
		return result;
	}

	public List<JsonObject> buildAssetsInfo() {
		List<JsonObject> result = new ArrayList<JsonObject>();
		for (String ip : this.Ips) {
			result.addAll(buildAssetsInfoFromIp(ip));
		}
		for (String name : this.Devices) {
			result.addAll(buildAssetsInfoFromName(name));
		}
		return result;

	}

	private JsonObject buildEquipmentInfoFromIP(String ip, int deviceState) {
		JsonObject json = new JsonObject();
		json.add(SVG_ATTRIBUTE_IP, ip);
		json.add(SVG_ATTRIBUTE_DESC, "DEVICE_IP" + ip);
		for (Map.Entry<String, Map<String, Object>> entry : loadEquipmentByIP(ip).entrySet()) {
			if (deviceState != -1) {
				json.add(SVG_ATTRIBUTE_STATE, deviceState);
			} else {
				json.add(SVG_ATTRIBUTE_STATE, getEquipmentStateCode((String) entry.getValue().get(EQUIPMENT_STATUS)));
			}

			break;
		}
		return json;
	}

	// 根据状态描述字符串获取设备对应的状态码，svg可以根据状态码显示设备的状态（用颜色表示）
	private int getEquipmentStateCode(String status) {
		if (status == null)
			return -1;
		status = status.toUpperCase();
		if (EquipmentState.GOOD.getStateString().equals(status)) {
			return EquipmentState.GOOD.getStateCode();
		}
		if (EquipmentState.WARN.getStateString().equals(status)) {
			return EquipmentState.WARN.getStateCode();
		}
		if (EquipmentState.ERROR.getStateString().equals(status)) {
			return EquipmentState.ERROR.getStateCode();
		}
		if (EquipmentState.DISABLED.getStateString().equals(status)) {
			return EquipmentState.DISABLED.getStateCode();
		}
		return -1;
	}

	private int getLineStateCode(String status) {
		if (status == null)
			return -1;
		status = status.toUpperCase();
		if (MonitorState.GOOD.getStateString().equals(status)) {
			return EquipmentState.GOOD.getStateCode();
		}
		if (MonitorState.WARN.getStateString().equals(status)) {
			return EquipmentState.WARN.getStateCode();
		}
		if (MonitorState.ERROR.getStateString().equals(status)) {
			return EquipmentState.ERROR.getStateCode();
		}
		if (MonitorState.DISABLED.getStateString().equals(status)) {
			return EquipmentState.ERROR.getStateCode();
		}
		return -1;
	}

	// 根据状态描述字符串获取监测器对应的状态码，svg可以根据状态码显示设备的状态（用颜色表示）
	private int getMonitorStateCode(String status) {
		if (status == null)
			return -1;
		status = status.toUpperCase();
		if (MonitorState.GOOD.getStateString().equals(status)) {
			return MonitorState.GOOD.getStateCode();
		}
		if (MonitorState.WARN.getStateString().equals(status)) {
			return MonitorState.WARN.getStateCode();
		}
		if (MonitorState.ERROR.getStateString().equals(status)) {
			return MonitorState.ERROR.getStateCode();
		}
		if (MonitorState.DISABLED.getStateString().equals(status)) {
			return MonitorState.DISABLED.getStateCode();
		}
		return -1;
	}

	// 根据状态描述字符串获取Group对应的状态码，svg可以根据状态码显示Group的状态（用颜色表示）
	private int getGroupStateCode(String status) {
		if (status == null)
			return -1;
		status = status.toUpperCase();
		if (GroupState.GOOD.getStateString().equals(status)) {
			return GroupState.GOOD.getStateCode();
		}
		if (GroupState.WARN.getStateString().equals(status)) {
			return GroupState.WARN.getStateCode();
		}
		if (GroupState.ERROR.getStateString().equals(status)) {
			return GroupState.ERROR.getStateCode();
		}
		return -1;
	}

	private static int getDeviceStateByMonitorState(int monitorState) {
		switch (monitorState) {
		case 1:
			return EquipmentState.GOOD.getStateCode();
		case 2:
			return EquipmentState.WARN.getStateCode();
		case 3:
			return EquipmentState.ERROR.getStateCode();
		case 4:
			return EquipmentState.DISABLED.getStateCode();
		default:
			return -1;
		}
	}

	// 根据group下的监测器的状态获得group的状态.
	private int getGroupStateByMonitorState(int monitorState) {
		switch (monitorState) {
		case 1:
			return GroupState.GOOD.getStateCode();
		case 2:
			return GroupState.WARN.getStateCode();
		case 3:
			return GroupState.ERROR.getStateCode();
		case 4:
			return GroupState.ERROR.getStateCode();
		default:
			return -1;
		}
	}

	private static String buildDesc(Map<String, Object> info, String eccMonitorType) {
		String title = (String) info.get("TITLE");
		String message = (String) info.get("MESSAGE");
		if (title == null || message == null)
			return "";
		StringBuilder desc = new StringBuilder();
		desc.append(title);
		desc.append("::");

		String[] terms = message.split(",");
		int i = 0;
		for (String term : terms) {
			i++;
			String[] keyValues = term.split("=");
			if (keyValues.length != 2)
				continue;
			String key = keyValues[0];
			String value = keyValues[1];
			try {
				desc.append(InternationalUtils.getReturnValue(eccMonitorType, key));
			} catch (Exception e) {
				desc.append(key);
			}
			desc.append("=");
			desc.append(value);
			if (i != terms.length) {
				desc.append(",");
			}

		}
		return desc.toString();
	}

	// =================Equipment=================

	private Map<String, Map<String, Object>> loadEquipmentByIP(String ip) {
		ISiteviewApi api = getSiteviewApi();
		Map<String, Map<String, Object>> results = new HashMap<String, Map<String, Object>>();
		try {
			IDbDataParameterCollection parameters = new SqlParameterCollection();
			// parameters.add(new SqlParameter(SqlDataType.String,ip));
			DataTable dataTable = api.get_NativeSQLSupportService().ExecuteNativeSQLQuery(String.format(EQUIPMENT_GET_BY_IP, ip), parameters);
			for (DataRow row : dataTable.get_Rows()) {
				Map<String, Object> result = new HashMap<String, Object>();
				for (Map.Entry<String, Object> entry : row.entrySet()) {
					result.put(entry.getKey().toUpperCase(), entry.getValue());
				}
				results.put((String) row.get(EQUIPMENT_ID), result);
			}
			return results;
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
		return results;
	}

	private ISiteviewApi getSiteviewApi() {
		return this.siteApi;
	}

	private Map<String, Map<String, Object>> loadEquipmentByTitle(String title) {
		ISiteviewApi api = getSiteviewApi();
		Map<String, Map<String, Object>> results = new HashMap<String, Map<String, Object>>();
		try {
			IDbDataParameterCollection parameters = new SqlParameterCollection();
			// parameters.add(new SqlParameter(SqlDataType.String,title));
			DataTable dataTable = api.get_NativeSQLSupportService().ExecuteNativeSQLQuery(String.format(EQUIPMENT_IP_GET_BY_TITLE, title), parameters);
			for (DataRow row : dataTable.get_Rows()) {
				Map<String, Object> result = new HashMap<String, Object>();
				for (Map.Entry<String, Object> entry : row.entrySet()) {
					result.put(entry.getKey().toUpperCase(), entry.getValue());
				}
				results.put((String) row.get(EQUIPMENT_ID), result);
			}
			return results;
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
		return results;
	}

	private Map<String, Map<String, Object>> loadGroupByName(String groupname) {
		ISiteviewApi api = getSiteviewApi();
		Map<String, Map<String, Object>> results = new HashMap<String, Map<String, Object>>();
		try {

			IDbDataParameterCollection parameters = new SqlParameterCollection();
			// parameters.add(new SqlParameter(SqlDataType.String,groupname));
			DataTable dataTable = api.get_NativeSQLSupportService().ExecuteNativeSQLQuery(String.format(GROUP_GET_BY_NAME, groupname), parameters);
			for (DataRow row : dataTable.get_Rows()) {
				Map<String, Object> result = new HashMap<String, Object>();
				for (Map.Entry<String, Object> entry : row.entrySet()) {
					result.put(entry.getKey().toUpperCase(), entry.getValue());
				}
				results.put((String) row.get(GROUP_ID), result);
			}
			return results;
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
		return results;
	}

	// =======================Monitor=====================

	private Map<String, Map<String, Object>> loadMonitorByIp(String ip) {
		ISiteviewApi api = getSiteviewApi();
		Map<String, Map<String, Object>> results = new HashMap<String, Map<String, Object>>();
		try {
			// 查询符合条件的设备id，可能有多个
			IDbDataParameterCollection parameters = new SqlParameterCollection();
			// parameters.add(new SqlParameter(SqlDataType.String,ip));
			DataTable dataTable = api.get_NativeSQLSupportService().ExecuteNativeSQLQuery(String.format(MONITOR_GET_BY_IP, ip), parameters);
			DataRowCollection rows = dataTable.get_Rows();
			for (DataRow row : rows) {
				Map<String, Object> result = new HashMap<String, Object>();
				for (Map.Entry<String, Object> entry : row.entrySet()) {
					result.put(entry.getKey().toUpperCase(), entry.getValue());
				}
				results.put((String) row.get(MONITOR_ID), result);
			}
			return results;
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
		return results;
	}

	private Map<String, Map<String, Object>> loadMonitorByDeviceTitle(String deviceTitle) {
		ISiteviewApi api = getSiteviewApi();
		Map<String, Map<String, Object>> results = new HashMap<String, Map<String, Object>>();
		try {
			// 查询符合条件的设备id，可能有多个
			IDbDataParameterCollection parameters = new SqlParameterCollection();
			// parameters.add(new SqlParameter(SqlDataType.String,deviceTitle));
			DataTable dataTable = api.get_NativeSQLSupportService().ExecuteNativeSQLQuery(String.format(MONITOR_GET_BY_DEVICE_TITLE, deviceTitle), parameters);
			DataRowCollection rows = dataTable.get_Rows();
			for (DataRow row : rows) {
				Map<String, Object> result = new HashMap<String, Object>();
				for (Map.Entry<String, Object> entry : row.entrySet()) {
					result.put(entry.getKey().toUpperCase(), entry.getValue());
				}
				results.put((String) row.get(MONITOR_ID), result);
			}
			return results;
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
		return results;
	}

	private Map<String, Map<String, Object>> loadMonitorByGroup(String group) {
		ISiteviewApi api = getSiteviewApi();
		Map<String, Map<String, Object>> results = new HashMap<String, Map<String, Object>>();
		try {
			String groupId = null;
			DataTable dataTable = api.get_NativeSQLSupportService().ExecuteNativeSQLQuery(String.format(GET_GROUPID_BY_GROUPNAME, group), null);
			DataRowCollection rows = dataTable.get_Rows();
			if (rows.size() == 0)
				return results;
			for (DataRow row : rows) {
				groupId = (String) row.get("RECID");
				DatabaseEngine engine = api.get_NativeSQLSupportService().get_CurrentDatabaseEngine();
				String sql = "";
				if (engine.name().toLowerCase().equals("mysql")) {
					sql = MYSQL_CHILDGROUP_GET_BY_GROUP;
				} else {
					sql = CHILDGROUP_GET_BY_GROUP;
				}
				dataTable = api.get_NativeSQLSupportService().ExecuteNativeSQLQuery(String.format(sql, groupId), null);
				rows = dataTable.get_Rows();
				for (DataRow row1 : rows) {
					// 获取group的recid
					String groupId1 = (String) row1.get("RECID");
					// 根据组获取组下所有监测器的数据,返回所有的监测器信息
					// parameters.add(new
					// SqlParameter(SqlDataType.String,groupId));
					dataTable = api.get_NativeSQLSupportService().ExecuteNativeSQLQuery(String.format(MONITOR_GET_BY_GROUP, groupId1), null);
					for (DataRow row2 : dataTable.get_Rows()) {
						Map<String, Object> result = new HashMap<String, Object>();
						for (Map.Entry<String, Object> entry : row2.entrySet()) {
							result.put(entry.getKey().toUpperCase(), entry.getValue());
						}
						results.put((String) row2.get(MONITOR_ID), result);
					}
				}
			}
			return results;
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
		return results;
	}

	private Map<String, Map<String, Object>> loadMonitorByDeviceAddressAndMonitorTitle(String deviceAddress, String monitorTitle) {
		ISiteviewApi api = getSiteviewApi();
		Map<String, Map<String, Object>> results = new HashMap<String, Map<String, Object>>();
		try {
			IDbDataParameterCollection parameters = new SqlParameterCollection();
			// parameters.add(new
			// SqlParameter(SqlDataType.String,monitorTitle));
			// parameters.add(new
			// SqlParameter(SqlDataType.String,deviceAddress));
			DataTable dataTable = api.get_NativeSQLSupportService().ExecuteNativeSQLQuery(String.format(MONITOR_GET_BY_IP_MONITORTITLE, monitorTitle, deviceAddress), parameters);
			DataRowCollection rows = dataTable.get_Rows();
			for (DataRow row : rows) {
				Map<String, Object> result = new HashMap<String, Object>();
				for (Map.Entry<String, Object> entry : row.entrySet()) {
					result.put(entry.getKey().toUpperCase(), entry.getValue());
				}
				results.put((String) row.get(MONITOR_ID), result);
			}
			return results;
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
		return results;
	}

}
