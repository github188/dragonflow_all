package com.siteview.NNM.aas;

import com.siteview.ecc.authorization.ItossAuthorizeServiceImpl;
import com.siteview.ecc.authorization.PermissionFactory;
import com.siteview.ecc.constants.Operation;
import com.siteview.ecc.constants.Resource;

public class NNMPermissions {
	public static NNMPermissions nnmPermissions = new NNMPermissions();

	public static NNMPermissions getInstance() {
		return nnmPermissions;
	}

	public boolean getNNMTopuOperationInView() {
		return getNNMPermissions(Resource.TOPU_OPERATION_IN_VIEW.getType(), Operation.TOPUOPERATIONINVIEW.getOperationString(), "*");
	}

	public boolean getNNMTopuOperationInViewAddDevice() {
		return getNNMPermissions(Resource.TOPU_OPERATION_IN_VIEW_ADD_DEVICE.getType(), Operation.TOPUOPERATIONINVIEW_ADD_DEVICE.getOperationString(), "*");
	}

	public boolean getNNMTopuOperationInViewAddline() {
		return getNNMPermissions(Resource.TOPU_OPERATION_IN_VIEW_ADD_LINE.getType(), Operation.TOPUOPERATIONINVIEW_ADD_LINE.getOperationString(), "*");
	}

	public boolean getNNMTopuOperationInViewSavetopu() {
		return getNNMPermissions(Resource.TOPU_OPERATION_IN_VIEW_SAVE_TOPU.getType(), Operation.TOPUOPERATIONINVIEW_SAVE_TOPU.getOperationString(), "*");
	}

	public boolean getNNMTopuOperationInViewEditDeviceProp() {
		return getNNMPermissions(Resource.TOPU_OPERATION_IN_VIEW_EDIT_DEVICE_PROP.getType(), Operation.TOPUOPERATIONINVIEW_EDIT_DEVICE_PROP.getOperationString(), "*");
	}

	public boolean getNNMTopuOperationInViewDevicePanel() {
		return getNNMPermissions(Resource.TOPU_OPERATION_IN_VIEW_DEVICE_PANEL.getType(), Operation.TOPUOPERATIONINVIEW_DEVICE_PANEL.getOperationString(), "*");
	}

	public boolean getNNMTopuOperationInViewEditLine() {
		return getNNMPermissions(Resource.TOPU_OPERATION_IN_VIEW_EDIT_LINE.getType(), Operation.TOPUOPERATIONINVIEW_EDIT_LINE.getOperationString(), "*");
	}

	public boolean getNNMTopuOperationInViewDeleteDevice() {
		return getNNMPermissions(Resource.TOPU_OPERATION_IN_VIEW_DELETE_DEVICE.getType(), Operation.TOPUOPERATIONINVIEW_DELETE_DEVICE.getOperationString(), "*");
	}

	public boolean getNNMTopuOperationInViewDeleteLine() {
		return getNNMPermissions(Resource.TOPU_OPERATION_IN_VIEW_DELETE_LINE.getType(), Operation.TOPUOPERATIONINVIEW_DELETE_LINE.getOperationString(), "*");
	}

	public boolean getNNMTopuOperationInViewCreateChildGraph() {
		return getNNMPermissions(Resource.TOPU_OPERATION_IN_VIEW_CREATE_CHILD_GRAPH.getType(), Operation.TOPUOPERATIONINVIEW_CREATE_CHILD_GRAPH.getOperationString(), "*");
	}

//	public boolean getNNMTopuOperationInViewEditChildGraph() {
//		return getNNMPermissions(Resource.TOPU_OPERATION_IN_VIEW_EDIT_CHILD_GRAPH.getType(), Operation.TOPUOPERATIONINVIEW_CREATE_CHILD_GRAPH.getOperationString(), "*");
//	}

	public boolean getNNMTopuOperationInViewDeleteChildGraph() {
		return getNNMPermissions(Resource.TOPU_OPERATION_IN_VIEW_DELETE_CHILD_GRAPH.getType(), Operation.TOPUOPERATIONINVIEW_DELETE_CHILD_GRAPH.getOperationString(), "*");
	}
	
	public boolean getNNMTopuOperationInViewDevice() {
		return getNNMPermissions(Resource.TOPU_OPERATION_IN_VIEW_DELETE_CHILD_GRAPH.getType(), Operation.TOPUOPERATIONINVIEW_DELETE_CHILD_GRAPH.getOperationString(), "*");
	}
	
	public boolean getNNMTopuOperationInViewLinePropery() {
		return getNNMPermissions(Resource.TOPU_OPERATION_IN_VIEW_LINE_PROPERY.getType(), Operation.TOPUOPERATIONINVIEW_DELETE_LINE_PROPERY.getOperationString(), "*");
	}
	public boolean getNNMTopuOperationInViewLineSet() {
		return getNNMPermissions(Resource.TOPU_OPERATION_IN_VIEW_LINE_SET.getType(), Operation.TOPUOPERATIONINVIEW_DELETE_LINE_SET.getOperationString(), "*");
	}
	
	public boolean getNNMTopuOperationInViewTopuSet() {
		return getNNMPermissions(Resource.TOPU_OPERATION_IN_VIEW_TOPUSETUP.getType(), Operation.TOPUOPERATIONINVIEW_TOPUSETUP.getOperationString(), "*");
	}
	public boolean getNNMTopuOperationInViewShowPc() {
		return getNNMPermissions(Resource.TOPU_OPERATION_IN_VIEW_SHOW_PC.getType(), Operation.TOPUOPERATIONINVIEW_SHOWPC.getOperationString(), "*");
	}
	
	public boolean getNNMTopuOperationInViewDeviceConnect() {
		return getNNMPermissions(Resource.TOPU_OPERATION_IN_VIEW_CONNECT.getType(), Operation.TOPUOPERATIONINVIEW_DEVICE_CONNECT.getOperationString(), "*");
	}
	
	//
	public boolean getNNMDeviceInformationQuery() {
		return getNNMPermissions(Resource.DEVICE_INFORMATION_QUERY.getType(), Operation.DEVICE_INFORMATION_QUERY.getOperationString(), "*");
	}

	public boolean getNNMDeviceManagement() {
		return getNNMPermissions(Resource.DEVICE_MANAGEMENT.getType(), Operation.SHOW_NNM_DEVICE_MANAGEMENT.getOperationString(), "*");
	}

	public boolean getNNMDeviceManagementDeviceProp() {
		return getNNMPermissions(Resource.DEVICE_MANAGEMENT_DEVICE_PROP.getType(), Operation.DEVICE_MANAGEMENT_DEVICE_PROP.getOperationString(), "*");
	}

	public boolean getNNMDeviceManagementDeviceLocate() {
		return getNNMPermissions(Resource.DEVICE_MANAGEMENT_DEVICE_LOCATE.getType(), Operation.DEVICE_MANAGEMENT_DEVICE_LOCATE.getOperationString(), "*");
	}

	public boolean getNNMDeviceManagementDevicePortRealtimeAnalysis() {
		return getNNMPermissions(Resource.DEVICE_MANAGEMENT_DEVICE_PORT_REALTIME_ANALYSIS.getType(), Operation.DEVICE_MANAGEMENT_DEVICE_PORT_REALTIME_ANALYSIS.getOperationString(), "*");
	}

	public boolean getNNMDeviceManagementDeviceAnalysus() {
		return getNNMPermissions(Resource.DEVICE_MANAGEMENT_DEVICE_CPUMEMORY_ANALYSIS.getType(), Operation.DEVICE_MANAGEMENT_DEVICE_CPUMEMORY_ANALYSIS.getOperationString(), "*");
	}

	public boolean getNNMDeviceManagementDeviceLink() {
		return getNNMPermissions(Resource.DEVICE_MANAGEMENT_DEVICE_LINK.getType(), Operation.DEVICE_MANAGEMENT_DEVICE_LINK.getOperationString(), "*");
	}
	
	public boolean getNNMDeviceManagementDevicePanel() {
		return getNNMPermissions(Resource.DEVICE_MANAGEMENT_DEVICE_PANEL.getType(), Operation.DEVICE_MANAGEMENT_DEVICE_PANEL.getOperationString(), "*");
	}
	public boolean getNNMDeviceManagementDeviceRefresh() {
		return getNNMPermissions(Resource.TOPU_OPERATION_IN_VIEW_DELETE_REFRESH_DEVICE.getType(), Operation.TOPUOPERATIONINVIEW_REFRESH_DEVICE.getOperationString(), "*");
	}

	
	// public static boolean getNNMDeviceManagementDeviceInfo() {
	// return
	// getNNMPermissions(Resource.DEVICE_MANAGEMENT_DEVICE_INFO.getType(),
	// Operation.DEVICE_MANAGEMENT_DEVICE_INFO.getOperationString(), "*");
	// }

	public boolean getNNMDeviceManagementDeviceAlarmInfo() {
		return getNNMPermissions(Resource.DEVICE_MANAGEMENT_DEVICE_ALARM_INFO.getType(), Operation.DEVICE_MANAGEMENT_DEVICE_ALARM_INFO.getOperationString(), "*");
	}

	public boolean getNNMDeviceManagementThreshold() {
		return getNNMPermissions(Resource.DEVICE_MANAGEMENT_THRESHOLD.getType(), Operation.DEVICE_MANAGEMENT_THRESHOLD.getOperationString(), "*");
	}

	public boolean getNNMTools() {
		return getNNMPermissions(Resource.TOOLS.getType(), Operation.SHOW_NNM_TOOLS.getOperationString(), "*");
	}

	public boolean getNNMToolsPing() {
		return getNNMPermissions(Resource.TOOLS_PING.getType(), Operation.TOOLS_PING.getOperationString(), "*");
	}

	public boolean getNNMToolsSsh() {
		return getNNMPermissions(Resource.TOOLS_SSH.getType(), Operation.TOOLS_SSH.getOperationString(), "*");
	}

	public boolean getNNMToolsTelnet() {
		return getNNMPermissions(Resource.TOOLS_TELNET.getType(), Operation.TOOLS_TELNET.getOperationString(), "*");
	}

	public boolean getNNMToolsTraceroute() {
		return getNNMPermissions(Resource.TOOLS_TRACEROUTE.getType(), Operation.TOOLS_TRACEROUTE.getOperationString(), "*");
	}

	public boolean getNNMToolsSnmp() {
		return getNNMPermissions(Resource.TOOLS_SNMP.getType(), Operation.TOOLS_SNMP.getOperationString(), "*");

	}

	//
	public boolean getNNMInfoMaintainEdit() {
		return getNNMPermissions(Resource.DEVICE_INFORMATION_MAINTENANCE_EDIT.getType(), Operation.DEVICE_INFORMATION_MAINTENANCE_EDIT.getOperationString(), "*");
	}

	public boolean getNNMInfoMaintainAdd() {
		return getNNMPermissions(Resource.DEVICE_INFORMATION_MAINTENANCE_ADD.getType(), Operation.DEVICE_INFORMATION_MAINTENANCE_ADD.getOperationString(), "*");
	}

	public boolean getNNMInfoMaintainSearch() {
		return getNNMPermissions(Resource.DEVICE_INFORMATION_MAINTENANCE_SEARCH.getType(), Operation.DEVICE_INFORMATION_MAINTENANCE_SEARCH.getOperationString(), "*");
	}

	public boolean getNNMInfoMaintainAddFirm() {
		return getNNMPermissions(Resource.DEVICE_INFORMATION_MAINTENANCE_ADD_FIRM.getType(), Operation.DEVICE_INFORMATION_MAINTENANCE_ADD_FIRM.getOperationString(), "*");
	}

	//
	public boolean getNNMDeviceInformationQueryDeviceInfo() {
		return getNNMPermissions(Resource.DEVICE_INFORMATION_QUERY_DEVICE_INFO.getType(), Operation.DEVICE_INFORMATION_QUERY_DEVICE_INFO.getOperationString(), "*");
	}

	public boolean getNNMDeviceInformationQueryInterface() {
		return getNNMPermissions(Resource.DEVICE_INFORMATION_QUERY_INTERFACE_INFO.getType(), Operation.DEVICE_INFORMATION_QUERY_INTERFACE_INFO.getOperationString(), "*");
	}

	public boolean getNNMDeviceInformationQueryRoutingtable() {
		return getNNMPermissions(Resource.DEVICE_INFORMATION_QUERY_ROUTINGTABLE_INFO.getType(), Operation.DEVICE_INFORMATION_QUERY_ROUTINGTABLE_INFO.getOperationString(), "*");
	}

	public boolean getNNMDeviceInformationQueryForwarding() {
		return getNNMPermissions(Resource.DEVICE_INFORMATION_QUERY_FORWARDING_INFO.getType(), Operation.DEVICE_INFORMATION_QUERY_FORWARDING_INFO.getOperationString(), "*");
	}

	public boolean getNNMDeviceInformationQueryARP() {
		return getNNMPermissions(Resource.DEVICE_INFORMATION_QUERY_ARP_INFO.getType(), Operation.DEVICE_INFORMATION_QUERY_ARP_INFO.getOperationString(), "*");
	}

	public boolean getNNMDeviceInformationQueryCDP() {
		return getNNMPermissions(Resource.DEVICE_INFORMATION_QUERY_CDP_INFO.getType(), Operation.DEVICE_INFORMATION_QUERY_CDP_INFO.getOperationString(), "*");
	}

	public boolean getNNMDeviceInformationQueryIp() {
		return getNNMPermissions(Resource.DEVICE_INFORMATION_QUERY_IP_INFO.getType(), Operation.DEVICE_INFORMATION_QUERY_IP_INFO.getOperationString(), "*");
	}

	private boolean getNNMPermissions(String type, String operationString, String str) {
		return ItossAuthorizeServiceImpl.getInstance().isPermitted(PermissionFactory.createPermission(type, operationString, str));
	}

}
