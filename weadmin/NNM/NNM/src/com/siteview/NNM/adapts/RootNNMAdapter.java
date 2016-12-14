package com.siteview.NNM.adapts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.IWorkbenchAdapter2;
import org.eclipse.ui.plugin.AbstractUIPlugin;








import siteview.windows.forms.Branding;

import com.siteview.NNM.Activator;
import com.siteview.NNM.modles.RootNNM;
import com.siteview.NNM.modles.SvgManagementModle;
import com.siteview.NNM.modles.SvgTypeModle;
import com.siteview.NNM.modles.TopoModle;
import com.siteview.NNM.resource.ImageResource;



public class RootNNMAdapter implements IWorkbenchAdapter, IWorkbenchAdapter2{

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
		if(o instanceof RootNNM){
			return ((RootNNM) o).getList().toArray();
		}
		return new Object[0];
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		// modify by ff 20150331 统一logo图标
		ImageDescriptor temp = new ImageDescriptor() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public ImageData getImageData() {
				return Branding.getDefaultIcon().getImageData();
			}
		};
//		ImageDescriptor temp=AbstractUIPlugin.imageDescriptorFromPlugin(
//				Activator.PLUGIN_ID, ImageResource.SiteViewNNM);
		return temp;
	}

	@Override
	public String getLabel(Object o) {
		// TODO Auto-generated method stub
		if(o instanceof RootNNM)
		 return ((RootNNM)o).getName();
		return "";
	}

	@Override
	public Object getParent(Object o) {
		// TODO Auto-generated method stub
//		return NNMInstance.getinstance();
		return null;
	}

}
