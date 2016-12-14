package com.siteview.NNM.dialogs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.siteview.nnm.data.model.TopoChart;
import com.siteview.nnm.data.model.svNode;

@SuppressWarnings("deprecation")
public class DeviceTraceRouteDialogs extends Dialog {
	String ip;
	String community;
	int port;
	int nver;
	String typetable;
	Text ip_text;
	Text result_text;
	Group group_result;
	String line = null;
	
	final ServerPushSession pushSession = new ServerPushSession();

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DeviceTraceRouteDialogs(Shell parentShell, String typetable, String tag, String nodeid) {
		super(parentShell);
		TopoChart tochart = com.siteview.nnm.data.DBManage.Topos.get(tag);
		svNode entityobj = (svNode) tochart.getNodes().get(nodeid);
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

	protected void configureShell(Shell newShell) {
		newShell.setText("TraceRoute");
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
		// ImageKeys.loadimage();
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout());

		Group group = new Group(container, SWT.NONE);
		group.setText("TraceRoute测试");
		GridData gd_group = new GridData();
		gd_group.widthHint = 375;
		group.setLayoutData(gd_group);
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.horizontalSpacing = 10;
		group.setLayout(gridLayout);

		GridData data_text = new GridData();
		data_text.widthHint = 120;
		
		GridData data_label = new GridData();
		data_label.widthHint = 60;

		GridData data_but = new GridData();
		data_but.widthHint = 80;

		Label label = new Label(group, SWT.NONE | SWT.RIGHT);
		label.setText("目标地址:");
		label.setLayoutData(data_label);

		ip_text = new Text(group, SWT.BORDER);
		ip_text.setText(ip);
		ip_text.setLayoutData(data_text);

		final Button button = new Button(group, SWT.NONE);
		button.setText("TraceRoute");
		button.setLayoutData(data_but);
		button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				String serverAddress = ip_text.getText().toString().trim();
				String callbackid = UUID.randomUUID().toString();
//				UICallBack.activate(callbackid);
				pushSession.start();
				textResultThread thread = new textResultThread();
				thread.runSetValue(serverAddress, button, result_text, Display.getCurrent(), callbackid);
				thread.start();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		group_result = new Group(container, SWT.NONE);
		group_result.setLayout(new GridLayout());
		group_result.setLayoutData(new GridData(GridData.FILL_BOTH));

//		Composite composite = new Composite(group_result, SWT.NONE);
//		composite.setLayout(new GridLayout());
		result_text = new Text(group_result, SWT.NONE | SWT.WRAP | SWT.V_SCROLL);//|SWT.READ_ONLY
		result_text.setLayoutData(new GridData(GridData.FILL_BOTH));
		return container;
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
		return new Point(400, 400);
	}

	private class textResultThread extends Thread {
		private Text result_text1 = null;
		private Display display = null;
		private String uicallbackid;
		private String serverAddress;
		final StringBuffer sa = new StringBuffer();
		private Button button1;

		public void runSetValue(String serverAddress, Button button,Text text1, final Display display1, String uicallbackid) {
			this.serverAddress = serverAddress;
			result_text1 = text1;
			display = display1;
			this.button1=button;
			this.uicallbackid = uicallbackid;
		}

		@Override
		public void run() {
			super.run();
//			UICallBack.runNonUIThreadWithFakeContext(display, new Runnable() {
			RWT.getUISession().exec(new Runnable() {
				@Override
				public void run() {
					display.asyncExec(new Runnable() {
						@Override
						public void run() {
							button1.setEnabled(false);
						}
					});
					String command = "Tracert  " + serverAddress;
					Process process = null;
					BufferedReader br = null;
					try {
						process = Runtime.getRuntime().exec(command);
						br = new BufferedReader(new InputStreamReader(process.getInputStream()));
						while ((line = br.readLine()) != null) {
							sa.append(line + "\n");
							System.out.println(line);
							display.asyncExec(new Runnable() {
								public void run() {
									result_text1.setText(sa.toString());
								}
							});
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					} finally {
						try {
							br.close();
							process.destroy();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					display.asyncExec(new Runnable() {
						@Override
						public void run() {
//							UICallBack.deactivate(uicallbackid);
							pushSession.stop();
							button1.setEnabled(true);
						}
					});
				
				}

			});

		}
	}

}
