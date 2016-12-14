package com.siteview.NNM.uijobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Siteview.DataTable;
import Siteview.SiteviewException;
import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;
import Siteview.Database.IDbDataParameterCollection;
import Siteview.Database.SqlParameterCollection;

import com.siteview.nnm.data.model.entity;
import com.siteview.nnm.data.model.svNode;

public class SvgSaveUtil {
	public static void savaSvgByNode(ISiteviewApi api,Map<String, entity> allEntiry){
		String delete_nnmsvgnode_sql="delete from NNMSvgNode";
		UpdateorDelete(delete_nnmsvgnode_sql, api);
		List<String> subnetname=new ArrayList<String>();
		List<svNode> pc_ip=new ArrayList<svNode>();
		Map<String,String> map=new HashMap<String,String>();
		Iterator<String> it = allEntiry.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			entity entiry = allEntiry.get(key);
			svNode node = (svNode) entiry;
			String ip=node.getLocalip();
			if(!ip.contains("."))
				continue;
			String iplist=node.getProperys().get("ipList");
			String subnet=node.getProperys().get("subnetName");
			if(iplist!=null&&iplist.length()>0&&subnet!=null&&subnet.length()>0){
				String [] ips_=iplist.split(",");
				String[] sub=subnet.split(",");
				for(int i=0;i<ips_.length;i++){
					String s0=ips_[i];
					if(!subnetname.contains(sub[i])){
						subnetname.add(sub[i]);
						String first=sub[i];
						String mask=first.substring(first.indexOf("/")+1);
						first=first.substring(0, first.indexOf("/"));
						String endip=Ipend.endip(first, mask);
						map.put(sub[i], endip);
					}
					savaSvg(api, sub[i], ips_[i], node.getMac(),
							node.getSvgtype()+"", node.getModel(), node.getFactory(),
							node.getSvid(), node.getDevicename());
				}
			}else if(iplist!=null&&iplist.length()>0){
				pc_ip.add(node);
			}
		}
		for(svNode node:pc_ip){
			Iterator<String> ite=map.keySet().iterator();
			while(ite.hasNext()){
				String keyip=ite.next();
				String endip=map.get(keyip);
				if(ScanUtils.ipInScale(node.getProperys().get("ipList"), keyip.substring(0, keyip.indexOf("/")), endip)){
					savaSvg(api, keyip, node.getProperys().get("ipList"), node.getMac(),
							node.getSvgtype()+"", node.getModel(), node.getFactory(),
							node.getSvid(), node.getDevicename());
					break;
				}
			}
		}
	}
	
	public static void savaSvg(ISiteviewApi api,String subnet,String ip,String mac, 
			String svgtype,String svgstle,String svgfirm,String svgid,String svgname){
		try {
			BusinessObject bo = api.get_BusObService().Create("NNMSvgNode");
			bo.GetField("Subnet").SetValue(new SiteviewValue(subnet));
			bo.GetField("IP").SetValue(new SiteviewValue(ip));
			bo.GetField("Mac").SetValue(new SiteviewValue(mac));
			bo.GetField("SvgType").SetValue(new SiteviewValue(svgtype));
			bo.GetField("SvgStyle").SetValue(new SiteviewValue(svgstle));
			bo.GetField("SvgFirm").SetValue(new SiteviewValue(svgfirm));
			bo.GetField("SvgId").SetValue(new SiteviewValue(svgid));
			bo.GetField("SvgName").SetValue(new SiteviewValue(svgname));
			bo.SaveObject(api, true, true);
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
	}
	/*
	 * 调用原生态修改和删除sql
	 */
	public static void UpdateorDelete(String sql, ISiteviewApi api) {
		IDbDataParameterCollection collection = new SqlParameterCollection();
		StringBuffer str = new StringBuffer();
		str.append(sql);
		try {
			api.get_NativeSQLSupportService().ExecuteNativeSQL(str.toString(), collection);
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * 调用原生态查询sql
	 */
	public static DataTable Select(String sql, ISiteviewApi api) {
		IDbDataParameterCollection collection = new SqlParameterCollection();
		StringBuffer str = new StringBuffer();
		str.append(sql);
		DataTable dt = null;
		try {
			dt = api.get_NativeSQLSupportService().ExecuteNativeSQLQuery(str.toString(), collection);
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
		return dt;
	}
	/*
	 * 保存IP-MAC异动
	 */
	public static void savaSvgChange(ISiteviewApi api,String oldip,String oldmac,String newip, 
			String svgtype,String newmac,String svgname){
		try {
			BusinessObject bo = api.get_BusObService().Create("NNMSvgChange");
			bo.GetField("OldIp").SetValue(new SiteviewValue(oldip));
			bo.GetField("OldMac").SetValue(new SiteviewValue(oldmac));
			bo.GetField("NewIp").SetValue(new SiteviewValue(newip));
			bo.GetField("SvgType").SetValue(new SiteviewValue(svgtype));
			bo.GetField("NewMac").SetValue(new SiteviewValue(newmac));
			bo.GetField("SvgName").SetValue(new SiteviewValue(svgname));
			bo.SaveObject(api, true, true);
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
	}
}
