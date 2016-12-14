package com.siteview.NNM.dialogs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import siteview.windows.forms.MsgBox;

import com.siteview.NNM.ContentProviders.PortsContentProvider;
import com.siteview.NNM.ContentProviders.PortsLabelProvider;
import com.siteview.NNM.modles.DPort;
import com.siteview.ecc.authorization.ItossAuthorizeServiceImpl;
import com.siteview.ecc.authorization.PermissionFactory;
import com.siteview.ecc.constants.Operation;
import com.siteview.ecc.constants.Resource;
import com.siteview.nnm.data.ConfigDB;
import com.siteview.nnm.data.DBManage;
import com.siteview.nnm.data.PortManage;
import com.siteview.nnm.data.model.TopoChart;
import com.siteview.nnm.data.model.entity;
import com.siteview.nnm.data.model.svEdge;
import com.siteview.nnm.data.model.svNode;
import com.siteview.topo.TopoMap;
import com.siteview.topology.model.SnmpPara;
import com.siteview.topology.snmp.snmpTools;

public class DevicePropery extends Dialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String nodeid = "";
	Text txtmac;
	Combo cbip;
	svNode entityobj;
	String nodeip = "";
	private Table table;
	Text txtcustom;
	Combo txttype;
	TopoMap topochart;
	Text txtmemo;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public DevicePropery(Shell parentShell, String nodeid, String lab,
			TopoMap topochart) {
		super(parentShell);
		setShellStyle(SWT.MAX | SWT.RESIZE);
		this.nodeid = nodeid;
		this.topochart = topochart;
		TopoChart tochart = com.siteview.nnm.data.DBManage.Topos.get(lab);
		this.entityobj = (svNode) tochart.getNodes().get(nodeid);

		nodeip = this.entityobj.getLocalip();

	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
      try{
    	  
     
		String desc1 = "";
		String memo1 = "";
		Connection conn = ConfigDB.getConn();
		String sql = "select descrip,memo1 from devices where nid='" + nodeid
				+ "'";
		ResultSet rs = ConfigDB.query(sql, conn);
		try {
			while (rs.next()) {
				desc1 = rs.getString("descrip");
				if (desc1 == null)
					desc1 = "";
				memo1 = rs.getString("memo1");
				if (memo1 == null)
					memo1 = "";
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception ex) {

		}
		ConfigDB.close(conn);
		TabFolder tabFolder = new TabFolder(container, SWT.NONE);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("\u4E00\u822C\u4FE1\u606F");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(composite);
		composite.setLayout(new GridLayout(4, false));
		Label lbip = new Label(composite, SWT.NONE);
		lbip.setText("\u8BBE\u5907\u5730\u5740\uFF1A");
		cbip = new Combo(composite, SWT.NONE);
		cbip.setVisibleItemCount(10);
		cbip.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cbip.add(this.entityobj.getLocalip());
		String[] iplists = this.entityobj.getProperys().get("ipList")
				.split("\\,");
		for (String it : iplists) {
			if (!this.entityobj.getLocalip().equals(it))
				cbip.add(it);
		}
		cbip.select(0);

		Label lbmac = new Label(composite, SWT.NONE);
		lbmac.setText("\u8BBE\u5907mac\uFF1A");
		txtmac = new Text(composite, SWT.BORDER);
		txtmac.setEditable(false);
		txtmac.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txtmac.setText(this.entityobj.getMac());

		Label lbname = new Label(composite, SWT.NONE);
		lbname.setText("\u8BBE\u5907\u540D\u79F0\uFF1A");
		lbname.setAlignment(SWT.RIGHT);
		Text txtname = new Text(composite, SWT.BORDER | SWT.READ_ONLY);

		txtname.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txtname.setText(this.entityobj.getDevicename());

		Label lbcustom = new Label(composite, SWT.NONE);
		lbcustom.setAlignment(SWT.RIGHT);
		lbcustom.setText("\u81EA\u5B9A\u4E49\u540D\u79F0\uFF1A");

		txtcustom = new Text(composite, SWT.BORDER);
		txtcustom.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		if (this.entityobj.getCustomname() != null)
			txtcustom.setText(this.entityobj.getCustomname());

		Label lbtype = new Label(composite, SWT.NONE);
		lbtype.setText("\u8BBE\u5907\u7C7B\u578B\uFF1A");
		lbtype.setAlignment(SWT.RIGHT);
		txttype = new Combo(composite, SWT.NONE);
		txttype.setVisibleItemCount(10);
		txttype.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		// gd_txtname.widthHint = 107;
		txttype.add("三层交换机");
		txttype.add("二层交换机");
		txttype.add("路由器");
		txttype.add("防火墙");
		txttype.add("服务器");
		txttype.add("PC终端");
		txttype.add("其它");
		// txttype.add("非snmp服务器");
		int inselect = this.entityobj.getSvgtype() + 1;
		if (txttype.getItemCount() > inselect) {
			txttype.select(this.entityobj.getSvgtype());
		} else {
			txttype.select(6);
		}

		// if(this.entityobj.getSvgtype()==8){
		// txttype.select(7);
		// }

		Label lbfact = new Label(composite, SWT.NONE);
		lbfact.setAlignment(SWT.RIGHT);
		lbfact.setText("\u5236\u9020\u5382\u5546\uFF1A");

		Text txtfact = new Text(composite, SWT.BORDER);
		txtfact.setEditable(false);
		txtfact.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		if (this.entityobj.getFactory() != null)
			txtfact.setText(this.entityobj.getFactory());

		Label lbsubnet = new Label(composite, SWT.NONE);
		lbsubnet.setText("\u6240\u5728\u5B50\u7F51\uFF1A");
		lbsubnet.setAlignment(SWT.RIGHT);
		Text txtsubnet = new Text(composite, SWT.BORDER);
		txtsubnet.setEditable(false);
		txtsubnet.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txtsubnet.setText(this.entityobj.getProperys().get("subnetName"));

		Label lbmaskfield = new Label(composite, SWT.NONE);
		lbmaskfield.setAlignment(SWT.RIGHT);
		lbmaskfield.setText("\u5B50\u7F51\u63A9\u7801\uFF1A");

		Text txtmaskfield = new Text(composite, SWT.BORDER);
		txtmaskfield.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txtmaskfield.setEditable(false);
		String maskfieldd=this.entityobj.getProperys().get("maskField");
		if(maskfieldd.startsWith(",")){
			maskfieldd=maskfieldd.substring(1);
		}
		txtmaskfield.setText(maskfieldd);

		Label lboid = new Label(composite, SWT.NONE);
		lboid.setAlignment(SWT.LEFT);
		lboid.setText("\u8BBE\u5907OID\uFF1A");

		Text txtoid = new Text(composite, SWT.BORDER);
		txtoid.setEditable(false);
		// gd_txtoid.widthHint = 444;
		GridData gd_txtoid = new GridData(GridData.FILL_HORIZONTAL);
		gd_txtoid.horizontalSpan = 3;
		txtoid.setLayoutData(gd_txtoid);
		txtoid.setText(this.entityobj.getProperys().get("sysObjectID"));

		Label lbruntime = new Label(composite, SWT.NONE);
		lbruntime.setAlignment(SWT.LEFT);
		lbruntime.setText("\u8FD0\u884C\u65F6\u95F4\uFF1A");

		Text txtruntime = new Text(composite, SWT.BORDER);
		txtruntime.setEditable(false);
		// gd_txtruntime.widthHint = 445;
		GridData gd_txtruntime = new GridData(GridData.FILL_HORIZONTAL);
		gd_txtruntime.horizontalSpan = 3;
		txtruntime.setLayoutData(gd_txtruntime);
		txtruntime.setText(getUptime());

		Label lbdescrip = new Label(composite, SWT.NONE);
		lbdescrip.setAlignment(SWT.RIGHT);
		lbdescrip.setText("\u8BBE\u5907\u63CF\u8FF0\uFF1A");
		Text txdescrip = new Text(composite, SWT.BORDER | SWT.V_SCROLL
				| SWT.MULTI | SWT.WRAP);
		txdescrip.setEditable(false);

		GridData gd_txdescrip = new GridData(GridData.FILL_HORIZONTAL);
		gd_txdescrip.horizontalSpan = 3;
		gd_txdescrip.heightHint = 72;
		txdescrip.setLayoutData(gd_txdescrip);
		txdescrip.setText(desc1);

		Label lbmemo = new Label(composite, SWT.NONE);
		lbmemo.setAlignment(SWT.LEFT);
		lbmemo.setText("\u8BBE\u5907\u5907\u6CE8\uFF1A");
		txtmemo = new Text(composite, SWT.BORDER | SWT.MULTI);
		GridData gd_txtmemo = new GridData(GridData.FILL_HORIZONTAL);
		gd_txtmemo.horizontalSpan = 3;
		gd_txtmemo.heightHint = 69;
		txtmemo.setLayoutData(gd_txtmemo);
		txtmemo.setText(memo1);

		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("\u7AEF\u53E3\u4FE1\u606F");
		Composite composite_1 = new Composite(tabFolder, SWT.Modify);
		tbtmNewItem.setControl(composite_1);
		composite_1.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite_1.setLayout(new GridLayout());
		TableViewer tableViewer = new TableViewer(composite_1, SWT.MULTI
				| SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		tableViewer.setColumnProperties(new String[] {});

		table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));

		final TableColumn newColumnTableColumn = new TableColumn(table,
				SWT.NONE);
		newColumnTableColumn.setWidth(80);
		newColumnTableColumn.setText("端口序号");

		final TableColumn newColumnTableColumn_1 = new TableColumn(table,
				SWT.NONE);
		newColumnTableColumn_1.setWidth(90);
		newColumnTableColumn_1.setText("端口类型");
		final TableColumn newColumnTableColumn_5 = new TableColumn(table,
				SWT.NONE);
		newColumnTableColumn_5.setWidth(90);
		newColumnTableColumn_5.setText("别名");
		final TableColumn newColumnTableColumn_2 = new TableColumn(table,
				SWT.NONE);
		newColumnTableColumn_2.setWidth(85);
		newColumnTableColumn_2.setText("Mac地址");

		final TableColumn newColumnTableColumn_3 = new TableColumn(table,
				SWT.NONE);
		newColumnTableColumn_3.setWidth(100);
		newColumnTableColumn_3.setText("端口描述");

		final TableColumn newColumnTableColumn_4 = new TableColumn(table,
				SWT.NONE);
		newColumnTableColumn_4.setWidth(400);
		newColumnTableColumn_4.setText("对应IP");

		// 设置内容器
		tableViewer.setContentProvider(new PortsContentProvider());
		// 设置标签器
		tableViewer.setLabelProvider(new PortsLabelProvider());
		// 把数据集合给tableView

		tableViewer.setInput(buildata());
      }catch(Exception ex){
    	  
      }
		return container;
	}

	private ArrayList buildata() {
		ArrayList lll = new ArrayList();
		Map<String, entity> allNodes = new HashMap<String, entity>();
		for (String topolabel : DBManage.Topos.keySet()) {
			TopoChart topochart = DBManage.Topos.get(topolabel);
			allNodes.putAll(topochart.getNodes());
		}
		Connection conn = ConfigDB.getConn();
		String sql = "select pindex,porttype,mac,desc,alias from ports where id='"
				+ nodeid + "'";
		ResultSet rs = ConfigDB.query(sql, conn);
		try {
			while (rs.next()) {
				String pindex = rs.getString("pindex");
				String porttype = rs.getString("porttype");
				String mac = rs.getString("mac");
				String desc = rs.getString("desc");
				String alias = rs.getString("alias");
				DPort dport = new DPort();
				dport.setID(pindex);
				dport.setpAlias(alias);
				dport.setPType(PortManage.portTypenum2TypeDesc.get(porttype));
				dport.setPMac(mac);
				dport.setPDesc(desc);
				dport.setPIP(GetPortConnectionIP(pindex, nodeid, allNodes));
				lll.add(dport);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception ex) {

		}
		ConfigDB.close(conn);
		return lll;
	}

	private String GetPortConnectionIP(String pindex, String portID,
			Map<String, entity> allNodes) {

		String IPs = "";
		for (TopoChart topochart : DBManage.Topos.values()) {
			for (svEdge sedge : topochart.getEdges().values()) {
				if (sedge.getLsource().equals(portID)
						&& sedge.getSinterface().equals(pindex)) {
					String rightID = sedge.getLtarget();
					svNode nn = (svNode) allNodes.get(rightID);
					// hub
					if (nn.getSvgtype() == 6 || nn.getSvgtype() == 7) {
						IPs = GetHubConnectionIP(rightID, allNodes);
					} else {
						IPs = nn.getLocalip() + "," + IPs;
					}
				} else if (sedge.getLtarget().equals(portID)
						&& sedge.getTinterface().equals(pindex)) {
					String leftID = sedge.getLsource();
					svNode nn = (svNode) allNodes.get(leftID);
					if (nn.getSvgtype() == 6 || nn.getSvgtype() == 7) {
						IPs = GetHubConnectionIP(leftID, allNodes);
					} else {
						IPs = nn.getLocalip() + "," + IPs;
					}
				}

			}

		}
		if (IPs.isEmpty())
			IPs = "No Connection Device";
		return IPs;
	}

	private String GetHubConnectionIP(String hubid, Map<String, entity> allNodes) {
		String ips = "";
		String newip = "";

		for (TopoChart topochart : DBManage.Topos.values()) {
			for (svEdge sedge : topochart.getEdges().values()) {
				if (sedge.getLsource().equals(hubid)) {
					String rightID = sedge.getLtarget();
					svNode rnode = (svNode) allNodes.get(rightID);
					newip = rnode.getLocalip();
					if (!nodeip.equals(newip))
						ips = newip + "," + ips;

				} else if (sedge.getLtarget().equals(hubid)) {
					String leftID = sedge.getLsource();
					svNode lnode = (svNode) allNodes.get(leftID);
					newip = lnode.getLocalip();
					if (!nodeip.endsWith(newip))
						ips = newip + "," + ips;

				}
			}
		}

		return ips;

	}

	/**
	 * 
	 * @return
	 */
	private String getUptime() {
		String snmpenable = this.entityobj.getProperys().get("snmpEnabled");
		if (snmpenable == null || snmpenable.isEmpty()
				|| snmpenable.equals("0")) {
			return "0";
		}
		String dip = this.entityobj.getLocalip();
		String comm = this.entityobj.getProperys().get("Community");
		if (comm == null || comm.isEmpty()) {
			return "0";
		}
		String dver = this.entityobj.getProperys().get("Version");
		SnmpPara para = new SnmpPara(dip, comm, 2000, 2);
		if (dver.equals("1")) {
			para.setSnmpver(1);
		} else if (dver.equals("2")) {
			para.setSnmpver(2);
		}
		String rr = snmpTools.getValue(para, "1.3.6.1.2.1.1.3.0");
		if (rr == null || rr.isEmpty())
			rr = "0";
		return rr;
	}

	protected void buttonPressed(int buttonId) {
		try {
			if (buttonId == IDialogConstants.OK_ID) {
				if (ItossAuthorizeServiceImpl
						.getInstance()
						.isPermitted(
								PermissionFactory
										.createPermission(
												Resource.TOPU_OPERATION_IN_VIEW_EDIT_DEVICE_PROP
														.getType(),
												Operation.TOPUOPERATIONINVIEW_EDIT_DEVICE_PROP
														.getOperationString(),
												"*"))) {
					try {
						int nidid = Integer.parseInt(nodeid.substring(1));
						int se1 = txttype.getSelectionIndex();
						if (se1 == 7)
							se1 = 8;
						DBManage.updatenode(nidid, cbip.getText(), se1 + "",
								txtcustom.getText().trim());
						Connection conn = ConfigDB.getConn();
						String sql = "update devices set memo1='"
								+ txtmemo.getText() + "' where nid='" + nodeid
								+ "'";
						ConfigDB.excute(sql, conn);
						ConfigDB.close(conn);
					} catch (Exception ee) {
					}

					this.topochart.setPnodedata(new JsonObject()
							.add("nid", nodeid)
							.add("cname", txtcustom.getText().trim())
							.add("ntype", txttype.getSelectionIndex()).add("newip", cbip.getText()));
				} else {
					MsgBox.ShowError("提示","无此权限!");
					return;

				}
			}
		} catch (Exception e) {
		}
		super.buttonPressed(buttonId);
	}

	protected void configureShell(Shell newShell) {
		newShell.setText("设备属性");
		super.configureShell(newShell);
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "保存", true);
		createButton(parent, IDialogConstants.CANCEL_ID, "取消", false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(596, 493);
	}
}
