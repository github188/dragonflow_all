package com.siteview.ecc.yft.Servlet;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.siteview.ecc.yft.util.ODataUtils;
import com.siteview.ecc.yft.util.YFTAlarmEvent;
import com.siteview.utils.db.DBQueryUtils;
import com.siteview.utils.db.DBUtils;
import com.siteview.utils.workorder.WorkOrderUtils;
//摄像机接口
public class YFTVideo extends HttpServlet {
	ISiteviewApi api;
	private int i=0;
	private int j=0;
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	SimpleDateFormat simp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		i++;
		boolean flag=false;
		api=EccSchedulerManager.getCurrentSiteviewApi();
//		getRemoteHost(req);
		if(api==null)
			return;
		String videoflag=req.getParameter("VIDEOFLAG");//国标
		if(!YFTAlarmEvent.videos.contains(videoflag))
			YFTAlarmEvent.videos.add(videoflag);
		String online=req.getParameter("ONLINESTATUS");//在线状态
		String newimageloss=req.getParameter("IMAGELOSS");//图像丢失
		String newsignalloss=req.getParameter("SIGNALLOSS");//信号丢失
		String status=req.getParameter("STATUS");//当前状态 1,0 1代表正常 0代表异常 非断线，录像丢失
		Date d=new Date();
		Date today=new Date();
		today.setHours(0);
		today.setMinutes(0);
		today.setSeconds(0);
		String s=simp.format(d)+"--videoflag--"+videoflag+"--newonline--"+(online==null?"a":online)+"---newimageloss--"+(newimageloss==null?"b":newimageloss)
				+"--newsignalloss--"+(newsignalloss==null?"c":newsignalloss);
		String onlinetime=simp.format(d); 
		if(videoflag==null||(online==null&&newimageloss==null&&newsignalloss==null&&status==null)){
			resp.getWriter().append("error:Missing parameters");
			j++;
			return;
		}
		if(status!=null&&!status.equals("1")){//YFTIntact 完好,DateTime 日期,videoflag
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
					bo.GetField("MachineType").SetValue(new SiteviewValue("VIDEOALARMRESULT"));//设备类型
					bo.SaveObject(api, true, true);
				} catch (SiteviewException e) {
					e.printStackTrace();
				}
			}
		}
		String sql="select * from VIDEOALARMRESULT where VIDEOFLAG='"+videoflag+"'";
		String losstime=onlinetime;
		String videoname="";
		List<Disconnection> list=new ArrayList<Disconnection>();
		DataTable dt=DBQueryUtils.Select(sql, api);
		if(dt.get_Rows().size()==0){
			resp.getWriter().append("good");
			j++;
			return;
		}else{
			DataTable dt0=DBQueryUtils.Select(String.format("Select VIDEONAME from VIDEOPOINTINFO where VIDEOFLAG='%s'", videoflag), api);
			if(dt0.get_Rows().size()>0)
				videoname=dt0.get_Rows().get(0).get("VIDEONAME")==null?"":dt0.get_Rows().get(0).get("VIDEONAME").toString();
			DataRow dr=dt.get_Rows().get(0);
			String newonline=dr.get("ONLINESTATUS")==null?"":dr.get("ONLINESTATUS").toString();//在线
			String imageloss=dr.get("IMAGELOSS")==null?"":dr.get("IMAGELOSS").toString();//图像
			String signalloss=dr.get("SIGNALLOSS")==null?"":dr.get("SIGNALLOSS").toString();//信号
			String oldonlinetime=dr.get("OFFLINETIME")==null?"":dr.get("OFFLINETIME").toString();//需增加字段 断线开始时间
			String oldlosstime=dr.get("IMAGELOSSTIME")==null?"":dr.get("IMAGELOSSTIME").toString();//需增加字段 信号丢失开始时间
			String alarmtime=dr.get("ALARMTIME")==null?"":dr.get("ALARMTIME").toString();
			s+="--online--"+newonline+"---imageloss--"+imageloss+"--signalloss--"+signalloss
					+"--oldonlinetime-"+oldonlinetime+"--oldlosstime--"+oldlosstime;
			try {
				if(alarmtime.equals("")||alarmtime.length()==0||
						simp.parse(alarmtime).getTime()<today.getTime())
					alarmtime=simp.format(today);
			} catch (ParseException e1) {
//				e1.printStackTrace();
			}
			if(oldonlinetime.equals(""))
				oldonlinetime=alarmtime;
			if(oldlosstime.equals(""))
				oldlosstime=alarmtime;
			if(online!=null){
				if(!newonline.equals(online)){
					if(online.equals("1")){
						try {
							Date d0=simp.parse(oldonlinetime);
							int duration=(int) ((d.getTime()-d0.getTime())/1000);
							Disconnection dis=new Disconnection();
							dis.setDuration(duration);
							dis.setEndtime(onlinetime);
							dis.setStarttime(oldonlinetime);
							dis.setType("ONLINESTATUS");
							dis.setVideoflag(videoflag);
							list.add(dis);
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}else{
						sql="update VIDEOALARMRESULT set OFFLINETIME='"+simp.format(d)+"' where VIDEOFLAG='"+videoflag+"'";
						DBQueryUtils.UpdateorDelete(sql, api);
						s+="--sql--"+sql;
					}
				}else if(!newonline.equals(online)&&newonline.equals("0")){
					onlinetime=oldonlinetime;
					try {
						long ltime=today.getTime()-simp.parse(oldonlinetime).getTime();
						if(!flag)
							flag=AutomaticWorkOrder(videoflag,"摄像机","VIDEOALARMRESULT", "离线", ltime, api,videoname);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}else{
					onlinetime=oldonlinetime;
				}
			}else{
				try {
					long ltime=today.getTime()-simp.parse(oldonlinetime).getTime();
					if(!flag)
						flag=AutomaticWorkOrder(videoflag,"摄像机","VIDEOALARMRESULT", "离线", ltime, api,videoname);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			if(newimageloss!=null&&newsignalloss!=null){
				if((newimageloss.equals(imageloss)&&newsignalloss.equals(signalloss))
						||(newimageloss.equals(signalloss)&&newsignalloss.equals(imageloss))){
					losstime=oldlosstime;
				}else{
					if(newimageloss.equals("1")&&newsignalloss.equals("1")){
						try {
							Date d0 = simp.parse(oldlosstime);
							int duration=(int) ((d.getTime()-d0.getTime())/1000);
							Disconnection dis=new Disconnection();
							dis.setDuration(duration);
							dis.setEndtime(losstime);
							dis.setStarttime(oldlosstime);
							dis.setType("VIDEOLOSS");
							dis.setVideoflag(videoflag);
							list.add(dis);
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}else if((newimageloss.equals("0")||newsignalloss.equals("0"))&&imageloss.equals("1")&&signalloss.equals("1")){
						sql="update VIDEOALARMRESULT set IMAGELOSSTIME='"+simp.format(d)+"' where VIDEOFLAG='"+videoflag+"'";
						DBQueryUtils.UpdateorDelete(sql, api);
						s+="--sql--"+sql;
					}
				}
			}
			if((!"1".equals(newimageloss)||!"1".equals(newsignalloss))
					&&!"1".equals(imageloss)&&!"1".equals(signalloss)){
					try {
						long ltime=today.getTime()-simp.parse(oldlosstime).getTime();
						if(!"1".equals(newimageloss)&&!flag)
							flag=AutomaticWorkOrder(videoflag,"摄像机","VIDEOALARMRESULT", "画面丢失", ltime, api,videoname);
						else if(!"1".equals(newsignalloss)&&!flag)
							flag=AutomaticWorkOrder(videoflag,"摄像机","VIDEOALARMRESULT", "信号丢失", ltime, api,videoname);
					} catch (ParseException e) {
						e.printStackTrace();
					}
			}
			
			resp.getWriter().append("good");
//			s+="---list--"+(list.size()>0?(list.get(0).getVideoflag()+"--"+list.get(0).getStarttime()+list.get(0).getEndtime()+list.get(0).getType()):"0")+"--s--"+list.size();
			savaVideoStatistics(api, list);//43010300001310000625
//			System.out.println(s);
			if(online!=null&&online.length()>0){
				YFTAlarmEvent.getAlarm(api, videoflag, "ONLINESTATUS", "VIDEOALARMRESULT",online,flag);
			}else if(status!=null&&status.length()>0){
				YFTAlarmEvent.getAlarm(api, videoflag, status, "VIDEOALARMRESULT","0",flag);
			}else{
				if(newimageloss.equals("1")&&newsignalloss.equals("1"))
					YFTAlarmEvent.getAlarm(api, videoflag, "IMAGELOSS,SIGNALLOSS", "VIDEOALARMRESULT","1",flag);
				else 
					YFTAlarmEvent.getAlarm(api, videoflag, "IMAGELOSS,SIGNALLOSS", "VIDEOALARMRESULT","0",flag);
			}
		}
	}
	public void savaVideoStatistics(ISiteviewApi api,List<Disconnection> list){
		for(Disconnection d:list){
			try {
				BusinessObject bo=api.get_BusObService().Create("VideoStatistics");
				bo.GetField("StartTime").SetValue(new SiteviewValue(d.getStarttime()));
				bo.GetField("EndTime").SetValue(new SiteviewValue(d.getEndtime()));
				bo.GetField("LostType").SetValue(new SiteviewValue(d.getType()));
				bo.GetField("Videoflag").SetValue(new SiteviewValue(d.getVideoflag()));
				bo.GetField("lostduration").SetValue(new SiteviewValue(d.getDuration()));
				bo.SaveObject(api, true, true);
			} catch (SiteviewException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @param videoflag  国标
	 * @param machinetype 设备类型
	 * @param monitortype 监测类型
	 * @param ltime
	 * @param api
	 */
	public static boolean AutomaticWorkOrder(String videoflag,String machinetype,String machineTypeId,String monitortype,long ltime,ISiteviewApi api,String videoname){
		boolean flag=false;
		String sql="SELECT a.*,p.DisplayName,ss.GroupID FROM AutomaticWorkOrder a,Profile p,SiteviewSecMap ss where a.MonitorTypeName='%s' and a.EquipmentType='%s' AND a.CreatedBy=p.LoginID AND ss.LoginID = p.LoginID";
		DataTable dt=DBQueryUtils.Select(String.format(sql, monitortype,machineTypeId), api);
		
		//故障类别
		Map<String,String> fMap = getFaultMap(api,machinetype,monitortype);
		
		for (DataRow dr : dt.get_Rows()) {
			String operation = dr.get("Operation") == null ? "" : dr.get("Operation").toString();
			long longtime = Integer.parseInt(dr.get("Longtime") == null ? "0" : dr.get("Longtime")
					.toString());
			String desc=monitortype+" "+operation+" "+longtime+"(小时)";
			if (compareTime(longtime, operation, ltime)) {
				// 查询资产是否有对应的工单
				String sql0 = "SELECT * FROM WorkOrderCommon WHERE RecId in (SELECT WorkOrderId from WorkOrderAssets where AssetId in (SELECT RecId from HardwareAssets where GBCode='%s')) and Status!='gb' ";
				DataTable dt0 = DBQueryUtils.Select(String.format(sql0, videoflag), api);
				String id = "";
				for (DataRow dr0 : dt0.get_Rows()) {
					id = dr0.get("RecId").toString();
				}
				try {
					flag=true;
					BusinessObject bo = DBQueryUtils.queryOnlyBo("RecId", id, "WorkOrderCommon", api);
					if (bo == null) {
						String sql_1="SELECT RecId from HardwareAssets  WHERE GBCode='"+videoflag+"'";
						DataTable dt1=DBQueryUtils.Select(sql_1, api);
						String assetsid="";
						for(DataRow dr0:dt1.get_Rows()){
							assetsid=dr0.get("RecId")==null?"":dr0.get("RecId").toString();
						}
						bo = DBUtils.createBusinessObject("WorkOrderCommon", api);
						if(assetsid.length()>0){
							BusinessObject bo1=api.get_BusObService().Create("WorkOrderAssets");
							bo1.GetField("WorkOrderId").SetValue(new SiteviewValue(bo.get_RecId()));
							bo1.GetField("AssetId").SetValue(new SiteviewValue(assetsid));
							bo1.SaveObject(api, true, true);
						}
						WorkOrderUtils.setWorkOrderCount("add", videoflag, assetsid, api);
						bo.GetField("Status").SetValue(new SiteviewValue("cg"));
						bo.GetField("Subject").SetValue(new SiteviewValue("自动触发工单--"+videoname+" 故障"));
						bo.GetField("WorkOrderDesc").SetValue(new SiteviewValue(desc));
						DataRow typeRow = DBUtils.getDataRow(api,
								"SELECT RecId FROM WorkFlow WHERE WorkOrderType = 'WorkOrderCommon'");
						if (typeRow != null)
							bo.GetField("WorkOrderType").SetValue(
									new SiteviewValue(typeRow.get("RecId").toString()));// 工单流程
						int num = WorkOrderUtils.getSerialNumber(api, "WorkOrderCommon", "WorkOrderNumber");
						bo.GetField("WorkOrderNumber").SetValue(new SiteviewValue(num));
						bo.GetField("SerialNumber").SetValue(new SiteviewValue(num));

						bo.GetField("ParentId").SetValue(new SiteviewValue(dr.get("GroupID").toString()));
						 bo.GetField("FaultLevelId").SetValue(new SiteviewValue("低"));
						 if (fMap.get(machinetype) != null) {
								bo.GetField("FaultLarge").SetValue(new SiteviewValue(fMap.get(machinetype)
										==null?"":fMap.get(machinetype)));
								bo.GetField("FaultSmall").SetValue(
										new SiteviewValue(fMap.get(monitortype)==null?"":fMap.get(monitortype)));
								DataRow slaRow = DBUtils.getDataRow(api,
										"SELECT * FROM ServiceLevelAgreement WHERE ServiceCatalog = '" + fMap.get(machinetype)
										==null?"":fMap.get(machinetype)
												+ "'");
								if (slaRow != null)
									bo.GetField("SLAId").SetValue(new SiteviewValue(slaRow.get("RecId").toString()));
							}

						bo.GetField("CurrentHandle").SetValue(new SiteviewValue(dr.get("DisplayName").toString()));
						bo.SaveObject(api, true, true);
					}
				} catch (SiteviewException e) {
					e.printStackTrace();
				}
			}
		}
		return flag;
	}

	/**
	 * 比较规则
	 * @param longtime
	 * @param operation
	 * @param ltime
	 * @return
	 */
	public static boolean compareTime(long longtime, String operation, long ltime) {
		boolean flag = true;
		if (longtime == 0)
			flag = false;
		else if (operation.equals("==")) {
			flag = (longtime * 3600 * 1000) == ltime;
		} else if (operation.equals("!=")) {
			flag = (longtime * 3600 * 1000) != ltime;
		} else if (operation.equals(">=")) {
			flag = ltime>=(longtime * 3600 * 1000);
		} else if (operation.equals(">")) {
			flag = ltime>(longtime * 3600 * 1000) ;
		} else if (operation.equals("<=")) {
			flag =ltime<=(longtime * 3600 * 1000);
		} else if (operation.equals("<")) {
			flag = ltime<(longtime * 3600 * 1000);
		} else {
			flag = false;
		}
		return flag;
	}
	/**
	 * 获取故障大细类集合
	 * @param api
	 * @return
	 */
	public static Map<String, String> getFaultMap(ISiteviewApi api,String etype,String mtype) {
		Map<String, String> fMap = new HashMap<String,String>(); // 故障大类ID-Map<故障细类名称,故障细类ID>
		DataTable fDataTable = DBUtils.select(String.format("SELECT * FROM FaultClassification where FaultName='%s' or FaultName='%s' ORDER BY ParentID ASC ",mtype,etype), api);
		for (DataRow fRow : fDataTable.get_Rows()) {
			String fName = fRow.get("FaultName").toString();
			String fId = fRow.get("RecId").toString();
			String pId = fRow.get("ParentID") == null ? "" : fRow.get("ParentID").toString();
			if(fName.equals(etype)&&pId.length()==0){
				fMap.put(etype, fId);
			}else if(fName.equals(mtype)&&pId.equals(fMap.get(etype))){
				fMap.put(mtype, fId);
			}
		}
		return fMap;
	}
	public static String getRemoteHost(HttpServletRequest request){
	    String ip = request.getHeader("x-forwarded-for");
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
	        ip = request.getHeader("Proxy-Client-IP");
	    }
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
	        ip = request.getHeader("WL-Proxy-Client-IP");
	    }
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
	        ip = request.getRemoteAddr();
	    }
	    return ip.equals("0:0:0:0:0:0:0:1")?"127.0.0.1":ip;
	}
}
class Disconnection{//VideoStatistics
	private String starttime;//StartTime
	private String endtime;//EndTime
	private int duration;//lostduration
	private String videoflag;//Videoflag
	private String type;//LostType
	public String getStarttime() {
		return starttime;
	}
	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}
	public String getEndtime() {
		return endtime;
	}
	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public String getVideoflag() {
		return videoflag;
	}
	public void setVideoflag(String videoflag) {
		this.videoflag = videoflag;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}