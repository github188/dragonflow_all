package com.siteview.ecc.yft.report;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.IAuthenticationBundle;
import Siteview.PasswordModel;
import Siteview.Api.ISiteviewApi;
import Siteview.Api.SiteviewApi;

import com.siteview.ecc.yft.bean.ServerDetail;
import com.siteview.utils.au.GetCount;
import com.siteview.utils.date.DateUtils;
import com.siteview.utils.db.DBUtils;
import com.siteview.utils.text.TextUtils;

public class ServerPerDayReport {
	/**
	 * 保存所有非视频类数据
	 * @param api
	 * @param startTime
	 * @param eDate
	 */
	public static List<ServerDetail> setAllServerData(ISiteviewApi api, String index, String type,String startTime, Date eDate) {
		return ServerUtils.setData(index, type, getServerDetails(api, type, startTime, eDate), eDate);
	}

	/**
	 * 获取非视频类日报数据
	 * @param api
	 * @param startTime
	 * @param startTime 
	 * @param eDate
	 * @return
	 */
	public static List<ServerDetail> getServerDetails(ISiteviewApi api, String type, String startTime, Date eDate) {
		List<ServerDetail> servers = new ArrayList<ServerDetail>();
		servers.add(getServerData(api, type, "服务器", "server", startTime, eDate));
		servers.add(getServerData(api, type, "网络设备", "network", startTime, eDate));
		servers.add(getServerData(api, type, "防火墙", "firewall", startTime, eDate));
		servers.add(getServerData(api, type, "数据库", "database", startTime, eDate));
		return servers;
	}
	
	/**
	 * 日报
	 * @param api
	 * @param startTime
	 * @param eDate
	 */
	public static List<ServerDetail> setServerDailyData(ISiteviewApi api, String startTime, Date eDate){
		return setAllServerData(api, "yftitoss", "serverDaily", startTime, eDate);
	}
	
	/**
	 * 获取非视频类数据对象
	 * 
	 * @param api
	 * @param eqType
	 *          根据设备类型查询结果
	 * @param eqTypeEn
	 *          保存的设备类型英文
	 * @param startTime
	 * @param startTime2 
	 * @param eDate
	 * @return
	 */
	public static ServerDetail getServerData(ISiteviewApi api, String type, String eqType, String eqTypeEn,
			String startTime, Date eDate) {
		String area = InitReport.map.get("the_system_name");
		String areacode = InitReport.map.get("the_system_code");
		String parea = InitReport.map.get("the_parent_name") == null ? "" : InitReport.map
				.get("the_parent_name");
		String pareacode = InitReport.map.get("the_parent_code") == null ? "" : InitReport.map
				.get("the_parent_code");
		String endTime = DateUtils.formatDefaultDate(eDate);
		long total = GetCount.getEqNum(api, eqType, null, endTime);
		Map<String, String> map = getGoodAndOnlineNum(api, eqType, startTime, endTime);

		ServerDetail server = new ServerDetail();
		long goodCount = Long.parseLong(map.get("goodCount"));
		long onlineCount = Long.parseLong(map.get("onlineCount"));

		server.setTotal(total);
		server.setIntactcount(goodCount);
		server.setOnlinecount(onlineCount);
		double goodRate = 0;
		double onlineRate = 0;
		if (total != 0) {
			goodRate = TextUtils.formatDouble((double) goodCount / (double) total, 4);
			onlineRate = TextUtils.formatDouble((double) onlineCount / (double) total, 4);
		}
		server.setIntactrate(goodRate);
		server.setOnlinerate(onlineRate);
		server.setServertype(eqTypeEn);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(eDate);
		calendar.add(Calendar.HOUR_OF_DAY, -2);
		server.setDate(calendar.getTime());
		server.setType(type);
		server.setArea(area);
		server.setAreacode(areacode);
		server.setParentarea(parea);
		server.setParentareacode(pareacode);
		return server;
	}
public static void main(String[] args) throws Exception {
	ISiteviewApi foundationApi = SiteviewApi.get_CreateInstance("/E:/itoss_rap_20150826/.metadata/.plugins/org.eclipse.pde.core/.bundle_pool/../../../../itsm/com.siteview.businessstore/config/[@]/E:/itoss_rap_20150826/.metadata/.plugins/org.eclipse.pde.core/.bundle_pool/../../../../itsm/com.siteview.businessstore.weadmin/h2df/itoss");
	foundationApi.Connect("{Common}workorder");
	IAuthenticationBundle authentication = foundationApi.GetAuthenticationBundle();
	authentication.set_UserType("User");
	authentication.set_AuthenticationId("admin");
	authentication.set_PasswordModel(PasswordModel.SKIPPASSWORD);
	foundationApi.Login(authentication);
	String starttime = "2016-2-21";
	Date startdate = null;
	Date enddate = null;
	String endtime = starttime;
	startdate = DateUtils.parseDefaultDate(starttime + " 00:00:00");
	enddate = DateUtils.parseDefaultDate(endtime + " 23:59:59");
	startdate = DateUtils.parseDefaultDate(DateUtils.getWeekStartTime(startdate));
	enddate = DateUtils.parseDefaultDate(DateUtils.getWeekEndTime(startdate));
	String startTime = DateUtils.formatDefaultDate(startdate);
	String endTime = DateUtils.formatDefaultDate(enddate);
	long total = GetCount.getEqNum(foundationApi, "服务器", null, endTime);
	Map<String, String> map = getGoodAndOnlineNum(foundationApi, "服务器", startTime, endTime);
//	long total = 5;
	long goodCount = 4;
	long onlineCount = 4;
	double goodRate = 0;
	double onlineRate = 0;
	if (total != 0) {
		goodRate = TextUtils.formatDouble((double) goodCount / (double) total, 2);
		onlineRate = TextUtils.formatDouble((double) onlineCount / (double) total, 2);
	}
	System.out.println(goodRate+":"+onlineRate);
}
	/**
	 * 获取资源在线数和完好数
	 * 
	 * @param api
	 * @param eqType
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static Map<String, String> getGoodAndOnlineNum(ISiteviewApi api, String eqType,
			String startTime, String endTime) {
		Map<String, String> map = new HashMap<String, String>();
		String sql = "SELECT a.goodCount,b.onlineCount FROM (SELECT COUNT(*) goodCount,1 num FROM Equipment WHERE RecId NOT IN (SELECT EquipmentRecId FROM AlarmEvent"
				+ " where CreatedDateTime>'%s' and LastModDateTime<'%s' )"
				+ " AND EquipmentType IN (SELECT EqName FROM EquipmentTypeRel WHERE ParentID IN (SELECT RecId FROM EquipmentTypeRel WHERE typeAlias='%s'))) a"
				+ " LEFT JOIN "
				+ " (SELECT COUNT(*) onlineCount,1 num FROM Equipment WHERE RecId NOT IN ("
				+ "SELECT EquipmentRecId FROM AlarmEventLog WHERE MonitorType = 'Ping' "
				+ " and CreatedDateTime>'%s' and LastModDateTime<'%s' )"
				+ " AND EquipmentType IN (SELECT EqName FROM EquipmentTypeRel WHERE ParentID IN (SELECT RecId FROM EquipmentTypeRel WHERE typeAlias='%s')))b ON a.num = b.num";
		DataTable dataTable = DBUtils.select(
				String.format(sql,startTime,endTime,eqType,startTime,endTime, eqType), api);
		String goodCount = "0";
		String onlineCount = "0";
		for (DataRow row : dataTable.get_Rows()) {
			goodCount = row.get("goodCount").toString();
			onlineCount = row.get("onlineCount").toString();
		}
		map.put("goodCount", goodCount);
		map.put("onlineCount", onlineCount);
		return map;
	}

	/**
	 * 获取不可用时长
	 * 
	 * @param api
	 * @param eqType
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws ParseException
	 */
	public static long getUnavailableTimeNum(ISiteviewApi api, String eqType, String startTime,
			String endTime) throws ParseException {
		String sql = "SELECT * FROM Equipment WHERE RecId IN(SELECT * FROM AlarmEventLog WHERE MonitorType ='Ping' GROUP BY EquipmentRecId) "
				+ " AND EquipmentType IN (SELECT EqName FROM EquipmentTypeRel WHERE ParentID IN (SELECT RecId FROM EquipmentTypeRel WHERE typeAlias='%s')) ";
		DataTable dataTable = DBUtils.select(String.format(sql, eqType), api);
		long seconds = 0;
		for (DataRow row : dataTable.get_Rows()) {
			long time = new Date().getTime()
					- DateUtils.parseDefaultDate(row.get("CreatedDateTime").toString()).getTime();
			seconds += time / 1000;
		}
		return seconds;
	}
}
