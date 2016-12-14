package com.siteview.NNM.Handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.widgets.Display;

import siteview.windows.forms.MsgBox;

import com.siteview.NNM.dialogs.ScanNetwork;
import com.siteview.ecc.authorization.ItossAuthorizeServiceImpl;
import com.siteview.ecc.authorization.PermissionFactory;
import com.siteview.ecc.constants.Operation;
import com.siteview.ecc.constants.Resource;

public class TopoScanCommand extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		boolean sanright = ItossAuthorizeServiceImpl.getInstance().isPermitted(
				PermissionFactory.createPermission(
						Resource.TOPU_SCAN.getType(),
						Operation.SHOW_NNM_TOPU_SCAN.getOperationString(), "*"));
		if (sanright) {
			ScanNetwork network = new ScanNetwork(Display.getCurrent()
					.getActiveShell());
			network.open();
		}else{
			MsgBox.ShowError("提示","无此权限!");
			
		}
		return null;
	}

}
