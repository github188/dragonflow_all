package com.siteview.ecc.yft.odata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siteview.utils.db.DBQueryUtils;
import com.siteview.utils.workorder.WorkOrderUtils;

import Siteview.SiteviewException;
import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;
import siteview.IFunctionExtension;

public class SetWorkOrderCount implements IFunctionExtension {

	public SetWorkOrderCount() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api,
			Map<String, String> inputParamMap) {
		List<Map<String,String>> outP=new ArrayList<Map<String,String>>();
		Map<String,String> outM=new HashMap<String,String>();
		String type=inputParamMap.get("WORKORDERSTATUS");
		String asset=inputParamMap.get("ASSETSID");
		String equipment=inputParamMap.get("EQUIPMENTID");
		String[] assets=asset.split(",");
		String[] equipments=equipment.split(",");
		if(assets.length==equipments.length){
			for(int i=0;i<assets.length;i++){
				WorkOrderUtils.setWorkOrderCount(type, equipments[i], assets[i], api);
			}
			outM.put("RETURN", "true");
		}else{
			outM.put("RETURN", "false");
		}
		outP.add(outM);
		return outP;
	}

}
