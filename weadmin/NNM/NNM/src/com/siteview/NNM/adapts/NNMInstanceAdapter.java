package com.siteview.NNM.adapts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.IWorkbenchAdapter2;

import com.siteview.NNM.modles.Modle;
import com.siteview.NNM.modles.RootNNM;
import com.siteview.NNM.modles.SvgManagementModle;
import com.siteview.NNM.modles.SvgTypeModle;
import com.siteview.NNM.modles.TopoModle;


public class NNMInstanceAdapter implements IWorkbenchAdapter, IWorkbenchAdapter2{

	
	private List<Object> topos = new ArrayList<Object>();
	private List<RootNNM> NNMs= new ArrayList<RootNNM>();
	
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
		if(o instanceof NNMInstance){
			NNMs.clear();
			NNMs.addAll(((NNMInstance) o).getRoots());
			return NNMs.toArray();
		}else if(o instanceof RootNNM){
			topos.clear();
			topos.addAll(((RootNNM) o).getList());
			return topos.toArray();
		}else if(o instanceof SvgManagementModle)
		{
			List<SvgTypeModle> svgtype=new ArrayList<SvgTypeModle>();
			svgtype.add(((SvgManagementModle) o).getSvgtype());
			return svgtype.toArray();
		}else if(o instanceof SvgTypeModle)
		{
			return ((SvgTypeModle) o).getSvgs().toArray();
		}
		return new Object[0];
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLabel(Object o) {
		// TODO Auto-generated method stub
		return ((NNMInstance)o).getName();
	}

	@Override
	public Object getParent(Object o) {
		// TODO Auto-generated method stub
		return null;
	}

}
