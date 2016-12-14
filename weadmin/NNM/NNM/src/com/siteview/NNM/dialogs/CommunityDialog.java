package com.siteview.NNM.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.siteview.NNM.modles.SnmpModle;

public class CommunityDialog extends Dialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Text comtext;
	private Combo autext;
	private Text usertext;
	private Text pwdtext;
	private Button v1b;
	private Button v3b;
	private SnmpModle snmp;
	private Text prottext;
	private Combo prautext;
	private Text prpwdtext;

	public CommunityDialog(Shell parentShell, SnmpModle snmp) {
		super(parentShell);
		this.snmp = snmp;
	}

	protected void configureShell(Shell newShell) {
		newShell.setText("SNMP配置");
		super.configureShell(newShell);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(300, 350);
	}

	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout taw = new GridLayout(2, false);
		// taw.numColumns=2;
		container.setLayout(taw);
		v1b = new Button(container, SWT.RADIO);
		v1b.setText("SNMP V1/V2");
		v3b = new Button(container, SWT.RADIO);
		v3b.setText("SNMP V3");
		if (snmp.getType() == 1)
			v1b.setSelection(true);
		else
			v3b.setSelection(true);

		Label comlb = new Label(container, SWT.NONE);
		comlb.setText("共同体");
		comtext = new Text(container, SWT.BORDER);
		comtext.setText(snmp.getCommuntity());
		comtext.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Label protlb = new Label(container, SWT.NONE);
		protlb.setText("端 口");
		prottext = new Text(container, SWT.BORDER);
		prottext.setText(snmp.getPort() + "");
		prottext.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label aulb = new Label(container, SWT.NONE);
		aulb.setText("验证算法");
		autext = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		autext.setText(snmp.getAuth());
		autext.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		autext.add("MD5");
		autext.add("SHA");
		autext.add("无");

		Label userlb = new Label(container, SWT.NONE);
		userlb.setText("用户名");
		usertext = new Text(container, SWT.BORDER);
		usertext.setText(snmp.getUser());
		usertext.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label pwdlb = new Label(container, SWT.NONE);
		pwdlb.setText("密 码");
		pwdtext = new Text(container, SWT.BORDER | SWT.PASSWORD);
		pwdtext.setText(snmp.getPwd());
		pwdtext.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		v1b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				changeButton();
			}
		});
		v3b.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				changeButton();
			}
		});
		Label praulb = new Label(container, SWT.NONE);
		praulb.setText("私密算法");
		prautext = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		prautext.setText(snmp.getPrau());
		prautext.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		prautext.add("DES");
		prautext.add("AES128");
		prautext.add("AES192");
		prautext.add("AES256");
		Label prpwdlb = new Label(container, SWT.NONE);
		prpwdlb.setText("私密算法");
		prpwdtext = new Text(container, SWT.BORDER | SWT.PASSWORD);
		prpwdtext.setText(snmp.getPrpwd());
		prpwdtext.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		changeButton();
		return container;
	}

	public void changeButton() {
		if (v1b.getSelection()) {
			comtext.setEnabled(true);
			autext.setEnabled(false);
			usertext.setEnabled(false);
			pwdtext.setEnabled(false);
			prautext.setEnabled(false);
			prpwdtext.setEnabled(false);
		} else {
			comtext.setEnabled(false);
			autext.setEnabled(true);
			usertext.setEnabled(true);
			pwdtext.setEnabled(true);
			prautext.setEnabled(true);
			prpwdtext.setEnabled(true);
		}
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "确定", true);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == Dialog.OK) {
			snmp.setPort(Integer.parseInt(prottext.getText()));
			if (v1b.getSelection()) {
				snmp.setType(1);
				snmp.setCommuntity(comtext.getText());
			} else {
				snmp.setType(3);
				snmp.setAuth(autext.getText());
				snmp.setUser(usertext.getText());
				snmp.setPwd(pwdtext.getText());
				snmp.setPrau(prautext.getText());
				snmp.setPrpwd(prpwdtext.getText());
			}
		}
		super.buttonPressed(buttonId);
	}
}
