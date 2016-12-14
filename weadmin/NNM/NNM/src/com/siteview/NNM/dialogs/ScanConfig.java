package com.siteview.NNM.dialogs;

import java.sql.Connection;
import java.sql.ResultSet;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.siteview.nnm.data.ConfigDB;

import siteview.windows.forms.MsgBox;

public class ScanConfig extends Dialog {

 	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Spinner spdepth;
	public Spinner spthreadcount;
	public Spinner spretries;
	public Spinner sptimeout;
	public Spinner spipcount;
	public Text txtport;
	public Text otherport;

	private int depth = 0;
	private int threads = 0;
	private int retries = 0;
	private int timeout = 0;
	private int ipcount = 0;
	private String ports = "";
	private String otherports = "";
	private int nmaprun = 0;
	private String dump = "0";
	private String arpscan = "0";
	private String nbrscan = "0";
	private String vrrpscan = "0";
	private String bgpscan = "0";
	private Button btnCheckButton;
	private Button btnDumb;
	private Button btnwarp;
	private Button btnnbr;
	private Button btnvrrp;
	private Button btnbgp;
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public ScanConfig(Shell parentShell) {
		super(parentShell);
		Connection conn = ConfigDB.getConn();
		String sql = "select depth,threads,retries,timeout,ipcount,ports,otherports,nmaprun,dump,arpscan,vrrpscan,bgpscan,nbrscan from scanconfig";
		ResultSet rs = ConfigDB.query(sql, conn);
		try {
			while (rs.next()) {
				depth = Integer.parseInt(rs.getString("depth"));
				threads = Integer.parseInt(rs.getString("threads"));
				retries = Integer.parseInt(rs.getString("retries"));
				timeout = Integer.parseInt(rs.getString("timeout"));
				ipcount = Integer.parseInt(rs.getString("ipcount"));
				ports = rs.getString("ports");
				otherports = rs.getString("otherports");
				if (otherports == null)
					otherports = "";
				nmaprun = rs.getInt("nmaprun");
				dump = rs.getString("dump");
				arpscan = rs.getString("arpscan");
				vrrpscan = rs.getString("vrrpscan");
				bgpscan = rs.getString("bgpscan");
				nbrscan = rs.getString("nbrscan");
			}
		} catch (Exception e) {

		}
		ConfigDB.close(conn);
	}
	
	protected void configureShell(Shell newShell) {
		newShell.setText("扫描配置");
		super.configureShell(newShell);
	}
	
	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 2;
		GridData data1 = new GridData();
		data1.horizontalAlignment = SWT.LEFT;
		data1.widthHint = 150;

		Composite tComposite = new Composite(container, SWT.NONE);
		tComposite.setLayout(new GridLayout(2, false));
		Label lbdepth = new Label(tComposite, SWT.NONE);
		lbdepth.setText("扫描深度：");
		spdepth = new Spinner(tComposite, SWT.BORDER);
		spdepth.setMaximum(10);
		spdepth.setSelection(depth);
		spdepth.setLayoutData(data1);
		Label lbthread = new Label(tComposite, SWT.NONE);
		lbthread.setText("并行线程数：");
		data1 = new GridData();
		data1.widthHint = 150;
		spthreadcount = new Spinner(tComposite, SWT.BORDER);
		spthreadcount.setMaximum(200);
		spthreadcount.setSelection(threads);
		spthreadcount.setLayoutData(data1);
		Label lbretries = new Label(tComposite, SWT.NONE);
		lbretries.setText("重试次数：");
		data1 = new GridData();
		data1.widthHint = 150;
		spretries = new Spinner(tComposite, SWT.BORDER);
		spretries.setMaximum(10);
		spretries.setSelection(retries);
		spretries.setLayoutData(data1);
		Label lbtimeout = new Label(tComposite, SWT.NONE);
		lbtimeout.setText("超时时间(毫秒)：");
		data1 = new GridData();
		data1.widthHint = 150;
		sptimeout = new Spinner(tComposite, SWT.BORDER);
		sptimeout.setMaximum(60000);
		sptimeout.setSelection(timeout);
		sptimeout.setLayoutData(data1);
		Label lbipcount = new Label(tComposite, SWT.NONE);
		lbipcount.setText("扫描的ip数目：");
		data1 = new GridData();
		data1.widthHint = 150;
		spipcount = new Spinner(tComposite, SWT.BORDER);
		spipcount.setMaximum(500);
		spipcount.setSelection(ipcount);
		spipcount.setLayoutData(data1);
		Label lbport = new Label(tComposite, SWT.NONE);
		lbport.setText("常用端口(默认)：");
		data1 = new GridData();
		data1.widthHint = 250;
		txtport = new Text(tComposite, SWT.BORDER);
		txtport.setText(ports);
		txtport.setEditable(false);
		txtport.setLayoutData(data1);
		Label lbport1 = new Label(tComposite, SWT.NONE);
		lbport1.setText("指定端口：");
		data1 = new GridData();
		data1.widthHint = 250;
		otherport = new Text(tComposite, SWT.BORDER);
		otherport.setText(otherports);
		otherport.setLayoutData(data1);

		Label lbnmap = new Label(tComposite, SWT.NONE);
		lbnmap.setText("资产扫描：");

		btnCheckButton = new Button(tComposite, SWT.CHECK);
		if (nmaprun == 0) {
			btnCheckButton.setSelection(false);
		} else {
			btnCheckButton.setSelection(true);
		}
		btnCheckButton.setText("");
		Label lbdumb = new Label(tComposite, SWT.NONE);
		lbdumb.setText("创建哑设备：");
		btnDumb = new Button(tComposite, SWT.CHECK);
		if (dump.equals("1")) {
			btnDumb.setSelection(true);
		} else {
			btnDumb.setSelection(false);
		}
		btnDumb.setText("");

		Label lbarp = new Label(tComposite, SWT.NONE);
		lbarp.setText("Arp扫描：");
		btnwarp = new Button(tComposite, SWT.CHECK);
		btnwarp.setSelection(false);
		if (arpscan.equals("1")) {
			btnwarp.setSelection(true);
		}
		btnwarp.setText("");
		Label lbNBR = new Label(tComposite, SWT.NONE);
		lbNBR.setText("邻居扫描：");
		btnnbr = new Button(tComposite, SWT.CHECK);
		btnnbr.setSelection(false);
		if (nbrscan.equals("1")) {
			btnnbr.setSelection(true);
		}
		btnnbr.setText("");

		Label lbvrrp = new Label(tComposite, SWT.NONE);
		lbvrrp.setText("vrrp扫描：");
		btnvrrp = new Button(tComposite, SWT.CHECK);
		btnvrrp.setSelection(false);
		if (vrrpscan.equals("1")) {
			btnvrrp.setSelection(true);
		}
		btnvrrp.setText("");

		Label lbbgp = new Label(tComposite, SWT.NONE);
		lbbgp.setText("bgp扫描：");
		btnbgp = new Button(tComposite, SWT.CHECK);
		btnbgp.setSelection(false);
		if (bgpscan.equals("1")) {
			btnbgp.setSelection(true);
		}
		btnbgp.setText("");
		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "确定", true);
		createButton(parent, IDialogConstants.CANCEL_ID, "取消", false);
	}

	public static boolean isNumeric(String str) {
		if (str.matches("\\d*")) {
			return true;
		} else {
			return false;
		}
	}
	
//	if(scan_scales.isEmpty() && scan_seeds.isEmpty()){
//		MsgBox.ShowError("提示","请输入ip范围或扫描种子");
//		return;
//	}
	protected void buttonPressed(int buttonId) {
		try {
			if (buttonId == IDialogConstants.OK_ID) {
				String[] pp = otherport.getText().split("\\,");
				boolean ifalse = false;
				for (String p : pp) {
					if (!isNumeric(p)) {
						ifalse = true;
						break;
					}
				}
				if (ifalse) {
					MsgBox.ShowError("提示", "端口格式不正确!");
					return;
				}
				int nmaprun = 0;
				if (btnCheckButton.getSelection())
					nmaprun = 1;
				int idump = 0;
				if (btnDumb.getSelection())
					idump = 1;
				int iarp = 0;
				if (btnwarp.getSelection())
					iarp = 1;
				int ivrrp = 0;
				if (btnvrrp.getSelection())
					ivrrp = 1;
				int ibgp = 0;
				if (btnbgp.getSelection())
					ibgp = 1;
				int inbr = 0;
				if (btnnbr.getSelection())
					inbr = 1;

				Connection conn = ConfigDB.getConn();
				String sql = "update scanconfig set depth='" + spdepth.getText() + "',threads='"
						+ spthreadcount.getText() + "',retries='" + spretries.getText() + "',timeout='"
						+ sptimeout.getText() + "',ipcount='" + spipcount.getText() + "',ports='" + txtport.getText()
						+ "',otherports='" + otherport.getText() + "',nmaprun=" + nmaprun + ",dump=" + idump
						+ ",arpscan=" + iarp + ",vrrpscan=" + ivrrp + ",bgpscan=" + ibgp + ",nbrscan=" + inbr;
				ConfigDB.excute(sql, conn);
				ConfigDB.close(conn);
			}
		} catch (Exception e) {
		}
		super.buttonPressed(buttonId);
	}
	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(430, 470);
	}

}
