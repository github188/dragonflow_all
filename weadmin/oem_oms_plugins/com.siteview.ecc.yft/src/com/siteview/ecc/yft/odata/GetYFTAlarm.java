package com.siteview.ecc.yft.odata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import com.siteview.ecc.yft.bean.YFTAlarm;
import com.siteview.utils.db.DBQueryUtils;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.Api.ISiteviewApi;
import siteview.IFunctionExtension;
/*
 * 获取英飞拓设备告警配置列表
 */
public class GetYFTAlarm implements IFunctionExtension {
	public static Map<String,String> hashmap=new HashMap<String,String>();
	static {
		hashmap.put("SIGNALLOSS", "信号丢失");
		hashmap.put("BRIGHT","偏亮");
		hashmap.put("IPADDRESS","IP地址");
		hashmap.put("SCREENSCROLL","滚屏");
		hashmap.put("COLORCOST","偏色");
		hashmap.put("STREAK","条纹");
		hashmap.put("SNOWFLAKE","雪花");
		hashmap.put("ALARMTIME","告警时间");
		hashmap.put("DIRECTIONTYPE","朝向");
		hashmap.put("PTZ","云台控制");
		hashmap.put("BASETYPE","设备类型");
		hashmap.put("DEFINITION","清晰度");
		hashmap.put("Image","图片链接");
		hashmap.put("MANUFACTURER","品牌	");
		hashmap.put("return","反馈");
		hashmap.put("video","视频链接");
		hashmap.put("FREEZE","冻结");
		hashmap.put("BRAND","型号");
		hashmap.put("DIM","偏暗");
		hashmap.put("SCREENSHAKE","抖屏");
		hashmap.put("ONLINESTATUS","在线");
		hashmap.put("IPLACE","安装地址");
		hashmap.put("VIDEONAME","摄像机名称");
		hashmap.put("IMAGELOSS","画面丢失");
		hashmap.put("COVERSTATUS","遮挡");
		hashmap.put("NETBREAK","状态");
		hashmap.put("IPERROR","IP地址冲突");
		hashmap.put("USERILLEGAL","非法访问");
		hashmap.put("OTHERERROR","其他错误");
		hashmap.put("ALARMTIME","告警时间");
		hashmap.put("DISKERRINFO","硬盘出错");
		hashmap.put("DISKFULLINFO","硬盘满");
		hashmap.put("DISKLOSTINFO","硬盘丢失");
		hashmap.put("INPUTOVERLOADCHAN","输入过载通道");
		hashmap.put("VIDEOERRCHAN","视频异常通道");
		hashmap.put("ENCODEERRCHAN","编码失败通道");
		hashmap.put("BANDWIDTHFULL","带宽占满");
		hashmap.put("RAIDFULLINFO","阵列满");
		hashmap.put("RAIDERRINFO","阵列出错");
		hashmap.put("RAIDLOSTINFO","阵列丢失损坏");
		hashmap.put("TEMPERATUREOVER","温度过高");
		hashmap.put("RECORD","录像");
		hashmap.put("VIDEOALARMRESULT", "摄像机");
		hashmap.put("DVRALARMRESULT", "DVR");
		hashmap.put("NVRALARMRESULT", "NVR");
		hashmap.put("CODERALARMRESULT", "编码器");
		hashmap.put("IPSANALARMRESULT", "IPSAN");
		hashmap.put("ONLINESTATUS", "离线");
	}
	public GetYFTAlarm() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api,
			Map<String, String> inputParamMap) {
		List<Map<String,String>> outparam=new ArrayList<Map<String,String>>();
		Map<String,String> outmap=new HashMap<String,String>();
		Map<String,YFTAlarm> yftalarmmap=new HashMap<String,YFTAlarm>();
		List<Map<String,String>> alarmconfig=new ArrayList<Map<String,String>>();
		List<YFTAlarm> yftAlarmlist=new ArrayList<YFTAlarm>();
		String sql="select * from YFTAlarm y LEFT JOIN (SELECT AlarmName,RecId from AlarmConfig)a on y.ALARMCONFIGRECID=a.RecId"; 
		DataTable dt=DBQueryUtils.Select(sql, api);
		for(DataRow dr:dt.get_Rows()){
			String operation=dr.get("operation")==null?"":dr.get("operation").toString();
			String storagetype=dr.get("storagetype")==null?"":dr.get("storagetype").toString();
			String status=dr.get("status")==null?"":dr.get("status").toString();
			String returnvalue=dr.get("returnvalue")==null?"":dr.get("returnvalue").toString();
			String returnitem=dr.get("returnitem")==null?"":dr.get("returnitem").toString();
			returnitem=hashmap.get(returnitem)==null?returnitem:hashmap.get(returnitem);
			String isor=dr.get("isor")==null?"":dr.get("isor").toString();
			returnitem+=" "+operation+" "+returnvalue;
			YFTAlarm yft=yftalarmmap.get(storagetype);
			if(yft==null){
				yft=new YFTAlarm();
				yftAlarmlist.add(yft);
			}
			yft.setType(hashmap.get(storagetype)==null?storagetype:hashmap.get(storagetype));
			yft.setAlarmconfig(dr.get("AlarmconfigRecId")==null?"":dr.get("AlarmconfigRecId").toString());
			yft.setAlarmname(dr.get("AlarmName")==null?"":dr.get("AlarmName").toString());
			if(status.equals("error")){
				String errora=yft.getErrorAlarm();
				if(errora==null)
					errora="";
				if(errora.length()>0)
					errora+=",";
				errora+=returnitem;
				yft.setErrorAlarm(errora);
				yft.setIsor_error(isor);
			}else if(status.equals("warning")){
				String errora=yft.getWarningAlarm();
				if(errora==null)
					errora="";
				if(errora.length()>0)
					errora+=",";
				errora+=returnitem;
				yft.setWarningAlarm(errora);
				yft.setIsor_warning(isor);
			}else{
				String errora=yft.getGoodAlarm();
				if(errora==null)
					errora="";
				if(errora.length()>0)
					errora+=",";
				errora+=returnitem;
				yft.setGoodAlarm(errora);
				yft.setIsor_good(isor);
			}
			yftalarmmap.put(storagetype, yft);
		}
		sql="select AlarmName,RecId from AlarmConfig";
		dt=DBQueryUtils.Select(sql, api);
		for(DataRow dr:dt.get_Rows()){
			HashMap<String,String> map=new HashMap<String, String>();
			map.put("recId", dr.get("RecId")==null?"":dr.get("RecId").toString());
			map.put("alarmName", dr.get("AlarmName")==null?"":dr.get("AlarmName").toString());
			alarmconfig.add(map);
		}
		outmap.put("ALARMCONFIG", JSONArray.fromObject(alarmconfig).toString());
		outmap.put("ALARMS", JSONArray.fromObject(yftAlarmlist).toString());
		outparam.add(outmap);
		return outparam;
	}

}
