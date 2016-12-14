package com.siteview.NNM.ContentProviders;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.Saveable;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonContentProvider;
import org.eclipse.ui.navigator.SaveablesProvider;

public class NNMTreeContentProvider extends SaveablesProvider implements ITreeContentProvider ,IAdaptable{
	
	
	protected IWorkbenchAdapter getAdapter(Object element) {
		IWorkbenchAdapter adapter = null;
		if (element instanceof IAdaptable)
			adapter = (IWorkbenchAdapter) ((IAdaptable) element)
					.getAdapter(IWorkbenchAdapter.class);
		if (element != null && adapter == null)
			adapter = (IWorkbenchAdapter) Platform.getAdapterManager()
					.loadAdapter(element, IWorkbenchAdapter.class.getName());
		return adapter;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		// TODO Auto-generated method stub
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		// TODO Auto-generated method stub
		IWorkbenchAdapter adapter = getAdapter(parentElement);
		if (adapter != null) {
			return adapter.getChildren(parentElement);
		}
		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		IWorkbenchAdapter adapter = getAdapter(element);
		if (adapter != null) {
			return adapter.getParent(element);
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		// TODO Auto-generated method stub
		try
		{
		return getChildren(element).length > 0;
		}catch(Exception e)
		{
			return false;
		}
	}

	@Override
	public Saveable[] getSaveables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] getElements(Saveable saveable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Saveable getSaveable(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

}
