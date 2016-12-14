package com.siteview.NNM.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.siteview.NNM.Views.NNMTreeView;
import com.siteview.NNM.modles.SubChartModle;
import com.siteview.NNM.modles.TopoModle;
import com.siteview.nnm.data.DBManage;
import com.siteview.nnm.data.FlowDataManager;
import com.siteview.nnm.data.model.EdgeFlow;
import com.siteview.nnm.data.model.svEdge;

import siteview.windows.forms.MsgBox;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.layout.GridData;

public class DelSubtopoDialog extends Dialog {
	private Text text;
	String subname="";

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public DelSubtopoDialog(Shell parentShell) {
		super(parentShell);
	}
	public DelSubtopoDialog(Shell parentShell,String suname) {
		super(parentShell);
		this.subname=suname;
	}
	protected void configureShell(Shell newShell) {
		//newShell.setSize(450, 320);
		//newShell.setLocation(400, 175);
		newShell.setText("删除子图");
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
		gridLayout.numColumns = 3;
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("子图名称：");
		
		text = new Text(container, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text.setText(subname);

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
	protected void buttonPressed(int buttonId) {
		try{
			if(buttonId==IDialogConstants.OK_ID){
				String subname =text.getText().trim();
				if(subname.isEmpty() || !DBManage.Topos.containsKey(subname)){
					MsgBox.ShowError("提示", "子图名称不正确!");
					return;
				}
				if(!DBManage.subhosts.contains(subname)){
					MsgBox.ShowError("提示", "此子图不能直接删除，主图撤销子图!");
					return;
				}
				DBManage.clearsubdata(subname);
				TreeItem[] items = NNMTreeView.getCNFNNMTreeView().getCommonViewer().getTree().getItems();
				List<SubChartModle> list=((TopoModle) items[0].getItems()[0].getData()).getList();
				for (SubChartModle sub1 : list) {
					if (sub1.getName().equals(subname)) {
						((TopoModle) items[0].getItems()[0].getData()).getList().remove(sub1);
						break;
					}
				}
				NNMTreeView.getCNFNNMTreeView().getCommonViewer().refresh();
			}
			super.buttonPressed(buttonId);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 200);
	}

}
