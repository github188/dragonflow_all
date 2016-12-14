package com.siteview.NNM.Editors;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.EditorPart;

import com.siteview.NNM.dialogs.table.ExportAssestInfo;
import com.siteview.NNM.uijobs.MyTimer;
import com.siteview.ecc.kernel.core.EquipmentUnifiedMonitorManager;
import com.siteview.ecc.kernel.equipmentunifiedmonitor.EquipmentUnifiedMonitor;
import com.siteview.snmpinterface.entities.FlowData;
import com.siteview.utils.control.ControlUtils;

public class TopNetWorkEditor extends EditorPart {
	public final static String ID = "TopNetWorkEditor";
	private FormToolkit formToolkit;
	private ScrolledForm form;
	private Composite body;

	private Composite composite;
	private Section topSection;
	private Section inSection;
	private Section outSection;
	private Section inErrorSection;
	private Section outErrorSection;
	private Section inUsageSection;
	private Section outUsageSection;
	private Table intable;
	private Table outtable;
	private Table inerrortable;
	private Table outerrortable;
	private Table inusagetable;
	private Table outusagetable;
	private Comparator<String> comparator;
	private Timer timer;
	private MyTimer myTime;
	private TopNetWorkEditor top;
	
	public TopNetWorkEditor() {
		comparator = new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				String s1 = "0";
				String s2 = "0";
				if (o1 == null || o2 == null || o1.trim().length() == 0 || o2.trim().length() == 0)
					return 0;
				try {
					if (o1.contains("="))
						s1 = o1.substring(o1.indexOf("=") + 1);
					if (o2.contains("="))
						s2 = o2.substring(o2.indexOf("=") + 1);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				try {
					if (Double.parseDouble(s1) < Double.parseDouble(s2))
						return 1;
					if (Double.parseDouble(s1) == Double.parseDouble(s2))
						return 0;
				} catch (Exception e) {
				}
				return -1;
			}
		};
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
		top = this;
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
//		parent.setLayout(new FormLayout());
		parent.setLayout(new FillLayout());
		formToolkit = new FormToolkit(parent.getDisplay());
		Section descSection = ControlUtils.createSectionContainsDesc(formToolkit, parent,
				Section.TITLE_BAR | Section.EXPANDED | Section.DESCRIPTION ,"接口排名",
				"根据查询条件,查看网络设备接口不同状态排名情况");
		descSection.setLayout(new FillLayout());
		Composite composite_=new Composite(descSection, SWT.NONE);
		composite_.setLayout(new FormLayout());
//		GridLayoutFactory.fillDefaults().numColumns(1).extendedMargins(5, 0, 0, 0).spacing(5, 5).applyTo(composite);
		descSection.setClient(composite_);
		
		
		createTopSection(composite_);
		// createMonitorToolBar();
		composite = new Composite(composite_, SWT.NONE);
		composite.setLayout(new FillLayout());
		FormData c1_fd = new FormData();
		c1_fd.left = new FormAttachment(parent, -5);
		c1_fd.right = new FormAttachment(100);
		c1_fd.top = new FormAttachment(topSection);
		c1_fd.bottom = new FormAttachment(100);
		composite.setLayoutData(c1_fd);

		// 创建表单对象
		form = formToolkit.createScrolledForm(composite);
		form.setLayout(new FillLayout());
		body = form.getBody();
		body.setLayout(new GridLayout());
		createInTable();
		createOutTable();
		createInUsageTable();
		createOutUsageTable();
		createInErrorTable();
		createOutErrorTable();
		getData();
		createTableItem();
	}

	public void createTableItem() {
		Collections.sort(in, comparator);
		Collections.sort(out, comparator);
		Collections.sort(inerror, comparator);
		Collections.sort(outerror, comparator);
		Collections.sort(inusage, comparator);
		Collections.sort(outusage, comparator);
		sortMap(inMap);
		sortMap(outMap);
		sortMap(inErrorMap);
		sortMap(outErrorMap);
		sortMap(inErrorMap);
		sortMap(outErrorMap);
		intable.removeAll();
		outtable.removeAll();
		inerrortable.removeAll();
		outerrortable.removeAll();
		inusagetable.removeAll();
		outusagetable.removeAll();

		Map<String, EquipmentUnifiedMonitor> eqs = EquipmentUnifiedMonitorManager.getUnifiedMonitors();
		for (int i = 0; i < in.size(); i++) {
			if (i >= (int) com.getData(com.getSelectionIndex() + ""))
				break;
			String inStr = in.get(i);
			// createItem(eqs, instr, intable, 1);

			String outStr = out.get(i);
			String inUsageStr = inusage.get(i);
			String outUsageStr = outusage.get(i);
			String inErrorStr = inerror.get(i);
			String outErrorStr = outerror.get(i);
			createItem(eqs, inStr, outStr, inUsageStr, outUsageStr, inErrorStr, outErrorStr);
		}
		form.reflow(true);
	}

	private void createItem(Map<String, EquipmentUnifiedMonitor> eqs, String inStr, String outStr, String inUsageStr,
			String outUsageStr, String inErrorStr, String outErrorStr) {
		String[] text = new String[9];
		String ifname = inStr.substring(0, inStr.indexOf("="));

		String key = ifname.substring(0, ifname.lastIndexOf("."));
		EquipmentUnifiedMonitor equm = eqs.get(key);
		if (equm == null)
			return;
		Map<String, Object> sessionMap = equm.getValueMap();
		if (sessionMap == null)
			return;
		text[0] = equm.getEquipmentDataMap().get("ServerAddress") == null ? ""
				: equm.getEquipmentDataMap().get("ServerAddress").toString();
		Map<String, Map<String, FlowData>> valueMap = (Map<String, Map<String, FlowData>>) sessionMap.get("mapValue");
		if (valueMap == null)
			return;
		Map<String, FlowData> dataMap = valueMap.get(key);
		String oid = ifname.substring(ifname.lastIndexOf(".") + 1, ifname.length());
		FlowData data = dataMap.get(oid);
		if (data == null)
			return;
		String inspeed = inStr.substring(inStr.indexOf("=") + 1, inStr.length());
		String outspeed = outMap.get(outStr.substring(0, outStr.indexOf("=")));
		String inUtilization = inUsageMap.get(inUsageStr.substring(0, inUsageStr.indexOf("=")));
		String outUtilization = outUsageMap.get(outUsageStr.substring(0, outUsageStr.indexOf("=")));
		String inLossRate = inErrorMap.get(inErrorStr.substring(0, inErrorStr.indexOf("=")));
		String outLossRate = outErrorMap.get(outErrorStr.substring(0, outErrorStr.indexOf("=")));
		ifname = ifname.substring(ifname.lastIndexOf(".") + 1);
		// str = str.substring(str.indexOf("=") + 1);
		text[1] = data.getName();
		text[2] = inspeed;
		text[3] = outspeed;
		text[4] = inUtilization;
		text[5] = outUtilization;
		text[6] = inLossRate;
		text[7] = outLossRate;
		TableItem tableitem = new TableItem(intable, SWT.NONE);
		tableitem.setText(text);

		text[2] = outspeed;
		text[3] = inspeed;
		text[4] = inUtilization;
		text[5] = outUtilization;
		text[6] = inLossRate;
		text[7] = outLossRate;
		tableitem = new TableItem(outtable, SWT.NONE);
		tableitem.setText(text);

		text[2] = inUtilization;
		text[3] = inspeed;
		text[4] = outspeed;
		text[5] = outUtilization;
		text[6] = inLossRate;
		text[7] = outLossRate;
		tableitem = new TableItem(inusagetable, SWT.NONE);
		tableitem.setText(text);

		text[2] = outUtilization;
		text[3] = inspeed;
		text[4] = outspeed;
		text[5] = inUtilization;
		text[6] = inLossRate;
		text[7] = outLossRate;
		tableitem = new TableItem(outusagetable, SWT.NONE);
		tableitem.setText(text);

		text[2] = inLossRate;
		text[3] = inspeed;
		text[4] = outspeed;
		text[5] = inUtilization;
		text[6] = outUtilization;
		text[7] = outLossRate;
		tableitem = new TableItem(inerrortable, SWT.NONE);
		tableitem.setText(text);

		text[2] = outLossRate;
		text[3] = inspeed;
		text[4] = outspeed;
		text[5] = inUtilization;
		text[6] = outUtilization;
		text[7] = inLossRate;
		tableitem = new TableItem(outerrortable, SWT.NONE);
		tableitem.setText(text);
	}

	private void sortMap(Map<String, String> map) {
		List<Map.Entry<String, String>> mappingList = new ArrayList<Map.Entry<String, String>>(map.entrySet());
		Collections.sort(mappingList, new Comparator<Map.Entry<String, String>>() {
			public int compare(Map.Entry<String, String> mapping1, Map.Entry<String, String> mapping2) {
				return mapping1.getValue().compareTo(mapping2.getValue());
			}
		});
	}

	/**
	 * 初始化界面Top空间
	 */
	private void createTopSection(Composite parent) {
		topSection = formToolkit.createSection(parent, Section.TITLE_BAR);
		FormData topFd = new FormData();
		topFd.left = new FormAttachment(0);
		topFd.right = new FormAttachment(100);
		topFd.top = new FormAttachment(0, 5);
		topSection.setLayoutData(topFd);
		topSection.setText("查询条件");

		Composite c = new Composite(topSection, SWT.NONE);
		c.setLayout(new GridLayout(10, false));
		com = new Combo(c, SWT.READ_ONLY);
		com.add("排名前10");
		com.add("排名前20");
		com.add("排名前50");
		com.add("排名前100");
		com.setData("0", 10);
		com.setData("1", 20);
		com.setData("2", 50);
		com.setData("3", 100);
		com.select(0);
		com.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				createTableItem();
			}
		});

		final Button b = new Button(c, SWT.CHECK);
		b.setText("自动刷新");
		b.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (b.getSelection()) {
					if (timer == null)
						timer = new Timer();
					if (myTime == null)
						myTime = new MyTimer(top, Display.getCurrent());
					timer.schedule(myTime, 60 * 1000);
				} else {
					myTime.cancel();
					timer.cancel();
					myTime = null;
					timer = null;
				}
			}

		});

		Button b1 = new Button(c, SWT.NONE);
		b1.setText("刷   新");
		b1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getData();
				createTableItem();
			}
		});

		Button b2 = new Button(c, SWT.NONE);
		b2.setText("导  出");
		b2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				File excel_file = ExportAssestInfo.createExcel(intable, outtable, inerrortable, outerrortable,
						inusagetable, outusagetable);
				try {
					Browser browser = new Browser(Display.getCurrent().getActiveShell(), SWT.NONE);
					browser.setSize(0, 0);
					String url = getUrl(excel_file.getParent() + "/" + excel_file.getName(), excel_file.getName());
					boolean flag = browser.setUrl(url);
					if (flag) {
						return;
					}
				} catch (java.lang.Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		topSection.setClient(c);
	}

	private void createInTable() {
		inSection = formToolkit.createSection(body,
				Section.TWISTIE | Section.TITLE_BAR | Section.LEFT_TEXT_CLIENT_ALIGNMENT);
		inSection.setText("接收速率(Mbit/s)");
		inSection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		inSection.setExpanded(true);

		intable = new Table(inSection, SWT.NONE);
		intable.setHeaderVisible(true);

		String[] names = { "设备ip", "接口名", "接收速率(Mbit/s)", "发送速率(Mbit/s)", "接收带宽比(%)", "发送带宽比(%)", "输入丢包率(%)",
				"输出丢包率(%)" };
		int[] widths = { 200, 200, 130, 130, 130, 130, 130, 130 };
		ControlUtils.createTableColunms(intable, names, widths);

		inSection.setClient(intable);
	}

	private void createOutTable() {
		outSection = formToolkit.createSection(body,
				Section.TWISTIE | Section.TITLE_BAR | Section.LEFT_TEXT_CLIENT_ALIGNMENT);
		outSection.setText("发送速率(Mbit/s)");
		outSection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		outSection.setExpanded(true);

		outtable = new Table(outSection, SWT.NONE);
		outtable.setHeaderVisible(true);

		String[] names = { "设备ip", "接口名", "发送速率(Mbit/s)", "接收速率(Mbit/s)", "接收带宽比(%)", "发送带宽比(%)", "输入丢包率(%)",
				"输出丢包率(%)" };
		int[] widths = { 200, 200, 130, 130, 130, 130, 130, 130 };
		ControlUtils.createTableColunms(outtable, names, widths);

		outSection.setClient(outtable);
	}

	private void createInErrorTable() {
		inErrorSection = formToolkit.createSection(body,
				Section.TWISTIE | Section.TITLE_BAR | Section.LEFT_TEXT_CLIENT_ALIGNMENT);
		inErrorSection.setText("输入丢包率(%)");
		inErrorSection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		inErrorSection.setExpanded(true);

		inerrortable = new Table(inErrorSection, SWT.NONE);
		inerrortable.setHeaderVisible(true);

		String[] names = { "设备ip", "接口名", "输入丢包率(%)", "接收速率(Mbit/s)", "发送速率(Mbit/s)", "接收带宽比(%)", "发送带宽比(%)",
				"输出丢包率(%)" };
		int[] widths = { 200, 200, 130, 130, 130, 130, 130, 130 };
		ControlUtils.createTableColunms(inerrortable, names, widths);

		inErrorSection.setClient(inerrortable);
	}

	private void createOutErrorTable() {
		outErrorSection = formToolkit.createSection(body,
				Section.TWISTIE | Section.TITLE_BAR | Section.LEFT_TEXT_CLIENT_ALIGNMENT);
		outErrorSection.setText("输出丢包率(%)");
		outErrorSection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		outErrorSection.setExpanded(true);

		outerrortable = new Table(outErrorSection, SWT.NONE);
		outerrortable.setHeaderVisible(true);

		String[] names = { "设备ip", "接口名", "输出丢包率(%)", "接收速率(Mbit/s)", "发送速率(Mbit/s)", "接收带宽比(%)", "发送带宽比(%)",
				"输入丢包率(%)" };
		int[] widths = { 200, 200, 130, 130, 130, 130, 130, 130 };
		ControlUtils.createTableColunms(outerrortable, names, widths);

		outErrorSection.setClient(outerrortable);
	}

	private void createInUsageTable() {
		inUsageSection = formToolkit.createSection(body,
				Section.TWISTIE | Section.TITLE_BAR | Section.LEFT_TEXT_CLIENT_ALIGNMENT);
		inUsageSection.setText("接收带宽比(%)");
		inUsageSection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		inUsageSection.setExpanded(true);

		inusagetable = new Table(inUsageSection, SWT.NONE);
		inusagetable.setHeaderVisible(true);

		String[] names = { "设备ip", "接口名", "接收带宽比(%)", "接收速率(Mbit/s)", "发送速率(Mbit/s)", "发送带宽比(%)", "输入丢包率(%)",
				"输出丢包率(%)" };
		int[] widths = { 200, 200, 130, 130, 130, 130, 130, 130 };
		ControlUtils.createTableColunms(inusagetable, names, widths);

		inUsageSection.setClient(inusagetable);
	}

	private void createOutUsageTable() {
		outUsageSection = formToolkit.createSection(body,
				Section.TWISTIE | Section.TITLE_BAR | Section.LEFT_TEXT_CLIENT_ALIGNMENT);
		outUsageSection.setText("发送带宽比(%)");
		outUsageSection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		outUsageSection.setExpanded(true);

		outusagetable = new Table(outUsageSection, SWT.NONE);
		outusagetable.setHeaderVisible(true);

		String[] names = { "设备ip", "接口名", "发送带宽比(%)", "接收速率(Mbit/s)", "发送速率(Mbit/s)", "接收带宽比(%)", "输入丢包率(%)",
				"输出丢包率(%)" };
		int[] widths = { 200, 200, 130, 130, 130, 130, 130, 130 };
		ControlUtils.createTableColunms(outusagetable, names, widths);

		outUsageSection.setClient(outusagetable);
	}

	public Table getIntable() {
		return intable;
	}

	@Override
	public void setFocus() {
	}

	List<String> in = new ArrayList<String>();
	List<String> out = new ArrayList<String>();
	List<String> inerror = new ArrayList<String>();
	List<String> outerror = new ArrayList<String>();
	List<String> inusage = new ArrayList<String>();
	List<String> outusage = new ArrayList<String>();

	private Map<String, String> inMap = new LinkedHashMap<String, String>();
	private Map<String, String> outMap = new LinkedHashMap<String, String>();
	private Map<String, String> inErrorMap = new LinkedHashMap<String, String>();
	private Map<String, String> outErrorMap = new LinkedHashMap<String, String>();
	private Map<String, String> inUsageMap = new LinkedHashMap<String, String>();
	private Map<String, String> outUsageMap = new LinkedHashMap<String, String>();

	public void getData() {
		in.clear();
		out.clear();
		inerror.clear();
		outerror.clear();
		inusage.clear();
		outusage.clear();
		inMap.clear();
		outMap.clear();
		inErrorMap.clear();
		outErrorMap.clear();
		inUsageMap.clear();
		outUsageMap.clear();
		Map<String, EquipmentUnifiedMonitor> eqs = EquipmentUnifiedMonitorManager.getUnifiedMonitors();
		Iterator<String> ite = eqs.keySet().iterator();
		while (ite.hasNext()) {
			String key = ite.next();
			EquipmentUnifiedMonitor equm = eqs.get(key);
			Map<String, Object> sessionMap = equm.getValueMap();
			List<String> list = new ArrayList<String>();
			Map<String, Map<String, FlowData>> valueMap = (Map<String, Map<String, FlowData>>) sessionMap
					.get("mapValue");
			if (valueMap == null)
				continue;
			Map<String, FlowData> oidMap = valueMap.get(key);
			if (oidMap == null)
				continue;
			for (String oid : oidMap.keySet()) {
				if (list.contains(oid))
					continue;
				list.add(oid);
				FlowData data = oidMap.get(oid);
				if (data == null)
					continue;
				String keys = key + "." + oid + "=";
				long portSpeed = data.getPortSpeed();
				String inSpeed = "";
				String outSpeed = "";
				if (portSpeed >= 1000000000) {
					inSpeed = data.getHcInSpeed() + "";
					outSpeed = data.getHcOutSpeed() + "";
				} else {
					BigDecimal bg = new BigDecimal(data.getInSpeed() / 1024);
					inSpeed = bg.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
					bg = new BigDecimal(data.getOutSpeed() / 1024);
					outSpeed = bg.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
				}
				in.add(keys + inSpeed);
				out.add(keys + outSpeed);
				inerror.add(keys + data.getInLossRate());
				outerror.add(keys + data.getOutLossRate());
				String inUtilizationStr = "0.0";
				String outUtilizationStr = "0.0";
				if (portSpeed != 0) {
					double inUtilization = 0;
					double outUtilization = 0;
//					if (portSpeed > 1000000000)
//						portSpeed = 10000000000L;
					if (portSpeed != 0) {
						inUtilization = Double.parseDouble(inSpeed) / (portSpeed / (1000 * 1000)) * 100;
						outUtilization = Double.parseDouble(outSpeed) / (portSpeed / (1000 * 1000)) * 100;
					}
					if (!Double.isNaN(inUtilization) && !Double.isInfinite(inUtilization)) {
						BigDecimal bg = new BigDecimal(inUtilization);
						inUtilizationStr = bg.setScale(3, BigDecimal.ROUND_HALF_UP).toString();
					}
					if (!Double.isNaN(inUtilization) && !Double.isInfinite(inUtilization)) {
						BigDecimal bg = new BigDecimal(outUtilization);
						outUtilizationStr = bg.setScale(3, BigDecimal.ROUND_HALF_UP).toString();
					}
				}
				inusage.add(keys + inUtilizationStr);
				outusage.add(keys + outUtilizationStr);

				inMap.put(key + "." + oid, inSpeed);
				outMap.put(key + "." + oid, outSpeed);
				inErrorMap.put(key + "." + oid, data.getInLossRate() + "");
				outErrorMap.put(key + "." + oid, data.getOutLossRate() + "");
				inUsageMap.put(key + "." + oid, inUtilizationStr);
				outUsageMap.put(key + "." + oid, outUtilizationStr);
			}
		}
	}

	private Combo com;

	private String getUrl(String token, String filename) {
		StringBuffer url = new StringBuffer();
		url.append(RWT.getServiceManager().getServiceHandlerUrl("DownloadSigarHandler"));
		url.append("&").append("file").append("=").append(token).append("&").append("filename").append("=")
				.append(filename);
		return RWT.getResponse().encodeURL(url.toString());
	}
}
