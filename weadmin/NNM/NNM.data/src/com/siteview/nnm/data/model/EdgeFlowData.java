package com.siteview.nnm.data.model;

public class EdgeFlowData {
	public int inFlow;
	public int outFlow;
	public int inPkts;
	public int outPkts;
	public int inBroadcast;
	public int outBroadcast;
	public int portWorkStuatus;
	public int portAdminStatus;
	public String portIndex;
	
	public EdgeFlowData(int inFlow, int outFlow, int inPkts, int outPkts, int inBroadcast, int outBroadcast, int portWorkStuatus, int portAdminStatus, String portIndex)
	{
	    this.inFlow = inFlow;
	    this.outFlow = outFlow;
	    this.inPkts = inPkts;
	    this.outPkts = outPkts;
	    this.inBroadcast = inBroadcast;
	    this.outBroadcast = outBroadcast;
	    this.portWorkStuatus = portWorkStuatus;
	    this.portAdminStatus = portAdminStatus;
	    this.portIndex = portIndex;
	}
}
