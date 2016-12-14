package com.siteview.ecc.yft.report;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import siteview.IFunctionExtension;
import Siteview.Api.ISiteviewApi;

import com.siteview.ecc.elasticsearchutil.ElasticSearchIndexerImpl;
import com.siteview.ecc.yft.es.AssetsEsConnectionConfig;
import com.siteview.utils.date.DateUtils;

public class GetAssetsMaintenanceReport implements IFunctionExtension {
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public GetAssetsMaintenanceReport() {
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api, Map<String, String> inputParamMap) {
		List<Map<String, String>> outList = new ArrayList<Map<String, String>>();
		if (inputParamMap == null || inputParamMap.isEmpty()) {
			return outList;
		}
		String reportType = inputParamMap.get("REPORT_TYPE");
		String startTime = inputParamMap.get("START_TIME");
		String endTime = inputParamMap.get("END_TIME");
		if ("day".equals(reportType)) {
		} else if ("Weekly".equals(reportType)) {
		} else if ("Monthly".equals(reportType)) {
			startTime = startTime + " 00:00:00";
			endTime = endTime + " 00:00:00";
			try {                                   
				outList = getAssetsMaintenanceInfo(AssetsEsConnectionConfig.Es_Mapping_Report_Assets_M_Month, sdf.parse(startTime).getTime(), sdf.parse(endTime).getTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else if ("Yearly".equals(reportType)) {
			startTime = startTime + " 00:00:00";
			endTime = endTime + " 00:00:00";
			try {
				outList = getAssetsMaintenanceInfo(AssetsEsConnectionConfig.Es_Mapping_Report_Assets_M_Year, sdf.parse(startTime).getTime(), sdf.parse(endTime).getTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else if ("select".equals(reportType)) {
			if (startTime == null || "".equals(startTime)) {
				return outList;
			} else if (endTime == null || "".equals(endTime)) {
				return outList;
			}
		}

		return outList;
	}

	public static List<Map<String, String>> getAssetsMaintenanceInfo(String type, long starttime, long endtime) {
		List<Map<String, String>> assetsInfo = new ArrayList<Map<String, String>>();
		ElasticSearchIndexerImpl indexer = (ElasticSearchIndexerImpl) ElasticSearchIndexerImpl.getiElasticSearchIndexerImpl();
		SearchResponse response = indexer.getClient().prepareSearch(AssetsEsConnectionConfig.Es_Name).setTypes(type).setPostFilter(QueryBuilders.rangeQuery("date").from(starttime).to(endtime)).setFrom(0).setSize(10000).setExplain(true).execute().actionGet();
		SearchHit[] hits = response.getHits().getHits();
//		Calendar cal = Calendar.getInstance();
//		cal.setTimeInMillis(starttime);
//		String startt=cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DAY_OF_MONTH);
//		cal.clear();
//		cal.setTimeInMillis(endtime);
//		String endt=cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DAY_OF_MONTH);
		for (int i = 0; i < hits.length; i++) {
			Map<String,String> map = new HashMap<String,String>();
			SearchHit hit = hits[i];
			Map<?, ?> result = hit.getSource();
		
			map.put("START_TIME", 	result.get("starttime")==null?"":sdf.format( DateUtils.getESTime(result.get("starttime").toString())));
			map.put("END_TIME", result.get("endtime")==null?"": sdf.format(DateUtils.getESTime(result.get("endtime").toString())));
			map.put("ASSETS_CODE", result.get("assetscode")==null?"":result.get("assetscode").toString());
			map.put("GB_CODE", result.get("gbcode")==null?"":result.get("gbcode").toString());
			map.put("MAINTENANCE_PAY", result.get("maintenancepay")==null?"":result.get("maintenancepay").toString());
			map.put("MAINTENANCE_TIME", result.get("checktime")==null?"":sdf.format( DateUtils.getESTime(result.get("checktime").toString())));
			map.put("MANUFACTURER_INFO", result.get("manufacturerinfo")==null?"": result.get("manufacturerinfo").toString());
			map.put("CHECK_NAME", result.get("checkname")==null?"": result.get("checkname").toString());
			map.put("AREA_NAME", result.get("areaname")==null?"":result.get("areaname").toString());
			map.put("PRODUCT_TYPE", result.get("typename")==null?"":result.get("typename").toString());
			map.put("ASSETS_NAME", result.get("assetsname")==null?"":result.get("assetsname").toString());
			assetsInfo.add(map);
		}
		return assetsInfo;
	}

}
