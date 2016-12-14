package com.siteview.ecc.yft.odata;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Siteview.Convert;
import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.SiteviewException;
import Siteview.Api.ISiteviewApi;
import Siteview.Database.IDbConnection;
import Siteview.DbLiaison.SymmetricAlgorithm;
import siteview.IFunctionExtension;

public class GetYFTGisSubsetInfo implements IFunctionExtension {

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api,
			Map<String, String> inputParamMap) {

		List<Map<String,String>> listMap = new ArrayList<Map<String,String>>();
		
		try {
			String safegroup_id = inputParamMap.get("SAFEGROUP_ID")!=null?inputParamMap.get("SAFEGROUP_ID").trim():api.get_AuthenticationService().get_CurrentSecurityGroupId(); 
			StringBuffer sb = new StringBuffer();
			sb.append("select Organization.o_code from OrganizeAndSafeGroupRel,Organization where safegroup_id = ").append("'").append(safegroup_id).append("' and Organization.RecId=OrganizeAndSafeGroupRel.organize_id order by Organization.o_code");
			DataTable dt = api.get_NativeSQLSupportService().ExecuteNativeSQLQuery(sb.toString(),null);
			
			if(dt.get_Rows().size()>0){ 
				String organize_id = dt.get_Rows().get(0).get_Item("o_code").toString();
					
//				String sql = "SELECT * FROM (SELECT o.*,oa.safegroup_id FROM Organization o,OrganizeAndSafeGroupRel oa WHERE o.RecId=oa.organize_id AND FIND_IN_SET(o_code,'%s') AND o_code<>'%s') otemp, Monitor e WHERE e.MonitorType='Ping' AND otemp.o_name=e.Title ";
//				String sql = "SELECT * FROM (SELECT o.*,oa.safegroup_id FROM Organization o,OrganizeAndSafeGroupRel oa WHERE o.RecId=oa.organize_id AND FIND_IN_SET(o_code,'%s')) otemp, Monitor e WHERE e.MonitorType='Ping' AND otemp.o_name=e.Title ";
				String sql = "SELECT a.*, GOOD, ERROR, WARNING, DISABLED FROM ( SELECT * FROM (SELECT ou.*, oa.safegroup_id FROM Organization ou, OrganizeAndSafeGroupRel oa WHERE ou.RecId=oa.organize_id AND FIND_IN_SET(o_code,'%s')) o,"
						+ " ( SELECT GroupName NAME, STATUS FROM EccGroup UNION SELECT Title NAME, MonitorStatus STATUS FROM Monitor) s WHERE o.o_name=s.NAME ) a "
						+ "LEFT JOIN (SELECT o.parentId, SUM(CASE WHEN STATUS = 'good' THEN 1 ELSE 0 END) GOOD, SUM(CASE WHEN STATUS = 'error' THEN 1 ELSE 0 END) ERROR, "
						+ "SUM(CASE WHEN STATUS='warning' THEN 1 ELSE 0 END) WARNING, SUM(CASE WHEN STATUS='disabled' THEN 1 ELSE 0 END) DISABLED FROM "
						+ "(SELECT ou.*, oa.safegroup_id FROM Organization ou, OrganizeAndSafeGroupRel oa WHERE ou.RecId=oa.organize_id ) o, "
						+ "(SELECT GroupName NAME, STATUS FROM EccGroup UNION SELECT Title NAME, MonitorStatus STATUS FROM Monitor where MonitorStatus!='disabled') s WHERE o.o_name=s.NAME GROUP BY o.parentId) b ON a.o_code=b.parentId ";
				
				
				if(inputParamMap.get("MAPLV") != null)
				{
					sql = String.format(sql, getOrganizationIds(api, organize_id));
					sql = sql + String.format("WHERE MAPLV='%s'", inputParamMap.get("MAPLV"));
				}else
				{
					sql = String.format(sql, organize_id);
				}
				
				dt = api.get_NativeSQLSupportService().ExecuteNativeSQLQuery(sql,null);
				
				for(DataRow dr:dt.get_Rows()){
					HashMap<String,String> map = new HashMap<String,String>();
					map.put("ID",dr.get_Item("o_code").toString());
					map.put("PARENT_ID",ifNullToEmpty(dr.get_Item("parentId")));
					map.put("NAME",dr.get_Item("o_name").toString());
					map.put("HOST",dr.get_Item("o_host").toString());
					map.put("IN_HOST",ifNullToEmpty(dr.get_Item("in_host")));
					map.put("USER",dr.get_Item("prop_login_user").toString());
					
					String passwd = dr.get_Item("prop_login_pwd").toString();
					SymmetricAlgorithm s = new SymmetricAlgorithm("AES");
					passwd = new String(s.decrypt(Convert.FromBase64String(passwd)));

					map.put("PASSWD",passwd);
					map.put("SAFEGROUP_ID",dr.get_Item("safegroup_id").toString());
					map.put("COORDINATES",dr.get_Item("o_desc").toString());
					map.put("STATUS",dr.get_Item("status").toString());
					map.put("MAPLV",dr.get_Item("MAPLV").toString());
					
					map.put("GOOD",ifNullToEmpty(dr.get_Item("GOOD")));
					map.put("ERROR",ifNullToEmpty(dr.get_Item("ERROR")));
					map.put("WARNING",ifNullToEmpty(dr.get_Item("WARNING")));
					map.put("DISABLED",ifNullToEmpty(dr.get_Item("WARNING")));
					
					listMap.add(map);
				}
			}
		} catch (SiteviewException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return listMap;
	}
	
	
	
	private String ifNullToEmpty(Object object)
	{
		return object == null ? "" : object.toString();
	}
	
	
	public String getOrganizationIds(ISiteviewApi api, String id) throws Exception{
		String cTemp = id;
		String pTemp = "$";
		StringBuffer tempBuffer = new StringBuffer();
		IDbConnection database_connection = null;
		Connection connection = null;
		Statement stmt = null;
		try{
			database_connection = api.get_NativeSQLSupportService().get_DatabaseConnection().get_Connection();
			connection = database_connection.get_Connection();
			stmt = connection.createStatement();
			while(cTemp!=null){
				tempBuffer.setLength(0);
				pTemp = tempBuffer.append(pTemp).append(",").append(cTemp).toString();
				tempBuffer.setLength(0);
				tempBuffer.append("SELECT GROUP_CONCAT(").append(getIdNameInField()).append(") as cTemp FROM ").append(getTreeNodeTableName()).append(" WHERE FIND_IN_SET(").append(getParentIdNameInField()).append(",").append("'").append(cTemp).append("'").append(")>0");
				ResultSet rs = stmt.executeQuery(tempBuffer.toString());
				if(rs.next()){
					cTemp = rs.getString("cTemp");
				}else{
					cTemp = null;
				}
				rs.close();
			}
			stmt.close();
			return pTemp;
		
		}catch(Exception ex){
			throw new SiteviewException(ex.getMessage(),ex);
		
		}finally{
			if(database_connection!=null){
				if(connection!=null){
					connection.close();
				}
				database_connection.Close();
			}
		}
	}
	
	
	
	public String getTreeNodeTableName() {
		return "Organization";
	}

	public String getIdNameInField() {
		return "o_code";
	}

	public String getParentIdNameInField() {
		return "parentId";
	}
}
