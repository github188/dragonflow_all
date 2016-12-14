package com.siteview.NNM.Handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.widgets.BrowserUtil;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import com.siteview.NNM.Activator;
import com.siteview.NNM.Editors.TopNetWorkEditor;
import com.siteview.NNM.Editors.TopNetWorkEditorInput;

public class TopNetWorkCommand extends AbstractHandler implements IHandler{
	TopNetWorkEditorInput input=new TopNetWorkEditorInput();

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		try {
			IWorkbenchPage page = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IEditorPart editor = page.findEditor(input);
			if (editor == null) {
				page.openEditor(input, TopNetWorkEditor.ID);
			} else {
				page.activate(editor);
			}
		}  catch (PartInitException e1) {
			e1.printStackTrace();
		}
		return null;
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
