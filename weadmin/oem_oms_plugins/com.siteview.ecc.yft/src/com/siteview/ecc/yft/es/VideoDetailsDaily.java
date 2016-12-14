package com.siteview.ecc.yft.es;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siteview.ecc.elasticsearchutil.ElasticSchema;
import com.siteview.ecc.elasticsearchutil.ElasticSearchIndexer;
import com.siteview.ecc.elasticsearchutil.ElasticSearchIndexerImpl;
import com.siteview.ecc.yft.bean.LoseVideorecord;
/*
 * 摄像机详细报表（断线，录像丢失，视频丢失）
 */
public class VideoDetailsDaily implements ElasticSchema{
	private String index;
	private String type;
	@Override
	public String getIndex() {
		// TODO Auto-generated method stub
		return index;
	}
	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return type;
	}
	@Override
	public Map<String, Class<?>> getFieldNameTypeMapping() {
		Map<String,Class<?>> mapping=new HashMap<String,Class<?>>();
		mapping.put("videoname", String.class);
		mapping.put("ipaddress", String.class);
		mapping.put("installationaddress", String.class);
		mapping.put("offperiod", String.class);
		mapping.put("longoffline", Long.class);
		mapping.put("date", Date.class);
		mapping.put("videoflag",String.class);
		mapping.put("losttimes", Long.class);
		return mapping;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public void setType(String type) {
		this.type = type;
	}
	public static void saveVideoDetails(String index,String type,List<LoseVideorecord> list,Date d){
		try {
			ElasticSearchIndexer indexer=ElasticSearchIndexerImpl.getiElasticSearchIndexerImpl();
			VideoDetailsDaily schema=new VideoDetailsDaily();
			schema.setIndex(index);
			schema.setType(type);
			for(LoseVideorecord losevideo:list){
				Map<String, Object> mapping = new HashMap<String, Object>();
				mapping.put("videoname", losevideo.getVideoname());//总数
				mapping.put("ipaddress", losevideo.getIpaddress());//ip
				mapping.put("installationaddress", losevideo.getIplace());//安装地址
				mapping.put("offperiod", losevideo.getTime());//离线时间段
				mapping.put("longoffline", losevideo.getLongtime());//离线时长
				mapping.put("videoflag", losevideo.getVideoflag());
				mapping.put("losttimes", losevideo.getLosttimes());
				mapping.put("date", d);//图像异常数
				indexer.index(schema, mapping);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
