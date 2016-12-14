package com.siteview.NNM.dialogs.table;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.siteview.NNM.ContentProviders.PortsContentProvider;
import com.siteview.NNM.ContentProviders.PortsLabelProvider;
import com.siteview.NNM.modles.DPort;
import com.siteview.NNM.modles.UpPort;
import com.siteview.NNM.util.DeviceInfoUtils;
import com.siteview.nnm.data.ConfigDB;
import com.siteview.nnm.data.model.svNode;
import com.siteview.utils.control.ControlUtils;

import net.sf.json.JSONArray;

public class DeviceInfoTabFolder {

	public static void createTabFolderUI(Composite composite, final svNode entityobj, final String nodeid) {
		TabFolder tabFolder = new TabFolder(composite, SWT.NONE);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

		TabItem tabItem_machineProperties = new TabItem(tabFolder, SWT.NONE);
		tabItem_machineProperties.setText("本机属性");
		Composite composite_machineProperties = new Composite(tabFolder, SWT.Modify);
		tabItem_machineProperties.setControl(composite_machineProperties);
		composite_machineProperties.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite_machineProperties.setLayout(new GridLayout(4, false));
		createMachineProperties(composite_machineProperties, entityobj, nodeid);

		TabItem tabItem_MachinePort = new TabItem(tabFolder, SWT.NONE);
		tabItem_MachinePort.setText("本机端口");
		Composite composite_MachinePort = new Composite(tabFolder, SWT.Modify);
		tabItem_MachinePort.setControl(composite_MachinePort);
		composite_MachinePort.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite_MachinePort.setLayout(new GridLayout());
		createMachinePort(composite_MachinePort, entityobj, nodeid);

		TabItem tabItem_PartProperties = new TabItem(tabFolder, SWT.NONE);
		tabItem_PartProperties.setText("上联属性");
		Composite composite_PartProperties = new Composite(tabFolder, SWT.Modify);
		tabItem_PartProperties.setControl(composite_PartProperties);
		composite_PartProperties.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite_PartProperties.setLayout(new GridLayout());
		createPartProperties(composite_PartProperties, entityobj);

		TabItem tabItem_PartPort = new TabItem(tabFolder, SWT.NONE);
		tabItem_PartPort.setText("上联端口");
		Composite composite_PartPort = new Composite(tabFolder, SWT.Modify);
		tabItem_PartPort.setControl(composite_PartPort);
		composite_PartPort.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite_PartPort.setLayout(new GridLayout());
		createPartport(composite_PartPort, entityobj);
	}

	public static void createTabFolderUI(final Display display, final Composite composite, final svNode entityobj, final String nodeid) {

		display.asyncExec(new Runnable() {

			@Override
			public void run() {
				for (Control control : composite.getChildren()) {
					control.dispose();
				}
				createTabFolderUI(composite, entityobj, nodeid);
				composite.layout();
			}
		});

	}

	/**
	 * Part Properties
	 * 
	 * @param composite_PartProperties
	 * @param entityobj
	 */
	private static void createPartProperties(Composite composite_PartProperties, svNode entityobj) {
		TableViewer tableViewer = new TableViewer(composite_PartProperties, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		tableViewer.setColumnProperties(new String[] {});

		Table table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));

		String[] names = { "HUB名称", "网络IP", "设备名称", "连接端口" };
		int[] widths = { 100, 120, 150, 100 };
		ControlUtils.createTableColumns(tableViewer, names, widths);
		
		// 设置内容器
		tableViewer.setContentProvider(new PortsContentProvider());
		// 设置标签器
		tableViewer.setLabelProvider(new PortsLabelProvider());
	}

	/**
	 * Part Port
	 * 
	 * @param composite_PartPort
	 * @param entityobj
	 */
	private static void createPartport(Composite composite_PartPort, svNode entityobj) {
		TableViewer tableViewer = new TableViewer(composite_PartPort, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		tableViewer.setColumnProperties(new String[] {});

		Table table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));

		String[] names = { "设备IP", "设备名称", "连接端口" };
		int[] widths = { 120, 150, 200 };
		ControlUtils.createTableColumns(tableViewer, names, widths);
		
		// 设置内容器
		tableViewer.setContentProvider(new PortsContentProvider());
		// 设置标签器
		tableViewer.setLabelProvider(new PortsLabelProvider());
		// 把数据集合给tableView
		if (entityobj != null) {
			ArrayList<UpPort> data1 = new ArrayList<UpPort>();
			UpPort port1 = new UpPort();
			Connection conn = null;
			try {
				conn = ConfigDB.getConn();
				Map<String, String> map = DeviceInfoUtils.getPartPortData(conn, entityobj);
				port1.setIp(map.get("ip"));
				port1.setName(map.get("name"));
				port1.setPort(map.get("port"));
				data1.add(port1);
				tableViewer.setInput(data1);
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (conn != null)
					ConfigDB.close(conn);
			}
//			for (String lid : EntityManager.allEdges.keySet()) {
//				if (EntityManager.allEdges.get(lid).getLtarget().equals(entityobj.getSvid())) {
//					entity enode = EntityManager.allEntity.get(EntityManager.allEdges.get(lid).getLsource());
//
//					port1.setIp(((svNode) enode).getLocalip());
//					port1.setName(((svNode) enode).getDevicename());
//					Connection conn = ConfigDB.getConn();
//					String sql = "select desc from ports where id='" + EntityManager.allEdges.get(lid).getLsource() + "' and pindex='" + EntityManager.allEdges.get(lid).getSinterface() + "'";
//					String desc = "";
//
//					try {
//						ResultSet rs = ConfigDB.query(sql, conn);
//						while (rs.next()) {
//							desc = rs.getString("desc");
//							if (desc == null)
//								desc = "";
//						}
//					} catch (NumberFormatException e) {
//						e.printStackTrace();
//					} catch (SQLException e) {
//						e.printStackTrace();
//					} catch (Exception ex) {
//
//					}
//					ConfigDB.close(conn);
//					port1.setPort("");
//					if (((svNode) enode).getSvgtype() != 6)
//						port1.setPort(EntityManager.allEdges.get(lid).getSinterface() + " :" + desc);
//					data1.add(port1);
//					break;
//				}
//			}
//			tableViewer.setInput(data1);
			// composite_PartPort.layout();
		}
	}

	/**
	 * Machine Properties
	 * 
	 * @param entityobj
	 * 
	 * @param composite_machineProperties
	 */
	private static void createMachineProperties(Composite composite, final svNode entityobj, final String nodeid) {
		new Label(composite, SWT.RIGHT).setText("\u8BBE\u5907\u5730\u5740\uFF1A");
		final Combo cbip = new Combo(composite, SWT.NONE);
		cbip.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(composite, SWT.RIGHT).setText("\u8BBE\u5907mac\uFF1A");
		final Text txtmac = new Text(composite, SWT.BORDER);
		txtmac.setEditable(false);
		txtmac.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(composite, SWT.RIGHT).setText("\u8BBE\u5907\u540D\u79F0\uFF1A");
		final Text txtname = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		txtname.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(composite, SWT.RIGHT).setText("\u81EA\u5B9A\u4E49\u540D\u79F0\uFF1A");
		final Text txtcustom = new Text(composite, SWT.BORDER);
		txtcustom.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(composite, SWT.RIGHT).setText("\u8BBE\u5907\u7C7B\u578B\uFF1A");
		final Combo txttype = new Combo(composite, SWT.READ_ONLY);
		txttype.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		txttype.add("三层交换机");
		txttype.add("二层交换机");
		txttype.add("路由器");
		txttype.add("防火墙");
		txttype.add("服务器");
		txttype.add("PC终端");
		txttype.add("其它");
		txttype.select(0);

		new Label(composite, SWT.RIGHT).setText("\u5236\u9020\u5382\u5546\uFF1A");
		final Text txtfact = new Text(composite, SWT.BORDER);
		txtfact.setEditable(false);
		txtfact.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(composite, SWT.RIGHT).setText("\u6240\u5728\u5B50\u7F51\uFF1A");
		final Text txtsubnet = new Text(composite, SWT.BORDER);
		txtsubnet.setEditable(false);
		txtsubnet.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(composite, SWT.RIGHT).setText("\u5B50\u7F51\u63A9\u7801\uFF1A");
		final Text txtmaskfield = new Text(composite, SWT.BORDER);
		txtmaskfield.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txtmaskfield.setEditable(false);

		new Label(composite, SWT.RIGHT).setText("\u8BBE\u5907OID\uFF1A");
		final Text txtoid = new Text(composite, SWT.BORDER);
		txtoid.setEditable(false);
		GridData gd_txtoid = new GridData(GridData.FILL_HORIZONTAL);
		gd_txtoid.horizontalSpan = 3;
		txtoid.setLayoutData(gd_txtoid);

		new Label(composite, SWT.RIGHT).setText("\u8FD0\u884C\u65F6\u95F4\uFF1A");
		final Text txtruntime = new Text(composite, SWT.BORDER);
		txtruntime.setEditable(false);
		GridData gd_txtruntime = new GridData(GridData.FILL_HORIZONTAL);
		gd_txtruntime.horizontalSpan = 3;
		txtruntime.setLayoutData(gd_txtruntime);

		new Label(composite, SWT.RIGHT).setText("\u8BBE\u5907\u63CF\u8FF0\uFF1A");
		final Text txdescrip = new Text(composite, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI | SWT.WRAP);
		txdescrip.setEditable(false);
		GridData gd_txdescrip = new GridData(GridData.FILL_HORIZONTAL);
		gd_txdescrip.horizontalSpan = 3;
		gd_txdescrip.heightHint = 72;
		txdescrip.setLayoutData(gd_txdescrip);

		new Label(composite, SWT.RIGHT).setText("\u8BBE\u5907\u5907\u6CE8\uFF1A");
		final Text txtmemo = new Text(composite, SWT.BORDER | SWT.MULTI);
		GridData gd_txtmemo = new GridData(GridData.FILL_HORIZONTAL);
		gd_txtmemo.horizontalSpan = 3;
		gd_txtmemo.heightHint = 69;
		txtmemo.setLayoutData(gd_txtmemo);
		
		if (entityobj != null) {
			Connection conn = null;
			try {
				conn = ConfigDB.getConn();
				
				Map<String,String> map = DeviceInfoUtils.getMachineProperties(conn, entityobj, nodeid);
				cbip.add(entityobj.getLocalip());
				String ips = map.get("ipAddress");
				JSONArray ipArray = JSONArray.fromObject(ips);
				for (int i = 0; i < ipArray.size(); i++) {
					if (ipArray.get(i) instanceof String) {
						String ip = (String) ipArray.get(i);
						if (!entityobj.getLocalip().equals(ip))
							cbip.add((String) ipArray.get(i));
					}
				}
				cbip.select(0);
				txtmac.setText(map.get("mac"));
				txtcustom.setText(map.get("customName"));
				txtname.setText(map.get("deviceName"));
				int inselect = entityobj.getSvgtype() + 1;
				if (txttype.getItemCount() > inselect) {
					txttype.select(entityobj.getSvgtype());
				} else {
					txttype.select(6);
				}
				txtruntime.setText(map.get("uptime"));
				txtoid.setText(entityobj.getProperys().get("sysObjectID"));
				txtmaskfield.setText(entityobj.getProperys().get("maskField"));
				txtsubnet.setText(entityobj.getProperys().get("subnetName"));
				txtfact.setText(map.get("factory"));
				txdescrip.setText(map.get("desc"));
				txtmemo.setText(map.get("remark"));
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if(conn != null)
					ConfigDB.close(conn);
			}
//			cbip.add(entityobj.getLocalip());
//			String[] iplists = entityobj.getProperys().get("ipList").split("\\,");
//			for (String it : iplists) {
//				if (!entityobj.getLocalip().equals(it))
//					cbip.add(it);
//			}
//			cbip.select(0);
//			txtmac.setText(entityobj.getMac());
//			if (entityobj.getCustomname() != null)
//				txtcustom.setText(entityobj.getCustomname());
//			txtname.setText(entityobj.getDevicename());
//			int inselect = entityobj.getSvgtype() + 1;
//			if (txttype.getItemCount() > inselect) {
//				txttype.select(entityobj.getSvgtype());
//			} else {
//				txttype.select(6);
//			}
//			txtruntime.setText(getUptime(entityobj));
//			txtoid.setText(entityobj.getProperys().get("sysObjectID"));
//			txtmaskfield.setText(entityobj.getProperys().get("maskField"));
//			txtsubnet.setText(entityobj.getProperys().get("subnetName"));
//			if (entityobj.getFactory() != null)
//				txtfact.setText(entityobj.getFactory());
		}
	}

	/**
	 * Machine Port
	 * 
	 * @param composite_MachinePort
	 * @param entityobj
	 */
	private static void createMachinePort(Composite composite_MachinePort, final svNode entityobj, final String nodeid) {
		final TableViewer tableViewer = new TableViewer(composite_MachinePort, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		tableViewer.setColumnProperties(new String[] {});

		Table table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));

		String[] names = { "端口序号", "端口类型", "端口别名", "Mac地址", "端口描述", "对应IP" };
		int[] widths = { 100, 120, 120, 150, 150, 400 };
		ControlUtils.createTableColumns(tableViewer, names, widths);

		// 设置内容器
		tableViewer.setContentProvider(new PortsContentProvider());
		// 设置标签器
		tableViewer.setLabelProvider(new PortsLabelProvider());
		// 把数据集合给tableView
		if (entityobj != null) {
			ArrayList<DPort> dport_list = buildata(entityobj, nodeid);
			if (!dport_list.isEmpty())
				tableViewer.setInput(dport_list);
			composite_MachinePort.layout();
		}
	}

	private static ArrayList<DPort> buildata(svNode entityobj,String nodeid) {
		ArrayList<DPort> lll = new ArrayList<DPort>();
		Connection conn = null;
		try {
			conn = ConfigDB.getConn();
			List<Map<String, String>>  list = DeviceInfoUtils.getMachinePortData(conn, entityobj, nodeid);
			for (Map<String, String> map : list) {
				DPort dport = new DPort();
				dport.setID(map.get("index"));
				dport.setPType(map.get("porttype"));
				dport.setPMac(map.get("mac"));
				dport.setPDesc(map.get("desc"));
				dport.setPIP(map.get("ip"));
				lll.add(dport);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
//		Map<String, entity> allNodes = new HashMap<String, entity>();
//		for (String topolabel : DBManage.Topos.keySet()) {
//			TopoChart topochart = DBManage.Topos.get(topolabel);
//			allNodes.putAll(topochart.getNodes());
//		}
//		Connection conn = ConfigDB.getConn();
//		String sql = "select pindex,porttype,mac,desc from ports where id='" + nodeid + "'";
//		ResultSet rs = ConfigDB.query(sql, conn);
//		try {
//			while (rs.next()) {
//				String pindex = rs.getString("pindex");
//				String porttype = rs.getString("porttype");
//				String mac = rs.getString("mac");
//				String desc = rs.getString("desc");
//				DPort dport = new DPort();
//				dport.setID(pindex);
//				dport.setPType(PortManage.portTypenum2TypeDesc.get(porttype));
//				dport.setPMac(mac);
//				dport.setPDesc(desc);
//				dport.setPIP(GetPortConnectionIP(pindex, nodeid, allNodes, nodeip));
//				lll.add(dport);
//			}
//		} catch (NumberFormatException e) {
//			e.printStackTrace();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} catch (Exception ex) {
//
//		}
//		ConfigDB.close(conn);
		return lll;
	}
}
