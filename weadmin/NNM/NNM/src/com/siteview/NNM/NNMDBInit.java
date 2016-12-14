package com.siteview.NNM;

import com.siteview.ecc.kernel.init.IEccDataInit;

import Siteview.SiteviewException;
import Siteview.Api.ISiteviewApi;

public class NNMDBInit implements IEccDataInit {

	public NNMDBInit() {
	}

	@Override
	public void initParamter(ISiteviewApi siteviewApi) {

	}

	@Override
	public void execute() throws SiteviewException {
		com.siteview.nnm.data.DBManage.init();
		com.siteview.nnm.data.DBManage.getTopoCharts();
		com.siteview.nnm.data.EntityManager.AddEntityTasks();
		com.siteview.nnm.data.FlowDataManager.AddEdgeFlowTasks();
	}

	@Override
	public boolean checkIFRun() throws SiteviewException {
		return false;
	}

}
