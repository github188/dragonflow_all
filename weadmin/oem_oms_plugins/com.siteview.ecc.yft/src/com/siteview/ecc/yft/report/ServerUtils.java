package com.siteview.ecc.yft.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.range.date.InternalDateRange;
import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg;
import org.elasticsearch.search.aggregations.metrics.max.InternalMax;

import Siteview.Api.ISiteviewApi;

import com.siteview.ecc.elasticsearchutil.ElasticSearchIndexer;
import com.siteview.ecc.elasticsearchutil.ElasticSearchIndexerImpl;
import com.siteview.ecc.yft.bean.ServerDetail;
import com.siteview.ecc.yft.es.ServerDetailsDaily;
import com.siteview.utils.text.TextUtils;

/**
 * 非视频类工具类
 * 
 * @author Administrator
 *
 */
public class ServerUtils {
	/**
	 * 从es获取非视频类设备报表数据
	 * 
	 * @param api
	 * @param starttime
	 * @param endtime
	 * @param type
	 * @param eqTypeEn
	 * @return
	 */
	public static List<ServerDetail> getServerDetailsFromES(ISiteviewApi api, Long starttime,
			Long endtime, String type, String eqTypeEn) {
		String area = InitReport.map.get("the_system_name");
		String areacode = InitReport.map.get("the_system_code");
		String parea = InitReport.map.get("the_parent_name") == null ? "" : InitReport.map
				.get("the_parent_name");
		String pareacode = InitReport.map.get("the_parent_code") == null ? "" : InitReport.map
				.get("the_parent_code");
		List<ServerDetail> servers = new ArrayList<ServerDetail>();
		ServerDetail server = new ServerDetail();
		try{
			ElasticSearchIndexerImpl indexer = (ElasticSearchIndexerImpl) ElasticSearchIndexerImpl
					.getiElasticSearchIndexerImpl();
			SearchResponse response = indexer
					.getClient()
					.prepareSearch("yftitoss")
					.setTypes(type)
					.setQuery(QueryBuilders.matchQuery("servertype", eqTypeEn))
					.setPostFilter(QueryBuilders.rangeQuery("date").from(starttime).to(endtime))
					// 指定值的范围
					.addAggregation(
							AggregationBuilders
									.dateRange("agg_date")
									.field("date")
									.addRange(starttime, endtime)
									.subAggregation(AggregationBuilders.max("max_total").field("total"))
									.subAggregation(AggregationBuilders.max("max_onlinecount").field("onlinecount"))
									.subAggregation(AggregationBuilders.max("max_intactcount").field("intactcount"))
									.subAggregation(
											AggregationBuilders.max("max_unavailablecount").field("unavailablecount"))
									.subAggregation(AggregationBuilders.avg("avg_onlinerate").field("onlinerate"))
									.subAggregation(AggregationBuilders.avg("avg_intactrate").field("intactrate")))
									.setFrom(0).setSize(1000).setExplain(true)
									.execute().actionGet();
	
			Map<String, Aggregation> aggMap = response.getAggregations().asMap();
			InternalDateRange range = (InternalDateRange) aggMap.get("agg_date");
			org.elasticsearch.search.aggregations.bucket.range.date.InternalDateRange.Bucket bu = (org.elasticsearch.search.aggregations.bucket.range.date.InternalDateRange.Bucket) ((List) range
					.getBuckets()).get(0);
			List list = bu.getAggregations().asList();
			
			for (int i = 0; i < list.size(); i++) {
				Object obj = list.get(i);
				if (obj instanceof InternalAvg) {
					InternalAvg inavg = (InternalAvg) obj;
					if (inavg.getName().equals("avg_onlinerate")) {
						server.setOnlinerate(TextUtils.formatDouble(inavg.getValue(),4));
					} else if (inavg.getName().equals("avg_intactrate")) {
						server.setIntactrate(TextUtils.formatDouble(inavg.getValue(),4));
					}
				} else if (obj instanceof InternalMax) {
					InternalMax sum = (InternalMax) obj;
					if (sum.getName().equals("max_total"))
						server.setTotal((long) sum.getValue());
					else if (sum.getName().equals("max_onlinecount")) {
						server.setOnlinecount((long) sum.getValue());
					} else if (sum.getName().equals("max_intactcount"))
						server.setIntactcount((long) sum.getValue());
					else if (sum.getName().equals("max_unavailablecount")) {
						server.setUnavailablecount((long) sum.getValue());
					}
				}
			}
		}catch(Exception ex){}
		server.setType(type);
		server.setServertype(eqTypeEn);
		server.setDate(new Date(endtime));
		server.setType(type);
		server.setArea(area);
		server.setAreacode(areacode);
		server.setParentarea(parea);
		server.setParentareacode(pareacode);
		servers.add(server);
		return servers;
	}

	/**
	 * 保存非视频类数据到ES中
	 * 
	 * @param index
	 * @param type
	 * @param details
	 * @param date
	 */
	public static List<ServerDetail> setData(String index, String type, List<ServerDetail> details, Date date) {
		ElasticSearchIndexer indexer = ElasticSearchIndexerImpl.getiElasticSearchIndexerImpl();
		ServerDetailsDaily schema = new ServerDetailsDaily();
		schema.setIndex(index);
		schema.setType(type);
		for (ServerDetail detail : details) {
			saveServerEsData(indexer, schema, detail, date);
		}
		return details;
	}
	
	public static void setData(String index, String type, ServerDetail detail, Date date) {
		ElasticSearchIndexer indexer = ElasticSearchIndexerImpl.getiElasticSearchIndexerImpl();
		ServerDetailsDaily schema = new ServerDetailsDaily();
		schema.setIndex(index);
		schema.setType(type);
		saveServerEsData(indexer, schema, detail, date);
	}

	public static void saveServerEsData(ElasticSearchIndexer indexer, ServerDetailsDaily schema,
			ServerDetail detail, Date date) {
		Map<String, Object> mapping = new HashMap<String, Object>();
		mapping.put("total", detail.getTotal());// 总数
		mapping.put("onlinecount", detail.getOnlinecount());
		mapping.put("onlinerate", detail.getOnlinerate());
		mapping.put("intactrate", detail.getIntactrate());
		mapping.put("intactcount", detail.getIntactcount());
		mapping.put("unavailablecount", detail.getUnavailablecount());
		mapping.put("servertype", detail.getServertype());

		mapping.put("area", detail.getArea());
		mapping.put("areacode", detail.getAreacode());
		mapping.put("parentarea", detail.getParentarea());
		mapping.put("parentareacode", detail.getParentareacode());
		Calendar calendar = Calendar.getInstance();
		if(detail.getDate() != null)
			calendar.setTime(detail.getDate());
		else
			calendar.setTime(date);
		calendar.add(Calendar.HOUR_OF_DAY, -2);
		mapping.put("date", calendar.getTime());
		try {
			indexer.index(schema, mapping);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
