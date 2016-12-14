package com.siteview.NNM.Editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IWorkbenchPage;

public class NNMainEditorInput implements IEditorInput{
	Object treeselection;
	String subtopo;
	
	TreeViewer treeviewer;
	public TreeViewer getTreeviewer() {
		return treeviewer;
	}

	public void setTreeviewer(TreeViewer treeviewer) {
		this.treeviewer = treeviewer;
	}



	

	public String getSubtopo() {
		return subtopo;
	}

	public void setSubtopo(String subtopo) {
		this.subtopo = subtopo;
	}

	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	@Override
	public String getName() {
		return "拓扑图管理";
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return "";
	}
	public Object getTreeselection() {
		return treeselection;
	}

	public void setTreeselection(Object treeselection) {
		this.treeselection = treeselection;
	}
}
