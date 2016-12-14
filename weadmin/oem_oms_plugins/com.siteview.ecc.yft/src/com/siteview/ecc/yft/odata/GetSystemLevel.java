package com.siteview.ecc.yft.odata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siteview.ecc.yft.report.InitReport;

import Siteview.Api.ISiteviewApi;
import siteview.IFunctionExtension;
/*
 * 获取系统级别(厅级1或者市级2)
 */
public class GetSystemLevel implements IFunctionExtension {

	public GetSystemLevel() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api,
			Map<String, String> inputParamMap) {
		List<Map<String,String>> outparam=new ArrayList<Map<String,String>>();
		Map<String,String> outmap=new HashMap<String,String>();
		if(InitReport.map!=null&&InitReport.map.get("the_parent_domain")!=null)
			outmap.put("LEVEL", "2");
		else
			outmap.put("LEVEL", "1");
		outparam.add(outmap);
		return outparam;
	}

}
