package com.siteview.NNM.dialogs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;

import siteview.windows.forms.MsgBox;

import com.siteview.NNM.Activator;
import com.siteview.NNM.NNMInstanceData;
import com.siteview.NNM.Editors.NNMainEditorInput;
import com.siteview.NNM.Editors.TopoManage;
import com.siteview.NNM.modles.RootNNM;
import com.siteview.NNM.modles.SubChartModle;
import com.siteview.NNM.modles.TopoModle;
import com.siteview.nnm.data.ConfigDB;
import com.siteview.nnm.data.DBManage;
import com.siteview.nnm.data.EntityManager;
import com.siteview.nnm.data.FlowDataManager;
import com.siteview.nnm.data.model.TopoChart;
import com.siteview.nnm.data.model.entity;
import com.siteview.nnm.data.model.svEdge;
import com.siteview.nnm.data.model.svNode;
import com.siteview.topo.TopoMap;

import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.swt.widgets.Display;

public class DeviceLocate extends Dialog {
	private Text text;
	private Combo combotype;
	TopoMap topochart;
	String subtopo;
	private Text textjg;
	private String mip = "";
	private String mmac = "";
	private final FormToolkit formToolkit = new FormToolkit(
			Display.getDefault());

	protected void configureShell(Shell newShell) {
		// newShell.setSize(450, 320);
		// newShell.setLocation(400, 175);
		newShell.setText("设备定位");
		super.configureShell(newShell);
	}

	/*
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	/**
	 * @wbp.parser.constructor
	 */
	public DeviceLocate(Shell parentShell,  String subtopo) {
		super(parentShell);
		this.topochart = topochart;
		this.subtopo = subtopo;
	}

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public DeviceLocate(Shell parentShell, String ip, String mac) {
		super(parentShell);
		this.mip = ip;
		this.mmac = mac;
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 2;
		Label lbtype = new Label(container, SWT.NONE);
		lbtype.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		lbtype.setText("设备类别:");
		combotype = new Combo(container, SWT.BORDER);
		combotype.add("设备IP");
		combotype.select(0);
		combotype.add("设备MAC");
		combotype.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				String key = combotype.getText();
				if (key.equals("设备IP")) {
					text.setText(mip);
				} else {
					text.setText(mmac);
				}

			}
		});
		// combotype.addSelectionListener(listener);
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblNewLabel.setText("定位设备:");

		text = new Text(container, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text.setText(mip);
		new Label(container, SWT.NONE);
		Button btn = new Button(container, SWT.NONE);
		btn.setText("设备定位");
		btn.addListener(SWT.MouseUp, new Listener() {
			@Override
			public void handleEvent(Event event) {
				String ip = text.getText();
				for (String key : EntityManager.allEntity.keySet()) {
					entity ent = EntityManager.allEntity.get(key);
					if (ent instanceof svNode) {
						String ip1 = "";
						if (combotype.getText().equals("设备IP")) {
							ip1 = ((svNode) ent).getLocalip();
						} else {
							ip1 = ((svNode) ent).getMac();
						}
						if (ip1.equals(ip)) {
							ip = ((svNode) ent).getLocalip();
						}
					}
				}
				String portip = "";
				String portname = "";
				String portport = "";
				for (String lid : EntityManager.allEdges.keySet()) {

					if (EntityManager.allEdges.get(lid).getLtarget()
							.equals(EntityManager.Ip2nids.get(ip))) {
						entity enode = EntityManager.allEntity
								.get(EntityManager.allEdges.get(lid)
										.getLsource());
						try {
							portip = ((svNode) enode).getLocalip();
							portname = ((svNode) enode).getDevicename();
						} catch (Exception ex) {

						}
						Connection conn = ConfigDB.getConn();
						String sql = "select desc from ports where id='"
								+ EntityManager.allEdges.get(lid).getLsource()
								+ "' and pindex='"
								+ EntityManager.allEdges.get(lid)
										.getSinterface() + "'";
						String desc = "";

						try {
							ResultSet rs = ConfigDB.query(sql, conn);
							while (rs.next()) {
								desc = rs.getString("desc");
								if (desc == null)
									desc = "";
							}
						} catch (NumberFormatException e) {
							e.printStackTrace();
						} catch (SQLException e) {
							e.printStackTrace();
						} catch (Exception ex) {

						}
						ConfigDB.close(conn);
						if (((svNode) enode).getSvgtype() != 6)
							portport = EntityManager.allEdges.get(lid)
									.getSinterface() + " :" + desc;

					}
				}
				String desc = "无设备连接信息";
				if (!portport.isEmpty()) {
					desc = text.getText() + " 在 " + portip + " 的" + portport
							+ "端口上(依据拓扑扫描结果)";
				}
				if (portip.startsWith("HUB")) {
					desc = text.getText() + "连接在 " + portip + " (" + portname
							+ ")";
				}
				if(desc.equals("无设备连接信息"))
					desc=getLocate(ip, combotype);
				textjg.setText(desc);
			}

		});
		Label lbjg = new Label(container, SWT.NONE);
		lbjg.setText("定位结果:");

		Group group = new Group(container, SWT.NONE);
		GridData gd_group = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1,
				1);
		gd_group.heightHint = 90;
		gd_group.widthHint = 288;
		group.setLayoutData(gd_group);

		textjg = new Text(group, SWT.WRAP | SWT.MULTI);
		textjg.setBounds(10, 10, 274, 81);

		return container;
	}
	public static String getLocate(String ip,Combo combotype){
		for (String subtopo1 : DBManage.Topos.keySet()) {
			if(subtopo1.equals("host"))
				continue;
			TopoChart tchart = DBManage.Topos.get(subtopo1);
			for (String kk : tchart.getNodes().keySet()) {
				entity ent = tchart.getNodes().get(kk);
				if (ent instanceof svNode) {
					String ip1 = "";
					if (combotype.getText().equals("设备IP")) {
						ip1 = ((svNode) ent).getLocalip();
					} else {
						ip1 = ((svNode) ent).getMac();
					}
					if (ip1.equals(ip)) {
						return ip+"存在与子图"+subtopo1+"中";
					}
				}}}
		return "";
	}
	
	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "拓扑图中显示", true);
		createButton(parent, IDialogConstants.CANCEL_ID, "取消", false);
	}

	protected void buttonPressed(int buttonId) {
		try {
			String ip = this.text.getText();
			if (buttonId == IDialogConstants.OK_ID) {
				String subto = "";
				for (String subtopo1 : DBManage.Topos.keySet()) {
					TopoChart tchart = DBManage.Topos.get(subtopo1);
					for (String kk : tchart.getNodes().keySet()) {
						entity ent = tchart.getNodes().get(kk);
						if (ent instanceof svNode) {
							String ip1 = "";
							if (combotype.getText().equals("设备IP")) {
								ip1 = ((svNode) ent).getLocalip();
							} else {
								ip1 = ((svNode) ent).getMac();
							}
							if (ip1.equals(ip)) {
								if (this.topochart != null) {
									if (subtopo1.equals(subtopo)) {
										ip = ((svNode) ent).getLocalip();
										((TopoMap) NNMInstanceData
												.getNNMData("topoChart"))
												.setLocatenode(ip,true);
										//topochart.setLocatenode(ip);

									} else {
										ip = ((svNode) ent).getLocalip();
										NNMainEditorInput editiput = (NNMainEditorInput) NNMInstanceData
												.getNNMData("editiput");

										IWorkbenchPage page = Activator
												.getDefault().getWorkbench()
												.getActiveWorkbenchWindow()
												.getActivePage();
										IEditorPart editor = page
												.findEditor(editiput);
										page.closeEditor(editor, false);
										editiput.setSubtopo(subtopo1);
										try {
											IEditorPart ep = page.openEditor(
													editiput, TopoManage.ID);
										} catch (PartInitException e) {
											e.printStackTrace();
										}

										editiput.getTreeviewer()
												.getTree()
												.setSelection(
														getsuitem(
																editiput.getTreeviewer()
																		.getTree()
																		.getItems()[0]
																		.getItems()[0],
																subtopo1));
										final String ips=ip;
										Display.getCurrent().timerExec(1000, new Runnable() {
											@Override
											public void run() {
												((TopoMap) NNMInstanceData
														.getNNMData("topoChart"))
														.setLocatenode(ips,true);
											}
										});
										// TreeItem[] items = editiput
										// .getTreeviewer().getTree()
										// .getItems();
										// SubChartModle submod = setselecttion(
										// items[0].getItems()[0]
										// .getData(),
										// subtopo1);
										// if (submod != null) {
										// try {
										// editiput.getTreeviewer()
										// .setSelection(
										// new StructuredSelection(
										// submod),
										// true);
										// if (editiput.getTreeviewer()
										// .getTree()
										// .getSelection().length > 0)
										// expandtreeitem((TreeItem) editiput
										// .getTreeviewer()
										// .getTree()
										// .getSelection()[0]
										// .getParentItem());
										// } catch (Exception ex) {
										// }
										// }

									}
								} else {
									ip = ((svNode) ent).getLocalip();
									NNMainEditorInput editiput = (NNMainEditorInput) NNMInstanceData
											.getNNMData("editiput");
									TreeItem[] items = editiput.getTreeviewer()
											.getTree().getItems();
									TreeItem toponnm = items[0].getItems()[0];
									// toponnm.setExpanded(true);
									// editiput.getTreeviewer()
									// .getTree().select(toponnm);
									this.subtopo = editiput.getSubtopo();
									if (this.subtopo.equals(subtopo1)) {
										editiput.getTreeviewer().getTree()
												.setSelection(toponnm);
										// toponnm.setChecked(true);
										((TopoMap) NNMInstanceData
												.getNNMData("topoChart"))
												.setLocatenode(ip,true);
										
										IWorkbenchPage page = Activator
												.getDefault().getWorkbench()
												.getActiveWorkbenchWindow()
												.getActivePage();
										editiput.setTreeselection(editiput
												.getTreeviewer().getTree()
												.getSelection()[0].getData());
										IEditorPart ep = page.openEditor(
												editiput, TopoManage.ID);
									}else{
										editiput.getTreeviewer().getTree()
										.setSelection(toponnm);
										IWorkbenchPage page = Activator
												.getDefault().getWorkbench()
												.getActiveWorkbenchWindow()
												.getActivePage();
										IEditorPart editor = page
												.findEditor(editiput);
										page.closeEditor(editor, false);
										editiput.setSubtopo(subtopo1);
										try {
											IEditorPart ep = page.openEditor(
													editiput, TopoManage.ID);
										} catch (PartInitException e) {
											e.printStackTrace();
										}
									}
									final String ips=ip;
									Display.getCurrent().timerExec(1000, new Runnable() {
										@Override
										public void run() {
											((TopoMap) NNMInstanceData
													.getNNMData("topoChart"))
													.setLocatenode(ips,true);
										}
									});
								}
								subto = subtopo1;
								this.close();
								break;
							}
						}
					}
				}
				if (subto.isEmpty()) {
					MsgBox.ShowMessage("无此设备!");
				} else if (!subto.equals(this.subtopo)) {
					// if (subto.equals("host")) {
					// MsgBox.ShowMessage("此设备在拓扑主图!");
					// } else {
					// MsgBox.ShowMessage("此设备拓扑子图 " + subto + " 上!");
					// }
				}

			}
			this.close();
		} catch (Exception e) {
		}
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

	public TreeItem getsuitem(TreeItem treeitem, String lab) {
		TreeItem rr = null;
		for (TreeItem item : treeitem.getItems()) {
			if (item.getText().equals(lab)) {
				rr = item;
			}
		}
		return rr;
	}

	public void expandtreeitem(TreeItem treeitem) {

		if (treeitem != null && !(treeitem.getData() instanceof RootNNM)) {
			treeitem.setExpanded(true);
			expandtreeitem(treeitem.getParentItem());
		}
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(408, 310);
	}
}
