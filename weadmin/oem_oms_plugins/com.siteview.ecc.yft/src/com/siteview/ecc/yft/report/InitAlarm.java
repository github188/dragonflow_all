package com.siteview.ecc.yft.report;

import java.util.concurrent.TimeUnit;

import Siteview.SiteviewException;
import Siteview.Api.ISiteviewApi;

import com.siteview.ecc.kernel.init.IEccDataInit;
import com.siteview.ecc.kernel.scheduler.EccSchedulerBuilder;
import com.siteview.ecc.yft.util.YFTReadFile;

public class InitAlarm implements IEccDataInit {
	ISiteviewApi api;
	public InitAlarm() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initParamter(ISiteviewApi siteviewApi) {
		// TODO Auto-generated method stub
		this.api=siteviewApi;
	}

	@Override
	public void execute() throws SiteviewException {
		// TODO Auto-generated method stub
		if(InitReport.map==null)
			InitReport.map=YFTReadFile.getFileInfo();
		if(InitReport.map!=null&&InitReport.map.get("the_parent_domain")!=null)
			EccSchedulerBuilder.addCustomTaskJob(
				api, "yftAlarmExtension", "yftAlarmExtension", TimeUnit.HOURS, 2, 10*60*1000);
	}

	@Override
	public boolean checkIFRun() throws SiteviewException {
		// TODO Auto-generated method stub
		return false;
	}

}
