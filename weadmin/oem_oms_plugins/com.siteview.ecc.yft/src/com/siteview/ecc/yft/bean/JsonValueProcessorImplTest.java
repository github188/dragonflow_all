package com.siteview.ecc.yft.bean;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;
//json转换date数据
public class JsonValueProcessorImplTest implements  JsonValueProcessor{
	 private String format = "yyyy-MM-dd";  
	@Override
	public Object processArrayValue(Object value, JsonConfig arg1) {
		 String[] obj = {};  
	        if (value instanceof Date[]) {  
	            SimpleDateFormat sf = new SimpleDateFormat(format);  
	            Date[] dates = (Date[]) value;  
	            obj = new String[dates.length];  
	            for (int i = 0; i < dates.length; i++) {  
	                obj[i] = sf.format(dates[i]);  
	            }  
	        }  
	        return obj;  
	}

	@Override
	public Object processObjectValue(String key, Object value, JsonConfig arg2) {
		  if (value instanceof java.util.Date) {  
	            String str = new SimpleDateFormat(format).format((Date) value);  
	            return str;  
	        }  
	        return value.toString();  
	}
}