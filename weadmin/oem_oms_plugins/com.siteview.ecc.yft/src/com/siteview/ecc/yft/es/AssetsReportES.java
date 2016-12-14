package com.siteview.ecc.yft.es;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siteview.ecc.elasticsearchutil.ElasticSchema;
import com.siteview.ecc.elasticsearchutil.ElasticSearchIndexer;
import com.siteview.ecc.elasticsearchutil.ElasticSearchIndexerImpl;
import com.siteview.ecc.yft.report.AssetsInfo;

public class AssetsReportES implements ElasticSchema{
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
		mapping.put("assetscode", String.class);
		mapping.put("assetsbrand",String.class);
		mapping.put("assetsmodel", String.class);
		mapping.put("assetsname",String.class);
		mapping.put("assetsstatus", String.class);
		mapping.put("assetstype", String.class);
		mapping.put("createdby", String.class);
		mapping.put("createddatetime", Date.class);
		mapping.put("maintenancenumber", Integer.class);
		mapping.put("maintenancepeople", String.class);
		mapping.put("warrantyperiod", Date.class);
		mapping.put("wpflag", Boolean.class);
		mapping.put("date", Date.class);
		return mapping;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public static void SaveInfo(String index,String type,List<AssetsInfo> assetsInfo,Date date ){
		ElasticSearchIndexer indexer=ElasticSearchIndexerImpl.getiElasticSearchIndexerImpl();
		AssetsReportES schema=new AssetsReportES();
		schema.setIndex(index);
		schema.setType(type);
		for (AssetsInfo info : assetsInfo) {
			Map<String, Object> mapping = new HashMap<String, Object>();
			mapping.put("assetscode", info.getAssetsCode());
			mapping.put("assetsbrand", info.getAssetsBrand());
			mapping.put("assetsmodel", info.getAssetsModel());
			mapping.put("assetsname", info.getAssetsName());
			mapping.put("assetsstatus", info.getAssetsStatus());
			mapping.put("assetstype", info.getAssetsType());
			mapping.put("createdby", info.getCreatedBy());
			mapping.put("createddatetime", info.getCreatedDateTime());
			mapping.put("maintenancenumber", info.getMaintenanceNumber());
			mapping.put("maintenancepeople", info.getMaintenancePeople());
			mapping.put("warrantyperiod", info.getWarrantyPeriod());
			mapping.put("wpflag", info.isWpflag());
			mapping.put("date", info.getEndDate());
			try {
				indexer.index(schema, mapping);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
		
	}

}
