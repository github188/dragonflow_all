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

import siteview.IFunctionExtension;
import Siteview.Api.ISiteviewApi;

import com.siteview.ecc.elasticsearchutil.ElasticSearchIndexerImpl;
import com.siteview.ecc.yft.bean.Onlinerate;
/*
 * 摄像机在线趋势报表
 */
public class VideoTrendsOnlineReport implements IFunctionExtension {
	SimpleDateFormat simp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public VideoTrendsOnlineReport() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api,
			Map<String, String> inputParamMap) {
		List<Map<String,String>> outParam=new ArrayList<Map<String,String>>();
		Map<String,String> outmap=new HashMap<String,String>();
		String datetime=inputParamMap.get("DATETIME");
		if(datetime==null||datetime.length()==0)
			return outParam;
		try {
			Date startdate=simp.parse(datetime+" 00:00:00");
			Date enddate=simp.parse(datetime+" 23:59:59");
			List<Onlinerate> ons=getDaily(startdate.getTime(), enddate.getTime());
			outmap.put("REPORTDATA", JSONArray.fromObject(ons).toString());
			outParam.add(outmap);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return outParam;
	}
	public List<Onlinerate> getDaily(long starttime,long endtime){
		List<Onlinerate> ons=new ArrayList<Onlinerate>();
		ElasticSearchIndexerImpl indexer=(ElasticSearchIndexerImpl) ElasticSearchIndexerImpl.getiElasticSearchIndexerImpl();
		SearchResponse response = indexer.getClient().prepareSearch("yftitoss")
				  .setTypes("videoOnlineHoursReport")
				  .setPostFilter(QueryBuilders.rangeQuery("date")
				  .from(starttime)
				  .to(endtime))
				  .setFrom(0).setSize(25).setExplain(true)
				  .execute()
		          .actionGet();
		SearchHit[] hits = response.getHits().getHits();
		for (int i = 0; i < hits.length; i++) {
			Onlinerate on=new Onlinerate();
			SearchHit hit = hits[i];
			Map result = hit.getSource();
			
	        on.setOnlinerate(result.get("onlinerate")==null?0:
					Double.parseDouble(result.get("onlinerate").toString()));
           String logtime=result.get("date").toString();
		   on.setTime(simp.format(VideoAssessmentReport.getTime(logtime)));
           ons.add(on);
		}
		return ons;
	}
}
