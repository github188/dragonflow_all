package com.siteview.NNM.dialogs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Spinner;

import com.siteview.NNM.uijobs.IoUtilss;
import com.siteview.nnm.data.ConfigDB;
import com.siteview.topo.TopoMap;

public class Toposet extends Dialog {

	Spinner spokflow;
	Label lbmidflow;
	Spinner sperrorflow;

	Spinner spokpkts;
	Label lbmidpkts;
	Spinner sperrorpkts;

	Spinner spokbroad;
	Label lbmidbroad;
	Spinner sperrorbroad;

	Spinner spokPercent;
	Label lbmidPercent;// Percent
	Spinner sperrorPercent;
	TopoMap topochart;
	String linkid = null;
	boolean isedit = false;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public Toposet(Shell parentShell, TopoMap topochart) {
		super(parentShell);
		this.topochart = topochart;
	}

	public Toposet(Shell parentShell, TopoMap topochart, String linkid) {
		super(parentShell);
		this.topochart = topochart;
		this.linkid = linkid;
	}

	protected void configureShell(Shell newShell) {
		// newShell.setSize(450, 320);
		// newShell.setLocation(400, 175);
		newShell.setText("拓扑图设置");
		super.configureShell(newShell);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(final Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 3;

		int warnflow = 50;
		int errorflow = 2000;
		int warnpkts = 50;
		int errorpkts = 1000;
		int warnbroad = 20;
		int errorbroad = 200;
		int warnpercent = 20;
		int errorpercent = 80;
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
			IoUtilss.saveTopoSet(warnflow + "", errorflow + "", warnpkts + "",
					errorpkts + "", warnbroad + "", errorbroad + "",
					warnpercent + "", errorpercent + "");
		}
		if (this.linkid != null) {
			Connection conn = ConfigDB.getConn();
			String sql = "select warnflow,errorflow,warnpkts,errorpkts,warnbroad,errorbroad,warnpercent,errorperent from linkset where linkid='"
					+ this.linkid + "'";
			ResultSet rs = ConfigDB.query(sql, conn);
			try {
				while (rs.next()) {
					warnflow = rs.getInt("warnflow");
					errorflow = rs.getInt("errorflow");
					warnpkts = rs.getInt("warnpkts");
					errorpkts = rs.getInt("errorpkts");
					warnbroad = rs.getInt("warnbroad");
					errorbroad = rs.getInt("errorbroad");
					warnpercent = rs.getInt("warnpercent");
					errorpercent = rs.getInt("errorperent");
					isedit = true;
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
		}

		TabFolder tabFolder = new TabFolder(container, SWT.NONE);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("线路流量阀值设置");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(composite);
		composite.setLayout(new GridLayout(3, false));
		Label lbflow = new Label(composite, SWT.NONE);
		lbflow.setFont(SWTResourceManager.getFont("宋体", 9, SWT.BOLD));
		lbflow.setText("总流量(Kbps)");
		Label lbokflow = new Label(composite, SWT.NONE);
		GridData gd_okflow = new GridData(GridData.FILL_HORIZONTAL);
		gd_okflow.widthHint = 80;
		lbokflow.setLayoutData(gd_okflow);
		lbokflow.setText("            ");
		lbokflow.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

		spokflow = new Spinner(composite, SWT.BORDER);
		spokflow.setMaximum(80000000);
		spokflow.setSelection(warnflow);
		spokflow.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		spokflow.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				lbmidflow.setText("> " + spokflow.getText() + " && < "
						+ sperrorflow.getText());
			}
		});
		Label lbflow1 = new Label(composite, SWT.NONE);
		lbflow1.setText("  ");
		Label lbwarnflow = new Label(composite, SWT.NONE);
		gd_okflow = new GridData(GridData.FILL_HORIZONTAL);
		gd_okflow.widthHint = 80;
		lbwarnflow.setLayoutData(gd_okflow);
		lbwarnflow.setText("            ");
		lbwarnflow.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
		lbmidflow = new Label(composite, SWT.NONE);
		lbmidflow.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label lbflow2 = new Label(composite, SWT.NONE);
		lbflow2.setText("");
		Label lberrorflow = new Label(composite, SWT.NONE);
		gd_okflow = new GridData(GridData.FILL_HORIZONTAL);
		gd_okflow.widthHint = 80;
		lberrorflow.setLayoutData(gd_okflow);
		lberrorflow.setText("            ");
		lberrorflow.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
		sperrorflow = new Spinner(composite, SWT.BORDER);
		sperrorflow.setMaximum(900000000);
		sperrorflow.setSelection(errorflow);
		sperrorflow.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		sperrorflow.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				lbmidflow.setText("> " + spokflow.getText() + " && < "
						+ sperrorflow.getText());
			}
		});
		lbmidflow.setText("> " + spokflow.getText() + " && < "
				+ sperrorflow.getText());

		Label lbflowPercent = new Label(composite, SWT.NONE);
		lbflowPercent.setFont(SWTResourceManager.getFont("宋体", 9, SWT.BOLD));
		lbflowPercent.setText("带宽占用比(%)");
		Label lbokflowPercent = new Label(composite, SWT.NONE);
		gd_okflow = new GridData(GridData.FILL_HORIZONTAL);
		gd_okflow.widthHint = 80;
		lbokflowPercent.setLayoutData(gd_okflow);
		lbokflowPercent.setText("            ");
		lbokflowPercent.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_GREEN));

		spokPercent = new Spinner(composite, SWT.BORDER);
		spokPercent.setMaximum(80000000);
		spokPercent.setSelection(warnpercent);
		spokPercent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		spokPercent.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				lbmidPercent.setText("> " + spokPercent.getText() + " && < "
						+ sperrorPercent.getText());
			}
		});
		Label lbflowPercent1 = new Label(composite, SWT.NONE);
		lbflowPercent1.setText("  ");
		Label lbwarnflowPercent = new Label(composite, SWT.NONE);
		gd_okflow = new GridData(GridData.FILL_HORIZONTAL);
		gd_okflow.widthHint = 80;
		lbwarnflowPercent.setLayoutData(gd_okflow);
		lbwarnflowPercent.setText("            ");
		lbwarnflowPercent.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_YELLOW));
		lbmidPercent = new Label(composite, SWT.NONE);
		lbmidPercent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label lbflowPercent2 = new Label(composite, SWT.NONE);
		lbflowPercent2.setText("");
		Label lberrorflowPercent = new Label(composite, SWT.NONE);
		gd_okflow = new GridData(GridData.FILL_HORIZONTAL);
		gd_okflow.widthHint = 80;
		lberrorflowPercent.setLayoutData(gd_okflow);
		lberrorflowPercent.setText("            ");
		lberrorflowPercent.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_RED));
		sperrorPercent = new Spinner(composite, SWT.BORDER);
		sperrorPercent.setMaximum(900000000);
		sperrorPercent.setSelection(errorpercent);
		sperrorPercent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		lbmidPercent.setText("> " + spokPercent.getText() + " && < "
				+ sperrorPercent.getText());
		sperrorPercent.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				lbmidPercent.setText("> " + spokPercent.getText() + " && < "
						+ sperrorPercent.getText());
			}
		});
		Label lbpkts = new Label(composite, SWT.NONE);
		lbpkts.setFont(SWTResourceManager.getFont("宋体", 9, SWT.BOLD));
		lbpkts.setText("帧流量(Pkts/s)");
		Label lbokpkts = new Label(composite, SWT.NONE);
		gd_okflow = new GridData(GridData.FILL_HORIZONTAL);
		gd_okflow.widthHint = 80;
		lbokpkts.setLayoutData(gd_okflow);
		lbokpkts.setText("            ");
		lbokpkts.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

		spokpkts = new Spinner(composite, SWT.BORDER);
		spokpkts.setMaximum(80000000);
		spokpkts.setSelection(warnpkts);
		spokpkts.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		spokpkts.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				lbmidpkts.setText("> " + spokpkts.getText() + " && < "
						+ sperrorpkts.getText());
			}
		});
		Label lbpkts1 = new Label(composite, SWT.NONE);
		lbpkts1.setText("  ");
		Label lbwarnpkts = new Label(composite, SWT.NONE);
		lbwarnpkts.setText("            ");
		gd_okflow = new GridData(GridData.FILL_HORIZONTAL);
		gd_okflow.widthHint = 80;
		lbwarnpkts.setLayoutData(gd_okflow);
		lbwarnpkts.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
		lbmidpkts = new Label(composite, SWT.NONE);
		lbmidpkts.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label lbpkts2 = new Label(composite, SWT.NONE);
		lbpkts2.setText("");
		Label lberrorpkts = new Label(composite, SWT.NONE);
		gd_okflow = new GridData(GridData.FILL_HORIZONTAL);
		gd_okflow.widthHint = 80;
		lberrorpkts.setLayoutData(gd_okflow);
		lberrorpkts.setText("            ");
		lberrorpkts.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
		sperrorpkts = new Spinner(composite, SWT.BORDER);
		sperrorpkts.setMaximum(900000000);
		sperrorpkts.setSelection(errorpkts);
		sperrorpkts.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		lbmidpkts.setText("> " + spokpkts.getText() + " && < "
				+ sperrorpkts.getText());
		sperrorpkts.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				lbmidpkts.setText("> " + spokpkts.getText() + " && < "
						+ sperrorpkts.getText());
			}
		});
		Label lbbroad = new Label(composite, SWT.NONE);
		lbbroad.setFont(SWTResourceManager.getFont("宋体", 9, SWT.BOLD));
		lbbroad.setText("广播量(个/s)");
		Label lbokbroad = new Label(composite, SWT.NONE);
		lbokbroad.setText("            ");
		gd_okflow = new GridData(GridData.FILL_HORIZONTAL);
		gd_okflow.widthHint = 80;
		lbokbroad.setLayoutData(gd_okflow);
		lbokbroad.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));

		spokbroad = new Spinner(composite, SWT.BORDER);
		spokbroad.setMaximum(80000000);
		spokbroad.setSelection(warnbroad);
		spokbroad.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		spokbroad.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				lbmidbroad.setText("> " + spokbroad.getText() + " && < "
						+ sperrorbroad.getText());
			}
		});
		Label lbbroad1 = new Label(composite, SWT.NONE);
		lbbroad1.setText("  ");
		Label lbwarnbroad = new Label(composite, SWT.NONE);
		gd_okflow = new GridData(GridData.FILL_HORIZONTAL);
		gd_okflow.widthHint = 80;
		lbwarnbroad.setLayoutData(gd_okflow);
		lbwarnbroad.setText("            ");
		lbwarnbroad
				.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
		lbmidbroad = new Label(composite, SWT.NONE);
		lbmidbroad.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label lbbroad2 = new Label(composite, SWT.NONE);
		lbbroad2.setText("");
		Label lberrorbroad = new Label(composite, SWT.NONE);
		gd_okflow = new GridData(GridData.FILL_HORIZONTAL);
		gd_okflow.widthHint = 80;
		lberrorbroad.setLayoutData(gd_okflow);
		lberrorbroad.setText("            ");
		lberrorbroad.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
		sperrorbroad = new Spinner(composite, SWT.BORDER);
		sperrorbroad.setMaximum(900000000);
		sperrorbroad.setSelection(errorbroad);
		sperrorbroad.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		lbmidbroad.setText("> " + spokbroad.getText() + " && < "
				+ sperrorbroad.getText());
		sperrorbroad.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				lbmidbroad.setText("> " + spokbroad.getText() + " && < "
						+ sperrorbroad.getText());
			}
		});
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
				if (this.linkid == null) {
					IoUtilss.saveTopoSet(spokflow.getText(),
							sperrorflow.getText(), spokpkts.getText(),
							sperrorpkts.getText(), spokbroad.getText(),
							sperrorbroad.getText(), spokPercent.getText(),
							sperrorPercent.getText());
					JsonObject jdata = new JsonObject()
							.add("error",
									Integer.parseInt(sperrorflow.getText()))
							.add("warn", Integer.parseInt(spokflow.getText()))
							.add("pktserror",
									Integer.parseInt(sperrorpkts.getText()))
							.add("pktswarn",
									Integer.parseInt(spokpkts.getText()))
							.add("broadcasterror",
									Integer.parseInt(sperrorbroad.getText()))
							.add("broadcastwarn",
									Integer.parseInt(spokbroad.getText()))
							.add("bwusagerror",
									Integer.parseInt(sperrorPercent.getText()))
							.add("bwusagewarn",
									Integer.parseInt(spokPercent.getText()));
					topochart.setToposetdata(jdata);
				} else {
					if (this.isedit) {
						try {
							Connection conn = ConfigDB.getConn();
							String sql = "update linkset set warnflow="
									+ spokflow.getText() + " ,errorflow="
									+ sperrorflow.getText() + ",warnpkts="
									+ spokpkts.getText() + ",errorpkts="
									+ sperrorpkts.getText() + ",warnbroad="
									+ spokbroad.getText() + ",errorbroad="
									+ sperrorbroad.getText() + ",warnpercent="
									+ spokPercent.getText() + ",errorperent="
									+ sperrorPercent.getText()
									+ " where linkid='" + this.linkid + "'";
							ConfigDB.excute(sql, conn);
							ConfigDB.close(conn);
						} catch (Exception ex) {
						}
					} else {
						try {
							Connection conn = ConfigDB.getConn();
					

							String sql = "insert into linkset (warnflow,errorflow,warnpkts,errorpkts,warnbroad,errorbroad,warnpercent,errorperent,linkid) values ( ?, ?, ?, ?, ?, ?, ?, ? ,?)";
							PreparedStatement preparedStmt = conn
									.prepareStatement(sql);
							preparedStmt.setInt(1, Integer.parseInt(spokflow.getText()));
							preparedStmt.setInt(2, Integer.parseInt(sperrorflow.getText()));
							preparedStmt.setInt(3, Integer.parseInt(spokpkts.getText()));
							preparedStmt.setInt(4, Integer.parseInt(sperrorpkts.getText()));
							preparedStmt.setInt(5, Integer.parseInt(spokbroad.getText()));
							preparedStmt.setInt(6, Integer.parseInt(sperrorbroad.getText()));
							preparedStmt.setInt(7, Integer.parseInt(spokPercent.getText()));
							preparedStmt.setInt(8, Integer.parseInt(sperrorPercent.getText()));
							preparedStmt.setString(9, this.linkid);
							 preparedStmt.execute();
							ConfigDB.close(conn);
						} catch (Exception ex) {
						}

					}
					JsonObject jdata = new JsonObject()
							.add("lid", this.linkid)
							.add("error",
									Integer.parseInt(sperrorflow.getText()))
							.add("warn", Integer.parseInt(spokflow.getText()))
							.add("pktserror",
									Integer.parseInt(sperrorpkts.getText()))
							.add("pktswarn",
									Integer.parseInt(spokpkts.getText()))
							.add("broadcasterror",
									Integer.parseInt(sperrorbroad.getText()))
							.add("broadcastwarn",
									Integer.parseInt(spokbroad.getText()))
							.add("bwusagerror",
									Integer.parseInt(sperrorPercent.getText()))
							.add("bwusagewarn",
									Integer.parseInt(spokPercent.getText()));
					topochart.setLinksetdata(jdata);
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
		return new Point(400, 550);
	}

}
