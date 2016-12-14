package com.siteview.workorder.odata.yft;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import siteview.IFunctionExtension;
import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.SiteviewException;
import Siteview.Api.ISiteviewApi;

import com.siteview.utils.date.DateUtils;
import com.siteview.utils.db.DBUtils;
import com.siteview.workorder.odata.yft.entities.CountObject;

/**
 * 获取工单首页统计数据
 * 
 * @author Administrator
 *
 */
public class GetOrderCount implements IFunctionExtension {

	public GetOrderCount() {
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api, Map<String, String> inputParamMap) {
		List<Map<String, String>> functionListMap = new ArrayList<Map<String, String>>();
		// String details = inputParamMap.get("DETAILS");
		String createBy = inputParamMap.get("CREATEBY");
		Map<String, String> valueMap = new HashMap<String, String>();
		try {
			// 我参与了的工单sql
			String cysql = "SELECT DISTINCT(a.WorkOrderId) FROM (SELECT DISTINCT(w.WorkOrderId) FROM WorkOrderCommon wc RIGHT JOIN (SELECT * FROM WorkFlowLog wl WHERE wl.CreatedBy='%s')w ON w.WorkOrderId=wc.RecId"
					+ " UNION (SELECT RecId d FROM WorkOrderCommon WHERE CreatedBy='%s' OR CurrentHandle = '"+api.get_AuthenticationService().get_CurrentUserDisplayName()+"') UNION (SELECT DISTINCT(we.WorkOrderId) FROM WorkOrderCommon wc LEFT JOIN WorkOrderExtension we ON wc.RecId=we.WorkOrderId AND we.CreatedBy='%s')) a";
			boolean isAdministrators = DBUtils.isAdministratorGroup(api);
			// 待处理工单人员排序,所有人看到的都一样,都是统计所有待处理的工单,然后排序
			createPendingOrderSort(api, valueMap, createBy, isAdministrators);

			// 故障工单类型饼状图数据,所有人看到的都一样,都是统计所有的工单,然后分类
			createPieOrderData(api, valueMap, createBy, isAdministrators);

			// 我参与的工单
			createInparticipatedOrder(api, valueMap, createBy);

			// 统计待处理工单-状态为待处理;已完成工单-状态为关闭状态;所有工单
			createPendCompAllOrder(api, valueMap, cysql, createBy, isAdministrators);

			// 统计延期和未延期工单
			createExtensionOrder(api, valueMap, cysql, createBy, isAdministrators);

			// 超时工单
			createTimeoutOrder(api, valueMap, cysql, createBy, isAdministrators);

			// 不满意工单
			createNotSaOrder(api, valueMap, cysql, createBy, isAdministrators);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		functionListMap.add(valueMap);
		return functionListMap;
	}

	/**
	 * 统计延期和未延期工单
	 * 
	 * @param api
	 * @param valueMap
	 * @param createBy
	 * @param cysql
	 * @param isAdministrators
	 */
	private void createExtensionOrder(ISiteviewApi api, Map<String, String> valueMap, String cysql,
			String createBy, boolean isAdministrators) {
		String gbEcount = "0"; // 已完成延期单
		String ngbEcount = "0"; // 未完成延期单
		String sql = "SELECT a.gbCount,b.ngbCount FROM (SELECT COUNT(*) gbCount,1 num FROM WorkOrderCommon wc WHERE WC.RecId IN("
				+ cysql
				+ ") AND wc.Status='gb' AND wc.RecId IN (SELECT WorkOrderId FROM WorkOrderExtension)) a"
				+ " LEFT JOIN (SELECT COUNT(*) ngbCount,1 num FROM WorkOrderCommon wc WHERE RecId IN("
				+ cysql
				+ ") AND wc.Status!='gb' AND wc.RecId IN (SELECT WorkOrderId FROM WorkOrderExtension)) b"
				+ " ON a.num = b.num";
		DataRow countRow = null;
		if (isAdministrators) {
			sql = "SELECT a.gbCount,b.ngbCount FROM (SELECT COUNT(*) gbCount,1 num FROM WorkOrderCommon wc WHERE wc.Status='gb' AND wc.RecId IN (SELECT WorkOrderId FROM WorkOrderExtension)) a"
					+ " LEFT JOIN (SELECT COUNT(*) ngbCount,1 num FROM WorkOrderCommon wc WHERE wc.Status!='gb' AND wc.RecId IN (SELECT WorkOrderId FROM WorkOrderExtension)) b"
					+ " ON a.num = b.num";
			countRow = DBUtils.getDataRow(api, sql);
		} else {
			sql = "SELECT a.gbCount,b.ngbCount FROM (SELECT COUNT(*) gbCount,1 num FROM WorkOrderCommon wc WHERE wc.RecId IN("
					+ cysql
					+ ") AND wc.Status='gb' AND wc.RecId IN (SELECT WorkOrderId FROM WorkOrderExtension)) a"
					+ " LEFT JOIN (SELECT COUNT(*) ngbCount,1 num FROM WorkOrderCommon wc WHERE RecId IN("
					+ cysql
					+ ") AND wc.Status!='gb' AND wc.RecId IN (SELECT WorkOrderId FROM WorkOrderExtension)) b"
					+ " ON a.num = b.num";
			countRow = DBUtils
					.getDataRow(api, String.format(sql, createBy, createBy, createBy, createBy, createBy, createBy));
		}
		if (countRow != null) {
			gbEcount = countRow.get("gbCount").toString();
			ngbEcount = countRow.get("ngbCount").toString();
		}
		valueMap.put("COMPLETE_EX_COUNT", gbEcount);
		valueMap.put("INCOMPLETE_EX_COUNT", ngbEcount);
	}

	/**
	 * 故障工单类型饼状图数据
	 * 
	 * @param api
	 * @param valueMap
	 * @param cysql
	 * @param createBy
	 * @param isAdministrators
	 * @throws SiteviewException
	 */
	private void createPieOrderData(ISiteviewApi api, Map<String, String> valueMap, 
			String createBy, boolean isAdministrators) throws SiteviewException {
		String sql = "";
		DataTable dataTable = null;
//		if (isAdministrators) {
			sql = "SELECT fc.FaultName,a.c FROM (SELECT FaultLarge,COUNT(FaultLarge) c FROM WorkOrderCommon GROUP BY FaultLarge)a LEFT JOIN FaultClassification fc ON a.FaultLarge = fc.RecId";
			dataTable = DBUtils.select(sql, api);
//		} else {
//			sql = "SELECT fc.FaultName,a.c FROM (SELECT FaultLarge,COUNT(FaultLarge) c FROM WorkOrderCommon WHERE RecId IN("
//					+ cysql
//					+ ") GROUP BY FaultLarge)a LEFT JOIN FaultClassification fc ON a.FaultLarge = fc.RecId";
//			dataTable = DBUtils.select(String.format(sql, createBy, createBy), api);
//		}
		valueMap.put("FAULTTYPE_COUNT",
				JSONArray.fromObject(CommonUtils.getCountObjects(dataTable, "FaultName")).toString());
	}

	/**
	 * 统计不满意工单个数
	 * 
	 * @param api
	 * @param valueMap
	 * @param createBy
	 * @param cysql
	 * @param isAdministrators
	 */
	private void createNotSaOrder(ISiteviewApi api, Map<String, String> valueMap, String cysql,
			String createBy, boolean isAdministrators) {
		String saSql = "";
		DataRow countRow = null;
		if (isAdministrators) {
			saSql = "SELECT COUNT(s.RecId) c FROM Satisfaction s , WorkOrderCommon w WHERE w.RecId = s.WorkOrderId AND (s.ServicesAging = 1 OR s.ServicesAging = 2)";
			countRow = DBUtils.getDataRow(api, saSql);
		} else {
			saSql = "SELECT COUNT(s.RecId) c FROM Satisfaction s , WorkOrderCommon w WHERE w.RecId IN("
					+ cysql
					+ ") AND w.RecId = s.WorkOrderId AND (s.ServicesAging = 1 OR s.ServicesAging = 2)";
			countRow = DBUtils.getDataRow(api, String.format(saSql, createBy, createBy, createBy));
		}
		String sacount = "0"; // 不满意工单个数
		if (countRow != null) {
			sacount = countRow.get("c").toString();
		}
		valueMap.put("NOT_SATISFIED_COUNT", sacount); // 不满意工单个数
	}

	private void createTimeoutOrder(ISiteviewApi api, Map<String, String> valueMap, String cysql,
			String createBy, boolean isAdministrators) {
		String timeoutcount = "0"; // 超时工单个数
		String timeoutSql = "";
		DataRow countRow = null;
		String time = DateUtils.formatDefaultDate(new Date());
		//关闭和完成状态的情况不判断预计解决时间
		if (isAdministrators) {
			timeoutSql = "SELECT COUNT(*) c FROM WorkOrderCommon WHERE TimeOutNumber > 0 OR ((Status != 'wc' AND Status != 'gb') AND (AppointmentTime < '"+time+"' AND DispatchNumber > 0))";
			countRow = DBUtils.getDataRow(api, timeoutSql);
		} else {
			timeoutSql = "SELECT COUNT(*) c FROM WorkOrderCommon WHERE RecId IN(" + cysql
					+ ") AND (TimeOutNumber > 0 OR ((Status != 'wc' AND Status != 'gb') AND (AppointmentTime < '"+time+"' AND DispatchNumber > 0)))";
			countRow = DBUtils.getDataRow(api, String.format(timeoutSql, createBy, createBy, createBy));

		}
		if (countRow != null) {
			timeoutcount = countRow.get("c").toString();
		}
		valueMap.put("TIMEOUT_COUNT", timeoutcount); // 超时个数
	}

	/**
	 * 统计待处理工单-状态为待处理;已完成工单-状态为关闭状态;所有工单
	 * 
	 * @param api
	 * @param valueMap
	 * @param cysql
	 * @param isAdministrators
	 * @param createBy2
	 * @throws SiteviewException
	 */
	private void createPendCompAllOrder(ISiteviewApi api, Map<String, String> valueMap, String cysql,
			String createBy, boolean isAdministrators) throws SiteviewException {
		String clcount = "0"; // 待处理
		String gbcount = "0"; // 完成工单
		String allcount = "0"; // 所有工单

		String pendingSql = "";
		DataRow countRow = null;
		if (isAdministrators) {
			pendingSql = "SELECT c.clCount,c.gbCount,d.allCount FROM("
					+ "SELECT a.clCount,b.gbCount,1 num FROM "
					+ "(SELECT COUNT(*) clCount,1 num FROM WorkOrderCommon WHERE (Status = 'cl' OR Status='fp' OR Status='sqyqpf' OR Status='sqyq' OR Status='sqyqjj' OR Status='wc') AND CurrentHandle = '"
					+ api.get_AuthenticationService().get_CurrentUserDisplayName()
					+ "')a"
					+ " LEFT JOIN (SELECT COUNT(*) gbCount,1 num FROM WorkOrderCommon WHERE Status='gb') b ON a.num = b.num) c"
					+ " LEFT JOIN (SELECT COUNT(*) allCount,1 num FROM WorkOrderCommon) d ON c.num=d.num";
			countRow = DBUtils.getDataRow(api, pendingSql);
		} else {
			pendingSql = "SELECT c.clCount,c.gbCount,d.allCount FROM("
					+ "SELECT a.clCount,b.gbCount,1 num FROM "
					+ "(SELECT COUNT(*) clCount,1 num FROM WorkOrderCommon WHERE (Status = 'cl' OR Status='fp' OR Status='sqyqpf' OR Status='sqyq' OR Status='sqyqjj' OR Status='wc') AND CurrentHandle = '"
					+ api.get_AuthenticationService().get_CurrentUserDisplayName() + "')a"
					+ " LEFT JOIN (SELECT COUNT(*) gbCount,1 num FROM WorkOrderCommon WHERE RecId IN("
					+ cysql + ") AND Status='gb') b ON a.num = b.num) c"
					+ " LEFT JOIN (SELECT COUNT(*) allCount,1 num FROM WorkOrderCommon WHERE RecId IN("
					+ cysql + ")) d ON c.num=d.num";
			countRow = DBUtils.getDataRow(api,
					String.format(pendingSql, createBy, createBy, createBy, createBy, createBy, createBy));
		}
		if (countRow != null) {
			clcount = countRow.get("clCount").toString();
			gbcount = countRow.get("gbCount").toString();
			allcount = countRow.get("allCount").toString();
		}
		valueMap.put("CL_COUNT", clcount); // 处理个数
		valueMap.put("WC_COUNT", gbcount); // 完成个数
		valueMap.put("ALL_COUNT", allcount); // 所有个数
	}

	/**
	 * 统计我参与的工单个数
	 * 
	 * @param api
	 * @param valueMap
	 * @param createBy
	 */
	private void createInparticipatedOrder(ISiteviewApi api, Map<String, String> valueMap,
			String createBy) {
		String iparticipatedOrderSql = "SELECT COUNT(DISTINCT(a.WorkOrderId)) c FROM "
				+ "(SELECT DISTINCT(w.WorkOrderId) FROM WorkOrderCommon wc LEFT JOIN WorkFlowLog w ON w.WorkOrderId=wc.RecId AND w.CreatedBy='%s' "
				+ " UNION (SELECT RecId d FROM WorkOrderCommon WHERE CreatedBy='%s') UNION (SELECT DISTINCT(we.WorkOrderId) FROM WorkOrderCommon wc LEFT JOIN WorkOrderExtension we ON wc.RecId=we.WorkOrderId AND we.CreatedBy='%s')) a"; // 排除日志里面有工单里面没有的情况
		DataRow countRow = DBUtils.getDataRow(api,
				String.format(iparticipatedOrderSql, createBy, createBy, createBy));
		String cycount = "0"; // 参与工单
		if (countRow != null) {
			cycount = countRow.get("c").toString();
		}
		valueMap.put("CY_COUNT", cycount); // 参与个数
	}

	/**
	 * 待处理工单人员排序
	 * 
	 * @param api
	 * @param valueMap
	 * @param isAdministrators
	 * @param createBy2
	 * @throws SiteviewException
	 */
	private void createPendingOrderSort(ISiteviewApi api, Map<String, String> valueMap, String createBy, boolean isAdministrators) throws SiteviewException {
		String sql = "";
		DataTable dataTable = null;
//		if (isAdministrators) {
			sql = "SELECT COUNT(CurrentHandle) c,CurrentHandle FROM WorkOrderCommon WHERE CurrentHandle!='' AND Status = 'cl' OR Status='fp' OR Status='sqyqpf' OR Status='sqyq' OR Status='sqyqjj' OR Status='wc' GROUP BY CurrentHandle ORDER BY c";
			dataTable = DBUtils.select(sql, api);
//		} else {
//			sql = "SELECT COUNT(CreatedBy) c,CreatedBy FROM WorkOrderCommon WHERE RecId IN(" + cysql
//					+ ") AND Status = 'cl' GROUP BY CreatedBy ORDER BY c";
//			dataTable = DBUtils.select(String.format(sql, createBy, createBy), api);
//		}
		List<CountObject> users = CommonUtils.getCountObjects(dataTable, "CurrentHandle");
		valueMap.put("PENDING_USERCOUNT", JSONArray.fromObject(users).toString());
	}

}
