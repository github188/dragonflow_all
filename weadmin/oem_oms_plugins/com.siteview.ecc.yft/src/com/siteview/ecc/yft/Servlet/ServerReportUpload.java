package com.siteview.ecc.yft.Servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import com.siteview.ecc.yft.bean.JsonValueProcessorImpl;
import com.siteview.ecc.yft.bean.ServerDetail;
import com.siteview.ecc.yft.report.ServerUtils;
import com.siteview.utils.date.DateUtils;
import com.siteview.utils.organization.OrganizationUtil;

/**
 * 获取上传的非视频类数据
 * @author Administrator
 *
 */
public class ServerReportUpload extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		doPost(req, resp);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		req.setCharacterEncoding("UTF-8");
		BufferedReader buffered=req.getReader();
		String data="";
		String line = "";
		
		while((line = buffered.readLine())!=null){
			data+=line;
		}
		 JsonConfig jsonConfig = new JsonConfig();  
		 jsonConfig.registerJsonValueProcessor(java.util.Date.class, new JsonValueProcessorImpl());
		Object[] obj=JSONArray.fromObject(data,jsonConfig).toArray();
		for(Object o:obj){
			if(o instanceof JSONObject){
				JSONObject json=(JSONObject) o;
				ServerDetail server = new ServerDetail();
				try {
					server.setDate(DateUtils.parseDefaultDate(json.getString("date")));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				server.setTotal(json.getLong("total"));
				server.setOnlinecount(json.getLong("onlinecount"));
				server.setOnlinerate(json.getDouble("onlinerate"));
				server.setArea(json.getString("area"));
				server.setAreacode(json.getString("areacode"));
				server.setParentarea(json.getString("parentarea"));
				server.setParentareacode(json.getString("parentareacode"));
				server.setIntactcount(json.getLong("intactcount"));
				server.setIntactrate(json.getDouble("intactrate"));
				server.setUnavailablecount(json.getLong("unavailablecount"));
				server.setServertype(json.getString("servertype"));
				server.setType(json.getString("type"));
				ServerUtils.setData("yftitoss", server.getType(), server, server.getDate());
			}
		}
		resp.getOutputStream().write("good".getBytes("UTF-8"));
		resp.getOutputStream().flush();
	}

}
