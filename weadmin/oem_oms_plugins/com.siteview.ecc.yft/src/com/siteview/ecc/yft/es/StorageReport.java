package com.siteview.ecc.yft.es;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.siteview.alarm.bean.DepartmentalReports;
import com.siteview.ecc.elasticsearchutil.ElasticSchema;
import com.siteview.ecc.elasticsearchutil.ElasticSearchIndexer;
import com.siteview.ecc.elasticsearchutil.ElasticSearchIndexerImpl;
/*
 * 存储类设备报表保存
 */
public class StorageReport implements ElasticSchema{
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
		Map<String,Class<?>> mapping=new HashMap<String,Class<?>>();
		mapping.put("total", Integer.class);//总数
		mapping.put("onlinecount", Integer.class);//在线数
		mapping.put("onlinerate", Double.class);//在线率
		mapping.put("intactrate", Double.class);//完好率
		mapping.put("intactcount", Integer.class);//完好数
		mapping.put("notlong", Long.class);
		mapping.put("date", Date.class);
		mapping.put("area", String.class);
		mapping.put("areacode", String.class);
		mapping.put("parentarea", String.class);
		mapping.put("parentareacode", String.class);
		return mapping;
	}
	public static void saveVideoDaily(DepartmentalReports dep,String index,String type,Date date){
		ElasticSearchIndexer indexer=ElasticSearchIndexerImpl.getiElasticSearchIndexerImpl();
		StorageReport schema=new StorageReport();
		schema.setIndex(index);
		schema.setType(type);
		Map<String, Object> mapping = new HashMap<String, Object>();
		mapping.put("total", dep.getTotal());//总数
		mapping.put("onlinecount", dep.getOnlinecount());//在线数
		mapping.put("onlinerate", dep.getOnlinerate());//在线率
		mapping.put("intactrate", dep.getIntactrate());//完好率
		mapping.put("intactcount",dep.getIntactcount());//完好数
		mapping.put("date",date);
		mapping.put("notlong", dep.getNotlong());
		mapping.put("area", dep.getArea());
		mapping.put("areacode", dep.getAreacode());
		mapping.put("parentarea", dep.getParentarea());
		mapping.put("parentareacode", dep.getParentareacode());
		try {
			indexer.index(schema, mapping);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public void setType(String type) {
		this.type = type;
	}
}
