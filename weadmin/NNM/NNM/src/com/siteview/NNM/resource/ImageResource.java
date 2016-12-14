package com.siteview.NNM.resource;


import org.eclipse.jface.resource.ImageRegistry;

import com.siteview.NNM.Activator;

import siteview.windows.forms.Branding;
import siteview.windows.forms.ImageHelper;


public class ImageResource {
	public static final String SiteViewNNM = "Images/logo.png";
	public static final String topo = "Images/TopologyManage.png";
	public static final String subchart="Images/subchar.png";
	public static final String SwitchRoute="Images/3n.png";
	public static final String Switch="Images/switch.png";
	public static final String Route="Images/route.png";
	public static final String Fire="Images/firewall.png";
	public static final String Server="Images/server.png";
	public static final String Add="Images/add.png";
	public static final String Edit="Images/edit.png";
	public static final String Delete="Images/Delete.png";
	public static final String database="Images/database.png";
	public static final String windows="Images/Windows.png";
	public static final String unix="Images/Unix.png";
	
	public static ImageRegistry images=new ImageRegistry();
	public static void loadimage(){
		if(images.get(SiteViewNNM)==null)
			// modify by ff 20150331 统一logo图标
			images.put(SiteViewNNM, Branding.getDefaultIcon());
		
		if(images.get(topo)==null)
			images.put(topo, ImageHelper.LoadImage(Activator.PLUGIN_ID, topo));
	
		if(images.get(subchart)==null)
			images.put(subchart, ImageHelper.LoadImage(Activator.PLUGIN_ID, subchart));
	
	}
}
