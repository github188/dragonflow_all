package com.siteview.ecc.yft.odata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import siteview.IFunctionExtension;
import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.SiteviewException;
import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;

public class GetYFTAssetBindAction implements IFunctionExtension{

	Map<String,String> typeMap = new HashMap<String,String>();
	Map<String,String> orgMap = new HashMap<String,String>();
	
	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api,
			Map<String, String> inputParamMap) {

		List<Map<String,String>> listMap = new ArrayList<Map<String,String>>();
		List<String> list = new ArrayList<String>();

		int sucessNum = 0;
		if(typeMap.size() == 0)
		{
			try {
				getProductTypeAndStatusMap(api);
			} catch (SiteviewException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
			
		String unBindList = inputParamMap.get("UNBINDLIST");
		if(unBindList == null || "".equals(unBindList))
		{
//			String[] typeArr = new String[]{"EQUIPMENTTYPE", "VIDEOPOINTINFO", "SERVERINFO"};
			
			try {
//				for(String str : typeArr)
//				{
					listMap = getAllUnbindEq(api, listMap, "");
//				}
				sucessNum = listMap.size();
			
				sucessNum = batchBind(api, listMap, list, sucessNum);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else
		{
			String[] arr = unBindList.split(",");
			String sql = createBatchQuerySql(arr);
			DataTable dt = null;
			try {
				dt = api.get_NativeSQLSupportService().ExecuteNativeSQLQuery(sql, null);
				listMap = parseDataTable(dt, listMap);
			
				sucessNum = listMap.size();
				sucessNum = batchBind(api, listMap, list, sucessNum);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		List<Map<String,String>> resultList = new ArrayList<Map<String,String>>();
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("SUCCESS_TOTAL", listMap.size() - sucessNum + "");
		map.put("FAILURE_EQUIPMENT_LIST", JSONArray.fromObject(list).toString());
		System.out.println(map.get("SUCCESS_TOTAL") + "-------" + map.get("FAILURE_EQUIPMENT_LIST"));
		resultList.add(map);
		return resultList;
	}
	
	
	private String createBatchQuerySql(String[] arr)
	{
		StringBuffer sbuff = new StringBuffer();
		sbuff.append("(");
		int i = 0;
		for(String str : arr)
		{
			if(i != 0)
			{
				sbuff.append(",");
			}
			sbuff.append("'");
			sbuff.append(str);
			sbuff.append("'");
			i ++;
		}
		sbuff.append(")");
		
		StringBuffer sqlbuff = new StringBuffer();
		sqlbuff.append("SELECT * FROM ( ");
		sqlbuff.append("SELECT '' MODEL, 'EQUIPMENTTYPE' EQUIPMENTTYPE, RecId GBCODE, title NAME, EquipmentType TYPE, ServerAddress IP, '' AREAID FROM Equipment ");
		sqlbuff.append("UNION ");
		sqlbuff.append("SELECT BRAND MODEL, 'VIDEOPOINTINFO' EQUIPMENTTYPE, VIDEOFLAG GBCODE, VIDEONAME NAME, '摄像机' TYPE, IPADDRESS IP, CIVILCODE AREAID FROM VIDEOPOINTINFO ");
		sqlbuff.append("UNION ");
		sqlbuff.append("SELECT BRAND MODEL, 'SERVERINFO' EQUIPMENTTYPE, SERVERFLAG GBCODE, SERVERNAME NAME, IPLACE TYPE, SERVERIP IP, CIVILCODE AREAID FROM SERVERINFO ");
		sqlbuff.append(")ss WHERE GBCODE NOT IN (SELECT GBCode GBCODE FROM HardwareAssets WHERE GBCode IS NOT NULL AND GBCode != '' ) ");
		sqlbuff.append("AND GBCODE IN %s");
		
		return String.format(sqlbuff.toString(), sbuff.toString());
	}
	
	private int batchBind(ISiteviewApi api, List<Map<String,String>> listMap, List<String> list, int sucessNum)
	{
		for(Map<String, String> map : listMap)
		{
			try {
//				if((int) (Math.random() * 10) > 5)
//					throw new SiteviewException();
//				else
//				{
					createAssets(api, map);
					sucessNum --;
//				}
				
			} catch (SiteviewException e) {
				System.out.println("bindassets---create assets error");
				list.add(map.get("NAME"));
			}
		}
		return sucessNum;
	}
	
	
	private List<Map<String,String>> getAllUnbindEq(ISiteviewApi api, List<Map<String,String>> listMap, String type) throws SiteviewException
	{
		
		StringBuffer sqlbuff = new StringBuffer();
		sqlbuff.append("SELECT * FROM ( ");
		sqlbuff.append("SELECT '' MODEL, 'EQUIPMENTTYPE' EQUIPMENTTYPE, RecId GBCODE,  title NAME, EquipmentType TYPE, ServerAddress IP, '' AREAID FROM Equipment ");
		sqlbuff.append("UNION ");
		sqlbuff.append("SELECT BRAND MODEL, 'VIDEOPOINTINFO' EQUIPMENTTYPE, VIDEOFLAG GBCODE, VIDEONAME NAME, '摄像机' TYPE, IPADDRESS IP, CIVILCODE AREAID FROM VIDEOPOINTINFO ");
		sqlbuff.append("UNION ");
		sqlbuff.append("SELECT BRAND MODEL, 'SERVERINFO' EQUIPMENTTYPE, SERVERFLAG GBCODE, SERVERNAME NAME, IPLACE TYPE, SERVERIP IP, CIVILCODE AREAID FROM SERVERINFO ");
		sqlbuff.append(")ss WHERE GBCODE NOT IN (SELECT GBCode GBCODE FROM HardwareAssets WHERE GBCode IS NOT NULL AND GBCode != '') ");
		
		DataTable dt = api.get_NativeSQLSupportService().ExecuteNativeSQLQuery(sqlbuff.toString(),null);
		
		listMap = parseDataTable(dt, listMap);
				
//		}
		return listMap;
	}

	
	private List<Map<String,String>> parseDataTable(DataTable dt, List<Map<String,String>> listMap)
	{
		for(DataRow dr:dt.get_Rows()){
			HashMap<String,String> map = new HashMap<String,String>();
			map.put("EQUIPMENTTYPE", ifNullToEmpty(dr.get_Item("EQUIPMENTTYPE")));
			map.put("GBCODE", ifNullToEmpty(dr.get_Item("GBCODE")));
//			map.put("GROUPNAME", ifNullToEmpty(dr.get_Item("GROUPNAME")));
			map.put("NAME", ifNullToEmpty(dr.get_Item("NAME")));
			map.put("TYPE", ifNullToEmpty(dr.get_Item("TYPE")));
			map.put("IP", ifNullToEmpty(dr.get_Item("IP")));
			map.put("AREAID", ifNullToEmpty(dr.get_Item("AREAID")));
			map.put("MODEL", ifNullToEmpty(dr.get_Item("MODEL")));
//				map.put("STATUS", ifNullToEmpty(dr.get_Item("STATUS")));
			
			listMap.add(map);
		}
		return listMap;
	}
	
	
	private String ifNullToEmpty(Object object)
	{
		return object == null ? "" : object.toString();
	}
	
	
	private void createAssets(ISiteviewApi api, Map<String, String> map) throws SiteviewException {
		BusinessObject hardwareBo = api.get_BusObService().Create("HardwareAssets");
		String gbcode = map.get("GBCODE");
		hardwareBo.GetField("GBCode").SetValue(new SiteviewValue(gbcode));
		hardwareBo.GetField("AssetsCode").SetValue(new SiteviewValue(map.get("GBCODE")));
		hardwareBo.GetField("AssetsName").SetValue(new SiteviewValue(map.get("NAME")));
		hardwareBo.GetField("IPAddress").SetValue(new SiteviewValue(map.get("IP")));
		//区域
		hardwareBo.GetField("AreaID").SetValue(new SiteviewValue(ifNullToEmpty(typeMap.get(map.get("AREAID")))));
		
		//状态
		hardwareBo.GetField("AssetStates").SetValue(new SiteviewValue(ifNullToEmpty(typeMap.get("使用中"))));
		
		//产品类型
		String eqType = map.get("EQUIPMENTTYPE");
		String type = map.get("TYPE");
		String productType = "";
		if("EQUIPMENTTYPE".equals(eqType))
		{
			if("Network".equals(type))
				productType = typeMap.get("Network");
			else
				productType = typeMap.get("SER");
		}else if("VIDEOPOINTINFO".equals(eqType))
		{	
			productType = typeMap.get("Video");
		}else
		{
			String code = gbcode.substring(10, 13);

			productType = typeMap.get(code);
			
//			if("118".equals(code))
//			{
//				productType = typeMap.get("NVR");
//			}else if ("111".equals(code))
//			{
//				productType = typeMap.get("DVR");
//			}else if ("210".equals(code))
//			{
//				productType = typeMap.get("IPSAN");
//			}else if ("113".equals(code))
//			{
//				productType = typeMap.get("CODER");
//			}
		}
		
		hardwareBo.GetField("ProductType").SetValue(new SiteviewValue(ifNullToEmpty(productType)));
		
		String orgID = "";
		if("EQUIPMENTTYPE".equals(eqType))
		{
			orgID = api.get_AuthenticationService().get_CurrentSecurityGroupId();
		}else
		{
			orgID = orgMap.get(map.get("AREAID"));
			
		}
		hardwareBo.GetField("UnitName").SetValue(new SiteviewValue(ifNullToEmpty(orgID)));
		hardwareBo.GetField("ProductModel").SetValue(new SiteviewValue(ifNullToEmpty(map.get("MODEL"))));
			
		hardwareBo.SaveObject(api, true, true);
	}
	
	
	
	public void getProductTypeAndStatusMap(ISiteviewApi api) throws SiteviewException
	{
		String sql = "SELECT CodeName NAME, RecId ID FROM ProductType UNION SELECT StatusName NAME, RecId ID FROM AssetsStatus UNION SELECT Code NAME, RecId ID FROM Area";
		
		DataTable dt = api.get_NativeSQLSupportService().ExecuteNativeSQLQuery(sql,null);
		
		for(DataRow dr:dt.get_Rows()){
			typeMap.put(ifNullToEmpty(dr.get_Item("NAME")), ifNullToEmpty(dr.get_Item("ID")));
		}
		
//		String sql2= "SELECT o_code NAME, RecId ID FROM Organization";
		String sql2= "SELECT o_code NAME, os.safegroup_id ID FROM Organization o, OrganizeAndSafeGroupRel os WHERE o.RecId=os.organize_id";
		
		dt = api.get_NativeSQLSupportService().ExecuteNativeSQLQuery(sql2,null);
		
		for(DataRow dr:dt.get_Rows()){
			String code = ifNullToEmpty(dr.get_Item("NAME"));
			int l = code.length();
			StringBuffer sb = new StringBuffer();
			sb.append(code);
			for(int i = 0; i < 8 - l; i ++)
			{
				sb.append("0");
			}
			orgMap.put(sb.toString(), ifNullToEmpty(dr.get_Item("ID")));
		}
	}
}
