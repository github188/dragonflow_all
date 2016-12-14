package com.siteview.ecc.yft.report;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import siteview.IFunctionExtension;
import Siteview.Api.ISiteviewApi;

import com.siteview.ecc.elasticsearchutil.ElasticSearchIndexerImpl;
import com.siteview.ecc.yft.bean.JsonValueProcessorImplTest;
import com.siteview.ecc.yft.es.AssetsEsConnectionConfig;

public class GetAssetsStatisticsReport implements IFunctionExtension {
	static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	public GetAssetsStatisticsReport() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api, Map<String, String> inputParamMap) {
		List<Map<String, String>> outlist = new ArrayList<Map<String, String>>();
		if (inputParamMap == null || inputParamMap.size() == 0)
			return outlist;
		String reportType = inputParamMap.get("REPORT_TYPE");
		String startTime = inputParamMap.get("START_TIME");
		String endTime = inputParamMap.get("END_TIME");
		List<AssetsInfo> assetsInfos=null;
		if ("day".equals(reportType)) {
		} else if ("Weekly".equals(reportType)) {
		} else if ("Monthly".equals(reportType)) {
			startTime = startTime+" 00:00:00";
			endTime =  endTime+ " 00:00:00";
			try {
				 assetsInfos = getAssetsInfo(AssetsEsConnectionConfig.Es_Mapping_Report_Assets_Info_Month,sdf1.parse(startTime).getTime(),sdf1.parse(endTime).getTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else if ("Yearly".equals(reportType)) {
			startTime = startTime+" 00:00:00";
			endTime =  endTime+ " 00:00:00";
			try {
				 assetsInfos = getAssetsInfo(AssetsEsConnectionConfig.Es_Mapping_Report_Assets_Info_Year,sdf1.parse(startTime).getTime(),sdf1.parse(endTime).getTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else if ("select".equals(reportType)) {
			if (startTime == null || "".equals(startTime)) {
				return outlist;
			} else if (endTime == null || "".equals(endTime)) {
				return outlist;
			}
		}
		Map<String, String> map = new HashMap<String, String>();
		
		JsonConfig jsonConfig = new JsonConfig();  
		jsonConfig.registerJsonValueProcessor(java.util.Date.class, new JsonValueProcessorImplTest());
				
		map.put("ASSETS_INFO", JSONArray.fromObject(assetsInfos,jsonConfig).toString());
		map.put("START_TIME", startTime);
		map.put("END_TIME", endTime);
		outlist.add(map);
		return outlist;
	}

	public static List<AssetsInfo> getAssetsInfo(String type, long starttime, long endtime) {
		List<AssetsInfo> assetsInfo = new ArrayList<AssetsInfo>();
		ElasticSearchIndexerImpl indexer = (ElasticSearchIndexerImpl) ElasticSearchIndexerImpl.getiElasticSearchIndexerImpl();
		SearchResponse response = indexer.getClient().prepareSearch(AssetsEsConnectionConfig.Es_Name).setTypes(type).setPostFilter(QueryBuilders.rangeQuery("date").from(starttime).to(endtime)).setFrom(0).setSize(10000).setExplain(true).execute().actionGet();
		SearchHit[] hits = response.getHits().getHits();
		for (int i = 0; i < hits.length; i++) {
			SearchHit hit = hits[i];
			Map<?, ?> result = hit.getSource();
			AssetsInfo info = new AssetsInfo();
			info.setAssetsBrand(result.get("assetsbrand")==null?"":result.get("assetsbrand").toString());
			info.setAssetsCode(result.get("assetscode")==null?"":result.get("assetscode").toString());
			info.setAssetsModel(result.get("assetsmodel")==null?"":result.get("assetsmodel").toString());
			info.setAssetsName(result.get("assetsname")==null?"":result.get("assetsname").toString());
			info.setAssetsStatus(result.get("assetsstatus")==null?"":result.get("assetsstatus").toString());
			info.setAssetsType(result.get("assetstype")==null?"":result.get("assetstype").toString());
			info.setCreatedBy(result.get("createdby")==null?"":result.get("createdby").toString());
			try {
				info.setCreatedDateTime(sdf.parse(result.get("createddatetime")==null?"1970-01-01T08:00:00.000Z":result.get("createddatetime").toString()));
				info.setWarrantyPeriod(sdf.parse(result.get("warrantyperiod")==null?"1970-01-01T08:00:00.000Z":result.get("warrantyperiod").toString()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			info.setMaintenanceNumber(result.get("maintenancenumber")==null?0:Integer.parseInt(result.get("maintenancenumber").toString()));
		
			info.setWpflag(Boolean.parseBoolean(result.get("wpflag").toString()));
			assetsInfo.add(info);
		}
		return assetsInfo;
	}

}
