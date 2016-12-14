package com.siteview.ecc.yft.odata;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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
 * 摄像机报表数据
 */
public class VideoAssessmentReport implements IFunctionExtension {
	public VideoAssessmentReport() {
	}
	SimpleDateFormat simp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api,
			Map<String, String> inputParamMap) {
		String type=inputParamMap.get("REPORT_TYPE");
		Date startdate=null;
		Date enddate=null;
		boolean b=false;
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.registerJsonValueProcessor(java.util.Date.class, new JsonValueProcessorImplTest());
		String starttime=inputParamMap.get("STARTTIME");
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
				if ("videoWeekly".equals(type)) {
					startdate = DateUtils.parseDefaultDate(DateUtils.getWeekStartTime(startdate));
					enddate = DateUtils.parseDefaultDate(DateUtils.getWeekEndTime(startdate));
				} else if ("videoMonthly".equals(type)) {
					startdate = DateUtils.parseDefaultDate(DateUtils.getMonthStartTime(startdate));
					enddate = DateUtils.parseDefaultDate(DateUtils.getMonthEndTime(startdate));
				} else if ("videoYearly".equals(type)) {
					startdate = DateUtils.parseDefaultDate(DateUtils.getYearStartTime(startdate));
					enddate = DateUtils.parseDefaultDate(DateUtils.getYearEndTime(startdate));
				} else if ("videoQuarterly".equals(type)) {
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
			List<DepartmentalReports> list=getDaily(type, startdate.getTime(), 
					enddate.getTime(),"",api,map);
			outmap.put("REPORTDATA", JSONArray.fromObject(list,jsonConfig).toString());
		}else{
			if(!b){
				Map<String,DepartmentalReports> map=new HashMap<String,DepartmentalReports>();
				List<DepartmentalReports> list=getDaily(type, startdate.getTime(), 
						enddate.getTime(),"",api,map);
				outmap.put("REPORTDATA", JSONArray.fromObject(getCodes(api, map, paretareacode),jsonConfig).toString());
			}else{
				Map<String,List<DepartmentalReports>> map=new HashMap<String,List<DepartmentalReports>>();
				Map<String,List<Map>> map_code=new HashMap<String,List<Map>>();
				List<List<Map>> list0=new ArrayList<List<Map>>();
				getCustomizeDaily(type, startdate.getTime(),enddate.getTime(),"",api,map,map_code);
				outmap.put("REPORTDATA", JSONArray.fromObject(getCustomizeCodes(api, map, paretareacode,map_code,list0),jsonConfig).toString());
				outmap.put("BROKENLINE",JSONArray.fromObject( list0,jsonConfig).toString());
			}
		}
		outParam.add(outmap);
		return outParam;
	}
	static DecimalFormat df = new DecimalFormat("#0.00"); 
	public List<DepartmentalReports> getDaily(String type,long starttime,long endtime,
			String parentareacode,ISiteviewApi api,Map<String,DepartmentalReports> map){
		BoolQueryBuilder qb = QueryBuilders.boolQuery();
		qb.must(QueryBuilders.matchQuery("parentareacode", parentareacode));
		List<DepartmentalReports> deps=new ArrayList<DepartmentalReports>();
		ElasticSearchIndexerImpl indexer=(ElasticSearchIndexerImpl) ElasticSearchIndexerImpl.getiElasticSearchIndexerImpl();
		SearchResponse response = null;
		if(parentareacode.length()>0)
			response=indexer.getClient().prepareSearch("yftitoss")
				  .setTypes(type)
				  .setQuery(qb)
				  .setPostFilter(QueryBuilders.rangeQuery("date")
				  .from(starttime)
				  .to(endtime))
				  .addSort(SortBuilders.fieldSort("date").unmappedType("string").order(SortOrder.ASC))
				  .setFrom(0).setSize(1000).setExplain(true)
				  .execute()
		          .actionGet();
		else
			response=indexer.getClient().prepareSearch("yftitoss")
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
			DepartmentalReports dep=new DepartmentalReports();
			SearchHit hit = hits[i];
			Map result = hit.getSource();
			dep.setIntactcount(result.get("intactcount")==null?0:
				Integer.parseInt(result.get("intactcount").toString()));
			dep.setIntactrate(result.get("intactrate")==null?0:
				Double.parseDouble(result.get("intactrate").toString()));
			dep.setTotal(result.get("total")==null?0:
				Integer.parseInt(result.get("total").toString()));
			dep.setVideolosscount(result.get("videolosscount")==null?0:
				Integer.parseInt(result.get("videolosscount").toString()));
            dep.setVideolossduration(result.get("videolossduration")==null?0:
				Double.parseDouble(result.get("videolossduration").toString()));
            dep.setVideolossrate(result.get("videolossrate")==null?0:
				Double.parseDouble(result.get("videolossrate").toString()));
           dep.setAnomalycount(result.get("anomalycount")==null?0:
				Integer.parseInt(result.get("anomalycount").toString()));
           dep.setAnomalyrate(result.get("anomalyrate")==null?0:
				Double.parseDouble(result.get("anomalyrate").toString()));
           dep.setOnlinecount(result.get("onlinecount")==null?0:
				Integer.parseInt(result.get("onlinecount").toString()));
           dep.setOnlinerate(result.get("onlinerate")==null?0:
				Double.parseDouble(result.get("onlinerate").toString()));
           String logtime=result.get("date").toString();
		   dep.setDate(getTime(logtime));
		   dep.setArea(result.get("area")==null?"":result.get("area").toString());
		   dep.setAreacode(result.get("areacode")==null?"":result.get("areacode").toString());
           dep.setAnomalyrate(Double.parseDouble(df.format(dep.getAnomalyrate()*100)));
           dep.setIntactrate(Double.parseDouble(df.format(dep.getIntactrate()*100)));
           dep.setOnlinerate(Double.parseDouble(df.format(dep.getOnlinerate()*100)));
           dep.setVideolossrate(Double.parseDouble(df.format(dep.getVideolossrate()*100)));
           deps.add(dep);
           map.put(dep.getAreacode(), dep);
		}
		return deps;
	}
	public List<DepartmentalReports> getCustomizeDaily(String type,long starttime,long endtime,
			String parentareacode,ISiteviewApi api,Map<String,List<DepartmentalReports>> map,Map<String,List<Map>> map_code){
		BoolQueryBuilder qb = QueryBuilders.boolQuery();
		qb.must(QueryBuilders.matchQuery("parentareacode", parentareacode));
		List<DepartmentalReports> deps=new ArrayList<DepartmentalReports>();
		ElasticSearchIndexerImpl indexer=(ElasticSearchIndexerImpl) ElasticSearchIndexerImpl.getiElasticSearchIndexerImpl();
		SearchResponse response = null;
		if(parentareacode.length()>0)
			response=indexer.getClient().prepareSearch("yftitoss")
				  .setTypes(type)
				  .setQuery(qb)
				  .setPostFilter(QueryBuilders.rangeQuery("date")
				  .from(starttime)
				  .to(endtime))
				  .setFrom(0).setSize(1000).setExplain(true)
				 .addSort(SortBuilders.fieldSort("date").unmappedType("string").order(SortOrder.ASC))
				  .execute()
		          .actionGet();
		else
			response=indexer.getClient().prepareSearch("yftitoss")
			  .setTypes(type)
			  .setPostFilter(QueryBuilders.rangeQuery("date")
			  .from(starttime)
			  .to(endtime))
			  .setFrom(0).setSize(1000).setExplain(true)
			  .addSort(SortBuilders.fieldSort("date").unmappedType("string").order(SortOrder.ASC))
			  .execute()
	          .actionGet();
		SearchHit[] hits = response.getHits().getHits();
		for (int i = 0; i < hits.length; i++) {
			DepartmentalReports dep=new DepartmentalReports();
			SearchHit hit = hits[i];
			Map result = hit.getSource();
			dep.setIntactcount(result.get("intactcount")==null?0:
				Integer.parseInt(result.get("intactcount").toString()));
			dep.setIntactrate(result.get("intactrate")==null?0:
				Double.parseDouble(result.get("intactrate").toString()));
			dep.setTotal(result.get("total")==null?0:
				Integer.parseInt(result.get("total").toString()));
			dep.setVideolosscount(result.get("videolosscount")==null?0:
				Integer.parseInt(result.get("videolosscount").toString()));
            dep.setVideolossduration(result.get("videolossduration")==null?0:
				Double.parseDouble(result.get("videolossduration").toString()));
            dep.setVideolossrate(result.get("videolossrate")==null?0:
				Double.parseDouble(result.get("videolossrate").toString()));
           dep.setAnomalycount(result.get("anomalycount")==null?0:
				Integer.parseInt(result.get("anomalycount").toString()));
           dep.setAnomalyrate(result.get("anomalyrate")==null?0:
				Double.parseDouble(result.get("anomalyrate").toString()));
           dep.setOnlinecount(result.get("onlinecount")==null?0:
				Integer.parseInt(result.get("onlinecount").toString()));
           dep.setOnlinerate(result.get("onlinerate")==null?0:
				Double.parseDouble(result.get("onlinerate").toString()));
           String logtime=result.get("date").toString();
		   dep.setDate(getTime(logtime));
		   dep.setArea(result.get("area")==null?"":result.get("area").toString());
		   dep.setAreacode(result.get("areacode")==null?"":result.get("areacode").toString());
           dep.setAnomalyrate(Double.parseDouble(df.format(dep.getAnomalyrate()*100)));
           dep.setIntactrate(Double.parseDouble(df.format(dep.getIntactrate()*100)));
           dep.setOnlinerate(Double.parseDouble(df.format(dep.getOnlinerate()*100)));
           dep.setVideolossrate(Double.parseDouble(df.format(dep.getVideolossrate()*100)));
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
	public static Date getTime(String time){
		TimeZone utc = TimeZone.getTimeZone("UTC");
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		f.setTimeZone(utc);
		try {
			Date d=f.parse(time);
			return d;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}
	public static String getTimeBegin(int i,Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if (i == 0) {
			int mondayPlus;
			int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; // 因为按中国礼拜一作为第一天所以这里减1
			if (dayOfWeek == 1) {
				mondayPlus = 0;
			} else {
				mondayPlus = 1 - dayOfWeek;
			}
//			mondayPlus-=8;
			GregorianCalendar currentDate = new GregorianCalendar();
			currentDate.setTime(date);
			currentDate.add(GregorianCalendar.DATE, mondayPlus);
			return format.format(currentDate.getTime()) + " 00:00:00.000";
		} else if (i == 1) {
			calendar.add(Calendar.MONTH, 0);
			calendar.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
		} else if (i == 2) {
//			calendar.setTime(new Date());
//			int month = getQuarterInMonth(calendar.get(Calendar.MONTH), true);
//			calendar.set(Calendar.MONTH, -3);
//			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar = getCurrentQuarterStartTime();
		} else if (i == 3) {
			int year = calendar.get(Calendar.YEAR);
			calendar.clear();
			calendar.set(Calendar.YEAR, year);
		}
		String first = format.format(calendar.getTime()) + " 00:00:00.000";
		return first;
	}
	public static String getTimeEnd(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int day_of_week = cal.get(Calendar.DAY_OF_WEEK) - 2;
		cal.add(Calendar.DATE, -day_of_week);
		cal.add(Calendar.DATE, 6);
		return  format.format(cal.getTime())+" 23:59:59.999";
	}
	// 返回第几个月份，不是几月
	// 季度一年四季， 第一季度：2月-4月， 第二季度：5月-7月， 第三季度：8月-10月， 第四季度：11月-1月
	private static int getQuarterInMonth(int month, boolean isQuarterStart) {
		int months[] = { 1, 4, 7, 10 };
		if (!isQuarterStart) {
			months = new int[] { 3, 6, 9, 12 };
		}
		if (month >= 2 && month <= 4)
			return months[0];
		else if (month >= 5 && month <= 7)
			return months[1];
		else if (month >= 8 && month <= 10)
			return months[2];
		else
			return months[3];
	}
	
	//获取季度开始时间
	public static Calendar getCurrentQuarterStartTime() {
		Calendar c = Calendar.getInstance();
		int currentMonth = c.get(Calendar.MONTH) + 1;
		try {
			if (currentMonth >= 1 && currentMonth <= 3)
				c.set(Calendar.MONTH, 0);
			else if (currentMonth >= 4 && currentMonth <= 6)
				c.set(Calendar.MONTH, 3);
			else if (currentMonth >= 7 && currentMonth <= 9)
				c.set(Calendar.MONTH, 4);
			else if (currentMonth >= 10 && currentMonth <= 12)
				c.set(Calendar.MONTH, 9);
			c.set(Calendar.DATE, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}
	public static List<List<DepartmentalReports>> getCodes(ISiteviewApi api,Map<String,DepartmentalReports> deps,String parecode){
		String sql="select * from Organization where o_code like '"+parecode+"%'";
		List<List<DepartmentalReports>> de=new ArrayList<List<DepartmentalReports>>();
		List<Integer> lists=new ArrayList<Integer>();
		DataTable dt=DBQueryUtils.Select(sql, api);
		Map<String,List<DepartmentalReports>> codemap=new HashMap<String,List<DepartmentalReports>>();
		for(DataRow dr:dt.get_Rows()){
			String code=dr.get("o_code")==null?"":dr.get("o_code").toString();
			DepartmentalReports dep=deps.get(code);
			if(dep==null)
				continue;
			List<DepartmentalReports> list=codemap.get(code.length()+"");
			if(!lists.contains(code.length()))
				lists.add(code.length());
			if(list==null)
				list=new ArrayList<DepartmentalReports>();
			if(!list.contains(code))
				list.add(dep);
			codemap.put(code.length()+"", list);
		}
		Collections.sort(lists);
		for(int i=0;i<lists.size();i++){
			if(codemap.get(lists.get(i)+"")!=null&&codemap.get(lists.get(i)+"").size()>0)
				de.add(codemap.get(lists.get(i)+""));
		}
		return de;
	}
	
	public static List<List<DepartmentalReports>> getCustomizeCodes(ISiteviewApi api,Map<String,List<DepartmentalReports>> deps,String parecode,
			Map<String,List<Map>> m,List<List<Map>> list0){
		Map<String,List<Map>> _codemap=new HashMap<String,List<Map>>();
		String sql="select * from Organization where o_code like '"+parecode+"%'";
		List<List<DepartmentalReports>> de=new ArrayList<List<DepartmentalReports>>();
		List<Integer> lists=new ArrayList<Integer>();
		DataTable dt=DBQueryUtils.Select(sql, api);
		Map<String,List<DepartmentalReports>> codemap=new HashMap<String,List<DepartmentalReports>>();
		for(DataRow dr:dt.get_Rows()){
			String code=dr.get("o_code")==null?"":dr.get("o_code").toString();
			String codename=dr.get("o_name")==null?"":dr.get("o_name").toString();
			List<DepartmentalReports> dep=deps.get(code);
			if(dep==null)
				continue;
			List<DepartmentalReports> list=codemap.get(code.length()+"");
			List<Map> list1=_codemap.get(code.length()+"");
			if(list1==null)
				list1=new ArrayList<Map>();
			Map<String,List> map=new HashMap<String, List>();
			map.put(codename, m.get(code));
			list1.add(map);
			_codemap.put(code.length()+"", list1);
			if(!lists.contains(code.length()))
				lists.add(code.length());
			if(list==null)
				list=new ArrayList<DepartmentalReports>();
				list.addAll(dep);
			codemap.put(code.length()+"", list);
		}
		Collections.sort(lists);
		for(int i=0;i<lists.size();i++){
			de.add(codemap.get(lists.get(i)+""));
			list0.add(_codemap.get(lists.get(i)+""));
		}
		return de;
	}
}
