package com.siteview.ecc.yft.es;

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
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;

import com.siteview.ecc.elasticsearchutil.ElasticSchema;
import com.siteview.ecc.elasticsearchutil.ElasticSearchIndexer;
import com.siteview.ecc.elasticsearchutil.ElasticSearchIndexerImpl;
import com.siteview.ecc.yft.bean.WorkOrderReport;
import com.siteview.ecc.yft.report.InitReport;

/**
 * 英飞拓工单报表
 * 
 * @author Administrator
 *
 */
public class WorkOrderReportYFT implements ElasticSchema {
	private String index;
	private String type;

	@Override
	public String getIndex() {
		return index;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public Map<String, Class<?>> getFieldNameTypeMapping() {
		Map<String, Class<?>> mapping = new HashMap<String, Class<?>>();
		mapping.put("newtotal", Long.class);// 新建工单总数
		mapping.put("completecount", Long.class);// 完成工单数
		mapping.put("completerate", Double.class);// 完成工单率
		mapping.put("avgduration", Double.class);// 平均维护时长
		mapping.put("outcompleterate", Double.class);// 超时完成率
		mapping.put("outnocompleterate", Double.class);// 超时未完成率
		mapping.put("area", String.class);
		mapping.put("areacode", String.class);
		mapping.put("parentarea", String.class);
		mapping.put("parentareacode", String.class);
		mapping.put("date", Date.class);
		return mapping;
	}

	public static void saveData(String index, String type, Map<String, Object> mapping) {
		ElasticSearchIndexer indexer = ElasticSearchIndexerImpl.getiElasticSearchIndexerImpl();
		WorkOrderReportYFT schema = new WorkOrderReportYFT();
		schema.setIndex(index);
		schema.setType(type);
		try {
			indexer.index(schema, mapping);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static WorkOrderReport saveData(String index, String type, WorkOrderReport orderReport) {
		ElasticSearchIndexer indexer = ElasticSearchIndexerImpl.getiElasticSearchIndexerImpl();
		WorkOrderReportYFT schema = new WorkOrderReportYFT();
		schema.setIndex(index);
		schema.setType(type);
		try {
			indexer.index(schema, getMapFromObject(orderReport));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orderReport;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 从ES中获取数据计算
	 */
	public static Map<String, Object> calculateReport(String type, long startTime, Date eDate) {
//		String area = InitReport.map.get("the_system_name");
//		String areacode = InitReport.map.get("the_system_code");
//		String parea = InitReport.map.get("the_parent_name") == null ? "" : InitReport.map
//				.get("the_parent_name");
//		String pareacode = InitReport.map.get("the_parent_code") == null ? "" : InitReport.map
//				.get("the_parent_code");
//		Map<String, Object> mapping = new HashMap<String, Object>();
//		// String endTime = DateUtils.formatDefaultDate(eDate);
//		List list = getEsData(type, startTime, eDate);
//		if (list.size() > 0) {
//			Calendar calendar = Calendar.getInstance();
//			calendar.setTime(eDate);
//			calendar.add(Calendar.HOUR_OF_DAY, -2);
//			mapping.put("date", calendar.getTime());
//		}
//		for (int i = 0; i < list.size(); i++) {
//			Object obj = list.get(i);
//			if (obj instanceof InternalAvg) {
//				InternalAvg inavg = (InternalAvg) obj;
//				if (inavg.getName().equals("avg_completionRate")) {
//					mapping.put("completerate", inavg.getValue());
//				} else if (inavg.getName().equals("avg_maintainduration")) {
//					mapping.put("avgduration", inavg.getValue());
//				} else if (inavg.getName().equals("avg_outcompleterate")) {
//					mapping.put("outcompleterate", inavg.getValue());
//				} else if (inavg.getName().equals("avg_outnocompleterate")) {
//					mapping.put("outnocompleterate", inavg.getValue());
//				}
//			} else if (obj instanceof InternalSum) {
//				InternalSum sum = (InternalSum) obj;
//				if (sum.getName().equals("sum_newtotal")) {
//					mapping.put("newtotal", (long) sum.getValue());
//				} else if (sum.getName().equals("sum_completecount")) {
//					mapping.put("completecount", (long) sum.getValue());
//				}
//			}
//		}
//		mapping.put("area", area);
//		mapping.put("areacode", areacode);
//		mapping.put("parentarea", parea);
//		mapping.put("parentareacode", pareacode);
//		return mapping;
		return getMapFromObject(calculateWorkOrderReport(type, startTime, eDate));
	}
	
	public static WorkOrderReport calculateWorkOrderReport(String type, long startTime, Date eDate) {
		String area = InitReport.map.get("the_system_name");
		String areacode = InitReport.map.get("the_system_code");
		String parea = InitReport.map.get("the_parent_name") == null ? "" : InitReport.map
				.get("the_parent_name");
		String pareacode = InitReport.map.get("the_parent_code") == null ? "" : InitReport.map
				.get("the_parent_code");
		WorkOrderReport orderReport = new WorkOrderReport();
		List list = getEsData(type, startTime, eDate);
		if (list.size() > 0) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(eDate);
			calendar.add(Calendar.HOUR_OF_DAY, -2);
			orderReport.setDate(calendar.getTime());
		}
		for (int i = 0; i < list.size(); i++) {
			Object obj = list.get(i);
			if (obj instanceof InternalAvg) {
				InternalAvg inavg = (InternalAvg) obj;
				if (inavg.getName().equals("avg_completionRate")) {
					orderReport.setCompleterate(inavg.getValue());
				} else if (inavg.getName().equals("avg_maintainduration")) {
					orderReport.setAvgduration(inavg.getValue());
				} else if (inavg.getName().equals("avg_outcompleterate")) {
					orderReport.setOutcompleterate(inavg.getValue());
				} else if (inavg.getName().equals("avg_outnocompleterate")) {
					orderReport.setOutnocompleterate(inavg.getValue());
				}
			} else if (obj instanceof InternalSum) {
				InternalSum sum = (InternalSum) obj;
				if (sum.getName().equals("sum_newtotal")) {
					orderReport.setNewtotal((long) sum.getValue());
				} else if (sum.getName().equals("sum_completecount")) {
					orderReport.setCompletecount((long) sum.getValue());
				}
			}
		}
		orderReport.setType(type);
		orderReport.setArea(area);
		orderReport.setAreacode(areacode);
		orderReport.setParentarea(parea);
		orderReport.setParentareacode(pareacode);
		return orderReport;
	}

	public static List getEsData(String type, long startTime, Date eDate){
		ElasticSearchIndexerImpl indexer = (ElasticSearchIndexerImpl) ElasticSearchIndexerImpl
				.getiElasticSearchIndexerImpl();
		SearchResponse response = indexer
				.getClient()
				.prepareSearch("yftitoss")
				.setTypes(type)
				.setPostFilter(QueryBuilders.rangeQuery("date").from(startTime).to(eDate.getTime()))
				// 指定值的范围
				.addAggregation(
						AggregationBuilders.dateRange("agg_date")
						.field("date")
						.addRange(startTime, eDate.getTime())
						.subAggregation(AggregationBuilders.sum("sum_newtotal").field("newtotal"))
						// 新建总数
						.subAggregation(AggregationBuilders.sum("sum_completecount").field("completecount")) // 完成总数
						.subAggregation(AggregationBuilders.avg("avg_completionRate").field("completerate")) // 完成率
						.subAggregation(AggregationBuilders.avg("avg_maintainduration").field("avgduration")) // 维护时长
						.subAggregation(AggregationBuilders.avg("avg_outcompleterate").field("outcompleterate")) // 超时完成率
						.subAggregation(AggregationBuilders.avg("avg_outnocompleterate").field("outnocompleterate"))// 超时未完成率
						)
						.setFrom(0).setSize(1000).setExplain(true)
						.execute().actionGet();
		Map<String, Aggregation> aggMap = response.getAggregations().asMap();
		InternalDateRange range = (InternalDateRange) aggMap.get("agg_date");
		org.elasticsearch.search.aggregations.bucket.range.date.InternalDateRange.Bucket bu = (org.elasticsearch.search.aggregations.bucket.range.date.InternalDateRange.Bucket) ((List) range
				.getBuckets()).get(0);
		return bu.getAggregations().asList();
	}
	
	/**
	 * 从对象封装mapping集合
	 * @param orderReport
	 */
	public static Map<String, Object> getMapFromObject(WorkOrderReport orderReport) {
		Map<String, Object> mapping = new HashMap<String, Object>();
		mapping.put("newtotal", orderReport.getNewtotal());// 新建工单总数
		mapping.put("completecount", orderReport.getCompletecount());// 完成工单数
		mapping.put("completerate", orderReport.getCompleterate());// 完成工单率
		mapping.put("avgduration", orderReport.getAvgduration());// 平均维护时长
		mapping.put("outcompleterate", orderReport.getOutcompleterate());// 超时完成率
		mapping.put("outnocompleterate", orderReport.getOutnocompleterate());// 超时未完成率
		mapping.put("area", orderReport.getArea());
		mapping.put("areacode", orderReport.getAreacode());
		mapping.put("parentarea", orderReport.getParentarea());
		mapping.put("parentareacode", orderReport.getParentareacode());
		mapping.put("date", orderReport.getDate());
		return mapping;
	}
}
