package com.siteview.ecc.yft.report;

import java.util.concurrent.TimeUnit;

import Siteview.SiteviewException;
import Siteview.Api.ISiteviewApi;

import com.siteview.ecc.kernel.init.IEccDataInit;
import com.siteview.ecc.kernel.scheduler.EccSchedulerBuilder;

public class InitAssetsReport implements IEccDataInit {

	private ISiteviewApi api;

	public InitAssetsReport() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initParamter(ISiteviewApi siteviewApi) {
		this.api = siteviewApi;
	}

	@Override
	public void execute() throws SiteviewException {
		System.out.println("自动调度生成报表");
		EccSchedulerBuilder.addCustomTaskJob(api, "AutomaticGenerationAssetsReport", "AutomaticGenerationAssetsReport", TimeUnit.MINUTES, 1,  60 * 1000);

	}

	@Override
	public boolean checkIFRun() throws SiteviewException {
		return false;
	}

}
