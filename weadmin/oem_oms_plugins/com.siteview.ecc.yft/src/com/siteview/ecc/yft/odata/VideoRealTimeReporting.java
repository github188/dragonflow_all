package com.siteview.ecc.yft.odata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import com.siteview.ecc.yft.bean.VideoRealTime;
import com.siteview.utils.db.DBQueryUtils;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.Api.ISiteviewApi;
import siteview.IFunctionExtension;
/*
 * 摄像机实时报表
 */
public class VideoRealTimeReporting implements IFunctionExtension {
	public VideoRealTimeReporting() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api,
			Map<String, String> inputParamMap) {
		List<Map<String,String>> outparam=new ArrayList<Map<String,String>>();
		Map<String,String> outmap=new HashMap<String,String>();
		String sql="select * from ("+
				"select count(*)total from VIDEOALARMRESULT)a,"+
				"(select count(*)onli from VIDEOALARMRESULT WHERE ONLINESTATUS='1')b,"+
				"(select count(*)sig from VIDEOALARMRESULT WHERE SIGNALLOSS='0')c,"+
				"(select count(*)image from VIDEOALARMRESULT WHERE IMAGELOSS='0')d,"+
				"(select count(*)covers from VIDEOALARMRESULT WHERE COVERSTATUS='0')e,"+
				"(select count(*)brig from VIDEOALARMRESULT WHERE BRIGHT='0')f,"+
				"(select count(*)color from VIDEOALARMRESULT WHERE COLORCOST='0')g,"+
				"(select count(*)dim from VIDEOALARMRESULT WHERE DIM='0')h,"+
				"(select count(*)defin from VIDEOALARMRESULT WHERE DEFINITION='0')i,"+
				"(select count(*)snow from VIDEOALARMRESULT WHERE SNOWFLAKE='0')j,"+
				"(select count(*)str from VIDEOALARMRESULT WHERE STREAK='0')k,"+
				"(select count(*)free from VIDEOALARMRESULT WHERE FREEZE='0')l,"+
				"(select count(*)ptz from VIDEOALARMRESULT WHERE PTZ='0')m,"+
				"(select count(*)screenshake from VIDEOALARMRESULT WHERE SCREENSHAKE='0')n,"+
				"(select count(*)other from VIDEOALARMRESULT WHERE OTHERERROR='0')o,"+
				"(select count(*)screenscrool from VIDEOALARMRESULT WHERE SCREENSCROLL='0')p";
		DataTable dt=DBQueryUtils.Select(sql, api);
		VideoRealTime video=new VideoRealTime();
		for(DataRow dr:dt.get_Rows()){
			video.setTotal(dr.get("total")==null?0:Integer.parseInt(dr.get("total").toString()));
			video.setBRIGHT(dr.get("brig")==null?0:Integer.parseInt(dr.get("brig").toString()));
			video.setCOLORCOST(dr.get("color")==null?0:Integer.parseInt(dr.get("color").toString()));
			video.setCOVERSTATUS(dr.get("covers")==null?0:Integer.parseInt(dr.get("covers").toString()));
			video.setDEFINITION(dr.get("defin")==null?0:Integer.parseInt(dr.get("defin").toString()));
			video.setDIM(dr.get("dim")==null?0:Integer.parseInt(dr.get("dim").toString()));
			video.setFREEZE(dr.get("free")==null?0:Integer.parseInt(dr.get("free").toString()));
			video.setIMAGELOSS(dr.get("image")==null?0:Integer.parseInt(dr.get("image").toString()));
			video.setONLINESTATUS(dr.get("onli")==null?0:Integer.parseInt(dr.get("onli").toString()));
			video.setOTHERERROR(dr.get("other")==null?0:Integer.parseInt(dr.get("other").toString()));
			video.setPTZ(dr.get("ptz")==null?0:Integer.parseInt(dr.get("ptz").toString()));
			video.setSCREENSCROLL(dr.get("screenscrool")==null?0:Integer.parseInt(dr.get("screenscrool").toString()));
			video.setSCREENSHAKE(dr.get("screenshake")==null?0:Integer.parseInt(dr.get("screenshake").toString()));
			video.setSNOWFLAKE(dr.get("snow")==null?0:Integer.parseInt(dr.get("snow").toString()));
			video.setSTREAK(dr.get("str")==null?0:Integer.parseInt(dr.get("str").toString()));
			video.setSIGNALLOSS(dr.get("sig")==null?0:Integer.parseInt(dr.get("sig").toString()));
		}
		outmap.put("REPORTDATA", JSONArray.fromObject(video).toString());
		outparam.add(outmap);
		return outparam;
	}

}
