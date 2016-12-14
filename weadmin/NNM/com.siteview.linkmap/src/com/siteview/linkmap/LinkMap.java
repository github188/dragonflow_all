package com.siteview.linkmap;

import java.util.Arrays;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.swt.widgets.Composite;

public class LinkMap extends BaseMap{

	
	private static final String REMOTE_TYPE = "linkmap.LinkMap";
	
	private String nodetooltipdesc;
	private String grouptooltipdesc;

	private String linktooltipdesc;
	private String menudesc;


	private JsonObject[] refreshdata;
	private JsonObject[] layoutdata;
	
	private JsonObject plinedata;//线端口改变
	private JsonObject pnodedata;
	

	

	private float maxX;//最大x坐标 调整大小
	private float maxY;//最大y坐标 调整大小
	private float compWidth;//组件的宽
	private float compHeight;//组件的高
	private int linkDisplayType;//线路显示类型
	private int nodeDisplayType;//node 显示类型
	private float topy;//组件top
	private boolean render;//重新布局
	private boolean cleardata;//清除数据

	

	private float leftx;//组件left
	
	public LinkMap(Composite parent, int style) {
		super(parent,style, REMOTE_TYPE);
		// TODO Auto-generated constructor stub
		//初始化
		
		String mdesc ="node$ip$name$model$custom:link$flow$pkts$broadcast$bwusage:scale size$zoom in$zoom out$original size:layout$layout1$layout2$layout3";
		mdesc="设备标注$IP 地址$设备名称$设备型号$自定义名称:线路标注$总流量$帧流量$广播量$带宽占用比";
		//"调整大小$放大$缩小$原始大小$合适大小:重绘$环形$普通$直角:保存拓扑图:添加设备";
		this.setMenudesc(mdesc);	
	}
	
	
	public int getLinkDisplayType() {
		checkWidget();
		return linkDisplayType;
	}
	/**
	 * link 显示类型
	 * 1 flow 2 pkts 3 broadcast 4 bwusage
	 * @return
	 */
	public void setLinkDisplayType(int linkDisplayType) {
		checkWidget();
		if(this.linkDisplayType!=linkDisplayType){
		this.linkDisplayType = linkDisplayType;
		remoteObject.set( "linkDisplayType", linkDisplayType );
		}
	}
	public int getNodeDisplayType() {
		checkWidget();
		return nodeDisplayType;
	}
	/**
	 * node 的显示类型
	 * 1 ip 2 name 3 model 4 customername
	 * @param nodeDisplayType
	 */
	public void setNodeDisplayType(int nodeDisplayType) {
		checkWidget();
		if(this.nodeDisplayType!=nodeDisplayType){
		this.nodeDisplayType = nodeDisplayType;
		remoteObject.set( "nodeDisplayType", nodeDisplayType );
		}
	}
	/**
	 * 最大x坐标，调整滚动条相关 
	 */
	public void setMaxX(float maxX) {
		checkWidget();
		if(this.maxX!=maxX){
		this.maxX = maxX;
		remoteObject.set( "maxX", maxX );
		}
	}
    /**
     * 最大y坐标，调整滚动条相关 
     * @param maxY
     */
	public void setMaxY(float maxY) {
		checkWidget();
		if(this.maxY!=maxY){
		this.maxY = maxY;
		remoteObject.set("maxY",maxY);
		}
	}
	/**
	 * 组件top
	 * @param topy
	 */
	public void setTopy(float topy) {
		checkWidget();
		if(this.topy != topy)
		{
		this.topy = topy;
		remoteObject.set("topy",topy);
		}
	}

    /**
     * 组件left
     * @param leftx
     */
	public void setLeftx(float leftx) {
		checkWidget();
		if(this.leftx != leftx)
		{
		this.leftx = leftx;
		remoteObject.set("leftx",leftx);
		}
	}
   /**
    * 重新布局	
    * @param render
    */
	public void setRender(boolean render) {
		checkWidget();
		if(this.render!=render)
		{
		this.render = render;
		remoteObject.set("render",render);
		}
	}
	
 /**
 *  清除所有数据
 * @param cleardata
 */
	public void setCleardata(boolean cleardata) {
		checkWidget();
		if(this.cleardata!=cleardata)
		{
		this.cleardata = cleardata;
		remoteObject.set("cleardata",cleardata);
		}
	}

	/**
	 * 组件宽
	 * @param compWidth
	 */
	public void setCompWidth(float compWidth) {
		checkWidget();
		if(this.compWidth!=compWidth){
		this.compWidth = compWidth;
		remoteObject.set("compWidth",compWidth);
		}
	}

	/**
	 * 组件高
	 * @param compHeight
	 */
	public void setCompHeight(float compHeight) {
		checkWidget();
		if(this.compHeight!=compHeight){
		this.compHeight = compHeight;
		remoteObject.set("compHeight",compHeight);
		}
	}
	
   /**
    * node 描述中文
    * 
    */
	private void setNodetooltipdesc(String nodetooltipdesc) {
		checkWidget();
		if(this.nodetooltipdesc!=nodetooltipdesc)
		{
		this.nodetooltipdesc = nodetooltipdesc;
		remoteObject.set( "nodetooltipdesc", nodetooltipdesc );
		}
	}
	/**
	 * 
	 * @param linktooltipdesc
	 * link 描述中文
	 * 
	 */
	private void setLinktooltipdesc(String linktooltipdesc) {
		checkWidget();
		if(this.linktooltipdesc!=linktooltipdesc)
		{
		this.linktooltipdesc = linktooltipdesc;
		remoteObject.set( "linktooltipdesc", linktooltipdesc );
		}
	}
	/**
	 * 子图描述中文
	 * @param grouptooltipdesc
	 */
	public void setGrouptooltipdesc(String grouptooltipdesc) {
		checkWidget();
		if(this.grouptooltipdesc!=grouptooltipdesc){
		this.grouptooltipdesc = grouptooltipdesc;
		remoteObject.set( "grouptooltipdesc", grouptooltipdesc );
		}
	}

	/**
	 *  主菜单 菜单描述
	 * @param menudesc
	 */
	private void setMenudesc(String menudesc) {
		checkWidget();
		if(this.menudesc!=menudesc)
		{
		this.menudesc = menudesc;
		remoteObject.set( "menudesc", menudesc );
		}
	}

	/**
	 * 
	 * @param plinedata
	 */
	public void setPlinedata(JsonObject plinedata) {
		checkWidget();
		//if(this.newlinedata!=newlinedata){
		this.plinedata = plinedata;
		remoteObject.set( "plinedata", plinedata );
		//}
	}
	/**
	 * 
	 * @param pnodedata
	 */
	public void setPnodedata(JsonObject pnodedata) {
		checkWidget();
		//if(this.newlinedata!=newlinedata){
		this.pnodedata = pnodedata;
		remoteObject.set( "pnodedata", pnodedata );
		//}
	}

	public JsonObject[] getLayoutdata() {
		checkWidget();
		return layoutdata == null ? null : layoutdata.clone();
	}
   
	/**
	 * 设置排版坐标数据
	 * @param layoutdata
	 */
	public void setLayoutdata(JsonObject... layoutdata) {
		checkWidget();
		if (!Arrays.equals(this.layoutdata, layoutdata)) {
			this.layoutdata = layoutdata.clone();
			remoteObject.set("layoutdata", jsonArray(layoutdata));
		}
	}
	public JsonObject[] getRefreshdata() {
		checkWidget();
		return refreshdata == null ? null : refreshdata.clone();
	}

	/**
	 * 定时刷新数据
	 * @param refreshdata
	 */
	public void setRefreshdata(JsonObject... refreshdata) {
		checkWidget();
		if (!Arrays.equals(this.refreshdata, refreshdata)) {
			this.refreshdata = refreshdata.clone();
			remoteObject.set("refreshdata", jsonArray(refreshdata));
		}
	}
	
	private static JsonArray jsonArray(JsonObject[] values) {
		// TODO use array.addAll in future versions
		JsonArray array = new JsonArray();
		for (int i = 0; i < values.length; i++) {
			array.add(values[i]);
		}
		return array;
	}


}
