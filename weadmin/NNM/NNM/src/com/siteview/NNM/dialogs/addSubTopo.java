package com.siteview.NNM.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import Siteview.Windows.Forms.MsgBox;

import com.siteview.topo.TopoMap;

public class addSubTopo extends Dialog {
	public Text text;
	private JsonArray topodata;
	TopoMap subchart;
	public String name;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public addSubTopo(Shell parentShell,JsonArray data,TopoMap subchart) {
		super(parentShell);
		this.topodata=data;
		this.subchart=subchart;
	}
	protected void configureShell(Shell newShell) {
		newShell.setSize(450, 320);
		newShell.setLocation(400, 175);
		newShell.setText("创建拓扑缩略图");
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
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setText("\u540D\u79F0\uFF1A");
		
		text = new Text(container, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button button = createButton(parent, IDialogConstants.OK_ID, "保存",
				true);
		
		createButton(parent, IDialogConstants.CANCEL_ID,
				"取消", true);
	}
	protected void buttonPressed(int buttonId) {
		try{
			if(buttonId==IDialogConstants.OK_ID){
				name=text.getText().trim();
				if(name==null||name.length()==0){
					MsgBox.ShowWarning("必须填写子图名");
					return;
				}
				if(com.siteview.nnm.data.DBManage.Topos.containsKey(name)){
					MsgBox.ShowWarning("子图名重复!");
					return;
				}
				JsonObject jdata= com.siteview.nnm.data.DBManage.createsubtopo(this.topodata, text.getText());
				subchart.setNewgroup(jdata);
				name=text.getText();
			}
			this.close();
		}catch (Exception e) {
		}
	}
	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

}
