package com.siteview.workorder.odata.yft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.eclipse.rap.json.JsonObject;

import siteview.IFunctionExtension;
import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.SiteviewException;
import Siteview.Api.ISiteviewApi;

import com.siteview.utils.db.DBUtils;
import com.siteview.utils.dictionary.Dictionary;
import com.siteview.utils.dictionary.DictionaryUtils;
import com.siteview.utils.html.StringUtils;
import com.siteview.workorder.odata.yft.entities.WorkFlowDetail;

/**
 * 获取自定义流程需要数据
 * 
 * @author Administrator
 *
 */
public class GetCustomFlowInfo implements IFunctionExtension {

	public GetCustomFlowInfo() {
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api, Map<String, String> inputParamMap) {
		List<Map<String, String>> functionListMap = new ArrayList<Map<String, String>>();
		Map<String, String> valueMap = new HashMap<String, String>();
		String name = inputParamMap.get("NAME"); // 工单名称
		String statusDictNo = inputParamMap.get("STATUS_DICTNO"); //状态字典NO
		try {
			valueMap.put("ROLES", getAllRoles(api));
			valueMap.put("STATUS", getAllStatus(api,statusDictNo));
			if (name == null || "".equals(name)) {
				valueMap.put("DETAILS", "");
				valueMap.put("NODESTABLE", "");
			} else {
				valueMap.put("DETAILS", getWorkOrderDetails(api, name));
				valueMap.put("NODESTABLE", getNodesTable(api, name));
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		functionListMap.add(valueMap);
		return functionListMap;
	}

	/**
	 * 获取节点位置表数据
	 * 
	 * @param api
	 * @param name
	 * @return
	 */
	private String getNodesTable(ISiteviewApi api, String name) {
		String sql = "SELECT nt.*,wf.Name wfName FROM NodesTable nt,WorkFlow wf WHERE nt.WorkFlowId=wf.RecId";
		if (name != null && !"".equals(name)) {
			sql += " AND wf.Name = '" + name + "'";
		}
		DataTable dataTable = DBUtils.select(sql, api);
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		for (DataRow row : dataTable.get_Rows()) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("Name", StringUtils.getNotNullStr(row.get("Name")));
			map.put("Nx", StringUtils.getNotNullStr(row.get("Nx")));
			map.put("Ny", StringUtils.getNotNullStr(row.get("Ny")));
			map.put("WorkFlowId", StringUtils.getNotNullStr(row.get("WorkFlowId")));
			map.put("WorkFlowName", StringUtils.getNotNullStr(row.get("wfName")));
			list.add(map);
		}
		return JSONArray.fromObject(list).toString();
	}

	/**
	 * 获取工单详细信息
	 */
	private String getWorkOrderDetails(ISiteviewApi api, String name) {
		List<WorkFlowDetail> details = new ArrayList<WorkFlowDetail>();
		String sql = "SELECT wd.* FROM WorkFlowDetails wd,WorkFlow wf WHERE wd.WorkFlowId = wf.RecId "
				+ " AND wf.WorkOrderType = 'WorkOrderCommon'";
		if (name != null && !"".equals(name)) {
			sql += " AND wf.Name = '" + name + "'";
		}
		sql += " ORDER BY wd.SN";
		DataTable dataTable = DBUtils.select(sql, api);
		for (DataRow row : dataTable.get_Rows()) {
			WorkFlowDetail detail = new WorkFlowDetail();
			String fromId = row.get("FromId").toString();
			String toId = row.get("ToId").toString();
			detail.setFrom(fromId);
			detail.setTo(toId);
			detail.setDes(row.get("FlowAction").toString());
			String status = StringUtils.getNotNullStr(row.get("Status"));
			Dictionary statusDictionary = DictionaryUtils.getDicDataNameFromValue(api, status);
			if (statusDictionary != null)
				detail.setState(statusDictionary.getDictDataName());
			else
				detail.setState(status);
			String flag = StringUtils.getNotNullStr(row.get("Flag"));
			if ("false".equalsIgnoreCase(flag) || "0".equals(flag))
				flag = "0";
			else if ("true".equalsIgnoreCase(flag) || "1".equals(flag))
				flag = "1";
			detail.setFlag(flag);
			String nextstatus = StringUtils.getNotNullStr(row.get("NextStatus"));
			statusDictionary = DictionaryUtils.getDicDataNameFromValue(api, nextstatus);
			if (statusDictionary != null)
				detail.setNextstatus(statusDictionary.getDictDataName());
			else
				detail.setNextstatus(nextstatus);
			detail.setStartcx(Integer.parseInt(row.get("Startcx").toString()));
			detail.setStartcy(Integer.parseInt(row.get("Startcy").toString()));
			detail.setEndcx(Integer.parseInt(row.get("Endcx").toString()));
			detail.setEndcy(Integer.parseInt(row.get("Endcy").toString()));
			detail.setMidPointx(Integer.parseInt(row.get("MidPointx").toString()));
			detail.setMidPointy(Integer.parseInt(row.get("MidPointy").toString()));
			try {
				if (row.get("template_id") != null) {
					detail.setTemplateId(StringUtils.getNotNullStr(row.get("template_id")));
				}
			} catch (Exception e) {
			}
			details.add(detail);
		}

		return JSONArray.fromObject(details).toString();
	}

	/**
	 * 获取所有角色名称数据
	 * 
	 * @param api
	 * @return
	 * @throws SiteviewException
	 */
	private String getAllRoles(ISiteviewApi api) throws SiteviewException {
		JsonObject[] devdata = null;
		List<JsonObject> jsons = new ArrayList<JsonObject>();
		DataTable dt = api.get_NativeSQLSupportService().ExecuteNativeSQLQuery(
				"SELECT FDName,FDDesc FROM SiteviewDefs WHERE FDObType='RoleDef'", null);
		String[] rolenames = null;
		if (dt.get_Rows().size() > 0) {
			devdata = new JsonObject[dt.get_Rows().size() + 1];
			rolenames = new String[dt.get_Rows().size()];
			int i = 0;
			for (DataRow dr : dt.get_Rows()) {
				rolenames[i] = dr.get_Item(0).toString();
				JsonObject jo = new JsonObject().add("name", dr.get_Item(0).toString())
						.add("boid", "boid" + i)
						.add("desc", dr.get_Item(1) == null ? "" : dr.get_Item(1).toString());
				devdata[i] = jo;
				i++;
				jsons.add(jo);
			}
			JsonObject jo = new JsonObject().add("name", "关闭").add("boid", "boid" + i).add("desc", "");
			devdata[i] = jo;
			jsons.add(jo);
		} else {
			devdata = new JsonObject[0];
		}

		return jsons.toString();
	}

	private String getAllStatus(ISiteviewApi api,String no) {
		List<JsonObject> jsons = new ArrayList<JsonObject>();
		List<Dictionary> status = null;
		if(no != null)
			status =DictionaryUtils.getValuesFromDicNo(api, no, false);
		else
			status = DictionaryUtils.getWorkOrderStatusYFT(api);
		for (Dictionary dictionary : status) {
			JsonObject jo = new JsonObject().add("name", dictionary.getDictDataName())
					.add("name1", dictionary.getDictDataValue()).add("desc", dictionary.getDictDataDesc());
			jsons.add(jo);
		}
		return jsons.toString();
	}
}
