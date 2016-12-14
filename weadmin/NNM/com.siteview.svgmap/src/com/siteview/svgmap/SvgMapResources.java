package com.siteview.svgmap;

import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Vector;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.JavaScriptLoader;
import org.eclipse.rap.rwt.service.ResourceLoader;
import org.eclipse.rap.rwt.service.ResourceManager;

public class SvgMapResources {
	private static final String[] CHART_JS_RESOURCES = new String[] {
	    "js/svg/SvgMap.js",
	    "js/svg/item-list.js",
	    "js/svg/VisioMap.js",
	    "js/svg/ShapeItem.js"
	  
	  };
	private static final String[] Css_RESOURCES = new String[] {
		  "svg/main.css"
	};
	  private static final ResourceLoader RESOURCE_LOADER = new ResourceLoader() {
	    public InputStream getResourceAsStream( String resourceName ) throws IOException {
	      return SvgMapResources.class.getClassLoader().getResourceAsStream( resourceName );
	    }
	  };

	  static void ensureJavaScriptResources() {
	    String d3Location = null;
	    String chartLocation = null;
	    String CSSLocation = null;
	    ResourceManager resourceManager = RWT.getApplicationContext().getResourceManager();
	    try {
	      // TODO register resources only once
	      d3Location = register( resourceManager,
	                             "lib/d3.v3.min.js",
	                             RESOURCE_LOADER.getResourceAsStream( "resources/d3.v3.min.js" ) );
	      chartLocation = register( resourceManager,
	                                "d3svgmap/d3svgmap.js",
	                                concatResources( RESOURCE_LOADER, CHART_JS_RESOURCES ) );
	      CSSLocation  = register( resourceManager,
                  "main.css",
                  RESOURCE_LOADER.getResourceAsStream( "resources/main.css" ));
	    } catch( IOException exception ) {
	      throw new RuntimeException( "Failed to register resource", exception );
	    }
	    JavaScriptLoader loader = RWT.getClient().getService( JavaScriptLoader.class );
	    loader.require( d3Location );
	    loader.require( chartLocation + "?nocache=" + System.currentTimeMillis() );
	  }

	  private static String register( ResourceManager resourceManager,
	                                  String registerPath,
	                                  InputStream inputStream ) throws IOException
	  {
	    String location;
	    try {
	      resourceManager.register( registerPath, inputStream );
	      location = resourceManager.getLocation( registerPath );
	    } finally {
	      inputStream.close();
	    }
	    return location;
	  }

	  private static InputStream concatResources( ResourceLoader loader, String... resourceNames )
	    throws IOException
	  {
	    Vector<InputStream> inputStreams = new Vector<InputStream>( resourceNames.length );
	    for( String resourceName : resourceNames ) {
	      inputStreams.add( loader.getResourceAsStream( resourceName ) );
	    }
	    return new SequenceInputStream( inputStreams.elements() );
	  }

}
