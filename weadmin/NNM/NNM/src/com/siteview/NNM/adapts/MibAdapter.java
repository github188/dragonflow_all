package com.siteview.NNM.adapts;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.IWorkbenchAdapter2;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.siteview.NNM.Activator;
import com.siteview.NNM.dialogs.table.ImageKeys;
import com.siteview.mib.model.MibModel;

public class MibAdapter implements IWorkbenchAdapter, IWorkbenchAdapter2 {

	@Override
	public RGB getForeground(Object element) {
		return null;
	}

	@Override
	public RGB getBackground(Object element) {
		return null;
	}

	@Override
	public FontData getFont(Object element) {
		return null;
	}

	@Override
	public Object[] getChildren(Object o) {
		if (o instanceof MibModel)
			return ((MibModel) o).getFilemib().toArray();
		return new Object[0];
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		ImageDescriptor temp = AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, ImageKeys.MIB);
		return temp;
	}

	@Override
	public String getLabel(Object o) {
		if (o instanceof MibModel)
			return ((MibModel) o).getName();
		return "";
	}

	@Override
	public Object getParent(Object o) {
		return null;
	}

}
