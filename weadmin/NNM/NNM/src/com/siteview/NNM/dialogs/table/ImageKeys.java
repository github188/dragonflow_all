package com.siteview.NNM.dialogs.table;

import org.eclipse.jface.resource.ImageRegistry;

import com.siteview.NNM.Activator;

import siteview.windows.forms.ImageHelper;

public class ImageKeys {
	public static ImageRegistry images = new ImageRegistry();
	public static final String UNCHECKED = "Images/check-unselected.png";
	public static final String CHECKED = "Images/check-selected.png";
	public static final String SWITCHROUTER_BLUE = "Images/imgs/SwitchRouter_Blue.png";
	public static final String SWITCH_BLUE = "Images/imgs/Switch_Blue.png";
	public static final String SERVER_BLUE = "Images/imgs/Server_Blue.png";
	public static final String ROUTER_BLUE = "Images/imgs/Router_Blue.png";
	public static final String PC_BLUE = "Images/imgs/PC_Blue.png";
	public static final String OTHER_BLUE = "Images/imgs/Other_Blue.png";
	public static final String HUB_BLUE = "Images/imgs/HUB_Blue.png";
	public static final String GROUPING_BLUE = "Images/imgs/Grouping_Blue.png";
	public static final String GROUP_BLUE = "Images/imgs/Group_Blue.png";
	public static final String FIREWALL_BLUE = "Images/imgs/Firewall_Blue.png";
	public static final String ALERTPIC = "Images/imgs/alertpic.gif";
	public static final String SUBNET = "Images/imgs/Subnet.png";
	public static final String GOOD =  "Images/imgs/good.png";
	public static final String NODATA =  "Images/imgs/nodata.png";
	public static final String ILLOGICAL =  "Images/imgs/Illogical.png";
	public static final String ALL =  "Images/imgs/all.png";
	public static final String EXPORT =  "Images/imgs/export.png";
	public static final String MIB =  "Images/imgs/mib.png";
	public static final String IPMAC =  "Images/imgs/ipmac.png";
	public static final String FILEMIB =  "Images/imgs/filemib.png";
	public static final String TABLEMIB =  "Images/imgs/tablemib.png";
	public static final String TABLESMIB =  "Images/imgs/tablesmib.png";
	public static final String INFOMIB =  "Images/imgs/infomib.png";
	public static final String TOPNET = "Images/imgs/topnetwork.png";
	public static void loadimage() {
		if (images.get(UNCHECKED) == null)
			images.put(UNCHECKED, ImageHelper.LoadImage(Activator.PLUGIN_ID, UNCHECKED));
		if (images.get(CHECKED) == null)
			images.put(CHECKED, ImageHelper.LoadImage(Activator.PLUGIN_ID, CHECKED));
		if (images.get(SWITCHROUTER_BLUE) == null)
			images.put(SWITCHROUTER_BLUE, ImageHelper.LoadImage(Activator.PLUGIN_ID, SWITCHROUTER_BLUE));
		if (images.get(SWITCH_BLUE) == null)
			images.put(SWITCH_BLUE, ImageHelper.LoadImage(Activator.PLUGIN_ID, SWITCH_BLUE));
		if (images.get(SERVER_BLUE) == null)
			images.put(SERVER_BLUE, ImageHelper.LoadImage(Activator.PLUGIN_ID, SERVER_BLUE));
		if (images.get(ROUTER_BLUE) == null)
			images.put(ROUTER_BLUE, ImageHelper.LoadImage(Activator.PLUGIN_ID, ROUTER_BLUE));
		if (images.get(PC_BLUE) == null)
			images.put(PC_BLUE, ImageHelper.LoadImage(Activator.PLUGIN_ID, PC_BLUE));
		if (images.get(OTHER_BLUE) == null)
			images.put(OTHER_BLUE, ImageHelper.LoadImage(Activator.PLUGIN_ID, OTHER_BLUE));
		if (images.get(HUB_BLUE) == null)
			images.put(HUB_BLUE, ImageHelper.LoadImage(Activator.PLUGIN_ID, HUB_BLUE));
		if (images.get(GROUPING_BLUE) == null)
			images.put(GROUPING_BLUE, ImageHelper.LoadImage(Activator.PLUGIN_ID, GROUPING_BLUE));
		if (images.get(GROUP_BLUE) == null)
			images.put(GROUP_BLUE, ImageHelper.LoadImage(Activator.PLUGIN_ID, GROUP_BLUE));
		if (images.get(FIREWALL_BLUE) == null)
			images.put(FIREWALL_BLUE, ImageHelper.LoadImage(Activator.PLUGIN_ID, FIREWALL_BLUE));
		if (images.get(ALERTPIC) == null)
			images.put(ALERTPIC, ImageHelper.LoadImage(Activator.PLUGIN_ID, ALERTPIC));
		if (images.get(SUBNET) == null)
			images.put(SUBNET, ImageHelper.LoadImage(Activator.PLUGIN_ID, SUBNET));
		if (images.get(GOOD) == null)
			images.put(GOOD, ImageHelper.LoadImage(Activator.PLUGIN_ID, GOOD));
		if (images.get(NODATA) == null)
			images.put(NODATA, ImageHelper.LoadImage(Activator.PLUGIN_ID, NODATA));
		if (images.get(ILLOGICAL) == null)
			images.put(ILLOGICAL, ImageHelper.LoadImage(Activator.PLUGIN_ID, ILLOGICAL));
		if (images.get(ALL) == null)
			images.put(ALL, ImageHelper.LoadImage(Activator.PLUGIN_ID, ALL));
		if (images.get(EXPORT) == null)
			images.put(EXPORT, ImageHelper.LoadImage(Activator.PLUGIN_ID, EXPORT));
		if (images.get(MIB) == null)
			images.put(MIB, ImageHelper.LoadImage(Activator.PLUGIN_ID, MIB));
		if (images.get(FILEMIB) == null)
			images.put(FILEMIB, ImageHelper.LoadImage(Activator.PLUGIN_ID, FILEMIB));
		if (images.get(TABLEMIB) == null)
			images.put(TABLEMIB, ImageHelper.LoadImage(Activator.PLUGIN_ID, TABLEMIB));
		if (images.get(TABLESMIB) == null)
			images.put(TABLESMIB, ImageHelper.LoadImage(Activator.PLUGIN_ID, TABLESMIB));
		if (images.get(INFOMIB) == null)
			images.put(INFOMIB, ImageHelper.LoadImage(Activator.PLUGIN_ID, INFOMIB));
		if (images.get(IPMAC) == null)
			images.put(IPMAC, ImageHelper.LoadImage(Activator.PLUGIN_ID, IPMAC));
		if (images.get(TOPNET) == null)
			images.put(TOPNET, ImageHelper.LoadImage(Activator.PLUGIN_ID, TOPNET));
	}

}
