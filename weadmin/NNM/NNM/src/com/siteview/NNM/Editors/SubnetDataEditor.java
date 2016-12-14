package com.siteview.NNM.Editors;

import java.awt.Font;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.EditorPart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.TextAnchor;

import com.siteview.NNM.dialogs.table.ExportAssestInfo;
import com.siteview.NNM.dialogs.table.ImageKeys;
import com.siteview.NNM.uijobs.JobUtils;
import com.siteview.NNM.util.NNMContextMenu;
import com.siteview.NNM.util.NNMTreeOperateUtils;
import com.siteview.monitor.tools.TableColumnSortor;
import com.siteview.nnm.data.model.svNode;
import com.siteview.utils.control.ControlUtils;

import core.dashboards.ChartPanel;
import net.sf.json.JSONArray;

public class SubnetDataEditor extends EditorPart {
	public static final String ID="SubnetdataEditor";
	private Table table;
	Section topSection;
	public Section ipSection;
	Section chartSection;
	Composite c;
	Composite c1;
	List<String> listip=new ArrayList<String>();
	CTabFolder tabFolder;
	ToolBar toolBar;
	ScrolledForm form;
	private ToolItem all;
	private ToolItem good;
	private ToolItem error;
	private ToolItem export;
	
	private ToolBar toptoolBar;
	private ToolItem occupancy;
	private ToolItem onoccupancy;
	private ToolItem onping;
	ChartPanel chartpanel1;
	public SubnetDataEditor() {
	}

	@Override
	public void doSave(IProgressMonitor monitor) {

	}

	@Override
	public void doSaveAs() {

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
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
		tabFolder = new CTabFolder(parent, SWT.FLAT | SWT.TOP);
		Composite subnetdatacom = new Composite(tabFolder, SWT.NONE);
		CTabItem item = new CTabItem(tabFolder, SWT.NULL);
		item.setText("子网设备");
		item.setControl(subnetdatacom);
		subnetdatacom.setLayout(new FillLayout());
		tabFolder.setSelection(0);
		
//		Composite composite = new Composite(tabFolder, SWT.NONE);
		
		CTabItem alarmItem = new CTabItem(tabFolder, SWT.NULL);
		alarmItem.setText("ip占用情况");
		Composite cc=new Composite(tabFolder, SWT.NONE);
		cc.setLayout(new FormLayout());
		
		FormToolkit ipformToolkit = new FormToolkit(tabFolder.getDisplay());
		chartSection=ipformToolkit.createSection(cc, Section.TITLE_BAR);
		chartSection.setText("图表");
		FormData ch_fd=new FormData();
		ch_fd.bottom=new FormAttachment(0,250);
		ch_fd.top=new FormAttachment(0);
		ch_fd.left=new FormAttachment(0);
		ch_fd.right=new FormAttachment(100);
		chartSection.setLayoutData(ch_fd);
		chartSection.setLayout(new FillLayout());
		c1=new Composite(chartSection, SWT.NONE);
		c1.setLayout(new FillLayout());
		
		ipSection = ipformToolkit.createSection(cc, Section.TITLE_BAR);
		ipSection.setText(this.getPartName());
		ipSection.setLayout(new FillLayout());
		FormData ip_fd=new FormData();
		ip_fd.bottom=new FormAttachment(100);
		ip_fd.top=new FormAttachment(0,251);
		ip_fd.left=new FormAttachment(0);
		ip_fd.right=new FormAttachment(100);
		ipSection.setLayoutData(ip_fd);
		
		alarmItem.setControl(cc);
		form = ipformToolkit.createScrolledForm(ipSection);
		c = form.getBody();
		ColumnLayout taw=new ColumnLayout();
		taw.maxNumColumns=8;
		c.setLayout(taw);
		createMonitorToolBar();
		ipSection.setClient(form);
		
		
		FormToolkit formToolkit = new FormToolkit(subnetdatacom.getDisplay());
		topSection = formToolkit.createSection(subnetdatacom, Section.TITLE_BAR);
		topSection.setText(this.getPartName());
		
		topSection.setLayout(new FillLayout());
		table = new Table(topSection, SWT.NONE);
		table.setHeaderVisible(true);
//		table.setLinesVisible(true);
		topSection.setClient(table);
		new NNMContextMenu(table, 3);
		List<TableColumn> tableColumns = ControlUtils.createTableColunms(table,
				new String[] { "IP地址", "设备名称", "设备类型", "mac地址", "厂商" }, new int[] { 200, 300, 300, 200, 200 });
		for (TableColumn column : tableColumns) {
			TableColumnSortor.addStringSorter(table, column);
		}
		topSection.layout();
		createTableItem();
		c.layout();
		ipSection.layout();
		form.reflow(true);
	}

	private void createMonitorToolBar() {
		ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT | SWT.WRAP);
		toolBar = toolBarManager.createControl(ipSection);
		good = new ToolItem(toolBar, SWT.RADIO);
		good.setText("占用");
		good.setSelection(true);
		good.setImage(ImageKeys.images.get(ImageKeys.GOOD));
		good.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (good.getSelection()) {
					error.setSelection(false);
					good.setSelection(true);
					all.setSelection(false);
					createc();
				}
			}
		});
		good.setSelection(true);
		
		error = new ToolItem(toolBar, SWT.RADIO);
		error.setText("未占用");
		error.setSelection(true);
		error.setImage(ImageKeys.images.get(ImageKeys.NODATA));
		error.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (error.getSelection()) {
					error.setSelection(true);
					good.setSelection(false);
					all.setSelection(false);
					createc();
				}
			}
		});
		error.setSelection(false);
		
		all = new ToolItem(toolBar, SWT.RADIO);
		all.setText("全部");
		all.setSelection(true);
		all.setImage(ImageKeys.images.get(ImageKeys.ALL));
		all.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (all.getSelection()) {
					error.setSelection(false);
					good.setSelection(false);
					all.setSelection(true);
					createc();
				}
			}
		});
		all.setSelection(false);
		
		export = new ToolItem(toolBar, SWT.NONE);
		export.setText("导出");
		export.setSelection(true);
		export.setImage(ImageKeys.images.get(ImageKeys.EXPORT));
		export.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int ipcount=0;
				String ip=ipSection.getText();
				if(ip.contains("("))
					ipcount=Integer.parseInt(ip.substring(ip.indexOf("/")+1,ip.indexOf("(")));
				else
					ipcount=Integer.parseInt(ip.substring(ip.indexOf("/")+1));
				ipcount=(int) Math.pow(2, 32-ipcount)-1;
				
				String [] sum={listip.size()+"",(ipcount-listip.size())+""};
				String name="占用ip";
				if(all.getSelection()){
					name="全部ip";
				}else if(good.getSelection()){
					name="占用ip";
				}else if(error.getSelection()){
					name="非占用ip";
				}
				File excel_file = ExportAssestInfo.createExcel(c.getChildren(), "ips", 8,sum,name);
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
//				MsgBox.ShowConfirm("正在火热开发中...敬请期待");
			}
		});
		ipSection.setTextClient(toolBar);
		
		
		ToolBarManager toolBarManager0 = new ToolBarManager(SWT.FLAT | SWT.WRAP);
		toptoolBar = toolBarManager0.createControl(chartSection);
		occupancy = new ToolItem(toptoolBar, SWT.READ_ONLY);
		occupancy.setText("占用ip且能ping通");
		occupancy.setSelection(true);
		occupancy.setImage(ImageKeys.images.get(ImageKeys.GOOD));
//		occupancy.setEnabled(false);
		
		onping = new ToolItem(toptoolBar, SWT.READ_ONLY);
		onping.setText("占用ip且ping不通");
		onping.setSelection(true);
		onping.setImage(ImageKeys.images.get(ImageKeys.ILLOGICAL));
//		onping.setEnabled(false);
		
		onoccupancy = new ToolItem(toptoolBar, SWT.READ_ONLY);
		onoccupancy.setText("未占用的ip");
		onoccupancy.setSelection(true);
		onoccupancy.setImage(ImageKeys.images.get(ImageKeys.NODATA));
//		onoccupancy.setEnabled(false);
		
		chartSection.setTextClient(toptoolBar);
	}
	
	private String getUrl(String token, String filename) {
		StringBuffer url = new StringBuffer();
		url.append(RWT.getServiceManager().getServiceHandlerUrl("DownloadSigarHandler"));
		url.append("&").append("file").append("=").append(token).append("&").append("filename").append("=").append(filename);
		return RWT.getResponse().encodeURL(url.toString());
	}
	
	@Override
	public void setFocus() {
		this.setPartName(this.getEditorInput().getName());
		topSection.setText(this.getEditorInput().getName());
	}

	public void createTableItem() {
		listip.clear();
		for (TableItem tb : table.getItems())
			tb.dispose();
		List<svNode> svnodes = ((SubnetDataEditorInput) this.getEditorInput()).getSvnodes();
		String subname = this.getPartName();
		List<Map<String, Object>> list = NNMTreeOperateUtils.getSubNetData(JSONArray.fromObject(svnodes).toString(),
				subname);
		for (Map<String, Object> map : list) {
			TableItem tableitem = new TableItem(table, SWT.NONE);
			String[] s = new String[5];
			s[0] = (String) map.get("ip");
			s[1] = (String) map.get("name");
			s[2] = (String) map.get("devicetype");
			s[3] = (String) map.get("mac");
			s[4] = (String) map.get("firm");
			svNode sv = (svNode) map.get("data");
			tableitem.setText(s);
			tableitem.setData(sv);
			listip.add(s[0]);
		}
		createc();
	}
	
	private void addLabel(List<String> ips, String imageKey, List<CLabel> clabels) {
		for (String ip_ : ips) {
			CLabel cla = new CLabel(c, SWT.NONE);
			cla.setText(ip_);
			cla.setImage(ImageKeys.images.get(imageKey));
			if (clabels != null)
				clabels.add(cla);
		}
	}
	
	DefaultCategoryDataset ds;
	public static Color goodBtnColor = new Color(Display.getCurrent(), 0, 255, 0);
	public static Font font = new java.awt.Font("SimSun", SWT.NONE, 14);
	public String oldendip = "";

	public void createc() {
		changeIpsection();
		List<CLabel> clabels = new ArrayList<CLabel>();
		List<String> ips = new ArrayList<String>();
		ipSection.setText(this.getPartName());
		for (Control con : c.getChildren())
			con.dispose();
		String ip = this.getPartName();
		Map<String, List<String>> map = null;
		if (error.getSelection()) {
			map = NNMTreeOperateUtils.getSubNetIpOccupancyData(ip, "nodata", listip);
			if (map.get("nodata") != null) {
				ips = map.get("nodata");
				addLabel(ips, ImageKeys.NODATA, null);
			}
		} else if (all.getSelection()) {
			map = NNMTreeOperateUtils.getSubNetIpOccupancyData(ip, "all", listip);
			List<String> goodList = map.get("good");
			List<String> nodataList = map.get("nodata");
			if (goodList != null) {
				ips = goodList;
				addLabel(ips, ImageKeys.ILLOGICAL, clabels);
				if (nodataList != null) {
					addLabel(nodataList, ImageKeys.NODATA, null);
					ips.addAll(nodataList);
				}
			} else {
				if (nodataList != null) {
					ips = nodataList;
					addLabel(nodataList, ImageKeys.NODATA, null);
				}
			}
		} else if (good.getSelection()) {
			map = NNMTreeOperateUtils.getSubNetIpOccupancyData(ip, "good", listip);
			if (map.get("good") != null) {
				ips = map.get("good");
				addLabel(ips, ImageKeys.ILLOGICAL, clabels);
			}
		}
		if (map != null) {
			List<String> endipList = map.get("endip");
			List<String> ipcountList = map.get("ipcount");
			if (endipList != null && endipList.size() > 0 && ipcountList != null && ipcountList.size() > 0) {
				String endip = endipList.get(0);
				if (oldendip.length() == 0 || !oldendip.equals(endip)) {
					oldendip = endip;
					createchart(Integer.parseInt(ipcountList.get(0)));
				}
			}
		}
		form.reflow(true);
		if (good.getSelection() || all.getSelection()) {
			int[] is = new int[] { 0, 0 };
			JobUtils job = new JobUtils();
			job.pingIp(Display.getCurrent(), clabels, ips, ds, is, this);
		}
	}
	
	public void changeIpsection(){
		String select="占用ip";
		if(good.getSelection()){
			select="占用ip";
		}else if(all.getSelection()){
			select="全部ip";
		}else if(error.getSelection()){
			select="非占用ip";
		}
		ipSection.setText(this.getPartName()+"("+select+")");
	}
	
	public void createchart(int ipcount){
		for(Control con:c1.getChildren())
			con.dispose();
//		int n=Integer.parseInt(endip.substring(endip.lastIndexOf(".")+1));
		DefaultPieDataset dataset = new DefaultPieDataset();   
		dataset.setValue("占用ip", listip.size());   
		dataset.setValue("未占用ip", ipcount-listip.size());   
		JFreeChart chart = ChartFactory.createPieChart("Ip占用情况图", dataset, true, true, false); 
		TextTitle textTitle = chart.getTitle();   
		textTitle.setFont(font);
		
		chart.addSubtitle(new TextTitle(this.getPartName()));  
		PiePlot pieplot = (PiePlot) chart.getPlot();  
		pieplot.setCircular(true);
		pieplot.setSectionPaint(0, java.awt.Color.GREEN);
		pieplot.setSectionPaint(1, java.awt.Color.GRAY);
		pieplot.setBackgroundPaint(java.awt.Color.WHITE);// 设置背景颜色
		Font smallFont = new Font("SimSun", SWT.NONE, 12);
		LegendTitle legend = chart.getLegend();
		if (legend != null) {
			legend.setItemFont(smallFont);
		}
		pieplot.setLabelFont(smallFont);
		StandardPieSectionLabelGenerator standarPieIG = new StandardPieSectionLabelGenerator("{0}:({1},{2})", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance());  
		pieplot.setLabelGenerator(standarPieIG);
		
		ChartPanel chartpanel=new ChartPanel(c1, SWT.NONE, chart);
		chartpanel.pack();
		chartpanel.layout();
		
		 ds = new DefaultCategoryDataset();  
	     ds.addValue(listip.size(), "占用ip","占用ip");  
	     ds.addValue(ipcount-listip.size(), "未占用ip", "未占用ip");  
	     JFreeChart chart0 = ChartFactory.createBarChart3D("ip分布情况","ip分布情况","分布",ds,  
                  PlotOrientation.VERTICAL,false,false,false); 
	     TextTitle textTitle0 = chart0.getTitle();   
	     textTitle0.setFont(font);
		 LegendTitle l = chart.getLegend();
	     l.setItemFont(font);//设置注释字体
	     CategoryPlot plot = (CategoryPlot) chart0.getPlot();
	     plot.getRangeAxis().setLabelFont(font);//设置y轴字体
         plot.getRangeAxis().setTickLabelFont(font);//设置刻度字体
         plot.getDomainAxis().setLabelFont(font);//设置X轴字体
         plot.getDomainAxis().setTickLabelFont(font);//设置刻度字体
         plot.setDomainGridlinesVisible(true);
         plot.setBackgroundPaint(java.awt.Color.WHITE);// 设置背景颜色
         BarRenderer3D renderer = (BarRenderer3D) plot.getRenderer(); 
		renderer.setSeriesPaint(0, java.awt.Color.GREEN);
		renderer.setBaseItemLabelsVisible(true);
		renderer.setSeriesPaint(1, java.awt.Color.GRAY);
		chartpanel1=new ChartPanel(c1, SWT.NONE, chart0);
		renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());//显示每个柱的数值 
		renderer.setBaseItemLabelsVisible(true); 
		//注意：此句很关键，若无此句，那数字的显示会被覆盖，给人数字没有显示出来的问题 
		renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition( 
		ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_CENTER)); 
		renderer.setItemLabelAnchorOffset(10D);// 设置柱形图上的文字偏离值 
		renderer.setItemLabelsVisible(true); 
		//设置bar的最小宽度，以保证能显示数值
		renderer.setMinimumBarLength(0.1);
		renderer.setMaximumBarWidth(1);
		renderer.setItemLabelAnchorOffset(0.2);
		chartpanel1.pack();
		chartpanel1.layout();
		chartSection.setClient(c1);
		chartSection.layout();
	}
	
	public void getchart(){
//		for(Control c:chartpanel1.getChildren())
//			c.dispose();
		chartpanel1.dispose();
		JFreeChart chart0=ChartFactory.createBarChart3D("ip分布情况","ip分布情况","分布",ds,  
                PlotOrientation.VERTICAL,false,false,false); 
	     TextTitle textTitle0 = chart0.getTitle();   
	     textTitle0.setFont(font);
	     CategoryPlot plot = (CategoryPlot) chart0.getPlot();
	     plot.getRangeAxis().setLabelFont(font);//设置y轴字体
       plot.getRangeAxis().setTickLabelFont(font);//设置刻度字体
       plot.getDomainAxis().setLabelFont(font);//设置X轴字体
       plot.getDomainAxis().setTickLabelFont(font);//设置刻度字体
       plot.setBackgroundPaint(java.awt.Color.WHITE);// 设置背景颜色
       plot.getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
//       plot.getRangeAxis().setStandardTickUnits(createTickUnitSource());
       BarRenderer3D renderer = (BarRenderer3D) plot.getRenderer(); 
		renderer.setSeriesPaint(0, java.awt.Color.GREEN);
		renderer.setSeriesPaint(1, java.awt.Color.GRAY);
		renderer.setSeriesPaint(2, java.awt.Color.GREEN);
		renderer.setSeriesPaint(3, java.awt.Color.YELLOW);
		renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());//显示每个柱的数值 
		renderer.setBaseItemLabelsVisible(true); 
		//注意：此句很关键，若无此句，那数字的显示会被覆盖，给人数字没有显示出来的问题 
		renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition( 
		ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_CENTER)); 
		renderer.setItemLabelAnchorOffset(10D);// 设置柱形图上的文字偏离值 
		renderer.setItemLabelsVisible(true); 
		renderer.setItemLabelAnchorOffset(0);
		//设置bar的最小宽度，以保证能显示数值
		renderer.setMinimumBarLength(0.2);
		//最大宽度
		renderer.setMaximumBarWidth(0.07);
		chartpanel1=new ChartPanel(c1, SWT.NONE, chart0);
		chartpanel1.pack();
		chartpanel1.layout();
		c1.layout();
	}
	public static TickUnitSource createTickUnitSource(){
		TickUnits units = new TickUnits();
        
        DecimalFormat df5 = new DecimalFormat("0.000");
        DecimalFormat df6 = new DecimalFormat("0.00");
        DecimalFormat df7 = new DecimalFormat("0.0");
        DecimalFormat df8 = new DecimalFormat("#,##0");
        DecimalFormat df9 = new DecimalFormat("#,###,##0");
        DecimalFormat df10 = new DecimalFormat("#,###,###,##0");
        units.add(new NumberTickUnit(0.001, df5, 2));
        units.add(new NumberTickUnit(0.01, df6, 2));
        units.add(new NumberTickUnit(0.1, df7, 2));
        units.add(new NumberTickUnit(1, df8, 2));
        units.add(new NumberTickUnit(10, df8, 2));
        units.add(new NumberTickUnit(100, df8, 2));
        units.add(new NumberTickUnit(1000, df8, 2));
        units.add(new NumberTickUnit(10000, df8, 2));
        units.add(new NumberTickUnit(100000, df8, 2));
        units.add(new NumberTickUnit(1000000, df9, 2));
        units.add(new NumberTickUnit(10000000, df9, 2));
        units.add(new NumberTickUnit(100000000, df9, 2));
        units.add(new NumberTickUnit(1000000000, df10, 2));
        units.add(new NumberTickUnit(10000000000.0, df10, 2));
        units.add(new NumberTickUnit(100000000000.0, df10, 2));

        units.add(new NumberTickUnit(0.025, df5, 5));
        units.add(new NumberTickUnit(0.25, df6, 5));
        units.add(new NumberTickUnit(2.5, df7, 5));
        units.add(new NumberTickUnit(25, df8, 5));
        units.add(new NumberTickUnit(250, df8, 5));
        units.add(new NumberTickUnit(2500, df8, 5));
        units.add(new NumberTickUnit(25000, df8, 5));
        units.add(new NumberTickUnit(250000, df8, 5));
        units.add(new NumberTickUnit(2500000, df9, 5));
        units.add(new NumberTickUnit(25000000, df9, 5));
        units.add(new NumberTickUnit(250000000, df9, 5));
        units.add(new NumberTickUnit(2500000000.0, df10, 5));
        units.add(new NumberTickUnit(25000000000.0, df10, 5));
        units.add(new NumberTickUnit(250000000000.0, df10, 5));

        units.add(new NumberTickUnit(0.005, df5, 5));
        units.add(new NumberTickUnit(0.05, df6, 5));
        units.add(new NumberTickUnit(0.5, df7, 5));
        units.add(new NumberTickUnit(5L, df8, 5));
        units.add(new NumberTickUnit(50L, df8, 5));
        units.add(new NumberTickUnit(500L, df8, 5));
        units.add(new NumberTickUnit(5000L, df8, 5));
        units.add(new NumberTickUnit(50000L, df8, 5));
        units.add(new NumberTickUnit(500000L, df8, 5));
        units.add(new NumberTickUnit(5000000L, df9, 5));
        units.add(new NumberTickUnit(50000000L, df9, 5));
        units.add(new NumberTickUnit(500000000L, df9, 5));
        units.add(new NumberTickUnit(5000000000L, df10, 5));
        units.add(new NumberTickUnit(50000000000L, df10, 5));
        units.add(new NumberTickUnit(500000000000L, df10, 5));
        return units;
	}
}
