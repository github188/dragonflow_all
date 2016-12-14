package com.siteview.NNM.dialogs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;

import Core.AutoTasks.Api.AutoTaskExecutor;
import Siteview.AutoTaskDef;
import Siteview.DefRequest;
import Siteview.SiteviewException;
import Siteview.SiteviewValue;
import Siteview.UpdateResult;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ConnectionBroker;

import com.siteview.NNM.uijobs.refreshDevice;
import com.siteview.NNM.uijobs.refreshWorker;
import com.siteview.ecc.kernel.scheduler.EccSchedulerBuilder;
import com.siteview.nnm.data.ConfigDB;
import com.siteview.security.SiteviewSecurityMgr;
import com.siteview.topo.TopoMap;
import com.siteview.topology.model.DeviceInfo;
import com.siteview.topology.model.SnmpPara;
import com.siteview.topology.scan.DeviceTypes;
import com.siteview.topology.snmp.pair;
import com.siteview.topology.snmp.snmpTools;
import com.siteview.topology.util.ScanUtils;
import com.siteview.utils.db.DBQueryUtils;

import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Button;

import siteview.windows.forms.MsgBox;

public class AddDevice extends Dialog {
	private Text textIP;
	private Text txtPublic;
	Spinner spinner;
	TopoMap topochart;
	String subtopo;
	Combo combotype;
	JsonArray data = null;
	Map<String, String> nentity = null;
	String sysdesc = "";
	String devID = "";
	Label labelprogress;
	ISiteviewApi api;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public AddDevice(Shell parentShell, TopoMap topochart, JsonArray data,
			String subtopo) {
		super(parentShell);
		this.data = data;
		this.topochart = topochart;
		this.subtopo = subtopo;
		this.nentity = new HashMap<String, String>();
	}

	protected void configureShell(Shell newShell) {
		// newShell.setSize(450, 320);
		// newShell.setLocation(400, 175);
		newShell.setText("添加设备");
		super.configureShell(newShell);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		Group group = new Group(container, SWT.NONE);
		group.setText("\u8BBE\u5907\u4FE1\u606F");
		GridData gd_group = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1,
				1);
		gd_group.heightHint = 213;
		gd_group.widthHint = 414;
		group.setLayoutData(gd_group);

		Label lblNewLabel = new Label(group, SWT.NONE);
		lblNewLabel.setBounds(30, 39, 85, 15);
		lblNewLabel.setText("\u8BBE\u5907IP\u5730\u5740\uFF1A");

		Label label = new Label(group, SWT.NONE);
		label.setText("\u8BBE\u5907\u7C7B\u578B\uFF1A");
		label.setBounds(30, 87, 72, 15);

		Label lblget = new Label(group, SWT.NONE);
		lblget.setText("\u5171\u540C\u4F53\u540DGET\uFF1A");
		lblget.setBounds(30, 138, 99, 15);
		lblget.setBounds(30, 138, 99, 15);

		textIP = new Text(group, SWT.BORDER);
		textIP.setBounds(146, 33, 230, 21);

		txtPublic = new Text(group, SWT.BORDER);
		txtPublic.setText("public");
		txtPublic.setBounds(146, 132, 230, 21);

		combotype = new Combo(group, SWT.NONE);
		combotype.setItems(new String[] {
				"\u81EA\u52A8\u5224\u5B9A(\u63A8\u8350)",
				"\u4E09\u5C42\u4EA4\u6362\u673A",
				"\u4E8C\u5C42\u4EA4\u6362\u673A", "\u8DEF\u7531\u5668",
				"\u9632\u706B\u5899", "\u670D\u52A1\u5668", "PC\u7EC8\u7AEF",
				"\u5176\u4ED6" });
		combotype.setBounds(146, 79, 229, 23);
		combotype.select(0);

		labelprogress = new Label(group, SWT.NONE);
		labelprogress.setBounds(30, 187, 85, 15);
		labelprogress.setText("进度...");
		labelprogress.setVisible(false);
		
		Label lblms = new Label(group, SWT.NONE);
		lblms.setText("超时(ms)：");
		lblms.setBounds(30, 187, 99, 15);
		
		spinner = new Spinner(group, SWT.BORDER);
		spinner.setMaximum(9000000);
		spinner.setMinimum(10);
		spinner.setSelection(5000);
		spinner.setBounds(146, 184, 141, 25);
		
		Button btnNewButton = new Button(group, SWT.NONE);
		btnNewButton.setBounds(306, 182, 63, 25);
		btnNewButton.setText("测试");
		btnNewButton.addListener(SWT.MouseUp, new Listener() {
			@Override
			public void handleEvent(Event event) {
				SnmpPara para = new SnmpPara(textIP.getText().trim(), txtPublic
						.getText().trim(), spinner.getSelection(), 2, 2);
				String oidStr = "1.3.6.1.2.1.1.2.0";
				String sysOid = snmpTools.getValue(para, oidStr);
				MsgBox.ShowMessage("设备oid: "+sysOid);
			}

		});
		return container;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "添加", true);
		createButton(parent, IDialogConstants.CANCEL_ID, "取消", true);
	}

	protected void buttonPressed(int buttonId) {
		try {
			if (buttonId == IDialogConstants.OK_ID) {

				// this.getShell().getDisplay().syncExec(runnable);
				int nx = 0, ny = 0;
				if (data != null) {
					nx = (int) data.get(0).asFloat();
					ny = (int) data.get(1).asFloat();

				}
				int type1 = 0;
				boolean isType = true;
				if (combotype.getSelectionIndex() == 0) {
					type1 = 5;
				} else {
					isType = false;
					type1 = combotype.getSelectionIndex() - 1;
				}
				nentity.put("nx", nx + "");
				nentity.put("ny", ny + "");
				nentity.put("ip", textIP.getText().trim());
				nentity.put("Community", txtPublic.getText().trim());
				nentity.put("type", type1 + "");
				nentity.put("name", "");
				nentity.put("ipList", "");
				nentity.put("model", "");
				nentity.put("subnetName", "");
				nentity.put("manufact", "");
				nentity.put("sysObjectID", "");
				nentity.put("macList", "");
				nentity.put("maskField", "");
				nentity.put("macList", "");
				if (combotype.getSelectionIndex() == 7) {
					nentity.put("snmpEnabled", "0");
					JsonObject da=com.siteview.nnm.data.DBManage.createnode(
							this.subtopo, nentity);
					this.topochart.setNewdevice(da);
				} else {
					nentity.put("snmpEnabled", "1");
					refreshDevice waitdialog = new refreshDevice(getShell());
					waitdialog.setCancelable(true);
					api = ConnectionBroker.get_SiteviewApi();
					int timeout=spinner.getSelection();
					refreshWorker woker1 = new refreshWorker(waitdialog, textIP
							.getText().trim(), txtPublic.getText().trim(),
							isType, this.subtopo,timeout);
					woker1.nentity.putAll(nentity);
					woker1.api=api;
					try {
						waitdialog.run(true, true, woker1);
					} catch (Exception e) {
					}
					this.topochart.setNewdevice(woker1.da);
					
				}

			
				//
				// JsonObject da = com.siteview.nnm.data.DBManage.createnode(
				// this.subtopo, nentity);
				// devID = da.get("nid").asString();
				//
				// getInterface();
				// topochart.setNewdevice(da);
			}

			this.close();
		} catch (Exception e) {
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
					AutoTaskExecutor.ExecuteTask(api, groupBo, def, null, false);
				}
			} catch (SiteviewException e) {
				e.printStackTrace();
			}

		}
		return groupBo.get_RecId();
	}

    private void Divice2SAM(String groupid,String ip,String port,String os,String Community){
    	String boName = "";
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
		}else if (os.toLowerCase().contains("h3c")) {
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
			// api = ConnectionBroker.get_SiteviewApi();
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
						equipmentBo.GetField("EquipmentType").SetValue(new SiteviewValue(boName));
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
			thrown=t;
			
		}
		if (thrown == null) {

			try {
				add2ALM(ip, equipmentBo, boName);
			} catch (SiteviewException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(addmonitor)
			addmonitor(equipmentBo);
		} 
    }
    private void add2ALM( String ip,BusinessObject equipmentBo,String boname) throws SiteviewException{
    	BusinessObject assetsListBo = null;
		String EquipmentTypeID = "";
		try {
			assetsListBo = DBQueryUtils.queryOnlyBo("IPAddress",
					ip, "AssetsList", api);
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
			assetsListBo.GetField("IPAddress").SetValue(
					new SiteviewValue(ip));
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
    private void addmonitor(BusinessObject device){
    	try {
			Collection<BusinessObject> coll = DBQueryUtils.getBussCollection("range", "Equipment." + device.GetField("Equipmenttype").get_Value().toString(), "EccMonitorList",api);
			Iterator<BusinessObject> it = coll.iterator();
			while (it.hasNext()) {
				BusinessObject bo = it.next();
				String automatic = bo.GetField("Automatic").get_Value().toString();
				if ("True".equals(automatic)) {
					String monitorName = bo.GetField("MonitorName").get_Value().toString();
					String MonitorTableName = bo.GetField("MonitorTableName").get_Value().toString();
					String monitorpackage=bo.GetField("description").get_Value().toString();
					String isKeepalive=bo.GetField("IsKeepalive").get_Value().toString();
					setMonitorInfo(MonitorTableName, monitorName, device, api,monitorpackage,isKeepalive);
				}
			}
		}catch (Throwable t) {

			
		}
    }
    private void setMonitorInfo(String monitorName, String title, BusinessObject equipmentBo,
    		ISiteviewApi api,String monitorpackage,String isKeepalive) {
		try {
			if(SiteviewSecurityMgr.getInstance().validateAddMonitor(api, equipmentBo.GetField("EquipmentType").get_NativeValue().toString(), equipmentBo.get_RecId())){
				String equipmentID = equipmentBo.get_RecId();
				String groups = equipmentBo.GetField("GroupId").get_Value().toString();
//				String groupName = equipmentBo.GetField("GroupName").get_Value().toString();
				BusinessObject bo = api.get_BusObService().Create("Monitor");
				if(monitorName.toLowerCase().contains("ping")){
					bo.GetField("HostName").SetValue(new SiteviewValue(equipmentBo.GetField("ServerAddress").get_Value().toString()));
				}
				bo.GetField("MonitorType").SetValue(new SiteviewValue(monitorName.substring(11)));
				bo.GetField("MonitorPackage").SetValue(new SiteviewValue(monitorpackage));
				bo.GetField("equipmentid").SetValue(new SiteviewValue(equipmentID));
				bo.GetField("Groupid").SetValue(new SiteviewValue(groups));
				bo.GetField("BelongID").SetValue(new SiteviewValue(DBQueryUtils.getUser(api).get_BusObId()));
				bo.GetField("Title").SetValue(new SiteviewValue(title));
				bo.GetField("IsKeepalive").SetValue(new SiteviewValue(isKeepalive));
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
		String monitorType = bo.get_Name().substring(bo.get_Name().indexOf(".") + 1, bo.get_Name().length());
		AutoTaskDef def;
		try {
			def = (AutoTaskDef) api.get_LiveDefinitionLibrary().GetDefinition(DefRequest.ByName("AutoTaskDef", sb.append(monitorType).append("Extension").toString()));
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
	public void initCreateAlarmDef(BusinessObject bo, ISiteviewApi api) throws SiteviewException {
		String monitorName = bo.get_Name();
		if (monitorName.contains("EccMonitor.")) {
			monitorName = monitorName.replace("EccMonitor.", "");
		}
		Collection<?> ico = DBQueryUtils.getBussCollection("MonitorType", monitorName, "EccAlarmCondition", api);
		Iterator<?> ien = ico.iterator();
		while (ien.hasNext()) {
			BusinessObject def = (BusinessObject) ien.next();
			BusinessObject alarm = api.get_BusObService().Create("Alarm");
			alarm.GetField("Monitorid").SetValue(new SiteviewValue(bo.get_RecId()));
			alarm.GetField("MonitorType").SetValue(new SiteviewValue(monitorName));
			alarm.GetField("AlarmStatus").SetValue(new SiteviewValue(def.GetField("AlarmStatus").get_NativeValue().toString()));
			alarm.GetField("AlramValue").SetValue(new SiteviewValue(def.GetField("AlramValue").get_NativeValue().toString()));
			alarm.GetField("Operator").SetValue(new SiteviewValue(def.GetField("Operators").get_NativeValue().toString()));
			alarm.GetField("ReturnValue").SetValue(new SiteviewValue(def.GetField("SaveReturnValue").get_NativeValue().toString()));
			alarm.SaveObject(api, Boolean.valueOf(false), Boolean.valueOf(true));
		}
	}
	private void updateui() {
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				labelprogress.setText("正在获取设备类型...");
				// getDeviceType();
				// getDeviceInfo();
			}
		});
	}

	private void getDeviceType() {
		SnmpPara para = new SnmpPara(textIP.getText().trim(), txtPublic
				.getText().trim(), 2000, 2, 2);
		String oidStr = "1.3.6.1.2.1.1.2.0";
		String sysOid = snmpTools.getValue(para, oidStr);
		nentity.put("manufact", "");
		nentity.put("model", "");
		nentity.put("sysObjectID", "");
		if (sysOid != null && !sysOid.isEmpty()) {
			nentity.put("sysObjectID", sysOid);
			if (DeviceTypes.devtype_map.containsKey(sysOid)) {
				DeviceInfo pro = DeviceTypes.devtype_map.get(sysOid);
				String devtype_res = pro.getDevType();
				String factory_res = pro.getFactory();
				nentity.put("manufact", factory_res);
				String model_res = pro.getRomVersion();
				nentity.put("model", model_res);
				String typeName_res = pro.getDevName();
				if (combotype.getSelectionIndex() == 0) {
					nentity.put("type", devtype_res);
				}
			} else {
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
				if (combotype.getSelectionIndex() == 0) {
					nentity.put("type", devtype_res);
				}
			}
		} else {
			nentity.put("snmpEnabled", "0");
		}
	}

	private void getInterface() {
		// 端口
		this.labelprogress.setText("正在获取设备接口...");
		SnmpPara para = new SnmpPara(textIP.getText().trim(), txtPublic
				.getText().trim(), 2000, 2, 2);
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
		Connection conn = ConfigDB.getConn();
		try {
			PreparedStatement prep = conn
					.prepareStatement("insert into ports (id,desc,pindex,porttype,mac,speed) values (?, ?, ?, ?, ?, ?);");
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
				prep.setString(1, devID);
				prep.setString(2, desc);
				prep.setString(3, index);
				prep.setString(4, portype);
				prep.setString(5, mac);
				prep.setString(6, speed);
				prep.addBatch();
			}
			conn.setAutoCommit(false);
			prep.executeBatch();
			conn.setAutoCommit(true);
		} catch (Exception ex) {

		}
		ConfigDB.close(conn);
	}

	private void getDeviceInfo() {
		// 设备名称
		SnmpPara para = new SnmpPara(textIP.getText().trim(), txtPublic
				.getText().trim(), 2000, 2, 2);
		String oidStr = "1.3.6.1.2.1.1.5.0";
		String sysname = snmpTools.getValue(para, oidStr);
		nentity.put("name", "");
		if (sysname != null && !sysname.isEmpty()) {
			nentity.put("name", sysname);
		}
		// 设备描述
		oidStr = "1.3.6.1.2.1.1.1.0";
		sysdesc = snmpTools.getValue(para, oidStr);
		if (sysdesc == null)
			sysdesc = "";
		// 设备厂商
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

		String ipList = "";
		String subnetName = "";
		String macList = "";
		String maskField = "";
		oidStr = "1.3.6.1.2.1.4.20.1.1";
		Vector<pair> svIpTable = snmpTools.getTable(para, oidStr);
		oidStr = "1.3.6.1.2.1.4.20.1.3";
		Vector<pair> svMaskTable = snmpTools.getTable(para, oidStr);
		for (int j = 0; j < svIpTable.size(); j++) {
			ipList = ipList + svIpTable.get(j).getValue() + ",";
			maskField = maskField + svMaskTable.get(j).getValue() + ",";
			String ip = svIpTable.get(j).getValue();
			String mask = svMaskTable.get(j).getValue();
			if (ip != null && !ip.isEmpty() && mask != null && !mask.isEmpty()) {
				String vv = ScanUtils.getSubnetByIPMask(ip, mask);
				subnetName = subnetName + vv + ",";
			}
		}
		nentity.put("ipList", ipList);
		nentity.put("maskField", maskField);
		nentity.put("subnetName", subnetName);
		oidStr = "1.3.6.1.2.1.2.2.1.6";
		Vector<pair> svMacTable = snmpTools.getTable(para, oidStr);
		for (int n = 0; n < svMacTable.size(); n++) {
			if (svMacTable.get(n).getKey().length() > 20) {
				if (svMacTable.get(n).getValue().length() < 12) {
					macList = macList
							+ svMacTable.get(n).getValue().toUpperCase() + ",";
				} else {
					macList = macList
							+ svMacTable.get(n).getValue().replaceAll(":", "")
									.substring(0, 12).toUpperCase() + ",";
				}
			}
		}
		nentity.put("macList", macList);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 350);
	}
}
