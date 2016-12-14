package com.siteview.flowdiagram;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
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
import org.osgi.framework.FrameworkUtil;



public class FlowDiagram extends Composite {

	/**
	 * svgcontent : svg file mdata : monitor data ip", "192.168.6.250"
	 * .add("mid", "1") .add("state", 1) .add("desc",  (state >100 is
	 * divice else is monitor 1ok2warning3error4stop) linkdata: subfile name
	 * state such as: test 101 (error 102 warning 101 ok 100)
	 */
	private static final long serialVersionUID = 1L;
	private static final String REMOTE_TYPE = "d3flowdiagram.FlowDiagram";
	protected final RemoteObject remoteObject;

	// js
	private String svgcontent;
	
	private float topy;// top
	private float leftx;// left
	private JsonObject[] odata;
	private  JsonObject[] tdata;
	
	
	private final OperationHandler operationHandler = new AbstractOperationHandler() {
		@Override
		public void handleNotify(String eventName, JsonObject properties) {
			if ("Selection".equals(eventName)) {
				Event event = new Event();
				event.text = properties.get("index").asString();
				event.data = properties.get("data");
				// event.item = items.get(event.index);
				notifyListeners(SWT.Selection, event);
			}
		}
	};

	public FlowDiagram(Composite parent, int style) {
		super(parent, style);
		Connection connection = RWT.getUISession().getConnection();
		remoteObject = connection.createRemoteObject(REMOTE_TYPE);
		remoteObject.setHandler(operationHandler);
		remoteObject.set("parent", WidgetUtil.getId(this));
		// load js..
		FlowDiagramResources.ensureJavaScriptResources();
	}

	@Override
	public void dispose() {
		super.dispose();
		remoteObject.destroy();
	}

	@Override
	public void addListener(int eventType, Listener listener) {
		boolean wasListening = isListening(SWT.Selection);
		super.addListener(eventType, listener);
		if (eventType == SWT.Selection && !wasListening) {
			remoteObject.listen("Selection", true);
		}
	}

	@Override
	public void removeListener(int eventType, Listener listener) {
		boolean wasListening = isListening(SWT.Selection);
		super.removeListener(eventType, listener);
		if (eventType == SWT.Selection && wasListening) {
			if (!isListening(SWT.Selection)) {
				remoteObject.listen("Selection", false);
			}
		}
	}

	@Override
	protected void removeListener(int eventType, SWTEventListener listener) {
		super.removeListener(eventType, listener);
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



	/**
	 * 
	 * @return
	 */
	public JsonObject[] getOdata() {
		checkWidget();
		return odata == null ? null : odata.clone();
	}
	/**
	 * 
	 * @param linkdata
	 */
	public void setOdata(JsonObject... odata){
		if (!Arrays.equals(this.odata, odata)) {
			this.odata = odata.clone();
			remoteObject.set("odata", jsonArray(odata));
		}	
	}
	/**
	 * 
	 * @return
	 */
	public JsonObject[] getTdata() {
		checkWidget();
		return tdata == null ? null : tdata.clone();
	}
	/**
	 * 
	 * @param linkdata
	 */
	public void setTdata(JsonObject... tdata){
		if (!Arrays.equals(this.tdata, tdata)) {
			this.tdata = tdata.clone();
			remoteObject.set("tdata", jsonArray(tdata));
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
	 * top
	 * 
	 * @param topy
	 */
	public void setTopy(float topy) {
		checkWidget();
		if (this.topy != topy) {
			this.topy = topy;
			remoteObject.set("topy", topy);
		}
	}
	/**
	 * left
	 * 
	 * @param leftx
	 */
	public void setLeftx(float leftx) {
		checkWidget();
		if (this.leftx != leftx) {
			this.leftx = leftx;
			remoteObject.set("leftx", leftx);
		}
	}
	public static String getProductPath(){

		try {
			return	FileLocator.toFileURL(
					FrameworkUtil.getBundle(FlowDiagram.class).getEntry("")).getPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		//return "";
		
	}
	public String readToString(File file, String encoding) {
		Long filelength = file.length();
		byte[] filecontent = new byte[filelength.intValue()];
		try {
			FileInputStream in = new FileInputStream(file);
			in.read(filecontent);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			return new String(filecontent, encoding);
		} catch (UnsupportedEncodingException e) {
			System.err.println("The OS does not support " + encoding);
			e.printStackTrace();
			return "";
		}
	}

	public void buildContent() {
		String path=getProductPath();
		File file=new File(path+"flowdiagram.svg");
        String vv=readToString(file,"utf-8");
        this.setSvgcontent(vv);
	}

	public void setNewSize(String newSize) {
		remoteObject.set("newSize", newSize);
	}
}
