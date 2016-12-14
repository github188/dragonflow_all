package com.siteview.ecc.yft.odata;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import siteview.IFunctionExtension;
import Siteview.Api.ISiteviewApi;

import com.siteview.ecc.elasticsearchutil.ElasticSearchIndexerImpl;
import com.siteview.ecc.yft.bean.LoseVideorecord;
/*
 * 摄像机详细报表数据
 */
public class VideoDetailedReport implements IFunctionExtension {
	SimpleDateFormat simp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	SimpleDateFormat simp0=new SimpleDateFormat("yyyy-MM-dd");
	public VideoDetailedReport() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api,
			Map<String, String> inputParamMap) {
		String type=inputParamMap.get("TYPE");
		String datatime=inputParamMap.get("DATETIME");
		String endtime=inputParamMap.get("ENDTIME");
				
		Date startdate=null;
		Date enddate=null;
		if(datatime!=null){
			try {
				startdate=simp.parse(datatime+" 00:00:00.000");
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if(endtime!=null){
			try {
				enddate=simp.parse(endtime+" 23:59:59.999");
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else{
			try {
				enddate=simp.parse(datatime+" 23:59:59.999");
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if(type.endsWith("Weekly")){
				String s=VideoAssessmentReport.getTimeBegin(0, startdate);
				try {
					startdate=simp.parse(s);
					enddate=simp.parse(VideoAssessmentReport.getTimeEnd(startdate));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}else if(type.endsWith("Monthly")){
				startdate.setDate(1);
			}else if(type.endsWith("Yearly")){
				startdate.setMonth(1);
				startdate.setDate(1);
			}
		}
		List<Map<String,String>> outParam=new ArrayList<Map<String,String>>();
		List<LoseVideorecord> list=getDaily(type, startdate.getTime(), enddate.getTime());
		Map<String,String> outmap=new HashMap<String,String>();
		outmap.put("REPORTDATA", JSONArray.fromObject(list).toString());
		outParam.add(outmap);
		return outParam;
	}
	public List<LoseVideorecord> getDaily(String type,long starttime,long endtime){
		List<LoseVideorecord> ons=new ArrayList<LoseVideorecord>();
		ElasticSearchIndexerImpl indexer=(ElasticSearchIndexerImpl) ElasticSearchIndexerImpl.getiElasticSearchIndexerImpl();
		SearchResponse response = indexer.getClient().prepareSearch("yftitoss")
				  .setTypes(type)
				  .setPostFilter(QueryBuilders.rangeQuery("date")
				  .from(starttime)
				  .to(endtime))
				  .addSort(SortBuilders.fieldSort("date").unmappedType("string").order(SortOrder.ASC))
				  .setFrom(0).setSize(1000).setExplain(true)
				  .execute()
		          .actionGet();
		SearchHit[] hits = response.getHits().getHits();
		for (int i = 0; i < hits.length; i++) {
			LoseVideorecord on=new LoseVideorecord();
			SearchHit hit = hits[i];
			Map result = hit.getSource();
			on.setIpaddress(result.get("ipaddress")==null?"":
				result.get("ipaddress").toString());
	        on.setVideoname(result.get("videoname")==null?"":
				result.get("videoname").toString());
	        on.setVideoflag(result.get("videoflag")==null?"":
				result.get("videoflag").toString());
	        on.setIplace(result.get("installationaddress")==null?"":
				result.get("installationaddress").toString());
	        on.setLongtime(result.get("longoffline")==null?0:
				Long.parseLong(result.get("longoffline").toString()));
		   on.setTime(result.get("offperiod")==null?"":
				result.get("offperiod").toString());
		   on.setLosttimes(result.get("losttimes")==null?0
				   :Long.parseLong(result.get("losttimes").toString()));
		   String logtime=result.get("date").toString();
		   on.setDate(simp0.format(VideoAssessmentReport.getTime(logtime)));
           ons.add(on);
		}
		return ons;
	}
}