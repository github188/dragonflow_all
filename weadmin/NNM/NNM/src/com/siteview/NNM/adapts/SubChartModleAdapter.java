package com.siteview.NNM.adapts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.IWorkbenchAdapter2;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.siteview.NNM.Activator;
import com.siteview.NNM.modles.SubChartModle;
import com.siteview.NNM.resource.ImageResource;

public class SubChartModleAdapter implements IWorkbenchAdapter, IWorkbenchAdapter2{

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
		if(o instanceof SubChartModle){
			return ((SubChartModle) o).getList().toArray();
		}
		return new Object[0];
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		// TODO Auto-generated method stub
		ImageDescriptor temp=AbstractUIPlugin.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, ImageResource.subchart);
		return temp;
	}

	@Override
	public String getLabel(Object o) {
		// TODO Auto-generated method stub
		 return ((SubChartModle)o).getName();
	}

	@Override
	public Object getParent(Object o) {
		// TODO Auto-generated method stub
		return null;
	}

}
