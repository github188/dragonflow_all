package com.siteview.ecc.yft.report;

import com.siteview.utils.db.DBQueryUtils;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.Api.ISiteviewApi;

public class PerHourReport {
	//摄像机每小时断线率
	public static double getPerHour(ISiteviewApi api){
		String sql="SELECT (v1.c2/v0.c) as cc from (SELECT count(*) c "
				+ "from VIDEOALARMRESULT) v0,(select count(*) c2 "
				+ "from VIDEOALARMRESULT where ONLINESTATUS=1)v1 "
				+ "where v1.c2>0 and v1.c2 is not NULL";
		DataTable dt=DBQueryUtils.Select(sql, api);
		double d=0.0;
		for(DataRow dr:dt.get_Rows()){
			String s=dr.get("cc")==null?"0":dr.get("cc").toString();
			d=Double.parseDouble(PerDayReport.df.format(Double.parseDouble(s)));
		}
		return d;
	}
}
