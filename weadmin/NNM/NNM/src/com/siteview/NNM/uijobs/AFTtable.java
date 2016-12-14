package com.siteview.NNM.uijobs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siteview.nnm.data.ConfigDB;
import com.siteview.nnm.data.EntityManager;
import com.siteview.nnm.data.model.entity;
import com.siteview.nnm.data.model.svNode;
import com.siteview.topology.model.Pair;
import com.siteview.topology.model.SnmpPara;
import com.siteview.topology.scan.MibScan;
import com.siteview.topology.util.Utils;

public class AFTtable {

	/**
	 * result [ip=192.168.6.1,mac=cccclddd,port=26]
	 * 
	 * @param ip
	 *            oid.startsWith("1.3.6.1.4.1.9")
	 * @return
	 */
	public static List<Map<String, String>> getAftable(String ip) {
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		String nid = EntityManager.Ip2nids.get(ip);
		entity node = EntityManager.allEntity.get(nid);
		svNode snmpdev = (svNode) node;
		String sysobjoid = snmpdev.getProperys().get("sysObjectID");
		String comm = snmpdev.getProperys().get("Community");
		String ver = snmpdev.getProperys().get("Version");
		MibScan snmp = new MibScan();
		SnmpPara spr = new SnmpPara(ip, comm, 300, 2, Integer.parseInt(ver));
		if(ver.equals("3")){
			String username="";
			String password="";
			String authorization="";
			String privacy="";
			String privacypass="";
			Connection conn = ConfigDB.getConn();
			String sql = "select username,password,authorization,privacy,privacypass from devices where nid='" + nid + "'";
			ResultSet rs = ConfigDB.query(sql, conn);
			try {
				while (rs.next()) {
					username = rs.getString("username");
					if (username == null)
						username = "";
					password = rs.getString("password");
					if (password == null)
						password = "";
					authorization = rs.getString("authorization");
					if (authorization == null)
						authorization = "";
					privacy = rs.getString("privacy");
					if (privacy == null)
						privacy = "";
					privacypass = rs.getString("privacypass");
					if (privacypass == null)
						privacypass = "";
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception ex) {

			}
			ConfigDB.close(conn);	
			spr.setSecurityName(username);
			spr.setAuthProtocol(authorization);
			spr.setAuthPass(password);
			spr.setPrivacyProtocol(privacy);
			spr.setPrivacyPass(privacypass);
		}
		List<Pair> Port2Macs = new ArrayList<Pair>();
		if (sysobjoid.startsWith("1.3.6.1.4.1.9")) {
			List<Pair<String, String>> portMacs = new ArrayList<Pair<String, String>>();
			portMacs = snmp.getMibTable(spr, "1.3.6.1.2.1.17.4.3.1.2");

			for (Pair<String, String> imac : portMacs) {
				if (imac.getFirst().length() <= 22)
					continue;
				String[] v1 = imac.getFirst().substring(23).split("\\.");
				String mac_str = "";

				//
				if (v1.length < 6) {
					continue;
				}
				mac_str = Utils.parseToHexString(v1[0])
						+ Utils.parseToHexString(v1[1])
						+ Utils.parseToHexString(v1[2])
						+ Utils.parseToHexString(v1[3])
						+ Utils.parseToHexString(v1[4])
						+ Utils.parseToHexString(v1[5]);
				String temport=imac.getSecond().trim();
				Port2Macs.add(new Pair<String, String>(mac_str,temport));
			}

		} else {
			List<Pair<String, String>> portMacs = new ArrayList<Pair<String, String>>();
			portMacs = snmp.getMibTable(spr, "1.3.6.1.2.1.17.4.3.1.2");

			for (Pair<String, String> imac : portMacs) {
				if (imac.getFirst().length() <= 22)
					continue;
				String[] v1 = imac.getFirst().substring(23).split("\\.");
				String mac_str = "";

				//
				if (v1.length < 6) {
					continue;
				}
				mac_str = Utils.parseToHexString(v1[0])
						+ Utils.parseToHexString(v1[1])
						+ Utils.parseToHexString(v1[2])
						+ Utils.parseToHexString(v1[3])
						+ Utils.parseToHexString(v1[4])
						+ Utils.parseToHexString(v1[5]);
				String temport=imac.getSecond().trim();
				Port2Macs.add(new Pair<String, String>(mac_str,temport));
			}
			portMacs = snmp.getMibTable(spr, "1.3.6.1.2.1.17.7.1.2.2.1.2");

			for (Pair<String, String> imac : portMacs) {
				if (imac.getFirst().length() <= 22)
					continue;
				String[] v1 = imac.getFirst().substring(23).split("\\.");
				String mac_str = "";

				//
				if (v1.length < 6) {
					continue;
				}
				mac_str = Utils.parseToHexString(v1[0])
						+ Utils.parseToHexString(v1[1])
						+ Utils.parseToHexString(v1[2])
						+ Utils.parseToHexString(v1[3])
						+ Utils.parseToHexString(v1[4])
						+ Utils.parseToHexString(v1[5]);
				String temport=imac.getSecond().trim();
				Port2Macs.add(new Pair<String, String>(mac_str,temport));
			}

		}
		Map<String,String> data1=null;
		for(Pair pp:Port2Macs){
			data1=new HashMap<String,String>();
			String ip1="未活动";
			if(EntityManager.allIPMAC.containsKey(pp.getFirst())){
				Map<String,String> data=EntityManager.allIPMAC.get(pp.getFirst());
				ip1=data.get("ip");
			}
			data1.put("ip", ip1);
			data1.put("mac", pp.getFirst().toString());
			data1.put("port", pp.getSecond().toString());
			result.add(data1);
			
		}
		return result;
	}
}
