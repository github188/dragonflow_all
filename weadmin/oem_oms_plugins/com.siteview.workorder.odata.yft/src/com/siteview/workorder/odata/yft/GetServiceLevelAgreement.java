package com.siteview.workorder.odata.yft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import com.siteview.utils.db.DBQueryUtils;
import com.siteview.workorder.odata.yft.entities.ServiceLevelAgreement;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.Api.ISiteviewApi;
import siteview.IFunctionExtension;

public class GetServiceLevelAgreement implements IFunctionExtension {

	public GetServiceLevelAgreement() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api,
			Map<String, String> inputParamMap) {
		List<Map<String,String>> outParam=new ArrayList<Map<String,String>>();
		Map<String,String> outmap=new HashMap<String,String>();
		String sql="select sc.FaultName,sl.* from ServiceLevelAgreement sl left join "
		+ "(select FaultName,RecId from FaultClassification )sc on sc.RecId=sl.ServiceCatalog "
		+ "ORDER BY CreatedDateTime ";
		DataTable dt=DBQueryUtils.Select(sql, api);
		List<ServiceLevelAgreement> slas=new ArrayList<ServiceLevelAgreement>();
		for(DataRow dr:dt.get_Rows()){
			ServiceLevelAgreement sla=new ServiceLevelAgreement();
			sla.setServiceCatalogName(dr.get("FaultName")==null?"":dr.get("FaultName").toString());
			sla.setResponseTime(dr.get("ResponseTime")==null?"0":dr.get("ResponseTime").toString());
			sla.setServiceCatalogRecId(dr.get("ServiceCatalog")==null?"":dr.get("ServiceCatalog").toString());
			sla.setServiceLevelAgreementName(dr.get("Title")==null?"":dr.get("Title").toString());
			sla.setServiceLevelAgreementRecId(dr.get("RecId")==null?"":dr.get("RecId").toString());
			sla.setServiceProvider(dr.get("ServiceContent")==null?"":dr.get("ServiceContent").toString());
			sla.setSolveTime(dr.get("SolutionTime")==null?"0":dr.get("SolutionTime").toString());
			sla.setStatus(dr.get("Status")==null?"":dr.get("Status").toString());
			sla.setTimeoutCharges(dr.get("TotalCount")==null?"":dr.get("TotalCount").toString());
			sla.setCreatedby(dr.get("CreatedBy")==null?"":dr.get("CreatedBy").toString());
			sla.setCreateddatetime(dr.get("CreatedDateTime")==null?"":dr.get("CreatedDateTime").toString());
			slas.add(sla);
		}
		outmap.put("SERVICELEVELAGREEMENT", JSONArray.fromObject(slas).toString());
		outParam.add(outmap);
		return outParam;
	}

}
