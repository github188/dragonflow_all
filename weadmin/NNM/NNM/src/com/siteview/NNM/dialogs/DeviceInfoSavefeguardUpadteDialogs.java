package com.siteview.NNM.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.SiteviewException;
import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;
import Siteview.Database.IDbDataParameterCollection;
import Siteview.Database.SqlParameterCollection;
import Siteview.Windows.Forms.ConnectionBroker;
import Siteview.Windows.Forms.MsgBox;

import com.siteview.topology.model.DeviceInfo;
import com.siteview.topology.scan.DeviceTypes;
import com.siteview.utils.control.ControlUtils;
import com.siteview.utils.db.DBQueryUtils;
import com.siteview.utils.db.DBUtils;

public class DeviceInfoSavefeguardUpadteDialogs extends Dialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5350106177276825169L;
	private TableItem tableItem;
	private Table table;
	private CCombo combo_mibType;
	List<String> mibTypes;
	String mibType = null;
	private CCombo combo_mibSubType;
	List<String> mibSubTypes = new ArrayList<String>();
	String mibSubType;
	private Text text_vendor;
	String vendor="";
	private Text text_mibModel;
	String mibModel="";
	private Text text_sysOid;
	String sysOid="";
	private Text text_cpuOid;
	String cpuOid="";
	private Text text_cpu1mOid ;
	private Text text_cpu5mOid ;
	private Text text_cpu5sOid ;
	private Text text_memFreeOid;
	String memFreeOid="";
	private Text text_memUseOid;
	String memUseOid="";
	private Text text_memTotalOid;
	String memTotalOid="";
	private Text text_powerOid;
	String powerOid="";
	private Text text_temperatureOid;
	String temperatureOid="";

	String fanstatusOid="";
	private Text text_fanstatusOid;

	private Text text_conectionOid;
	String conectionOid="";

	BusinessObject bo = null;
	String title;
	boolean addOrupdate = true;

	public DeviceInfoSavefeguardUpadteDialogs(Shell parentShell, TableItem tableItem, Table table, List<String> mibTypes, boolean addOrupdate, String title, String vendor) {
		super(parentShell);
		this.mibTypes = mibTypes;
		this.title = title;
		this.tableItem = tableItem;
		this.table = table;
		this.addOrupdate = addOrupdate;
		this.vendor = vendor;
		init();
	}

	private void init() {
		try {
			if (tableItem == null) {
				bo = DBUtils.createBusinessObject("Mib", ConnectionBroker.get_SiteviewApi());
			} else {
				String recid = (String) tableItem.getData("RecId");
				if (recid == null) {
					if (tableItem.getText(0) != null && !"".equals(tableItem.getText(0)))
						bo = DBQueryUtils.queryOnlyBo("SysOID", tableItem.getText(0), "Mib",
								ConnectionBroker.get_SiteviewApi());
					else {
						bo = DBUtils.createBusinessObject("Mib", ConnectionBroker.get_SiteviewApi());
					}
				} else {
					bo = DBQueryUtils.queryOnlyBo("RecId", recid, "Mib", ConnectionBroker.get_SiteviewApi());
				}
			}

			if (tableItem != null) {
				mibSubType = bo.GetField("MibSubType").get_Value().toString();
				mibType = bo.GetField("MibType").get_Value().toString();

				vendor = bo.GetField("Vendor").get_Value().toString();
				mibModel = bo.GetField("MibModel").get_Value().toString();
				sysOid = bo.GetField("SysOID").get_Value().toString();
				cpuOid = bo.GetField("CpuOID").get_Value().toString();
				memFreeOid = bo.GetField("MemFreeOID").get_Value().toString();
				memUseOid = bo.GetField("MemUseOID").get_Value().toString();
				memTotalOid = bo.GetField("MemTotalOID").get_Value().toString();
				powerOid = bo.GetField("PowerOID").get_Value().toString();
				temperatureOid = bo.GetField("TemperatureOID").get_Value().toString();
				fanstatusOid = bo.GetField("FanstatusOID").get_Value().toString();
				conectionOid = bo.GetField("ConectionOID").get_Value().toString();
				// mibSubTypes
				String query1 = "Select  MibSubType from  MibSubType where MibType='" + mibType + "'";
				DataTable dt1 = SelectExecute(query1, ConnectionBroker.get_SiteviewApi());
				if (dt1 != null) {
					for (DataRow dr : dt1.get_Rows()) {
						String deviceVendor = (String) dr.get_Item("MibSubType");
						if (!mibSubTypes.contains(deviceVendor))
							mibSubTypes.add(deviceVendor);
					}
				}
			}

		} catch (SiteviewException e) {
			e.printStackTrace();
		}
	}

	public static DataTable SelectExecute(String sql, ISiteviewApi api) {
		IDbDataParameterCollection collection = new SqlParameterCollection();
		StringBuffer str = new StringBuffer();
		str.append(sql);
		DataTable dt = null;
		try {
			dt = api.get_NativeSQLSupportService().ExecuteNativeSQLQuery(str.toString(), collection);
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
		return dt;
	}

	protected Point getInitialSize() {
		return new Point(400, 600);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(title);
	}

	@SuppressWarnings("serial")
	protected Control createDialogArea(Composite parent) {
		Composite com = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.horizontalSpacing = 30;
		gridLayout.verticalSpacing = 10;
		com.setLayout(gridLayout);

		GridData data_text = new GridData();
		data_text.widthHint = 240;
//		Label label_mibtype = new Label(com, SWT.NONE);
//		label_mibtype.setText("Mib类型");
//		combo_mibType = new Combo(com, SWT.NONE | SWT.READ_ONLY);
//		combo_mibType.setLayoutData(data_text);
		
		combo_mibType = ControlUtils.createLabelAndCombo(com, "Mib类型");
		for (int i = 0; i < mibTypes.size(); i++) {
			combo_mibType.add(mibTypes.get(i), i);
		}

		combo_mibType.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				mibSubTypes.clear();
				combo_mibSubType.removeAll();
				mibType = combo_mibType.getText();
				String query1 = "Select  MibSubType from  MibSubType where MibType='" + mibType + "'";
				DataTable dt1 = SelectExecute(query1, ConnectionBroker.get_SiteviewApi());
				if (dt1 != null) {
					for (DataRow dr : dt1.get_Rows()) {
						String deviceVendor = (String) dr.get_Item("MibSubType");
						if (!mibSubTypes.contains(deviceVendor)) {
							mibSubTypes.add(deviceVendor);
						}

					}
				}
				for (int i = 0; i < mibSubTypes.size(); i++) {
					combo_mibSubType.add(mibSubTypes.get(i), i);
				}
				combo_mibSubType.setText(mibSubTypes.get(0));

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		
		if (mibType != null) 
			combo_mibType.setText(mibType);
		else
			combo_mibType.select(0);
		
		combo_mibSubType = ControlUtils.createLabelAndCombo(com, "Mib子类型");
//		Label label_mibSubType = new Label(com, SWT.NONE);
//		label_mibSubType.setText("Mib子类型");
//		combo_mibSubType = new Combo(com, SWT.NONE | SWT.READ_ONLY);
//		combo_mibSubType.setLayoutData(data_text);
		
		if (mibType == null) {
			String query1 = "Select  MibSubType from  MibSubType where MibType='" + combo_mibType.getText() + "'";
			DataTable dt1 = SelectExecute(query1, ConnectionBroker.get_SiteviewApi());
			if (dt1 != null) {
				for (DataRow dr : dt1.get_Rows()) {
					String deviceVendor = (String) dr.get_Item("MibSubType");
					if (!mibSubTypes.contains(deviceVendor)) {
						mibSubTypes.add(deviceVendor);
					}
				}
			}
		}
		for (int i = 0; i < mibSubTypes.size(); i++) {
			combo_mibSubType.add(mibSubTypes.get(i), i);
		}
		combo_mibSubType.select(0);

		text_vendor = ControlUtils.createLabelAndText(com, "厂商");
		text_mibModel = ControlUtils.createLabelAndText(com, "设备型号");
		text_sysOid = ControlUtils.createLabelAndText(com, "系统OID");
		text_cpuOid = ControlUtils.createLabelAndText(com, "CPU使用率OID", SWT.NONE, SWT.BORDER | SWT.PASSWORD);
		text_cpu1mOid = ControlUtils.createLabelAndText(com, "CPU1分钟使用率OID", SWT.NONE, SWT.BORDER | SWT.PASSWORD);
		text_cpu5mOid = ControlUtils.createLabelAndText(com, "CPU5分钟使用率OID", SWT.NONE, SWT.BORDER | SWT.PASSWORD);
		text_cpu5sOid = ControlUtils.createLabelAndText(com, "CPU5秒钟使用率OID", SWT.NONE, SWT.BORDER | SWT.PASSWORD);
		
		text_memUseOid = ControlUtils.createLabelAndText(com, "使用内存OID", SWT.NONE, SWT.BORDER | SWT.PASSWORD);
		text_memFreeOid = ControlUtils.createLabelAndText(com, "剩余内存OID", SWT.NONE, SWT.BORDER | SWT.PASSWORD);
		text_memTotalOid = ControlUtils.createLabelAndText(com, "总内存OID", SWT.NONE, SWT.BORDER | SWT.PASSWORD);
		text_powerOid = ControlUtils.createLabelAndText(com, "电源OID", SWT.NONE, SWT.BORDER | SWT.PASSWORD);
		text_temperatureOid = ControlUtils.createLabelAndText(com, "温度OID", SWT.NONE, SWT.BORDER | SWT.PASSWORD);
		text_fanstatusOid = ControlUtils.createLabelAndText(com, "风扇OID", SWT.NONE, SWT.BORDER | SWT.PASSWORD);
		text_conectionOid = ControlUtils.createLabelAndText(com, "连接OID", SWT.NONE, SWT.BORDER | SWT.PASSWORD);
		
		if(vendor != null)
			text_vendor.setText(vendor);
		if (tableItem != null) {
			text_vendor.setText(vendor);
			text_mibModel.setText(mibModel);
			text_sysOid.setText(sysOid);
			text_temperatureOid.setText(temperatureOid);
			text_memFreeOid.setText(memFreeOid);
			text_memUseOid.setText(memUseOid);
			text_memTotalOid.setText(memTotalOid);
			text_powerOid.setText(powerOid);
			text_cpuOid.setText(cpuOid);
			text_fanstatusOid.setText(fanstatusOid);
			text_conectionOid.setText(conectionOid);
			combo_mibType.setText(mibType);
			try {
				text_cpu1mOid.setText(bo.GetField("Cpu1mOID").get_NativeValue().toString());
				text_cpu5mOid.setText(bo.GetField("Cpu5mOID").get_NativeValue().toString());
				text_cpu5sOid.setText(bo.GetField("Cpu5sOID").get_NativeValue().toString());
			} catch (SiteviewException e1) {
				e1.printStackTrace();
			}
			// for (int i = 0; i < mibSubTypes.size(); i++) {
			// combo_mibSubType.add(mibSubTypes.get(i), i);
			// }
			combo_mibSubType.setText(mibSubType);
		}
		return com;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID, "取消", true);
		if (addOrupdate) {
			createButton(parent, IDialogConstants.FINISH_ID, "删除", true);
		}
		createButton(parent, IDialogConstants.OK_ID, "保存", true);
	}

	protected void buttonPressed(int buttonId) {
		if (buttonId == Dialog.OK) {
			addOrupdate = true;
			try {
				mibType = combo_mibType.getText().toString();
				String vendor1 = text_vendor.getText().toString().trim();
				mibModel = text_mibModel.getText().toString().trim();
				sysOid = text_sysOid.getText().toString().trim();
				String recid = null;
				if (sysOid == null || "".equals(sysOid)) {
					MsgBox.ShowWarning("保存", "系统Oid必须不能为空");
					return;
				} else if (sysOid != null && !"".equals(sysOid) && (vendor != null && vendor.equals(vendor1))) {
					recid = bo.get_RecId();
					bo.GetField("MibType").SetValue(new SiteviewValue(mibType));
					bo.GetField("MibSubType").SetValue(new SiteviewValue(combo_mibSubType.getText().toString()));
					bo.GetField("Vendor").SetValue(new SiteviewValue(vendor));
					bo.GetField("MibModel").SetValue(new SiteviewValue(mibModel));
					bo.GetField("SysOID").SetValue(new SiteviewValue(sysOid));
					bo.GetField("CpuOID").SetValue(new SiteviewValue(text_cpuOid.getText().toString().trim()));
					bo.GetField("Cpu1mOID").SetValue(new SiteviewValue(text_cpu1mOid.getText().toString().trim())); //1分钟
					bo.GetField("Cpu5mOID").SetValue(new SiteviewValue(text_cpu5mOid.getText().toString().trim())); //5分钟
					bo.GetField("Cpu5sOID").SetValue(new SiteviewValue(text_cpu5sOid.getText().toString().trim())); //5秒钟
					bo.GetField("MemFreeOID").SetValue(new SiteviewValue(text_memFreeOid.getText().toString().trim()));
					bo.GetField("MemUseOID").SetValue(new SiteviewValue(text_memUseOid.getText().toString().trim()));
					bo.GetField("MemTotalOID").SetValue(new SiteviewValue(text_memTotalOid.getText().toString().trim()));
					bo.GetField("PowerOID").SetValue(new SiteviewValue(text_powerOid.getText().toString().trim()));
					bo.GetField("TemperatureOID").SetValue(new SiteviewValue(text_temperatureOid.getText().toString().trim()));
					bo.GetField("FanstatusOID").SetValue(new SiteviewValue(text_fanstatusOid.getText().toString().trim()));
					bo.GetField("ConectionOID").SetValue(new SiteviewValue(text_conectionOid.getText().toString().trim()));
					bo.SaveObject(ConnectionBroker.get_SiteviewApi(), true, true);
					updateDeviceTypes(sysOid, mibType, combo_mibSubType.getText().toString(), vendor, mibModel);
				} else if (sysOid != null && !"".equals(sysOid)) {
					BusinessObject mibBo = ConnectionBroker.get_SiteviewApi().get_BusObService().Create("Mib");
					mibBo.GetField("Vendor").SetValue(new SiteviewValue(vendor1));
					vendor = vendor1;
					mibBo.GetField("MibType").SetValue(new SiteviewValue(mibType));
					mibBo.GetField("MibSubType").SetValue(new SiteviewValue(combo_mibSubType.getText().toString()));
					mibBo.GetField("Vendor").SetValue(new SiteviewValue(vendor));
					mibBo.GetField("MibModel").SetValue(new SiteviewValue(mibModel));
					mibBo.GetField("SysOID").SetValue(new SiteviewValue(sysOid));
					mibBo.GetField("CpuOID").SetValue(new SiteviewValue(text_cpuOid.getText().toString().trim()));
					mibBo.GetField("Cpu1mOID").SetValue(new SiteviewValue(text_cpu1mOid.getText().toString().trim())); //1分钟
					mibBo.GetField("Cpu5mOID").SetValue(new SiteviewValue(text_cpu5mOid.getText().toString().trim())); //5分钟
					mibBo.GetField("Cpu5sOID").SetValue(new SiteviewValue(text_cpu5sOid.getText().toString().trim())); //5秒钟
					mibBo.GetField("MemFreeOID").SetValue(new SiteviewValue(text_memFreeOid.getText().toString().trim()));
					mibBo.GetField("MemUseOID").SetValue(new SiteviewValue(text_memUseOid.getText().toString().trim()));
					mibBo.GetField("MemTotalOID").SetValue(new SiteviewValue(text_memTotalOid.getText().toString().trim()));
					mibBo.GetField("PowerOID").SetValue(new SiteviewValue(text_powerOid.getText().toString().trim()));
					mibBo.GetField("TemperatureOID").SetValue(new SiteviewValue(text_temperatureOid.getText().toString().trim()));
					mibBo.GetField("FanstatusOID").SetValue(new SiteviewValue(text_fanstatusOid.getText().toString().trim()));
					mibBo.GetField("ConectionOID").SetValue(new SiteviewValue(text_conectionOid.getText().toString().trim()));
					mibBo.SaveObject(ConnectionBroker.get_SiteviewApi(), true, true);
					recid = mibBo.get_RecId();
					updateDeviceTypes(sysOid, mibType, combo_mibSubType.getText().toString(), vendor, mibModel);
				}
				if (tableItem == null) {
					tableItem = new TableItem(table, SWT.NONE);
				}
				tableItem.setText(0, sysOid);
				tableItem.setText(1, vendor);
				tableItem.setText(2, mibModel);
				tableItem.setText(3, mibType);
				tableItem.setText(4, mibSubType);
				tableItem.setData("RecId", recid);
			} catch (SiteviewException e) {
				e.printStackTrace();
			}
		} else if (buttonId == IDialogConstants.FINISH_ID) {
			addOrupdate = false;
			try {
				bo.DeleteObject(ConnectionBroker.get_SiteviewApi());
				if (tableItem != null) {
					tableItem.dispose();
				}
			} catch (SiteviewException e) {
				e.printStackTrace();
			}
		} else if (buttonId == Dialog.CANCEL) {
		}
		super.close();
	}

	public void updateDeviceTypes(String sysOid, String mibType, String mibSubType, String deviceVendor, String mibModel) {
		String devtype = "5";
		String devname = "HOST";
		if (mibType.trim().equals("网络设备")) {
			devtype = "6";
			devname = "OTHER";
			if (mibSubType.trim().equals("三层交换机")) {
				devtype = "0";
				devname = "ROUTE_SWITCH";
			} else if (mibSubType.trim().equals("二层交换机")) {
				devtype = "1";
				devname = "SWITCH";
			} else if (mibSubType.trim().equals("路由器")) {
				devtype = "2";
				devname = "ROUTE";
			} else if (mibSubType.trim().equals("防火墙")) {
				devtype = "3";
				devname = "FIREWALL";
			} else if (mibSubType.trim().equals("HUB")) {
				devtype = "6";
				devname = "HUB";
			}
		}
		if (mibType.trim().equals("服务器")) {
			devtype = "4";
			devname = "SERVER";
		}
		if (DeviceTypes.devtype_map.containsKey(sysOid)) {
			DeviceTypes.devtype_map.get(sysOid).setFactory(deviceVendor);
			DeviceTypes.devtype_map.get(sysOid).setRomVersion(mibModel);
			DeviceTypes.devtype_map.get(sysOid).setDevType(devtype);
			DeviceTypes.devtype_map.get(sysOid).setDevName(devname);
		} else {

			DeviceInfo devicePro = new DeviceInfo();
			devicePro.setFactory(deviceVendor);
			devicePro.setRomVersion(mibModel);

			devicePro.setDevType(devtype);
			devicePro.setDevName(devname);
			DeviceTypes.devtype_map.put(sysOid, devicePro);
		}
	}

	public String getMibType() {
		return mibType;
	}

	public String getVendor() {
		return vendor;
	}

	public String getMibModel() {
		return mibModel;
	}

	public String getSysOid() {
		return sysOid;
	}

	public boolean isAddOrupdate() {
		return addOrupdate;
	}

	public void setAddOrupdate(boolean addOrupdate) {
		this.addOrupdate = addOrupdate;
	}

}
