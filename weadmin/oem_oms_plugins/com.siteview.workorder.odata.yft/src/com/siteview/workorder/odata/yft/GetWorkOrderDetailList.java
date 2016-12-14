package com.siteview.workorder.odata.yft;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siteview.utils.date.DateUtils;
import com.siteview.utils.db.DBUtils;
import com.siteview.utils.dictionary.Dictionary;
import com.siteview.utils.dictionary.DictionaryUtils;
import com.siteview.utils.html.StringUtils;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.SiteviewException;
import Siteview.Api.ISiteviewApi;
import siteview.IFunctionExtension;

public class GetWorkOrderDetailList implements IFunctionExtension {

	public GetWorkOrderDetailList() {
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api, Map<String, String> inputParamMap) {
		List<Map<String, String>> functionListMap = new ArrayList<Map<String, String>>();
		String param = inputParamMap.get("PARAM");
		try {
			String sql = "SELECT DISTINCT(a.WorkOrderId) FROM (SELECT DISTINCT(w.WorkOrderId) FROM WorkOrderCommon wc RIGHT JOIN (SELECT * FROM WorkFlowLog wl WHERE wl.CreatedBy='%s')w ON w.WorkOrderId=wc.RecId"
					+ " UNION (SELECT RecId d FROM WorkOrderCommon WHERE CreatedBy='%s' OR CurrentHandle = '"+api.get_AuthenticationService().get_CurrentUserDisplayName()+"') UNION (SELECT WorkOrderId FROM WorkOrderExtension WHERE CreatedBy='%s')) a";
			String createBy = api.get_AuthenticationService().get_CurrentLoginId();
			boolean isAdministrators = DBUtils.isAdministratorGroup(api);
			switch (param) {
			case "1": // 我参与的工单
				createIparticipatedOrder(api, functionListMap);
				break;
			case "2": // 待处理工单
				createPedingOrder(api, functionListMap);
				break;
			case "3": // 已完成工单
				createCompleteOrder(api, functionListMap, sql, createBy, isAdministrators);
				break;
			case "4": // 所有工单
				createAllOrder(api, functionListMap, sql, createBy, isAdministrators);
				break;
			case "5": // 超时工单
				createTimeOutOrder(api, functionListMap, sql, createBy, isAdministrators);
				break;
			case "6": // 不满意工单
				createNotSaOrder(api, functionListMap, sql, createBy, isAdministrators);
				break;
			case "7": // 未完成延期单
				createInCompExtensionOrder(api, functionListMap, sql, createBy, isAdministrators);
				break;
			case "8": // 已完成延期单
				createCompExtensionOrder(api, functionListMap, sql, createBy, isAdministrators);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return functionListMap;
	}

	/**
	 * 已完成延期单
	 * 
	 * @param api
	 * @param functionListMap
	 * @param isAdministrators
	 * @param createBy
	 * @throws SiteviewException
	 */
	private void createCompExtensionOrder(ISiteviewApi api,
			List<Map<String, String>> functionListMap, String cysql, String createBy,
			boolean isAdministrators) throws SiteviewException {
		if (isAdministrators) {
			String sql = "SELECT a.*,fc.FaultName FROM (SELECT wo.* FROM WorkOrderCommon wo WHERE wo.Status='gb' AND wo.RecId IN (SELECT WorkOrderId FROM WorkOrderExtension))a LEFT JOIN FaultClassification fc ON a.FaultLarge=fc.RecId ORDER BY a.CreatedDateTime DESC";
			setListMapValue(api, sql, functionListMap);
		} else {
			String sql = "SELECT a.*,fc.FaultName FROM (SELECT wo.* FROM WorkOrderCommon wo WHERE wo.RecId IN ("
					+ cysql
					+ ") AND wo.Status='gb' AND wo.RecId IN (SELECT WorkOrderId FROM WorkOrderExtension))a LEFT JOIN FaultClassification fc ON a.FaultLarge=fc.RecId ORDER BY a.CreatedDateTime DESC";
			setListMapValue(api, String.format(sql, createBy, createBy, createBy), functionListMap);
		}
	}

	/**
	 * 未完成延期单
	 * 
	 * @param api
	 * @param functionListMap
	 * @param isAdministrators
	 * @param createBy2
	 * @throws SiteviewException
	 */
	private void createInCompExtensionOrder(ISiteviewApi api,
			List<Map<String, String>> functionListMap, String cysql, String createBy,
			boolean isAdministrators) throws SiteviewException {
		if (isAdministrators) {
			String sql = "SELECT a.*,fc.FaultName FROM (SELECT wo.* FROM WorkOrderCommon wo WHERE wo.Status!='gb' AND wo.RecId IN (SELECT WorkOrderId FROM WorkOrderExtension))a LEFT JOIN FaultClassification fc ON a.FaultLarge=fc.RecId ORDER BY a.CreatedDateTime DESC";
			setListMapValue(api, sql, functionListMap);
		} else {
			String sql = "SELECT a.*,fc.FaultName FROM (SELECT wo.* FROM WorkOrderCommon wo WHERE wo.RecId IN ("
					+ cysql
					+ ") AND wo.Status!='gb' AND wo.RecId IN (SELECT WorkOrderId FROM WorkOrderExtension))a LEFT JOIN FaultClassification fc ON a.FaultLarge=fc.RecId ORDER BY a.CreatedDateTime DESC";
			setListMapValue(api, String.format(sql, createBy, createBy ,createBy), functionListMap);
		}
	}

	// 不满意工单
	private void createNotSaOrder(ISiteviewApi api, List<Map<String, String>> functionListMap,
			String cysql, String createBy, boolean isAdministrators) throws SiteviewException {
		if (isAdministrators) {
			String sql = "SELECT fc.FaultName,wo.* FROM (SELECT w.* FROM Satisfaction s , WorkOrderCommon w WHERE w.RecId = s.WorkOrderId AND (s.ServicesAging = 1 OR s.ServicesAging = 2)) wo"
					+ " LEFT JOIN FaultClassification fc ON wo.FaultLarge=fc.RecId ORDER BY wo.CreatedDateTime DESC";
			setListMapValue(api, sql, functionListMap);

		} else {
			String sql = "SELECT fc.FaultName,wo.* FROM (SELECT w.* FROM Satisfaction s , WorkOrderCommon w WHERE w.RecId IN ("
					+ cysql
					+ ") AND w.RecId = s.WorkOrderId AND (s.ServicesAging = 1 OR s.ServicesAging = 2)) wo"
					+ " LEFT JOIN FaultClassification fc ON wo.FaultLarge=fc.RecId ORDER BY wo.CreatedDateTime DESC";
			setListMapValue(api, String.format(sql, createBy, createBy, createBy), functionListMap);
		}
	}

	// 超时工单
	private void createTimeOutOrder(ISiteviewApi api, List<Map<String, String>> functionListMap,
			String cysql, String createBy, boolean isAdministrators) throws SiteviewException {
		String time = DateUtils.formatDefaultDate(new Date());
		if (isAdministrators) {
			String sql = "SELECT a.*,fc.FaultName FROM (SELECT wo.* FROM WorkOrderCommon wo WHERE wo.TimeOutNumber > 0 OR ((Status != 'wc' AND Status != 'gb') AND (AppointmentTime < '"+time+"' AND DispatchNumber > 0)))a LEFT JOIN FaultClassification fc ON a.FaultLarge=fc.RecId ORDER BY a.CreatedDateTime DESC";
			setListMapValue(api, sql, functionListMap);
		} else {
			String sql = "SELECT a.*,fc.FaultName FROM (SELECT wo.* FROM WorkOrderCommon wo WHERE wo.RecId IN ("
					+ cysql
					+ ") AND (wo.TimeOutNumber > 0 OR ((Status != 'wc' AND Status != 'gb') AND (AppointmentTime < '"+time+"' AND DispatchNumber > 0))) ORDER BY CreatedDateTime DESC)a LEFT JOIN FaultClassification fc ON a.FaultLarge=fc.RecId ORDER BY a.CreatedDateTime DESC";
			setListMapValue(api, String.format(sql, createBy, createBy, createBy), functionListMap);
		}
	}

	private void createAllOrder(ISiteviewApi api, List<Map<String, String>> functionListMap,
			String sql, String createBy, boolean isAdministrators) throws SiteviewException {
		createOrderDifStatus(api, functionListMap, null, sql, createBy, isAdministrators);
	}

	private void createCompleteOrder(ISiteviewApi api, List<Map<String, String>> functionListMap,
			String sql, String createBy, boolean isAdministrators) throws SiteviewException {
		createOrderDifStatus(api, functionListMap, "gb", sql, createBy, isAdministrators);
	}

	// 待处理工单列表数据
	private void createPedingOrder(ISiteviewApi api, List<Map<String, String>> functionListMap)
			throws SiteviewException {
		// createOrderDifStatus(api, functionListMap, "cl", sql);
		String sql = "SELECT a.*,fc.FaultName FROM (SELECT wo.* FROM WorkOrderCommon wo WHERE CurrentHandle = '"
				+ api.get_AuthenticationService().get_CurrentUserDisplayName()
				+ "'"
				+ " AND (Status = 'cl' OR Status='fp' OR Status='sqyqpf' OR Status='sqyq' OR Status='sqyqjj' OR Status='wc'))a LEFT JOIN FaultClassification fc ON a.FaultLarge=fc.RecId ORDER BY a.CreatedDateTime DESC";
		setListMapValue(api, sql, functionListMap);
	}

	/**
	 * 统计不同状态的工单列表数据
	 * 
	 * @param api
	 * @param functionListMap
	 * @param status
	 * @param cysql
	 * @param createBy
	 * @param isAdministrators
	 * @throws SiteviewException
	 */
	private void createOrderDifStatus(ISiteviewApi api, List<Map<String, String>> functionListMap,
			String status, String cysql, String createBy, boolean isAdministrators)
			throws SiteviewException {
		String sql = "";
		if (isAdministrators) {
			sql = "SELECT a.*,fc.FaultName FROM (SELECT wo.* FROM WorkOrderCommon wo ";
			if (status != null) {
				sql += " WHERE wo.Status='" + status + "'";
			}
			
		} else {
			sql = "SELECT a.*,fc.FaultName FROM (SELECT wo.* FROM WorkOrderCommon wo WHERE wo.RecId IN ("
					+ cysql + ")";
			if (status != null) {
				sql += " AND wo.Status='" + status + "'";
			}
		}
		sql += ")a LEFT JOIN FaultClassification fc ON a.FaultLarge=fc.RecId ORDER BY a.CreatedDateTime DESC";
		if (isAdministrators)
			setListMapValue(api, sql, functionListMap);
		else
			setListMapValue(api, String.format(sql, createBy, createBy, createBy), functionListMap);
	}

	private void setListMapValue(ISiteviewApi api, String sql,
			List<Map<String, String>> functionListMap) {
		DataTable dataTable = DBUtils.select(sql, api);
		for (DataRow row : dataTable.get_Rows()) {
			functionListMap.add(setMapValue(api, row));
		}
	}

	// 获取我参与的工单
	private void createIparticipatedOrder(ISiteviewApi api,
			List<Map<String, String>> functionListMap) throws SiteviewException {
		String sql = "SELECT DISTINCT(a.WorkOrderId) FROM (SELECT DISTINCT(w.WorkOrderId) FROM WorkOrderCommon wc RIGHT JOIN (SELECT * FROM WorkFlowLog wl WHERE wl.CreatedBy='%s')w ON w.WorkOrderId=wc.RecId"
				+ " UNION (SELECT RecId d FROM WorkOrderCommon WHERE CreatedBy='%s') UNION (SELECT WorkOrderId FROM WorkOrderExtension WHERE CreatedBy='%s')) a";
		String iparticipatedOrderSql = "SELECT wo.*,fc.FaultName FROM (SELECT w.* FROM (" + sql
				+ ")b LEFT JOIN WorkOrderCommon w ON b.WorkOrderId = w.RecId) wo"
				+ " LEFT JOIN FaultClassification fc ON wo.FaultLarge=fc.RecId ORDER BY wo.CreatedDateTime DESC";
		String createBy = api.get_AuthenticationService().get_CurrentLoginId();
		DataTable dataTable = DBUtils.select(String.format(iparticipatedOrderSql, createBy, createBy, createBy),
				api);
		for (DataRow row : dataTable.get_Rows()) {
			if (row.get("RecId") != null) {
				functionListMap.add(setMapValue(api, row));
			}
		}
	}

	// 给map设置表格数据
	private Map<String, String> setMapValue(ISiteviewApi api, DataRow row) {
		Map<String, String> valueMap = new HashMap<String, String>();
		valueMap.put("WORKORDER_ID", row.get("RecId").toString());
		valueMap.put("WORKORDERNUM", StringUtils.getNotNullStr(row.get("WorkOrderNumber")));
		valueMap.put("SUBJECT", StringUtils.getNotNullStr(row.get("Subject")));
		valueMap.put("CREATEBY", row.get("CreatedBy").toString());
		valueMap.put("PRIORITY", StringUtils.getNotNullStr(row.get("FaultLevelId")));
		valueMap.put("CREATEDATA_TIME",
				StringUtils.removeLastPoint(row.get("CreatedDateTime").toString()));
		valueMap.put("CURRENT_HANDLE", StringUtils.getNotNullStr(row.get("CurrentHandle")));
		valueMap.put("SP", StringUtils.getNotNullStr(row.get("ServiceContent"))); // 服务提供商
		valueMap.put("WORKORDERMARK", StringUtils.getNotNullStr(row.get("WorkOrderMark")).length()
				==0?"0":StringUtils.getNotNullStr(row.get("WorkOrderMark")));
		Dictionary statusDic = DictionaryUtils.getDicDataNameFromValue(api,
				StringUtils.getNotNullStr(row.get("Status")));
		String status = "";
		if (statusDic != null)
			status = statusDic.getDictDataName();
		valueMap.put("STATUS", status);
		valueMap.put("FAULT_TYPE", StringUtils.getNotNullStr(row.get("FaultName"))); // 故障类型
		String flowId = StringUtils.getNotNullStr(row.get("WorkOrderType"));
		valueMap.put("WORKFLOW_ID", flowId); // 流程ID
		String flowName = "";
		if(!"".equals(flowId)){
			DataRow flowRow = DBUtils.getDataRow(api, "SELECT * FROM WorkFlow WHERE RecId='"+flowId+"'");
			if(flowRow != null)
				flowName = StringUtils.getNotNullStr(flowRow.get("Name"));
		}
		valueMap.put("WORKFLOW_NAME", flowName); // 流程名称
		DataRow kbRow = DBUtils.getDataRow(api, "SELECT * FROM FaultKnowledgeBase WHERE WorkOrderId = '" + row.get("RecId").toString() + "'");
		String isAddKb = "";
		String kbId = "";
		if(kbRow != null) {
			isAddKb = "true";
			kbId = kbRow.get("RecId").toString();
		} else {
			isAddKb = "false";
		}
		valueMap.put("ISADD_KB", isAddKb);
		valueMap.put("KB_ID", kbId);
		return valueMap;
	}
}
