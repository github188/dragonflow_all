package com.siteview.NNM.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import ILOG.Diagrammer.GraphicContainer;
import ILOG.Diagrammer.GraphicObject;
import ILOG.Diagrammer.TopoNode;
import ILOG.Diagrammer.GraphLayout.ForceDirectedLayout;
import ILOG.Diagrammer.GraphLayout.GraphLayout;
import ILOG.Diagrammer.GraphLayout.LayoutFlowDirection;
import ILOG.Diagrammer.GraphLayout.TreeLayout;
import ILOG.Diagrammer.GraphLayout.TreeLayoutAlignment;
import ILOG.Diagrammer.GraphLayout.TreeLayoutLinkStyle;
import ILOG.Diagrammer.GraphLayout.TreeLayoutMode;

import com.siteview.linkmap.LineItem;
import com.siteview.linkmap.LinkMap;
import com.siteview.linkmap.NodeItem;
import com.siteview.nnm.data.EntityManager;
import com.siteview.nnm.data.FlowDataManager;
import com.siteview.nnm.data.model.EdgeFlow;
import com.siteview.nnm.data.model.TopoChart;
import com.siteview.nnm.data.model.entity;
import com.siteview.nnm.data.model.svEdge;
import com.siteview.nnm.data.model.svNode;
import com.siteview.nnm.data.model.svgroup;
import com.siteview.snmpinterface.EquipmentQueuer;
import com.siteview.snmpinterface.entities.FlowData;

public class DeviceLink extends Dialog {

	String subtopo;
	String nodeid;
	Map<String, entity> n_nodes;
	Map<String, svEdge> n_Lines;
	Map<String, svNode> nodes=new HashMap<String,svNode>();
	Map<String, svNode> hubnodes=new HashMap<String,svNode>();
	List <svEdge> Lines=new ArrayList<svEdge>();
	static byte[] lock = new byte[0];
	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public DeviceLink(Shell parentShell, String nodeid, String lab) {
		super(parentShell);
		setShellStyle(SWT.MAX | SWT.RESIZE);
		this.subtopo = lab;
		this.nodeid = nodeid;
	}

	protected void configureShell(Shell newShell) {
		// newShell.setSize(450, 320);
		// newShell.setLocation(400, 175);
		newShell.setText("设备连接图");
		super.configureShell(newShell);
	}
	@Override
	  protected boolean isResizable() {
	    return true;
	  }
	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(final Composite parent) {
		// Composite container = (Composite) super.createDialogArea(parent);
		final LinkMap linkChart = new LinkMap(parent, SWT.BORDER);
		parent.setLayout(new GridLayout(1, false));
		linkChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		linkChart.setMaxX(2000);
		linkChart.setMaxY(1200);

		Refreshdata(linkChart);
		parent.getDisplay().timerExec(1000, new Runnable() {
			public void run() {
				linkChart.setRefreshdata(buildFlowdata());
				parent.getDisplay().timerExec(5000, this);
			}
		});
		return linkChart;
		// return container;
	}

	/**
	 * update data
	 */
	private void Refreshdata(LinkMap topoChart) {
		long startTime = System.currentTimeMillis();
		// com.siteview.nnm.data.DBManage.getNodesAndEdges();
		com.siteview.nnm.data.DBManage.getTopoCharts();
		TopoChart topochart = com.siteview.nnm.data.DBManage.Topos
				.get(this.subtopo);
		n_nodes = topochart.getNodes();
		n_Lines = topochart.getEdges();
		// topoChart.setCompHeight(800);
		long endTime = System.currentTimeMillis();
		System.err.println("获取数据共耗时" + (endTime - startTime) + " (毫秒)");

	//	NodeItem item = null;
		svNode node = (svNode) n_nodes.get(this.nodeid);
		nodes.put(this.nodeid, node);

		try {
		
			for (svEdge edge : n_Lines.values()) {
				if (edge.getLsource().equals(this.nodeid)) {
					Lines.add(edge);
					svNode node1 = (svNode) n_nodes.get(edge.getLtarget());
				 if(!nodes.containsKey(edge.getLtarget())){
					nodes.put(edge.getLtarget(), node1);
					if(node1.getSvgtype()==6){
						hubnodes.put(edge.getLtarget(), node1);
					}
					}

				}
				if (edge.getLtarget().equals(this.nodeid)) {
					Lines.add(edge);
					
					svNode node1 = (svNode) n_nodes.get(edge.getLsource());
					if(!nodes.containsKey(edge.getLsource()))
					{nodes.put(edge.getLsource(), node1);
					if(node1.getSvgtype()==6){
						hubnodes.put(edge.getLtarget(), node1);
					}
					}

				}

			}
			for (svEdge edge : n_Lines.values()) {
				if(hubnodes.containsKey(edge.getLsource())){
					Lines.add(edge);
					svNode node1 = (svNode) n_nodes.get(edge.getLtarget());
					if(!nodes.containsKey(edge.getLtarget()))
					nodes.put(edge.getLtarget(), node1);
				}
			}
		} catch (Exception ex) {
			int nn = 0;
		}
		Map<String, svNode> nodesss= layout(nodes,Lines);
		NodeItem item = null;
		for(String nidid:nodesss.keySet()){
			svNode node1 = (svNode) nodesss.get(nidid);
			item = new NodeItem(topoChart);
			item.setSvid(node1.getSvid());
			item.setSvgtype(node1.getSvgtype());
			item.setNx(node1.getNx());
			item.setNy(node1.getNy());
			item.setLocalip(node1.getLocalip());
			item.setCustomname("");
			item.setModel(node1.getModel());
			item.setFactory(node1.getFactory());
			item.setMac(node1.getMac());
			item.setDevicename(node1.getDevicename());
		}
		LineItem line = null;
		int i = 0;
		for(svEdge edge:  Lines){
			line = new LineItem(topoChart);
			i++;

			line.setLid(edge.getLid());
			line.setLsource(edge.getLsource());
			line.setLtarget(edge.getLtarget());
			line.setSinterface(edge.getSinterface());
			line.setTinterface(edge.getTinterface());
			int j = (int) (Math.random() * 70);
			if (j == 0)
				j = 1;

			line.setFlow(j);
			line.setWarn(80);
			line.setError(100);
			if (i == 30) {
				line.setFlow(170);
			}
		}
		long endendTime = System.currentTimeMillis();

		System.out.println("绘制连接图据耗时" + (endendTime - endTime) + " (毫秒)");
	}

	private Map<String, svNode> layout(Map<String, svNode> svNodes, List<svEdge> edges) {
		Map<String, svNode> layoutdata = new HashMap<String, svNode>();
		GraphicContainer group = new GraphicContainer();
		GraphLayout graphLayout = null;
		// 环形
		graphLayout = new TreeLayout();
		((TreeLayout) graphLayout).set_FlowDirection(LayoutFlowDirection.Left);
		((TreeLayout) graphLayout).set_SiblingOffset(70);// ----------
		((TreeLayout) graphLayout).set_ParentChildOffset(70);
		((TreeLayout) graphLayout).set_BranchOffset(70);
		//((TreeLayout) graphLayout).set_TipOverBranchOffset(70);
		// ((TreeLayout) graphLayout).set_OverlapPercentage(100);
		// ((TreeLayout) graphLayout).set_OrthForkPercentage(300);
		// ((TreeLayout) graphLayout).set_BranchOffset(100);
		// ((TreeLayout) graphLayout).set_ParentChildOffset(100);
		// ((TreeLayout) graphLayout).set_FirstCircleEvenlySpacing(true);
		// ((TreeLayout) graphLayout).set_GeometryUpToDate(true);
		((TreeLayout) graphLayout).set_Alignment(TreeLayoutAlignment.Center);
		((TreeLayout) graphLayout).set_LinkStyle(TreeLayoutLinkStyle.Straight);
		((TreeLayout) graphLayout).set_LayoutMode(TreeLayoutMode.Radial);
		((TreeLayout) graphLayout).set_AspectRatio(1.6f);
		// ((TreeLayout) graphLayout).set_LayoutRegion(new
		// Rectangle2D(0,0,2700,2200));

		if (graphLayout == null)
			return layoutdata;
		GraphicObject node = null;

		// graphLayout.SetFitToView(flag);

		Map<String, TopoNode> topondes = new HashMap<String, TopoNode>();
		for (String nodid : svNodes.keySet()) {
			node = new TopoNode(group, nodid);
			group.addNode(node);
			topondes.put(nodid, (TopoNode) node);
		}

		for (svEdge svlink : edges) {
			TopoNode node1 = topondes.get(svlink.getLsource());
			TopoNode node2 = topondes.get(svlink.getLtarget());
			if (node1 == null || node2 == null) {
				continue;
			}
			group.addLink(node1, node2);
		}
		//ForceDirectedLayout.NODESIZE = group.getNodes().size();
		graphLayout.Attach(group);
		graphLayout.Layout();

		java.util.ArrayList<GraphicObject> lis = group.getNodes();
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
				svNode nodenode= svNodes.get(tn.getId());
				//System.err.println((int) tn.getLocalx());
				//System.err.println((int) tn.getLocaly());
				nodenode.setNx((int) tn.getLocalx());
				nodenode.setNy((int) tn.getLocaly());
				layoutdata.put(tn.getId(), nodenode);
			}
		}

		int minx1 = (int) Math.abs(minx);
		int miny1 = (int) Math.abs(miny);
		for (String key : layoutdata.keySet()) {
			int px = (int)layoutdata.get(key).getNx();
			int py = (int)layoutdata.get(key).getNy();
			
			int newpx = px + minx1 + 64;
			int newpy = py + miny1 + 64;
			if (newpy > 1200)
				newpy = 84;
			//System.err.println(newpy);
			//System.err.println(newpy);
			layoutdata.get(key).setNx(newpx);
			layoutdata.get(key).setNy(newpy);
		}

		return layoutdata;
	}
	public  JsonObject[] buildFlowdata() {
		
		JsonObject[] flowdata = new JsonObject[Lines.size()];
		JsonObject jo = null;
		int i = 0;
		synchronized (lock) {
			for (svEdge lineid : Lines) {
				EdgeFlow edgeflow = FlowDataManager.EdgeFlowMap.get(lineid.getLid());
				String devid = edgeflow.leftid;
				String devport = edgeflow.leftport;
				edgeflow.flowRate=0;
				edgeflow.flowPercent=0;
				edgeflow.pktsRate=0;
				edgeflow.broadcastRate=0;
				edgeflow.avgPktLen=0;
				if (!edgeflow.isleft) {
					devid = edgeflow.rightid;
					devport = edgeflow.rightport;
				}
				if(EntityManager.allEntity.containsKey(devid)){
					 entity tempdev=EntityManager.allEntity.get(devid);
					 if(tempdev instanceof svNode){
						 svNode snmpdev=(svNode)tempdev;
						 String ip= snmpdev.getLocalip();
						 String comm=snmpdev.getProperys().get(
									"Community");
						Map<String,FlowData> ports= EquipmentQueuer.getEquipmentData(ip+comm);
						if(ports!=null){
							if(ports.containsKey(devport)){
								FlowData tempflowdata=ports.get(devport);
								if(tempflowdata.getInFlow()>=0){
									edgeflow.flowRate+=tempflowdata.getInFlow();
								}
								if(tempflowdata.getOutFlow()>=0){
									edgeflow.flowRate+=tempflowdata.getOutFlow();
								}
								if(tempflowdata.getInBroadcast()>=0){
									edgeflow.broadcastRate+=tempflowdata.getInBroadcast();
								}
								if(tempflowdata.getOutBroadcast()>=0){
									edgeflow.broadcastRate+=tempflowdata.getOutBroadcast();
								}
								if(tempflowdata.getInPkts()>=0){
									edgeflow.pktsRate+=tempflowdata.getInPkts();
								}
								if(tempflowdata.getOutPkts()>=0){
									edgeflow.pktsRate+=tempflowdata.getOutPkts();
								}
								if(tempflowdata.getPortSpeed()>0){
									edgeflow.flowPercent=(edgeflow.flowRate*1024*100)/tempflowdata.getPortSpeed();
								}else
								{
									edgeflow.flowPercent=0;
								}
								if(edgeflow.pktsRate>0){
									float tempv= edgeflow.flowRate;
									tempv=tempv*128/edgeflow.pktsRate;
									if(tempv>1500)
										tempv=1500;
									else if(tempv<1)
										tempv=1;
									edgeflow.avgPktLen=tempv;
								}else{
									edgeflow.avgPktLen=0;
								}
							}
						}
					 }
				}
               
				// int j = (int) (Math.random() * 70);
				// if (j == 0)
				// j = 1;
				// edgeflow.flowRate=j;
				 jo=new JsonObject().add("lid", lineid.getLid())
				  .add("flow", edgeflow.flowRate)
				 .add("pkts", edgeflow.pktsRate)
				 .add("broadcast", edgeflow.broadcastRate)
				 .add("bwusage", edgeflow.flowPercent)
				 .add("avgframeLen", edgeflow.avgPktLen);
				 flowdata[i]=jo;
				i++;
			}
		}
		return flowdata;
	}
	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(550, 450);
	}

}
