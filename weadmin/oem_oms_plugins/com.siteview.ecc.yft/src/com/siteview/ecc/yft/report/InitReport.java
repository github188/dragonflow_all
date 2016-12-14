package com.siteview.ecc.yft.report;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import Siteview.SiteviewException;
import Siteview.Api.ISiteviewApi;

import com.siteview.ecc.kernel.init.IEccDataInit;
import com.siteview.ecc.kernel.scheduler.EccSchedulerBuilder;
import com.siteview.ecc.yft.util.YFTReadFile;

public class InitReport implements IEccDataInit {
	ISiteviewApi api;

	public InitReport() {
	}

	@Override
	public void initParamter(ISiteviewApi siteviewApi) {
		this.api = siteviewApi;
	}

	public static Map<String, String> map = null;

	@Override
	public void execute() throws SiteviewException {
		if (map == null)
			map = YFTReadFile.getFileInfo();
		if (map.get("the_parent_domain") != null)
			EccSchedulerBuilder.addCustomTaskJob(api, "yftReportExtension", "yftReportExtension", TimeUnit.HOURS, 1, 10 * 60 * 1000);
		System.setProperty("the_system_code",map.get("the_system_code")!=null?map.get("the_system_code"):"");
		System.setProperty("the_system_name",map.get("the_system_name")!=null?map.get("the_system_name"):"");
		System.setProperty("the_parent_code",map.get("the_parent_code")!=null?map.get("the_parent_code"):"");
		System.setProperty("the_parent_name",map.get("the_parent_name")!=null?map.get("the_parent_name"):"");
		System.setProperty("the_diagnosis_name",map.get("the_diagnosis_name")!=null?map.get("the_diagnosis_name"):"");
		System.setProperty("the_parent_domain",map.get("the_parent_domain")!=null?map.get("the_parent_domain"):"");
	}

	@Override
	public boolean checkIFRun() throws SiteviewException {
		// TODO Auto-generated method stub
		return false;
	}

}
