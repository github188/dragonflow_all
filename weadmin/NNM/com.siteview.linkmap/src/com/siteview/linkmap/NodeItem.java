package com.siteview.linkmap;

import org.eclipse.rap.rwt.RWT;
//import org.eclipse.rap.rwt.internal.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Item;

/**
 * 
 * @author Administrator
 * svgtype 0 line node{ 1 switch 2 Routing 3 switch-rout 4 firewall 5 server 6 pc 7hub 99 group}
 * svid 唯一标示
 * nx  node x 坐标
 * ny  node y 坐标
 * localip ip地址
 * mac mac地址
 * devicename 设备名称
 * customname 自定义名称
 * factory    单位
 * model      类型
 *
 */

@SuppressWarnings("restriction")
public class NodeItem extends Item{

	//js obj name
	private static final String REMOTE_TYPE = "linkmap.NodeItem";
	private final RemoteObject remoteObject;
	private BaseMap baseMap;
	private int svgtype;
	private String svid;
	private float nx;
	private float ny;
	private String localip;
	private String mac;
	private String devicename;
	private String customname;
	private String factory;
	private String model;
	
	public NodeItem(BaseMap baseMap) {
		super(baseMap, SWT.NONE);
		this.baseMap=baseMap;
		baseMap.addItem(this);
		remoteObject = RWT.getUISession().getConnection()
				.createRemoteObject(REMOTE_TYPE);
		remoteObject.set("parent", baseMap.getRemoteId());
	}
	public String getSvid() {
		checkWidget();
		return svid;
	}
	public void setSvid(String svid) {
		checkWidget();
		if(this.svid!=svid)
		{
		this.svid = svid;
		remoteObject.set("svid",svid);
		}
	}
	
	public int getSvgtype() {
		checkWidget();
		return svgtype;
	}

	public void setSvgtype(int svgtype) {
		checkWidget();
		if(this.svgtype!=svgtype)
		{
		this.svgtype = svgtype;
		remoteObject.set("svgtype",svgtype);
		}
	}
	public float getNx() {
		checkWidget();
		return nx;
	}
	public void setNx(float nx) {
		checkWidget();
		if(this.nx != nx)
		{
		this.nx = nx;
		remoteObject.set("nx", nx);
		}
	}
	public float getNy() {
		checkWidget();
		return ny;
	}
	public void setNy(float ny) {
		checkWidget();
		if(this.ny != ny )
		{
		this.ny = ny;
		remoteObject.set("ny", ny);
		}
	}
	public String getLocalip() {
		checkWidget();
		return localip;
	}
	public void setLocalip(String localip) {
		checkWidget();
		if(this.localip!=localip)
		{
		this.localip = localip;
		remoteObject.set("localip", localip);
		}
	}
	public String getMac() {
		checkWidget();
		return mac;
	}
	public void setMac(String mac) {
		checkWidget();
		if(this.mac!=mac)
		{
		this.mac = mac;
		remoteObject.set("mac", mac);
		}
	}
	public String getDevicename() {
		checkWidget();
		return devicename;
	}
	public void setDevicename(String devicename) {
		checkWidget();
		if(this.devicename!=devicename)
		{
		this.devicename = devicename;
		remoteObject.set("name", devicename);
		}
	}
	public String getCustomname() {
		checkWidget();
		return customname;
	}
	public void setCustomname(String customname) {
		checkWidget();
		if(this.customname!=customname)
		{
		this.customname = customname;
		remoteObject.set("customname", customname);
		}
	}
	public String getFactory() {
		checkWidget();
		return factory;
	}
	public void setFactory(String factory) {
		checkWidget();
		if(this.factory!=factory)
		{
		this.factory = factory;
		remoteObject.set("factory", factory);
		}
	}
	public String getModel() {
		checkWidget();
		return model;
	}
	public void setModel(String model) {
		checkWidget();
		if(this.model!=model)
		{
		this.model = model;
		remoteObject.set("model", model);
		}
	}
	@Override
	public void setText(String text) {
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
