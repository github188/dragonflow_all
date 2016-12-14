package com.siteview.NNM.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.siteview.NNM.aas.NNMPermissions;
import com.siteview.NNM.dialogs.table.TableColumnSortor;
import com.siteview.utils.control.ControlUtils;

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

public class DeviceInfoSavefeguardDialogs extends Dialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Comparator<String> comparator;
	Tree tree = null;
	Table table = null;
	List<String> deviceList = new ArrayList<String>();
	List<String> mibTypes = new ArrayList<String>();
	Text text_search;

	public DeviceInfoSavefeguardDialogs(Shell parentShell) {
		super(parentShell);
		init();
	}

	private void init() {
		comparator = new Comparator<String>() {

			@Override
			public int compare(String str1, String str2) {
				if (str1.compareTo(str2) > 0)
					return 1;
				else if (str1.compareTo(str2) == 0)
					return 0;
				else
					return -1;
			}

		};

		String query = "SELECT Vendor FROM Mib GROUP BY Vendor ORDER BY Vendor";
		DataTable dt = SelectExecute(query, ConnectionBroker.get_SiteviewApi());
		if (dt != null) {
			for (DataRow dr : dt.get_Rows()) {
				String deviceVendor = (String) dr.get_Item("Vendor");
				if (!deviceList.contains(deviceVendor))
					deviceList.add(deviceVendor);
			}
		}
		String query1 = "Select  MibType from  MibType ";
		DataTable dt1 = SelectExecute(query1, ConnectionBroker.get_SiteviewApi());
		if (dt1 != null) {
			for (DataRow dr : dt1.get_Rows()) {
				String deviceVendor = (String) dr.get_Item("MibType");
				if (!mibTypes.contains(deviceVendor))
					mibTypes.add(deviceVendor);
			}
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

	protected void configureShell(Shell newShell) {
		// newShell.setSize(450, 320);
		// newShell.setLocation(400, 175);
		newShell.setText("设备信息维护");
		super.configureShell(newShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		Group group = new Group(container, SWT.NONE);
		group.setText("设备信息库");
		GridData gd_group = new GridData(GridData.FILL_BOTH);
		gd_group.heightHint = 500;
		gd_group.widthHint = 150;
		group.setLayoutData(gd_group);

		GridLayout gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = 10;
		group.setLayout(gridLayout);
		createTreeDevice(group);// tree

		Group group_table = new Group(container, SWT.NONE);
		group_table.setText("详情");
		GridData gd_group_table = new GridData(GridData.FILL_BOTH);
		group_table.setLayoutData(gd_group_table);

		GridLayout gridLayout_table = new GridLayout(1, false);
		gridLayout_table.horizontalSpacing = 10;
		gridLayout_table.verticalSpacing = 10;
		group_table.setLayout(gridLayout_table);
		createTableDevice(group_table);// table
		createTableSearch(group_table);

		return super.createDialogArea(parent);

	}

	@SuppressWarnings("serial")
	private void createTableSearch(Group group_table) {
		Composite composite = new Composite(group_table, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));

		GridData gd_1 = new GridData();
		gd_1.widthHint = 100;

		text_search = new Text(composite, SWT.BORDER);
		GridData gd = new GridData();
		gd.verticalSpan = 2;
		gd.widthHint = 440;
		text_search.setLayoutData(gd);

		Button button_search = new Button(composite, SWT.NONE);
		button_search.setText("搜索");
		button_search.setEnabled(NNMPermissions.getInstance().getNNMInfoMaintainSearch());
		button_search.setLayoutData(gd_1);
		button_search.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event event) {
				for (TableItem tableitem : table.getItems()) {
					tableitem.dispose();
				}
				for (Control control : table.getChildren()) {
					control.dispose();
				}
				String search = text_search.getText();
				String query = "Select RecId, SysOID,Vendor,MibModel,MibType,MibSubType from  Mib Where SysOid like '%" + search + "%'  or  MibModel like '%"+search+"%'";
				DataTable dt = SelectExecute(query, ConnectionBroker.get_SiteviewApi());
				if (dt != null) {
					for (DataRow dr : dt.get_Rows()) {
						String sysOid = (String) dr.get_Item("SysOID");
						String deviceVendor = (String) dr.get_Item("Vendor");
						String mibModel = (String) dr.get_Item("MibModel");
						String mibType = (String) dr.get_Item("MibType");
						String mibSubType=(String) dr.get_Item("MibSubType");
						TableItem tableItem = new TableItem(table, SWT.NONE | SWT.CHECK);
						tableItem.setText(0, sysOid);
						tableItem.setText(1, deviceVendor);
						tableItem.setText(2, mibModel);
						tableItem.setText(3, mibType);
						tableItem.setText(4, mibSubType);
						tableItem.setData("RecId", (String) dr.get_Item("RecId"));
					}
				}
			}
		});

		Button button_Vendor = new Button(composite, SWT.NONE);
		button_Vendor.setText("添加厂商");
		button_Vendor.setLayoutData(gd_1);
		button_Vendor.setEnabled(NNMPermissions.getInstance().getNNMInfoMaintainAddFirm());
		button_Vendor.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event event) {
				String search = text_search.getText();
				if (deviceList.contains(search)) {
					MsgBox.ShowWarning("添加厂商结果", "该厂商已经存在");
					return;
				}
				deviceList.add(search);
				Collections.sort(deviceList, comparator);
				TreeItem item1 = tree.getItems()[0];
				if (item1.getText().equals("设备信息库")) {
					for (TreeItem treeItem : item1.getItems()) {
						treeItem.dispose();
					}
					for (String device : deviceList) {
						TreeItem item_1 = new TreeItem(item1, SWT.NONE);
						item_1.setText(device);
						item_1.setData(device);
					}
					try {
						BusinessObject mibBo = ConnectionBroker.get_SiteviewApi().get_BusObService().Create("Mib");
						mibBo.GetField("Vendor").SetValue(new SiteviewValue(search));
						mibBo.SaveObject(ConnectionBroker.get_SiteviewApi(), true, true);
					} catch (SiteviewException e) {
						e.printStackTrace();
					}
					item1.setExpanded(true);
					tree.layout();
				}

			}
		});
	}

	@SuppressWarnings("serial")
	public void createTreeDevice(Group group) {
		tree = new Tree(group, SWT.BORDER | SWT.SELECTED);
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		tree.setLayoutData(new GridData(GridData.FILL_BOTH));
		tree.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				for (TableItem tableitem : table.getItems()) {
					tableitem.dispose();
				}
				for (Control control : table.getChildren()) {
					control.dispose();
				}
				String query = "Select RecId, SysOID,Vendor,MibModel,MibType ,MibSubType from  Mib Where Vendor = '" + e.item.getData() + "'";
				DataTable dt = SelectExecute(query, ConnectionBroker.get_SiteviewApi());
				if (dt != null) {
					for (DataRow dr : dt.get_Rows()) {
						String sysOid = (String) dr.get_Item("SysOID");
						String deviceVendor = (String) dr.get_Item("Vendor");
						String mibModel = (String) dr.get_Item("MibModel");
						String mibType = (String) dr.get_Item("MibType");
						String mibSubType = (String) dr.get_Item("MibSubType");
						TableItem tableItem = new TableItem(table, SWT.NONE | SWT.CHECK);
						tableItem.setText(0, sysOid);
						tableItem.setText(1, deviceVendor);
						tableItem.setText(2, mibModel);
						tableItem.setText(3, mibType);
						tableItem.setText(4, mibSubType);
						tableItem.setData("RecId", (String) dr.get_Item("RecId"));
						table.setData("Vendor",deviceVendor);
					}
//					TableItem tableItem1 = new TableItem(table, SWT.NONE | SWT.CHECK);
				}

			}

		});

		TreeItem item1 = new TreeItem(tree, SWT.NONE);
		item1.setText("设备信息库");
		Collections.sort(deviceList, comparator);
		for (String device : deviceList) {
			TreeItem item_1 = new TreeItem(item1, SWT.NONE);
			item_1.setText(device);
			item_1.setData(device);
		}
		item1.setExpanded(true);
	}

	@SuppressWarnings("serial")
	public void createTableDevice(final Group group) {
		table = new Table(group, SWT.NONE);
		table.setLayout(new GridLayout());
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.addListener(SWT.MouseDoubleClick, new Listener() {

			@Override
			public void handleEvent(Event event) {
				Point point = new Point(event.x, event.y);
				TableItem tableItem = table.getItem(point);
				boolean flag = false;
				if (tableItem == null) {
					flag = true;
				} 
				
//				else {
//					for (int i = 0; i < table.getColumnCount(); i++) {
//						if (!"".equals(tableItem.getText(i))) {
//							break;
//						}
//						if (i == table.getColumnCount() - 1) {
//							flag = true;
//						}
//					}
//				}
				String vendor = null;
				if(tree.getSelection() != null && tree.getSelection().length > 0){
					vendor = tree.getSelection()[0].getText();
				}
				if (flag) {
					if(!NNMPermissions.getInstance().getNNMInfoMaintainAdd()){
						MsgBox.ShowError("提示","无此权限!");
						return ;
					}
					DeviceInfoSavefeguardUpadteDialogs deviceinfo = new DeviceInfoSavefeguardUpadteDialogs(Display.getCurrent().getActiveShell(), tableItem, table, mibTypes, false, "添加设备信息库",vendor);
					deviceinfo.open();
					if ((deviceinfo.getVendor()!=null&&!"".equals(deviceinfo.getVendor()))&&!deviceList.contains(deviceinfo.getVendor())) {
						deviceList.add(deviceinfo.getVendor());
						TreeItem item1 = tree.getItems()[0];
						for (TreeItem treeItem : item1.getItems()) {
							treeItem.dispose();
						}
						Collections.sort(deviceList, comparator);
						for (String device : deviceList) {
							TreeItem item_1 = new TreeItem(item1, SWT.NONE);
							item_1.setText(device);
							item_1.setData(device);
						}
					}
					tree.layout();
					group.layout();
				} else {
					if(!NNMPermissions.getInstance().getNNMInfoMaintainEdit()){
						MsgBox.ShowError("提示","无此权限!");
						return ;
					}
					DeviceInfoSavefeguardUpadteDialogs deviceinfo = new DeviceInfoSavefeguardUpadteDialogs(Display.getCurrent().getActiveShell(), tableItem, table, mibTypes,true, "编辑设备信息库",vendor);
					deviceinfo.open();
					if(deviceinfo.isAddOrupdate()){
						if (!deviceList.contains(deviceinfo.getVendor())) {
							deviceList.add(deviceinfo.getVendor());
							TreeItem item1 = tree.getItems()[0];
							for (TreeItem treeItem : item1.getItems()) {
								treeItem.dispose();
							}
							Collections.sort(deviceList, comparator);
							for (String device : deviceList) {
								TreeItem item_1 = new TreeItem(item1, SWT.NONE);
								item_1.setText(device);
								item_1.setData(device);
							}
						}
					}else{
						if(table.getItems().length==0&&deviceList.contains(deviceinfo.getVendor())){
							deviceList.remove(deviceinfo.getVendor());
							TreeItem item1 = tree.getItems()[0];
							for (TreeItem treeItem : item1.getItems()) {
								treeItem.dispose();
							}
							Collections.sort(deviceList, comparator);
							for (String device : deviceList) {
								TreeItem item_1 = new TreeItem(item1, SWT.NONE);
								item_1.setText(device);
								item_1.setData(device);
							}
						}
					}
					group.layout();
					tree.layout();
				}
			}
		});

		String[] names = new String[]{"系统OID","厂商","型号","设备类型","设备子类型"};
		int[] widths = new int[]{180,100,200,100,100};
		List<TableColumn> columns = ControlUtils.createTableColunms(table, names, widths);
		for(TableColumn column : columns){
			TableColumnSortor.addStringSorter(table, column);
		}
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		parent.setLayout(new GridLayout(3, false));
		createLabel(parent);
		createButton(parent, IDialogConstants.CANCEL_ID, "取消", true);
//		createButton(parent, IDialogConstants.OK_ID, "保存", true);
	}

	private void createLabel(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText("双击Table中数据进行数据编辑       双击Table中空白处进行数据添加                       ");
	}

	@Override
	protected void buttonPressed(int buttonId) {
		super.buttonPressed(buttonId);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(900, 600);
	}

	@Override
	public boolean close() {
		return super.close();
	}
}
