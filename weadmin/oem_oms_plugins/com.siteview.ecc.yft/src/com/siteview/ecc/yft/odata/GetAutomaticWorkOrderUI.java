package com.siteview.ecc.yft.odata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import com.siteview.utils.db.DBQueryUtils;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.Api.ISiteviewApi;
import siteview.IFunctionExtension;
/*
 * 获取自动触发工单配置界面数据
 */
public class GetAutomaticWorkOrderUI implements IFunctionExtension {

	public GetAutomaticWorkOrderUI() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api,
			Map<String, String> inputParamMap) {
		List<Map<String, String>> outp=new ArrayList<Map<String,String>>();
		Map<String,String> outm=new HashMap<String, String>();
		Map<String,String> equipmenttype=new HashMap<String,String>();
		Map<String,List> monitortype=new HashMap<String,List>();
		String sql="select RecId,typeAlias from EquipmentTypeRel where ParentID='' OR ParentID is null";
		DataTable dt=DBQueryUtils.Select(sql, api);
		for(DataRow dr:dt.get_Rows()){
			String recid=dr.get("RecId")==null?"":dr.get("RecId").toString();
			String typeAlias=dr.get("typeAlias")==null?"":dr.get("typeAlias").toString();
			equipmenttype.put(typeAlias,recid);
		}
		equipmenttype.put("DVR","DVRALARMRESULT");
		equipmenttype.put("NVR","NVRALARMRESULT");
		equipmenttype.put("编码器","CODERALARMRESULT");
		equipmenttype.put("摄像机","VIDEOALARMRESULT");
		equipmenttype.put("IPSAN","IPSANALARMRESULT");
		sql="select distinct(MonitorTableName),MonitorName,et.ParentID from EccMonitorList e,EquipmentTypeRel et where e.Range=et.relationEqName";
		dt=DBQueryUtils.Select(sql, api);
		for(DataRow dr:dt.get_Rows()){
			String key=dr.get("ParentID")==null?"":dr.get("ParentID").toString();
			String monitortable=dr.get("MonitorTableName")==null?"":dr.get("MonitorTableName").toString();
			String monitorname=dr.get("MonitorName")==null?"":dr.get("MonitorName").toString();
			List<String> list=monitortype.get(key);
			if(list==null)
				list=new ArrayList<String>();
			list.add(monitorname+"="+monitortable);
			monitortype.put(key, list);
		}
		String[] video={"SIGNALLOSS","IMAGELOSS","ONLINESTATUS"};
				List list=new ArrayList<String>();
				for(String s:video){
					list.add(GetYFTAlarm.hashmap.get(s)+"="+s);
				}	
				monitortype.put("VIDEOALARMRESULT", list); 
				String[] dvr={"NETBREAK"};
				List dvrlist=new ArrayList<String>();
				for(String s:dvr){
					dvrlist.add(GetYFTAlarm.hashmap.get(s)+"="+s);
				}	
				monitortype.put("DVRALARMRESULT", dvrlist); 
				String[] nvr={"NETBREAK"};
				List nvrlist=new ArrayList<String>();
				for(String s:nvr){
					nvrlist.add(GetYFTAlarm.hashmap.get(s)+"="+s);
				}	
				monitortype.put("NVRALARMRESULT", nvrlist); 
				String[] code={"NETBREAK"};
				List codelist=new ArrayList<String>();
				for(String s:code){
					codelist.add(GetYFTAlarm.hashmap.get(s)+"="+s);
				}	
				monitortype.put("CODERALARMRESULT", codelist); 
				
				String[] ipsan={"NETBREAK"};
				List ipsanlist=new ArrayList<String>();
				for(String s:ipsan){
					ipsanlist.add(GetYFTAlarm.hashmap.get(s)+"="+s);
				}	
				monitortype.put("IPSANALARMRESULT", ipsanlist); 
				outm.put("EQUIPMENTTYPE", JSONArray.fromObject(equipmenttype).toString());
				outm.put("MONITORTYPE", JSONArray.fromObject(monitortype).toString());
		String sql0="select * from AutomaticWorkOrder";
		List<Map> au=new ArrayList<Map>() ;
		 dt=DBQueryUtils.Select(sql0, api);
		 for(DataRow dr:dt.get_Rows()){
			Map<String,String> m=new  HashMap<String, String>();
			m.put("equipmenttype", dr.get("EquipmentType")==null?"":dr.get("EquipmentType").toString());
			m.put("equipmettypename", dr.get("EquipmentTypeName")==null?"":dr.get("EquipmentTypeName").toString());
			m.put("monitortype", dr.get("MonitorType")==null?"":dr.get("MonitorType").toString());
			m.put("monitortypename", dr.get("MonitorTypeName")==null?"":dr.get("MonitorTypeName").toString());
			m.put("operation", dr.get("Operation")==null?"":dr.get("Operation").toString());
			m.put("longtime", dr.get("Longtime")==null?"":dr.get("Longtime").toString());
			m.put("recid", dr.get("RecId")==null?"":dr.get("RecId").toString());
			au.add(m);
		 }
		 outm.put("AUTOMATICWORKORDER", JSONArray.fromObject(au).toString());
		outp.add(outm);
		return outp;
	}

}
