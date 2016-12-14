package com.siteview.NNM.Handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.widgets.Display;

import com.siteview.NNM.dialogs.MibSTable;

public class DeviceInfoSelectHandler extends AbstractHandler implements IHandler {
//	private DeviceInfoSelectInput deviveInfoSelect = new DeviceInfoSelectInput();

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// IWorkbenchPage page =
		// Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();

		MibSTable mibSTable = new MibSTable(Display.getCurrent().getActiveShell());
		mibSTable.open();
		return null;
	}
}
