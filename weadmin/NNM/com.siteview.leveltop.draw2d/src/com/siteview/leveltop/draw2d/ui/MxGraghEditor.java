package com.siteview.leveltop.draw2d.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.siteview.ecc.main.ui.cnftree.MonitorTreeViews;
import com.siteview.leveltop.draw2d.nls.Message;
import com.siteview.leveltop.draw2d.pojo.LevelTopCustomEvent;
import com.siteview.utils.entities.TreeEquipmentObject;
import com.siteview.utils.entities.TreeGroupObject;
import com.siteview.utils.entities.TreeObject;

import Siteview.SiteviewException;
import siteview.forms.common.VariableRunnable;

public class MxGraghEditor extends EditorPart {
	
	public static final String ID = "com.siteview.leveltop.draw2d.ui.MxGraghEditor";

	private boolean isDirty = false;

	private Map<LevelTopCustomEvent,VariableRunnable> eventMap;

	private LevelTopComposite content;
	
	@Override
	public void doSave(IProgressMonitor monitor) {
		try {
			content.saveTopoMapInfo();
		} catch (SiteviewException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doSaveAs() {

	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.setSite(site);
		super.setInput(input);
	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		content = new LevelTopComposite(parent, SWT.NONE);
		eventMap = new ConcurrentHashMap<LevelTopCustomEvent,VariableRunnable>(); 
		eventMap.put(LevelTopCustomEvent.DIRTY,new VariableRunnable() {
			@Override
			public void run(Object... params) {
				if(params!=null&&params.length>0&&params[0]!=null&&params[0] instanceof Boolean){
					setDirty((Boolean)params[0]);
				}
				
			}
		});
		eventMap.put(LevelTopCustomEvent.SAVE,new VariableRunnable() {
			
			@Override
			public void run(Object... params) {
				doSave(null);
			}
		});
		content.setEventMap(eventMap);
		System.out.println("2");
	}

	@Override
	public void setFocus() {
		if(content!=null){
			ISelection selection = MonitorTreeViews.getMonitorTreeViews().getCommonViewer().getSelection();
			if(selection!=null){
				IStructuredSelection iss = (IStructuredSelection)selection;
				Object obj = iss.getFirstElement();
				String[] strArray = null;
				if(obj!=null){
					if(obj instanceof TreeEquipmentObject){
						TreeEquipmentObject tro = (TreeEquipmentObject)obj;
						strArray = new String[]{tro.getId()};
					}
					else if(obj instanceof TreeGroupObject){
						TreeGroupObject tro = (TreeGroupObject)obj;
						List<TreeObject> childrens = tro.getChildren();
						if(childrens.size()>0){
							List<String> ids = new ArrayList<String>();
							for(TreeObject to:childrens){
								if(to instanceof TreeEquipmentObject){
									ids.add(((TreeEquipmentObject)to).getId());
								}
							}
							if(ids.size()>0){
								strArray = new String[ids.size()];
								ids.toArray(strArray);
								
							}
						}
					}
					if(strArray!=null&&strArray.length>0){
						final String[] result = strArray;
						content.setMessage(Message.get().LOAD_RESOURCE_TOPO_SELECT_NODE);
						Display.getCurrent().timerExec(3000,new Runnable() {
							@Override
							public void run() {
								content.setMessage(Message.get().LOAD_RESOURCE_TOPO_SELECT_NODE);
								selectMxNode(result);
								content.setMessage("");
							}
						});
					}
				}
			}
		}
	}
	
	public void setDirty(boolean dirty){
		if(this.isDirty!=dirty){
			this.isDirty = dirty;
			this.firePropertyChange(PROP_DIRTY);
		}
	}
	
	public void refresh(){
		content.synchResourceStatus();
	}
	
	public void selectMxNode(String... ids){
		content.selectMxNode(ids);
	}
	
}
