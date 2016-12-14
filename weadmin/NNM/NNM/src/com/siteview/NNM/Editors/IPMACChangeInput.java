package com.siteview.NNM.Editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.siteview.NNM.nls.NNMMessage;

import Siteview.Windows.Forms.ConnectionBroker;
import siteview.windows.forms.Branding;

public class IPMACChangeInput implements IEditorInput{

	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return NNMMessage.getvalue("IpMac", Branding.sysConfigRun.getConvertLocale());
	}

	@Override
	public IPersistableElement getPersistable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getToolTipText() {
		// TODO Auto-generated method stub
		return NNMMessage.getvalue("IpMac", Branding.sysConfigRun.getConvertLocale());
	}

}
