package com.siteview.ecc.view.apptopu.odata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.rap.json.JsonObject;

import Siteview.Api.ISiteviewApi;
import siteview.IFunctionExtension;

public class GetVisioList implements IFunctionExtension{
	public GetVisioList(){
		
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api, Map<String, String> inputParamMap) {
		// TODO Auto-generated method stub
		List<Map<String, String>> functionListMap = new ArrayList<Map<String, String>>();
		SvgDBConfig svgdb=new SvgDBConfig(api);
		try {
			List<JsonObject> rr=svgdb.loadAll();
			Map<String, String> valueMap = new HashMap<String, String>();
			valueMap.put("VisioList",rr.toString());
			functionListMap.add(valueMap);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return functionListMap;
	}

}
