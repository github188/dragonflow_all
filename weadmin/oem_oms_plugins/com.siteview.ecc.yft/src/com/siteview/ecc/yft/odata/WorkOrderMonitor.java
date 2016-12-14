package com.siteview.ecc.yft.odata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import com.siteview.utils.db.DBQueryUtils;

import Siteview.DataColumn;
import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.Api.ISiteviewApi;
import siteview.IFunctionExtension;
/*
 * 监测器触发工单配置
 */
public class WorkOrderMonitor implements IFunctionExtension {
	public WorkOrderMonitor() {
		// TODO Auto-generated constructor stub
	}
	String sql_vidoe="SELECT v.*,vi.VIDEONAME,vi.IPADDRESS,vi.SERVERFLAG,vi.IPLACE,vi.DIRECTIONTYPE,vi.BASETYPE,vi.VENDORID,"
			+ "vi.MANUFACTURER from VIDEOALARMRESULT v,VIDEOPOINTINFO vi where v.VIDEOFLAG=vi.VIDEOFLAG and v.VIDEOFLAG='%s'";
	
	String sql_storge="SELECT  s.SERVERNAME,s.SERVERIP,s.VENDORID,s.MANUFACTURER,v.* from SERVERINFO s,%s v where v.SERVERFLAG=s.SERVERFLAG and s.SERVERFLAG='%s'";
 	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api,
			Map<String, String> inputParamMap) {
 		List<Map<String,String>> outp=new ArrayList<Map<String,String>>();
 		Map<String,String> outm=new HashMap<String, String>();
		String  gb=inputParamMap.get("GB");
		String  type=inputParamMap.get("EQUIPMENTTYPE");
		DataTable dt=null;
		DataTable dt1=null;
		if(type==null){
			outm.put("BASEDATA", "");
			outm.put("OTHERDATA", "");
			outp.add(outm);
		}else if(type.equals("摄像机")){
			 dt=DBQueryUtils.Select(String.format(sql_vidoe, gb), api);
			 dt1=DBQueryUtils.Select(String.format("SELECT  LOSTSTARTTIME,LOSTENDTIME,LOSTDURATION "
			 		+ "from VIDEORECORDRESULT where VIDEOFLAG='%s' order by LOSTSTARTTIME asc", gb), api);
		}else{ 
			if(type.equals("DVR")){
				dt=DBQueryUtils.Select(String.format(sql_storge,"DVRALARMRESULT", gb), api);
			}else if(type.equals("NVR")){
				dt=DBQueryUtils.Select(String.format(sql_storge,"NVRALARMRESULT", gb), api);
			}else if(type.equals("IPSAN")){
				dt=DBQueryUtils.Select(String.format(sql_storge,"IPSANALARMRESULT", gb), api);
			}else if(type.equals("编码器")){
				dt=DBQueryUtils.Select(String.format(sql_storge,"CODERALARMRESULT", gb), api);
			}
			dt1=DBQueryUtils.Select(String.format("SELECT  VIDEOFLAG,VIDEONAME,IPADDRESS "
					+ "from VIDEOPOINTINFO WHERE SERVERFLAG='%s'", gb), api);
		}
		if(dt!=null){
			Map<String,String> map=new HashMap<String, String>();
			for(DataRow dr:dt.get_Rows()){
				Iterator ite=dt.get_Columns().iterator();
				while(ite.hasNext()){
					DataColumn dc=(DataColumn) ite.next();
					String key=dc.get_CloumnSimpleName();
					map.put(key, dr.get(key)==null?"": dr.get(key).toString());
				}
			}
			outm.put("BASEDATA", JSONArray.fromObject(map).toString());
		}
		if(dt1!=null){
			Map<String,String> map=new HashMap<String, String>();
			for(DataRow dr:dt1.get_Rows()){
				Iterator ite=dt1.get_Columns().iterator();
				while(ite.hasNext()){
					DataColumn dc=(DataColumn) ite.next();
					String key=dc.get_CloumnSimpleName();
					map.put(key, dr.get(key)==null?"": dr.get(key).toString());
				}
			}
			outm.put("OTHERDATA", JSONArray.fromObject(map).toString());
		}
		outp.add(outm);
		return outp;
	}

}
