package com.siteview.ecc.yft.es;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.siteview.ecc.elasticsearchutil.ElasticSchema;

/**
 * 非视频类设备日报
 * @author Administrator
 *
 */
public class ServerDetailsDaily implements ElasticSchema {
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
		mapping.put("total", Long.class);// 总数
		mapping.put("onlinecount", Long.class);// 在线数
		mapping.put("onlinerate", Double.class);// 在线率
		mapping.put("intactrate", Double.class);// 完好率
		mapping.put("intactcount", Long.class);// 完好数
		mapping.put("unavailablecount", Long.class);// 不可用总时长
		mapping.put("servertype", String.class); // 设备类型
		mapping.put("area", String.class);
		mapping.put("areacode", String.class);
		mapping.put("parentarea", String.class);
		mapping.put("parentareacode", String.class);
		mapping.put("date", Date.class);
		return mapping;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public void setType(String type) {
		this.type = type;
	}

}
