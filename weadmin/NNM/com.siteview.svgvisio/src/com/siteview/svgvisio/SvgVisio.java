package com.siteview.svgvisio;


import java.util.Arrays;
import java.util.Map;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.widgets.WidgetUtil;
import org.eclipse.rap.rwt.remote.AbstractOperationHandler;
import org.eclipse.rap.rwt.remote.Connection;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.SWTEventListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class SvgVisio extends Composite {

	/**
	 * svgcontent : svg file
	 * mdata : monitor data ip", "192.168.6.250"
         		.add("mid", "1")
         		.add("state", 1)
         		.add("desc", "状态是ok
         		(state >100 is divice else is monitor 1ok2warning3error4stop)
	 * linkdata: subfile  name state such as: test 101 (error 102 warning 101 ok 100)
	 */
	private static final long serialVersionUID = 1L;
	private static final String REMOTE_TYPE = "d3svgvisio.SvgVisio";
	protected final RemoteObject remoteObject;
    
	//js
	private String svgcontent;
	private JsonObject[] mdata;
	private  JsonObject[] devdata;
	private JsonObject[] linkdata;
	private JsonObject[] linedata;
	private float topy;//组件top
	private float leftx;//组件left
	//java
	/**
	 *svg file path
	 */
	private String fpath; 
	/**
	 * svg file name
	 */
	private String svgname;
	public String getFpath() {
		return fpath;
	}
	public void setFpath(String fpath) {
		this.fpath = fpath;
	}
	public String getSvgname() {
		return svgname;
	}
	public void setSvgname(String svgname) {
		this.svgname = svgname;
		
//		buildContent();
	}
	/**
	 * ip list
	 */
	private String[] ips;
	/**
	 * line list (interface ..)
	 */
	private String[]  lines;
	/**
	 * url list 
	 */
	private String[]  links;
    private String[] groups;
	
	private String[] devices;
	
	public String[] getGroups() {
		return groups;
	}
	public void setGroups(String[] groups) {
		this.groups = groups;
	}
	public String[] getDevices() {
		return devices;
	}
	public void setDevices(String[] devices) {
		this.devices = devices;
	}
	public String[]  getIps() {
		return ips;
	}
	public void setIps(String[]  ips) {
		this.ips = ips;
	}
	public String[]  getLines() {
		return lines;
	}
	public void setLines(String[]  lines) {
		this.lines = lines;
	}
	public String[]  getLinks() {
		return links;
	}
	public void setLinks(String[]  links) {
		this.links = links;
	}
	private final OperationHandler operationHandler = new AbstractOperationHandler() {
		@Override
		public void handleNotify(String eventName, JsonObject properties) {
			if ("Selection".equals(eventName)) {
				Event event = new Event();
				event.text = properties.get("index").asString();
				event.data =  properties.get("data");
				// event.item = items.get(event.index);
				notifyListeners(SWT.Selection, event);
			}
		}
	};

	public SvgVisio(Composite parent, int style) {
		super(parent, style);
		Connection connection = RWT.getUISession().getConnection();
		remoteObject = connection.createRemoteObject(REMOTE_TYPE);
		remoteObject.setHandler(operationHandler);
		remoteObject.set("parent", WidgetUtil.getId(this));
		// load js..
		SvgVisioResources.ensureJavaScriptResources();
	}
	@Override
	  public void dispose() {
	    super.dispose();
	    remoteObject.destroy();
	  }

	  @Override
	  public void addListener( int eventType, Listener listener ) {
	    boolean wasListening = isListening( SWT.Selection );
	    super.addListener( eventType, listener );
	    if( eventType == SWT.Selection && !wasListening ) {
	      remoteObject.listen( "Selection", true );
	    }
	  }

	  @Override
	  public void removeListener( int eventType, Listener listener ) {
	    boolean wasListening = isListening( SWT.Selection );
	    super.removeListener( eventType, listener );
	    if( eventType == SWT.Selection && wasListening ) {
	      if( !isListening( SWT.Selection ) ) {
	        remoteObject.listen( "Selection", false );
	      }
	    }
	  }
	  @Override
	  protected void removeListener( int eventType, SWTEventListener listener ) {
	    super.removeListener( eventType, listener );
	  }
	  String getRemoteId() {
		    return remoteObject.getId();
		  }
	public String getSvgcontent() {
		checkWidget();
		return svgcontent;
	}
    /**
     * 
     * @param svgcontent
     */
	public void setSvgcontent(String svgcontent) {
		if (this.svgcontent != svgcontent) {
			this.svgcontent = svgcontent;
			remoteObject.set("svgcontent", svgcontent);
		}
	}
	public JsonObject[] getMdata() {
		checkWidget();
		return mdata == null ? null : mdata.clone();
	}

	/**
	 * 
	 * @param refreshdata
	 */
	public void setRefreshdata(JsonObject... mdata) {
		checkWidget();
		if (!Arrays.equals(this.mdata, mdata)) {
			this.mdata = mdata.clone();
			remoteObject.set("mdata", jsonArray(mdata));
		}
	}
	/**
	 * 
	 * @param refreshdata
	 */
	public void setDevdata(JsonObject... devdata) {
		checkWidget();
		if (!Arrays.equals(this.devdata, devdata)) {
			this.devdata = devdata.clone();
			remoteObject.set("devdata", jsonArray(devdata));
		}
	}
	/**
	 * 
	 * @return
	 */
	public JsonObject[] getLinkdata() {
		checkWidget();
		return linkdata == null ? null : linkdata.clone();
	}
	/**
	 * 
	 * @param linkdata
	 */
	public void setLinkdata(JsonObject... linkdata){
		if (!Arrays.equals(this.linkdata, linkdata)) {
			this.linkdata = linkdata.clone();
			remoteObject.set("linkdata", jsonArray(linkdata));
		}	
	}
	/**
	 * 
	 * @return
	 */
	public JsonObject[] getLinedata() {
		checkWidget();
		return linedata == null ? null : linedata.clone();
	}
	/**
	 * 
	 * @param linkdata
	 */
	public void setLinedata(JsonObject... linedata){
		if (!Arrays.equals(this.linedata, linedata)) {
			this.linedata = linedata.clone();
			remoteObject.set("linedata", jsonArray(linedata));
		}	
	}
	/**
	 * 
	 * @param values
	 * @return
	 */
	private static JsonArray jsonArray(JsonObject[] values) {
		// TODO use array.addAll in future versions
		JsonArray array = new JsonArray();
		for (int i = 0; i < values.length; i++) {
			array.add(values[i]);
		}
		return array;
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
	public void buildContent(){
		h2dbData db=new h2dbData(this.getFpath());
		Map<String,String> svgmap= db.getSvg(this.getSvgname());
		if(svgmap==null) return;
		this.setIps(svgmap.get("ips").split(","));
		this.setLinks(svgmap.get("links").split(","));
		this.setLines(svgmap.get("lines").split(","));
		this.setGroups(svgmap.get("groups").split(","));
		this.setDevices(svgmap.get("devices").split(","));
		this.setSvgcontent(svgmap.get("svg"));
		
		
	}
	
	public void setNewSize(String newSize) {
			remoteObject.set("newSize", newSize);
	}
}
