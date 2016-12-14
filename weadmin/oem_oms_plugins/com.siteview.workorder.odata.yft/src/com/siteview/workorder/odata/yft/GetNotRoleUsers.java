package com.siteview.workorder.odata.yft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import com.siteview.ecc.foundation.AbsOrganization4MySQLService;
import com.siteview.ecc.foundation.SecurityGroupManager;
import com.siteview.ecc.yft.util.Organization4MySQLService;
import com.siteview.utils.db.DBUtils;
import com.siteview.utils.html.StringUtils;
import com.siteview.workorder.odata.yft.entities.OdataObject;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.ISecurityGroup;
import Siteview.Api.ISiteviewApi;
import siteview.IFunctionExtension;

/**
 * 获取不在当前角色下的用户
 * 
 * @author Administrator
 *
 */

public class GetNotRoleUsers implements IFunctionExtension {

	public GetNotRoleUsers() {
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api, Map<String, String> inputParamMap) {
		List<Map<String, String>> functionListMap = new ArrayList<Map<String, String>>();
		Map<String, String> valueMap = new HashMap<String, String>();
		try {
			String roleName = inputParamMap.get("ROLE_NAME");
			String securityId = api.isSuperAdminGroup()?null:api.get_AuthenticationService().get_CurrentSecurityGroupId();
			List<String> list=new ArrayList<String>();//包含了选择角色的组织
			SecurityGroupManager groupManager = new SecurityGroupManager(api);
			List<ISecurityGroup> groups=groupManager.getSecurityGroups();
			for(ISecurityGroup g:groups){
				if(g.get_RoleList().contains(roleName)){
					list.add(g.get_Name());
				}
			}
			AbsOrganization4MySQLService service = new Organization4MySQLService(api);
			String ids = service.getOrganizationIdsBySafegroupId(securityId);
			
			StringBuffer userRoleSql = new StringBuffer();
			if(!api.isSuperAdminGroup()){
				userRoleSql.append("SELECT a.* FROM (SELECT SiteviewSecMap.loginID,SiteviewSecMap.GroupName,profile.DisplayName FROM Organization,OrganizeAndSafeGroupRel,SiteviewSecMap ,Profile profile WHERE Organization.RecId=OrganizeAndSafeGroupRel.organize_id AND SiteviewSecMap.GroupID=OrganizeAndSafeGroupRel.safegroup_id AND profile.LoginID=SiteviewSecMap.loginID");
				if (ids != null) {
					userRoleSql.append(" and FIND_IN_SET(o_code,'").append(ids).append("') ");
				}
				userRoleSql.append(" GROUP BY SiteviewSecMap.loginID ORDER BY SiteviewSecMap.loginID) AS a WHERE a.loginID NOT IN (SELECT UserId FROM UserRole WHERE RoleId='"+roleName+"')");
			}
			else{
				userRoleSql.append("SELECT * FROM (select SiteviewSecMap.loginID,SiteviewSecMap.GroupName,p.DisplayName from SiteviewSecMap,Profile p where p.LoginID=SiteviewSecMap.loginID) a ");
				userRoleSql.append("where a.loginID NOT IN (select UserId from UserRole where RoleId='"+roleName+"')");
			}
			DataTable userTable = api.get_NativeSQLSupportService().ExecuteNativeSQLQuery(userRoleSql.toString(),null);
			List<OdataObject> objs = new ArrayList<OdataObject>();
			for (DataRow row : userTable.get_Rows()) {
				String loginId = row.get("LoginID").toString();
				String groupname=row.get("GroupName").toString();
				if(!list.contains(groupname))
					continue;
				OdataObject obj = new OdataObject();
				obj.setName(row.get("DisplayName").toString());
				obj.setId(loginId);
				objs.add(obj);
			}
			valueMap.put("USERS", JSONArray.fromObject(objs).toString());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			valueMap.put("ERROR_MSG", e.getMessage());
		}
		functionListMap.add(valueMap);
		return functionListMap;
	}

}
