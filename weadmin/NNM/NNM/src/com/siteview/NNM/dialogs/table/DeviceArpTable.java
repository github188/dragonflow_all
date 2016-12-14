package com.siteview.NNM.dialogs.table;

import java.util.ArrayList;
import java.util.Iterator;
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

public class DeviceArpTable {
	public static List<String> oids = null;

	static {
		if (oids == null) {
			oids = new ArrayList<String>();
		}
		oids.add("1.3.6.1.2.1.4.22.1.1");
		oids.add("1.3.6.1.2.1.4.22.1.2");
		oids.add("1.3.6.1.2.1.4.22.1.3");
		oids.add("1.3.6.1.2.1.4.22.1.4");

	}

	public static void createTableColumns(final Table table) {
		final TableColumn TableColumn1 = new TableColumn(table, SWT.NONE);
		TableColumn1.setText("接口序号");
		TableColumn1.setWidth(70);

		TableColumn TableColumn2 = new TableColumn(table, SWT.NONE);
		TableColumn2.setText("物理地址");
		TableColumn2.setWidth(150);
		TableColumnSortor.addStringSorter(table, TableColumn2);

		TableColumn TableColumn3 = new TableColumn(table, SWT.NONE);
		TableColumn3.setText("ip地址");
		TableColumn3.setWidth(150);
		TableColumnSortor.addStringSorter(table, TableColumn3);

		TableColumn TableColumn4 = new TableColumn(table, SWT.NONE);
		TableColumn4.setText("arp类型");
		TableColumn4.setWidth(100);
		TableColumnSortor.addStringSorter(table, TableColumn4);

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
											if (i == 0 &&table!=null&& table.getItems().length > 0) {
												break;
											}
											Iterator<String> it = values.keySet().iterator();
											while (it.hasNext()) {
												String values_key = it.next();
												int m=1;
												if (values_key.contains(oids.get(n))&& values_key.contains(snmp_list.get(i))) {
													for (TableItem tableItem : table.getItems()) {
														String key = (String) tableItem.getData();
														if(key.equals(values_key)){
															m=0;
															break;
														}
													}
													if(m==1){
														TableItem tableItem = new TableItem(table, SWT.NONE);
														values_key = values_key.replace(oids.get(n), "");
														tableItem.setData(values_key);
													}
												}
											}
											break;
										}
										for (TableItem tableItem : table.getItems()) {
											String key = (String) tableItem.getData();
											if(n==1){
												String mac=values.get(oids.get(n) + key);
												if(mac.contains(":")){
													mac=mac.replaceAll(":", "").toUpperCase();
												}
												tableItem.setText(n, mac);
											}else{
												tableItem.setText(n, values.get(oids.get(n) + key));
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
