package com.siteview.workorder.odata.yft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import com.siteview.utils.db.DBQueryUtils;
import com.siteview.workorder.odata.yft.entities.FaultType;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.Api.ISiteviewApi;
import siteview.IFunctionExtension;

public class GetFaultType implements IFunctionExtension {

	public GetFaultType() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api,
			Map<String, String> inputParamMap) {
		List<Map<String,String>> outParam=new ArrayList<Map<String,String>>();
		Map<String,String> outmap=new HashMap<String,String>();
		
		List<FaultType> faulttypes=new ArrayList<FaultType>();
		Map<String,List<FaultType>> faultmap=new HashMap<String,List<FaultType>>();
		String sql="select * from ServiceCatalog ";
		DataTable dt=DBQueryUtils.Select(sql, api);
		for(DataRow dr:dt.get_Rows()){
			String name=dr.get("Name")==null?"":dr.get("Name").toString();
			String recid=dr.get("RecId")==null?"":dr.get("RecId").toString();
			FaultType fault=new FaultType();
			fault.setFaultTypeName(name);
			fault.setFaultTypeRecId(recid);
			faulttypes.add(fault);
		}
		sql="select * from ServiceItem";
		dt=DBQueryUtils.Select(sql, api);
		for(DataRow dr:dt.get_Rows()){
			String parentid=dr.get("CatalogRecId")==null?"":dr.get("CatalogRecId").toString();
			String name=dr.get("Name")==null?"":dr.get("Name").toString();
			String recid=dr.get("RecId")==null?"":dr.get("RecId").toString();
			FaultType fault=new FaultType();
			fault.setFaultTypeName(name);
			fault.setFaultTypeRecId(recid);
			List<FaultType> list=faultmap.get(parentid);
			if(list==null)
				list=new ArrayList<FaultType>();
			list.add(fault);
			faultmap.put(parentid, list);
		}
		outmap.put("SERVICECATALOG", JSONArray.fromObject(faulttypes).toString());
		outmap.put("SERVICEITEM", JSONArray.fromObject(faultmap).toString());
		outParam.add(outmap);
		return outParam;
	}

}
