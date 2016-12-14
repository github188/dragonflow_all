package com.siteview.ecc.yft.odata.duty;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import siteview.IFunctionExtension;
import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.SiteviewException;
import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;

import com.siteview.ecc.yft.bean.CommonObject;
import com.siteview.utils.date.DateUtils;
import com.siteview.utils.db.DBUtils;

/**
 * 获取日历详细数据
 * 
 * @author Administrator
 *
 */
public class DutyManage implements IFunctionExtension {

	public DutyManage() {
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api, Map<String, String> inputParamMap) {
		List<Map<String, String>> outParam = new ArrayList<Map<String, String>>();
		String opType = inputParamMap.get("OPERATOR_TYPE");

		Map<String, String> valueMap = new HashMap<String, String>();
		try {
			// 添加排班设置
			if (opType != null) {
				if ("ADD".equals(opType))
					addDutySet(api, inputParamMap);
				else if ("GET".equals(opType)) {
					// 获取值班日历
					getCalendarData(api, valueMap, inputParamMap);
				}
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		outParam.add(valueMap);
		return outParam;
	}

	private void getCalendarData(ISiteviewApi api, Map<String, String> valueMap,
			Map<String, String> inputParamMap) throws Exception {
		List<Map<String, String>> calendarList = new ArrayList<Map<String, String>>();
		String startDateTime = inputParamMap.get("STARTDATETIME"); // 日历查询开始时间
		String endDateTime = inputParamMap.get("ENDDATETIME"); // 日历查询结束时间
		Map<String, Collection<DutyTable>> maps = getDutyTables(api);
		Collection<DutyTable> dutyTables =  maps.get("alldutytable");
		Collection<DutyTable> pcTables =  maps.get("pcdutytable");

		Collection<DutyTable> addTables =  maps.get("adddutytable");

//		int days = DateUtils.daysBetween(startDateTime, endDateTime);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(DateUtils.parseYMDDate(startDateTime));// 日历上时间
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(DateUtils.parseYMDDate(endDateTime));
		
		// 当前时间点是否可以签到
		Calendar nowcalendar = Calendar.getInstance();
		boolean isSign = false;
		boolean continuejudge = true; //是否继续判断是否可以签到
		//值班计划集合，Map<值班日期,Map<排班安排名称,用户集合>>
		Map<Date,Map<String,List<CommonObject>>> addMap = new HashMap<Date,Map<String,List<CommonObject>>>();
		Map<Date,Map<String,List<CommonObject>>> pcMap = new HashMap<Date,Map<String,List<CommonObject>>>();
		addDutyMapData(addTables, addMap);
		addDutyMapData(pcTables, pcMap);
		
		//用户是否签到过
		boolean isSignd = false; //是否签到过，默认没
		String userid = api.get_AuthenticationService().get_CurrentUserRecId();
		String stime = DateUtils.formatYMDDate(nowcalendar.getTime()) + " 00:00:00";
		String etime = DateUtils.formatYMDDate(nowcalendar.getTime()) + " 23:59:59";
		String sql = "SELECT COUNT(*) c FROM DutySignIn WHERE SignUserId = '%s' AND (CreatedDateTime >= '%s' AND CreatedDateTime <= '%s')";
		DataRow row = DBUtils.getDataRow(api, String.format(sql, userid,stime,etime));
		if(row != null && Integer.parseInt(row.get("c").toString()) > 0){
			isSignd = true;
		}
		
		while(calendar.getTimeInMillis() <= endCalendar.getTimeInMillis()){
			if (addMap.get(calendar.getTime()) != null) {
				if (pcMap.get(calendar.getTime()) == null) { //如果当前日期没排除计划,就直接加入
					Map<String, List<CommonObject>> map = addMap.get(calendar.getTime());
					for (String key : map.keySet()) {
						if(map.get(key)!=null&&map.get(key).size()>0){
							Map<String, String> calendarMap = new HashMap<String, String>();
							calendarMap.put("start", DateUtils.formatYMDDate(calendar.getTime()));
							calendarMap.put("end", DateUtils.formatYMDDate(calendar.getTime()));
							if (!isSign && !isSignd) {
								if (DateUtils.formatYMDDate(calendar.getTime()).equals(
										DateUtils.formatYMDDate(nowcalendar.getTime()))) {
									for(CommonObject user : map.get(key)){
										isSign = user.getId().equals(api.get_AuthenticationService().get_CurrentUserRecId());
										if(isSign)
											break;
									}
								}
							}
							calendarMap.put("title", key + ":" + usersToString(map.get(key)));
							// calendarMap.put("title", usersToString(users) + "," + name);
							// calendarMap.put("title", sb.toString());
							calendarList.add(calendarMap);
						}
					}
				} else {
					//有排除计划，看排除计划中包括的人员
					Map<String,List<CommonObject>> pcUserMap = pcMap.get(calendar.getTime());
					Map<String, List<CommonObject>> map = addMap.get(calendar.getTime());
					for (String addkey : map.keySet()) {
						List<CommonObject> addUsersList = map.get(addkey);
						List<CommonObject> addUsers = new ArrayList<CommonObject>();
						addUsers.addAll(addUsersList);
						if (pcUserMap.get(addkey) != null) {
							List<CommonObject> pcUsers = pcUserMap.get(addkey);
							for (int i = addUsers.size() - 1; i >= 0; i--) {
								CommonObject addUs = addUsers.get(i);
								for(CommonObject pcUs : pcUsers){ //看排除用户是否包含加入用户,如包含就把加入用户中减去
									if (addUs.equals(pcUs)) {
										addUsers.remove(addUs);
									}
								}
							}
						}
						if(addUsers.size()>0){
							Map<String, String> calendarMap = new HashMap<String, String>();
							calendarMap.put("start", DateUtils.formatYMDDate(calendar.getTime()));
							calendarMap.put("end", DateUtils.formatYMDDate(calendar.getTime()));
							calendarMap.put("title", addkey + ":" + usersToString(addUsers));
							if (!isSign && !isSignd) {
								if (DateUtils.formatYMDDate(calendar.getTime()).equals(
										DateUtils.formatYMDDate(nowcalendar.getTime()))) {
									for (CommonObject user : addUsers) {
										isSign = user.getId().equals(
												api.get_AuthenticationService().get_CurrentUserRecId());
										if(isSign)
											break;
									}
								}
							}
							// calendarMap.put("title", usersToString(users) + "," + name);
							// calendarMap.put("title", sb.toString());
							calendarList.add(calendarMap);
						}
					}
				}
			}
			
//		}
//		for (int i = 0; i < days; i++) {
			/*StringBuilder sb = new StringBuilder();
			for (DutyTable dutyTable : addTables) { //所有加入类型值班计划
				String name = dutyTable.getName();
				String dutyRule = dutyTable.getDutyRule();
				String dutyForm = dutyTable.getDutyForm();
				List<CommonObject> users = dutyTable.getUsers();
				if (dutyForm != null) {
					boolean isadd = isAdd(calendar, dutyTable, pcTables, false);

					if (isadd) {
//						if (sb.length() > 0)
//							sb.append(";");
//						sb.append(usersToString(users) + "," + name);
						Map<String, String> calendarMap = new HashMap<String, String>();
						calendarMap.put("start", DateUtils.formatYMDDate(calendar.getTime()));
						calendarMap.put("end", DateUtils.formatYMDDate(calendar.getTime()));
						calendarMap.put("title", usersToString(users) + "," + name);
//						calendarMap.put("title", sb.toString());
						calendarList.add(calendarMap);
					}
					// 是否可以签到
					if (!isSign && continuejudge) { // 只要有一个能签到即可
						String userid = api.get_AuthenticationService().get_CurrentUserRecId();
						boolean contains = false; //是否包含值班人
						for (CommonObject user : users) {
							String id = user.getId();
							if (id.equals(userid)) { // 值班人中包含当前人
								contains = true;
								break;
							}
						}
						if (contains) { // 包含，再判断是否签到过
							String stime = DateUtils.formatYMDDate(nowcalendar.getTime()) + " 00:00:00";
							String etime = DateUtils.formatYMDDate(nowcalendar.getTime()) + " 23:59:59";
							String sql = "SELECT COUNT(*) c FROM DutySignIn WHERE SignUserId = '%s' AND (CreatedDateTime >= '%s' AND CreatedDateTime <= '%s')";
							DataRow row = DBUtils.getDataRow(api, String.format(sql, userid,stime,etime));
							if(row == null || Integer.parseInt(row.get("c").toString()) <= 0){
								isSign = isAdd(nowcalendar, dutyTable, pcTables, true);
							} else {
								isSign = false;
								continuejudge = false;
							}
						}
					}
				}
			}*/
			
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		valueMap.put("CALENDAR_DATA", JSONArray.fromObject(calendarList).toString());
		valueMap.put("IS_SIGN", String.valueOf(isSign)); // 是否可以签到,true可以
	}

	/**
	 * 封装值班Map集合
	 * @param addTables
	 * @param addMap
	 * @throws ParseException
	 */
	private void addDutyMapData(Collection<DutyTable> addTables, Map<Date, Map<String, List<CommonObject>>> addMap)
			throws ParseException {
		for (DutyTable dutyTable : addTables) {
			String name = dutyTable.getName();
			String dutyRule = dutyTable.getDutyRule();
			String dutyForm = dutyTable.getDutyForm();
			List<CommonObject> users = dutyTable.getUsers();
			Date sDate = DateUtils.parseYMDDate(dutyTable.getStartDateTime());
			Date eDate = DateUtils.parseYMDDate(dutyTable.getEndDateTime());
			Calendar scalendar = Calendar.getInstance();
			scalendar.setTime(sDate);
			Calendar ecalendar = Calendar.getInstance();
			ecalendar.setTime(eDate);
			List<CommonObject> userList = new ArrayList<CommonObject>();
			userList.addAll(users); //直接用users对象修改会影响到后面
			
			if (dutyForm != null) {
				while (scalendar.getTimeInMillis() <= ecalendar.getTimeInMillis()) {
					boolean add = false;
					if ("每天".equals(dutyForm)) {
						add = true;
					} else if ("每周".equals(dutyForm)) { // 勾选星期
						String[] weeks = dutyTable.getWeekField().split(",", -1);
						Integer dayOfWeek = null; //周几,0-6代表周一到周日
						if(scalendar.get(Calendar.DAY_OF_WEEK) == 1)
							dayOfWeek = 6;
						else
							dayOfWeek = scalendar.get(Calendar.DAY_OF_WEEK) - 2;
						if(dayOfWeek >= 0)
							add = isContainsWeeks(weeks, dayOfWeek);
					} else if ("每月".equals(dutyForm)) {
						int smonthDay = Integer.parseInt(dutyTable.getWeekField()); // 开始几号
						int emonthDay = Integer.parseInt(dutyTable.getDayNumber()); // 结束几号
						int dOfMon = scalendar.get(Calendar.DAY_OF_MONTH); // 排除几号
						add = (dOfMon >= smonthDay && dOfMon <= emonthDay); // 日期在加入的排班选择的日期内
					}
					if (add) {
						Map<String, List<CommonObject>> map = null;
						if (addMap.get(scalendar.getTime()) != null) { // 存在同一天的排班安排
							map = addMap.get(scalendar.getTime()); // 排班安排-人员对应关系

							if (map.get(name) != null) { // 之前有相同的排班计划
								List<CommonObject> preUsers = map.get(name); // 之前排班安排下的人
								List<CommonObject> preUsersList = new ArrayList<CommonObject>(); // 之前排班安排下的人
								preUsersList.addAll(preUsers);
								// 当前和之前的人员统计相同人员
								for (CommonObject user : users) {
									for (int i = preUsersList.size() - 1; i >= 0; i--) {
										CommonObject preUser = preUsersList.get(i);
										if (user.equals(preUser)) {
											preUsersList.remove(preUser);
										}
									}
								}
								userList.addAll(preUsersList); // 同一个日期的同一个排班安排名称，当前的用户增加之前不相同的用户
							}
							map.put(name, userList);
						} else {
							map = new HashMap<String, List<CommonObject>>();
							map.put(name, userList);
						}
						addMap.put(scalendar.getTime(), map);
					}
					scalendar.add(Calendar.DAY_OF_MONTH, 1);
				}
			}
		}
//		System.out.println(addMap);
	}
	
	/**
	 * 判断是否添加到日历上或是否签到
	 * 
	 * @param calendar
	 * @param dutyTable
	 * @param pcTables
	 * @param isSign
	 * @return
	 * @throws ParseException
	 */
	private boolean isAdd(Calendar calendar, DutyTable dutyTable, Collection<DutyTable> pcTables,
			boolean isSign) throws ParseException {
		Date caDate = calendar.getTime(); // 日历上时间
		String dutyForm = dutyTable.getDutyForm();
		String sDateTime = dutyTable.getStartDateTime();
		String eDateTime = dutyTable.getEndDateTime();
		String sTime = dutyTable.getStartTime();
		String eTime = dutyTable.getEndTime();
		Date sDate = DateUtils.parseYMDDate(sDateTime);
		Date eDate = DateUtils.parseYMDDate(eDateTime);
		Date shmsTime = DateUtils.parseHMSDate(sTime);
		Date ehmsTime = DateUtils.parseHMSDate(eTime);
		boolean isadd = false; // 是否添加
		Date signDate = DateUtils.parseHMSDate(DateUtils.formatHMSDate(caDate));
		if (caDate.getTime() >= sDate.getTime() && caDate.getTime() <= eDate.getTime()) {// 是否属于加入排班日期中
			if (pcTables.size() == 0) { // 没排除排班,满足加入排班就加入
				isadd = true;
			} else {
				if ("每天".equals(dutyForm)) {
					isadd = isContainsPcDays(caDate, pcTables);
				} else if ("每周".equals(dutyForm)) { // 勾选星期
					String[] weeks = dutyTable.getWeekField().split(",", -1);
					if (isContainsWeeks(weeks, calendar.get(Calendar.DAY_OF_WEEK)))
						isadd = isContainsPcDays(caDate, pcTables);
					else
						isadd = false;
				} else if ("每月".equals(dutyForm)) {
					int smonthDay = Integer.parseInt(dutyTable.getWeekField()); // 开始几号
					int emonthDay = Integer.parseInt(dutyTable.getDayNumber()); // 结束几号
					int dOfMon = calendar.get(Calendar.DAY_OF_MONTH); // 排除几号
					if (dOfMon >= smonthDay && dOfMon <= emonthDay) // 日期在加入的排班选择的日期内
						isadd = isContainsPcDays(caDate, pcTables);
					else
						isadd = false;
				}
			}
		}
		if (isSign) { // 判断是否可以签到,如果要判断就返回是否签到,不需要就判断是否添加到日历上
			if (isadd) {
				// 是否可以签到
				if (signDate.after(shmsTime) && signDate.before(ehmsTime))
					return true;
				else
					return false;
			} else
				return false;
		}
		return isadd;
	}

	/**
	 * 判断是否属于排除排班日期中,如果在,返回为false
	 * 
	 * @return 返回是否添加到日历中,true表示可以添加
	 * @throws ParseException
	 */
	private boolean isContainsPcDays(Date caDate, Collection<DutyTable> pcTables) throws ParseException {
		boolean isadd = false;
		// 判断是否属于排除排班日期中
		for (DutyTable dTable : pcTables) { // 排除日期
			String pcdutyForm = dTable.getDutyForm();
			String pcsDateTime = dTable.getStartDateTime();
			String pceDateTime = dTable.getEndDateTime();
			Date pcsDate = DateUtils.parseYMDDate(pcsDateTime);
			Date pceDate = DateUtils.parseYMDDate(pceDateTime);
			int pcdays = DateUtils.daysBetween(pcsDateTime, pceDateTime);
			Calendar pcCalendar = Calendar.getInstance();
			pcCalendar.setTime(DateUtils.parseYMDDate(pcsDateTime));
			// 日历日期是否是属于排除日期段内
			if (caDate.getTime() >= pcsDate.getTime() && caDate.getTime() <= pceDate.getTime()) {
				if ("每天".equals(pcdutyForm)) {
				} else if ("每周".equals(pcdutyForm)) { // 勾选星期
					String[] weeks = dTable.getWeekField().split(",", -1);
					for (int pc = 0; pc < pcdays; pc++) { // 排除日期内是否包含所勾选的星期
						if (isContainsWeeks(weeks, pcCalendar.get(Calendar.DAY_OF_WEEK))) { // 排除日期如果包含勾选的星期
							if (caDate.getTime() == pcCalendar.getTime().getTime()) { // 日历日期等于排除日期
								isadd = false;
								break;
							} else
								isadd = true;
						}
						pcCalendar.add(Calendar.DAY_OF_MONTH, 1);
					}
				} else if ("每月".equals(pcdutyForm)) {
					int smonthDay = Integer.parseInt(dTable.getWeekField()); // 开始几号
					int emonthDay = Integer.parseInt(dTable.getDayNumber()); // 结束几号
					for (int pc = 0; pc < pcdays; pc++) {
						int dOfMon = pcCalendar.get(Calendar.DAY_OF_MONTH); // 排除几号
						if (dOfMon >= smonthDay && dOfMon <= emonthDay)
							isadd = false;
						else
							isadd = true;
						pcCalendar.add(Calendar.DAY_OF_MONTH, 1);
					}
				}
			} else
				isadd = true;
		}
		return isadd;
	}

	/**
	 * 星期数组中是否保存某个星期
	 * 
	 * @param weeks
	 * @param week
	 * @return
	 */
	private boolean isContainsWeeks(String[] weeks, int week) {
		boolean flag = false;
		for (String w : weeks) {
			if (w.equals(String.valueOf(week))) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	/**
	 * 用户集合转换成字符串
	 * 
	 * @param users
	 *          usera,userb,userc...
	 * @return
	 */
	private String usersToString(List<CommonObject> users) {
		StringBuilder sb = new StringBuilder();
		for (CommonObject user : users) {
			if (sb.length() > 0)
				sb.append(",");
			sb.append(user.getName());
		}
		return sb.toString();
	}

	/**
	 * 获取值班表集合
	 * 
	 * @param api
	 * @return
	 */
	private Map<String, Collection<DutyTable>> getDutyTables(ISiteviewApi api) {
		String sql = "SELECT edt.RecId eid,edt.*,edg.*,p.DisplayName,p.RecId pid,da.Name daName,da.StartTime dsTime,da.EndTime deTime "
				+ "FROM EccDutyTable edt,EccDutyGroup edg,DutyDetail dd,Profile p,DutyArrangements da "
				+ "WHERE edt.DutyGroup = edg.RecId AND"
				+ " dd.DutyGroupId = edg.RecId AND p.RecId = dd.UserInfo AND da.RecId=edt.DutyTableName";
		DataTable dataTable = DBUtils.select(sql, api);
		Map<String, Collection<DutyTable>> returnMap = new HashMap<String, Collection<DutyTable>>();
		Map<String, DutyTable> map = new HashMap<String, DutyTable>();
		Map<String, DutyTable> pcMap = new HashMap<String, DutyTable>(); // 排除的值班表集合
		Map<String, DutyTable> addMap = new HashMap<String, DutyTable>(); // 加入的值班表集合
		for (DataRow row : dataTable.get_Rows()) {
			String id = row.get("eid").toString();
			String dname = row.get("daName").toString(); // 排班名称
			String dutyRule = row.get("ApplyYearNumber").toString();
			String dutyForm = row.get("DutyForm").toString();
			String weekField = row.get("WeekField").toString();
			String sDateTime = row.get("StartDateTime").toString();
			String eDateTime = row.get("EndDateTime").toString();
			String dayNumber = row.get("DayNumber").toString();
			String dsTime = row.get("dsTime").toString();
			String deTime = row.get("deTime").toString();
			DutyTable dutyTable = map.get(id);
			List<CommonObject> users;
			if (dutyTable == null) {
				dutyTable = new DutyTable();
				users = new ArrayList<CommonObject>();
			} else {
				users = dutyTable.getUsers();
			}
			dutyTable.setId(id);
			dutyTable.setName(dname);
			dutyTable.setDutyRule(dutyRule);
			dutyTable.setDayNumber(dayNumber);
			dutyTable.setDutyForm(dutyForm);
			dutyTable.setWeekField(weekField);
			dutyTable.setEndDateTime(eDateTime);
			dutyTable.setStartDateTime(sDateTime);
			dutyTable.setStartTime(dsTime);
			dutyTable.setEndTime(deTime);

			CommonObject user = new CommonObject();
			user.setId(row.get("pid").toString());
			user.setName(row.get("DisplayName").toString());
			users.add(user);
			dutyTable.setUsers(users);
			map.put(id, dutyTable);
			if ("排除".equals(dutyRule)) {
				pcMap.put(id, dutyTable);
			} else if ("加入".equals(dutyRule)) {
				addMap.put(id, dutyTable);
			}
		}
		returnMap.put("alldutytable", map.values());
		returnMap.put("pcdutytable",  pcMap.values());
		returnMap.put("adddutytable", addMap.values());
		return returnMap;
	}

	/**
	 * 添加排班设置
	 * 
	 * @param inputParamMap
	 * @return
	 * @throws SiteviewException
	 */
	private boolean addDutySet(ISiteviewApi api, Map<String, String> inputParamMap)
			throws SiteviewException {
		String dutyTableName = inputParamMap.get("DUTYTABLE_NAME"); // 排班名称-recid
		String dutyRule = inputParamMap.get("DUTY_RULE"); // 排班规则,加入或排除
		String dutyGroup = inputParamMap.get("DUTY_GROUP"); // 排班组ID
		String dutyForm = inputParamMap.get("DUTY_FORM"); // 排班规则,每天或每周或每月
		String startDateTime = inputParamMap.get("STARTDATETIME"); // 排班开始时间
		String endDateTime = inputParamMap.get("ENDDATETIME"); // 排班结束时间
		String cycle = inputParamMap.get("CYCLE"); // 选择每周后的日期,用逗号隔开
		String start = inputParamMap.get("START"); // 每周或每月开始日期
		String end = inputParamMap.get("END"); // 每周或每月结束日期

		BusinessObject tableBo = DBUtils.createBusinessObject("EccDutyTable", api);
		if (dutyForm != null) {
			if ("每周".equals(dutyForm)) { // 勾选星期
				tableBo.GetField("WeekField").SetValue(new SiteviewValue(cycle));
			} else if ("每月".equals(dutyForm)) {
				tableBo.GetField("WeekField").SetValue(new SiteviewValue(start));
				tableBo.GetField("DutyTableName").SetValue(new SiteviewValue(end));
			}
		}
		tableBo.GetField("DutyTableName").SetValue(new SiteviewValue(dutyTableName));
		tableBo.GetField("ApplyYearNumber").SetValue(new SiteviewValue(dutyRule));
		tableBo.GetField("DutyGroup").SetValue(new SiteviewValue(dutyGroup));
		tableBo.GetField("DutyForm").SetValue(new SiteviewValue(dutyForm));
		tableBo.GetField("StartDateTime").SetValue(new SiteviewValue(startDateTime));
		tableBo.GetField("EndDateTime").SetValue(new SiteviewValue(endDateTime));
		boolean tableFlag = tableBo.SaveObject(api, true, true).get_Success();

		return tableFlag;
	}

	private boolean saveDutyCalendar(ISiteviewApi api, Map<String, String> inputParamMap)
			throws ParseException {
		boolean flag = true;
		String dutyTableName = inputParamMap.get("DUTYTABLE_NAME"); // 排班名称-recid
		String dutyForm = inputParamMap.get("DUTY_FORM"); // 排班规则,每天或每周或每月
		String startDateTime = inputParamMap.get("STARTDATETIME"); // 排班开始时间
		String endDateTime = inputParamMap.get("ENDDATETIME"); // 排班结束时间
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(DateUtils.parseYMDDate(startDateTime));
		int days = DateUtils.daysBetween(startDateTime, endDateTime);
		// 查找排班安排
		DataRow dutyRow = DBUtils.getDataRow(api, "SELECT * FROM DutyArrangements WHERE RecId='"
				+ dutyTableName + "'");
		if (dutyRow != null) {
			if (dutyForm != null) {
				String stime = dutyRow.get("StartTime").toString(); // 时分秒时间
				String etime = dutyRow.get("EndTime").toString();
				if (DateUtils.parseHMSDate(stime).getTime() > DateUtils.parseHMSDate(etime).getTime()) { // 时间跨天了

				} else {

				}
				for (int i = 0; i < days; i++) {
					if ("每天".equals(dutyForm)) {

						calendar.add(Calendar.DAY_OF_MONTH, 1);
						calendar.getTime();
					} else if ("每周".equals(dutyForm)) {

					} else if ("每月".equals(dutyForm)) {

					}
				}
			}
		}
		return flag;
	}
}
