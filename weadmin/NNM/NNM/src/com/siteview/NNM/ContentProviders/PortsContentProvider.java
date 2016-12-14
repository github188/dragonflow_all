package com.siteview.NNM.ContentProviders;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import java.util.List;
public class PortsContentProvider implements IStructuredContentProvider{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object[] getElements(Object inputElement) {
		// TODO Auto-generated method stub
		   if(inputElement instanceof List){
               return ((List)inputElement).toArray();
           }else{
               return new Object[0];
           }
	}

}
