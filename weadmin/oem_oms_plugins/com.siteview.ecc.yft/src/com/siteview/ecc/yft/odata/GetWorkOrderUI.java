package com.siteview.ecc.yft.odata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siteview.ecc.yft.Servlet.YFTVideo;
import com.siteview.utils.db.DBQueryUtils;
import com.siteview.utils.db.DBUtils;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.SiteviewException;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;
import siteview.IFunctionExtension;

public class GetWorkOrderUI implements IFunctionExtension {
/*
 * 触发工单需回填数据
 */
	public GetWorkOrderUI() {
	}

	@Override
	public List<Map<String, String>> executeFunct(ISiteviewApi api,Map<String, String> inputParamMap) {
		List<Map<String, String>> outP=new ArrayList<Map<String,String>>();
		String id=inputParamMap.get("RECID");
		
		if(id!=null&&id.trim().length()>0){
			String machinetype="";
			String machinetypename="";
			try {
				BusinessObject bo=DBQueryUtils.queryOnlyBo("RecId", id, "Equipment", api);
				if(bo!=null){
					machinetype=bo.GetField("EquipmentType").get_NativeValue().toString();
					String sql="SELECT RecId,typeAlias from EquipmentTypeRel where RecId in(SELECT ParentID from EquipmentTypeRel where relationEqName='Equipment."+machinetype+"')";
					DataTable dt=DBQueryUtils.Select(sql, api);
					for(DataRow dr:dt.get_Rows()){
						machinetype=dr.get("RecId")==null?"":dr.get("RecId").toString();
						machinetypename=dr.get("typeAlias")==null?"":dr.get("typeAlias").toString();
					}
				}
			} catch (SiteviewException e) {
				e.printStackTrace();
			}
			
			String monitortype=inputParamMap.get("MONITORTYPE");
			String sql_1="SELECT RecId,ProductType from HardwareAssets  WHERE GBCode='"+id+"'";
			DataTable dt1=DBQueryUtils.Select(sql_1, api);
			String assetsid="";
			String ptype="";
			for(DataRow dr0:dt1.get_Rows()){
				assetsid=dr0.get("RecId")==null?"":dr0.get("RecId").toString();
				ptype=dr0.get("ProductType")==null?"":dr0.get("ProductType").toString();
			}
			
			Map<String, String> outm=new HashMap<String, String>();
			outm.put("ASSETID", assetsid);
			//故障类别
			Map<String,String> fMap = YFTVideo.getFaultMap(api,machinetypename,monitortype);
			if (fMap.get(machinetypename) != null) {
					outm.put("FAULTLARGE",fMap.get(machinetypename)==null?"":fMap.get(machinetypename));
					outm.put("FAULTSMALL", fMap.get(monitortype)==null?"":fMap.get(monitortype));
					DataRow slaRow = DBUtils.getDataRow(api,"SELECT * FROM ServiceLevelAgreement WHERE ServiceCatalog = '" + (fMap.get(machinetypename)==null?"":fMap.get(machinetypename))+ "'");
					if (slaRow != null){
						outm.put("SLAID", slaRow.get("RecId").toString());
					}
			}
			if(outm.get("FAULTLARGE")==null||outm.get("FAULTLARGE").length()==0)
				outm.put("FAULTLARGE",ptype);
			outP.add(outm);
		}
		
		return outP;
	}

}
