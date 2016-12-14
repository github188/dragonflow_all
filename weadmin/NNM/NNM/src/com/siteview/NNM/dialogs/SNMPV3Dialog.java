package com.siteview.NNM.dialogs;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.siteview.nnm.data.ConfigDB;

public class SNMPV3Dialog extends Dialog {

	Map<String, String> db = null;
	/**
	 * 
	 */
	private static final long serialVersionUID = -2587428874902769896L;

	public SNMPV3Dialog(Shell parentShell) {
		super(parentShell);

	}

	@Override
	protected Point getInitialSize() {
		return new Point(430, 450);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("SNMPV3");
	}

	Text textname;
	Text textdesc;
	Text textuser;
	Text textpwd;
	Text textprivacy;
	Text textprot;
	Text texttimeout;
	Text texttire;
	Text protocolText;
	Combo combo;
	Combo combo_1;

	// Text authPwdText;
	// Text protocolEncryptText;
	// Text encryptPwdText;
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite com = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.horizontalSpacing = 30;
		gridLayout.verticalSpacing = 10;

		com.setLayout(gridLayout);
		GridData data = new GridData();
		data.widthHint = 250;

		Label lablename = new Label(com, SWT.NONE);
		lablename.setText("名称");
		textname = new Text(com, SWT.BORDER);
		textname.setLayoutData(data);

		GridData data_1 = new GridData();
		data_1.heightHint = 40;
		data_1.widthHint = 250;
		Label labledesc = new Label(com, SWT.NONE);
		labledesc.setText("描述");
		textdesc = new Text(com, SWT.BORDER | SWT.WRAP);
		textdesc.setLayoutData(data_1);

		Label verify = new Label(com, SWT.NONE);
		verify.setText("身份验证协议");
		combo = new Combo(com, SWT.NONE | SWT.READ_ONLY);
		combo.add("MD5");
		combo.add("SHA");
		combo.add("");
		combo.select(0);
		combo.setLayoutData(data);

		Label lableuser = new Label(com, SWT.NONE);
		lableuser.setText("用户名");
		textuser = new Text(com, SWT.BORDER);
		textuser.setLayoutData(data);

		Label lablecon = new Label(com, SWT.NONE);
		lablecon.setText("密码");
		textpwd = new Text(com, SWT.BORDER);
		textpwd.setLayoutData(data);

		Label verify_1 = new Label(com, SWT.NONE);
		verify_1.setText("Privacy Protocol");
		combo_1 = new Combo(com, SWT.NONE | SWT.READ_ONLY);
		combo_1.add("DES");
		combo_1.add("AES128");
		combo_1.add("AES192");
		combo_1.add("AES256");
		combo_1.add("");
		combo_1.select(0);
		combo_1.setLayoutData(data);

		Label privacy_pwd = new Label(com, SWT.NONE);
		privacy_pwd.setText("Privacy PassWord");
		textprivacy = new Text(com, SWT.BORDER);
		textprivacy.setLayoutData(data);

		Label lableprot = new Label(com, SWT.NONE);
		lableprot.setText("端口");
		textprot = new Text(com, SWT.BORDER);
		textprot.setLayoutData(data);
		textprot.setText("161");

		Label labletimeout = new Label(com, SWT.NONE);
		labletimeout.setText("超时");
		texttimeout = new Text(com, SWT.BORDER);
		texttimeout.setLayoutData(data);
		texttimeout.setText("5");

		Label lablere = new Label(com, SWT.NONE);
		lablere.setText("SNMP重试");
		lablere.setVisible(false);
		texttire = new Text(com, SWT.BORDER);
		texttire.setLayoutData(data);
		texttire.setText("0");
		texttire.setVisible(false);
		return com;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == Dialog.OK) {
			if ("".equals(textname.getText().toString())) {

				return;
			}
			if ("".equals(textuser.getText().trim())) {
				return;
			}
			try {
				Connection conn = ConfigDB.getConn();
				String sql = "insert into snmpv3(name,description,authorization,username,password,privacy,privacypass,port,timeout,retries) values('"
						+ textname.getText() + "','" + textdesc.getText() + "','" + combo.getText() + "','"
						+ textuser.getText() + "','" + textpwd.getText() + "','" + combo_1.getText() + "','"
						+ textprivacy.getText() + "','" + textprot.getText() + "','" + texttimeout.getText() + "','"
						+ texttire.getText() + "');";
				ConfigDB.excute(sql, conn);
				db = new HashMap<String, String>();
				db.put("name", textname.getText());
				db.put("description", textdesc.getText());
				db.put("authorization", combo.getText());
				db.put("username", textuser.getText());
				db.put("password", textpwd.getText());
				db.put("privacy", combo_1.getText());
				db.put("privacypass", textprivacy.getText());
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
