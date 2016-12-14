package com.siteview.ecc.yft.odata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Siteview.DataColumnCollection;
import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.SiteviewException;
import Siteview.Api.ISiteviewApi;
import siteview.IFunctionExtension;

public class GetUserAlarmStaff implements IFunctionExtension {

	public GetUserAlarmStaff() {
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api, Map<String, String> inputParamMap) {
		List<Map<String, String>> listMap = new ArrayList<Map<String, String>>();
		String alarmConfigRecId = inputParamMap.get("ALARMRULEID");
		StringBuffer sql = new StringBuffer("SELECT AlarmStaff.*,Profile.FirstName AS UserName FROM AlarmStaff,Profile WHERE AlarmStaff.UserLogID = Profile.RecId ");
		if(alarmConfigRecId!=null&&alarmConfigRecId.trim().length()>0){
			sql.append(" and AlarmConfigRecId = '").append(alarmConfigRecId.trim()).append("'");
		}
		try {
			DataTable dt = api.get_NativeSQLSupportService().ExecuteNativeSQLQuery(sql.toString(), null);
			DataColumnCollection cols = dt.get_Columns();
			for(DataRow dr:dt.get_Rows()){
				Map<String,String> map = new HashMap<String,String>();
				for(Iterator iterator = cols.iterator();iterator.hasNext();){
					String key = iterator.next().toString();
					map.put(key, dr.get_Item(key).toString());
				}
				listMap.add(map);
			}
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
		return listMap;
	}

}
