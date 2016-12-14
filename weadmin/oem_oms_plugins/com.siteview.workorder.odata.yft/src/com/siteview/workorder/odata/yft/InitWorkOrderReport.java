package com.siteview.workorder.odata.yft;

import java.util.concurrent.TimeUnit;

import Siteview.SiteviewException;
import Siteview.Api.ISiteviewApi;

import com.siteview.ecc.kernel.init.IEccDataInit;
import com.siteview.ecc.kernel.scheduler.EccSchedulerBuilder;

public class InitWorkOrderReport implements IEccDataInit {
	private ISiteviewApi api;
	
	public InitWorkOrderReport() {
	}

	@Override
	public void initParamter(ISiteviewApi siteviewApi) {
		api = siteviewApi;
	}

	@Override
	public void execute() throws SiteviewException {
//		EccSchedulerBuilder.addCustomTaskJob(api, "yftWorkOrderReportExtension", "yftWorkOrderReportExtension",
//				TimeUnit.MINUTES, 1, 10 * 60 * 1000);
		EccSchedulerBuilder.addCustomTaskJob(api, "yftWorkOrderReportExtension", "yftWorkOrderReportExtension",
				TimeUnit.DAYS, 1, 10 * 60 * 1000);
	}

	@Override
	public boolean checkIFRun() throws SiteviewException {
		return false;
	}

}
