package com.siteview.NNM.adapts;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.IWorkbenchAdapter2;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.siteview.NNM.Activator;
import com.siteview.NNM.dialogs.table.ImageKeys;
import com.siteview.NNM.modles.SvgModle;
import com.siteview.NNM.resource.ImageResource;

public class SvgAdapter implements IWorkbenchAdapter, IWorkbenchAdapter2{

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
		return new Object[0];
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		// TODO Auto-generated method stub
		String name=getLabel(object);
		if(name.startsWith("三层交换机")){
			name=ImageKeys.SWITCHROUTER_BLUE;
		}else if(name.startsWith("二层交换机")){
			name=ImageKeys.SWITCH_BLUE;
		}else if(name.startsWith("路由")){
			name=ImageKeys.ROUTER_BLUE;
		}else if(name.startsWith("防火墙")){
			name=ImageKeys.FIREWALL_BLUE;
		}else if(name.startsWith("服务器")){
			name=ImageKeys.SERVER_BLUE;
		}else if(name.startsWith("pc终端")){
			name=ImageKeys.PC_BLUE;
		}else if(name.startsWith("其他")){
			name=ImageKeys.OTHER_BLUE;
		}
		ImageDescriptor temp=AbstractUIPlugin.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, name);
		return temp;
	}

	@Override
	public String getLabel(Object o) {
		// TODO Auto-generated method stub
		if(o instanceof SvgModle)
			return ((SvgModle) o).getName();
		return "";
	}

	@Override
	public Object getParent(Object o) {
		// TODO Auto-generated method stub
		return null;
	}

}
