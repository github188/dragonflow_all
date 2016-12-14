package com.siteview.NNM.Views;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.navigator.CommonNavigator;

import com.siteview.NNM.Activator;
import com.siteview.NNM.NNMInstanceData;
import com.siteview.NNM.Editors.IPMACChangeEditor;
import com.siteview.NNM.Editors.IPMACChangeInput;
import com.siteview.NNM.Editors.MIBBrowseEditor;
import com.siteview.NNM.Editors.MIBBrowseInput;
import com.siteview.NNM.Editors.NNMainEditorInput;
import com.siteview.NNM.Editors.SubnetDataEditor;
import com.siteview.NNM.Editors.SubnetDataEditorInput;
import com.siteview.NNM.Editors.SubnetEditor;
import com.siteview.NNM.Editors.SubnetEditorInput;
import com.siteview.NNM.Editors.SvgEditor;
import com.siteview.NNM.Editors.SvgEditorInput;
import com.siteview.NNM.Editors.SvgManagermentEditor;
import com.siteview.NNM.Editors.SvgManagermentInput;
import com.siteview.NNM.Editors.TopoManage;
import com.siteview.NNM.adapts.NNMInstance;
import com.siteview.NNM.dialogs.table.ImageKeys;
import com.siteview.NNM.modles.IpMacChangeModle;
import com.siteview.NNM.modles.IpManagermentModle;
import com.siteview.NNM.modles.RootNNM;
import com.siteview.NNM.modles.SubChartModle;
import com.siteview.NNM.modles.SubnetDataModle;
import com.siteview.NNM.modles.SubnetModle;
import com.siteview.NNM.modles.SvgManagementModle;
import com.siteview.NNM.modles.SvgModle;
import com.siteview.NNM.modles.SvgTypeModle;
import com.siteview.NNM.modles.TopoModle;
import com.siteview.NNM.nls.NNMMessage;
import com.siteview.NNM.resource.ImageResource;
import com.siteview.mib.model.FileMibModel;
import com.siteview.mib.model.MibModel;

import Siteview.Windows.Forms.ConnectionBroker;
import siteview.windows.forms.Branding;

public class NNMTreeView extends CommonNavigator {
	public static final String ID = "com.siteview.NNM.Views.NNMTreeView";
	protected static final String TopoMap = null;
	public Tree tree;
	public Object item = null;
	public Object item1 = null;
	public RootNNM nnmroot = null;
	int width = 0;
	int height = 0;
	public NNMainEditorInput sv = new NNMainEditorInput();
	public SvgManagermentInput svgm = new SvgManagermentInput();
	public SvgEditorInput svg = new SvgEditorInput();
	public SubnetEditorInput subnet = new SubnetEditorInput();
	public SubnetDataEditorInput subnetdata = new SubnetDataEditorInput();
	public IPMACChangeInput ipmac = new IPMACChangeInput();
	public MIBBrowseInput mibb = new MIBBrowseInput();

	@Override
	protected ActionGroup createCommonActionGroup() {
		return new NNMActionGroup();
	}

	public static NNMTreeView getCNFNNMTreeView() {
		NNMTreeView cnf = null;
		try {
			cnf = (NNMTreeView) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ID);
		} catch (Exception e) {
		}
		if (cnf == null) {
			cnf = (NNMTreeView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ID);
		}

		return cnf;
	}

	/**
	 * NNM 程序入口
	 */
	protected Object getInitialInput() {
		// 加载图
		ImageResource.loadimage();
		ImageKeys.loadimage();
		return NNMInstance.getinstance();
	}

	/***
	 * Adds the listeners to the Common Viewer
	 ***/
	protected void initListeners(final TreeViewer viewer) {
		if (nnmroot == null) {
			nnmroot = NNMInstance.getinstance().getRoot();
			item = nnmroot;
			NNMInstanceData.setNNMData("nnmroot", item);
			// EccStaticAttribute.setEccData("SiteViewEccViewItemOb", item);
		}
		viewer.setAutoExpandLevel(2);
		tree = viewer.getTree();
		sv.setTreeselection(item1);
		sv.setSubtopo("host");
		sv.setTreeviewer(viewer);
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart ep;
		try {
			ep = page.openEditor(sv, TopoManage.ID);
			((TopoManage) ep).subtopo = "host";
		} catch (PartInitException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// 监视view的width
		final Composite nn = viewer.getControl().getParent();
		nn.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				int ww = nn.getBounds().width + 36;
				if (ww != width) {
					width = ww;
					NNMInstanceData.setNNMData("topoleft", ww);
				}
				// height = nn.getClientArea().height;

			}
		});
		NNMInstanceData.setNNMData("editiput", sv);
		tree.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				if (tree.getSelection().length <= 0)
					return;
				Object obj = tree.getSelection()[0].getData();
				String lb1 = tree.getSelection()[0].getText();
				IWorkbenchPage page = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
				item1 = obj;
				try {
					// if (e.button == 1) {//|| obj instanceof RootNNM

					String subtopo = sv.getSubtopo();
					if (obj instanceof TopoModle) {
						IEditorPart editor = page.findEditor(sv);
						if (subtopo == null || !subtopo.equals("host")) {
							page.closeEditor(editor, false);
							// if (editor == null) {
							sv.setTreeselection(item1);
							sv.setSubtopo("host");
							sv.setTreeviewer(viewer);
							IEditorPart ep = page.openEditor(sv, TopoManage.ID);
							((TopoManage) ep).subtopo = "host";
						}
						if (editor == null && subtopo != null) {
							sv.setTreeselection(item1);
							sv.setSubtopo("host");
							sv.setTreeviewer(viewer);
							IEditorPart ep = page.openEditor(sv, TopoManage.ID);
							((TopoManage) ep).subtopo = "host";
						} else {
							page.activate(editor);
						}

						// } else {
						// sv.setTreeselection(item1);
						// ((TopoManage) editor).subtopo = "host";
						// ((TopoManage) editor).setFocus();
						// page.activate(editor);
						// }
					} else if (obj instanceof SubChartModle) {
						IEditorPart editor = page.findEditor(sv);
						// if (obj.equals(sv.getTreeselection())
						// && editor != null) {
						// page.activate(editor);
						// return;
						// }
						if (subtopo == null || !subtopo.equals(lb1)) {
							page.closeEditor(editor, false);
							// if (editor == null) {
							sv.setSubtopo(lb1);
							sv.setTreeviewer(viewer);
							sv.setTreeselection(item1);
							IEditorPart ep = page.openEditor(sv, TopoManage.ID);
						}
						if (editor == null && subtopo != null) {
							sv.setSubtopo(lb1);
							sv.setTreeviewer(viewer);
							sv.setTreeselection(item1);
							IEditorPart ep = page.openEditor(sv, TopoManage.ID);
						}
						// } else {
						// sv.setTreeselection(item1);
						// ((TopoManage) editor).subtopo = lb1;
						// ((TopoManage) editor).setFocus();
						// page.activate(editor);
						// }
					} else if (obj instanceof SvgManagementModle || obj instanceof SvgTypeModle) {
						IEditorPart editor = page.findEditor(svgm);
						if (obj instanceof SvgManagementModle) {
							svgm.setName(NNMMessage.getvalue("DeviceManagement", Branding.sysConfigRun.getConvertLocale()));
							svgm.setSvgtype(((SvgManagementModle) obj).getSvgtype());
						} else {
							svgm.setName(NNMMessage.getvalue("DeviceType", Branding.sysConfigRun.getConvertLocale()));
							svgm.setSvgtype((SvgTypeModle) obj);
						}
						if (editor == null) {
							page.openEditor(svgm, SvgManagermentEditor.ID);
						} else {
							((SvgManagermentEditor) editor).setFocus();
							page.activate(editor);
						}
					} else if (obj instanceof SvgModle) {
						IEditorPart editor = page.findEditor(svg);
						svg.setName(((SvgModle) obj).getName());
						if (editor == null) {
							page.openEditor(svg, SvgEditor.ID);
						} else {
							editor.setFocus();
							page.activate(editor);
						}
					} else if (obj instanceof SubnetModle) {
						IEditorPart editor = page.findEditor(subnet);
						subnet.setName(((SubnetModle) obj).getName());
						subnet.setSubd(((SubnetModle) obj).getList());
						if (editor == null) {
							page.openEditor(subnet, SubnetEditor.ID);
						} else {
							editor.setFocus();
							page.activate(editor);
						}
					} else if (obj instanceof IpManagermentModle) {
						IEditorPart editor = page.findEditor(subnet);
						subnet.setName(((IpManagermentModle) obj).getName());
						subnet.setSubd(((IpManagermentModle) obj).getSub().getList());
						if (editor == null) {
							page.openEditor(subnet, SubnetEditor.ID);
						} else {
							editor.setFocus();
							page.activate(editor);
						}
					} else if (obj instanceof SubnetDataModle) {
						IEditorPart editor = page.findEditor(subnetdata);
						subnetdata.setName(((SubnetDataModle) obj).getSubnetname());
						subnetdata.setSvnodes(((SubnetDataModle) obj).getSvnodes());
						if (editor == null) {
							page.openEditor(subnetdata, SubnetDataEditor.ID);
						} else {
							editor.setFocus();
							((SubnetDataEditor) editor).createTableItem();
							page.activate(editor);
						}
					} else if (obj instanceof IpMacChangeModle) {
						IEditorPart editor = page.findEditor(ipmac);
						if (editor == null) {
							page.openEditor(ipmac, IPMACChangeEditor.ID);
						} else {
							editor.setFocus();
							// ((IPMACChangeEditor)editor).createTableItem();
							page.activate(editor);
						}
					} else if (obj instanceof FileMibModel) {
						TreeViewer treeview = NNMTreeView.getCNFNNMTreeView().getCommonViewer();
						if (!treeview.getExpandedState(obj)) {
							treeview.expandToLevel(obj, 1);
						}
						IEditorPart editor = page.findEditor(mibb);
						mibb.setFilemib((FileMibModel) obj);
						if (editor == null) {
							page.openEditor(mibb, MIBBrowseEditor.ID);
						} else {
							editor.setFocus();
							((MIBBrowseEditor) editor).refresh();
							page.activate(editor);
						}
					} else if (obj instanceof MibModel) {
						TreeViewer treeview = NNMTreeView.getCNFNNMTreeView().getCommonViewer();
						if (!treeview.getExpandedState(obj)) {
							treeview.expandToLevel(obj, 1);
						}
						IEditorPart editor = page.findEditor(mibb);
						mibb.setFilemib(null);
						mibb.setMib((MibModel) obj);
						if (editor == null) {
							page.openEditor(mibb, MIBBrowseEditor.ID);
						} else {
							editor.setFocus();
							((MIBBrowseEditor) editor).refresh();
							page.activate(editor);
						}
					}
					// }
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});
		tree.addMouseListener(new MouseListener() {
			public void mouseUp(MouseEvent e) {
			}

			public void mouseDown(MouseEvent e) {
			}

			public void mouseDoubleClick(MouseEvent e) {
				if (tree.getSelection().length > 0) {
					Object obj = tree.getSelection()[0].getData();
					TreeViewer treeview = NNMTreeView.getCNFNNMTreeView().getCommonViewer();
					if (!treeview.getExpandedState(obj)) {
						treeview.expandToLevel(obj, 1);
					} else {
						// treeview.collapseToLevel(obj,1);
					}
				}
			}
		});
	}
}
