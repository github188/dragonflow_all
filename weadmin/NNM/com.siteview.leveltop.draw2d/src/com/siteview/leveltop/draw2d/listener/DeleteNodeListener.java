package com.siteview.leveltop.draw2d.listener;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.weadmin.mxgraph_rap.GraphJS;

public class DeleteNodeListener extends SelectionAdapter{
	
	private static final long serialVersionUID = 1L;
	
	private GraphJS canvas;
	
	public DeleteNodeListener(){

	}
	
	public DeleteNodeListener(GraphJS canvas){
		this.canvas = canvas;
	}
	
	public void setCanvas(GraphJS canvas){
		this.canvas = canvas;
	}
	
	@Override
	public void widgetSelected(SelectionEvent e) {
		canvas.removeCells();
	}
}
