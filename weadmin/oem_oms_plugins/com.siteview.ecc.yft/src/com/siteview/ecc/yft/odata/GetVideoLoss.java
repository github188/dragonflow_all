package com.siteview.ecc.yft.odata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import com.siteview.utils.db.DBQueryUtils;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.Api.ISiteviewApi;
import siteview.IFunctionExtension;
/*
 * 获取摄像机录像丢失列表
 */
public class GetVideoLoss implements IFunctionExtension {

	public GetVideoLoss() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api,
			Map<String, String> inputParamMap) {
//		VIDEOLOSSDATA，VIDEOFLAG
		String videoflag=inputParamMap.get("VIDEOFLAG");
		List<Map<String,String>> listMap = new ArrayList<Map<String,String>>();
		Map<String,String> outmap=new HashMap<String,String>();
		String sql="select * from VIDEORECORDRESULT WHERE VIDEOFLAG='%s' ORDER BY  LOSTSTARTTIME ASC";
		DataTable dt=DBQueryUtils.Select(String.format(sql, videoflag), api);
		List<Map> list=new ArrayList<Map>();
		for(DataRow dr:dt.get_Rows()){
			Map<String,String> map=new HashMap<String, String>();
			String time=dr.get("LOSTSTARTTIME")==null?"": dr.get("LOSTSTARTTIME").toString();
			if(time.contains("."))
				time=time.substring(0,time.indexOf("."));
			map.put("loststartime", time);
			time=dr.get("LOSTENDTIME")==null?"": dr.get("LOSTENDTIME").toString();
			if(time.contains("."))
				time=time.substring(0,time.indexOf("."));
			map.put("lostendtime", time);
			map.put("lostduration", dr.get("LOSTDURATION")==null?"": dr.get("LOSTDURATION").toString());
			list.add(map);
			map.put("no", list.size()+"");
		}
		outmap.put("VIDEOLOSSDATA", JSONArray.fromObject(list).toString());
		listMap.add(outmap);
		return listMap;
	}

}
