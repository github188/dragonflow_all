package com.siteview.ecc.view.apptopu.odata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Siteview.Api.ISiteviewApi;
import siteview.IFunctionExtension;

public class GetVisioByName implements IFunctionExtension{
	public GetVisioByName(){
		
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api, Map<String, String> inputParamMap) {
		// TODO Auto-generated method stub
		List<Map<String, String>> functionListMap = new ArrayList<Map<String, String>>();
		String name = inputParamMap.get("Name");
		SvgDBConfig svgdb=new SvgDBConfig(api);
		try {
			Map<String, String> valueMap = new HashMap<String, String>();
			valueMap =svgdb.load(name);
			functionListMap.add(valueMap);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return functionListMap;
	}
}
