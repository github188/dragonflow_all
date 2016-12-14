package com.siteview.NNM.dialogs;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.siteview.ecc.monitor.nls.EccMessage;
import com.siteview.nnm.data.ConfigDB;

import Siteview.Windows.Forms.MsgBox;

public class TelnetSSHPassword extends Dialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6927031329822192572L;

	public TelnetSSHPassword(Shell parentShell) {
		super(parentShell);
	}

	// private BusinessObject bo;
	private Map<String, String> db;
	Text textname;
	Text textdesc;
	Combo textproto;
	Text textuser;
	Text textpwd;
	Text textprot;
	Text texttimeout;
	// Text texttire;
	Text textcomp;
	Text textlogp;
	Text textpwdp;

	// private TableItem tableitem;
	// private Table table;
	@Override
	protected Point getInitialSize() {
		return new Point(400, 400);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Telnet/SSH");
	}

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
		lablename.setText(EccMessage.get().NAME);
		textname = new Text(com, SWT.BORDER);
		textname.setLayoutData(data);

		GridData data_1 = new GridData();
		data_1.heightHint = 40;
		data_1.widthHint = 250;
		Label labledesc = new Label(com, SWT.NONE);
		labledesc.setText(EccMessage.get().DESCRIPTION);
		textdesc = new Text(com, SWT.BORDER | SWT.WRAP);
		textdesc.setLayoutData(data_1);

		Label lableproto = new Label(com, SWT.NONE);
		lableproto.setText("协议");
		textproto = new Combo(com, SWT.BORDER | SWT.READ_ONLY);
		textproto.setLayoutData(data);
		textproto.add("Telnet");
		textproto.add("SSH");
		textproto.setText("SSH");
		textproto.addModifyListener(new ModifyListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -4046592665553549941L;

			@Override
			public void modifyText(ModifyEvent event) {
				// TODO Auto-generated method stub
				if (textproto.getText().equals("Telnet"))
					textprot.setText("23");
				else
					textprot.setText("22");
			}
		});

		Label lableuser = new Label(com, SWT.NONE);
		lableuser.setText("用户名");
		textuser = new Text(com, SWT.BORDER);
		textuser.setLayoutData(data);

		Label lablepwd = new Label(com, SWT.NONE);
		lablepwd.setText("密码");
		textpwd = new Text(com, SWT.BORDER | SWT.PASSWORD);
		textpwd.setLayoutData(data);

		Label lableprot = new Label(com, SWT.NONE);
		lableprot.setText("端口");
		textprot = new Text(com, SWT.BORDER);
		textprot.setLayoutData(data);
		textprot.setText("22");

		Label labletimeout = new Label(com, SWT.NONE);
		labletimeout.setText("超时");
		texttimeout = new Text(com, SWT.BORDER);
		texttimeout.setLayoutData(data);
		texttimeout.setText("5");

		// Label lablere=new Label(com, SWT.NONE);
		// lablere.setText(AutoScanMessage.get().RETRIES);
		// texttire=new Text(com, SWT.BORDER);
		// texttire.setLayoutData(data);
		// texttire.setText("0");
		//
		Label lablecomp = new Label(com, SWT.NONE);
		lablecomp.setText("重试次数");
		textcomp = new Text(com, SWT.BORDER);
		textcomp.setLayoutData(data);
		// textcomp.setText(":");

		// Label lablelogp=new Label(com, SWT.NONE);
		// lablelogp.setText(AutoScanMessage.get().RETRIES);
		// textlogp=new Text(com, SWT.BORDER);
		// textlogp.setLayoutData(data);
		// textlogp.setText(":");

		// Label lablepwdp=new Label(com, SWT.NONE);
		// lablepwdp.setText(AutoScanMessage.get().RETRIES);
		// textpwdp=new Text(com, SWT.BORDER);
		// textpwdp.setLayoutData(data);
		// textpwdp.setText(":");
		// BackfillData();
		return com;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		// TODO Auto-generated method stub
		if (buttonId == Dialog.OK) {
			if ("".equals(textname.getText().toString())) {
				MsgBox.ShowMessage("名称不能为空");
				return;
			}

			if ("".equals(textuser.getText().toString())) {
				MsgBox.ShowMessage("用户名不能为空");
				return;
			}
			if ("".equals(textpwd.getText().toString())) {
				MsgBox.ShowMessage("密码不能为空");
				return;
			}
			try {
				Connection conn = ConfigDB.getConn();
				String sql = "insert into telnetssh(name,description,protocol,user,pass,port,timeout,retries) values('"
						+ textname.getText() + "','" + textdesc.getText() + "','" + textproto.getText() + "','"
						+ textuser.getText() + "','" + textpwd.getText() + "','" + textprot.getText() + "','"
						+ texttimeout.getText() + "','" + textcomp.getText() + "');";
				ConfigDB.excute(sql, conn);
				db = new HashMap<String, String>();
				db.put("name", textname.getText());
				db.put("description", textdesc.getText());
				db.put("protocol", textproto.getText());
				db.put("user", textuser.getText());
				db.put("pass", textpwd.getText());
				db.put("port", textprot.getText());
				db.put("timeout", texttimeout.getText());
				db.put("retries", textcomp.getText());
				ConfigDB.close(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		super.buttonPressed(buttonId);
	}

	public Map<String, String> getDB() {
		return db;
	}
}
