package com.siteview.mddesignflow;

import java.util.Arrays;
import java.util.List;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.remote.AbstractOperationHandler;
import org.eclipse.rap.rwt.remote.Connection;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.remote.RemoteObject;
//import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.widgets.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.SWTEventListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class MdFlowDesign extends Composite {

	private int vv;//
	private String label;
	private int nwidth = 200;
	private int nheight = 30;
	private String flowv = "";
	private int showstate = 0;
	private int saveresult = 0;
	private JsonObject[] userdata;
	private JsonObject[] statedata;
	private JsonObject[] businessobjects;// List of Business Objects
	private JsonObject[] resultdata;
	private String[] imagesdata;
	private static final long serialVersionUID = 1L;
	private static final String REMOTE_TYPE = "mermaidjs.MdFlowDesign";
	protected final RemoteObject remoteObject;
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

	public MdFlowDesign(Composite parent, int style) {
		super(parent, style);
		Connection connection = RWT.getUISession().getConnection();
		remoteObject = connection.createRemoteObject(REMOTE_TYPE);
		remoteObject.setHandler(operationHandler);
		remoteObject.set("parent", WidgetUtil.getId(this));
		// load js..
		mdflowdesignResource.ensureJavaScriptResources();
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

	public int getVv() {
		checkWidget();
		return vv;
	}

	public void setVv(int vv) {
		checkWidget();
		if (this.vv != vv) {
			this.vv = vv;
			remoteObject.set("vv", vv);
		}
	}

	public void setShowstate(int showstate) {
		if (this.showstate != showstate) {
			this.showstate = showstate;
			remoteObject.set("showstate", showstate);
		}
	}

	// saveresult
	public void setSaveresult(int saveresult) {
		// if(this.saveresult!=saveresult){
		this.saveresult = saveresult;
		remoteObject.set("saveresult", saveresult);
		// }
	}

	public int getNwidth() {
		checkWidget();
		return nwidth;
	}

	public void setNwidth(int nwidth) {
		checkWidget();
		if (this.nwidth != nwidth) {
			this.nwidth = nwidth;
			remoteObject.set("width", nwidth);
		}
	}

	public int getNheight() {
		checkWidget();
		return nheight;
	}

	public void setNheight(int nheight) {
		checkWidget();
		if (this.nheight != nheight) {
			this.nheight = nheight;
			remoteObject.set("height", nheight);
		}
	}

	public String getLabel() {
		checkWidget();
		return label;
	}

	public void setLabel(String label) {
		checkWidget();
		if (this.label != label) {
			this.label = label;
			remoteObject.set("label", label);
		}
	}

	public String getFlowv() {
		checkWidget();
		return flowv;
	}

	public void setFlowv(String flowv) {
		checkWidget();
		if (this.flowv != flowv) {
			this.flowv = flowv;
			remoteObject.set("flowv", flowv);
		}
	}

	public JsonObject[] getUserdata() {
		checkWidget();
		return userdata == null ? null : userdata.clone();
	}

	/**
	 * 
	 * @param refreshdata
	 */
	public void setUserdata(JsonObject... userdata) {
		checkWidget();
		if (!Arrays.equals(this.userdata, userdata)) {
			this.userdata = userdata.clone();
			remoteObject.set("userdata", jsonArray(userdata));
		}
	}

	public JsonObject[] getBusinessobjects() {
		checkWidget();
		return businessobjects == null ? null : businessobjects.clone();
	}

	/**
	 * 
	 * @param List
	 *            of Business Objects
	 */
	public void setBusinessobjects(JsonObject... businessobjects) {
		checkWidget();
		if (!Arrays.equals(this.businessobjects, businessobjects)) {
			this.businessobjects = businessobjects.clone();
			remoteObject.set("businessobjects", objectsjsonArray(businessobjects));
		}
	}

	public JsonObject[] getStatedata() {
		checkWidget();
		return statedata == null ? null : statedata.clone();
	}

	/**
	 * 
	 * @param
	 */
	public void setStatedata(JsonObject... statedata) {
		checkWidget();
		if (!Arrays.equals(this.statedata, statedata)) {
			this.statedata = statedata.clone();
			remoteObject.set("statedata", jsonArray(statedata));
		}
	}

	public JsonObject[] getResultdata() {
		checkWidget();
		return resultdata == null ? null : resultdata.clone();
	}

	/**
	 * 
	 * @param
	 */
	public void setResultdata(JsonObject... resultdata) {
		checkWidget();
		if (!Arrays.equals(this.resultdata, resultdata)) {
			this.resultdata = resultdata.clone();
			remoteObject.set("resultdata", jsonArray(resultdata));
		}
	}

	/**
	 * 
	 * @param values
	 * @return
	 */
	private static JsonArray objectsjsonArray(JsonObject[] values) {
		// TODO use array.addAll in future versions
		JsonArray array = new JsonArray();
		List imgss = Arrays.asList(mdflowdesignResource.Imgs_RESOURCES);
		for (int i = 0; i < values.length; i++) {
			JsonObject obj = values[i];
			String nname = obj.get("name").asString();
			if (imgss.contains(nname + ".png")) {
				obj.set("image1", 1);
			} else {
				obj.set("image1", 0);
			}
			array.add(obj);
		}
		return array;
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

}
