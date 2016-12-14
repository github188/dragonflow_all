package com.siteview.NNM.dialogs.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.siteview.utils.snmp.SnmpWalk;

public class DeviceRouterTable {
	public static List<String> oids = null;

	static {
		if (oids == null) {
			oids = new ArrayList<String>();
		}
		oids.add("1.3.6.1.2.1.4.21.1.1");
		oids.add("1.3.6.1.2.1.4.21.1.7");
		oids.add("1.3.6.1.2.1.4.21.1.8");
		oids.add("1.3.6.1.2.1.4.21.1.9");
		oids.add("1.3.6.1.2.1.4.21.1.11");

	}

	@SuppressWarnings("serial")
	public static void createTableColumns(final Table table) {
		final TableColumn TableColumn1 = new TableColumn(table, SWT.NONE);
		TableColumn1.setText("路由目的IP地址");
		TableColumn1.setWidth(150);
		// TableColumn1.setImage(ImageKeys.images.get(ImageKeys.UNCHECKED));
		// TableColumn1.addSelectionListener(new SelectionListener() {
		//
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// if
		// (TableColumn1.getImage().equals(ImageKeys.images.get(ImageKeys.UNCHECKED)))
		// {
		// TableColumn1.setImage(ImageKeys.images.get(ImageKeys.CHECKED));
		// for (TableItem item : table.getItems()) {
		// item.setChecked(true);
		// }
		// } else if
		// (TableColumn1.getImage().equals(ImageKeys.images.get(ImageKeys.CHECKED)))
		// {
		// TableColumn1.setImage(ImageKeys.images.get(ImageKeys.UNCHECKED));
		// for (TableItem item : table.getItems()) {
		// item.setChecked(false);
		// }
		// }
		// }
		//
		// @Override
		// public void widgetDefaultSelected(SelectionEvent e) {
		//
		// }
		// });

		TableColumn TableColumn2 = new TableColumn(table, SWT.NONE);
		TableColumn2.setText("路由下一跳地址");
		TableColumn2.setWidth(150);

		TableColumn TableColumn3 = new TableColumn(table, SWT.NONE);
		TableColumn3.setText("路由类型");
		TableColumn3.setWidth(100);

		TableColumn TableColumn4 = new TableColumn(table, SWT.NONE);
		TableColumn4.setText("路由协议");
		TableColumn4.setWidth(100);

		TableColumn TableColumn5 = new TableColumn(table, SWT.NONE);
		TableColumn5.setText("路由掩码");
		TableColumn5.setWidth(100);
		TableColumnSortor.addStringSorter(table, TableColumn1);
		TableColumnSortor.addStringSorter(table, TableColumn2);
		TableColumnSortor.addStringSorter(table, TableColumn3);
		TableColumnSortor.addStringSorter(table, TableColumn4);
		TableColumnSortor.addStringSorter(table, TableColumn5);
	}

	public static void setData(final Display display, final String serverAddress, final int port, final String community, final int version, final Table table, boolean flag) {
		if (flag) {
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					for (TableItem tableItem : table.getItems()) {
						tableItem.dispose();
					}
				}
			});
		}
		for (int index = 0; index < oids.size(); index++) {
			final String oid = oids.get(index);
			final int n = index;
			try {
				ExecutorService executor = Executors.newSingleThreadExecutor();
				FutureTask<Boolean> future = (FutureTask<Boolean>) executor.submit(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						Runnable runnable = new Runnable() {

							@Override
							public void run() {
								int version1 = version;
								SnmpWalk snmpWalk = new SnmpWalk(serverAddress, port, community, oid, version1);
								Map<String, String> snmp_values = snmpWalk.snmpWalkMap();
								if (snmp_values.size() == 1 && snmp_values.get("error") == null) {
									if (version1 == 2) {
										version1 = 1;
									} else if (version1 == 1) {
										version1 = 2;
									}
									snmpWalk = new SnmpWalk(serverAddress, port, community, oid, version1);
									snmp_values = snmpWalk.snmpWalkMap();
								}
								final List<String> snmp_list = snmpWalk.snmpWalk();
								if (snmp_values.size() < 1 && snmp_list.size() < 1) {
									return;
								}
								if (snmp_list.size() == 1 && snmp_list.get(0).contains("error")) {
									return;
								}
								final Map<String, String> values = snmp_values;
								display.asyncExec(new Runnable() {

									@Override
									public void run() {
										for (int i = 0; i < snmp_list.size(); i++) {
											if (i == 0 && table.getItems().length > 0) {
												break;
											}
											TableItem tableItem = new TableItem(table, SWT.NONE);
											tableItem.setData(snmp_list.get(i));
										}
										if (n == 3) {
											for (int i = 0; i < table.getItems().length; i++) {
												TableItem tableItem = table.getItems()[i];
												if("2".equals(snmp_list.get(i))){
													tableItem.setText(n, "local("+snmp_list.get(i)+")");
												}else{
													tableItem.setText(n, snmp_list.get(i));	
												}
												
											}
										} else {
											for (TableItem tableItem : table.getItems()) {
												String key = (String) tableItem.getData();
												String value = values.get(oids.get(n) + "." + key);
												if(n==2){
													if("3".equals(value)){
														tableItem.setText(n, "direct("+value+")");
													}else if("4".equals(value)){
														tableItem.setText(n, "indirect("+value+")");
													}else{
														tableItem.setText(n, value);	
													}
												}else{
													tableItem.setText(n, value);
												}
											}
										}
									}
								});
							}
						};
						runnable.run();
						return true;
					}
				});
				future.get(1000, TimeUnit.MILLISECONDS);
			} catch (Exception e) {

			}

		}
	}
}
