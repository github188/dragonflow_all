package com.siteview.NNM.Editors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.part.EditorPart;

import com.siteview.NNM.Views.NNMTreeView;
import com.siteview.NNM.dialogs.CommunityDialog;
import com.siteview.NNM.dialogs.MoreIPDialog;
import com.siteview.NNM.dialogs.table.ImageKeys;
import com.siteview.NNM.modles.SnmpModle;
import com.siteview.mib.model.FileMibModel;
import com.siteview.nnm.data.EntityManager;
import com.siteview.utils.snmp.SnmpWalk;

import siteview.windows.forms.MsgBox;

public class MIBBrowseEditor extends EditorPart {
	public static final String ID = "MIBBrowseEditor";
	private FileMibModel filemib;
	private MIBBrowseInput mibb;
	Composite tablec;
	Text ttext;
	Text ttable;
	Table mibtable;
	Text iptext;

	Composite filec;
	Section topSection;
	Table ftable;
	SnmpModle snmp = new SnmpModle();
	Button mibtb;
	StackLayout st;
	Label OIDl;
	Comparator comp;

	public MIBBrowseEditor() {
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
		this.setPartName(input.getName());
		this.setSite(site);
		filemib = ((MIBBrowseInput) this.getEditorInput()).getFilemib();
		mibb = (MIBBrowseInput) input;
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
		st = new StackLayout();
		parent.setLayout(st);
		tablec = new Composite(parent, SWT.NONE);
		filec = new Composite(parent, SWT.NONE);
		createFilec();
		createTablec();
		refresh();
		comp = new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				String key1 = "";
				String key2 = "";
				if (o1.contains(".")) {
					key1 = o1.substring(o1.lastIndexOf(".") + 1);
					o1 = o1.substring(0, o1.lastIndexOf("."));
				}
				if (o2.contains(".")) {
					key2 = o2.substring(o2.lastIndexOf(".") + 1);
					o2 = o2.substring(0, o2.lastIndexOf("."));
				}
				if (o1.equals(o2))
					return Integer.parseInt(key1) - Integer.parseInt(key2);
				return compare(o1, o2);
			}
		};
	}

	@Override
	public void setFocus() {
	}

	public void createTablec() {
		tablec.setLayout(new FormLayout());
		ttext = new Text(tablec, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		FormData text_fd = new FormData();
		text_fd.left = new FormAttachment(0);
		text_fd.right = new FormAttachment(100);
		text_fd.top = new FormAttachment(0);
		text_fd.bottom = new FormAttachment(0, 100);
		ttext.setLayoutData(text_fd);

		Label iplabel = new Label(tablec, SWT.NONE);
		iplabel.setText("IP地址:");
		FormData iplabel_fd = new FormData();
		iplabel_fd.left = new FormAttachment(0);
		iplabel_fd.right = new FormAttachment(0, 50);
		iplabel_fd.top = new FormAttachment(ttext, 15);
		iplabel_fd.bottom = new FormAttachment(iplabel, 30);
		iplabel.setLayoutData(iplabel_fd);

		iptext = new Text(tablec, SWT.BORDER);
		FormData iptext_fd = new FormData();
		iptext_fd.left = new FormAttachment(iplabel, 5);
		iptext_fd.right = new FormAttachment(iptext, 140);
		iptext_fd.top = new FormAttachment(ttext, 5);
		iptext_fd.bottom = new FormAttachment(iptext, 30);
		iptext.setLayoutData(iptext_fd);

		Button b = new Button(tablec, SWT.NONE);
		b.setText("----");
		FormData b_fd = new FormData();
		b_fd.left = new FormAttachment(iptext, 5);
		b_fd.right = new FormAttachment(b, 70);
		b_fd.top = new FormAttachment(ttext, 5);
		b_fd.bottom = new FormAttachment(b, 30);
		b.setLayoutData(b_fd);
		b.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				MoreIPDialog more = new MoreIPDialog(Display.getCurrent().getActiveShell(), EntityManager.allEntity);
				if (more.open() == IDialogConstants.OK_ID) {
					if (more.getServerAddress() != null && !more.getServerAddress().equals(""))
						iptext.setText(more.getServerAddress());
				}
			}

		});

		Button selectb = new Button(tablec, SWT.NONE);
		selectb.setText("选项");
		FormData selectb_fd = new FormData();
		selectb_fd.left = new FormAttachment(b, 5);
		selectb_fd.right = new FormAttachment(selectb, 70);
		selectb_fd.top = new FormAttachment(ttext, 5);
		selectb_fd.bottom = new FormAttachment(selectb, 30);
		selectb.setLayoutData(selectb_fd);
		selectb.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CommunityDialog com = new CommunityDialog(Display.getCurrent().getActiveShell(), snmp);
				com.open();
			}

		});

		Button mibvb = new Button(tablec, SWT.NONE);
		mibvb.setText("MIB取值");
		FormData mibvb_fd = new FormData();
		mibvb_fd.left = new FormAttachment(selectb, 5);
		mibvb_fd.right = new FormAttachment(mibvb, 70);
		mibvb_fd.top = new FormAttachment(ttext, 5);
		mibvb_fd.bottom = new FormAttachment(mibvb, 30);
		mibvb.setLayoutData(mibvb_fd);

		mibtb = new Button(tablec, SWT.NONE);
		mibtb.setText("MIB取表");
		if (filemib != null && filemib.getType().equals("tables") && filemib.getOb().size() > 0 && filemib.getOb().get(0).getOb().size() > 0)
			mibtb.setEnabled(true);
		else
			mibtb.setEnabled(false);
		FormData mibtb_fd = new FormData();
		mibtb_fd.left = new FormAttachment(mibvb, 5);
		mibtb_fd.right = new FormAttachment(mibtb, 70);
		mibtb_fd.top = new FormAttachment(ttext, 5);
		mibtb_fd.bottom = new FormAttachment(mibtb, 30);
		mibtb.setLayoutData(mibtb_fd);
		OIDl = new Label(tablec, SWT.NONE);
		FormData OIDl_fd = new FormData();
		OIDl_fd.left = new FormAttachment(mibtb, 5);
		OIDl_fd.right = new FormAttachment(100);
		OIDl_fd.top = new FormAttachment(ttext, 15);
		OIDl_fd.bottom = new FormAttachment(OIDl, 30);
		OIDl.setLayoutData(OIDl_fd);

		mibvb.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String ip = iptext.getText();
				if (ip == null || ip.length() == 0) {
					MsgBox.ShowError("错误", "必须先填写设备");
					return;
				}
				mibtable.removeAll();
				refreshMibtable();
				SnmpWalk snmpWalk;
				if (snmp.getType() == 3)
					snmpWalk = new SnmpWalk(ip, snmp.getPort(), snmp.getCommuntity(), filemib.getOid(), snmp.getType(), 5000, snmp.getUser(), snmp.getAuth(), snmp.getPwd(), snmp.getPrau(), snmp.getPrpwd());
				else
					snmpWalk = new SnmpWalk(ip, snmp.getPort(), snmp.getCommuntity(), filemib.getOid(), snmp.getType());
				Map<String, String> snmp_values = snmpWalk.snmpWalkMap();
				Iterator<String> ite = snmp_values.keySet().iterator();
				while (ite.hasNext()) {
					String key = ite.next();
					TableItem tableitem = new TableItem(mibtable, SWT.NONE);
					tableitem.setText(0, key);
					String value = snmp_values.get(key).trim();
					while (value.contains("   ")) {
						value = value.replaceAll("   ", "  ");
					}
					value = value.replaceAll("  ", " , ");
					tableitem.setText(1, value);
				}
			}

		});

		mibtb.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String ip = iptext.getText();
				if (ip == null || ip.length() == 0) {
					MsgBox.ShowError("错误", "必须先填写设备");
					return;
				}
				mibtable.removeAll();
				SnmpWalk snmpWalk;
				if (snmp.getType() == 3)
					snmpWalk = new SnmpWalk(ip, snmp.getPort(), snmp.getCommuntity(), filemib.getOid(), snmp.getType(), 5000, snmp.getUser(), snmp.getAuth(), snmp.getPwd(), snmp.getPrau(), snmp.getPrpwd());
				else
					snmpWalk = new SnmpWalk(ip, snmp.getPort(), snmp.getCommuntity(), filemib.getOid(), snmp.getType());
				Map<String, String> snmp_values = snmpWalk.snmpWalkMap();
				TableColumn[] tablecolumns = mibtable.getColumns();
				if (filemib.getOb().size() == 0 && filemib.getOb().get(0).getOb().size() == 0)
					return;
				List<FileMibModel> filemibs = filemib.getOb().get(0).getOb();
				// 修改表头
				for (int i = 0; i < filemibs.size(); i++) {
					TableColumn tablecolumn;
					if (i >= tablecolumns.length)
						tablecolumn = new TableColumn(mibtable, SWT.NONE);
					else
						tablecolumn = tablecolumns[i];
					tablecolumn.setText(filemibs.get(i).getName());
					if (i == 0)
						tablecolumn.setWidth(50);
					else
						tablecolumn.setWidth(200);
				}
				if (filemibs.size() < tablecolumns.length)
					for (int i = filemibs.size(); i < tablecolumns.length; i++) {
						TableColumn tablecolumn = tablecolumns[i];
						tablecolumn.dispose();
					}
				Iterator<String> ite = snmp_values.keySet().iterator();
				Map<String, List<String>> map = new HashMap<String, List<String>>();
				while (ite.hasNext()) {
					String key = ite.next();
					for (FileMibModel f : filemibs) {
						String s = f.getOid();
						if (key.startsWith(s + ".")) {
							List list = map.get(s);
							if (list == null)
								list = new ArrayList<String>();
							list.add(key);
							map.put(s, list);
							continue;
						}
					}
				}
				for (String s : map.keySet()) {
					List<String> value = map.get(s);
					Collections.sort(value, comp);
				}
				// 填入数据
				for (int n = 0; n < map.get(filemibs.get(0).getOid()).size(); n++) {
					String[] text = new String[filemibs.size()];
					for (int k = 0; k < filemibs.size(); k++) {
						String oid = filemibs.get(k).getOid();
						List list = map.get(oid);
						if (list == null || list.size() <= n)
							text[k] = "";
						else {
							String keyoid = map.get(oid).get(n);
							text[k] = snmp_values.get(keyoid);
						}
					}
					TableItem tableitem = new TableItem(mibtable, SWT.NONE);
					tableitem.setText(text);
				}
			}
		});

		mibtable = new Table(tablec, SWT.BORDER);
		mibtable.setHeaderVisible(true);
		FormData mibtable_fd = new FormData();
		mibtable_fd.left = new FormAttachment(0);
		mibtable_fd.right = new FormAttachment(100);
		mibtable_fd.top = new FormAttachment(iplabel, 0);
		mibtable_fd.bottom = new FormAttachment(100);
		mibtable.setLayoutData(mibtable_fd);

		TableColumn oidc = new TableColumn(mibtable, SWT.NONE);
		oidc.setText("OID");
		oidc.setWidth(200);

		TableColumn valuec = new TableColumn(mibtable, SWT.NONE);
		valuec.setText("值");
		valuec.setWidth(300);
	}

	public void createFilec() {
		filec.setLayout(new FillLayout());
		FormToolkit formToolkit = new FormToolkit(filec.getDisplay());
		topSection = formToolkit.createSection(filec, Section.TITLE_BAR);
		ftable = new Table(topSection, SWT.NONE);
		TableColumn tableColumn = new TableColumn(ftable, SWT.NONE);
		tableColumn.setText("名称");
		tableColumn.setWidth(200);
		ftable.setHeaderVisible(true);
		ftable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem tableitem = ftable.getSelection()[0];
				if (tableitem != null) {
					mibb.setFilemib((FileMibModel) tableitem.getData());
					refresh();
				}
			}
		});
		topSection.setClient(ftable);
	}

	public void refreshTablec() {
		if (filemib.getType().equals("tables") && filemib.getOb().size() > 0 && filemib.getOb().get(0).getOb().size() > 0)
			mibtb.setEnabled(true);
		else
			mibtb.setEnabled(false);
		if (filemib != null)
			OIDl.setText("OID:(" + filemib.getOid() + ")");
		else
			OIDl.setText("MIB游览");
		ttext.setText(filemib.getDescription());
	}

	public void refreshFilec(List<FileMibModel> lists) {
		if (filemib != null)
			topSection.setText(filemib.getName() + "(" + filemib.getOid() + ")");
		else
			topSection.setText("MIB游览");
		ftable.removeAll();
		for (FileMibModel f : lists) {
			TableItem tableitem = new TableItem(ftable, SWT.NONE);
			tableitem.setText(f.getName() + "(" + f.getEndnum() + ")");
			String type = f.getType();
			String image = "";
			if (type.equals("file")) {
				image = ImageKeys.FILEMIB;
			} else if (type.equals("table")) {
				image = ImageKeys.TABLEMIB;
			} else if (type.equals("tables")) {
				image = ImageKeys.TABLESMIB;
			} else {
				image = ImageKeys.INFOMIB;
			}
			tableitem.setImage(ImageKeys.images.get(image));
			tableitem.setData(f);
		}
	}

	public void refresh() {
		if (mibb.getFilemib() == null) {
			this.setPartName("MIB浏览");
			refreshFilec(mibb.getMib().getFilemib());
			st.topControl = filec;
			filec.getParent().layout();
		} else {
			CommonViewer nnmtree = NNMTreeView.getCNFNNMTreeView().getCommonViewer();
			nnmtree.setSelection(new StructuredSelection(mibb.getFilemib()));
			if (!nnmtree.getExpandedState(mibb.getFilemib())) {
				nnmtree.expandToLevel(mibb.getFilemib(), 1);
			}
			filemib = ((MIBBrowseInput) this.getEditorInput()).getFilemib();
			this.setPartName(filemib.getName());
			if (filemib.getType().equals("file")) {
				refreshFilec(mibb.getFilemib().getOb());
				st.topControl = filec;
				filec.getParent().layout();
			} else {
				refreshTablec();
				st.topControl = tablec;
				mibtable.removeAll();
				tablec.getParent().layout();
			}
		}
	}

	public void refreshMibtable() {
		TableColumn[] tablecolumns = mibtable.getColumns();
		if (tablecolumns.length == 2) {
			tablecolumns[0].setText("OID");
			tablecolumns[0].setWidth(200);
			tablecolumns[1].setText("值");
			tablecolumns[1].setWidth(200);
		} else if (tablecolumns.length > 2) {
			tablecolumns[0].setText("OID");
			tablecolumns[1].setText("值");
			tablecolumns[0].setWidth(200);
			tablecolumns[1].setWidth(200);
			for (int n = 2; n < tablecolumns.length; n++) {
				tablecolumns[n].dispose();
			}
		} else if (tablecolumns.length < 1) {
			TableColumn tablecolumn = new TableColumn(mibtable, SWT.NONE);
			tablecolumn.setText("OID");
			tablecolumn.setWidth(200);
			TableColumn tablecolumn0 = new TableColumn(mibtable, SWT.NONE);
			tablecolumn0.setText("值");
			tablecolumn0.setWidth(200);
		} else {
			tablecolumns[0].setText("OID");
			tablecolumns[0].setWidth(200);
			TableColumn tablecolumn0 = new TableColumn(mibtable, SWT.NONE);
			tablecolumn0.setText("值");
			tablecolumn0.setWidth(200);
		}
	}
}
