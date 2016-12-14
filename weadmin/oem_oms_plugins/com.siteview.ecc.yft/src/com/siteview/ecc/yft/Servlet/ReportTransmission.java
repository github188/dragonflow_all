package com.siteview.ecc.yft.Servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import com.siteview.alarm.bean.DepartmentalReports;
import com.siteview.ecc.yft.bean.JsonValueProcessorImpl;
import com.siteview.ecc.yft.bundle.YFTReport;
import com.siteview.ecc.yft.es.StorageReport;
import com.siteview.ecc.yft.es.VideoReport;
//报表接收接口
public class ReportTransmission extends HttpServlet {
	SimpleDateFormat simp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		BufferedReader buffered=req.getReader();
		String data="";
		String line = "";
		while((line = buffered.readLine())!=null){
			data+=line;
		}
		String code="";
		int i=0;
		 JsonConfig jsonConfig = new JsonConfig();  
		 jsonConfig.registerJsonValueProcessor(java.util.Date.class, new JsonValueProcessorImpl());
		Object[] obj=JSONArray.fromObject(data,jsonConfig).toArray();
		for(Object o:obj){
			if(o instanceof JSONObject){
				JSONObject json=(JSONObject) o;
				i++;
				DepartmentalReports dep=new DepartmentalReports();
				try {
					Date datetime=simp.parse(json.getString("date"));
					dep.setDate(datetime);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if(code.length()==0)
					code=json.getString("area");
				dep.setAlarmcount(json.getInt("alarmcount"));
				dep.setAnomalycount(json.getInt("anomalycount"));
				dep.setAnomalyrate(json.getDouble("anomalyrate"));
				dep.setArea(json.getString("area"));
				dep.setAreacode(json.getString("areacode"));
				dep.setParentarea(json.getString("parentarea"));
				dep.setParentareacode(json.getString("parentareacode"));
				dep.setIntactcount(json.getInt("intactcount"));
				dep.setIntactrate(json.getDouble("intactrate"));
				dep.setNotlong(json.getLong("notlong"));
				dep.setOnlinecount(json.getInt("onlinecount"));
				dep.setOnlinerate(json.getDouble("onlinerate"));
				dep.setTotal(json.getInt("total"));
				dep.setType(json.getString("type"));
				dep.setVideolosscount(json.getInt("videolosscount"));
				dep.setVideolossduration(json.getLong("videolossduration"));
				dep.setVideolossrate(json.getDouble("videolossrate"));
				if(dep.getType().startsWith("video")){
					VideoReport.saveVideoDaily(dep, "yftitoss", dep.getType(), dep.getDate());
				}else if(dep.getType().startsWith("storage")){
					StorageReport.saveVideoDaily(dep, "yftitoss", dep.getType(), dep.getDate());
				}
			}
		}
		resp.getOutputStream().write("good".getBytes("UTF-8"));
		resp.getOutputStream().flush();
		YFTReport.writeToTxtByFileWriter(code+"-video-"+i+"----"+simp.format(new Date())+"\n");
	}
}
