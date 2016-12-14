package com.siteview.ecc.yft.odata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siteview.utils.db.DBQueryUtils;

import Siteview.DataTable;
import Siteview.Api.ISiteviewApi;
import siteview.IFunctionExtension;
/*
 * 是否生成工单
 */
public class IsCreateWorkOrder implements IFunctionExtension {

	public IsCreateWorkOrder() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api,
			Map<String, String> inputParamMap) {
		List<Map<String,String>> listMap = new ArrayList<Map<String,String>>();
		Map<String,String> outmap=new HashMap<String,String>();
		String gb=inputParamMap.get("GBCODE");
		String sql="SELECT * FROM WorkOrderCommon WHERE RecId in (SELECT WorkOrderId from WorkOrderAssets where AssetId in (SELECT RecId from HardwareAssets where GBCode='%s')) and Status!='gb' ";
		DataTable dt=DBQueryUtils.Select(String.format(sql, gb), api);
		outmap.put("WORKORDERCOUNT", dt.get_Rows().size()+"");
		listMap.add(outmap);
		return listMap;
	}

}
