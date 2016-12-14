package com.siteview.ecc.yft.odata;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import siteview.IFunctionExtension;
import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.SiteviewException;
import Siteview.Api.ISiteviewApi;

import com.siteview.alarm.bean.DepartmentalReports;
import com.siteview.ecc.elasticsearchutil.ElasticSearchIndexerImpl;
import com.siteview.ecc.yft.bean.JsonValueProcessorImplTest;
import com.siteview.ecc.yft.report.InitReport;
import com.siteview.utils.date.DateUtils;
import com.siteview.utils.db.DBQueryUtils;
/*
 * 存储设备报表
 */
public class StorageAssessmentReport implements IFunctionExtension {

	public StorageAssessmentReport() {
		// TODO Auto-generated constructor stub
	}
	SimpleDateFormat simp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api,
			Map<String, String> inputParamMap) {
		String type=inputParamMap.get("REPORT_TYPE");
		Date startdate=null;
		Date enddate=null;
		boolean b=false;
		String starttime=inputParamMap.get("STARTTIME");
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.registerJsonValueProcessor(java.util.Date.class, new JsonValueProcessorImplTest());
		if(type==null||starttime==null)
			return null;
		if(starttime!=null){
			try {
				startdate=simp.parse(starttime+" 00:00:00.000");
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		String endtime=inputParamMap.get("ENDTIME");
		if(endtime==null){
			endtime=starttime;
			try {
				enddate=simp.parse(endtime+" 23:59:59.999");
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else{
			b=true;
			try {
				enddate=simp.parse(endtime+" 23:59:59.999");
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if (!b) {
			try {
				if (type.endsWith("Weekly")) {
					startdate = DateUtils.parseDefaultDate(DateUtils.getWeekStartTime(startdate));
					enddate = DateUtils.parseDefaultDate(DateUtils.getWeekEndTime(startdate));
				} else if (type.endsWith("Monthly")) {
					startdate = DateUtils.parseDefaultDate(DateUtils.getMonthStartTime(startdate));
					enddate = DateUtils.parseDefaultDate(DateUtils.getMonthEndTime(startdate));
				} else if (type.endsWith("Yearly")) {
					startdate = DateUtils.parseDefaultDate(DateUtils.getYearStartTime(startdate));
					enddate = DateUtils.parseDefaultDate(DateUtils.getYearEndTime(startdate));
				} else if (type.endsWith("Quarterly")) {
					startdate = DateUtils.parseDefaultDate(DateUtils.getQuarterStartTime(startdate));
					enddate = DateUtils.parseDefaultDate(DateUtils.getQuarterEndTime(startdate));
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		String paretareacode="";
		if(InitReport.map.get("the_parent_domain")==null){
			try {
				String groupid=api.get_AuthenticationService().get_CurrentSecurityGroupId();
				String groupname=api.get_AuthenticationService().get_CurrentSecurityGroup();
				String sql="";
				if(!groupname.equalsIgnoreCase("administrators"))
					 sql="select o.o_code from Organization o,OrganizeAndSafeGroupRel og where o.RecId=og.organize_id AND og.safegroup_id='%s'";
				else
					sql="select o_code from Organization where parentId=''";
				DataTable dt=DBQueryUtils.Select(String.format(sql, groupid), api);
				for(DataRow dr:dt.get_Rows()){
					paretareacode=dr.get("o_code")==null?"":dr.get("o_code").toString();
				}
			} catch (SiteviewException e) {
				e.printStackTrace();
			}
		}
		List<Map<String,String>> outParam=new ArrayList<Map<String,String>>();
		Map<String,String> outmap=new HashMap<String,String>();
		if(paretareacode==null||paretareacode.length()==0){
			Map<String,DepartmentalReports> map=new HashMap<String,DepartmentalReports>();
			List<DepartmentalReports> list=getDaily(type, startdate.getTime(), enddate.getTime(),"",map);
			outmap.put("REPORTDATA", JSONArray.fromObject(list,jsonConfig).toString());
		}else{
			if(!b){
				Map<String,DepartmentalReports> map=new HashMap<String,DepartmentalReports>();
				List<DepartmentalReports> list=getDaily(type, startdate.getTime(), enddate.getTime(),"",map);
				outmap.put("REPORTDATA", JSONArray.fromObject(VideoAssessmentReport.getCodes(api, map, paretareacode),jsonConfig).toString());
			}else{
				Map<String,List<DepartmentalReports>> map=new HashMap<String,List<DepartmentalReports>>();
				Map<String,List<Map>> map_code=new HashMap<String,List<Map>>();
				List<List<Map>> list0=new ArrayList<List<Map>>();
				getCustomizeDaily(type, startdate.getTime(),enddate.getTime(),"",api,map,map_code);
				outmap.put("REPORTDATA", JSONArray.fromObject(VideoAssessmentReport.getCustomizeCodes(api, map, paretareacode,map_code,list0),jsonConfig).toString());
				outmap.put("BROKENLINE",JSONArray.fromObject( list0,jsonConfig).toString());
			}
		}outParam.add(outmap);
		return outParam;
	}
	static DecimalFormat df = new DecimalFormat("#0.00");
	public List<DepartmentalReports> getDaily(String type,long starttime,long endtime,String paretareacode,Map map){
		BoolQueryBuilder qb = QueryBuilders.boolQuery();
		qb.must(QueryBuilders.matchQuery("parentareacode", paretareacode));
		List<DepartmentalReports> deps=new ArrayList<DepartmentalReports>();
		ElasticSearchIndexerImpl indexer=(ElasticSearchIndexerImpl) ElasticSearchIndexerImpl.getiElasticSearchIndexerImpl();
		SearchResponse response=null;
		if(paretareacode.length()>0){
			response = indexer.getClient().prepareSearch("yftitoss")
				  .setTypes(type)
				  .setQuery(qb)
				  .setPostFilter(QueryBuilders.rangeQuery("date")
				  .from(starttime)
				  .to(endtime))
				  .addSort(SortBuilders.fieldSort("date").unmappedType("string").order(SortOrder.ASC))
				  .setFrom(0).setSize(1000).setExplain(true)
				  .execute()
		          .actionGet();
		}else{
			response = indexer.getClient().prepareSearch("yftitoss")
					  .setTypes(type)
					  .setPostFilter(QueryBuilders.rangeQuery("date")
					  .from(starttime)
					  .to(endtime))
					  .addSort(SortBuilders.fieldSort("date").unmappedType("string").order(SortOrder.ASC))
					  .setFrom(0).setSize(1000).setExplain(true)
					  .execute()
			          .actionGet();
		}
		SearchHit[] hits = response.getHits().getHits();
		for (int i = 0; i < hits.length; i++) {
			DepartmentalReports dep=new DepartmentalReports();
			SearchHit hit = hits[i];
			Map result = hit.getSource();
			dep.setTotal(result.get("total")==null?0:
				Integer.parseInt(result.get("total").toString()));
			dep.setOnlinecount(result.get("onlinecount")==null?0:
					Integer.parseInt(result.get("onlinecount").toString()));
	        dep.setOnlinerate(result.get("onlinerate")==null?0:
					Double.parseDouble(result.get("onlinerate").toString()));
			dep.setIntactcount(result.get("intactcount")==null?0:
				Integer.parseInt(result.get("intactcount").toString()));
			dep.setIntactrate(result.get("intactrate")==null?0:
				Double.parseDouble(result.get("intactrate").toString()));
			dep.setNotlong(result.get("notlong")==null?0:
				Long.parseLong(result.get("notlong").toString()));
           String logtime=result.get("date").toString();
		   dep.setDate(VideoAssessmentReport.getTime(logtime));
		   dep.setArea(result.get("area")==null?"":result.get("area").toString());
		   dep.setAreacode(result.get("areacode")==null?"":result.get("areacode").toString());
		   dep.setAnomalyrate(Double.parseDouble(df.format(dep.getAnomalyrate()*100)));
           dep.setIntactrate(Double.parseDouble(df.format(dep.getIntactrate()*100)));
           dep.setOnlinerate(Double.parseDouble(df.format(dep.getOnlinerate()*100)));
           dep.setVideolossduration(Double.parseDouble(df.format(dep.getVideolossrate()*100)));
		   deps.add(dep);
		   map.put(dep.getAreacode(), dep);
		}
		return deps;
	}
	public List<DepartmentalReports> getCustomizeDaily(String type,long starttime,long endtime,
			String paretareacode,ISiteviewApi api,Map<String,List<DepartmentalReports>> map,Map<String,List<Map>> map_code){
		BoolQueryBuilder qb = QueryBuilders.boolQuery();
		qb.must(QueryBuilders.matchQuery("parentareacode", paretareacode));
		List<DepartmentalReports> deps=new ArrayList<DepartmentalReports>();
		ElasticSearchIndexerImpl indexer=(ElasticSearchIndexerImpl) ElasticSearchIndexerImpl.getiElasticSearchIndexerImpl();
		SearchResponse response=null;
		if(paretareacode.length()>0){
			response = indexer.getClient().prepareSearch("yftitoss")
				  .setTypes(type)
				  .setQuery(qb)
				  .setPostFilter(QueryBuilders.rangeQuery("date")
				  .from(starttime)
				  .to(endtime))
				  .setFrom(0).setSize(1000).setExplain(true)
				  .addSort(SortBuilders.fieldSort("date").unmappedType("string").order(SortOrder.ASC))
				  .execute()
		          .actionGet();
		}else{
			response = indexer.getClient().prepareSearch("yftitoss")
					  .setTypes(type)
					  .setPostFilter(QueryBuilders.rangeQuery("date")
					  .from(starttime)
					  .to(endtime))
					  .setFrom(0).setSize(1000).setExplain(true)
					  .addSort(SortBuilders.fieldSort("date").unmappedType("string").order(SortOrder.ASC))
					  .execute()
			          .actionGet();
		}
		SearchHit[] hits = response.getHits().getHits();
		for (int i = 0; i < hits.length; i++) {
			DepartmentalReports dep=new DepartmentalReports();
			SearchHit hit = hits[i];
			Map result = hit.getSource();
			dep.setTotal(result.get("total")==null?0:
				Integer.parseInt(result.get("total").toString()));
			dep.setOnlinecount(result.get("onlinecount")==null?0:
					Integer.parseInt(result.get("onlinecount").toString()));
	        dep.setOnlinerate(result.get("onlinerate")==null?0:
					Double.parseDouble(result.get("onlinerate").toString()));
			dep.setIntactcount(result.get("intactcount")==null?0:
				Integer.parseInt(result.get("intactcount").toString()));
			dep.setIntactrate(result.get("intactrate")==null?0:
				Double.parseDouble(result.get("intactrate").toString()));
			dep.setNotlong(result.get("notlong")==null?0:
				Long.parseLong(result.get("notlong").toString()));
           String logtime=result.get("date").toString();
		   dep.setDate(VideoAssessmentReport.getTime(logtime));
		   dep.setArea(result.get("area")==null?"":result.get("area").toString());
		   dep.setAreacode(result.get("areacode")==null?"":result.get("areacode").toString());
		   dep.setAnomalyrate(Double.parseDouble(df.format(dep.getAnomalyrate()*100)));
           dep.setIntactrate(Double.parseDouble(df.format(dep.getIntactrate()*100)));
           dep.setOnlinerate(Double.parseDouble(df.format(dep.getOnlinerate()*100)));
           dep.setVideolossduration(Double.parseDouble(df.format(dep.getVideolossrate()*100)));
		   deps.add(dep);
		     List<DepartmentalReports> list=map.get(dep.getAreacode());
	           List<Map> list1=map_code.get(dep.getAreacode());
	           if(list1==null)
	        	   list1=new ArrayList<Map>();
	           if(list==null)
	        	   list=new ArrayList<DepartmentalReports>();
	           Map<String,Object> map1=new HashMap<String, Object>();
	           map1.put("date", dep.getDate());
	           map1.put("onlinerate", dep.getOnlinerate());
	           list1.add(map1);
	           map_code.put(dep.getAreacode(), list1);
	           list.add(dep);
	           map.put(dep.getAreacode(), list);
		}
		Iterator<String> ite=map_code.keySet().iterator();
		while(ite.hasNext()){
			List<Map> listm=map_code.get(ite.next());
			Collections.sort(listm, new Comparator<Map>() {
				@Override
				public int compare(Map o1, Map o2) {
					Date d1=(Date) o1.get("date");
					Date d2=(Date) o2.get("date");
					return (int) (d2.getTime()-d1.getTime());
				}
			});
		}
		return deps;
	}
}
