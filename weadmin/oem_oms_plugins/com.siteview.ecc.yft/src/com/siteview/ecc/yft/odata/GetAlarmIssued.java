package com.siteview.ecc.yft.odata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import siteview.IFunctionExtension;
import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.Api.ISiteviewApi;

import com.siteview.ecc.yft.bean.AlarmIssued;
import com.siteview.utils.db.DBQueryUtils;
/*
 * 获取告警规则列表
 */
public class GetAlarmIssued implements IFunctionExtension {

	public GetAlarmIssued() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api,
			Map<String, String> inputParamMap) {
		List<Map<String,String>> outParam=new ArrayList<Map<String,String>>();
		Map<String,String> outmap=new HashMap<String, String>();
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
		String[] video={"SIGNALLOSS","BRIGHT","FREEZE","DIM","SCREENSHAKE","IMAGELOSS","COVERSTATUS",
		"SCREENSCROLL","COLORCOST","STREAK","SNOWFLAKE","PTZ","DEFINITION","ONLINESTATUS"};
		List list=new ArrayList<String>();
		for(String s:video){
			list.add(GetYFTAlarm.hashmap.get(s)+"="+s);
		}	
		monitortype.put("VIDEOALARMRESULT", list); 
		String[] dvr={"NETBREAK","IPERROR","DISKFULLINFO","DISKERRINFO","DISKLOSTINFO","VIDEOERRCHAN",
				"ENCODEERRCHAN","INPUTOVERLOADCHAN","OTHERERROR"};
		List dvrlist=new ArrayList<String>();
		for(String s:dvr){
			dvrlist.add(GetYFTAlarm.hashmap.get(s)+"="+s);
		}	
		monitortype.put("DVRALARMRESULT", dvrlist); 
		String[] nvr={"NETBREAK","IPERROR","DISKFULLINFO","DISKERRINFO","DISKLOSTINFO","BANDWIDTHFULL",
				"OTHERERROR","USERILLEGAL"};
		List nvrlist=new ArrayList<String>();
		for(String s:nvr){
			nvrlist.add(GetYFTAlarm.hashmap.get(s)+"="+s);
		}	
		monitortype.put("NVRALARMRESULT", nvrlist); 
		String[] code={"NETBREAK","IPERROR","USERILLEGAL","VIDEOERRCHAN","ENCODEERRCHAN"
				,"INPUTOVERLOADCHAN","OTHERERROR"};
		List codelist=new ArrayList<String>();
		for(String s:code){
			codelist.add(GetYFTAlarm.hashmap.get(s)+"="+s);
		}	
		monitortype.put("CODERALARMRESULT", codelist); 
		
		String[] ipsan={"NETBREAK","IPERROR","USERILLEGAL","RAIDFULLINFO","RAIDERRINFO"
				,"RAIDLOSTINFO","TEMPERATUREOVER","OTHERERROR"};
		List ipsanlist=new ArrayList<String>();
		for(String s:ipsan){
			ipsanlist.add(GetYFTAlarm.hashmap.get(s)+"="+s);
		}	
		monitortype.put("IPSANALARMRESULT", ipsanlist); 
		Map<String,AlarmIssued> map=new HashMap<String,AlarmIssued>();
		List<AlarmIssued> alarms=new ArrayList<AlarmIssued>();
		sql="select * from AlarmIssued";
		dt=DBQueryUtils.Select(sql, api);
		for(DataRow dr:dt.get_Rows()){
			AlarmIssued alarm=new AlarmIssued();
			alarm.setMachineType(dr.get("MachineType")==null?"":dr.get("MachineType").toString());
			alarm.setMachineTypeId(dr.get("MachineTypeId")==null?"":dr.get("MachineTypeId").toString());
			alarm.setWhetherSend(dr.get("WhetherSend")==null?"":dr.get("WhetherSend").toString());
			alarm.setCycle(dr.get("Cycle")==null?"":dr.get("Cycle").toString());
			alarm.setAlarmNumber(dr.get("AlarmNumber")==null?"":dr.get("AlarmNumber").toString());
			alarm.setResponseTime(dr.get("ResponseTime")==null?"":dr.get("ResponseTime").toString());
			alarm.setId(dr.get("RecId")==null?"":dr.get("RecId").toString());
			alarm.setUpdatetime(dr.get("UpdateTime")==null?"5":dr.get("UpdateTime").toString());
			map.put(alarm.getId(), alarm);
			alarms.add(alarm);
		}
		sql="select * from AlarmIssuedRel";
		dt=DBQueryUtils.Select(sql, api);
		for(DataRow dr:dt.get_Rows()){
			String key=dr.get("AlarmIssuedRecId")==null?"":dr.get("AlarmIssuedRecId").toString();
			String monitortable=dr.get("MonitorTypeTable")==null?"":dr.get("MonitorTypeTable").toString();
			String monitorname=dr.get("MonitorTypeName")==null?"":dr.get("MonitorTypeName").toString();
			AlarmIssued alarm=map.get(key);
			if(alarm!=null){
				String rel=alarm.getAlarmIssuedRel();
				if(rel==null)
					rel="";
				if(rel.length()>0)
					rel+=",";
				rel+=monitortable+"="+monitorname;
				alarm.setAlarmIssuedRel(rel);
			}
		}
		outmap.put("EQUIPMENTTYPE", JSONArray.fromObject(equipmenttype).toString());
		outmap.put("MONITORTYPE", JSONArray.fromObject(monitortype).toString());
		outmap.put("ALARMISSUED", JSONArray.fromObject(alarms).toString());
		outParam.add(outmap);
		return outParam;
	}
}
