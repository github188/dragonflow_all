package com.siteview.ecc.yft.Servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bouncycastle.asn1.isismtt.ISISMTTObjectIdentifiers;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import Siteview.AuthenticationSource;
import Siteview.IUserInfo;
import Siteview.SiteviewException;
import Siteview.SiteviewValue;
import Siteview.User;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;
import Siteview.Xml.Scope;

import com.siteview.ecc.aas.dao.DaoFactory;
import com.siteview.ecc.aas.dao.RoleDispatcher;
import com.siteview.ecc.kernel.core.EccSchedulerManager;
import com.siteview.ecc.security.utils.DaoUtils;
import com.siteview.ecc.yft.bean.JsonValueProcessorImpl;
import com.siteview.ecc.yft.bean.ServerDetail;
import com.siteview.ecc.yft.report.ServerUtils;
import com.siteview.utils.date.DateUtils;

public class SetUserImport extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		ISiteviewApi api=EccSchedulerManager.getCurrentSiteviewApi();
		req.setCharacterEncoding("UTF-8");
//		BufferedReader buffered=req.getReader();
//		String data="";
//		String line = "";
//		while((line = buffered.readLine())!=null){
//			data+=line;
//		}
		String userid=req.getParameter("loginID").toString();
		String pwd=req.getParameter("pwd");
		String email=req.getParameter("email");
		String sms=req.getParameter("sms");
		String name=req.getParameter("displayName");
		String groupid=req.getParameter("safegroup");
		String roles=req.getParameter("roles");
		String type=req.getParameter("type");
		try {
			if(api.get_AuthenticationService().GetUser("User", userid) == null&&type.equals("add")){
				User user = api.get_AuthenticationService().CreateUser("User", userid);
				user.set_SecurityGroupName(groupid);
				user.set_SecurityGroupId(api.get_SecurityService().GetSecurityGroupIdByName(groupid));
				user.set_AllowInternalAuthentication(Boolean.valueOf(true));
				user.set_UseLDAP(Boolean.valueOf(false));
				IUserInfo uinfo = user.get_InternalUserInfo();
				if (uinfo == null) {
					uinfo = api.get_AuthenticationService().CreateUserInfo("User",AuthenticationSource.Internal);
				}
				uinfo.set_AuthenticationId(userid);
				uinfo.set_Password(pwd);
				user.set_InternalUserInfo(uinfo);
				BusinessObject userBo = saveUserInfo(api, user, userid,name,email,sms);
				user.set_BusObId(userBo.get_Id());
				api.get_AuthenticationService().SaveUser(user);
				saveUserRole(api, userid,roles);
			}else if(api.get_AuthenticationService().GetUser("User", userid) !=null &&type.equals("delete")){
				User u = api.get_AuthenticationService().GetUser("User", userid);
				api.get_AuthenticationService().DeleteUser(u, true);
				api.get_SettingsService().Delete(Scope.User, u.get_OriginalLoginId());
				// 删RoleUser数据
				RoleDispatcher roleDispatcher = DaoFactory.createRoleDispatcher();
				api.get_NativeSQLSupportService().ExecuteNativeSQL("delete from UserAndOrganizeRel where loginId='"+userid+"'",null);
				roleDispatcher.revokeUserRoleFromUserId(u.get_LoginId(), api);
			}
		} catch (SiteviewException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		resp.getOutputStream().write("good".getBytes("UTF-8"));
		resp.getOutputStream().flush();
	}
	private BusinessObject saveUserInfo(ISiteviewApi api, User user, String userid,String username,String email,String sms) throws SiteviewException {
		boolean b = false;
		BusinessObject userBo = DaoUtils.getUserBusinessObject(api, user.get_BusObId());
		userBo.GetField("loginID").SetValue(new SiteviewValue(userid));
		userBo.GetField("FirstName").SetValue(new SiteviewValue(username));
		userBo.GetField("LastName").SetValue(new SiteviewValue(""));
		userBo.GetField("Email").SetValue(new SiteviewValue(email));
		userBo.GetField("Cellphone").SetValue(new SiteviewValue(sms)); //�ֻ�
		userBo.GetField("Phone1Link").SetValue(new SiteviewValue(sms)); //�̶��绰
//		userBo.GetField("DepartmentCode").SetValue(new SiteviewValue(deptName));
//		userBo.GetField("Status").SetValue(new SiteviewValue(inputParamMap.get("LOGIN_STATUS") == null ? "" : inputParamMap.get("LOGIN_STATUS")));
//		userBo.GetField("Title").SetValue(new SiteviewValue(jobName));
//		userBo.GetField("Supervisor").SetValue(new SiteviewValue(inputParamMap.get("SUPERVISOR") == null ? "" : inputParamMap.get("SUPERVISOR")));
		boolean a = userBo.SaveObject(api, true, true).get_Success();
		return userBo;
	}
	/**
	 * 保存用户和角色关联关系
	 * @param api
	 * @param inputParamMap
	 * @throws SiteviewException
	 */
	public void saveUserRole(ISiteviewApi api, String loginId,String roleName)
			throws SiteviewException {
		RoleDispatcher roleImpl = DaoFactory.createRoleDispatcher();
		// 用户角色一对多关系
		if (roleName != null) {
			String[] roles = roleName.split(",", -1);
			for (String role : roles)
				roleImpl.dispatchRole2User(loginId, role, api);
		}
	}
}
