package com.siteview.NNM.modles;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableItem;

import com.siteview.nnm.data.EntityManager;
import com.siteview.nnm.data.model.entity;
import com.siteview.nnm.data.model.svNode;
import com.siteview.nnm.data.model.svgroup;

public class SvgTypeModle {
	public final String name="设备类型";
	public List<SvgModle> svgs=new ArrayList<SvgModle>();
	public SvgTypeModle(){
		int[] i=getcount(); 
		SvgModle svg=new SvgModle("三层交换机",i[0],0); 
		svgs.add(svg);
		
		SvgModle svg0=new SvgModle("二层交换机",i[1],1);
		svgs.add(svg0);
		
		SvgModle svg1=new SvgModle("路由",i[2],2);
		svgs.add(svg1);
		
		SvgModle svg2=new SvgModle("防火墙",i[3],3);
		svgs.add(svg2);
		
		SvgModle svg3=new SvgModle("服务器",i[4],4);
		svgs.add(svg3);
		
		SvgModle svg5=new SvgModle("pc终端",i[5],5);
		svgs.add(svg5);
		
		SvgModle svg6=new SvgModle("其他",i[6],6);
		svgs.add(svg6);
	}
	public List<SvgModle> getSvgs() {
		return svgs;
	}
	public void setSvgs(List<SvgModle> svgs) {
		this.svgs = svgs;
	}
	public String getName() {
		return name;
	}
	public int[] getcount(){
		Map<String, entity> allEntiry=EntityManager.allEntity;
		Iterator<String> it = allEntiry.keySet().iterator();
		//mac 和ip 相同即为同一设备，需要排除掉
		List<String> ipmacs=new ArrayList<String>();
		int[] i=new int[7]; 
		while (it.hasNext()) {
			String key = it.next();
			entity entiry = allEntiry.get(key);
			if(entiry instanceof svNode){
				svNode node = (svNode) entiry;
				String ip=node.getLocalip();
				String mac=node.getMac();
				if(mac==null) mac="";
				String ipmac=ip+mac;
				if(ipmacs.contains(ipmac)){
					continue;
				}else{
					ipmacs.add(ipmac);
				}
				String maskField=node.getProperys().get("maskField");
				if(maskField!=null&&maskField.length()>0)
					ip+="/"+maskField;
				if (entiry.getSvgtype() == 0) {
					i[0]++;
				} else if (entiry.getSvgtype() == 1) {
					i[1]++;
				} else if (entiry.getSvgtype() == 2) {
					i[2]++;
				} else if (entiry.getSvgtype() == 3) {
					i[3]++;
				} else if (entiry.getSvgtype() == 4) {
					i[4]++;
				} else if (entiry.getSvgtype() == 5) {
					i[5]++;
				} else if (entiry.getSvgtype() == 6) {
					i[6]++;
				}
			}
		}
		return i;
	}
}
