package com.siteview.NNM.modles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.siteview.NNM.uijobs.Ipend;
import com.siteview.NNM.uijobs.ScanUtils;
import com.siteview.nnm.data.EntityManager;
import com.siteview.nnm.data.model.entity;
import com.siteview.nnm.data.model.svNode;
import com.siteview.nnm.data.model.svgroup;

public class SubnetModle {
	private  String name="子网";
	List<SubnetDataModle> list=new ArrayList<SubnetDataModle>();
	public SubnetModle(){
		getSub();
	}
	public String getName() {
		return name;
	}
	public List<SubnetDataModle> getList() {
		return list;
	}
	
	public void getSub(){
		Map<String,List<svNode>> ips=new HashMap<String,List<svNode>>();
		Map<String, entity> allEntiry=EntityManager.allEntity;
		Iterator<String> it = allEntiry.keySet().iterator();
		List<String> subnetname=new ArrayList<String>();
		List<svNode> pc_ip=new ArrayList<svNode>();
		Map<String,String> map=new HashMap<String,String>();
		while (it.hasNext()) {
			String key = it.next();
			entity entiry = allEntiry.get(key);
			if(entiry instanceof svgroup)
				continue;
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
						try{
							if(first.length()>6&&Double.parseDouble(mask)>0){
								String endip=Ipend.endip(first, mask);
								map.put(sub[i], endip);
							}
						}catch(Exception ex){
						}
					}
					String ip_=sub[i];
					List<svNode> svs=ips.get(ip_);
					if(svs==null)
						svs=new ArrayList<svNode>();
					svs.add(node);
					ips.put(ip_, svs);
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
					List<svNode> svs=ips.get(keyip);
					if(svs==null)
						svs=new ArrayList<svNode>();
					svs.add(node);
					ips.put(keyip, svs);
					break;
				}
			}
		}
		
		for(String s :subnetname){
			SubnetDataModle subd=new SubnetDataModle();
			subd.setSubnetname(s);
			List<svNode> svnodes=ips.get(s);
			if(svnodes==null)
				subd.setCount(0);
			else
				subd.setCount(svnodes.size());
			subd.setSvnodes(svnodes);
			list.add(subd);
		}
	}
}
