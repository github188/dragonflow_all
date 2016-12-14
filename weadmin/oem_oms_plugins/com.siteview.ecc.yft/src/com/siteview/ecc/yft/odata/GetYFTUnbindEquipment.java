package com.siteview.ecc.yft.odata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.SiteviewException;
import Siteview.Api.ISiteviewApi;
import net.sf.json.JSONArray;
import siteview.IFunctionExtension;

public class GetYFTUnbindEquipment implements IFunctionExtension{

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api,
			Map<String, String> inputParamMap) {

		List<Map<String,String>> listMap = new ArrayList<Map<String,String>>();
		
		String type = inputParamMap.get("EQUIPMENTTYPE");
		String status = inputParamMap.get("STATUS");
		String FROM = inputParamMap.get("FROM");
		String TO = inputParamMap.get("TO");
		String COUNT = inputParamMap.get("COUNT");
		String ORDERBY = inputParamMap.get("ORDERBY");
		
		
		
//		if(type == null || "EQUIPMENTTYPE".equals(type) || "VIDEOPOINTINFO".equals(type) || "SERVERINFO".equals(type))
//		{
			StringBuffer sqlbuff = new StringBuffer();
//			sqlbuff.append("SELECT * FROM ( SELECT 'EQUIPMENTTYPE' EQUIPMENTTYPE, e.RecId GBCODE, e.GroupName GROUPNAME, e.title NAME, e.EquipmentType TYPE, e.ServerAddress IP, (CASE WHEN h.GBCode IS NOT NULL THEN 0 ELSE 1 END) STATUS FROM Equipment e LEFT JOIN HardwareAssets h ON e.RecId=h.GBCode UNION SELECT 'VIDEOPOINTINFO' EQUIPMENTTYPE, v.VIDEOFLAG GBCODE, '' GROUPNAME, v.VIDEONAME NAME, '摄像机' TYPE, v.IPADDRESS IP, (CASE WHEN h.GBCode IS NOT NULL THEN 0 ELSE 1 END) STATUS FROM VIDEOPOINTINFO v LEFT JOIN HardwareAssets h ON v.VIDEOFLAG=h.GBCode UNION SELECT 'SERVERINFO' EQUIPMENTTYPE, s.SERVERFLAG GBCODE, '' GROUPNAME, s.SERVERNAME NAME, s.IPLACE TYPE, s.SERVERIP IP, (CASE WHEN h.GBCode IS NOT NULL THEN 0 ELSE 1 END) STATUS FROM SERVERINFO s LEFT JOIN HardwareAssets h ON s.SERVERFLAG=h.GBCode ) t ");
//			sqlbuff.append("SELECT * FROM ( SELECT 'EQUIPMENTTYPE' EQUIPMENTTYPE, GBCODE, GROUPNAME, NAME, TYPE, IP, (CASE WHEN hh.FLAG IS NOT NULL THEN 0 ELSE 1 END) STATUS FROM (SELECT RecId GBCODE, GroupName GROUPNAME, title NAME, EquipmentType TYPE, ServerAddress IP FROM Equipment) ee LEFT JOIN (SELECT GBCode FLAG FROM HardwareAssets) hh ON ee.GBCODE=hh.FLAG UNION  SELECT 'VIDEOPOINTINFO' EQUIPMENTTYPE, GBCODE, '' GROUPNAME, NAME, '摄像机' TYPE, IP, (CASE WHEN hh.FLAG IS NOT NULL THEN 0 ELSE 1 END) STATUS FROM (SELECT VIDEOFLAG GBCODE, VIDEONAME NAME, IPADDRESS IP FROM VIDEOPOINTINFO) vv LEFT JOIN (SELECT GBCode FLAG FROM HardwareAssets) hh ON vv.GBCODE=hh.FLAG UNION SELECT 'SERVERINFO' EQUIPMENTTYPE, GBCODE, '' GROUPNAME, NAME, TYPE, IP, (CASE WHEN hh.FLAG IS NOT NULL THEN 0 ELSE 1 END) STATUS FROM (SELECT SERVERFLAG GBCODE, SERVERNAME NAME, IPLACE TYPE, SERVERIP IP FROM SERVERINFO) ss LEFT JOIN (SELECT GBCode FLAG FROM HardwareAssets) hh ON ss.GBCODE=hh.FLAG ) t ");
//			
////			if("VIDEOPOINTINFO".equals(type))
////			{
////				sqlbuff = "SELECT 'VIDEOPOINTINFO' EQUIPMENTTYPE, v.VIDEOFLAG GBCODE, '' GROUPNAME, v.VIDEONAME NAME, '摄像机' TYPE, v.IPADDRESS IP FROM VIDEOPOINTINFO v LEFT JOIN HardwareAssets h ON v.VIDEOFLAG=h.GBCode WHERE h.GBCode IS NULL";
////			}else if("SERVERINFO".equals(type))
////			{
////				sqlbuff = "SELECT 'SERVERINFO' EQUIPMENTTYPE, s.SERVERFLAG GBCODE, '' GROUPNAME, s.SERVERNAME NAME, s.IPLACE TYPE, s.SERVERIP IP FROM SERVERINFO s LEFT JOIN HardwareAssets h ON s.SERVERFLAG=h.GBCode WHERE h.GBCode IS NULL";
////			}else if("EQUIPMENTTYPE".equals(type))
////			{
////				sqlbuff = "SELECT 'EQUIPMENTTYPE' EQUIPMENTTYPE, e.RecId GBCODE, e.GroupName GROUPNAME, e.title NAME, e.EquipmentType TYPE, e.ServerAddress IP FROM Equipment e LEFT JOIN HardwareAssets h ON e.RecId=h.GBCode WHERE h.GBCode IS NULL";
////			}
//			String sql = sqlbuff.toString();
//			if(type != null && status != null)
//			{
//				sqlbuff.append("WHERE EQUIPMENTTYPE='%s' AND STATUS='%s'");
//				sql = String.format(sqlbuff.toString(), type, status);
//			}else if(type != null || status != null)
//			{
//				if(type != null)
//				{
//					sqlbuff.append("WHERE EQUIPMENTTYPE='%s'");
//					sql = String.format(sqlbuff.toString(), type);
//				}
//				else
//				{
//					sqlbuff.append("WHERE STATUS='%s'");
//					sql = String.format(sqlbuff.toString(), status);
//				}
//					
//			}
			
			String sql = "SELECT COUNT(GBCODE) COUNT FROM ( SELECT RecId GBCODE FROM Equipment UNION SELECT VIDEOFLAG GBCODE FROM VIDEOPOINTINFO UNION SELECT SERVERFLAG GBCODE FROM SERVERINFO )ss WHERE GBCODE NOT IN (SELECT GBCode GBCODE FROM HardwareAssets WHERE GBCode IS NOT NULL AND GBCode != '')";
			
			try {
				DataTable dt = api.get_NativeSQLSupportService().ExecuteNativeSQLQuery(sql, null);
				
				if(dt.get_Rows().size() > 0)
				{
					DataRow dr = dt.get_Rows().get(0);
					COUNT = ifNullToEmpty(dr.get_Item("COUNT"));
				}
			} catch (SiteviewException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			sqlbuff.append("SELECT * FROM ( ");
			sqlbuff.append("SELECT 'EQUIPMENTTYPE' EQUIPMENTTYPE, e.RecId GBCODE, g.GroupName GROUPNAME, e.title NAME, e.EquipmentType TYPE, e.ServerAddress IP FROM Equipment e, EccGroup g WHERE e.GroupId=g.RecId ");
			sqlbuff.append("UNION ");
			sqlbuff.append("SELECT 'VIDEOPOINTINFO' EQUIPMENTTYPE, VIDEOFLAG GBCODE, '' GROUPNAME, VIDEONAME NAME, '摄像机' TYPE, IPADDRESS IP FROM VIDEOPOINTINFO ");
			sqlbuff.append("UNION ");
			sqlbuff.append("SELECT 'SERVERINFO' EQUIPMENTTYPE, SERVERFLAG GBCODE, '' GROUPNAME, SERVERNAME NAME, IPLACE TYPE, SERVERIP IP FROM SERVERINFO ");
			sqlbuff.append(")ss WHERE GBCODE NOT IN (SELECT GBCode GBCODE FROM HardwareAssets WHERE GBCode IS NOT NULL AND GBCode != '') ");

			if(ORDERBY != null && !"".equals(ORDERBY))
				sqlbuff.append(String.format("ORDER BY %s ", ORDERBY));
			else
				sqlbuff.append("ORDER BY GBCODE ");
			
			
			if (FROM != null && !"".equals(FROM) && TO != null && !"".equals(TO)) {
				sqlbuff.append(String.format("limit %s,%s", FROM, TO));
			}
			
			try {
				DataTable dt = api.get_NativeSQLSupportService().ExecuteNativeSQLQuery(sqlbuff.toString(), null);
				
				for(DataRow dr:dt.get_Rows()){
					HashMap<String,String> map = new HashMap<String,String>();
					map.put("EQUIPMENTTYPE", ifNullToEmpty(dr.get_Item("EQUIPMENTTYPE")));
					map.put("GBCODE", ifNullToEmpty(dr.get_Item("GBCODE")));
					map.put("GROUPNAME", ifNullToEmpty(dr.get_Item("GROUPNAME")));
					map.put("NAME", ifNullToEmpty(dr.get_Item("NAME")));
					map.put("TYPE", ifNullToEmpty(dr.get_Item("TYPE")));
					map.put("IP", ifNullToEmpty(dr.get_Item("IP")));
					map.put("STATUS", ifNullToEmpty(dr.get_Item("STATUS"))); // 0:bind,1:unbind
					
					listMap.add(map);
				}
				
			} catch (SiteviewException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//		}
			
		List<Map<String,String>> result = new ArrayList<Map<String,String>>();
		
		Map<String,String>  valueMap = new HashMap<String,String>();
		valueMap.put("UNBIND_EQUIPMENTS",  JSONArray.fromObject(listMap).toString());
		valueMap.put("COUNT", COUNT);
		result.add(valueMap);
		return result;
	}

	private String ifNullToEmpty(Object object)
	{
		return object == null ? "" : object.toString();
	}
	
}
