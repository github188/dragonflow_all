package com.siteview.ecc.yft.odata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.Api.ISiteviewApi;
import net.sf.json.JSONArray;
import siteview.IFunctionExtension;

public class GetDiffAlarmUser implements IFunctionExtension {

	public GetDiffAlarmUser() {
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api, Map<String, String> inputParamMap) {
		
		List<Map<String,String>> listMap = new ArrayList<Map<String,String>>();
		
		String from = inputParamMap.get("FROM");
		String count =  inputParamMap.get("COUNT");
		String username =  inputParamMap.get("USER_NAME");
		String usertype =  inputParamMap.get("USERTYPE");
		String alarmconfigrecid =  inputParamMap.get("ALARMCONFIGRECID");
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT #express FROM Profile");
		//Profile.LoginID,Profile.DisplayName,Profile.RecId AS USER_ID
		if(usertype!=null&&alarmconfigrecid!=null&&usertype.trim().length()>0&&alarmconfigrecid.trim().length()>0){
			boolean hasWhere = false;
			boolean hasAnd = false;
			sql.append(",(");
			sql.append("SELECT UserLogID FROM AlarmStaff").append(" ");
			if(usertype!=null&&usertype.trim().length()>0){
				hasWhere = true;
				hasAnd = true;
				sql.append("where UserType=").append(usertype).append(" ");
			}
			if(alarmconfigrecid!=null&&alarmconfigrecid.trim().length()>0){
				if(!hasWhere){
					sql.append("where ");
				}
				if(hasAnd){
					sql.append("and ");
				}
				sql.append("AlarmConfigRecId=").append("'").append(alarmconfigrecid).append("'").append(" ");
			}
			sql.append(") AS staff").append(" ");
			sql.append("WHERE Profile.RecId!=staff.UserLogID ");
			if(username!=null&&username.trim().length()>0){
				sql.append("and DisplayName like ").append("'%").append(username).append("%'");
			}
		}
		else{
			if(username!=null&&username.trim().length()>0){
				sql.append(" where DisplayName like ").append("'%").append(username).append("%'");
			}
		}
		
		String countSql = sql.toString().replace("#express","count(*) total");
		Map<String,String> resultMap = new HashMap<String,String>();
		try{ 
			DataTable dt = api.get_NativeSQLSupportService().ExecuteNativeSQLQuery(countSql, null);
			if(dt.get_Rows().size()>0){
				resultMap.put("TOTAL_COUNT",dt.get_Rows().get(0).get("total").toString());
			}
			else{
				resultMap.put("TOTAL_COUNT","0");
			}
			if(from!=null&&count!=null&&from.trim().length()>0&&count.trim().length()>0){
				sql.append(" limit ").append(from).append(",").append(count);
			}
			dt = api.get_NativeSQLSupportService().ExecuteNativeSQLQuery(sql.toString().replace("#express","Profile.LoginID,Profile.DisplayName,Profile.RecId AS USER_ID"),null);
			List<Map<String,String>> userMap = new ArrayList<Map<String,String>>();
			for(DataRow dr:dt.get_Rows()){
				Map<String,String> map = new HashMap<String,String>();
				map.put("LoginID", dr.get_Item("LoginID").toString());
				map.put("DisplayName", dr.get_Item("DisplayName").toString());
				map.put("USER_ID", dr.get_Item("USER_ID").toString());
				userMap.add(map);
			}
			resultMap.put("USERS",JSONArray.fromObject(userMap).toString());
			listMap.add(resultMap);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
		return listMap;
	}

}
