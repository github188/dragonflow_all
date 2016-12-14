package com.siteview.workorder.odata.yft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import com.siteview.utils.db.DBUtils;
import com.siteview.workorder.odata.yft.entities.UserObject;

import Siteview.DataRow;
import Siteview.Api.ISiteviewApi;
import siteview.IFunctionExtension;

/**
 * 删除角色用户关联表
 * @author Administrator
 *
 */
public class DeleteUserRole implements IFunctionExtension {

	public DeleteUserRole() {
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api, Map<String, String> inputParamMap) {
		String roleName = inputParamMap.get("ROLE_NAME");
		String loginId = inputParamMap.get("LOGIN_ID");
		String flag = "true";
		boolean b = false; //是否有删除失败用户
		List<Map<String, String>> functionListMap = new ArrayList<Map<String, String>>();
		Map<String, String> valueMap = new HashMap<String, String>();
		List<UserObject> objs = new ArrayList<UserObject>();
		try {
			if(loginId != null){
				String[] ids = loginId.split(",",-1);
				for (String id : ids) {
					DataRow countRow = DBUtils.getDataRow(api, String.format("SELECT COUNT(*) c FROM UserRole WHERE UserId = '%s'",id));
					int count = Integer.parseInt(countRow.get("c").toString());
					if(count <= 1) { //用户只有1个角色了,不能删除
						UserObject object = new UserObject();
						object.setLoginId(id);
						object.setFlag("1");
						objs.add(object);
						flag = "false";
					} else {
						String sql = "DELETE FROM UserRole WHERE RoleId='%s' AND UserId = '%s'";
						try {
							if(DBUtils.delete(String.format(sql, roleName, id), api) <= 0){ //只要有一个删除失败,返回false
								flag = "false";
							}
						} catch (Exception e) {
							flag = "false";
						}
//						flag = DBUtils.delete(String.format(sql, roleName, id), api) > 0 ? "true" : "false";
						if ("false".equals(flag)) {
							UserObject object = new UserObject();
							object.setLoginId(id);
							object.setFlag("0");
							objs.add(object);
							if(!b) //只要有一个是删除失败
								b = true;
						}
					}
				}
			}
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			flag = "false";
		}
		valueMap.put("FAIL_USERS", JSONArray.fromObject(objs).toString());
		valueMap.put("OUT_FLAG", flag);
		functionListMap.add(valueMap);
		return functionListMap;
	}

}
