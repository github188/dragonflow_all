package com.siteview.ecc.yft.odata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.siteview.ecc.yft.util.ODataUtils;

import Siteview.Operators;
import Siteview.QueryInfoToGet;
import Siteview.SiteviewException;
import Siteview.SiteviewQuery;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;
import siteview.IFunctionExtension;

public class GetYFTGisNodeToken implements IFunctionExtension {

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api,
			Map<String, String> inputParamMap) {
		
		
		
		List<Map<String,String>> listMap = new ArrayList<Map<String,String>>();
		
		if (inputParamMap == null || inputParamMap.size() == 0) return listMap;
		String O_CODE = inputParamMap.get("O_CODE");
		String arg = inputParamMap.get("HOST");
		
		SiteviewQuery q = new SiteviewQuery();
		q.set_BusinessObjectName("Organization");
		q.set_InfoToGet(QueryInfoToGet.All);
		
		String host = "";
		String in_host = "";
		String username = "";
		String passwd = "";
		
		try {
			q.set_BusObSearchCriteria(q.get_CriteriaBuilder().FieldAndValueExpression("o_code", Operators.Equals, O_CODE));
			
			Collection<?> bos = api.get_BusObService().get_SimpleQueryResolver().ResolveQueryToBusObList(q);
			
			for (Iterator<?> it = bos.iterator(); it.hasNext();) {
				BusinessObject bo = (BusinessObject) it.next();
				
				host = (String) bo.GetField("o_host").get_NativeValue();
				in_host = (String) bo.GetField("in_host").get_NativeValue();
				username = (String) bo.GetField("prop_login_user").get_NativeValue();
				passwd = (String) bo.GetField("prop_login_pwd").get_NativeValue();
				break;
			}
			
		} catch (SiteviewException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String url = "";
		if("O_HOST".equals(arg))
		{
			url = assemble(host);
		}else if("IN_HOST".equals(arg))
		{
			url = assemble(in_host);
		}
				
		if(!"".equals(url))
		{
			String token = ODataUtils.getToken(url, username, passwd);
			Map<String, String> map = new HashMap<String, String>();
			map.put("TOKEN", token);
			listMap.add(map);
		}
		
		return listMap;
	}

	
	public String assemble(String host)
	{
		String url = "";
		if(!"".equals(host))
		{
			String loginUrl = host.endsWith("/") ? "%s%srest/auth/login" : "%s%s/rest/auth/login";
			String header = "http://";
			String prefix =  host.startsWith(header) ? "" : header;
			
			url = String.format(loginUrl, prefix, host);
		}
		return url;
	}
}
