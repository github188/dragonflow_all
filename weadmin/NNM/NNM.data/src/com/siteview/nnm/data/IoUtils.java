package com.siteview.nnm.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.FrameworkUtil;






/**
 * 保存文件的工具类
 */
public class IoUtils {

	   private static String dirpath = null;
	   public static String getDirPath() {
			
			if(dirpath == null){
				 try {
					 dirpath = System.getProperty("user.dir")+"/flowSnmpException/";
//				    			FileLocator.toFileURL(
//				    			FrameworkUtil.getBundle(DBManage.class).getEntry("")).getPath();
				    }
				    catch (Exception e) {
				      e.printStackTrace();
				    }
				    System.out.println("dirpath----------->" + dirpath);
			}
			File file =new File(dirpath);    
			if  (!file.exists()  && !file.isDirectory())      
			{       
			    file.mkdir();    
			} 
		   
		    return dirpath;
		}
	public static boolean saveSnmpExceptionflow(List<String> excptionvalue){
		StringBuffer line = new StringBuffer("");
		for(String k:excptionvalue){
			
			line.append(k+"\r\n");
		}
		
		return writeData("SnmpExceptionflow.txt",line.toString());
	}
	
	public static boolean isave(){
		String path = getDirPath() + "SnmpExceptionflow.txt";
		File file = new File(path);
		if(file.exists()){
			return true;
		}
		return false;
		
	}
	public static List<String> ReadSnmpExceptionflow(){
		List<String> context=new ArrayList<String>();
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(getDirPath() + "SnmpExceptionflow.txt");
			br = new BufferedReader(fr);
			String line = "";
			while ((line = br.readLine()) != null) {
				context.add(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			;
			
		} catch (IOException e) {
			e.printStackTrace();
			;
		
		} finally {
			closeFileReader(fr, br);
		}
		return context;
	}
	/**
	 * 保存文件
	 * @param fileName 文件名，目录为当前项目根目录
	 * @param data 文件内容
	 * @return
	 */
	public static boolean writeData(String fileName,String data){
		//String path = getProductPath() + fileName; getDirPath
		String path = getDirPath() + fileName;
		File file = new File(path);
		if(file.exists()){
			file.delete();
		}
		FileWriter fw = null;
		try {
			fw = new FileWriter(file);
			fw.write(data);
			fw.flush();
		} catch (IOException e) {
			e.printStackTrace();
			;
			return false;
		} finally {
			if(fw!=null){
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
					;
					fw = null;
					return true;
				}
			}
			fw = null;
		}
		return true;
	}

	
	private static void closeFileReader(FileReader fr, BufferedReader br) {
		if (fr != null) {
			try {
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
				;
			}
			fr = null;
		}
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
				;
			}
			br = null;
		}
	}
	
	public static void main(String[] args) {
		FileReader fr = null;
		BufferedReader br = null;
		String name = "d:/test.txt";
		try {
			fr = new FileReader( name);
			br = new BufferedReader(fr);
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] vstr = line.split("\\[::\\]");
				System.out.println(vstr.length);
			}
		}catch (Exception e) {
		}
	}

	
	
}
