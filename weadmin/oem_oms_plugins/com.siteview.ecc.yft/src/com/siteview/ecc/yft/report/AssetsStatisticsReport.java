package com.siteview.ecc.yft.report;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.Api.ISiteviewApi;

import com.siteview.utils.db.DBQueryUtils;
import com.siteview.utils.html.StringUtils;

public class AssetsStatisticsReport {
   public  static List<AssetsInfo> getAssetsInfo(String startTime, String endTime,ISiteviewApi api){
	   StringBuilder sqlsb = new StringBuilder();
		sqlsb.append("SELECT  h.CreatedBy,h.CreatedDateTime, h.AssetsCode,h.AssetsName,h.GBCode,s.StatusName,h.AssetsBrand  ,p.TypeName,h.ProductModel,h.MaintenancePeople,h.WarrantyPeriod,m.MNumber ");
		sqlsb.append("from HardwareAssets h ");
		sqlsb.append("left join (	select GBCode, count(RecId) MNumber from   AssetsMaintenanceList  GROUP BY GBCode)  m on m.GBCode=h.RecId ");
		sqlsb.append(" LEFT JOIN ProductType p ON h.ProductType=p.RecId");
//		sqlsb.append(" LEFT JOIN  EquipmentBrand  b ON h.AssetsBrand=b.RecId ");
		sqlsb.append(" LEFT JOIN AssetsStatus s ON h.AssetStates=s.RecId");
		sqlsb.append(" where WarrantyPeriod>'");
		sqlsb.append(startTime);
		sqlsb.append("' AND WarrantyPeriod<'");
		sqlsb.append(endTime);
		sqlsb.append("'");

		DataTable dataTable = DBQueryUtils.Select(sqlsb.toString(), api);
		List<AssetsInfo> list = new ArrayList<AssetsInfo>();
		if (dataTable == null || dataTable.get_Rows().size() == 0) {
			return list;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (DataRow row : dataTable.get_Rows()) {
			AssetsInfo assetsInfo = new AssetsInfo();
			assetsInfo.setAssetsBrand(row.get("AssetsBrand")==null?"":row.get("AssetsBrand").toString());
			assetsInfo.setAssetsCode(row.get("AssetsCode").toString());
			assetsInfo.setAssetsName(row.get("AssetsName").toString());
			assetsInfo.setAssetsStatus(row.get("StatusName")==null?"":row.get("StatusName").toString());
			assetsInfo.setAssetsType(row.get("TypeName")==null?"":row.get("TypeName").toString());
			assetsInfo.setAssetsModel(row.get("ProductModel")==null?"":row.get("ProductModel").toString());
			assetsInfo.setMaintenanceNumber(row.get("MNumber")==null?0:Integer.parseInt(row.get("MNumber").toString()));
//			assetsInfo.setMaintenanceNumber(0);
			assetsInfo.setMaintenancePeople(row.get("MaintenancePeople")==null?"":row.get("MaintenancePeople").toString());
			String warrantyPeriod = StringUtils.removeLastPoint(row.get("WarrantyPeriod")==null?"":row.get("WarrantyPeriod").toString());
			try {
				assetsInfo.setEndDate(sdf.parse(endTime));
				assetsInfo.setWarrantyPeriod(sdf.parse(warrantyPeriod));
				boolean wpflag = false;
				if (sdf.parse(warrantyPeriod).before(sdf.parse(endTime)))
					wpflag = true;
				assetsInfo.setWpflag(wpflag);
				assetsInfo.setCreatedDateTime(sdf.parse(StringUtils.removeLastPoint(row.get("CreatedDateTime").toString())));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			assetsInfo.setCreatedBy(StringUtils.removeLastPoint(row.get("CreatedBy").toString()));
			assetsInfo.setAssetsModel(row.get("ProductModel").toString());
			list.add(assetsInfo);
		}
		return list;
   }
}
