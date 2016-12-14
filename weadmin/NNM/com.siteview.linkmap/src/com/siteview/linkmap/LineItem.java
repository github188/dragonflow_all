package com.siteview.linkmap;

import org.eclipse.rap.rwt.RWT;
//import org.eclipse.rap.rwt.internal.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Item;
/**
 * 
 * @author Administrator
 * line 实现
 * lid 线唯一标示
 * sinterface 原接口
 * tinterface 目标接口
 * flow 流量
 * pkts 帧流量
 * broadcast 
 * bwusage 带宽占用比
 * avgframeLen 平均帧长度
 * error 错误阀值
 * warn  危险阀值
 * 
 */
public class LineItem extends Item{

	private static final String REMOTE_TYPE = "linkmap.LineItem";
	private final RemoteObject remoteObject;
    private final BaseMap baseMap;
	private String lid;
	private String lsource;//原点
	private String ltarget;//目标
	private String sinterface;
	private String tinterface;
	private float flow;
	private float pkts;
	private float broadcast;
	private float bwusage;
	private float avgframeLen;
	private float error;
	private float warn;

	public LineItem(BaseMap baseMap) {
		super(baseMap, SWT.NONE);
		this.baseMap=baseMap;
		baseMap.addItem(this);
		remoteObject = RWT.getUISession().getConnection()
				.createRemoteObject(REMOTE_TYPE);
		remoteObject.set("parent", baseMap.getRemoteId());
	}
	public String getLid() {
		checkWidget();
		return lid;
	}
	public void setLid(String lid) {
		checkWidget();
		if(this.lid!=lid)
		{
		this.lid = lid;
		remoteObject.set("lid",lid);
		}
	}
	public String getLsource() {
		checkWidget();
		return lsource;
	}
	public void setLsource(String lsource) {
		checkWidget();
		if(this.lsource!=lsource)
		{
		this.lsource = lsource;
		remoteObject.set("lsource",lsource);
		}
	}
	public String getLtarget() {
		checkWidget();
		return ltarget;
	}
	public void setLtarget(String ltarget) {
		checkWidget();
		if(this.ltarget!=ltarget)
		{
		this.ltarget = ltarget;
		remoteObject.set("ltarget",ltarget);
		}
	}
	public String getSinterface() {
		checkWidget();
		return sinterface;
	}

	public void setSinterface(String sinterface) {
		checkWidget();
		if(this.sinterface!=sinterface)
		{
		this.sinterface = sinterface;
		remoteObject.set("sinterface",sinterface);
		}
	}

	public String getTinterface() {
		checkWidget();
		return tinterface;
	}

	public void setTinterface(String tinterface) {
		checkWidget();
		if(this.tinterface!=tinterface)
		{
		this.tinterface = tinterface;
		remoteObject.set("tinterface",tinterface);
		}
	}

	public float getFlow() {
		checkWidget();
		return flow;
	}

	public void setFlow(float flow) {
		checkWidget();
		if(this.flow!=flow)
		{
		this.flow = flow;
		remoteObject.set("flow",flow);
		}
	}

	public float getPkts() {
		checkWidget();
		return pkts;
	}

	public void setPkts(float pkts) {
		checkWidget();
		if(this.pkts!=pkts)
		{
		this.pkts = pkts;
		remoteObject.set("pkts",pkts);
		}
	}

	public float getBroadcast() {
		checkWidget();
		return broadcast;
	}

	public void setBroadcast(float broadcast) {
		checkWidget();
		if(this.broadcast!=broadcast)
		{
		this.broadcast = broadcast;
		remoteObject.set("broadcast",broadcast);
		}
	}

	public float getBwusage() {
		checkWidget();
		return bwusage;
	}

	public void setBwusage(float bwusage) {
		checkWidget();
		if(this.bwusage!=bwusage)
		{
		this.bwusage = bwusage;
		remoteObject.set("bwusage",bwusage);
		}
	}

	public float getAvgframeLen() {
		checkWidget();
		return avgframeLen;
	}

	public void setAvgframeLen(float avgframeLen) {
		checkWidget();
		if(this.avgframeLen!=avgframeLen)
		{
		this.avgframeLen = avgframeLen;
		remoteObject.set("avgframeLen",avgframeLen);
		}
	}

	public float getError() {
		checkWidget();
		return error;
	}

	public void setError(float error) {
		checkWidget();
		if(this.error!=error)
		{
		this.error = error;
		remoteObject.set("error",error);
		}
	}

	public float getWarn() {
		checkWidget();
		return warn;
	}

	public void setWarn(float warn) {
		checkWidget();
		if(this.warn!=warn)
		{
		this.warn = warn;
		remoteObject.set("warn",warn);
		}
	}

	@Override
	public void setText(String text) {
		//checkWidget();
		super.setText(text);
		remoteObject.set("text", text);
	}

	@Override
	public void dispose() {
		super.dispose();
		getBaseMap().removeItem(this);
		remoteObject.destroy();
	}

	private BaseMap getBaseMap() {
		return baseMap;
		//return (BaseMap) getAdapter(WidgetAdapter.class).getParent();
	}

}

