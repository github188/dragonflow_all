package com.siteview.ecc.yft.es;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.siteview.ecc.elasticsearchutil.ElasticSchema;

/**
 * 计费考核报表
 * @author Administrator
 *
 */
public class AccountingAssessmentReport implements ElasticSchema {
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
		mapping.put("organization", String.class); // 区域,组织机构
		mapping.put("createdate", String.class); // 工单创建时间
		mapping.put("workordernum", String.class); // 工单号
		mapping.put("longdelay", Double.class); // 延长时长
		mapping.put("costdeduction", Double.class); // 所扣费用
		mapping.put("isp", String.class); // 服务提供商
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
