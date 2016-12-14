package com.siteview.leveltop.draw2d.handle;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.siteview.leveltop.draw2d.ui.MxGraghEditor;
import com.siteview.leveltop.draw2d.ui.MxGraghInput;


public class MxGraphHandler extends AbstractHandler{
	
	private MxGraghInput input = new MxGraghInput();

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if(page!=null){
				page.openEditor(input, MxGraghEditor.ID);
			}
			
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
