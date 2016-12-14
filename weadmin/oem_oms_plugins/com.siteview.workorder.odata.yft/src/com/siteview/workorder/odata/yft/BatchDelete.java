package com.siteview.workorder.odata.yft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siteview.utils.db.DBUtils;

import Siteview.Api.ISiteviewApi;
import siteview.IFunctionExtension;

/**
 * 批量删除方法
 * 
 * @author Administrator
 *
 */
public class BatchDelete implements IFunctionExtension {

	public BatchDelete() {
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api, Map<String, String> inputParamMap) {
		List<Map<String, String>> functionListMap = new ArrayList<Map<String, String>>();
		Map<String, String> valueMap = new HashMap<String, String>();
		String flag = "false";
		try {
			String table = inputParamMap.get("TABLENAME");
			String key = inputParamMap.get("KEYWORD");
			String value = inputParamMap.get("VALUE");
			if (table != null && key != null && value != null) {
				String[] vs = value.split(",", -1);
				String deleteSql = "DELETE FROM " + table + " WHERE " + key + " IN(" + value + ")";
				int n = DBUtils.delete(deleteSql, api);
				if (n == vs.length)
					flag = "true";
			}
		} catch (Exception e) {
			flag = "false";
			System.out.println(e.getMessage());
		}
		valueMap.put("OUT_FLAG", flag);
		functionListMap.add(valueMap);
		return functionListMap;
	}

}
