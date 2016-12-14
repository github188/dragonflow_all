package com.siteview.ecc.yft.bundle;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;

import siteview.IAutoTaskExtension;
import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;

import com.siteview.ecc.yft.bean.OrganizationCode;
import com.siteview.ecc.yft.report.AccountingAssessmentUtils;
import com.siteview.ecc.yft.report.AdvancedReports;
import com.siteview.ecc.yft.report.PerWeekReport;
import com.siteview.ecc.yft.report.ServerPerDayReport;
import com.siteview.ecc.yft.report.ServerUtils;
import com.siteview.utils.date.DateUtils;
import com.siteview.utils.db.DBQueryUtils;
import com.siteview.utils.db.DBUtils;

public class YFTAdvancedReportBundle implements IAutoTaskExtension {
/*
 * 厅级报表统计
 */
	public YFTAdvancedReportBundle() {
		// TODO Auto-generated constructor stub
	}
	SimpleDateFormat simp=new SimpleDateFormat("yyyy-MM-dd");
	@Override
	public String run(Map<String, Object> params) throws Exception {
		ISiteviewApi api = (ISiteviewApi) params.get("_CURAPI_");
		Calendar cal=Calendar.getInstance();
		Date date=cal.getTime();
		int hour=cal.get(Calendar.HOUR_OF_DAY);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int month = cal.get(Calendar.MONTH) + 1;
		int w = cal.get(Calendar.DAY_OF_WEEK);//1=周天 2=周一
		if(hour==4){
			SimpleDateFormat simp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String context=simp.format(date)+"--daily:";
			cal.add(Calendar.DAY_OF_MONTH, -1);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			date=cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			Date date1=cal.getTime();
			Map<String,OrganizationCode> map=AdvancedReports.getOrganization(api);
			AdvancedReports.createReport(map, api, date.getTime(),
				date1.getTime(), new String[]{"videoDaily","storageDVRDaily","storageNVRDaily","storageCODEDaily"});
			
			//非视频类日报
			Calendar dayStartCalendar = (Calendar) cal.clone();
			dayStartCalendar.set(Calendar.HOUR_OF_DAY, 0);
			dayStartCalendar.set(Calendar.MINUTE, 0);
			dayStartCalendar.set(Calendar.SECOND, 0);
			
			ServerPerDayReport.setServerDailyData(api,
					DateUtils.formatDefaultDate(dayStartCalendar.getTime()), date1);

			AdvancedReports.createServerChildReport(api, "serverDaily", map, dayStartCalendar.getTime()
					.getTime(), date1.getTime());
			
			if(w==2){//周报
				context+="--weekly:";
				Calendar cal1=Calendar.getInstance();
				cal1.add(Calendar.DAY_OF_MONTH, -7);
				cal1.set(Calendar.MINUTE, 0);
				cal1.set(Calendar.SECOND, 0);
				Date d1=cal.getTime();
				Date d0=cal1.getTime();
				AdvancedReports.createReport(map, api, d0.getTime(),
					d1.getTime(), new String[]{"videoWeekly","storageDVRWeekly","storageNVRWeekly","storageCODEWeekly"});
			
				//非视频类服务器周报
				ServerUtils.setData("yftitoss", "serverWeekly", 
						PerWeekReport.getServerDetailsWeekly(api, d0.getTime(), d1.getTime(), "serverDaily"), d1);
				AdvancedReports.createServerChildReport(api, "serverWeekly", map, d0.getTime(), d1.getTime());
				//工单报表
				AdvancedReports.createWorkordereChildReport(api, "workOrderWeekly", map, d0.getTime(),d1.getTime());
			}
			if(day==1){
				context+="--Monthly:";
				Calendar cal1=Calendar.getInstance();
				cal1.add(Calendar.MONTH, -1);
				cal1.set(Calendar.HOUR, 0);
				cal1.set(Calendar.MINUTE, 0);
				cal1.set(Calendar.SECOND, 0);
				Date d0=cal1.getTime();
				AdvancedReports.createReport(map, api, d0.getTime(),
						date1.getTime(), new String[]{"videoMonthly","storageDVRMonthly","storageNVRMonthly","storageCODEMonthly"});
			
				//非视频类月报
				ServerUtils.setData("yftitoss", "serverMonthly", 
						PerWeekReport.getServerDetailsWeekly(api, cal1.getTime().getTime(), date1.getTime(), "serverDaily"), date1);
				AdvancedReports.createServerChildReport(api, "serverMonthly", map, d0.getTime(), date1.getTime());
				
				//计费考核月报
				AccountingAssessmentUtils.saveAccountingAssessmentReport(api, "yftitoss", "accountingassessmentMonthly",
						DateUtils.formatDefaultDate(cal1.getTime()), date1);
				
				//工单报表
				AdvancedReports.createWorkordereChildReport(api, "workOrderMonthly", map,  cal1.getTime().getTime(), date1.getTime());
				if(month==4||month==7||month==10||month==1){
					context+="--Quarterly Bulletin:";
					cal1.add(Calendar.MONTH, -2);
					d0=cal1.getTime();
					AdvancedReports.createReport(map, api, d0.getTime(),
							date1.getTime(), new String[]{"videoQuarterly","storageDVRQuarterly","storageNVRQuarterly","storageCODEQuarterly"});
					//非视频类季报
					ServerUtils.setData("yftitoss", "serverQuarterly", 
							PerWeekReport.getServerDetailsWeekly(api, cal1.getTime().getTime(), date1.getTime(), "serverMonthly"), date1);
					
					AdvancedReports.createServerChildReport(api, "serverQuarterly", map, cal1.getTime()
							.getTime(), date1.getTime());
					//工单报表
					AdvancedReports.createWorkordereChildReport(api, "workOrderQuarterly", map,  cal1.getTime().getTime(), date1.getTime());
					if(month==1){
						context+="--annual report:";
						Calendar cal2=Calendar.getInstance();
						cal2.add(Calendar.YEAR, -1);
						cal2.set(Calendar.HOUR, 0);
						cal2.set(Calendar.MINUTE, 0);
						cal2.set(Calendar.SECOND, 0);
						AdvancedReports.createReport(map, api, cal2.getTime().getTime(),
							date1.getTime(), new String[]{"videoYearly","storageDVRYearly","storageNVRYearly","storageCODEYearly"});	
						
						//非视频类年报
						ServerUtils.setData("yftitoss", "serverYearly", 
								PerWeekReport.getServerDetailsWeekly(api, cal1.getTime().getTime(), date1.getTime(), "serverQuarterly"), date1);
						AdvancedReports.createServerChildReport(api, "serverYearly", map, cal1.getTime()
								.getTime(), date1.getTime());
						//工单报表
						AdvancedReports.createWorkordereChildReport(api, "workOrderYearly", map,  cal1.getTime().getTime(), date1.getTime());
					}
				}
			}
			YFTReport.writeToTxtByFileWriter(context+"--"+simp.format(new Date())+"\n");
		}else if(hour==0){//告警升级
			SimpleDateFormat simp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			Map<String,Map<String,List<AutomaticWorkoredr>>> map=new HashMap<String, Map<String,List<AutomaticWorkoredr>>>();
			String sql="select updatetime,MachineType from AlarmIssued ";
			Map<String,Map<String,String>> _map=new HashMap<String,Map<String,String>>();
			DataTable dt=DBQueryUtils.Select(sql, api);
			Map<String,Integer> map=new HashMap<String, Integer>();
			for(DataRow dr:dt.get_Rows()){
				String key=dr.get("MachineType")==null?"":dr.get("MachineType").toString();
				int i=Integer.parseInt(dr.get("updatetime")==null?"5":dr.get("updatetime").toString());
				map.put(key, i);
			}
			Date nowdate=new Date();
			Collection<?> col=DBUtils.getBussCollection("alarmAction", "0", "AlarmUpload", api);
			Iterator<?> ite=col.iterator();
			while(ite.hasNext()){
				BusinessObject bo=(BusinessObject) ite.next();
				Date createtime=(Date) bo.GetField("LastModDateTime").get_NativeValue();
				createtime.setHours(0);
				createtime.setMinutes(0);
				createtime.setSeconds(0);
				String type=bo.GetField("machineType")==null?"":bo.GetField("machineType").get_NativeValue().toString();
				String pareac=bo.GetField("parentAreaCode")==null?"":bo.GetField("parentAreaCode").get_NativeValue().toString();
				int cycle=bo.GetField("cycle")==null?0:Integer.parseInt(bo.GetField("cycle").get_NativeValue().toString());
				long l=nowdate.getTime()-createtime.getTime();
				int l1=map.get(type)==null?0:map.get(type);
				if(l>(l1*24*3600*1000)){
					BusinessObject bo1=DBQueryUtils.queryOnlyBo("o_code", pareac, "Organization", api);//sx
					if(bo1!=null){
						String parentid=bo1.GetField("parentId").get_NativeValue().toString();//dizhou
						if(parentid.length()>0){
							BusinessObject bo2=DBQueryUtils.queryOnlyBo("o_code", parentid, "Organization", api);
							if(bo2!=null){
								String pparentid= bo2.GetField("parentId").get_NativeValue().toString();
								if(pparentid==null||pparentid.length()==0){
									Map<String,String> map1=_map.get(parentid+":"+type);
									if(map1==null){
										map1=new HashMap<String,String>();
										map1.put("cycle", cycle+"");
										map1.put("areaCode", pareac);
										map1.put("areaName", bo1.GetField("o_name").get_NativeValue().toString());
										map1.put("parentAreaName", bo2.GetField("o_name").get_NativeValue().toString());
										map1.put("parentAreaCode", parentid);
										map1.put("machineType", type);
										map1.put("alarmAction", "0");
										map1.put("alarmUploadId",bo.GetField("AlarmUploadId").get_NativeValue().toString());
									}else{
										map1.put("cycle", (cycle+Integer.parseInt(map1.get("cycle"))+""));
										String upid=bo.GetField("AlarmUploadId").get_NativeValue().toString();
										if(!map1.get("alarmUploadId").contains(upid))
											map1.put("alarmUploadId",map1.get("alarmUploadId")+","+upid);
									}
									_map.put(parentid+":"+type, map1);
								}else{
									BusinessObject newbo=api.get_BusObService().Create("AlarmUpload");
									newbo.GetField("areaCode").SetValue(new SiteviewValue(bo.GetField("areaCode").get_NativeValue()));
									newbo.GetField("cycle").SetValue(new SiteviewValue(bo.GetField("cycle").get_NativeValue()));
									newbo.GetField("areaName").SetValue(new SiteviewValue(bo.GetField("areaName").get_NativeValue()));
									newbo.GetField("parentAreaName").SetValue(new SiteviewValue(bo2.GetField("o_name").get_NativeValue()));
									newbo.GetField("parentAreaCode").SetValue(new SiteviewValue(bo2.GetField("o_code").get_NativeValue()));
									newbo.GetField("AlarmUploadId").SetValue(new SiteviewValue(bo.GetField("AlarmUploadId").get_NativeValue()));
									newbo.GetField("machineType").SetValue(new SiteviewValue(type));
									newbo.GetField("alarmAction").SetValue(new SiteviewValue(0));
									newbo.SaveObject(api, true, true);
								}
								bo.GetField("alarmAction").SetValue(new SiteviewValue(3));
								bo.SaveObject(api, true, true);
							}
						}
					}
				}
			}
			Iterator<String> _ite=_map.keySet().iterator();
			while(_ite.hasNext()){
				String key=_ite.next();
				Map<String,String> map1=_map.get(key);
				BusinessObject bo=DBQueryUtils.queryOnlyBo("areaCode",
						map1.get("areaCode"),"machineType",map1.get("machineType"), "AlarmUpload", api);
				if(bo==null)
					bo=api.get_BusObService().Create("AlarmUpload");
				bo.GetField("areaCode").SetValue(new SiteviewValue(map1.get("areaCode")));
				bo.GetField("cycle").SetValue(new SiteviewValue(map1.get("cycle")));
				bo.GetField("areaName").SetValue(new SiteviewValue(map1.get("areaName")));
				bo.GetField("parentAreaName").SetValue(new SiteviewValue(map1.get("parentAreaName")));
				bo.GetField("parentAreaCode").SetValue(new SiteviewValue(map1.get("parentAreaCode")));
				bo.GetField("machineType").SetValue(new SiteviewValue(map1.get("machineType")));
				bo.GetField("alarmAction").SetValue(new SiteviewValue(map1.get("alarmAction")));
				bo.GetField("AlarmUploadId").SetValue(new SiteviewValue(map1.get("alarmUploadId")));
				bo.SaveObject(api, true, true);
			}
		}
		return null;
	}

	@Override
	public boolean hasCustomUI() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void creatConfigUI(Composite parent, Map<String, String> params) {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, String> getConfig() {
		// TODO Auto-generated method stub
		return null;
	}

}
