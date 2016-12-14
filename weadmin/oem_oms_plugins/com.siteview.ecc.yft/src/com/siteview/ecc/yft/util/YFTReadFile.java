package com.siteview.ecc.yft.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import com.siteview.utils.path.PathUtils;

public class YFTReadFile {
//	static String filePath = PathUtils.getPath(Activator.PLUGIN_ID) +"organizationconfig.properties";
//	static String filePath_jar=System.getProperty("user.dir") + File.separator+"organizationconfig.properties";
	public static Map<String, String> getFileInfo() {
		try {
			return getESProp();
		} catch (Exception e) {
			e.printStackTrace();
			return new HashMap<String,String>();
		}
	}

	public static Map<String, String> getFileInfo(String filePath) {
		File file = new File(filePath);
		FileInputStream inStream = null;
		Properties proper = null;
		BufferedReader buff=null;
		Map<String, String> map = new HashMap<String, String>();
		try {
			inStream = new FileInputStream(file);
			 buff=new BufferedReader(new InputStreamReader(
					inStream,"utf-8"));
			proper = new Properties();
			proper.load(buff);
			Enumeration<Object> en = proper.keys();
			while (en.hasMoreElements()) {
				String key = (String) en.nextElement();
				String value = proper.get(key).toString();
				map.put(key, value);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(buff!=null)
				try {
					buff.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return map;
	}
	public static Map<String,String> getESProp() throws Exception{
		String userdir = System.getProperty("user.dir");
		File file = null;
		if(userdir.indexOf("eclipse")==-1){
			String configpath = userdir+"/config";
			String defaultpath = configpath+"/organizationconfig.properties";
			file = new File(defaultpath);
			File configdir = new File(configpath);
			if(!configdir.exists()){
				configdir.mkdirs();
			}
			if(!file.exists()){
				String path = PathUtils.getPath("com.siteview.ecc.yft","config/organizationconfig.properties");
				FileUtils.copyFile(new File(path),file);
			}
		}
		else{
			String path = PathUtils.getPath("com.siteview.ecc.yft","config/organizationconfig.properties");
			file = new File(path);
		}
		return getFileInfo(file.getPath());
	}
	
	public static void main(String[] args) {
		getFileInfo("c://organizationconfig.properties");
	}
}
