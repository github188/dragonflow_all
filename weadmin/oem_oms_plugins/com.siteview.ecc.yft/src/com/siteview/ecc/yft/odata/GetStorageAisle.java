package com.siteview.ecc.yft.odata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import siteview.IFunctionExtension;
import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.Api.ISiteviewApi;

import com.siteview.utils.db.DBQueryUtils;

public class GetStorageAisle implements IFunctionExtension {

	public GetStorageAisle() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api,
			Map<String, String> inputParamMap) {
		String serverflag=inputParamMap.get("SERVERFLAG");
		List<Map<String,String>> outp=new ArrayList<Map<String,String>>();
		Map<String,String> outm=new HashMap<String,String>();
		List<Map> list=new ArrayList<Map>();
		String sql="select * from VIDEOPOINTINFO where SERVERFLAG='"+serverflag+"'";
		DataTable dt=DBQueryUtils.Select(sql, api);
		for(DataRow dr:dt.get_Rows()){
			Map<String,String> map=new HashMap<String,String>();
			map.put("IPADDRESS", dr.get("IPADDRESS")==null?"":dr.get("IPADDRESS").toString());
			map.put("VIDEONAME", dr.get("VIDEONAME")==null?"":dr.get("VIDEONAME").toString());
			map.put("VIDEOFLAG", dr.get("VIDEOFLAG")==null?"":dr.get("VIDEOFLAG").toString());
			list.add(map);
		}
		outm.put("VIDEODATA", JSONArray.fromObject(list).toString());
		outp.add(outm);
		return outp;
	}

}
