package com.siteview.workorder.odata.yft;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import siteview.IFunctionExtension;
import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.SiteviewException;
import Siteview.User;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;

import com.siteview.utils.date.DateUtils;
import com.siteview.utils.db.DBUtils;
import com.siteview.utils.dictionary.Dictionary;
import com.siteview.utils.dictionary.DictionaryUtils;
import com.siteview.utils.html.StringUtils;
import com.siteview.workorder.odata.yft.entities.ExtensionRequest;
import com.siteview.workorder.odata.yft.entities.HardWareAsset;
import com.siteview.workorder.odata.yft.entities.Satisfaction;
import com.siteview.workorder.odata.yft.entities.WorkFlowLog;

/**
 * 编辑查看工单获取工单详细内容
 * 
 * @author Administrator
 *
 */
public class GetOrderDetails implements IFunctionExtension {

	public GetOrderDetails() {
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api, Map<String, String> inputParamMap) {
		List<Map<String, String>> functionListMap = new ArrayList<Map<String, String>>();
		try {
			String id = inputParamMap.get("WORKORDER_ID");
			String sql = "SELECT sa.Title,sa.ResponseTime slaRT,sa.SolutionTime slaST,c.* FROM (SELECT fs.FaultName fSmallName,b.* FROM (SELECT fb.FaultName fLargeName,a.* FROM (SELECT wo.* FROM WorkOrderCommon wo WHERE wo.RecId='%s')a LEFT JOIN FaultClassification fb ON a.FaultLarge = fb.RecId)b"
					+ " LEFT JOIN FaultClassification fs ON b.FaultSmall=fs.RecId)c"
					+ " LEFT JOIN ServiceLevelAgreement sa ON c.SLAId=sa.RecId";
			Map<String, String> valueMap = new HashMap<String, String>();
			DataRow row = DBUtils.getDataRow(api, String.format(sql, id));

			String workOrderNum = "";
			if (row != null) {
				String createTime = row.get("CreatedDateTime").toString(); // 工单创建时间
				String dispatchTime = row.get("ResponseTime").toString(); // 工单派工时间,计算工单到期时间
				valueMap.put("SUBJECT", row.get("Subject").toString());
				valueMap.put("DESC", row.get("WorkOrderDesc").toString());
				workOrderNum = row.get("WorkOrderNumber").toString();
				valueMap.put("WORKORDERNUM", workOrderNum);
				valueMap.put("GROUP_ID", StringUtils.getNotNullStr(row.get("ParentId")));
				valueMap.put("DISPATCH_TIME", dispatchTime);
				valueMap.put("DISPATCH_NUM", StringUtils.getNotNullStr(row.get("DispatchNumber")));
				valueMap.put("FAULT_LARGE", StringUtils.getNotNullStr(row.get("fLargeName")));
				valueMap.put("FAULT_SMALL", StringUtils.getNotNullStr(row.get("fSmallName")));
				valueMap.put("APPOINTMENTTIME", StringUtils.getNotNullStr(row.get("AppointmentTime")));// 预计解决时间
				String flowId = StringUtils.getNotNullStr(row.get("WorkOrderType"));
				valueMap.put("WORKFLOW_ID", flowId); // 流程ID
				String flowName = "";
				if (!"".equals(flowId)) {
					DataRow flowRow = DBUtils.getDataRow(api, "SELECT * FROM WorkFlow WHERE RecId='" + flowId
							+ "'");
					if (flowRow != null)
						flowName = StringUtils.getNotNullStr(flowRow.get("Name"));
				}
				valueMap.put("WORKFLOW_NAME", flowName); // 流程名称
				
				String createBy = row.get("CreatedBy").toString();
				valueMap.put("CREATEBY", createBy);
				//用户信息
				String usersql = "SELECT * FROM Profile WHERE LoginID = '" +createBy+ "'";
				DataRow userRow = DBUtils.getDataRow(api, usersql);
				String cellphone = "";
				String telephone = "";
				String dept = "";
				if (userRow != null) {
					cellphone = StringUtils.getNotNullStr(userRow.get("Cellphone"));
					telephone = StringUtils.getNotNullStr(userRow.get("Phone1Link"));
					dept = StringUtils.getNotNullStr(userRow.get("DepartmentCode"));
				}
				valueMap.put("DEPT", dept);
				valueMap.put("CELLPHONE", cellphone);
				valueMap.put("TELEPHONE", telephone);

				valueMap.put("PRIORITY", StringUtils.getNotNullStr(row.get("FaultLevelId")));
				String status_En = StringUtils.getNotNullStr(row.get("Status"));
				valueMap.put("STATUS_EN", status_En);
				Dictionary statusDic = DictionaryUtils.getDicDataNameFromValue(api,
						StringUtils.getNotNullStr(row.get("Status")));
				String status = "";
				if (statusDic != null)
					status = statusDic.getDictDataName();
				valueMap.put("STATUS", status);
				valueMap.put("CURRENT_HANDLER",StringUtils.getNotNullStr(row.get("CurrentHandle")));
				
				String extendedDeadline = ""; // 延期截止日期
				String responseTime = ""; // 响应时间
				String resolveTime = ""; // 解决时间
				String slaTitle = "";
				String solutionTimeNum = "0"; //sla解决时间数字
				String responseTimeNum = "0"; //sla响应时间数字
				String extendTimeNum = "0"; //延期时间数字
				if (row.get("Title") != null) { // SLA选择后才有对应数据
					slaTitle = row.get("Title").toString();
					
					Calendar calendar = Calendar.getInstance();
					if(dispatchTime != null && !"".equals(dispatchTime)) //派工时间不为空才有延期时间计算
						calendar.setTime(DateUtils.parseDefaultDate(dispatchTime));
					else
						calendar.setTime(DateUtils.parseDefaultDate(createTime));

					solutionTimeNum = row.get("slaST").toString();
					// 响应时间
					Calendar resCalendar = (Calendar) calendar.clone();
					responseTimeNum = row.get("slaRT").toString();
					resCalendar.add(Calendar.HOUR_OF_DAY, Integer.parseInt(responseTimeNum));
					responseTime = DateUtils.formatDefaultDate(resCalendar.getTime());
					// 解决时间
					Calendar solutionCalendar = (Calendar) calendar.clone();
					
					solutionCalendar.add(Calendar.HOUR_OF_DAY, Integer.parseInt(solutionTimeNum));
					Date resolveDate = solutionCalendar.getTime(); // 解决时间
					resolveTime = DateUtils.formatDefaultDate(resolveDate);

					// 延期截止日期
					if(dispatchTime != null && !"".equals(dispatchTime)) {
						Map<String,String> map = getExtendedDeadline(api,id, calendar, resolveDate);
						extendedDeadline = map.get("extendedDeadline");
						extendTimeNum = map.get("extendTime");
					}
				}
				valueMap.put("SLA_TITLE", slaTitle);
				valueMap.put("RESPONSE_TIME", responseTimeNum);
				valueMap.put("RESOLVE_TIME", solutionTimeNum);
//				valueMap.put("RESPONSE_TIME", responseTime);
//				valueMap.put("RESOLVE_TIME", resolveTime);
				if(!"".equals(extendTimeNum))
					valueMap.put("RESOLVE_TIME_NUM", (Integer.parseInt(solutionTimeNum) + Integer.parseInt(extendTimeNum)) +""); //解决时间数字,要加上延迟时间
				else
					valueMap.put("RESOLVE_TIME_NUM", solutionTimeNum); //解决时间数字,要加上延迟时间

				valueMap.put("EXTENDED_DEADLINE", extendedDeadline); // 延期截止时间
				valueMap.put("RESOLVE_LONG", StringUtils.getNotNullStr(row.get("Workload"))); // 解决时长
				
				//处理表单响应时间
				String workOrderResponseTime = "";
				if(row.get("WorkOrderResponseTime") != null)
					workOrderResponseTime = StringUtils.removeLastPoint(row.get("WorkOrderResponseTime").toString());
				valueMap.put("WORKORDER_RESPONSETIME", workOrderResponseTime);
				
				DataRow kbRow = DBUtils.getDataRow(api, "SELECT * FROM FaultKnowledgeBase WHERE WorkOrderId = '" + id + "'");
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
			}
			// 资产信息
			setAssetsInfo(api, valueMap, id);

			// 延期单信息
			setExtensionInfo(api, valueMap, id, workOrderNum);

			// 满意度信息
			setSaInfo(api, valueMap, id);

			// 流程日志信息
			setWorkFlowLogInfo(api, valueMap, id);
			functionListMap.add(valueMap);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return functionListMap;
	}

	/**
	 * 流程日志信息
	 * 
	 * @param api
	 * @param valueMap
	 * @param id
	 * @throws SiteviewException
	 */
	public static void setWorkFlowLogInfo(ISiteviewApi api, Map<String, String> valueMap, String id)
			throws SiteviewException {
		String sql = "SELECT * FROM WorkFlowLog WHERE WorkOrderId = '%s'";
		DataTable dataTable = DBUtils.select(String.format(sql, id), api);
		List<WorkFlowLog> logs = new ArrayList<WorkFlowLog>();
		for (DataRow row : dataTable.get_Rows()) {
			WorkFlowLog flowLog = new WorkFlowLog();
			String createBy = row.get("CreatedBy").toString();
			flowLog.setId(row.get("RecId").toString());
			flowLog.setHandleTime(StringUtils.removeLastPoint(row.get("CreatedDateTime").toString()));
			flowLog.setContent(row.get("Content").toString());
			User user = api.get_AuthenticationService().GetUser("User", createBy);
			String name = "";
			if(user != null) {
				BusinessObject userBo = DBUtils.getUserBusinessObject(api, user.get_BusObId());
				if(userBo != null)
					name = userBo.GetField("DisplayName").get_NativeValue().toString();
			}
			flowLog.setHandler(name);
			logs.add(flowLog);
		}
		valueMap.put("LOGS", JSONArray.fromObject(logs).toString());
	}

	/**
	 * 获取延期截止日期
	 * 
	 * @param api
	 * @param id 
	 * @param calendar
	 * @param resolveDate
	 * @return
	 */
	private Map<String,String> getExtendedDeadline(ISiteviewApi api, String id, Calendar calendar, Date resolveDate) {
		String exDlineTimeSql = "SELECT * FROM WorkOrderExtension woe,ExtensionRequest er WHERE woe.WorkOrderId='%s' "
				+ " AND woe.ExtensionId=er.RecId ORDER BY er.CreatedDateTime DESC";
		DataRow exDRow = DBUtils.getDataRow(api, String.format(exDlineTimeSql, id));
		String extendedDeadline = "";
		String extendTime = "";
		Map<String,String> map = new HashMap<String,String>();
		if (exDRow != null) {
			if (exDRow.get("Status") != null) {
				String exDStatus = exDRow.get("Status").toString();
				if ("申请延期批复".equals(exDStatus)) {
					extendTime = exDRow.get("ExtendTime").toString(); // 单位小时
					// sla解决时间+延长时间=延期截止时间
					Calendar exCalendar = (Calendar) calendar.clone();
					exCalendar.setTime(resolveDate);
					exCalendar.add(Calendar.HOUR_OF_DAY, Integer.parseInt(extendTime));
					extendedDeadline = DateUtils.formatDefaultDate(exCalendar.getTime());
				}
			}
		}
		map.put("extendTime", extendTime);
		map.put("extendedDeadline", extendedDeadline);
		return map;
	}

	/**
	 * 设置满意度信息数据
	 * 
	 * @param id
	 *          工单ID
	 * @param api
	 * @param valueMap
	 */
	public static void setSaInfo(ISiteviewApi api, Map<String, String> valueMap, String id) {
		String saSql = "SELECT s.* FROM (SELECT * FROM WorkOrderCommon wc WHERE wc.RecId='%s')a LEFT JOIN Satisfaction s ON a.RecId=s.WorkOrderId";
		DataTable saDataTable = DBUtils.select(String.format(saSql, id), api);
		List<Satisfaction> satisfactions = new ArrayList<Satisfaction>();
		for (DataRow saRow : saDataTable.get_Rows()) {
			Satisfaction satisfaction = new Satisfaction();
			if (saRow.get("RecId") != null) {
				satisfaction.setServicesAging(saRow.get("ServicesAging").toString());
				satisfaction.setSuggestions(saRow.get("ServiceSuggestions").toString());
				satisfactions.add(satisfaction);
			}
		}
		valueMap.put("SATISFACTION", JSONArray.fromObject(satisfactions).toString());
	}

	/**
	 * 设置延期单信息数据
	 * 
	 * @param id
	 *          工单ID
	 * @param api
	 * @param valueMap
	 * @param workOrderNum
	 */
	private void setExtensionInfo(ISiteviewApi api, Map<String, String> valueMap, String id,
			String workOrderNum) {
		String extenSql = "SELECT b.*,p.CellPhone FROM(SELECT wr.* FROM (SELECT * FROM WorkOrderExtension we WHERE we.WorkOrderId = '%s')a LEFT JOIN ExtensionRequest wr ON a.ExtensionId = wr.RecId)b "
				+ "LEFT JOIN Profile p ON b.CreatedBy = p.LoginID";
		DataTable extenTable = DBUtils.select(String.format(extenSql, id), api);
		List<ExtensionRequest> requesters = new ArrayList<ExtensionRequest>();
		for (DataRow eRow : extenTable.get_Rows()) {
			ExtensionRequest requester = new ExtensionRequest();
			if (eRow.get("RecId") != null) {
				requester.setId(eRow.get("RecId").toString());
				requester.setApprovalTime(StringUtils.removeLastPoint(StringUtils.getNotNullStr(eRow
						.get("ApproverTime"))));
				requester.setApprover(StringUtils.getNotNullStr(eRow.get("Approver")));
				requester.setRequester(eRow.get("CreatedBy").toString());
				requester.setRequesterPhone(StringUtils.getNotNullStr(eRow.get("CellPhone")));
				requester.setRequestTime(StringUtils
						.removeLastPoint(eRow.get("CreatedDateTime").toString()));
				requester.setStatus(StringUtils.getNotNullStr(eRow.get("Status")));
				requester.setSerialNumber(StringUtils.getNotNullStr(eRow.get("SerialNumber")));
				requester.setWorkOrderNum(workOrderNum);
				requester.setExtendTime(StringUtils.getNotNullStr(eRow.get("ExtendTime")));
				requester.setReason(StringUtils.getNotNullStr(eRow.get("Reason")));
				requester.setOpinion(StringUtils.getNotNullStr(eRow.get("Opinion")));
				requesters.add(requester);
			}
		}
		valueMap.put("EXTENSIONS", JSONArray.fromObject(requesters).toString());
	}

	/**
	 * 设置资产信息数据
	 * 
	 * @param id
	 *          工单ID
	 * @param api
	 * @param valueMap
	 */
	private void setAssetsInfo(ISiteviewApi api, Map<String, String> valueMap, String id) {
		String assetSql = "SELECT pt.RecId ptId,pt.CodeName,pt.TypeName,b.* FROM ("
				+ "SELECT ha.* FROM (SELECT * FROM WorkOrderAssets wa WHERE wa.WorkOrderId='%s')a "
				+ "LEFT JOIN HardwareAssets ha ON a.AssetId=ha.RecId) b LEFT JOIN ProductType pt ON pt.RecId = b.ProductType";
		DataTable dataTable = DBUtils.select(String.format(assetSql, id), api);
		List<HardWareAsset> assets = new ArrayList<HardWareAsset>();
		for (DataRow aRow : dataTable.get_Rows()) {
			if (aRow.get("RecId") != null) {
				HardWareAsset asset = new HardWareAsset();
				asset.setId(aRow.get("RecId").toString());
				asset.setName(StringUtils.getNotNullStr(aRow.get("AssetsName")));
				asset.setCode(StringUtils.getNotNullStr(aRow.get("AssetsCode")));
				asset.setGBCode(StringUtils.getNotNullStr(aRow.get("GBCode")));
				asset.setInstallAddress(StringUtils.getNotNullStr(aRow.get("InstallAddress")));
				asset.setMaintenanceMan(StringUtils.getNotNullStr(aRow.get("MaintenancePeople")));
				asset.setMaintenanceManUnit(StringUtils.getNotNullStr(aRow.get("MaintenanceUnit")));
				asset.setToward(StringUtils.getNotNullStr(aRow.get("Toward")));
				asset.setProductTypeId(StringUtils.getNotNullStr(aRow.get("ptId")));
				asset.setProductTypeName(StringUtils.getNotNullStr(aRow.get("TypeName")));
				asset.setProductCodeName(StringUtils.getNotNullStr(aRow.get("CodeName")));
				asset.setDeviceCode(StringUtils.getNotNullStr(aRow.get("DeviceCode")));
				asset.setIpAddress(StringUtils.getNotNullStr(aRow.get("IPAddress")));
				assets.add(asset);
			}
		}

		valueMap.put("ASSETS", JSONArray.fromObject(assets).toString());
	}

}
