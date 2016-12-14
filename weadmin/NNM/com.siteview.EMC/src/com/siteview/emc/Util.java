package com.siteview.emc;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class Util {

	private static String errorMsg;
	private static boolean isExistError = false;

	public static String execCommand(String command) throws Exception {

		StringBuffer buffer = new StringBuffer();
		InputStreamReader inReader = null;
		StringBuffer errorStr = new StringBuffer();
		Process pro = null;
		try {
			pro = Runtime.getRuntime().exec(command);
			inReader = new InputStreamReader(pro.getInputStream());

			int i = -1;
			while ((i = inReader.read()) != -1) {
				buffer.append((char) i);
			}
			
			inReader.close();

			// 错误信息
			inReader = new InputStreamReader(pro.getErrorStream(), "utf-8");
			i = -1;
			while ((i = inReader.read()) != -1) {
				errorStr.append((char) i);
			}
			if (errorStr != null && !"".equals(errorStr.toString())) {
				errorMsg = errorStr.toString();
				isExistError = true;
			}

		} 
		catch (Exception e) {
			isExistError = true;
			throw e;
		} 
		finally {
			if (inReader != null) {
				inReader.close();
			}
			if(pro!=null){
				pro.destroy();
			}
		}
		return buffer.toString();
	}
	
	public static String readTxt(String filname){
		String encoding="GBK";
		StringBuffer buffer = new StringBuffer();
		try{
			File file=new File(filname);
			if(file.isFile() && file.exists()){
				InputStreamReader read = new InputStreamReader(new FileInputStream(file),encoding);
				//BufferedReader bufferedReader = new BufferedReader(read);	
				//String lineTxt = null;
				int i = -1;
//				while((lineTxt = bufferedReader.readLine()) != null){
//					buffer.append(lineTxt);
//				}
				while ((i = read.read()) != -1) {
					buffer.append((char) i);
				}
				read.close();
			}
			
		}catch(Exception e){
			
		}
		
		return buffer.toString();
	}
}
