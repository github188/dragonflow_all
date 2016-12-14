package com.siteview.ecc.yft.odata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import com.siteview.utils.db.DBQueryUtils;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.Api.ISiteviewApi;
import siteview.IFunctionExtension;

public class GetSupervisionUser implements IFunctionExtension {

	public GetSupervisionUser() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api,
			Map<String, String> inputParamMap) {
		String maincode=inputParamMap.get("MAINCODE");
		String othercode=inputParamMap.get("OTHERCODE");
		List<Map<String, String>> outP=new ArrayList<Map<String, String>>();
		Map<String, String> outM=new HashMap<String,String>();
		outP.add(outM);
		String sql="SELECT wfd.* from WorkFlowDetails wfd ,WorkFlow w where w.Name like '%督办%' and wfd.WorkFlowId=w.RecId";
		DataTable dt=DBQueryUtils.Select(sql, api);
		String roles="";
		for(DataRow dr:dt.get_Rows()){
			String formrole=dr.get("FromId")==null?"":dr.get("FromId").toString();
			String torole=dr.get("ToId")==null?"":dr.get("ToId").toString();
			if(formrole.length()>0&&!roles.contains(formrole)){
				if(roles.length()>0)
					roles+=",";
				roles+="'"+formrole+"'";
			}
			
			if(torole.length()>0&&!roles.contains(torole)){
				if(roles.length()>0)
					roles+=",";
				roles+="'"+torole+"'";
			}
		}
		
		if(maincode.length()>0&&othercode.length()>0&&!maincode.endsWith(othercode)){
			othercode+=","+maincode;
			othercode=othercode.replaceAll(",", "','");
		}
		sql="select s.LoginID,s.GroupName,p.DisplayName from SiteviewSecMap s ,UserRole u,Profile p where s.GroupName in ('%s') "
				+ "and u.roleId in (%s) and u.UserId=s.LoginID and p.LoginID=s.LoginID ";
		dt=DBQueryUtils.Select(String.format(sql,othercode,roles), api);
		Map<String,String> map=new HashMap<String,String>();
		Map<String,String> map1=new HashMap<String,String>();
		for(DataRow dr:dt.get_Rows()){
			String code=dr.get("GroupName")==null?"":dr.get("GroupName").toString();
			String userid=dr.get("loginID")==null?"":dr.get("loginID").toString();
			String displayName=dr.get("DisplayName")==null?"":dr.get("DisplayName").toString();
			String s=map.get(code);
			String s1=map1.get(code);
			if(s==null)
				s="";
			if(s1==null)
				s1="";
			if(!s.contains(userid)){
				if(s.length()>0){
					s+=",";
					s1+=",";
				}
				s+=userid;
				s1+=displayName;
			}
			map.put(code, s);
			map1.put(code, s1);
		}
		String[] ss=othercode.split("','");
		List<Map> lists=new ArrayList<Map>();
		for(String name:ss){
			Map<String,String> maps=new HashMap<String,String>();
			maps.put("GroupName", name);
			maps.put("User", map.get(name)==null?"":map.get(name).toString());
			maps.put("UserName",  map1.get(name)==null?"":map1.get(name).toString());
			lists.add(maps);
		}
		outM.put("DATA", JSONArray.fromObject(lists).toString());
		return outP;
	}

}
