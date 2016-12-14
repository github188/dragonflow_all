package com.siteview.NNM.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;

import siteview.windows.forms.MsgBox;

import com.siteview.nnm.data.model.svNode;
import com.siteview.topology.model.SnmpPara;
import com.siteview.topology.snmp.snmpTools;

public class PrivateCommDialog extends Dialog {
	private Text txtPrivate;
	svNode entityobj;
	String portindex;
	boolean openport = true;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public PrivateCommDialog(Shell parentShell, svNode entityobj,
			String portindex, boolean openport) {
		super(parentShell);
		this.entityobj = entityobj;
		this.portindex = portindex;
		this.openport = openport;
	}

	protected void configureShell(Shell newShell) {
		// newShell.setSize(450, 320);
		// newShell.setLocation(400, 175);
		newShell.setText("设置共同体名");
		super.configureShell(newShell);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 2;
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setText("请输入共同体名称：");
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));

		txtPrivate = new Text(container, SWT.BORDER);
		txtPrivate.setText("private");
		txtPrivate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		return container;
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

	protected void buttonPressed(int buttonId) {
		try {
			if (buttonId == IDialogConstants.OK_ID) {
				String ip = this.entityobj.getLocalip();
				SnmpPara spr = new SnmpPara(ip, txtPrivate.getText().trim(),
						2000, 2);
				spr.setSnmpver(2);
				boolean rr = false;
				if (this.openport) {
					rr = snmpTools.setvalue(spr, "1.3.6.1.2.1.2.2.1.7."
							+ portindex, "1");
				} else {
					rr = snmpTools.setvalue(spr, "1.3.6.1.2.1.2.2.1.7."
							+ portindex, "2");
				}
				if(!rr){
					MsgBox.ShowError("提示", "修改接口状态失败!");
					return;
				}
			}
			this.close();
		} catch (Exception e) {
		}
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(400, 200);
	}

}
