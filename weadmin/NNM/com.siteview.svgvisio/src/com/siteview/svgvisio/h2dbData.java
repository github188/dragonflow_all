package com.siteview.svgvisio;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class h2dbData {

	String dbname = "svgdb";
	String sourceURL = "jdbc:h2:";
	String user = "sa";
	String key = "sa";

	public h2dbData() {
		// TODO Auto-generated constructor stub
	}

	public h2dbData(String path) {
		this.sourceURL = sourceURL + path + dbname;
		try {
			Class.forName("org.h2.Driver");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return
	 */
	private Connection getConn() {
		Connection Conn = null;
		try {
			Conn = DriverManager.getConnection(sourceURL, user, key);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Conn;
	}

	/**
	 * 
	 * @param ips
	 *            192.168.6.5,192.168.6.4...
	 * @param links
	 * @param lines
	 * @param svg
	 *            :svg file content
	 * @param name
	 *            :svg file name
	 * @return
	 */
	public boolean addSvg(String ips, String links, String lines, String svg,
			String name) {
		Connection Conn = this.getConn();
		if (Conn == null)
			return false;
		try {
			Statement stmt = Conn.createStatement();
			String sql = "INSERT INTO SVGTABLE(name,ips,links,lines,svg) VALUES('"
					+ name
					+ "', '"
					+ ips
					+ "','"
					+ links
					+ "','"
					+ lines
					+ "','" + svg + "') ";
			stmt.execute(sql);
			stmt.close();
			Conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public Map<String, String> getSvg(String name) {
		Connection Conn = this.getConn();
		if (Conn == null)
			return null;
		Map<String, String> Result = new HashMap<>();
		try {
			Statement stmt = Conn.createStatement();
			String SQL = "select ips,links,lines,svg,groups,devices from svgtable where vname ='"
					+ name + "'";
			ResultSet rset = stmt.executeQuery(SQL);
			String ips = "";
			String links = "";
			String lines = "";
			String svg = "";
			String groups ="";
			String devices ="";
			while (rset.next()) {
				ips = rset.getString("ips");
				links = rset.getString("links");
				lines = rset.getString("lines");
				svg = rset.getString("svg");
				groups = rset.getString("groups");
				devices = rset.getString("devices");
			}
			Result.put("ips", ips);
			Result.put("links", links);
			Result.put("lines", lines);
			Result.put("svg", svg);
			Result.put("groups", groups);
			Result.put("devices", devices);
			rset.close();
			stmt.close();
			Conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		return Result;

	}

}
