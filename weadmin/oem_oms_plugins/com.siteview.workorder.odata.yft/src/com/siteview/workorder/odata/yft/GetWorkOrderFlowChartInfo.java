package com.siteview.workorder.odata.yft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.rap.json.JsonObject;

import siteview.IFunctionExtension;
import Siteview.DataRow;
import Siteview.DataRowCollection;
import Siteview.DataTable;
import Siteview.Api.ISiteviewApi;

import com.siteview.utils.db.DBUtils;
import com.siteview.utils.workorder.WorkOrderUtils;

/**
 * 获取流程图相关信息
 * 
 * @author Administrator
 *
 */
public class GetWorkOrderFlowChartInfo implements IFunctionExtension {
	private DataRowCollection collection;

	public GetWorkOrderFlowChartInfo() {
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api, Map<String, String> inputParamMap) {
		List<Map<String, String>> functionListMap = new ArrayList<Map<String, String>>();
		Map<String, String> valueMap = new HashMap<String, String>();
		String id = inputParamMap.get("WORKORDER_ID");
		String name = inputParamMap.get("NAME"); //流程名称,用于新建的时候展示流程图
		try {
			String desc = "";
			if (id == null || "".equals(id)) {
				if ((name != null && !"".equals(name))) {
					desc = getAllChartInfoFromName(api, name);
				}
			} else {
				desc = getAllChartInfoFromId(api,id);
			}
			valueMap.put("DESC", desc);
			String logData = "";
			if(id != null && !"".equals(id))
				logData = getLogData(api, id);
			valueMap.put("LOG_DESC", logData);
			valueMap.put("PASS_DESC", getPassData());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		functionListMap.add(valueMap);
		return functionListMap;
	}

	private String getAllChartInfoFromId(ISiteviewApi api, String id) {
		String vv = "graph LR;";
		String sql = "SELECT wd.* FROM WorkOrderCommon wo,WorkFlowDetails wd,WorkFlow wf WHERE wo.WorkOrderType=wf.RecId AND"
				+ " wd.WorkFlowId = wf.RecId AND wo.RecId = '%s'"
				+ "  ORDER BY wd.SN";
		DataTable dataTable = DBUtils.select(String.format(sql, id), api);
		StringBuilder sb = new StringBuilder();
		for (DataRow row : dataTable.get_Rows()) {
			String fromId = row.get("FromId").toString();
			String toId = row.get("ToId").toString();
			sb.append(fromId).append("--").append(row.get("FlowAction")).append("-->").append(toId)
					.append(";");
		}

		return vv + sb.toString();
	}

	private String getAllChartInfoFromName(ISiteviewApi api, String name) {
		String vv = "graph LR;";
		String sql = "SELECT * FROM WorkFlowDetails wd,WorkFlow wf WHERE wd.WorkFlowId = wf.RecId "
				+ " AND wf.WorkOrderType = 'WorkOrderCommon' AND wf.Name ='%s’ ORDER BY wd.SN";
		DataTable dataTable = DBUtils.select(String.format(sql, name), api);
		StringBuilder sb = new StringBuilder();
		for (DataRow row : dataTable.get_Rows()) {
			String fromId = row.get("FromId").toString();
			String toId = row.get("ToId").toString();
			sb.append(fromId).append("--").append(row.get("FlowAction")).append("-->").append(toId)
					.append(";");
		}

		return vv + sb.toString();
	}

	/**
	 * 流程图信息
	 * 
	 * @return
	 */
	public String getAllChartInfo(ISiteviewApi api) {
		String vv = "graph LR;";

		String sql = "SELECT * FROM WorkFlowDetails wd,WorkFlow wf WHERE wd.WorkFlowId = wf.RecId "
				+ " AND wf.WorkOrderType = 'WorkOrderCommon' ORDER BY wd.SN";
		DataTable dataTable = DBUtils.select(sql, api);
		StringBuilder sb = new StringBuilder();
		for (DataRow row : dataTable.get_Rows()) {
			String fromId = row.get("FromId").toString();
			String toId = row.get("ToId").toString();
			sb.append(fromId).append("--").append(row.get("FlowAction")).append("-->").append(toId)
					.append(";");
		}

		return vv + sb.toString();
	}

	/*
	 * 鼠标放在流程图角色上后，弹出提示内容 说明 name 对象标识 gdid 事件 传递回来的参数 desc 详细描述 desc 换行 <br>
	 * worker 可以触发事件的名称
	 */
	private String getLogData(ISiteviewApi api, String orderId) {
		// JsonObject[] devdata = null;
		List<JsonObject> jsons = new ArrayList<JsonObject>();
		String sql = "SELECT * FROM WorkFlowLog WHERE WorkOrderId = '" + orderId
				+ "' ORDER BY LastModDateTime,CreatedDateTime";
		DataTable dataTable = DBUtils.select(sql, api);
		collection = dataTable.get_Rows();
		// devdata = new JsonObject[collection.size()];
		for (int i = 0; i < collection.size(); i++) {
			DataRow row = collection.get(i);
			String fromId = WorkOrderUtils.getNotNullObject(row.get("FromId"));
			String createdDateTime = WorkOrderUtils.getNotNullObject(row.get("CreatedDateTime"));
			String flowAction = WorkOrderUtils.getNotNullObject(row.get("FlowAction"));
			if (createdDateTime.contains("."))
				createdDateTime = createdDateTime.substring(0, createdDateTime.lastIndexOf("."));
			String fromUser = WorkOrderUtils.getNotNullObject(row.get("FromUser"));

			JsonObject jsonObject = new JsonObject().add("name", fromId).add("gdid", "gdid" + i)
					.add("desc", flowAction + "<br> 时间:" + createdDateTime).add("worker", fromUser);
			// devdata[i] = jsonObject;
			jsons.add(jsonObject);
		}

		return jsons.toString();
	}

	/**
	 * 走过的流程数据
	 * 
	 * @return
	 */
	private String getPassData() {
		List<JsonObject> jsons = new ArrayList<JsonObject>();
//		JsonObject jo = new JsonObject().add("name", "分服务台").add("state", 1);
//		jsons.add(jo);
		if (collection != null) {
			// statedata = new JsonObject[collection.size()];
			// 点亮走过的流程,分配的工单，分配人和接收人都点亮
			for (int i = 0; i < collection.size(); i++) {

				DataRow row = collection.get(i);
				String fromId = WorkOrderUtils.getNotNullObject(row.get("FromId"));
				String toId = WorkOrderUtils.getNotNullObject(row.get("ToId"));
				if (i == 0) { // 第一条日志
					JsonObject jo = new JsonObject().add("name", fromId).add("state", 1);
					jsons.add(jo);
					if (toId == null && "".equals(toId)) { // 没分配人
						JsonObject jsonObject = new JsonObject().add("name", fromId).add("state", 1);
						jsonObject.add("start", fromId);
						jsons.add(jsonObject);
					} else { //分配了人员
						JsonObject jsonObject = new JsonObject().add("name", toId).add("state", 1);
						jsonObject.add("start", fromId);
						jsons.add(jsonObject);
					}
				} else {
					if (toId != null && !"".equals(toId)) {
						JsonObject jsonObject = new JsonObject().add("name", toId).add("state", 1);
						jsonObject.add("start", fromId);
						jsons.add(jsonObject);
					}
				}
			}
		}
		return jsons.toString();
		// return jsons.toArray(new JsonObject[jsons.size()]).toString();

	}
}
