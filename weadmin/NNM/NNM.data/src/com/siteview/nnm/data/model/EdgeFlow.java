package com.siteview.nnm.data.model;

public class EdgeFlow {
	public String alias;
	public String portId;
	public String leftid;
	public String rightid;
	public String leftport;
	public String rightport;
    public int portstate=1;
	public boolean isleft;
	public EdgeFlowData edgeFlowData;

	public float flowRate;
	public double outflow;
	public double inflow;
	public float flowPercent;
	public float pktsRate;
	public float broadcastRate;
	public float avgPktLen;

	public float speed;

	public EdgeFlow() {
		this.alias = "";
		this.flowRate = 0;
		this.outflow = 0;
		this.inflow = 0;
		this.flowPercent = 0;
		this.pktsRate = 0;
		this.broadcastRate = 0;
		this.avgPktLen = 0;
		this.isleft = true;
		this.portstate=1;
	}

	@Override
	public String toString() {
		return "portId:" + portId;
	}
}
