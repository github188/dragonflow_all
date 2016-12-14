package com.siteview.leveltop.draw2d.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.siteview.NNM.uijobs.ScanWorker;
import com.siteview.leveltop.draw2d.pojo.EdgeInfo;
import com.siteview.leveltop.draw2d.pojo.NodeInfo;
import com.siteview.leveltop.draw2d.pojo.ResourceTypeInfo;
import com.siteview.nnm.data.model.svPoint;
import com.siteview.utils.db.DBQueryUtils;
import com.siteview.utils.db.DBUtils;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.SiteviewException;
import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;

public class LevelTopDataUtils {

	private static LevelTopDataUtils instance = new LevelTopDataUtils();

	public static LevelTopDataUtils getInstance() {
		return instance;
	}

	public List<Map<String, String>> GetNetWorkNodes(ISiteviewApi api) {
		List<Map<String, String>> outlist = new ArrayList<Map<String, String>>();
		// List<Map<String, String>> outP = new ArrayList<Map<String,
		// String>>();
		String type = "0";
		// Map<String, String> outM = new HashMap<String, String>();

		String sql = "SELECT n.NodeRelationMonitor as RecId,n.NodeType,n.NodeId,n.NodeName,e.EquipmentStatus,e.EquipmentType,t.TypePosition FROM NetWorkNode n LEFT JOIN (SELECT EquipmentStatus,RecId,EquipmentType FROM Equipment )e ON n.NodeRelationMonitor=e.RecId ,NetWorkNodeType t WHERE  t.RecId = n.nodeType AND TopType='0'";

		DataTable dt = DBQueryUtils.Select(sql, api);
		Map<String, String> out0 = new HashMap<String, String>();
		out0.put("id", "0");
		out0.put("pid", "");
		out0.put("name", "主页");
		out0.put("open", "true");
		out0.put("type", "");
		out0.put("model", "");
		out0.put("textureURL", "");
		outlist.add(out0);
		String ids = "";
		String sql0 = "select * from NetWorkNodeType where TopType='%s'";
		DataTable dt0 = DBUtils.select(String.format(sql0, type), api);
		List<String> list = new ArrayList<String>();
		for (DataRow dr : dt0.get_Rows()) {
			Map<String, String> out1 = new HashMap<String, String>();
			out1.put("id", dr.get("RecId").toString());
			if (ids.length() > 0)
				ids += ",";
			ids += " '" + dr.get("RecId").toString() + "' ";
			out1.put("pid", "0");
			out1.put("name", dr.get("TypeName") == null ? "" : dr.get("TypeName").toString());
			out1.put("typePosition", dr.get("TypePosition") == null ? "" : dr.get("TypePosition").toString());
			out1.put("open", "false");
			out1.put("type", "layer");
			out1.put("model", "");
			if (!list.contains(out1.get("id")))
				list.add(out1.get("id"));
			out1.put("textureURL", "layer_app.png");
			outlist.add(out1);
		}
		sql0 = "select n.RecId  nid,m.SysOID,e.EquipmentModel from NetWorkNode n ,Equipment e,Mib m where n.NodeRelationMonitor!='' and e.RecId=n.NodeRelationMonitor and m.MibModel=e.EquipmentModel and m.Vendor=e.ConnectionType";
		dt0 = DBQueryUtils.Select(sql0, api);
		Map<String, String> sysoid = new HashMap<String, String>();
		Map<String, String> sysmodel = new HashMap<String, String>();
		for (DataRow dr : dt0.get_Rows()) {
			sysoid.put(dr.get("nid").toString(), dr.get("SysOID") == null ? "" : dr.get("SysOID").toString());
			sysmodel.put(dr.get("nid").toString(), dr.get("EquipmentModel") == null ? "" : dr.get("EquipmentModel").toString());
		}
		sql0 = "select * from NetWorkNode where NodeType in (select RecId from NetWorkNodeType where TOPTYPE='%s')";
		dt0 = DBQueryUtils.Select(String.format(sql0, type), api);
		Map<String, String> localxy = new HashMap<String, String>();
		for (DataRow dr : dt0.get_Rows()) {
			String x = dr.get("XLocal") == null || dr.get("XLocal").toString().length() == 0 ? "0" : dr.get("XLocal").toString();
			String y = dr.get("YLocal") == null || dr.get("YLocal").toString().length() == 0 ? "0" : dr.get("YLocal").toString();
			localxy.put(dr.get("RecId").toString(), x + ":" + y);
		}
		Map<String, svPoint> getlayout = ScanWorker.getlayout(dt, DBQueryUtils.Select(String.format("SELECT NetWorkRelation.* FROM NetWorkRelation ,NetWorkNodeTypeRelation WHERE NetWorkRelation.RelationLevel = NetWorkNodeTypeRelation.RecId AND RelationCode='%s'", type), api), localxy);
		for (DataRow dr : dt.get_Rows()) {
			Map<String, String> out = new HashMap<String, String>();
			out.put("ip", dr.get("NodeId") == null ? "0" : dr.get("NodeId").toString());
			out.put("id", dr.get("RecId") == null ? "0" : dr.get("RecId").toString());
			out.put("pid", dr.get("NodeType") == null ? "0" : dr.get("NodeType").toString());
			if (!list.contains(out.get("pid")))
				continue;
			out.put("name", dr.get("NodeName") == null ? "0" : dr.get("NodeName").toString());
			String status = dr.get("EquipmentStatus") == null ? "good" : dr.get("EquipmentStatus").toString();
			out.put("status", status.length() == 0 ? "good" : status);
			out.put("open", "false");
			out.put("type", sysoid.get(out.get("id")) == null ? "" : sysoid.get(out.get("id")));
			out.put("textureURL", "");
			out.put("model", dr.get("EquipmentType").toString());
			out.put("TypePosition", dr.get("TypePosition").toString());
			out.put("px", "0");
			out.put("py", "0");
			svPoint sv = getlayout.get(dr.get("RecId").toString());
			if (sv != null) {
				out.put("px", sv.getPx() + "");
				out.put("py", sv.getPy() + "");
			}
			outlist.add(out);
		}
		return outlist;
	}

	public List<Map<String, String>> getNetWorkNodeLines(ISiteviewApi api) {
		List<Map<String, String>> outP = new ArrayList<Map<String, String>>();
		String type = "0";
		String sql = "select n.*,nw.RecId nid,nw.FromType ftype,nw.ToType ttype  from NetWorkRelation n,NetWorkNodeTypeRelation nw WHERE nw.RelationCode='%s' and nw.RecId=n.RelationLevel";
		DataTable dt = DBQueryUtils.Select(String.format(sql, type), api);
		for (DataRow dr : dt.get_Rows()) {
			Map<String, String> hashmap = new HashMap<String, String>();
			hashmap.put("RecId", dr.get("RecId").toString());
			hashmap.put("FromNode", dr.get("FromNode") == null ? "" : dr.get("FromNode").toString());
			hashmap.put("ToNode", dr.get("ToNode") == null ? "" : dr.get("ToNode").toString());
			hashmap.put("Status", "good");
			hashmap.put("FromLevelId", dr.get("ftype") == null ? "" : dr.get("ftype").toString());
			hashmap.put("ToLevelId", dr.get("ttype") == null ? "" : dr.get("ttype").toString());
			outP.add(hashmap);
		}
		return outP;
	}

	public Map<String, Integer> computeNodeRelationValue(List<Map<String, String>> netWorkNodeListMap, int width, int height) {
		int i = 0;
		int max_x = 0;
		int min_x = 0;
		int max_y = 0;
		int min_y = 0;

		for (Map<String, String> netWorkNodeMap : netWorkNodeListMap) {
			if (i > 3) {
				int px = Integer.parseInt(netWorkNodeMap.get("px"));
				int py = Integer.parseInt(netWorkNodeMap.get("py"));

				if (max_x == 0 || min_x == 0) {
					max_x = px;
					min_x = px;
				} else {
					if (px > max_x) {
						max_x = px;
					}
					if (px < min_x) {
						min_x = px;
					}
				}

				if (max_y == 0 || min_y == 0) {
					max_y = py;
					min_y = py;
				} else {
					if (py > max_y) {
						max_y = py;
					}
					if (py < min_y) {
						min_y = py;
					}
				}
			}
			i++;
		}

		int percent_x = max_x == min_x ? 1 : Math.round(width * 1f / (max_x - min_x));
		// int pyCenter = (max_y + min_y) / 2;
		int percent_y = max_y == min_y ? 1 : Math.round(height / (max_y - min_y));

		Map<String, Integer> maps = new LinkedHashMap<String, Integer>();
		maps.put("max_x", max_x);
		maps.put("min_x", min_x);
		maps.put("max_y", max_y);
		maps.put("min_y", min_y);
		maps.put("percent_x", percent_x);
		maps.put("percent_y", percent_y);

		return maps;
	}

	/*
	 * 创建节点信息
	 */
	public String createNetworkNode(ISiteviewApi siteviewApi, String equipmentId, String ip, String equipmentAlias, String equipmentType) throws SiteviewException {
		StringBuffer sqlbuffer = new StringBuffer();
		sqlbuffer.append("SELECT (CASE ").append("WHEN TypeAlias=").append("'").append("网络设备").append("'").append(" THEN ").append("'").append("网络").append("' ").append("WHEN TypeAlias=").append("'").append("服务器").append("'").append(" THEN ").append("'").append("服务").append("' ").append("ELSE ").append("'").append("应用").append("' ").append("END) AS TypeAlias ").append("FROM EquipmentTypeRel WHERE RecId IN (").append("SELECT ParentId FROM EquipmentTypeRel WHERE EqName=").append("'").append(equipmentType).append("'").append(")");

		String typeAlias = siteviewApi.get_NativeSQLSupportService().ExecuteNativeSQLQuery(sqlbuffer.toString(), null).get_Rows().get(0).get("TypeAlias").toString();
		sqlbuffer.setLength(0);
		sqlbuffer.append("SELECT * FROM NetworkNodeType WHERE TypeName like ").append("'%").append(typeAlias).append("%' and TopType=0");

		DataTable dt = siteviewApi.get_NativeSQLSupportService().ExecuteNativeSQLQuery(sqlbuffer.toString(), null);
		String nodeTypeId = null;
		if (dt.get_Rows().size() > 0) {
			nodeTypeId = dt.get_Rows().get(0).get("RecId").toString();
		}
		if (nodeTypeId != null) {
			BusinessObject bo_node = siteviewApi.get_BusObService().Create("NetWorkNode");
			bo_node.GetField("NodeType").SetValue(new SiteviewValue(nodeTypeId));
			bo_node.GetField("NodeId").SetValue(new SiteviewValue(ip));
			bo_node.GetField("NodeName").SetValue(new SiteviewValue(equipmentAlias));
			bo_node.GetField("NodeRelationMonitor").SetValue(new SiteviewValue(equipmentId));
			bo_node.SaveObject(siteviewApi, false, true);
			return bo_node.get_RecId();
		}
		return "";
	}

	/*
	 * 创建关系节点
	 */
	public void createNetworkNodeRelation(ISiteviewApi siteviewApi, String sourceEquipmentId, String destEquipmentId) throws SiteviewException {
		StringBuffer sqlbuffer = new StringBuffer();
		sqlbuffer.append("SELECT source.sourceId,source.sourceType,source.sourceTypeName,dest.destid,dest.destType,dest.destTypeName FROM (").append("SELECT NetworkNode.RecId AS sourceId,NodeType AS sourceType ,NetworkNodeType.TypeName AS sourceTypeName FROM NetworkNode,NetworkNodeType WHERE NetworkNode.NodeType=NetworkNodeType.RecId AND NetworkNode.RecId=").append("'").append(sourceEquipmentId).append("'").append(") AS source,(").append("SELECT NetworkNode.RecId AS destId  ,NodeType AS destType   ,NetworkNodeType.TypeName AS destTypeName   FROM NetworkNode,NetworkNodeType WHERE NetworkNode.NodeType=NetworkNodeType.RecId AND NetworkNode.RecId=").append("'").append(destEquipmentId).append("'").append(") AS dest");

		DataTable dt = siteviewApi.get_NativeSQLSupportService().ExecuteNativeSQLQuery(sqlbuffer.toString(), null);

		if (dt != null && dt.get_Rows().size() > 0) {
			DataRow row = dt.get_Rows().get(0);
			String sourceId = row.get("sourceId").toString();
			String destId = row.get("destId").toString();
			String sourceType = row.get("sourceType").toString();
			String destType = row.get("destType").toString();

			sqlbuffer.setLength(0);
			sqlbuffer.append("SELECT * FROM NetworkNodeTypeRelation WHERE FromType=").append("'").append(sourceType).append("'").append(" AND ToType=").append("'").append(destType).append("' ");
			sqlbuffer.append(" UNION ");
			sqlbuffer.append("SELECT * FROM NetworkNodeTypeRelation WHERE FromType=").append("'").append(destType).append("'").append(" AND ToType=").append("'").append(sourceType).append("' ");

			dt = siteviewApi.get_NativeSQLSupportService().ExecuteNativeSQLQuery(sqlbuffer.toString(), null);

			if (dt != null && dt.get_Rows().size() > 0) {
				String recId = dt.get_Rows().get(0).get("RecId").toString();
				BusinessObject bo_nodeRelation = siteviewApi.get_BusObService().Create("NetworkRelation");
				bo_nodeRelation.GetField("RelationLevel").SetValue(new SiteviewValue(recId));
				bo_nodeRelation.GetField("FromNode").SetValue(new SiteviewValue(sourceId));
				bo_nodeRelation.GetField("ToNode").SetValue(new SiteviewValue(destId));
				bo_nodeRelation.SaveObject(siteviewApi, false, true);
			}

		}
	}
	
	/*
	 * 获取简单的映射关系
	 */
	public Map<String,ResourceTypeInfo> getEquipmentSimpleTypeMap(ISiteviewApi api) throws SiteviewException{
		Map<String,ResourceTypeInfo> mapInfo = new HashMap<String,ResourceTypeInfo>();
		
		String sql = 
		"SELECT a.EqName,b.Category ,b.TypePosition,c.RecId as LevelId FROM ("+
		"	SELECT EqName,ParentId FROM EquipmentTypeRel WHERE ParentId!=''"+
		") a,("+
		"	SELECT RecId,TypeAlias AS Category,(CASE WHEN TypeAlias='网络设备' OR TypeAlias='防火墙' OR TypeAlias='负载均衡' OR TypeAlias='无线网络' THEN 1 WHEN TypeAlias='服务器' THEN 2 ELSE 3 END) AS TypePosition FROM EquipmentTypeRel"+
		") b,NetworkNodeType c "+
		"WHERE a.ParentId = b.RecId AND c.TypePosition=b.TypePosition"; 

		DataTable dt = api.get_NativeSQLSupportService().ExecuteNativeSQLQuery(sql,null);
		for(DataRow dr:dt.get_Rows()){
			ResourceTypeInfo info = new ResourceTypeInfo(dr.get_Item("EqName").toString(), dr.get_Item("Category").toString(), Integer.parseInt(dr.get_Item("TypePosition").toString()),dr.get_Item("LevelId").toString());
			mapInfo.put(dr.get_Item("EqName").toString(),info);
		}
		
		return mapInfo;
	}
	
	/*
	 * 获取所有节点数据
	 */
	public Map<String,NodeInfo> getNodeInfoMap(ISiteviewApi api) throws SiteviewException{
		Map<String,NodeInfo> mapInfo = new HashMap<String,NodeInfo>();
		
		String sql = 
		"select NodeType as resource_level_id,NodeName as resource_alias,NodeRelationMonitor as resource_id from NetworkNode";
		
		DataTable dt = api.get_NativeSQLSupportService().ExecuteNativeSQLQuery(sql,null);
		for(DataRow dr:dt.get_Rows()){
			NodeInfo nodeInfo = new NodeInfo();
			nodeInfo.setResource_alias(dr.get_Item("resource_alias").toString());
			nodeInfo.setResource_id(dr.get_Item("resource_id").toString());
			mapInfo.put(nodeInfo.getResource_id(),nodeInfo);
		}
		
		return mapInfo;
	}
	
	/*
	 * 获取所有的边数据
	 */
	public Map<String,EdgeInfo> getEdgeInfoMap(ISiteviewApi api) throws SiteviewException{
		Map<String,EdgeInfo> mapInfo = new HashMap<String,EdgeInfo>();
		
		String sql = "SELECT FromNode AS sourceResourceId,ToNode AS targetResourceId FROM NetworkRelation";
		DataTable dt = api.get_NativeSQLSupportService().ExecuteNativeSQLQuery(sql,null);
		for(DataRow dr:dt.get_Rows()){
			EdgeInfo edgeInfo = new EdgeInfo();
			edgeInfo.setSourceResourceId(dr.get_Item("sourceResourceId").toString());
			edgeInfo.setTargetResourceId(dr.get_Item("targetResourceId").toString());
			mapInfo.put(edgeInfo.getLinkId(),edgeInfo);
		}
		
		return mapInfo;
	}
	
	public DataTable getLevelTopMap(ISiteviewApi api) throws SiteviewException{
		String sql = 
		"select * from LevelTopMap";
		return api.get_NativeSQLSupportService().ExecuteNativeSQLQuery(sql,null);
	}
	
	public DataTable synResourceStatus(ISiteviewApi api) throws SiteviewException{
		String sql = 
		"SELECT Equipment.RecId AS resource_id,Equipment.EquipmentStatus AS resource_status,Equipment.EquipmentType,NodeId FROM NetworkNode,Equipment WHERE NetworkNode.NodeRelationMonitor = Equipment.RecId";
		return api.get_NativeSQLSupportService().ExecuteNativeSQLQuery(sql,null);
	}
}
