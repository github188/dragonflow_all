package com.siteview.ecc.yft.odata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import com.siteview.alarm.bean.UploadAlarm;
import com.siteview.utils.alarm.AlarmUtils;
import com.siteview.utils.db.DBQueryUtils;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.Api.ISiteviewApi;
import siteview.IFunctionExtension;

public class GetAlarmUpgradeDetails implements IFunctionExtension {

	public GetAlarmUpgradeDetails() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api,
			Map<String, String> inputParamMap) {
		// TODO Auto-generated method stub
		String alarmuploadid=inputParamMap.get("ALARMUPLOADID");
		String areaCode=inputParamMap.get("AREACODE");
		List<Map<String, String>> outP=new ArrayList<Map<String, String>>();
		Map<String,String> outM=new HashMap<String,String>();
		outM.put("ALARMUPLOADDETAILS", "");
		outM.put("ALARMUPLOADLOG", "");
		outP.add(outM);
		if(alarmuploadid!=null&&alarmuploadid.length()>0){
			alarmuploadid=alarmuploadid.replaceAll(",", "','");
			alarmuploadid="'"+alarmuploadid+"'";
			String sql="SELECT * from AlarmUpload where AlarmUploadId in (%s) and parentAreaCode='%s'";
			DataTable dt=DBQueryUtils.Select(String.format(sql, alarmuploadid,areaCode), api);
			List<UploadAlarm> up=new ArrayList<UploadAlarm>();
 			for(DataRow dr:dt.get_Rows()){
 				UploadAlarm ua=new UploadAlarm();
 				ua.setId(dr.get("RecId")==null?"":dr.get("RecId").toString());
 				ua.setArea(dr.get("areaName")==null?"":dr.get("areaName").toString());
 				ua.setAreacode(dr.get("areaCode")==null?"":dr.get("areaCode").toString());
 				ua.setParentarea(dr.get("parentAreaName")==null?"":dr.get("parentAreaName").toString());
 				ua.setParentareacode(dr.get("parentAreaCode")==null?"":dr.get("parentAreaCode").toString());
 				ua.setMachinetype(dr.get("machineType")==null?"":dr.get("machineType").toString());
 				ua.setCycle(dr.get("cycle")==null?0:Integer.parseInt(dr.get("cycle").toString()));
 				ua.setSourceId(dr.get("AlarmUploadId")==null?"":dr.get("AlarmUploadId").toString());
 				String time=dr.get("CreatedDateTime")==null?"":dr.get("CreatedDateTime").toString();
 				if(time.contains("."))
 					time=time.substring(0,time.indexOf("."));
 				ua.setDate(time);
 				String status=dr.get("alarmAction")==null?"0":dr.get("alarmAction").toString();
 				if(status.equals("0"))
 					ua.setStatus("未处理");
 				else if(status.equals("1"))
 					ua.setStatus("已确认");
 				else if(status.equals("2"))
 					ua.setStatus("已发公告");
 				else if(status.equals("3"))
 					ua.setStatus("已升级");
 				else if(status.equals("4"))
 					ua.setStatus("已督办");
 				up.add(ua);
			}
 			List<Map> listmap=new ArrayList<Map>();
 			if(up.size()>0){
 				UploadAlarm ua=up.get(0);
 				sql="select * from AlarmUploadLog WHERE AlarmUploadId='%s'";
 				dt=DBQueryUtils.Select(String.format(sql, ua.getSourceId()), api);
 				for(DataRow dr:dt.get_Rows()){
 					Map<String,String> _map=new HashMap<String, String>();
					_map.put("MonitorValue", dr.get("MonitorValue")==null?"":dr.get("MonitorValue").toString());
					_map.put("EquipmentIp", dr.get("EquipmentIp")==null?"":dr.get("EquipmentIp").toString());
					String status=dr.get("MonitorStatus")==null?"error":dr.get("MonitorStatus").toString();
					if(status.equalsIgnoreCase("error"))
						status="错误";
					else if(status.equalsIgnoreCase("warning"))
						status="危险";
					_map.put("MonitorStatus",status);
					_map.put("EquipmentName", dr.get("EquipmentName")==null?"":dr.get("EquipmentName").toString());
					_map.put("AreaCode", dr.get("AreaCode")==null?"":dr.get("AreaCode").toString());
					_map.put("AreaName", dr.get("AreaName")==null?"":dr.get("AreaName").toString());
					listmap.add(_map);
 				}
 			}
 			outM.put("ALARMUPLOADDETAILS", JSONArray.fromObject(up).toString());
 			outM.put("ALARMUPLOADLOG", JSONArray.fromObject(listmap).toString());
		}
		return outP;
	}
}
