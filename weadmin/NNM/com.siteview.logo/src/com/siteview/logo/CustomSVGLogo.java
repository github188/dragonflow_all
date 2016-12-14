package com.siteview.logo;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.widgets.WidgetUtil;
import org.eclipse.rap.rwt.remote.AbstractOperationHandler;
import org.eclipse.rap.rwt.remote.Connection;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

public class CustomSVGLogo extends Composite implements ICanvasLog{

	private static final long serialVersionUID = 1L;
	private static final String REMOTE_TYPE = "d3svglogo.SvgLogo";
	protected final RemoteObject remoteObject;
	public String svgcontent;
	String xml = "";

	public CustomSVGLogo(Composite parent, int style, String xml, int width, int height) {
		super(parent, style);
		Connection connection = RWT.getUISession().getConnection();
		remoteObject = connection.createRemoteObject(REMOTE_TYPE);
		remoteObject.setHandler(operationHandler);
		remoteObject.set("parent", WidgetUtil.getId(this));
		this.xml = xml;
		// load js..
		LogoResource.ensureJavaScriptResources();
		if(xml!=null){
			String nn = String.format(this.xml, width + "px", height + "px");
			remoteObject.set("svgcontent", nn);
		}
		else{
			this.setVisible(false);
		}
	}

	private final OperationHandler operationHandler = new AbstractOperationHandler() {

		private static final long serialVersionUID = 1L;

		@Override
		public void handleNotify(String eventName, JsonObject properties) {
			if ("Selection".equals(eventName)) {
				Event event = new Event();
				// event.item = items.get(event.index);
				notifyListeners(SWT.Selection, event);
			}
		}
	};
	int width, height;

	public void setWidth(int width) {
		checkWidget();
		if (this.width != width) {
			this.width = width;
			remoteObject.set("width", width);
		}
	}

	public void setHeight(int height) {
		checkWidget();
		if (this.height != height) {
			this.height = height;
			remoteObject.set("height", height);
		}
	}

}

