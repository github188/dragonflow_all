package com.siteview.NNM.dialogs;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.util.BundleUtility;

import com.siteview.NNM.Activator;
import com.siteview.NNM.NNMInstanceData;
import com.siteview.NNM.Editors.NNMainEditorInput;
import com.siteview.NNM.Editors.TopoManage;
import com.siteview.NNM.Views.NNMTreeView;
import com.siteview.NNM.modles.RootNNM;
import com.siteview.NNM.modles.SubChartModle;
import com.siteview.NNM.modles.TopoModle;
import com.siteview.NNM.resource.ImageResource;
import com.siteview.NNM.uijobs.RefreshNNMTree;
import com.siteview.NNM.uijobs.ScanWaitdialog;
import com.siteview.NNM.uijobs.ScanWorker;
import com.siteview.ecc.monitor.nls.EccMessage;
import com.siteview.nmap.scan.DBAuthent;
import com.siteview.nnm.data.ConfigDB;
import com.siteview.nnm.data.DBManage;
import com.siteview.nnm.data.model.entity;
import com.siteview.nnm.data.model.svEdge;
import com.siteview.topology.model.Pair;
import com.siteview.topology.model.Snmpv3config;
import com.siteview.topology.scan.TopoScan;

import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ConnectionBroker;
import siteview.windows.forms.ImageHelper;
import siteview.windows.forms.MsgBox;

public class ScanNetwork extends Dialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Map<String, entity> n_nodes;
	Map<String, svEdge> n_Lines;
	Text sIpText1;
	Text eIpText1;
	Text sIpText2;
	Text eIpText2;
	Text IpText;
	Text subtopoText;
	org.eclipse.swt.widgets.List iplist;
	org.eclipse.swt.widgets.List ipwdlist;

	public Button ip4Button;
	public Button ip6Button;
	public Button ip4btn;
	public Button ip6btn;
	public Button btnnormal;
	public Button btncdp;
	public Button btnlog;
	public Button btnsubtopo;//子图扫描

	Tree iprangeTree;
	TreeItem treeItem_0;
	private Text Communitytext;

	TreeItem treeItem;
	Tree tree;
	private Action snmpv1v2;// snmpv1v2
	private Action telnet_ssh;
	private Action database;
	private Action snmpv3;// snmpv1v2

	public Map<String, List<HashMap<String, String>>> userpass = new HashMap<String, List<HashMap<String, String>>>();
	private ISiteviewApi m_api;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public ScanNetwork(Shell parentShell) {
		super(parentShell);
		this.m_api = ConnectionBroker.get_SiteviewApi();
	}

	protected void configureShell(Shell newShell) {
		newShell.setText("扫描");
		super.configureShell(newShell);
	}

	public static boolean matches_IPV4(String text) {
		if (text != null && !text.isEmpty()) {
			String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
			String regex1 = "^(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])$";
			if (text.matches(regex)) {
				if (text.matches(regex1))
					return true;
			} else {
				return false;
			}
		}
		// 返回判断信息
		return false;
	}

	public static boolean matches_IPV6(String address) {
		boolean result = false;
		String regHex = "(\\p{XDigit}{1,4}";
		String regIPv6Full = "^(" + regHex + ":){7}" + regHex + "$";
		String regIPv6AbWithColon = "^(" + regHex + "(:|::)){0,6}" + regHex
				+ "$";
		String regIPv6AbStartWithDoubleColon = "^(" + "::(" + regHex
				+ ":){0,5}" + regHex + ")$";
		String regIPv6 = "^(" + regIPv6Full + ")|("
				+ regIPv6AbStartWithDoubleColon + ")|(" + regIPv6AbWithColon
				+ ")$";
		if (address.indexOf(":") != -1) {
			if (address.length() <= 39) {
				String addressTemp = address;
				int doubleColon = 0;
				while (addressTemp.indexOf("::") != -1) {
					addressTemp = addressTemp
							.substring(addressTemp.indexOf("::") + 2,
									addressTemp.length());
					doubleColon++;
				}
				if (doubleColon <= 1) {
					result = address.matches(regIPv6);
				}
			}
		}

		return result;
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		GridData IP_data = new GridData();
		IP_data.widthHint = 310;
		GridData IP_data1 = new GridData();
		IP_data1.widthHint = 310;
		GridData data_2 = new GridData();
		data_2.widthHint = 200;
		
		TabFolder tabFolder = new TabFolder(composite, SWT.NONE);
		TabItem tbIpRange = new TabItem(tabFolder, SWT.NONE);
		tbIpRange.setText("IP\u5730\u5740\u8303\u56F4");
		Composite startComposite = new Composite(tabFolder, SWT.NONE);
		startComposite.setLayout(new GridLayout(2, false));
		Composite endComposite = new Composite(tabFolder, SWT.NONE);
		endComposite.setLayout(new GridLayout(2, false));
		tbIpRange.setControl(startComposite);
		TabItem tbIpSeed = new TabItem(tabFolder, SWT.NONE);
		tbIpSeed.setText("\u6838\u5FC3\u8DEF\u7531ip\u79CD\u5B50");
		tbIpSeed.setControl(endComposite);

		ip4Button = new Button(startComposite, SWT.RADIO);
		ip4Button.setText("IPv4");
		ip4Button.setSelection(true);
		ip6Button = new Button(startComposite, SWT.RADIO);
		ip6Button.setText("IPv6");
		data_2 = new GridData();
		// data_2.horizontalIndent = -70;
		data_2.widthHint = 200;
		ip6Button.setLayoutData(data_2);
		ip6Button.setVisible(false);

		Label startIpLabel = new Label(startComposite, SWT.NONE);
		startIpLabel.setText("Start IP:");

		sIpText1 = new Text(startComposite, SWT.BORDER);
		sIpText1.setLayoutData(IP_data);

		Label endIpLabel = new Label(startComposite, SWT.NONE);
		endIpLabel.setText("End IP:");

		eIpText1 = new Text(startComposite, SWT.BORDER);
		eIpText1.setLayoutData(IP_data1);

		CLabel cLabel = new CLabel(startComposite, SWT.BORDER);
		cLabel.setText("Add");
		cLabel.setImage(ImageHelper.LoadImage(Activator.PLUGIN_ID,
				ImageResource.Add));

		CLabel delete = new CLabel(startComposite, SWT.BORDER);
		delete.setText("Delete");
		data_2 = new GridData();
		// data_2.horizontalIndent = -70;
		data_2.widthHint = 200;
		delete.setImage(ImageHelper.LoadImage(Activator.PLUGIN_ID,
				ImageResource.Delete));
		delete.setLayoutData(data_2);

		Composite ipRange = new Composite(startComposite, SWT.BORDER);
		GridData data_1 = new GridData();
		data_1.heightHint = 90;
		data_1.widthHint = 410;
		data_1.horizontalSpan = 2;
		data_1.verticalSpan = 2;
		ipRange.setLayoutData(data_1);
		ipRange.setLayout(new FillLayout());
		iprangeTree = new Tree(ipRange, SWT.FULL_SELECTION | SWT.CHECK);
		treeItem_0 = new TreeItem(iprangeTree, SWT.NONE);
		treeItem_0.setText("全选");
		showScanRange(treeItem_0);
		treeItem_0.setExpanded(true);

		cLabel.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event event) {
				String sIpTexts1 = sIpText1.getText().trim();
				String eIpTexts1 = eIpText1.getText().trim();
				if (!ip6Button.getSelection()) {
					if (!matches_IPV4(sIpTexts1) || !matches_IPV4(eIpTexts1)) {
						MsgBox.ShowError(EccMessage.get().ERROR, "ip地址无效！");
						return;
					}
				} else {
					if (!matches_IPV6(sIpTexts1)) {
						MsgBox.ShowError(EccMessage.get().ERROR, "ip地址无效！");
						return;
					}
				}
				String iprange = sIpTexts1 + "-" + eIpTexts1;
				TreeItem[] items = treeItem_0.getItems();
				for (TreeItem treeItem : items) {
					String tempIPRange = treeItem.getText();
					if (iprange.equals(tempIPRange)) {
						MsgBox.ShowError(EccMessage.get().ERROR, "ip地址范围已经存在！");
						return;
					}
				}

				TreeItem treeitem = new TreeItem(treeItem_0, SWT.NONE);
				treeitem.setText(iprange);
				treeitem.setChecked(treeItem_0.getChecked());
				treeitem.setChecked(true);
				treeItem_0.setExpanded(true);

				Connection conn = ConfigDB.getConn();
				int ipv66 = 0;
				if (ip6Button.getSelection())
					ipv66 = 1;
				String sql = "insert into iprange(startip,endip,ipv6) values('"
						+ sIpTexts1 + "','" + eIpTexts1 + "'," + ipv66 + ");";
				ConfigDB.excute(sql, conn);
				ConfigDB.close(conn);
				// try {
				// BusinessObject bo =
				// ConnectionBroker.get_SiteviewApi().get_BusObService().Create("ScanRange");
				// bo.GetField("Range").SetValue(new SiteviewValue(iprange));
				// bo.SaveObject(ConnectionBroker.get_SiteviewApi(), true,
				// true);
				// treeitem.setData(bo);
				// } catch (SiteviewException e) {
				// e.printStackTrace();
				// }
			}
		});
		delete.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub

				int i = 0;
				TreeItem[] tree = iprangeTree.getItems();
				TreeItem[] treeitems = tree[0].getItems();
				for (TreeItem treeItem : treeitems) {
					if (treeItem.getChecked()) {
						i = 1;
						Connection conn = ConfigDB.getConn();
						String[] ipss = treeItem.getText().split("\\-");
						String sql = "delete from iprange where startip='"
								+ ipss[0] + "' and endip='" + ipss[1] + "'";
						ConfigDB.excute(sql, conn);
						ConfigDB.close(conn);
						treeItem.dispose();
					}
				}
				if (i == 0) {
					MsgBox.ShowMessage("选择删除的范围!");
					return;
				}

			}

		});
		iprangeTree.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				boolean flag = e.detail == SWT.CHECK ? true : false;
				TreeItem treeitem = (TreeItem) e.item;
				if (!flag) {
					treeitem.setChecked(!treeitem.getChecked());
					if (treeitem.getChecked()) {
						String range = treeitem.getText();
						if (range.contains("-")) {
							String startip = range.substring(0,
									range.indexOf("-"));
							String endip = range.substring(
									range.indexOf("-") + 1, range.length());
							if (startip.contains(".") && endip.contains(".")) {
								sIpText1.setText(startip);
								eIpText1.setText(endip);
							}
						}
					}
				}
				checkorUncheckChild(treeitem);
			}

		});
		String localip = "";
		try {
			localip = InetAddress.getLocalHost().getHostAddress();
			if (localip.equals("127.0.0.1")) {
				sIpText1.setText("192.168.1.1");
				eIpText1.setText("192.168.1.254");
			} else {
				sIpText1.setText(localip.substring(0, localip.lastIndexOf(".")) + ".1");
				eIpText1.setText(localip.substring(0, localip.lastIndexOf(".")) + ".254");
			}
		} catch (UnknownHostException e) {
		}

		ip4btn = new Button(endComposite, SWT.RADIO);
		ip4btn.setText("IPv4");
		ip4btn.setSelection(true);
		ip6btn = new Button(endComposite, SWT.RADIO);
		ip6btn.setText("IPv6");
		ip6btn.setVisible(false);
		Label ipLabel = new Label(endComposite, SWT.NONE);
		ipLabel.setText("IP 地址：");
		IP_data = new GridData();
		IP_data.widthHint = 310;
		IpText = new Text(endComposite, SWT.BORDER);
		IpText.setLayoutData(IP_data);
		CLabel cLabel1 = new CLabel(endComposite, SWT.BORDER);
		cLabel1.setText("Add");
		cLabel1.setImage(ImageHelper.LoadImage(Activator.PLUGIN_ID, ImageResource.Add));

		CLabel delete1 = new CLabel(endComposite, SWT.BORDER);
		delete1.setText("Delete");
		data_2 = new GridData();
		data_2.widthHint = 200;
		delete1.setImage(ImageHelper.LoadImage(Activator.PLUGIN_ID, ImageResource.Delete));
		delete1.setLayoutData(data_2);

		iplist = new org.eclipse.swt.widgets.List(endComposite, SWT.BORDER);
		data_1 = new GridData();
		data_1.heightHint = 100;
		data_1.widthHint = 410;
		data_1.horizontalSpan = 2;
		data_1.verticalSpan = 2;
		iplist.setLayoutData(data_1);

		cLabel1.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event event) {
				String IpTexts = IpText.getText().trim();

				if (!ip6Button.getSelection()) {
					if (!matches_IPV4(IpTexts) || !matches_IPV4(IpTexts)) {
						MsgBox.ShowError(EccMessage.get().ERROR, "ip地址无效!");
						return;
					}
				} else {
					if (!matches_IPV6(IpTexts)) {
						MsgBox.ShowError(EccMessage.get().ERROR, "ip地址无效!");
						return;
					}
				}
				iplist.add(IpTexts);
				Connection conn = ConfigDB.getConn();
				String sql = "insert into ipseeds(seeds) values('" + IpTexts
						+ "');";
				ConfigDB.excute(sql, conn);
				ConfigDB.close(conn);
				// try {
				// BusinessObject bo =
				// ConnectionBroker.get_SiteviewApi().get_BusObService().Create("ScanRange");
				// bo.GetField("Range").SetValue(new SiteviewValue(iprange));
				// bo.SaveObject(ConnectionBroker.get_SiteviewApi(), true,
				// true);
				// treeitem.setData(bo);
				// } catch (SiteviewException e) {
				// e.printStackTrace();
				// }
			}

		});
		delete1.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event event) {
				int i = 0;
				String[] selectseeds = iplist.getSelection();
				Connection conn = ConfigDB.getConn();
				for (String ipItem : selectseeds) {
					i = 1;
					String sql = "delete from ipseeds where seeds='" + ipItem
							+ "'";
					ConfigDB.excute(sql, conn);
					iplist.remove(ipItem);
				}

				ConfigDB.close(conn);
				if (i == 0) {
					MsgBox.ShowMessage("选择删除的范围!");
					return;
				}
			}

		});

		TabFolder tabFolder1 = new TabFolder(composite, SWT.NONE);
		TabItem tbpwd = new TabItem(tabFolder1, SWT.NONE);
		tbpwd.setText("\u5168\u5C40\u5BC6\u7801");

		Composite start1Composite = new Composite(tabFolder1, SWT.NONE);
		start1Composite.setLayout(new GridLayout(2, false));
		Composite ipcommComposite = new Composite(tabFolder1, SWT.NONE);
		ipcommComposite.setLayout(new GridLayout(4, false));
		tbpwd.setControl(start1Composite);
		TabItem tbiprangepwd = new TabItem(tabFolder1, SWT.NONE);
		tbiprangepwd.setText("IP\u8303\u56F4\u5BC6\u7801");
		tbiprangepwd.setControl(ipcommComposite);

		// Label needPwdLabel = new Label(start1Composite, SWT.NONE);
		// needPwdLabel.setText("要添加的密码:");
		final CLabel addPwdLabel = new CLabel(start1Composite, SWT.NONE);
		addPwdLabel.setText("add");
		addPwdLabel.setImage(ImageHelper.LoadImage(Activator.PLUGIN_ID, ImageResource.Add));
		data_2 = new GridData();
		data_2.widthHint = 70;
		addPwdLabel.setLayoutData(data_2);
		final CLabel delPwdLabel = new CLabel(start1Composite, SWT.NONE);
		delPwdLabel.setText("delete");
		delPwdLabel.setImage(ImageHelper.LoadImage(Activator.PLUGIN_ID, ImageResource.Delete));

		delPwdLabel.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event event) {
				int i = 0;
				TreeItem[] tree1 = tree.getItems();
				TreeItem[] treeitems = tree1[0].getItems();
				for (TreeItem treeItem : treeitems) {
					if (treeItem.getChecked()) {
						i = 1;
						Connection conn = ConfigDB.getConn();
						HashMap<String, String> dbdb = (HashMap<String, String>) treeItem.getData();
						String sql = "";
						if (dbdb.containsKey("readcomm"))
							sql = "delete from snmpv1v2 where name='" + dbdb.get("name") + "' and readcomm='"
									+ dbdb.get("readcomm") + "'";
						if (dbdb.containsKey("protocol"))
							sql = "delete from telnetssh where name='" + dbdb.get("name") + "' and protocol='"
									+ dbdb.get("protocol") + "'";
						if (dbdb.containsKey("dbtype"))
							sql = "delete from databasetype where name='" + dbdb.get("name") + "' and dbtype='"
									+ dbdb.get("dbtype") + "'";
						if (dbdb.containsKey("authorization"))
							sql = "delete from snmpv3 where name='" + dbdb.get("name") + "' and authorization='"
									+ dbdb.get("authorization") + "'";
						ConfigDB.excute(sql, conn);
						ConfigDB.close(conn);
						treeItem.dispose();
					}
				}
				if (i == 0) {
					MsgBox.ShowMessage("选择删除的范围!");
					return;
				}
			}

		});
		createAction();
		final MenuManager pm = new MenuManager();
		pm.setRemoveAllWhenShown(true);
		pm.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				manager.add(snmpv1v2);
				manager.add(snmpv3);
				// manager.add(windows_wmic);
				manager.add(telnet_ssh);
				manager.add(database);
			}
		});
		final Menu popmenu = pm.createContextMenu(addPwdLabel);
		addPwdLabel.setMenu(popmenu);
		addPwdLabel.addListener(SWT.MouseUp, new Listener() {
			public void handleEvent(Event e) {
				popmenu.setLocation(((CLabel) e.widget).toDisplay(0, 20));
				popmenu.setVisible(true);
			}
		});
		new Label(start1Composite, SWT.NONE);
		new Label(start1Composite, SWT.NONE);
		Composite pwdRange = new Composite(start1Composite, SWT.BORDER);
		data_1 = new GridData();
		data_1.heightHint = 120;
		data_1.widthHint = 410;
		data_1.horizontalSpan = 2;
		data_1.verticalSpan = 2;
		pwdRange.setLayoutData(data_1);
		pwdRange.setLayout(new FillLayout());
		tree = new Tree(pwdRange, SWT.FULL_SELECTION | SWT.CHECK);
		treeItem = new TreeItem(tree, SWT.NONE);
		treeItem.setText("全选");
		showPassword(treeItem);
		treeItem.setExpanded(true);
		tree.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				boolean flag = e.detail == SWT.CHECK ? true : false;
				TreeItem treeitem = (TreeItem) e.item;
				if (!flag) {
					treeitem.setChecked(!treeitem.getChecked());
				}
				checkorUncheckChild(treeitem);
			}

		});

		Label commlabel = new Label(ipcommComposite, SWT.NONE);
		commlabel.setText("community:");
		IP_data = new GridData();
		IP_data.widthHint = 120;
		Communitytext = new Text(ipcommComposite, SWT.BORDER);
		Communitytext.setLayoutData(IP_data);
		new Label(ipcommComposite, SWT.NONE);
		new Label(ipcommComposite, SWT.NONE);
		IP_data = new GridData();
		IP_data.widthHint = 120;
		Label startIpLabel1 = new Label(ipcommComposite, SWT.NONE);
		startIpLabel1.setText("Start IP:");
		sIpText2 = new Text(ipcommComposite, SWT.BORDER);
		sIpText2.setLayoutData(IP_data);

		Label endIpLabel1 = new Label(ipcommComposite, SWT.NONE);
		endIpLabel1.setText("End IP:");
		IP_data = new GridData();
		IP_data.widthHint = 120;
		eIpText2 = new Text(ipcommComposite, SWT.BORDER);
		eIpText2.setLayoutData(IP_data);
		final CLabel addPwdRangeLabel = new CLabel(ipcommComposite, SWT.NONE);
		addPwdRangeLabel.setText("add");
		addPwdRangeLabel.setImage(ImageHelper.LoadImage(Activator.PLUGIN_ID, ImageResource.Add));
		data_2 = new GridData();
		data_2.widthHint = 60;
		addPwdRangeLabel.setLayoutData(data_2);
		final CLabel deleteRangeLabel = new CLabel(ipcommComposite, SWT.NONE);
		deleteRangeLabel.setText("delete");
		deleteRangeLabel.setImage(ImageHelper.LoadImage(Activator.PLUGIN_ID, ImageResource.Delete));
		new Label(ipcommComposite, SWT.NONE);
		new Label(ipcommComposite, SWT.NONE);
		ipwdlist = new org.eclipse.swt.widgets.List(ipcommComposite, SWT.BORDER);
		data_1 = new GridData();
		data_1.heightHint = 70;
		data_1.widthHint = 410;
		data_1.horizontalSpan = 4;
		data_1.verticalSpan = 2;
		ipwdlist.setLayoutData(data_1);

		addPwdRangeLabel.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event event) {
				String comm = Communitytext.getText().trim();
				String sip = sIpText2.getText().trim();
				String eip = eIpText2.getText().trim();

				if (!ip6Button.getSelection()) {
					if (!matches_IPV4(sip) || !matches_IPV4(eip)) {
						MsgBox.ShowError(EccMessage.get().ERROR, "ip地址无效");
						return;
					}
				} else {
					if (!matches_IPV6(sip)) {
						MsgBox.ShowError(EccMessage.get().ERROR, "ip地址无效");
						return;
					}
				}

				ipwdlist.add(comm + ":" + sip + "-" + eip);
				Connection conn = ConfigDB.getConn();
				String sql = "insert into iprangecomm(startip,endip,comm) values('"
						+ sip + "','" + eip + "','" + comm + "');";
				ConfigDB.excute(sql, conn);
				ConfigDB.close(conn);
				// try {
				// BusinessObject bo =
				// ConnectionBroker.get_SiteviewApi().get_BusObService().Create("ScanRange");
				// bo.GetField("Range").SetValue(new SiteviewValue(iprange));
				// bo.SaveObject(ConnectionBroker.get_SiteviewApi(), true,
				// true);
				// treeitem.setData(bo);
				// } catch (SiteviewException e) {
				// e.printStackTrace();
				// }
			}

		});
		deleteRangeLabel.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event event) {
				int i = 0;
				String[] selectips = ipwdlist.getSelection();
				Connection conn = ConfigDB.getConn();
				for (String ipItem : selectips) {
					i = 1;
					String[] ipsss = ipItem.substring(ipItem.indexOf(":") + 1).split("\\-");
					String sql = "delete from iprangecomm where startip='" + ipsss[0] + "' and endip='" + ipsss[1]
							+ "'";
					ConfigDB.excute(sql, conn);
					ipwdlist.remove(ipItem);
				}

				ConfigDB.close(conn);
				if (i == 0) {
					MsgBox.ShowMessage("选择删除的范围!");
					return;
				}

			}

		});
		Composite hhComposite = new Composite(composite, SWT.NONE);
		GridLayout gl_hhComposite = new GridLayout(6, false);
		gl_hhComposite.marginHeight = 2;
		hhComposite.setLayout(gl_hhComposite);
		btnnormal = new Button(hhComposite, SWT.RADIO);
		btnnormal.setVisible(true);
		btnnormal.setSelection(true);
		btnnormal.setText("通用算法");
		btncdp = new Button(hhComposite, SWT.RADIO);
		btncdp.setText("CDP算法");
		btnlog =new Button(hhComposite, SWT.RADIO);
		btnlog.setText("历史数据");
		Label ll = new Label(hhComposite, SWT.NONE);		
		ll.setText("****");
		ll.setVisible(false);
		new Label(hhComposite, SWT.NONE);
		Button btnconfig = new Button(hhComposite, SWT.RIGHT);
		btnconfig.setText("扫描配置");
		btnconfig.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event event) {
				ScanConfig sc = new ScanConfig(Display.getCurrent().getActiveShell());
				sc.open();
			}

		});
		Composite subcomposite=new Composite(composite, SWT.NONE);
		GridLayout gl_subcomposite = new GridLayout(6, false);
		gl_subcomposite.marginHeight = 1;
		subcomposite.setLayout(gl_subcomposite);
		btnsubtopo =new Button(subcomposite,SWT.CHECK);
		btnsubtopo.setText("子图增量扫描");
		Label lbsubtopo= new Label(subcomposite, SWT.NONE);
		lbsubtopo.setText("子图名称：");
		GridData subdata = new GridData();
		subdata.widthHint = 120;
		subtopoText= new Text(subcomposite, SWT.BORDER);
		subtopoText.setLayoutData(subdata);
		new Label(subcomposite, SWT.NONE);
		new Label(subcomposite, SWT.NONE);
		new Label(subcomposite, SWT.NONE);
		showSeedandRangePass();
		return composite;
	}

	private void showScanRange(TreeItem treeItem) {
		Connection conn = null;
		try {
			conn = ConfigDB.getConn();
			String sql = "select startip,endip,ipv6 from iprange";
			ResultSet rs = ConfigDB.query(sql, conn);
			while (rs.next()) {
				String startip = "";
				String endip = "";
				startip = rs.getString("startip");
				endip = rs.getString("endip");
				boolean ipv6 = false;
				ipv6 = rs.getBoolean("ipv6");
				if (startip != null && !startip.isEmpty() && endip != null && !endip.isEmpty()) {
					String rangeip = startip + "-" + endip;
					TreeItem child = new TreeItem(treeItem, SWT.NONE);
					child.setText(rangeip);
					child.setData(ipv6);
				}
			}
		} catch (Exception ex) {

		} finally {
			if (conn != null)
				ConfigDB.close(conn);
		}
	}

	private void showSeedandRangePass() {
		Connection conn = null;
		try {
			conn = ConfigDB.getConn();
			String sql = "select seeds from ipseeds";
			ResultSet rs = ConfigDB.query(sql, conn);
			while (rs.next()) {
				String ipseed = "";
				ipseed = rs.getString("seeds");
				if (ipseed != null && !ipseed.isEmpty()) {
					iplist.add(ipseed);
				}
			}
			sql = "select startip,endip,comm from iprangecomm";
			ResultSet rs1 = ConfigDB.query(sql, conn);
			while (rs1.next()) {
				String comm = "";
				comm = rs1.getString("comm");
				String sip = rs1.getString("startip");
				String eip = rs1.getString("endip");
				if (comm != null && !comm.isEmpty() && sip != null && eip != null) {
					ipwdlist.add(comm + ":" + sip + "-" + eip);
				}
			}

		} catch (Exception ex) {

		} finally {
			if (conn != null)
				ConfigDB.close(conn);
		}
	}

	/**
	 * 获取snmpv1v2密码
	 * @param conn
	 * @throws SQLException
	 */
	private void snmpv1v2Pwd(Connection conn) throws SQLException {
		String sql = "select name,description,readcomm,writecomm,port,timeout,retries from snmpv1v2";
		ResultSet rs = ConfigDB.query(sql, conn);
		while (rs.next()) {
			HashMap<String, String> db1 = new HashMap<String, String>();
			String name = rs.getString("name");
			if (name == null)
				name = "";
			String description = rs.getString("description");
			if (description == null)
				description = "";
			String readcomm = rs.getString("readcomm");
			if (readcomm == null)
				readcomm = "public";
			String writecomm = rs.getString("writecomm");
			if (writecomm == null)
				writecomm = "public";
			String port = rs.getString("port");
			if (port == null)
				port = "161";
			String timeout = rs.getString("timeout");
			if (timeout == null)
				timeout = "1000";
			String retries = rs.getString("retries");
			if (retries == null)
				retries = "2";
			db1.put("name", name);
			db1.put("description", description);
			db1.put("readcomm", readcomm);
			db1.put("writecomm", writecomm);
			db1.put("port", port);
			db1.put("timeout", timeout);
			db1.put("retries", retries);
			userpass.get("SNMP V1/V2").add(db1);
		}
	}
	
	private void snmpv3Pwd(Connection conn) throws SQLException {
		String sql = "select name,description,authorization,username,password,privacy,privacypass,port,timeout,retries from snmpv3";
		ResultSet rs = ConfigDB.query(sql, conn);
		while (rs.next()) {
			HashMap<String, String> db1 = new HashMap<String, String>();
			String name = rs.getString("name");
			if (name == null)
				name = "";
			String description = rs.getString("description");
			if (description == null)
				description = "";
			String authorization = rs.getString("authorization");
			if (authorization == null)
				authorization = "null";
			String username = rs.getString("username");
			if (username == null)
				username = "";
			String password = rs.getString("password");
			if (password == null)
				password = "";
			String privacy = rs.getString("privacy");
			if (privacy == null)
				privacy = "";
			String privacypass = rs.getString("privacypass");
			if (privacypass == null)
				privacypass = "";
			String port = rs.getString("port");
			if (port == null)
				port = "161";
			String timeout = rs.getString("timeout");
			if (timeout == null)
				timeout = "1000";
			String retries = rs.getString("retries");
			if (retries == null)
				retries = "2";
			db1.put("name", name);
			db1.put("description", description);
			db1.put("authorization", authorization);
			db1.put("username", username);
			db1.put("password", password);
			db1.put("privacy", privacy);
			db1.put("privacypass", privacypass);
			db1.put("port", port);
			db1.put("timeout", timeout);
			db1.put("retries", retries);
			userpass.get("SNMP V3").add(db1);
		}
	}
	
	private void telnetSshPwd(Connection conn) throws SQLException {
		String sql = "select name,description,protocol,user,pass,port,timeout,retries from telnetssh";
		ResultSet rs1 = ConfigDB.query(sql, conn);
		while (rs1.next()) {
			HashMap<String, String> db1 = new HashMap<String, String>();
			String name = rs1.getString("name");
			if (name == null)
				name = "";
			String description = rs1.getString("description");
			if (description == null)
				description = "";
			String protocol = rs1.getString("protocol");
			if (protocol == null)
				protocol = "SSH";
			String user = rs1.getString("user");
			if (user == null)
				user = "root";
			String pass = rs1.getString("pass");
			if (pass == null)
				pass = "root";
			String port = rs1.getString("port");
			if (port == null)
				port = "22";
			String timeout = rs1.getString("timeout");
			if (timeout == null)
				timeout = "1000";
			String retries = rs1.getString("retries");
			if (retries == null)
				retries = "2";
			db1.put("name", name);
			db1.put("description", description);
			db1.put("protocol", protocol);
			db1.put("user", user);
			db1.put("pass", pass);
			db1.put("port", port);
			db1.put("timeout", timeout);
			db1.put("retries", retries);
			userpass.get("Telnet/SSH").add(db1);
		}
	}
	
	private void databasetypePwd(Connection conn) throws SQLException {
		String sql = "select name,description,dbtype,dbname,user,pass,port,timeout from databasetype";
		ResultSet rs2 = ConfigDB.query(sql, conn);
		while (rs2.next()) {
			HashMap<String, String> db1 = new HashMap<String, String>();
			String name = rs2.getString("name");
			if (name == null)
				name = "";
			String description = rs2.getString("description");
			if (description == null)
				description = "";
			String dbtype = rs2.getString("dbtype");
			if (dbtype == null)
				dbtype = "SqlServer";
			String dbname = rs2.getString("dbname");
			if (dbname == null)
				dbname = "master";
			String user = rs2.getString("user");
			if (user == null)
				user = "sa";
			String pass = rs2.getString("pass");
			if (pass == null)
				pass = "sa";
			String port = rs2.getString("port");
			if (port == null)
				port = "1433";
			String timeout = rs2.getString("timeout");
			if (timeout == null)
				timeout = "1000";

			db1.put("name", name);
			db1.put("description", description);
			db1.put("dbtype", dbtype);
			db1.put("dbname", dbname);
			db1.put("user", user);
			db1.put("pass", pass);
			db1.put("port", port);
			db1.put("timeout", timeout);
			userpass.get("DataBaseType").add(db1);
		}
	}
	
	public void showPassword(TreeItem treeItem) {
		userpass.put("SNMP V1/V2", new ArrayList<HashMap<String, String>>());
		userpass.put("SNMP V3", new ArrayList<HashMap<String, String>>());
		userpass.put("Telnet/SSH", new ArrayList<HashMap<String, String>>());
		userpass.put("DataBaseType", new ArrayList<HashMap<String, String>>());
		HashMap<String, String> db1 = null;
		Connection conn = null;
		try {
			conn = ConfigDB.getConn();
			snmpv1v2Pwd(conn);
			snmpv3Pwd(conn);
			telnetSshPwd(conn);
			databasetypePwd(conn);
		} catch (Exception ex) {
		} finally {
			if (conn != null)
				ConfigDB.close(conn);
		}

		if (userpass.get("SNMP V1/V2").size() == 0) {
			db1 = new HashMap<String, String>();
			db1.put("name", "public");
			db1.put("description", "");
			db1.put("readcomm", "public");
			db1.put("writecomm", "public");
			db1.put("port", "161");
			db1.put("timeout", "1000");
			db1.put("retries", "0");
			userpass.get("SNMP V1/V2").add(db1);
		}

		Iterator<String> ite = userpass.keySet().iterator();
		while (ite.hasNext()) {
			String key = ite.next();
			for (HashMap<String, String> bo : userpass.get(key)) {
				String name = "";
				String databaseType = "";
				String authenticationProtocol = "";
				String userName = "";
				name = bo.get("name");
				if (bo.containsKey("dbtype"))
					databaseType = bo.get("dbtype");
				if (bo.containsKey("protocol"))
					authenticationProtocol = bo.get("protocol");
				if (bo.containsKey("user"))
					userName = bo.get("user");
				TreeItem treeItem_1 = new TreeItem(treeItem, SWT.NONE);
				if ("DataBaseType".equals(key)) {
					treeItem_1.setText(name + "(" + databaseType + ")");
				} else if ("Telnet/SSH".equals(key)) {
					treeItem_1.setText(name + "(" + authenticationProtocol + ")");
				} else {
					treeItem_1.setText(name + "(" + key + ")");
				}
				treeItem_1.setData(bo);
				treeItem_1.setChecked(true);
			}
			treeItem.setExpanded(true);
		}
	}

	private void createAction() {
		snmpv1v2 = new Action("SNMP V1/V2") {
			public void run() {
				SNMPV1V2Password add = new SNMPV1V2Password(Display.getDefault().getActiveShell());
				if (add.open() == IDialogConstants.OK_ID) {
					Map<String, String> bo = add.getDB();
					if (bo != null) {
						userpass.get("SNMP V1/V2").add((HashMap<String, String>) bo);
						TreeItem treeitem = new TreeItem(treeItem, SWT.NONE);
						treeitem.setText(bo.get("name") + "(SNMP V1/V2)");
						treeitem.setData(bo);
						treeitem.setChecked(true);
					}
				}
			}
		};
		URL url = BundleUtility.find(Platform.getBundle(Activator.PLUGIN_ID), ImageResource.Add);
		ImageDescriptor temp = ImageDescriptor.createFromURL(url);
		snmpv1v2.setImageDescriptor(temp);
		snmpv3 = new Action("SNMP V3") {
			public void run() {
				SNMPV3Dialog add = new SNMPV3Dialog(Display.getDefault().getActiveShell());
				if (add.open() == IDialogConstants.OK_ID) {
					Map<String, String> bo = add.getDB();
					if (bo != null) {
						userpass.get("SNMP V3").add((HashMap<String, String>) bo);
						TreeItem treeitem = new TreeItem(treeItem, SWT.NONE);
						treeitem.setText(bo.get("name") + "(SNMP V3)");
						treeitem.setData(bo);
						treeitem.setChecked(true);
					}
				}
			}
		};
		URL url6 = BundleUtility.find(Platform.getBundle(Activator.PLUGIN_ID), ImageResource.Add);
		ImageDescriptor temp6 = ImageDescriptor.createFromURL(url6);
		snmpv3.setImageDescriptor(temp6);

		telnet_ssh = new Action("Telnet/SSH") {
			public void run() {
				TelnetSSHPassword add = new TelnetSSHPassword(Display.getDefault().getActiveShell());
				if (add.open() == IDialogConstants.OK_ID) {
					Map<String, String> bo = add.getDB();
					if (bo != null) {
						userpass.get("Telnet/SSH").add((HashMap<String, String>) bo);
						String authenticationProtocol = bo.get("protocol");
						TreeItem treeitem = new TreeItem(treeItem, SWT.NONE);
						treeitem.setText(bo.get("name") + "(" + authenticationProtocol + ")");
						treeitem.setData(bo);
						treeitem.setChecked(true);
					}
				}
			}
		};
		URL url3 = BundleUtility.find(Platform.getBundle(Activator.PLUGIN_ID), ImageResource.Add);
		ImageDescriptor temp3 = ImageDescriptor.createFromURL(url3);
		telnet_ssh.setImageDescriptor(temp3);

		database = new Action("DataBaseType") {
			public void run() {
				DataPassword add = new DataPassword(Display.getDefault().getActiveShell());
				if (add.open() == IDialogConstants.OK_ID) {
					Map<String, String> bo = add.getDB();
					if (bo != null) {
						userpass.get("DataBaseType").add((HashMap<String, String>) bo);
						String databaseType = bo.get("dbtype");
						TreeItem treeitem = new TreeItem(treeItem, SWT.NONE);
						treeitem.setText(bo.get("name") + "(" + databaseType + ")");
						treeitem.setData(bo);
						treeitem.setChecked(true);
					}
				}
			}
		};

		URL url4 = BundleUtility.find(Platform.getBundle(Activator.PLUGIN_ID), ImageResource.Add);
		ImageDescriptor temp4 = ImageDescriptor.createFromURL(url4);
		database.setImageDescriptor(temp4);
	}

	public void checkorUncheckChild(TreeItem treeitem) {
		for (TreeItem item : treeitem.getItems()) {
			item.setChecked(treeitem.getChecked());
			checkorUncheckChild(item);
		}
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "确定", true);
		createButton(parent, IDialogConstants.CANCEL_ID, "取消", false);
	}

	private CountDownLatch scanCound;

	protected void buttonPressed(int buttonId) {
		try {
			if (buttonId == IDialogConstants.OK_ID) {
				List<Snmpv3config> SNMPV3s = new ArrayList<Snmpv3config>();
				List<Map<String, String>> SSHAuthents = new ArrayList<Map<String, String>>();
				List<Pair<String, String>> scan_scales = new ArrayList<Pair<String, String>>(); //扫描IP范围
				List<DBAuthent> DBAuthents = new ArrayList<DBAuthent>();
				TreeItem[] ipRangItem = iprangeTree.getItems();
				if (ipRangItem[0] != null
						&& ipRangItem[0].getText().equals("全选")) {
					ipRangItem = ipRangItem[0].getItems();
				}
				for (TreeItem treeItem : ipRangItem) {
					if (treeItem.getChecked()) {
						String ss = treeItem.getText().toString();
						String startIP = ss.substring(0, ss.indexOf("-")).trim();
						String endIp = ss.substring(ss.indexOf("-") + 1, ss.length()).trim();
						scan_scales.add(new Pair<String, String>(startIP, endIp));
					}
				}
				if (scan_scales.size() < 1) {
					// MsgBox.ShowError(EccMessage.get().ERROR,
					// AutoScanMessage.get().CHOOSE_SCAN_RANGE);
					// return;
				}
				TreeItem[] snmpItems = tree.getItems(); //全局密码树
				if (snmpItems.length > 0 && snmpItems[0] != null && snmpItems[0].getText().toString().equals("全选")) {
					snmpItems = snmpItems[0].getItems();
				}
				for (TreeItem snmpItem : snmpItems) {
					HashMap<String, String> bo = null;
					// for (TreeItem childItem : childsnmpItems) {
					if (snmpItem.getChecked()) {
						bo = (HashMap<String, String>) snmpItem.getData();
						if (bo == null)
							continue;
						try {
							if (bo.containsKey("readcomm")) { // snmpv1v2密码
								String comm = bo.get("readcomm");
								if (!TopoScan.communityqueue.contains(comm))
									TopoScan.communityqueue.add(comm);
							} else if (bo.containsKey("dbtype")) { //databasetype
								String dataBaseName = bo.get("dbname");
								String DataType = bo.get("dbtype");
								String username = bo.get("user");
								String pwd = bo.get("pass");
								String port = bo.get("port");
								DBAuthent dbau = new DBAuthent(DataType, dataBaseName, username, pwd);
								dbau.setPort(Long.parseLong(port));
								DBAuthents.add(dbau);
							} else if (bo.containsKey("protocol")) { //snmpv3
								String username = bo.get("user");
								String pwd = bo.get("pass");
								String port = bo.get("port");
								if (username != null && pwd != null) {
									Map<String, String> SSHmap = new HashMap<String, String>();
									SSHmap.put("pwd", pwd);
									SSHmap.put("user", username);
									SSHmap.put("port", port);
									SSHAuthents.add(SSHmap);
								}
							} else if (bo.containsKey("authorization")) {
								String authorization = bo.get("authorization");
								String username = bo.get("username");
								String password = bo.get("password");
								String privacy = bo.get("privacy");
								String privacypass = bo.get("privacypass");
								Snmpv3config snmpp = new Snmpv3config(authorization, username, password, privacy,
										privacypass);
								SNMPV3s.add(snmpp);
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}
				if (TopoScan.communityqueue.size() == 0 && SNMPV3s.size() == 0) {
					MsgBox.ShowError("提示", "请输入网络设备的全局密码!");
					return;
				}

				ScanWaitdialog waitdialog = new ScanWaitdialog(Display.getCurrent().getActiveShell());
				waitdialog.setCancelable(true);
				scanCound = new CountDownLatch(1);
				List<String> scan_seeds = new ArrayList<String>();
				for (String comm : iplist.getItems()) {
					if (!scan_seeds.contains(comm))
						scan_seeds.add(comm);
				}
				List<String> ipspwd = new ArrayList<String>();
				for (String ss : ipwdlist.getItems()) {
					if (!ipspwd.contains(ss))
						ipspwd.add(ss);
				}
				// ipwdlist
				String ArithmeticType = "0";
				if (btncdp.getSelection()) {
					ArithmeticType = "1";
				}
				if (ArithmeticType.equals("1")) {
					if (scan_seeds.isEmpty()) {
						MsgBox.ShowError("提示", "请输入cisco扫描种子IP");
						return;
					}
				}
				if(btnlog.getSelection()){
					ArithmeticType = "2";
				}
				if (scan_scales.isEmpty() && scan_seeds.isEmpty() && !btnlog.getSelection()) {
					MsgBox.ShowError("提示", "请输入ip范围或扫描种子");
					return;
				}
				String nname="";
				if(btnsubtopo.getSelection()){
					 nname= subtopoText.getText().trim();
					if(nname.isEmpty()){
						MsgBox.ShowError("提示", "子图名称不能为空！");
						return;
					}
					if(DBManage.Topos.containsKey(nname)){
						MsgBox.ShowError("提示", "子图名称已经存在！");
						return;
					}
				}
				//String subtoponame=subtopoText.getText();
				ScanWorker worker = new ScanWorker(waitdialog, scanCound,scan_scales, scan_seeds, ipspwd, ArithmeticType,m_api,nname);
				worker.setDBAuthents(DBAuthents);
				worker.setSSHAuthents(SSHAuthents);
				worker.setSNMPV3s(SNMPV3s);
				// ScanWait swait=new
				// ScanWait(null,scan_scales,Communitytext.getText());
				this.close();
				try {
					waitdialog.run(true, false, worker);
					if (nname.isEmpty()) {
						TreeItem[] items = NNMTreeView.getCNFNNMTreeView().getCommonViewer().getTree().getItems();
						NNMTreeView.getCNFNNMTreeView().getCommonViewer().getTree()
						.setSelection(items[0].getItems()[0]);
						
						for (SubChartModle sub1 : ((TopoModle) items[0].getItems()[0].getData()).getList()) {
							if (!DBManage.subhosts.contains(sub1.getName()))
								((TopoModle) items[0].getItems()[0].getData()).getList().remove(sub1);
						}
					} else {
						SubChartModle subc1 = new SubChartModle();
						subc1.setName(nname);
						((TopoModle) NNMTreeView.getCNFNNMTreeView().getCommonViewer().getTree().getItem(0).getItem(0)
								.getData()).getList().add(subc1);
					}
					NNMTreeView.getCNFNNMTreeView().getCommonViewer().refresh();
				} catch (Exception e) {
				}
				RefreshUI();
				//if(nname.isEmpty() && DBManage.subhosts.size()==0)
				RefreshNNMTree.refresh(nname.isEmpty());
				// swait.open();
			} else {
				this.close();
			}

		} catch (Exception e) {
		}
		super.buttonPressed(buttonId);
	}

	private void RefreshUI() {
		NNMainEditorInput editiput = (NNMainEditorInput) NNMInstanceData
				.getNNMData("editiput");
		IWorkbenchPage page = Activator.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		IEditorPart editor = page.findEditor(editiput);
		page.closeEditor(editor, false);
		editiput.setSubtopo("host");
		try {
			IEditorPart ep = page.openEditor(editiput, TopoManage.ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		
	}

	public SubChartModle setselecttion(Object data, String name) {
		SubChartModle mo = null;
		if (data instanceof TopoModle) {
			for (SubChartModle subchart : ((TopoModle) data).getList()) {
				if (subchart.getName().equals(name)) {
					return subchart;
				}

			}

		}

		return mo;

	}

	public void expandtreeitem(TreeItem treeitem) {

		if (treeitem != null && !(treeitem.getData() instanceof RootNNM)) {
			treeitem.setExpanded(true);
			expandtreeitem(treeitem.getParentItem());
		}
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(470, 650);
	}

}
