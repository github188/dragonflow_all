package com.siteview.NNM.util;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.siteview.NNM.dialogs.DeviceLink;
import com.siteview.NNM.dialogs.DeviceLocate;
import com.siteview.NNM.dialogs.DevicePanel;
import com.siteview.NNM.dialogs.DevicePropery;
import com.siteview.NNM.dialogs.DiagnosticToolsDialog;
import com.siteview.NNM.dialogs.MibSTable;
import com.siteview.NNM.dialogs.QuickViewAlarmDialog;
import com.siteview.NNM.dialogs.cpuMemdialog;
import com.siteview.ecc.authorization.ItossAuthorizeServiceImpl;
import com.siteview.ecc.constants.Operation;
import com.siteview.ecc.constants.Resource;
import com.siteview.nnm.data.model.svNode;

import siteview.windows.forms.MsgBox;

/**
 * 右键菜单:设备属性,设备定位,设备端口实时分析,设备连接,设备面板,设备信息查询,设备告警信息查询,诊断工具
 * 
 * @author Administrator
 *
 */
public class NNMContextMenu {
	private Action propertyaction;
	private Action onlineaction;
	private Action panelaction;

	private Action locateaction;// 设备定位
	private Action portaction;// 设备端口实时分析
	private Action cpumemoryaction;// CPU 内存 分析

	private Action selectaction;// 设备信息查询
	private Action alarmaction;// 设备告警信息查看
	private Action thresholdaction;// 阀值
	private Action toolaction;// 工具

	private Menu popMenu;
	private Table table;
	private int diagnoseIndex;
	public NNMContextMenu(Table table,int diagnoseIndex) {
		this.table = table;
		this.diagnoseIndex = diagnoseIndex;
		createAction();
		fillPopMenu();
	}

	public void createAction() {
		propertyaction = new Action("设备属性") {
			private static final long serialVersionUID = 1L;

			public void run() {
				if (table.getSelection().length > 0) {
					TableItem tableitem = table.getSelection()[0];
					svNode node = (svNode) tableitem.getData();
					DevicePropery dp = new DevicePropery(Display.getDefault().getActiveShell(), node.getSvid(), "host",
							null);
					dp.open();
				}
			}
		};
		propertyaction.setEnabled(
				ItossAuthorizeServiceImpl.getInstance().isPermitted(Resource.DEVICE_MANAGEMENT_DEVICE_PROP.getType(),
						Operation.DEVICE_MANAGEMENT_DEVICE_PROP.getOperationString(), "*"));
		onlineaction = new Action("设备连接") {
			private static final long serialVersionUID = 1L;
			public void run() {
				if (table.getSelection().length > 0) {
					TableItem tableitem = table.getSelection()[0];
					svNode node = (svNode) tableitem.getData();
					DeviceLink dp = new DeviceLink(Display.getCurrent().getActiveShell(), node.getSvid(), "host");
					dp.open();
				}
			}
		};
		onlineaction.setEnabled(
				ItossAuthorizeServiceImpl.getInstance().isPermitted(Resource.DEVICE_MANAGEMENT_DEVICE_LINK.getType(),
						Operation.DEVICE_MANAGEMENT_DEVICE_LINK.getOperationString(), "*"));

		panelaction = new Action("设备面板") {
			private static final long serialVersionUID = 1L;
			public void run() {
				if (table.getSelection().length > 0) {
					TableItem tableitem = table.getSelection()[0];
					svNode node = (svNode) tableitem.getData();
					if (node.getSvgtype() == 0 || node.getSvgtype() == 1 || node.getSvgtype() == 2
							|| node.getSvgtype() == 3) {
						DevicePanel dpl = new DevicePanel(Display.getCurrent().getActiveShell(), node);
						dpl.open();
					}

				}
			}
		};
		panelaction.setEnabled(
				ItossAuthorizeServiceImpl.getInstance().isPermitted(Resource.DEVICE_MANAGEMENT_DEVICE_PANEL.getType(),
						Operation.DEVICE_MANAGEMENT_DEVICE_PANEL.getOperationString(), "*"));

		locateaction = new Action("设备定位") {
			private static final long serialVersionUID = 1L;
			public void run() {
				if (table.getSelection().length > 0) {
					TableItem tableitem = table.getSelection()[0];
					svNode node = (svNode) tableitem.getData();
					DeviceLocate lcate = new DeviceLocate(Display.getCurrent().getActiveShell(), node.getLocalip(),
							node.getMac());
					lcate.open();
				}
			}
		};
		locateaction.setEnabled(
				ItossAuthorizeServiceImpl.getInstance().isPermitted(Resource.DEVICE_MANAGEMENT_DEVICE_LOCATE.getType(),
						Operation.DEVICE_MANAGEMENT_DEVICE_LOCATE.getOperationString(), "*"));

		portaction = new Action("设备端口实时分析") {
			private static final long serialVersionUID = 1L;
			public void run() {
				if (table.getSelection().length > 0) {
					TableItem tableitem = table.getSelection()[0];
					svNode node = (svNode) tableitem.getData();
					MsgBox.ShowConfirm("正在火热开发中...敬请期待");
				}
			}
		};
		portaction.setEnabled(ItossAuthorizeServiceImpl.getInstance().isPermitted(
				Resource.DEVICE_MANAGEMENT_DEVICE_PORT_REALTIME_ANALYSIS.getType(),
				Operation.DEVICE_MANAGEMENT_DEVICE_PORT_REALTIME_ANALYSIS.getOperationString(), "*"));

		cpumemoryaction = new Action("CPU 内存 分析") {
			private static final long serialVersionUID = 1L;
			public void run() {
				if (table.getSelection().length > 0) {
					TableItem tableitem = table.getSelection()[0];
					svNode node = (svNode) tableitem.getData();
					if (node.getSvgtype() == 0 || node.getSvgtype() == 1 || node.getSvgtype() == 2
							|| node.getSvgtype() == 3) {
						cpuMemdialog cpumem = new cpuMemdialog(Display.getCurrent().getActiveShell(), node);
						cpumem.open();
					}
				}
			}
		};
		cpumemoryaction.setEnabled(ItossAuthorizeServiceImpl.getInstance().isPermitted(
				Resource.DEVICE_MANAGEMENT_DEVICE_CPUMEMORY_ANALYSIS.getType(),
				Operation.DEVICE_MANAGEMENT_DEVICE_CPUMEMORY_ANALYSIS.getOperationString(), "*"));

		selectaction = new Action("设备信息查询") {
			private static final long serialVersionUID = 1L;
			public void run() {
				if (table.getSelection().length > 0) {
					TableItem tableitem = table.getSelection()[0];
					svNode node = (svNode) tableitem.getData();
					MibSTable moreip = new MibSTable(Display.getCurrent().getActiveShell(), "", "host", node.getSvid());
					moreip.open();
				}
			}
		};
		selectaction.setEnabled(
				ItossAuthorizeServiceImpl.getInstance().isPermitted(Resource.DEVICE_INFORMATION_QUERY.getType(),
						Operation.DEVICE_INFORMATION_QUERY.getOperationString(), "*"));

		alarmaction = new Action("设备告警信息查看") {
			private static final long serialVersionUID = 1L;
			public void run() {
				if (table.getSelection().length > 0) {
					TableItem tableitem = table.getSelection()[0];
					svNode node = (svNode) tableitem.getData();
					QuickViewAlarmDialog qva = new QuickViewAlarmDialog(Display.getCurrent().getActiveShell(), node,
							node.getLocalip());
					qva.open();
				}
			}
		};
		alarmaction.setEnabled(ItossAuthorizeServiceImpl.getInstance().isPermitted(
				Resource.DEVICE_MANAGEMENT_DEVICE_ALARM_INFO.getType(),
				Operation.DEVICE_MANAGEMENT_DEVICE_ALARM_INFO.getOperationString(), "*"));

		thresholdaction = new Action("阀值") {
			private static final long serialVersionUID = 1L;
			public void run() {
				if (table.getSelection().length > 0) {
					TableItem tableitem = table.getSelection()[0];
					svNode node = (svNode) tableitem.getData();
					MsgBox.ShowConfirm("正在火热开发中...敬请期待");
				}
			}
		};
		thresholdaction.setEnabled(
				ItossAuthorizeServiceImpl.getInstance().isPermitted(Resource.DEVICE_MANAGEMENT_THRESHOLD.getType(),
						Operation.DEVICE_MANAGEMENT_THRESHOLD.getOperationString(), "*"));

		toolaction = new Action("诊断工具") {
			private static final long serialVersionUID = 1L;

			public void run() {
				if (table.getSelection().length > 0) {
					TableItem tableitem = table.getSelection()[0];
					svNode node = (svNode) tableitem.getData();
					DiagnosticToolsDialog dtd = new DiagnosticToolsDialog(Display.getDefault().getActiveShell(), node, diagnoseIndex);
					dtd.open();
				}
			}
		};
		toolaction.setEnabled(ItossAuthorizeServiceImpl.getInstance().isPermitted(Resource.TOOLS.getType(),
				Operation.SHOW_NNM_TOOLS.getOperationString(), "*"));

	}

	/*
	 * 弹出菜单
	 */
	public void fillPopMenu() {
		MenuManager menuManage = new MenuManager();
		menuManage.add(propertyaction);
		menuManage.add(locateaction);
		menuManage.add(portaction);
		menuManage.add(cpumemoryaction);
		menuManage.add(onlineaction);
		menuManage.add(panelaction);
		menuManage.add(selectaction);
		menuManage.add(alarmaction);
		// menuManage.add(thresholdaction);
		menuManage.add(toolaction);
		popMenu = menuManage.createContextMenu(table);
		table.setMenu(popMenu);
	}
}
