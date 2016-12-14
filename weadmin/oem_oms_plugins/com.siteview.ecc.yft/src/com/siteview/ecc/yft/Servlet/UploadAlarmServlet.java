package com.siteview.ecc.yft.Servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.siteview.ecc.kernel.core.EccSchedulerManager;

import Siteview.SiteviewException;
import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class UploadAlarmServlet extends HttpServlet{
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	SimpleDateFormat simp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		ISiteviewApi api=EccSchedulerManager.getCurrentSiteviewApi();
		BufferedReader buffered=req.getReader();
		String data="";
		String line = "";
		while((line = buffered.readLine())!=null){
			data+=line;
		}
		Object[] obj=JSONArray.fromObject(data).toArray();
		for(Object o:obj){
			if(o instanceof JSONObject){
				JSONObject json=(JSONObject) o;
				try {
					BusinessObject bo=api.get_BusObService().Create("AlarmUpload");
					bo.GetField("areaCode").SetValue(new SiteviewValue(json.getString("areacode")));
					bo.GetField("areaName").SetValue(new SiteviewValue(json.getString("area")));
					bo.GetField("parentAreaCode").SetValue(new SiteviewValue(json.getString("areacode")));
					bo.GetField("parentAreaName").SetValue(new SiteviewValue(json.getString("area")));
					bo.GetField("machineType").SetValue(new SiteviewValue(json.getString("machinetype")));
					bo.GetField("AlarmUploadId").SetValue(new SiteviewValue(bo.get_RecId()));
					bo.GetField("cycle").SetValue(new SiteviewValue(json.getInt("cycle")));
					bo.GetField("alarmAction").SetValue(new SiteviewValue(0));
					bo.SaveObject(api, true, true);
					Object[] jsona=json.getJSONArray("log").toArray();
					for(Object oo:jsona){
						if(oo instanceof JSONObject){
							BusinessObject bo1=api.get_BusObService().Create("AlarmUploadLog");
							bo1.GetField("AreaCode").SetValue(new SiteviewValue(json.getString("areacode")));
							bo1.GetField("AreaName").SetValue(new SiteviewValue(json.getString("area")));
							bo1.GetField("EquipmentIp").SetValue(new SiteviewValue(((JSONObject) oo).get("EquipmentIp")==null?"":((JSONObject) oo).get("EquipmentIp")));
							bo1.GetField("EquipmentName").SetValue(new SiteviewValue(((JSONObject) oo).get("EquipmentName")==null?"":((JSONObject) oo).get("EquipmentName")));
							bo1.GetField("MonitorStatus").SetValue(new SiteviewValue(((JSONObject) oo).get("MonitorStatus")==null?"":((JSONObject) oo).get("MonitorStatus")));
							bo1.GetField("MonitorValue").SetValue(new SiteviewValue(((JSONObject) oo).get("MonitorValue")==null?"":((JSONObject) oo).get("MonitorValue")));
							bo1.GetField("AlarmUploadId").SetValue(new SiteviewValue(bo.get_RecId()));
							bo1.SaveObject(api, true, true);
						}
					}
				} catch (SiteviewException e) {
					e.printStackTrace();
				}
			}
		}
		resp.getOutputStream().write("good".getBytes());
	}
}
