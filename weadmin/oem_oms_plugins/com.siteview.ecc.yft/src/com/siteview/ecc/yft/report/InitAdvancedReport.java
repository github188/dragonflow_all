package com.siteview.ecc.yft.report;

import java.util.concurrent.TimeUnit;

import Siteview.SiteviewException;
import Siteview.Api.ISiteviewApi;

import com.siteview.ecc.kernel.init.IEccDataInit;
import com.siteview.ecc.kernel.scheduler.EccSchedulerBuilder;
import com.siteview.ecc.main.odata.GetCNFTree;
import com.siteview.ecc.yft.util.YFTReadFile;

public class InitAdvancedReport implements IEccDataInit {
	ISiteviewApi api;
	public InitAdvancedReport() {
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
		if(InitReport.map!=null&&InitReport.map.get("the_parent_domain")==null){
			EccSchedulerBuilder.addCustomTaskJob(
					api, "yftAdvancedReportExtension", "yftAdvancedReportExtension", TimeUnit.HOURS, 2, 20*60*1000);
			GetCNFTree.flg=true;
		}
	}

	@Override
	public boolean checkIFRun() throws SiteviewException {
		// TODO Auto-generated method stub
		return false;
	}

}
