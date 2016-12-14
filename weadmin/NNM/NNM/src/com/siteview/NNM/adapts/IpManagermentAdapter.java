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
import com.siteview.NNM.dialogs.table.ImageKeys;
import com.siteview.NNM.modles.IpManagermentModle;
import com.siteview.NNM.modles.SubnetModle;
import com.siteview.NNM.modles.SvgModle;

public class IpManagermentAdapter  implements IWorkbenchAdapter, IWorkbenchAdapter2{

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
		if(o instanceof IpManagermentModle){
			List<Object> sub=new ArrayList<Object>();
			if(((IpManagermentModle) o).getSub()!=null)
				sub.add(((IpManagermentModle) o).getSub());
			if(((IpManagermentModle) o).getIpmac()!=null)
				sub.add(((IpManagermentModle) o).getIpmac());
			return sub.toArray();
		}
			return new Object[0];
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		// TODO Auto-generated method stub
		ImageDescriptor temp=AbstractUIPlugin.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, ImageKeys.PC_BLUE);
		return temp;
	}

	@Override
	public String getLabel(Object o) {
		if(o instanceof IpManagermentModle)
			return ((IpManagermentModle) o).getName();
		return "";
	}

	@Override
	public Object getParent(Object o) {
		// TODO Auto-generated method stub
		return null;
	}

}
