package com.siteview.leveltop.draw2d.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.internal.PluginActionContributionItem;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.part.PluginTransfer;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraph;
import com.siteview.ecc.main.ui.cnftree.MonitorTreeViews;
import com.siteview.leveltop.draw2d.Activator;
import com.siteview.leveltop.draw2d.container.DitryContainer;
import com.siteview.leveltop.draw2d.listener.DeleteNodeListener;
import com.siteview.leveltop.draw2d.listener.OpenNodeListener;
import com.siteview.leveltop.draw2d.nls.Message;
import com.siteview.leveltop.draw2d.pojo.EdgeInfo;
import com.siteview.leveltop.draw2d.pojo.LevelTopCustomEvent;
import com.siteview.leveltop.draw2d.pojo.NodeInfo;
import com.siteview.leveltop.draw2d.pojo.NodeStyle;
import com.siteview.leveltop.draw2d.pojo.ResourceTypeInfo;
import com.siteview.leveltop.draw2d.util.LevelTopDataUtils;
import com.siteview.leveltop.draw2d.util.MxGraphUtils;
import com.siteview.leveltop.draw2d.util.VariableUtils;
import com.siteview.utils.control.ControlUtils;
import com.siteview.utils.entities.ImageKeys;
import com.siteview.utils.entities.TreeEquipmentObject;
import com.siteview.utils.entities.TreeObject;
import com.siteview.utils.tree.EccTreeData;
import com.weadmin.mxgraph_rap.GraphJS;
import com.weadmin.mxgraph_rap.GraphJS.MxGraphEvent;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.SiteviewException;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ConnectionBroker;
import siteview.forms.common.VariableRunnable;
import siteview.windows.forms.ImageHelper;

@SuppressWarnings("restriction")
public class LevelTopComposite extends Composite {

	private static final long serialVersionUID = 1L;
	private GraphJS canvas;
	private ISiteviewApi siteviewApi;
	private mxGraph contents;
	private MxGraphUtils mxGraphUtils;
	private boolean isInit;
	private LevelTopDataUtils dataUtils;
	private VariableUtils variableUtils;

	private Button saveTopoBtn,deleteNodeBtn,refreshBtn;
	
	
	private Map<String, ResourceTypeInfo> resourceTypeInfoMap;

	private DitryContainer dirContainer;

	private Map<LevelTopCustomEvent, VariableRunnable> eventMap;

	private Display display;
	private CLabel labelMessage;

	private ImageRegistry imageRegistor;

	private Menu deleteMenu;
	
	private DeleteNodeListener deleteNodeListener;
	private OpenNodeListener openNodeListener;
	
	public LevelTopComposite(Composite parent, int style) {
		super(parent, style);
		parent.setLayout(new FillLayout());
		this.siteviewApi = ConnectionBroker.get_SiteviewApi();
		this.mxGraphUtils = MxGraphUtils.getInstance();
		this.dataUtils = LevelTopDataUtils.getInstance();
		this.variableUtils = VariableUtils.getInstance();
		this.dirContainer = new DitryContainer();
		this.display = parent.getDisplay();
		initListener();
		initImageRepostory();
		initContentArea();
	}

	public void setEventMap(Map<LevelTopCustomEvent, VariableRunnable> eventMap) {
		this.eventMap = eventMap;
	}

	public void initImageRepostory() {
		this.imageRegistor = new ImageRegistry(Display.getCurrent());
		imageRegistor.put("load", ImageHelper.LoadDescriptor(Activator.PLUGIN_ID, "icons/load.gif"));
		imageRegistor.put("good", ImageHelper.LoadDescriptor(Activator.PLUGIN_ID, "icons/good.png"));
		imageRegistor.put("warning", ImageHelper.LoadDescriptor(Activator.PLUGIN_ID, "icons/warning.png"));
		imageRegistor.put("error", ImageHelper.LoadDescriptor(Activator.PLUGIN_ID, "icons/error.png"));
		imageRegistor.put("disabled", ImageHelper.LoadDescriptor(Activator.PLUGIN_ID, "icons/disabled.png"));
		imageRegistor.put("disapear", ImageHelper.LoadDescriptor(Activator.PLUGIN_ID, "icons/disapear.png"));
	}
	
	public void initListener(){
		openNodeListener = new OpenNodeListener();
		deleteNodeListener = new DeleteNodeListener();
	}

	public void initEdgeMenu(Composite composite) {
		deleteMenu = new Menu(composite);
		MenuItem menuItemDeleteEage = new MenuItem(deleteMenu, SWT.NONE);
		menuItemDeleteEage.setImage(ImageKeys.getImage(ImageKeys.DeleteMonitor));
		menuItemDeleteEage.setText(Message.get().DELETE_EDGE);
		menuItemDeleteEage.addSelectionListener(deleteNodeListener);
	}

	public void initContentArea() {

		this.setLayout(new FormLayout());
		Composite toolbarComposite = createToolarComposite();
		Composite draw2dComposite = createCanversParentComposite(toolbarComposite);

		draw2dComposite.setLayout(new FillLayout());

		canvas = new GraphJS(draw2dComposite, SWT.NONE);
		deleteNodeListener.setCanvas(canvas);
		contents = new mxGraph();
		canvas.setGraph(contents);
		initEdgeMenu(canvas);

		draw2dComposite.addControlListener(new ControlListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void controlResized(ControlEvent e) {
				if (!isInit) {
					try {
						loadData(((Composite) e.getSource()).getSize());
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					isInit = true;
				}
			}

			@Override
			public void controlMoved(ControlEvent e) {

			}
		});

		addDropTargetListener();

		addCanvasListener();

		try {
			resourceTypeInfoMap = dataUtils.getEquipmentSimpleTypeMap(siteviewApi);
		} catch (SiteviewException e1) {
			e1.printStackTrace();
		}

	}

	/*
	 * 创建工具栏
	 */
	public Composite createToolarComposite() {
		Composite toolbarComposite = new Composite(this, SWT.NONE);
		//
		FormData formdate = new FormData();
		formdate.top = new FormAttachment(0);
		formdate.left = new FormAttachment(0);
		formdate.right = new FormAttachment(100, 0);
		formdate.bottom = new FormAttachment(0, 40);
		toolbarComposite.setLayoutData(formdate);

		GridLayoutFactory.fillDefaults().numColumns(1).extendedMargins(5, 5, 0, 0).spacing(5, 0).applyTo(toolbarComposite);
		Composite operatorComposite = new Composite(toolbarComposite, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(16).extendedMargins(5, 5, 0, 0).spacing(5, 0).applyTo(operatorComposite);
		saveTopoBtn = ControlUtils.createHighlightButton(operatorComposite, Message.get().SAVE_TOPO);
		//// redrawBtn = ControlUtils.createCommonButton(toolbarComposite,
		//// "重绘拓扑");
		new Label(operatorComposite, SWT.NONE).setText("|");

		deleteNodeBtn = ControlUtils.createCommonButton(operatorComposite, Message.get().DELETE_SEL_NODE);
		
		new Label(operatorComposite, SWT.NONE).setText("|");

		refreshBtn =  ControlUtils.createCommonButton(operatorComposite, Message.get().RESOURCE_TOPO_SYNCH);
		refreshBtn.addSelectionListener(new SelectionAdapter() {

			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				synchResourceStatus();
			}
		});
		
		new Label(operatorComposite, SWT.NONE).setText("|");
		
		Button btnIn = ControlUtils.createCommonButton(operatorComposite, Message.get().RESOURCE_TOPO_OUT);

		Button btnOut = ControlUtils.createCommonButton(operatorComposite, Message.get().RESOURCE_TOPO_IN);
		
		Label labelSp2 = new Label(operatorComposite,SWT.NONE);
		labelSp2.setText("|");
		 
		Label labelStatu = new Label(operatorComposite,SWT.NONE);
	    labelStatu.setText(Message.get().RESOURCE_TOPO_STATUS);
		  
		CLabel labelStatu_good = new CLabel(operatorComposite,SWT.NONE);
		labelStatu_good.setText(Message.get().RESOURCE_NODE_GOOD);
		labelStatu_good.setImage(imageRegistor.get("good"));
		labelStatu_good.setToolTipText(Message.get().RESOURCE_NODE_GOOD_TIP);
		  
		CLabel labelStatu_warning = new CLabel(operatorComposite,SWT.NONE);
		labelStatu_warning.setText(Message.get().RESOURCE_NODE_WARNING);
		labelStatu_warning.setImage(imageRegistor.get("warning"));
		labelStatu_warning.setToolTipText(Message.get().RESOURCE_NODE_WARNING_TIP);
		  
		CLabel labelStatu_error = new CLabel(operatorComposite,SWT.NONE);
		labelStatu_error.setText(Message.get().RESOURCE_NODE_ERROR);
		labelStatu_error.setImage(imageRegistor.get("error"));
		labelStatu_error.setToolTipText(Message.get().RESOURCE_NODE_ERROR_TIP);
		
		CLabel labelStatu_disabled = new CLabel(operatorComposite,SWT.NONE);
		labelStatu_disabled.setText(Message.get().RESOURCE_NODE_DISABLED);
		labelStatu_disabled.setImage(imageRegistor.get("disabled"));
		labelStatu_disabled.setToolTipText(Message.get().RESOURCE_NODE_DISABLED_TIP);
		
		CLabel labelStatu_disapear = new CLabel(operatorComposite,SWT.NONE);
		labelStatu_disapear.setText((Message.get().RESOURCE_NODE_DISAPEAR));
		labelStatu_disapear.setImage(imageRegistor.get("disapear"));
		labelStatu_disapear.setToolTipText(Message.get().RESOURCE_NODE_DISAPEAR_TIP);

		labelMessage = new CLabel(operatorComposite, SWT.BORDER);
		labelMessage.setImage(imageRegistor.get("load"));
		GridData gridData = new GridData();
		gridData.widthHint = 300;
		labelMessage.setLayoutData(gridData);
		labelMessage.setVisible(false);

		btnIn.addSelectionListener(new SelectionAdapter() {

			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				canvas.zoomIn();
			}
		});

		btnOut.addSelectionListener(new SelectionAdapter() {

			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				canvas.zoomOut();
			}
		});

		saveTopoBtn.addSelectionListener(new SelectionAdapter() {

			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					saveTopoMapInfo();
				} catch (SiteviewException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		deleteNodeBtn.addSelectionListener(deleteNodeListener);

		Label labelHBar = new Label(toolbarComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData data = new GridData(GridData.FILL_BOTH);
		labelHBar.setLayoutData(data);

		return toolbarComposite;
	}

	/*
	 * 创建画版父面板
	 */
	public Composite createCanversParentComposite(Composite topComposite) {

		Label lblTitle = new Label(this, SWT.NONE);
		lblTitle.setData(RWT.MARKUP_ENABLED, Boolean.TRUE);
		lblTitle.setText(String.format("<span style='color:#575757;font-weight:bold;'>%s</span>",Message.get().RESOURCE_TOPO_NOTE));

		FormData formdate = new FormData();
		formdate.top = new FormAttachment(topComposite, 0);
		formdate.left = new FormAttachment(0, 10);
		formdate.right = new FormAttachment(0, 200);
		formdate.bottom = new FormAttachment(0, 60);
		lblTitle.setLayoutData(formdate);

		Composite draw2dComposite = new Composite(this, SWT.NONE);

		formdate = new FormData();
		formdate.top = new FormAttachment(topComposite, 5);
		formdate.left = new FormAttachment(0);
		formdate.right = new FormAttachment(100, 0);
		formdate.bottom = new FormAttachment(100);

		draw2dComposite.setLayoutData(formdate);

		return draw2dComposite;
	}

	/*
	 * 加载数据
	 */
	public void loadData(Point point) throws Exception {

		/*
		 * 从数据库中拿去点和边
		 */
		dataUtils = LevelTopDataUtils.getInstance();

		DataTable dt = dataUtils.getLevelTopMap(siteviewApi);
		if (dt.get_Rows().size() > 0) {
			String xml = dt.get_Rows().get(0).get_Item("mapData").toString();
			canvas.loadGrapXml(xml);
		} else {
			List<Map<String, String>> netWorkNodeListMap = dataUtils.GetNetWorkNodes(siteviewApi);
			List<Map<String, String>> netWorkNodeLineListMap = dataUtils.getNetWorkNodeLines(siteviewApi);

			final int screen_width = point.x - 300;
			final int screen_height = point.y - 50;
			Map<String, Integer> relationMap = dataUtils.computeNodeRelationValue(netWorkNodeListMap, screen_width, screen_height);
			int min_x = relationMap.get("min_x");
			int min_y = relationMap.get("min_y");
			int percent_x = relationMap.get("percent_x");
			int percent_y = relationMap.get("percent_y");

			/*
			 * 开始绘图-点，跳过前四条无用的数据
			 */

			int i = 0;
			for (Map<String, String> netWorkNodeMap : netWorkNodeListMap) {
				if (i > 3) {
					int x = Integer.parseInt(netWorkNodeMap.get("px"));
					int y = Integer.parseInt(netWorkNodeMap.get("py"));
					String text = netWorkNodeMap.get("name");
					int level = Integer.parseInt(netWorkNodeMap.get("TypePosition"));
					mxGraphUtils.addNode(contents, netWorkNodeMap.get("id"), (x - min_x) * percent_x + 48, (y - min_y) * percent_y, 48, 48, text, variableUtils.getLevelTypeStyle(level));
				}
				i++;
			}

			/*
			 * 开始绘图-边
			 */
			if (netWorkNodeLineListMap.size() > 0) {
				for (Map<String, String> netWorkNodeLineMap : netWorkNodeLineListMap) {
					String fromNodeId = netWorkNodeLineMap.get("FromNode");
					String ToNodeId = netWorkNodeLineMap.get("ToNode");
					mxGraphUtils.addEdge(contents, fromNodeId, ToNodeId, null, "");
				}
			}

			/*
			 * 数据刷新到前台
			 */
			canvas.setModel(contents.getModel());
		}
		
		final DataTable statuDt = dataUtils.synResourceStatus(siteviewApi);
		this.setMessage(Message.get().LOAD_RESOURCE_TOPO_STATUS);
		Display.getCurrent().timerExec(3000, new Runnable() {
			public void run() {
				List<String> ids = new ArrayList<String>();
				for (DataRow dr : statuDt.get_Rows()) {
					String id = dr.get_Item("resource_id").toString();
					String status = dr.get_Item("resource_status").toString();
					String nodeId = dr.get_Item("NodeId").toString();
					String equipmentType = dr.get_Item("EquipmentType").toString();
					int level = resourceTypeInfoMap.get(equipmentType).getLevel();
					mxCell mxCell = mxGraphUtils.getNodeById(contents,id);
					if (mxCell != null&&mxCell.isVertex()) {
						canvas.setCellStyle(mxCell.getId(), variableUtils.getStyle(mxCell,status).toStyleString());
						canvas.setTooltip(mxCell.getId(),variableUtils.mkToolTipHtml(nodeId,level,status));
					}
					ids.add(id);
				}
				Map<String, Object> cells = ((mxGraphModel) contents.getModel()).getCells();
				for(Entry<String,Object> entry:cells.entrySet()){
					mxCell mxCell = (mxCell)entry.getValue();
					if(mxCell.isVertex()&&!ids.contains(mxCell.getId())){
						canvas.setCellStyle(mxCell.getId(), variableUtils.getStyle(mxCell,"disapear").toStyleString());
						canvas.setTooltip(mxCell.getId(),variableUtils.mkToolTipHtml("unknow",-1,"disapear"));
					}
				}
				LevelTopComposite.this.setMessage("");
			}
		});
		
	}

	/*
	 * 给画布添加DropTarget事件
	 */
	public void addDropTargetListener() {
		DropTarget dt = new DropTarget(canvas, DND.DROP_MOVE);
		dt.setTransfer(new Transfer[] { LocalSelectionTransfer.getTransfer(), PluginTransfer.getInstance() });
		dt.addDropListener(new DropTargetAdapter() {

			private static final long serialVersionUID = 1L;

			public void drop(DropTargetEvent event) {

				Point currentPoint = canvas.toControl(event.x, event.y);
				int x = currentPoint.x;
				int y = currentPoint.y;

				TreeSelection treeSelection = (TreeSelection) event.data;
				Object obj = treeSelection.getFirstElement();
				TreeEquipmentObject treeEqObj = (TreeEquipmentObject) obj;

				final String resource_id = treeEqObj.getId();
				
				String[] sameResourceId = new String[1];
				String[] linkResourceId = new String[1];
				int[] order = new int[1];
				if (mxGraphUtils.getNodeById(contents, resource_id) == null) {
					findSameNode(resource_id,sameResourceId,linkResourceId,order);
					if(sameResourceId[0]==null){
						String resource_alias = treeEqObj.getTitle();
						String equipmentType = treeEqObj.getEquipmenttype();
						String status = treeEqObj.getStatus();
						String ip = treeEqObj.getIpAddress();

						ResourceTypeInfo resourceType = resourceTypeInfoMap.get(equipmentType);

						int[] initSize = variableUtils.getLevelTypeSize(resourceType.getLevel());

						mxCell mxCell = mxGraphUtils.addNode(contents, resource_id, x, y, initSize[0], initSize[1], resource_alias, variableUtils.getLevelTypeStyle(resourceType.getLevel()));
						if (mxCell != null) {
							canvas.setCellStyle(mxCell.getId(), variableUtils.getStyle(mxCell, status).toStyleString());
							canvas.setTooltip(mxCell.getId(),variableUtils.mkToolTipHtml(ip,resourceType.getLevel(), status));
						}
						
						if(linkResourceId[0]!=null){
							if(order[0]==1){
								mxGraphUtils.addEdge(contents,linkResourceId[0],resource_id,"",null);
							}
							else{
								mxGraphUtils.addEdge(contents,resource_id,linkResourceId[0],"",null);
							}
						}
						
						setDirty(true);
					}
					else{
						selectMxNode(sameResourceId[0]);
					}
				} 
				else {
					selectMxNode(resource_id);
				}

			}
		});
	}
	
	public void findSameNode(String resourceId,String[] sameResourceId,String[] linkResourceId,int[] order){
		TreeEquipmentObject resourceTro = (TreeEquipmentObject)EccTreeData.treemodles.get(resourceId);
		ResourceTypeInfo resourceTypeInfo = resourceTypeInfoMap.get(resourceTro.getEquipmenttype());
		Map<String, Object> cells = ((mxGraphModel) contents.getModel()).getCells();
		boolean isSameResource = false;
		boolean isLinkEdge = false;
		for (Entry<String, Object> entry : cells.entrySet()) {
			mxCell mxCell = (mxCell) entry.getValue();
			if(mxCell.isVertex()){
				String cur_res_id = mxCell.getId();
				TreeEquipmentObject currentTro = (TreeEquipmentObject)EccTreeData.treemodles.get(cur_res_id);
				if(currentTro!=null&&currentTro.getIpAddress().equals(resourceTro.getIpAddress())){
					if(currentTro.getEquipmenttype().equals(resourceTro.getEquipmenttype())){
						sameResourceId[0] = cur_res_id;
						isSameResource = true;
					}
					else{
						ResourceTypeInfo currentResourceInfo = resourceTypeInfoMap.get(currentTro.getEquipmenttype());
						int result = Math.abs(currentResourceInfo.getLevel()-resourceTypeInfo.getLevel());
						if(result==1){
							order[0]=currentResourceInfo.getLevel()-resourceTypeInfo.getLevel();
							linkResourceId[0] = cur_res_id;
							isLinkEdge = true;
						}
					}
				}
			}
			if(isSameResource&&isLinkEdge){
				break;
			}
		}
	}

	/*
	 * 添加canvas事件
	 */
	public void addCanvasListener(){
		canvas.addGraphListener(new mxIEventListener() {
			@Override
			public void invoke(Object sender, mxEventObject evt) {
				final Map<String, Object> prop = evt.getProperties();
				if(evt.getName().equals(MxGraphEvent.CELL_CONNECT)){
					if(prop.get("source").toString().equalsIgnoreCase("false")){
//						String edgejson = prop.get("edge").toString();
//						JSONObject jsonObject = JSONObject.fromObject(edgejson);
//						String sourceId = jsonObject.getString("source");
//						String targetId = jsonObject.getString("target");
//						String edgeId = jsonObject.getString("id");
//						mxCell mxSource = mxGraphUtils.getEdge(contents,sourceId);
//						mxCell mxTarget = mxGraphUtils.getEdge(contents,targetId);
//						mxCell edgeMxcell = mxGraphUtils.getNodeById(contents,edgeId);
//						if(mxSource!=null&&mxTarget!=null){
//							canvas.updateEdgeLabelPosition(edgeId, Math.abs((int)mxSource.getGeometry().getX()-(int)mxTarget.getGeometry().getX()),Math.abs((int)mxSource.getGeometry().getX()-(int)mxTarget.getGeometry().getX()), variableUtils.getAngle((int)mxSource.getGeometry().getX(),(int)mxSource.getGeometry().getY(),(int)mxTarget.getGeometry().getX(),(int)mxTarget.getGeometry().getY()));
//						}
						
						
						
						setDirty(true);
					}
				}
				else if(evt.getName().equals(MxGraphEvent.CELL_MOVED)){
					setDirty(true);
				}
				else if(evt.getName().equals(MxGraphEvent.CELL_REMOVE)){
					setDirty(true);
				}
				else if(evt.getName().equals(MxGraphEvent.EDGE_SELECT)){
					final String button = prop.get("button").toString();
					if(button.equals("2")){
						display.asyncExec(new Runnable() {
							@Override
							public void run() {
								int x = (int) Double.parseDouble(prop.get("x").toString());
								int y = (int) Double.parseDouble(prop.get("y").toString());
								Point point = canvas.toDisplay(x, y);
								deleteMenu.getItems()[0].setText(Message.get().DELETE_EDGE);
								deleteMenu.setLocation(point.x, point.y);
								deleteMenu.setVisible(true);
							}
						});
					}
					
				}
				else if(evt.getName().equals(MxGraphEvent.NODE_SELECT)){
					final String button = prop.get("button").toString();
					final String nodeId = prop.get("id").toString();
					final TreeObject element = EccTreeData.treemodles.get(nodeId);
					display.asyncExec(new Runnable() {
						@Override
						public void run() {
							if(element!=null){
								CommonViewer treeview = MonitorTreeViews.getMonitorTreeViews().getCommonViewer();
//								if (!treeview.getExpandedState(element.getParent()))
//									treeview.expandToLevel(element.getParent(), 1);
								MonitorTreeViews.getMonitorTreeViews().getCommonViewer().setSelection(new StructuredSelection(element));
								TreeItem[] selection = treeview.getTree().getSelection();
								if(selection.length>0){
									treeview.getTree().setTopItem((TreeItem) treeview.getTree().getSelection()[0]);
								}
								
								if(button.equals("2")){
									
									int x = (int) Double.parseDouble(prop.get("x").toString());
									int y = (int) Double.parseDouble(prop.get("y").toString());
									Point point = canvas.toDisplay(x, y);
									
									Menu treeMenu = treeview.getTree().getMenu();
									
									treeMenu.setLocation(point.x, point.y);
									treeMenu.setVisible(true);
									
									MenuItem openMenuItem = new MenuItem(treeMenu, SWT.NONE, 0);
									openMenuItem.setText(Message.get().OPEN_SOURCE);
									openMenuItem.setImage(ImageKeys.getImage(ImageKeys.Monitor));
									openMenuItem.addSelectionListener(openNodeListener);
									
									new MenuItem(treeMenu, SWT.SEPARATOR, 1);
									
									MenuItem deleteMenuItem = new MenuItem(treeMenu, SWT.NONE, 2);
									deleteMenuItem.setText(Message.get().DELETE_NODE);
									deleteMenuItem.setImage(ImageKeys.getImage(ImageKeys.DeleteMonitor));
									deleteMenuItem.addSelectionListener(deleteNodeListener);
									
									new MenuItem(treeMenu, SWT.SEPARATOR, 3);
									
									for(MenuItem menuItem:treeMenu.getItems()){
										if(menuItem.getData()!=null&&menuItem.getData() instanceof PluginActionContributionItem){
											PluginActionContributionItem item = (PluginActionContributionItem)menuItem.getData();
											if(item.getId().endsWith("locationAction")){
												menuItem.dispose();
											}
										}
									}
									
								}
							}
							else{
								if(button.equals("2")){
									int x = (int) Double.parseDouble(prop.get("x").toString());
									int y = (int) Double.parseDouble(prop.get("y").toString());
									Point point = canvas.toDisplay(x, y);
									deleteMenu.getItems()[0].setText(Message.get().DELETE_NODE);
									deleteMenu.setLocation(point.x, point.y);
									deleteMenu.setVisible(true);
								}
							}
						}
					});
				}
			}
		});
	}


	public void synchResourceStatus() {
		this.setMessage(Message.get().LOAD_RESOURCE_TOPO_STATUS);
		Map<String, Object> cells = ((mxGraphModel) contents.getModel()).getCells();
		for (Entry<String, Object> entry : cells.entrySet()) {
			mxCell mxCell = (mxCell) entry.getValue();
			if (mxCell.isVertex()) {
				String id = mxCell.getId();
				if (EccTreeData.treemodles.get(id) != null) {
					TreeEquipmentObject tro = (TreeEquipmentObject) EccTreeData.treemodles.get(id);
					NodeStyle nodeStyle = new NodeStyle();
					nodeStyle.fromStyleString(mxCell.getStyle());
					canvas.setCellStyle(id, variableUtils.getStyle(mxCell, tro.getStatus()).toStyleString());
					canvas.setTooltip(id,variableUtils.mkToolTipHtml(tro.getIpAddress(),resourceTypeInfoMap.get(tro.getEquipmenttype()).getLevel(),tro.getStatus()));
				} else {
					canvas.setCellStyle(id, variableUtils.getStyle(mxCell, "disapear").toStyleString());
					canvas.setTooltip(id,variableUtils.mkToolTipHtml("unkown",-1,"disapear"));
				}
			}
		}
		this.setMessage("");
	}

	public void selectMxNode(final String... ids) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				canvas.selectCells(ids);
			}
		});

	}

	/*
	 * 保存图信息
	 */
	public void saveTopoMapInfo() throws SiteviewException {

		Map<String, NodeInfo> nodeInfoMap = dataUtils.getNodeInfoMap(siteviewApi);
		Map<String, EdgeInfo> edgeInfoMap = dataUtils.getEdgeInfoMap(siteviewApi);

		StringBuffer sb = new StringBuffer();

		Map<String, Object> cells = ((mxGraphModel) contents.getModel()).getCells();

		List<String> edgeCache = new ArrayList<String>();

		for (Entry<String, Object> entry : cells.entrySet()) {
			mxCell mxCell = (mxCell) entry.getValue();
			if (mxCell.isEdge()) {
				String sourceResourceId = mxCell.getSource().getId();
				String targetResourceId = mxCell.getTarget().getId();
				sb.append(sourceResourceId).append("-").append(targetResourceId);
				EdgeInfo edgeInfo = edgeInfoMap.get(sb.toString());
				if (edgeInfo == null) {
					dirContainer.addEdgeInfo(new EdgeInfo(sourceResourceId, targetResourceId));
				}
				edgeCache.add(sb.toString());
				sb.setLength(0);
			} else if (mxCell.isVertex()) {
				String resource_id = mxCell.getId();
				String resource_alias = mxCell.getValue().toString();
				NodeInfo db_nodeInfo = nodeInfoMap.get(resource_id);
				if (db_nodeInfo == null) {
					dirContainer.addNodeInfo(new NodeInfo(resource_id, resource_alias));
				} else if (db_nodeInfo.isChange(resource_id, resource_alias)) {
					dirContainer.updateNodeInfo(new NodeInfo(resource_id, resource_alias));
				}
			}
		}

		/*
		 * 清理数据库中多余的数据
		 */
		for (Entry<String, NodeInfo> entry : nodeInfoMap.entrySet()) {
			mxCell findNode = mxGraphUtils.getNodeById(contents, entry.getKey());
			if (findNode == null) {
				dirContainer.deleteNodeInfo(entry.getValue());
			}
		}

		/*
		 * 清理数据库中多余的数据
		 */
		for (Entry<String, EdgeInfo> entry : edgeInfoMap.entrySet()) {
			if (!edgeCache.contains(entry.getValue().getLinkId())) {
				dirContainer.deleteEdgeInfo(entry.getValue());
			}
		}

		try {
			dirContainer.storeInDatabase(siteviewApi, canvas.getGraphXml());
			setDirty(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setDirty(final boolean dirty) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				eventMap.get(LevelTopCustomEvent.DIRTY).run(dirty);
			}
		});

	}

	public void setMessage(String content) {
		labelMessage.setText(content);
		labelMessage.setVisible(content.length()>0);
		refreshBtn.setEnabled(content.length()==0);
	}
	
	// saveOtherTopoBtn.addSelectionListener(new SelectionAdapter() {
	//
	// private static final long serialVersionUID = 1L;
	//
	// @Override
	// public void widgetSelected(SelectionEvent e) {
	// try{
	// File file = new
	// File(System.getProperty("user.dir")+"/temp/"+GuidUtils.CreateGuid()+".xml");
	// if(!file.getParentFile().exists()){
	// file.mkdirs();
	// }
	// FileUtils.writeStringToFile(file, canvas.getGraphXml());
	// ExportFileService.exportToBrowser(file);
	// }
	// catch(Exception ex){
	// ex.printStackTrace();
	// }
	// }
	// });
	//
	// loadLocalBtn.addSelectionListener(new SelectionAdapter() {
	// private static final long serialVersionUID = 1L;
	//
	// @Override
	// public void widgetSelected(SelectionEvent e) {
	// FileDialog fileDialog = new
	// FileDialog(Display.getCurrent().getActiveShell());
	// String result = fileDialog.open();
	// if(result!=null&&result.trim().length()>0){
	// File uploadFile = null;
	// if(result.endsWith("zip")){
	// try {
	// File zipFile = new File(result);
	// @SuppressWarnings("resource")
	// ZipFile zipPackage = new ZipFile(zipFile);
	// @SuppressWarnings("unchecked")
	// Enumeration<ZipEntry> entries =
	// (Enumeration<ZipEntry>)zipPackage.entries();
	// if(entries.hasMoreElements()) {
	// ZipEntry entry = entries.nextElement();
	// File unzipFile = new File (zipFile.getParentFile(),entry.getName());
	// BufferedInputStream bis = new
	// BufferedInputStream(zipPackage.getInputStream(entry));
	// BufferedOutputStream bos = new BufferedOutputStream(new
	// FileOutputStream(unzipFile));
	// byte[] buf = new byte[4096];
	// int length = 0;
	// while ((length = bis.read(buf)) > 0) {
	// bos.write(buf, 0, length);
	// bos.flush();
	// }
	// bis.close();
	// bos.close();
	// uploadFile = unzipFile;
	// }
	// } catch (Exception e1) {
	// MsgBox.ShowWarning("该文件无法用zip格式解析, 请检查上传文件是否符合zip格式包");
	// e1.printStackTrace();
	// }
	// }
	// else{
	// uploadFile = new File(result);
	// }
	// try {
	// canvas.loadGrapXml(FileUtils.readFileToString(uploadFile));
	// } catch (IOException e1) {
	// e1.printStackTrace();
	// }
	// }
	// }
	// });
}
