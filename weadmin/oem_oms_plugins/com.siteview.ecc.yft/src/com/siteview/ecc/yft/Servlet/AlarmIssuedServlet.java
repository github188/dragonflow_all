package com.siteview.ecc.yft.Servlet;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.SiteviewException;
import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;

import com.siteview.ecc.kernel.core.EccSchedulerManager;
import com.siteview.utils.db.DBQueryUtils;
//yft设备报警配置和下发接口
public class AlarmIssuedServlet extends HttpServlet {

	ISiteviewApi api;
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// MachineType,MachineTypeId,WhetherSend,Cycle,AlarmNumber,ResponseTime
		//MonitorTypeTable,MonitorTypeName
		req.setCharacterEncoding("UTF-8");
		Map<String,String> map=new HashMap<String,String>();
		api=EccSchedulerManager.getCurrentSiteviewApi();
		String machinetype="";
		String machinetypeid="";
		String whethersend="";
		String cycle="";
		String alarmNumber="";
		String responseTime="";
		String monitortype="";
		String updatetime="";
		Map<String,Object> pmap=new HashMap<String,Object>();
		if(req.getParameter("MachineType")==null){
			BufferedReader buffered=req.getReader();
			String data="";
			String line = "";
			while((line = buffered.readLine())!=null){
				data+=line;
			}
			if(data.length()>0){
				if(data.startsWith("&"))
					data=data.substring(1,data.length());
				String[] datas=data.split("&");
				Map<String,String> hmap=new HashMap<String,String>();
				for(String s:datas){
					if(s.contains("=")){
						hmap.put(s.substring(0,s.indexOf("=")), s.substring(s.indexOf("=")+1));
					}
				}
				machinetype=hmap.get("MachineType");
				machinetypeid=hmap.get("MachineTypeId");
				whethersend=hmap.get("WhetherSend");
				cycle=hmap.get("Cycle");
				alarmNumber=hmap.get("AlarmNumber");
				responseTime=hmap.get("ResponseTime");
				monitortype=hmap.get("AlarmIssuedRel");
				updatetime=hmap.get("UpdateTime");
			}
		}else{
			machinetype=req.getParameter("MachineType");
			machinetypeid=req.getParameter("MachineTypeId");
			whethersend=req.getParameter("WhetherSend");
			cycle=req.getParameter("Cycle");
			alarmNumber=req.getParameter("AlarmNumber");
			responseTime=req.getParameter("ResponseTime");
			monitortype=req.getParameter("AlarmIssuedRel");
			updatetime=req.getParameter("UpdateTime");
		}
		if(machinetype==null||machinetypeid==null||whethersend==null
				||cycle==null||alarmNumber==null||responseTime==null||monitortype==null){
			resp.getOutputStream().print("error:null");
			return;
		}
		pmap.put("MachineType", machinetype);
		pmap.put("MachineTypeId", machinetypeid);
		pmap.put("WhetherSend", whethersend);
		pmap.put("Cycle", cycle);
		pmap.put("AlarmNumber", alarmNumber);
		pmap.put("ResponseTime", responseTime);
		pmap.put("Istwo", "1");
		pmap.put("AlarmIssuedRel", monitortype);
		pmap.put("UpdateTime", updatetime);
		String param = "&MachineType=" + machinetype   
	            + "&MachineTypeId=" + machinetypeid   
	            + "&WhetherSend=" + whethersend   
	            + "&Cycle=" + cycle   
	            + "&AlarmNumber=" + alarmNumber   
	            + "&ResponseTime=" + responseTime
	            + "&Istwo=1"
	            +"&AlarmIssuedRel="+monitortype
	            +"&UpdateTime="+updatetime;  
		BusinessObject bo=null;
		try {
			 bo=DBQueryUtils.queryOnlyBo("MachineTypeId", machinetypeid, "AlarmIssued", api);
			 if(bo==null)
				 bo=api.get_BusObService().Create("AlarmIssued");
			 bo.GetField("MachineType").SetValue(new SiteviewValue(machinetype));
			 bo.GetField("MachineTypeId").SetValue(new SiteviewValue(machinetypeid));
			 bo.GetField("WhetherSend").SetValue(new SiteviewValue(whethersend));
			 bo.GetField("Cycle").SetValue(new SiteviewValue(cycle));
			 bo.GetField("AlarmNumber").SetValue(new SiteviewValue(alarmNumber));
			 bo.GetField("ResponseTime").SetValue(new SiteviewValue(responseTime));
			 bo.GetField("UpdateTime").SetValue(new SiteviewValue(updatetime));
			 bo.SaveObject(api,true, false, false);
			 String sql="delete  from AlarmIssuedRel where AlarmIssuedRecId='%s'";
			 DBQueryUtils.UpdateorDelete(String.format(sql, bo.get_RecId()), api);
			 sql="update Monitor set IsDisabled=1 where EccMonitorType in (";
			 String types="";
			 String[] monitortypes=monitortype.split(",");
				for(String s:monitortypes){
					if(s.contains("=")){
						String monitorTypeTable=s.substring(0,s.indexOf("="));
						String monitorTypeName=s.substring(s.indexOf("=")+1);
						BusinessObject borel=api.get_BusObService().Create("AlarmIssuedRel");
						borel.GetField("MonitorTypeTable").SetValue(new SiteviewValue(monitorTypeTable));
						borel.GetField("MonitorTypeName").SetValue(new SiteviewValue(monitorTypeName));
						borel.GetField("AlarmIssuedRecId").SetValue(new SiteviewValue(bo.get_RecId()));
						borel.SaveObject(api, true, true);
						if(monitorTypeTable.contains("EccMonitor."))
						monitorTypeTable=monitorTypeTable.substring(11);
						if(types.length()>0)
							types+=",";
						types+="'"+monitorTypeTable+"'";
					}
				}
			if(types.length()>0){
				sql+=types+")";
				DBQueryUtils.UpdateorDelete(sql, api);
			}
			if(req.getParameter("Istwo")==null){
				String sql0="select * from Organization";
				DataTable dt=DBQueryUtils.Select(sql0, api);
				for(DataRow dr:dt.get_Rows()){
					String o_host=dr.get("o_host")==null?"":dr.get("o_host").toString();
					if(o_host.length()>0&&!o_host.startsWith("localhost")
							&&!o_host.startsWith("127.0.0.1")){
						String url="http://"+o_host+"/alarmIssued";
						try{
							System.out.println(param);
							sendReport(url, param);
//							URLConnectionUtil.doGet(url, pmap);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}
			}
		} catch (SiteviewException e) {
			e.printStackTrace();
			resp.getOutputStream().print("error:null");
		}
		resp.getOutputStream().print(true);
	}
	public static void sendReport(String urlAddress,String param) throws IOException{
		URL url = new URL(urlAddress);
		HttpURLConnection httpurlconnection = (HttpURLConnection) url.openConnection();// 
		httpurlconnection.setRequestMethod("POST");
		httpurlconnection.setDoOutput(true);
		httpurlconnection.setRequestProperty("Accept-Language", "utf-8");
	    // 设置 HttpURLConnection的字符编码
		httpurlconnection.setRequestProperty("Accept-Charset", "UTF-8");
		httpurlconnection.setRequestProperty("Accept-Encoding", "UTF-8");
		
		httpurlconnection.setRequestProperty("Content-Type", "text/html,application/json");
		httpurlconnection.connect();
		DataOutputStream outStream = new DataOutputStream(httpurlconnection.getOutputStream());
		outStream.write(param.getBytes("utf-8"));
		outStream.flush();
		InputStream input = httpurlconnection.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input,"utf-8"));
		String line = null;
		StringBuffer sb = new StringBuffer();
		while ((line = reader.readLine()) != null) {
			sb.append(line);
//			System.out.println(line);
		}
		if (reader != null) {
			outStream.close();
			reader.close();
		}
		if (httpurlconnection != null) {
			httpurlconnection.disconnect();
		}
	} 
}
