package com.siteview.NNM.Editors;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.EditorPart;

import com.siteview.NNM.dialogs.SetUpDataTime;
import com.siteview.NNM.util.NNMTreeOperateUtils;
import com.siteview.utils.control.ControlUtils;

import Siteview.Windows.Forms.ConnectionBroker;

public class IPMACChangeEditor extends EditorPart{
	public static final String ID="IPMACChangeEditor";
	private Table table;
	Section topSection;
	Text text;
	Text mactext;
	String starttime;
	String endtime;
	Combo dtime;
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
		this.setInput(input);
		this.setPartName(input.getName());
		this.setSite(site);
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
		parent.setLayout(new FillLayout());
		FormToolkit formToolkit = new FormToolkit(parent.getDisplay());
		topSection = formToolkit.createSection(parent, Section.TITLE_BAR);
		topSection.setText(this.getPartName());

		topSection.setLayout(new FillLayout());
		Composite c = new Composite(topSection, SWT.NONE);
		c.setLayout(new FormLayout());
		Label lable = new Label(c, SWT.NONE);
		lable.setText("轮循间隔:");
		FormData lable_fd = new FormData();
		lable_fd.top = new FormAttachment(0, 5);
		lable_fd.left = new FormAttachment(0, 2);
		lable_fd.right = new FormAttachment(0, 60);
		lable_fd.bottom = new FormAttachment(0, 30);
		lable.setLayoutData(lable_fd);

		Combo combo = new Combo(c, SWT.NONE);
		FormData combo_fd = new FormData();
		combo_fd.top = new FormAttachment(0, 0);
		combo_fd.left = new FormAttachment(lable, 5);
		combo_fd.right = new FormAttachment(combo, 150);
		combo_fd.bottom = new FormAttachment(0, 30);
		combo.setLayoutData(combo_fd);
		combo.add("2小时");
		combo.add("3小时");
		combo.add("6小时");
		combo.add("8小时");
		combo.add("12小时");
		combo.select(0);

		Button button = new Button(c, SWT.NONE);
		FormData button_fd = new FormData();
		button_fd.top = new FormAttachment(0, 0);
		button_fd.left = new FormAttachment(combo, 5);
		button_fd.right = new FormAttachment(button, 50);
		button_fd.bottom = new FormAttachment(0, 30);
		button.setLayoutData(button_fd);
		button.setText("应用");

		Label iplable = new Label(c, SWT.NONE);
		iplable.setText("IP地址:");
		FormData ip_fd = new FormData();
		ip_fd.top = new FormAttachment(0, 45);
		ip_fd.left = new FormAttachment(0, 2);
		ip_fd.right = new FormAttachment(0, 60);
		ip_fd.bottom = new FormAttachment(0, 80);
		iplable.setLayoutData(ip_fd);
		
		text = new Text(c, SWT.BORDER);
		FormData text_fd = new FormData();
		text_fd.top = new FormAttachment(0, 40);
		text_fd.left = new FormAttachment(iplable, 5, SWT.RIGHT);
		text_fd.right = new FormAttachment(text, 150);
		text_fd.bottom = new FormAttachment(0, 70);
		text.setLayoutData(text_fd);

		Label maclable = new Label(c, SWT.NONE);
		maclable.setText("MAC地址:");
		FormData mac_fd = new FormData();
		mac_fd.top = new FormAttachment(0, 45);
		mac_fd.left = new FormAttachment(text, 10, SWT.RIGHT);
		mac_fd.right = new FormAttachment(maclable, 50);
		mac_fd.bottom = new FormAttachment(0, 80);
		maclable.setLayoutData(mac_fd);
		
		mactext = new Text(c, SWT.BORDER);
		FormData mactext_fd = new FormData();
		mactext_fd.top = new FormAttachment(0, 40);
		mactext_fd.left = new FormAttachment(maclable, 5, SWT.RIGHT);
		mactext_fd.right = new FormAttachment(mactext, 150);
		mactext_fd.bottom = new FormAttachment(0, 70);
		mactext.setLayoutData(mactext_fd);
		
		Label timelable=new Label(c, SWT.NONE);
		timelable.setText("监测时间:");
		FormData timelable_fd=new FormData();
		timelable_fd.top=new FormAttachment(0,45);
		timelable_fd.left=new FormAttachment(mactext,10,SWT.RIGHT);
		timelable_fd.right=new FormAttachment(timelable,50);
		timelable_fd.bottom=new FormAttachment(0,80);
		timelable.setLayoutData(timelable_fd);
		
		dtime=new Combo(c, SWT.BORDER | SWT.READ_ONLY);
		FormData dtime_fd=new FormData();
		dtime_fd.top=new FormAttachment(0,40);
		dtime_fd.left=new FormAttachment(timelable,5,SWT.RIGHT);
		dtime_fd.right=new FormAttachment(dtime,150);
		dtime_fd.bottom=new FormAttachment(0,70);
		dtime.setLayoutData(dtime_fd);
		dtime.add("过去3小时内");
		dtime.add("过去4小时内");
		dtime.add("过去6小时内");
		dtime.add("过去12小时内");
		dtime.add("过去24小时内");
		dtime.add("过去2天内");
		dtime.add("自定义");
		dtime.select(0);
		dtime.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				if (dtime.getSelectionIndex() == 6) {
					SetUpDataTime sudt = new SetUpDataTime(Display.getCurrent().getActiveShell());
					sudt.open();
					starttime = sudt.getStartime();
					endtime = sudt.getEndtime();
					dtime.setText(starttime + "到" + endtime);
				}
			}
		});
		
		Button select = new Button(c, SWT.NONE);
		FormData select_fd = new FormData();
		select_fd.top = new FormAttachment(0, 40);
		select_fd.left = new FormAttachment(dtime, 5);
		select_fd.right = new FormAttachment(select, 50);
		select_fd.bottom = new FormAttachment(0, 70);
		select.setLayoutData(select_fd);
		select.setText("查询");
		select.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String ip = text.getText();
				String mac = mactext.getText();
				switch (dtime.getSelectionIndex()) {
				case 0:
					gettimebeforh(3);
					break;
				case 1:
					gettimebeforh(4);
					break;
				case 2:
					gettimebeforh(6);
					break;
				case 3:
					gettimebeforh(12);
					break;
				case 4:
					gettimebeforh(24);
					break;
				case 5:
					gettimebeforh(48);
					break;
				default:
					break;
				}
				List<Map<String, String>> list = NNMTreeOperateUtils.getIpMacChangeData(ConnectionBroker.get_SiteviewApi(), starttime, endtime, mac, ip);
				createTableItem(list);
			}
		});
		
		table = new Table(c, SWT.NONE);
		FormData table_fd = new FormData();
		table_fd.top = new FormAttachment(select, 5);
		table_fd.left = new FormAttachment(0);
		table_fd.right = new FormAttachment(100);
		table_fd.bottom = new FormAttachment(100);
		table.setLayoutData(table_fd);
		
		table.setHeaderVisible(true);
		topSection.setClient(c);
		ControlUtils.createTableColunms(table, new String[] { "原ip", "原mac", "现ip", "现mac", "设备名称", "设备类型", "监测时间" },
				new int[] { 200, 200, 200, 200, 200, 200, 200 });
		topSection.layout();
	}

	@Override
	public void setFocus() {
	}

	public void createTableItem(List<Map<String, String>> list) {
		table.removeAll();
		for (Map<String, String> map : list) {
			TableItem tb = new TableItem(table, SWT.NONE);
			String s[] = new String[7];
			s[0] = map.get("oldIp");
			s[1] = map.get("oldMac");
			s[2] = map.get("newIp");
			s[3] = map.get("newMac");
			s[4] = map.get("svgName");
			s[5] = map.get("svgType");
			s[6] = map.get("createddatetime");
			tb.setText(s);
		}
	}
	
	public void gettimebeforh(int i) {
		Calendar can = Calendar.getInstance();
		SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		endtime = sim.format(can.getTime());

		can.add(Calendar.HOUR_OF_DAY, -i);
		starttime = sim.format(can.getTime());
	}
}
