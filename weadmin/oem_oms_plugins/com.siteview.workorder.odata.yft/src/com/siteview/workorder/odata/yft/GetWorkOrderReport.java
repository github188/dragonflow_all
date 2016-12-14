package com.siteview.workorder.odata.yft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;

import siteview.IFunctionExtension;
import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.Api.ISiteviewApi;

import com.siteview.ecc.elasticsearchutil.ElasticSearchIndexerImpl;
import com.siteview.ecc.yft.bean.JsonValueProcessorImplTest;
import com.siteview.ecc.yft.bean.WorkOrderReport;
import com.siteview.ecc.yft.util.ODataUtils;
import com.siteview.utils.date.DateUtils;
import com.siteview.utils.db.DBQueryUtils;
import com.siteview.utils.text.TextUtils;

public class GetWorkOrderReport implements IFunctionExtension {

	public GetWorkOrderReport() {
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api, Map<String, String> inputParamMap) {
		String type = inputParamMap.get("REPORT_TYPE"); // 默认是周报
		Date startdate = null;
		Date enddate = null;
		String starttime = inputParamMap.get("STARTTIME");
		String endtime = inputParamMap.get("ENDTIME");
		boolean b = false; //是否是自定义报表
		List<Map<String, String>> outParam = new ArrayList<Map<String, String>>();
		try {
			if (starttime != null) {
				starttime += " 00:00:00";
				startdate = DateUtils.parseDefaultDate(DateUtils.getWeekStartTime(DateUtils
						.parseDefaultDate(starttime)));
				if (endtime == null) {
					endtime = starttime;
					b = false;
				} else { //自定义报表
					b = true;
					endtime += " 23:59:59";
				}
				enddate = DateUtils.parseDefaultDate(DateUtils.getWeekEndTime(startdate));

				List<WorkOrderReport> list = null;
				if (b) { //自定义时间不需要再另外算时间
					startdate = DateUtils.parseDefaultDate(starttime);
					enddate = DateUtils.parseDefaultDate(endtime);
				} else {
					if (type == null || "".equals(type) || "workOrderWeekly".equals(type)) { // 周报
						type = "workOrderWeekly";
					} else if ("workOrderMonthly".equals(type)) {
						startdate = DateUtils.parseDefaultDate(DateUtils.getMonthStartTime(DateUtils
								.parseDefaultDate(starttime)));
						enddate = DateUtils.parseDefaultDate(DateUtils.getMonthEndTime(startdate));
					} else if ("workOrderQuarterly".equals(type)) {
						startdate = DateUtils.parseDefaultDate(DateUtils.getQuarterStartTime(DateUtils
								.parseDefaultDate(starttime)));
						enddate = DateUtils.parseDefaultDate(DateUtils.getQuarterEndTime(startdate));
					} else if ("workOrderYearly".equals(type)) {
						startdate = DateUtils.parseDefaultDate(DateUtils.getYearStartTime(DateUtils
								.parseDefaultDate(starttime)));
						enddate = DateUtils.parseDefaultDate(DateUtils.getYearEndTime(startdate));
					}
				}
				String parentareacode = ODataUtils.getParentAreaCode(api);
				Map<String, List<WorkOrderReport>> map = new HashMap<String, List<WorkOrderReport>>();
				Map<String, List<Map>> map_code = new HashMap<String, List<Map>>(); // 自定义报表返回集合,<地区,返回值集合<日期,值>>
				
				list = getOrders(type, startdate.getTime(), enddate.getTime(), "" ,map, map_code);
				Map<String, String> outmap = new HashMap<String, String>();
				JsonConfig jsonConfig = new JsonConfig();
				jsonConfig.registerJsonValueProcessor(java.util.Date.class,
						new JsonValueProcessorImplTest());
				if (parentareacode == null || parentareacode.length() == 0)
					outmap.put("REPORTDATA", JSONArray.fromObject(list, jsonConfig).toString());
				else {
//					if (list.size() > 0) {
						List<List<Map>> list0 = new ArrayList<List<Map>>();
						outmap.put(
								"REPORTDATA",
								JSONArray.fromObject(getCodes(api, map, parentareacode, map_code, list0),
										jsonConfig).toString());
						if (b) {
							outmap.put("BROKENLINE", JSONArray.fromObject(list0, jsonConfig).toString());
						}
//					}
//					else {
//						outmap.put("REPORTDATA", JSONArray.fromObject(list, jsonConfig).toString());
//					}
				}
				outParam.add(outmap);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return outParam;
	}

	public List<WorkOrderReport> getOrders(String type, long starttime, long endtime,
			String parentareacode, Map<String, List<WorkOrderReport>> map, Map<String, List<Map>> map_code) {
		BoolQueryBuilder qb = QueryBuilders.boolQuery();
		qb.must(QueryBuilders.matchQuery("parentareacode", parentareacode));

		List<WorkOrderReport> orders = new ArrayList<WorkOrderReport>();
		ElasticSearchIndexerImpl indexer = (ElasticSearchIndexerImpl) ElasticSearchIndexerImpl
				.getiElasticSearchIndexerImpl();
		SearchRequestBuilder builder = indexer.getClient().prepareSearch("yftitoss").setTypes(type);
		TypesExistsResponse typeresponse = indexer.getClient().admin().indices()
				.prepareTypesExists("yftitoss").setTypes(type).execute().actionGet();
		if (typeresponse.isExists()) {
			if (parentareacode.length() > 0)
				builder.setQuery(qb);
			SearchResponse response = builder
					.setPostFilter(QueryBuilders.rangeQuery("date").from(starttime).to(endtime))
					.addSort("date", SortOrder.ASC).setFrom(0).setSize(10000).setExplain(true).execute()
					.actionGet();
			SearchHit[] hits = response.getHits().getHits();
			for (int i = 0; i < hits.length; i++) {
				try {
					WorkOrderReport order = new WorkOrderReport();
					SearchHit hit = hits[i];
					Map result = hit.getSource();
					order.setNewtotal(result.get("newtotal") == null ? 0 : Long.parseLong(result
							.get("newtotal").toString()));
					order.setCompletecount(result.get("completecount") == null ? 0 : Long.parseLong(result.get(
							"completecount").toString()));
					order.setCompleterate(result.get("completerate") == null ? 0 : TextUtils.formatDouble(
							Double.parseDouble(result.get("completerate").toString())*100, 2));
					order.setAvgduration(result.get("avgduration") == null ? 0 : Double.parseDouble(result.get(
							"avgduration").toString()));
					order.setOutcompleterate(result.get("outcompleterate") == null ? 0 : TextUtils
							.formatDouble(Double.parseDouble(result.get("outcompleterate").toString())*100, 2));
					order.setOutnocompleterate(result.get("outnocompleterate") == null ? 0 : TextUtils
							.formatDouble(Double.parseDouble(result.get("outnocompleterate").toString())*100, 2));
					String logtime = result.get("date").toString();
					order.setDate(DateUtils.getESTime(logtime));
					order.setArea(result.get("area") == null ? "" : result.get("area").toString());
					order.setAreacode(result.get("areacode") == null ? "" : result.get("areacode").toString());
					orders.add(order);
					
					List<Map> list1 = map_code.get(order.getAreacode());
					if (list1 == null)
						list1 = new ArrayList<Map>();
					Map<String, Object> map1 = new HashMap<String, Object>();
					map1.put("date", order.getDate());
					map1.put("newtotal", order.getNewtotal());
					map1.put("completecount", order.getCompletecount());
					map1.put("completerate", order.getCompleterate());
					map1.put("avgduration", order.getAvgduration());
					map1.put("outcompleterate", order.getOutnocompleterate());
					map1.put("outnocompleterate", order.getOutnocompleterate());
					list1.add(map1);
					map_code.put(order.getAreacode(), list1);
					List<WorkOrderReport> list = map.get(order.getAreacode());
					if (list == null)
						list = new ArrayList<WorkOrderReport>();
					list.add(order);
					map.put(order.getAreacode(), list);
				} catch (Exception e) {
					continue;
				}
			}
		}
		return orders;
	}
	
	public static List<List<WorkOrderReport>> getCodes(ISiteviewApi api,
			Map<String, List<WorkOrderReport>> deps, String parecode,Map<String,List<Map>> m,List<List<Map>> list0) {
		String sql = "select * from Organization where o_code like '" + parecode + "%'";
		DataTable dt = DBQueryUtils.Select(sql, api);
		List<List<WorkOrderReport>> de = new ArrayList<List<WorkOrderReport>>();
		List<Integer> lists = new ArrayList<Integer>();
		Map<String, List<Map>> _codemap = new HashMap<String, List<Map>>();
		Map<String, List<WorkOrderReport>> codemap = new HashMap<String, List<WorkOrderReport>>();
		for (DataRow dr : dt.get_Rows()) {
			String code = dr.get("o_code") == null ? "" : dr.get("o_code").toString();
			String codename = dr.get("o_name") == null ? "" : dr.get("o_name").toString();
			List<WorkOrderReport> dep = deps.get(code);
			if (dep != null) {
			//厅级数据
				List<Map> list1 = _codemap.get(code.length() + "");
				if (list1 == null)
					list1 = new ArrayList<Map>();
				Map<String, List> tmap = new HashMap<String, List>();
				tmap.put(codename, m.get(code));
				list1.add(tmap);
				_codemap.put(code.length()+"", list1);
				
				List<WorkOrderReport> list = codemap.get(code.length() + "");
				if (!lists.contains(code.length()))
					lists.add(code.length());
				if (list == null)
					list = new ArrayList<WorkOrderReport>();
				if (!list.contains(code))
					list.addAll(dep);
				codemap.put(code.length() + "", list);
			}
		}
		Collections.sort(lists);
		for (int i = 0; i < lists.size(); i++) {
			de.add(codemap.get(lists.get(i) + ""));
			list0.add(_codemap.get(lists.get(i)+""));
		}
		return de;
	}
	
	public void get(List<WorkOrderReport> list) {
		Map<String, Map<String,Object>> map = new HashMap<String, Map<String,Object>>();
		
		for (WorkOrderReport order : list) {
			String pCode = order.getParentareacode();
			String code = order.getAreacode();
			Map<String,Object> cMap = map.get(code);
			long nTotal = order.getNewtotal();
			long cCount = order.getCompletecount();
			double cRate = order.getCompleterate();
			if(cMap == null)
				cMap = new HashMap<String,Object>();
			else {
				nTotal += Long.parseLong(cMap.get("newtotal").toString());
				cCount += Long.parseLong(cMap.get("completecount").toString());
				cRate += Double.parseDouble(cMap.get("completerate").toString());
				cMap.get("completerate_num");
			}
			cMap.put("newtotal", nTotal);
			cMap.put("completecount", cCount);
			
			map.put(code, cMap);
		}

	}
}
