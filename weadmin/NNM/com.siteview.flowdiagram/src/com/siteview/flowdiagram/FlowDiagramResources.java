package com.siteview.flowdiagram;

import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Vector;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.JavaScriptLoader;
import org.eclipse.rap.rwt.service.ResourceLoader;
import org.eclipse.rap.rwt.service.ResourceManager;

public class FlowDiagramResources {

	public FlowDiagramResources() {
		// TODO Auto-generated constructor stub
	}

	private static final String[] CHART_JS_RESOURCES = new String[] { "js/flowdiagram/FlowDiagram.js" };
	// load image
	private static final String[] Imgs_RESOURCES = new String[] {
			"state_green.gif", 
			"state_yellow.gif",
			"state_red.gif",
			"state_stop.gif"

	};
	private static final ResourceLoader RESOURCE_LOADER = new ResourceLoader() {
		public InputStream getResourceAsStream(String resourceName)
				throws IOException {
			return FlowDiagramResources.class.getClassLoader()
					.getResourceAsStream(resourceName);
		}
	};

	static void ensureJavaScriptResources() {
		String d3Location = null;
		String chartLocation = null;
		//String CSSLocation = null;
		ResourceManager resourceManager = RWT.getApplicationContext()
				.getResourceManager();
		try {
			// TODO register resources only once
			d3Location = register(resourceManager, "lib/d3.v3.min.js",
					RESOURCE_LOADER
							.getResourceAsStream("resources/d3.v3.min.js"));
			chartLocation = register(resourceManager,
					"d3flowdiagram/d3flowdiagram.js",
					concatResources(RESOURCE_LOADER, CHART_JS_RESOURCES));
//			CSSLocation = register(resourceManager, "svgdiagram.css",
//					RESOURCE_LOADER
//							.getResourceAsStream("resources/svgdiagram.css"));

			// imgs
			for (String fileName : Imgs_RESOURCES) {
				register(resourceManager, "d3flowdiagram/" + fileName,
						RESOURCE_LOADER
								.getResourceAsStream("js/flowdiagram/images/"
										+ fileName));
			}

		} catch (IOException exception) {
			throw new RuntimeException("Failed to register resource", exception);
		}
		JavaScriptLoader loader = RWT.getClient().getService(
				JavaScriptLoader.class);
		loader.require(d3Location);
		loader.require(chartLocation + "?nocache=" + System.currentTimeMillis());
	}

	private static String register(ResourceManager resourceManager,
			String registerPath, InputStream inputStream) throws IOException {
		String location;
		try {
			resourceManager.register(registerPath, inputStream);
			location = resourceManager.getLocation(registerPath);
		} finally {
			inputStream.close();
		}
		return location;
	}

	private static InputStream concatResources(ResourceLoader loader,
			String... resourceNames) throws IOException {
		Vector<InputStream> inputStreams = new Vector<InputStream>(
				resourceNames.length);
		for (String resourceName : resourceNames) {
			inputStreams.add(loader.getResourceAsStream(resourceName));
		}
		return new SequenceInputStream(inputStreams.elements());
	}

}
