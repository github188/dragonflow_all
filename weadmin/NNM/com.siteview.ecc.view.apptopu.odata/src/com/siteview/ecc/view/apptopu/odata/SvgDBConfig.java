package com.siteview.ecc.view.apptopu.odata;

import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.rap.json.JsonObject;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.SiteviewException;
import Siteview.Api.ISiteviewApi;
import Siteview.Database.IDbDataParameterCollection;
import Siteview.Database.SqlParameterCollection;
import Siteview.Windows.Forms.ConnectionBroker;


/**
 * 
 * bin.liu
 *
 */
public class SvgDBConfig {
	private ISiteviewApi api;
	
	public SvgDBConfig(ISiteviewApi api){
		this.api =api;
		
	}
//	// h2���ݿ�����
//	public static final String H2_DATABASE = "svgdb";
//	public static final String H2_USER = "sa";
//	public static final String H2_PASSWORD = "sa";
//	public static final String H2_DRIVER_CLASS = "org.h2.Driver";
//
//	// sql������svg
//	public static final String GET_ALL_SVG = "select vname,show_name,svg from svgtable";
//	public static final String GET_SVG_BY_VNAME = "select * from svgtable where vname=?";
//	public static final String DELETE_SVG_BY_VNAME = "delete from svgtable where vname=?";
//	public static final String GET_ALL_SVG_VNAME = "select distinct vname from svgtable";
//	public static final String UPDATE_SHOW_VNAME = "update svgtable set show_name=? where vname=?";
//	private static String h2Url = "jdbc:h2:";
//	static{
//		try {
//			h2Url = h2Url+FileLocator.toFileURL(Platform.getBundle("com.siteview.ecc.view.apptopu.store").getEntry("svgdb")).getPath()+H2_DATABASE;
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private Connection getConnection() {
//		try {
//			Class driver_class = Class.forName(H2_DRIVER_CLASS);driver_class.getClassLoader();
//			return DriverManager.getConnection(h2Url,
//					H2_USER, H2_PASSWORD);
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return null;
//
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//
//	SvgDBConfig() {
//	}
//	
//
//	public List<JsonObject> loadAll() {
//		Connection con = getConnection();
//		if (con == null)
//			return null;
//		Statement statement = null;
//		ResultSet result = null;
//		List<JsonObject> models = new ArrayList<JsonObject>();
//		try {
//			statement = con.createStatement();
//			result = statement.executeQuery(GET_ALL_SVG);
//			while (result.next()) {
//				JsonObject model = new JsonObject();
//				model.add("Vname", result.getString("vname"));
//				model.add("Svg", result.getString("svg"));
//				model.add("Showname", result.getString("show_name"));
//				models.add(model);
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return null;
//		}finally{
//			close(con,statement,result);
//		}
//		return models;
//	}
//
//
//	public Map<String,String> load(String vname) {
//		Connection con = getConnection();
//		if (con == null)
//			return null;
//		PreparedStatement statement = null;
//		ResultSet result = null;
//		 Map<String,String> temprr = new HashMap<String,String>();
//		try {
//			statement = con.prepareStatement(GET_SVG_BY_VNAME);
//			statement.setString(1, vname);
//			result = statement.executeQuery();
//			while (result.next()) {
//				temprr.put("Ips", result.getString("ips"));
//				temprr.put("Vname", result.getString("vname"));
//				temprr.put("Links", result.getString("links"));
//				temprr.put("Lines", result.getString("lines"));
//				temprr.put("Showname", result.getString("show_name"));
//				temprr.put("Svg", result.getString("svg"));
//				temprr.put("Groups", result.getString("groups"));
//				temprr.put("Devices", result.getString("devices"));
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return null;
//		}finally{
//			close(con,statement,result);
//		}
//		return temprr;
//	}
//	
//	public boolean delete(String vname) {
//		Connection connection = getConnection();
//		if(connection==null)
//			return false;
//		PreparedStatement pst = null;
//		try {
//			pst = connection.prepareStatement(DELETE_SVG_BY_VNAME);
//			pst.setString(1, vname);
//			if(pst.executeUpdate()!=0){
//				return true;
//			}else{
//				return false;
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return false;
//		}finally{
//			close(connection,pst,null);
//		}
//	}
//	
//	private void close(Connection connection,Statement statement,ResultSet resultSet){
//		if(resultSet!=null){
//			try {
//				resultSet.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//		if(statement!=null){
//			try {
//				statement.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//		if(connection!=null){
//			try {
//				connection.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//	
//	public List<String> loadAllVname() {
//		Connection con = getConnection();
//		if (con == null)
//			return null;
//		Statement statement = null;
//		ResultSet result = null;
//		List<String> vnames = new ArrayList<String>();
//		try {
//			statement = con.createStatement();
//			result = statement.executeQuery(GET_ALL_SVG_VNAME);
//			while (result.next()) {
//				vnames.add(result.getString("vname"));
//			}
//			return vnames;
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return null;
//		}finally{
//			close(con,statement,result);
//		}
//	}
//	
//	private String convertStringFromClob(Clob clob){
//		Reader reader = null;
//		StringBuilder result = new StringBuilder();
//		try {
//			char[] chars = new char[1024];
//			reader = clob.getCharacterStream();
//			int length = 0;
//			while((length=reader.read(chars))!=-1){
//				result.append(new String(chars,0,length));
//			}
//			return result.toString();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}finally{
//			if(reader!=null){
//				try {
//					reader.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		return "";
//	}
//	
//	
//	public boolean updateShowname(String vname, String newname) {
//		Connection con = getConnection();
//		if(con == null)
//			return false;
//		PreparedStatement preparedStatement = null;
//		try {
//			preparedStatement = con.prepareStatement(UPDATE_SHOW_VNAME);
//			preparedStatement.setString(1, newname);
//			preparedStatement.setString(2, vname);
//			return preparedStatement.executeUpdate()==0?false:true;
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}finally{
//			close(con,preparedStatement,null);
//		}
//		return false;
//	}

	
	

	public List<JsonObject>loadAll() {
		List<JsonObject> models = new ArrayList<JsonObject>();
		try {
			IDbDataParameterCollection parameters = new SqlParameterCollection();
			DataTable dataTable = api.get_NativeSQLSupportService().ExecuteNativeSQLQuery("select VNAME,SVG,SHOW_NAME from svgtable", parameters);
			for(DataRow row :dataTable.get_Rows()){
				JsonObject model = new JsonObject();
				model.add("Vname",(String) row.get("VNAME"));
				model.add("Svg", (String) row.get("SVG"));
				model.add("Showname", (String) row.get("SHOW_NAME"));
				models.add(model);
			}
			return models;
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
		return models;
	}

	/* (non-Javadoc)
	 * @see com.siteview.visualview.dao.SvgDao#load(java.lang.String)
	 */

	public  Map<String,String> load(String vname) {
		 Map<String,String> temprr = new HashMap<String,String>();
		try {
			IDbDataParameterCollection parameters = new SqlParameterCollection();
			DataTable dataTable = api.get_NativeSQLSupportService().ExecuteNativeSQLQuery(String.format("select * from svgtable where VNAME = '%s'",vname), parameters);
			for(DataRow row :dataTable.get_Rows()){
				temprr.put("Ips", (String) row.get("ips"));
				temprr.put("Vname", vname);
				temprr.put("Links", (String) row.get("links"));
				temprr.put("Lines", (String) row.get("lines"));
				temprr.put("Showname",(String) row.get("show_name"));
				temprr.put("Svg", (String) row.get("svg"));
				temprr.put("Groups",  (String) row.get("groups"));
				temprr.put("Devices", (String) row.get("devices"));
				break;
			}
			return temprr;
		} catch (SiteviewException e) {
			e.printStackTrace();
			return temprr;
		}
	}

	/* (non-Javadoc)
	 * @see com.siteview.visualview.dao.SvgDao#delete(java.lang.String)
	 */

	public boolean delete(String vname) {
		try {
			IDbDataParameterCollection parameters = new SqlParameterCollection();
			int result = api.get_NativeSQLSupportService().ExecuteNativeSQL(String.format("delete from svgtable where VNAME = '%s'", vname), parameters);
			if(result!=0){
				return true;
			}else{
				return false;
			}
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.siteview.visualview.dao.SvgDao#loadAllVname()
	 */

	public List<String> loadAllVname() {
		List<String> results = new ArrayList<String>();
		try {
			IDbDataParameterCollection parameters = new SqlParameterCollection();
			DataTable dataTable = api.get_NativeSQLSupportService().ExecuteNativeSQLQuery("select VNAME from svgtable", parameters);
			for(DataRow row :dataTable.get_Rows()){
				results.add((String) row.get("VNAME"));
			}
			return results;
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
		return results;
	}

	/* (non-Javadoc)
	 * @see com.siteview.visualview.dao.SvgDao#updateShowname(java.lang.String, java.lang.String)
	 */

	public boolean updateShowname(String vname, String newname) {
		try {
			IDbDataParameterCollection parameters = new SqlParameterCollection();
			int result = api.get_NativeSQLSupportService().ExecuteNativeSQL(String.format("update svgtable set SHOW_NAME = '%s' where VNAME = '%s'", newname,vname), parameters);
			if(result!=0){
				return true;
			}else{
				return false;
			}
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
		return false;
	}

	

}
