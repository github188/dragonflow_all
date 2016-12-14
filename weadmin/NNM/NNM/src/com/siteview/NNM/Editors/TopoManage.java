package com.siteview.NNM.Editors;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.EditorPart;

import com.siteview.NNM.Activator;
import com.siteview.NNM.NNMInstanceData;
import com.siteview.NNM.Views.NNMTreeView;
import com.siteview.NNM.aas.NNMPermissions;
import com.siteview.NNM.dialogs.AddDevice;
import com.siteview.NNM.dialogs.AddLine;
import com.siteview.NNM.dialogs.DeviceLink;
import com.siteview.NNM.dialogs.DeviceLocate;
import com.siteview.NNM.dialogs.DevicePanel;
import com.siteview.NNM.dialogs.DevicePropery;
import com.siteview.NNM.dialogs.DiagnosticToolsDialog;
import com.siteview.NNM.dialogs.LinkPropery;
import com.siteview.NNM.dialogs.MibSTable;
import com.siteview.NNM.dialogs.Toposet;
import com.siteview.NNM.dialogs.addSubTopo;
import com.siteview.NNM.dialogs.cpuMemdialog;
import com.siteview.NNM.modles.RootNNM;
import com.siteview.NNM.modles.SubChartModle;
import com.siteview.NNM.modles.TopoModle;
import com.siteview.NNM.uijobs.IoUtilss;
import com.siteview.NNM.uijobs.refreshDevice;
import com.siteview.NNM.uijobs.refreshWorker;
import com.siteview.ecc.authorization.ItossAuthorizeServiceImpl;
import com.siteview.ecc.authorization.PermissionFactory;
import com.siteview.ecc.constants.Operation;
import com.siteview.ecc.constants.Resource;
import com.siteview.ecc.monitor.nls.EccMessage;
import com.siteview.nnm.data.ConfigDB;
import com.siteview.nnm.data.DBManage;
import com.siteview.nnm.data.EntityManager;
import com.siteview.nnm.data.FlowDataManager;
import com.siteview.nnm.data.model.TopoChart;
import com.siteview.nnm.data.model.entity;
import com.siteview.nnm.data.model.svEdge;
import com.siteview.nnm.data.model.svNode;
import com.siteview.nnm.data.model.svgroup;
import com.siteview.topo.LineItem;
import com.siteview.topo.NodeEvents;
import com.siteview.topo.NodeItem;
import com.siteview.topo.TopoEvents;
import com.siteview.topo.TopoMap;
import com.siteview.utils.db.DBQueryUtils;
import com.siteview.utils.international.InternationalUtils;

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
import ILOG.Diagrammer.GraphLayout.TreeLayoutLevelAlignment;
import ILOG.Diagrammer.GraphLayout.TreeLayoutLinkStyle;
import ILOG.Diagrammer.GraphLayout.TreeLayoutMode;
import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.Windows.Forms.ConnectionBroker;
import siteview.windows.forms.MsgBox;

public class TopoManage extends EditorPart {

	public static final String ID = "TopoManage";
	public String subtopo = "host";
	public boolean isave;
	int width = 0;
	int pointX = 0;
	int height = 0;
	Map<String, entity> n_nodes;
	Map<String, svEdge> n_Lines;
	NNMainEditorInput editiput;
	 List<String> hidelines;

	@Override
	public void doSave(IProgressMonitor monitor) {

	}

	@Override
	public void doSaveAs() {

	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		this.setSite(site);
		this.setInput(input);
		// String name = input.getName();
		this.setPartName(input.getName());// 设置编辑器上方显示的名称
		this.subtopo = ((NNMainEditorInput) input).subtopo;
		this.editiput = (NNMainEditorInput) input;
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 排版计算
	 * 
	 * @param _tag
	 * @param n_nodes
	 * @param n_Lines
	 * @return
	 */
	private JsonObject[] layout(String _tag, Map<String, entity> n_nodes, Map<String, svEdge> n_Lines) {
		JsonObject[] layoutdata = new JsonObject[n_nodes.size() + 2];
		GraphicContainer group = new GraphicContainer();
		GraphLayout graphLayout = null;
		// 环形
		if (_tag.equals(TopoEvents.circular.toString())) {

			graphLayout = new TreeLayout();
			((TreeLayout) graphLayout).set_FlowDirection(LayoutFlowDirection.Left);
			((TreeLayout) graphLayout).set_SiblingOffset(60);// ----------
			// ((TreeLayout) graphLayout).set_ParentChildOffset(300);
			// ((TreeLayout) graphLayout).set_OverlapPercentage(100);
			// ((TreeLayout) graphLayout).set_OrthForkPercentage(300);
			// ((TreeLayout) graphLayout).set_BranchOffset(100);
			((TreeLayout) graphLayout).set_ParentChildOffset(90);
			// ((TreeLayout) graphLayout).set_FirstCircleEvenlySpacing(true);
			// ((TreeLayout) graphLayout).set_GeometryUpToDate(true);
			((TreeLayout) graphLayout).set_Alignment(TreeLayoutAlignment.Center);
			((TreeLayout) graphLayout).set_LinkStyle(TreeLayoutLinkStyle.Straight);
			((TreeLayout) graphLayout).set_LayoutMode(TreeLayoutMode.Radial);
			((TreeLayout) graphLayout).set_AspectRatio(1f);
//			 ((TreeLayout) graphLayout).set_LayoutRegion(new
//			 Rectangle2D(0,0,2700,2200));
//			 ((TreeLayout) graphLayout).SetFitToView(flag);

		} else if (_tag.equals(TopoEvents.normal.toString()))// 普通
		{
			graphLayout = new ForceDirectedLayout();

			((ForceDirectedLayout) graphLayout).SetLayoutReport(new ForceDirectedLayoutReport());
			((ForceDirectedLayout) graphLayout).set_PreferredLinksLength(100);
			((ForceDirectedLayout) graphLayout).set_AdditionalNodeRepulsionWeight(0.4f);
			((ForceDirectedLayout) graphLayout).set_LayoutRegion(new Rectangle2D(0, 0, 2700, 2200));
			((ForceDirectedLayout) graphLayout).set_ForceFitToLayoutRegion(true);
			((ForceDirectedLayout) graphLayout).set_RespectNodeSizes(true);
			((ForceDirectedLayout) graphLayout).set_NodeDistanceThreshold(16.0f);
			
			
		} else if (_tag.equals(TopoEvents.orthogonal.toString()))// 垂直
		{

			graphLayout = new TreeLayout();
			((TreeLayout) graphLayout).set_FlowDirection(LayoutFlowDirection.Left);
			((TreeLayout) graphLayout).set_Alignment(TreeLayoutAlignment.Center);
			((TreeLayout) graphLayout).set_LinkStyle(TreeLayoutLinkStyle.Orthogonal);
			((TreeLayout) graphLayout).set_LayoutMode(TreeLayoutMode.TipRootsAndLeavesOver);
			((TreeLayout) graphLayout).set_AspectRatio(1f);
			((TreeLayout) graphLayout).set_LevelAlignment(TreeLayoutLevelAlignment.Center);

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
		for (String nodid : n_nodes.keySet()) {
			node = new TopoNode(group, nodid);
			group.addNode(node);
			topondes.put(nodid, (TopoNode) node);
		}

		for (svEdge svlink : n_Lines.values()) {
			TopoNode node1 = topondes.get(svlink.getLsource());
			TopoNode node2 = topondes.get(svlink.getLtarget());
			group.addLink(node1, node2);
		}
		ForceDirectedLayout.NODESIZE = group.getNodes().size();
		graphLayout.Attach(group);
		graphLayout.Layout();

		JsonObject jo = null;

		java.util.ArrayList<GraphicObject> lis = group.getNodes();
		System.out.println(lis.size());
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
				jo = new JsonObject().add("nid", tn.getId()).add("nx", (int) tn.getLocalx()).add("ny", (int) tn.getLocaly());
				layoutdata[n++] = jo;
			}
		}
		jo = new JsonObject().add("nx", (int) maxx).add("ny", (int) maxy);
		layoutdata[n++] = jo;
		jo = new JsonObject().add("nx", (int) minx).add("ny", (int) miny);
		layoutdata[n++] = jo;

		return layoutdata;
	}
	@Override
	public void createPartControl(final Composite parent) {
		parent.setLayout(new FillLayout());
		SashForm sash=new SashForm(parent, SWT.VERTICAL);
		sash.setLayout(new GridLayout(1,false));
		// TODO Auto-generated method stub
		final TopoMap topoChart = new TopoMap(sash, SWT.BORDER);
	

		// final Map<String,svNode> n_nodes = new HashMap<String,svNode>();
		// final Map<String,svEdge> n_Lines = new HashMap<String,svEdge>();
		topoChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		topoChart.setMaxX(2700);
		topoChart.setMaxY(2200);
		
		createAlarmList(sash);
		sash.setWeights(new int[]{7,3});
		Connection conn = ConfigDB.getConn();
		String showpc = "0";
		int linkDisplayType = 1;
		int nodeDisplayType = 1;
		String sql = "select showpc,nodeshowtype,linkshowtype from scanconfig";
		ResultSet rs = ConfigDB.query(sql, conn);
		try {
			while (rs.next()) {
				showpc = rs.getString("showpc");
				if (showpc == null)
					showpc = "0";
				linkDisplayType = Integer.parseInt(rs.getString("linkshowtype"));
				nodeDisplayType = Integer.parseInt(rs.getString("nodeshowtype"));
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception ex) {

		}
		
		if(!this.subtopo.equals("host")){
			String sql1 ="select showpc from  subtopoconfig where name='"+this.subtopo+"'";
			rs = ConfigDB.query(sql1, conn);
			try {
				while (rs.next()) {
					this.isave=true;
					showpc = rs.getString("showpc");
					if (showpc == null)
						showpc = "0";
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception ex) {

			}
		}
		ConfigDB.close(conn);
		if (showpc.equals("0")) {
			topoChart.setShowpc(0);
		} else {
			topoChart.setShowpc(1);
		}
		 hidelines=new ArrayList<String>();
		 if(IoIoUtils.isave())
		 hidelines=IoIoUtils.Readhidelines(); 
		
		String[] hideliness=new String[hidelines.size()];
		for(int i=0;i<hidelines.size();i++){
			hideliness[i]=hidelines.get(i);
		}
		topoChart.setHidelines(hideliness);
		topoChart.setLinkDisplayType(linkDisplayType);
		topoChart.setNodeDisplayType(nodeDisplayType);
		if(this.subtopo.equals("host")){
			topoChart.setIsubtopo(0);
		}else{
			topoChart.setIsubtopo(1);
		}
		

		// 修正坐标偏移
		parent.addControlListener(new ControlAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void controlResized(ControlEvent e) {
				if (NNMInstanceData.getNNMData("topoleft") == null)
					return;
				int topoleft1 = (int) NNMInstanceData.getNNMData("topoleft");
				topoChart.setLeftx(topoleft1);
				// 设置组件长宽
				int w = parent.getBounds().width;
				int h = parent.getBounds().height;
				topoChart.setCompWidth(w - 20);
				topoChart.setCompHeight(h - 20);

			}
		});

		Refreshdata(topoChart);
		NNMInstanceData.setNNMData("topoChart", topoChart);

		// 处理ui事件
		topoChart.addListener(SWT.Selection, new Listener() {
			private static final long serialVersionUID = 1L;

			public void handleEvent(Event event) {
				String eventag = event.text;
				// 拓扑排版
				if (eventag.equals(TopoEvents.circular.toString()) || eventag.equals(TopoEvents.normal.toString()) || eventag.equals(TopoEvents.orthogonal.toString())) {
					String sql = "select showpc from scanconfig";
					Connection conn = ConfigDB.getConn();
					String showpcc = "1";
					ResultSet rs = ConfigDB.query(sql, conn);
					try {
						while (rs.next()) {
							showpcc = rs.getString("showpc");
							if (showpcc == null)
								showpcc = "0";
						}
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception ex) {

					}
					ConfigDB.close(conn);

					if (showpcc.equals("1")) {
						JsonObject[] laydata = layout(eventag, n_nodes, n_Lines);
						if (laydata[0] != null)
							topoChart.setLayoutdata(laydata);
					} else {
						Map<String, entity> newnodes = new HashMap<String, entity>();
						for (String hsvid : n_nodes.keySet()) {
							if (n_nodes.get(hsvid).getSvgtype() != 5) {
								newnodes.put(hsvid, n_nodes.get(hsvid));
							}
						}
						JsonObject[] laydata = layout(eventag, newnodes, n_Lines);
						if (laydata[0] != null)
							topoChart.setLayoutdata(laydata);

					}

				}
				// 定位
				else if (eventag.equals(TopoEvents.locatenode.toString()) && NNMPermissions.getInstance().getNNMDeviceManagementDeviceLocate()) {
					DeviceLocate lcate = new DeviceLocate(null,subtopo);
					lcate.open();
				}
				// 拓扑图设置
				else if (eventag.equals(TopoEvents.toposet.toString())&&NNMPermissions.getInstance().getNNMTopuOperationInViewTopuSet()) {
					Toposet toposet = new Toposet(topoChart.getShell(), topoChart);
					toposet.open();
				}
				// 保存拓扑图
				else if (eventag.equals(TopoEvents.savetopo.toString())) {
					if (ItossAuthorizeServiceImpl.getInstance().isPermitted(PermissionFactory.createPermission(Resource.TOPU_OPERATION_IN_VIEW_SAVE_TOPU.getType(), Operation.TOPUOPERATIONINVIEW_SAVE_TOPU.getOperationString(), "*"))) {
						JsonArray data = (JsonArray) event.data;
						com.siteview.nnm.data.DBManage.saveTopo(data, subtopo);
					} else {
						MsgBox.ShowError("提示", "无此权限!");
					}
				}
				// 创建子图
				else if (eventag.equals(TopoEvents.createsubtopo.toString())) {
					if (ItossAuthorizeServiceImpl.getInstance().isPermitted(
							PermissionFactory.createPermission(Resource.TOPU_OPERATION_IN_VIEW_CREATE_CHILD_GRAPH.getType(), Operation.TOPUOPERATIONINVIEW_CREATE_CHILD_GRAPH.getOperationString(), "*"))) {
						JsonArray data = (JsonArray) event.data;

						addSubTopo addsub = new addSubTopo(topoChart.getShell(), data, topoChart);
						addsub.open();
						if (addsub.name != null && addsub.name.length() > 0) {
							SubChartModle subc1 = new SubChartModle();
							subc1.setName(addsub.name);
							((TopoModle) NNMTreeView.getCNFNNMTreeView().getCommonViewer().getTree().getItem(0).getItem(0).getData()).getList().add(subc1);
							NNMTreeView.getCNFNNMTreeView().getCommonViewer().refresh();
							DBManage.getTopoCharts();
						}
					} else {
						MsgBox.ShowError("提示", "无此权限!");
					}

				}
				// 查看子图
				else if (eventag.equals(TopoEvents.looksubchart.toString())) {

					String gid = ((JsonValue) event.data).asString();
					IWorkbenchPage page = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
					IEditorPart editor = page.findEditor(editiput);
					page.closeEditor(editor, false);
					editiput.setSubtopo(gid);
					try {
						IEditorPart ep = page.openEditor(editiput, TopoManage.ID);
					} catch (PartInitException e) {
						e.printStackTrace();
					}

					TreeItem[] items = editiput.getTreeviewer().getTree().getItems();
					SubChartModle submod = setselecttion(items[0].getItems()[0].getData(), gid);
					if (submod != null) {
						try {
							editiput.getTreeviewer().setSelection(new StructuredSelection(submod), true);
							if (editiput.getTreeviewer().getTree().getSelection().length > 0)
								expandtreeitem((TreeItem) editiput.getTreeviewer().getTree().getSelection()[0].getParentItem());
						} catch (Exception ex) {
						}
					}

				}// 撤销子图
				else if (eventag.equals(TopoEvents.cancelsubchart.toString())) {
					if (ItossAuthorizeServiceImpl.getInstance().isPermitted(
							PermissionFactory.createPermission(Resource.TOPU_OPERATION_IN_VIEW_DELETE_CHILD_GRAPH.getType(), Operation.TOPUOPERATIONINVIEW_DELETE_CHILD_GRAPH.getOperationString(), "*"))) {
						String gid = ((JsonValue) event.data).asString();
						TreeItem[] items = NNMTreeView.getCNFNNMTreeView().getCommonViewer().getTree().getItems();
						System.out.println(gid);
						for (SubChartModle sub1 : ((TopoModle) items[0].getItems()[0].getData()).getList()) {
							if (sub1.getName().equals(gid)) {
								((TopoModle) items[0].getItems()[0].getData()).getList().remove(sub1);
								break;
							}
						}
						NNMTreeView.getCNFNNMTreeView().getCommonViewer().refresh();
						JsonObject[] jdatas = com.siteview.nnm.data.DBManage.cancelSubchart(gid);
						topoChart.setSubchartdata(jdatas);
						DBManage.getTopoCharts();
					} else {
						MsgBox.ShowError("提示", "无此权限!");
					}
				} else if (eventag.equals(TopoEvents.showpc.toString())&&NNMPermissions.getInstance().getNNMTopuOperationInViewShowPc()) {
					int showpc = ((JsonValue) event.data).asInt();
					try {
						Connection conn = ConfigDB.getConn();
						if(subtopo.equals("host")){
						String sql = "update scanconfig set showpc=" + showpc;
						ConfigDB.excute(sql, conn);
						}else{
							if(isave){
								String sql = "update subtopoconfig set showpc=" + showpc+" where name='"+subtopo+"'";
								ConfigDB.excute(sql, conn);
							}else{
								String sql = "insert into  subtopoconfig(showpc,name) values("+showpc+",'"+subtopo+"') ";
								ConfigDB.excute(sql, conn);
							}
						}
						ConfigDB.close(conn);
					} catch (Exception ex) {
					}
				} else if (eventag.equals(TopoEvents.ip.toString()) || eventag.equals(TopoEvents.name.toString()) || eventag.equals(TopoEvents.model.toString()) || eventag.equals(TopoEvents.customname.toString())) {
					String nodetype = "1";
					if (eventag.equals(TopoEvents.name.toString())) {
						nodetype = "2";
					} else if (eventag.equals(TopoEvents.model.toString())) {
						nodetype = "3";
					} else if (eventag.equals(TopoEvents.customname.toString())) {
						nodetype = "4";
					}
					try {
						Connection conn = ConfigDB.getConn();
						String sql = "update scanconfig set nodeshowtype=" + nodetype;
						ConfigDB.excute(sql, conn);
						ConfigDB.close(conn);
					} catch (Exception ex) {
					}

				} else if (eventag.equals(TopoEvents.flow.toString()) || eventag.equals(TopoEvents.inflow.toString()) || eventag.equals(TopoEvents.outflow.toString()) || eventag.equals(TopoEvents.pkts.toString())
						|| eventag.equals(TopoEvents.broadcast.toString()) || eventag.equals(TopoEvents.bwusage.toString())) {
					String linktype = "1";
					if (eventag.equals(TopoEvents.inflow.toString())) {
						linktype = "6";
					} else if (eventag.equals(TopoEvents.outflow.toString())) {
						linktype = "5";
					} else if (eventag.equals(TopoEvents.pkts.toString())) {
						linktype = "2";
					} else if (eventag.equals(TopoEvents.broadcast.toString())) {
						linktype = "3";
					} else if (eventag.equals(TopoEvents.bwusage.toString())) {
						linktype = "4";
					}
					try {
						Connection conn = ConfigDB.getConn();
						String sql = "update scanconfig set linkshowtype=" + linktype;
						ConfigDB.excute(sql, conn);
						ConfigDB.close(conn);
					} catch (Exception ex) {
					}

				}
				// 设备面板图
				else if (eventag.equals(NodeEvents.devicepanel.toString())) {
					if (ItossAuthorizeServiceImpl.getInstance().isPermitted(PermissionFactory.createPermission(Resource.TOPU_OPERATION_IN_VIEW_DEVICE_PANEL.getType(), Operation.TOPUOPERATIONINVIEW_DEVICE_PANEL.getOperationString(), "*"))) {
						String gid = ((JsonValue) event.data).asString();
						TopoChart topochart = com.siteview.nnm.data.DBManage.Topos.get(subtopo);
						entity tempnode = topochart.getNodes().get(gid);
						if (tempnode.getSvgtype() == 0 || tempnode.getSvgtype() == 1 || tempnode.getSvgtype() == 2 || tempnode.getSvgtype() == 3) {
							DevicePanel dpl = new DevicePanel(topoChart.getShell(), gid, subtopo);
							dpl.open();
						}
					} else {
						MsgBox.ShowError("提示", "无此权限!");
					}
				}
				// 设备连接图
				else if (eventag.equals(NodeEvents.deviceconnect.toString())&&NNMPermissions.getInstance().getNNMTopuOperationInViewDeviceConnect()) {
					String nodeid = ((JsonValue) event.data).asString();
					DeviceLink delink = new DeviceLink(topoChart.getShell(), nodeid, subtopo);
					delink.open();
				}
				// 设备属性
				else if (eventag.equals(NodeEvents.devicepropery.toString()) && NNMPermissions.getInstance().getNNMTopuOperationInViewDevicePanel()) {
					String nodeid = ((JsonValue) event.data).asString();
					DevicePropery dp = new DevicePropery(topoChart.getShell(), nodeid, subtopo, topoChart);
					dp.open();
				}
				// 刷新设备
				else if (eventag.equals(NodeEvents.devicerefresh.toString())&&NNMPermissions.getInstance().getNNMDeviceManagementDeviceRefresh()) {
					String nodeid = ((JsonValue) event.data).asString();
					refreshDevice waitdialog = new refreshDevice(null);
					waitdialog.setCancelable(true);
					refreshWorker woker1 = new refreshWorker(waitdialog, nodeid, subtopo, topoChart);
					try {
						waitdialog.run(true, true, woker1);
					} catch (Exception e) {
					}
				}
				//cpu
				else if(eventag.equals(NodeEvents.devicecpumem.toString())){
					String nodeid = ((JsonValue) event.data).asString();
					TopoChart topochart = com.siteview.nnm.data.DBManage.Topos.get(subtopo);
					entity tempnode = topochart.getNodes().get(nodeid);
					if (tempnode.getSvgtype() == 0 || tempnode.getSvgtype() == 1 || tempnode.getSvgtype() == 2 || tempnode.getSvgtype() == 3) {
		
					cpuMemdialog cpumem=new cpuMemdialog(null,(svNode)tempnode);
					cpumem.open();
					}
					
				}
				//告警快速查看
				else if(eventag.equals(NodeEvents.devicealertfast.toString())){
					String nodeid = ((JsonValue) event.data).asString();
					TopoChart topochart = com.siteview.nnm.data.DBManage.Topos.get(subtopo);
					entity tempnode = topochart.getNodes().get(nodeid);
					String ip=((svNode)tempnode).getLocalip();
					folder.layout();
					Control[] control=folder.getChildren();
					for(int i=0;i<control.length;i++){
						Control detailsItem=control[i];
						String title=detailsItem.getData().toString();
						if(title.startsWith(ip)){
							folder.setSelection(i);
							return;
						}
					}
					createCTabFolder(ip);
				}
				//告警分类查看
				else if(eventag.equals(NodeEvents.devicealertcategory.toString())){
					
				}
				// 设备各种表
				else if ((eventag.equals(NodeEvents.deviceiftable.toString()) && NNMPermissions.getInstance().getNNMDeviceInformationQueryInterface())
						|| (eventag.equals(NodeEvents.deviceroutetable.toString()) && NNMPermissions.getInstance().getNNMDeviceInformationQueryARP())
						|| (eventag.equals(NodeEvents.devicemactable.toString()) && NNMPermissions.getInstance().getNNMDeviceInformationQueryRoutingtable())
						|| (eventag.equals(NodeEvents.devicearptable.toString()) && NNMPermissions.getInstance().getNNMDeviceInformationQueryARP())
						|| (eventag.equals(NodeEvents.devicecdptable.toString()) && NNMPermissions.getInstance().getNNMDeviceInformationQueryCDP())
						|| (eventag.equals(NodeEvents.deviceiptable.toString()) && NNMPermissions.getInstance().getNNMDeviceInformationQueryIp())) {
					if(NNMPermissions.getInstance().getNNMDeviceInformationQuery()){
						String nodeid = ((JsonValue) event.data).asString();
						MibSTable mtable = new MibSTable(topoChart.getShell(), eventag, subtopo, nodeid);
						mtable.open();
					}
				}
				// 添加设备
				else if (eventag.equals(TopoEvents.adddevice.toString())) {
					if (ItossAuthorizeServiceImpl.getInstance().isPermitted(PermissionFactory.createPermission(Resource.TOPU_OPERATION_IN_VIEW_ADD_DEVICE.getType(), Operation.TOPUOPERATIONINVIEW_ADD_DEVICE.getOperationString(), "*"))) {
						JsonArray data = (JsonArray) event.data;
						AddDevice addev = new AddDevice(topoChart.getShell(), topoChart, data, subtopo);
						addev.open();
					} else {
						MsgBox.ShowError("提示", "无此权限!");
					}
				}
				// 添加连线
				else if (eventag.equals(TopoEvents.addline.toString())) {
					if (ItossAuthorizeServiceImpl.getInstance().isPermitted(PermissionFactory.createPermission(Resource.TOPU_OPERATION_IN_VIEW_ADD_LINE.getType(), Operation.TOPUOPERATIONINVIEW_ADD_LINE.getOperationString(), "*"))) {
						if (event.data instanceof JsonArray) {
							JsonArray data = (JsonArray) event.data;
							AddLine addline = new AddLine(topoChart.getShell(), data, topoChart, subtopo);
							addline.open();
						} else {
							AddLine addline = new AddLine(topoChart.getShell(), null, topoChart, subtopo);
							addline.open();
						}
					} else {
						topoChart.setNewlinedata(new JsonObject().add("lid", "nil"));
					}

				}
				// 删除线路
				else if (eventag.equals(TopoEvents.delline.toString())) {
					if (ItossAuthorizeServiceImpl.getInstance().isPermitted(PermissionFactory.createPermission(Resource.TOPU_OPERATION_IN_VIEW_DELETE_LINE.getType(), Operation.TOPUOPERATIONINVIEW_DELETE_LINE.getOperationString(), "*"))) {
						String lid = ((JsonValue) event.data).asString().substring(1);
						com.siteview.nnm.data.DBManage.delline(Integer.parseInt(lid));
						topoChart.setDeline(1);
						// 删除流量采集
						FlowDataManager.DeleteEdgeFlowTask("l" + lid);
					} else {
						MsgBox.ShowError("提示", "无此权限!");
					}
				}
				// 线路属性
				else if (eventag.equals(TopoEvents.linepropery.toString())&&NNMPermissions.getInstance().getNNMTopuOperationInViewLinePropery()) {
					String lid = ((JsonValue) event.data).asString();
					LinkPropery lp = new LinkPropery(topoChart.getShell(), lid, subtopo, topoChart);
					lp.open();

				} else if (eventag.equals(TopoEvents.linkset.toString())&&NNMPermissions.getInstance().getNNMTopuOperationInViewLineSet()) {
					String lid = ((JsonValue) event.data).asString();
					Toposet toposet = new Toposet(topoChart.getShell(), topoChart, lid);
					toposet.open();
				}else if(eventag.equals("linkhideshow")){
					String lid = ((JsonValue) event.data).asString();
					if(hidelines.contains(lid)){
						hidelines.remove(lid);
					}else{
						hidelines.add(lid);
					}
					IoIoUtils.savehidelines(hidelines);
				}
				// 删除node
				else if (eventag.equals(TopoEvents.delgroup.toString())&&NNMPermissions.getInstance().getNNMTopuOperationInViewDeleteDevice()) {
//					if (ItossAuthorizeServiceImpl.getInstance().isPermitted(PermissionFactory.createPermission(Resource.TOPU_OPERATION_IN_VIEW_DELETE_DEVICE.getType(), Operation.TOPUOPERATIONINVIEW_DELETE_DEVICE.getOperationString(), "*"))) {
					JsonArray data = (JsonArray) event.data;   
					
					if(data.size()==1){
					    	topoChart.setDelnode(1);
					    }else{
					    	topoChart.setDelnode(2);
					    }
					    
						com.siteview.nnm.data.DBManage.delgroupnode(data);
//					} else {
//						MsgBox.ShowError("提示", "无此权限!");
//						return;
//					}
				} else if (eventag.equals(NodeEvents.devicetelnet.toString())&&NNMPermissions.getInstance().getNNMTools()) {
					String nodeid = ((JsonValue) event.data).asString();
					DiagnosticToolsDialog dtd = new DiagnosticToolsDialog(topoChart.getShell(), subtopo, nodeid, 2);
					dtd.open();
				} else if (eventag.equals(NodeEvents.devicesnmptest.toString())&&NNMPermissions.getInstance().getNNMTools()) {
					String nodeid = ((JsonValue) event.data).asString();
					DiagnosticToolsDialog dtd = new DiagnosticToolsDialog(topoChart.getShell(), subtopo, nodeid, 4);
					dtd.open();
				} else if (eventag.equals(NodeEvents.devicesshtest.toString())&&NNMPermissions.getInstance().getNNMTools()) {
					String nodeid = ((JsonValue) event.data).asString();
					DiagnosticToolsDialog dtd = new DiagnosticToolsDialog(topoChart.getShell(), subtopo, nodeid, 1);
					dtd.open();
				} else if (eventag.equals(NodeEvents.deviceping.toString())&&NNMPermissions.getInstance().getNNMTools()) {
					String nodeid = ((JsonValue) event.data).asString();
					DiagnosticToolsDialog dtd = new DiagnosticToolsDialog(topoChart.getShell(), subtopo, nodeid, 0);
					dtd.open();
				} else if (eventag.equals(NodeEvents.devicetracetoute.toString())&&NNMPermissions.getInstance().getNNMTools()) {
					String nodeid = ((JsonValue) event.data).asString();
					DiagnosticToolsDialog dtd = new DiagnosticToolsDialog(topoChart.getShell(), subtopo, nodeid, 3);
					dtd.open();
				} else if (eventag.equals(NodeEvents.deviceport.toString())) {
				} else if (eventag.equals(NodeEvents.lineflow.toString())) {
				} else if (eventag.equals(NodeEvents.devicecpumem.toString())) {
				} else if (eventag.equalsIgnoreCase(NodeEvents.devicealertfast.toString()) || eventag.equalsIgnoreCase(NodeEvents.devicealertcategory.toString())) {
				} else if (eventag.equalsIgnoreCase(NodeEvents.devicealertconfig.toString())) {
				} else if (eventag.equalsIgnoreCase(NodeEvents.deviceweb.toString())){
					
				}
				else {
					MsgBox.ShowError("提示", "无此权限!");
				}

			}
		});

		// 刷新相关
		// final Display display = this.getShell().getDisplay();
		// final JsonObject [] refreshdata=new JsonObject[4];
		// JsonObject jo=null;
		// jo=new JsonObject().add("flow", 66)
		// .add("pkts", 12)
		// .add("broadcast", 33.6)
		// .add("bwusage", 0.0)
		// .add("avgframeLen", 23);
		// refreshdata[0]=jo;
		// jo=new JsonObject().add("flow", 12)
		// .add("pkts", 12)
		// .add("broadcast", 33.6)
		// .add("bwusage", 0.0)
		// .add("avgframeLen", 23);
		// refreshdata[1]=jo;
		// jo=new JsonObject().add("flow", 35)
		// .add("pkts", 12)
		// .add("broadcast", 33.6)
		// .add("bwusage", 0.0)
		// .add("avgframeLen", 23);
		// refreshdata[2]=jo;
		// jo=new JsonObject().add("flow", 32)
		// .add("pkts", 12)
		// .add("broadcast", 33.6)
		// .add("bwusage", 0.3)
		// .add("avgframeLen", 23);
		// refreshdata[3]=jo;
		//
		// EntityManager.AddEntityTasks();
		// FlowDataManager.AddEdgeFlowTasks();
		parent.getDisplay().timerExec(1000, new Runnable() {
			public void run() {
				if (!topoChart.isDisposed()) {
					topoChart.setRefreshdata(FlowDataManager.buildFlowdata());
					topoChart.setAlertdata(EntityManager.buildAlertdata());
					topoChart.getDisplay().timerExec(120000, this);
					if(!folder.isDisposed()&&folder.getSelection()!=null
						&&!folder.getSelection().getControl().isDisposed()){
						String ip=folder.getSelection().getControl().getData()==null?
								"":folder.getSelection().getControl().getData().toString();
						createTableItem((Table)folder.getSelection().getControl(),ip);
					}
				}
			}
		});

	}

	public SubChartModle setselecttion(Object data, String name) {
		SubChartModle mo = null;
		if (data instanceof TopoModle) {
			for (SubChartModle subchart : ((TopoModle) data).getList()) {
				if (subchart.getName().equals(name)) {
					return subchart;
				}

			}

		}

		return mo;

	}

	public void expandtreeitem(TreeItem treeitem) {

		if (treeitem != null && !(treeitem.getData() instanceof RootNNM)) {
			treeitem.setExpanded(true);
			expandtreeitem(treeitem.getParentItem());
		}
	}

	/**
	 * update data
	 */
	private void Refreshdata(TopoMap topoChart) {
		long startTime = System.currentTimeMillis();
		// com.siteview.nnm.data.DBManage.getNodesAndEdges();
		com.siteview.nnm.data.DBManage.getTopoCharts();
		TopoChart topochart = com.siteview.nnm.data.DBManage.Topos.get(this.subtopo);
		n_nodes = topochart.getNodes();
		n_Lines = topochart.getEdges();
		// topoChart.setCompHeight(800);
		long endTime = System.currentTimeMillis();
		System.err.println("获取数据共耗时" + (endTime - startTime) + " (毫秒)");
		int warnflow = 50;
		int errorflow = 2000;
		int warnpkts = 50;
		int errorpkts = 1000;
		int warnbroad = 20;
		int errorbroad = 200;
		int warnpercent = 20;
		int errorpercent = 80;
		try {
			if (IoUtilss.isave()) {
				List<String> snmpinters = IoUtilss.ReadTopoSet();

				for (String kkey : snmpinters) {
					if (kkey.startsWith("warnflow")) {
						String[] temvs = kkey.split("\\=");
						warnflow = Integer.parseInt(temvs[1]);

					} else if (kkey.startsWith("errorflow")) {
						String[] temvs = kkey.split("\\=");
						errorflow = Integer.parseInt(temvs[1]);
					} else if (kkey.startsWith("warnpkts")) {
						String[] temvs = kkey.split("\\=");
						warnpkts = Integer.parseInt(temvs[1]);
					} else if (kkey.startsWith("errorpkts")) {
						String[] temvs = kkey.split("\\=");
						errorpkts = Integer.parseInt(temvs[1]);
					} else if (kkey.startsWith("warnbroad")) {
						String[] temvs = kkey.split("\\=");
						warnbroad = Integer.parseInt(temvs[1]);
					} else if (kkey.startsWith("errorbroad")) {
						String[] temvs = kkey.split("\\=");
						errorbroad = Integer.parseInt(temvs[1]);
					} else if (kkey.startsWith("warnpercent")) {
						String[] temvs = kkey.split("\\=");
						warnpercent = Integer.parseInt(temvs[1]);
					} else if (kkey.startsWith("errorperent")) {
						String[] temvs = kkey.split("\\=");
						errorpercent = Integer.parseInt(temvs[1]);
					}
				}
			} else {
				IoUtilss.saveTopoSet(warnflow + "", errorflow + "", warnpkts + "", errorpkts + "", warnbroad + "", errorbroad + "", warnpercent + "", errorpercent + "");
			}
		} catch (Exception ex) {

		}
		try {
			NodeItem item = null;
			for (entity node1 : n_nodes.values()) {
				if (node1 instanceof svNode) {
					svNode node = (svNode) node1;
					// System.out.println(id);
					// System.out.println(type);
					item = new NodeItem(topoChart);
					item.setSvid(node.getSvid());
					item.setSvgtype(node.getSvgtype());
					item.setNx(node.getNx());
					item.setNy(node.getNy());
					// item.setText(node.getLocalip());
					item.setLocalip(node.getLocalip());
					item.setCustomname(node.getCustomname());
					item.setModel(node.getModel());
					item.setFactory(node.getFactory());
					item.setMac(node.getMac());
					item.setDevicename(node.getDevicename());
				} else if (node1 instanceof svgroup) {
					svgroup node = (svgroup) node1;
					// System.out.println(id);
					// System.out.println(type);
					item = new NodeItem(topoChart);
					item.setSvid(node.getSvid());
					item.setSvgtype(node.getSvgtype());
					item.setNx(node.getNx());
					item.setNy(node.getNy());
					// item.setText(node.getLocalip());
					item.setLocalip(node.getName());
					item.setCustomname(node.getName());
					item.setModel(node.getName());
					item.setFactory(node.getIplist().replace(",", "\n"));
					item.setMac(node.getCreatedate());
					item.setDevicename(node.getName());
				}
			}

		} catch (Exception ex) {
		}

		int warnflow1 = 50;
		int errorflow1 = 2000;
		int warnpkts1 = 50;
		int errorpkts1 = 1000;
		int warnbroad1 = 20;
		int errorbroad1 = 200;
		int warnpercent1 = 20;
		int errorpercent1 = 80;
		Connection conn = ConfigDB.getConn();
		Map<String, String> portss = new HashMap<String, String>();
		String sql = "select id,pindex,desc from ports ";
		ResultSet rs = ConfigDB.query(sql, conn);
		try {
			while (rs.next()) {
				String nid = rs.getString("id");
				String pindex = rs.getString("pindex");
				String desc = rs.getString("desc");
				String key = nid + pindex;
				if (!portss.containsKey(key)) {
					portss.put(key, desc);
				}

			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception ex) {

		}
		String sql1 = "select warnflow,errorflow,warnpkts,errorpkts,warnbroad,errorbroad,warnpercent,errorperent,linkid from linkset";
		Map<String, Map<String, Integer>> linksetdata = new HashMap<String, Map<String, Integer>>();
		ResultSet rs1 = ConfigDB.query(sql1, conn);
		try {
			while (rs1.next()) {
				String linkid = rs1.getString("linkid");
				warnflow1 = rs1.getInt("warnflow");
				errorflow1 = rs1.getInt("errorflow");
				warnpkts1 = rs1.getInt("warnpkts");
				errorpkts1 = rs1.getInt("errorpkts");
				warnbroad1 = rs1.getInt("warnbroad");
				errorbroad1 = rs1.getInt("errorbroad");
				warnpercent1 = rs1.getInt("warnpercent");
				errorpercent1 = rs1.getInt("errorperent");
				Map<String, Integer> idata = new HashMap<String, Integer>();
				idata.put("warnflow", warnflow1);
				idata.put("errorflow", errorflow1);
				idata.put("warnpkts", warnpkts1);
				idata.put("errorpkts", errorpkts1);
				idata.put("warnbroad", warnbroad1);
				idata.put("errorbroad", errorbroad1);
				idata.put("warnpercent", warnpercent1);
				idata.put("errorperent", errorpercent1);
				linksetdata.put(linkid, idata);

			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception ex) {

		}
		ConfigDB.close(conn);
		try {
			LineItem line = null;
			int i = 0;
			for (svEdge edge : n_Lines.values()) {
				line = new LineItem(topoChart);
				i++;

				line.setLid(edge.getLid());
				line.setLsource(edge.getLsource());
				line.setLtarget(edge.getLtarget());
				line.setSinterface(edge.getSinterface());
				line.setTinterface(edge.getTinterface());
				String keykey = edge.getLsource() + edge.getSinterface();
				if (portss.containsKey(keykey)) {
					line.setSdesc(portss.get(keykey));
				}
				String keykey1 = edge.getLtarget() + edge.getTinterface();
				if (portss.containsKey(keykey1)) {
					line.setTdesc(portss.get(keykey1));
				}
				int j = (int) (Math.random() * 70);
				if (j == 0)
					j = 1;

				line.setFlow(j);
				if (linksetdata.containsKey(edge.getLid())) {
					line.setWarn(linksetdata.get(edge.getLid()).get("warnflow"));
					line.setError(linksetdata.get(edge.getLid()).get("errorflow"));
					line.setPktswarn(linksetdata.get(edge.getLid()).get("warnpkts"));
					line.setPktserror(linksetdata.get(edge.getLid()).get("errorpkts"));
					line.setBroadcasterror(linksetdata.get(edge.getLid()).get("warnbroad"));
					line.setBroadcastwarn(linksetdata.get(edge.getLid()).get("errorbroad"));
					line.setBwusagerror(linksetdata.get(edge.getLid()).get("warnpercent"));
					line.setBwusagewarn(linksetdata.get(edge.getLid()).get("errorperent"));
					line.setSelfalert(1);
				} else {
					line.setWarn(warnflow);
					line.setError(errorflow);
					line.setPktswarn(warnpkts);
					line.setPktserror(errorpkts);
					line.setBroadcasterror(errorbroad);
					line.setBroadcastwarn(warnbroad);
					line.setBwusagerror(errorpercent);
					line.setBwusagewarn(warnpercent);
					line.setSelfalert(0);
				}
				if (i == 30) {
					line.setFlow(170);
				}

			}
		} catch (Exception ex) {
			int nn = 0;
		}

		long endendTime = System.currentTimeMillis();

		System.out.println("绘制拓扑图据耗时" + (endendTime - endTime) + " (毫秒)");
	}

	public void cleardata() {

	}

	@Override
	public void setFocus() {
	}
	
	private FormToolkit formToolkit;
	private CTabFolder folder; // 切换视图
	public void createAlarmList(final SashForm alarmlist){
		formToolkit = new FormToolkit(Display.getDefault());
		final Section sctnNewSection_select = formToolkit.createSection(alarmlist, Section.TWISTIE | Section.TITLE_BAR | Section.LEFT_TEXT_CLIENT_ALIGNMENT);
		sctnNewSection_select.setLayout(new FillLayout());
		sctnNewSection_select.setText("报警列表");
//		final GridData gd=new GridData(GridData.FILL_HORIZONTAL);
//		gd.heightHint=500;
//		sctnNewSection_select.setLayoutData(gd);
		sctnNewSection_select.setExpanded(true);
		sctnNewSection_select.addExpansionListener(new IExpansionListener() {
			public void expansionStateChanging(ExpansionEvent e) {
				if((Boolean)e.data){
					alarmlist.setWeights(new int[]{7,3});
				}else{
					alarmlist.setWeights(new int[]{19,1});
				}
				alarmlist.layout();
			}
			public void expansionStateChanged(ExpansionEvent e) {
			}
		});
		folder = new CTabFolder(sctnNewSection_select, SWT.FLAT | SWT.TOP|SWT.CLOSE);
		sctnNewSection_select.setClient(folder);
		
		CTabItem detailsItem = new CTabItem(folder, SWT.NONE);
		detailsItem.setText("基本报警");
		
		final Table table=new Table(folder, SWT.BORDER);
		table.setLayout(new FillLayout());
		table.setHeaderVisible(true);
		table.setData("");
		table.setData(RWT.MARKUP_ENABLED, Boolean.TRUE);
		detailsItem.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent event) {
				table.dispose();
				folder.layout();
			}
		});
		TableColumn status=new TableColumn(table,SWT.NONE);
		status.setText("状态");
		status.setWidth(50);
		
		TableColumn machineip=new TableColumn(table,SWT.NONE);
		machineip.setText("设备ip");
		machineip.setWidth(200);
		
		TableColumn name=new TableColumn(table,SWT.NONE);
		name.setText("监测器");
		name.setWidth(200);
		
		TableColumn count=new TableColumn(table,SWT.NONE);
		count.setText("持续次数");
		count.setWidth(100);
		
		TableColumn alarmstatus=new TableColumn(table,SWT.NONE);
		alarmstatus.setText("告警状态");
		alarmstatus.setWidth(100);
		
		TableColumn time=new TableColumn(table,SWT.NONE);
		time.setText("报警时间");
		time.setWidth(200);
		
		TableColumn detail=new TableColumn(table,SWT.NONE);
		detail.setText("详细");
		detail.setWidth(600);
		createSubAction(table);
		fillPopMenu(table);
		detailsItem.setControl(table);
		createTableItem(table, null);
		folder.setSelection(detailsItem);
		alarmlist.layout();
	}
	public void createCTabFolder(String ip){
		CTabItem detailsItem = new CTabItem(folder, SWT.CLOSE);
		detailsItem.setData(ip);
		detailsItem.setText(ip+"报警");
		final Table table=new Table(folder, SWT.BORDER);
		table.setLayout(new FillLayout());
		table.setHeaderVisible(true);
		table.setData(ip);
		table.setData(RWT.MARKUP_ENABLED, Boolean.TRUE);
		TableColumn status=new TableColumn(table,SWT.NONE);
		status.setText("状态");
		status.setWidth(50);
		detailsItem.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent event) {
				table.dispose();
//				folder.layout();
			}
		});
		TableColumn machineip=new TableColumn(table,SWT.NONE);
		machineip.setText("设备ip");
		machineip.setWidth(200);
		
		TableColumn name=new TableColumn(table,SWT.NONE);
		name.setText("监测器");
		name.setWidth(200);
		
		TableColumn count=new TableColumn(table,SWT.NONE);
		count.setText("持续次数");
		count.setWidth(100);
		
		TableColumn alarmstatus=new TableColumn(table,SWT.NONE);
		alarmstatus.setText("告警状态");
		alarmstatus.setWidth(100);
		
		TableColumn time=new TableColumn(table,SWT.NONE);
		time.setText("报警时间");
		time.setWidth(200);
		
		TableColumn detail=new TableColumn(table,SWT.NONE);
		detail.setText("详细");
		detail.setWidth(600);
		detailsItem.setControl(table);
		createTableItem(table,ip);
		createSubAction(table);
		fillPopMenu(table);
		folder.setSelection(detailsItem);
	}
	private void createTableItem(Table table, String ip) {
		String sql="";
		DBQueryUtils.UpdateorDelete("delete from AlarmEventLog where MonitorStatus='good' ",  ConnectionBroker.get_SiteviewApi());
		if(ip!=null&&ip.length()>0)
			sql="select * from AlarmEventLog where EquipmentRecId "
				+ "in (select RecId from Equipment where ServerAddress "
				+ "='"+ip+"' )and isAlarm=1 and AlarmStatus not like '3'"
						+ "and MonitorRecId in (select RecId from Monitor)";
		else{
//			SimpleDateFormat simpdt=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			Date d=new Date();
//			String endtime=simpdt.format(d);
//			Calendar cal=Calendar.getInstance();
//			cal.add(Calendar.HOUR_OF_DAY, -2);
//			d=cal.getTime();
//			String starttime=simpdt.format(d);
			sql="select a.*,e.ServerAddress from AlarmEventLog a,Equipment e where "
					+ " isAlarm=1 and AlarmStatus not like '3'"
							+ "and MonitorRecId in (select RecId from Monitor) and a.EquipmentRecId=e.RecId";
		}
		DataTable dt=DBQueryUtils.Select(sql,  ConnectionBroker.get_SiteviewApi());
		table.removeAll();
		for(DataRow dr:dt.get_Rows()){
			TableItem tableitme=new TableItem(table, SWT.NONE);
			String[] text=new String[7];
			String monitortype=dr.get("MonitorType").toString();
			StringBuilder messageSb = new StringBuilder();
			StringBuilder toolMessage = new StringBuilder();
			text[0]=dr.get("MonitorStatus").toString();
			if(ip==null||ip.length()==0)
				text[1]=dr.get("ServerAddress").toString();
			else
				text[1]=ip;
			text[2]=dr.get("MonitorName").toString();
			text[3]=dr.get("MonitorCount").toString();
			text[4]=dr.get("AlarmStatus").toString();
			if(text[4].equals("1"))
				text[4]="告警";
			else if(text[4].equals("2"))
				text[4]="暂停";
			text[5]=dr.get("LastModDateTime").toString();
			text[6]=dr.get("MonitorValue").toString();
			InternationalUtils.getReturnValueMessage(
					ConnectionBroker.get_SiteviewApi(),
					dr.get("MonitorRecId").toString(),
					text[6], monitortype, messageSb, toolMessage, true);
			text[6]= messageSb.toString();
			tableitme.setText(text);
			tableitme.setData(dr.get("RecId").toString());
		}
	}
	private Menu popMenu;
	public void fillPopMenu(Table table){
		MenuManager menuManage=new MenuManager();
		menuManage.add(startalarm);
		menuManage.add(endalarm);
		popMenu=menuManage.createContextMenu(table);
		table.setMenu(popMenu);
	}
	private IAction startalarm;
	private IAction endalarm;
	private void createSubAction(final Table table){
		startalarm=new Action(EccMessage.get().RESET_ALARM) {
			public void run() {
				if(table.getSelection().length<=0)
					return;
				TableItem tableitem =table.getSelection()[0];
				if(tableitem.getText(4).equals("告警"))
					return;
				tableitem.setText(4,"告警");
				String update_sql="update AlarmEventLog set AlarmStatus='1' where RecId='"+tableitem.getData().toString()+"'";
				DBQueryUtils.UpdateorDelete(update_sql, ConnectionBroker.get_SiteviewApi());
			}
		};
		endalarm=new Action(EccMessage.get().ALARM_STOP) {
			public void run() {
				if(table.getSelection().length<=0)
					return;
				TableItem tableitem =table.getSelection()[0];
				if(tableitem.getText(4).equals("暂停"))
					return;
				tableitem.setText(4,"暂停");
				String update_sql="update AlarmEventLog set AlarmStatus='2' where RecId='"+tableitem.getData().toString()+"'";
				DBQueryUtils.UpdateorDelete(update_sql, ConnectionBroker.get_SiteviewApi());
			}
		};
	}
}
