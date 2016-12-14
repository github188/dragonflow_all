package com.siteview.NNM.adapts;

import java.util.ArrayList;
import java.util.List;

import com.siteview.NNM.NNMInstanceData;
import com.siteview.NNM.modles.IpManagermentModle;
import com.siteview.NNM.modles.Modle;
import com.siteview.NNM.modles.RootNNM;
import com.siteview.NNM.modles.SubChartModle;
import com.siteview.NNM.modles.SvgManagementModle;
import com.siteview.NNM.modles.TopoModle;
import com.siteview.ecc.authorization.ItossAuthorizeServiceImpl;
import com.siteview.ecc.authorization.PermissionFactory;
import com.siteview.ecc.constants.Operation;
import com.siteview.ecc.constants.Resource;
import com.siteview.mib.model.MibModel;



public class NNMInstance {
	public String name="拓扑图管理";
	public RootNNM rootnnm;
	public List<RootNNM> rootnnms;
	
	public NNMInstance nnmInstance;
	public NNMInstance(){
		try {
			nnmInstance=this;
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public RootNNM getRoot() {
		return rootnnm;
	}
	public void setRoot(RootNNM rootnnm) {
		this.rootnnm = rootnnm;
	}
	public List<RootNNM> getRoots() {
		return rootnnms;
	}
	public void setRoots(List<RootNNM> rootnnms) {
		this.rootnnms = rootnnms;
	}
	public void init() throws Exception {
		rootnnms=new ArrayList<RootNNM>();
		
		this.rootnnm=new RootNNM();
		boolean flag=true;
		List<Object> list=new ArrayList<Object>();
//		flag=ItossAuthorizeServiceImpl.getInstance().isPermitted
//				(PermissionFactory.createPermission(
//						Resource.TOPU_SCAN.getType(), Operation.SHOW_NNM_TOPU_SCAN.getOperationString(), "*"));
//		if(flag){
			TopoModle tm=new TopoModle();
			tm.setList(builddata());
			list.add(tm);
//		}
		flag=ItossAuthorizeServiceImpl.getInstance().isPermitted
				(PermissionFactory.createPermission(
						Resource.DEVICE_MANAGEMENT.getType(), Operation.SHOW_NNM_DEVICE_MANAGEMENT.getOperationString(), "*"));
		if(flag){
			SvgManagementModle svg=new SvgManagementModle();
			list.add(svg);
		}
		flag=ItossAuthorizeServiceImpl.getInstance().isPermitted
				(PermissionFactory.createPermission(
						Resource.IP_MANAGEMENT.getType(), Operation.SHOW_NNM_IP_MANAGEMENT.getOperationString(), "*"));
		if(flag){
			IpManagermentModle ip=new IpManagermentModle();
			list.add(ip);
		}
		flag=ItossAuthorizeServiceImpl.getInstance().isPermitted
				(PermissionFactory.createPermission(
						Resource.MIB_BROWSER.getType(), Operation.SHOW_NNM_MIB_BROWSER.getOperationString(), "*"));
		if(flag){
			MibModel mib=new MibModel();
			list.add(mib);
		}
		rootnnm.setList(list);
		
		this.rootnnms.add(rootnnm);
	}
	/**
	 * 构建数据
	 * @param list
	 * @return
	 */
	private List<SubChartModle> builddata()
	{
		List<SubChartModle> list=new ArrayList<SubChartModle>();
	   for(String sub:	com.siteview.nnm.data.DBManage.Topos.keySet())
	   {
		   if(sub.equals("host"))
		   {
			   continue;
		   }
		   SubChartModle scm=new SubChartModle();
		   scm.setName(sub);
		   list.add(scm);
	   }
	   
	   return list;
	   
		
	}
	public static NNMInstance getinstance(){
		NNMInstance Instance=null;
		try{
			Instance=(NNMInstance) NNMInstanceData.getNNMData("NNMINSTANCE");
		}catch (Exception e) {
			Instance=null;
		}
		if(Instance==null){
			Instance=new NNMInstance();
			NNMInstanceData.setNNMData("NNMINSTANCE",Instance);
		}
		return Instance;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
