package com.siteview.ecc.yft.ui;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.EditorPart;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.SiteviewException;
import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ConnectionBroker;
import Siteview.Windows.Forms.MsgBox;

import com.siteview.utils.db.DBQueryUtils;
import com.siteview.utils.entities.ImageKeys;

public class AlarmIssuedEditor extends EditorPart{
	public static String ID="com.siteview.ecc.yft.AlarmIssuedEditor";
	private FormToolkit formToolkit;
	private ScrolledForm form;
	private Composite body;
	private Section selectSection;
	private ISiteviewApi api;

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
		this.setSite(site);
		this.setInput(input);
		this.setPartName(input.getName());
		api=ConnectionBroker.get_SiteviewApi();
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
	Combo machine_type;
	Text cycle;
	Text untreated_alarm_number;
	Text allow_processing_time;
	Button issend;
	Button notsend;
	Composite monitortype;
	Table table;
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		parent.setLayout(new FillLayout());
		formToolkit = new FormToolkit(parent.getDisplay());
		form = formToolkit.createScrolledForm(parent);
		form.setLayout(new FillLayout());
		body = form.getBody();
		body.setLayout(new GridLayout());
		
		selectSection = formToolkit.createSection(body,  Section.TITLE_BAR
				| Section.LEFT_TEXT_CLIENT_ALIGNMENT);
		selectSection.setText("告警下发");
		selectSection.setLayoutData(new GridData(GridData.FILL_BOTH));
		selectSection.setExpanded(true);
//		selectSection.setLayout(new FillLayout());
		GridLayoutFactory.fillDefaults().numColumns(1).extendedMargins(0, 0, 0, 0).applyTo(selectSection);
		Composite toolBar = new Composite(selectSection, SWT.NONE);
		toolBar.setLayout(new GridLayout());
		toolBar.setLayoutData(new GridData(GridData.FILL_BOTH));
		Button button = new Button(toolBar, SWT.NONE | SWT.READ_ONLY);
		button.setText("保存");
		button.setImage(ImageKeys.getImage(ImageKeys.ADD));
		button.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				String machinetype=machine_type.getText();
				String machinetypeId=machine_type.getData(machinetype).toString();
				String cycle_=cycle.getText().trim();
				String alarm_number=untreated_alarm_number.getText().trim();
				String reposetime=allow_processing_time.getText().trim();
				boolean flag=issend.getSelection();
				Map<String,String> map=getMonitorType();
				if(map.size()==0){
					MsgBox.ShowConfirm("必须至少勾选一个告警类型");
					return;
				}
				try {
					BusinessObject bo=DBQueryUtils.queryOnlyBo("MachineType", machinetype,
							"AlarmIssued", api);
					if(bo==null){
						bo=api.get_BusObService().Create("AlarmIssued");
					}else{
						Collection<BusinessObject> col=DBQueryUtils
						.getBussCollection("AlarmIssuedRecId", bo.get_RecId(), "AlarmIssuedRel", api);
						Iterator<BusinessObject> ite=col.iterator();
						while(ite.hasNext()){
							BusinessObject borel=ite.next();
							String monitortypetable=borel.GetField("MonitorTypeTable").get_NativeValue().toString();
							if(map.get(monitortypetable)==null)
								borel.DeleteObject(api);
							else
								map.remove(monitortypetable);
						}
					}
					bo.GetField("MachineType").SetValue(new SiteviewValue(machinetype));
					bo.GetField("MachineTypeId").SetValue(new SiteviewValue(machinetypeId));
					bo.GetField("Cycle").SetValue(new SiteviewValue(cycle_));
					bo.GetField("ResponseTime").SetValue(new SiteviewValue(reposetime));
					bo.GetField("AlarmNumber").SetValue(new SiteviewValue(alarm_number));
					bo.GetField("WhetherSend").SetValue(new SiteviewValue(flag));
					bo.SaveObject(api, true, true);
					Iterator<String> ite=map.keySet().iterator();
					while(ite.hasNext()){
						String key=ite.next();
						String value=map.get(key);
						BusinessObject borel=api.get_BusObService().Create("AlarmIssuedRel");
						borel.GetField("MonitorTypeTable").SetValue(new SiteviewValue(key));
						borel.GetField("MonitorTypeName").SetValue(new SiteviewValue(value));
						borel.GetField("AlarmIssuedRecId").SetValue(new SiteviewValue(bo.get_RecId()));
						borel.SaveObject(api, true, true);
					}
					MsgBox.ShowConfirm("下发成功");
				} catch (SiteviewException e1) {
					e1.printStackTrace();
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		selectSection.setTextClient(toolBar);
		
		final Composite select_c=new Composite(selectSection, SWT.BORDER);
		selectSection.setClient(select_c);
		GridLayout gridLayout = new GridLayout(2,false);
		select_c.setLayout(gridLayout);
		select_c.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label label=new Label(select_c, SWT.NONE);
		label.setText("设备类型");
		machine_type=new Combo(select_c, SWT.NONE);
		GridData gd=new GridData();
		gd.widthHint=500;
		machine_type.setLayoutData(gd);
		machine_type.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				// TODO Auto-generated method stub
				setMonitorType();
				if(table!=null){
					table.layout();
					select_c.layout();
				}
			}
		});
		
		Label relabel=new Label(select_c, SWT.NONE);
		relabel.setText("巡检周期(天)");
		cycle=new Text(select_c, SWT.BORDER);
		cycle.setLayoutData(gd);
		
		Label label_=new Label(select_c, SWT.NONE);
		label_.setText("未处理告警数");
		untreated_alarm_number=new Text(select_c, SWT.BORDER);
		untreated_alarm_number.setLayoutData(gd);
		
		Label labeltime=new Label(select_c, SWT.NONE);
		labeltime.setText("告警允许响应时长(小时)");
		allow_processing_time=new Text(select_c, SWT.BORDER);
		allow_processing_time.setLayoutData(gd);
		
		Label label_not=new Label(select_c, SWT.NONE);
		label_not.setText("周期未达数是否告警");
		Composite c1=new Composite(select_c, SWT.NONE);
		c1.setLayout(new GridLayout(2,false));
		issend = new Button(c1, SWT.RADIO);
		issend.setText("是");
		issend.setSelection(true);
		notsend = new Button(c1, SWT.RADIO);
		notsend.setText("否");
		Label monitorlabel=new Label(select_c, SWT.NONE);
		monitorlabel.setText("告警类型");
		monitortype=new Composite(select_c, SWT.NONE);
		GridData gd_c=new GridData();
		monitortype.setLayoutData(gd_c);
		GridData g=new GridData(GridData.FILL_BOTH);
		g.horizontalSpan=2;
		initmahicne_type();
		setMonitorType();
		Composite c=new Composite(select_c, SWT.BORDER);
//		c.setLayout(new FillLayout());
		GridLayoutFactory.fillDefaults().numColumns(1).extendedMargins(0, 0, 0, 0).applyTo(c);
		
		table=new Table(c, SWT.NONE);
		table.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		c.setLayoutData(g);
		table.setHeaderVisible(true);
		table.setVisible(true);
		table.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				try {
					Point point = new Point(event.x, event.y);
					TableItem tableItem = table.getItem(point);
					if (tableItem == null || tableItem.isDisposed())
						return;
					int column = -1;
					for (int i = 0; i < table.getColumnCount(); i++) {
						Rectangle rec = tableItem.getBounds(i);
						if (rec.contains(point))
							column = i;
					}
					if(column==5){
						machine_type.setText(tableItem.getText(0));
						cycle.setText(tableItem.getText(1));
						untreated_alarm_number.setText(tableItem.getText(2));
						allow_processing_time.setText(tableItem.getText(3));
						issend.setSelection(tableItem.getText(4).equals("true"));
						notsend.setSelection(tableItem.getText(4).equals("false"));
						String sql="select * from AlarmIssuedRel";
						DataTable dt=DBQueryUtils.Select(sql, api);
						Map<String,String> map=new HashMap<String,String>();
						for(DataRow dr:dt.get_Rows()){
							String monitortypename=dr.get("MonitorTypeName")==null?"":dr.get("MonitorTypeName").toString();
							String monitortypetable=dr.get("MonitorTypeTable")==null?"":dr.get("MonitorTypeTable").toString();
							map.put(monitortypename, monitortypetable);
						}
						for(Control c:monitortype.getChildren()){
							Button b=(Button) c;
							if(map.get(b.getText())!=null||map.get(b.getData(b.getText()).toString())!=null)
								b.setSelection(true);
						}
					}else if(column==6){
						String id=tableItem.getData().toString();
						DBQueryUtils.UpdateorDelete("delete from AlarmIssuedRel where AlarmIssuedRecId='"+id+"'", api);
						DBQueryUtils.UpdateorDelete("delete from AlarmIssued where RecId='"+id+"'", api);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		TableColumn tableColumn0=new TableColumn(table, SWT.NONE);
		tableColumn0.setText("设备类型");
		tableColumn0.setWidth(100);
		
		TableColumn tableColumn1=new TableColumn(table, SWT.NONE);
		tableColumn1.setText("巡检周期(天)");
		tableColumn1.setWidth(100);
		
		TableColumn tableColumn2=new TableColumn(table, SWT.NONE);
		tableColumn2.setText("告警未处理数量");
		tableColumn2.setWidth(100);
		
		TableColumn tableColumn3=new TableColumn(table, SWT.NONE);
		tableColumn3.setText("告警允许响应时长(小时)");
		tableColumn3.setWidth(300);
		
		TableColumn tableColumn4=new TableColumn(table, SWT.NONE);
		tableColumn4.setText("周期未达到数目是否发送告警");
		tableColumn4.setWidth(300);
		
		TableColumn tableColumn5=new TableColumn(table, SWT.NONE);
		tableColumn5.setText("编辑");
		tableColumn5.setWidth(60);
		TableColumn tableColumn6=new TableColumn(table, SWT.NONE);
		tableColumn6.setText("删除");
		tableColumn6.setWidth(60);
		
//		TableColumn tableColumn7=new TableColumn(table, SWT.NONE);
//		tableColumn7.setText("告警类型");
//		tableColumn7.setWidth(500);
		setTable();
	}
	public void initmahicne_type(){
		String sql="select RecId,typeAlias from EquipmentTypeRel where ParentID='' OR ParentID is null";
		DataTable dt=DBQueryUtils.Select(sql, api);
		for(DataRow dr:dt.get_Rows()){
			String recid=dr.get("RecId")==null?"":dr.get("RecId").toString();
			String typeAlias=dr.get("typeAlias")==null?"":dr.get("typeAlias").toString();
			machine_type.add(typeAlias);
			machine_type.setData(typeAlias,recid);
		}
		machine_type.setText("服务器");
	}
	public void setMonitorType(){
		for(Control c:monitortype.getChildren()){
			c.dispose();
		}
		monitortype.setLayout(new GridLayout(6, false));
		String sql="select distinct(MonitorTableName),MonitorName from "
				+ "EccMonitorList e where e.Range in (select relationEqName "
				+ "from EquipmentTypeRel where ParentID='"+machine_type.getData(machine_type.getText()).toString()+"')";
		DataTable dt=DBQueryUtils.Select(sql, api);
		for(DataRow dr:dt.get_Rows()){
			String name=dr.get("MonitorName")==null?"":dr.get("MonitorName").toString();
			String type=dr.get("MonitorTableName")==null?"":dr.get("MonitorTableName").toString();
			Button b=new Button(monitortype, SWT.CHECK);
			b.setText(name);
			b.setData(name,type);
		}
		monitortype.layout();
		selectSection.layout();
	}
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}
	public void setTable(){
		String sql="select * from AlarmIssued";
		DataTable dt=DBQueryUtils.Select(sql, api);
		for(DataRow dr:dt.get_Rows()){
			String[] text=new String[7];
			TableItem tableItem=new TableItem(table, SWT.NONE);
			text[0]=dr.get("MachineType")==null?"":dr.get("MachineType").toString();
			text[1]=dr.get("Cycle")==null?"":dr.get("Cycle").toString();
			text[2]=dr.get("AlarmNumber")==null?"":dr.get("AlarmNumber").toString();
			text[3]=dr.get("ResponseTime")==null?"":dr.get("ResponseTime").toString();
			text[4]=dr.get("WhetherSend")==null?"":dr.get("WhetherSend").toString();
			text[5]="";
			text[6]="";
//			text[7]=dr.get("")==null?"":dr.get("").toString();
			tableItem.setText(text);
			tableItem.setImage(5, ImageKeys.getImage(ImageKeys.Edit));
			tableItem.setImage(6, ImageKeys.getImage(ImageKeys.DeleteMonitor));
			tableItem.setData(dr.get("RecId").toString());
		}
		table.layout();
	}
	public Map<String,String> getMonitorType(){
		Map<String,String> map=new HashMap<String,String>();
		for(Control b:monitortype.getChildren()){
			if(((Button)b).getSelection()){
				map.put(((Button)b).getData(((Button)b).getText()).toString(),((Button)b).getText());
			}
		}
		return map;
	}
}
