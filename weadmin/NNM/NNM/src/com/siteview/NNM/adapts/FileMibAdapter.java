package com.siteview.NNM.adapts;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.IWorkbenchAdapter2;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.siteview.NNM.Activator;
import com.siteview.NNM.dialogs.table.ImageKeys;
import com.siteview.mib.model.FileMibModel;

public class FileMibAdapter implements IWorkbenchAdapter, IWorkbenchAdapter2 {

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
		if (o instanceof FileMibModel)
			return ((FileMibModel) o).getOb().toArray();
		return new Object[0];
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		// TODO Auto-generated method stub
		String image = "";
		if (object instanceof FileMibModel) {
			String type = ((FileMibModel) object).getType();
			if (type.equals("file")) {
				image = ImageKeys.FILEMIB;
			} else if (type.equals("table")) {
				image = ImageKeys.TABLEMIB;
			} else if (type.equals("tables")) {
				image = ImageKeys.TABLESMIB;
			} else {
				image = ImageKeys.INFOMIB;
			}
		}
		ImageDescriptor temp = AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, image);
		return temp;
	}

	@Override
	public String getLabel(Object o) {
		if (o instanceof FileMibModel)
			return ((FileMibModel) o).getName() + "(" + ((FileMibModel) o).getEndnum() + ")";
		return "";
	}

	@Override
	public Object getParent(Object o) {
		return null;
	}

}
