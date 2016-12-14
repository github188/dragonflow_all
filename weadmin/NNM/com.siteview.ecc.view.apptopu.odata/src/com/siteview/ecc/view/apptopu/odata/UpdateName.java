package com.siteview.ecc.view.apptopu.odata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Siteview.Api.ISiteviewApi;
import siteview.IFunctionExtension;

public class UpdateName implements IFunctionExtension{

	public UpdateName(){
		
	}
	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api, Map<String, String> inputParamMap) {
		// TODO Auto-generated method stub
		List<Map<String, String>> functionListMap = new ArrayList<Map<String, String>>();
		String name = inputParamMap.get("Name");
		String newname =  inputParamMap.get("NewName");
		Map<String, String> valueMap = new HashMap<String, String>();
		valueMap.put("Flag", "false");
		SvgDBConfig svgdb=new SvgDBConfig(api);
		try {
			boolean flag =svgdb.updateShowname(name, newname);
			if(flag)
			valueMap.put("Flag", "true");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		functionListMap.add(valueMap);
		return functionListMap;
	}

}
