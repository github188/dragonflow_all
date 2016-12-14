package com.siteview.NNM.ContentProviders;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.siteview.NNM.modles.DPort;
import com.siteview.NNM.modles.UpPort;


public class PortsLabelProvider extends LabelProvider implements ITableLabelProvider{

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		if (element instanceof DPort){
			DPort p = (DPort)element;
            if(columnIndex == 0){
                return p.getID();
            }else if(columnIndex == 1){
                return p.getPType();
            }else if (columnIndex == 2){
                return p.getpAlias();
            }else if (columnIndex ==3){
                return p.getPMac();
            }else if (columnIndex == 4){
                return p.getPDesc();
            }else if (columnIndex == 5){
                return p.getPIP();
            }
        }else if(element instanceof UpPort){
        	UpPort p = (UpPort)element;
        	if(columnIndex == 0){
                return p.getIp();
            }else if(columnIndex == 1){
                return p.getName();
            }else if (columnIndex ==2){
                return p.getPort();
            }
        }
        return null;
	}

}
