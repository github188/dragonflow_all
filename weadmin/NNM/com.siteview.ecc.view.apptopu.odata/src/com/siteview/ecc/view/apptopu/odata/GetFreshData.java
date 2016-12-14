package com.siteview.ecc.view.apptopu.odata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.rap.json.JsonObject;

import Siteview.Api.ISiteviewApi;
import siteview.IFunctionExtension;

public class GetFreshData implements IFunctionExtension{
	public GetFreshData(){
		
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api, Map<String, String> inputParamMap) {
		// TODO Auto-generated method stub
		List<Map<String, String>> functionListMap = new ArrayList<Map<String, String>>();
		String name = inputParamMap.get("Name");
		SvgDBConfig svgdb=new SvgDBConfig(api);
		try {
			Map<String, String> tempMap = new HashMap<String, String>();
			tempMap =svgdb.load(name);
			Map<String, String> valueMap=new HashMap<String, String>();
			List<String> ips=new ArrayList<String>();
			List<String> groups=new ArrayList<String>();
			List<String> devices=new ArrayList<String>();
			List<String> lines=new ArrayList<String>();
			List<String> links=new ArrayList<String>();
			String strip = tempMap.get("Ips");
			String Strgroup = tempMap.get("Groups");
			String strdevice =tempMap.get("Devices");
			String strline =tempMap.get("Lines");
			String strlink = tempMap.get("Links");
			for(String key1:strip.split(",")){
				if(key1!=null && !key1.isEmpty()){
					ips.add(key1);
				}
			}
			for(String key1:Strgroup.split(",")){
				if(key1!=null && !key1.isEmpty()){
					groups.add(key1);
				}
			}
			for(String key1:strdevice.split(",")){
				if(key1!=null && !key1.isEmpty()){
					devices.add(key1);
				}
			}
			for(String key1:strline.split(",")){
				if(key1!=null && !key1.isEmpty()){
					lines.add(key1);
				}
			}
			for(String key1:strlink.split(",")){
				if(key1!=null && !key1.isEmpty()){
					links.add(key1);
				}
			}
			MonitorDB monitordb=new MonitorDB(ips,devices,groups,lines,api);
			List<JsonObject> monitorinfo= monitordb.BuildMonitorInfo();
			List<JsonObject> linedata= monitordb.buildLineInfo();
			List<JsonObject> assetdata= monitordb.buildAssetsInfo();
			valueMap.put("ipData", monitorinfo.toString()); 
			valueMap.put("assetData", assetdata.toString());
			valueMap.put("lineData", linedata.toString());
			valueMap.put("Svg", tempMap.get("Svg"));
			
			functionListMap.add(valueMap);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return functionListMap;
	}

}
