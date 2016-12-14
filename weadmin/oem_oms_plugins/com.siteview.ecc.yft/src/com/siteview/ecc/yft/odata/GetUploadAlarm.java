package com.siteview.ecc.yft.odata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import com.siteview.alarm.bean.UploadAlarm;
import com.siteview.ecc.yft.report.InitReport;
import com.siteview.utils.db.DBQueryUtils;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.SiteviewException;
import Siteview.Api.ISiteviewApi;
import siteview.IFunctionExtension;
/*
 * 告警升级列表
 */
public class GetUploadAlarm implements IFunctionExtension {

	public GetUploadAlarm() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api,
			Map<String, String> inputParamMap) {
		List<Map<String,String>> outparam=new ArrayList<Map<String,String>>();
		Map<String,String> outmap=new HashMap<String,String>();
		String equipmenttype=inputParamMap.get("EQUIPMENTTYPE");
		String areacode=inputParamMap.get("AREACODE");
		String starttime=inputParamMap.get("STARTTIME");
		String endtime=inputParamMap.get("ENDTIME");
		String wheres="";
		if(equipmenttype!=null){
			if(wheres.length()>0)
				wheres+=" and ";
			wheres+=" MachineType='"+equipmenttype+"'";
		}
		if(starttime!=null&&endtime!=null){
			if(wheres.length()>0)
				wheres+=" and ";
			wheres+=" CreatedDateTime>='"+starttime+"' and CreatedDateTime<='"+endtime+"'";
		}
		
		if(areacode!=null){
			if(wheres.length()>0)
				wheres+=" and ";
			wheres+=" parentAreaCode='"+areacode+"'";
		}else{
			String groupid="";
			String groupname="";
			try {
				groupname = api.get_AuthenticationService().get_CurrentSecurityGroup();
			} catch (SiteviewException e1) {
				e1.printStackTrace();
			}
			if(!groupname.equalsIgnoreCase("Administrators")&&areacode==null
					&&InitReport.map.get("the_parent_domain")==null){
				try {
					groupid=api.get_AuthenticationService().get_CurrentSecurityGroupId();
					String sql="select o.o_code from Organization o,OrganizeAndSafeGroupRel og where o.RecId=og.organize_id AND og.safegroup_id='%s'";
					DataTable dt=DBQueryUtils.Select(String.format(sql, groupid), api);
					for(DataRow dr:dt.get_Rows()){
						areacode=dr.get("o_code")==null?"":dr.get("o_code").toString();
					}
					if(wheres.length()>0)
						wheres+=" and ";
					wheres+=" parentAreaCode='"+areacode+"'";
				} catch (SiteviewException e) {
					e.printStackTrace();
				}
			}
		}
		outmap.put("ALARMDATA", JSONArray.fromObject(getUpload(wheres, api)).toString());
		outparam.add(outmap);
		return outparam;
	}
	public static List<UploadAlarm> getUpload(String wheres,ISiteviewApi api){
		List<UploadAlarm> alarm=new ArrayList<UploadAlarm>();
		String sql="select * from AlarmUpload ";
		if(wheres.length()>0)
			sql+=" where "+wheres;
		DataTable dt=DBQueryUtils.Select(sql+" ORDER BY CreatedDateTime desc ", api);
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
				ua.setStatus("已发督办单");
			alarm.add(ua);
		}
		return alarm;
	}
}
