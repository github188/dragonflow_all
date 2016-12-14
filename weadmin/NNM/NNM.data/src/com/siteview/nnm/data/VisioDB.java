
package com.siteview.nnm.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class VisioDB {

	public static Map<String,Map<String,Integer>> visiodb=new HashMap<String,Map<String,Integer>>() ;
	static {
		Connection conn=ConfigDB.getvConn();
		Map<String, Integer> obj=null;
		String sql="select xh,svgwidth,svgheight,sysoid from visiomain order by xh";
		ResultSet rs= ConfigDB.query(sql, conn);
		try {
			while(rs.next()){
			   int	xh=rs.getInt("xh");
			   int  svgwidth=rs.getInt("svgwidth");
			   int svgheight=rs.getInt("svgheight");
			   String sysoid=rs.getString("sysoid");
			   obj=new HashMap<String,Integer>();
			   obj.put("width", svgwidth);
			   obj.put("height", svgheight);
			   obj.put("xh", xh);
			   visiodb.put(sysoid, obj);
			
				
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(Exception ex){
			
		}
		ConfigDB.close(conn);
		
	}
}
