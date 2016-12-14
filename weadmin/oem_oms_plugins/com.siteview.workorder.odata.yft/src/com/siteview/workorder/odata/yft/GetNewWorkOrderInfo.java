package com.siteview.workorder.odata.yft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siteview.utils.db.DBUtils;
import com.siteview.utils.html.StringUtils;

import Siteview.DataRow;
import Siteview.SiteviewException;
import Siteview.Api.ISiteviewApi;
import siteview.IFunctionExtension;

public class GetNewWorkOrderInfo implements IFunctionExtension {

	public GetNewWorkOrderInfo() {
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api, Map<String, String> inputParamMap) {
		List<Map<String, String>> functionListMap = new ArrayList<Map<String, String>>();
		Map<String, String> valueMap = new HashMap<String, String>();
		try {
			String table = inputParamMap.get("TABLENAME"); // 表名
			String keyword = inputParamMap.get("KEYWORD"); // 哪个字段代表序列号
			if (keyword == null)
				keyword = "SerialNumber";
			if (table != null)
				setSerialNumber(api, valueMap, table, keyword);

			setCurrentUserInfo(api, valueMap);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		functionListMap.add(valueMap);
		return functionListMap;
	}

	/**
	 * 设置当前人员信息
	 * 
	 * @param api
	 * @param valueMap
	 * @throws SiteviewException
	 */
	private void setCurrentUserInfo(ISiteviewApi api, Map<String, String> valueMap)
			throws SiteviewException {
		String sql = "SELECT * FROM Profile WHERE LoginID = '"
				+ api.get_AuthenticationService().get_CurrentLoginId() + "'";
		DataRow row = DBUtils.getDataRow(api, sql);
		// 获取电话
		String phone = "";
		String cellPhone = ""; // 手机
		String department = "";
		if (row != null) {
			cellPhone = StringUtils.getNotNullStr(row.get("Cellphone"));
			phone = StringUtils.getNotNullStr(row.get("Phone1Link"));
			department = StringUtils.getNotNullStr(row.get("DepartmentCode"));
		}
		valueMap.put("CELLPHONE", cellPhone); //手机
		valueMap.put("DEPARTMENT", department); //
		valueMap.put("PHONE", phone); //电话
	}

	/**
	 * 获取序列号
	 * 
	 * @param api
	 * @param valueMap
	 * @param table
	 * @param keyword
	 */
	private void setSerialNumber(ISiteviewApi api, Map<String, String> valueMap, String table,
			String keyword) {
		String sql = "SELECT * FROM " + table + " ORDER BY CreatedDateTime DESC";
		DataRow row = DBUtils.getDataRow(api, sql);
		int num = 1;
		if (row != null) {
			if (!"".equals(row.get(keyword))) {
				String serialNumber = row.get(keyword).toString(); // 序列号
				num = Integer.parseInt(serialNumber) + 1; // 序列号累加
			}
		}
		valueMap.put("SERIALNUMBER", num + "");
	}
}
