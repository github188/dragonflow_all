package com.siteview.ecc.yft.bundle;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;

import com.siteview.utils.db.DBQueryUtils;

import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;
import siteview.IAutoTaskExtension;

public class ModifyAssetMonitor implements IAutoTaskExtension {

	public ModifyAssetMonitor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String run(Map<String, Object> params) throws Exception {
		BusinessObject bo = (BusinessObject) params.get("_CUROBJ_");
		ISiteviewApi api = (ISiteviewApi) params.get("_CURAPI_");
		String gbcode=bo.GetField("gbcode").get_NativeValue()==null?"":bo.GetField("gbcode").get_NativeValue().toString();
		String kh=bo.GetField("NormalType").get_NativeValue()==null?"":bo.GetField("NormalType").get_NativeValue().toString();
		String bm=bo.GetField("DeviceCode").get_NativeValue()==null?"":bo.GetField("DeviceCode").get_NativeValue().toString();
		if(gbcode.length()==0)
			return null;
		DBQueryUtils.UpdateorDelete(String.format("update SERVERINFO SET NormalType='%s' "
				+ ", DeviceCode='%s' WHERE  SERVERFLAG='%s'",kh,bm,gbcode), api);
		DBQueryUtils.UpdateorDelete(String.format("update VIDEOPOINTINFO SET NormalType='%s' "
				+ ", DeviceCode='%s' WHERE  VIDEOFLAG='%s'",kh,bm,gbcode), api);
		
		return null;
	}

	@Override
	public boolean hasCustomUI() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void creatConfigUI(Composite parent, Map<String, String> params) {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, String> getConfig() {
		// TODO Auto-generated method stub
		return null;
	}

}
