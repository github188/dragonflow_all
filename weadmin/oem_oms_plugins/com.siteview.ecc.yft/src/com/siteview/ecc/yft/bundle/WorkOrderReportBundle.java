package com.siteview.ecc.yft.bundle;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

import org.eclipse.swt.widgets.Composite;

import siteview.IAutoTaskExtension;
import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.Api.ISiteviewApi;

import com.siteview.ecc.yft.bean.JsonValueProcessorImpl;
import com.siteview.ecc.yft.bean.JsonValueProcessorImplTest;
import com.siteview.ecc.yft.bean.WorkOrderReport;
import com.siteview.ecc.yft.es.WorkOrderReportYFT;
import com.siteview.ecc.yft.report.InitReport;
import com.siteview.utils.date.DateUtils;
import com.siteview.utils.db.DBUtils;
import com.siteview.utils.text.TextUtils;

public class WorkOrderReportBundle implements IAutoTaskExtension {
	private ISiteviewApi api;

	public WorkOrderReportBundle() {
	}

	@Override
	public String run(Map<String, Object> params) throws Exception {
		api = (ISiteviewApi) params.get("_CURAPI_");
		Calendar currentCalendar = Calendar.getInstance();
		int month = currentCalendar.get(Calendar.MONTH) + 1; // 月份
		int dayofmonth = currentCalendar.get(Calendar.DAY_OF_MONTH);
		int day = currentCalendar.get(Calendar.DAY_OF_WEEK);// 1=周天 2=周一..
		currentCalendar.add(Calendar.DAY_OF_MONTH, -1); // 减一天
		// 当天结束时间
		Calendar nowCalendar = (Calendar) currentCalendar.clone();
		nowCalendar.set(Calendar.HOUR_OF_DAY, 23);
		nowCalendar.set(Calendar.MINUTE, 59);
		nowCalendar.set(Calendar.SECOND, 59);
		List<WorkOrderReport> uploadOrder = new ArrayList<WorkOrderReport>(); //需要上传的数据
		WorkOrderReport orderReport = null;
		// 周一统计,周报
		if (day == 2) {
			// 本周前开始时间
			Calendar weekAgoCalendar = Calendar.getInstance();
			weekAgoCalendar.add(Calendar.DAY_OF_MONTH, -7);
			weekAgoCalendar.set(Calendar.HOUR_OF_DAY, 0);
			weekAgoCalendar.set(Calendar.MINUTE, 0);
			weekAgoCalendar.set(Calendar.SECOND, 0);
			System.out.println(weekAgoCalendar.getTime() + ":" + nowCalendar.getTime());
			orderReport = saveData(DateUtils.formatDefaultDate(weekAgoCalendar.getTime()), nowCalendar.getTime(),
					"yftitoss", "workOrderWeekly");
			if(orderReport != null)
				uploadOrder.add(orderReport);
		}
		if (dayofmonth == 1) {
			Calendar monthAgoCalendar = Calendar.getInstance();
			monthAgoCalendar.add(Calendar.MONTH, -1);
			monthAgoCalendar.set(Calendar.HOUR_OF_DAY, 0);
			monthAgoCalendar.set(Calendar.MINUTE, 0);
			monthAgoCalendar.set(Calendar.SECOND, 0);
			System.out.println(monthAgoCalendar.getTime() + ":" + nowCalendar.getTime());

			orderReport = saveData(DateUtils.formatDefaultDate(monthAgoCalendar.getTime()), nowCalendar.getTime(),
					"yftitoss", "workOrderMonthly");
			if(orderReport != null)
				uploadOrder.add(orderReport);

			// Quarterly季报
			if (month == 4 || month == 7 || month == 10 || month == 1) {
				Calendar quarterlyCalendar = Calendar.getInstance();
				quarterlyCalendar.add(Calendar.MONTH, -3);
				quarterlyCalendar.set(Calendar.HOUR_OF_DAY, 0);
				quarterlyCalendar.set(Calendar.MINUTE, 0);
				quarterlyCalendar.set(Calendar.SECOND, 0);
				System.out.println(quarterlyCalendar.getTime() + ":" + nowCalendar.getTime());

				orderReport = saveData(DateUtils.formatDefaultDate(quarterlyCalendar.getTime()), nowCalendar.getTime(),
						"yftitoss", "workOrderQuarterly");
				if(orderReport != null)
					uploadOrder.add(orderReport);
			}

			// 年报
			if (month == 1) {
				Calendar yearCalendar = Calendar.getInstance();
				yearCalendar.add(Calendar.YEAR, -1);
				yearCalendar.set(Calendar.HOUR_OF_DAY, 0);
				yearCalendar.set(Calendar.MINUTE, 0);
				yearCalendar.set(Calendar.SECOND, 0);

				// 从es里面读取并计算
				orderReport = WorkOrderReportYFT.calculateWorkOrderReport("workOrderMonthly",
						yearCalendar.getTime().getTime(), nowCalendar.getTime());
				if (orderReport != null) {
					orderReport.setType("workOrderYearly");
					uploadOrder.add(orderReport);
					WorkOrderReportYFT.saveData("yftitoss", "workOrderYearly", orderReport);
				}
//						WorkOrderReportYFT.calculateReport("workOrderMonthly",
//								yearCalendar.getTime().getTime(), nowCalendar.getTime()));
//				WorkOrderReportYFT.calculateReport("workOrderMonthly",
//						DateUtils.formatDefaultDate(yearCalendar.getTime()), nowCalendar.getTime()));
			}
		}
		if(uploadOrder.size() > 0 && InitReport.map.get("the_parent_domain") != null)
			sendReport(uploadOrder,"http://"+InitReport.map.get("the_parent_domain")+"/workorderUpload");
		return null;
	}

	/**
	 * 上传数据
	 * @param upload
	 * @param urlAddress
	 * @throws IOException
	 */
	public void sendReport(List<WorkOrderReport> upload, String urlAddress) throws IOException {
		URL url = new URL(urlAddress);
		HttpURLConnection httpurlconnection = (HttpURLConnection) url.openConnection();//
		httpurlconnection.setRequestMethod("POST");
		httpurlconnection.setDoOutput(true);
		httpurlconnection.setRequestProperty("Accept-Language", "utf-8");
		// 设置 HttpURLConnection的字符编码
		httpurlconnection.setRequestProperty("Accept-Charset", "UTF-8");
		httpurlconnection.setRequestProperty("Accept-Encoding", "UTF-8");
		httpurlconnection.setRequestProperty("Content-Type", "text/html");
		httpurlconnection.connect();
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.registerJsonValueProcessor(java.util.Date.class, new JsonValueProcessorImpl());
		JSONArray jsonObject = JSONArray.fromObject(upload, jsonConfig);
		DataOutputStream outStream = new DataOutputStream(httpurlconnection.getOutputStream());
		outStream.write(jsonObject.toString().getBytes("utf-8"));
		outStream.flush();
		InputStream input = httpurlconnection.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input, "utf-8"));
		String line = null;
		StringBuffer sb = new StringBuffer();
		while ((line = reader.readLine()) != null) {
			sb.append(line);
			// System.out.println(line);
		}
		if (reader != null) {
			outStream.close();
			reader.close();
		}
		if (httpurlconnection != null) {
			httpurlconnection.disconnect();
		}
	} 
	/**
	 * 保存数据
	 * 
	 * @param stime
	 * @param etime
	 * @param index
	 * @param type
	 */
	private WorkOrderReport saveData(String stime, Date etime, String index, String type) {
		return WorkOrderReportYFT.saveData(index, type, getWeekOrderReport(type,stime, etime));
	}

	/**
	 * 获取工单周报数据
	 */
	private WorkOrderReport getWeekOrderReport(String type,String sTime, Date eDate) {
		String area = InitReport.map.get("the_system_name");
		String areacode = InitReport.map.get("the_system_code");
		String parea = InitReport.map.get("the_parent_name") == null ? "" : InitReport.map
				.get("the_parent_name");
		String pareacode = InitReport.map.get("the_parent_code") == null ? "" : InitReport.map
				.get("the_parent_code");
		WorkOrderReport orderReport = new WorkOrderReport();
//		Map<String, Object> mapping = new HashMap<String, Object>();
		// 周新建工单数
		String eTime = DateUtils.formatDefaultDate(eDate);
		String allSql = "SELECT COUNT(*) allCount,1 num FROM WorkOrderCommon WHERE CreatedDateTime >= '"
				+ sTime + "' AND CreatedDateTime<='" + eTime + "'";
		String gbSql = "SELECT COUNT(*) gbCount,1 num FROM WorkOrderCommon WHERE Status='gb' AND CompleteTime >= '"
				+ sTime + "' AND CompleteTime<='" + eTime + "'";
		String time = DateUtils.formatDefaultDate(new Date());
		String timeoutSql = "SELECT COUNT(*) timeOutCount,1 num FROM WorkOrderCommon WHERE (TimeOutNumber > 0 OR ((Status != 'wc' AND Status != 'gb') AND (AppointmentTime < '"+time+"' AND DispatchNumber > 0))) AND LastModDateTime >= '"
				+ sTime + "' AND LastModDateTime<='" + eTime + "'";
		String ngbTimeoutSql = "SELECT COUNT(*) ngbTimeoutCount,1 num FROM WorkOrderCommon WHERE (Status!='gb' AND Status != 'wc') AND (TimeOutNumber > 0 OR ((Status != 'wc' AND Status != 'gb') AND (AppointmentTime < '"+time+"' AND DispatchNumber > 0))) AND LastModDateTime >= '"
				+ sTime + "' AND LastModDateTime<='" + eTime + "'"; // 超时未完成
		String gbTimeoutSql = "SELECT COUNT(*) gbTimeoutCount,1 num FROM WorkOrderCommon WHERE Status='gb' AND TimeOutNumber > 0 AND CompleteTime >= '"
				+ sTime + "' AND CompleteTime<='" + eTime + "'";
		String sql = "SELECT g.allCount,g.gbCount,g.timeOutCount,g.gbTimeoutCount,h.ngbTimeoutCount FROM ("
				+ ngbTimeoutSql
				+ ")h LEFT JOIN (SELECT e.allCount,e.gbCount,e.timeOutCount,f.gbTimeoutCount,1 num FROM ("
				+ gbTimeoutSql
				+ ")f LEFT JOIN (SELECT c.allCount,c.gbCount,d.timeOutCount,1 num FROM "
				+ "(SELECT a.allCount,b.gbCount,1 num FROM ("
				+ allSql
				+ ")a LEFT JOIN ("
				+ gbSql
				+ ")b ON a.num = b.num)c LEFT JOIN ("
				+ timeoutSql
				+ ")d ON c.num=d.num)e"
				+ " ON e.num = f.num)g ON g.num = h.num";
		DataRow row = DBUtils.getDataRow(api, sql);
		if (row != null) {
			String all = row.get("allCount").toString(); // 周新建工单数
			String gb = row.get("gbCount").toString(); // 周完成工单数
			String timeOut = row.get("timeOutCount").toString(); // 周超时工单数
			String gbtimeOut = row.get("gbTimeoutCount").toString(); // 周超时且完成工单数
			String ngbtimeOut = row.get("ngbTimeoutCount").toString(); // 周超时且没完成工单数

			String completionRate = "0"; // 周完成率：一周内工单状态为关闭的工单数/一周内新建的工单数
			String timeoutCompletionRate = "0"; // 超时完成率：一周内超时了的并且状态为关闭的工单数/一周内超时的工单数
			String timeoutNoCompletionRate = "0"; // 超时未完成率：一周内超时了的并且状态不为关闭的工单数/一周内超时的工单数
			String mTimeAvg = "0"; // 平均维护时长：一周内状态为完成的工单总耗时/一周内的完成了的工单数
			if (!"0".equals(all)) {
				completionRate = TextUtils.format(Float.parseFloat(gb) / Float.parseFloat(all), 4);
			}
			if (!"0".equals(timeOut)) {
				timeoutCompletionRate = TextUtils.format(
						Float.parseFloat(gbtimeOut) / Float.parseFloat(timeOut), 4);
				timeoutNoCompletionRate = TextUtils.format(
						Float.parseFloat(ngbtimeOut) / Float.parseFloat(timeOut), 4);
			}
			int gbNum = Integer.parseInt(gb);
			if (gbNum != 0)
				mTimeAvg = maintenanceTimeAvg(sTime, eTime, Integer.parseInt(gb));

//			mapping.put("newtotal", Long.parseLong(all));// 新建工单总数
//			mapping.put("completecount", Long.parseLong(gb));// 完成工单数
//			mapping.put("completerate", Double.parseDouble(completionRate));// 完成工单率
//			mapping.put("avgduration", Double.parseDouble(mTimeAvg));// 平均维护时长
//			mapping.put("outcompleterate", Double.parseDouble(timeoutCompletionRate));// 超时完成率
//			mapping.put("outnocompleterate", Double.parseDouble(timeoutNoCompletionRate));// 超时未完成率
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(eDate);
			calendar.add(Calendar.HOUR_OF_DAY, -2);
//			mapping.put("date", calendar.getTime());
			
			orderReport.setNewtotal(Long.parseLong(all));// 新建工单总数
			orderReport.setCompletecount(Long.parseLong(gb));// 完成工单数
			orderReport.setCompleterate(Double.parseDouble(completionRate));// 完成工单率
			orderReport.setAvgduration(Double.parseDouble(mTimeAvg));// 平均维护时长
			orderReport.setOutcompleterate(Double.parseDouble(timeoutCompletionRate));// 超时完成率
			orderReport.setOutnocompleterate(Double.parseDouble(timeoutNoCompletionRate));// 超时未完成率
			orderReport.setDate(calendar.getTime());
			orderReport.setType(type);
			orderReport.setArea(area);
			orderReport.setAreacode(areacode);
			orderReport.setParentarea(parea);
			orderReport.setParentareacode(pareacode);
		}
		return orderReport;
	}

	/**
	 * 获取平均维护时长 一周内状态为完成的工单总耗时/一周内的完成了的工单数
	 * 
	 * @return
	 */
	private String maintenanceTimeAvg(String sTime, String eTime, int gbNum) {
		String sql = "SELECT * FROM WorkOrderCommon WHERE Status='gb' AND CreatedDateTime >= '" + sTime
				+ "' AND CreatedDateTime<='" + eTime + "'";
		DataTable dataTable = DBUtils.select(sql, api);
		float total = 0;
		for (DataRow row : dataTable.get_Rows()) {
			if (row.get("Workload") != null && !"".equals(row.get("Workload"))) {
				float num = Float.parseFloat(row.get("Workload").toString());
				total += num;
			}
		}
		return TextUtils.format(total / gbNum, 2);
	}

	@Override
	public boolean hasCustomUI() {
		return false;
	}

	@Override
	public void creatConfigUI(Composite parent, Map<String, String> params) {

	}

	@Override
	public Map<String, String> getConfig() {
		return null;
	}

}
