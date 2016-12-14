package com.siteview.NNM.Editors;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.EditorPart;

import com.siteview.NNM.util.NNMContextMenu;
import com.siteview.NNM.util.NNMTreeOperateUtils;
import com.siteview.monitor.tools.TableColumnSortor;
import com.siteview.nnm.data.model.svNode;
import com.siteview.utils.control.ControlUtils;

/**
 * NNM树 设备管理-设备类型-xxx(三层交换机,路由等)Editor
 * 
 * @author Administrator
 *
 */
public class SvgEditor extends EditorPart {
	public final static String ID = "SvgEditor";
	private Table table;
	private Section topSection;

	public SvgEditor() {

	}

	@Override
	public void doSave(IProgressMonitor monitor) {

	}

	@Override
	public void doSaveAs() {

	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		this.setInput(input);
		this.setSite(site);
		this.setPartName(input.getName());
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		FormToolkit formToolkit = new FormToolkit(parent.getDisplay());
		topSection = formToolkit.createSection(parent, Section.TITLE_BAR);
		topSection.setText(this.getPartName());

		topSection.setLayout(new FillLayout());
		table = new Table(topSection, SWT.NONE);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		topSection.setClient(table);
		List<TableColumn> tableColumns = ControlUtils.createTableColunms(table,
				new String[] { "IP地址", "设备名称", "设备型号", "mac地址", "厂商" }, new int[] { 200, 300, 300, 200, 200 });
		for (TableColumn column : tableColumns) {
			TableColumnSortor.addStringSorter(table, column);
		}
		new NNMContextMenu(table, 1);
		topSection.layout();
		createtableitem();
	}

	@Override
	public void setFocus() {
		this.setPartName(this.getEditorInput().getName());
		topSection.setText(this.getPartName());
		createtableitem();
	}

	public void createtableitem() {
		for (TableItem tableitem : table.getItems()) {
			tableitem.dispose();
		}
		List<Map<String, Object>> list = NNMTreeOperateUtils.getEquipmentTypeData(this.getPartName());
		for (Map<String, Object> map : list) {
			svNode node = (svNode) map.get("data");
			String[] text = new String[5];
			text[0] = node.getLocalip();
			text[1] = node.getDevicename();
			text[2] = node.getModel();
			text[3] = node.getMac();
			text[4] = node.getFactory();
			TableItem routeritem_1 = new TableItem(table, SWT.NONE);
			routeritem_1.setText(text);
			routeritem_1.setData(node);
		}
	}

}
