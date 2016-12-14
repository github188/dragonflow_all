package com.siteview.leveltop.draw2d.container;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siteview.leveltop.draw2d.pojo.EdgeInfo;
import com.siteview.leveltop.draw2d.pojo.NodeInfo;

import Siteview.SiteviewException;
import Siteview.Api.ISiteviewApi;
import Siteview.Database.IDbConnection;

public class DitryContainer {
	
	public static final String ADD_NODEINFO="ADD_NODEINFO";
	public static final String UPDATE_NODEINFO="UPDATE_NODEINFO";
	public static final String DELETE_NODEINFO="DELETE_NODEINFO";
	
	public static final String ADD_EDGEINFO="ADD_EDGEINFO";
	public static final String UPDATE_EDGEINFO="UPDATE_EDGEINFO";
	public static final String DELETE_EDGEINFO="DELETE_NODEINFO";
	
	private Map<String,List<NodeInfo>> nodeInfoMap;
	private Map<String,List<EdgeInfo>> edgeInfoMap;
	
	public DitryContainer(){
		this.nodeInfoMap  = new HashMap<String,List<NodeInfo>>();
		this.edgeInfoMap  = new HashMap<String,List<EdgeInfo>>();
	}
	
	public void addNodeInfo(NodeInfo nodeInfo){
		if(!nodeInfoMap.containsKey(ADD_NODEINFO)){
			nodeInfoMap.put(ADD_NODEINFO,new ArrayList<NodeInfo>());
		}
		nodeInfoMap.get(ADD_NODEINFO).add(nodeInfo);
	}
	
	public void updateNodeInfo(NodeInfo nodeInfo){
		if(!nodeInfoMap.containsKey(UPDATE_NODEINFO)){
			nodeInfoMap.put(UPDATE_NODEINFO,new ArrayList<NodeInfo>());
		}
		nodeInfoMap.get(UPDATE_NODEINFO).add(nodeInfo);
	}
	
	public void deleteNodeInfo(NodeInfo nodeInfo){
		if(!nodeInfoMap.containsKey(DELETE_NODEINFO)){
			nodeInfoMap.put(DELETE_NODEINFO,new ArrayList<NodeInfo>());
		}
		nodeInfoMap.get(DELETE_NODEINFO).add(nodeInfo);
	}
	
	public void addEdgeInfo(EdgeInfo edgeInfo){
		if(!edgeInfoMap.containsKey(ADD_EDGEINFO)){
			edgeInfoMap.put(ADD_EDGEINFO,new ArrayList<EdgeInfo>());
		}
		edgeInfoMap.get(ADD_EDGEINFO).add(edgeInfo);
	}
	
	public void updateEdgeInfo(EdgeInfo edgeInfo){
		if(!edgeInfoMap.containsKey(UPDATE_EDGEINFO)){
			edgeInfoMap.put(UPDATE_EDGEINFO,new ArrayList<EdgeInfo>());
		}
		edgeInfoMap.get(UPDATE_EDGEINFO).add(edgeInfo);
	}
	
	public void deleteEdgeInfo(EdgeInfo edgeInfo){
		if(!edgeInfoMap.containsKey(DELETE_EDGEINFO)){
			edgeInfoMap.put(DELETE_EDGEINFO,new ArrayList<EdgeInfo>());
		}
		edgeInfoMap.get(DELETE_EDGEINFO).add(edgeInfo);
	}
	
	public void storeInDatabase(ISiteviewApi siteviewApi,String xml) throws SiteviewException{
		IDbConnection databaseConnection = null;
		Connection connection = null;
		try{
			String loginName = siteviewApi.get_AuthenticationService().get_CurrentLoginId();
			String safegroup_id = siteviewApi.get_AuthenticationService().get_CurrentSecurityGroupId();
			
			databaseConnection = siteviewApi.get_NativeSQLSupportService().get_DatabaseConnection().get_Connection();
			connection = databaseConnection.get_Connection();
			connection.setAutoCommit(false);
			
			//delete node info
			if(nodeInfoMap.get(DELETE_NODEINFO)!=null&&nodeInfoMap.get(DELETE_NODEINFO).size()>0){
				PreparedStatement pst = connection.prepareStatement("delete from NetWorkNode where NodeRelationMonitor=?");
				for(NodeInfo nodeinfo:nodeInfoMap.get(DELETE_NODEINFO)){
					pst.setString(1,nodeinfo.getResource_id());
					pst.addBatch();
				}
				pst.executeBatch();
				pst.close();
			}
			
			//delete edge info
			if(edgeInfoMap.get(DELETE_EDGEINFO)!=null&&edgeInfoMap.get(DELETE_EDGEINFO).size()>0){
				PreparedStatement pst =  connection.prepareStatement("delete from NetworkRelation where FromNode=? and ToNode=?");
				for(EdgeInfo edgeInfo:edgeInfoMap.get(DELETE_EDGEINFO)){
					pst.setString(1,edgeInfo.getSourceResourceId());
					pst.setString(2,edgeInfo.getTargetResourceId());
					pst.addBatch();
				}
				pst.executeBatch();
				pst.close();
			}
			
			//update node info
			if(nodeInfoMap.get(UPDATE_NODEINFO)!=null&&nodeInfoMap.get(UPDATE_NODEINFO).size()>0){
				PreparedStatement pst = connection.prepareStatement("update NetWorkNode set NodeName=?,LastModDateTime=NOW(),LastModBy=? where NodeRelationMonitor=?");
				for(NodeInfo nodeinfo:nodeInfoMap.get(UPDATE_NODEINFO)){
					pst.setString(1,nodeinfo.getResource_alias());
					pst.setString(2,loginName);
					pst.setString(3,nodeinfo.getResource_id());
					pst.addBatch();
				}
				pst.executeBatch();
				pst.close();
			}
			
			//add node info
			if(nodeInfoMap.get(ADD_NODEINFO)!=null&&nodeInfoMap.get(ADD_NODEINFO).size()>0){
				PreparedStatement pst =  connection.prepareStatement("INSERT INTO NetWorkNode VALUES(UPPER(REPLACE(UUID(),'-','')),NOW(),?,NOW(),?,'',?,?,?,?,?)");
				for(NodeInfo nodeinfo:nodeInfoMap.get(ADD_NODEINFO)){
					pst.setString(1,loginName);
					pst.setString(2,loginName);
					pst.setString(3,"");
					pst.setString(4,nodeinfo.getResource_alias());
					pst.setString(5,nodeinfo.getResource_id());
					pst.setString(6,"");
					pst.setString(7,"");
					pst.addBatch();
				}
				pst.executeBatch();
				pst.close();
			}
			
			// add edge info
			if(edgeInfoMap.get(ADD_EDGEINFO)!=null&&edgeInfoMap.get(ADD_EDGEINFO).size()>0){
				PreparedStatement pst =  connection.prepareStatement("INSERT INTO NetworkRelation VALUES(UPPER(REPLACE(UUID(),'-','')),NOW(),?,NOW(),?,'',?,?,?,?)");
				for(EdgeInfo edgeInfo:edgeInfoMap.get(ADD_EDGEINFO)){
					pst.setString(1,loginName);
					pst.setString(2,loginName);
					pst.setString(3,edgeInfo.getSourceResourceId());
					pst.setString(4,"");
					pst.setString(5,edgeInfo.getTargetResourceId());
					pst.setString(6,"");
					pst.addBatch();
				}
				pst.executeBatch();
				pst.close();
			}
			
			//update node ip and type
			String updateNodeInfoSql = 
			"UPDATE NetWorkNode,Equipment,("+
			"   SELECT a.EqName,c.RecId AS NodeType FROM ("+
			"	   SELECT EqName,ParentId FROM EquipmentTypeRel WHERE ParentId!=''"+
			"   ) a,("+
			"	   SELECT RecId,TypeAlias AS Category,(CASE WHEN TypeAlias='网络设备' THEN 1 WHEN TypeAlias='服务器' THEN 2 ELSE 3 END) AS TypePosition FROM EquipmentTypeRel"+
			"   ) b,NetWorkNodeType c "+
			"   WHERE a.ParentId = b.RecId AND c.TypePosition=b.TypePosition"+ 
			") AS NodeRelation "+
			"SET NetWorkNode.NodeId = Equipment.ServerAddress , NetWorkNode.NodeType=NodeRelation.NodeType "+
			"WHERE NetWorkNode. NodeId='' AND NetWorkNode.NodeType='' AND NetWorkNode.NodeRelationMonitor=Equipment.RecId AND Equipment.EquipmentType = NodeRelation.EqName";
			
			Statement stmt = connection.createStatement();
			stmt.executeUpdate(updateNodeInfoSql);
			stmt.close();
			
			String updateEdgeInfoSql = 
			"UPDATE NetWorkRelation,("+
			"		SELECT n.RecId NodeId,e.RecId AS EquipmentId,e.EquipmentType,n.NodeType  FROM NetWorkNode n,Equipment e WHERE n.NodeRelationMonitor=e.RecId"+
			") a,("+
			"		SELECT n.RecId NodeId,e.RecId AS EquipmentId,e.EquipmentType,n.NodeType  FROM NetWorkNode n,Equipment e WHERE n.NodeRelationMonitor=e.RecId"+
			") b,NetWorkNodeTypeRelation r "+
			"SET NetWorkRelation.RelationLevel = r.RecId "+
			"WHERE NetWorkRelation.RelationLevel='' AND NetWorkRelation.FromNode = a.EquipmentId AND NetWorkRelation.ToNode = b.EquipmentId AND ((r.FromType=a.NodeType AND r.ToType=b.NodeType) OR (r.FromType=b.NodeType AND r.ToType=a.NodeType))";
			stmt = connection.createStatement();
			stmt.executeUpdate(updateEdgeInfoSql);
			stmt.close();
			
			boolean hasExistsMapData = false;
			
			String checkLevelMapDataExistsSql =
			"select RecId from LevelTopMap";
			
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(checkLevelMapDataExistsSql);
			if(rs.next()){
				hasExistsMapData = true;
			}
			rs.close();
			stmt.close();
			
			if(!hasExistsMapData){
				String insertMapDatasql = "insert into LevelTopMap values(UPPER(REPLACE(UUID(),'-','')),now(),?,now(),?,?,?,?,?)";
				PreparedStatement pstmt = connection.prepareStatement(insertMapDatasql);
				pstmt.setString(1,loginName);
				pstmt.setString(2,loginName);
				pstmt.setString(3,"");
				pstmt.setClob(4,new StringReader(xml));
				pstmt.setString(5,"");
				pstmt.setString(6,safegroup_id);
				pstmt.executeUpdate();
			}
			else{
				String updateMapDatasql = "update LevelTopMap set LastModBy=?,LastModDateTime=now(),mapName=?,mapData=?,mapVersion=?";
				PreparedStatement pstmt = connection.prepareStatement(updateMapDatasql);
				pstmt.setString(1,loginName);
				pstmt.setString(2,"");
				pstmt.setClob(3,new StringReader(xml));
				pstmt.setString(4,"");
				pstmt.executeUpdate();
			}
			
			connection.commit();
		}
		catch(Exception e){
			if(connection!=null){
				try {
					connection.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			throw new SiteviewException(e.getMessage(),e);
		}
		finally{
			if(connection!=null){
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(databaseConnection!=null){
				databaseConnection.Close();
			}
			clearDirtyList();
		}
	}
	
	public void clearDirtyList(){
		nodeInfoMap.clear();
		edgeInfoMap.clear();
	}
}
