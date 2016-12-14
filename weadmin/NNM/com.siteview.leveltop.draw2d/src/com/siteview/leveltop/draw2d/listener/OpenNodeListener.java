package com.siteview.leveltop.draw2d.listener;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import com.siteview.ecc.main.ui.cnftree.MonitorTreeViews;
import com.siteview.leveltop.draw2d.nls.Message;
import com.siteview.utils.entities.TreeEquipmentObject;

import siteview.windows.forms.MsgBox;

public class OpenNodeListener extends SelectionAdapter{

	private static final long serialVersionUID = 1L;
	
	@Override
	public void widgetSelected(SelectionEvent e) {
		IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		boolean isOpen = false;
		if(editor.isDirty()){
			if(!MsgBox.ShowConfirm(Message.get().RESOURCE_TOPO_DIRY)){
				isOpen = true;
			}
		}
		else{
			isOpen = true;
		}
		if(isOpen){
			MonitorTreeViews treeViewer = MonitorTreeViews.getMonitorTreeViews();
			IStructuredSelection iselection = (IStructuredSelection) treeViewer.getCommonViewer().getSelection();
			if(iselection!=null){
				Object obj = iselection.getFirstElement();
				if(obj!=null&&obj instanceof TreeEquipmentObject){
					TreeEquipmentObject tro = (TreeEquipmentObject)obj;
					treeViewer.openOrActiveMachineMainEditor(tro);
				}
			}
//			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeEditor(editor, false);
		}
	}

}
