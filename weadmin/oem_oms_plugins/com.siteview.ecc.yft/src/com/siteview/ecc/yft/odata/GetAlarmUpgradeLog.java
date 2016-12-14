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

public class GetAlarmUpgradeLog implements IFunctionExtension {

	public GetAlarmUpgradeLog() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api,
			Map<String, String> inputParamMap) {
		String alarmuploadid=inputParamMap.get("ALARMUPLOADID");
		List<Map<String, String>> outP=new ArrayList<Map<String, String>>();
		Map<String,String> outM=new HashMap<String,String>();
		List<Map> listmap=new ArrayList<Map>();
		if(alarmuploadid.contains(","))
			alarmuploadid=alarmuploadid.replaceAll(",", "','");
		
		String sql="select * from AlarmUploadLog WHERE AlarmUploadId in ('%s')";
		DataTable dt = DBQueryUtils.Select(String.format(sql, alarmuploadid),api);
		for (DataRow dr : dt.get_Rows()) {
			Map<String, String> _map = new HashMap<String, String>();
			_map.put("MonitorValue", dr.get("MonitorValue") == null ? "" : dr.get("MonitorValue")
					.toString());
			_map.put("EquipmentIp", dr.get("EquipmentIp") == null ? "" : dr
					.get("EquipmentIp").toString());
			String status=dr.get("MonitorStatus")==null?"error":dr.get("MonitorStatus").toString();
			if(status.equalsIgnoreCase("error"))
				status="错误";
			else if(status.equalsIgnoreCase("warning"))
				status="危险";
			_map.put("MonitorStatus", status);
			_map.put("EquipmentName", dr.get("EquipmentName") == null ? ""
					: dr.get("EquipmentName").toString());
			_map.put("AreaCode",
					dr.get("AreaCode") == null ? "" : dr.get("AreaCode")
							.toString());
			_map.put("AreaName",
					dr.get("AreaName") == null ? "" : dr.get("AreaName")
							.toString());
			listmap.add(_map);
		}
		outM.put("ALARMUPLOADLOG", JSONArray.fromObject(listmap).toString());
		outP.add(outM);
		return outP;
	}

}
