package com.siteview.ecc.yft.odata.duty;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import siteview.IFunctionExtension;
import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.SiteviewException;
import Siteview.Api.ISiteviewApi;

import com.siteview.ecc.yft.bean.CommonObject;
import com.siteview.utils.date.DateUtils;
import com.siteview.utils.db.DBUtils;
import com.siteview.utils.html.StringUtils;

/**
 * 获取值班管理列表数据
 * 
 * @author Administrator
 *
 */
public class GetDutyManageListInfo implements IFunctionExtension {

	public GetDutyManageListInfo() {
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api, Map<String, String> inputParamMap) {
		List<Map<String, String>> outParam = new ArrayList<Map<String, String>>();
		Map<String, String> outmap = new HashMap<String, String>();
		String param = inputParamMap.get("PARAM");
		String dutyGroup = inputParamMap.get("DUTYGROUP_ID");
		List<Map<String, String>> userList = new ArrayList<Map<String, String>>();
		List<Map<String, String>> dutyTableList = new ArrayList<Map<String, String>>();
		try {
			//获取排班安排,DutyArrangements
			outmap.put("DUTY_ARRANGEMENTS", JSONArray.fromObject(getDutyArrangements(api)).toString());
			
			// 获取值班组下值班用户信息
			if (dutyGroup != null) {
				getDutyUsersFromDutyGroup(api, dutyGroup, userList);
				getDutyTablesFromDutyGroup(api, dutyGroup, dutyTableList);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		outmap.put("USER_LIST", JSONArray.fromObject(userList).toString());
		outmap.put("DUTYTABLE_LIST", JSONArray.fromObject(dutyTableList).toString());
		outParam.add(outmap);
		return outParam;
	}

	/**
	 * 获取排班安排
	 * @param api
	 * @return 
	 * @throws ParseException 
	 */
	private List<Map<String, String>> getDutyArrangements(ISiteviewApi api) throws ParseException {
		String sql = "SELECT * FROM DutyArrangements";
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		DataTable dataTable = DBUtils.select(sql, api);
		for (DataRow row : dataTable.get_Rows()) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("RecId", StringUtils.getNotNullStr(row.get("RecId")));
			map.put("Name", StringUtils.getNotNullStr(row.get("Name")));
			map.put("StartTime", DateUtils.formatHMSDate(DateUtils.parseHMSDate(row.get("StartTime").toString())));
			map.put("EndTime", DateUtils.formatHMSDate(DateUtils.parseHMSDate(row.get("EndTime").toString())));
			list.add(map);
		}
		return list;
	}

	/**
	 * 获取值班组下值班用户信息
	 * 
	 * @param api
	 * @param dutyGroup
	 *          值班组ID
	 * @param userList
	 */
	private void getDutyUsersFromDutyGroup(ISiteviewApi api, String dutyGroup,
			List<Map<String, String>> userList) {
		String usersql = "SELECT d.RecId,d.UserInfo,p.DisplayName FROM DutyDetail d,Profile p WHERE p.RecId=d.UserInfo AND d.DutyGroupId='%s'";
		DataTable userData = DBUtils.select(String.format(usersql, dutyGroup), api);
		for (DataRow row : userData.get_Rows()) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("RECID", row.get("RecId").toString());
			map.put("USER_ID", row.get("UserInfo").toString());
			map.put("USER_NAME", row.get("DisplayName").toString());
			userList.add(map);
		}
	}

	/**
	 * 获取值班组下排班信息
	 * 
	 * @param api
	 * @param dutyGroup
	 * @param dutyTableList
	 * @throws ParseException 
	 */
	private void getDutyTablesFromDutyGroup(ISiteviewApi api, String dutyGroup,
			List<Map<String, String>> dutyTableList) throws ParseException {
		String dutyTableSql = "SELECT dt.RecId dtId,da.RecId daId,dt.ApplyYearNumber,dt.DutyForm,dt.WeekField,dt.DayNumber,dt.StartDateTime,dt.EndDateTime,da.Name,da.StartTime,da.EndTime "
				+ "FROM EccDutyTable dt,DutyArrangements da "
				+ "WHERE dt.DutyGroup = '%s' AND dt.DutyTableName=da.RecId";
		DataTable dutyTableData = DBUtils.select(String.format(dutyTableSql, dutyGroup), api);
		for (DataRow row : dutyTableData.get_Rows()) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("DUTYTABLE_ID", row.get("dtId").toString());
			map.put("DUTYARRANGEMENTS_ID", row.get("daId").toString());
			map.put("SCHEDULING", row.get("Name").toString()); // 排班名称
			map.put("DUTY_RULE", row.get("ApplyYearNumber").toString()); // 规则，加入或排除
			String dutyForm = row.get("DutyForm").toString();
			String period = "每天";
			if ("每周".equals(dutyForm)) {
				String weekField = row.get("WeekField").toString();
				period = getWeekStr(weekField);
			} else if ("每月".equals(dutyForm)) {
				String startNo = row.get("WeekField").toString();
				String endNo = row.get("DayNumber").toString();
				period = "每月" + startNo + "号至" + endNo + "号";
			}
			map.put("PERIOD", period); // 时间段
			map.put("STARTDATETIME", DateUtils.formatYMDDate(DateUtils.parseYMDDate(row.get("StartDateTime").toString())));
			map.put("ENDDATETIME", DateUtils.formatYMDDate(DateUtils.parseYMDDate(row.get("EndDateTime").toString())));
			dutyTableList.add(map);
		}
	}

	/**
	 * 获取选择星期字符串
	 * 
	 * @param weekField
	 * @return
	 */
	private String getWeekStr(String weekField) {
		String[] weeks = weekField.split(",", -1);
		StringBuilder weekSb = new StringBuilder();
		for (String week : weeks) {
			if (weekSb.length() > 0)
				weekSb.append(",");
			if ("0".equals(week)) {
				weekSb.append("周一");
			} else if ("1".equals(week)) {
				weekSb.append("周二");
			} else if ("2".equals(week)) {
				weekSb.append("周三");
			} else if ("3".equals(week)) {
				weekSb.append("周四");
			} else if ("4".equals(week)) {
				weekSb.append("周五");
			} else if ("5".equals(week)) {
				weekSb.append("周六");
			} else if ("6".equals(week)) {
				weekSb.append("周日");
			}
		}
		return weekSb.toString();
	}

	/**
	 * 获取rap界面值班列表数据
	 * 
	 * @param api
	 * @param param
	 * @param outParam
	 * @throws SiteviewException
	 */
	private void getRapDutyInfo(ISiteviewApi api, String param, List<Map<String, String>> outParam)
			throws SiteviewException {
		Map<String, String> valueMap = new HashMap<String, String>();
		if (param != null && "1".equals(param)) { // 获取值班组列表
			String sql = "SELECT dg.DutyGroupName,dg.DutyGroupDesc,dg.SecurityGroup,dd.UserInfo,dd.DutyGroupId,p.DisplayName FROM EccDutyGroup dg,DutyDetail dd,Profile p WHERE dg.RecId=dd.DutyGroupId AND p.RecId = dd.UserInfo ";
			if (!api.isSuperAdminGroup()) {
				sql += " AND dg.SecurityGroup = '"+ api.get_AuthenticationService().get_CurrentSecurityGroupId() + "'";
			}
			DataTable dataTable = DBUtils.select(sql, api);
			Map<String, DutyGroup> map = new HashMap<String, DutyGroup>();
			for (DataRow row : dataTable.get_Rows()) {
				String id = row.get("DutyGroupId").toString();
				String name = row.get("DutyGroupName").toString();
				String desc = row.get("DutyGroupDesc").toString();
				String securityGroup = row.get("SecurityGroup").toString();
				String uId = row.get("UserInfo").toString(); // 用户ID
				String uName = row.get("DisplayName").toString(); // 用户名
				DutyGroup dutyGroup = map.get(id); // 群组对应用户集合
				List<CommonObject> objs;
				if (dutyGroup == null) {
					dutyGroup = new DutyGroup();
					objs = new ArrayList<CommonObject>();
					dutyGroup.setDesc(desc);
					dutyGroup.setId(id);
					dutyGroup.setName(name);
				} else {
					// 值班组有人员了的
					objs = dutyGroup.getUsers();
				}
				// DutyGroup dutyGroup = new DutyGroup();
				CommonObject object = new CommonObject();
				object.setId(uId);
				object.setName(uName);
				objs.add(object);
				dutyGroup.setUsers(objs);
				map.put(id, dutyGroup);
			}
			valueMap.put("DUTYS", JSONArray.fromObject(map.values()).toString());
		} else {
			String from = " FROM EccDutyTable edt,DutyTableUser dtu,Profile p";
			String where = " WHERE edt.RecId=dtu.DutyTableId AND p.RecId = dtu.UserId";
			if (!api.isSuperAdminGroup()) {
				from += ",EccDutyGroup edg ";
				where += " AND edg.SecurityGroup = '"
						+ api.get_AuthenticationService().get_CurrentSecurityGroupId()
						+ "' AND edg.RecId = edt.DutyGroup";
			}
			String sql = "SELECT edt.RecId dutyId,edt.DutyTableName,edt.DutyForm,edt.WeekField,edt.DayNumber,edt.StartDateTime,edt.StartTime,edt.EndDateTime,edt.EndTime,dtu.UserId,p.DisplayName "
					+ from + where;
			DataTable dataTable = DBUtils.select(sql, api);
			Map<String, DutyTable> map = new HashMap<String, DutyTable>();
			for (DataRow row : dataTable.get_Rows()) {
				String id = row.get("dutyId").toString();
				DutyTable dutyTable = map.get(id);
				List<CommonObject> objs;
				if (dutyTable == null) {
					dutyTable = new DutyTable();
					objs = new ArrayList<CommonObject>();
				} else {
					objs = dutyTable.getUsers();
				}
				dutyTable.setId(id);
				dutyTable.setDayNumber(StringUtils.getNotNullStr(row.get("DayNumber")));
				dutyTable.setDutyForm(StringUtils.getNotNullStr(row.get("DutyForm")));
				dutyTable.setWeekField(StringUtils.getNotNullStr(row.get("WeekField")));
				dutyTable.setName(StringUtils.getNotNullStr(row.get("DutyTableName")));
				dutyTable.setStartDateTime(row.get("StartDateTime").toString().split(" ", -1)[0]);
				dutyTable.setEndDateTime(row.get("EndDateTime").toString().split(" ", -1)[0]);
				String startTime = null;
				if (row.get("StartTime") == null)
					startTime = "00:00:00";
				else
					startTime = DateUtils.formatHMSDate((Date) row.get("StartTime"));
				startTime = StringUtils.removeLastPoint(startTime);
				String endTime = null;
				if (row.get("EndTime") == null)
					endTime = "00:00:00";
				else
					endTime = DateUtils.formatHMSDate((Date) row.get("EndTime"));
				endTime = StringUtils.removeLastPoint(endTime);
				dutyTable.setStartTime(startTime);
				dutyTable.setEndTime(endTime);
				String uId = row.get("UserId").toString(); // 用户ID
				String uName = row.get("DisplayName").toString(); // 用户名
				CommonObject object = new CommonObject();
				object.setId(uId);
				object.setName(uName);
				objs.add(object);
				dutyTable.setUsers(objs);
				map.put(id, dutyTable);
			}
			valueMap.put("DUTYS", JSONArray.fromObject(map.values()).toString());
		}

		outParam.add(valueMap);

	}
}
