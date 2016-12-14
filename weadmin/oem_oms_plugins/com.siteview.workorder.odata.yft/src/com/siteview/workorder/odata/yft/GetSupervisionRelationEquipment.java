package com.siteview.workorder.odata.yft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siteview.utils.db.DBQueryUtils;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.Api.ISiteviewApi;
import siteview.IFunctionExtension;

public class GetSupervisionRelationEquipment implements IFunctionExtension {

	public GetSupervisionRelationEquipment() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api,
			Map<String, String> inputParamMap) {
		// TODO Auto-generated method stub
		String s=inputParamMap.get("SOLVESTEPS");
		s=s.replaceAll(",", "','");
		String sql="select * from AlarmUploadLog where AlarmUploadId in ('%s') order by AreaName ";
		DataTable dt=DBQueryUtils.Select(String.format(sql, s), api);
		List<Map<String,String>> outList=new ArrayList<Map<String,String>>();
		Map<String,String> outmap=new HashMap<String, String>();
		for(DataRow dr:dt.get_Rows()){
			outmap.put("EQUIPMENTNAME", dr.get("EquipmentName")==null?"":dr.get("EquipmentName").toString());
			outmap.put("AREACODE", dr.get("AreaCode")==null?"":dr.get("AreaCode").toString());
			outmap.put("AREANAME", dr.get("AreaName")==null?"":dr.get("AreaName").toString());
			String status=dr.get("MonitorStatus")==null?"error":dr.get("MonitorStatus").toString();
			if(status.equalsIgnoreCase("error"))
				status="错误";
			else if(status.equalsIgnoreCase("warning"))
				status="危险";
			outmap.put("MONITORSTATUS",status );
			
			outmap.put("MONITORVALUE", dr.get("MonitorValue")==null?"":dr.get("MonitorValue").toString());
			outmap.put("EQUIPMENTIP", dr.get("EquipmentIp")==null?"":dr.get("EquipmentIp").toString());
			outList.add(outmap);
		}
		return outList;
	}

}
