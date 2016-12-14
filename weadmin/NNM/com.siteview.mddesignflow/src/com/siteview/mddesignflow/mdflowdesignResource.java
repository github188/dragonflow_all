package com.siteview.mddesignflow;

import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Vector;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.JavaScriptLoader;
import org.eclipse.rap.rwt.service.ResourceLoader;
import org.eclipse.rap.rwt.service.ResourceManager;


public class mdflowdesignResource {

	public mdflowdesignResource() {
		// TODO Auto-generated constructor stub
	}


	
	private static final String[] CHART_JS_RESOURCES = new String[] {
	    "mdjs/mdflowdesign/MdFlowDesign.js"
	    
	  };
	//load image
		public static final String[] Imgs_RESOURCES = new String[] {
			"总服务台.png",
			"总服务台-1.png",
			"服务台.png",
			"服务台-1.png",
			"分服务台.png",
			"分服务台-1.png",
			"工程师.png",
			"工程师-1.png",
			"备件.png",
			"备件-1.png",
			"一线技术支持.png",
			"一线技术支持-1.png",
			"二线技术支持.png",
			"二线技术支持-1.png",
			"电话回访-1.png",
			"电话回访.png",
			"监理抽查.png",
			"监理抽查-1.png",
			"现场满意度调查.png",
			"现场满意度调查-1.png",
			"视频监控管理人员.png",
			"视频监控管理人员-1.png",
			"派出所监控管理员.png",
			"派出所监控管理员-1.png",
			"管理员.png",
			"管理员-1.png",
			"运维人员.png",
			"运维人员-1.png",
			"知识库.png",
			"知识库-1.png",
			"关闭.png",
			"关闭-1.png",
			"default.png",
			"default-1.png",
			"bg.gif",
			"arrow.gif",
			"arrowon.gif"
			
			
		};
	  private static final ResourceLoader RESOURCE_LOADER = new ResourceLoader() {
	    public InputStream getResourceAsStream( String resourceName ) throws IOException {
	      return mdflowdesignResource.class.getClassLoader().getResourceAsStream( resourceName );
	    }
	  };

	  static void ensureJavaScriptResources() {
	    String d3Location = null,d3Location1=null;
	    
	    String chartLocation = null;
	   // String otherLocation=null;
	    String CSSLocation = null;
	    ResourceManager resourceManager = RWT.getApplicationContext().getResourceManager();
	    try {
	      // TODO register resources only once
	    	 CSSLocation  = register( resourceManager,
	                  "mermaid.css",
	                  RESOURCE_LOADER.getResourceAsStream( "mdflowdesignresources/mermaid.css" ));
	      d3Location = register( resourceManager,
	                             "mermaid.js",
	                             RESOURCE_LOADER.getResourceAsStream( "mdflowdesignresources/mermaid.min.js" ) );
//	      d3Location1 = register( resourceManager,
//                  "ModalPopupWindow.js",
//                  RESOURCE_LOADER.getResourceAsStream( "mdflowdesignresources/ModalPopupWindow.js" ) );
//	      
	      chartLocation = register( resourceManager,
	                                "mdflowdesign.js",
	                                concatResources( RESOURCE_LOADER, CHART_JS_RESOURCES ) );
	     
	      
	      
	      
	      //imgs
	      for( String fileName : Imgs_RESOURCES ) {
	          register( resourceManager, "mdflowdesign/"+fileName,RESOURCE_LOADER.getResourceAsStream("mdflowdesignresources/"+ fileName ) );
	        }
	      
	    } catch( IOException exception ) {
	      throw new RuntimeException( "Failed to register resource", exception );
	    }
	    JavaScriptLoader loader = RWT.getClient().getService( JavaScriptLoader.class );
	    loader.require( chartLocation + "?nocache=" + System.currentTimeMillis() );
	    loader.require( d3Location );
	   // loader.require( d3Location1 );
	    
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
