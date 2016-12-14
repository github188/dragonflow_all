package com.siteview.NNM;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.siteview.NNM.Editors.NNMainEditorInput;
import com.siteview.NNM.Views.NNMTreeView;

import siteview.IPerspectiveEventExtension;

public class NNMPerspectiveActive implements IPerspectiveEventExtension {

	public NNMPerspectiveActive() {
	}

	@Override
	public void changePerspective(String changeID) {

	}

	@Override
	public void activePerspective() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if(page!=null){
			NNMTreeView view = (NNMTreeView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViews()[0];
			NNMainEditorInput sv = view.sv;
			IEditorPart editor = page.findEditor(sv);
			if (editor!=null) {
				page.activate(editor);
			}
			else{
				try {
					page.openEditor(sv, "TopoManage");
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

}
