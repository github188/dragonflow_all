package com.siteview.ecc.yft.es;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.siteview.ecc.elasticsearchutil.ElasticSchema;
import com.siteview.ecc.elasticsearchutil.ElasticSearchIndexer;
import com.siteview.ecc.elasticsearchutil.ElasticSearchIndexerImpl;
/*
 * 视频每小时断线报表
 */
public class VideoOnlineHoursReport implements ElasticSchema{

	@Override
	public String getIndex() {
		// TODO Auto-generated method stub
		return "yftitoss";
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return "videoOnlineHoursReport";
	}

	@Override
	public Map<String, Class<?>> getFieldNameTypeMapping() {
		// TODO Auto-generated method stub
		Map<String,Class<?>> mapping=new HashMap<String,Class<?>>();
		mapping.put("onlinerate", Double.class);//总数
		mapping.put("date", Date.class);//图像异常数
		return mapping;
	}
	public static void saveVideoDaily(Date date,double d){
		ElasticSearchIndexer indexer=ElasticSearchIndexerImpl.getiElasticSearchIndexerImpl();
		VideoOnlineHoursReport schema=new VideoOnlineHoursReport();
		Map<String, Object> mapping = new HashMap<String, Object>();
		mapping.put("onlinerate", d);//总数
		mapping.put("date",date);
		try {
			indexer.index(schema, mapping);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
