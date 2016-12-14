package com.siteview.leveltop.draw2d.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class MxGraghInput implements IEditorInput {
	
	public MxGraghInput(){
		
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
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
		return "";
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return "";
	}
	
	@Override
	public boolean equals(Object arg0) {
		if(arg0==null||!(arg0 instanceof MxGraghInput)){
			return false;
		}
		return true;
	}

}
