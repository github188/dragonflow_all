package com.siteview.linkmap;

import java.util.LinkedList;
import java.util.List;

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

/**
 * 
 * @author Administrator
 *  拓扑基础组件
 *  拓扑由line和node组成
 *
 */

public abstract class BaseMap extends Composite {

	private final List<NodeItem> items;
	private final List<LineItem> lines;
	
	 protected final RemoteObject remoteObject;
	private final OperationHandler operationHandler = new AbstractOperationHandler() {
		@Override
		public void handleNotify(String eventName, JsonObject properties) {
			if ("Selection".equals(eventName)) {
				Event event = new Event();
				//link0 线路属性link1线路流量实时分析link2删除线路
				event.text = properties.get("index").asString();
				event.data =  properties.get("data");
				//event.item = items.get(event.index);
				try{
				notifyListeners(SWT.Selection, event);
				}catch(Exception ex){
					
				}
				
			}
		}
	};

	public BaseMap(Composite parent, int style,String remoteType ) {
		super(parent, style);
		items = new LinkedList<NodeItem>();
		lines = new LinkedList<LineItem>();
		// TODO Auto-generated constructor stub
		Connection connection = RWT.getUISession().getConnection();
		remoteObject = connection.createRemoteObject(remoteType);
		remoteObject.setHandler(operationHandler);
		remoteObject.set("parent", WidgetUtil.getId(this));
		// 加载相关资源js等
		LoadResources.ensureJavaScriptResources();
	}
	public NodeItem[] getItems() {
	    checkWidget();
	    return items.toArray( new NodeItem[ 0 ] );
	  }

	  @Override
	  public void dispose() {
	    super.dispose();
	    remoteObject.destroy();
	  }

	  @Override
	 /**
	  * 监听后台消息
	  */
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

	  void addItem( NodeItem item ) {
	    items.add( item );
	  }

	  void removeItem( NodeItem item ) {
	    items.remove( item );
	  }
	  
	  void addItem( LineItem item ) {
		  lines.add( item );
		  }

	  void removeItem( LineItem item ) {
		lines.remove( item );
		  }


	  String getRemoteId() {
	    return remoteObject.getId();
	  }


}
