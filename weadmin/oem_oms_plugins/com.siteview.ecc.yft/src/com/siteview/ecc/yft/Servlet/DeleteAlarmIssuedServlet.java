package com.siteview.ecc.yft.Servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.Api.ISiteviewApi;

import com.siteview.ecc.kernel.core.EccSchedulerManager;
import com.siteview.utils.db.DBQueryUtils;
//删除报警规则
public class DeleteAlarmIssuedServlet extends HttpServlet {
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	ISiteviewApi api;
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		api=EccSchedulerManager.getCurrentSiteviewApi();
		String machinetypeid="";
		String istwo=null;
		if(req.getParameter("id")==null){
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
					String[] ss=s.split("=");
					if(ss.length==2)
						hmap.put(ss[0], ss[1]);
					else
						hmap.put(ss[0], "");
				}
				machinetypeid=hmap.get("id");
				istwo=hmap.get("Istwo");
			}
		}else{
			machinetypeid=req.getParameter("id");
			istwo=req.getParameter("Istwo");
		}
		String param = "&id=" + machinetypeid +"&Istwo=1";
		String sql="delete from AlarmIssued where MachineTypeId='%s'";
		String sql1="delete  from AlarmIssuedRel where AlarmIssuedRecId "
				+ "not in (select RecId from AlarmIssued)";
		DBQueryUtils.UpdateorDelete(String.format(sql, machinetypeid), api);
		DBQueryUtils.UpdateorDelete(sql1, api);
		if(istwo==null){
			String sql0="select * from Organization";
			DataTable dt=DBQueryUtils.Select(sql0, api);
			for(DataRow dr:dt.get_Rows()){
				String o_host=dr.get("o_host")==null?"":dr.get("o_host").toString();
				if(o_host.length()>0&&!o_host.startsWith("localhost")
						&&!o_host.startsWith("127.0.0.1")){
					String url="http://"+o_host+"/deleteAlarmIssued";
					try{
						AlarmIssuedServlet.sendReport(url, param);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}
		resp.getOutputStream().print(true);
	}
}
