package com.siteview.NNM.dialogs;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.siteview.ecc.monitor.nls.EccMessage;
import com.siteview.nnm.data.ConfigDB;

public class SNMPV1V2Password extends Dialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Text textname;
	Text textdesc;
	Text textreadcom;
	Text textwcom;
	Text textprot;
	Text texttimeout;
	Text texttire;

	private Map<String, String> db;

	public SNMPV1V2Password(Shell parentShell) {
		super(parentShell);

	}

	@Override
	protected Point getInitialSize() {
		return new Point(400, 350);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("SNMPV1/V2");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite com = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.horizontalSpacing = 30;
		gridLayout.verticalSpacing = 10;

		com.setLayout(gridLayout);
		GridData data = new GridData();
		data.widthHint = 200;

		Label lablename = new Label(com, SWT.NONE);
		lablename.setText(EccMessage.get().NAME);
		textname = new Text(com, SWT.BORDER);
		textname.setLayoutData(data);

		GridData data_1 = new GridData();
		data_1.heightHint = 40;
		data_1.widthHint = 200;
		Label labledesc = new Label(com, SWT.NONE);
		labledesc.setText(EccMessage.get().DESCRIPTION);
		textdesc = new Text(com, SWT.BORDER | SWT.WRAP);
		textdesc.setLayoutData(data_1);

		Label lablereadcom = new Label(com, SWT.NONE);
		lablereadcom.setText("读共同体");
		textreadcom = new Text(com, SWT.BORDER | SWT.PASSWORD);
		textreadcom.setLayoutData(data);
		textreadcom.setText("public");

		Label lablewcom = new Label(com, SWT.NONE);
		lablewcom.setText("写共同体");
		textwcom = new Text(com, SWT.BORDER | SWT.PASSWORD);
		textwcom.setLayoutData(data);
		textwcom.setText("public");

		Label lableprot = new Label(com, SWT.NONE);
		lableprot.setText("SNMP port");
		textprot = new Text(com, SWT.BORDER);
		textprot.setLayoutData(data);
		textprot.setText("161");

		Label labletimeout = new Label(com, SWT.NONE);
		labletimeout.setText("SNMP Time out");
		texttimeout = new Text(com, SWT.BORDER);
		texttimeout.setLayoutData(data);
		texttimeout.setText("5");

		Label lablere = new Label(com, SWT.NONE);
		lablere.setText("SNMP Retries");
		texttire = new Text(com, SWT.BORDER);
		texttire.setLayoutData(data);
		texttire.setText("0");

		return com;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		// TODO Auto-generated method stub
		if (buttonId == Dialog.OK) {
			if ("".equals(textname.getText().toString())) {
				// MsgBox.ShowMessage(AutoScanMessage.get().NAME+AutoScanMessage.get().NOT_NULL);
				return;
			}
			if ("".equals(textreadcom.getText().toString())) {
				// MsgBox.ShowMessage(AutoScanMessage.get().SNMP_READ_COMMUNITY+AutoScanMessage.get().NOT_NULL);
				return;
			}
			if ("".equals(textwcom.getText().toString())) {
				// MsgBox.ShowMessage(AutoScanMessage.get().SNMP_WRITER_COMMUNITY+AutoScanMessage.get().NOT_NULL);
				return;
			}

			try {
				Connection conn = ConfigDB.getConn();
				String sql = "insert into snmpv1v2(name,description,readcomm,writecomm,port,timeout,retries) values('"
						+ textname.getText() + "','" + textdesc.getText() + "','" + textreadcom.getText() + "','"
						+ textwcom.getText() + "','" + textprot.getText() + "','" + texttimeout.getText() + "','"
						+ texttire.getText() + "');";
				ConfigDB.excute(sql, conn);
				db = new HashMap<String, String>();
				db.put("name", textname.getText());
				db.put("description", textdesc.getText());
				db.put("readcomm", textreadcom.getText());
				db.put("writecomm", textwcom.getText());
				db.put("port", textprot.getText());
				db.put("timeout", texttimeout.getText());
				db.put("retries", texttire.getText());
				ConfigDB.close(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		super.buttonPressed(buttonId);
	}

	public Map getDB() {
		return db;
	}

}
