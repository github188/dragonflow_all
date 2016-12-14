package com.siteview.NNM.dialogs.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.siteview.utils.snmp.SnmpWalk;

public class DeviceInterfaceTable {
	public static List<String> oids = null;

	static {
		if (oids == null) {
			oids = new ArrayList<String>();
		}
		oids.add("1.3.6.1.2.1.2.2.1.1");
		oids.add("1.3.6.1.2.1.2.2.1.2");
		oids.add("1.3.6.1.2.1.2.2.1.3");
		oids.add("1.3.6.1.2.1.2.2.1.6");
		oids.add("1.3.6.1.2.1.2.2.1.4");
		oids.add("1.3.6.1.2.1.2.2.1.5");
		oids.add("1.3.6.1.2.1.2.2.1.7");
		oids.add("1.3.6.1.2.1.2.2.1.8");
		oids.add("1.3.6.1.2.1.2.2.1.9");
		oids.add("1.3.6.1.2.1.2.2.1.10");
		oids.add("1.3.6.1.2.1.2.2.1.11");
		oids.add("1.3.6.1.2.1.2.2.1.12");
		oids.add("1.3.6.1.2.1.2.2.1.13");
		oids.add("1.3.6.1.2.1.2.2.1.14");
		oids.add("1.3.6.1.2.1.2.2.1.15");
		oids.add("1.3.6.1.2.1.2.2.1.16");
		oids.add("1.3.6.1.2.1.2.2.1.17");
		oids.add("1.3.6.1.2.1.2.2.1.18");
		oids.add("1.3.6.1.2.1.2.2.1.19");
		oids.add("1.3.6.1.2.1.2.2.1.20");
		oids.add("1.3.6.1.2.1.2.2.1.21");
	}

	public static void createTableColumns(final Table table) {
		final TableColumn TableColumn1 = new TableColumn(table, SWT.NONE);
		TableColumn1.setText("接口索引");
		TableColumn1.setWidth(80);

		TableColumn TableColumn2 = new TableColumn(table, SWT.NONE);
		TableColumn2.setText("接口描述");
		TableColumn2.setWidth(120);

		TableColumn TableColumn3 = new TableColumn(table, SWT.NONE);
		TableColumn3.setText("接口类型");
		TableColumn3.setWidth(100);

		TableColumn TableColumn4 = new TableColumn(table, SWT.NONE);
		TableColumn4.setText("物理地址");
		TableColumn4.setWidth(150);

		TableColumn TableColumn5 = new TableColumn(table, SWT.NONE);
		TableColumn5.setText("最大传输数");
		TableColumn5.setWidth(100);

		TableColumn TableColumn6 = new TableColumn(table, SWT.NONE);
		TableColumn6.setText("接口速率");
		TableColumn6.setWidth(100);

		TableColumn TableColumn7 = new TableColumn(table, SWT.NONE);
		TableColumn7.setText("管理状态");
		TableColumn7.setWidth(100);

		TableColumn TableColumn8 = new TableColumn(table, SWT.NONE);
		TableColumn8.setText("工作状态");
		TableColumn8.setWidth(100);

		TableColumn TableColumn9 = new TableColumn(table, SWT.NONE);
		TableColumn9.setText("当前状态的时间");
		TableColumn9.setWidth(100);

		TableColumn TableColumn10 = new TableColumn(table, SWT.NONE);
		TableColumn10.setText("接收字节数");
		TableColumn10.setWidth(100);

		TableColumn TableColumn11 = new TableColumn(table, SWT.NONE);
		TableColumn11.setText("接收的单播分组数");
		TableColumn11.setWidth(160);

		TableColumn TableColumn12 = new TableColumn(table, SWT.NONE);
		TableColumn12.setText("接收的非单播分组数");
		TableColumn12.setWidth(150);

		TableColumn TableColumn13 = new TableColumn(table, SWT.NONE);
		TableColumn13.setText("收到的被丢弃的分组数");
		TableColumn13.setWidth(160);

		TableColumn TableColumn14 = new TableColumn(table, SWT.NONE);
		TableColumn14.setText("接收错包数");
		TableColumn14.setWidth(100);

		TableColumn TableColumn15 = new TableColumn(table, SWT.NONE);
		TableColumn15.setText("收到的未知协议丢包数");
		TableColumn15.setWidth(150);

		TableColumn TableColumn16 = new TableColumn(table, SWT.NONE);
		TableColumn16.setText("发送的字节总数");
		TableColumn16.setWidth(120);

		TableColumn TableColumn17 = new TableColumn(table, SWT.NONE);
		TableColumn17.setText("发送的单播分组数");
		TableColumn17.setWidth(120);

		TableColumn TableColumn18 = new TableColumn(table, SWT.NONE);
		TableColumn18.setText("发送的非单播分组数");
		TableColumn18.setWidth(150);

		TableColumn TableColumn19 = new TableColumn(table, SWT.NONE);
		TableColumn19.setText("发送丢包数");
		TableColumn19.setWidth(100);

		TableColumn TableColumn20 = new TableColumn(table, SWT.NONE);
		TableColumn20.setText("发送错包数");
		TableColumn20.setWidth(100);

		TableColumn TableColumn21 = new TableColumn(table, SWT.NONE);
		TableColumn21.setText("在输出队中的分组数");
		TableColumn21.setWidth(120);
		TableColumnSortor.addStringSorter(table, TableColumn1);
		TableColumnSortor.addStringSorter(table, TableColumn2);
		TableColumnSortor.addStringSorter(table, TableColumn3);
		TableColumnSortor.addStringSorter(table, TableColumn4);
		TableColumnSortor.addStringSorter(table, TableColumn5);
		TableColumnSortor.addStringSorter(table, TableColumn6);
		TableColumnSortor.addStringSorter(table, TableColumn7);
		TableColumnSortor.addStringSorter(table, TableColumn8);
		TableColumnSortor.addStringSorter(table, TableColumn9);
		TableColumnSortor.addStringSorter(table, TableColumn10);
		TableColumnSortor.addStringSorter(table, TableColumn11);
		TableColumnSortor.addStringSorter(table, TableColumn12);
		TableColumnSortor.addStringSorter(table, TableColumn13);
		TableColumnSortor.addStringSorter(table, TableColumn14);
		TableColumnSortor.addStringSorter(table, TableColumn15);
		TableColumnSortor.addStringSorter(table, TableColumn16);
		TableColumnSortor.addStringSorter(table, TableColumn17);
		TableColumnSortor.addStringSorter(table, TableColumn18);
		TableColumnSortor.addStringSorter(table, TableColumn19);
		TableColumnSortor.addStringSorter(table, TableColumn20);

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
								if (snmp_list.size() < 1 && snmp_list.size() < 1) {
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
												if (snmp_list.size() > i) {
													String mac = snmp_list.get(i).toUpperCase();
													if (mac.contains(":"))
														mac = mac.replaceAll(":", "");
													tableItem.setText(n, mac);
												}

											}
										} else {
											for (TableItem tableItem : table.getItems()) {
												String key = (String) tableItem.getData();
												String value = values.get(oid + "." + key);

												if (n == 6 || n == 7) {
													if ("1".equals(value))
														value = "up";
													else if ("2".equals(value))
														value = "down";
													tableItem.setText(n, value);
												} else if (n == 9 || n == 10 || n == 15 || n == 16) {
													if (!"".equals(value)) {
														value = Long.parseLong(value) / 1024 + "";
													} else {
														value = "0";
													}
													tableItem.setText(n, value);
												} else {
													if (value==null||"".equals(value))
														value = "0";
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
			} catch (InterruptedException e) {
				// e.printStackTrace();
//				System.out.println("获取数据中有超时");
			} catch (ExecutionException e) {
//				 e.printStackTrace();
//				 System.out.println("获取数据中有超时");
			} catch (TimeoutException e) {
//				 e.printStackTrace();
//				 System.out.println("获取数据中有超时");
			}

		}
	}

}
