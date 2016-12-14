package com.siteview.NNM.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;

import com.siteview.topology.model.SnmpPara;
import com.siteview.topology.snmp.snmpTools;

import siteview.windows.forms.MsgBox;

public class CpuMemtest extends Dialog {
	private Text textIP;
	private Text txtPublic;
	private Text textoid;
	private String ip;
	private String comm;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public CpuMemtest(Shell parentShell,String ip,String comm) {
		super(parentShell);
		this.ip=ip;
		this.comm=comm;
	}
	protected void configureShell(Shell newShell) {
		// newShell.setSize(450, 320);
		// newShell.setLocation(400, 175);
		newShell.setText("设备oid取值测试");
		super.configureShell(newShell);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(2, false));
		
		Label lblNewLabel_3 = new Label(container, SWT.NONE);
		
		Label lblNewLabel_4 = new Label(container, SWT.NONE);
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setText("IP地址：");
		
		textIP = new Text(container, SWT.BORDER);
		textIP.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textIP.setText(ip);
		Label lblNewLabel_1 = new Label(container, SWT.NONE);
		lblNewLabel_1.setText("共同体：");
		
		txtPublic = new Text(container, SWT.BORDER);
		txtPublic.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtPublic.setText(comm);
		Label lblNewLabel_2 = new Label(container, SWT.NONE);
		lblNewLabel_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_2.setText("oid值：");
		
		textoid = new Text(container, SWT.BORDER);
		textoid.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Button btnNewButton = new Button(container, SWT.NONE);
		btnNewButton.setText("测试");
		btnNewButton.addListener(SWT.MouseUp, new Listener() {
			@Override
			public void handleEvent(Event event) {
				SnmpPara para = new SnmpPara(textIP.getText().trim(), txtPublic
						.getText().trim(), 5000, 2, 2);
				String oidStr = textoid.getText();
//				String sysOid = snmpTools.getNextValues(para, oidStr);
//				para=new SnmpPara(textIP.getText().trim(), txtPublic
//						.getText().trim(), 5000, 2, 1);
//				if(sysOid==null)
//				sysOid=snmpTools.getNextValues(para, oidStr);
			 String sysOid = snmpTools.getValue(para, oidStr);
				if(sysOid==null) sysOid="";
				if(sysOid.isEmpty()){
					 MsgBox.ShowMessage("未取到值!确认添加相关索引号？");
				}else{
				    MsgBox.ShowMessage("oid值: "+sysOid);
				}
			}

		});
		
		return container;
	}

	/**
	 * Create contents of the button bar.
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
		return new Point(300, 240);
	}

}
