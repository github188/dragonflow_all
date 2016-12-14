package com.siteview.ecc.yft.odata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.SiteviewException;
import Siteview.Api.ISiteviewApi;
import siteview.IFunctionExtension;

public class GetYFTGisLV implements IFunctionExtension {

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api, Map<String, String> inputParamMap) {
		// TODO Auto-generated method stub
		
		List<Map<String, String>> functionListMap = new ArrayList<Map<String, String>>();
		List<Map<String, String>> tempList = new ArrayList<Map<String, String>>();
		
		
		
		String sql = "SELECT * FROM DictionaryData where DictNo='tptjb'";
		
		
		DataTable dt = null;
		try {
			dt = api.get_NativeSQLSupportService().ExecuteNativeSQLQuery(sql,null);
		} catch (SiteviewException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String[][] arrList = new String[dt.get_Rows().size()][2];
		
		int a = 0;
		
		for(DataRow dr : dt.get_Rows()){
			HashMap<String,String> map = new HashMap<String,String>();
			String id = dr.get_Item("RecId").toString();
			map.put("ID", id);
			String name = dr.get_Item("DictDataName").toString();
			map.put("NAME", name);
			map.put("CODE",dr.get_Item("DictDataValue").toString());
//			map.put("SVG",dr.get_Item("svg").toString());
			String lv = dr.get_Item("DictDataDesc").toString();
			map.put("LV", lv);
			
			arrList[a] = new String[]{lv.split("-")[0], a+""};
			a ++;
			tempList.add(map);
		}
		
		for(int i = 0 ; i < arrList.length-1 ; i++){
			for(int j = i+1 ; j < arrList.length ; j++){
				String[] temp ;
				if(Integer.parseInt(arrList[i][0]) > Integer.parseInt(arrList[j][0])){
					temp = arrList[j];
					arrList[j] = arrList[i];
					arrList[i] = temp;
				}
			}
		}
		
		for(int i = 0; i < arrList.length; i ++)
		{
			functionListMap.add(tempList.get(Integer.parseInt(arrList[i][1])));
		}
		
		return functionListMap;
	}
	
}
