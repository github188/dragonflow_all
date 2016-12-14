package com.siteview.ecc.yft.odata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import com.siteview.utils.db.DBQueryUtils;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.Api.ISiteviewApi;
import siteview.IFunctionExtension;
/*
 * 获取告警规则配置界面数据
 */
public class GetAlarmConfigUI implements IFunctionExtension {

	public GetAlarmConfigUI() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api,
			Map<String, String> inputParamMap) {
		List<Map<String,String>> outp=new ArrayList<Map<String,String>>();
		Map<String,String> outmap=new HashMap<String, String>();
		List<Map> smsmodle=new ArrayList<Map>();
		List<Map> mailmodle=new ArrayList<Map>();
		String sql="SELECT * from EccMailModle";
		DataTable dt=DBQueryUtils.Select(sql, api);
		for(DataRow dr:dt.get_Rows()){
			Map<String,String> map=new HashMap<String,String>();
			String type=dr.get("ModleType")==null?"":dr.get("ModleType").toString();
			map.put("RecId", dr.get("RecId")==null?"":dr.get("RecId").toString());
			map.put("ModleType", type);
			map.put("MailTitle", dr.get("MailTitle")==null?"":dr.get("MailTitle").toString());
			map.put("MailContent", dr.get("MailContent")==null?"":dr.get("MailContent").toString());
			if(type.equalsIgnoreCase("SMS")){
				smsmodle.add(map);
			}else if(type.equalsIgnoreCase("email")){
				mailmodle.add(map);
			}
		}
//		sql="SELECT DisplayName,LoginID,RecId FROM `Profile`";
//		dt=DBQueryUtils.Select(sql, api);
//		List<Map> userlist=new ArrayList<Map>();
//		for(DataRow dr:dt.get_Rows()){
//			Map<String,String> map=new HashMap<String,String>();
//			map.put("USER_ID", dr.get("RecId")==null?"":dr.get("RecId").toString());
//			map.put("DisplayName", dr.get("DisplayName")==null?"":dr.get("DisplayName").toString());
//			map.put("LoginID", dr.get("LoginID")==null?"":dr.get("LoginID").toString());
//			userlist.add(map);
//		}
		outmap.put("SMSTEMPLATE", JSONArray.fromObject(smsmodle).toString());
		outmap.put("MAILTEMPLATE", JSONArray.fromObject(mailmodle).toString());
		outmap.put("USER",""); //JSONArray.fromObject(userlist).toString());
		outp.add(outmap);
		return outp;
	}

}
