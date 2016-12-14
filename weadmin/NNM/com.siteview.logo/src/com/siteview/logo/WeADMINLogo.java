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

public class WeADMINLogo extends Composite implements ICanvasLog{

	private static final long serialVersionUID = 1L;
	private static final String REMOTE_TYPE = "d3svglogo.SvgLogo";
	protected final RemoteObject remoteObject;
	public String svgcontent;
	String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<!-- Generator: Adobe Illustrator 15.0.0, SVG Export Plug-In . SVG Version: 6.00 Build 0)  -->"
			+ "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">"
			+ "<svg version=\"1.1\" id=\"_1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" "
			+ "width=\"%s\" height=\"%s\" viewBox=\"0 0 248.131 203.377\" style=\"enable-background:new 0 0 248.131 203.377;\""
			+ "xml:space=\"preserve\">"
			+ "<radialGradient id=\"SVGID_1_\" cx=\"119.4146\" cy=\"80.6006\" r=\"50.8142\" gradientUnits=\"userSpaceOnUse\">"
			+ "<stop  offset=\"0\" style=\"stop-color:#00E6FA\"/>"
			+ "<stop  offset=\"0.3\" style=\"stop-color:#02BDFC\"/>"
			+ "<stop  offset=\"0.8\" style=\"stop-color:#0091FF\"/>"
			+ "<stop  offset=\"1\" style=\"stop-color:#007BFF\"/>"
			+ "</radialGradient>"
			+ "<path style=\"fill:url(#SVGID_1_);\" d=\"M160.507,72.171c0.683-2.229,1.068-4.579,1.068-7.016c0-13.924-11.934-25.213-26.662-25.213"
			+ "	c-13.346,0-24.373,9.288-26.321,21.398c-3.313-3.088-7.648-4.975-12.416-4.975c-9.134,0-16.739,6.882-18.432,16.001"
			+ "	C67.496,75.884,60.16,85.145,60.16,96.052c0,13.92,11.938,25.207,26.66,25.207c0.224,0,0.442-0.027,0.667-0.033v0.033h64.343v-0.009"
			+ "	c0.06,0,0.12,0.009,0.181,0.009c14.724,0,26.658-11.287,26.658-25.207C178.669,84.939,171.06,75.527,160.507,72.171z\"/>"
			+ "<path style=\"opacity:0.25;fill:#FFFFFF;\" d=\"M171.043,78.308c-2.892-2.751-6.443-4.872-10.418-6.137"
			+ "	c0.683-2.229,1.068-4.579,1.068-7.016c0-13.924-11.935-25.213-26.662-25.213c-13.346,0-24.372,9.288-26.32,21.398"
			+ "	c-3.312-3.088-7.648-4.975-12.416-4.975c-9.134,0-16.737,6.882-18.432,16.001c-2.275,0.781-4.398,1.86-6.337,3.164"
			+ "	c9.546,12.708,28.744,21.409,50.917,21.409C142.955,96.94,160.902,89.482,171.043,78.308z\"/>"
			+ "<radialGradient id=\"SVGID_2_\" cx=\"151.2036\" cy=\"109.6758\" r=\"27.7259\" fx=\"147.2458\" fy=\"101.195\" gradientUnits=\"userSpaceOnUse\">"
			+ "	<stop  offset=\"0\" style=\"stop-color:#C1FFA2\"/>"
			+ "	<stop  offset=\"0.1194\" style=\"stop-color:#B9FC98\"/>"
			+ "	<stop  offset=\"0.3192\" style=\"stop-color:#A3F57F\"/>"
			+ "	<stop  offset=\"0.575\" style=\"stop-color:#7FE854\"/>"
			+ "	<stop  offset=\"0.8738\" style=\"stop-color:#4FD81B\"/>"
			+ "	<stop  offset=\"1\" style=\"stop-color:#38D000\"/>"
			+ "</radialGradient>"
			+ "<path style=\"fill:url(#SVGID_2_);\" d=\"M170.972,126.147c5.603-4.536,9.11-10.977,9.11-18.123c0-13.737-12.928-24.871-28.879-24.871"
			+ "	c-15.95,0-28.878,11.134-28.878,24.871c0,13.738,12.928,24.873,28.878,24.873c4.973,0,9.652-1.084,13.738-2.993l9.309,6.295"
			+ "	L170.972,126.147z\"/>"
			+ "<path style=\"fill:#328619;stroke:#077036;stroke-width:0.25;stroke-miterlimit:10;\" d=\"M144.764,100.595"
			+ "	c0,2.02-1.633,3.652-3.651,3.652c-2.019,0-3.656-1.633-3.656-3.652c0-2.019,1.637-3.654,3.656-3.654"
			+ "	C143.13,96.94,144.764,98.576,144.764,100.595z\"/>"
			+ "<circle style=\"fill:#328619;stroke:#077036;stroke-width:0.25;stroke-miterlimit:10;\" cx=\"161.393\" cy=\"100.595\" r=\"3.654\"/>"
			+ "<path style=\"opacity:0.2;fill:#FCFBFA;\" d=\"M128.422,45.727c0,0,16.688-4.375,25.375,11.813"
			+ "	C153.797,57.539,143.485,45.352,128.422,45.727z\"/>"
			+ "<g>"
			+ "	<path style=\"fill:#04619B;\" d=\"M62.358,146.389h5.459l1.965,8.81l2.876-8.81h5.44l2.884,8.798l1.967-8.798h5.431l-4.101,15.735"
			+ "h-5.636l-3.262-9.907l-3.25,9.907h-5.636L62.358,146.389z\"/>"
			+ "<path style=\"fill:#04619B;\" d=\"M104.204,157.509H93.85c0.093,0.701,0.317,1.224,0.672,1.567c0.499,0.493,1.15,0.74,1.954,0.74"
			+ "		c0.507,0,0.99-0.107,1.446-0.322c0.279-0.136,0.579-0.375,0.901-0.719l5.088,0.397c-0.778,1.145-1.717,1.966-2.817,2.463"
			+ "s-2.677,0.746-4.732,0.746c-1.785,0-3.189-0.213-4.212-0.639s-1.872-1.102-2.544-2.028s-1.009-2.017-1.009-3.269"
			+ "c0-1.782,0.674-3.224,2.024-4.325c1.349-1.103,3.212-1.653,5.589-1.653c1.929,0,3.451,0.247,4.568,0.74"
			+ "c1.116,0.494,1.966,1.21,2.55,2.146c0.583,0.938,0.875,2.158,0.875,3.66V157.509z M98.951,155.416"
			+ "c-0.102-0.845-0.37-1.449-0.806-1.814c-0.436-0.364-1.009-0.547-1.719-0.547c-0.821,0-1.476,0.275-1.967,0.826"
			+ "c-0.313,0.344-0.512,0.855-0.596,1.535H98.951z\"/>"
			+ "<path style=\"fill:#04619B;\" d=\"M118.354,159.526h-6.525l-0.907,2.598h-5.869l6.991-15.735h6.27l6.989,15.735h-6.019"
			+ "		L118.354,159.526z M117.162,156.124l-2.053-5.656l-2.032,5.656H117.162z\"/>"
			+ "	<path style=\"fill:#04619B;\" d=\"M127.207,146.389h8.539c1.683,0,3.043,0.193,4.079,0.58s1.893,0.941,2.569,1.664"
			+ "s1.167,1.563,1.472,2.521c0.305,0.959,0.457,1.976,0.457,3.049c0,1.682-0.227,2.985-0.679,3.912"
			+ "c-0.453,0.927-1.081,1.703-1.884,2.329c-0.804,0.626-1.667,1.043-2.588,1.25c-1.261,0.287-2.402,0.43-3.426,0.43h-8.539V146.389z"
			+ "M132.955,149.952v8.598h1.408c1.201,0,2.056-0.112,2.563-0.338c0.508-0.226,0.905-0.619,1.193-1.181s0.432-1.473,0.432-2.731"
			+ "c0-1.668-0.322-2.809-0.964-3.424c-0.643-0.615-1.709-0.924-3.198-0.924H132.955z\"/>"
			+ "<path style=\"fill:#04619B;\" d=\"M147.279,146.389h7.559l2.915,9.574l2.894-9.574h7.555v15.735h-4.708v-12l-3.627,12h-4.261"
			+ "		l-3.621-12v12h-4.707V146.389z\"/>"
			+ "	<path style=\"fill:#04619B;\" d=\"M172.098,146.389h5.76v15.735h-5.76V146.389z\"/>"
			+ "	<path style=\"fill:#04619B;\" d=\"M182.02,146.389h5.367l7.003,8.706v-8.706h5.418v15.735h-5.418l-6.966-8.64v8.64h-5.405V146.389z\"/>"
			+ "</g>" + "</svg>";

	public WeADMINLogo(Composite parent, int style, int width, int height) {
		super(parent, style);
		Connection connection = RWT.getUISession().getConnection();
		remoteObject = connection.createRemoteObject(REMOTE_TYPE);
		remoteObject.setHandler(operationHandler);
		remoteObject.set("parent", WidgetUtil.getId(this));
		// load js..
		LogoResource.ensureJavaScriptResources();
		String nn = String.format(this.xml, width + "px", height + "px");

		remoteObject.set("svgcontent", nn);
	}

	private final OperationHandler operationHandler = new AbstractOperationHandler() {
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
