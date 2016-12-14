package com.siteview.ecc.yft.report;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.dom4j.datatype.DatatypeAttribute;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.SiteviewException;
import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;

import com.siteview.alarm.bean.DepartmentalReports;
import com.siteview.ecc.yft.bean.LoseVideorecord;
import com.siteview.ecc.yft.bean.ServerDetail;
import com.siteview.utils.db.DBQueryUtils;

public class PerDayReport {
	//每天录像丢失报表VIDEORECORDRESULT
	public static List<LoseVideorecord> LoseVideorecordresult(ISiteviewApi api){
		SimpleDateFormat simp=new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		Date d=cal.getTime();
		String endtime=simp.format(d)+" 23:59:59";
		String starttime=simp.format(d)+" 00:00:00";
		String sql="select v.time,v.dura,v.coun,v1.* from (SELECT "
				+ "group_concat(concat(RIGHT(LOSTSTARTTIME,"
				+ "length(LOSTSTARTTIME)-11),'-',RIGHT(LOSTENDTIME,"
				+ "length(LOSTENDTIME)-11))) as time,VIDEOFLAG,sum(lostduration) dura,count(*) coun  "
				+ "from VIDEORECORDRESULT where LOSTSTARTTIME>'%s' "
				+ "AND LOSTENDTIME<'%s' group by VIDEOFLAG )v "
				+ "LEFT JOIN (SELECT VIDEONAME,VIDEOFLAG,IPADDRESS,IPLACE from "
				+ "VIDEOPOINTINFO)v1 on v.VIDEOFLAG=v1.VIDEOFLAG";
		DataTable dt=DBQueryUtils.Select(String.format(sql,starttime,endtime), api);
		List<LoseVideorecord> list=new ArrayList<LoseVideorecord>();
		try{
			for(DataRow dr:dt.get_Rows()){
				LoseVideorecord losev=new LoseVideorecord();
				losev.setIpaddress(dr.get("IPADDRESS")==null?"":dr.get("IPADDRESS").toString());
				losev.setIplace(dr.get("IPLACE")==null?"":dr.get("IPLACE").toString());
				losev.setTime(dr.get("time")==null?"":dr.get("time").toString());
				losev.setVideoflag(dr.get("VIDEOFLAG")==null?"":dr.get("VIDEOFLAG").toString());
				losev.setVideoname(dr.get("VIDEONAME")==null?"":dr.get("VIDEONAME").toString());
				losev.setLongtime(dr.get("dura")==null?0:Long.parseLong(dr.get("dura").toString()));
	//			if(losev.getLongtime()>0)
	//				losev.setLongtime(losev.getLongtime()/1000);
	//			else
	//				losev.setLongtime(0);
				losev.setLosttimes(dr.get("coun")==null?0:Long.parseLong(dr.get("coun").toString()));
				list.add(losev);
			}
		}catch(Exception e){
		}
		return list;
	}
	//每天视频丢失报表VideoStatistics type:LostType=VIDEOLOSS,
	//select StartTime,EndTime,LostType,Videoflag,lostduration from VideoStatistics 
	//离线状态详细统计报表（日报）,摄像机录像丢失详细统计报表 ONLINESTATUS
	public static List<LoseVideorecord> LoseViewrecordresult(ISiteviewApi api,String type){
		SimpleDateFormat simp=new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		Date d=cal.getTime();
		String endtime=simp.format(d)+" 23:59:59";
		String starttime=simp.format(d)+" 00:00:00";
		List<LoseVideorecord> list=new ArrayList<LoseVideorecord>();
		String sql="select v.time,v.dura,v.c,v1.* from (SELECT "
				+ "group_concat(concat(RIGHT(StartTime,"
				+ "length(StartTime)-11),'-',RIGHT(EndTime,"
				+ "length(EndTime)-11))) as time,count(*) c,Videoflag,sum(lostduration) dura "
				+ "from VideoStatistics where StartTime>'%s' "
				+ "AND EndTime<'%s' and LostType='%s' group by Videoflag )v "
				+ "LEFT JOIN (SELECT VIDEONAME,VIDEOFLAG,IPADDRESS,IPLACE from "
				+ "VIDEOPOINTINFO)v1 on v.Videoflag=v1.VIDEOFLAG";
		DataTable dt=DBQueryUtils.Select(String.format(sql,starttime,endtime,type), api);
		for(DataRow dr:dt.get_Rows()){
			LoseVideorecord losev=new LoseVideorecord();
			losev.setIpaddress(dr.get("IPADDRESS")==null?"":dr.get("IPADDRESS").toString());
			losev.setIplace(dr.get("IPLACE")==null?"":dr.get("IPLACE").toString());
			losev.setTime(dr.get("time")==null?"":dr.get("time").toString());
			losev.setVideoflag(dr.get("VIDEOFLAG")==null?"":dr.get("VIDEOFLAG").toString());
			losev.setVideoname(dr.get("VIDEONAME")==null?"":dr.get("VIDEONAME").toString());
			losev.setLongtime(dr.get("dura")==null?0:Long.parseLong(dr.get("dura").toString()));
//			if(losev.getLongtime()>0)
//				losev.setLongtime(losev.getLongtime());
//			else
//				losev.setLongtime(0);
			losev.setLosttimes(dr.get("c")==null?0:Integer.parseInt(dr.get("c").toString()));
			list.add(losev);
		}
		return list;
	}
	static DecimalFormat df = new DecimalFormat("#0.0000"); 
	//摄像机视频考核日报,摄像机连续不在线时间 默认hour为2小时=7200s
	public static DepartmentalReports VideoAssessmentDaily(ISiteviewApi api,int hour){
		SimpleDateFormat simp=new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		Date d=cal.getTime();
		String endtime=simp.format(d)+" 23:59:59";
		String starttime=simp.format(d)+" 00:00:00";
		String area=InitReport.map.get("the_system_name");
		String areacode=InitReport.map.get("the_system_code");
		DepartmentalReports department=new DepartmentalReports();
		String sql="SELECT v1.c2,v0.c from (SELECT count(*) c "
				+ "from VIDEOALARMRESULT) v0,(select count(DISTINCT(VIDEOFLAG)) c2 "
				+ "from VideoStatistics where LostType='ONLINESTATUS' and "
				+ " StartTime>='%s' and EndTime<='%s' and lostduration>='%s' )v1 ";
		DataTable dt=DBQueryUtils.Select(String.format(sql, starttime,endtime,hour), api);
		for(DataRow dr:dt.get_Rows()){
			String s=dr.get("c")==null?"0":dr.get("c").toString();
			department.setTotal(Integer.parseInt(s));
			s=dr.get("c2")==null?"0":dr.get("c2").toString();
			department.setOnlinecount(department.getTotal()-Integer.parseInt(s));
			double d0=0.0;
			if(department.getOnlinecount()>0&&department.getTotal()>0)
				d0=Double.parseDouble(department.getOnlinecount()+"")/department.getTotal();
			department.setOnlinerate(Double.parseDouble(df.format(d0)));
		}
		//select * from YFTIntact where DateTime>'%s' and Videoflag='%s' 
		 sql = "SELECT (v1.c2/v0.c) as cc,v1.c2,v1.sum from (SELECT count(*) c "
		 		+ "from VIDEOALARMRESULT) v0,(select count(DISTINCT(VIDEOFLAG)) c2,"
		 		+ "sum(lostduration) sum from VideoStatistics where LostType='VIDEOLOSS' "
		 		+ "and  StartTime>='%s' and EndTime<='%s')v1 ";

		    dt = DBQueryUtils.Select(String.format(sql,starttime, endtime), api);
		    for (DataRow dr : dt.get_Rows()) {
		      String s = (dr.get("cc") == null) ? "0" : dr.get("cc").toString();
		      department.setVideolossrate(Double.parseDouble(s));
		      s = (dr.get("c2") == null) ? "0" : dr.get("c2").toString();
		      department.setVideolosscount(Integer.parseInt(s));
		      s = (dr.get("sum") == null) ? "0" : dr.get("sum").toString();
		      department.setVideolossduration(Double.parseDouble(s));
		    }
		    sql="select count(*) count from (select Videoflag from YFTIntact where ErrorDateTime>'%s' "
					+ " and ErrorDateTime<'%s' and MachineType='VIDEOALARMRESULT' "
					+ " union select Videoflag from VideoStatistics where "
					+ " StartTime>'%s' and EndTime<'%s') a";
			dt=DBQueryUtils.Select(String.format(sql, starttime,endtime,starttime,endtime), api);
			for(DataRow dr:dt.get_Rows()){
				String s=dr.get("count")==null?"0":dr.get("count").toString();
				department.setIntactcount(department.getTotal()-Integer.parseInt(s));
				if(department.getIntactcount()>0&&department.getTotal()>0)
					department.setIntactrate(Double.parseDouble(df.format(Double.parseDouble(department.getIntactcount()+"")/department.getTotal())));
			}	
		   
		sql="select count(*) count from YFTIntact where ErrorDateTime>='%s' and ErrorDateTime<='%s' and MachineType='VIDEOALARMRESULT'";
		dt=DBQueryUtils.Select(String.format(sql, starttime,endtime), api);
		for(DataRow dr:dt.get_Rows()){
			String s=dr.get("count")==null?"0":dr.get("count").toString();
			department.setAnomalycount(Integer.parseInt(s));
			double d0=0.0;
			if(department.getAnomalycount()>0&&department.getTotal()>0)
				d0=Double.parseDouble(department.getAnomalycount()+"")/department.getTotal();
			department.setAnomalyrate(Double.parseDouble(df.format(d0)));
		}
		department.setArea(area);
		department.setAreacode(areacode);
		try {
			department.setDate(simp.parse(endtime));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return department;
	}
	//存储设备日统计报表
	public static DepartmentalReports StorageServlet(ISiteviewApi api,String table){
		String area=InitReport.map.get("the_system_name");
		String areacode=InitReport.map.get("the_system_code");
		DepartmentalReports department=new DepartmentalReports();
		SimpleDateFormat simp=new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		Date d=cal.getTime();
		String endtime=simp.format(d)+" 23:59:59";
		String starttime=simp.format(d)+" 00:00:00";
		String sql="select b.total,a.count,a.sum from (select count(*) total from "+table+")b,("
				+ "select count(DISTINCT(SERVERFLAG)) count,sum(offlineduration)as sum from StorageStatistics where StorageType='%s' and "
				+ "StartTime>='%s' and EndTime<='%s' )a ";
		DataTable dt=DBQueryUtils.Select(String.format(sql, table,starttime,endtime), api);
		for(DataRow dr:dt.get_Rows()){
			String s=dr.get("total")==null?"0":dr.get("total").toString();
			department.setTotal(Integer.parseInt(s));
			s=dr.get("count")==null?"0":dr.get("count").toString();
			department.setOnlinecount(department.getTotal()-Integer.parseInt(s));
			s=dr.get("sum")==null?"0":dr.get("sum").toString();
			department.setNotlong(Long.parseLong(s));
			if(department.getOnlinecount()>0&&department.getTotal()>0)
				department.setOnlinerate(
						Double.parseDouble(df.format(Double.parseDouble(department.getOnlinecount()+"")/department.getTotal())));
		}
		sql="select count(*) count from (select Videoflag from YFTIntact where ErrorDateTime>='%s' "
				+ " and ErrorDateTime<='%s' and MachineType='%s' "
				+ " union select SERVERFLAG from StorageStatistics where "
				+ " StartTime>='%s' and EndTime<='%s' and StorageType='%s') a";
		dt=DBQueryUtils.Select(String.format(sql, starttime,endtime,table,
				starttime,endtime,table), api);
		for(DataRow dr:dt.get_Rows()){
			String s=dr.get("count")==null?"0":dr.get("count").toString();
			department.setIntactcount(department.getTotal()-Integer.parseInt(s));
			if(department.getTotal()==0||department.getIntactcount()==0)
				department.setIntactrate(0);
			else
				department.setIntactrate(Double.parseDouble(df.format(Double.parseDouble(department.getIntactcount()+"")/department.getTotal())));
		}
		department.setArea(area);
		department.setAreacode(areacode);
		try {
			department.setDate(simp.parse(endtime));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return department;
	}
	
	/**
	 * 获取非视频类设备日报数据
	 * @param api
	 * @param starttime
	 * @param endtime
	 * @param type
	 * @param eqTypeEn
	 * @return
	 */
	public static List<ServerDetail> getServerDetailsDaily(ISiteviewApi api, Long starttime,
			Long endtime, String type, String eqTypeEn) {
		return ServerUtils.getServerDetailsFromES(api, starttime, endtime, type, eqTypeEn);
	}
	/**
	 * 结算当天视频类设备掉线与视频丢失数据
	 * @param api
	 * @param cal
	 * @param simp
	 */
	public static void yftVideo(ISiteviewApi api,Calendar cal,SimpleDateFormat simp){
		SimpleDateFormat simp0=new SimpleDateFormat("yyyy-MM-dd");
		String time2=simp0.format(cal.getTime())+" 00:00:01";
		cal.add(Calendar.DAY_OF_MONTH, -1);
		String time=simp0.format(cal.getTime())+" 23:59:58";
		String sql="select VIDEOFLAG,ONLINESTATUS,OFFLINETIME,IMAGELOSSTIME from VIDEOALARMRESULT  WHERE ONLINESTATUS!='1' OR IMAGELOSS!='1' OR SIGNALLOSS!='1' ";
		DataTable dt=DBQueryUtils.Select(sql, api);
		String whereoff="";
		String whereimge="";
		for(DataRow dr:dt.get_Rows()){
			String vidoflag=dr.get("VIDEOFLAG")==null?"":dr.get("VIDEOFLAG").toString();
			String onlinestatus=dr.get("ONLINESTATUS")==null?"0":dr.get("ONLINESTATUS").toString();
			String offlinetime=dr.get("OFFLINETIME")==null?"":dr.get("OFFLINETIME").toString();
			String imgelosstime=dr.get("IMAGELOSSTIME")==null?"":dr.get("IMAGELOSSTIME").toString();
			
			if(!onlinestatus.equals("1")){
				if(whereoff.length()>0)
					whereoff+=" or ";
				whereoff+=" VIDEOFLAG='"+vidoflag+"' ";
				if(offlinetime==null||offlinetime.length()==0||vidoflag.length()==0)
					continue;
				try {
					BusinessObject bo=api.get_BusObService().Create("VideoStatistics");
					bo.GetField("StartTime").SetValue(new SiteviewValue(offlinetime));
					bo.GetField("EndTime").SetValue(new SiteviewValue(time));
					bo.GetField("LostType").SetValue(new SiteviewValue("ONLINESTATUS"));
					bo.GetField("Videoflag").SetValue(new SiteviewValue(vidoflag));
					int duration=0;
					try {
						duration = (int) ((simp.parse(time).getTime()-simp.parse(offlinetime).getTime())/1000);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					bo.GetField("lostduration").SetValue(new SiteviewValue(duration));
					bo.SaveObject(api, true, true);
				} catch (SiteviewException e) {
					e.printStackTrace();
				}
			}else{
				if(whereimge.length()>0)
					whereimge+=" or ";
				whereimge+=" VIDEOFLAG='"+vidoflag+"' ";
				if(imgelosstime==null||offlinetime.length()==0)
					continue;
				try {
					BusinessObject bo=api.get_BusObService().Create("VideoStatistics");
					bo.GetField("StartTime").SetValue(new SiteviewValue(imgelosstime));
					bo.GetField("EndTime").SetValue(new SiteviewValue(time));
					bo.GetField("LostType").SetValue(new SiteviewValue("VIDEOLOSS"));
					bo.GetField("Videoflag").SetValue(new SiteviewValue(vidoflag));
					int duration=0;
					try {
						duration = (int) ((simp.parse(time).getTime()-simp.parse(imgelosstime).getTime())/1000);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					bo.GetField("lostduration").SetValue(new SiteviewValue(duration));
					bo.SaveObject(api, true, true);
				} catch (SiteviewException e) {
					e.printStackTrace();
				}
			}
		}
		if(whereoff.length()>0){
			DBQueryUtils.UpdateorDelete("update VIDEOALARMRESULT set OFFLINETIME='"+time2+"' where "+whereoff, api);
		}
		if(whereimge.length()>0){
			DBQueryUtils.UpdateorDelete("update  VIDEOALARMRESULT set IMAGELOSSTIME='"+time2+"' where "+whereimge, api);
		}
		yftServer(api, time, time2, simp);
	}
	/**
	 * 结算当天非视频类设备掉线数据
	 * @param api
	 * @param time
	 * @param time2
	 * @param simp
	 */
	public static void yftServer(ISiteviewApi api,String time,String time2,SimpleDateFormat simp){
		List<String> list=new ArrayList<String>();
		list.add("DVRALARMRESULT");
		list.add("NVRALARMRESULT");
		list.add("IPSANALARMRESULT");
		list.add("CODERALARMRESULT");
		for(String table:list){
			DataTable dt=DBQueryUtils.Select("SELECT SERVERFLAG,OFFLINETIME from "+table+" where NETBREAK!='1' ", api);
			String whereoff="";
			for(DataRow dr:dt.get_Rows()){
				String videoflag=dr.get("SERVERFLAG")==null?"":dr.get("SERVERFLAG").toString();
				String offtime=dr.get("OFFLINETIME")==null?"0":dr.get("OFFLINETIME").toString();
				if(whereoff.length()>0)
					whereoff+=" or ";
				whereoff+=" SERVERFLAG='"+videoflag+"' ";
				
				try {
					BusinessObject bo=api.get_BusObService().Create("StorageStatistics");//新建表dvr,nvr等断线
					bo.GetField("StartTime").SetValue(new SiteviewValue(offtime));
					int duration=0;
					try {
						duration = (int) ((simp.parse(time).getTime()-simp.parse(offtime).getTime())/1000);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					bo.GetField("lostduration").SetValue(new SiteviewValue(duration));
					bo.GetField("EndTime").SetValue(new SiteviewValue(time));
					bo.GetField("StorageType").SetValue(new SiteviewValue(table));
					bo.GetField("SERVERFLAG").SetValue(new SiteviewValue(videoflag));
					bo.SaveObject(api, true, true);
				} catch (SiteviewException e) {
					e.printStackTrace();
				}
			}
			if(whereoff.length()>0){
				DBQueryUtils.UpdateorDelete("update "+table+" set OFFLINETIME='"+time2+"' where "+whereoff, api);
			}
		}
	}
}
