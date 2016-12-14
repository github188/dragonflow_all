package com.siteview.NNM.dialogs;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ProgressMonitor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.snmp4j.mp.SnmpConstants;

import Siteview.Windows.Forms.MsgBox;

import com.siteview.NNM.aas.NNMPermissions;
import com.siteview.NNM.dialogs.table.DeviceArpTable;
import com.siteview.NNM.dialogs.table.DeviceCdpTable;
import com.siteview.NNM.dialogs.table.DeviceInfoTabFolder;
import com.siteview.NNM.dialogs.table.DeviceInterfaceTable;
import com.siteview.NNM.dialogs.table.DeviceIpTable;
import com.siteview.NNM.dialogs.table.DeviceMacTable;
import com.siteview.NNM.dialogs.table.DeviceRouterTable;
import com.siteview.NNM.dialogs.table.ExportAssestInfo;
import com.siteview.NNM.dialogs.table.ImageKeys;
import com.siteview.nnm.data.EntityManager;
import com.siteview.nnm.data.model.TopoChart;
import com.siteview.nnm.data.model.entity;
import com.siteview.nnm.data.model.svNode;

public class MibSTable extends Dialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Group group;
	private Composite composite2;
	private Table table;
	Text ip_text;
	Combo info_combo;
	String ip = "";// ip
	String community = "public";// 共同体
	int nver = 1;// 版本 version
	String typetable;
	int port = 161;
	Timer timer = null;
	Combo ref_com;
	Job job;
	String nodeid;
	svNode entityobj;
	Label status;
	Button select_button;
	Button export_button;
	String job_status = "good";

	/**
	 * Create the dialog. typetable
	 * 
	 * 
	 * 设备接口表 deviceiftable, 设备路由表 deviceroutetable, 1.3.6.1.2.1.4.21.1.1
	 * 路由目的IP地址 1.3.6.1.2.1.4.21.1.7 路由下一跳地址 1.3.6.1.2.1.4.21.1.8 路由类型
	 * 1.3.6.1.2.1.4.21.1.9 路由协议 1.3.6.1.2.1.4.21.1.11 路由掩码 设备转发表
	 * devicemactable, 设备arp表devicearptable, 设备cdp表 devicecdptable, 设备ip表
	 * deviceiptable,
	 * 
	 * @param parentShell
	 */
	public MibSTable(Shell parentShell, String typetable, String tag, String nodeid) {
		super(parentShell);
		setShellStyle(SWT.MAX | SWT.RESIZE);
		this.nodeid = nodeid;
		TopoChart tochart = com.siteview.nnm.data.DBManage.Topos.get(tag);
		entityobj = (svNode) tochart.getNodes().get(nodeid);
		if(entityobj==null){
			Iterator<String> ite=com.siteview.nnm.data.DBManage.Topos.keySet().iterator();
			while(ite.hasNext()&&entityobj==null){
				String key=ite.next();
				if(key.equals(tag)){
					continue;
				}
				tochart = com.siteview.nnm.data.DBManage.Topos.get(key);
				entityobj= (svNode) tochart.getNodes().get(nodeid);
			}
		}
		if(entityobj==null)
			return;
		ip = entityobj.getLocalip();
		community = entityobj.getProperys().get("Community");
		String dver = entityobj.getProperys().get("Version");
		port = (Integer.parseInt(entityobj.getProperys().get("port").equals("") ? "161" : entityobj.getProperys().get("port")));
		if (dver.equals("1")) {
			nver = 1;
		} else if (dver.equals("2")) {
			nver = 2;
		}
		this.typetable = typetable;
	}

	public MibSTable(Shell parentShell) {
		super(parentShell);
		// setShellStyle(SWT.MAX | SWT.RESIZE);
	}

	protected void configureShell(Shell newShell) {
		// newShell.setSize(450, 320);
		// newShell.setLocation(400, 175);
		newShell.setText("设备信息查询");
		super.configureShell(newShell);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@SuppressWarnings("serial")
	@Override
	protected Control createDialogArea(Composite parent) {
		ImageKeys.loadimage();
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout());

		group = new Group(container, SWT.NONE);
		GridData gd_group = new GridData();
		// GridData gd_group = new GridData(SWT.LEFT, SWT.CENTER, false, false,
		// 1, 1);
		gd_group.widthHint = 763;
		group.setLayoutData(gd_group);
		GridLayout gridLayout = new GridLayout(9, false);
		gridLayout.horizontalSpacing = 10;
		group.setLayout(gridLayout);

		GridData gd_label = new GridData();
		gd_label.widthHint = 60;

		GridData gd_text = new GridData();
		gd_text.widthHint = 120;

		GridData gd_button = new GridData();
		gd_button.widthHint = 80;

		Label label = new Label(group, SWT.NONE);
		label.setText("IP地址:");
		label.setLayoutData(gd_label);

		ip_text = new Text(group, SWT.BORDER);
		ip_text.setLayoutData(gd_text);
		ip_text.setText(ip);
		// ip_text.setEnabled(false);

		Button button = new Button(group, SWT.NONE);
		button.setText("更多...");
		button.setLayoutData(gd_button);
		button.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event event) {
				Map<String, entity> allEntity = EntityManager.allEntity;
				MoreIPDialog more = new MoreIPDialog(null, allEntity);
				more.open();
				if (more.getServerAddress() != null && !more.getServerAddress().equals(""))
					ip_text.setText(more.getServerAddress());
			}
		});

		Label ref_label = new Label(group, SWT.NONE);
		ref_label.setText("刷新间隔:");

		ref_com = new Combo(group, SWT.NONE);
		ref_com.add("不刷新", 0);
		ref_com.add("10", 1);
		ref_com.add("30", 2);
		ref_com.add("60", 3);
		ref_com.add("120", 4);
		ref_com.add("300", 5);
		ref_com.add("600", 6);

		ref_com.select(0);
		ref_com.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (job != null) {
					job.cancel();
				}
				getTimerTask();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		Label ref_util = new Label(group, SWT.NONE);
		ref_util.setText("秒");

		Label info_label = new Label(group, SWT.NONE);
		info_label.setText("信息表");

		LinkedHashMap<String, String> infos = new LinkedHashMap<String, String>();
		if (NNMPermissions.getInstance().getNNMDeviceInformationQueryDeviceInfo())
			infos.put("deviceinfo", "设备信息");
		if (NNMPermissions.getInstance().getNNMDeviceInformationQueryInterface())
			infos.put("deviceiftable", "接口表信息");
		if (NNMPermissions.getInstance().getNNMDeviceInformationQueryRoutingtable())
			infos.put("deviceroutetable", "路由表信息");
		if (NNMPermissions.getInstance().getNNMDeviceInformationQueryForwarding())
			infos.put("devicemactable", "转发表信息");
		if (NNMPermissions.getInstance().getNNMDeviceInformationQueryARP())
			infos.put("devicearptable", "ARP表信息");
		if (NNMPermissions.getInstance().getNNMDeviceInformationQueryCDP())
			infos.put("devicecdptable", "CDP表信息");
		if (NNMPermissions.getInstance().getNNMDeviceInformationQueryIp())
			infos.put("deviceiptable", "IP表信息");
		info_combo = new Combo(group, SWT.BORDER | SWT.READ_ONLY);

		Iterator<String> it = infos.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			info_combo.add(infos.get(key));
			info_combo.setData(infos.get(key), key);

		}
		if (typetable == null || "".equals(typetable)) {
			info_combo.select(0);
		} else {
			for (int i = 0; i < info_combo.getItemCount(); i++) {
				if (info_combo.getData(info_combo.getItem(i)).equals(typetable)) {
					info_combo.select(i);
					break;
				}
			}
		}
		info_combo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				for (Control control : composite2.getChildren()) {
					control.dispose();
				}
				if (table != null) {
					table.dispose();
				}
				table = new Table(composite2, SWT.NONE);
				table.setLayoutData(new GridData(GridData.FILL_BOTH));
				table.setHeaderVisible(true);
				table.setLinesVisible(true);
				status = new Label(composite2, SWT.NONE);
				createTableColumnsOfTypes();
				composite2.layout();

				String time = ref_com.getItem(ref_com.getSelectionIndex());
				if (time.equals("不刷新")) {
					SetMibData smd = new SetMibData();
					smd.passData(ip_text, info_combo, Display.getCurrent(), composite2);
					smd.start();
				} else {
					if (job != null) {
						job.cancel();
					}
					getTimerTask();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		select_button = new Button(group, SWT.NONE);
		select_button.setText("查询");
		select_button.setLayoutData(gd_button);
		select_button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// createTableTypesSetData();
				// composite2.layout();
				SetMibData smd = new SetMibData();
				smd.passData(ip_text, info_combo, Display.getCurrent(), composite2);
				smd.start();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		Composite startComposite = new Composite(group, SWT.NONE);
		startComposite.setLayout(new GridLayout(2, false));
		GridData gd_composite = new GridData();
		gd_composite.horizontalSpan = 8;
		startComposite.setLayoutData(gd_composite);

		Button ip4Button = new Button(startComposite, SWT.RADIO);
		ip4Button.setText("表格方式");
		ip4Button.setSelection(true);

		export_button = new Button(group, SWT.NONE);
		export_button.setText("导出");
		export_button.setLayoutData(gd_button);
		export_button.addListener(SWT.MouseDown, new Listener() {

			@Override
			public void handleEvent(Event event) {
				File excel_file = ExportAssestInfo.createExcel(table, info_combo.getText().toString());
				try {
					Browser browser = new Browser(Display.getCurrent().getActiveShell(), SWT.NONE);
					browser.setSize(0, 0);
					String url = getUrl(excel_file.getParent() + "/" + excel_file.getName(), excel_file.getName());
					boolean flag = browser.setUrl(url);
					if (flag) {
						return;
					}
				} catch (java.lang.Exception e) {
					e.printStackTrace();
				}

			}
		});

		composite2 = new Composite(container, SWT.NONE);
		composite2.setLayout(new GridLayout());
		composite2.setLayoutData(new GridData(GridData.FILL_BOTH));

		createTableColumnsOfTypes();
		// createTableTypesSetData();
		return container;
	}

	public void getTimerTask() {
		if (timer == null) {
			timer = new Timer();
		} else if (timer != null) {
			timer.cancel();
			timer = new Timer();
		}
		String time = ref_com.getItem(ref_com.getSelectionIndex());
		if (time.equals("不刷新")) {
			if (job != null) {
				job.cancel();
			}
			return;
		}
		final String temp = (String) info_combo.getData(info_combo.getText());
		final String ip = ip_text.getText().toString();
		final int ref = Integer.parseInt(time) * 1000;
		final Display display = Display.getCurrent();
		job_status = "good";
		try {
			job = new Job(time) {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					if (job.shouldRun()) {
						MibTask mib = new MibTask();
						mib.passData(ip, temp, display);
						mib.run();
						if ("good".equals(job_status)) {
							schedule(ref);
						}
					}
					return Status.OK_STATUS;
				}
			};
			job.schedule(ref);
		} catch (Exception e) {
		}

	}

	private void createTableColumnsOfTypes() {
		for (Control control : composite2.getChildren()) {
			control.dispose();
		}
		if (info_combo.getData(info_combo.getText()).equals("deviceinfo")) {
			String ip = ip_text.getText().toString();
			Map<String, entity> allEntity = EntityManager.allEntity;
			Iterator<String> it = allEntity.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				entity entitys = allEntity.get(key);
				if (entitys instanceof svNode) {
					if ((!"".equals(ip)) && ip.equals(((svNode) entitys).getLocalip())) {
						entityobj = (svNode) entitys;
						nodeid = key;
						break;
					}
				}
			}
			DeviceInfoTabFolder.createTabFolderUI(composite2, entityobj, nodeid);
		} else {
			if (table != null && !table.isDisposed()) {
				table.dispose();
			}
			table = new Table(composite2, SWT.NONE);
			table.setLayoutData(new GridData(GridData.FILL_BOTH));
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			status = new Label(composite2, SWT.NONE);
			if (info_combo.getData(info_combo.getText()).equals("deviceiftable")) {
				DeviceInterfaceTable.createTableColumns(table);
			} else if (info_combo.getData(info_combo.getText()).equals("deviceroutetable")) {
				DeviceRouterTable.createTableColumns(table);
			} else if (info_combo.getData(info_combo.getText()).equals("devicemactable")) {
				DeviceMacTable.createTableColumns(table);
			} else if (info_combo.getData(info_combo.getText()).equals("devicearptable")) {
				DeviceArpTable.createTableColumns(table);
			} else if (info_combo.getData(info_combo.getText()).equals("devicecdptable")) {
				DeviceCdpTable.createTableColumns(table);
			} else if (info_combo.getData(info_combo.getText()).equals("deviceiptable")) {
				DeviceIpTable.createTableColumns(table);
			}
		}
	}

	@Override
	public boolean close() {
		if (timer != null) {
			timer.cancel();
		}
		if (job != null && job.shouldRun()) {
			job.cancel();
		}
		return super.close();
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// // createButton(parent, IDialogConstants.OK_ID,
		// IDialogConstants.OK_LABEL,
		// true);
		// createButton(parent, IDialogConstants.CANCEL_ID,
		// IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(800, 600);
	}

	private class SetMibData extends Thread {
		private String ip;
		private String temp;
		Display display;

		public void passData(final Text ip_text, final Combo info_combo, Display display, final Composite composite2) {
			this.display = display;
			display.asyncExec(new Runnable() {

				@Override
				public void run() {
					ip = ip_text.getText().toString();
					temp = (String) info_combo.getData(info_combo.getText());
				}
			});
		}

		@Override
		public void run() {
			Map<String, entity> allEntity = EntityManager.allEntity;
			Iterator<String> it = allEntity.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				entity entitys = allEntity.get(key);
				if (entitys instanceof svNode) {
					if ((!"".equals(ip)) && ip.equals(((svNode) entitys).getLocalip())) {
						entityobj = (svNode) entitys;
						nodeid = key;
						break;
					}
				}
			}
			if ((ip != null && !"".equals(ip)) && (entityobj == null || "".equals(entityobj))) {
				display.asyncExec(new Runnable() {

					@Override
					public void run() {
						MsgBox.ShowWarning("查询结果", "该IP不存在与子网中");
					}
				});
				return;
			}
			if (temp.equals("deviceinfo")) {
				display.asyncExec(new Runnable() {

					@Override
					public void run() {
						export_button.setEnabled(false);
						composite2.layout();
						group.layout();
					}
				});
				DeviceInfoTabFolder.createTabFolderUI(display, composite2, entityobj, nodeid);
			} else {
				setEnabled(false, display);
				if (temp.equals("deviceiftable")) {
					DeviceInterfaceTable.setData(display, ip, port, community, nver, table, true);
				} else if (temp.equals("deviceroutetable")) {
					DeviceRouterTable.setData(display, ip, port, community, nver, table, true);
				} else if (temp.equals("devicemactable")) {
					DeviceMacTable.setData(display, ip, port, community, nver, table, true);
				} else if (temp.equals("devicearptable")) {
					DeviceArpTable.setData(display, ip, port, community, nver, table, true);
				} else if (temp.equals("devicecdptable")) {
					DeviceCdpTable.setData(display, ip, port, community, nver, table, true);
				} else if (temp.equals("deviceiptable")) {
					DeviceIpTable.setData(display, ip, port, community, nver, table, true);
				}
				setEnabled(true, display);
			}

		}

	}

	private class MibTask implements Runnable {// extends TimerTask
		private String ip;
		private String temp;
		Display disaly;

		public void passData(String ip, String temp, Display display) {
			this.disaly = display;
			this.ip = ip;
			this.temp = temp;
		}

		@Override
		public void run() {
			Map<String, entity> allEntity = EntityManager.allEntity;
			Iterator<String> it = allEntity.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				entity entitys = allEntity.get(key);
				if (entitys instanceof svNode) {
					if ((!"".equals(ip)) && ip.equals(((svNode) entitys).getLocalip())) {
						entityobj = (svNode) entitys;
						nodeid = key;
						break;
					}
				}
			}
			if ((ip != null && !"".equals(ip)) && (entityobj == null || "".equals(entityobj))) {
				disaly.asyncExec(new Runnable() {

					@Override
					public void run() {
						MsgBox.ShowWarning("查询结果", "该IP不存在与子网中");
						job_status = "error";
					}
				});
				return;
			}
			if (temp.equals("deviceinfo")) {
				DeviceInfoTabFolder.createTabFolderUI(disaly, composite2, entityobj, nodeid);
			} else {
				setEnabled(false, disaly);
				if (temp.equals("deviceiftable")) {
					DeviceInterfaceTable.setData(disaly, ip, port, community, nver, table, true);
				} else if (temp.equals("deviceroutetable")) {
					DeviceRouterTable.setData(disaly, ip, port, community, nver, table, true);
				} else if (temp.equals("devicemactable")) {
					DeviceMacTable.setData(disaly, ip, port, community, nver, table, true);
				} else if (temp.equals("devicearptable")) {
					DeviceArpTable.setData(disaly, ip, port, community, nver, table, true);
				} else if (temp.equals("devicecdptable")) {
					DeviceCdpTable.setData(disaly, ip, port, community, nver, table, true);
				} else if (temp.equals("deviceiptable")) {
					DeviceIpTable.setData(disaly, ip, port, community, nver, table, true);
				}
				setEnabled(true, disaly);
			}

		}

	}

	private String getUrl(String token, String filename) {
		StringBuffer url = new StringBuffer();
		url.append(RWT.getServiceManager().getServiceHandlerUrl("DownloadSigarHandler"));
		url.append("&").append("file").append("=").append(token).append("&").append("filename").append("=").append(filename);
		return RWT.getResponse().encodeURL(url.toString());
	}

	public void setEnabled(final boolean flag, Display display) {
		display.asyncExec(new Runnable() {

			@Override
			public void run() {
				if (!flag) {
					status.setText("查询中...");
				} else {
					status.setText("完毕.");
				}
				select_button.setEnabled(flag);
				info_combo.setEnabled(flag);
				export_button.setEnabled(flag);
				ip_text.setEnabled(flag);
				composite2.layout();
				group.layout();
			}
		});

	}

}
