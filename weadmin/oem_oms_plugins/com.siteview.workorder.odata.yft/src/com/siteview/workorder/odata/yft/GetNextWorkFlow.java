package com.siteview.workorder.odata.yft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import siteview.IFunctionExtension;
import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.Api.ISiteviewApi;

import com.siteview.utils.db.DBUtils;
import com.siteview.utils.html.StringUtils;
import com.siteview.workorder.odata.yft.entities.NextPerson;

/**
 * 获取工单下个流程角色和角色下对应人员
 * @author Administrator
 *
 */
public class GetNextWorkFlow implements IFunctionExtension {

	public GetNextWorkFlow() {
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api, Map<String, String> inputParamMap) {
		List<Map<String, String>> functionListMap = new ArrayList<Map<String, String>>();
		String fromId = inputParamMap.get("FROM_ID"); //来自哪个角色
		String workflowId = inputParamMap.get("WORKFLOW_ID"); //工单流程ID
		String groupId = inputParamMap.get("GROUP_ID"); //安全群组ID
		
		try {
			if(fromId != null) {
				String sql = "SELECT fd.*,wf.Name,wf.RecId wfRecId FROM WorkFlow wf,WorkFlowDetails fd WHERE wf.RecId = '%s' AND wf.RecId=fd.WorkFlowId AND fd.FromId='"
						+ fromId + "'";
				DataTable table = DBUtils.select(String.format(sql, workflowId), api);
				for (DataRow row : table.get_Rows()) {
					Map<String, String> valueMap = new HashMap<String, String>();
					valueMap.put("FROM_ID", fromId);
					valueMap.put("TO_ID", StringUtils.getNotNullStr(row.get("ToId"))); //下级节点
					valueMap.put("TO_USERS",getNextPerson(api,StringUtils.getNotNullStr(row.get("ToId")),groupId));
					valueMap.put("FLOW_ACTION", StringUtils.getNotNullStr(row.get("FlowAction"))); //流程操作
					valueMap.put("STATUS", StringUtils.getNotNullStr(row.get("Status"))); //流程状态
					functionListMap.add(valueMap);
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return functionListMap;
	}

	//获取角色下人员信息
	private String getNextPerson(ISiteviewApi api, String roleName, String groupId) {
		String sql = "SELECT a.*,p.DisplayName,p.RecId,p.Email FROM (SELECT RoleId,UserId FROM UserRole u WHERE u.RoleId = '%s') a,Profile p "
				+ " WHERE a.UserId=p.LoginID ";
		DataTable table = DBUtils.select(String.format(sql, roleName), api);
		List<NextPerson> nexts = new ArrayList<NextPerson>();
		for (DataRow row : table.get_Rows()) {
			if (row.get("RecId") != null) {
				NextPerson next = new NextPerson();
				next.setRecId(row.get("RecId").toString());
				next.setName(row.get("DisplayName").toString());
				next.setLoginId(row.get("UserId").toString());
				next.setRoleName(roleName);
				next.setEmail(row.get("Email").toString());
				nexts.add(next);
			}
		}
		return JSONArray.fromObject(nexts).toString();
	}

}
