package com.siteview.ecc.yft.es;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siteview.ecc.elasticsearchutil.ElasticSchema;
import com.siteview.ecc.elasticsearchutil.ElasticSearchIndexer;
import com.siteview.ecc.elasticsearchutil.ElasticSearchIndexerImpl;

public class AssetsMaintenanceReportES implements ElasticSchema {
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
		mapping.put("starttime", Date.class);
		mapping.put("endtime", Date.class);
		mapping.put("assetscode", String.class);
		mapping.put("gbcode", String.class);
		mapping.put("maintenancepay", String.class);
		mapping.put("checktime", Date.class);
		mapping.put("checkname", String.class);
		mapping.put("manufacturerinfo", String.class);
		mapping.put("areaname", String.class);
		mapping.put("typename", String.class);
		mapping.put("assetsname", String.class);
		mapping.put("date", Date.class);
		return mapping;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public void setType(String type) {
		this.type = type;
	}

	public static void SaveInfo(String index, String type, List<Map<String, String>> maintenanceInfo, Date date) throws ParseException {
		ElasticSearchIndexer indexer = ElasticSearchIndexerImpl.getiElasticSearchIndexerImpl();
		AssetsMaintenanceReportES schema = new AssetsMaintenanceReportES();
		schema.setIndex(index);
		schema.setType(type);
		 SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (Map<String, String> info : maintenanceInfo) {
			Map<String, Object> mapping = new HashMap<String, Object>();
			mapping.put("starttime",sdf1.parse(info.get("START_TIME")));
			mapping.put("endtime", sdf1.parse(info.get("END_TIME")));
			mapping.put("assetscode", info.get("ASSETS_CODE"));
			mapping.put("gbcode", info.get("GB_CODE"));
			mapping.put("maintenancepay", info.get("MAINTENANCE_PAY"));
			mapping.put("checktime", sdf1.parse(info.get("MAINTENANCE_TIME")));
			mapping.put("checkname", info.get("CHECK_NAME"));
			mapping.put("manufacturerinfo", info.get("MANUFACTURER_INFO"));
			mapping.put("areaname", info.get("AREA_NAME"));
			mapping.put("typename", info.get("PRODUCT_TYPE"));
			mapping.put("assetsname", info.get("ASSETS_NAME"));
			mapping.put("date", sdf1.parse(info.get("END_TIME")));
			try {
				indexer.index(schema, mapping);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
