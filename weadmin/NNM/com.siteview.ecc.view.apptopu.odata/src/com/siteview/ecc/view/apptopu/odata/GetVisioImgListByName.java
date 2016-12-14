package com.siteview.ecc.view.apptopu.odata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siteview.visualview.editors.SvgVisioContainer;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.SiteviewException;

//import com.siteview.visualview.dao.AssetDao;
//import com.siteview.visualview.dao.EquipmentDao;
//import com.siteview.visualview.dao.MonitorDao;
//import com.siteview.visualview.dao.SvgDao;
//import com.siteview.visualview.dao.impl.AssetDaoImpl;
//import com.siteview.visualview.dao.impl.EquipmentDaoImpl;
//import com.siteview.visualview.dao.impl.MonitorDaoImpl;
//import com.siteview.visualview.dao.impl.SvgDaoImpl;
//import com.siteview.visualview.model.SvgVisioModel;
//import com.siteview.visualview.utils.JasonObjectUtils;

import Siteview.Api.ISiteviewApi;
import siteview.IFunctionExtension;

public class GetVisioImgListByName implements IFunctionExtension {

	public GetVisioImgListByName() {
		// TODO Auto-generated constructor stub
	}
	
	
	public static void main(String[] args)
	{
		System.out.println("12345678".substring(3, 6));
	}
	
	private String resource = System.getProperty("user.dir") + "/resource/visiopng/";
	
	
	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api, Map<String, String> inputParamMap) {
		// TODO Auto-generated method stub
		List<Map<String, String>> functionListMap = new ArrayList<Map<String, String>>();
		
		
		String sql = "SELECT * FROM svgtable ";
		
		String org = inputParamMap.get("ORGANIZATION");
		
		if(org != null)
		{
			sql = sql + "where ORGANIZATION='" + org + "'";
		}
		
		DataTable dt = null;
		try {
			dt = api.get_NativeSQLSupportService().ExecuteNativeSQLQuery(sql,null);
		} catch (SiteviewException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(DataRow dr : dt.get_Rows()){
			HashMap<String,String> map = new HashMap<String,String>();
			String id = dr.get_Item("RecId").toString();
			map.put("ID", id);
			String name = dr.get_Item("vname").toString();
			map.put("VNAME", name);
			map.put("SHOW_NAME",dr.get_Item("show_name").toString());
//			map.put("SVG",dr.get_Item("svg").toString());
			
			findSvgPng(id, dr.get_Item("svg").toString());
			String pngPath = "resource/visiopng/%s.png";
			map.put("PNG", String.format(pngPath, id));
			
			functionListMap.add(map);
		}
		return functionListMap;
	}
	
	
	private void findSvgPng(String name, String svg)
	{
		File dir = new File(resource);
		if(!dir.exists())
		{
			dir.mkdir();
		}
		
		File png = new File(resource, name + ".png");
		
		if(!png.exists())
		{
			InputStream in = SvgVisioContainer.convertSvgToPng(svg);
			if(in != null)
			{
				createFile(png, in);
			}
		}
		
	}
	
	
	public void createFile(File file, InputStream is) {
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		int ch = 0;
		try {
			while((ch=is.read()) != -1){
				fos.write(ch);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally{
			try {
				fos.close();
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	
	private String ifNullToEmpty(Object object)
	{
		return object == null ? "" : object.toString();
	}
}
