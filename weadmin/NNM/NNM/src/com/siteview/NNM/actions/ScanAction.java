package com.siteview.NNM.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.ActionDelegate;

import siteview.windows.forms.MsgBox;

import com.siteview.NNM.dialogs.ScanNetwork;
import com.siteview.ecc.authorization.ItossAuthorizeServiceImpl;
import com.siteview.ecc.authorization.PermissionFactory;
import com.siteview.ecc.constants.Operation;
import com.siteview.ecc.constants.Resource;

public class ScanAction extends ActionDelegate {
	public void run(IAction ation) {
		boolean sanright = ItossAuthorizeServiceImpl.getInstance().isPermitted(
				PermissionFactory.createPermission(
						Resource.TOPU_SCAN.getType(),
						Operation.SHOW_NNM_TOPU_SCAN.getOperationString(), "*"));

		// if (ItossAuthorizeServiceImpl.getInstance().isPermitted(
		// PermissionFactory.createPermission(
		// Resource.TOPU_SCAN.getType(),
		// Operation.TOPUSCAN_PERMIT.getOperationString(), "*"))) {
		if (sanright) {
			ScanNetwork scan = new ScanNetwork(Display.getCurrent().getActiveShell());
			scan.open();
		}else{
			MsgBox.ShowError("提示","无此权限!");
			return;
		}
		// }
	}

	public void selectionChanged(IAction action, ISelection selection) {

	}
}
