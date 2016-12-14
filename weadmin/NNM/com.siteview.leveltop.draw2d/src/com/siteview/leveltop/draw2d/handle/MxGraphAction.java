package com.siteview.leveltop.draw2d.handle;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionDelegate;

import com.siteview.leveltop.draw2d.ui.MxGraghEditor;
import com.siteview.leveltop.draw2d.ui.MxGraghInput;
import com.siteview.utils.entities.TreeEquipmentObject;

public class MxGraphAction  extends ActionDelegate{
	/*
	 * �豸�Ǳ��CNF ACTION����
	 */
	TreeSelection selection;
    public void selectionChanged(IAction action, ISelection selection) {
    	if(selection!=null&&selection instanceof TreeSelection){
			this.selection=(TreeSelection) selection;
		}
    }
    
    private MxGraghInput input = new MxGraghInput();
   
    public void run(IAction ation){
    	if(selection!=null&&!selection.isEmpty()&&selection.getFirstElement() instanceof TreeEquipmentObject){
//    		TreeEquipmentObject equipment = (TreeEquipmentObject)selection.getFirstElement() ;
    		try {
    			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
    			if(page!=null){
    				page.openEditor(input, MxGraghEditor.ID);
    			}
    			
    		} 
    		catch (Exception e) {
    			e.printStackTrace();
    		}
        }
    }
}
