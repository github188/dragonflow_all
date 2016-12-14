package com.siteview.NNM;

import Siteview.SiteviewException;
import Siteview.Api.ISiteviewApi;

import com.siteview.ecc.kernel.init.IEccDataInit;
import com.siteview.nnm.data.EntityManager;
import com.siteview.nnm.ipmac.IpMacWorkers;


public class Siteviewdatabase implements IEccDataInit {

	private ISiteviewApi siteviewApi;

	public void initParamter(ISiteviewApi siteviewApi) {
		this.siteviewApi = siteviewApi;
	}

	@Override
	public void execute() throws SiteviewException {
		// TODO Auto-generated method stub
		//获取所有mac
		EntityManager.getAllIPMac(siteviewApi);
		System.err.println("Start Siteviewdatabase siteviewApi");
		IpMacWorkers.startAllIPMac(siteviewApi);
	}

	@Override
	public boolean checkIFRun() throws SiteviewException {
		// TODO Auto-generated method stub
		return false;
	}

}
