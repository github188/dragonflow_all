package com.siteview.NNM.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.snmp4j.mp.SnmpConstants;

import Siteview.Windows.Forms.MsgBox;

import com.siteview.nnm.data.model.TopoChart;
import com.siteview.nnm.data.model.svNode;
import com.siteview.utils.snmp.SnmpGet;

public class DeviceSnmptTestDialogs extends Dialog {
	String ip;
	String community;
	int port;
	int nver;
	String typetable;
	Text ip_text;
	Text port_text;
	Text community_text;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DeviceSnmptTestDialogs(Shell parentShell, String typetable, String tag, String nodeid) {
		super(parentShell);
		TopoChart tochart = com.siteview.nnm.data.DBManage.Topos.get(tag);
		svNode entityobj = (svNode) tochart.getNodes().get(nodeid);
		ip = entityobj.getLocalip();
		community = entityobj.getProperys().get("Community");
		String dver = entityobj.getProperys().get("Version");
		port = (Integer.parseInt(entityobj.getProperys().get("port").equals("") ? "161" : entityobj.getProperys().get("port")));
		if (dver.equals("1")) {
			nver = 1;
		} else if (dver.equals("2")) {
			nver = 2;
		}
		this.typetable = typetable;
	}

	protected void configureShell(Shell newShell) {
		// newShell.setSize(450, 320);
		// newShell.setLocation(400, 175);
		newShell.setText("SNMP测试连接");
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
		// ImageKeys.loadimage();
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout());
		
		Group group = new Group(container, SWT.NONE);
		GridData gd_group = new GridData();
		gd_group.widthHint=205;
		group.setLayoutData(gd_group);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.horizontalSpacing = 10;
		group.setLayout(gridLayout);

		GridData data_text = new GridData();
		data_text.widthHint = 120;

		GridData data_but = new GridData();
		data_but.widthHint = 130;

		Label label = new Label(group, SWT.NONE|SWT.RIGHT);
		label.setText("IP地址:");

		ip_text = new Text(group, SWT.BORDER);
		ip_text.setText(ip);
		ip_text.setLayoutData(data_text);
		ip_text.setEnabled(false);

		Label label_port = new Label(group, SWT.NONE|SWT.RIGHT);
		label_port.setText("端口:");
		port_text = new Text(group, SWT.BORDER);
		port_text.setText(port + "");
		port_text.setLayoutData(data_text);

		Label label_community = new Label(group, SWT.NONE|SWT.RIGHT);
		label_community.setText("共同体:");
		community_text = new Text(group, SWT.BORDER);
		community_text.setText(community);
		community_text.setLayoutData(data_text);

		 new Label(group, SWT.NONE);
		
		Button button = new Button(group, SWT.NONE);
		button.setText("测试连接");
		button.setLayoutData(data_but);
		button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				String serverAddress = ip_text.getText().toString().trim();
				String edition = nver + "";
				String community = community_text.getText().toString().trim();
				int port = Integer.parseInt(port_text.getText().toString().trim().equals("") ? "161" : port_text.getText().toString());
				SnmpGet snmpGet=null;
				try {
					 snmpGet = getSnmpGet(serverAddress, edition, port, community, "1.3.6.1.2.1.1.1.0", 3500);
					String ss = snmpGet.snmpGet();
					System.out.println(ss);
					if(ss.contains("error")){
						MsgBox.ShowWarning("测试连接", "连接失败！");//("连接失败！");//(null, "", "测试连接");
						return;
					}
					MsgBox.ShowWarning("测试连接", "连接成功！");
				} catch (Exception ex) {
					ex.printStackTrace();
					MsgBox.ShowWarning("测试连接", "连接失败！");
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		
//		Button button = new Button(container, SWT.NONE);
//		button.setText("取消");
		return container;
	}

	public static SnmpGet getSnmpGet(String serverAddress, String edition, int port, String community, String oid, long timeOut) {
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
			// String username =
			// bo.GetField("username").get_NativeValue().toString();
			// String passwd =
			// bo.GetField("passwd").get_NativeValue().toString();
			// String authenticationProtocol =
			// bo.GetField("authenticationProtocol").get_NativeValue().toString();
			// String privacyProtocol =
			// bo.GetField("privacyProtocol").get_NativeValue().toString();
			// String privacyPwd =
			// bo.GetField("privacyPwd").get_NativeValue().toString();
			// snmpGet = new SnmpGet(serverAddress, port, community, oid,
			// version, timeOut, username, authenticationProtocol, passwd,
			// privacyProtocol, privacyPwd);
		}
		return snmpGet;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// // createButton(parent, IDialogConstants.OK_ID,
		// IDialogConstants.OK_LABEL,
		// true);
		// createButton(parent, IDialogConstants.CANCEL_ID,
		// IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(240, 200);
	}

}
