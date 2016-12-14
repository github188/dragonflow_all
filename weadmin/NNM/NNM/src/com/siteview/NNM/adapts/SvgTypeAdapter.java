package com.siteview.NNM.adapts;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.IWorkbenchAdapter2;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.siteview.NNM.Activator;
import com.siteview.NNM.dialogs.table.ImageKeys;
import com.siteview.NNM.modles.SvgTypeModle;

public class SvgTypeAdapter implements IWorkbenchAdapter, IWorkbenchAdapter2{

	@Override
	public RGB getForeground(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RGB getBackground(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FontData getFont(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] getChildren(Object o) {
		// TODO Auto-generated method stub
		if(o instanceof SvgTypeModle)
			return ((SvgTypeModle) o).getSvgs().toArray();
		return new Object[0];
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		// TODO Auto-generated method stub
		ImageDescriptor temp=AbstractUIPlugin.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, ImageKeys.HUB_BLUE);
		return temp;
	}

	@Override
	public String getLabel(Object o) {
		// TODO Auto-generated method stub
		if(o instanceof SvgTypeModle)
			return ((SvgTypeModle) o).getName();
		return null;
	}

	@Override
	public Object getParent(Object o) {
		// TODO Auto-generated method stub
		return null;
	}

}
