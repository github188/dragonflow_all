package com.siteview.NNM.Editors;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.EditorPart;

import com.siteview.NNM.Activator;
import com.siteview.NNM.Views.NNMTreeView;
import com.siteview.NNM.dialogs.table.ImageKeys;
import com.siteview.NNM.modles.SubnetDataModle;
import com.siteview.NNM.modles.SubnetModle;

public class SubnetEditor extends EditorPart {
	public static final String ID="SubnetEditor";
	private Table table;
	Section topSection;
	public SubnetEditor() {
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
		table = new Table(topSection, SWT.NONE);
		table.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				TableItem tableitem=table.getSelection()[0];
				SubnetDataModle sub=(SubnetDataModle) tableitem.getData();
				NNMTreeView.getCNFNNMTreeView().subnetdata.setName(tableitem.getText(0));
				NNMTreeView.getCNFNNMTreeView().subnetdata.setSvnodes(sub.getSvnodes());
				IWorkbenchPage page = Activator.getDefault().getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				IEditorPart editor = page.findEditor(NNMTreeView.getCNFNNMTreeView().subnetdata);
				if(editor==null){
					try {
						page.openEditor(NNMTreeView.getCNFNNMTreeView().subnetdata, SubnetDataEditor.ID);
					} catch (PartInitException e1) {
						e1.printStackTrace();
					}
				}else{
					editor.setFocus();
					((SubnetDataEditor)editor).createTableItem();
					page.activate(editor);
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});
//		table.setHeaderVisible(true);
//		table.setLinesVisible(true);
		topSection.setClient(table);
		TableColumn ip=new TableColumn(table, SWT.NONE);
		ip.setWidth(200);
		topSection.layout();
		createtableitem();
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		this.setPartName(this.getEditorInput().getName());
		topSection.setText(this.getEditorInput().getName());
	}
	
	public void createtableitem(){
		for(TableItem ta:table.getItems())
			ta.dispose();
		List<SubnetDataModle> list=((SubnetEditorInput)this.getEditorInput()).getSubd();
		for(SubnetDataModle sub:list){
			TableItem tableitem=new TableItem(table, SWT.NONE);
			tableitem.setText(sub.getSubnetname()+"("+sub.getCount()+")");
			tableitem.setImage(ImageKeys.images.get(ImageKeys.SUBNET));
			tableitem.setData(sub);
		}
	}
}
