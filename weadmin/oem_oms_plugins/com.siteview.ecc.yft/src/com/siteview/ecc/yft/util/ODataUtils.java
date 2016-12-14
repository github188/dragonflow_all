package com.siteview.ecc.yft.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.locks.ReentrantLock;

import Siteview.Convert;
import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.SiteviewException;
import Siteview.Api.ISiteviewApi;

import com.siteview.ecc.yft.report.InitReport;
import com.siteview.utils.db.DBQueryUtils;
import com.siteview.utils.db.DBUtils;

public class ODataUtils {

	public static final String APPLICATION_JSON = "application/json";

	public static String getToken(String odata_token_url, String userName, String passwd) {
		passwd = Convert.ToBase64String(passwd.getBytes());
		String result = sendGet(odata_token_url, "user=" + userName + "&passwd=" + passwd);

		// if (result.contains("token")) {
		// result = result.split(",")[1].trim();
		// result = result.substring(result.indexOf(":") + 2, result.length() - 1);
		// }
		// Type listType = new TypeToken<List<String>>(){}.getType();
		return result;
	}

	public static String sendGet(String url, String param) {
		String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = url;
			if (param != null && !"".equals(param))
				urlNameString = urlNameString + "?" + param;
			URL realUrl = new URL(urlNameString);
			URLConnection connection = realUrl.openConnection();
			// connection.setHostnameVerifier(new NullHostNameVerifier());
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
			connection
					.setRequestProperty(
							"User-Agent",
							"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
			connection.connect();
			in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("login in " + url + "error \r\n" + e);
			return e.getMessage();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 获取当前用户的父区域code
	 * 
	 * @param api
	 * @return
	 */
	public static String getParentAreaCode(ISiteviewApi api) {
		String parentareacode = "";
		if (InitReport.map.get("the_parent_domain") == null) { // 厅级
			try {
				String groupid = api.get_AuthenticationService().get_CurrentSecurityGroupId();
				String groupname = api.get_AuthenticationService().get_CurrentSecurityGroup();
				String sql = "";
				if (!groupname.equalsIgnoreCase("administrators"))
					sql = "select o.o_code from Organization o,OrganizeAndSafeGroupRel og where o.RecId=og.organize_id AND og.safegroup_id='%s'";
				else
					sql = "select o_code from Organization where parentId=''";
				DataTable dt = DBQueryUtils.Select(String.format(sql, groupid), api);
				for (DataRow dr : dt.get_Rows()) {
					parentareacode = dr.get("o_code") == null ? "" : dr.get("o_code").toString();
				}
			} catch (SiteviewException e) {
				e.printStackTrace();
			}
		}
		return parentareacode;
	}

	public static void main(String[] args) {

		System.out.println(getToken("http://192.168.9.63:10080/rest/auth/login", "admin", "manage"));

	}
}
