package com.siteview.ecc.yft.bundle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;

import com.siteview.ecc.yft.es.AssetsEsConnectionConfig;
import com.siteview.ecc.yft.es.AssetsMaintenanceReportES;
import com.siteview.ecc.yft.es.AssetsReportES;
import com.siteview.ecc.yft.report.AssetsInfo;
import com.siteview.ecc.yft.report.AssetsMaintenanceReport;
import com.siteview.ecc.yft.report.AssetsStatisticsReport;

import Siteview.Api.ISiteviewApi;
import siteview.IAutoTaskExtension;

public class AutomaticGenerationAssetsReport implements IAutoTaskExtension {

	public AutomaticGenerationAssetsReport() {
	}

	@Override
	public String run(Map<String, Object> params) throws Exception {
		ISiteviewApi api = (ISiteviewApi) params.get("_CURAPI_");
		Calendar cal = Calendar.getInstance();
		// Date date = cal.getTime();
		int minute = cal.get(Calendar.MINUTE);
		int hour = cal.get(Calendar.HOUR);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int month = cal.get(Calendar.MONTH) + 1;
		int year = cal.get(Calendar.YEAR);
		// int w = cal.get(Calendar.DAY_OF_WEEK);
		// System.out.println(day);
		String startTime = "";
		String endTime = "";
		if (day == 1 && hour == 0 && minute == 0) {
			if (month == 1) {
				startTime = year-1 + "-" + "12-01 00:00:00";
			} else {
				if (month <= 10) {
					startTime = year + "-0" + (month - 1) + "-01 00:00:00";
				} else {
					startTime = year + "-" + (month - 1) + "-01 00:00:00";
				}

			}
		
			if (month < 10) {
					endTime = year + "-0" + month + "-01 00:00:00";
			} else {
				endTime = year + "-" + month + "-01 00:00:00";
			}
			System.out.println(startTime+"-----"+endTime);
			if (!"".equals(startTime) || !"".equals(endTime) && minute == 0) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date;
				try {
					date = sdf.parse(startTime);
					List<AssetsInfo> assetsInfos = AssetsStatisticsReport.getAssetsInfo(startTime, endTime, api);
					if (assetsInfos.size() > 0) {
						System.out.println(startTime + "开始生成资产月报表");
						AssetsReportES.SaveInfo(AssetsEsConnectionConfig.Es_Name, "assetsreportmonth", assetsInfos, date);
					}
					List<Map<String, String>> outList = new ArrayList<Map<String, String>>();
					outList = AssetsMaintenanceReport.getAssetsMaintenanceReportInfo(outList, startTime, endTime, api);
					System.out.println(outList.size());
					if (!outList.isEmpty()) {
						System.out.println(startTime + "开始生成维修月报表");
						AssetsMaintenanceReportES.SaveInfo(AssetsEsConnectionConfig.Es_Name, "maintenancereportmonth", outList, date);
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		if (month == 1 && day == 1 && hour == 0 && minute == 0) {
			startTime = (year - 1) + "-01-01 00:00:00";
			endTime = year + "-01-01 00:00:00";
			if (!"".equals(startTime) || !"".equals(endTime) && minute == 0) {
				System.out.println(startTime+"-----"+endTime);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date;
				try {
					date = sdf.parse(startTime);
					List<AssetsInfo> assetsInfos = AssetsStatisticsReport.getAssetsInfo(startTime, endTime, api);
					if (assetsInfos.size() > 0) {
						System.out.println(startTime + "开始生成资产年报表");
						AssetsReportES.SaveInfo(AssetsEsConnectionConfig.Es_Name, "assetsreportyear", assetsInfos, date);
					}
					List<Map<String, String>> outList = new ArrayList<Map<String, String>>();
					outList = AssetsMaintenanceReport.getAssetsMaintenanceReportInfo(outList, startTime, endTime, api);
					if (!outList.isEmpty()) {
						System.out.println(startTime + "开始生成维修年报表");
						AssetsMaintenanceReportES.SaveInfo(AssetsEsConnectionConfig.Es_Name, "maintenancereportyear", outList, date);
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}

		}

		return null;
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
