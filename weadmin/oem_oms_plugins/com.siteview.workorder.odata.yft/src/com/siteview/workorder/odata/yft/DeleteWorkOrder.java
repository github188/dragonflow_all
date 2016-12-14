package com.siteview.workorder.odata.yft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.siteview.utils.db.DBUtils;

import Siteview.Api.ISiteviewApi;
import siteview.IFunctionExtension;

/**
 * 删除工单
 * @author Administrator
 *
 */
public class DeleteWorkOrder implements IFunctionExtension {

	public DeleteWorkOrder() {
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api, Map<String, String> inputParamMap) {
		List<Map<String, String>> functionListMap = new ArrayList<Map<String, String>>();
		Map<String, String> valueMap = new HashMap<String, String>();
		String orderIds = inputParamMap.get("WORKORDER_ID");
		String flag = "false";
		try {
			if (orderIds != null && !"".equals(orderIds)) {
				JSONArray idArray = JSONArray.fromObject(orderIds);
				for(int i=0;i<idArray.size();i++){
					JSONObject jo = idArray.getJSONObject(i);
					String orderId = jo.getString("id");
					// 删除工单
					String deleteOrderSql = "DELETE FROM WorkOrderCommon WHERE RecId = '%s'";
					DBUtils.delete(String.format(deleteOrderSql, orderId), api);
					// 删除工单和延期单关联表
					String deleteOrderExtensionSql = "DELETE FROM WorkOrderExtension WHERE WorkOrderId = '%s'";
					DBUtils.delete(String.format(deleteOrderExtensionSql, orderId), api);
					// 删除工单和资产关联表
					String deleteOrderAssetsSql = "DELETE FROM WorkOrderAssets WHERE WorkOrderId = '%s'";
					DBUtils.delete(String.format(deleteOrderAssetsSql, orderId), api);
					// 删除工单满意度
					String deleteOrderSaSql = "DELETE FROM Satisfaction WHERE WorkOrderId = '%s'";
					DBUtils.delete(String.format(deleteOrderSaSql, orderId), api);
				}

				flag = "true";
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			flag = "false";
			valueMap.put("ERROR_MSG", e.getMessage());
		}
		valueMap.put("OUT_FLAG", flag);
		functionListMap.add(valueMap);
		return functionListMap;
	}

}
