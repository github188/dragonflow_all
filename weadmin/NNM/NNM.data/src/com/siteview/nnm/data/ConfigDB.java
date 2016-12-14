package com.siteview.nnm.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.FrameworkUtil;

import com.siteview.topology.model.IDBody;
import com.siteview.topology.model.IfRec;
import com.siteview.topology.model.Pair;

public class ConfigDB {

	/**
	 * 
	 * @return
	 */
	public static Connection getConn() {
		Connection conn = null;
		if (conn == null) {
			try {
//				String dbName = FileLocator.toFileURL(
//						FrameworkUtil.getBundle(ConfigDB.class).getEntry(""))
//						.getPath()
//						+ "data/ConfigData.db";
				String dbName = FileLocator.toFileURL(Platform.getBundle("NNM.database").getEntry("data")).getPath()+"ConfigData.db";
				Class.forName("org.sqlite.JDBC");
				conn = DriverManager.getConnection("jdbc:sqlite:" + dbName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return conn;

	}
	public static Connection getvConn(){
		Connection conn = null;
		if (conn == null) {
			try {
//				String dbName = FileLocator.toFileURL(
//						FrameworkUtil.getBundle(ConfigDB.class).getEntry(""))
//						.getPath()
//						+ "data/ConfigData.db";
				String dbName = FileLocator.toFileURL(Platform.getBundle("NNM.vdatabase").getEntry("data")).getPath()+"vData.db";
				Class.forName("org.sqlite.JDBC");
				conn = DriverManager.getConnection("jdbc:sqlite:" + dbName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return conn;
	}

	public static int excute(String sql, Connection connection) {
		Statement statement;
		int result = 0;
		try {
			statement = connection.createStatement();
			statement.setQueryTimeout(30);
			result = statement.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public static ResultSet query(String sql, Connection connection) {
		Statement statement;
		ResultSet result = null;
		try {
			statement = connection.createStatement();
			statement.setQueryTimeout(30);
			result = statement.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public static int delete(String sql, Connection connection) {
		Statement statement;
		int result = 0;
		try {
			statement = connection.createStatement();
			statement.setQueryTimeout(30);
			result = statement.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public static void batchInsert(
			Map<String, Pair<String, List<IfRec>>> ifprop_list,
			Connection connection,boolean cleard) {
		if (DBManage.Ip2Deviceids.size() == 0)
			return;
		if(cleard)
		delete("delete from ports", connection);
		try {

			PreparedStatement prep = connection
					.prepareStatement("insert into ports (id,desc,pindex,portnum,porttype,mac,speed,alias) values (?, ?, ?, ?, ?, ?, ?,?);");
			for (String tempip : ifprop_list.keySet()) {
				String devid = DBManage.Ip2Deviceids.get(tempip);
				if (devid != null) {
					Pair<String, List<IfRec>> ppair = ifprop_list.get(tempip);
					List<IfRec> portss = ppair.getSecond();
					if (portss == null)
						continue;
					for (IfRec ifRec : portss) {
						String desc = ifRec.getIfDesc();
						if (desc == null)
							desc = "";
						String index = ifRec.getIfIndex();
						if (index == null)
							index = "";
						String portnum = ifRec.getIfPort();
						if (portnum == null)
							portnum = "";
						String portype = ifRec.getIfType();
						if (portype == null)
							portype = "";
						String mac = ifRec.getIfMac();
						if (mac == null)
							mac = "";
						String speed = ifRec.getIfSpeed();
						if (speed == null)
							speed = "";
						String alias=ifRec.getAlias();
						if(alias==null)
							alias="";
						prep.setString(1, devid);
						prep.setString(2, desc);
						prep.setString(3, index);
						prep.setString(4, portnum);
						prep.setString(5, portype);
						prep.setString(6, mac);
						prep.setString(7, speed);
						prep.setString(8, alias);
						prep.addBatch();
					}

				}
			}
			connection.setAutoCommit(false);
			prep.executeBatch();
			connection.setAutoCommit(true);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void batchInsertDevice(Connection connection, Map<String, IDBody> devlist,boolean cleand) {
		if (DBManage.Ip2Deviceids.size() == 0)
			return;
		if(cleand)
		delete("delete from devices", connection);
		try {

			PreparedStatement prep = connection
					.prepareStatement("insert into devices (nid,descrip,username,password,authorization,privacy,privacypass) values (?, ? , ?, ?,?,?,?);");
			for (String tempip : devlist.keySet()) {
				if( !DBManage.Ip2Deviceids.containsKey(tempip))
					continue;
				String devid = DBManage.Ip2Deviceids.get(tempip);
				if (devid != null) {
					IDBody ibody = devlist.get(tempip);
					if (ibody == null)
						continue;
					String desc = ibody.getSysDesc();
					if (desc == null)
						desc = "";
					String username=ibody.getSecurityName();
					if(username==null)
						username="";
					String password=ibody.getAuthPass();
					if(password==null)
						password="";
					String authorization=ibody.getAuthProtocol();
					if (authorization==null)
						authorization="";
					String privacy=ibody.getPrivacyProtocol();
					if(privacy==null)
						privacy="";
					String privacypass=ibody.getPrivacyPass();
					if(privacypass==null)
						privacypass="";
                    
					prep.setString(1, devid);
					prep.setString(2, desc);
					prep.setString(3, username);
					prep.setString(4, password);
					prep.setString(5, authorization);
					prep.setString(6, privacy);
					prep.setString(7, privacypass);
					prep.addBatch();

				}
			}
			connection.setAutoCommit(false);
			prep.executeBatch();
			connection.setAutoCommit(true);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void close(Connection conn) {
		try {
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
