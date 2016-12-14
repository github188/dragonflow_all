package com.siteview.ecc.yft.report;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.Api.ISiteviewApi;

import com.siteview.utils.db.DBQueryUtils;

public class AssetsMaintenanceReport {
	
	public static List<Map<String,String>> getAssetsMaintenanceReportInfo(List<Map<String, String>> outList,String startTime,String endTime,ISiteviewApi api){
		StringBuilder sqlsb = new StringBuilder();
		sqlsb.append("Select  a.CreatedDateTime, a.CheckName, a.GBCode ,a.MaintenancePay,a.ManufacturerInfo,a.CheckTime ,r.Name AreaName,p.TypeName,h.AssetsName,h.AssetsCode ");
		sqlsb.append(" from  AssetsMaintenanceList  a LEFT JOIN Area r on a.AreaID=r.RecId ");
		sqlsb.append(" left join HardwareAssets h on  a.GBCode=h.RecId left join ProductType p  on p.RecId=a.ProductType");
		sqlsb.append(" WHERE a.CreatedDateTime>'");
		sqlsb.append(startTime);
		sqlsb.append("' AND a.CreatedDateTime<'");
		sqlsb.append(endTime);
		sqlsb.append("'");
		DataTable dataTable = DBQueryUtils.Select(sqlsb.toString(), api);
		if (dataTable == null || dataTable.get_Rows().isEmpty())
			return outList;
		for (DataRow row : dataTable.get_Rows()) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("START_TIME", row.get("CreatedDateTime").toString());
			map.put("END_TIME", row.get("CheckTime")==null?"1970-01-01 08:00:00":row.get("CheckTime").toString());
			map.put("ASSETS_CODE", isNotNull(row.get("AssetsCode")));
			map.put("GB_CODE", isNotNull(row.get("GBCode")));
			map.put("MAINTENANCE_PAY", isNotNull(row.get("MaintenancePay")));
			map.put("MAINTENANCE_TIME", row.get("CheckTime")==null?"1970-01-01 08:00:00":row.get("CheckTime").toString());
			map.put("CHECK_NAME", isNotNull(row.get("CheckName")));
			map.put("MANUFACTURER_INFO", isNotNull(row.get("ManufacturerInfo")));
			map.put("AREA_NAME", isNotNull(row.get("AreaName")));
			map.put("PRODUCT_TYPE", isNotNull(row.get("TypeName")));
			map.put("ASSETS_NAME", isNotNull(row.get("AssetsName")));
			outList.add(map);
		}
		return outList;
	}
	public static String isNotNull(Object  object){
		return object==null?"":object.toString();
	}

}
