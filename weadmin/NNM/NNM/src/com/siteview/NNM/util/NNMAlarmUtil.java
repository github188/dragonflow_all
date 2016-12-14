package com.siteview.NNM.util;

import java.util.ArrayList;
import java.util.List;

import com.siteview.utils.db.DBQueryUtils;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.Api.ISiteviewApi;

public class NNMAlarmUtil {
	public static List<String[]> getAlarm(String ip,ISiteviewApi api){
		String sql="select * from AlarmEventLog where (AlarmStatus='1' or AlarmStatus='2') and EquipmentRecId in ("
				+ "select RecId from Equipment where ServerAddress="+ip+") ";
		DataTable dt=DBQueryUtils.Select(sql, api);
		List<String[]>  list=new ArrayList<String[]>();
		for(DataRow dr:dt.get_Rows()){
			String[] s=new String[3];
			s[0]=dr.get("CreatedDateTime").toString();
			s[1]=dr.get("MonitorName").toString();
			s[2]=dr.get("MonitorValue").toString();
			list.add(s);
		}
		return list;
	}
	
	public static String getStatus(String ip,ISiteviewApi api){
		String sql="select EquipmentStatus from AlarmEvent where EquipmentIp='"+ip+"' and (EquipmentStatus='error' or EquipmentStatus='warning')";
		DataTable dt=DBQueryUtils.Select(sql, api);
		String status="good";
		for(DataRow dr:dt.get_Rows()){
			String s=dr.get("EquipmentStatus").toString();
			if(status=="good")
				status=s;
			
			if(status.equals("error"))
				return status;
			
			if(status.equals("warning")&&s.equals("error"))
				return s;
		}
		return status;
	}
}
