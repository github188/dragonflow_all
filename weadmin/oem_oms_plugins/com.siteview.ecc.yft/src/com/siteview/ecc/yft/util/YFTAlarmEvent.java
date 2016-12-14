package com.siteview.ecc.yft.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.SiteviewException;
import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;

import com.siteview.ecc.alarm.AlertEventUtil;
import com.siteview.ecc.alarm.inter.SiteviewEccAlarmMgr;
import com.siteview.ecc.alarm.nls.AlarmMessage;
import com.siteview.ecc.alarm.tools.BoTools;
import com.siteview.ecc.datamanage.impl.MonitorLogImpl;
import com.siteview.utils.date.DateUtils;
import com.siteview.utils.db.DBQueryUtils;

public class YFTAlarmEvent {
	public static List<String> videos=new ArrayList<String>();
	public static Map<String,String> videomap=new HashMap<String,String>();
	public static Map<String,String> dvrmap=new HashMap<String,String>();
	public static Map<String,List<String>> storage=new HashMap<String,List<String>>();
	static{
		storage.put("DVRALARMRESULT", new ArrayList<String>());
		storage.put("NVRALARMRESULT", new ArrayList<String>());
		storage.put("CODERALARMRESULT", new ArrayList<String>());
		storage.put("IPSANALARMRESULT", new ArrayList<String>());
		
		videomap.put("SIGNALLOSS", "信号丢失");
		videomap.put("BRIGHT","偏亮");
		videomap.put("SCREENSCROLL","滚屏");
		videomap.put("COLORCOST","偏色");
		videomap.put("STREAK","条纹");
		videomap.put("SNOWFLAKE","雪花");
		videomap.put("DEFINITION","清晰度");
		videomap.put("FREEZE","冻结");
		videomap.put("DIM","偏暗");
		videomap.put("SCREENSHAKE","抖屏");
		videomap.put("ONLINESTATUS","离线");
		videomap.put("IMAGELOSS","画面丢失");
		videomap.put("COVERSTATUS","遮挡");
	
		videomap.put("NETBREAK","离线");
		videomap.put("IPERROR","IP地址冲突");
		videomap.put("USERILLEGAL","非法访问");
		videomap.put("OTHERERROR","其他错误");
		videomap.put("DISKERRINFO","硬盘出错");
		videomap.put("DISKFULLINFO","硬盘满");
		videomap.put("DISKLOSTINFO","硬盘丢失");
		videomap.put("VIDEOERRCHAN","视频异常通道");
		videomap.put("ENCODEERRCHAN","编码失败通道");
		videomap.put("INPUTOVERLOADCHAN","输入过载通道");
		videomap.put("OTHERERROR","其他错误");
		videomap.put("RAIDFULLINFO","阵列满");
		videomap.put("RAIDERRINFO","阵列出错");
		videomap.put("RAIDLOSTINFO","阵列丢失损坏");
		videomap.put("TEMPERATUREOVER","温度过高");
		videomap.put("BANDWIDTHFULL", "带宽占满");
	}
	public static String YFTAlarmStatus(String s,ISiteviewApi api,String type,List list){
		String sql="select * from YFTAlarm where storagetype='"+type+"' order by status";
		DataTable dt=DBQueryUtils.Select(sql, api);
		String _status="good";
		String alarmconfig="";
		for(DataRow dr:dt.get_Rows()){
			String status=dr.get("status")==null?"":dr.get("status").toString();
			String key=dr.get("returnitem")==null?"":dr.get("returnitem").toString();
			String value=dr.get("returnvalue")==null?"":dr.get("returnvalue").toString();
			String opreat=dr.get("operation")==null?"":dr.get("operation").toString();
			alarmconfig=dr.get("AlarmConfigRecId")==null?"":dr.get("AlarmConfigRecId").toString();
			if(!s.contains(key))
				continue;
			if(_status.equals("error"))
				return "error";
			if(status.equals("good"))
				continue;
			if(s.contains(key)){
				if(opreat.equals("==")){
					if(value.equals("0")){
						_status=status;
					}
				}else if(opreat.equals("!=")){
					if(!value.equals("0")){
						_status=status;
					}
				}else if(opreat.equals(">=")){
					try{
						double values=Double.parseDouble(value);
						if(values<0){
							_status=status;
						}
					}catch(Exception ex){
					}
				}else if(opreat.equals(">")){
					try{
						double values=Double.parseDouble(value);
						if(values>=0){
							_status=status;
						}
					}catch(Exception ex){
					}
				}else if(opreat.equals("<=")){
					try{
						double values=Double.parseDouble(value);
						if(values>0){
							_status=status;
						}
					}catch(Exception ex){
					}
				}else if(opreat.equals("<")){
					try{
						double values=Double.parseDouble(value);
						if(values>=0){
							_status=status;
						}
					}catch(Exception ex){
					}
				}else if(opreat.equals("contains")){
					if(value.equals("0")){
						_status=status;
					}
				}else if(opreat.equals("!contains")){
					if(!"0".contains(value)){
						_status=status;
					}
				}
			}
		}
		list.add(alarmconfig);
		return _status;
	}
	public static void getAlarm(ISiteviewApi api,String id,String errorname,String table,String status,boolean flag){
		if(table.equals("VIDEOALARMRESULT")){
			String[] video={"SIGNALLOSS","BRIGHT","FREEZE","DIM","SCREENSHAKE","IMAGELOSS","COVERSTATUS",
					"SCREENSCROLL","COLORCOST","STREAK","SNOWFLAKE","PTZ","DEFINITION","ONLINESTATUS"};
			setAlarm(video, "VIDEOFLAG", id, "VIDEOALARMRESULT", videomap, api,errorname,status,"VIDEOPOINTINFO","VIDEONAME",flag);
		}else if(table.equals("DVRALARMRESULT")){
			String[] dvr={"NETBREAK","IPERROR","DISKFULLINFO","DISKERRINFO","DISKLOSTINFO","VIDEOERRCHAN",
					"ENCODEERRCHAN","INPUTOVERLOADCHAN","OTHERERROR"};
			setAlarm(dvr, "SERVERFLAG", id, "DVRALARMRESULT", videomap, api,errorname,status,"SERVERINFO","SERVERNAME",flag);
		}else if(table.equals("NVRALARMRESULT")){
			String[] nvr={"NETBREAK","IPERROR","DISKFULLINFO","DISKERRINFO","DISKLOSTINFO","BANDWIDTHFULL",
					"OTHERERROR","USERILLEGAL"};
			setAlarm(nvr, "SERVERFLAG", id, "NVRALARMRESULT", videomap, api,errorname,status,"SERVERINFO","SERVERNAME",flag);
		}else if(table.equals("CODERALARMRESULT")){
			String[] code={"NETBREAK","IPERROR","USERILLEGAL","VIDEOERRCHAN","ENCODEERRCHAN"
					,"INPUTOVERLOADCHAN","OTHERERROR"};
			setAlarm(code, "SERVERFLAG", id, "CODERALARMRESULT", videomap, api,errorname,status,"SERVERINFO","SERVERNAME",flag);
		}else if(table.equals("IPSANALARMRESULT")){
			String[] ipsan={"NETBREAK","IPERROR","USERILLEGAL","RAIDFULLINFO","RAIDERRINFO"
					,"RAIDLOSTINFO","TEMPERATUREOVER","OTHERERROR"};
			setAlarm(ipsan, "SERVERFLAG", id, "IPSANALARMRESULT", videomap, api,errorname,status,"SERVERINFO","SERVERNAME",flag);
		}
	}
	public static void setAlarm(String[] fileds,String filedname,String id
			,String table,Map map,ISiteviewApi api,String errerfiled,String values,String table2,String ip,boolean flag){
		String sql="select t1.*,t2."+ip+" from "+table+" t1,"+table2+" t2 where t1."+filedname+"=('%s') and t2."+filedname+"='%s'";
		DataTable dt=DBQueryUtils.Select(String.format(sql, id,id), api);
		for(DataRow dr:dt.get_Rows()){
			String type="";
			String typecn="";
			String ipadd=dr.get(ip)==null?"":dr.get(ip).toString();
			for(String s:fileds){
				String key=s;
				String value=dr.get(key)==null?"1":dr.get(key).toString();
				if((!errerfiled.contains(key)&&value.equals("0"))||(errerfiled.contains(key)&&values.equals("0"))){
					if(type.length()>0){
						type+=",";
						typecn+=",";
					}
					type+=key;
					typecn+=map.get(key);
				}
			}
			List<String> list=new ArrayList<String>();
			String status=YFTAlarmStatus(type, api, table,list);
			setAlarmEvent(api, status, type, typecn, id,ipadd,table,flag,list.size()>0?list.get(0):"");
		}
	}
	
	public static void setAlarmEvent(ISiteviewApi api,String status,String type,String typecn,String id,String ip,String table,boolean flag,String alarmconfig){
		try {
			BusinessObject bo = DBQueryUtils.queryOnlyBo("EquipmentRecId", id, "AlarmEvent", api);
			String eqstatus="";
			if(status.equals("good")&&bo==null)
				return;
			if(status.equals("good")){
				String sql="delete from AlarmEventLog where EquipmentRecId='"+id+"'";
				DBQueryUtils.UpdateorDelete(sql, api);
				bo.DeleteObject(api);
//				bo1.DeleteObject(api);
				return;
			}
			if(bo!=null){
				eqstatus=bo.GetField("EquipmentStatus").get_NativeValue().toString();
			}else{
				bo=api.get_BusObService().Create("AlarmEvent");
				bo.GetField("EVENTSTATUS").SetValue(new SiteviewValue(1));
			}
			String sql="delete from AlarmEventLog WHERE EquipmentRecId='"+id+"'";
			String wheres="";
			String[] ss=typecn.split(",");
			String[] types=type.split(",");
			if(flag)
				bo.GetField("EVENTSTATUS").SetValue(new SiteviewValue(3));
			int count=0;
			for(int i=0;i<ss.length;i++)
			{	
				String s=ss[i];
				if(wheres.length()>0)
					wheres+=" , ";
				wheres+="'"+s+"'";
				Map<String,String> map=new HashMap<String,String>();
				map.put("EquipmentRecId", id);
				map.put("MonitorValue", s);
				BusinessObject bo1=DBQueryUtils.queryOnlyBo(map, "AlarmEventLog", api);
				if(bo1==null)
					bo1=api.get_BusObService().Create("AlarmEventLog");
				bo1.GetField("MonitorRecId").SetValue(new SiteviewValue(table));//设备类型
				count=bo1.GetField("MonitorCount").get_NativeValue().equals("")?0:Integer.parseInt(bo1.GetField("MonitorCount").get_NativeValue().toString());
				if(count>0)
					bo1.GetField("MonitorCount").SetValue(new SiteviewValue(count+1));
				bo1.GetField("MonitorType").SetValue(new SiteviewValue(types[i]));//偏亮，偏色等
				bo1.GetField("AlarmEventRecId").SetValue(new SiteviewValue(bo.get_RecId()));
				bo1.GetField("IsAlarm").SetValue(new SiteviewValue(1));
				bo1.GetField("EquipmentRecId").SetValue(new SiteviewValue(id));//国标
				bo1.GetField("AlarmStatus").SetValue(
						new SiteviewValue(bo.GetField("EVENTSTATUS").get_NativeValue().toString()));
				bo1.GetField("AlarmStatus").SetValue(new SiteviewValue(bo.GetField("EVENTSTATUS").get_NativeValue().toString()));
				bo1.GetField("MonitorValue").SetValue(new SiteviewValue(s));//中文
				bo1.GetField("MonitorName").SetValue(new SiteviewValue(s));//英文
				bo1.GetField("MonitorStatus").SetValue(new SiteviewValue(status));
				bo1.SaveObject(api, true, true);
			}
			if(wheres.length()>0)
				sql+=" and MonitorValue not in ("+wheres+")";
			DBQueryUtils.UpdateorDelete(sql, api);
			count=count+1;
			DBQueryUtils.UpdateorDelete(String.format("update AlarmEventLog set MonitorCount=%s where EquipmentRecId='%s' ",count,id), api);
			bo.GetField("AlarmConfigRecId").SetValue(new SiteviewValue(alarmconfig));
			bo.GetField("EquipmentIp").SetValue(new SiteviewValue(ip));
			bo.GetField("EquipmentStatus").SetValue(new SiteviewValue(status));
			bo.GetField("EquipmentRecId").SetValue(new SiteviewValue(id));
			bo.GetField("ALARMCONTENT").SetValue(new SiteviewValue(typecn));
			bo.SaveObject(api, true, true);
			if(alarmconfig.length()>0){
				Map map=Alarm(api, alarmconfig, count+"", ip, "", status, typecn, typecn, ip, id, table,bo);
				try {
					if(map!=null)
						MonitorLogImpl.insertAlarmLog(map, api);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
	}
	public static Map<String,String> Alarm(ISiteviewApi api,String alarmconfig,String count,
			String machineName,String groupname,String status,String monitorname,String message,String ip,String eqid,String type,BusinessObject alarmevent){
		try {
			BusinessObject alarmconfigbo=DBQueryUtils.queryOnlyBo("RecId", alarmconfig, "AlarmConfig", api);
			if(alarmconfigbo==null)
				return null;
			boolean alarmstatus=(boolean) alarmconfigbo.GetField("AlarmStatus").get_NativeValue();
			if(!alarmstatus)
				return null;
			boolean error=(boolean) alarmconfigbo.GetField("ErrorAlarm").get_NativeValue();
			boolean waring=(boolean) alarmconfigbo.GetField("WarningAlarm").get_NativeValue();
			int alarmfi=((Number)alarmconfigbo.GetField("AlarmFilter").get_NativeValue()).intValue();
			int start= ((Number)alarmconfigbo.GetField("AlarmStart").get_NativeValue()).intValue();
			int end= ((Number)alarmconfigbo.GetField("AlarmEnd").get_NativeValue()).intValue();
			boolean email=(boolean) alarmconfigbo.GetField("EamilAlarm").get_NativeValue();
			boolean sms=(boolean) alarmconfigbo.GetField("SMSAlarm").get_NativeValue();
			boolean wechar=(boolean) alarmconfigbo.GetField("WeChatAlarm").get_NativeValue();
			boolean sound=(boolean) alarmconfigbo.GetField("SoundAlarm").get_NativeValue();
			boolean script=(boolean) alarmconfigbo.GetField("ScriptAlarm").get_NativeValue();
			boolean istimes=false;
			if(alarmconfigbo.GetField("Times")!=null)
				istimes=alarmconfigbo.GetField("Times").get_NativeValue()==null?false:(boolean) alarmconfigbo.GetField("Times").get_NativeValue();
			String alarmName=alarmconfigbo.GetField("AlarmName").get_NativeValue().toString();
			boolean incident= false;
			if(alarmconfigbo.GetField("IsCreateIncident")!=null){
				incident =(boolean) alarmconfigbo.GetField("IsCreateIncident").get_NativeValue();
			}
			int stop=((Number)alarmconfigbo.GetField("StopTimes").get_NativeValue()).intValue();
			if((status.equalsIgnoreCase("error")&&error)||(status.equalsIgnoreCase("warning")&&waring)){
				if(istimes){
					int timenum=((Number)alarmconfigbo.GetField("TimesNum").get_NativeValue()).intValue();
					Date created=(Date) alarmevent.GetField("CreatedDateTime").get_NativeValue();
					Date last= (Date) alarmevent.GetField("LastModDateTime").get_NativeValue();
					long l=(last.getTime()-created.getTime())/3600000;
					if(timenum>l)
						return null;
					count="0";
				}
				else if(alarmfi==1){
					if(start>Double.parseDouble(count)||(stop!=0&&stop<=Double.parseDouble(count)))
						return null;
				}else if(alarmfi==2){
					if(start!=Double.parseDouble(count))
						return null;
				}else if(alarmfi==3){
					if(start>Double.parseDouble(count))
						return null;
					double ssend=(Double.parseDouble(count)-start)%end;
					if(ssend!=0)
						return null;
					if(stop!=0&&(((Double.parseDouble(count)-start)/end)+1)>=stop)
						return null;
				}
			}else if(status.equalsIgnoreCase("good")){
			}else{
				return null;
			}
			String alrmname=alarmconfigbo.GetField("AlarmName").get_NativeValue().toString();
			if(alrmname.equals("DEFAULT_ALARM"))
				alrmname=AlarmMessage.getvalue(alrmname, api.get_Locale());
			String isemial = "DO_NOT_SEND";
			String iswechar = "DO_NOT_SEND";
			String issound = "DO_NOT_SEND";
			String isscript = "DO_NOT_SEND";
			String issms = "DO_NOT_SEND";
			if (email) {
				String mailmodle = alarmconfigbo.GetField("MailTemplates").get_NativeValue().toString();
				String alarmLevel = alarmconfigbo.GetField("AlarmLevel").get_NativeValue().toString();
				List<String> userlist = new ArrayList<String>();
				List<String> uplist = new ArrayList<String>();
				StringBuilder pathSb = new StringBuilder();
				boolean flag = sendEmail(api, mailmodle, message, groupname, machineName, alarmName, alarmconfigbo,
						Integer.parseInt(count), alarmLevel, alarmfi, start, end, userlist, uplist, monitorname,
						status);
				if (flag)
					isemial = "SENT_SUCCESSFULLY";
				else if (userlist.size() == 0 && uplist.size() == 0)
					isemial = "DO_NOT_SEND";
				else
					isemial = "SENT_FAILURE";
			}
			if(incident){
//				incident(api, monitorbo, machinedt,status);
			}
			if (wechar) {
				String account = alarmconfigbo.GetField("WeChatUser").get_NativeValue().toString();
				boolean flag = sendWechar(api, monitorname, message, machineName, "", ip);
				if (flag)
					iswechar = "SENT_SUCCESSFULLY";
				else
					iswechar = "SENT_FAILURE";
			}
			if (sound) {
				boolean flag = AlertEventUtil.sendSound(api, alarmconfigbo);
				if (flag)
					issound = "SENT_SUCCESSFULLY";
				else
					issound = "SENT_FAILURE";
			}
			if (script) {
				boolean flag = AlertEventUtil.sendScript(api, alarmconfigbo);
				if (flag)
					isscript = "SENT_SUCCESSFULLY";
				else
					isscript = "SENT_FAILURE";
			}
			if (sms) {
				String mailmodle = alarmconfigbo.GetField("SMSTemplates").get_NativeValue().toString();
				String alarmLevel = alarmconfigbo.GetField("AlarmLevel").get_NativeValue().toString();
				List<String> userlist = new ArrayList<String>();
				List<String> uplist = new ArrayList<String>();
				boolean flag = sendSMS(api, isemial, issms, iswechar, issound, isscript, eqid, mailmodle, groupname,
						machineName, alarmName, alarmconfigbo, Integer.parseInt(count), alarmLevel, alarmfi, start, end,
						userlist, uplist, status, monitorname, message);
				if (flag)
					issms = "SENT_SUCCESSFULLY";
				else if (userlist.size() == 0 && uplist.size() == 0)
					issms = "DO_NOT_SEND";
				else
					issms = "SENT_FAILURE";
			} 
//			else {
				return AlertEventUtil.saveAlarmLog(alrmname, machineName, monitorname, isemial, issms, iswechar, issound, isscript, eqid,eqid,
						message, api, type, status);
//			}
//			ElasticSchemaUtil.saveAlarmLog(alrmname, machinedt.getString("title"),
//					monitorname, isemial, iswechar, issound, isscript,monitorbo.get_RecId(),machinedt.getString("RecId"));
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean sendEmail(ISiteviewApi api, String modleid, String message, String groupname,
			String machineName, String alarmName, BusinessObject alarmconfigbo, int count, String levent,
			int selecttype, int start, int end, List userlist, List uplist, String monitorname, String status) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar nowCalender = Calendar.getInstance();
			String content = "";
			DataRow dr = AlertEventUtil.getModelConext(modleid, api);
			if (dr != null) {
				content = dr.get("MailContent").toString();
				content = levent + ":<br/>" + content;
				content = content.replace("@FullPathGroup@", "" + "<br/>");
				content = content.replace("@AllGroup@", groupname + "<br/>");
				content = content.replace("@Group@", groupname);
				content = content.replace("@Monitor@", monitorname + "<br/>");
				content = content.replace("@Device@", machineName + "<br/>");
				content = content.replace("@Status@", status + "<br/>");
				content = content.replace("@Time@", dateFormat.format(nowCalender.getTime()) + "<br/>");
				content = content.replace("@MonitorAlertDes@", message);
			}
			BusinessObject sendMailbo = BoTools.CreateBo("MailType", "send", "EccMail", api);

			AlertEventUtil.getEmailOrSmsContent(alarmconfigbo, api, userlist, uplist, nowCalender, "Email");
			Map<String, Object> paramMap = new ConcurrentHashMap<String, Object>();
			paramMap.put("sendMailbo", sendMailbo);
			paramMap.put("content", content);
			boolean flag = false;
			// 升级次数发送邮件
			int promotionCount = ((Number) alarmconfigbo.GetField("UpgradeTimes").get_NativeValue()).intValue();
			if (selecttype == 3) {
				if (count == start)
					count = 1;
				else
					count = (count - start) / end + 1;
			}
			if (promotionCount != 0 && promotionCount <= count) { // 报警次数大于等于升级次数
				if (uplist.size() > 0) {
					paramMap.put("email", uplist);
					flag = SiteviewEccAlarmMgr.sendAlarm(SiteviewEccAlarmMgr.EMAIL, paramMap);
				}
			} else if (userlist.size() > 0) {
				paramMap.put("email", userlist);
				flag = SiteviewEccAlarmMgr.sendAlarm(SiteviewEccAlarmMgr.EMAIL, paramMap);
			}
			return flag;
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean sendSMS(ISiteviewApi api, String email, String sms, String wechat, String sound,
			String script, String equipmentid, String modleid, String groupname, String machineName, String alarmName,
			BusinessObject alarmconfigbo, int count, String levent, int selecttype, int start, int end, List userlist,
			List uplist, String status, String monitorname, String message) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar nowCalender = Calendar.getInstance();
			String content = "";
			DataRow dr = AlertEventUtil.getModelConext(modleid, api);
			if (dr != null) {
				content = dr.get("MailContent").toString();
				content = levent + ":" + content;
				content = content.replace("@FullPathGroup@", "");
				content = content.replace("@AllGroup@", groupname);
				content = content.replace("@Group@", groupname);
				content = content.replace("@Monitor@", monitorname);
				content = content.replace("@Device@", machineName);
				content = content.replace("@Status@", status);
				content = content.replace("@Time@", dateFormat.format(new Date()));
				content = content.replace("@MonitorAlertDes@", message);
			}
			
			// BusinessObject sendMailbo = BoTools.CreateBo("MailType", "send",
			// "EccMail", api);

			AlertEventUtil.getEmailOrSmsContent(alarmconfigbo, api, userlist, uplist,nowCalender, "Cellphone");
			Map<String, Object> paramMap = new ConcurrentHashMap<String, Object>();
			// paramMap.put("sendMailbo", sendMailbo);
			paramMap.put("content", content);
			paramMap.put("api", api);
			paramMap.put("alarmName", alarmName);
			paramMap.put("machineName", machineName);
			paramMap.put("monitorname", monitorname);
			paramMap.put("email", email);
			paramMap.put("wechat", wechat);
			paramMap.put("sms", sms);
			paramMap.put("sound", sound);
			paramMap.put("script", script);
			paramMap.put("monitorId", equipmentid);
			paramMap.put("equipmentid", equipmentid);
			paramMap.put("value", message);
			paramMap.put("type", "");
			paramMap.put("status", status);
			boolean flag = false;
			// 升级次数发送邮件
			int promotionCount = ((Number) alarmconfigbo.GetField("UpgradeTimes").get_NativeValue()).intValue();
			if (selecttype == 3) {
				if (count == start)
					count = 1;
				else
					count = (count - start) / end + 1;
			}
			if (promotionCount != 0 && promotionCount <= count) { // 报警次数大于等于升级次数
				if (uplist.size() > 0) {
					paramMap.put("telephone", uplist);
					flag = SiteviewEccAlarmMgr.sendSmsAlarm(SiteviewEccAlarmMgr.SMS, paramMap);
				}
			} else if (userlist.size() > 0) {
				paramMap.put("telephone", userlist);
				flag = SiteviewEccAlarmMgr.sendSmsAlarm(SiteviewEccAlarmMgr.SMS, paramMap);
			}
			return flag;
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean sendWechar(ISiteviewApi api, String monitorName, String message, String machineName,
			String id, String ip) {
		String language = "zh";
		boolean flag = false;
		try {
			String reson = "";
			Map<String, Object> map = new ConcurrentHashMap<String, Object>();
			map.put("account", "");
			map.put("belongId", "");
			map.put("machineName", machineName);
			map.put("machineId", id);
			map.put("equipmentIp", ip);
			map.put("monitorName", monitorName);
			map.put("reson", reson);
			map.put("message", message);
			map.put("date", DateUtils.formatDefaultDate(new Date()));
			flag = SiteviewEccAlarmMgr.sendAlarm(SiteviewEccAlarmMgr.WECHAT, map);
		} catch (SiteviewException e1) {
			e1.printStackTrace();
		} // 监测器名称
		return flag;
	}
}