package com.siteview.ecc.yft.odata;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.FaultAction;

import com.siteview.ecc.foundation.SecurityGroupManager;
import com.siteview.utils.db.DBQueryUtils;
import com.siteview.utils.workorder.WorkOrderUtils;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.ISecurityGroup;
import Siteview.SiteviewException;
import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;
import siteview.IFunctionExtension;

public class SupervisionWorkOrder implements IFunctionExtension {

	public SupervisionWorkOrder() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * 督办工单创建
	 */
	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api,
			Map<String, String> inputParamMap) {
		// TODO Auto-generated method stub
		List<Map<String, String>> outP=new ArrayList<Map<String,String>>();
		Map<String, String> outM=new HashMap<String, String>();
		String machinetype=inputParamMap.get("MACHINETTYPE");
		String maincode=inputParamMap.get("MAINCODE");
		String mainuser=inputParamMap.get("MAINUSER");
		String mainusername=inputParamMap.get("MAINUSERNAME");
		String mainclye=inputParamMap.get("MAINCLYE");
		String othercode=inputParamMap.get("OTHERCODE");
		String otheruser=inputParamMap.get("OTHERUSER");
		String otherusername=inputParamMap.get("OTHERUSERNAME");
		String otherclye=inputParamMap.get("OTHERCLYE");
		String mainalarm=inputParamMap.get("MAINALARMUPLOADID");
		String otheralarmup=inputParamMap.get("OTHERALARMUPLOADID");
		SecurityGroupManager groupManager = new SecurityGroupManager(api);
		try {
			String groupname=api.get_AuthenticationService().get_CurrentSecurityGroup();
			String uploadid=mainalarm;
			String s="";
			if(otheralarmup.length()>0){
				uploadid+=";"+otheralarmup;
				uploadid=uploadid.replaceAll(";", "','");
				s="update AlarmUpload set AlarmAction='4' where AlarmUploadId in ('%s')";
			}else{
				if(groupname.equals(maincode))
					s="update AlarmUpload set AlarmAction='4' where RecId='%s'";
				else
					s="update AlarmUpload set AlarmAction='4' where AlarmUploadId='%s'";
			}
			DBQueryUtils.UpdateorDelete(String.format(s, uploadid), api);
			String workflow="";
			String roles="";
			String fFaultLarge="";
			Date date=new Date();
			DataTable dt=DBQueryUtils.Select("select wd.* from WorkFlow w,WorkFlowDetails wd where Name like '%督办%' and w.RecId =wd.WorkFlowId", api);
			for(DataRow dr:dt.get_Rows()){
				workflow=dr.get("WorkFlowId")==null?"":dr.get("WorkFlowId").toString();
				String role=dr.get("FromId")==null?"":dr.get("FromId").toString();
				if(!roles.contains(role)){
					if(roles.length()>0)
						roles+=",";
					roles+="'"+role+"'";
				}	
				role=dr.get("ToId")==null?"":dr.get("ToId").toString();
				if(!roles.contains(role)){
					if(roles.length()>0)
						roles+=",";
					roles+="'"+role+"'";
				}
			}
			String sql="select * from UserRole where RoleId in (%s)";
			dt=DBQueryUtils.Select(String.format(sql, roles), api);
			Map<String,String> map=new HashMap<String, String>();
			for(DataRow dr:dt.get_Rows()){
				map.put(dr.get("UserId")==null?"":dr.get("UserId").toString(),
						dr.get("RoleId")==null?"":dr.get("RoleId").toString());
			}
			
			sql="select RecId from FaultClassification where FaultName='%s' ORDER BY ParentID ASC";
			dt=DBQueryUtils.Select(String.format(sql, machinetype), api);
			if(dt.get_Rows().size()>0){
				fFaultLarge=dt.get_Rows().get(0).get("RecId").toString();
			}
			int number=WorkOrderUtils
					.getSerialNumber(api, "WorkOrderCommon", "WorkOrderNumber");
			
			BusinessObject bo=api.get_BusObService().Create("WorkOrderCommon");
			bo.GetField("Subject").SetValue(new SiteviewValue(maincode+machinetype+"告警升级督办单"));
			bo.GetField("CurrentHandle").SetValue(new SiteviewValue(mainusername));
			bo.GetField("WorkOrderDesc").SetValue(new SiteviewValue(maincode+"在周期内未处理告警达到"+mainclye+"条"));
			bo.GetField("WorkOrderNumber").SetValue(new SiteviewValue(number));
			bo.GetField("SerialNumber").SetValue(new SiteviewValue(number));
			bo.GetField("Status").SetValue(new SiteviewValue("cl"));
			bo.GetField("FaultLevelId").SetValue(new SiteviewValue("高"));
			bo.GetField("SolveSteps").SetValue(new SiteviewValue(mainalarm));
			try{
				ISecurityGroup savedGroup =groupManager.getSecurityGroup(maincode);
				if(savedGroup!=null)
				bo.GetField("ParentId").SetValue(new SiteviewValue(savedGroup.get_Id()));
			}catch(Exception ex){}
			bo.GetField("ResponseTime").SetValue(new SiteviewValue(date));
			bo.GetField("SolutionTime").SetValue(new SiteviewValue(date));
			bo.GetField("WorkOrderType").SetValue(new SiteviewValue(workflow));
			bo.GetField("WorkOrderMark").SetValue(new SiteviewValue("1"));
			bo.GetField("FaultLarge").SetValue(new SiteviewValue(fFaultLarge));
			createWorkOrderFlow(api, api.get_AuthenticationService().get_CurrentLoginId(),
					mainuser, bo.get_RecId(),map,api.get_AuthenticationService().get_CurrentUserDisplayName(),mainusername);
			bo.SaveObject(api, true, true);
			number++;
			if(othercode!=null&&othercode.length()>0){
				String[] othercodes=othercode.split(",");
				String[] otherusers=otheruser.split(",");
				String[] otherclyes=otherclye.split(",");
				String[] otherusernames=otherusername.split(",");
				String[] otheralarm=otheralarmup.split(";");
				if(otherclyes.length==othercodes.length&&otherclyes.length==otherusers.length&&
						otherclyes.length==otherusernames.length)
					for(int i=0;i<othercodes.length;i++){
						BusinessObject subbo=api.get_BusObService().Create("WorkOrderCommon");
						subbo.GetField("Subject").SetValue(new SiteviewValue(othercodes[i]+machinetype+"告警升级督办单"));
						subbo.GetField("CurrentHandle").SetValue(new SiteviewValue(otherusernames[i]));
						subbo.GetField("WorkOrderDesc").SetValue(new SiteviewValue(othercodes[i]+"在周期内未处理告警达到"+otherclyes[i]+"条"));
						subbo.GetField("WorkOrderNumber").SetValue(new SiteviewValue(number));
						subbo.GetField("SerialNumber").SetValue(new SiteviewValue(number));
						subbo.GetField("Status").SetValue(new SiteviewValue("cl"));
						subbo.GetField("FaultLevelId").SetValue(new SiteviewValue("高"));
						subbo.GetField("SolveSteps").SetValue(new SiteviewValue(otheralarm[i]));
						try{
							ISecurityGroup savedGroup =groupManager.getSecurityGroup(othercodes[i]);
							if(savedGroup!=null)
								subbo.GetField("ParentId").SetValue(new SiteviewValue(savedGroup.get_Id()));
						}catch(Exception ex){}
						subbo.GetField("ResponseTime").SetValue(new SiteviewValue(date));
						subbo.GetField("SolutionTime").SetValue(new SiteviewValue(date));
						subbo.GetField("WorkOrderType").SetValue(new SiteviewValue(workflow));
						subbo.GetField("WorkOrderMark").SetValue(new SiteviewValue("1"));
						subbo.GetField("FaultLarge").SetValue(new SiteviewValue(fFaultLarge));
						subbo.SaveObject(api, true, true);
						number++;
						CreateWorkOrdersRelatedTicket(bo.get_RecId(), subbo.get_RecId(),
								api,mainuser,otherusers[i],map,mainusername,otherusernames[i]);
					}
			}
			outM.put("RETURN", "good");
		} catch (SiteviewException e) {
			e.printStackTrace();
			outM.put("RETURN", e.getMessage());
		}
		outP.add(outM);
		return outP;
	}
	/**
	 * 工单与工单关联
	 * @param borecid 主工单id
	 * @param subborecid 关联子工单id
	 * @param api
	 * @param mainuser 子工单的发起人
	 * @param otheruser 子工单分配到的人
	 * @param map 人员与角色map
	 * @param mainname 子工单发起人名
	 * @param otherusername 子工单分配到的人名
	 */
	public static void CreateWorkOrdersRelatedTicket(String borecid,String subborecid,ISiteviewApi api,
			String mainuser,String otheruser,Map<String,String> map,String mainname,String otherusername){
		try {
			BusinessObject bo=api.get_BusObService().Create("WorkOrdersRelatedTicket");
			bo.GetField("MainTicket").SetValue(new SiteviewValue(borecid));
			bo.GetField("SubTicketStatus").SetValue(new SiteviewValue("cl"));
			bo.GetField("SubTicket").SetValue(new SiteviewValue(subborecid));
			bo.SaveObject(api, true, true);
			
			createWorkOrderFlow(api, mainuser, otheruser, subborecid,map,mainname,otherusername);
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 创建工单流程日志
	 * @param api
	 * @param fromuser 工单来源人
	 * @param touser 工单分配到的人
	 * @param workorderid 工单id
	 * @param map 用户与角色
	 * @param formusername 工单来源人名字
	 * @param tousername 工单分配到的人名
	 */
	public static void createWorkOrderFlow(ISiteviewApi api,String fromuser,String touser,
			String workorderid,Map<String,String> map,String formusername,String tousername){
		try {
			BusinessObject _bo=api.get_BusObService().Create("WorkFlowLog");
			_bo.GetField("FlowAction").SetValue(new SiteviewValue("分配工单"));
			_bo.GetField("WorkOrderId").SetValue(new SiteviewValue(workorderid));
			_bo.GetField("FromId").SetValue(new SiteviewValue(map.get(fromuser)==null?
					api.get_AuthenticationService().get_CurrentSecurityGroup():map.get(fromuser)));
			_bo.GetField("ToId").SetValue(new SiteviewValue(map.get(touser)==null?"":map.get(touser)));
			_bo.GetField("Step").SetValue(new SiteviewValue("1"));
			_bo.GetField("Content").SetValue(new SiteviewValue("分配工单"));
			_bo.GetField("FromUser").SetValue(new SiteviewValue(formusername));
			_bo.GetField("ToUser").SetValue(new SiteviewValue(tousername));
			Date d=new Date();
			_bo.GetField("DispatchTime").SetValue(new SiteviewValue(d));
			_bo.GetField("ArrivalTime").SetValue(new SiteviewValue(d));
			_bo.SaveObject(api, true, true);
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
	}
}
