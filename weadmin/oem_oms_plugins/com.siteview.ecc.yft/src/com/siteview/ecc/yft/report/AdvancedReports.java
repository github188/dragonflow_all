package com.siteview.ecc.yft.report;

import java.text.DecimalFormat;
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
import org.elasticsearch.search.aggregations.bucket.range.date.DateRangeBuilder;
import org.elasticsearch.search.aggregations.bucket.range.date.InternalDateRange;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.elasticsearch.search.aggregations.metrics.tophits.InternalTopHits;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.Api.ISiteviewApi;

import com.siteview.alarm.bean.DepartmentalReports;
import com.siteview.ecc.elasticsearchutil.ElasticSearchIndexerImpl;
import com.siteview.ecc.yft.bean.OrganizationCode;
import com.siteview.ecc.yft.bean.ServerDetail;
import com.siteview.ecc.yft.bean.WorkOrderReport;
import com.siteview.ecc.yft.es.WorkOrderReportYFT;
import com.siteview.utils.db.DBQueryUtils;

/*
 * 厅级报表
 */
public class AdvancedReports {
	public static Map<String, OrganizationCode> getOrganization(ISiteviewApi api) {
		Map<String, OrganizationCode> map = new HashMap<String, OrganizationCode>();
		String sql = "select * from Organization";
		DataTable dt = DBQueryUtils.Select(sql, api);
		for (DataRow dr : dt.get_Rows()) {
			String o_code = dr.get("o_code") == null ? "" : dr.get("o_code").toString();
			String o_name = dr.get("o_name") == null ? "" : dr.get("o_name").toString();
			String o_parent = dr.get("parentId") == null ? "" : dr.get("parentId").toString();
			OrganizationCode organ = map.get(o_code);
			if (organ == null)
				organ = new OrganizationCode();
			organ.setO_code(o_code);
			organ.setO_name(o_name);
			organ.setO_parent_code(o_parent);
			if (o_parent.length() > 0) {
				OrganizationCode parentorgan = map.get(o_parent);
				if (parentorgan == null)
					parentorgan = new OrganizationCode();
				parentorgan.getList().add(organ);
				map.put(o_parent, parentorgan);
			}
			map.put(o_code, organ);
		}
		return map;
	}

	public static void createReport(Map<String, OrganizationCode> map, ISiteviewApi api,
			long starttime, long endtime, String[] tablename) {
		List<String> code = new ArrayList<String>();
		createVideoChildReport(api, tablename[0], map, code, starttime, endtime);
		code.clear();
		createStorageChildReport(api, tablename[1], map, code, starttime, endtime);
		code.clear();
		createStorageChildReport(api, tablename[2], map, code, starttime, endtime);
		code.clear();
		createStorageChildReport(api, tablename[3], map, code, starttime, endtime);
	}

	public static void createVideoChildReport(ISiteviewApi api, String type,
			Map<String, OrganizationCode> codemap, List<String> code, long starttime, long endtime) {
		List<DepartmentalReports> deps = VideoReport(api, starttime, endtime, type, codemap, code);
		if (deps.size() > 0) {
			for (DepartmentalReports dep : deps) {
				com.siteview.ecc.yft.es.VideoReport.saveVideoDaily(dep, "yftitoss", type, new Date(
						starttime + 4000));
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			createVideoChildReport(api, type, codemap, code, starttime, endtime);
		}
	}

	public static void createStorageChildReport(ISiteviewApi api, String type,
			Map<String, OrganizationCode> codemap, List<String> code, long starttime, long endtime) {
		List<DepartmentalReports> deps = StorageReport(api, starttime, endtime, type, codemap, code);
		if (deps.size() > 0) {
			for (DepartmentalReports dep : deps) {
				com.siteview.ecc.yft.es.StorageReport.saveVideoDaily(dep, "yftitoss", type, new Date(
						starttime + 4000));
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			createStorageChildReport(api, type, codemap, code, starttime, endtime);
		}
	}

	static DecimalFormat df = new DecimalFormat("#0.0000");

	public static List<DepartmentalReports> VideoReport(ISiteviewApi api, Long starttime,
			Long endtime, String type, Map<String, OrganizationCode> codemap, List<String> code) {
		List<DepartmentalReports> deps = new ArrayList<DepartmentalReports>();
		ElasticSearchIndexerImpl indexer = (ElasticSearchIndexerImpl) ElasticSearchIndexerImpl
				.getiElasticSearchIndexerImpl();
		SearchResponse response = indexer
				.getClient()
				.prepareSearch("yftitoss")
				.setTypes(type)
				.setPostFilter(QueryBuilders.rangeQuery("date").from(starttime).to(endtime))
				// 指定值的范围
				.addAggregation(
						AggregationBuilders
								.dateRange("agg_date")
								.field("date")
								.addRange(starttime, endtime)
								.subAggregation(
										AggregationBuilders
												.terms("parentareacodes")
												.field("parentareacode")
												.subAggregation(AggregationBuilders.topHits("parentareacode"))
												.subAggregation(AggregationBuilders.sum("sum_total").field("total"))
												.subAggregation(
														AggregationBuilders.sum("avg_onlinecount").field("onlinecount"))
												.subAggregation(
														AggregationBuilders.avg("avg_onlinerate").field("onlinerate"))
												.subAggregation(
														AggregationBuilders.sum("avg_videolossduration").field(
																"videolossduration"))
												.subAggregation(
														AggregationBuilders.avg("avg_anomalyrate").field("anomalyrate"))
												.subAggregation(
														AggregationBuilders.sum("avg_anomalycount").field("anomalycount"))
												.subAggregation(
														AggregationBuilders.avg("avg_videolossrate").field("videolossrate"))
												.subAggregation(
														AggregationBuilders.sum("avg_videolosscount").field("videolosscount"))
												.subAggregation(
														AggregationBuilders.avg("avg_intactrate").field("intactrate"))
												.subAggregation(
														AggregationBuilders.sum("avg_intactcount").field("intactcount")).size(1000)))
					.setFrom(0).setSize(1000).setExplain(true)
					.execute().actionGet();
		Map<String, Aggregation> aggMap = response.getAggregations().asMap();
		InternalDateRange range = (InternalDateRange) aggMap.get("agg_date");
		org.elasticsearch.search.aggregations.bucket.range.date.InternalDateRange.Bucket bu = (org.elasticsearch.search.aggregations.bucket.range.date.InternalDateRange.Bucket) ((List) range
				.getBuckets()).get(0);
		List list = bu.getAggregations().asList();
		for (int i = 0; i < list.size(); i++) {
			try {
				StringTerms strterms = (StringTerms) list.get(i);
				List<Bucket> buckets = strterms.getBuckets();
				for (Bucket buck : buckets) {
					DepartmentalReports dep = new DepartmentalReports();
					Map map = buck.getAggregations().asMap();
					InternalTopHits areacodehit = (InternalTopHits) map.get("parentareacode");
					Map<String, Object> hashmap = areacodehit.getHits().getAt(0).getSource();
					String areacode = hashmap.get("parentareacode").toString();
					dep.setArea(codemap.get(areacode).getO_name());
					dep.setAreacode(areacode);
					dep.setParentareacode(codemap.get(areacode) == null ? "" : codemap.get(areacode)
							.getO_parent_code());
					dep.setParentarea(codemap.get(dep.getParentareacode()) == null ? "" : codemap.get(
							dep.getParentareacode()).getO_name());
					if (code.contains(areacode))
						continue;
					else
						code.add(areacode);
					InternalAvg inavg = null;
					InternalSum sum = (InternalSum) map.get("avg_onlinecount");
					dep.setOnlinecount((int) sum.getValue());
					inavg = (InternalAvg) map.get("avg_onlinerate");
					dep.setOnlinerate(Double.parseDouble(df.format(inavg.getValue())));
					sum = (InternalSum) map.get("avg_videolossduration");
					dep.setVideolossduration(sum.getValue());
					inavg = (InternalAvg) map.get("avg_anomalyrate");
					dep.setAnomalyrate(Double.parseDouble(df.format(inavg.getValue())));
					sum = (InternalSum) map.get("avg_anomalycount");
					dep.setAnomalycount((int) sum.getValue());
					inavg = (InternalAvg) map.get("avg_videolossrate");
					dep.setVideolossrate(Double.parseDouble(df.format(inavg.getValue())));
					sum = (InternalSum) map.get("avg_videolosscount");
					dep.setVideolosscount((int) sum.getValue());
					inavg = (InternalAvg) map.get("avg_intactrate");
					dep.setIntactrate(Double.parseDouble(df.format(inavg.getValue())));
					sum = (InternalSum) map.get("avg_intactcount");
					dep.setIntactcount((int) sum.getValue());
					InternalSum max = (InternalSum) map.get("sum_total");
					dep.setTotal((int) max.getValue());
					dep.setDate(new Date(endtime));
					if(dep.getOnlinecount()>0&&dep.getTotal()>0)
						dep.setOnlinerate(Double.parseDouble(df.format(Double.parseDouble(dep.getOnlinecount()+"")/dep.getTotal())));
					if(dep.getIntactcount()>0&&dep.getTotal()>0)
						dep.setIntactrate(Double.parseDouble(df.format(Double.parseDouble(dep.getIntactcount()+"")/dep.getTotal())));
					deps.add(dep);
				}
			} catch (Exception e) {
				continue;
			}
		}
		return deps;
	}

	public static List<DepartmentalReports> StorageReport(ISiteviewApi api, Long starttime,
			Long endtime, String type, Map<String, OrganizationCode> codemap, List<String> code) {
		List<DepartmentalReports> deps = new ArrayList<DepartmentalReports>();
		ElasticSearchIndexerImpl indexer = (ElasticSearchIndexerImpl) ElasticSearchIndexerImpl
				.getiElasticSearchIndexerImpl();
		SearchResponse response = indexer
				.getClient()
				.prepareSearch("yftitoss")
				.setTypes(type)
				.setPostFilter(QueryBuilders.rangeQuery("date").from(starttime).to(endtime))
				// 指定值的范围
				.addAggregation(
						AggregationBuilders
								.dateRange("agg_date")
								.field("date")
								.addRange(starttime, endtime)
								.subAggregation(
										AggregationBuilders
												.terms("parentareacodes")
												.field("parentareacode")
												.subAggregation(AggregationBuilders.topHits("parentareacode"))
												.subAggregation(AggregationBuilders.sum("sum_total").field("total"))
												.subAggregation(
														AggregationBuilders.sum("avg_onlinecount").field("onlinecount"))
												.subAggregation(
														AggregationBuilders.avg("avg_onlinerate").field("onlinerate"))
												.subAggregation(AggregationBuilders.sum("avg_notlong").field("notlong"))
												.subAggregation(
														AggregationBuilders.avg("avg_intactrate").field("intactrate"))
												.subAggregation(
														AggregationBuilders.sum("avg_intactcount").field("intactcount")).size(1000)))
												.setFrom(0).setSize(1000).setExplain(true)
				.execute().actionGet();
		Map<String, Aggregation> aggMap = response.getAggregations().asMap();
		InternalDateRange range = (InternalDateRange) aggMap.get("agg_date");
		org.elasticsearch.search.aggregations.bucket.range.date.InternalDateRange.Bucket bu = (org.elasticsearch.search.aggregations.bucket.range.date.InternalDateRange.Bucket) ((List) range
				.getBuckets()).get(0);
		List list = bu.getAggregations().asList();
		for (int i = 0; i < list.size(); i++) {
			StringTerms strterms = (StringTerms) list.get(i);
			List<Bucket> buckets = strterms.getBuckets();
			for (Bucket buck : buckets) {
				try {
					DepartmentalReports dep = new DepartmentalReports();
					Map map = buck.getAggregations().asMap();
					InternalTopHits areacodehit = (InternalTopHits) map.get("parentareacode");
					Map<String, Object> hashmap = areacodehit.getHits().getAt(0).getSource();
					String areacode = hashmap.get("parentareacode").toString();
					dep.setArea(codemap.get(areacode).getO_name());
					dep.setAreacode(areacode);
					dep.setParentareacode(codemap.get(areacode) == null ? "" : codemap.get(areacode)
							.getO_parent_code());
					dep.setParentarea(codemap.get(dep.getParentareacode()) == null ? "" : codemap.get(
							dep.getParentareacode()).getO_name());
					if (code.contains(areacode))
						continue;
					else
						code.add(areacode);
					InternalAvg inavg = null;
					InternalSum sum = (InternalSum) map.get("avg_onlinecount");
					dep.setOnlinecount((int) sum.getValue());
					inavg = (InternalAvg) map.get("avg_onlinerate");
					dep.setOnlinerate(Double.parseDouble(df.format(inavg.getValue())));
					sum = (InternalSum) map.get("avg_notlong");
					dep.setNotlong((long) sum.getValue());
					inavg = (InternalAvg) map.get("avg_intactrate");
					dep.setIntactrate(Double.parseDouble(df.format(inavg.getValue())));
					sum = (InternalSum) map.get("avg_intactcount");
					dep.setIntactcount((int) sum.getValue());
					InternalSum max = (InternalSum) map.get("sum_total");
					dep.setTotal((int) max.getValue());
					dep.setDate(new Date(endtime));
					if(dep.getOnlinecount()>0&&dep.getTotal()>0)
						dep.setOnlinerate(Double.parseDouble(df.format(Double.parseDouble(dep.getOnlinecount()+"")/dep.getTotal())));
					if(dep.getIntactcount()>0&&dep.getTotal()>0)
						dep.setIntactrate(Double.parseDouble(df.format(Double.parseDouble(dep.getIntactcount()+"")/dep.getTotal())));
					deps.add(dep);
				} catch (Exception e) {
					continue;
				}
			}
		}
		return deps;
	}

	/**
	 * 统计下级上传上来的工单，生成新数据
	 * 
	 * @param api
	 * @param type
	 * @param codemap
	 * @param starttime
	 * @param endtime
	 */
	public static void createWorkordereChildReport(ISiteviewApi api, String type,
			Map<String, OrganizationCode> codemap, long starttime, long endtime) {
		List<WorkOrderReport> wrs = WorkOrderReport(api, starttime, endtime, type, codemap);
		for (WorkOrderReport orderReport : wrs) {
			WorkOrderReportYFT.saveData("yftitoss", type, orderReport);
		}
	}

	public static List<WorkOrderReport> WorkOrderReport(ISiteviewApi api, Long starttime,
			Long endtime, String type, Map<String, OrganizationCode> codemap) {
		List<WorkOrderReport> deps = new ArrayList<WorkOrderReport>();
		ElasticSearchIndexerImpl indexer = (ElasticSearchIndexerImpl) ElasticSearchIndexerImpl
				.getiElasticSearchIndexerImpl();
		SearchResponse response = indexer
				.getClient()
				.prepareSearch("yftitoss")
				.setTypes(type)
				.setPostFilter(QueryBuilders.rangeQuery("date").from(starttime).to(endtime))
				// 指定值的范围
				.addAggregation(
						AggregationBuilders
								.dateRange("agg_date")
								.field("date")
								.addRange(starttime, endtime)
								.subAggregation(
										AggregationBuilders
												.terms("parentareacodes")
												.field("parentareacode")
												.subAggregation(AggregationBuilders.topHits("parentareacode"))
												.subAggregation(AggregationBuilders.sum("sum_total").field("newtotal"))
												.subAggregation(
														AggregationBuilders.sum("sum_completecount").field("completecount"))
												.subAggregation(
														AggregationBuilders.avg("avg_completerate").field("completerate"))
												.subAggregation(
														AggregationBuilders.sum("avg_avgduration").field("avgduration"))
												.subAggregation(
														AggregationBuilders.avg("avg_outcompleterate").field("outcompleterate"))
												.subAggregation(
														AggregationBuilders.avg("avg_outnocompleterate").field(
																"outnocompleterate")).size(1000)))
																.setFrom(0).setSize(1000).setExplain(true).execute().actionGet();
		Map<String, Aggregation> aggMap = response.getAggregations().asMap();
		InternalDateRange range = (InternalDateRange) aggMap.get("agg_date");
		org.elasticsearch.search.aggregations.bucket.range.date.InternalDateRange.Bucket bu = (org.elasticsearch.search.aggregations.bucket.range.date.InternalDateRange.Bucket) ((List) range
				.getBuckets()).get(0);
		List list = bu.getAggregations().asList();
		for (int i = 0; i < list.size(); i++) {
			StringTerms strterms = (StringTerms) list.get(i);
			List<Bucket> buckets = strterms.getBuckets();
			for (Bucket buck : buckets) {
				try {
					WorkOrderReport dep = new WorkOrderReport();
					Map map = buck.getAggregations().asMap();
					InternalTopHits areacodehit = (InternalTopHits) map.get("parentareacode");
					Map<String, Object> hashmap = areacodehit.getHits().getAt(0).getSource();
					String areacode = hashmap.get("parentareacode").toString();
					dep.setArea(codemap.get(areacode).getO_name());
					dep.setAreacode(areacode);
					String parentCode = codemap.get(areacode).getO_parent_code();
					if (parentCode == null)
						parentCode = "";
					dep.setParentareacode(parentCode);
					if (parentCode.length() > 0)
						dep.setParentarea(codemap.get(dep.getParentareacode()).getO_name());
					else
						dep.setParentarea("");
					
					InternalAvg inavg = (InternalAvg) map.get("avg_completerate");
					dep.setCompleterate(inavg.getValue());
					InternalSum sum = (InternalSum) map.get("avg_avgduration");
					dep.setAvgduration((long) sum.getValue());
					inavg = (InternalAvg) map.get("avg_outcompleterate");
					dep.setOutcompleterate(Double.parseDouble(df.format(inavg.getValue())));
					inavg = (InternalAvg) map.get("avg_outnocompleterate");
					dep.setOutnocompleterate(Double.parseDouble(df.format(inavg.getValue())));
					InternalSum max = (InternalSum) map.get("sum_total");
					dep.setNewtotal((long) max.getValue());
					max = (InternalSum) map.get("sum_completecount");
					dep.setCompletecount((long) inavg.getValue());
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(new Date(endtime));
					calendar.add(Calendar.HOUR_OF_DAY, -2);
					dep.setDate(calendar.getTime());
					deps.add(dep);
				} catch (Exception e) {
					continue;
				}
			}
		}
		return deps;
	}

	public static List<ServerDetail> createServerChildReport(ISiteviewApi api, String type,
			Map<String, OrganizationCode> codemap, long starttime, long endtime) {

		List<ServerDetail> deps = new ArrayList<ServerDetail>();
		try {
			ElasticSearchIndexerImpl indexer = (ElasticSearchIndexerImpl) ElasticSearchIndexerImpl
					.getiElasticSearchIndexerImpl();
			//父区域聚合
			TermsBuilder parentBuilder = AggregationBuilders.terms("parentareacodes").field("parentareacode").size(1000);
			//服务器类型聚合
			TermsBuilder serverTypeBuilder = AggregationBuilders.terms("servertypes").field("servertype");
			parentBuilder.subAggregation(serverTypeBuilder).size(1000);
			//两个字段聚合后再去计算
			serverTypeBuilder.subAggregation(AggregationBuilders.sum("sum_total").field("total"))
				.subAggregation(AggregationBuilders.sum("sum_onlinecount").field("onlinecount"))
				.subAggregation(AggregationBuilders.sum("sum_intactcount").field("intactcount"))
				.subAggregation(AggregationBuilders.sum("sum_unavailablecount").field("unavailablecount"))
				.size(1000);
			
			DateRangeBuilder dateRangeBuilder = AggregationBuilders.dateRange("agg_date").field("date").addRange(starttime, endtime);
			dateRangeBuilder.subAggregation(parentBuilder);
			
			SearchResponse response = indexer.getClient().prepareSearch("yftitoss").setTypes(type)
					.setPostFilter(QueryBuilders.rangeQuery("date").from(starttime).to(endtime))
					.addAggregation(dateRangeBuilder).setFrom(0).setSize(1000).setExplain(true).execute().actionGet();		
			Map<String, Aggregation> aggMap = response.getAggregations().asMap();
			InternalDateRange range = (InternalDateRange) aggMap.get("agg_date");
			org.elasticsearch.search.aggregations.bucket.range.date.InternalDateRange.Bucket bu = (org.elasticsearch.search.aggregations.bucket.range.date.InternalDateRange.Bucket) ((List) range
					.getBuckets()).get(0);
			List list = bu.getAggregations().asList();
			for (int i = 0; i < list.size(); i++) {
				StringTerms strterms = (StringTerms) list.get(i);
				List<Bucket> buckets = strterms.getBuckets();
				for (Bucket buck : buckets) {
					try {
//						ServerDetail dep = new ServerDetail();
						Map<String, Aggregation> map = buck.getAggregations().asMap();
						String areacode = buck.getKeyAsString();
						//-----
						StringTerms typesTerm = (StringTerms) map.get("servertypes");
						List<Bucket> buckets2 = typesTerm.getBuckets();
						for(Bucket typeBuck : buckets2){
							Map<String, Aggregation> typeMap = typeBuck.getAggregations().asMap();
							if (codemap.get(areacode) != null) {
								ServerDetail dep = new ServerDetail();
								dep.setArea(codemap.get(areacode).getO_name());
								dep.setAreacode(areacode);
								String parentCode = codemap.get(areacode).getO_parent_code();
								if (parentCode == null)
									parentCode = "";
								dep.setParentareacode(parentCode);
								if (parentCode.length() > 0)
									dep.setParentarea(codemap.get(dep.getParentareacode()).getO_name());
								else
									dep.setParentarea("");

//								InternalAvg inavg = (InternalAvg) typeMap.get("avg_onlinerate");
//								inavg = (InternalAvg) typeMap.get("avg_intactrate");
								InternalSum max = (InternalSum) typeMap.get("sum_total");
								dep.setTotal((long) max.getValue());
								max = (InternalSum) typeMap.get("sum_onlinecount");
								dep.setOnlinecount((long) max.getValue());
								max = (InternalSum) typeMap.get("sum_intactcount");
								dep.setIntactcount((long) max.getValue());
								max = (InternalSum) typeMap.get("sum_unavailablecount");
								dep.setUnavailablecount((long) max.getValue());
								dep.setServertype(typeBuck.getKeyAsString());
								dep.setDate(new Date(endtime));
								dep.setOnlinerate(0.0);
								dep.setIntactrate(0.0);
								if(dep.getOnlinecount()>0&&dep.getTotal()>0)
									dep.setOnlinerate(Double.parseDouble(df.format(Double.parseDouble(dep.getOnlinecount()+"")/dep.getTotal())));
								if(dep.getIntactcount()>0&&dep.getTotal()>0)
									dep.setIntactrate(Double.parseDouble(df.format(Double.parseDouble(dep.getIntactcount()+"")/dep.getTotal())));
								deps.add(dep);
							}
						}
//						InternalTopHits areacodehit = (InternalTopHits) map.get("parentareacode");
//						Map<String, Object> hashmap = areacodehit.getHits().getAt(0).getSource();
//						String areacode = hashmap.get("parentareacode").toString();
//						dep.setArea(codemap.get(areacode).getO_name());
//						dep.setAreacode(areacode);
//						String parentCode = codemap.get(areacode).getO_parent_code();
//						if (parentCode == null)
//							parentCode = "";
//						dep.setParentareacode(parentCode);
//						if (parentCode.length() > 0)
//							dep.setParentarea(codemap.get(dep.getParentareacode()).getO_name());
//						else
//							dep.setParentarea("");
//						String serverType = hashmap.get("servertype").toString();
//						InternalAvg inavg = (InternalAvg) map.get("avg_onlinerate");
//						dep.setOnlinerate(Double.parseDouble(df.format(inavg.getValue())));
//						inavg = (InternalAvg) map.get("avg_intactrate");
//						dep.setIntactrate(Double.parseDouble(df.format(inavg.getValue())));
//						InternalSum max = (InternalSum) map.get("sum_total");
//						dep.setTotal((long) max.getValue());
//						max = (InternalSum) map.get("sum_onlinecount");
//						dep.setOnlinecount((long) max.getValue());
//						max = (InternalSum) map.get("sum_intactcount");
//						dep.setIntactcount((long) max.getValue());
//						max = (InternalSum) map.get("sum_unavailablecount");
//						dep.setUnavailablecount((long) max.getValue());
//						dep.setServertype(serverType);
//						dep.setDate(new Date(endtime));
//						deps.add(dep);
					} catch (Exception e) {
						continue;
					}
				}
			}
		} catch (Exception e) {
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(endtime));
		calendar.add(Calendar.HOUR_OF_DAY, -2);
		ServerUtils.setData("yftitoss", type, deps, calendar.getTime());
		return deps;
	}
}