package com.siteview.ecc.yft.odata.fault;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import com.siteview.utils.db.DBUtils;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.Api.ISiteviewApi;
import siteview.IFunctionExtension;

/**
 * 故障管理接口
 * 
 * @author Administrator
 *
 */
public class GetFaultClassification implements IFunctionExtension {

	public GetFaultClassification() {
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api, Map<String, String> inputParamMap) {
		List<Map<String, String>> outParam = new ArrayList<Map<String, String>>();
		try {
			Map<String, String> valueMap = new HashMap<String, String>();
			String sql = "SELECT a.RecId pId,b.RecId cId,a.FaultName pName,b.FaultName cName FROM "
					+ "(SELECT RecId,FaultName,ParentID FROM FaultClassification WHERE ParentID = '') a,"
					+ "(SELECT RecId,FaultName,ParentID FROM FaultClassification WHERE ParentID != '') b"
					+ " WHERE b.ParentID = a.RecId ORDER BY b.ParentID";
			DataTable dataTable = DBUtils.select(sql, api);
			List<FaultClassification> fs = new ArrayList<FaultClassification>();
			for (DataRow row : dataTable.get_Rows()) {
				String pId = row.get("pId").toString();
				String cId = row.get("cId").toString();
				String pName = row.get("pName").toString();
				String cName = row.get("cName").toString();
				FaultClassification classification = new FaultClassification();
				classification.setcId(cId);
				classification.setpId(pId);
				classification.setcName(cName);
				classification.setpName(pName);
				fs.add(classification);
			}
			valueMap.put("FAULTS", JSONArray.fromObject(fs).toString());
			outParam.add(valueMap);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return outParam;
	}

}
