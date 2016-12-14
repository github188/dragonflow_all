package com.siteview.NNM.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionDelegate;

import com.siteview.NNM.Activator;
import com.siteview.NNM.NNMInstanceData;
import com.siteview.NNM.Editors.NNMainEditorInput;
import com.siteview.NNM.Editors.TopoManage;
import com.siteview.NNM.Views.NNMTreeView;
import com.siteview.NNM.dialogs.DelSubtopoDialog;
import com.siteview.NNM.dialogs.ScanNetwork;
import com.siteview.NNM.modles.SubChartModle;
import com.siteview.NNM.modles.TopoModle;
import com.siteview.ecc.authorization.ItossAuthorizeServiceImpl;
import com.siteview.ecc.authorization.PermissionFactory;
import com.siteview.ecc.constants.Operation;
import com.siteview.ecc.constants.Resource;

import siteview.windows.forms.MsgBox;

public class SubchartRemove extends ActionDelegate{
	public void run(IAction ation) {
		boolean sanright = ItossAuthorizeServiceImpl.getInstance().isPermitted(
				PermissionFactory.createPermission(
						Resource.TOPU_SCAN.getType(),
						Operation.SHOW_NNM_TOPU_SCAN.getOperationString(), "*"));

		// if (ItossAuthorizeServiceImpl.getInstance().isPermitted(
		// PermissionFactory.createPermission(
		// Resource.TOPU_SCAN.getType(),
		// Operation.TOPUSCAN_PERMIT.getOperationString(), "*"))) {
		TreeItem[] items = NNMTreeView.getCNFNNMTreeView().getCommonViewer().getTree().getSelection();
		if (sanright) {
			String text1="";
			if(items.length>0)
				text1=items[0].getText();
			
			DelSubtopoDialog scan = new DelSubtopoDialog(Display.getCurrent().getActiveShell(),text1);
			scan.open();
			TreeItem[] items1 = NNMTreeView.getCNFNNMTreeView()
					.getCommonViewer().getTree().getItems();
			NNMTreeView.getCNFNNMTreeView()
			.getCommonViewer().getTree().setSelection(items1[0].getItems()[0]);	
			RefreshUI();
			
		}else{
			MsgBox.ShowError("提示","无此权限!");
			return;
		}
		// }
	}
	private void RefreshUI() {
		NNMainEditorInput editiput = (NNMainEditorInput) NNMInstanceData
				.getNNMData("editiput");
		IWorkbenchPage page = Activator.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		IEditorPart editor = page.findEditor(editiput);
		page.closeEditor(editor, false);
		editiput.setSubtopo("host");
		try {
			IEditorPart ep = page.openEditor(editiput, TopoManage.ID);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void selectionChanged(IAction action, ISelection selection) {

	}
}
