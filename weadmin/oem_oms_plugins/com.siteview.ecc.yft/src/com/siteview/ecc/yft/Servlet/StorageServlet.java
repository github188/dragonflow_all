package com.siteview.ecc.yft.Servlet;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.SiteviewException;
import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;

import com.siteview.ecc.kernel.core.EccSchedulerManager;
import com.siteview.ecc.yft.util.YFTAlarmEvent;
import com.siteview.utils.db.DBQueryUtils;
//yft存储设备接口
public class StorageServlet extends HttpServlet {
	ISiteviewApi api;
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	SimpleDateFormat simp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {//DVRALARMRESULT,NVRALARMRESULT,CODERALARMRESULT,IPSANALARMRESULT
 		api=EccSchedulerManager.getCurrentSiteviewApi();
 		boolean flag=false;//是否生成过工单
 		if(api==null)
			return;
		String tabletype=req.getParameter("TYPE");//设备类型
		tabletype=tabletype.toUpperCase();
		String videoflag=req.getParameter("SERVERFLAG");//国标
		List list=YFTAlarmEvent.storage.get(tabletype);
		if(list!=null&&!list.contains(videoflag))
			list.add(videoflag);
		String sql="select * from %s where SERVERFLAG='"+videoflag+"'";
		String online=req.getParameter("NETBREAK");//在线状态NETBREAK
		String status=req.getParameter("STATUS");//当前状态 1,0 1代表正常 0代表异常 非断线，录像丢失
		if(videoflag==null||(online==null&&status==null&&tabletype==null)){
			resp.getWriter().append("error:Missing parameters");
			return;
		}
		Date d=new Date();
		String onlinetime=simp.format(d);
		if(status!=null){//YFTIntact 完好,DateTime 日期,videoflag
			String time=onlinetime.substring(0,10);
			String starttime=time+" 00:00:00";//新建表
			String sql0="select * from YFTIntact where ErrorDateTime>='%s' and Videoflag='%s' ";//
			DataTable dt0=DBQueryUtils.Select(String.format(sql0, starttime,videoflag), api);
			if(dt0.get_Rows().size()<=0){
				try {
					BusinessObject bo=api.get_BusObService().Create("YFTIntact");
					bo.GetField("Videoflag").SetValue(new SiteviewValue(videoflag));
					bo.GetField("ErrorDateTime").SetValue(new SiteviewValue(
							bo.GetField("CreatedDateTime").get_NativeValue()));
					bo.GetField("MachineType").SetValue(new SiteviewValue(tabletype));//设备类型
					bo.SaveObject(api, true, true);
				} catch (SiteviewException e) {
					e.printStackTrace();
				}
			}
		}
		DataTable dt=DBQueryUtils.Select(String.format(sql,tabletype.toUpperCase()), api);
		if(dt.get_Rows().size()==0){
			resp.getWriter().append("good");
			return;
		}else{
			Date today=new Date();
			today.setHours(0);
			today.setMinutes(0);
			today.setSeconds(0);
			String videoname="";
			DataTable dt0=DBQueryUtils.Select(String.format("select SERVERNAME from SERVERINFO  WHERE SERVERFLAG='%s'", videoflag), api);
			if(dt0.get_Rows().size()>0)
				videoname=dt0.get_Rows().get(0).get("SERVERNAME")==null?"":dt0.get_Rows().get(0).get("SERVERNAME").toString();
			DataRow dr=dt.get_Rows().get(0);
			String netbreak=dr.get("NETBREAK")==null?"0":dr.get("NETBREAK").toString();
			String offtime=dr.get("OFFLINETIME")==null?"":dr.get("OFFLINETIME").toString();
			String alarmtime=dr.get("ALARMTIME")==null?"":dr.get("ALARMTIME").toString();
			if(alarmtime.equals(""))
				alarmtime=simp.format(new Date());
			if(offtime.equals(""))
				offtime=alarmtime;
			try {
				if(simp.parse(offtime).getTime()<today.getTime())
					offtime=simp.format(today);
				if(online!=null){
					if(netbreak.equals("0")&&online.equals("1")){
						try {
							BusinessObject bo=api.get_BusObService().Create("StorageStatistics");//新建表dvr,nvr等断线
							bo.GetField("StartTime").SetValue(new SiteviewValue(offtime));
							try {
								Date d0=simp.parse(offtime);
								long l=d.getTime()-d0.getTime();
								bo.GetField("offlineduration").SetValue(new SiteviewValue(l/1000));
							} catch (ParseException e) {
								e.printStackTrace();
							}
							bo.GetField("EndTime").SetValue(new SiteviewValue(onlinetime));
							bo.GetField("StorageType").SetValue(new SiteviewValue(tabletype));
							bo.GetField("SERVERFLAG").SetValue(new SiteviewValue(videoflag));
							bo.SaveObject(api, true, true);
						} catch (SiteviewException e) {
							e.printStackTrace();
						}
					}else if(netbreak.equals("1")&&online.equals("0")){
						sql="update %s set OFFLINETIME='%s' where SERVERFLAG='%s'";
						DBQueryUtils.UpdateorDelete(String.format(sql, tabletype,onlinetime,videoflag), api);
					}
				}
				if(!"1".equals(netbreak)&&!"1".equals(online)){
					try {
						long ltime=today.getTime()-simp.parse(offtime).getTime();
						if(!flag)	
							flag=YFTVideo.AutomaticWorkOrder(videoflag,"摄像机",tabletype, "状态", ltime, api,videoname);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			resp.getWriter().append("good");
			if(online!=null&&online.length()>0){
				YFTAlarmEvent.getAlarm(api, videoflag, "NETBREAK", tabletype,online,flag);
			}else if(status!=null&&status.length()>0){
				YFTAlarmEvent.getAlarm(api, videoflag, status, tabletype,"0",flag);
			}
		}
	}
}
