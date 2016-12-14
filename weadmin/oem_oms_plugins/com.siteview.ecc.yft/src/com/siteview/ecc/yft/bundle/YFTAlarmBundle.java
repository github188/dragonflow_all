package com.siteview.ecc.yft.bundle;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.eclipse.swt.widgets.Composite;

import siteview.IAutoTaskExtension;
import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.DataTransport;
import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;

import com.siteview.alarm.bean.UploadAlarm;
import com.siteview.ecc.yft.Servlet.AlarmIssuedServlet;
import com.siteview.ecc.yft.report.InitReport;
import com.siteview.utils.db.DBQueryUtils;
/*
 * yft报警上传快速操作
 */
public class YFTAlarmBundle implements IAutoTaskExtension {

	public YFTAlarmBundle() {
	}
	SimpleDateFormat simp=new SimpleDateFormat("yyyy-MM-dd");
	@Override
	public String run(Map<String, Object> params) throws Exception {
		ISiteviewApi api = (ISiteviewApi) params.get("_CURAPI_");
		String url=InitReport.map.get("the_parent_domain");
		if(url==null)
			return null;
		Calendar cal=Calendar.getInstance();
		int hour=cal.get(Calendar.HOUR_OF_DAY);
		if(hour!=2)
			return "";
		String sql="select * from AlarmIssued";
		List<UploadAlarm> alarm=new ArrayList<UploadAlarm>();
		DataTable dt=DBQueryUtils.Select(sql, api);
		for(DataRow dr:dt.get_Rows()){
			String machinetype=dr.get("MachineType")==null?"":dr.get("MachineType").toString();
			String machinetypeId=dr.get("MachineTypeId")==null?"":dr.get("MachineTypeId").toString();
			String recid=dr.get("RecId")==null?"":dr.get("RecId").toString();
			String cycle=dr.get("Cycle")==null?"":dr.get("Cycle").toString();
			String responseTime=dr.get("ResponseTime")==null?"0":dr.get("ResponseTime").toString();
			String alarmNumber=dr.get("AlarmNumber")==null?"":dr.get("AlarmNumber").toString();
			String whetherSend=dr.get("WhetherSend")==null?"1":dr.get("WhetherSend").toString();
			String lastmoddatetime=dr.get("LastModDateTime")==null?"":dr.get("LastModDateTime").toString();
		
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			Date today=cal.getTime();
			String nowday=simp.format(today)+" 00:00:00";
			Date startday=simp.parse(lastmoddatetime);
			long l=today.getTime()-startday.getTime();
			long l1=l/(24*3600*1000*Integer.parseInt(cycle));
			if(responseTime.length()==1)
				responseTime="0"+responseTime;
			if(l1<=0)
				continue;
			long l0=l%(24*3600*1000*Integer.parseInt(cycle));
			if(l0>=(24*3600*1000))
				continue;
			//ParentID
			if(machinetypeId.endsWith("ARMRESULT")){
				sql="SELECT DISTINCT(EquipmentRecId) from AlarmEventLog where  MonitorRecId='%s' "
				+ "and MonitorType in (SELECT MonitorTypeTable from AlarmIssuedRel where AlarmIssuedRecId='%s')"
				+ " and MonitorStatus!='good' and AlarmStatus='1' and  TIMEDIFF('%s',CreatedDateTime)>'%s' GROUP BY EquipmentRecId";
				sql=String.format(sql,machinetypeId,recid,nowday,responseTime);
			}else{
					sql="select DISTINCT(EquipmentRecId) from AlarmEventLog where MonitorRecId in "
					+ "(select RecId from Monitor where EquipmentRecId in"
					+ "(select RecId from Equipment where EquipmentType in "
					+ "(select SUBSTRING(relationEqName ,11) from EquipmentTypeRel where ParentID='%s'))"
					+ "  and MonitorType in (select SUBSTRING(MonitorTypeTable ,12) "
					+ "from AlarmIssuedRel where AlarmIssuedRecId='%s')) and MonitorStatus!='good' "
					+ "and AlarmStatus='1' and  TIMEDIFF('%s',CreatedDateTime)>'%s' GROUP BY EquipmentRecId";
					sql=String.format(sql,machinetypeId,recid,nowday,responseTime);
			}
			DataTable dt0=DBQueryUtils.Select(sql, api);
			int i=dt0.get_Rows().size();
			if(i==0)
				continue;
			if(i>=Integer.parseInt(alarmNumber)||whetherSend.equals("1")||whetherSend.equals("true")){
				UploadAlarm uploadalarm=new UploadAlarm();
				uploadalarm.setArea(InitReport.map.get("the_system_name"));
				uploadalarm.setAreacode(InitReport.map.get("the_system_code"));
				uploadalarm.setParentarea(InitReport.map.get("the_parent_name"));
				uploadalarm.setParentareacode(InitReport.map.get("the_parent_code"));
				uploadalarm.setCycle(i);
				uploadalarm.setMachinetype(machinetype);
				uploadalarm.setSourceId("");
				BusinessObject bo=api.get_BusObService().Create("AlarmUpload");
				bo.GetField("areaCode").SetValue(new SiteviewValue(uploadalarm.getAreacode()));
				bo.GetField("areaName").SetValue(new SiteviewValue(uploadalarm.getArea()));
				bo.GetField("parentAreaCode").SetValue(new SiteviewValue(uploadalarm.getParentareacode()));
				bo.GetField("parentAreaName").SetValue(new SiteviewValue(uploadalarm.getParentarea()));
				bo.GetField("AlarmUploadId").SetValue(new SiteviewValue(bo.get_RecId()));
				bo.GetField("machineType").SetValue(new SiteviewValue(uploadalarm.getMachinetype()));
				bo.GetField("cycle").SetValue(new SiteviewValue(uploadalarm.getCycle()));
				bo.SaveObject(api, true, true);
				String sql0="select * from (select * from AlarmEvent where EquipmentRecId in (%s))ae "
						+ "left join (select EquipmentRecId,group_concat(MonitorName) mn from AlarmEventLog where "
						+ "MonitorStatus!='good' group by EquipmentRecId)ael on  ael.EquipmentRecId=ae.EquipmentRecId "
						+ "and ae.EquipmentStatus!='good' ";
				DataTable _dt=DBQueryUtils.Select(String.format(sql0, sql), api);
				List<String> listip=new ArrayList<String>();
				List<Map> alarmlog=new ArrayList<Map>();
				for(DataRow _dr:_dt.get_Rows()){
					String id= _dr.get("EquipmentRecId")==null?"":_dr.get("EquipmentRecId").toString();
					String mn=_dr.get("mn")==null?"":_dr.get("mn").toString();
					if(listip.contains(id)&&mn.length()==0)
						continue;
					listip.add(id);
					Map<String,String> _map=new HashMap<String, String>();
					_map.put("MonitorValue", mn);
					_map.put("EquipmentIp", _dr.get("EquipmentIp")==null?"":_dr.get("EquipmentIp").toString());
					_map.put("MonitorStatus", _dr.get("EquipmentStatus")==null?"":_dr.get("EquipmentStatus").toString());
					_map.put("EquipmentName",id);
					alarmlog.add(_map);
				}
				uploadalarm.setLog(alarmlog);
				alarm.add(uploadalarm);
			}
		}
		if(alarm.size()>0){
			AlarmIssuedServlet.sendReport("http://"+url+"/UploadAlarmServlet", JSONArray.fromObject(alarm).toString());
		}
		return null;
	}

	@Override
	public boolean hasCustomUI() {
		return false;
	}

	@Override
	public void creatConfigUI(Composite parent, Map<String, String> params) {
	}

	@Override
	public Map<String, String> getConfig() {
		return null;
	}
}
