package com.siteview.NNM.Handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ToolItem;

public class DeviceInfoHandler extends AbstractHandler implements IHandler {

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
			if ( null == event ) {
			    return null;
			}

			// Class check.
			if ( ! ( event.getTrigger() instanceof Event ) ) {
			    return null;
			}

			Event eventWidget = (Event)event.getTrigger();
			if(eventWidget==null)
				return null;
			// Makes sure event came from a ToolItem.
//			if ( ! ( eventWidget.widget instanceof ToolItem ) ) {
//				try {
//				} catch (SiteviewException e) {
//					e.printStackTrace();
//				} catch (PartInitException e1) {
//					e1.printStackTrace();
//				}
//			}
			if(eventWidget.widget instanceof ToolItem){
				ToolItem toolItem = (ToolItem)eventWidget.widget;
		
				// Creates fake selection event.
				Event newEvent = new Event();
				newEvent.button = 1;
				newEvent.widget = toolItem;
				newEvent.detail = SWT.ARROW;
				newEvent.x = toolItem.getBounds().x;
				newEvent.y = toolItem.getBounds().y + toolItem.getBounds().height;
		
				// Dispatches the event.
				toolItem.notifyListeners( SWT.Selection, newEvent );
			}
			return null;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isHandled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

}
