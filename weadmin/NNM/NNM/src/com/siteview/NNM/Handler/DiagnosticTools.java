package com.siteview.NNM.Handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

import com.siteview.NNM.dialogs.DiagnosticToolsDialog;

public class DiagnosticTools extends AbstractHandler implements IHandler  {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		DiagnosticToolsDialog  dtd = new DiagnosticToolsDialog(null);
		dtd.open();
		return null;
	}


}
