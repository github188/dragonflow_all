package com.siteview.NNM.adapts;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;

import com.siteview.NNM.modles.InfoMibModle;
import com.siteview.NNM.modles.IpMacChangeModle;
import com.siteview.NNM.modles.IpManagermentModle;
import com.siteview.NNM.modles.RootNNM;
import com.siteview.NNM.modles.SubChartModle;
import com.siteview.NNM.modles.SubnetDataModle;
import com.siteview.NNM.modles.SubnetModle;
import com.siteview.NNM.modles.SvgManagementModle;
import com.siteview.NNM.modles.SvgModle;
import com.siteview.NNM.modles.SvgTypeModle;
import com.siteview.NNM.modles.TableMibModle;
import com.siteview.NNM.modles.TopoModle;
import com.siteview.mib.model.FileMibModel;
import com.siteview.mib.model.MibModel;

public class NNMAdapterFactory implements IAdapterFactory {

	private IWorkbenchAdapter nnmInstance = new NNMInstanceAdapter();
	private IWorkbenchAdapter root = new RootNNMAdapter();
	private IWorkbenchAdapter topo = new TopoModleAdapter();
	private IWorkbenchAdapter subchart = new SubChartModleAdapter();
	private IWorkbenchAdapter svgmanager = new SvgManagementAdapter();
	private IWorkbenchAdapter svgtype = new SvgTypeAdapter();
	private IWorkbenchAdapter svg = new SvgAdapter();
	private IWorkbenchAdapter ipmanager = new IpManagermentAdapter();
	private IWorkbenchAdapter subnet = new SubnetAdapter();
	private IWorkbenchAdapter subnetdata = new SubnetDataAdapter();
	private IWorkbenchAdapter ipmac = new IpMacChangeAdapter();
	private IWorkbenchAdapter mib = new MibAdapter();
	private IWorkbenchAdapter filemib = new FileMibAdapter();
	private IWorkbenchAdapter tablemib = new TableMibAdapter();
	private IWorkbenchAdapter infomib = new InfoMibAdapter();
	private IWorkbenchAdapter subchar = new SubChartModleAdapter();

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType == IWorkbenchAdapter.class && adaptableObject instanceof NNMInstance)
			return nnmInstance;
		else if (adapterType == IWorkbenchAdapter.class && adaptableObject instanceof RootNNM)
			return root;
		else if (adapterType == IWorkbenchAdapter.class && adaptableObject instanceof TopoModle)
			return topo;
		else if (adapterType == IWorkbenchAdapter.class && adaptableObject instanceof SubChartModle)
			return subchart;
		else if (adapterType == IWorkbenchAdapter.class && adaptableObject instanceof SvgManagementModle)
			return svgmanager;
		else if (adapterType == IWorkbenchAdapter.class && adaptableObject instanceof SvgTypeModle)
			return svgtype;
		else if (adapterType == IWorkbenchAdapter.class && adaptableObject instanceof SvgModle)
			return svg;
		else if (adapterType == IWorkbenchAdapter.class && adaptableObject instanceof IpManagermentModle)
			return ipmanager;
		else if (adapterType == IWorkbenchAdapter.class && adaptableObject instanceof SubnetModle)
			return subnet;
		else if (adapterType == IWorkbenchAdapter.class && adaptableObject instanceof SubnetDataModle)
			return subnetdata;
		else if (adapterType == IWorkbenchAdapter.class && adaptableObject instanceof MibModel)
			return mib;
		else if (adapterType == IWorkbenchAdapter.class && adaptableObject instanceof FileMibModel)
			return filemib;
		else if (adapterType == IWorkbenchAdapter.class && adaptableObject instanceof TableMibModle)
			return tablemib;
		else if (adapterType == IWorkbenchAdapter.class && adaptableObject instanceof InfoMibModle)
			return infomib;
		else if (adapterType == IWorkbenchAdapter.class && adaptableObject instanceof IpMacChangeModle)
			return ipmac;
		else if (adapterType == IWorkbenchAdapter.class && adaptableObject instanceof SubChartModle)
			return subchar;
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		// TODO Auto-generated method stub
		return new Class[] { IWorkbenchAdapter.class };
	}

}
