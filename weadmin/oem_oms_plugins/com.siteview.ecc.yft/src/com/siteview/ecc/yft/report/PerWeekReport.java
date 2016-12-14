package com.siteview.ecc.yft.report;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.range.date.InternalDateRange;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg;
import org.elasticsearch.search.aggregations.metrics.max.InternalMax;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.elasticsearch.search.aggregations.metrics.tophits.InternalTopHits;

import Siteview.Api.ISiteviewApi;

import com.siteview.alarm.bean.DepartmentalReports;
import com.siteview.ecc.elasticsearchutil.ElasticSearchIndexerImpl;
import com.siteview.ecc.yft.bean.LoseVideorecord;
import com.siteview.ecc.yft.bean.ServerDetail;

public class PerWeekReport {
	public static DepartmentalReports VideoAssessmentWeekly(ISiteviewApi api,Long starttime,Long endtime,String type){
		String area=InitReport.map.get("the_system_name");
		String areacode=InitReport.map.get("the_system_code");
		DepartmentalReports dep=new DepartmentalReports();
		try{
			ElasticSearchIndexerImpl indexer=(ElasticSearchIndexerImpl) ElasticSearchIndexerImpl.getiElasticSearchIndexerImpl();
			SearchResponse response = indexer.getClient().prepareSearch("yftitoss")
					  .setTypes(type)
					  .setPostFilter(QueryBuilders.rangeQuery("date")
					  .from(starttime)
					  .to(endtime))   // 指定值的范围
					  .addAggregation(AggregationBuilders.dateRange("agg_date").field("date")
					  .addRange(starttime, endtime)
					  .subAggregation(AggregationBuilders.max("max_total").field("total"))
					  .subAggregation(AggregationBuilders.avg("avg_onlinecount").field("onlinecount"))
					  .subAggregation(AggregationBuilders.avg("avg_onlinerate").field("onlinerate"))
					  .subAggregation(AggregationBuilders.sum("sum_videolossduration").field("videolossduration"))
					  .subAggregation(AggregationBuilders.avg("avg_anomalyrate").field("anomalyrate"))
					  .subAggregation(AggregationBuilders.avg("avg_anomalycount").field("anomalycount"))
					  .subAggregation(AggregationBuilders.avg("avg_videolossrate").field("videolossrate"))
					  .subAggregation(AggregationBuilders.avg("avg_videolosscount").field("videolosscount"))
					  .subAggregation(AggregationBuilders.avg("avg_intactrate").field("intactrate"))
					  .subAggregation(AggregationBuilders.avg("avg_intactcount").field("intactcount")))
			          .setFrom(0).setSize(1000).setExplain(true)
					  .execute()
			          .actionGet();
			Map<String, Aggregation> aggMap = response.getAggregations().asMap();
			InternalDateRange range=(InternalDateRange) aggMap.get("agg_date");
			org.elasticsearch.search.aggregations.bucket.range.date.InternalDateRange.Bucket
			bu=(org.elasticsearch.search.aggregations.bucket.range.date.InternalDateRange.Bucket) ((List)range.getBuckets()).get(0);
			List list=bu.getAggregations().asList();
			for(int i=0;i<list.size();i++){
				Object obj=list.get(i);
				if(obj instanceof InternalAvg){
					InternalAvg inavg=(InternalAvg) obj;
					if(inavg.getName().equals("avg_onlinecount")){
						dep.setOnlinecount((int)inavg.getValue());
					}else if(inavg.getName().equals("avg_onlinerate")){
						if(Double.isNaN(inavg.getValue()) || Double.isInfinite(inavg.getValue()))
							dep.setIntactrate(0.0);
						else
							dep.setOnlinerate(Double.parseDouble(PerDayReport.df.format(inavg.getValue())));
					}else if(inavg.getName().equals("avg_anomalyrate")){
						if(Double.isNaN(inavg.getValue()) || Double.isInfinite(inavg.getValue()))
							dep.setIntactrate(0.0);
						else
							dep.setAnomalyrate(Double.parseDouble(PerDayReport.df.format(inavg.getValue())));
					}else if(inavg.getName().equals("avg_anomalycount")){
						dep.setAnomalycount((int)inavg.getValue());
					}else if(inavg.getName().equals("avg_videolossrate")){
						if(Double.isNaN(inavg.getValue()) || Double.isInfinite(inavg.getValue()))
							dep.setVideolossrate(0.0);
						else
							dep.setVideolossrate(Double.parseDouble(PerDayReport.df.format(inavg.getValue())));
					}else if(inavg.getName().equals("avg_videolosscount")){
						dep.setVideolosscount((int)inavg.getValue());
					}else if(inavg.getName().equals("avg_intactrate")){
						if(Double.isNaN(inavg.getValue()) || Double.isInfinite(inavg.getValue()))
							dep.setIntactrate(0.0);
						else
							dep.setIntactrate(Double.parseDouble(PerDayReport.df.format(inavg.getValue())));
					}else if(inavg.getName().equals("avg_intactcount")){
						dep.setIntactcount((int)inavg.getValue());
					}
				}else if(obj instanceof InternalMax){
					InternalMax max=(InternalMax) obj;
					dep.setTotal((int)max.getValue());
				}else if(obj instanceof InternalSum){
					InternalSum sum=(InternalSum) obj;
					dep.setVideolossduration(sum.getValue());
				}
			}
		}catch(Exception ex){}
		dep.setArea(area);
		dep.setAreacode(areacode);
		dep.setDate(new Date(endtime));
		return dep;
	}
	public static DepartmentalReports StorageAssessmentWeekly(ISiteviewApi api,Long starttime,Long endtime,String type){
		String area=InitReport.map.get("the_system_name");
		String areacode=InitReport.map.get("the_system_code");
		DepartmentalReports dep=new DepartmentalReports();
		try{
			ElasticSearchIndexerImpl indexer=(ElasticSearchIndexerImpl) ElasticSearchIndexerImpl.getiElasticSearchIndexerImpl();
			SearchResponse response = indexer.getClient().prepareSearch("yftitoss")
					  .setTypes(type)
					  .setPostFilter(QueryBuilders.rangeQuery("date")
					  .from(starttime)
					  .to(endtime))   // 指定值的范围
					  .addAggregation(AggregationBuilders.dateRange("agg_date").field("date")
					  .addRange(starttime, endtime)
					  .subAggregation(AggregationBuilders.max("max_total").field("total"))
					  .subAggregation(AggregationBuilders.avg("avg_onlinecount").field("onlinecount"))
					  .subAggregation(AggregationBuilders.avg("avg_onlinerate").field("onlinerate"))
					  .subAggregation(AggregationBuilders.avg("avg_notlong").field("notlong"))
					  .subAggregation(AggregationBuilders.avg("avg_intactrate").field("intactrate"))
					  .subAggregation(AggregationBuilders.avg("avg_intactcount").field("intactcount")))
			          .setFrom(0).setSize(1000).setExplain(true)
					  .execute()
			          .actionGet();
			Map<String, Aggregation> aggMap = response.getAggregations().asMap();
			InternalDateRange range=(InternalDateRange) aggMap.get("agg_date");
			org.elasticsearch.search.aggregations.bucket.range.date.InternalDateRange.Bucket
			bu=(org.elasticsearch.search.aggregations.bucket.range.date.InternalDateRange.Bucket) ((List)range.getBuckets()).get(0);
			List list=bu.getAggregations().asList();
			for(int i=0;i<list.size();i++){
				Object obj=list.get(i);
				if(obj instanceof InternalAvg){
					InternalAvg inavg=(InternalAvg) obj;
					if(inavg.getName().equals("avg_onlinecount")){
						dep.setOnlinecount((int)inavg.getValue());
					}else if(inavg.getName().equals("avg_onlinerate")){
						if(Double.isNaN(inavg.getValue()) || Double.isInfinite(inavg.getValue()))
							dep.setIntactrate(0.0);
						else
							dep.setOnlinerate(Double.parseDouble(PerDayReport.df.format(inavg.getValue())));
					}else if(inavg.getName().equals("avg_intactrate")){
						if(Double.isNaN(inavg.getValue()) || Double.isInfinite(inavg.getValue()))
							dep.setIntactrate(0.0);
						else
							dep.setIntactrate(Double.parseDouble(PerDayReport.df.format(inavg.getValue())));
					}else if(inavg.getName().equals("avg_intactcount")){
						dep.setIntactcount((int)inavg.getValue());
					}else if(inavg.getName().equals("avg_notlong")){
						dep.setNotlong((long)inavg.getValue());
					}
				}else if(obj instanceof InternalMax){
					InternalMax max=(InternalMax) obj;
					dep.setTotal((int)max.getValue());
				}
			}
		}catch(Exception ex){}
		dep.setArea(area);
		dep.setAreacode(areacode);
		dep.setDate(new Date(endtime));
		return dep;
	}
	public static List<LoseVideorecord> VideoLoseVideorecord(ISiteviewApi api,Long starttime,Long endtime,String type){
		List<LoseVideorecord> loses=new ArrayList<LoseVideorecord>();
		try{
			ElasticSearchIndexerImpl indexer=(ElasticSearchIndexerImpl) ElasticSearchIndexerImpl.getiElasticSearchIndexerImpl();
			SearchResponse response = indexer.getClient().prepareSearch("yftitoss")
					  .setTypes(type)
					  .setPostFilter(QueryBuilders.rangeQuery("date")
					  .from(starttime)
					  .to(endtime))   // 指定值的范围
					  .addAggregation(AggregationBuilders.dateRange("agg_date").field("date")
							  .addRange(starttime, endtime)
					  .subAggregation( AggregationBuilders.terms("videoflags").field("videoflag")
					  .subAggregation(AggregationBuilders.topHits("videoflag"))
					  .subAggregation(AggregationBuilders.sum("losttimes").field("losttimes"))
					  .subAggregation(AggregationBuilders.sum("longoffline").field("longoffline"))))
					  .setFrom(0).setSize(1000).setExplain(true)
					  .execute()
			          .actionGet();
			Map<String, Aggregation> aggMap = response.getAggregations().asMap();
			InternalDateRange range=(InternalDateRange) aggMap.get("agg_date");
			org.elasticsearch.search.aggregations.bucket.range.date.InternalDateRange.Bucket
			bu=(org.elasticsearch.search.aggregations.bucket.range.date.InternalDateRange.Bucket) ((List)range.getBuckets()).get(0);
			List list=bu.getAggregations().asList();
			for(int i=0;i<list.size();i++){
				StringTerms strterms=(StringTerms) list.get(i);
				List<Bucket> buckets=strterms.getBuckets();
				for(Bucket buck:buckets){
					LoseVideorecord lose=new LoseVideorecord();
					Map map=buck.getAggregations().asMap();
					InternalSum losttimes=(InternalSum) map.get("losttimes");
					InternalSum longoffline=(InternalSum) map.get("longoffline");
					InternalTopHits tophitflag=(InternalTopHits) map.get("videoflag");
					Map<String,Object> hashmap=tophitflag.getHits().getAt(0).getSource();
					lose.setIpaddress(hashmap.get("ipaddress").toString());
					lose.setIplace(hashmap.get("installationaddress").toString());
					lose.setVideoflag(hashmap.get("videoflag").toString());
					lose.setVideoname(hashmap.get("videoname").toString());
					lose.setLongtime((long)longoffline.getValue());
					lose.setLosttimes((long)losttimes.getValue());
					lose.setTime("");
					loses.add(lose);
				}
			}
		}catch(Exception ex){
			
		}
		return loses;
	}
	
	/**
	 * 获取非视频类设备周报数据
	 * @param api
	 * @param starttime
	 * @param endtime
	 * @param type
	 * @param eqTypeEn
	 * @return
	 */
	public static List<ServerDetail> getServerDetailsWeekly(ISiteviewApi api, Long starttime,
			Long endtime, String type) {
		List<ServerDetail> servers = new ArrayList<ServerDetail>();
		servers.addAll(ServerUtils.getServerDetailsFromES(api, starttime, endtime, type, "server"));
		servers.addAll(ServerUtils.getServerDetailsFromES(api, starttime, endtime, type, "network"));
		servers.addAll(ServerUtils.getServerDetailsFromES(api, starttime, endtime, type, "firewall"));
		servers.addAll(ServerUtils.getServerDetailsFromES(api, starttime, endtime, type, "database"));
		return servers;
	}

	public static List<ServerDetail> getServerDetailsWeekly(ISiteviewApi api, Long starttime,
			Long endtime, String type,String eqTypeEn) {
		return ServerUtils.getServerDetailsFromES(api, starttime, endtime, type, eqTypeEn);
	}
	
	public static void main(String[] args) {
		SimpleDateFormat simp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			VideoLoseVideorecord(null, simp.parse("2016-01-18 00:00:00").getTime(), simp.parse("2016-01-19 23:59:59").getTime(), "videoOfflineDetailsDaily");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
