package com.siteview.workorder.odata.yft.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import com.siteview.ecc.yft.bean.JsonValueProcessorImpl;
import com.siteview.ecc.yft.bean.WorkOrderReport;
import com.siteview.ecc.yft.es.WorkOrderReportYFT;
import com.siteview.utils.date.DateUtils;

public class WorkOrderUploadServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		req.setCharacterEncoding("UTF-8");
		BufferedReader buffered = req.getReader();
		String data = "";
		String line = "";
		while ((line = buffered.readLine()) != null) {
			data += line;
		}
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.registerJsonValueProcessor(java.util.Date.class, new JsonValueProcessorImpl());
		Object[] obj = JSONArray.fromObject(data, jsonConfig).toArray();
		for (Object o : obj) {
			if (o instanceof JSONObject) {
				JSONObject json = (JSONObject) o;
				WorkOrderReport order = new WorkOrderReport();
				try {
					order.setDate(DateUtils.parseDefaultDate(json.getString("date")));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				order.setNewtotal(json.getLong("newtotal"));
				order.setCompletecount(json.getLong("completecount"));
				order.setAvgduration(json.getDouble("avgduration"));
				order.setArea(json.getString("area"));
				order.setAreacode(json.getString("areacode"));
				order.setParentarea(json.getString("parentarea"));
				order.setParentareacode(json.getString("parentareacode"));
				order.setOutcompleterate(json.getDouble("outcompleterate"));
				order.setOutnocompleterate(json.getDouble("outnocompleterate"));
				order.setType(json.getString("type"));
				WorkOrderReportYFT.saveData("yftitoss", order.getType(), order);
			}
		}
	}

}
