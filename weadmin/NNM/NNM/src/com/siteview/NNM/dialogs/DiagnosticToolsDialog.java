package com.siteview.NNM.dialogs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.SocketException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;

import org.apache.commons.net.telnet.TelnetClient;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.snmp4j.mp.SnmpConstants;

import com.siteview.NNM.aas.NNMPermissions;
import com.siteview.NNM.dialogs.table.ImageKeys;
import com.siteview.nnm.data.EntityManager;
import com.siteview.nnm.data.model.TopoChart;
import com.siteview.nnm.data.model.entity;
import com.siteview.nnm.data.model.svNode;
import com.siteview.utils.db.DBQueryUtils;
import com.siteview.utils.ping.PingUtil;
import com.siteview.utils.snmp.SnmpGet;

import Siteview.SiteviewException;
import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ConnectionBroker;
import Siteview.Windows.Forms.MsgBox;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class DiagnosticToolsDialog extends Dialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ISiteviewApi api = ConnectionBroker.get_SiteviewApi();
	Text ip_text;
	Combo info_combo;
	String ip = "";// ip
	String community = "public";// 共同体
	int nver = 1;// 版本 version
	static int port = 161;
	Timer timer = null;
	Combo ref_com;
	Group group_result;
	Composite startComposite;
	Text result_text;

	// ping
	Text text_timeout;
	Text text_j;
	Text text_size;
	Group group;

	// snmp
	Text community_text;
	Text port_text;
	Combo combo_version;
	Composite versionComposite;
	Composite configComposite;
	Text username_text;
	Text passwd_text;
	Text privacyPwd_text;
	Combo combo_authenticationProtocol;
	Combo combo_privacyProtocol;
	SetMibData smd = null;
	ServerPushSession sps = new ServerPushSession();
	int k = 0;

	// tracert
	Text hop_text;
	Text text_command;
	Button button_command;
	Button save_connConfig;
	Button reset_connConfig;
	// telnet
	boolean execute = false;
	Text superusername_text;
	Text superpasswd_text;
	TelnetClient telnetclient = null;
	BufferedReader br = null;
	InputStream in;
	PrintStream out;
	String strlen = "";
	// ssh
	Connection sshconn;
	Session session;
	SSHCommand sshcommand = null;
	static String[] validationArr_wildcard = { "#", ">", "]" };
	static String[] validationArr_pwd = { "Password:" };
	static String[] validationArr_user = { "login:", "Username:" };

	/**
	 * Create the dialog. typetable
	 * 
	 * 
	 * 设备接口表 deviceiftable, 设备路由表 deviceroutetable, 1.3.6.1.2.1.4.21.1.1
	 * 路由目的IP地址 1.3.6.1.2.1.4.21.1.7 路由下一跳地址 1.3.6.1.2.1.4.21.1.8 路由类型
	 * 1.3.6.1.2.1.4.21.1.9 路由协议 1.3.6.1.2.1.4.21.1.11 路由掩码 设备转发表
	 * devicemactable, 设备arp表devicearptable, 设备cdp表 devicecdptable, 设备ip表
	 * deviceiptable,
	 * 
	 * @param parentShell
	 */
	public DiagnosticToolsDialog(Shell parentShell, String tag, String nodeid, int k) {
		super(parentShell);
		TopoChart tochart = com.siteview.nnm.data.DBManage.Topos.get(tag);
		svNode entityobj = (svNode) tochart.getNodes().get(nodeid);
		community = entityobj.getProperys().get("Community");
		String dver = entityobj.getProperys().get("Version");
		port = (Integer.parseInt(entityobj.getProperys().get("port").equals("") ? "161" : entityobj.getProperys().get("port")));
		if (dver.equals("1")) {
			nver = 1;
		} else if (dver.equals("2")) {
			nver = 2;
		}
		this.ip = entityobj.getLocalip();
		this.k = k;
	}

	/**
	 * Create the dialog. typetable
	 * 
	 * 
	 * 设备接口表 deviceiftable, 设备路由表 deviceroutetable, 1.3.6.1.2.1.4.21.1.1
	 * 路由目的IP地址 1.3.6.1.2.1.4.21.1.7 路由下一跳地址 1.3.6.1.2.1.4.21.1.8 路由类型
	 * 1.3.6.1.2.1.4.21.1.9 路由协议 1.3.6.1.2.1.4.21.1.11 路由掩码 设备转发表
	 * devicemactable, 设备arp表devicearptable, 设备cdp表 devicecdptable, 设备ip表
	 * deviceiptable,
	 * 
	 * @param parentShell
	 */
	public DiagnosticToolsDialog(Shell parentShell, svNode entityobj, int k) {
		super(parentShell);
		community = entityobj.getProperys().get("Community");
		String dver = entityobj.getProperys().get("Version");
		port = (Integer.parseInt(entityobj.getProperys().get("port").equals("") ? "161" : entityobj.getProperys().get("port")));
		if (dver.equals("1")) {
			nver = 1;
		} else if (dver.equals("2")) {
			nver = 2;
		}
		this.ip = entityobj.getLocalip();
		this.k = k;
	}
	public DiagnosticToolsDialog(Shell parentShell) {
		super(parentShell);
	}

	protected void configureShell(Shell newShell) {
		newShell.setText("诊断工具");
		super.configureShell(newShell);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@SuppressWarnings("serial")
	@Override
	protected Control createDialogArea(Composite parent) {
		sps.start();
		ImageKeys.loadimage();
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout());

		group = new Group(container, SWT.NONE);
		GridData gd_group = new GridData();
		// GridData gd_group = new GridData(SWT.LEFT, SWT.CENTER, false, false,
		// 1, 1);
		gd_group.widthHint = 763;
		gd_group.heightHint = 180;
		group.setLayoutData(gd_group);
		GridLayout gridLayout = new GridLayout(7, false);
		gridLayout.horizontalSpacing = 10;
		group.setLayout(gridLayout);

		GridData gd_label = new GridData();
		gd_label.widthHint = 60;

		GridData gd_text = new GridData();
		gd_text.widthHint = 170;
		GridData gd_com = new GridData();
		gd_com.widthHint = 100;

		GridData gd_button = new GridData();
		gd_button.widthHint = 100;

		GridData gd_button_1 = new GridData();
		gd_button_1.widthHint = 85;

		Label label = new Label(group, SWT.NONE | SWT.RIGHT);
		label.setText("IP/域名:");
		label.setLayoutData(gd_label);

		ip_text = new Text(group, SWT.BORDER);
		ip_text.setLayoutData(gd_text);
		ip_text.setText(ip);

		Button button = new Button(group, SWT.NONE);
		button.setText("更多...");
		button.setLayoutData(gd_button);
		button.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event event) {
				Map<String, entity> allEntity = EntityManager.allEntity;
				MoreIPDialog more = new MoreIPDialog(null, allEntity);
				more.open();
				if (more.getServerAddress() != null && !more.getServerAddress().equals(""))
					ip_text.setText(more.getServerAddress());
			}
		});

		Label info_label = new Label(group, SWT.NONE | SWT.RIGHT);
		info_label.setText("诊断工具");
		info_label.setLayoutData(gd_label);

		info_combo = new Combo(group, SWT.BORDER | SWT.READ_ONLY);
		if (NNMPermissions.getInstance().getNNMToolsPing())
			info_combo.add("Ping");
		if (NNMPermissions.getInstance().getNNMToolsSsh())
			info_combo.add("SSH");
		if (NNMPermissions.getInstance().getNNMToolsTelnet())
			info_combo.add("Telnet");
		if (NNMPermissions.getInstance().getNNMToolsTraceroute())
			info_combo.add("TraceRoute");
		if (NNMPermissions.getInstance().getNNMToolsSnmp())
			info_combo.add("Snmp测试");
		info_combo.setLayoutData(gd_com);
		info_combo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				createTableColumnsOfTypes();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		info_combo.select(k);

		final Button select_button = new Button(group, SWT.NONE);
		select_button.setText("诊断");
		select_button.setLayoutData(gd_button_1);
		select_button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// createTableTypesSetData();
				// composite2.layout();
				select_button.setText("诊断中...");
				result_text.setText("");
				smd = new SetMibData();
				String ip = ip_text.getText().trim();
				String temp = info_combo.getText();
				smd.passData(temp, Display.getCurrent(), result_text, select_button);
				StringBuffer sb = new StringBuffer();
				if (info_combo.getText().equals("Ping")) {
					int ping_timeout = Integer.parseInt(text_timeout.getText().trim());
					int ping_j = Integer.parseInt(text_j.getText().trim());
					int ping_size = Integer.parseInt(text_size.getText().trim());
					String command = PingUtil.pingCommand(ip, ping_timeout, ping_j, ping_size);
					smd.passPingData(command);
				} else if (info_combo.getText().equals("Snmp测试")) {
					Map<String, String> snmpData = new HashMap<String, String>();
					String serverAddress = ip_text.getText().toString().trim();
					String edition = nver + "";
					String community = community_text.getText().toString().trim();
					String timeout = text_timeout.getText().trim();
					int port = Integer.parseInt(port_text.getText().toString().trim().equals("") ? "161" : port_text.getText().toString());

					snmpData.put("edition", edition);
					snmpData.put("community", community);
					snmpData.put("serverAddress", serverAddress);
					snmpData.put("port", port + "");
					snmpData.put("timeout", timeout);
					if (edition.equals("V3")) {
						snmpData.put("username", username_text.getText().trim());
						snmpData.put("passwd", passwd_text.getText().trim());
						snmpData.put("authenticationProtocol", combo_authenticationProtocol.getText().trim());
						snmpData.put("privacyProtocol", combo_privacyProtocol.getText().trim());
						snmpData.put("privacyPwd", privacyPwd_text.getText().trim());
					}
					smd.passSnmpData(snmpData);
				} else if (info_combo.getText().equals("TraceRoute")) {
					String s = System.getProperty("os.name").toUpperCase();
					if (s.startsWith("WINDOWS")) {
						sb.append("Tracert ");
						String hop = hop_text.getText().toString().trim();
						if (!"".equals(hop)) {
							sb.append(" -h ");
							sb.append(hop);
						}
						String ping_timeout = text_timeout.getText().trim();
						if (!"".equals(ping_timeout)) {
							sb.append(" -w ");
							sb.append(ping_timeout);
						}
					} else {
						sb.append("traceroute ");
						String hop = hop_text.getText().toString().trim();
						if (!"".equals(hop)) {
							sb.append(" -m ");
							sb.append(hop);
						}
						String ping_timeout = text_timeout.getText().trim();
						if (!"".equals(ping_timeout)) {
							sb.append(" -w ");
							sb.append(Integer.parseInt(ping_timeout) / 1000);
						}
					}

					sb.append(" " + ip);
					smd.passPingData(sb.toString());
				} else if (info_combo.getText().equals("Telnet")) {
					int timeout = Integer.parseInt(text_timeout.getText());
					int port = Integer.parseInt(port_text.getText().toString().trim().equals("") ? "23" : port_text.getText().toString());
					String user = username_text.getText().trim();
					String password = passwd_text.getText().trim();
					String superUser = superusername_text.getText().trim();
					String superpwd = superpasswd_text.getText().trim();
					smd.passTelnetData(ip, timeout, port, user, password, superUser, superpwd);
				} else if (info_combo.getText().equals("SSH")) {
					int timeout = Integer.parseInt(text_timeout.getText());
					int port = Integer.parseInt(port_text.getText().toString().trim().equals("") ? "22" : port_text.getText().toString());
					String user = username_text.getText().trim();
					String password = passwd_text.getText().trim();
					smd.passSSHData(ip, timeout, port, user, password);
				}
				smd.start();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		Button stop_button = new Button(group, SWT.NONE);
		stop_button.setText("停止诊断");
		stop_button.setLayoutData(gd_button_1);
		stop_button.addSelectionListener(new SelectionListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void widgetSelected(SelectionEvent e) {
				select_button.setEnabled(true);
				select_button.setText("诊断");
				if (telnetclient != null && telnetclient.isConnected()) {
					try {
						telnetclient.disconnect();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				if (sshconn != null) {
					sshconn.close();
				}
				if (smd != null) {
					smd.stop();
					// sps.stop();

				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		startComposite = new Composite(group, SWT.NONE);
		startComposite.setLayout(new GridLayout(8, false));
		GridData gd_composite = new GridData(GridData.FILL_BOTH);
		gd_composite.horizontalSpan = 8;
		gd_composite.verticalSpan = 3;
		gd_composite.heightHint = 85;
		startComposite.setLayoutData(gd_composite);

		group_result = new Group(container, SWT.NONE);
		group_result.setText("测试结果");
		group_result.setLayout(new GridLayout());
		GridData gd_group1 = new GridData(GridData.FILL_BOTH);
		gd_group1.widthHint = 763;
		group_result.setLayoutData(gd_group1);

		result_text = new Text(group_result, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.READ_ONLY);
		result_text.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
		result_text.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		// result_text.setTextLimit(SWT.MAX);
		result_text.setLayoutData(new GridData(GridData.FILL_BOTH));
		// result_text.addKeyListener(new KeyAdapter() {
		// @Override
		// public void keyPressed(KeyEvent e) {
		// System.out.println(e.keyCode);
		// if (e.keyCode == SWT.CR) {
		// e.doit = false;
		// String command = result_text.getText();
		// if (command.contains(strlen)) {
		// command = command.substring(command.lastIndexOf(strlen) +
		// strlen.length(), command.length());
		// }
		// TelnetCommand telnetcommand = new TelnetCommand();
		// telnetcommand.setTelnetCommand(Display.getCurrent(), command,
		// button_command, true);
		// telnetcommand.start();
		// } else if (e.keyCode == SWT.TAB) {
		// e.doit = false;
		// }
		// super.keyPressed(e);
		// }
		// });
		// result_text.addFocusListener(new FocusListener() {
		//
		// @Override
		// public void focusLost(FocusEvent event) {
		// result_text.setSelection(2, 2);
		// result_text.setFocus();
		// }
		//
		// @Override
		// public void focusGained(FocusEvent event) {
		//
		// }
		// });
		// result_text.setEnabled(false);

		createTableColumnsOfTypes();
		configComposite = new Composite(group, SWT.NONE);
		configComposite.setLayout(new GridLayout(8, false));
		GridData config_gd_composite = new GridData(GridData.FILL_BOTH);
		config_gd_composite.horizontalSpan = 8;
		// config_gd_composite.verticalSpan=4;
		// version_gd_composite.heightHint=50;
		// version_gd_composite.horizontalIndent = -5;
		configComposite.setLayoutData(config_gd_composite);

		Label conn_label = new Label(configComposite, SWT.NONE);
		GridData conn_label_composite = new GridData();
		conn_label_composite.horizontalSpan = 7;
		conn_label_composite.widthHint = 645;
		conn_label.setLayoutData(conn_label_composite);

		save_connConfig = new Button(configComposite, SWT.NONE);
		save_connConfig.setText("保存配置");
		save_connConfig.setLayoutData(gd_button_1);
		save_connConfig.addListener(SWT.MouseDown, new Listener() {

			@Override
			public void handleEvent(Event event) {
				String connType = info_combo.getText();
				ip = ip_text.getText().trim();
				if ("".equals(ip)) {
					MsgBox.ShowWarning("保存结果", "IP不能为空");
				}
				if (connType.equals("SSH")) {
					saveSShConnConfig(connType);
				} else if (connType.equals("Ping")) {

				} else if (connType.equals("Telnet")) {
					saveTelnetConnConfig(connType);
				} else if (connType.equals("TraceRoute")) {
				} else if (connType.equals("Snmp测试")) {
					saveSNMPConnConfig();
				}
			}

		});
		// reset_connConfig = new Button(configComposite, SWT.NONE);
		// reset_connConfig.setText("重置配置");
		// reset_connConfig.setLayoutData(gd_button);
		// reset_connConfig.addListener(SWT.MouseDown, new Listener() {
		//
		// @Override
		// public void handleEvent(Event event) {
		// saveSNMPConnConfig();
		// }
		// });

		return container;
	}

	@SuppressWarnings("serial")
	private void createTableColumnsOfTypes() {
		final GridData gd_text = new GridData();
		gd_text.widthHint = 100;
		final GridData gd_text_1 = new GridData();
		gd_text_1.widthHint = 80;

		final GridData gd_label = new GridData();
		gd_label.widthHint = 60;

		final GridData gd_label_1 = new GridData();
		gd_label_1.widthHint = 100;
		GridData gd_button = new GridData();
		gd_button.widthHint = 80;
		if (versionComposite != null && !versionComposite.isDisposed()) {
			for (Control control_version : versionComposite.getChildren()) {
				control_version.dispose();
			}
		}
		if (startComposite != null) {
			for (Control control : startComposite.getChildren()) {
				control.dispose();
			}
		}
		ip = ip_text.getText().toString().trim();
		if (info_combo.getText().equals("Ping")) {

			Label label_j = new Label(startComposite, SWT.NONE | SWT.RIGHT);
			label_j.setText("执行次数:");
			label_j.setLayoutData(gd_label);
			text_j = new Text(startComposite, SWT.BORDER);
			text_j.setLayoutData(gd_text);
			text_j.setText("4");

			Label label = new Label(startComposite, SWT.NONE | SWT.RIGHT);
			label.setText("字节大小(byte):");
			label.setLayoutData(gd_label_1);
			text_size = new Text(startComposite, SWT.BORDER);
			text_size.setLayoutData(gd_text);
			text_size.setText("32");

			Label label_timeout = new Label(startComposite, SWT.NONE | SWT.RIGHT);
			label_timeout.setText("超时时间(毫秒):");
			label_timeout.setLayoutData(gd_label_1);
			text_timeout = new Text(startComposite, SWT.BORDER);
			text_timeout.setLayoutData(gd_text);
			text_timeout.setText("300");
		} else if (info_combo.getText().equals("SSH")) {
			// String ip = ip_text.getText().trim();
			Label label_username = new Label(startComposite, SWT.NONE | SWT.RIGHT);
			label_username.setText("用 户:");
			label_username.setLayoutData(gd_label);
			username_text = new Text(startComposite, SWT.BORDER);
			username_text.setLayoutData(gd_text);
			// username_text.setText("root");

			Label label_passwd = new Label(startComposite, SWT.NONE | SWT.RIGHT);
			label_passwd.setText("密    码:");
			label_passwd.setLayoutData(gd_label);
			passwd_text = new Text(startComposite, SWT.BORDER | SWT.PASSWORD);
			passwd_text.setLayoutData(gd_text);
			// passwd_text.setText("qwe123!@#");

			Label label_port = new Label(startComposite, SWT.NONE | SWT.RIGHT);
			label_port.setText("端    口:");
			label_port.setLayoutData(gd_label);
			port_text = new Text(startComposite, SWT.BORDER);
			port_text.setLayoutData(gd_text_1);
			port_text.setText("22");

			Label label_timeout = new Label(startComposite, SWT.NONE | SWT.RIGHT);
			label_timeout.setText("超时时间(毫秒):");
			label_timeout.setLayoutData(gd_label_1);
			text_timeout = new Text(startComposite, SWT.BORDER);
			text_timeout.setLayoutData(gd_text_1);
			text_timeout.setText("3000");

			Label label_command = new Label(startComposite, SWT.NONE | SWT.RIGHT);
			label_command.setText("具体命令:");
			label_command.setLayoutData(gd_label);

			GridData gd_text1 = new GridData();
			gd_text1.widthHint = 280;
			gd_text1.horizontalSpan = 3;
			text_command = new Text(startComposite, SWT.BORDER);
			text_command.setLayoutData(gd_text1);
			text_command.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.keyCode == SWT.CR) {
						e.doit = false;
						String command = text_command.getText();
						try {
							if (sshconn != null) {
								session = sshconn.openSession();
								session.requestPTY("Linux", 800, 240, 640, 480, null);
								sshcommand = new SSHCommand();
								sshcommand.setTelnetCommand(session, Display.getCurrent(), command, button_command);
								sshcommand.start();
							}
						} catch (IOException e1) {
							e1.printStackTrace();
						} finally {

						}
					} else if (e.keyCode == SWT.TAB) {
						e.doit = false;
					}
					super.keyPressed(e);
				}
			});

			button_command = new Button(startComposite, SWT.NONE);
			button_command.setText("执行");
			button_command.setLayoutData(gd_button);
			button_command.addListener(SWT.MouseUp, new Listener() {

				@Override
				public void handleEvent(Event event) {
					try {
						if (sshconn != null) {
							session = sshconn.openSession();
							session.requestPTY("Linux", 800, 240, 640, 480, null);
							String command = text_command.getText().toString();
							sshcommand = new SSHCommand();
							sshcommand.setTelnetCommand(session, Display.getCurrent(), command, button_command);
							sshcommand.start();
						}
					} catch (IOException e) {
						e.printStackTrace();
					} finally {

					}

				}
			});

			Button button_clean = new Button(startComposite, SWT.NONE);
			button_clean.setText("清空");
			button_clean.setLayoutData(gd_button);
			button_clean.addListener(SWT.MouseUp, new Listener() {

				@Override
				public void handleEvent(Event event) {
					if (sshcommand != null)
						sshcommand.stop();
					if (!"执行".equals(button_command.getText())) {
						button_command.setText("执行");
						button_command.setEnabled(true);
					}
					result_text.setText(strlen);
				}
			});

			Map<String, String> map = getSSHUserNameAndPassword(ip);
			if (map != null && map.size() > 1) {
				port_text.setText(map.get("port"));
				username_text.setText(map.get("username"));
				passwd_text.setText(map.get("passwd"));
			}
		} else if (info_combo.getText().equals("Telnet")) {
			Label label_username = new Label(startComposite, SWT.NONE | SWT.RIGHT);
			label_username.setText("用 户:");
			label_username.setLayoutData(gd_label);
			username_text = new Text(startComposite, SWT.BORDER);
			username_text.setLayoutData(gd_text);

			Label label_passwd = new Label(startComposite, SWT.NONE | SWT.RIGHT);
			label_passwd.setText("密    码:");
			label_passwd.setLayoutData(gd_label);
			passwd_text = new Text(startComposite, SWT.BORDER | SWT.PASSWORD);
			passwd_text.setLayoutData(gd_text);
			// passwd_text.setText("qwe123!@#");

			Label label_port = new Label(startComposite, SWT.NONE | SWT.RIGHT);
			label_port.setText("端     口:");
			label_port.setLayoutData(gd_label);
			port_text = new Text(startComposite, SWT.BORDER);
			port_text.setLayoutData(gd_text_1);
			port_text.setText("23");

			Label label_timeout = new Label(startComposite, SWT.NONE | SWT.RIGHT);
			label_timeout.setText("超时时间(毫秒):");
			label_timeout.setLayoutData(gd_label_1);
			text_timeout = new Text(startComposite, SWT.BORDER);
			text_timeout.setLayoutData(gd_text_1);
			text_timeout.setText("5000");

			Label label_superusername = new Label(startComposite, SWT.NONE | SWT.RIGHT);
			label_superusername.setText("超级用户:");
			label_superusername.setLayoutData(gd_label);
			superusername_text = new Text(startComposite, SWT.BORDER);
			superusername_text.setLayoutData(gd_text);
			// superusername_text.setText("en");

			Label label_superpasswd = new Label(startComposite, SWT.NONE | SWT.RIGHT);
			label_superpasswd.setText("超级密码:");
			label_superpasswd.setLayoutData(gd_label);
			superpasswd_text = new Text(startComposite, SWT.BORDER | SWT.PASSWORD);
			superpasswd_text.setLayoutData(gd_text);
			// superpasswd_text.setText("siteview");
			new Label(startComposite, SWT.NONE | SWT.RIGHT);
			new Label(startComposite, SWT.NONE | SWT.RIGHT);
			new Label(startComposite, SWT.NONE | SWT.RIGHT);
			new Label(startComposite, SWT.NONE | SWT.RIGHT);

			Label label_command = new Label(startComposite, SWT.NONE | SWT.RIGHT);
			label_command.setText("具体命令:");
			label_command.setLayoutData(gd_label);

			GridData gd_text1 = new GridData();
			gd_text1.widthHint = 280;
			gd_text1.horizontalSpan = 3;
			text_command = new Text(startComposite, SWT.BORDER);
			text_command.setLayoutData(gd_text1);
			text_command.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.keyCode == SWT.CR) {
						e.doit = false;
						ip = ip_text.getText().trim();
						int timeout = Integer.parseInt(text_timeout.getText());
						int port = Integer.parseInt(port_text.getText().toString().trim().equals("") ? "23" : port_text.getText().toString());
						String user = username_text.getText().trim();
						String password = passwd_text.getText().trim();
						String superUser = superusername_text.getText().trim();
						String superpwd = superpasswd_text.getText().trim();
						String command = text_command.getText().toString();
						TelnetCommand telnetcommand = new TelnetCommand();
						telnetcommand.setTelnetCommand(Display.getCurrent(), command, button_command, false);
						telnetcommand.setTelnetData(ip, timeout, port, user, password, superUser, superpwd);
						telnetcommand.start();
					} else if (e.keyCode == SWT.TAB) {
						e.doit = false;
					}
					super.keyPressed(e);
				}
			});

			button_command = new Button(startComposite, SWT.NONE);
			button_command.setText("执行");
			button_command.setLayoutData(gd_button);
			button_command.addListener(SWT.MouseUp, new Listener() {

				@Override
				public void handleEvent(Event event) {
					ip = ip_text.getText().trim();
					int timeout = Integer.parseInt(text_timeout.getText());
					int port = Integer.parseInt(port_text.getText().toString().trim().equals("") ? "23" : port_text.getText().toString());
					String user = username_text.getText().trim();
					String password = passwd_text.getText().trim();
					String superUser = superusername_text.getText().trim();
					String superpwd = superpasswd_text.getText().trim();
					String command = text_command.getText().toString();
					TelnetCommand telnetcommand = new TelnetCommand();
					telnetcommand.setTelnetCommand(Display.getCurrent(), command, button_command, false);
					telnetcommand.setTelnetData(ip, timeout, port, user, password, superUser, superpwd);
					telnetcommand.start();
				}
			});

			Button button_suspend = new Button(startComposite, SWT.NONE);
			button_suspend.setText("中止");
			button_suspend.setLayoutData(gd_button);
			button_suspend.addListener(SWT.MouseUp, new Listener() {

				@Override
				public void handleEvent(Event event) {
					if (smd != null) {
						smd.stop();
					}
					if (!"执行".equals(button_command.getText())) {
						button_command.setText("执行");
						button_command.setEnabled(true);
					}
				}
			});

			Button button_clean = new Button(startComposite, SWT.NONE);
			button_clean.setText("清空");
			button_clean.setLayoutData(gd_button);
			button_clean.addListener(SWT.MouseUp, new Listener() {

				@Override
				public void handleEvent(Event event) {
					result_text.setText(strlen);
					text_command.setText("");
				}
			});
			Map<String, String> map = getTelnetConnectionNeedData(ip, info_combo.getText());
			if (map != null && map.size() > 1) {
				username_text.setText(map.get("username"));
				passwd_text.setText(map.get("passwd"));
				port_text.setText(map.get("port"));
				superusername_text.setText(map.get("superusername"));
				superpasswd_text.setText(map.get("superuserpwd"));
			}
		} else if (info_combo.getText().equals("TraceRoute")) {

			Label label_community = new Label(startComposite, SWT.NONE | SWT.RIGHT);
			label_community.setText("跃点数:");
			label_community.setLayoutData(gd_label);

			hop_text = new Text(startComposite, SWT.BORDER);
			hop_text.setLayoutData(gd_text);
			hop_text.setText("10");

			Label label_timeout = new Label(startComposite, SWT.NONE | SWT.RIGHT);
			label_timeout.setText("超时时间(毫秒):");
			label_timeout.setLayoutData(gd_label_1);
			text_timeout = new Text(startComposite, SWT.BORDER);
			text_timeout.setLayoutData(gd_text_1);
			text_timeout.setText("5000");

		} else if (info_combo.getText().equals("Snmp测试")) {
			Label label_community = new Label(startComposite, SWT.NONE | SWT.RIGHT);
			label_community.setText("共同体:");
			label_community.setLayoutData(gd_label);
			community_text = new Text(startComposite, SWT.BORDER | SWT.PASSWORD);
			community_text.setLayoutData(gd_text);
			// community_text.setText("public");

			Label label_port = new Label(startComposite, SWT.NONE | SWT.RIGHT);
			label_port.setText("端   口:");
			label_port.setLayoutData(gd_label);
			port_text = new Text(startComposite, SWT.BORDER);
			port_text.setLayoutData(gd_text);
			port_text.setText("161");

			Label label_version = new Label(startComposite, SWT.NONE | SWT.RIGHT);
			label_version.setText("版本");
			label_version.setLayoutData(gd_label);
			combo_version = new Combo(startComposite, SWT.NONE);
			combo_version.add("V1", 0);
			combo_version.add("V2", 1);
			combo_version.add("V3", 2);
			combo_version.select(0);
			combo_version.setLayoutData(gd_text);

			Label label_timeout = new Label(startComposite, SWT.NONE | SWT.RIGHT);
			label_timeout.setText("超时时间(毫秒):");
			label_timeout.setLayoutData(gd_label_1);
			text_timeout = new Text(startComposite, SWT.BORDER);
			text_timeout.setLayoutData(gd_text_1);
			text_timeout.setText("3500");
			versionComposite = new Composite(startComposite, SWT.NONE);
			versionComposite.setLayout(new GridLayout(6, false));
			GridData version_gd_composite = new GridData(GridData.FILL_BOTH);
			version_gd_composite.horizontalSpan = 8;
			version_gd_composite.horizontalIndent = -5;
			versionComposite.setLayoutData(version_gd_composite);
			combo_version.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					GridData gd_button = new GridData();
					gd_button.widthHint = 80;
					if (combo_version.getText().equals("V1") || combo_version.getText().equals("V2")) {
						for (Control control : versionComposite.getChildren()) {
							control.dispose();
						}
						Map<String, String> map = getSNMPConnectionNeedData(ip, combo_version.getText().trim());
						if (map != null && map.size() > 1) {
							community_text.setText(map.get("community"));
							port_text.setText(map.get("port"));
							combo_version.setText(map.get("edition"));
						}
					} else if (combo_version.getText().equals("V3")) {
						// for (Control control :
						// versionComposite.getChildren()) {
						// control.dispose();
						// }
						if (versionComposite.getChildren().length > 0) {
							return;
						}
						Label label_authenticationProtocol = new Label(versionComposite, SWT.NONE | SWT.RIGHT);
						label_authenticationProtocol.setText("验证算法");
						label_authenticationProtocol.setLayoutData(gd_label);
						combo_authenticationProtocol = new Combo(versionComposite, SWT.NONE);
						combo_authenticationProtocol.setLayoutData(gd_text);
						combo_authenticationProtocol.add("MD5", 0);
						combo_authenticationProtocol.add("SHA", 1);
						combo_authenticationProtocol.add("无", 2);
						combo_authenticationProtocol.select(0);

						Label label_username = new Label(versionComposite, SWT.NONE | SWT.RIGHT);
						label_username.setText("用户:");
						label_username.setLayoutData(gd_label);
						username_text = new Text(versionComposite, SWT.BORDER);
						username_text.setLayoutData(gd_text);

						Label label_passwd = new Label(versionComposite, SWT.NONE | SWT.RIGHT);
						label_passwd.setText("用户密码:");
						label_passwd.setLayoutData(gd_label);
						passwd_text = new Text(versionComposite, SWT.BORDER | SWT.PASSWORD);
						passwd_text.setLayoutData(gd_text);

						Label label_privacyProtocol = new Label(versionComposite, SWT.NONE | SWT.RIGHT);
						label_privacyProtocol.setText("隐私算法");
						label_privacyProtocol.setLayoutData(gd_label);
						combo_privacyProtocol = new Combo(versionComposite, SWT.NONE);
						combo_privacyProtocol.add("DES", 0);
						combo_privacyProtocol.add("AES128", 1);
						combo_privacyProtocol.add("AES192", 2);
						combo_privacyProtocol.add("AES256", 3);
						combo_privacyProtocol.select(0);
						combo_privacyProtocol.setLayoutData(gd_text);

						Label label_privacyPwd = new Label(versionComposite, SWT.NONE | SWT.RIGHT);
						label_privacyPwd.setText("隐私密码:");
						label_privacyPwd.setLayoutData(gd_label);
						privacyPwd_text = new Text(versionComposite, SWT.BORDER | SWT.PASSWORD);
						privacyPwd_text.setLayoutData(gd_text);
						versionComposite.layout();

						Map<String, String> map = getSNMPConnectionNeedData(ip, "V3");
						if (map != null && map.size() > 1) {
							combo_authenticationProtocol.setText(map.get("authenticationProtocol"));
							username_text.setText(map.get("username"));
							passwd_text.setText(map.get("passwd"));
							combo_privacyProtocol.setText(map.get("privacyProtocol"));
							privacyPwd_text.setText(map.get("privacyPwd"));
						}
					}
					versionComposite.layout();
					startComposite.layout();
					group.layout();

				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {

				}
			});
			ip = ip_text.getText().trim();
			Map<String, String> map = getSNMPConnectionNeedData(ip, null);
			if (map != null && map.size() > 1) {
				community_text.setText(map.get("community"));
				port_text.setText(map.get("port"));
				combo_version.setText(map.get("edition"));
				if ("V3".equals(map.get("edition"))) {
					if (versionComposite.getChildren().length > 0) {
						return;
					}
					Label label_authenticationProtocol = new Label(versionComposite, SWT.NONE | SWT.RIGHT);
					label_authenticationProtocol.setText("验证算法");
					label_authenticationProtocol.setLayoutData(gd_label);
					combo_authenticationProtocol = new Combo(versionComposite, SWT.NONE);
					combo_authenticationProtocol.setLayoutData(gd_text);
					combo_authenticationProtocol.add("MD5", 0);
					combo_authenticationProtocol.add("SHA", 1);
					combo_authenticationProtocol.add("无", 2);
					combo_authenticationProtocol.select(0);

					Label label_username = new Label(versionComposite, SWT.NONE | SWT.RIGHT);
					label_username.setText("用户:");
					label_username.setLayoutData(gd_label);
					username_text = new Text(versionComposite, SWT.BORDER);
					username_text.setLayoutData(gd_text);

					Label label_passwd = new Label(versionComposite, SWT.NONE | SWT.RIGHT);
					label_passwd.setText("密码:");
					label_passwd.setLayoutData(gd_label);
					passwd_text = new Text(versionComposite, SWT.BORDER | SWT.PASSWORD);
					passwd_text.setLayoutData(gd_text);

					Label label_privacyProtocol = new Label(versionComposite, SWT.NONE | SWT.RIGHT);
					label_privacyProtocol.setText("隐私算法");
					label_privacyProtocol.setLayoutData(gd_label);
					combo_privacyProtocol = new Combo(versionComposite, SWT.NONE);
					combo_privacyProtocol.add("DES", 0);
					combo_privacyProtocol.add("AES128", 1);
					combo_privacyProtocol.add("AES192", 2);
					combo_privacyProtocol.add("AES256", 3);
					combo_privacyProtocol.select(0);
					combo_privacyProtocol.setLayoutData(gd_text);

					Label label_privacyPwd = new Label(versionComposite, SWT.NONE | SWT.RIGHT);
					label_privacyPwd.setText("隐私密码:");
					label_privacyPwd.setLayoutData(gd_label);
					privacyPwd_text = new Text(versionComposite, SWT.BORDER | SWT.PASSWORD);
					privacyPwd_text.setLayoutData(gd_text);
					versionComposite.layout();

					combo_authenticationProtocol.setText(map.get("authenticationProtocol"));
					username_text.setText(map.get("username"));
					passwd_text.setText(map.get("passwd"));
					combo_privacyProtocol.setText(map.get("privacyProtocol"));
					privacyPwd_text.setText(map.get("privacyPwd"));
				}
			}
		}
		startComposite.layout();
	}

	/**
	 * Save equipment SNMP connection configuration
	 * 
	 * @param trim
	 */
	protected void saveSNMPConnConfig() {
		String connType = "SNMP";
		ISiteviewApi api = ConnectionBroker.get_SiteviewApi();
		ip = ip_text.getText().trim();
		String community = community_text.getText();
		String edition = combo_version.getText().trim();
		String userName = "";
		String pwd = "";
		String port = port_text.getText().trim();
		;
		String authenticationProtocol = "";
		String superUserName = "";
		String superUserPWD = "";
		if (edition.equals("V3")) {
			userName = username_text.getText().trim();
			pwd = passwd_text.getText().trim();
			authenticationProtocol = combo_authenticationProtocol.getText().trim();
			superUserName = combo_privacyProtocol.getText().trim();
			superUserPWD = privacyPwd_text.getText().trim();
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("IPAddress", ip);
		map.put("ConnType", connType);
		map.put("Agreement", edition);
		try {
			BusinessObject ccBo = DBQueryUtils.queryOnlyBo(map, "ConnConfig", api);
			if (ccBo == null) {
				ccBo = api.get_BusObService().Create("ConnConfig");
				ccBo.GetField("ConnType").SetValue(new SiteviewValue(connType));
				ccBo.GetField("UserName").SetValue(new SiteviewValue(userName));
				ccBo.GetField("UserPWD").SetValue(new SiteviewValue(pwd));
				ccBo.GetField("superUserName").SetValue(new SiteviewValue(superUserName));
				ccBo.GetField("superUserPWD").SetValue(new SiteviewValue(superUserPWD));
				ccBo.GetField("Port").SetValue(new SiteviewValue(port));
				ccBo.GetField("IPAddress").SetValue(new SiteviewValue(ip));
				ccBo.GetField("PortAgreement").SetValue(new SiteviewValue(authenticationProtocol));
				ccBo.GetField("Agreement").SetValue(new SiteviewValue(edition));
				ccBo.GetField("MAC").SetValue(new SiteviewValue(community));
				Boolean flag = ccBo.SaveObject(api, true, true).get_SaveSuccess();
				if (flag) {
					MsgBox.ShowWarning("保存结果", "连接配置保存成功");
				}
			} else {
				ccBo.GetField("ConnType").SetValue(new SiteviewValue(connType));
				ccBo.GetField("UserName").SetValue(new SiteviewValue(userName));
				ccBo.GetField("UserPWD").SetValue(new SiteviewValue(pwd));
				ccBo.GetField("superUserName").SetValue(new SiteviewValue(superUserName));
				ccBo.GetField("superUserPWD").SetValue(new SiteviewValue(superUserPWD));
				ccBo.GetField("Port").SetValue(new SiteviewValue(port));
				ccBo.GetField("IPAddress").SetValue(new SiteviewValue(ip));
				ccBo.GetField("PortAgreement").SetValue(new SiteviewValue(authenticationProtocol));
				ccBo.GetField("Agreement").SetValue(new SiteviewValue(edition));
				ccBo.GetField("MAC").SetValue(new SiteviewValue(community));
				Boolean flag = ccBo.SaveObject(api, true, true).get_SaveSuccess();
				if (flag) {
					MsgBox.ShowWarning("保存结果", "连接配置更新成功");
				}
			}
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Save equipment TELNET connection configuration
	 */
	protected void saveTelnetConnConfig(String connType) {
		ISiteviewApi api = ConnectionBroker.get_SiteviewApi();
		ip = ip_text.getText().trim();
		String userName = username_text.getText().trim();
		String pwd = passwd_text.getText().trim();
		String port = port_text.getText().trim();
		String superUserName = superusername_text.getText().trim();
		String superUserPWD = superpasswd_text.getText().trim();
		Map<String, String> map = new HashMap<String, String>();
		map.put("IPAddress", ip);
		map.put("ConnType", connType);
		try {
			BusinessObject ccBo = DBQueryUtils.queryOnlyBo(map, "ConnConfig", api);
			if (ccBo == null) {
				ccBo = api.get_BusObService().Create("ConnConfig");
				ccBo.GetField("ConnType").SetValue(new SiteviewValue(connType));
				ccBo.GetField("UserName").SetValue(new SiteviewValue(userName));
				ccBo.GetField("UserPWD").SetValue(new SiteviewValue(pwd));
				ccBo.GetField("superUserName").SetValue(new SiteviewValue(superUserName));
				ccBo.GetField("superUserPWD").SetValue(new SiteviewValue(superUserPWD));
				ccBo.GetField("Port").SetValue(new SiteviewValue(port));
				ccBo.GetField("IPAddress").SetValue(new SiteviewValue(ip));
				ccBo.GetField("MAC").SetValue(new SiteviewValue(community));
				Boolean flag = ccBo.SaveObject(api, true, true).get_SaveSuccess();
				if (flag) {
					MsgBox.ShowWarning("保存结果", "连接配置保存成功");
				}
			} else {
				ccBo.GetField("ConnType").SetValue(new SiteviewValue(connType));
				ccBo.GetField("UserName").SetValue(new SiteviewValue(userName));
				ccBo.GetField("UserPWD").SetValue(new SiteviewValue(pwd));
				ccBo.GetField("superUserName").SetValue(new SiteviewValue(superUserName));
				ccBo.GetField("superUserPWD").SetValue(new SiteviewValue(superUserPWD));
				ccBo.GetField("Port").SetValue(new SiteviewValue(port));
				ccBo.GetField("IPAddress").SetValue(new SiteviewValue(ip));
				ccBo.GetField("MAC").SetValue(new SiteviewValue(community));
				Boolean flag = ccBo.SaveObject(api, true, true).get_SaveSuccess();
				if (flag) {
					MsgBox.ShowWarning("保存结果", "连接配置更新成功");
				}
			}
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Save equipment SSH connection configuration
	 */
	protected void saveSShConnConfig(String connType) {
		ISiteviewApi api = ConnectionBroker.get_SiteviewApi();
		ip = ip_text.getText().trim();
		String userName = username_text.getText().trim();
		String pwd = passwd_text.getText().trim();
		String port = port_text.getText().trim();
		Map<String, String> map = new HashMap<String, String>();
		map.put("IPAddress", ip);
		map.put("ConnType", connType);
		try {
			BusinessObject ccBo = DBQueryUtils.queryOnlyBo(map, "ConnConfig", api);
			if (ccBo == null) {
				ccBo = api.get_BusObService().Create("ConnConfig");
				ccBo.GetField("ConnType").SetValue(new SiteviewValue(connType));
				ccBo.GetField("UserName").SetValue(new SiteviewValue(userName));
				ccBo.GetField("UserPWD").SetValue(new SiteviewValue(pwd));
				ccBo.GetField("Port").SetValue(new SiteviewValue(port));
				ccBo.GetField("IPAddress").SetValue(new SiteviewValue(ip));
				Boolean flag = ccBo.SaveObject(api, true, true).get_SaveSuccess();
				if (flag) {
					MsgBox.ShowWarning("保存结果", "连接配置保存成功");
				}
			} else {
				ccBo.GetField("ConnType").SetValue(new SiteviewValue(connType));
				ccBo.GetField("UserName").SetValue(new SiteviewValue(userName));
				ccBo.GetField("UserPWD").SetValue(new SiteviewValue(pwd));
				ccBo.GetField("Port").SetValue(new SiteviewValue(port));
				ccBo.GetField("IPAddress").SetValue(new SiteviewValue(ip));
				Boolean flag = ccBo.SaveObject(api, true, true).get_SaveSuccess();
				if (flag) {
					MsgBox.ShowWarning("保存结果", "连接配置更新成功");
				}
			}
		} catch (SiteviewException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(800, 600);
	}

	private class SetMibData extends Thread {
		private String temp;
		private Display display = null;
		private Text result_text;
		final StringBuffer sa = new StringBuffer();
		private String command;
		private Button button;
		private Map<String, String> snmpData;
		private String ip;
		private int timeout;
		private int port;
		private String user;
		private String password;
		private String superUser;
		private String superpwd;

		public void passData(String temp, Display display, Text result_text, Button select_button) {
			this.display = display;
			this.temp = temp;
			this.result_text = result_text;
			this.button = select_button;

		}

		public void passPingData(String command) {
			this.command = command;
		}

		public void passSnmpData(Map<String, String> snmpData) {
			this.snmpData = snmpData;
		}

		public void passTelnetData(String ip, int timeout, int port, String user, String password, String superUser, String superpwd) {
			this.timeout = timeout;
			this.port = port;
			this.user = user;
			this.password = password;
			this.ip = ip;
			this.superUser = superUser;
			this.superpwd = superpwd;
		}

		public void passSSHData(String ip, int timeout, int port, String user, String password) {
			this.timeout = timeout;
			this.port = port;
			this.user = user;
			this.password = password;
			this.ip = ip;
		}

		@Override
		public void run() {
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					button.setEnabled(false);
				}
			});
			Process process = null;
			BufferedReader br = null;
			if (temp.equals("Ping")) {
				super.run();
				String line = null;
				try {
					process = Runtime.getRuntime().exec(command);
					br = new BufferedReader(new InputStreamReader(process.getInputStream()));
					while ((line = br.readLine()) != null) {
						sa.append(line + "\n");
						display.asyncExec(new Runnable() {
							public void run() {
								result_text.setText(sa.toString());
							}
						});
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					try {
						br.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					if(process!=null){
						process.destroy();
					}
				}
			} else if (temp.equals("SSH")) {
				// final String temp1 = sshLogin(this.ip, this.user, this.port,
				// this.password, this.timeout,"cat /proc/version");
				// display.asyncExec(new Runnable() {
				//
				// @Override
				// public void run() {
				// result_text.setText(result_text.getText() + "\n" + temp1);
				// }
				// });
				sshconn = new Connection(this.ip, this.port);
				try {
					sshconn.connect(null, timeout, timeout);
					if (sshconn.authenticateWithPassword(this.user, this.password)) {
						Session session = sshconn.openSession();
						session.execCommand("cat /proc/version");
						InputStream in = null;
						// session.requestPTY("Linux", 800, 240, 640, 480,
						// null);
						in = new StreamGobbler(session.getStdout());
						BufferedReader bf = new BufferedReader(new InputStreamReader(in, "UTF-8"));
						String line = bf.readLine();
						while (line != null) {
							final String temp = line;
							display.asyncExec(new Runnable() {

								@Override
								public void run() {
									result_text.setText(result_text.getText() + "\n" + temp);
								}
							});
							line = bf.readLine();
						}
						bf.close();
						in.close();
						session.close();
					} else {
						display.asyncExec(new Runnable() {
							public void run() {
								result_text.setText("用户或密码错误!");
							}
						});
					}
				} catch (IOException e) {
					final String ioException = e.getMessage();
					display.asyncExec(new Runnable() {
						public void run() {
							result_text.setText("连接失败!" + ioException);
						}
					});

				}
			} else if (temp.equals("Telnet")) {
				telnetLogin(this.ip, this.port, timeout, display, user, password, superUser, superpwd);
			} else if (temp.equals("TraceRoute")) {
				String line = null;
				try {
					process = Runtime.getRuntime().exec(command);
					br = new BufferedReader(new InputStreamReader(process.getInputStream()));
					while ((line = br.readLine()) != null) {
						sa.append(line + "\n");
						display.asyncExec(new Runnable() {
							public void run() {
								result_text.setText(sa.toString());
							}
						});
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					try {
						br.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					process.destroy();
				}
			} else if (temp.equals("Snmp测试")) {
				SnmpGet snmpGet = null;
				try {
					snmpGet = getSnmpGet(snmpData);
					final String ss = snmpGet.snmpGet();
					if (ss.contains("error")) {
						display.asyncExec(new Runnable() {
							public void run() {
								result_text.setText(ss + "\nConnection Failure");
							}
						});
					} else {
						display.asyncExec(new Runnable() {
							public void run() {
								result_text.setText(ss + "\nConnection Successful");
							}
						});
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					// MsgBox.ShowWarning("测试连接", "连接失败！");
				}

			}
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					button.setEnabled(true);
					button.setText("诊断");
				}
			});
		}

	}

	private class TelnetCommand extends Thread {
		private String command;
		private Display display;
		private Button button;
		boolean flag;
		private String ip;
		private int timeout;
		private int port;
		private String user;
		private String password;
		private String superUser;
		private String superpwd;

		@Override
		public void run() {
			display.asyncExec(new Runnable() {

				@Override
				public void run() {
					button.setText("执行中...");
					button.setEnabled(false);
				}
			});
			if (execute) {
				telnetLogin(ip, port, timeout, display, user, password, superUser, superpwd);
				execute = false;
			}
			if (telnetclient != null && telnetclient.isAvailable()) {
				try {
					write(command);
					final String result = readCiscoUntil2(strlen, flag, command, display);
					display.asyncExec(new Runnable() {

						@Override
						public void run() {
							result_text.setText(result_text.getText() + result);
						}
					});

				} catch (Exception e) {
					result_text.setText(result_text.getText() + e.getMessage());
				}
			}
			display.asyncExec(new Runnable() {

				@Override
				public void run() {
					button.setText("执行");
					button.setEnabled(true);
				}
			});
		}

		public void setTelnetCommand(Display current, String command, Button button_command, boolean flag) {
			this.command = command;
			this.display = current;
			this.button = button_command;
			this.flag = flag;
		}

		public void setTelnetData(String ip, int timeout, int port, String user, String password, String superUser, String superpwd) {
			this.timeout = timeout;
			this.port = port;
			this.user = user;
			this.password = password;
			this.ip = ip;
			this.superUser = superUser;
			this.superpwd = superpwd;
		}

	}

	private class SSHCommand extends Thread {
		private String command;
		private Display display;
		private Button button;
		private int timeout;
		private int port;
		private String user;
		private String password;
		private String ip;
		Session session;

		@Override
		public void run() {
			display.asyncExec(new Runnable() {

				@Override
				public void run() {
					button.setText("执行中...");
					button.setEnabled(false);
					result_text.setText(result_text.getText() + "\n");
				}
			});
			// sshLogin(ip,user,port,password,timeout,command);

			if (sshconn != null) {
				BufferedReader bf = null;
				InputStream in = null;
				try {
					session.execCommand(command);
					in = new StreamGobbler(session.getStdout());
					bf = new BufferedReader(new InputStreamReader(in, "utf-8"));
					String line = bf.readLine();
					while (line != null) {
						final String temp = line;
						display.asyncExec(new Runnable() {

							@Override
							public void run() {
								result_text.setText(result_text.getText() + "\n" + temp);
							}
						});
						line = bf.readLine();
					}

				} catch (Exception e) {
					result_text.setText(result_text.getText() + e.getMessage());
				} finally {
					try {
						if (bf != null)
							bf.close();
						if (in != null)
							in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			display.asyncExec(new Runnable() {

				@Override
				public void run() {
					button.setText("执行");
					button.setEnabled(true);
				}
			});
		}

		public void setTelnetCommand(Session session, Display current, String command, Button button_command) {
			this.command = command;
			this.display = current;
			this.button = button_command;
			this.session = session;
		}

		public void setSSHData(String ip, int timeout, int port, String user, String password) {
			this.timeout = timeout;
			this.port = port;
			this.user = user;
			this.password = password;
			this.ip = ip;
		}

	}

	public static SnmpGet getSnmpGet(Map<String, String> snmpData) {
		String serverAddress = snmpData.get("serverAddress");
		String edition = snmpData.get("edition");
		int port = Integer.parseInt(snmpData.get("port"));
		String community = snmpData.get("community");
		String oid = "1.3.6.1.2.1.1.1.0";
		long timeOut = Long.parseLong(snmpData.get("timeout"));

		int version = -1;
		SnmpGet snmpGet = null;
		if (edition.equalsIgnoreCase("1")) {
			version = SnmpConstants.version1;
			snmpGet = new SnmpGet(serverAddress, port, community, oid, version, timeOut);
		} else if (edition.equalsIgnoreCase("2")) {
			version = SnmpConstants.version2c;
			snmpGet = new SnmpGet(serverAddress, port, community, oid, version, timeOut);
		} else if (edition.equalsIgnoreCase("3")) {
			version = SnmpConstants.version3;
			String username = snmpData.get("username");
			String passwd = snmpData.get("passwd");
			String authenticationProtocol = snmpData.get("authenticationProtocol");
			String privacyProtocol = snmpData.get("privacyProtocol");
			String privacyPwd = snmpData.get("privacyPwd");
			snmpGet = new SnmpGet(serverAddress, port, community, oid, version, timeOut, username, authenticationProtocol, passwd, privacyProtocol, privacyPwd);
		}
		return snmpGet;
	}

	/**
	 * 判断命令执行的结果
	 * 
	 * @param content
	 * @param errorStr
	 * @param successStr
	 * @return
	 */
	private boolean isCommadSuccess(String content, String[] errorStr, String[] successStr) {
		for (int i = 0; i < errorStr.length; i++) {
			if (content.endsWith(errorStr[i])) {
				return false;
			}
		}
		for (int i = 0; i < successStr.length; i++) {
			if (content.endsWith(successStr[i])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Telnet Login
	 * 
	 * @param user
	 * @param password
	 * @param display
	 * @return
	 * @throws Exception
	 * 
	 */

	public boolean login(String user, String password, Display display) throws Exception {
		String str = readTelnetUntil(validationArr_user, validationArr_pwd);
		if (user != null && !"".equals(user) && (str.toLowerCase().equals("login:") || str.toLowerCase().equals("username:"))) {
			write(user);
			readUntil("Password:");
			write(password);
			return determineCommand(validationArr_wildcard, validationArr_user);
		} else if ("Password:".equals(str)) {
			write(password);
			return determineCommand(validationArr_wildcard, validationArr_pwd);
		} else {
			return false;
		}
		// return readUntil(">").equals("Bad passwords");
	}

	/**
	 * 获取 连接中的具体返回
	 * 
	 * @param pattern
	 * @return
	 * @throws Exception
	 */
	public String readUntil(String pattern) throws Exception {
		char lastChar = pattern.charAt(pattern.length() - 1);
		final StringBuffer sb = new StringBuffer();
		int line = -1;
		line = telnetclient.getInputStream().read();
		char ch = (char) line;
		while (line != -1) {
			sb.append(ch);
			if (ch == lastChar) {
				if (sb.toString().toLowerCase().endsWith(pattern.toLowerCase())) {
					return sb.toString();
				}
			}
			line = telnetclient.getInputStream().read();
			ch = (char) line;

		}
		return sb.toString();
	}

	/**
	 * 获取 连接中的具体返回
	 * 
	 * @param pattern
	 * @return
	 * @throws Exception
	 */
	public String readTelnetUntil(String[] user, String[] pwd) throws Exception {
		final StringBuffer sb = new StringBuffer();
		int line = -1;
		line = telnetclient.getInputStream().read();
		char ch = (char) line;
		while (line != -1) {
			sb.append(ch);
			// System.out.print(ch);
			for (int i = 0; i < user.length; i++) {
				char lastChar = user[i].charAt(user[i].length() - 1);
				if (ch == lastChar) {
					if (sb.toString().toLowerCase().endsWith(user[i].toLowerCase())) {
						return user[i];
					}
				}
			}
			for (int i = 0; i < pwd.length; i++) {
				char lastChar = pwd[i].charAt(pwd[i].length() - 1);
				if (ch == lastChar) {
					if (sb.toString().toLowerCase().endsWith(pwd[i].toLowerCase())) {
						return pwd[i];
					}
				}
			}

			line = telnetclient.getInputStream().read();
			ch = (char) line;

		}
		return sb.toString();
	}

	/**
	 * write command
	 * 
	 * @param value
	 */
	public void write(String value) {
		try {
			out = new PrintStream(telnetclient.getOutputStream());
			// System.out.print( value);
			this.out.println(value);
			this.out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断命令是否执行成功
	 * 
	 * @param successStr
	 *            成功判断符
	 * @param errorStr
	 *            失败判断符
	 * @return
	 * @throws Exception
	 */
	public boolean determineCommand(String[] successStr, String[] errorStr) throws Exception {
		int k = 0;
		StringBuffer sb = new StringBuffer();
		int line = -1;
		line = telnetclient.getInputStream().read();
		char ch = (char) line;
		try {
			while (line != -1) {
				sb.append(ch);
				System.out.print(ch);
				for (int i = 0; i < errorStr.length; i++) {
					if (sb.toString().endsWith(errorStr[i])) {
						k = 1;
						break;
					}
				}
				for (int i = 0; i < successStr.length; i++) {
					if (sb.toString().endsWith(successStr[i])) {
						k = 1;
						break;
					}
				}
				if (k == 1) {
					break;
				}
				line = telnetclient.getInputStream().read();
				ch = (char) line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String content = sb.toString();
		if (content.contains(">") && content.contains("\r\n")) {
			strlen = content.substring(content.lastIndexOf("\r\n") + 2, content.length()).trim();
		} else {
			if (content.contains("\r\n")) {
				content = content.replace("\r\n", "");
			}
			strlen = content.trim();
		}
		return isCommadSuccess(content, errorStr, successStr);

	}

	public String readCiscoUntil2(String pattern, boolean flag, String command, Display display) {
		final StringBuffer buffer = new StringBuffer();
		StringBuffer sb1 = new StringBuffer();
		try {
			br = new BufferedReader(new InputStreamReader(telnetclient.getInputStream()));

			int line = br.read();
			while (line != -1) {
				char ch = (char) line;
				buffer.append(ch);
				if (buffer.toString().trim().endsWith("More")) {
					execute = true;
					write(" ");
				} else if (buffer.toString().trim().endsWith(strlen)) {
					break;
				} else if (buffer.toString().trim().endsWith("[Quidway]")) {
					break;
				}
				if (flag) {
					if (buffer.toString().trim().endsWith(command)) {
						buffer.setLength(0);
						flag = false;
					}
				}
				line = br.read();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return buffer.toString();
		}
		for (int i = 0; i < buffer.length(); i++) {
			char ch1 = buffer.charAt(i);
			sb1.append(ch1);
			if (sb1.toString().endsWith(" --More--         ")) {
				String str = " --More--         ";
				sb1.replace(sb1.length() - str.length(), sb1.length(), "");
			} else if (sb1.toString().endsWith("---- More ----[42D                                          [42D")) {
				String str = "---- More ----[42D                                          [42D";
				sb1.replace(sb1.length() - str.length(), sb1.length(), "");
			}
		}
		if (sb1.length() != 0 && sb1.length() != buffer.length())
			return sb1.toString();
		return buffer.toString();
	}

	@Override
	public boolean close() {
		if (sps != null) {
			sps.stop();
		}
		if (telnetclient != null) {
			try {
				telnetclient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return super.close();
	}

	public void telnetLogin(String ip, int port, int timeout, Display display, String user, String password, String superUser, String superpwd) {
		if (telnetclient != null && telnetclient.isConnected()) {
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
				telnetclient.disconnect();
				telnetclient = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		telnetclient = new TelnetClient("Linux");
		telnetclient.setConnectTimeout(timeout);
		try {
			telnetclient.connect(ip, port);
			telnetclient.setSoTimeout(50000);
		} catch (SocketException e) {
			final String socketException = e.getMessage();
			display.asyncExec(new Runnable() {
				public void run() {
					result_text.setText("连接失败!" + socketException);
				}
			});
		} catch (IOException e) {
			final String ioException = e.getMessage();
			display.asyncExec(new Runnable() {
				public void run() {
					result_text.setText("连接失败!" + ioException);
				}
			});
		}
		try {
			in = telnetclient.getInputStream();
			out = new PrintStream(telnetclient.getOutputStream());
			// System.out.println(user+"---"+password);
			if (!login(user, password, display)) {
				display.asyncExec(new Runnable() {
					public void run() {
						result_text.setText("用户或密码错误!");
					}
				});
			} else {
				if (superpwd != null && !"".equals(superpwd)) {
					write(superUser);
					String str = readTelnetUntil(strlen.split(","), validationArr_pwd);
					if (str.equals("Password:")) {
						write(superpwd);
						if (determineCommand(validationArr_wildcard, validationArr_pwd)) {
							display.asyncExec(new Runnable() {
								public void run() {
									String result = result_text.getText();
									if (result == null || "".equals(result))
										result_text.setText(strlen);
									else
										result_text.setText(result + "\n" + strlen);
								}
							});
						} else {
							display.asyncExec(new Runnable() {
								public void run() {
									result_text.setText("超级用户或超级密码错误!" + "\n" + strlen);
								}
							});
						}
					} else if (str.endsWith(strlen)) {
						display.asyncExec(new Runnable() {
							public void run() {
								result_text.setText("超级用户不正确" + "\n" + strlen);
							}
						});
					}

				} else {
					display.asyncExec(new Runnable() {
						public void run() {
							String result = result_text.getText().trim();
							if (result == null || "".equals(result))
								result_text.setText(strlen);
							else
								result_text.setText(result + "\n" + strlen);

						}
					});
				}
			}

		} catch (Exception e) {
			final String exception = e.getMessage();
			display.asyncExec(new Runnable() {
				public void run() {
					result_text.setText("登录失败!" + exception);
				}
			});
		}
	}

	/**
	 * Get SSH connection configuration
	 * 
	 * @param ip
	 * @return
	 */
	private Map<String, String> getSSHUserNameAndPassword(String ip) {
		if (ip == null || "".equals(ip)) {
			return null;
		}
		ISiteviewApi api = ConnectionBroker.get_SiteviewApi();
		Map<String, String> map = new HashMap<String, String>();
		map.put("ServerAddress", ip);
		try {
			Collection<BusinessObject> coll = DBQueryUtils.getLikeBussCollection("ServerAddress", ip, "Equipment", api);
			if (coll != null) {
				Iterator<BusinessObject> it = coll.iterator();
				while (it.hasNext()) {
					map.clear();
					BusinessObject bo = it.next();
					String conntype = bo.GetField("connectiontype").get_Value().toString();
					if (conntype.toLowerCase().equals("ssh")) {
						String port = bo.GetField("port").get_Value().toString();
						String username = bo.GetField("username").get_Value().toString();
						String passwd = bo.GetField("passwd").get_Value().toString();
						map.put("port", port);
						map.put("username", username);
						map.put("passwd", passwd);
					}
				}
			}
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
		if (map.size() <= 1) {
			Map<String, String> conditionsmap = new HashMap<String, String>();
			conditionsmap.put("IPAddress", ip);
			conditionsmap.put("ConnType", "SSH");
			try {
				BusinessObject ccBo = DBQueryUtils.queryOnlyBo(conditionsmap, "ConnConfig", api);
				if (ccBo != null) {
					map.put("port", ccBo.GetField("Port").get_Value().toString());
					map.put("username", ccBo.GetField("UserName").get_Value().toString());
					map.put("passwd", ccBo.GetField("UserPWD").get_Value().toString());
				}

			} catch (Exception e) {

			}
		}
		return map;
	}

	private Map<String, String> getSNMPConnectionNeedData(String ip, String edition) {
		if (ip == null || "".equals(ip)) {
			return null;
		}
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String> map2 = new HashMap<String, String>();
		map2.put("ServerAddress", ip);
		if (edition != null)
			map2.put("edition", edition);
		try {
			Collection<BusinessObject> coll = DBQueryUtils.getBussCollection(map2, "Equipment", api);
			// Collection<BusinessObject> coll =
			// DBQueryUtils.getLikeBussCollection("ServerAddress", ip,
			// "Equipment", ConnectionBroker.get_SiteviewApi());
			if (coll != null && !coll.isEmpty()) {
				Iterator<BusinessObject> it = coll.iterator();
				while (it.hasNext()) {
					map.clear();
					BusinessObject bo = it.next();
					String conntype = bo.GetField("connectiontype").get_Value().toString();
					if (conntype.toLowerCase().equals("snmp")) {
						String port = bo.GetField("port").get_Value().toString();
						String community = bo.GetField("community").get_Value().toString();
						edition = bo.GetField("edition").get_Value().toString();
						String username = bo.GetField("username").get_Value().toString();
						String passwd = bo.GetField("passwd").get_Value().toString();
						String authenticationProtocol = bo.GetField("authenticationProtocol").get_Value().toString();
						String privacyPwd = bo.GetField("privacyPwd").get_Value().toString();
						String privacyProtocol = bo.GetField("privacyProtocol").get_Value().toString();
						map.put("port", port);
						map.put("community", community);
						map.put("edition", edition);
						map.put("username", username);
						map.put("passwd", passwd);
						map.put("authenticationProtocol", authenticationProtocol);
						map.put("privacyPwd", privacyPwd);
						map.put("privacyProtocol", privacyProtocol);
					}
				}
			}
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
		if (map.size() < 1) {
			try {
				Map<String, String> map1 = new HashMap<String, String>();
				map1.put("IPAddress", ip);
				map1.put("ConnType", "SNMP");
				if(edition!=null)
					map1.put("Agreement", edition);
				BusinessObject connBo = DBQueryUtils.queryOnlyBo(map1, "ConnConfig", api);
				if (connBo != null) {
					map.clear();
					String community = connBo.GetField("MAC").get_Value().toString();
					String port = connBo.GetField("Port").get_Value().toString();
					String username = connBo.GetField("UserName").get_Value().toString();
					String passwd = connBo.GetField("UserPWD").get_Value().toString();
					String privacyProtocol = connBo.GetField("SuperUserName").get_Value().toString();
					String privacyPwd = connBo.GetField("SuperUserPWD").get_Value().toString();
					edition = connBo.GetField("Agreement").get_Value().toString();
					String authenticationProtocol = connBo.GetField("PortAgreement").get_Value().toString();
					map.put("port", port);
					map.put("community", community);
					map.put("edition", edition);
					map.put("username", username);
					map.put("passwd", passwd);
					map.put("authenticationProtocol", authenticationProtocol);
					map.put("privacyPwd", privacyPwd);
					map.put("privacyProtocol", privacyProtocol);
				}
			} catch (SiteviewException e) {
				e.printStackTrace();
			}
		}
		return map;
	}

	private Map<String, String> getTelnetConnectionNeedData(String ip, String connType) {
		if (ip == null || "".equals(ip)) {
			return null;
		}
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String> map1 = new HashMap<String, String>();
		map1.put("IPAddress", ip);
		map1.put("ConnType", connType);
		try {
			BusinessObject connBo = DBQueryUtils.queryOnlyBo(map1, "ConnConfig", api);
			if (connBo != null) {
				map.clear();
				String port = connBo.GetField("Port").get_Value().toString();
				String username = connBo.GetField("UserName").get_Value().toString();
				String passwd = connBo.GetField("UserPWD").get_Value().toString();
				String superUserName = connBo.GetField("SuperUserName").get_Value().toString();
				String superUserPWD = connBo.GetField("SuperUserPWD").get_Value().toString();
				map.put("port", port);
				map.put("username", username);
				map.put("passwd", passwd);
				map.put("superusername", superUserName);
				map.put("superuserpwd", superUserPWD);
			}
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
		return map;
	}

}
