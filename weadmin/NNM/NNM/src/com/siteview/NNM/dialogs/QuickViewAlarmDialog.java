package com.siteview.NNM.dialogs;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.Windows.Forms.ConnectionBroker;

import com.siteview.ecc.monitor.nls.EccMessage;
import com.siteview.nnm.data.model.svNode;
import com.siteview.utils.db.DBQueryUtils;
import com.siteview.utils.international.InternationalUtils;

public class QuickViewAlarmDialog extends Dialog{
	svNode svnode;
	String ip;
	public QuickViewAlarmDialog(Shell parentShell,svNode svnode,String ip) {
		super(parentShell);
		this.svnode=svnode;
		this.ip=ip;
	}
	protected void configureShell(Shell newShell) {
		newShell.setSize(1300, 520);
		newShell.setLocation(400, 175);
		newShell.setText(ip+"告警事件");
		super.configureShell(newShell);
	}
	@Override
	protected Control createDialogArea(Composite parent) {
		// TODO Auto-generated method stub
		Composite c=(Composite) super.createDialogArea(parent);
		c.setLayout(new FillLayout());
		final Table table=new Table(c, SWT.BORDER);
		table.setLayout(new FillLayout());
		table.setLayout(new FillLayout());
		table.setHeaderVisible(true);
		table.setData(ip);
		table.setData(RWT.MARKUP_ENABLED, Boolean.TRUE);
		TableColumn status=new TableColumn(table,SWT.NONE);
		status.setText("状态");
		status.setWidth(50);
		TableColumn machineip=new TableColumn(table,SWT.NONE);
		machineip.setText("设备ip");
		machineip.setWidth(100);
		
		TableColumn name=new TableColumn(table,SWT.NONE);
		name.setText("监测器");
		name.setWidth(200);
		
		TableColumn count=new TableColumn(table,SWT.NONE);
		count.setText("持续次数");
		count.setWidth(100);
		
		TableColumn alarmstatus=new TableColumn(table,SWT.NONE);
		alarmstatus.setText("告警状态");
		alarmstatus.setWidth(100);
		
		TableColumn time=new TableColumn(table,SWT.NONE);
		time.setText("报警时间");
		time.setWidth(200);
		
		TableColumn detail=new TableColumn(table,SWT.NONE);
		detail.setText("详细");
		detail.setWidth(500);
		createTableItem(table,ip);
		createSubAction(table);
		fillPopMenu(table);
		return c;
	}
	private void createTableItem(Table table, String ip) {
		String sql="";
		if(ip!=null&&ip.length()>0)
			sql="select * from AlarmEventLog where EquipmentRecId "
				+ "in (select RecId from Equipment where ServerAddress "
				+ "='"+ip+"' )and isAlarm=1 and AlarmStatus not like '3'"
						+ "and MonitorRecId in (select RecId from Monitor)";
		else{
//			SimpleDateFormat simpdt=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			Date d=new Date();
//			String endtime=simpdt.format(d);
//			Calendar cal=Calendar.getInstance();
//			cal.add(Calendar.HOUR_OF_DAY, -2);
//			d=cal.getTime();
//			String starttime=simpdt.format(d);
			sql="select a.*,e.ServerAddress from AlarmEventLog a,Equipment e where "
					+ " isAlarm=1 and AlarmStatus not like '3'"
							+ "and MonitorRecId in (select RecId from Monitor) and a.EquipmentRecId=e.RecId";
		}
		DataTable dt=DBQueryUtils.Select(sql,  ConnectionBroker.get_SiteviewApi());
		table.removeAll();
		for(DataRow dr:dt.get_Rows()){
			TableItem tableitme=new TableItem(table, SWT.NONE);
			String[] text=new String[7];
			String monitortype=dr.get("MonitorType").toString();
			StringBuilder messageSb = new StringBuilder();
			StringBuilder toolMessage = new StringBuilder();
			text[0]=dr.get("MonitorStatus").toString();
			if(ip==null||ip.length()==0)
				text[1]=dr.get("ServerAddress").toString();
			else
				text[1]=ip;
			text[2]=dr.get("MonitorName").toString();
			text[3]=dr.get("MonitorCount").toString();
			text[4]=dr.get("AlarmStatus").toString();
			if(text[4].equals("1"))
				text[4]="告警";
			else if(text[4].equals("2"))
				text[4]="暂停";
			text[5]=dr.get("LastModDateTime").toString();
			text[6]=dr.get("MonitorValue").toString();
			InternationalUtils.getReturnValueMessage(
					ConnectionBroker.get_SiteviewApi(),
					dr.get("MonitorRecId").toString(),
					text[6], monitortype, messageSb, toolMessage, true);
			text[6]= messageSb.toString();
			tableitme.setText(text);
			tableitme.setData(dr.get("RecId").toString());
		}
	}
	private Menu popMenu;
	public void fillPopMenu(Table table){
		MenuManager menuManage=new MenuManager();
		menuManage.add(startalarm);
		menuManage.add(endalarm);
		popMenu=menuManage.createContextMenu(table);
		table.setMenu(popMenu);
	}
	private IAction startalarm;
	private IAction endalarm;
	private void createSubAction(final Table table){
		startalarm=new Action(EccMessage.get().RESET_ALARM) {
			public void run() {
				if(table.getSelection().length<=0)
					return;
				TableItem tableitem =table.getSelection()[0];
				if(tableitem.getText(4).equals("告警"))
					return;
				tableitem.setText(4,"告警");
				String update_sql="update AlarmEventLog set AlarmStatus='1' where RecId='"+tableitem.getData().toString()+"'";
				DBQueryUtils.UpdateorDelete(update_sql, ConnectionBroker.get_SiteviewApi());
			}
		};
		endalarm=new Action(EccMessage.get().ALARM_STOP) {
			public void run() {
				if(table.getSelection().length<=0)
					return;
				TableItem tableitem =table.getSelection()[0];
				if(tableitem.getText(4).equals("暂停"))
					return;
				tableitem.setText(4,"暂停");
				String update_sql="update AlarmEventLog set AlarmStatus='2' where RecId='"+tableitem.getData().toString()+"'";
				DBQueryUtils.UpdateorDelete(update_sql, ConnectionBroker.get_SiteviewApi());
			}
		};
	}
}
