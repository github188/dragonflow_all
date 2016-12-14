package com.siteview.ecc.yft.command;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.siteview.ecc.yft.Activator;
import com.siteview.ecc.yft.ui.AlarmIssuedEditor;
import com.siteview.ecc.yft.ui.AlarmIssuedInput;

public class AlarmIssuedCommand implements IHandler {
	private AlarmIssuedInput alarmeventinput=new AlarmIssuedInput();
	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart editor=page.findEditor(alarmeventinput);
		try {
			if(editor==null)
				page.openEditor(alarmeventinput , AlarmIssuedEditor.ID);
			else
				page.activate(editor);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isHandled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub
	}

}
