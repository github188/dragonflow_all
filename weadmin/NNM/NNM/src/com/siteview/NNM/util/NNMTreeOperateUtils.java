package com.siteview.NNM.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.siteview.NNM.uijobs.Ipend;
import com.siteview.NNM.uijobs.SvgSaveUtil;
import com.siteview.nnm.data.EntityManager;
import com.siteview.nnm.data.model.entity;
import com.siteview.nnm.data.model.svNode;
import com.siteview.nnm.data.model.svgroup;
import com.siteview.utils.html.StringUtils;

import Siteview.DataRow;
import Siteview.DataRowCollection;
import Siteview.DataTable;
import Siteview.Api.ISiteviewApi;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * NNM树操作工具类
 * 
 * @author Administrator
 *
 */
public class NNMTreeOperateUtils {

	/**
	 * 获取NNM设备管理对应设备类型下数据
	 * 
	 * @param equipmentType
	 *            设备类型:三层交换机 ex...
	 * @return
	 */
	public static List<Map<String, Object>> getEquipmentTypeData(String equipmentType) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (equipmentType != null) {
			Map<String, entity> allEntiry = EntityManager.allEntity;
			Iterator<String> it = allEntiry.keySet().iterator();
			// mac 和ip 相同即为同一设备，需要排除掉
			List<String> ipmacs = new ArrayList<String>();
			while (it.hasNext()) {
				String key = it.next();
				entity entiry = allEntiry.get(key);
				if (entiry instanceof svgroup)
					continue;
				svNode node = (svNode) entiry;
				String ip = node.getLocalip();
				String mac = node.getMac();
				if (mac == null)
					mac = "";
				String ipmac = ip + mac;
				if (ipmacs.contains(ipmac))
					continue;
				int svgType = entiry.getSvgtype();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("ip", node.getLocalip());
				map.put("name", node.getDevicename());
				map.put("model", node.getModel());
				map.put("mac", node.getMac());
				map.put("firm", node.getFactory());
				map.put("data", node);
				if (svgType == 0 && equipmentType.startsWith("三层交换机")) {
					ipmacs.add(ipmac);
					list.add(map);
				} else if (svgType == 1 && equipmentType.startsWith("二层交换机")) {
					ipmacs.add(ipmac);
					list.add(map);
				} else if (svgType == 2 && equipmentType.startsWith("路由")) {
					ipmacs.add(ipmac);
					list.add(map);
				} else if (svgType == 3 && equipmentType.startsWith("防火墙")) {
					ipmacs.add(ipmac);
					list.add(map);
				} else if (svgType == 4 && equipmentType.startsWith("服务器")) {
					ipmacs.add(ipmac);
					list.add(map);
				} else if (svgType == 5 && equipmentType.startsWith("pc终端")) {
					ipmacs.add(ipmac);
					list.add(map);
				} else if (svgType == 6 && equipmentType.startsWith("其他")) {
					ipmacs.add(ipmac);
					list.add(map);
				}
			}
		}
		return list;
	}

	/**
	 * 获取子网设备内容
	 * 
	 * @param svnodes
	 *            List<svNode>集合的JSONArray数组字符数据
	 * @param subName
	 *            子网名称(IP名称 172.0.2.8/29)
	 * @param listip
	 *            子网ip集合
	 * @return
	 */
	public static List<Map<String, Object>> getSubNetData(String svnodes, String subname) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<String> listip = new ArrayList<String>();
		if (svnodes != null) {
			JSONArray svNodesArray = JSONArray.fromObject(svnodes);
			for (int index = 0; index < svNodesArray.size(); index++) {
				JSONObject obj = svNodesArray.getJSONObject(index);
				svNode sv = (svNode) JSONObject.toBean(obj, svNode.class);
				String ip = sv.getProperys().get("ipList");
				String[] ips = ip.split(",");
				String subnet = sv.getProperys().get("subnetName");
				String[] subs = subnet.split(",");
				for (int i = 0; i < subs.length; i++) {
					String sub = subs[i];
					if (subname.startsWith(sub) && !listip.contains(ips[i])) {
						Map<String, Object> map = new HashMap<String, Object>();
						String ipAddr = ips[i] == null ? sv.getLocalip() : ips[i];
						map.put("ip", ipAddr);
						map.put("name", sv.getDevicename()); // sv.getDevicename();
						int n = sv.getSvgtype();
						String type = "";
						if (n == 0)
							type = "三层交换机";
						else if (n == 1)
							type = "二层交换机";
						else if (n == 2)
							type = "路由";
						else if (n == 3)
							type = "防火墙";
						else if (n == 4)
							type = "服务器";
						else if (n == 5)
							type = "pc终端";
						else if (n == 6)
							type = "其他";
						map.put("devicetype", type);
						map.put("mac", sv.getMac());
						map.put("firm", sv.getFactory());
						map.put("data", sv);
						list.add(map);
						listip.add(ipAddr);
					}
				}
			}

		}
		return list;
	}

	/**
	 * 子网ip占用情况
	 * 
	 * @param ip 子网ip
	 * @param ipStatus
	 *            ip占用状态 ("good"占用,"nodata"未占用,"all"全部)
	 * @param listip
	 */
	public static Map<String, List<String>> getSubNetIpOccupancyData(String ip, String ipStatus, List<String> listip) {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		String mask = "0"; // 子网掩码
		if (ip.indexOf("/") != -1) {
			mask = ip.substring(ip.indexOf("/") + 1);
			if (mask.contains("("))
				mask = mask.substring(0, mask.indexOf("("));
			ip = ip.substring(0, ip.indexOf("/"));
		}
		int ipcount = (int) Math.pow(2, 32 - Integer.parseInt(mask)) - 1;
		String endip = Ipend.endip(ip, mask); // 截止ip
		int i = Integer.parseInt(ip.substring(ip.lastIndexOf(".") + 1)); // ip截尾字符,例:192.168.9.12的12
		i++;
		String preIp = ip.substring(0, ip.lastIndexOf(".")); // ip前缀192.168.9
		Collections.sort(listip, new Comparator<String>() {
			public int compare(String o1, String o2) {
				String[] o1s = o1.split("\\.");
				String[] o2s = o2.split("\\.");
				for (int i = 0; i < o1s.length; i++) {
					if (Integer.parseInt(o1s[i]) != Integer.parseInt(o2s[i]))
						return Integer.parseInt(o1s[i]) - Integer.parseInt(o2s[i]);
				}
				return 0;
			}
		});
		List<String> endIpList = new ArrayList<String>();
		endIpList.add(endip);
		map.put("endip", endIpList);
		List<String> ipcountList = new ArrayList<String>();
		ipcountList.add(ipcount + "");
		map.put("ipcount", ipcountList);
		if (ipStatus != null) {
			if ("good".equalsIgnoreCase(ipStatus)) {
				for (String ip_ : listip) {
					List<String> list = map.get("good");
					if (list == null)
						list = new ArrayList<String>();
					list.add(ip_);
					map.put("good", list);
				}
			} else if ("all".equalsIgnoreCase(ipStatus)) { // good + nodata
				for (String ip_ : listip) {
					List<String> list = map.get("good");
					if (list == null)
						list = new ArrayList<String>();
					list.add(ip_);
					map.put("good", list);
				}
				setNotIp(map, i, preIp, endip, listip);
			} else if ("nodata".equalsIgnoreCase(ipStatus)) {
				setNotIp(map, i, preIp, endip, listip);
			}
		}
		return map;
	}

	/**
	 * 未占用ip
	 * 
	 * @param map
	 * @param i
	 * @param preIp
	 * @param endip
	 * @param listip
	 */
	public static void setNotIp(Map<String, List<String>> map, int i, String preIp, String endip, List<String> listip) {
		boolean b = true;
		int nn = 0;
		while (b) {
			if (i > 255) {
				int n = Integer.parseInt(preIp.substring(preIp.lastIndexOf(".") + 1));
				n++;
				preIp = preIp.substring(0, preIp.lastIndexOf(".") + 1);
				preIp += n;
				i = 1;
			}
			if (endip.equals(preIp + "." + i) || nn == 1000)
				b = false;
			if (!listip.contains(preIp + "." + i)) {
				List<String> list = map.get("nodata");
				if (list == null)
					list = new ArrayList<String>();
				list.add(preIp + "." + i);
				map.put("nodata", list);
			}
			i++;
			nn++;
		}
	}
	
	/**
	 * IP-MAC异动查询
	 * @param api
	 * @param starttime 开始时间
	 * @param endtime 结束时间
	 * @param mac mac地址
	 * @param ip ip地址
	 * @return
	 */
	public static List<Map<String, String>> getIpMacChangeData(ISiteviewApi api, String starttime, String endtime,
			String mac, String ip) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String selectsql = "select OldIp,OldMac,NewIp,NewMac,SvgName,SvgType,Createddatetime from NNMSvgChange where "
				+ "createddatetime > '" + starttime + "' and createddatetime" + "< '" + endtime + "' ";
		if (mac != null && mac.length() > 0)
			selectsql += "and (OldMac='" + mac + "' or NewMac='" + mac + "')";
		if (ip != null && ip.length() > 0)
			selectsql += "and (Oldip='" + ip + "' or NewIp='" + ip + "')";
		DataTable dt = SvgSaveUtil.Select(selectsql, api);
		DataRowCollection rows = dt.get_Rows();
		for (DataRow dr : rows) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("oldIp", dr.get_Item("OldIp").toString());
			map.put("oldMac", dr.get_Item("OldMac").toString());
			map.put("newIp", dr.get_Item("NewIp").toString());
			map.put("newMac", dr.get_Item("NewMac").toString());
			map.put("svgName", dr.get_Item("SvgName").toString());
			String svtype = dr.get_Item("SvgType").toString();
			String type = "pc";
			if (svtype.equals("5")) {
				type = "pc";
			} else if (svtype.equals("4")) {
				type = "server";
			} else if (svtype.equals("0") || svtype.equals("1") || svtype.equals("2") || svtype.equals("3")) {
				type = "network";
			}
			map.put("svgType", type);
			map.put("createddatetime", StringUtils.removeLastPoint(dr.get_Item("Createddatetime").toString()));
			list.add(map);
		}
		return list;
	}
}
