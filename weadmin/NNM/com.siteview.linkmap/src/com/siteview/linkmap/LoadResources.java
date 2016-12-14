package com.siteview.linkmap;

import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Vector;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.JavaScriptLoader;
import org.eclipse.rap.rwt.service.ResourceLoader;
import org.eclipse.rap.rwt.service.ResourceManager;

/**
 * 
 * @author Administrator
 * 加载js 和 ico 等资源文件
 * 
 *
 */

public class LoadResources {
	//加载js
	private static final String[] CHART_JS_RESOURCES = new String[] {
	    "js/linksvg/BaseMap.js",
	    "js/linksvg/NodeItem.js",
	    "js/linksvg/LineItem.js",
	    "js/linksvg/node-list.js",
	    "js/linksvg/LinkMap.js"
	  
	  };
	//加载图标
	private static final String[] Imgs_RESOURCES = new String[] {
		"Router_Blue.ico",
		"Switch_Blue.ico",
		"Firewall_Blue.ico",
		"SwitchRouter_Blue.ico",
		"Server_Blue.ico",
		"PC_Blue.ico",
		"Other_Blue.ico",
		"Grouping_Blue.ico",
		"HUB_Blue.ico",
		"Group_Blue.bmp",
		"alertpic.gif"
		
	};
	  private static final ResourceLoader RESOURCE_LOADER = new ResourceLoader() {
	    public InputStream getResourceAsStream( String resourceName ) throws IOException {
	      return LoadResources.class.getClassLoader().getResourceAsStream( resourceName );
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
	                                "linkmap/linkmap.js",
	                                concatResources( RESOURCE_LOADER, CHART_JS_RESOURCES ) );
	      CSSLocation  = register( resourceManager,
                  "linkmap.css",
                  RESOURCE_LOADER.getResourceAsStream( "resources/linkmap.css" ));
	      
	      
	      
	      
	      //imgs
	      for( String fileName : Imgs_RESOURCES ) {
	          register( resourceManager, "linkmap/"+fileName,RESOURCE_LOADER.getResourceAsStream("js/linksvg/imgs/"+ fileName ) );
	        }
	      
	      
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
