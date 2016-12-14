package com.siteview.workorder.odata.yft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siteview.utils.db.DBUtils;

import Siteview.DataRow;
import Siteview.Api.ISiteviewApi;
import siteview.IFunctionExtension;

/**
 * 获取自增长序列号
 * 
 * @author Administrator
 *
 */
public class GetAutoIncrementSerialNumber implements IFunctionExtension {

	public GetAutoIncrementSerialNumber() {
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api, Map<String, String> inputParamMap) {
		List<Map<String, String>> functionListMap = new ArrayList<Map<String, String>>();
		Map<String, String> valueMap = new HashMap<String, String>();
		try {
			String table = inputParamMap.get("TABLENAME"); //表名
			String keyword = inputParamMap.get("KEYWORD"); //哪个字段代表序列号
			if (keyword == null)
				keyword = "SerialNumber";
			String sql = "SELECT * FROM " + table + " ORDER BY CreatedDateTime DESC";
			DataRow row = DBUtils.getDataRow(api, sql);
			int num = 1;
			if (row != null) {
				String serialNumber = row.get(keyword).toString(); // 序列号
				num = Integer.parseInt(serialNumber) + 1; // 序列号累加
			}
			valueMap.put("SERIALNUMBER", num + "");
		} catch (Exception e) {
			e.getMessage();
		}
		functionListMap.add(valueMap);
		return functionListMap;
	}

}
