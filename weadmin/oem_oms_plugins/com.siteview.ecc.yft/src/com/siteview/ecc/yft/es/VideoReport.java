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
 * 视频统计报表
 */
public class VideoReport implements ElasticSchema{
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
		mapping.put("videolossduration", Double.class);//丢失视频总时长
		mapping.put("anomalyrate", Double.class);//图像异常率
		mapping.put("anomalycount", Integer.class);//图像异常数
		mapping.put("videolossrate", Double.class);//视频丢失率
		mapping.put("videolosscount", Integer.class);//视频丢失数
		mapping.put("intactrate", Double.class);//完好率
		mapping.put("intactcount", Integer.class);//完好数
		mapping.put("date", Date.class);
		mapping.put("area", String.class);
		mapping.put("areacode", String.class);
		mapping.put("parentarea", String.class);
		mapping.put("parentareacode", String.class);
		return mapping;
	}
	public static void saveVideoDaily(DepartmentalReports dep,String index,String type,Date date){
//		Calendar can=Calendar.getInstance();
//		can.add(calendarType, -1);
//		Date date=can.getTime();
//		date.setHours(0);
//		date.setMinutes(0);
//		date.setSeconds(0);
		ElasticSearchIndexer indexer=ElasticSearchIndexerImpl.getiElasticSearchIndexerImpl();
		VideoReport schema=new VideoReport();
		schema.setIndex(index);
		schema.setType(type);
		Map<String, Object> mapping = new HashMap<String, Object>();
		mapping.put("total", dep.getTotal());//总数
		mapping.put("onlinecount", dep.getOnlinecount());//在线数
		mapping.put("onlinerate", dep.getOnlinerate());//在线率
		mapping.put("videolossduration", dep.getVideolossduration());//丢失视频总时长
		mapping.put("anomalyrate", dep.getAnomalyrate());//图像异常率
		mapping.put("anomalycount", dep.getAnomalycount());//图像异常数
		mapping.put("videolossrate", dep.getVideolossrate());//视频丢失率
		mapping.put("videolosscount",dep.getVideolosscount());//视频丢失数
		mapping.put("intactrate", dep.getIntactrate());//完好率
		mapping.put("intactcount",dep.getIntactcount());//完好数
		mapping.put("date",date);
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
