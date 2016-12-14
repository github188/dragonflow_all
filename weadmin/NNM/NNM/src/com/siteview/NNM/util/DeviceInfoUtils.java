package com.siteview.NNM.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siteview.nnm.data.ConfigDB;
import com.siteview.nnm.data.DBManage;
import com.siteview.nnm.data.EntityManager;
import com.siteview.nnm.data.PortManage;
import com.siteview.nnm.data.model.TopoChart;
import com.siteview.nnm.data.model.entity;
import com.siteview.nnm.data.model.svEdge;
import com.siteview.nnm.data.model.svNode;
import com.siteview.topology.model.SnmpPara;
import com.siteview.topology.snmp.snmpTools;
import com.siteview.utils.snmp.SnmpGet;
import com.siteview.utils.snmp.SnmpWalk;

import net.sf.json.JSONArray;

/**
 * 设备信息查询工具类
 * 
 * @author Administrator
 *
 */
public class DeviceInfoUtils {
	/**
	 * 获取本机属性信息
	 * 
	 * @param conn
	 * @param entityobj
	 * @param nodeid
	 * @param outMap
	 * @throws SQLException
	 */
	public static Map<String, String> getMachineProperties(Connection conn, svNode entityobj, String nodeid)
			throws SQLException {
		String sql = "select descrip,memo1 from devices where nid='" + nodeid + "'";
		ResultSet rs = ConfigDB.query(sql, conn);
		Map<String, String> map = new HashMap<String, String>();
		String desc = "";
		String memo1 = "";
		while (rs.next()) {
			desc = rs.getString("descrip");
			if (desc == null)
				desc = "";
			memo1 = rs.getString("memo1");
			if (memo1 == null)
				memo1 = "";
		}

		List<String> ips = new ArrayList<String>();
		String localIp = entityobj.getLocalip();
		ips.add(localIp);
		String[] iplists = entityobj.getProperys().get("ipList").split("\\,");
		for (String it : iplists) {
			if (!entityobj.getLocalip().equals(it))
				ips.add(it);
		}
		map.put("ipAddress", JSONArray.fromObject(ips).toString()); // 设备地址
		map.put("mac", entityobj.getMac()); // MAC地址
		map.put("deviceName", entityobj.getDevicename()); // 设备名称
		map.put("customName", entityobj.getCustomname() == null ? "" : entityobj.getCustomname()); // 自定义名称
		map.put("deviceTypeSelect", entityobj.getSvgtype() + ""); // 设备类型选择项
		map.put("factory", entityobj.getFactory() == null ? "" : entityobj.getFactory()); // 制造厂商
		map.put("subnetName", entityobj.getProperys().get("subnetName")); // 所在子网
		map.put("subnetMask", entityobj.getProperys().get("maskField")); // 子网掩码
		map.put("oid", entityobj.getProperys().get("sysObjectID")); // 设备OID
		map.put("uptime", getUptime(entityobj)); // 运行时间
		map.put("desc", desc); // 设备描述
		map.put("remark", memo1); // 设备备注
		return map;
	}

	/**
	 * 获取设备时间
	 * 
	 * @param entityobj
	 * @return
	 */
	private static String getUptime(svNode entityobj) {
		String snmpenable = entityobj.getProperys().get("snmpEnabled");
		if (snmpenable == null || snmpenable.isEmpty() || snmpenable.equals("0")) {
			return "0";
		}
		String dip = entityobj.getLocalip();
		String comm = entityobj.getProperys().get("Community");
		if (comm == null || comm.isEmpty()) {
			return "0";
		}
		String dver = entityobj.getProperys().get("Version");
		SnmpPara para = new SnmpPara(dip, comm, 2000, 2);
		if (dver.equals("1")) {
			para.setSnmpver(1);
		} else if (dver.equals("2")) {
			para.setSnmpver(2);
		}
		String rr = snmpTools.getValue(para, "1.3.6.1.2.1.1.3.0");
		if (rr == null || rr.isEmpty())
			rr = "0";
		return rr;
	}

	/**
	 * 获取本机端口信息
	 * 
	 * @param conn
	 * @param entityobj
	 * @param nodeid
	 * @param outMap
	 * @throws SQLException
	 */
	public static List<Map<String, String>> getMachinePortData(Connection conn, svNode entityobj, String nodeid)
			throws SQLException {
		String sql = "select pindex,porttype,mac,desc from ports where id='" + nodeid + "'";
		ResultSet rs = ConfigDB.query(sql, conn);
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, entity> allNodes = new HashMap<String, entity>();
		for (String topolabel : DBManage.Topos.keySet()) {
			TopoChart topochart = DBManage.Topos.get(topolabel);
			allNodes.putAll(topochart.getNodes());
		}
		String nodeip = entityobj.getLocalip();
		while (rs.next()) {
			Map<String, String> map = new HashMap<String, String>();
			String pindex = rs.getString("pindex");
			String porttype = rs.getString("porttype");
			String mac = rs.getString("mac");
			String desc = rs.getString("desc");
			map.put("index", pindex);
			map.put("porttype", PortManage.portTypenum2TypeDesc.get(porttype));
			map.put("mac", mac);
			map.put("desc", desc);
			map.put("ip", getPortConnectionIP(pindex, nodeid, allNodes, nodeip));
			list.add(map);
		}
		return list;
	}

	private static String getPortConnectionIP(String pindex, String portID, Map<String, entity> allNodes,
			String nodeip) {
		String IPs = "";
		for (TopoChart topochart : DBManage.Topos.values()) {
			for (svEdge sedge : topochart.getEdges().values()) {
				if (sedge.getLsource().equals(portID) && sedge.getSinterface().equals(pindex)) {
					String rightID = sedge.getLtarget();
					svNode nn = (svNode) allNodes.get(rightID);
					// hub
					if (nn.getSvgtype() == 6 || nn.getSvgtype() == 7) {
						IPs = getHubConnectionIP(rightID, allNodes, nodeip);
					} else {
						IPs = nn.getLocalip() + "," + IPs;
					}
				} else if (sedge.getLtarget().equals(portID) && sedge.getTinterface().equals(pindex)) {
					String leftID = sedge.getLsource();
					svNode nn = (svNode) allNodes.get(leftID);
					if (nn.getSvgtype() == 6 || nn.getSvgtype() == 7) {
						IPs = getHubConnectionIP(leftID, allNodes, nodeip);
					} else {
						IPs = nn.getLocalip() + "," + IPs;
					}
				}

			}

		}
		if (IPs.isEmpty())
			IPs = "No Connection Device";
		return IPs;
	}

	private static String getHubConnectionIP(String hubid, Map<String, entity> allNodes, String nodeip) {
		String ips = "";
		String newip = "";
		for (TopoChart topochart : DBManage.Topos.values()) {
			for (svEdge sedge : topochart.getEdges().values()) {
				if (sedge.getLsource().equals(hubid)) {
					String rightID = sedge.getLtarget();
					svNode rnode = (svNode) allNodes.get(rightID);
					newip = rnode.getLocalip();
					if (!nodeip.equals(newip))
						ips = newip + "," + ips;

				} else if (sedge.getLtarget().equals(hubid)) {
					String leftID = sedge.getLsource();
					svNode lnode = (svNode) allNodes.get(leftID);
					newip = lnode.getLocalip();
					if (!nodeip.endsWith(newip))
						ips = newip + "," + ips;

				}
			}
		}
		return ips;
	}

	/**
	 * 获取上联端口数据
	 * 
	 * @param conn
	 * @param entityobj
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, String> getPartPortData(Connection conn, svNode entityobj) throws SQLException {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> map = new HashMap<String, String>();
		for (String lid : EntityManager.allEdges.keySet()) {
			if (EntityManager.allEdges.get(lid).getLtarget().equals(entityobj.getSvid())) {
				entity enode = EntityManager.allEntity.get(EntityManager.allEdges.get(lid).getLsource());
				map.put("ip", ((svNode) enode).getLocalip());
				map.put("name", ((svNode) enode).getDevicename());
				String sql = "select desc from ports where id='" + EntityManager.allEdges.get(lid).getLsource()
						+ "' and pindex='" + EntityManager.allEdges.get(lid).getSinterface() + "'";
				String desc = "";
				ResultSet rs = ConfigDB.query(sql, conn);
				while (rs.next()) {
					desc = rs.getString("desc");
					if (desc == null)
						desc = "";
				}
				String port = "";
				if (((svNode) enode).getSvgtype() != 6)
					port = EntityManager.allEdges.get(lid).getSinterface() + " :" + desc;
				map.put("port", port);
				list.add(map);
				break;
			}
		}
		return map;
	}

	/**
	 * 获取接口信息表数据
	 * 
	 * @param entityobj
	 * @return
	 */
	public static Map<String, String> getInterfacesData(svNode entityobj) {
		SnmpWalk snmpWalk = getSnmpwalk(entityobj, new String[] { "1.3.6.1.2.1.2.2.1" });
		return snmpWalk.getTableAsMaps();
	}

	/**
	 * 获取路由表信息数据,1.3.6.1.2.1.4.21.1.1:ipRouteDest,1.3.6.1.2.1.4.21.1.7:ipRouteNextHop,1.3.6.1.2.1.4.21.1.8:ipRouteType,
	 * 1.3.6.1.2.1.4.21.1.9:ipRouteProto,1.3.6.1.2.1.4.21.1.11:ipRouteMask
	 * 
	 * @param entityobj
	 * @return {1.3.6.1.2.1.4.21.1.1.ip:value}
	 */
	public static Map<String, String> getRoutingTableData(svNode entityobj) {
		SnmpWalk snmpWalk = getSnmpwalk(entityobj, new String[] { "1.3.6.1.2.1.4.21.1.1", "1.3.6.1.2.1.4.21.1.7",
				"1.3.6.1.2.1.4.21.1.8", "1.3.6.1.2.1.4.21.1.9", "1.3.6.1.2.1.4.21.1.11" });
		return snmpWalk.getTableAsMaps();
	}

	/**
	 * 获取ARP表信息数据,1.3.6.1.2.1.4.22.1.1:ipNetToMediaIfIndex
	 * ,1.3.6.1.2.1.4.22.1.2:ipNetToMediaPhysAddress
	 * ,1.3.6.1.2.1.4.22.1.3:ipNetToMediaNetAddress ,
	 * 1.3.6.1.2.1.4.22.1.4:ipNetToMediaType
	 * 
	 * @param entityobj
	 * @return {1.3.6.1.2.1.4.22.1.1.index.ip:value}
	 */
	public static Map<String, String> getARPTableData(svNode entityobj) {
		SnmpWalk snmpWalk = getSnmpwalk(entityobj, new String[] { "1.3.6.1.2.1.4.22.1.1", "1.3.6.1.2.1.4.22.1.2",
				"1.3.6.1.2.1.4.22.1.3", "1.3.6.1.2.1.4.22.1.4" });
		return snmpWalk.getTableAsMaps();
	}

	/**
	 * 获取CDP表信息数据 "1.3.6.1.4.1.9.9.23.1.2.1.1.4":cdpCacheAddress,
	 * "1.3.6.1.4.1.9.9.23.1.2.1.1.5":cdpCacheVersion, "1.3.6.1.4.1.9.9.23.1.2.1.1.6":cdpCacheDeviceId,
	 * "1.3.6.1.4.1.9.9.23.1.2.1.1.7":cdpCacheDevicePort, "1.3.6.1.4.1.9.9.23.1.2.1.1.8":cdpCachePlatform,
	 * "1.3.6.1.4.1.9.9.23.1.2.1.1.11":cdpCacheNativeVLAN, "1.3.6.1.4.1.9.9.23.1.2.1.1.12":cdpCacheDuplex,
	 * "1.3.6.1.4.1.9.9.23.1.2.1.1.16":cdpCacheMTU
	 * 
	 * @param entityobj
	 * @return
	 */
	public static Map<String, String> getCdpTableData(svNode entityobj) {
		SnmpWalk snmpWalk = getSnmpwalk(entityobj,
				new String[] { "1.3.6.1.4.1.9.9.23.1.2.1.1.4", "1.3.6.1.4.1.9.9.23.1.2.1.1.5",
						"1.3.6.1.4.1.9.9.23.1.2.1.1.6", "1.3.6.1.4.1.9.9.23.1.2.1.1.7", "1.3.6.1.4.1.9.9.23.1.2.1.1.8",
						"1.3.6.1.4.1.9.9.23.1.2.1.1.11", "1.3.6.1.4.1.9.9.23.1.2.1.1.12",
						"1.3.6.1.4.1.9.9.23.1.2.1.1.16" });
		return snmpWalk.getTableAsMaps();
	}

	/**
	 * 获取IP表信息 "1.3.6.1.2.1.4.20.1.1":ipAdEntAddr, "1.3.6.1.2.1.4.20.1.2":ipAdEntIfIndex,
	 * "1.3.6.1.2.1.4.20.1.3":ipAdEntNetMask
	 * 
	 * @param entityobj
	 * @return
	 */
	public static Map<String, String> getIPTableData(svNode entityobj) {
		SnmpWalk snmpWalk = getSnmpwalk(entityobj,
				new String[] { "1.3.6.1.2.1.4.20.1.1", "1.3.6.1.2.1.4.20.1.2", "1.3.6.1.2.1.4.20.1.3" });
		return snmpWalk.getTableAsMaps();
	}

	private static SnmpWalk getSnmpwalk(svNode entityobj, String[] oids) {
		String ip = entityobj.getLocalip();
		String community = entityobj.getProperys().get("Community");
		int port = (Integer.parseInt(
				entityobj.getProperys().get("port").equals("") ? "161" : entityobj.getProperys().get("port")));
		String dver = entityobj.getProperys().get("Version");
		int version = 1;
		if (dver.equals("1")) {
			version = 1;
		} else if (dver.equals("2")) {
			version = 2;
		}
		return new SnmpWalk(ip, port, community, oids, version);
	}

	private static SnmpGet getSnmpGet(svNode entityobj, String[] oids) {
		String ip = entityobj.getLocalip();
		String community = entityobj.getProperys().get("Community");
		int port = (Integer.parseInt(
				entityobj.getProperys().get("port").equals("") ? "161" : entityobj.getProperys().get("port")));
		String dver = entityobj.getProperys().get("Version");
		int version = 1;
		if (dver.equals("1")) {
			version = 1;
		} else if (dver.equals("2")) {
			version = 2;
		}
		return new SnmpGet(ip, port, community, oids, version);
	}

	public static void main(String[] args) {
		List<String> ips = new ArrayList<String>();
		ips.add("192.168.9.1");
		ips.add("192.168.9.2");
		System.out.println(JSONArray.fromObject(ips).toString());
		JSONArray array = JSONArray.fromObject(JSONArray.fromObject(ips).toString());
		if (array.get(0) instanceof String) {
			String new_name = (String) array.get(0);

		}
		System.out.println();
	}
}
