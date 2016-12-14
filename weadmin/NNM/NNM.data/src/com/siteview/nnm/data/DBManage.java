package com.siteview.nnm.data;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.helpers.collection.IteratorUtil;
import org.neo4j.tooling.GlobalGraphOperations;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;
import org.osgi.framework.FrameworkUtil;

import com.siteview.nnm.data.model.*;
import com.siteview.topology.model.Edge;
import com.siteview.topology.model.IDBody;
import com.siteview.topology.util.ScanUtils;

/**
 * 操作 neo4j 数据库相关接口
 * 
 **/

public class DBManage {

	private static GraphDatabaseService graphDb;
	private static ExecutionEngine engine;
	// private static boolean initl = false;
	public static Map<String, TopoChart> Topos = new HashMap<String, TopoChart>();
	public static Map<String, String> Ip2Deviceids = new HashMap<String, String>();
	public static List<String> subhosts=new ArrayList<String>();

	// public static Map<String, svNode> nodes = new HashMap<String, svNode>();
	// public static Map<String, svEdge> edges = new HashMap<String, svEdge>();
	// public static int maxgroupid;

	// public static Map<String,svgroup> groups=new HashMap<String,svgroup>();
	// public static Map<String, svNode> nodes = new HashMap<String, svNode>();
	// public static List<svEdge> rsps = new ArrayList<svEdge>();

	private static enum RelTypes implements RelationshipType {
		NEO_NODE, KNOWS
	}

	private static String pat = null;

	public static String getVisioPath() {
		if (pat == null) {
			try {
				pat = FileLocator.toFileURL(
						FrameworkUtil.getBundle(DBManage.class).getEntry(""))
						.getPath();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return pat;

	}

	/**
	 * 获取插件的路径
	 * 
	 * @return
	 */
	private static String path = null;

	public static String getPlatformPath() {

		if (path == null) {
			try {
				path = System.getProperty("user.dir") + "/";
				// FileLocator.toFileURL(
				// FrameworkUtil.getBundle(DBManage.class).getEntry("")).getPath();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("path----------->" + path);
		}

		return path;
	}

	/**
	 * 初始化 获取topos
	 */
	public static void init() {
		long starttime = System.currentTimeMillis();
		String path = getPlatformPath();
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(path
				+ "topo.db");
		graphDb.shutdown();
		// engine = new ExecutionEngine(graphDb);
		long endtime = System.currentTimeMillis();
		System.out.println("neo4j连接耗时:" + (endtime - starttime) + " 毫秒");

		// maxgroupid = 1;
		// getTopoChartsids();
	}

	/**
	 * 获取chart idlist
	 */
	// private static void getTopoChartsids() {
	// Topos.clear();
	// Topos.put("host", new TopoChart());
	// Label label = DynamicLabel.label("group");
	// try {
	// try (Transaction tx = graphDb.beginTx()) {
	// try (ResourceIterator<Node> nodelist = graphDb
	// .findNodesByLabelAndProperty(label, "type", 100)
	// .iterator()) {
	// while (nodelist.hasNext()) {
	// Node node = nodelist.next();
	// try {
	// //String sgid = node.getProperty("gid") + "";
	// //int gid = Integer.parseInt(sgid);
	// //if (gid > maxgroupid)
	// // maxgroupid = gid;
	// String lbl = node.getProperty("name") + "";
	// String createtime = node.getProperty("createtime")
	// + "";
	// if (!Topos.containsKey(lbl)) {
	// TopoChart tc = new TopoChart();
	// tc.setCreateTime(createtime);
	// tc.setName(lbl);
	// Topos.put(lbl, tc);
	// }
	//
	// } catch (Exception ex) {
	// }
	// }
	//
	// }
	// }
	// } catch (Exception ex) {
	// }
	// }

	/**
	 * 获取所有拓扑图
	 */
	public static void getTopoCharts() {
		// if (initl) {
		// initl = false;
		// } else {
		
		String path = getPlatformPath();
		boolean aviti=graphDb.isAvailable(2000);
		try{
		if(aviti){
			return;
		}
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(path
				+ "topo.db");
		}catch(Exception ex){
			graphDb.shutdown();
			return;
		}
		// getTopoChartsids();
		// }
		TopoChart topochart = null;
		Map<String, entity> tempnodes = null;
		Map<String, svEdge> tempedges = null;
		List<Node> listnode = new ArrayList<Node>();
		Label label = DynamicLabel.label("node");
		Label grouplabel = DynamicLabel.label("group");
		svNode svnode = null;
		Topos.clear();
		Topos.put("host", new TopoChart());
		try (Transaction tx = graphDb.beginTx()) {

			try (ResourceIterator<Node> nodelist = graphDb
					.findNodesByLabelAndProperty(grouplabel, "type", 100)
					.iterator()) {
				while (nodelist.hasNext()) {
					Node node = nodelist.next();
					try {
						String lbl = node.getProperty("name") + "";
						String createtime = node.getProperty("createtime") + "";
						String subhost =node.getProperty("subtopo")+"";
						if(subhost.equals("subhost")){
							subhosts.add(lbl);
						}
						if (!Topos.containsKey(lbl)) {
							TopoChart tc = new TopoChart();
							tc.setCreateTime(createtime);
							tc.setName(lbl);
							Topos.put(lbl, tc);
						}

					} catch (Exception ex) {
					}
				}

			}

			//
			for (String klabel : Topos.keySet()) {

				tempnodes = new HashMap<String, entity>();
				tempedges = new HashMap<String, svEdge>();

				// 处理组
				try (ResourceIterator<Node> grouplist = graphDb
						.findNodesByLabelAndProperty(grouplabel, "subtopo",
								klabel).iterator()) {

					while (grouplist.hasNext()) {
						Node node = grouplist.next();
						String sgid = "g" + node.getProperty("gid");
						if (tempnodes.containsKey(sgid)) {
							continue;
						}
						listnode.add(node);
						// String type = node.getProperty("type") + "";
						String nx = node.getProperty("nx") + "";
						String ny = node.getProperty("ny") + "";
						String createtime = node.getProperty("createtime") + "";
						String iplist = node.getProperty("iplist") + "";
						String name = node.getProperty("name") + "";
						svgroup svgp = new svgroup();
						svgp.setSvid(sgid);
						svgp.setSvgtype(100);
						svgp.setName(name);
						svgp.setCreatedate(createtime);
						svgp.setNx(Float.parseFloat(nx));
						svgp.setNy(Float.parseFloat(ny));
						svgp.setIplist(iplist);

						// nodes.put(node.getProperty("nid") + "", svnode);
						tempnodes.put(sgid, svgp);

					}

					grouplist.close();

				}
				// 处理设备节点
				try (ResourceIterator<Node> nodelist = graphDb
						.findNodesByLabelAndProperty(label, "subtopo", klabel)
						.iterator()) {
					while (nodelist.hasNext()) {
						Node node = nodelist.next();
						String svid = "n" + node.getProperty("nid");
						if (tempnodes.containsKey(svid)) {
							continue;
						}
						listnode.add(node);
						String type = node.getProperty("type") + "";
						String nx = "";
						String ny = "";
						if (klabel.equals("host")) {
							nx = node.getProperty("nx") + "";
							ny = node.getProperty("ny") + "";
						} else {
							nx = node.getProperty("subnx") + "";
							ny = node.getProperty("subny") + "";
						}

						String ip = node.getProperty("ip") + "";
						String model = node.getProperty("model") + "";
						String fact = node.getProperty("manufact") + "";
						String mac = node.getProperty("mac") + "";
						String name = node.getProperty("name") + "";
						String custname = node.getProperty("nameCN") + "";
						String desc = node.getProperty("description") + "";
						String memo = node.getProperty("smemo") + "";
						svnode = new svNode();
						svnode.setSvid(svid);
						svnode.setSvgtype(Integer.parseInt(type));
						svnode.setNx(Float.parseFloat(nx));
						svnode.setNy(Float.parseFloat(ny));
						svnode.setLocalip(ip);
						svnode.setDevicename(name);
						svnode.setCustomname(custname);
						svnode.setModel(model);
						svnode.setFactory(fact);
						svnode.setMac(mac);
						svnode.setDesc(desc);
						svnode.setMemo(memo);
						Map<String, String> pros = new HashMap<String, String>();
						for (String nkey : node.getPropertyKeys()) {
							if (!pros.containsKey(nkey)) {
								pros.put(nkey, node.getProperty(nkey) + "");
							}
						}
						svnode.setProperys(pros);
						// nodes.put(node.getProperty("nid") + "", svnode);
						tempnodes.put(svid, svnode);

					}

					// 处理边
					for (Node node : listnode) {
						Iterable<Relationship> rels = node.getRelationships(
								RelTypes.KNOWS, Direction.OUTGOING);
						svEdge svedge = null;
						for (Relationship rship : rels) {
							String id = "l" + rship.getId();
							if (tempedges.containsKey(id)) {
								continue;
							}
							String leftid = rship.getProperty("LeftPortID")
									+ "";

							String port1 = leftid.split(":")[1];
							String lefttype = rship.getStartNode().getProperty(
									"type")
									+ "";
							if (lefttype.equals("100")) {
								leftid = "g" + leftid.split(":")[0];
							} else {
								leftid = "n" + leftid.split(":")[0];
							}

							String rightid = rship.getProperty("RightPortID")
									+ "";
							String port2 = rightid.split(":")[1];
							String righttype = rship.getEndNode().getProperty(
									"type")
									+ "";
							if (righttype.equals("100")) {
								rightid = "g" + rightid.split(":")[0];
							} else {
								rightid = "n" + rightid.split(":")[0];
							}

							if (!tempnodes.containsKey(leftid)) {
								continue;
							}
							if (!tempnodes.containsKey(rightid)) {
								continue;
							}
							String flowfrom = rship.getProperty("FlowWhich")
									+ "";
							svedge = new svEdge();
							svedge.setLid(id);
							svedge.setLsource(leftid);
							svedge.setLtarget(rightid);
							svedge.setSinterface(port1);
							svedge.setTinterface(port2);
							svedge.setFlowfrom(flowfrom);
							// rsps.add(svedge);
							tempedges.put(id, svedge);
						}
					}
					nodelist.close();

				}
				topochart = Topos.get(klabel);
				topochart.setNodes(tempnodes);
				topochart.setEdges(tempedges);
			}
		}

		graphDb.shutdown();

	}

	/**
	 * 保存拓扑图坐标
	 * 
	 * @param topodata
	 */
	public static void saveTopo(JsonArray topodata, String subtopo) {
		Map tempnodess = new HashMap<String, JsonObject>();
		for (int j = 0; j < topodata.size(); j++) {
			JsonObject jobj = topodata.get(j).asObject();
			String svid1 = jobj.get("sid").asString();
			// {"sid":"n41","nx":2194,"ny":1010,"otype":2,"localip":"10.21.252.242","mac":"54 89 98 73 C3 2E","name":"AR2220_HS_FS_LNnet","customname":"","factory":"Huawei","model":"ar2220"}
			tempnodess.put(svid1, jobj);
		}
		graphDb.isAvailable(2000);
		String path = getPlatformPath();
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(path
				+ "topo.db");
		Transaction tx = graphDb.beginTx();
		try {
			Label label = DynamicLabel.label("node");
			for (Node node : graphDb.findNodesByLabelAndProperty(label,
					"subtopo", subtopo)) {
				String svid = "n" + node.getProperty("nid");
				JsonObject jobj1 = (JsonObject) tempnodess.get(svid);
				int nx = 0;
				int ny = 0;
				try {
					nx = (int) jobj1.get("nx").asFloat() + 16;
					ny = (int) jobj1.get("ny").asFloat() + 16;
				} catch (Exception ex) {

				}
				// nx = jobj1.get("nx").asInt() + 16;
				// ny = jobj1.get("ny").asInt() + 16;

				// 主图
               // System.err.println("nx:"+nx);
              //  System.err.println("ny:"+ny);
				if (jobj1 != null) {
					if (subtopo.equals("host")) {
						node.setProperty("nx", nx);
						node.setProperty("ny", ny);
					} else// 子图
					{
						node.setProperty("subnx", nx);
						node.setProperty("subny", ny);
					}
				}
			}

			Label grouplabel = DynamicLabel.label("group");
			for (Node node : graphDb.findNodesByLabelAndProperty(grouplabel,
					"subtopo", subtopo)) {
				String svid = "g" + node.getProperty("gid");
				JsonObject jobj1 = (JsonObject) tempnodess.get(svid);
				int nx = (int) jobj1.get("nx").asFloat() + 16;
				int ny = (int) jobj1.get("ny").asFloat() + 16;
				node.setProperty("nx", nx);
				node.setProperty("ny", ny);
			}
			tx.success();
		} catch (Exception ex) {
			int nn = 9;
			System.err.println(ex.getMessage());
			tx.failure();
		} finally {
			tx.close();
		}
		graphDb.shutdown();

	}

	/**
	 * 创建子图
	 * 
	 * @param subtopodata
	 */
	public static JsonObject createsubtopo(JsonArray subtopodata, String tag) {

		String iplist = "";
		// 标记子图
		graphDb.isAvailable(2000);
		String path = getPlatformPath();
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(path
				+ "topo.db");
		int minx = 5000, miny = 5000;
		JsonObject jsobj = new JsonObject();
		int len = subtopodata.size() - 2;
		List<Node> ips = new ArrayList<Node>();

		try (Transaction tx = graphDb.beginTx()) {
			Label label = DynamicLabel.label("node");
			for (int j = 0; j < len; j++) {
				String nid = subtopodata.get(j).asString().substring(1);
				for (Node node : graphDb.findNodesByLabelAndProperty(label,
						"nid", Integer.parseInt(nid))) {
					String ip = node.getProperty("ip") + "";
					int nx = Integer.parseInt(node.getProperty("nx") + "");
					int ny = Integer.parseInt(node.getProperty("ny") + "");
					if (nx < minx) {
						minx = nx;
					}
					if (ny < miny) {
						miny = ny;
					}
					node.setProperty("subtopo", tag);
					ips.add(node);
					iplist = iplist + ip + ",";
				}
			}
			// 创建group
			int maxgrouid = 0;
			Label label1 = DynamicLabel.label("group");
			try (ResourceIterator<Node> nodelist = graphDb
					.findNodesByLabelAndProperty(label1, "type", 100)
					.iterator()) {
				while (nodelist.hasNext()) {
					Node node = nodelist.next();
					int nid = Integer.parseInt(node.getProperty("gid") + "");
					if (nid > maxgrouid) {
						maxgrouid = nid;
					}
				}
				nodelist.close();
			}
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//

			Node subtopoNode = graphDb.createNode(label1);
			maxgrouid = maxgrouid + 1;
			subtopoNode.setProperty("gid", maxgrouid);
			subtopoNode.setProperty("name", tag);
			subtopoNode.setProperty("type", 100);
			String nx = subtopodata.get(subtopodata.size() - 2).toString();
			subtopoNode.setProperty("nx", nx);
			String ny = subtopodata.get(subtopodata.size() - 1).toString();
			subtopoNode.setProperty("ny", ny);
			subtopoNode.setProperty("subtopo", "host");
			String crtime = df.format(new Date());
			subtopoNode.setProperty("createtime", crtime);
			subtopoNode.setProperty("iplist", iplist);
			jsobj.add("gid", "g" + maxgrouid).add("name", tag)
					.add("nx", Integer.parseInt(nx))
					.add("ny", Integer.parseInt(ny)).add("createtime", crtime)
					.add("iplist", iplist.replace(",", "\n"));
			// 创建连线
			int updatex = 0;
			if (minx > 100) {
				updatex = minx - 100;
			}

			int updatay = 0;
			if (miny > 100) {
				updatay = miny - 100;
			}
			for (Node node2 : ips) {
				// 更新node坐标
				int nxx = Integer.parseInt(node2.getProperty("nx") + "")
						- updatex;
				int nyy = Integer.parseInt(node2.getProperty("ny") + "")
						- updatay;
				node2.setProperty("subnx", nxx);
				node2.setProperty("subny", nyy);
				//
				Iterable<Relationship> rels = node2.getRelationships(
						RelTypes.KNOWS, Direction.BOTH);
				for (Relationship rship : rels) {
					if (ips.contains(rship.getStartNode())
							&& ips.contains(rship.getEndNode())) {
						continue;
					}
					if (ips.contains(rship.getStartNode())) {
						String rpid = rship.getProperty("RightPortID") + "";
						Relationship rship1 = subtopoNode.createRelationshipTo(
								rship.getEndNode(), RelTypes.KNOWS);
						rship1.setProperty("LeftPortID", maxgrouid + ":0");
						rship1.setProperty("RightPortID", rpid);
						rship1.setProperty("FlowWhich", "right");
					}
					if (ips.contains(rship.getEndNode())) {
						String lpid = rship.getProperty("LeftPortID") + "";
						Relationship rship1 = rship.getStartNode()
								.createRelationshipTo(subtopoNode,
										RelTypes.KNOWS);
						rship1.setProperty("LeftPortID", lpid);
						rship1.setProperty("RightPortID", maxgrouid + ":0");
						rship1.setProperty("FlowWhich", "left");
					}

				}

			}
			tx.success();

		}
		graphDb.shutdown();
		return jsobj;
	}

	/**
	 * 撤销子图
	 * 
	 * @param tag
	 */
	public static JsonObject[] cancelSubchart(String tag) {
		graphDb.isAvailable(2000);
		String path = getPlatformPath();
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(path
				+ "topo.db");
		List<JsonObject> subdata = new ArrayList<JsonObject>();
		//
		JsonObject jsobj = null;
		try (Transaction tx = graphDb.beginTx()) {
			Label label = DynamicLabel.label("node");

			for (Node node : graphDb.findNodesByLabelAndProperty(label,
					"subtopo", tag)) {
				node.setProperty("subtopo", "host");
				jsobj = new JsonObject();
				String svid = "n" + node.getProperty("nid");
				String type = node.getProperty("type") + "";
				String nx = node.getProperty("nx") + "";
				String ny = node.getProperty("ny") + "";
				String ip = node.getProperty("ip") + "";
				String model = node.getProperty("model") + "";
				String fact = node.getProperty("manufact") + "";
				String mac = node.getProperty("mac") + "";
				String name = node.getProperty("name") + "";
				jsobj.add("jid", svid).add("type", Integer.parseInt(type))
						.add("nx", Integer.parseInt(nx))
						.add("ny", Integer.parseInt(ny)).add("ip", ip)
						.add("customname", "").add("model", model)
						.add("manufact", fact).add("mac", mac)
						.add("name", name);
				subdata.add(jsobj);
				Iterable<Relationship> rels = node.getRelationships(
						RelTypes.KNOWS, Direction.BOTH);
				for (Relationship rship : rels) {
					String subtop1 = rship.getStartNode()
							.getProperty("subtopo") + "";
					String subtop2 = rship.getEndNode().getProperty("subtopo")
							+ "";
					if (!subtop1.equals("host") || !subtop2.equals("host")) {
						continue;
					}
					jsobj = new JsonObject();
					String idid = "l" + rship.getId();
					String leftid = rship.getProperty("LeftPortID") + "";
					String port1 = leftid.split(":")[1];
					leftid = "n" + leftid.split(":")[0];
					String rightid = rship.getProperty("RightPortID") + "";
					String port2 = rightid.split(":")[1];
					rightid = "n" + rightid.split(":")[0];

					jsobj.add("jid", idid).add("source", leftid)
							.add("target", rightid).add("sinterface", port1)
							.add("tinterface", port2).add("error", 100)
							.add("warn", 80).add("flow", 50);

					subdata.add(jsobj);
				}

			}
			Label grouplabel = DynamicLabel.label("group");
			for (Node node : graphDb.findNodesByLabelAndProperty(grouplabel,
					"name", tag)) {
				node.setProperty("subtopo", "host");
				Iterable<Relationship> rels = node.getRelationships(
						RelTypes.KNOWS, Direction.BOTH);
				for (Relationship rship : rels) {
					rship.delete();
				}
				node.delete();
			}

			tx.success();
		}
		graphDb.shutdown();
		JsonObject[] subdatas = new JsonObject[subdata.size()];
		for (int n = 0; n < subdata.size(); n++) {
			subdatas[n] = subdata.get(n);

		}
		return subdatas;
	}

	/**
	 * 创建连线
	 * 
	 * @param nid1
	 * @param nid2
	 * @param port1
	 * @param port2
	 * @return
	 */
	public static JsonObject createLine(int nid1, int nid2, String port1,
			String port2) {
		graphDb.isAvailable(2000);
		String path = getPlatformPath();
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(path
				+ "topo.db");
		long lineid = 0;
		JsonObject jsobj = new JsonObject();
		try (Transaction tx = graphDb.beginTx()) {
			Label label = DynamicLabel.label("node");
			Node n1 = null, n2 = null;
			for (Node node : graphDb.findNodesByLabelAndProperty(label, "nid",
					nid1)) {
				n1 = node;
			}
			for (Node node : graphDb.findNodesByLabelAndProperty(label, "nid",
					nid2)) {
				n2 = node;
			}
			if (n1 != null && n2 != null) {
				Relationship rship = n1
						.createRelationshipTo(n2, RelTypes.KNOWS);
				rship.setProperty("LeftPortID", nid1 + ":" + port1);
				rship.setProperty("RightPortID", nid2 + ":" + port2);
				rship.setProperty("FlowWhich", "left");
				lineid = rship.getId();
			}
			tx.success();
		}
		graphDb.shutdown();
		jsobj.add("lid", "l" + lineid).add("port1", port1).add("port2", port2);
		return jsobj;
	}

	/**
	 * 更新连线
	 * 
	 * @param port1
	 * @param port2
	 */
	public static void updateline(int nid1, int nid2, long lid, String port1,
			String port2, boolean isleft) {
		graphDb.isAvailable(2000);
		String path = getPlatformPath();
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(path
				+ "topo.db");
		try (Transaction tx = graphDb.beginTx()) {
			Relationship rship = graphDb.getRelationshipById(lid);
			rship.setProperty("LeftPortID", nid1 + ":" + port1);
			rship.setProperty("RightPortID", nid2 + ":" + port2);
			if (isleft) {
				rship.setProperty("FlowWhich", "left");
			} else {
				rship.setProperty("FlowWhich", "right");
			}
			tx.success();
		}
		graphDb.shutdown();
	}

	/**
	 * 删除连线
	 * 
	 * @param id
	 */
	public static void delline(int lid1) {
		graphDb.isAvailable(2000);
		String path = getPlatformPath();
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(path
				+ "topo.db");
		try (Transaction tx = graphDb.beginTx()) {
			graphDb.getRelationshipById(lid1).delete();
			tx.success();
		}
		graphDb.shutdown();
	}

	/**
	 * 添加设备
	 * 
	 * @param nx
	 * @param ny
	 * @param comn 共同体
	 * @param type 类型
	 * @param subtop 子图
	 */
	public static JsonObject createnode(int nx, int ny, String ip, String comm,
			int type, String subtop) {
		graphDb.isAvailable(2000);
		String path = getPlatformPath();
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(path
				+ "topo.db");
		JsonObject jsobj = null;
		try (Transaction tx = graphDb.beginTx()) {
			Label label = DynamicLabel.label("node");
			int maxid = 1;
			try (ResourceIterator<Node> nodelist = graphDb
					.findNodesByLabelAndProperty(label, "nodeall", 1)
					.iterator()) {
				while (nodelist.hasNext()) {
					Node node = nodelist.next();
					int nid = Integer.parseInt(node.getProperty("nid") + "");
					if (nid > maxid) {
						maxid = nid;
					}
				}
				nodelist.close();
			}
			maxid = maxid + 1;
			Node newdev = graphDb.createNode(label);
			newdev.setProperty("nid", maxid);
			newdev.setProperty("name", "test");
			newdev.setProperty("nameCN", "test");
			newdev.setProperty("ip", ip);
			newdev.setProperty("ipList", "");
			newdev.setProperty("type", type + "");
			newdev.setProperty("ifNum", 8);
			newdev.setProperty("model", "");
			newdev.setProperty("subnetName", "");
			newdev.setProperty("manufact", "");
			newdev.setProperty("nx", nx);
			newdev.setProperty("ny", ny);
			newdev.setProperty("subnx", nx);
			newdev.setProperty("subny", ny);
			newdev.setProperty("sysObjectID", "");
			newdev.setProperty("mac", "");
			newdev.setProperty("macList", "");
			newdev.setProperty("maskField", "");
			newdev.setProperty("snmpEnabled", "1");
			newdev.setProperty("Community", comm);
			newdev.setProperty("port", "161");
			newdev.setProperty("Version", "2");
			newdev.setProperty("description", "");
			newdev.setProperty("smemo", "");
			newdev.setProperty("subtopo", subtop);
			newdev.setProperty("nodeall", 1);
			jsobj = new JsonObject();
			jsobj.add("nid", "n" + maxid).add("type", type).add("nx", nx)
					.add("ny", ny).add("ip", ip).add("customname", "")
					.add("model", "").add("manufact", "").add("mac", "")
					.add("name", "test");
			tx.success();
		}
		graphDb.shutdown();
		return jsobj;
	}

	public static JsonObject createnode(String subtop, Map<String, String> pdev) {
		graphDb.isAvailable(2000);
		String path = getPlatformPath();
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(path
				+ "topo.db");
		JsonObject jsobj = null;
		int maxid = 1;
		try (Transaction tx = graphDb.beginTx()) {
			Label label = DynamicLabel.label("node");

			try (ResourceIterator<Node> nodelist = graphDb
					.findNodesByLabelAndProperty(label, "nodeall", 1)
					.iterator()) {
				while (nodelist.hasNext()) {
					Node node = nodelist.next();
					int nid = Integer.parseInt(node.getProperty("nid") + "");
					if (nid > maxid) {
						maxid = nid;
					}
				}
				nodelist.close();
			}
			maxid = maxid + 1;
			Node newdev = graphDb.createNode(label);
			newdev.setProperty("nid", maxid);
			newdev.setProperty("name", pdev.get("name"));
			newdev.setProperty("nameCN", "");
			newdev.setProperty("ip", pdev.get("ip"));
			newdev.setProperty("ipList", pdev.get("ipList"));
			newdev.setProperty("type", pdev.get("type"));
			newdev.setProperty("ifNum", 8);
			newdev.setProperty("model", pdev.get("model"));
			newdev.setProperty("subnetName", pdev.get("subnetName"));
			newdev.setProperty("manufact", pdev.get("manufact"));
			newdev.setProperty("nx", Integer.parseInt(pdev.get("nx")));
			newdev.setProperty("ny", Integer.parseInt(pdev.get("ny")));
			newdev.setProperty("subnx", Integer.parseInt(pdev.get("nx")));
			newdev.setProperty("subny", Integer.parseInt(pdev.get("ny")));
			newdev.setProperty("sysObjectID", pdev.get("sysObjectID"));
			newdev.setProperty("mac", "");
			newdev.setProperty("macList", pdev.get("macList"));
			newdev.setProperty("maskField", pdev.get("maskField"));
			newdev.setProperty("snmpEnabled", pdev.get("snmpEnabled"));
			newdev.setProperty("Community", pdev.get("Community"));
			newdev.setProperty("port", "161");
			newdev.setProperty("Version", "2");
			newdev.setProperty("description", "");
			newdev.setProperty("smemo", "");
			newdev.setProperty("subtopo", subtop);
			newdev.setProperty("nodeall", 1);
			jsobj = new JsonObject();
			jsobj.add("nid", "n" + maxid)
					.add("type", Integer.parseInt(pdev.get("type")))
					.add("nx", Integer.parseInt(pdev.get("nx")))
					.add("ny", Integer.parseInt(pdev.get("ny")))
					.add("ip", pdev.get("ip")).add("customname", "")
					.add("model", pdev.get("model"))
					.add("manufact", pdev.get("manufact")).add("mac", "")
					.add("name", pdev.get("name"));
			tx.success();
		}
		graphDb.shutdown();
		// 更新当前数据
		svNode svnode = new svNode();
		svnode.setSvid("n" + maxid);
		svnode.setSvgtype(Integer.parseInt(pdev.get("type")));
		svnode.setNx(Float.parseFloat(pdev.get("nx")));
		svnode.setNy(Float.parseFloat(pdev.get("ny")));
		svnode.setLocalip(pdev.get("ip"));
		svnode.setDevicename(pdev.get("name"));
		svnode.setCustomname("");
		svnode.setModel(pdev.get("model"));
		svnode.setFactory(pdev.get("manufact"));
		svnode.setMac("");
		svnode.setDesc("");
		svnode.setMemo("");
		Map<String, String> pros = new HashMap<String, String>();
		for (String nkey : pdev.keySet()) {
			if (!pros.containsKey(nkey)) {
				pros.put(nkey, pdev.get(nkey));
			}
		}
		pros.put("mac", "");
		pros.put("port", "161");
		pros.put("mac", "");
		pros.put("Version", "2");
		pros.put("description", "");
		pros.put("smemo", "");
		pros.put("subtopo", subtop);
		pros.put("nameCN", "");
		svnode.setProperys(pros);
		Topos.get(subtop).getNodes().put("n" + maxid, svnode);
		try{
		EntityManager.AddEntity("n" + maxid, svnode);
		}catch(Exception ex){
			
		}
		return jsobj;
	}

	/**
	 * 更新node
	 * 
	 * @param ip
	 * @param devtype
	 * @param cusname
	 */
	public static void updatenode(int nid1, String ip, String devtype,
			String cusname) {
		graphDb.isAvailable(2000);
		String path = getPlatformPath();
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(path
				+ "topo.db");
		try (Transaction tx = graphDb.beginTx()) {
			Label label = DynamicLabel.label("node");
			Node n1 = null;
			for (Node node : graphDb.findNodesByLabelAndProperty(label, "nid",
					nid1)) {
				n1 = node;
			}
			if (n1 != null) {
				n1.setProperty("nameCN", cusname);
				n1.setProperty("ip", ip);
				n1.setProperty("type", devtype);
			}
			tx.success();
		}
		String nidid = "n" + nid1;
		for (String tagg : Topos.keySet()) {
			if (Topos.get(tagg).getNodes().containsKey(nidid)) {
				((svNode) Topos.get(tagg).getNodes().get(nidid))
						.setCustomname(cusname);
				((svNode) Topos.get(tagg).getNodes().get(nidid))
						.setSvgtype(Integer.parseInt(devtype));
			}

		}
		graphDb.shutdown();
	}

	/**
	 * 批量删除node
	 * 
	 * @param subgroupnode
	 */
	public static void delgroupnode(JsonArray subgroupnode) {

		graphDb.isAvailable(2000);
		String path = getPlatformPath();
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(path
				+ "topo.db");
		try (Transaction tx = graphDb.beginTx()) {
			Label label = DynamicLabel.label("node");
			for (int j = 0; j < subgroupnode.size(); j++) {
				String nid = subgroupnode.get(j).asString().substring(1);
				for (Node node : graphDb.findNodesByLabelAndProperty(label,
						"nid", Integer.parseInt(nid))) {
					// 删除相关的边
					for (Relationship rs : node.getRelationships()) {
						rs.delete();
					}
					// 删除node
					node.delete();

				}
			}
			tx.success();
		}
		graphDb.shutdown();
	}

	/**
	 * 清理所有主数据
	 */
	public static void cleardata() {
		try {
			graphDb.isAvailable(2000);
			String path = getPlatformPath();
			graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(path
					+ "topo.db");
			try (Transaction tx = graphDb.beginTx()) {
				for (Node node : GlobalGraphOperations.at(graphDb)
						.getAllNodes()) {
					String subtop = node.getProperty("subtopo")+""; 
					if(subtop.equals("subhost"))
						continue;
					if(subhosts.contains(subtop))
						continue;
					// 删除相关的边
					for (Relationship rs : node.getRelationships()) {
						rs.delete();
					}
					// 删除node
					node.delete();
				}
				tx.success();
			}
			graphDb.shutdown();
		} catch (Exception ex) {

			System.err.println(ex.getMessage());
		}

	}
	/**
	 * 清理子图数据集
	 * @param subtoponame
	 */
	public  static void clearsubdata(String subtoponame){
		try {
			graphDb.isAvailable(2000);
			String path = getPlatformPath();
			graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(path
					+ "topo.db");
			try (Transaction tx = graphDb.beginTx()) {
				for (Node node : GlobalGraphOperations.at(graphDb)
						.getAllNodes()) {
					String subtop = node.getProperty("subtopo")+""; 
					String name = node.getProperty("name")+"";
					if(!subtop.equals(subtoponame) && !name.equals(subtoponame))
						continue;
					// 删除相关的边
					for (Relationship rs : node.getRelationships()) {
						rs.delete();
					}
					// 删除node
					node.delete();
				}
				tx.success();
			}
			graphDb.shutdown();
		} catch (Exception ex) {

			System.err.println(ex.getMessage());
		}
	}

	/***
	 * return {id,ip} 保存扫描后的设备和边的数据
	 * 
	 * @param devid_list
	 * @param topo_edge_list
	 */
	public static void saveScanedTopo(Map<String, IDBody> devid_list,
			List<Edge> topo_edge_list, Map<String, svPoint> ldata) {
		graphDb.isAvailable(2000);
		String path = getPlatformPath();
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(path
				+ "topo.db");

		try (Transaction tx = graphDb.beginTx()) {
			Label label = DynamicLabel.label("node");
			Map<String, Node> ip2Node = new HashMap<String, Node>();
			int maxid = 0;
			try (ResourceIterator<Node> nodelist = graphDb
					.findNodesByLabelAndProperty(label, "nodeall", 1)
					.iterator()) {
				while (nodelist.hasNext()) {
					Node node = nodelist.next();
					int nid = Integer.parseInt(node.getProperty("nid") + "");
					if (nid > maxid) {
						maxid = nid;
					}
				}
				nodelist.close();
			}
			maxid = maxid + 1;
			// 保存节点
			for (String temip : devid_list.keySet()) {

				IDBody ibody = devid_list.get(temip);
				if (ibody == null) {
					continue;
				}
				String name = ibody.getSysName();
				if (name == null)
					name = "";
				String nameCN = "";
				Node newdev = graphDb.createNode(label);
				newdev.setProperty("nid", maxid);
				if (!ip2Node.containsKey(temip))
					ip2Node.put(temip, newdev);
				newdev.setProperty("name", name);
				newdev.setProperty("nameCN", nameCN);
				newdev.setProperty("ip", temip);
				Vector<String> iplist = ibody.getIps();
				String siplist = "";
				if (iplist != null && iplist.size() != 0) {
					siplist = getVString(iplist);
				}
				newdev.setProperty("ipList", siplist);
				String type = ibody.getDevType();
				newdev.setProperty("type", type);
				int ifNum = ibody.getInfinxs().size();
				newdev.setProperty("ifNum", ifNum);
				String model = ibody.getDevModel();
				if (model == null)
					model = "";
				if(model.contains("Windows"))
					model="Windows";
				newdev.setProperty("model", model);
				String subnetname = ScanUtils.getSubnetStr(ibody.getIps(),
						ibody.getMsks());
				newdev.setProperty("subnetName", subnetname);
				if (type.equals("5")) {
					if (!ibody.getSubNet().isEmpty())
						newdev.setProperty("subnetName", ibody.getSubNet());
				}
				String manufact = ibody.getDevFactory();
				if (manufact == null)
					manufact = "";
				newdev.setProperty("manufact", manufact);
				if (ldata.containsKey(temip)) {
					newdev.setProperty("nx", ldata.get(temip).getPx());
					newdev.setProperty("ny", ldata.get(temip).getPy());
				} else {
					newdev.setProperty("nx", 64);
					newdev.setProperty("ny", 64);
				}
				newdev.setProperty("subnx", 0);
				newdev.setProperty("subny", 0);
				String sysoid = ibody.getSysOid();
				if (sysoid == null)
					sysoid = "";
				newdev.setProperty("sysObjectID", sysoid);
				String mac = ibody.getBaseMac();
				if (mac == null) {
					mac = "";
				}
				newdev.setProperty("mac", mac);
				Vector<String> maclist = ibody.getMacs();
				String smaclist = "";
				if (maclist != null && maclist.size() != 0) {
					smaclist = getVString(maclist);
				}
				newdev.setProperty("macList", smaclist);
				String maskfield = "";
				Vector<String> msks = ibody.getMsks();
				if (msks != null && msks.size() > 0) {
					maskfield = getVString(msks);
				}
				newdev.setProperty("maskField", maskfield);
				String snmpEnabled = ibody.getSnmpflag();
				if (snmpEnabled == null || snmpEnabled.isEmpty()) {
					snmpEnabled = "0";
				}
				newdev.setProperty("snmpEnabled", snmpEnabled);
				String comm = ibody.getCommunity_get();
				if (comm == null)
					comm = "";
				newdev.setProperty("Community", comm);
				newdev.setProperty("port", "161");
				String snmpVersion = ibody.getSnmpVesion();
				if (snmpVersion == null || snmpVersion.isEmpty()) {
					snmpVersion = "2";
				}
				newdev.setProperty("Version", snmpVersion);
				String desc = ibody.getSysDesc();
				newdev.setProperty("description", "");
				newdev.setProperty("smemo", "");
				newdev.setProperty("subtopo", "host");
				newdev.setProperty("nodeall", 1);
				if (!Ip2Deviceids.containsKey(temip))
					Ip2Deviceids.put(temip, "n" + maxid);
				maxid = maxid + 1;
			}
			// 保存边
			for (Edge edge : topo_edge_list) {

				String leftip = edge.getIp_left();
				String rightip = edge.getIp_right();
				Node node1 = null;
				Node node2 = null;
				if (ip2Node.containsKey(leftip)) {
					node1 = ip2Node.get(leftip);
				}
				if (ip2Node.containsKey(rightip)) {
					node2 = ip2Node.get(rightip);
				}
				if (node1 != null && node2 != null) {
					String nid1 = "0", nid2 = "0";
					nid1 = node1.getProperty("nid") + "";
					nid2 = node2.getProperty("nid") + "";
					String port1 = edge.getInf_left();
					String port2 = edge.getInf_right();
					Relationship rship = node1.createRelationshipTo(node2,
							RelTypes.KNOWS);
					rship.setProperty("LeftPortID", nid1 + ":" + port1);
					rship.setProperty("RightPortID", nid2 + ":" + port2);
					rship.setProperty("FlowWhich", "left");
				}
			}

			tx.success();
		}
		graphDb.shutdown();

	}
	/**
	 * 实现增量扫描拓扑
	 * @param devid_list
	 * @param topo_edge_list
	 * @param ldata
	 */
	public static void saveIncrScanedTopo(Map<String, IDBody> devid_list,
			List<Edge> topo_edge_list, Map<String, svPoint> ldata,String subname){
		graphDb.isAvailable(2000);
		String path = getPlatformPath();
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(path
				+ "topo.db");

		try (Transaction tx = graphDb.beginTx()) {

           //group
			int maxgrouid = 0;
			Label label1 = DynamicLabel.label("group");
			try (ResourceIterator<Node> nodelist = graphDb
					.findNodesByLabelAndProperty(label1, "type", 100)
					.iterator()) {
				while (nodelist.hasNext()) {
					Node node = nodelist.next();
					int nid = Integer.parseInt(node.getProperty("gid") + "");
					if (nid > maxgrouid) {
						maxgrouid = nid;
					}
				}
				nodelist.close();
			}
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//

			Node subtopoNode = graphDb.createNode(label1);
			maxgrouid = maxgrouid + 1;
			subtopoNode.setProperty("gid", maxgrouid);
			subtopoNode.setProperty("name", subname);
			subtopoNode.setProperty("type", 100);
			subtopoNode.setProperty("nx", "0");
			subtopoNode.setProperty("ny", "0");
			subtopoNode.setProperty("subtopo", "subhost");
			String crtime = df.format(new Date());
			subtopoNode.setProperty("createtime", crtime);
			subtopoNode.setProperty("iplist", "");
			
		  //group
			
			
			Label label = DynamicLabel.label("node");
			Map<String, Node> ip2Node = new HashMap<String, Node>();
			int maxid = 1;
			try (ResourceIterator<Node> nodelist = graphDb
					.findNodesByLabelAndProperty(label, "nodeall", 1)
					.iterator()) {
				while (nodelist.hasNext()) {
					Node node = nodelist.next();
					int nid = Integer.parseInt(node.getProperty("nid") + "");
					if (nid > maxid) {
						maxid = nid;
					}
				}
				nodelist.close();
			}
			maxid = maxid + 1;
			// 保存节点
			for (String temip : devid_list.keySet()) {

				IDBody ibody = devid_list.get(temip);
				if (ibody == null) {
					continue;
				}
				String name = ibody.getSysName();
				if (name == null)
					name = "";
				String nameCN = "";
				Node newdev = graphDb.createNode(label);
				newdev.setProperty("nid", maxid);
				if (!ip2Node.containsKey(temip))
					ip2Node.put(temip, newdev);
				newdev.setProperty("name", name);
				newdev.setProperty("nameCN", nameCN);
				newdev.setProperty("ip", temip);
				Vector<String> iplist = ibody.getIps();
				String siplist = "";
				if (iplist != null && iplist.size() != 0) {
					siplist = getVString(iplist);
				}
				newdev.setProperty("ipList", siplist);
				String type = ibody.getDevType();
				newdev.setProperty("type", type);
				int ifNum = ibody.getInfinxs().size();
				newdev.setProperty("ifNum", ifNum);
				String model = ibody.getDevModel();
				if (model == null)
					model = "";
				newdev.setProperty("model", model);
				String subnetname = ScanUtils.getSubnetStr(ibody.getIps(),
						ibody.getMsks());
				newdev.setProperty("subnetName", subnetname);
				if (type.equals("5")) {
					if (!ibody.getSubNet().isEmpty())
						newdev.setProperty("subnetName", ibody.getSubNet());
				}
				String manufact = ibody.getDevFactory();
				if (manufact == null)
					manufact = "";
				newdev.setProperty("manufact", manufact);
				if (ldata.containsKey(temip)) {
					newdev.setProperty("nx", ldata.get(temip).getPx());
					newdev.setProperty("ny", ldata.get(temip).getPy());
				} else {
					newdev.setProperty("nx", 64);
					newdev.setProperty("ny", 64);
				}
				if(ldata.containsKey(temip)){
					newdev.setProperty("subnx", ldata.get(temip).getPx());
					newdev.setProperty("subny", ldata.get(temip).getPy());
				}else{
				newdev.setProperty("subnx", 64);
				newdev.setProperty("subny", 64);
				}
				String sysoid = ibody.getSysOid();
				if (sysoid == null)
					sysoid = "";
				newdev.setProperty("sysObjectID", sysoid);
				String mac = ibody.getBaseMac();
				if (mac == null) {
					mac = "";
				}
				newdev.setProperty("mac", mac);
				Vector<String> maclist = ibody.getMacs();
				String smaclist = "";
				if (maclist != null && maclist.size() != 0) {
					smaclist = getVString(maclist);
				}
				newdev.setProperty("macList", smaclist);
				String maskfield = "";
				Vector<String> msks = ibody.getMsks();
				if (msks != null && msks.size() > 0) {
					maskfield = getVString(msks);
				}
				newdev.setProperty("maskField", maskfield);
				String snmpEnabled = ibody.getSnmpflag();
				if (snmpEnabled == null || snmpEnabled.isEmpty()) {
					snmpEnabled = "0";
				}
				newdev.setProperty("snmpEnabled", snmpEnabled);
				String comm = ibody.getCommunity_get();
				if (comm == null)
					comm = "";
				newdev.setProperty("Community", comm);
				newdev.setProperty("port", "161");
				String snmpVersion = ibody.getSnmpVesion();
				if (snmpVersion == null || snmpVersion.isEmpty()) {
					snmpVersion = "2";
				}
				newdev.setProperty("Version", snmpVersion);
				String desc = ibody.getSysDesc();
				newdev.setProperty("description", "");
				newdev.setProperty("smemo", "");
				newdev.setProperty("subtopo",subname);
				newdev.setProperty("nodeall", 1);
				if (!Ip2Deviceids.containsKey(temip))
					Ip2Deviceids.put(temip, "n" + maxid);
				maxid = maxid + 1;
			}
			// 保存边
			for (Edge edge : topo_edge_list) {

				String leftip = edge.getIp_left();
				String rightip = edge.getIp_right();
				Node node1 = null;
				Node node2 = null;
				if (ip2Node.containsKey(leftip)) {
					node1 = ip2Node.get(leftip);
				}
				if (ip2Node.containsKey(rightip)) {
					node2 = ip2Node.get(rightip);
				}
				if (node1 != null && node2 != null) {
					String nid1 = "0", nid2 = "0";
					nid1 = node1.getProperty("nid") + "";
					nid2 = node2.getProperty("nid") + "";
					String port1 = edge.getInf_left();
					String port2 = edge.getInf_right();
					Relationship rship = node1.createRelationshipTo(node2,
							RelTypes.KNOWS);
					rship.setProperty("LeftPortID", nid1 + ":" + port1);
					rship.setProperty("RightPortID", nid2 + ":" + port2);
					rship.setProperty("FlowWhich", "left");
				}
			}

			tx.success();
		}
		graphDb.shutdown();
	}

	/**
	 * 
	 * @param vv
	 * @return
	 */
	private static String getVString(Vector<String> vv) {
		StringBuffer result = new StringBuffer();
		for (String key : vv) {
			result.append(key + ",");
		}
		return result.substring(0, result.length() - 1);
	}

	public static void shutdown() {
		graphDb.shutdown();
	}
}
