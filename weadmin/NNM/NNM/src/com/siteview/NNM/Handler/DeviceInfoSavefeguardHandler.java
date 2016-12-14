package com.siteview.NNM.Handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

import com.siteview.NNM.dialogs.DeviceInfoSavefeguardDialogs;

public class DeviceInfoSavefeguardHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		DeviceInfoSavefeguardDialogs  device = new DeviceInfoSavefeguardDialogs(null);
		device.open();
		return null;
	}
	
	

}
