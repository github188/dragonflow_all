package com.siteview.NNM.dialogs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;

import siteview.windows.forms.MsgBox;

import com.siteview.ecc.authorization.ItossAuthorizeServiceImpl;
import com.siteview.ecc.authorization.PermissionFactory;
import com.siteview.ecc.constants.Operation;
import com.siteview.ecc.constants.Resource;
import com.siteview.nnm.data.ConfigDB;
import com.siteview.nnm.data.DBManage;
import com.siteview.nnm.data.FlowDataManager;
import com.siteview.nnm.data.model.EdgeFlow;
import com.siteview.nnm.data.model.svEdge;
import com.siteview.nnm.data.model.svNode;
import com.siteview.nnm.data.model.svgroup;
import com.siteview.topo.TopoMap;

public class LinkPropery extends Dialog {
	private Text textip1;
	private Text textport1;
	private Text textip2;
	private Text textport2;
	String lid, tag;
	Combo combo1;
	Combo combo2;

	Button btnRadio1;
	Button btnRadio2;
	svEdge ed;
	svNode n1 = null;
	svNode n2 = null;
	svgroup g1 = null;
	svgroup g2 = null;
	TopoMap topochart;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public LinkPropery(Shell parentShell, String lid, String tag,
			TopoMap topochart) {
		super(parentShell);
		this.lid = lid;
		this.tag = tag;
		this.topochart = topochart;
	}

	protected void configureShell(Shell newShell) {
		// newShell.setSize(450, 320);
		// newShell.setLocation(400, 175);
		newShell.setText("线路属性");
		super.configureShell(newShell);
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
		ed = com.siteview.nnm.data.DBManage.Topos.get(tag).getEdges().get(lid);
		if (com.siteview.nnm.data.DBManage.Topos.get(tag).getNodes()
				.get(ed.getLsource()) instanceof svNode) {
			n1 = (svNode) com.siteview.nnm.data.DBManage.Topos.get(tag)
					.getNodes().get(ed.getLsource());
		} else if (com.siteview.nnm.data.DBManage.Topos.get(tag).getNodes()
				.get(ed.getLsource()) instanceof svgroup) {
			g1 = (svgroup) com.siteview.nnm.data.DBManage.Topos.get(tag)
					.getNodes().get(ed.getLsource());
		}
		if (com.siteview.nnm.data.DBManage.Topos.get(tag).getNodes()
				.get(ed.getLtarget()) instanceof svNode) {
			n2 = (svNode) com.siteview.nnm.data.DBManage.Topos.get(tag)
					.getNodes().get(ed.getLtarget());
		} else if (com.siteview.nnm.data.DBManage.Topos.get(tag).getNodes()
				.get(ed.getLtarget()) instanceof svgroup) {
			g2 = (svgroup) com.siteview.nnm.data.DBManage.Topos.get(tag)
					.getNodes().get(ed.getLtarget());
		}
		Group group = new Group(container, SWT.NONE);
		GridData gd_group = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1,
				1);
		gd_group.heightHint = 238;
		gd_group.widthHint = 436;
		group.setLayoutData(gd_group);

		Label lblNewLabel = new Label(group, SWT.NONE);
		lblNewLabel.setBounds(10, 27, 53, 15);
		lblNewLabel.setText("\u8BBE\u59071\uFF1A");

		textip1 = new Text(group, SWT.BORDER);
		textip1.setEditable(false);
		textip1.setBounds(82, 24, 338, 21);
		if (n1 != null) {
			if (n1.getDevicename().isEmpty()) {
				textip1.setText(n1.getLocalip());
			} else {
				textip1.setText(n1.getLocalip() + "(" + n1.getDevicename()
						+ ")");
			}
		} else {
			if (g1 != null) {
				textip1.setText(g1.getName());
			}

		}

		Label lblNewLabel_1 = new Label(group, SWT.NONE);
		lblNewLabel_1.setBounds(10, 69, 53, 15);
		lblNewLabel_1.setText("\u7AEF\u53E31:");

		textport1 = new Text(group, SWT.BORDER);
		textport1.setEditable(false);
		textport1.setBounds(82, 69, 89, 21);
		textport1.setText(ed.getSinterface());

		combo1 = new Combo(group, SWT.NONE);
		combo1.setVisibleItemCount(15);
		combo1.setBounds(185, 69, 235, 23);
		int selecindex = 0;
		int tempindex = 0;
		if (n1 != null) {
			if (!n1.getSvid().isEmpty()) {

				Connection conn = ConfigDB.getConn();
				String sql = "select pindex,desc from ports where id='"
						+ n1.getSvid() + "'";
				ResultSet rs = ConfigDB.query(sql, conn);
				try {
					while (rs.next()) {
						String pindex = rs.getString("pindex");
						String desc = rs.getString("desc");
						combo1.add(pindex + "[" + desc + "]");
						if (pindex.equals(ed.getSinterface())) {
							selecindex = tempindex;
						} else {
							tempindex = tempindex + 1;
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
				ConfigDB.close(conn);
				if (combo1.getItemCount() == 0) {
					textport1.setText("0");
					combo1.add("0[虚拟端口]");
				} else {
					combo1.select(selecindex);

				}

			}
		} else {
			textport1.setText("0");
			combo1.add("0[虚拟端口]");
		}
		combo1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				String key = combo1.getText();

				String[] kkk = key.split("\\[");
				String vvv = kkk[0];
				textport1.setText(vvv);
			}
		});

		Label label = new Label(group, SWT.NONE);
		label.setText("\u8BBE\u59072\uFF1A");
		label.setBounds(10, 110, 53, 15);

		textip2 = new Text(group, SWT.BORDER);
		textip2.setEditable(false);
		textip2.setBounds(82, 107, 338, 21);
		if (n2 != null) {
			if (n2.getDevicename().isEmpty()) {
				textip2.setText(n2.getLocalip());
			} else {
				textip2.setText(n2.getLocalip() + "(" + n2.getDevicename()
						+ ")");
			}
		} else {
			if (g2 != null) {
				textip2.setText(g2.getName());
			}

		}

		Label label_1 = new Label(group, SWT.NONE);
		label_1.setText("\u7AEF\u53E32:");
		label_1.setBounds(10, 145, 53, 15);

		textport2 = new Text(group, SWT.BORDER);
		textport2.setEditable(false);
		textport2.setBounds(82, 145, 89, 21);
		textport2.setText(ed.getTinterface());
		combo2 = new Combo(group, SWT.NONE);
		combo2.setVisibleItemCount(15);
		combo2.setBounds(185, 145, 235, 23);
		selecindex = 0;
		tempindex = 0;
		if (n2 != null) {
			if (!n2.getSvid().isEmpty()) {

				Connection conn = ConfigDB.getConn();
				String sql = "select pindex,desc from ports where id='"
						+ n2.getSvid() + "'";
				ResultSet rs = ConfigDB.query(sql, conn);
				try {
					while (rs.next()) {
						String pindex = rs.getString("pindex");
						String desc = rs.getString("desc");
						combo2.add(pindex + "[" + desc + "]");
						if (pindex.equals(ed.getTinterface())) {
							selecindex = tempindex;
						} else {
							tempindex = tempindex + 1;
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
				ConfigDB.close(conn);
				if (combo2.getItemCount() == 0) {
					textport2.setText("0");
					combo2.add("0[虚拟端口]");
				} else {
					combo2.select(selecindex);
				}

			}
		} else {
			textport2.setText("0");
			combo2.add("0[虚拟端口]");
		}
		combo2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				String key = combo2.getText();

				String[] kkk = key.split("\\[");
				String vvv = kkk[0];
				textport2.setText(vvv);
			}
		});
		btnRadio1 = new Button(group, SWT.RADIO);
		btnRadio1.setSelection(true);
		btnRadio1.setBounds(99, 189, 120, 19);
		btnRadio1.setText("\u8BBE\u59071");

		btnRadio2 = new Button(group, SWT.RADIO);
		btnRadio2.setText("\u8BBE\u59072");
		btnRadio2.setBounds(225, 189, 120, 19);

		Label label_2 = new Label(group, SWT.NONE);
		label_2.setText("\u6D41\u91CF\u53D6\u503C:");
		label_2.setBounds(10, 193, 72, 15);

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "保存", true);
		createButton(parent, IDialogConstants.CANCEL_ID, "取消", false);
	}

	protected void buttonPressed(int buttonId) {
		try {
			if (buttonId == IDialogConstants.OK_ID) {
				if (ItossAuthorizeServiceImpl
						.getInstance()
						.isPermitted(
								PermissionFactory
										.createPermission(
												Resource.TOPU_OPERATION_IN_VIEW_EDIT_LINE
														.getType(),
												Operation.TOPUOPERATIONINVIEW_EDIT_LINE
														.getOperationString(),
												"*"))) {
					if (textport1.getText().trim().isEmpty()) {
						MsgBox.ShowError("提示", "端口1不能为空!");
						return;
					}
					if (btnRadio1.getSelection() && combo1.getItemCount() == 1
							&& textport1.getText().equals("0")) {
						MsgBox.ShowError("提示", "设备1不支持流量取值!");
						return;
					}
					if (textport2.getText().trim().isEmpty()) {
						MsgBox.ShowError("提示", "端口2不能为空!");
						return;
					}
					if (btnRadio2.getSelection() && combo2.getItemCount() == 1
							&& textport2.getText().equals("0")) {
						MsgBox.ShowError("提示", "设备2不支持流量取值!");
						return;
					}
					// 删除流量
					FlowDataManager.DeleteEdgeFlowTask(lid);
					// 添加流量
					EdgeFlow edgeFlow = new EdgeFlow();
					edgeFlow.leftid = n1.getSvid();
					edgeFlow.rightid = n2.getSvid();
					edgeFlow.leftport = textport1.getText().trim();
					edgeFlow.rightport = textport2.getText().trim();
					boolean iileft = false;
					if (btnRadio1.getSelection()) {
						iileft = true;
						edgeFlow.isleft = true;
						edgeFlow.portId = n1.getSvid() + ":"
								+ textport1.getText().trim();
					} else {
						edgeFlow.isleft = false;
						edgeFlow.portId = n2.getSvid() + ":"
								+ textport2.getText().trim();
					}

					FlowDataManager.AddEdgeFlowTask(lid, edgeFlow);

					DBManage.updateline(
							Integer.parseInt(n1.getSvid().substring(1)),
							Integer.parseInt(n2.getSvid().substring(1)),
							Integer.parseInt(lid.substring(1)),
							textport1.getText(), textport2.getText(), iileft);
					String sinterface = textport1.getText().trim();
					String tinterface = textport2.getText().trim();
					String desc1 = combo1.getText();
					String desc2 = combo2.getText();
					if (desc1.indexOf("[") >= 0)
						desc1 = desc1.substring(desc1.indexOf("[") + 1)
								.replaceAll("]", "");
					if (desc2.indexOf("[") >= 0)
						desc2 = desc2.substring(desc2.indexOf("[") + 1)
								.replaceAll("]", "");
					topochart.setPlinedata(new JsonObject().add("lid", lid)
							.add("port1", sinterface).add("port2", tinterface)
							.add("desc1", desc1).add("desc2", desc2));
					com.siteview.nnm.data.DBManage.Topos.get(tag).getEdges()
							.get(lid).setSinterface(sinterface);
					com.siteview.nnm.data.DBManage.Topos.get(tag).getEdges()
							.get(lid).setTinterface(tinterface);
				} else {
					MsgBox.ShowError("提示","无此权限!");
					return;
				}
			}
			this.close();
		} catch (Exception e) {
		}
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(471, 408);
	}
}
