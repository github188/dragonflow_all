package com.siteview.ecc.yft.odata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siteview.utils.db.DBQueryUtils;

import Siteview.SiteviewException;
import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;
import siteview.IFunctionExtension;
/*
 * 设置英飞拓设备告警配置
 */
public class SetYFTAlarm implements IFunctionExtension {

	public SetYFTAlarm() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api,
			Map<String, String> inputParamMap) {
		List<Map<String,String>> outparam=new ArrayList<Map<String,String>>();
		Map<String,String> outmap=new HashMap<String,String>();
		outmap.put("RETURN", "false");
		outparam.add(outmap);
		String storagetype=inputParamMap.get("STORAGETYPE");
		String isor_error=inputParamMap.get("ISOR_ERROR");
		String isor_warning=inputParamMap.get("ISOR_WARNING");
		String isor_good=inputParamMap.get("ISOR_GOOD");
		String erroralarm=inputParamMap.get("ERRORALARM");
		String warningalarm=inputParamMap.get("WARNINGALARM");
		String goodalarm=inputParamMap.get("GOODALARM");
		String alarmconfig=inputParamMap.get("ALARMCONFIGRECID")==null?"":inputParamMap.get("ALARMCONFIGRECID");
		String sql="delete from YFTAlarm where storagetype='%s'";
		DBQueryUtils.UpdateorDelete(String.format(sql, storagetype), api);
		if(erroralarm!=null&&erroralarm.length()>0)
			addYFTAlarm(storagetype, "error", erroralarm, isor_error, api,alarmconfig);
		if(warningalarm!=null&&warningalarm.length()>0)
			addYFTAlarm(storagetype, "warning", warningalarm, isor_warning, api,alarmconfig);
		if(goodalarm!=null&&goodalarm.length()>0)
			addYFTAlarm(storagetype, "good", goodalarm, isor_good, api,alarmconfig);
		outmap.put("RETURN", "true");
		return outparam;
	}
	public void addYFTAlarm(String type,String status,String alarm,String isor,ISiteviewApi api,String alarmconfig){
		if(alarm!=null){
			String[] alarms=alarm.split(",");
			for(String s:alarms){
				String returnitem=s.substring(0,s.indexOf(" "));
				String option=s.substring(s.indexOf(" ")+1,s.lastIndexOf(" "));
				String returnvalue=s.substring(s.lastIndexOf(" ")+1);
				try {
					BusinessObject bo=api.get_BusObService().Create("YFTAlarm");
					bo.GetField("returnitem").SetValue(new SiteviewValue(returnitem));
					bo.GetField("returnvalue").SetValue(new SiteviewValue(returnvalue));
					bo.GetField("operation").SetValue(new SiteviewValue(option));
					bo.GetField("storagetype").SetValue(new SiteviewValue(type));
					bo.GetField("status").SetValue(new SiteviewValue(status));
					bo.GetField("isor").SetValue(new SiteviewValue(isor));
					bo.GetField("AlarmConfigRecId").SetValue(new SiteviewValue(alarmconfig));
					bo.SaveObject(api, true, true);
				} catch (SiteviewException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
