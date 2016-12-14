package com.siteview.nnm.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.rap.json.JsonObject;
import org.snmp4j.mp.SnmpConstants;

import com.siteview.nnm.data.model.TopoChart;
import com.siteview.nnm.data.model.entity;
import com.siteview.nnm.data.model.svEdge;
import com.siteview.nnm.data.model.svNode;
import com.siteview.snmpinterface.EquipmentQueueManager;
import com.siteview.utils.db.DBQueryUtils;

import Siteview.DataRow;
import Siteview.DataRowCollection;
import Siteview.DataTable;
import Siteview.SiteviewException;
import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;
import Siteview.Database.IDbDataParameterCollection;
import Siteview.Database.SqlParameterCollection;
import Siteview.Windows.Forms.ConnectionBroker;

public class EntityManager {
	static byte[] lock = new byte[0];
	public static Map<String, String> Ip2nids = new HashMap<String, String>();
	public static Map<String,List<String>> ip2list=new HashMap<String,List<String>>();
	public static Map<String, entity> allEntity = new HashMap<String, entity>();
	public static Map<String, svEdge> allEdges = new HashMap<String, svEdge>();
	public static Map<String, Map<String, String>> allIPMAC = new ConcurrentHashMap<String, Map<String, String>>();

	public static void AddEntityTasks() {
		synchronized (lock) {
			allEntity.clear();
			allEdges.clear();
			Ip2nids.clear();
			ip2list.clear();
			for (TopoChart topochart : DBManage.Topos.values()) {
				allEdges.putAll(topochart.getEdges());
				allEntity.putAll(topochart.getNodes());
				for (entity node : topochart.getNodes().values()) {
					if (node instanceof svNode) {
						svNode snmpdev = (svNode) node;
						if (!Ip2nids.containsKey(snmpdev.getLocalip())) {
							Ip2nids.put(snmpdev.getLocalip(), snmpdev.getSvid());
						}
						if (!ip2list.containsKey(snmpdev.getLocalip())) {
							List<String> vvv=new ArrayList<String>();
							vvv.add(snmpdev.getSvid());
							ip2list.put(snmpdev.getLocalip(), vvv);
						}else{
							ip2list.get(snmpdev.getLocalip()).add(snmpdev.getSvid());
						}
						String getenable = snmpdev.getProperys().get(
								"snmpEnabled");
						int devtype = snmpdev.getSvgtype();
						if (devtype == 0 || devtype == 1 || devtype == 2
								|| devtype == 3) {
							if (getenable.equals("1")) {
								String ip = snmpdev.getLocalip();
								String community = snmpdev.getProperys().get(
										"Community");
								String version = snmpdev.getProperys().get(
										"Version");
								int vvv = SnmpConstants.version1;
								if (version.equals("2")) {
									vvv = SnmpConstants.version2c;
								} else if (version.equals("3")) {
									vvv = SnmpConstants.version2c;
								}
								EquipmentQueueManager.startManagerFromStr(ip,
										community, vvv, 161);
							}
						}
					}
				}

			}
		}
	}

	public static void AddEntity(String id, entity node) {
		synchronized (lock) {
			allEntity.put(id, node);
			if (node instanceof svNode) {
				svNode snmpdev = (svNode) node;
				String getenable = snmpdev.getProperys().get("snmpEnabled");
				if (getenable.equals("1")) {
					String ip = snmpdev.getLocalip();
					String community = snmpdev.getProperys().get("Community");
					String version = snmpdev.getProperys().get("Version");
					int vvv = SnmpConstants.version1;
					if (version.equals("2")) {
						vvv = SnmpConstants.version2c;
					} else if (version.equals("3")) {
						vvv = SnmpConstants.version2c;
					}
					EquipmentQueueManager.startManagerFromStr(ip, community,
							vvv, 161);
				}
			}
		}
	}

	public static void saveIPMACBase(ISiteviewApi api) {
		String delete_nnmsvgnode_sql = "delete from NNMSvgNode";
		UpdateorDelete(delete_nnmsvgnode_sql, api);
		Iterator<String> it = allEntity.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			entity entiry = allEntity.get(key);
			svNode node = (svNode) entiry;
			String ip = node.getLocalip();
			if (!ip.contains("."))
				continue;
			String iplist = node.getProperys().get("ipList");
			String subnet = node.getProperys().get("subnetName");
			if (iplist != null && iplist.length() > 0 && subnet != null
					&& subnet.length() > 0) {
				String[] ips_ = iplist.split(",");
				String[] sub = subnet.split(",");
				for (int i = 0; i < ips_.length; i++) {
					saveSvg(api, sub[i], ips_[i], node.getMac(),
							node.getSvgtype() + "", node.getModel(),
							node.getFactory(), node.getSvid(),
							node.getDevicename());
				}
			} else if (iplist != null && iplist.length() > 0) {
				saveSvg(api, subnet, node.getProperys().get("ip"),
						node.getMac(), node.getSvgtype() + "", node.getModel(),
						node.getFactory(), node.getSvid(), node.getDevicename());
			}
		}
	}

	public static void saveSvg(ISiteviewApi api, String subnet, String ip,
			String mac, String svgtype, String svgstle, String svgfirm,
			String svgid, String svgname) {
		try {
			if (api == null)
				return;
			BusinessObject bo = api.get_BusObService().Create("NNMSvgNode");
			bo.GetField("Subnet").SetValue(new SiteviewValue(subnet));
			bo.GetField("IP").SetValue(new SiteviewValue(ip));
			bo.GetField("Mac").SetValue(new SiteviewValue(mac));
			bo.GetField("SvgType").SetValue(new SiteviewValue(svgtype));
			bo.GetField("SvgStyle").SetValue(new SiteviewValue(svgstle));
			bo.GetField("SvgFirm").SetValue(new SiteviewValue(svgfirm));
			bo.GetField("SvgId").SetValue(new SiteviewValue(svgid));
			bo.GetField("SvgName").SetValue(new SiteviewValue(svgname));
			bo.SaveObject(api, true, true);
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 调用原生态查询sql
	 */
	public static DataTable Select(String sql, ISiteviewApi api) {
		IDbDataParameterCollection collection = new SqlParameterCollection();
		StringBuffer str = new StringBuffer();
		str.append(sql);
		DataTable dt = null;
		try {
			dt = api.get_NativeSQLSupportService().ExecuteNativeSQLQuery(
					str.toString(), collection);
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
		return dt;
	}

	/*
	 * 调用原生态修改和删除sql
	 */
	public static void UpdateorDelete(String sql, ISiteviewApi api) {
		IDbDataParameterCollection collection = new SqlParameterCollection();
		StringBuffer str = new StringBuffer();
		str.append(sql);
		try {
			api.get_NativeSQLSupportService().ExecuteNativeSQL(str.toString(),
					collection);
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 保存IP-MAC异动
	 */
	public static void saveSvgChange(ISiteviewApi api, String oldip,
			String oldmac, String newip, String svgtype, String newmac,
			String svgname) {
		try {
			if (api == null)
				return;
			System.err.println(newmac + "::" + "oldip" + oldip + "--" + "newip"
					+ newip);
			BusinessObject bo = api.get_BusObService().Create("NNMSvgChange");
			bo.GetField("OldIp").SetValue(new SiteviewValue(oldip));
			bo.GetField("OldMac").SetValue(new SiteviewValue(oldmac));
			bo.GetField("NewIp").SetValue(new SiteviewValue(newip));
			bo.GetField("SvgType").SetValue(new SiteviewValue(svgtype));
			bo.GetField("NewMac").SetValue(new SiteviewValue(newmac));
			bo.GetField("SvgName").SetValue(new SiteviewValue(svgname));
			bo.SaveObject(api, true, true);
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
	}

	public static void getAllIPMac(ISiteviewApi api) {
		allIPMAC.clear();
		String sql = "select IP,Mac,SvgType,SvgName from NNMSvgNode ";
		DataTable dt = Select(sql, api);
		DataRowCollection rows = dt.get_Rows();
		for (DataRow dr : rows) {
			Map<String, String> data1 = new HashMap<String, String>();
			String mac = dr.get_Item("Mac").toString();
			if (mac.isEmpty())
				continue;
			data1.put("ip", dr.get_Item("IP").toString());
			data1.put("mac", dr.get_Item("Mac").toString());
			data1.put("svgtype", "5");
			data1.put("svgname", dr.get_Item("SvgName").toString());
			allIPMAC.put(mac, data1);
		}
	}

	/*
	 * 1 error 2 warning
	 */
	public static JsonObject[] buildAlertdata() {
		List<JsonObject> list = new ArrayList<JsonObject>();

//		String sql = "select Status,ServerAddress from Equipment where status='error' or status='warning'";
		String sql="select ServerAddress,e.EquipmentStatus from Equipment e where RecId in "
		+ "(select EquipmentRecId from AlarmEventLog where IsAlarm=1 "
		+ "and AlarmStatus like '1' and MonitorRecId in (select RecId from Monitor))"
		+ " order by e.EquipmentStatus desc";
		DataTable dt = DBQueryUtils.Select(sql,
				ConnectionBroker.get_SiteviewApi());

		for (DataRow dr : dt.get_Rows()) {
			String s = dr.get("EquipmentStatus").toString();
			String ip = dr.get("ServerAddress") == null ? "" : dr.get(
					"ServerAddress").toString();

			if (ip2list.get(ip) == null)
				continue;
			int status = 0;

			if (s.equals("error"))
				status = 1;
			if (s.equals("warning"))
				status = 2;
           for(String svid:ip2list.get(ip)){
			JsonObject jo = new JsonObject().add("nid", svid)
			.add("ip", ip)		
			.add("state", status);
			list.add(jo);
           }
		}

		JsonObject[] jsonobjects = new JsonObject[list.size()];
		for (int i = 0; i < list.size(); i++) {
			jsonobjects[i] = list.get(i);
		}
		return jsonobjects;

	}

}
