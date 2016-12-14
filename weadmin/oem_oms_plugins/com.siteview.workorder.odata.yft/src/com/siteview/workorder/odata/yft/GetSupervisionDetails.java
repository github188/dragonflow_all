package com.siteview.workorder.odata.yft;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.siteview.utils.db.DBQueryUtils;
import com.siteview.utils.db.DBUtils;
import com.siteview.utils.dictionary.Dictionary;
import com.siteview.utils.dictionary.DictionaryUtils;
import com.siteview.utils.html.StringUtils;
import com.siteview.workorder.odata.yft.entities.Satisfaction;
import com.siteview.workorder.odata.yft.entities.WorkFlowLog;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.SiteviewException;
import Siteview.SiteviewValue;
import Siteview.User;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;
import siteview.IFunctionExtension;

public class GetSupervisionDetails implements IFunctionExtension {

	public GetSupervisionDetails() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api,
			Map<String, String> inputParamMap) {
		List<Map<String,String>> outP=new ArrayList<Map<String,String>>();
		Map<String,String> outM=new HashMap<String,String>();
		String id = inputParamMap.get("WORKORDER_ID");
		List<Map> list=new ArrayList<Map>();
		List<WorkFlowLog> logs = new ArrayList<WorkFlowLog>();
		Map<String, Object> valueMap = new HashMap<String, Object>();
		try {
			String sql0="select * from WorkOrderViewRecord where WorkOrderId='%s' ";
			boolean flag=false;
			DataTable dt0=DBQueryUtils.Select(String.format(sql0, id), api);
			for(DataRow dr:dt0.get_Rows()){
				WorkFlowLog wlog=new WorkFlowLog();
				String userid=dr.get("CreatedBy").toString();
				if(userid.equals(api.get_AuthenticationService().get_CurrentLoginId())){
					flag=true;
				}
				String createBy = dr.get("ViewUser").toString();
				wlog.setId(dr.get("RecId").toString());
				wlog.setHandleTime(StringUtils.removeLastPoint(dr.get("CreatedDateTime").toString()));
				wlog.setContent("查看工单");
				wlog.setHandler(createBy);
				logs.add(wlog);
			}
			if(!flag){
				BusinessObject bo=api.get_BusObService().Create("WorkOrderViewRecord");
				bo.GetField("WorkOrderId").SetValue(new SiteviewValue(id));
				bo.GetField("ViewUser").SetValue(new SiteviewValue(api.get_AuthenticationService()
						.get_CurrentUserDisplayName()));
				bo.SaveObject(api, true, true);
				WorkFlowLog wlog=new WorkFlowLog();
				String createBy =bo.GetField("ViewUser").get_NativeValue().toString();
				wlog.setId(bo.get_RecId());
				SimpleDateFormat simp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				wlog.setHandleTime(simp.format(new Date()));
				wlog.setContent("查看工单");
				wlog.setHandler(createBy);
				logs.add(wlog);
			}
			setSaInfo(api, valueMap, id);//满意度
			setWorkFlowLogInfo(api, valueMap, id);// 流程日志信息
			if(valueMap.get("LOGS")!=null&&valueMap.get("LOGS") instanceof List){
				((List)valueMap.get("LOGS")).addAll(logs);
			}
			String sql="select w.*,f.FaultName from WorkOrderCommon w,FaultClassification f where w.RecId='%s'"
					+ " and w.FaultLarge=f.RecId";
			DataTable dt=DBQueryUtils.Select(String.format(sql, id), api);
			if(dt.get_Rows().size()==1){
				DataRow row=dt.get_Rows().get(0);
				String createTime = row.get("CreatedDateTime").toString(); // 工单创建时间
				String dispatchTime = row.get("ResponseTime").toString(); // 工单派工时间,计算工单到期时间
				valueMap.put("SUBJECT", row.get("Subject").toString());
				valueMap.put("CREATETIME", createTime);
				valueMap.put("DESC", row.get("WorkOrderDesc").toString());
				String workOrderNum = row.get("WorkOrderNumber").toString();
				valueMap.put("WORKORDERNUM", workOrderNum);
				valueMap.put("GROUP_ID", StringUtils.getNotNullStr(row.get("ParentId")));
				valueMap.put("DISPATCH_TIME", dispatchTime);
				valueMap.put("DISPATCH_NUM", StringUtils.getNotNullStr(row.get("DispatchNumber")));
				valueMap.put("FAULT_LARGE", StringUtils.getNotNullStr(row.get("FaultName")));
				valueMap.put("FAULT_SMALL", StringUtils.getNotNullStr(row.get("fSmallName")));
				valueMap.put("APPOINTMENTTIME", StringUtils.getNotNullStr(row.get("AppointmentTime")));// 预计解决时间
				valueMap.put("SOLVESTEPS", StringUtils.getNotNullStr(row.get("SolveSteps")));
				String flowId = StringUtils.getNotNullStr(row.get("WorkOrderType"));
				valueMap.put("WORKFLOW_ID", flowId); // 流程ID
				valueMap.put("STATUS_EN", StringUtils.getNotNullStr(row.get("Status")));
				valueMap.put("CREATEBY", StringUtils.getNotNullStr(row.get("CreatedBy")));
				valueMap.put("PRIORITY", StringUtils.getNotNullStr(row.get("FaultLevelId")));
				valueMap.put("WORKORDER_RESPONSETIME", StringUtils.getNotNullStr(row.get("ResponseTime")));
				Dictionary statusDic = DictionaryUtils.getDicDataNameFromValue(api,
						StringUtils.getNotNullStr(row.get("Status")));
				String status = "";
				if (statusDic != null)
					status = statusDic.getDictDataName();
				valueMap.put("STATUS", status);
				valueMap.put("CURRENT_HANDLER",StringUtils.getNotNullStr(row.get("CurrentHandle")));
				String flowName = "";
				if (!"".equals(flowId)) {
					DataRow flowRow = DBUtils.getDataRow(api, "SELECT * FROM WorkFlow WHERE RecId='" + flowId
							+ "'");
					if (flowRow != null)
						flowName = StringUtils.getNotNullStr(flowRow.get("Name"));
				}
				valueMap.put("WORKFLOW_NAME", flowName); // 流程名称
			}
			getSubWorkorder(id, api, list,valueMap.get("FAULT_LARGE")==null?""
					:valueMap.get("FAULT_LARGE").toString());//关联工单
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
		outM.put("SUB_WORKORDERS", JSONArray.fromObject(list).toString());
		outM.put("MAIN_WORKORDERS", JSONObject.fromObject(valueMap).toString());
		outP.add(outM);
		return outP;
	}
	public static void getSubWorkorder(String id,ISiteviewApi api,List<Map> list,String fname){
		String sql="SELECT * from WorkOrderCommon "
				+ " where RecId in (SELECT SubTicket from WorkOrdersRelatedTicket where MainTicket='%s') ";
		DataTable dt=DBQueryUtils.Select(String.format(sql, id), api);
		for(DataRow row:dt.get_Rows()){
			Map<String, String> valueMap = new HashMap<String, String>();
			String createTime = row.get("CreatedDateTime").toString(); // 工单创建时间
			String dispatchTime = row.get("ResponseTime").toString(); // 工单派工时间,计算工单到期时间
			valueMap.put("SUBJECT", row.get("Subject").toString());
			valueMap.put("CREATETIME", createTime);
			valueMap.put("DESC", row.get("WorkOrderDesc").toString());
			String workOrderNum = row.get("WorkOrderNumber").toString();
			valueMap.put("WORKORDERNUM", workOrderNum);
			valueMap.put("GROUP_ID", StringUtils.getNotNullStr(row.get("ParentId")));
			valueMap.put("DISPATCH_TIME", dispatchTime);
			valueMap.put("DISPATCH_NUM", StringUtils.getNotNullStr(row.get("DispatchNumber")));
			valueMap.put("FAULT_LARGE",fname);
			valueMap.put("FAULT_SMALL", StringUtils.getNotNullStr(row.get("fSmallName")));
			valueMap.put("APPOINTMENTTIME", StringUtils.getNotNullStr(row.get("AppointmentTime")));// 预计解决时间
			valueMap.put("SOLVESTEPS", StringUtils.getNotNullStr(row.get("SolveSteps")));
			String flowId = StringUtils.getNotNullStr(row.get("WorkOrderType"));
			valueMap.put("WORKFLOW_ID", flowId); // 流程ID
			valueMap.put("STATUS_EN", StringUtils.getNotNullStr(row.get("Status")));
			valueMap.put("CREATEBY", StringUtils.getNotNullStr(row.get("CreatedBy")));
			valueMap.put("PRIORITY", StringUtils.getNotNullStr(row.get("FaultLevelId")));
			valueMap.put("WORKORDER_RESPONSETIME", StringUtils.getNotNullStr(row.get("ResponseTime")));
			Dictionary statusDic = DictionaryUtils.getDicDataNameFromValue(api,
					StringUtils.getNotNullStr(row.get("Status")));
			String status = "";
			if (statusDic != null)
				status = statusDic.getDictDataName();
			valueMap.put("STATUS", status);
			valueMap.put("CURRENT_HANDLER",StringUtils.getNotNullStr(row.get("CurrentHandle")));
			list.add(valueMap);
		}
	}
	/**
	 * 设置满意度信息数据
	 * 
	 * @param id
	 *          工单ID
	 * @param api
	 * @param valueMap
	 */
	public static void setSaInfo(ISiteviewApi api, Map<String, Object> valueMap, String id) {
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
		valueMap.put("SATISFACTION",satisfactions);
	}
	/**
	 * 流程日志信息
	 * 
	 * @param api
	 * @param valueMap
	 * @param id
	 * @throws SiteviewException
	 */
	public static void setWorkFlowLogInfo(ISiteviewApi api, Map<String, Object> valueMap, String id)
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
		valueMap.put("LOGS", logs);
	}
}
