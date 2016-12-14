package com.siteview.NNM.Editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.siteview.NNM.nls.NNMMessage;
import com.siteview.mib.model.FileMibModel;
import com.siteview.mib.model.MibModel;

import Siteview.Windows.Forms.ConnectionBroker;
import siteview.windows.forms.Branding;


public class MIBBrowseInput implements IEditorInput{
	private FileMibModel filemib;
	private MibModel mib;
	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	@Override
	public String getName() {
		return NNMMessage.getvalue("MIBBrowse", Branding.sysConfigRun.getConvertLocale());
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		// TODO Auto-generated method stub
		return NNMMessage.getvalue("MIBBrowse", Branding.sysConfigRun.getConvertLocale());
	}

	public FileMibModel getFilemib() {
		return filemib;
	}

	public void setFilemib(FileMibModel filemib) {
		this.filemib = filemib;
	}

	public MibModel getMib() {
		return mib;
	}

	public void setMib(MibModel mib) {
		this.mib = mib;
	}
}
