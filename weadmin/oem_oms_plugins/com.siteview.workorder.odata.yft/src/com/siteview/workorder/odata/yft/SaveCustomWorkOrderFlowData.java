package com.siteview.workorder.odata.yft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siteview.utils.db.DBQueryUtils;
import com.siteview.utils.db.DBUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;
import siteview.IFunctionExtension;

/**
 * 保存自定义流程接口
 * @author Administrator
 *
 */
public class SaveCustomWorkOrderFlowData implements IFunctionExtension {

	public SaveCustomWorkOrderFlowData() {
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api, Map<String, String> inputParamMap) {
		List<Map<String, String>> functionListMap = new ArrayList<Map<String, String>>();
		Map<String, String> valueMap = new HashMap<String, String>();
		String flag = "false";
		try {
			String name = inputParamMap.get("FLOWNAME"); // 流程名称
			String orderTempId = inputParamMap.get("WORKORDER_TEMPLATEID"); // 主模板ID
			String acTemId = inputParamMap.get("ACTIONTABLEORDERID"); // 处理模板ID
			String detail = inputParamMap.get("FLOWDETAIL"); // 流程详细数据
			String flowId = inputParamMap.get("WORKFLOW_ID");
			String flowNodes = inputParamMap.get("FLOWNODES"); //流程节点
			String workordermark=inputParamMap.get("WORKORDERMARK")==null?"0":inputParamMap.get("WORKORDERMARK"); //工单标识
			BusinessObject workFlowBo = null;
			boolean workFlowFlag = false;
			if (detail != null) {
				JSONArray detailArray = JSONArray.fromObject(detail);
				if(flowId == null || "".equals(flowId)) { //为空位新建流程,不为空为编辑,编辑不能改名字
					if (detailArray.size() > 0) {
						workFlowBo = DBUtils.createBusinessObject("WorkFlow", api);
						if (name == null)
							name = "默认工单";
						workFlowBo.GetField("Name").SetValue(new SiteviewValue(name));
						workFlowBo.GetField("WorkOrderType").SetValue(new SiteviewValue("WorkOrderCommon"));
						workFlowBo.GetField("WORKORDERMARK").SetValue(new SiteviewValue(workordermark));
						if (workFlowBo.GetField("template_id") != null && orderTempId != null) {
							workFlowBo.GetField("template_id").SetValue(new SiteviewValue(orderTempId));
						}
						if (workFlowBo.GetField("templatesheet_id") != null && acTemId != null) {
							workFlowBo.GetField("templatesheet_id").SetValue(new SiteviewValue(acTemId));
						}
						workFlowFlag = workFlowBo.SaveObject(api, true, true).get_Success();
						flowId = workFlowBo.get_RecId();
					}
					
					
				} else {
					workFlowFlag = true;
				}
				if (workFlowFlag) {
					workFlowBo = DBQueryUtils.queryOnlyBo("RecId", flowId, "WorkFlow", api);
					if (workFlowBo != null){
						workFlowBo.GetField("Name").SetValue(new SiteviewValue(name));
						workFlowBo.GetField("WORKORDERMARK").SetValue(new SiteviewValue(workordermark));
						workFlowBo.SaveObject(api, true, true);
					}
					for (int i = 0; i < detailArray.size(); i++) {
						JSONObject jo = detailArray.getJSONObject(i);
						String from = jo.getString("from");
						String to = jo.getString("to");
						String status = jo.getString("state");
						String flowAction = jo.getString("des");
						int sn = jo.getInt("sn");
						String nextStatus = jo.getString("nextstatus");
						boolean isflag = "1".equals(jo.getString("flag"));
						BusinessObject bo = DBUtils.createBusinessObject("WorkFlowDetails", api);
						bo.GetField("WorkFlowId").SetValue(new SiteviewValue(flowId));
						bo.GetField("FromId").SetValue(new SiteviewValue(from));
						bo.GetField("ToId").SetValue(new SiteviewValue(to));
						bo.GetField("Status").SetValue(new SiteviewValue(status));
						bo.GetField("FlowAction").SetValue(new SiteviewValue(flowAction));
						bo.GetField("SN").SetValue(new SiteviewValue(sn));
						bo.GetField("NextStatus").SetValue(new SiteviewValue(nextStatus));
						bo.GetField("Flag").SetValue(new SiteviewValue(isflag));
						if(jo.get("Startcx") != null)
							bo.GetField("Startcx").SetValue(new SiteviewValue(jo.getInt("Startcx")));
						if(jo.get("Startcx") != null)
							bo.GetField("Startcy").SetValue(new SiteviewValue(jo.getInt("c")));
						if(jo.get("Endcx") != null)
							bo.GetField("Endcx").SetValue(new SiteviewValue(jo.getInt("Endcx")));
						if(jo.get("Endcy") != null)
							bo.GetField("Endcy").SetValue(new SiteviewValue(jo.getInt("Endcy")));
						if(jo.get("MidPointx") != null)
							bo.GetField("MidPointx").SetValue(new SiteviewValue(jo.getInt("MidPointx")));
						if(jo.get("MidPointy") != null)
							bo.GetField("MidPointy").SetValue(new SiteviewValue(jo.getInt("MidPointy")));
						if (bo.GetField("template_id") != null
								&& jo.get("actionTableOrderId") != null) { //对应模板ID
							bo.GetField("template_id").SetValue(
									new SiteviewValue(jo
											.getString("actionTableOrderId")));
						}
						bo.SaveObject(api, true, true);
					}
					
					//保存节点数据
					if (flowNodes != null) {
						JSONArray flowNodesArray = JSONArray.fromObject(flowNodes);
						if (flowNodesArray.size() > 0) {
							for (int i = 0; i < flowNodesArray.size(); i++) {
								JSONObject jo = flowNodesArray.getJSONObject(i);
								BusinessObject bo = DBUtils.createBusinessObject("NodesTable", api);
								bo.GetField("WorkFlowId").SetValue(new SiteviewValue(flowId));
								bo.GetField("Name").SetValue(new SiteviewValue(jo.getString("Name")));
								if (jo.get("Nx") != null)
									bo.GetField("Nx").SetValue(new SiteviewValue(jo.getInt("Nx")));
								if (jo.get("Ny") != null)
									bo.GetField("Ny").SetValue(new SiteviewValue(jo.getInt("Ny")));
								bo.SaveObject(api, true, true);
							}
						}
					}
				}
				if (workFlowFlag)
					flag = "true";
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
			flag = "false";
		}
		valueMap.put("OUT_FLAG", flag);

		functionListMap.add(valueMap);
		return functionListMap;
	}

}
