package com.siteview.NNM.Editors;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.siteview.NNM.Activator;
import com.siteview.NNM.Views.NNMTreeView;
import com.siteview.NNM.dialogs.table.ImageKeys;
import com.siteview.NNM.modles.SubnetDataModle;
import com.siteview.NNM.modles.SvgModle;

public class SvgManagermentEditor extends EditorPart {
	public final static String ID="SvgManagermentEditor";
	Section topSection;
	public SvgManagermentEditor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		// TODO Auto-generated method stub
		this.setInput(input);
		this.setSite(site);
		this.setPartName(input.getName());
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		parent.setLayout(new FillLayout());
		FormToolkit formToolkit = new FormToolkit(parent.getDisplay());
		topSection = formToolkit.createSection(parent, Section.TITLE_BAR);
		topSection.setText(this.getPartName());
		
		topSection.setLayout(new FillLayout());
		final Table t=new Table(topSection, SWT.NONE);
		t.setHeaderVisible(true);
		topSection.setClient(t);
		t.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem tableitem=t.getSelection()[0];
				IWorkbenchPage page = Activator.getDefault().getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				IEditorPart editor = page.findEditor(NNMTreeView.getCNFNNMTreeView().svg);
				NNMTreeView.getCNFNNMTreeView().svg.setName(tableitem.getText());
				if(editor==null){
					try {
						page.openEditor(NNMTreeView.getCNFNNMTreeView().svg, SvgEditor.ID);
					} catch (PartInitException e1) {
						e1.printStackTrace();
					}
				}else{
					editor.setFocus();
					page.activate(editor);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		TableColumn tac_name=new TableColumn(t,SWT.NONE);
		tac_name.setWidth(300);
		tac_name.setText("名称");

		List<SvgModle> svgs=((SvgManagermentInput)this.getEditorInput()).getSvgtype().getSvgs();
		for(SvgModle svg:svgs){
			int id=svg.getId();
			TableItem treeitem=new TableItem(t, SWT.NONE);
			treeitem.setText(0,svg.getName());
			if(id==0){
				treeitem.setImage(0,ImageKeys.images.get(ImageKeys.SWITCHROUTER_BLUE));
			}else if(id==1){
				treeitem.setImage(0,ImageKeys.images.get(ImageKeys.SWITCH_BLUE));
			}else if(id==2){
				treeitem.setImage(0,ImageKeys.images.get(ImageKeys.ROUTER_BLUE));
			}else if(id==3){
				treeitem.setImage(0,ImageKeys.images.get(ImageKeys.FIREWALL_BLUE));
			}else if(id==4){
				treeitem.setImage(0,ImageKeys.images.get(ImageKeys.SERVER_BLUE));
			}else if(id==5){
				treeitem.setImage(0,ImageKeys.images.get(ImageKeys.PC_BLUE));
			}else if(id==6){
				treeitem.setImage(0,ImageKeys.images.get(ImageKeys.OTHER_BLUE));
			}
		}
		t.layout();
		parent.layout();
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		this.setPartName(this.getEditorInput().getName());
		topSection.setText(this.getEditorInput().getName());
	}
}
