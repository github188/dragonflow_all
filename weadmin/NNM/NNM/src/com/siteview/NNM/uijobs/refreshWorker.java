package com.siteview.NNM.uijobs;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.rap.json.JsonObject;

import Core.AutoTasks.Api.AutoTaskExecutor;
import Siteview.AutoTaskDef;
import Siteview.DefRequest;
import Siteview.SiteviewException;
import Siteview.SiteviewValue;
import Siteview.UpdateResult;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;

import com.siteview.ecc.kernel.scheduler.EccSchedulerBuilder;
import com.siteview.nnm.data.ConfigDB;
import com.siteview.nnm.data.model.TopoChart;
import com.siteview.nnm.data.model.svNode;
import com.siteview.security.SiteviewSecurityMgr;
import com.siteview.topo.TopoMap;
import com.siteview.topology.model.DeviceInfo;
import com.siteview.topology.model.SnmpPara;
import com.siteview.topology.scan.DeviceTypes;
import com.siteview.topology.snmp.pair;
import com.siteview.topology.snmp.snmpTools;
import com.siteview.topology.util.ScanUtils;
import com.siteview.utils.db.DBQueryUtils;

public class refreshWorker implements IRunnableWithProgress {

	refreshDevice redialog;
	String ip = "";
	String comm = "";
	String dport = "161";
	int nver = 2;
	TopoMap topochart = null;
	public Map<String, String> nentity = null;
	String nodeid;
	boolean isType = true;
	int svgtype = 5;
	String subtopo = "";
	boolean addnode = false;
	public JsonObject da;
	public ISiteviewApi api;
	int timeout = 10000;

	public refreshWorker(refreshDevice redialog, String nodeid, String lab,
			TopoMap topochart) {
		this.redialog = redialog;
		this.topochart = topochart;
		this.nodeid = nodeid;
		TopoChart tochart = com.siteview.nnm.data.DBManage.Topos.get(lab);
		svNode entityobj = (svNode) tochart.getNodes().get(nodeid);
		ip = entityobj.getLocalip();
		comm = entityobj.getProperys().get("Community");
		String dver = entityobj.getProperys().get("Version");
		if (dver.equals("1")) {
			nver = 1;
		} else if (dver.equals("2")) {
			nver = 2;
		}
		svgtype = entityobj.getSvgtype();
		dport = entityobj.getProperys().get("port");
		nentity = new HashMap<String, String>();
	}

	public refreshWorker(refreshDevice redialog, String ip, String comm,
			boolean isType, String subtopo, int timeout) {
		this.redialog = redialog;
		this.ip = ip;
		this.comm = comm;
		this.subtopo = subtopo;
		this.isType = isType;
		this.topochart = topochart;
		this.timeout = timeout;
		addnode = true;
		nentity = new HashMap<String, String>();
	}

	private void changeName(String value, boolean append) {
		redialog.changeValue(value, append);
	}

	private void savedata() {
		changeName("正在保存数据...", true);
		if (addnode) {
			try {
				da = com.siteview.nnm.data.DBManage.createnode(this.subtopo,
						nentity);
				this.nodeid = da.get("nid").asString();
				if (nentity.get("snmpEnabled").equals("1")) {
					String os = nentity.get("manufact");
					if (os.equals("Microsoft"))
						os = "windows";
					String groupname = "服务器";

					if (nentity.get("type").equals("0")) {
						groupname = "三层交换机";
					} else if (nentity.get("type").equals("1")) {
						groupname = "二层交换机";
					} else if (nentity.get("type").equals("2")) {
						groupname = "路由器";
					} else if (nentity.get("type").equals("3")) {
						groupname = "防火墙";
					}
					String groupid;
					try {
						groupid = getGroup(groupname, api);
						Divice2SAM(groupid, nentity.get("ip"), "161", os,
								nentity.get("Community"));
					} catch (SiteviewException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			} catch (Exception ex) {

			}
		}
	}

	private String getGroup(String grouName, ISiteviewApi api)
			throws SiteviewException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("groupName", grouName);
		map.put("ParentGroupId", "");
		Collection<BusinessObject> coll = DBQueryUtils.getBussCollection(map,
				"EccGroup", api);
		BusinessObject groupBo = null;
		if (coll != null && !coll.isEmpty()) {
			Iterator<BusinessObject> iter = coll.iterator();
			groupBo = iter.next();
		}

		if (groupBo == null) {
			groupBo = api.get_BusObService().Create("EccGroup");
			groupBo.GetField("GroupName").SetValue(new SiteviewValue(grouName));
			groupBo.GetField("Status").SetValue(new SiteviewValue("good"));
			groupBo.GetField("BelongID").SetValue(
					new SiteviewValue(DBQueryUtils.getUser(api).get_BusObId()));
			UpdateResult rr = groupBo.SaveObject(api, true, true);

			StringBuffer sb = new StringBuffer();
			String monitorType = groupBo.get_Name().substring(
					groupBo.get_Name().indexOf(".") + 1,
					groupBo.get_Name().length());
			AutoTaskDef def;
			try {
				def = (AutoTaskDef) api.get_LiveDefinitionLibrary()
						.GetDefinition(
								DefRequest
										.ByName("AutoTaskDef",
												sb.append(monitorType)
														.append("Extension")
														.toString()));
				if (def != null) {
					AutoTaskExecutor
							.ExecuteTask(api, groupBo, def, null, false);
				}
			} catch (SiteviewException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return groupBo.get_RecId();
	}

	private void Divice2SAM(String groupid, String ip, String port, String os,
			String Community) {
		String boName = "";
		changeName("正在添加设备到SAM...", true);
		BusinessObject equipmentBo = null;
		String equipmentModel = "";
		boolean isVerity = false;
		if (os.toLowerCase().contains("linux")) {
			os = "Linux";
			boName = "SNMPUnix";
			isVerity = false;
		} else if (os.toLowerCase().contains("windows")) {
			os = "Windows";
			boName = "SNMPWindows";
			isVerity = false;
		} else if (os.toLowerCase().contains("cisco")) {
			os = "cisco";
			boName = "Network";
			equipmentModel = "other";
			isVerity = true;
		} else if (os.toLowerCase().contains("huawei")) {
			os = "Huawei";
			boName = "Network";
			equipmentModel = "other";
			isVerity = true;
		} else if (os.toLowerCase().contains("h3c")) {
			os = "H3C";
			boName = "Network";
			equipmentModel = "other";
			isVerity = true;
		} else {

			boName = "Network";
			isVerity = true;
		}
		Throwable thrown = null;
		boolean addmonitor = false;
		// no transaction is necessary since runSync handles this
		try {
			final String belongId = DBQueryUtils.getUser(api).get_BusObId();
			if (!Community.isEmpty()) {
				boolean validate = true;
				if (isVerity) {
					validate = SiteviewSecurityMgr.getInstance()
							.validateAddEquipment(api, boName);
				}
				if (validate) {
					try {
						equipmentBo = DBQueryUtils.queryOnlyBo("ServerAddress",
								ip, "Equipment", api);
					} catch (Exception e) {
						System.err.println(e.getMessage());
					}
					if (equipmentBo == null) {
						// get the dispatcher and invoke the service via runSync
						// --
						// will
						// run
						addmonitor = true;
						equipmentBo = api.get_BusObService().Create(
								"Equipment");
						equipmentBo.GetField("EquipmentType").SetValue(
								new SiteviewValue(boName));
						equipmentBo.GetField("GroupId").SetValue(
								new SiteviewValue(groupid));
//						equipmentBo.GetField("GroupName").SetValue(
//								new SiteviewValue("scan"));
						// equipmentBo.GetField("GroupName").SetValue(
						// new SiteviewValue(groupBo.GetField("GroupName")
						// .get_Value().toString()));
						equipmentBo.GetField("ServerAddress").SetValue(
								new SiteviewValue(ip));
						equipmentBo.GetField("BelongID").SetValue(
								new SiteviewValue(belongId));
						if (!"".equals(equipmentModel)) {
							equipmentBo.GetField("EquipmentModel").SetValue(
									new SiteviewValue((equipmentModel)));//
						}
						equipmentBo.GetField("edition").SetValue(
								new SiteviewValue(("V2")));//
						equipmentBo.GetField("Community").SetValue(
								new SiteviewValue(Community));
						equipmentBo.GetField("Port").SetValue(
								new SiteviewValue(port));
						equipmentBo.GetField("os").SetValue(
								new SiteviewValue(os));
						equipmentBo.GetField("title").SetValue(
								new SiteviewValue(ip + "(" + boName + ")"));
						equipmentBo.SaveObject(api, true, true, false, false,
								false);
						AutoTaskDef def;
						try {
							def = (AutoTaskDef) api.get_LiveDefinitionLibrary()
									.GetDefinition(
											DefRequest.ByName("AutoTaskDef",
													"AddEquipment"));
							if (def != null) {
								AutoTaskExecutor.ExecuteTask(api, equipmentBo,
										def, null, false);
							}
						} catch (SiteviewException e) {
							e.printStackTrace();
						}
					} else // update
					{

					}
				}
			}

		} catch (Throwable t) {
			thrown = t;

		}
		if (thrown == null) {

			try {
				add2ALM(ip, equipmentBo, boName);
			} catch (SiteviewException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (addmonitor)
				addmonitor(equipmentBo);
		}
	}

	private void add2ALM(String ip, BusinessObject equipmentBo, String boname)
			throws SiteviewException {
		changeName("正在添加设备到ALM...", true);
		BusinessObject assetsListBo = null;
		String EquipmentTypeID = "";
		try {
			assetsListBo = DBQueryUtils.queryOnlyBo("IPAddress", ip,
					"AssetsList", api);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		if (assetsListBo == null) {
			BusinessObject equipmentTypeRelBo = DBQueryUtils.queryOnlyBo(
					"EqName", boname, "EquipmentTypeRel", api);
			if (equipmentTypeRelBo != null) {
				EquipmentTypeID = equipmentTypeRelBo.get_RecId();
			}
			assetsListBo = api.get_BusObService().Create("AssetsList");
			assetsListBo.GetField("IPAddress").SetValue(new SiteviewValue(ip));
			assetsListBo.GetField("EquipmentVersion").SetValue(
					new SiteviewValue("V2"));
			assetsListBo.GetField("EquipmentTypeID").SetValue(
					new SiteviewValue(EquipmentTypeID));
		}
		if (equipmentBo != null) {
			// String equipmentType = equipmentBo.GetField("EquipmentType")
			// .get_Value().toString();
			// BusinessObject equipmentTypeRelBo = DBQueryUtils.queryOnlyBo(
			// "EqName", equipmentType, "EquipmentTypeRel", api);
			// if (equipmentTypeRelBo != null) {
			// assetsListBo.GetField("EquipmentTypeID").SetValue(
			// new SiteviewValue(equipmentTypeRelBo.get_RecId()));
			// }
			assetsListBo.GetField("EquipmentID").SetValue(
					new SiteviewValue(equipmentBo.get_RecId()));

			assetsListBo.GetField("EquipmentTitle").SetValue(
					new SiteviewValue(equipmentBo.GetField("title").get_Value()
							.toString()));
		}
		assetsListBo.SaveObject(api, true, true, false, false, false);
	}

	private void addmonitor(BusinessObject device) {
		changeName("正在添加默认监测器...", true);
		try {
			Collection<BusinessObject> coll = DBQueryUtils.getBussCollection(
					"range", "Equipment."
							+ device.GetField("Equipmenttype").get_Value()
									.toString(), "EccMonitorList", api);
			Iterator<BusinessObject> it = coll.iterator();
			while (it.hasNext()) {
				BusinessObject bo = it.next();
				String automatic = bo.GetField("Automatic").get_Value()
						.toString();
				if ("True".equals(automatic)) {
					String monitorName = bo.GetField("MonitorName").get_Value()
							.toString();
					String MonitorTableName = bo.GetField("MonitorTableName")
							.get_Value().toString();
					String monitorpackage=bo.GetField("description")
							.get_Value().toString();
					String IsKeepalive=	bo.GetField("IsKeepalive")
							.get_Value().toString();	
					setMonitorInfo(MonitorTableName, monitorName, device, api,monitorpackage,IsKeepalive);
				}
			}
		} catch (Throwable t) {

		}
	}

	private void setMonitorInfo(String monitorName, String title,
			BusinessObject equipmentBo, ISiteviewApi api,String monitorpackage,String isKeepalive) {
		try {
			if (SiteviewSecurityMgr.getInstance().validateAddMonitor(
					api,
					equipmentBo.GetField("EquipmentType").get_NativeValue()
							.toString(), equipmentBo.get_RecId())) {
				String equipmentID = equipmentBo.get_RecId();
				String groups = equipmentBo.GetField("GroupId").get_Value()
						.toString();
//				String groupName = equipmentBo.GetField("GroupName")
//						.get_Value().toString();
				BusinessObject bo = api.get_BusObService().Create("Monitor");
				if (monitorName.toLowerCase().contains("ping")) {
					bo.GetField("HostName").SetValue(
							new SiteviewValue(equipmentBo
									.GetField("ServerAddress").get_Value()
									.toString()));
				}
				bo.GetField("IsKeepalive").SetValue(new SiteviewValue(isKeepalive));
				bo.GetField("MonitorPackage").SetValue(new SiteviewValue(monitorpackage));
				bo.GetField("MonitorType").SetValue(new SiteviewValue(monitorName.substring(11)));
				bo.GetField("EquipmentId").SetValue(new SiteviewValue(equipmentID));
				bo.GetField("GroupId").SetValue(new SiteviewValue(groups));
				bo.GetField("BelongID").SetValue(
						new SiteviewValue(DBQueryUtils.getUser(api)
								.get_BusObId()));
				bo.GetField("Title").SetValue(new SiteviewValue(title));
				initCreateAlarmDef(bo, api);
				bo.SaveObject(api, true, true, false, false, false);
				EccSchedulerBuilder.start(bo, api);
				monitorAutoTaskExecutor(bo, api);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void monitorAutoTaskExecutor(BusinessObject bo, ISiteviewApi api) {
		StringBuffer sb = new StringBuffer();
		String monitorType = bo.get_Name().substring(
				bo.get_Name().indexOf(".") + 1, bo.get_Name().length());
		AutoTaskDef def;
		try {
			def = (AutoTaskDef) api.get_LiveDefinitionLibrary().GetDefinition(
					DefRequest.ByName("AutoTaskDef", sb.append(monitorType)
							.append("Extension").toString()));
			if (def != null) {
				AutoTaskExecutor.ExecuteTask(api, bo, def, null, false);
			}
		} catch (SiteviewException e) {
			e.printStackTrace();
		}

	}

	/*
	 * 创建默认报警条件
	 */
	public void initCreateAlarmDef(BusinessObject bo, ISiteviewApi api)
			throws SiteviewException {
		String monitorName = bo.get_Name();
		if (monitorName.contains("EccMonitor.")) {
			monitorName = monitorName.replace("EccMonitor.", "");
		}
		Collection<?> ico = DBQueryUtils.getBussCollection("MonitorType",
				monitorName, "EccAlarmCondition", api);
		Iterator<?> ien = ico.iterator();
		while (ien.hasNext()) {
			BusinessObject def = (BusinessObject) ien.next();
			BusinessObject alarm = api.get_BusObService().Create("Alarm");
			alarm.GetField("Monitorid").SetValue(
					new SiteviewValue(bo.get_RecId()));
			alarm.GetField("MonitorType").SetValue(
					new SiteviewValue(monitorName));
			alarm.GetField("AlarmStatus").SetValue(
					new SiteviewValue(def.GetField("AlarmStatus")
							.get_NativeValue().toString()));
			alarm.GetField("AlramValue").SetValue(
					new SiteviewValue(def.GetField("AlramValue")
							.get_NativeValue().toString()));
			alarm.GetField("Operator").SetValue(
					new SiteviewValue(def.GetField("Operators")
							.get_NativeValue().toString()));
			alarm.GetField("ReturnValue").SetValue(
					new SiteviewValue(def.GetField("SaveReturnValue")
							.get_NativeValue().toString()));
			alarm.SaveObject(api, Boolean.valueOf(false), Boolean.valueOf(true));
		}
	}

	private void getDeviceType() {
		changeName("正在获取设备类型...", true);
		if (comm == null || comm.isEmpty()) {
			return;
		}
		SnmpPara para = new SnmpPara(ip, comm, timeout, 3, nver);
		changeName("设备参数..." + ip+" "+comm+" "+timeout+" "+nver , true);
		String oidStr = "1.3.6.1.2.1.1.2.0";
		String sysOid = snmpTools.getValue(para, oidStr);
		if(sysOid == null || sysOid.isEmpty()){
			changeName("设备类型...null"  , true);
		}else{
		changeName("设备类型..." + sysOid, true);
		}
//		if (sysOid == null) {
//			sysOid = snmpTools.getNextValue(para, oidStr);
//		}
		nentity.put("manufact", "");
		nentity.put("model", "");
		nentity.put("sysObjectID", "");
		nentity.put("type", "");
		nentity.put("snmpEnabled", "1");
		if (sysOid != null && !sysOid.isEmpty()) {
			nentity.put("sysObjectID", sysOid);
			if (DeviceTypes.devtype_map.containsKey(sysOid)) {
				DeviceInfo pro = DeviceTypes.devtype_map.get(sysOid);
				String devtype_res = pro.getDevType();
				String factory_res = pro.getFactory();
				nentity.put("manufact", factory_res);
				String model_res = pro.getRomVersion();
				nentity.put("model", model_res);
				nentity.put("type", devtype_res);
			} else {
				String fact = "";
				if (sysOid.startsWith("1.3.6.1.4.1.9.")) {
					fact = "Cisco";
				} else if (sysOid.startsWith("1.3.6.1.4.1.2011.")) {
					fact = "Huawei";
				} else if (sysOid.startsWith("1.3.6.1.4.1.43.")) {
					fact = "H3C";
				} else if (sysOid.startsWith("1.3.6.1.4.1.25506.")) {
					fact = "H3C";
				} else if (sysOid.startsWith("1.3.6.1.4.1.3902.")) {
					fact = "zte";
				} else if (sysOid.startsWith("1.3.6.1.4.1.4881.")) {
					fact = "锐捷";
				}
				nentity.put("manufact", fact);
				String sysSvcs = snmpTools.getValue(para, "1.3.6.1.2.1.1.7.0");
				int isvc = Integer.parseInt(sysSvcs);
				String devtype_res = "5";
				if (isvc == 0) {
					devtype_res = "5";// host
				} else if ((isvc & 6) == 6) {
					devtype_res = "0"; // "ROUTE-SWITCH"
				} else if ((isvc & 4) == 4) {
					devtype_res = "2"; // ROUTER
				} else if ((isvc & 2) == 2) {
					devtype_res = "1"; // "SWITCH"
				} else {
					devtype_res = "5"; // host

				}
				nentity.put("type", devtype_res);
			}
			if (nentity.get("type").equals("5"))
				nentity.put("type", "4");
		} else {
			nentity.put("snmpEnabled", "0");
		}
	}

	private void getInterface() {
		// 端口
		changeName("正在获取设备接口...", true);
		if (comm == null || comm.isEmpty()) {
			return;
		}
		SnmpPara para = new SnmpPara(ip, comm, timeout, 3, nver);
		String oidStr = "1.3.6.1.2.1.2.2.1.1";
		Vector<pair> svIndexs = snmpTools.getTable(para, oidStr);
		oidStr = "1.3.6.1.2.1.2.2.1.2";
		Vector<pair> svDescrs = snmpTools.getTable(para, oidStr);
		oidStr = "1.3.6.1.2.1.2.2.1.3";
		Vector<pair> svTypes = snmpTools.getTable(para, oidStr);
		oidStr = "1.3.6.1.2.1.2.2.1.5";
		Vector<pair> svSpeeds = snmpTools.getTable(para, oidStr);
		oidStr = "1.3.6.1.2.1.2.2.1.6";
		Vector<pair> svMacs = snmpTools.getTable(para, oidStr);
		oidStr = "1.3.6.1.2.1.31.1.1.1.18";
		Vector<pair> svAlias = snmpTools.getTable(para, oidStr);
		Connection conn = ConfigDB.getConn();
		ConfigDB.delete("delete from ports where id='" + this.nodeid + "'",
				conn);
		try {
			PreparedStatement prep = conn
					.prepareStatement("insert into ports (id,desc,pindex,porttype,mac,speed,alias) values (?, ?, ?, ?, ?, ?,?);");
			for (int i = 0; i < svIndexs.size(); i++) {
				String desc = svDescrs.get(i).getValue();
				if (desc == null)
					desc = "";
				String index = svIndexs.get(i).getValue();
				if (index == null)
					index = "";
				String portype = svTypes.get(i).getValue();
				if (portype == null)
					portype = "";
				String mac = svMacs.get(i).getValue();
				if (mac == null)
					mac = "";
				mac = mac.replaceAll("\\.", "").toUpperCase();
				String speed = svSpeeds.get(i).getValue();
				String alias= svAlias.get(i).getValue();
				if(alias==null)
					alias="";
				prep.setString(1, this.nodeid);
				prep.setString(2, desc);
				prep.setString(3, index);
				prep.setString(4, portype);
				prep.setString(5, mac);
				prep.setString(6, speed);
				prep.setString(7, alias);
				prep.addBatch();
			}
			conn.setAutoCommit(false);
			prep.executeBatch();
			conn.setAutoCommit(true);
		} catch (Exception ex) {

		}
		ConfigDB.close(conn);
		changeName("刷新完成...", true);
	}

	private void getDeviceInfo() {
		changeName("正在获取设备详细...", true);
		// 设备名称
		if (comm == null || comm.isEmpty()) {
			return;
		}
		SnmpPara para = new SnmpPara(ip, comm, timeout, 3, nver);
		String oidStr = "1.3.6.1.2.1.1.5.0";
		String sysname = snmpTools.getValue(para, oidStr);
//		if (sysname == null) {
//			sysname = snmpTools.getNextValue(para, oidStr);
//		}
		nentity.put("name", "");
		if (sysname != null && !sysname.isEmpty()) {
			nentity.put("name", sysname);
			changeName("设备名称..." + sysname, true);
		}
		// 设备描述
		oidStr = "1.3.6.1.2.1.1.1.0";
		String sysdesc = snmpTools.getValue(para, oidStr);
//		if (sysdesc == null) {
//			sysdesc = snmpTools.getNextValue(para, oidStr);
//		}
		if (sysdesc == null)
			sysdesc = "";
		changeName("设备描述..." + sysdesc, true);
		Connection conn = ConfigDB.getConn();
		ConfigDB.delete("delete from devices where nid='" + this.nodeid + "'",
				conn);
		try {
			PreparedStatement prep = conn
					.prepareStatement("insert into devices (nid,descrip,username,password,authorization,privacy,privacypass) values (?, ? , ?, ?,?,?,?);");
			prep.setString(1, this.nodeid);
			prep.setString(2, sysdesc);
			prep.setString(3, "");
			prep.setString(4, "");
			prep.setString(5, "");
			prep.setString(6, "");
			prep.setString(7, "");
			prep.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 设备厂商
		if (nentity.get("manufact") == null
				|| nentity.get("manufact").isEmpty()) {
			String manufact = "";
			if (sysdesc.indexOf("cisco") >= 0) {
				manufact = "Cisco";
			} else if (sysdesc.indexOf("checkpoint") >= 0) {
				manufact = "CheckPoint";
			} else if (sysdesc.indexOf("netscreen") >= 0) {
				manufact = "Netscreen";
			} else if (sysdesc.indexOf("f5 networks") >= 0) {
				manufact = "F5 Networks";
			} else if (sysdesc.indexOf("alteon") >= 0) {
				manufact = "Alteon";
			} else if (sysdesc.indexOf("huawei") >= 0) {
				manufact = "Huawei";
			} else if (sysdesc.indexOf("foundry") >= 0) {
				manufact = "Foundry";
			} else if (sysdesc.indexOf("nortel") >= 0) {
				manufact = "Nortel";
			} else if (sysdesc.indexOf("microsoft") >= 0) {
				manufact = "Microsoft";
			} else if (sysdesc.indexOf("linux") >= 0) {
				manufact = "Linux";
			}
			nentity.put("manufact", manufact);
		}
		changeName("设备厂商..." + nentity.get("manufact"), true);
		String ipList = "";
		String subnetName = "";
		String macList = "";
		String maskField = "";
		oidStr = "1.3.6.1.2.1.4.20.1.1";
		Vector<pair> svIpTable = snmpTools.getTable(para, oidStr);
		oidStr = "1.3.6.1.2.1.4.20.1.3";
		Vector<pair> svMaskTable = snmpTools.getTable(para, oidStr);
		try {
			for (int j = 0; j < svIpTable.size(); j++) {
				ipList = ipList + svIpTable.get(j).getValue() + ",";
				maskField = maskField + svMaskTable.get(j).getValue() + ",";
				String ip = svIpTable.get(j).getValue();
				String mask = svMaskTable.get(j).getValue();
				if (ip != null && !ip.isEmpty() && mask != null
						&& !mask.isEmpty()) {
					String vv = ScanUtils.getSubnetByIPMask(ip, mask);
					subnetName = subnetName + vv + ",";
				}
			}
		} catch (Exception ex) {
			System.err.print(ex.getMessage());
		}
		nentity.put("ipList", ipList);
		changeName("ipList..." + ipList, true);
		nentity.put("maskField", maskField);
		changeName("maskField..." + maskField, true);
		nentity.put("subnetName", subnetName);
		oidStr = "1.3.6.1.2.1.2.2.1.6";
		changeName("subnetName..." + subnetName, true);
		Vector<pair> svMacTable = snmpTools.getTable(para, oidStr);
		try {
			for (int n = 0; n < svMacTable.size(); n++) {
				if (svMacTable.get(n).getKey().length() > 20) {
					if (svMacTable.get(n).getValue().length() < 12) {
						macList = macList
								+ svMacTable.get(n).getValue().toUpperCase()
								+ ",";
					} else {
						macList = macList
								+ svMacTable.get(n).getValue()
										.replaceAll(":", "").substring(0, 12)
										.toUpperCase() + ",";
					}
				}
			}
		} catch (Exception ex) {
			System.err.print(ex.getMessage());
		}
		nentity.put("macList", macList);
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		// monitor.beginTask("刷新设备...", 100);
		if (this.addnode) {
			if (this.isType)
				getDeviceType();
			// monitor.worked(20);
			if (monitor.isCanceled())
				return;
			getDeviceInfo();
			if (monitor.isCanceled())
				return;
			if (nentity.get("name").isEmpty()
					&& nentity.get("manufact").isEmpty()
					&& nentity.get("ipList").isEmpty() &&
					nentity.get("macList").isEmpty()) {
				changeName("未能成功获取设备信息!" , true);

			} else {
				// monitor.worked(70);
				savedata();
				if (monitor.isCanceled())
					return;
				// monitor.worked(90);
				getInterface();
			}
		} else {
			if (this.svgtype == 0 || this.svgtype == 1 || this.svgtype == 2
					|| this.svgtype == 3 || this.svgtype == 4) {
				if (this.isType)
					getDeviceType();
				if (monitor.isCanceled())
					return;
				// monitor.worked(20);
				getDeviceInfo();
				if (monitor.isCanceled())
					return;
				// monitor.worked(70);
				savedata();
				if (monitor.isCanceled())
					return;
				// monitor.worked(90);
				getInterface();
			}
		}
		// / monitor.worked(100);
		// TODO Auto-generated method stub

	}

}
