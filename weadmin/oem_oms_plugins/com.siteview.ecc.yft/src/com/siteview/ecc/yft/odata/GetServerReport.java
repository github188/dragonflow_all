package com.siteview.ecc.yft.odata;

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
import com.siteview.ecc.yft.bean.ServerDetail;
import com.siteview.ecc.yft.util.ODataUtils;
import com.siteview.utils.date.DateUtils;
import com.siteview.utils.db.DBQueryUtils;
import com.siteview.utils.text.TextUtils;

public class GetServerReport implements IFunctionExtension {

	public GetServerReport() {
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api, Map<String, String> inputParamMap) {
		String type = inputParamMap.get("REPORT_TYPE"); // 默认是日报
		String serverType = inputParamMap.get("SERVER_TYPE");
		Date startdate = null;
		Date enddate = null;
		String starttime = inputParamMap.get("STARTTIME");
		String endtime = inputParamMap.get("ENDTIME");
		boolean b = false; //是否是自定义报表
		List<Map<String, String>> outParam = new ArrayList<Map<String, String>>();
		try {
			if (starttime != null) {
				starttime += " 00:00:00";
				startdate = DateUtils.parseDefaultDate(starttime + " 00:00:00");
				if (endtime == null) {
					endtime = inputParamMap.get("STARTTIME") + " 23:59:59";
					b = false;
				} else {
					b = true;
					endtime += " 23:59:59";
				}
				enddate = DateUtils.parseDefaultDate(endtime);

				List<ServerDetail> list = null;
				if (b) {
					startdate = DateUtils.parseDefaultDate(starttime);
					enddate = DateUtils.parseDefaultDate(endtime);
				} else {
					if (type == null || "".equals(type) || "serverDaily".equals(type)) { // 日报
						// list = PerDayReport.getServerDetailsDaily(api,
						// startdate.getTime(),
						// enddate.getTime(),
						// type, serverType);
						type = "serverDaily";
					} else if ("serverWeekly".equals(type)) {
						startdate = DateUtils.parseDefaultDate(DateUtils.getWeekStartTime(startdate));
						enddate = DateUtils.parseDefaultDate(DateUtils.getWeekEndTime(startdate));
					} else if ("serverMonthly".equals(type)) {
						startdate = DateUtils.parseDefaultDate(DateUtils.getMonthStartTime(startdate));
						enddate = DateUtils.parseDefaultDate(DateUtils.getMonthEndTime(startdate));
					} else if ("serverQuarterly".equals(type)) {
						startdate = DateUtils.parseDefaultDate(DateUtils.getQuarterStartTime(startdate));
						enddate = DateUtils.parseDefaultDate(DateUtils.getQuarterEndTime(startdate));
					} else if ("serverYearly".equals(type)) {
						startdate = DateUtils.parseDefaultDate(DateUtils.getYearStartTime(startdate));
						enddate = DateUtils.parseDefaultDate(DateUtils.getYearEndTime(startdate));
					}
				}
				String parentareacode = ODataUtils.getParentAreaCode(api);
				Map<String, List<ServerDetail>> map = new HashMap<String, List<ServerDetail>>();
				Map<String, List<Map>> map_code = new HashMap<String, List<Map>>(); // 自定义报表返回集合,<地区,返回值集合<日期,值>>
				
				list = getServers(type, serverType, startdate.getTime(), enddate.getTime(), "", map, map_code);
				JsonConfig jsonConfig = new JsonConfig();
				jsonConfig.registerJsonValueProcessor(java.util.Date.class,
						new JsonValueProcessorImplTest());
				Map<String, String> outmap = new HashMap<String, String>();
				if (parentareacode == null || parentareacode.length() == 0){
					outmap.put("REPORTDATA", JSONArray.fromObject(list, jsonConfig).toString());
				}
				else {
					List<List<Map>> list0 = new ArrayList<List<Map>>();
					outmap.put("REPORTDATA", JSONArray.fromObject(getCodes(api, map, parentareacode,map_code,list0),jsonConfig)
							.toString());
					if (b) {
						outmap.put("BROKENLINE", JSONArray.fromObject(list0, jsonConfig).toString());
					}
				}
				outParam.add(outmap);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return outParam;
	}

	private List<List<ServerDetail>> getCodes(ISiteviewApi api, Map<String, List<ServerDetail>> map,
			String parentareacode,Map<String,List<Map>> m,List<List<Map>> list0) {
		String sql = "select * from Organization where o_code like '" + parentareacode + "%'";
		DataTable dt = DBQueryUtils.Select(sql, api);
		List<List<ServerDetail>> de = new ArrayList<List<ServerDetail>>();
		List<Integer> lists = new ArrayList<Integer>();
		Map<String, List<Map>> _codemap = new HashMap<String, List<Map>>();
		Map<String, List<ServerDetail>> codemap = new HashMap<String, List<ServerDetail>>();
		for (DataRow dr : dt.get_Rows()) {
			String code = dr.get("o_code") == null ? "" : dr.get("o_code").toString();
			String codename = dr.get("o_name") == null ? "" : dr.get("o_name").toString();
			List<ServerDetail> dep = map.get(code);
//			ServerDetail dep = map.get(code);
			if (dep != null) {
				List<ServerDetail> list = codemap.get(code.length() + "");
				//厅级数据
				List<Map> list1 = _codemap.get(code.length() + "");
				if (list1 == null)
					list1 = new ArrayList<Map>();
				Map<String, List> tmap = new HashMap<String, List>();
				tmap.put(codename, m.get(code));
				list1.add(tmap);
				_codemap.put(code.length()+"", list1);
				
				if (!lists.contains(code.length()))
					lists.add(code.length());
				if (list == null)
					list = new ArrayList<ServerDetail>();
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

	public List<ServerDetail> getServers(String type, String serverType, long starttime, long endtime, String parentareacode, Map<String, List<ServerDetail>> map, Map<String, List<Map>> map_code) {
		BoolQueryBuilder qb = QueryBuilders.boolQuery();
		qb.must(QueryBuilders.matchQuery("parentareacode", parentareacode));
		List<ServerDetail> servers = new ArrayList<ServerDetail>();
		ElasticSearchIndexerImpl indexer = (ElasticSearchIndexerImpl) ElasticSearchIndexerImpl
				.getiElasticSearchIndexerImpl();
		
		TypesExistsResponse typeresponse = indexer.getClient().admin().indices().prepareTypesExists("yftitoss")
				.setTypes(type).execute()
        .actionGet();
		if(typeresponse.isExists()){
			SearchRequestBuilder builder = indexer.getClient().prepareSearch("yftitoss").setTypes(type);
			if (parentareacode.length() > 0)
				builder.setQuery(qb);
			SearchResponse response = builder.setQuery(QueryBuilders.matchQuery("servertype", serverType))
					.setPostFilter(QueryBuilders.rangeQuery("date").from(starttime).to(endtime))
					.addSort("date", SortOrder.ASC).setFrom(0).setSize(10000).setExplain(true).execute()
					.actionGet();
			SearchHit[] hits = response.getHits().getHits();
			for (int i = 0; i < hits.length; i++) {
				try {
					ServerDetail server = new ServerDetail();
					SearchHit hit = hits[i];
					Map result = hit.getSource();
					server.setTotal(result.get("total") == null ? 0 : Long.parseLong(result.get("total")
							.toString()));
					server.setOnlinecount(result.get("onlinecount") == null ? 0 : Long.parseLong(result.get(
							"onlinecount").toString()));
					server.setOnlinerate(result.get("onlinerate") == null ? 0 : TextUtils.formatDouble(Double.parseDouble(result.get(
							"onlinerate").toString())*100, 2));
					server.setIntactcount(result.get("intactcount") == null ? 0 : Long.parseLong(result.get(
							"intactcount").toString()));
					server.setIntactrate(result.get("intactrate") == null ? 0 : TextUtils.formatDouble(Double.parseDouble(result.get(
							"intactrate").toString())*100,2));
					server.setServertype(serverType);
					String logtime = result.get("date").toString();
					server.setDate(DateUtils.getESTime(logtime));
					server.setArea(result.get("area") == null ? "" : result.get("area").toString());
					server.setAreacode(result.get("areacode") == null ? "" : result.get("areacode").toString());
					servers.add(server);
					List<Map> list1=map_code.get(server.getAreacode());
					if(list1==null)
						list1=new ArrayList<Map>();
					Map<String,Object> map1=new HashMap<String, Object>();
					map1.put("date", server.getDate());
					map1.put("total", server.getTotal());
					map1.put("onlinerate", server.getOnlinerate());
					map1.put("onlinecount", server.getOnlinecount());
					map1.put("intactcount", server.getIntactcount());
					map1.put("intactrate", server.getIntactrate());
					map1.put("servertype", server.getServertype());
					list1.add(map1);
					map_code.put(server.getAreacode(), list1);
					List<ServerDetail> list = map.get(server.getAreacode());
					if(list==null)
						list=new ArrayList<ServerDetail>();
					list.add(server);
					map.put(server.getAreacode(), list);
				} catch (Exception e) {
					continue;
				}
			}
		}
		
		return servers;
	}
}
