package com.siteview.authority.yft.odata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siteview.ecc.aas.model.Organization;
import com.siteview.utils.db.DBQueryUtils;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.SiteviewException;
import Siteview.Api.ISiteviewApi;
import net.sf.json.JSONArray;
import siteview.IFunctionExtension;

public class GetUploadAlarmUI implements IFunctionExtension {

	public GetUploadAlarmUI() {
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api, Map<String, String> inputParamMap) {
		List<Map<String, String>> outparam = new ArrayList<Map<String, String>>();
		Map<String, String> outmap = new HashMap<String, String>();
		List<Organization> list = new ArrayList<Organization>();
		String groupid = "";
		try {
			groupid = api.get_AuthenticationService().get_CurrentSecurityGroupId();
			String groupname = api.get_AuthenticationService().get_CurrentSecurityGroup();
			String sql = "";
			if (groupname.equalsIgnoreCase("Administrators")) {
				sql = "select o_code,o_name from Organization ";
			} else {
				sql = "select o_code,o_name from Organization where parentId in(select o_code from OrganizeAndSafeGroupRel ogr,Organization o where o.RecID=ogr.organize_id and ogr.safegroup_id='%s')";
			}
			DataTable dt = DBQueryUtils.Select(String.format(sql, groupid), api);
			for (DataRow dr : dt.get_Rows()) {
				Organization or = new Organization();
				or.setOrganizationCode(dr.get("o_code") == null ? "" : dr.get("o_code").toString());
				or.setOrganizationName(dr.get("o_name") == null ? "" : dr.get("o_name").toString());
				list.add(or);
			}
			List equipmenttype = new ArrayList();
			sql = "select RecId,typeAlias from EquipmentTypeRel where ParentID='' OR ParentID is null";
			dt = DBQueryUtils.Select(sql, api);
			for (DataRow dr : dt.get_Rows()) {
				String recid = dr.get("RecId") == null ? "" : dr.get("RecId").toString();
				String typeAlias = dr.get("typeAlias") == null ? "" : dr.get("typeAlias").toString();
				equipmenttype.add(typeAlias);
			}
			equipmenttype.add("DVR");
			equipmenttype.add("NVR");
			equipmenttype.add("编码器");
			equipmenttype.add("摄像机");
			equipmenttype.add("IPSAN");
			outmap.put("EQUIPMENTTYPE", JSONArray.fromObject(equipmenttype).toString());
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
		outmap.put("ORGANIZATION", JSONArray.fromObject(list).toString());
		outparam.add(outmap);
		return outparam;
	}

}
