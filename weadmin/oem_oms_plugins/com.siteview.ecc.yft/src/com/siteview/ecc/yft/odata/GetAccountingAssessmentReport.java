package com.siteview.ecc.yft.odata;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.range.date.InternalDateRange;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.elasticsearch.search.aggregations.metrics.tophits.InternalTopHits;

import siteview.IFunctionExtension;
import Siteview.Api.ISiteviewApi;

import com.siteview.ecc.elasticsearchutil.ElasticSearchIndexerImpl;
import com.siteview.ecc.yft.bean.AccountingAssessment;
import com.siteview.ecc.yft.bean.JsonValueProcessorImplTest;
import com.siteview.utils.date.DateUtils;
import com.siteview.utils.html.StringUtils;

public class GetAccountingAssessmentReport implements IFunctionExtension {

	public GetAccountingAssessmentReport() {
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api, Map<String, String> inputParamMap) {
		String type = inputParamMap.get("REPORT_TYPE"); // 默认是月报
		Date startdate = null;
		Date enddate = null;
		String starttime = inputParamMap.get("STARTTIME");
		String endtime = inputParamMap.get("ENDTIME");
		List<Map<String, String>> outParam = new ArrayList<Map<String, String>>();
		try {
			if (starttime != null) {
				starttime = starttime + " 00:00:00";
				startdate = DateUtils.parseDefaultDate(DateUtils.getMonthStartTime(DateUtils
						.parseDefaultDate(starttime)));
				if (endtime == null) {
					endtime = starttime;
				}
				enddate = DateUtils.parseDefaultDate(DateUtils.getMonthEndTime(startdate));

				List<AccountingAssessment> list = null;
				if (type == null || "".equals(type) || "accountingassessmentMonthly".equals(type)) { // 月报
				}  else if ("accountingassessmentQuarterly".equals(type)) {
					startdate = DateUtils.parseDefaultDate(DateUtils.getQuarterStartTime(DateUtils
							.parseDefaultDate(starttime)));
					enddate = DateUtils.parseDefaultDate(DateUtils.getQuarterEndTime(startdate));
				}else if ("accountingassessmentYearly".equals(type)) {
					startdate = DateUtils.parseDefaultDate(DateUtils.getYearStartTime(DateUtils
							.parseDefaultDate(starttime)));
					enddate = DateUtils.parseDefaultDate(DateUtils.getYearEndTime(startdate));
				}
				// 都从月报里面取值
				list = getAccAsses("accountingassessmentMonthly", startdate.getTime(), enddate.getTime());
				Map<String, String> outmap = new HashMap<String, String>();
				JsonConfig jsonConfig = new JsonConfig();
				jsonConfig.registerJsonValueProcessor(java.util.Date.class,
						new JsonValueProcessorImplTest());
				outmap.put("REPORTDATA", JSONArray.fromObject(list,jsonConfig).toString());
				List<AccountingAssessment> chartList = new ArrayList<AccountingAssessment>();//getAccAssesChart("accountingassessmentMonthly", startdate.getTime(), enddate.getTime());
				Map<String,AccountingAssessment> map = new HashMap<String,AccountingAssessment>();
				for(AccountingAssessment assessment : list){
					String isp = assessment.getIsp();
					AccountingAssessment acc = null;
					
					if(map.get(isp) == null)
						acc = new AccountingAssessment();
					else
						acc = map.get(isp);
					acc.setCostdeduction(acc.getCostdeduction() + assessment.getCostdeduction());
					acc.setIsp(isp);
					map.put(isp, acc);
				}
				chartList.addAll(map.values());
				outmap.put("REPORTCHARTDATA", JSONArray.fromObject(chartList).toString());
				outParam.add(outmap);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return outParam;
	}

	private List<AccountingAssessment> getAccAssesChart(String type, long starttime, long endtime) {
		ElasticSearchIndexerImpl indexer = (ElasticSearchIndexerImpl) ElasticSearchIndexerImpl
				.getiElasticSearchIndexerImpl();
		SearchResponse response = indexer
				.getClient()
				.prepareSearch("yftitoss")
				.setTypes(type)
				.setPostFilter(QueryBuilders.rangeQuery("date").from(starttime).to(endtime))
				.setFrom(0)
				.addAggregation(
						AggregationBuilders
								.dateRange("agg_date")
								.field("date")
								.addRange(starttime, endtime)
								.subAggregation(
										AggregationBuilders
												.terms("isps")
												.field("isp")
												.subAggregation(AggregationBuilders.topHits("isp"))
												.subAggregation(
														AggregationBuilders.sum("sum_costdeduction").field("costdeduction")).size(1000)))
				.setFrom(0).setSize(1000).setExplain(true).execute().actionGet();
		Map<String, Aggregation> aggMap = response.getAggregations().asMap();
		InternalDateRange range = (InternalDateRange) aggMap.get("agg_date");
		org.elasticsearch.search.aggregations.bucket.range.date.InternalDateRange.Bucket bu = (org.elasticsearch.search.aggregations.bucket.range.date.InternalDateRange.Bucket) ((List) range
				.getBuckets()).get(0);
		List list = bu.getAggregations().asList();
		List<AccountingAssessment> ass = new ArrayList<AccountingAssessment>();
		for (int i = 0; i < list.size(); i++) {
			StringTerms strterms = (StringTerms) list.get(i);
			List<Bucket> buckets = strterms.getBuckets();
			for (Bucket buck : buckets) {
				AccountingAssessment assessment = new AccountingAssessment();
				Map map = buck.getAggregations().asMap();
				InternalTopHits areacodehit = (InternalTopHits) map.get("isp");
				Map<String, Object> hashmap = areacodehit.getHits().getAt(0).getSource();
				String isp = hashmap.get("isp").toString();
				assessment.setIsp(isp);
				InternalSum max = (InternalSum) map.get("sum_costdeduction");
				assessment.setCostdeduction(max.getValue());
				ass.add(assessment);
			}
		}
		return ass;
	}

	public List<AccountingAssessment> getAccAsses(String type, long starttime, long endtime) {
		List<AccountingAssessment> list = new ArrayList<AccountingAssessment>();
		ElasticSearchIndexerImpl indexer = (ElasticSearchIndexerImpl) ElasticSearchIndexerImpl
				.getiElasticSearchIndexerImpl();
		SearchResponse response = indexer.getClient().prepareSearch("yftitoss").setTypes(type)
				.setPostFilter(QueryBuilders.rangeQuery("date").from(starttime).to(endtime)).setFrom(0)
				.setSize(10000).setExplain(true).execute().actionGet();
		SearchHit[] hits = response.getHits().getHits();
		for (int i = 0; i < hits.length; i++) {
			AccountingAssessment assessment = new AccountingAssessment();
			SearchHit hit = hits[i];
			Map result = hit.getSource();
			assessment.setCreatedate(StringUtils.getNotNullStr(result.get("createdate")));
			assessment.setOrganization(StringUtils.getNotNullStr(result.get("organization")));
			assessment.setWorkordernum(StringUtils.getNotNullStr(result.get("workordernum")));
			assessment.setLongdelay(result.get("longdelay") == null ? 0 : Double.parseDouble(result.get(
					"longdelay").toString()));
			assessment.setCostdeduction(result.get("costdeduction") == null ? 0 : Double
					.parseDouble(result.get("costdeduction").toString()));
			assessment.setIsp(StringUtils.getNotNullStr(result.get("isp")));
			String logtime = result.get("date").toString();
			assessment.setDate(DateUtils.getESTime(logtime));
			list.add(assessment);
		}
		return list;
	}
}
