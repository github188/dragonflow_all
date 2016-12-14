package com.siteview.svgmap;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.widgets.WidgetUtil;
import org.eclipse.rap.rwt.remote.AbstractOperationHandler;
import org.eclipse.rap.rwt.remote.Connection;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.service.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.SWTEventListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public abstract class SvgMap extends Composite {

	private final List<ShapeItem> items;
	
	 protected final RemoteObject remoteObject;
	private final OperationHandler operationHandler = new AbstractOperationHandler() {
		@Override
		public void handleNotify(String eventName, JsonObject properties) {
			if ("Selection".equals(eventName)) {
				Event event = new Event();
				event.text = properties.get("index").asString();
				event.data =  properties.get("data");
				//event.item = items.get(event.index);
				notifyListeners(SWT.Selection, event);
			}
		}
	};

	public SvgMap(Composite parent, int style,String remoteType ) {
		super(parent, style);
		items = new LinkedList<ShapeItem>();
		// TODO Auto-generated constructor stub
		Connection connection = RWT.getUISession().getConnection();
		remoteObject = connection.createRemoteObject(remoteType);
		remoteObject.setHandler(operationHandler);
		remoteObject.set("parent", WidgetUtil.getId(this));
		// 加载相关资源js等
		SvgMapResources.ensureJavaScriptResources();
	}
	public ShapeItem[] getItems() {
	    checkWidget();
	    return items.toArray( new ShapeItem[ 0 ] );
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

	  void addItem( ShapeItem item ) {
	    items.add( item );
	  }

	  void removeItem( ShapeItem item ) {
	    items.remove( item );
	  }

	  String getRemoteId() {
	    return remoteObject.getId();
	  }


}
