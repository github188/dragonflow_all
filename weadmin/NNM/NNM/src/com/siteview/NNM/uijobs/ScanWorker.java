package com.siteview.NNM.uijobs;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import ILOG.Diagrammer.GraphicContainer;
import ILOG.Diagrammer.GraphicObject;
import ILOG.Diagrammer.Rectangle2D;
import ILOG.Diagrammer.TopoNode;
import ILOG.Diagrammer.GraphLayout.ForceDirectedLayout;
import ILOG.Diagrammer.GraphLayout.ForceDirectedLayoutReport;
import ILOG.Diagrammer.GraphLayout.GraphLayout;
import ILOG.Diagrammer.GraphLayout.LayoutFlowDirection;
import ILOG.Diagrammer.GraphLayout.TreeLayout;
import ILOG.Diagrammer.GraphLayout.TreeLayoutAlignment;
import ILOG.Diagrammer.GraphLayout.TreeLayoutLinkStyle;
import ILOG.Diagrammer.GraphLayout.TreeLayoutMode;
import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ConnectionBroker;
import Siteview.thread.IPrincipal;

import com.siteview.NNM.Activator;
import com.siteview.NNM.NNMInstanceData;
import com.siteview.NNM.Editors.NNMainEditorInput;
import com.siteview.NNM.Editors.TopoManage;
import com.siteview.nmap.scan.DBAuthent;
import com.siteview.nnm.data.ConfigDB;
import com.siteview.nnm.data.DBManage;
import com.siteview.nnm.data.model.entity;
import com.siteview.nnm.data.model.svEdge;
import com.siteview.nnm.data.model.svPoint;
import com.siteview.nnm.elasticdata.ElasticDataAccess;
import com.siteview.nnm.ipmac.IpMacWorkers;
import com.siteview.topo.TopoEvents;
import com.siteview.topology.concurrent.JobManager;
import com.siteview.topology.model.ACdata;
import com.siteview.topology.model.AuxParam;
import com.siteview.topology.model.Edge;
import com.siteview.topology.model.IDBody;
import com.siteview.topology.model.Pair;
import com.siteview.topology.model.ScanParam;
import com.siteview.topology.model.Snmpv3config;
import com.siteview.topology.scan.TopoAnalyse;
import com.siteview.topology.scan.TopoScan;
import com.siteview.topology.scan.TraceAnalyse;
import com.siteview.topology.scan.TraceReader;
import com.siteview.topology.util.IoUtils;
import com.siteview.topology.util.ScanUtils;

public class ScanWorker implements IRunnableWithProgress {

	IProgressMonitor monitor = null;
	private volatile int progress = 1;
	private CountDownLatch scanCound;
	List<Pair<String, String>> scan_scales;
	List<String> scan_seeds;
	ScanWaitdialog waitdialog;
	List<String> ipspwd = new ArrayList<String>();
	String ArithmeticType = "0";
	private List<Map<String, String>> SSHAuthents;
	private List<Snmpv3config> SNMPV3s;
	private ISiteviewApi api;
	private IPrincipal principal;
	private String subtoponame="";
	public List<Snmpv3config> getSNMPV3s() {
		return SNMPV3s;
	}

	public void setSNMPV3s(List<Snmpv3config> sNMPV3s) {
		SNMPV3s = sNMPV3s;
	}

	private List<DBAuthent> DBAuthents;
	private boolean newrun=true;
	public List<Map<String, String>> getSSHAuthents() {
		return SSHAuthents;
	}

	public void setSSHAuthents(List<Map<String, String>> sSHAuthents) {
		SSHAuthents = sSHAuthents;
	}

	public List<DBAuthent> getDBAuthents() {
		return DBAuthents;
	}

	public void setDBAuthents(List<DBAuthent> dBAuthents) {
		DBAuthents = dBAuthents;
	}


	public ScanWorker(ScanWaitdialog waitdialog, CountDownLatch scanCound,
			List<Pair<String, String>> scan_scales, List<String> scan_seeds,
			List<String> ipspwd, String ArithmeticType,ISiteviewApi api,String subtoponame) {
		this.scanCound = scanCound;
		this.scan_scales = scan_scales;
		this.waitdialog = waitdialog;
		this.scan_seeds = scan_seeds;
		this.ipspwd = ipspwd;
		this.ArithmeticType = ArithmeticType;
		this.newrun=true;
		this.api=api;
		this.principal = Siteview.thread.Thread.get_CurrentPrincipal();
		this.subtoponame=subtoponame;

	}

	/**
	 * 控制任务进度条
	 */
	public synchronized void work() {
		if (progress < 98) {
			progress += 2;
			monitor.worked(2);
		}
	}

	public synchronized void finish() {
		monitor.worked(10000);
	}

	private void changeName(String value, boolean append) {
		waitdialog.changeValue(value, append);
	}

	private void setMessage(String msgStr) {
		waitdialog.worksetvalue(msgStr);
	}

	private void setcount(String labcount) {
		waitdialog.countvalue(labcount);
	}

	public void setDeviceValues(String swrt, String sw, String rt, String sr,
			String fr) {
		waitdialog.DeviceValues(swrt, sw, rt, sr, fr);
	}
	public void setNDeviceValues(String db,String wn,String ux) {
		waitdialog.NDeviceValues(db,wn,ux);
	}
	public String gettag() {
		return waitdialog.gettag();
	}

	private void Refreshdata() {
		long startTime = System.currentTimeMillis();
		com.siteview.nnm.data.DBManage.getTopoCharts();
		long endendTime = System.currentTimeMillis();
		changeName("刷新拓扑图据耗时(秒)：" + (endendTime - startTime) / 1000, true);
		System.out.println("刷新拓扑图据耗时(秒):" + (endendTime - startTime) / 1000);
	}
	private void Refreshlinedata(){
		long startTime = System.currentTimeMillis();
		com.siteview.nnm.data.EntityManager.AddEntityTasks();
		ISiteviewApi api = null;
		api = ConnectionBroker.get_SiteviewApi();
		com.siteview.nnm.data.EntityManager.saveIPMACBase(api);
		com.siteview.nnm.data.EntityManager.getAllIPMac(api);
		IpMacWorkers.startAllIPMac(api);
		com.siteview.nnm.data.FlowDataManager.AddEdgeFlowTasks();
		long endendTime = System.currentTimeMillis();
		changeName("加载线路取值(秒)：" + (endendTime - startTime) / 1000, true);
	}
	public static Map<String,svPoint> getlayout(
			DataTable dtnode, DataTable dtrealstion,Map<String,String> map){
		Map<String, svPoint> layoutdata = new HashMap<String, svPoint>();
		GraphicContainer group = new GraphicContainer();
		GraphLayout graphLayout = null;
		graphLayout = new TreeLayout();
		((TreeLayout) graphLayout)
				.set_FlowDirection(LayoutFlowDirection.Left);
	   ((TreeLayout) graphLayout).set_SiblingOffset(60);// ----------
	   ((TreeLayout) graphLayout).set_ParentChildOffset(90);
	   ((TreeLayout) graphLayout).set_Alignment(TreeLayoutAlignment.Center);
	   ((TreeLayout) graphLayout).set_LinkStyle(TreeLayoutLinkStyle.Straight);
	   ((TreeLayout) graphLayout).set_LayoutMode(TreeLayoutMode.Radial);
	   ((TreeLayout) graphLayout).set_AspectRatio(1f);
	   if (graphLayout == null)
			return layoutdata;
		GraphicObject node = null;
		Map<String, TopoNode> topondes = new HashMap<String, TopoNode>();
		for (DataRow dr: dtnode.get_Rows()) {
			String type=dr.get("NodeType")==null?"":dr.get("NodeType").toString();
			String nodid=dr.get("RecId")==null?"":dr.get("RecId").toString();
			node = new TopoNode(group, nodid);
			group.addNode(node);
			topondes.put(nodid, (TopoNode) node);
		}
		
		for (DataRow dr: dtrealstion.get_Rows()) {
			String formid=dr.get("FromNode")==null?"":dr.get("FromNode").toString();
			String toid=dr.get("ToNode")==null?"":dr.get("ToNode").toString();
			TopoNode node1 = topondes.get(formid);
			TopoNode node2 = topondes.get(toid);
			if (node1 == null || node2 == null) {
				continue;
			}
//			if(map.get(formid)!=null){
//				String xy=map.get(formid);
//				if(xy.contains(":")){
//					String local=xy.substring(0, xy.indexOf(":"));
//					((TopoNode)node1).setLocalx(Integer.parseInt(local));
//					local=xy.substring(xy.indexOf(":")+1);
//					((TopoNode)node1).setLocaly(Integer.parseInt(local));
//				}
//				
//			}
//			if(map.get(toid)!=null){
//				String xy=map.get(toid);
//				if(xy.contains(":")){
//					String local=xy.substring(0, xy.indexOf(":"));
//					((TopoNode)node2).setLocalx(Integer.parseInt(local));
//					local=xy.substring(xy.indexOf(":")+1);
//					((TopoNode)node2).setLocaly(Integer.parseInt(local));
//				}
//			}
			group.addLink(node1, node2);
		}
		ForceDirectedLayout.NODESIZE = group.getNodes().size();
		graphLayout.Attach(group);
		graphLayout.Layout();

		java.util.ArrayList<GraphicObject> lis = group.getNodes();
		int n = 0;
		float minx = 0, miny = 0, maxx = 0, maxy = 0;
		for (GraphicObject gobj : lis) {
			if (gobj.isNode()) {
				TopoNode tn = (TopoNode) gobj;
				if (maxx < tn.getLocalx()) {
					maxx = tn.getLocalx();
				}
				if (maxy < tn.getLocaly()) {
					maxy = tn.getLocaly();
				}
				if (tn.getLocalx() < minx) {
					minx = tn.getLocalx();
				}
				if (tn.getLocaly() < miny) {
					miny = tn.getLocaly();
				}
				svPoint pp = new svPoint((int) tn.getLocalx(),
						(int) tn.getLocaly());
				layoutdata.put(tn.getId(), pp);
			}
		}

		int minx1 = (int) Math.abs(minx);
		int miny1 = (int) Math.abs(miny);
		for (String key : layoutdata.keySet()) {
			int px = layoutdata.get(key).getPx();
			int py = layoutdata.get(key).getPy();
			int newpx = px + minx1 + 64;
			int newpy = py + miny1 + 64;
			String s=map.get(key);
			if(s!=null&&!s.equals("0:0")){
				String local=s.substring(0, s.indexOf(":"));
				layoutdata.get(key).setPx(Integer.parseInt(local));
				local=s.substring(s.indexOf(":")+1);
				layoutdata.get(key).setPy(Integer.parseInt(local));
			}else{
				if (newpy > 2100)
					newpy = 84;
				layoutdata.get(key).setPx(newpx);
				layoutdata.get(key).setPy(newpy);
			}
		}
		return layoutdata;
	}
	
	public static Map<String,svPoint> getLayout1(DataTable dtrealstion,
			Map<String,String> map,String type){
		Map<String, svPoint> layoutdata = new HashMap<String, svPoint>();
		String sql=String.format(
				"select * from NetWorkRelation where RelationLevel in ("
				+ "select RecId from NetWorkNodeTypeRelation where RelationCode='%s')",type);
		return layoutdata;
	}
	
	/**
	 * 排版计算
	 * 
	 * @param _tag
	 * @param n_nodes
	 * @param n_Lines
	 * @return
	 */
	private Map<String, svPoint> layout(String _tag,
			Map<String, IDBody> devid_list, List<Edge> topo_edge_list,String showpc) {
		Map<String, svPoint> layoutdata = new HashMap<String, svPoint>();
		GraphicContainer group = new GraphicContainer();
		GraphLayout graphLayout = null;
		// 环形
		if (_tag.equals("0")) {

			graphLayout = new TreeLayout();
			((TreeLayout) graphLayout)
					.set_FlowDirection(LayoutFlowDirection.Left);
		   ((TreeLayout) graphLayout).set_SiblingOffset(60);// ----------
		   ((TreeLayout) graphLayout).set_ParentChildOffset(90);
			// ((TreeLayout) graphLayout).set_OverlapPercentage(100);
			// ((TreeLayout) graphLayout).set_OrthForkPercentage(300);
			// ((TreeLayout) graphLayout).set_BranchOffset(100);
			// ((TreeLayout) graphLayout).set_ParentChildOffset(100);
			// ((TreeLayout) graphLayout).set_FirstCircleEvenlySpacing(true);
			// ((TreeLayout) graphLayout).set_GeometryUpToDate(true);
			((TreeLayout) graphLayout)
					.set_Alignment(TreeLayoutAlignment.Center);
			((TreeLayout) graphLayout)
					.set_LinkStyle(TreeLayoutLinkStyle.Straight);
			((TreeLayout) graphLayout).set_LayoutMode(TreeLayoutMode.Radial);
			
			((TreeLayout) graphLayout).set_AspectRatio(1f);
			// ((TreeLayout) graphLayout).set_LayoutRegion(new
			// Rectangle2D(0,0,2700,2200));

		} else if (_tag.equals("1"))// 普通
		{
			graphLayout = new ForceDirectedLayout();

			((ForceDirectedLayout) graphLayout)
					.SetLayoutReport(new ForceDirectedLayoutReport());
			((ForceDirectedLayout) graphLayout).set_PreferredLinksLength(100);
			((ForceDirectedLayout) graphLayout)
					.set_AdditionalNodeRepulsionWeight(0.4f);
			((ForceDirectedLayout) graphLayout)
					.set_LayoutRegion(new Rectangle2D(0, 0, 2700, 2200));
			((ForceDirectedLayout) graphLayout)
					.set_ForceFitToLayoutRegion(true);
			((ForceDirectedLayout) graphLayout).set_RespectNodeSizes(true);
		} else if (_tag.equals("2"))// 垂直
		{

			graphLayout = new TreeLayout();
			((TreeLayout) graphLayout)
					.set_FlowDirection(LayoutFlowDirection.Left);
			((TreeLayout) graphLayout)
					.set_Alignment(TreeLayoutAlignment.Center);
			((TreeLayout) graphLayout)
					.set_LinkStyle(TreeLayoutLinkStyle.Orthogonal);
			((TreeLayout) graphLayout)
					.set_LayoutMode(TreeLayoutMode.TipLeavesOver);
			((TreeLayout) graphLayout).set_AspectRatio(1f);

			// graphLayout = new HierarchicalLayout();
			// ((HierarchicalLayout) graphLayout)
			// .set_FlowDirection(LayoutFlowDirection.Bottom);
			// ((HierarchicalLayout)
			// graphLayout).set_LinkStyle(HierarchicalLayoutLinkStyle.NoReshape);//
			// ----------
			// ((HierarchicalLayout) graphLayout).set_VerticalNodeOffset(40);

			// graphLayout = new ForceDirectedLayout();
			// ((ForceDirectedLayout)
			// graphLayout).set_NodeDistanceThreshold(55);
			// ((ForceDirectedLayout) graphLayout)
			// .SetLayoutReport(new ForceDirectedLayoutReport());
			// ((ForceDirectedLayout) graphLayout)
			// .set_PreferredLinksLength(200);
			// ((ForceDirectedLayout) graphLayout)
			// .set_AdditionalNodeRepulsionWeight(0.4f);
			// ((ForceDirectedLayout) graphLayout).set_RespectNodeSizes(true);
		}
		if (graphLayout == null)
			return layoutdata;
		GraphicObject node = null;

		// graphLayout.SetFitToView(flag);

		Map<String, TopoNode> topondes = new HashMap<String, TopoNode>();
		for (String nodid : devid_list.keySet()) {
			IDBody ibody=devid_list.get(nodid);
			if(ibody.getDevType().equals("5") && !showpc.equals("1")){
				continue;
			}
			node = new TopoNode(group, nodid);
			group.addNode(node);
			topondes.put(nodid, (TopoNode) node);
		}

		for (Edge svlink : topo_edge_list) {
			TopoNode node1 = topondes.get(svlink.getIp_left());
			TopoNode node2 = topondes.get(svlink.getIp_right());
			if (node1 == null || node2 == null) {
				continue;
			}
			group.addLink(node1, node2);
		}
		ForceDirectedLayout.NODESIZE = group.getNodes().size();
		graphLayout.Attach(group);
		graphLayout.Layout();

		java.util.ArrayList<GraphicObject> lis = group.getNodes();
		int n = 0;
		float minx = 0, miny = 0, maxx = 0, maxy = 0;
		for (GraphicObject gobj : lis) {
			if (gobj.isNode()) {
				TopoNode tn = (TopoNode) gobj;
				if (maxx < tn.getLocalx()) {
					maxx = tn.getLocalx();
				}
				if (maxy < tn.getLocaly()) {
					maxy = tn.getLocaly();
				}
				if (tn.getLocalx() < minx) {
					minx = tn.getLocalx();
				}
				if (tn.getLocaly() < miny) {
					miny = tn.getLocaly();
				}
				svPoint pp = new svPoint((int) tn.getLocalx(),
						(int) tn.getLocaly());
				layoutdata.put(tn.getId(), pp);
			}
		}

		int minx1 = (int) Math.abs(minx);
		int miny1 = (int) Math.abs(miny);
		for (String key : layoutdata.keySet()) {
			int px = layoutdata.get(key).getPx();
			int py = layoutdata.get(key).getPy();
			int newpx = px + minx1 + 64;
			int newpy = py + miny1 + 64;
			if (newpy > 2100)
				newpy = 84;
			layoutdata.get(key).setPx(newpx);
			layoutdata.get(key).setPy(newpy);
		}

		return layoutdata;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		monitor.beginTask("扫描网络结构...", 100);
		this.monitor = monitor;
		/**
		 * 执行的任务
		 */
		Siteview.thread.Thread.set_CurrentPrincipal(principal);
		try {
			monitor.worked(progress);
			changeName("开始扫描...", false);
			AuxParam auxParam = new AuxParam();
			auxParam.setScan_type("0");
			auxParam.setPing_type("0");
			auxParam.setSeed_type("0");
			auxParam.setComp_type("1");
			auxParam.setSnmp_version("2");
			auxParam.setDumb_type("1");
			auxParam.setNbr_read_type("1");
			auxParam.setBgp_read_type("1");
			auxParam.setVrrp_read_type("1");
			
			int dbdepth=5;
		 	int dbthreads=50;
		 	int dbretries=3;
		 	int dbtimeout=300;
		 	int dbipcount=20;
		 	String sdumb="1";
		 	String sarp="0";
		 	String snbr="1";
		 	String sbgp="1";
		 	String svrrp="1";
		 	String snmapruh="0";
		 	String showpc="0";
		 	String dbports="80,22,23,135,161,1433,1521,3306";
		 	String dbotherports="";
		 	Connection conn=ConfigDB.getConn();
			String sql="select depth,threads,retries,timeout,ipcount,ports,otherports,nmaprun,dump,arpscan,vrrpscan,bgpscan,nbrscan,showpc from scanconfig";
			ResultSet rs= ConfigDB.query(sql, conn);
			try {
				while(rs.next()){
					dbdepth=Integer.parseInt(rs.getString("depth"));
					dbthreads=Integer.parseInt(rs.getString("threads"));
					dbretries=Integer.parseInt(rs.getString("retries"));
					dbtimeout=Integer.parseInt(rs.getString("timeout"));
					dbipcount=Integer.parseInt(rs.getString("ipcount"));	
					dbports=rs.getString("ports");
					dbotherports=rs.getString("otherports");
					if(dbotherports==null) dbotherports="";
				    snmapruh=rs.getString("nmaprun");
				    sdumb=rs.getString("dump");
				    sarp=rs.getString("arpscan");
				    snbr=rs.getString("nbrscan");
				    sbgp=rs.getString("bgpscan");
				    svrrp=rs.getString("vrrpscan");
				    showpc=rs.getString("showpc");
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch(Exception ex){
				
			}
			auxParam.setDumb_type(sdumb);
			auxParam.setSeed_type(sarp);
			auxParam.setBgp_read_type(sbgp);
			auxParam.setVrrp_read_type(svrrp);
			auxParam.setNbr_read_type(snbr);
			ConfigDB.close(conn);
			ScanParam s = new ScanParam();
			s.setArithmeticType(ArithmeticType);// 普通算法
			s.setSnmpv3s(getSNMPV3s());
			// 获取第一个元素
			if(TopoScan.communityqueue.size()>0){
			s.setCommunity_get_dft(TopoScan.communityqueue.get(0));
			}else{
				s.setCommunity_get_dft("");
			}
			List<Pair<Pair<Long, Long>, String>> comms = new ArrayList<Pair<Pair<Long, Long>, String>>();
			for (String commip : ipspwd) {
				String[] comm = commip.split("\\:");
				String comm1 = comm[0];
				String[] temp = comm[1].split("\\-");
				long ipstart = ScanUtils.ipToLong(temp[0]);
				long ipend = ScanUtils.ipToLong(temp[1]);
				Pair<Pair<Long, Long>, String> newpair = new Pair<Pair<Long, Long>, String>();
				newpair.setFirst(new Pair(ipstart, ipend));
				newpair.setSecond(comm1);
				comms.add(newpair);

			}
			s.setCommunitys_num(comms);
			s.setDepth(dbdepth);
			s.setTimeout(dbtimeout);
			s.setRetrytimes(dbretries);
			s.setThreadCount(dbthreads);
			s.setScan_scales(scan_scales);
			s.setScan_seeds(scan_seeds);
			// s.getScan_seeds().add("192.168.6.3");
			// Map<String, Map<String, String>> special_oid_list = new
			// ConcurrentHashMap<String, Map<String, String>>();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
			String scanstartime=df.format(new Date());
			if (ArithmeticType.equals("0")) {
				int currentDepth = 1;
				int Depth = s.getDepth();
				TopoScan scan = new TopoScan(s, auxParam);
				long start1 = System.currentTimeMillis();
				scan.Scan();
				TopoScan.lastCount=Integer.parseInt(JobManager.getInstance("Main").getPoolState()
						.get("numberOfCompletedTasks")+"");
				boolean iseed = scan.iseed;
				boolean notfinished = true;
				boolean runone = false;
				//int scalesize = 0;
				while (notfinished) {
					String fin = JobManager.getInstance("Main").getPoolState()
							.get("numberOfCompletedTasks")
							+ "";
					fin = (Integer.parseInt(fin) - TopoScan.lastCount) + "";
					//System.err.println("fin:   ***"+fin);
					//System.err.println("完成  ：" + fin);
					String currr = JobManager.getInstance("Main")
							.getPoolState()
							.get("currentNumberOfInvokerThreads")
							+ "";
//					System.err.println("当前线程数：" + currr);
					String working = JobManager.getInstance("Main")
							.getPoolState().get("numberOfActiveInvokerThreads")
							+ "";
				
					String str = "";
					while ((str = TopoScan.logqueue.poll()) != null) {
						changeName(str, true);
					}
//					if (!fin.equals("0") && working.equals("0")
//							 && TopoScan.scalesize==0 ) {
//						notfinished = false;
//					}
					if (!working.equals("0")) {
						runone = true;
					}
					if (!fin.equals("0") && working.equals("0") && runone) {
						notfinished = false;
						runone = false;
					}
					if (!notfinished) {
						if (iseed) {
							currentDepth++;
							if (currentDepth <= Depth) {
								notfinished = true;
								runone=false;
								 scan.scanNextDepth(currentDepth);
							}
						}
					}
					int intwork = Integer.parseInt(working);
					if (runone && intwork < 6) {
					    boolean alljobok = false;
						for (String key : TopoScan.alljob.keySet()) {
							if (TopoScan.alljob.get(key).equals("0")) {
								System.err.println("****  " + key
										+ "       ******");
								alljobok=true;
							}
						}
						if(!alljobok){
							try{
							Object oblist=JobManager.getInstance("Main").getPoolState().get("taskList");
							if(oblist!=null){
							List<Map<String, Object>> oblist1=(List<Map<String, Object>>)oblist;
							System.err.println("**** jobids " + oblist1.size()
									+ "       ******");
							
//							for(Map<String, Object> task :oblist1){
//								
//								 String jobid="";
//								 if(task.get("id")!=null)
//									 jobid=task.get("id")+"";
//								 System.err.println("**** jobid " + jobid
//											+ "       ******");
//							 }
							}
							}catch(Exception exex){
								
							}
						}
					}
					// if (working.equals("1")) {
					// notfinished = false;
					// for (String key : TopoScan.alljob.keySet()) {
					// if (TopoScan.alljob.get(key).equals("0")) {
					// System.err.println(key + "       ******");
					// }
					// }
					// }
					if (progress >= 20) {
						setMessage("完成任务 ：" + fin + "  工作任务数：" + working);
					}
					work();
					// String
					// labcount="三层："+TopoScan.SRcount+" 交换机："+TopoScan.SWcount+" 路由："+TopoScan.RTcount+" 防火墙："+TopoScan.FRcount+" 服务器:"+TopoScan.SEcount;
					// setcount(labcount);
					setDeviceValues(TopoScan.SRcount + "", TopoScan.SWcount
							+ "", TopoScan.RTcount + "", TopoScan.SEcount + "",
							TopoScan.FRcount + "");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (monitor.isCanceled()) {
						notfinished = false;
					}
				}
				finish();
				// 累加完成任务数
				String lastcount = JobManager.getInstance("Main")
						.getPoolState().get("numberOfCompletedTasks")
						+ "";
				TopoScan.lastCount = Integer.parseInt(lastcount);

				long end1 = System.currentTimeMillis();
				System.err.println("获取数据耗时(s)：" + (end1 - start1) / 1000);
				changeName("获取数据耗时(s)：" + (end1 - start1) / 1000, true);
				scan.IfMac2DevMac();
				scan.saveOriginData();
				scan.formatData();
				scan.saveFormatData();
				// *************************
				// ======Nmap 扫描====
				// *************************
				if (!TopoScan.servers.isEmpty() && snmapruh.equals("1") ) {
					monitor.beginTask("Nmap扫描...", 100);
					progress = 0;
					changeName("开始Nmap扫描...", true);
					List<Map<String, String>> SSHAuthents = getSSHAuthents();
					List<DBAuthent> DBAuthents = getDBAuthents();
					String[] ppp= dbports.split("\\,");
					List<String> ports = new ArrayList<String>();
					for(String pp:ppp){
					 if(!pp.isEmpty() && !ports.contains(pp))
						 ports.add(pp);
					}
					String[] otherppp=dbotherports.split("\\,");
					for(String pp:otherppp){
						 if(!pp.isEmpty() && !ports.contains(pp))
							 ports.add(pp);
					}
					scan.scanNmap(dbipcount, SSHAuthents, DBAuthents, ports);
					notfinished = true;
					while (notfinished) {
						String fin = JobManager.getInstance("Main")
								.getPoolState().get("numberOfCompletedTasks")
								+ "";
						fin = (Integer.parseInt(fin) - TopoScan.lastCount) + "";
						//System.err.println("完成  ：" + fin);
						String currr = JobManager.getInstance("Main")
								.getPoolState()
								.get("currentNumberOfInvokerThreads")
								+ "";
//						System.err.println("当前线程数：" + currr);
						String working = JobManager.getInstance("Main")
								.getPoolState()
								.get("numberOfActiveInvokerThreads")
								+ "";
						//System.err.println("工作线程数：" + working);
						String str = "";
						while ((str = TopoScan.logqueue.poll()) != null) {
							changeName(str, true);
						}
						if (!fin.equals("0") && working.equals("0")) {
							notfinished = false;
						}
						int intwork = Integer.parseInt(working);
						if (runone && intwork < 6) {
							for (String key : TopoScan.alljob.keySet()) {
								if (TopoScan.alljob.get(key).equals("0")) {
									System.err.println("****  " + key
											+ "       ******");
								}
							}
						}
						if (progress >= 4) {
							setMessage("完成任务 ：" + fin + "  工作任务数：" + working);
						}
						work();
						// String
						// labcount="三层："+TopoScan.SRcount+" 交换机："+TopoScan.SWcount+" 路由："+TopoScan.RTcount+" 防火墙："+TopoScan.FRcount+" 服务器:"+TopoScan.SEcount;
						// setcount(labcount);
						setNDeviceValues(TopoScan.DBcount+"",TopoScan.WNcount+"",TopoScan.UXcount+"");
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						if (monitor.isCanceled()) {
							notfinished = false;
						}
					}
					finish();
					// 累加完成任务数
					lastcount = JobManager.getInstance("Main").getPoolState()
							.get("numberOfCompletedTasks")
							+ "";
					TopoScan.lastCount = Integer.parseInt(lastcount);
					scan.updateDevices();
				}
				// ***********************
				// ======end Nmap 扫描====
				// ***********************
				try{
				scan.UpdatetoDatabase();
				}catch(Exception ex){
					System.err.println("************更新数据出错************");
				}
				scan.updatesPCubnetName();
				long end2 = System.currentTimeMillis();
				System.err.println("保存本地数据耗时(s)：" + (end2 - end1) / 1000);
				changeName("保存本地数据耗时(s)：" + (end2 - end1) / 1000, true);
				try{
				if (TopoScan.devid_list.isEmpty()) {
					TopoScan.topo_edge_list.clear();
				} else if (scan.existNetDevice(TopoScan.devid_list)) {
					TopoAnalyse analyse=null;
					try{
						//ac -ap数据测试===========
//						IDBody devid=null;
//						devid=new IDBody();
//						devid.setSysOid("1.3.6.1.4.1.9.1.1069");
//						devid.setSnmpflag("1");
//						devid.setSysDesc("cisco");
//						devid.setCommunity_get("public");
//						devid.setDevType("1");
//						devid.setDevModel("");
//						devid.setDevFactory("Cisco");
//						devid.setDevTypeName("SWITCH");
//						devid.setSysSvcs("72");
//						devid.setSysName("SWITCH");
//						devid.getIps().add("");
//						devid.getMsks().add("");
//						devid.getInfinxs().add("0");
//						devid.getMacs().add("bc:16:f5:99:98");
//						TopoScan.devid_list.put("10.118.228.34", devid);
//						List<ACdata> acs=new ArrayList<ACdata>();
//						ACdata acdata=null;
//					  //	for(int i=1;i<255;i++){
//							devid = new IDBody();
//							String tempip="192.168.4.0";
//							devid.setSnmpflag("0");
//							devid.setBaseMac("bc:16:f5:99:98");
//							devid.setSysName("B1-C16");
//							devid.setDevType("23");// host
//							devid.setDevModel("");
//							devid.setDevFactory("");
//							devid.getIps().add(tempip);
//							devid.getMsks().add("");
//							devid.getInfinxs().add("0");
//							devid.getMacs().add("bc:16:f5:99:98");
//							TopoScan.devid_list.put(tempip, devid);
//							acdata=new ACdata();
//							acdata.setIp(tempip);
//							acdata.setMac("bc:16:f5:99:98");
//							acdata.setName("B1-C16");
//							acdata.setState("1");
//							acs.add(acdata);
//					//	}
//					//	for(int i=1;i<255;i++){
//							devid = new IDBody();
//							 tempip="192.168.5.0";
//							devid.setSnmpflag("0");
//							devid.setBaseMac("bc:16:f5:99:98");
//							devid.setSysName("B1-C16");
//							devid.setDevType("23");// host
//							devid.setDevModel("");
//							devid.setDevFactory("");
//							devid.getIps().add(tempip);
//							devid.getMsks().add("");
//							devid.getInfinxs().add("0");
//							devid.getMacs().add("bc:16:f5:99:98");
//							TopoScan.devid_list.put(tempip, devid);
//							acdata=new ACdata();
//							acdata.setIp(tempip);
//							acdata.setMac("bc:16:f5:99:98");
//							acdata.setName("B1-C16");
//							acdata.setState("1");
//							acs.add(acdata);
//					//	}
//						TopoScan.ac_ap_list.put("10.118.228.34", acs);
						//============end=============
					 analyse = new TopoAnalyse(TopoScan.devid_list,
							TopoScan.ifprop_list, TopoScan.frm_aft_list,
							TopoScan.frm_arp_list, TopoScan.ospfnbr_list,
							TopoScan.rttbl_list, TopoScan.bgp_list,
							TopoScan.directdata_list, auxParam,TopoScan.ac_ap_list);
					}
					catch(Exception exx){
						System.err.println(exx.getMessage());
						exx.printStackTrace();
						
					}
					if (analyse!=null && analyse.edgeAnalyse()) {
						TopoScan.topo_edge_list = analyse.getTopo_edge_list();
						if ((auxParam.getTracert_type().equals("1"))
								&& (TraceAnalyse
										.getConnection(TopoScan.topo_edge_list) > 1)) {
							TraceReader traceR = new TraceReader(
									TopoScan.devid_list, TopoScan.bgp_list,
									s.getRetrytimes(), s.getTimeout(), 30);
							traceR.tracePrepare();
							if ("0".equals(auxParam.getScan_type())) {
								TopoScan.rtpath_list = traceR
										.getTraceRouteByIPs();
								// 将trace path 保存到文件
								IoUtils.saveTracertList(TopoScan.rtpath_list);
								TraceAnalyse traceA = new TraceAnalyse(
										TopoScan.devid_list,
										TopoScan.rtpath_list,
										traceR.routeDESTIPPairList,
										traceR.unManagedDevices, s);
								traceA.analyseRRByRtPath(TopoScan.topo_edge_list);
							} else {
								// 从文件扫描时
								TraceAnalyse traceA = new TraceAnalyse(
										TopoScan.devid_list,
										TopoScan.rtpath_list,
										traceR.routeDESTIPPairList,
										traceR.unManagedDevices);
								traceA.analyseRRByRtPath_Direct(TopoScan.topo_edge_list);
							}
						}
						// 补充边的附加信息
						try{
						scan.fillEdge(TopoScan.topo_edge_list);
						}catch(Exception ex){
							
						}
						// 创建哑设备
						try{
						scan.generateDumbDevice(TopoScan.topo_edge_list,
								TopoScan.devid_list);}
						catch(Exception ex){
							
						}
						TopoScan.topo_entity_list = TopoScan.devid_list;// 己添加了哑设备
						if ("0".equals(auxParam.getCommit_pc())) {
							for (Entry<String, IDBody> j : TopoScan.devid_list
									.entrySet()) {
								if ("5".equals(j.getValue().getDevType())) {
									TopoScan.topo_entity_list.put(j.getKey(),
											j.getValue());
								}
							}
						} else {
							TopoScan.topo_entity_list = TopoScan.devid_list;
						}
					} else {
						// 分析失败日志
					}
				}}catch(Exception ex){
					System.err.println("************分析数据出错************");
					System.err.println(ex.getMessage());
				}
				try{
				if (!TopoScan.devid_list.isEmpty())
					IoUtils.saveFrmDevIDList(TopoScan.devid_list);
				// 保存边数据
				IoUtils.saveEdgesandEntitys(TopoScan.topo_edge_list,TopoScan.devid_list);
				}catch(Exception ex){
					
				}
				long end3 = System.currentTimeMillis();
				System.err.println("分析数据耗时(s)：" + (end3 - end2) / 1000);
				changeName("分析数据耗时(s)：：" + (end3 - end2) / 1000, true);
				// JobManager.getInstance("Main").shutDown();
				ElasticDataAccess.recscan(scanstartime);
			}
			// =============================
			// ===========cdp===============
			// =============================

			else if(ArithmeticType.equals("1")){
				TopoScan scan = new TopoScan(s, auxParam);
				long start1 = System.currentTimeMillis();
				scan.ScanCDP();
				TopoScan.lastCount=Integer.parseInt(JobManager.getInstance("Main").getPoolState()
						.get("numberOfCompletedTasks")+"");
				boolean notfinished = true;
				boolean runone = false;
				while (notfinished) {
					String fin = JobManager.getInstance("Main").getPoolState()
							.get("numberOfCompletedTasks")
							+ "";
					fin = (Integer.parseInt(fin) - TopoScan.lastCount) + "";
					//System.err.println("完成  ：" + fin);
					String currr = JobManager.getInstance("Main")
							.getPoolState()
							.get("currentNumberOfInvokerThreads")
							+ "";
//					System.err.println("当前线程数：" + currr);
					String working = JobManager.getInstance("Main")
							.getPoolState().get("numberOfActiveInvokerThreads")
							+ "";
					//System.err.println("工作线程数：" + working);
					String str = "";
					while ((str = TopoScan.logqueue.poll()) != null) {
						if (str.equals("There existed no valided seeds.")) {
							notfinished = false;
						}
						changeName(str, true);
					}
					if (!fin.equals("0") && working.equals("0")) {
						notfinished = false;
					}
					if (!working.equals("0")) {
						runone = true;
					}
					if (!fin.equals("0") && working.equals("0") && runone) {
						notfinished = false;
						runone = false;
					}
					if (!notfinished) {
						if (!TopoScan.directdata_cur.isEmpty()) {
							scan.ScanNextCDP();
							notfinished = true;
						}
					}
					int intwork = Integer.parseInt(working);
					if (runone && intwork < 6) {
						for (String key : TopoScan.alljob.keySet()) {
							if (TopoScan.alljob.get(key).equals("0")) {
								System.err.println(key + "       ******");
							}
						}
					}
					if (progress >= 20) {
						setMessage("完成任务 ：" + fin + "  工作任务数：" + working);
					}
					work();
					// String
					// labcount="三层："+TopoScan.SRcount+" 交换机："+TopoScan.SWcount+" 路由："+TopoScan.RTcount+" 防火墙："+TopoScan.FRcount+" 服务器:"+TopoScan.SEcount;
					// setcount(labcount);
					setDeviceValues(TopoScan.SRcount + "", TopoScan.SWcount
							+ "", TopoScan.RTcount + "", TopoScan.SEcount + "",
							TopoScan.FRcount + "");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (monitor.isCanceled()) {
						notfinished = false;
					}
				}
				finish();
				long end1 = System.currentTimeMillis();
				System.err.println("获取数据耗时(s)：" + (end1 - start1) / 1000);
				changeName("获取数据耗时(s)：" + (end1 - start1) / 1000, true);
				scan.formatData();
				scan.saveFormatData();
				scan.UpdatetoDatabase();
				scan.updatesPCubnetName();
				long end2 = System.currentTimeMillis();
				System.err.println("保存本地数据耗时(s)：" + (end2 - end1) / 1000);
				changeName("保存本地数据耗时(s)：" + (end2 - end1) / 1000, true);
				if (TopoScan.devid_list.isEmpty()) {
					TopoScan.topo_edge_list.clear();
				} else if (scan.existNetDevice(TopoScan.devid_list)) {
					TopoAnalyse analyse = new TopoAnalyse(TopoScan.devid_list,
							TopoScan.ifprop_list, TopoScan.frm_aft_list,
							TopoScan.frm_arp_list, TopoScan.ospfnbr_list,
							TopoScan.rttbl_list, TopoScan.bgp_list,
							TopoScan.directdata_list, auxParam,TopoScan.ac_ap_list);
					if (analyse.EdgeAnalyseDirect()) {
						TopoScan.topo_edge_list = analyse.getTopo_edge_list();
					}
					// 补充边的附加信息
					scan.fillEdge(TopoScan.topo_edge_list);
					// 创建哑设备
					scan.generateDumbDevice(TopoScan.topo_edge_list,
							TopoScan.devid_list);
					TopoScan.topo_entity_list = TopoScan.devid_list;// 己添加了哑设备

					// 保存边数据
				
					IoUtils.saveEdgesandEntitys(TopoScan.topo_edge_list,TopoScan.devid_list);
					long end3 = System.currentTimeMillis();
					System.err.println("分析数据耗时(s)：" + (end3 - end2) / 1000);
					changeName("分析数据耗时(s)：：" + (end3 - end2) / 1000, true);
				}
				ElasticDataAccess.recscan(scanstartime);
			}
			//历史数据扫描
			else if(ArithmeticType.equals("2")){
				TopoScan scan = new TopoScan(s, auxParam);
				scan.scanfromlog();
				changeName("开始读入数据...", true);
				long databasetime=System.currentTimeMillis();
				scan.devid_list.putAll(ElasticDataAccess.buildIDbodys());
				monitor.worked(20);
				scan.aft_list.putAll(ElasticDataAccess.buildafts());
				scan.arp_list.putAll(ElasticDataAccess.buildarps());
				monitor.worked(30);
				scan.directdata_list.putAll(ElasticDataAccess.builddirectdatalist());
				scan.rttbl_list.putAll(ElasticDataAccess.buildrttbllist());
				monitor.worked(40);
				scan.ifprop_list.putAll(ElasticDataAccess.buildifproplist());
				scan.ospfnbr_list.putAll(ElasticDataAccess.buildospfnbrlist());
				scan.routeStandby_list.putAll(ElasticDataAccess.buildvrrp());
				scan.bgp_list.addAll(ElasticDataAccess.buildbgplist());
				monitor.worked(50);
				long databasetime1=System.currentTimeMillis();
				changeName("读入数据耗时(s)："+(databasetime1-databasetime)/1000, true);
				scan.IfMac2DevMac();
				scan.formatData();
				scan.updatesPCubnetName();
				monitor.worked(70);
				try{
					if (TopoScan.devid_list.isEmpty()) {
						TopoScan.topo_edge_list.clear();
					} else if (scan.existNetDevice(TopoScan.devid_list)) {
						
						TopoAnalyse analyse = new TopoAnalyse(TopoScan.devid_list,
								TopoScan.ifprop_list, TopoScan.frm_aft_list,
								TopoScan.frm_arp_list, TopoScan.ospfnbr_list,
								TopoScan.rttbl_list, TopoScan.bgp_list,
								TopoScan.directdata_list, auxParam,TopoScan.ac_ap_list);
						if (analyse.edgeAnalyse()) {
							TopoScan.topo_edge_list = analyse.getTopo_edge_list();
							if ((auxParam.getTracert_type().equals("1"))
									&& (TraceAnalyse
											.getConnection(TopoScan.topo_edge_list) > 1)) {
								TraceReader traceR = new TraceReader(
										TopoScan.devid_list, TopoScan.bgp_list,
										s.getRetrytimes(), s.getTimeout(), 30);
								traceR.tracePrepare();
								if ("0".equals(auxParam.getScan_type())) {
									TopoScan.rtpath_list = traceR
											.getTraceRouteByIPs();
									// 将trace path 保存到文件
									IoUtils.saveTracertList(TopoScan.rtpath_list);
									TraceAnalyse traceA = new TraceAnalyse(
											TopoScan.devid_list,
											TopoScan.rtpath_list,
											traceR.routeDESTIPPairList,
											traceR.unManagedDevices, s);
									traceA.analyseRRByRtPath(TopoScan.topo_edge_list);
								} else {
									// 从文件扫描时
									TraceAnalyse traceA = new TraceAnalyse(
											TopoScan.devid_list,
											TopoScan.rtpath_list,
											traceR.routeDESTIPPairList,
											traceR.unManagedDevices);
									traceA.analyseRRByRtPath_Direct(TopoScan.topo_edge_list);
								}
							}
							// 补充边的附加信息
							try{
							scan.fillEdge(TopoScan.topo_edge_list);
							}catch(Exception ex){
								
							}
							// 创建哑设备
							try{
							scan.generateDumbDevice(TopoScan.topo_edge_list,
									TopoScan.devid_list);}
							catch(Exception ex){
								
							}
							TopoScan.topo_entity_list = TopoScan.devid_list;// 己添加了哑设备
							if ("0".equals(auxParam.getCommit_pc())) {
								for (Entry<String, IDBody> j : TopoScan.devid_list
										.entrySet()) {
									if ("5".equals(j.getValue().getDevType())) {
										TopoScan.topo_entity_list.put(j.getKey(),
												j.getValue());
									}
								}
							} else {
								TopoScan.topo_entity_list = TopoScan.devid_list;
							}
						} else {
							// 分析失败日志
						}
					}}catch(Exception ex){
						System.err.println("************分析数据出错************");
					}
				long endanalyse=System.currentTimeMillis();
				monitor.worked(90);
				changeName("分析数据耗时(s)：：" + (endanalyse - databasetime1) / 1000, true);
				
			}
			
			long end3 = System.currentTimeMillis();
			// 清理数据
			if(subtoponame.isEmpty())
			DBManage.cleardata();
			// 存储数据
			String tag = "0";
			try {
				tag = gettag();
			} catch (Exception ee) {

				System.err.println(ee.getMessage());
			}
			Map<String, svPoint> ldata = layout(tag, TopoScan.devid_list,
					TopoScan.topo_edge_list,showpc);
			if(subtoponame.isEmpty()){
			DBManage.saveScanedTopo(TopoScan.devid_list,
					TopoScan.topo_edge_list, ldata);
			}else{
				DBManage.saveIncrScanedTopo(TopoScan.devid_list,
						TopoScan.topo_edge_list, ldata,subtoponame);
			}
			Connection connn= ConfigDB.getConn();
			ConfigDB.batchInsert(TopoScan.ifprop_list, connn,subtoponame.isEmpty());
			ConfigDB.batchInsertDevice(connn,TopoScan.devid_list,subtoponame.isEmpty());
			ConfigDB.close(connn);
			
			// 清空数据
			TopoScan.ip_list_visited.clear();
			TopoScan.devid_list.clear();
			TopoScan.ifprop_list.clear();
            for(String kk:TopoScan.arp_list.keySet()){
            	TopoScan.arp_list.get(kk).clear();
			}
			TopoScan.arp_list.clear();
			for(String kk:TopoScan.aft_list.keySet()){
            	TopoScan.aft_list.get(kk).clear();
			}
			TopoScan.aft_list.clear();
			for(String kk:TopoScan.ospfnbr_list.keySet()){
            	TopoScan.ospfnbr_list.get(kk).clear();
			}
			TopoScan.ospfnbr_list.clear();
			for(String kk:TopoScan.rttbl_list.keySet()){
            	TopoScan.rttbl_list.get(kk).clear();
			}
			TopoScan.rttbl_list.clear();
			TopoScan.bgp_list.clear();
			
			TopoScan.routeStandby_list.clear();

			TopoScan.logqueue.clear();
			TopoScan.devid_list_valid.clear();
			// communityqueue.clear();
			for(String kk:TopoScan.frm_aft_list.keySet()){
            	TopoScan.frm_aft_list.get(kk).clear();
			}
			TopoScan.frm_aft_list.clear();
			for(String kk:TopoScan.frm_arp_list.keySet()){
            	TopoScan.frm_arp_list.get(kk).clear();
			}
			TopoScan.frm_arp_list.clear();

			TopoScan.directdata_list.clear();

			// 清理设备子网与掩码对应关系列表
			TopoScan.deviceSubnetNumMaskMap.clear();
			TopoScan.topo_edge_list.clear();
			//clear data
			TopoScan.snmps.clear();
			TopoScan.unixs.clear();
			TopoScan.dbs.clear();
			TopoScan.servers.clear();
			TopoScan.wins.clear();
			TopoScan.pcs.clear();
			TopoScan.others.clear();
			System.gc();
			long end4 = System.currentTimeMillis();
			System.err.println("neo4j数据耗时(s)：" + (end4 - end3) / 1000);
			changeName("数据库保存数据耗时(s)：" + (end4 - end3) / 1000, true);
			Refreshdata();
			Refreshlinedata();
			// RefreshUI();
			progress = 1;
		} catch (Exception e) {
            e.printStackTrace();
			//throw new RuntimeException(e);
		} finally {
			scanCound.countDown();
			monitor.done();
		}
	}

}
