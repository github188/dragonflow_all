package com.siteview.NNM.dialogs;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import siteview.windows.forms.MsgBox;

import com.siteview.NNM.Activator;
import com.siteview.NNM.Editors.TopoManage;
import com.siteview.NNM.Views.NNMTreeView;
import com.siteview.NNM.aas.NNMPermissions;
import com.siteview.NNM.modles.SubChartModle;
import com.siteview.NNM.modles.TopoModle;
import com.siteview.NNM.uijobs.refreshDevice;
import com.siteview.NNM.uijobs.refreshWorker;
import com.siteview.NNM.util.DevicePanelUtils;
import com.siteview.ecc.authorization.ItossAuthorizeServiceImpl;
import com.siteview.ecc.authorization.PermissionFactory;
import com.siteview.ecc.constants.Operation;
import com.siteview.ecc.constants.Resource;
import com.siteview.nnm.data.ConfigDB;
import com.siteview.nnm.data.DBManage;
import com.siteview.nnm.data.EntityManager;
import com.siteview.nnm.data.FlowDataManager;
import com.siteview.nnm.data.PortManage;
import com.siteview.nnm.data.VisioDB;
import com.siteview.nnm.data.model.TopoChart;
import com.siteview.nnm.data.model.entity;
import com.siteview.nnm.data.model.svNode;
import com.siteview.snmpinterface.EquipmentQueuer;
import com.siteview.snmpinterface.entities.FlowData;
import com.siteview.svgmap.ShapeItem;
import com.siteview.svgmap.VisioImporter;
import com.siteview.svgmap.VisioMap;
import com.siteview.topo.NodeEvents;
import com.siteview.topo.TopoEvents;

public class DevicePanel extends Dialog {

	private int width = 400;
	private int height = 300;
	String nodeid = "";
	String sysoid = "";
	svNode entityobj;

	int[] statuss = null;
	String[] tooltips = null;
	private String portkey="";
	private String commm ="public";
	int rowCount=0;
	int models = 0;
	int svhide = 0;
	int rr=-1;

	/**
	 * Create the dialog.
	 *
	 * @param parentShell
	 */
	public DevicePanel(Shell parentShell, String nodeid, String lab) {
		super(parentShell);
		this.nodeid = nodeid;
		setShellStyle(SWT.MAX | SWT.RESIZE);
		TopoChart tochart = com.siteview.nnm.data.DBManage.Topos.get(lab);
		this.entityobj = (svNode) tochart.getNodes().get(nodeid);
		sysoid = entityobj.getProperys().get("sysObjectID");
		portkey= entityobj.getLocalip()+entityobj.getProperys().get("Community");
		commm= entityobj.getProperys().get("Community");

	}

	public DevicePanel(Shell parentShell, svNode entityobj) {
		super(parentShell);
		this.nodeid = entityobj.getSvid();
		setShellStyle(SWT.MAX | SWT.RESIZE);
		this.entityobj = entityobj;
		sysoid = entityobj.getProperys().get("sysObjectID");

	}

	protected void configureShell(Shell newShell) {
		// newShell.setSize(450, 320);
		// newShell.setLocation(400, 175);
		newShell.setText("设备面板图("+this.entityobj.getLocalip()+")");
		super.configureShell(newShell);
	}
	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(final Composite parent) {
		final VisioMap visioChart = new VisioMap(parent, SWT.NONE);
		parent.setLayout(new GridLayout(1, false));
		visioChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		// 处理ui事件
		visioChart.addListener(SWT.Selection, new Listener() {
			private static final long serialVersionUID = 1L;
			public void handleEvent(Event event) {
				String eventag = event.text;
				String portindex = ((JsonValue) event.data).asString();
				portindex = portindex.substring(1);
				System.out.println(portindex);
				Connection conn = ConfigDB.getConn();
				String sql = "select pindex from ports where (porttype='6' or porttype='117') and id='"
						+ nodeid + "'";
				String pindex = "";
				try {
					ResultSet rs = ConfigDB.query(sql, conn);
					int i = 1;
					while (rs.next()) {
						if (portindex.equals(i + "")) {
							pindex = rs.getString("pindex");
							if (pindex == null)
								pindex = "";
							System.out.println(pindex);
							break;
						}
						i = i + 1;
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (Exception ex) {
				}
				ConfigDB.close(conn);
				if (eventag.equals("openport")) {
					PrivateCommDialog cdialog = new PrivateCommDialog(parent
							.getShell(), entityobj, pindex, true);
					cdialog.open();
				} else if (eventag.equals("closeport")) {
					PrivateCommDialog cdialog = new PrivateCommDialog(parent
							.getShell(), entityobj, pindex, false);
					cdialog.open();
				} else if (eventag.equals("deviceip")) {
					svNode obj = null;
					for (String lid : EntityManager.allEdges.keySet()) {
						String lport = EntityManager.allEdges.get(lid)
								.getSinterface();
						String rport = EntityManager.allEdges.get(lid)
								.getTinterface();
						String ldev = EntityManager.allEdges.get(lid)
								.getLsource();
						if (lport.equals(pindex)
								&& ldev.equals(entityobj.getSvid())) {
							entity enode = EntityManager.allEntity
									.get(EntityManager.allEdges.get(lid)
											.getLtarget());
							obj = (svNode) enode;
						}
					}
					if (obj == null) {
						MsgBox.ShowError("提示", "未查询到端口信息!");
					} else {
						PortDevice pdialog = new PortDevice(parent.getShell(),
								obj);
						pdialog.open();
					}
				}else if(eventag.toLowerCase().equals("portport")){
					if(rr==-1){
					rr=11;
					PortDialog pdialog=new PortDialog(parent.getShell(),pindex,portkey,entityobj.getLocalip(),commm);
					rr= pdialog.open();
					if (rr==IDialogConstants.OK_ID){
						rr=-1;
					}
					}
				}
			}
		});

		rowCount = DevicePanelUtils.getRowCountBySvid(this.entityobj.getSvid());
		if (VisioDB.visiodb.containsKey(this.sysoid)) {
			Map<String, Integer> vmain = VisioDB.visiodb.get(this.sysoid);
			int xh = vmain.get("xh");
			// VisioImporter vi = new VisioImporter();// 1.3.6.1.4.1.9.1.516.vdx
			// 1.3.6.1.4.1.2011.10.1.80.vdx
			// 1.3.6.1.4.1.9.1.324.vdx
			String svid = DevicePanelUtils.getSvidByXh(xh);
			if(svid!=null && !svid.isEmpty()){
				this.models = Integer.parseInt(svid.substring(6));
			}
			if(this.rowCount>0 && this.models>0){
				this.svhide=this.rowCount/this.models;
			}
			List<Map<String,String>> ltips=new ArrayList<Map<String,String>>();
			ltips = DevicePanelUtils.getVisiopanelDataByXh(xh);

			for(Map<String,String> obj:ltips){
				if(obj.get("cssclass").trim().toLowerCase().equals("cc1")){
					addOneShapeItem(obj,visioChart);
				}else{
					boolean createitme=true;
					if(obj.get("svid").startsWith("svhide")){
						int hdindex=Integer.parseInt(obj.get("svid").substring(6));
						if(this.svhide>0 && hdindex<=this.svhide){
							createitme=false;
						}
						if(this.rowCount==0){
							createitme=false;
						}
					}
					if(createitme){
						addOneShapeItem(obj,visioChart);
					}
				}
			}
			// vi.ImportPage(path+"visio\\1.3.6.1.4.1.9.1.324.vdx", visioChart,
			// 0, 10, 10);
			visioChart.setLayoutData(new GridData((vmain.get("width") + 15),
					(vmain.get("height") + 15)));
			width = vmain.get("width") + 25;
			height = vmain.get("height") + 55;

			parent.getDisplay().timerExec(1000, new Runnable() {
				public void run() {
					getstatus();
					if (statuss != null) {
						visioChart.setStatuss(statuss);
						visioChart.setTooltipdata(tooltips);
					}
					// visioChart.setTooltipdata(dd);
					if(parent.getDisplay().isDisposed())
					parent.getDisplay().timerExec(10000, this);
				}
			});
		} else {
			String filename = DBManage.getPlatformPath() + "visiopanel/"
					+ this.sysoid + ".vdx";
			File file = new File(filename);
			if (file.exists()) {
				VisioImporter vi = new VisioImporter();
				vi.ImportPage(filename, visioChart, 0, 10, 10);
				visioChart.setLayoutData(new GridData((vi.svgwidth + 15),
						(vi.svgheight + 15)));
				parent.getDisplay().timerExec(1000, new Runnable() {
					public void run() {
						getstatus();
						if (statuss != null) {
							visioChart.setStatuss(statuss);
							visioChart.setTooltipdata(tooltips);
						}
						// visioChart.setTooltipdata(dd);
						parent.getDisplay().timerExec(10000, this);
					}
				});
				width = 890;
			} else {

				Connection conn = ConfigDB.getvConn();
				String sql = "select svgtype,transform,itemid,svid,linewidth,fillcolor,linecolor,fontname,fontsize,textanchor,cssclass,ecx,ecy,erx,ery,vvalue from visiopanelauto where factory is null order by id ";
				ResultSet rs = ConfigDB.query(sql, conn);
				Map<String, Map<String, String>> baseshapes = new HashMap<String, Map<String, String>>();
				try {
					int j = 1;
					Map<String, String> base1 = null;
					while (rs.next()) {
						base1 = new HashMap<String, String>();
						int svgtype = rs.getInt("svgtype");
						base1.put("svgtype", svgtype + "");
						String transform = rs.getString("transform");
						if (transform == null)
							transform = "";
						base1.put("transform", transform);
						String itemid = rs.getString("itemid");
						if (itemid == null)
							itemid = "";
						base1.put("itemid", itemid);
						String svid = rs.getString("svid");
						if (svid == null)
							svid = "";
						base1.put("svid", svid);
						int linewidth = rs.getInt("linewidth");
						base1.put("linewidth", linewidth + "");
						String fillcolor = rs.getString("fillcolor");
						base1.put("fillcolor", fillcolor);
						String linecolor = rs.getString("linecolor");
						base1.put("linecolor", linecolor);
						String fontname = rs.getString("fontname");
						if (fontname == null)
							fontname = "";
						base1.put("fontname", fontname);
						String fontsize = rs.getString("fontsize");
						if (fontsize == null)
							fontsize = "";
						base1.put("fontsize", fontsize);
						if (!fontsize.isEmpty()) {
							try {
								String tempv = fontsize.substring(0, 3);
								float emsize = Float.parseFloat(tempv);
								if (emsize < 0.3) {
									continue;
								}
							} catch (Exception ex) {

							}
						}
						String textanchor = rs.getString("textanchor");
						if (textanchor == null)
							textanchor = "";
						base1.put("textanchor", textanchor);
						String cssclass = rs.getString("cssclass");
						if (cssclass == null)
							cssclass = "";
						base1.put("cssclass", cssclass);
						String ecx = rs.getString("ecx");
						if (ecx == null)
							ecx = "";
						base1.put("ecx", ecx);
						String ecy = rs.getString("ecy");
						if (ecy == null)
							ecy = "";
						base1.put("ecy", ecy);
						String erx = rs.getString("erx");
						if (erx == null)
							erx = "";
						base1.put("erx", erx);
						String ery = rs.getString("ery");
						if (ery == null)
							ery = "";
						base1.put("ery", ery);
						String vvalue = rs.getString("vvalue");
						if (vvalue == null)
							vvalue = "";
						base1.put("vvalue", vvalue);
						baseshapes.put(j + "", base1);
						j = j + 1;
					}
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception ex) {

				}
				ConfigDB.close(conn);
				 conn = ConfigDB.getConn();
				sql = "select count(*) as coun from ports where (porttype='6' or porttype='117')  and id='"
						+ this.nodeid + "'";
				rs = ConfigDB.query(sql, conn);
				int coun = 12;
				try {
					while (rs.next()) {
						coun = rs.getInt("coun");
					}
				} catch (Exception ex) {

				}
				int groupsize = 6;// 组大小 两列
				if (coun % 6 != 0 && coun % 8 == 0) {
					groupsize = 8;
				}

				sql = "select pindex,porttype from ports where (porttype='6' or porttype='117')  and id='"
						+ this.nodeid + "'";
				rs = ConfigDB.query(sql, conn);
				List<Integer> rowsss = new ArrayList<Integer>();
				int row1 = 1;
				try {
					int column1 = 1;
					int portindex = 1;
					while (rs.next()) {
						if (row1 == 1) {
							if (!rowsss.contains(1)) {
								rowsss.add(1);
								ShapeItem tempitem = null;
								tempitem = new ShapeItem(visioChart);
								tempitem.setFillcolor("#efefef");
								tempitem.setLinecolor("#0a0a0a");
								tempitem.setLinewidth(1);
								tempitem.setSvgtype(4);
								tempitem.setEcx("30");
								tempitem.setEcy("30");
								tempitem.setErx("430");
								tempitem.setEry("32");
							}
						} else {
							if (!rowsss.contains(row1)) {
								rowsss.add(row1);
								ShapeItem tempitem = null;
								tempitem = new ShapeItem(visioChart);
								tempitem.setFillcolor("#efefef");
								tempitem.setLinecolor("#0a0a0a");
								tempitem.setLinewidth(1);
								tempitem.setSvgtype(4);
								tempitem.setEcx("30");
								tempitem.setEcy(35 * row1 + "");
								tempitem.setErx(430 + "");
								tempitem.setEry("30");
							}
						}
						String pindex = rs.getString("pindex");
						for (String key : baseshapes.keySet()) {
							Map<String, String> mapbase = baseshapes.get(key);
							ShapeItem tempitem = null;
							tempitem = new ShapeItem(visioChart);
							tempitem.setFillcolor(mapbase.get("fillcolor"));
							tempitem.setLinecolor(mapbase.get("linecolor"));
							tempitem.setLinewidth(Integer.parseInt(mapbase
									.get("linewidth")));
							if (!mapbase.get("itemid").isEmpty())
								tempitem.setItemid(mapbase.get("itemid"));
							tempitem.setSvgtype(Integer.parseInt(mapbase
									.get("svgtype")));
							String transform = "";
							int movex = 35 * column1;
							int movey = 35 * row1;
							if (row1 == 1)
								movey = 30;
							tempitem.setTransform("translate(" + movex + ","
									+ movey + ")");
							// if (!mapbase.get("transform").isEmpty())
							// tempitem.setTransform(mapbase.get("transform"));
							if (!mapbase.get("svid").isEmpty()) {
								String svid = mapbase.get("svid");
								if (svid.startsWith("c")) {
									svid = "c" + portindex;
								} else if (svid.startsWith("p")) {
									svid = "p" + portindex;
								}
								tempitem.setSvid(svid);
							}
							if (!mapbase.get("fontname").isEmpty())
								tempitem.setFontname(mapbase.get("fontname"));
							if (!mapbase.get("fontsize").isEmpty())
								tempitem.setFontsize(mapbase.get("fontsize"));
							if (!mapbase.get("textanchor").isEmpty())
								tempitem.setTextanchor(mapbase
										.get("textanchor"));
							if (!mapbase.get("cssclass").isEmpty())
								tempitem.setCssclass(mapbase.get("cssclass"));
							if (!mapbase.get("ecx").isEmpty()) {
								tempitem.setEcx(movex + "");
							}
							if (!mapbase.get("ecy").isEmpty()) {
								tempitem.setEcy(movey + "");
							}
							if (!mapbase.get("erx").isEmpty())
								tempitem.setErx(mapbase.get("erx"));
							if (!mapbase.get("ery").isEmpty())
								tempitem.setEry(mapbase.get("ery"));
							if (!mapbase.get("vvalue").isEmpty())
								tempitem.setValue(mapbase.get("vvalue"));

						}
						portindex = portindex + 1;
						column1 = column1 + 1;
						if (column1 > 12) {
							column1 = 1;
							row1 = row1 + 1;
						}

					}
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception ex) {

				}
				//
				String fact = "huawei1";
				if (this.sysoid.startsWith("1.3.6.1.4.1.9.")) {
					fact = "cisco";
				} else if (this.sysoid.startsWith("1.3.6.1.4.1.2011.")) {
					fact = "huawei";
				} else if (this.sysoid.startsWith("1.3.6.1.4.1.25506.")) {
					fact = "h3c";
				} else if (this.sysoid.startsWith("1.3.6.1.4.1.3902.")) {
					fact = "zte";
				} else if (this.sysoid.startsWith("1.3.6.1.4.1.4881.")) {
					fact = "ruijie";
				}
				sql = "select svgtype,transform,itemid,svid,linewidth,fillcolor,linecolor,fontname,fontsize,textanchor,cssclass,ecx,ecy,erx,ery,vvalue from visiopanelauto where factory ='"
						+ fact + "' ";

				try {

					ResultSet rs1 = ConfigDB.query(sql, conn);
					while (rs1.next()) {

						int svgtype = rs1.getInt("svgtype");

						String transform = rs1.getString("transform");
						if (transform == null)
							transform = "";
						String itemid = rs1.getString("itemid");
						if (itemid == null)
							itemid = "";
						String svid = rs1.getString("svid");
						if (svid == null)
							svid = "";
						int linewidth = rs1.getInt("linewidth");
						String fillcolor = rs1.getString("fillcolor");
						String linecolor = rs1.getString("linecolor");
						String fontname = rs1.getString("fontname");
						if (fontname == null)
							fontname = "";
						String fontsize = rs1.getString("fontsize");
						if (fontsize == null)
							fontsize = "";
						if (!fontsize.isEmpty()) {
							try {
								String tempv = fontsize.substring(0, 3);
								float emsize = Float.parseFloat(tempv);
								if (emsize < 0.3) {
									continue;
								}
							} catch (Exception ex) {

							}
						}
						String textanchor = rs1.getString("textanchor");
						if (textanchor == null)
							textanchor = "";
						String cssclass = rs1.getString("cssclass");
						if (cssclass == null)
							cssclass = "";
						String ecx = rs1.getString("ecx");
						if (ecx == null)
							ecx = "";
						String ecy = rs1.getString("ecy");
						if (ecy == null)
							ecy = "";
						String erx = rs1.getString("erx");
						if (erx == null)
							erx = "";
						String ery = rs1.getString("ery");
						if (ery == null)
							ery = "";
						String vvalue = rs1.getString("vvalue");
						if (vvalue == null)
							vvalue = "";
						ShapeItem tempitem = null;
						tempitem = new ShapeItem(visioChart);
						tempitem.setFillcolor(fillcolor);
						tempitem.setLinecolor(linecolor);
						tempitem.setLinewidth(linewidth);
						if (!itemid.isEmpty())
							tempitem.setItemid(itemid);
						tempitem.setSvgtype(svgtype);

						float movex = 480;
						float movey = 5;
						if (!transform.isEmpty()) {
							String tempx = transform.substring(
									transform.indexOf("(") + 1,
									transform.indexOf(","));
							float x = Float.parseFloat(tempx);
							String tempy = transform.substring(
									transform.indexOf(",") + 1,
									transform.indexOf(")"));
							float y = Float.parseFloat(tempy);
							movex = x + movex;
							movey = y + movey;
						}

						tempitem.setTransform("translate(" + movex + ","
								+ movey + ")");

						if (!fontname.isEmpty())
							tempitem.setFontname(fontname);
						if (!fontsize.isEmpty())
							tempitem.setFontsize(fontsize);
						if (!textanchor.isEmpty())
							tempitem.setTextanchor(textanchor);
						if (!cssclass.isEmpty())
							tempitem.setCssclass(cssclass);
						if (!ecx.isEmpty()) {
							float xteimp = Float.parseFloat(ecx) + movex;
							tempitem.setEcx(xteimp + "");
						}
						if (!ecy.isEmpty()) {
							float yteimp = Float.parseFloat(ecy) + movey;
							tempitem.setEcy(yteimp + "");
						}
						if (!erx.isEmpty())
							tempitem.setErx(erx);
						if (!ery.isEmpty())
							tempitem.setEry(ery);
						if (!vvalue.isEmpty())
							tempitem.setValue(vvalue);
					}
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception ex) {

				}
				ConfigDB.close(conn);
				visioChart.setLayoutData(new GridData((670 + 10), (1217 + 15)));
				width = 700;
				height = 100 + row1 * 35;

				parent.getDisplay().timerExec(1000, new Runnable() {
					public void run() {
						getstatus();
						if (statuss != null) {
							visioChart.setStatuss(statuss);
							visioChart.setTooltipdata(tooltips);
						}
						// visioChart.setTooltipdata(dd);
						parent.getDisplay().timerExec(10000, this);
					}
				});

			}

		}
		// height=vi.svgheight+30;
		// container.setSize(vi.svgwidth+30, vi.svgheight+30);
		return visioChart;
	}

	private void addOneShapeItem(Map<String,String> obj, VisioMap visioChart){
		ShapeItem tempitem = null;
		tempitem = new ShapeItem(visioChart);
		tempitem.setFillcolor(obj.get("fillcolor") );
		tempitem.setLinecolor(obj.get("linecolor"));
		tempitem.setLinewidth(Integer.parseInt(obj.get("linewidth")));
		if (!obj.get("itemid").isEmpty())
		tempitem.setItemid(obj.get("itemid"));
		tempitem.setSvgtype(Integer.parseInt(obj.get("svgtype")));
		if (!obj.get("transform").isEmpty())
		tempitem.setTransform(obj.get("transform"));
		if (!obj.get("svid").isEmpty())
		tempitem.setSvid(obj.get("svid"));
		if (!obj.get("fontname").isEmpty())
		tempitem.setFontname(obj.get("fontname"));
		if (!obj.get("fontsize").isEmpty())
		tempitem.setFontsize(obj.get("fontsize"));
		if (!obj.get("textanchor").isEmpty())
		tempitem.setTextanchor(obj.get("textanchor"));
		if (!obj.get("cssclass").isEmpty())
		tempitem.setCssclass(obj.get("cssclass"));
		if (!obj.get("ecx").isEmpty())
		tempitem.setEcx(obj.get("ecx"));
		if (!obj.get("ecy").isEmpty())
		tempitem.setEcy(obj.get("ecy"));
		if (!obj.get("erx").isEmpty())
		tempitem.setErx(obj.get("erx"));
		if (!obj.get("ery").isEmpty())
		tempitem.setEry(obj.get("ery"));
		if (!obj.get("vvalue").isEmpty())
		tempitem.setValue(obj.get("vvalue"));
	}

	private void getstatus() {
		statuss = null;
		tooltips = null;
		if (rowCount == 0)
		{
			return;
		}
		Connection conn = ConfigDB.getConn();
		statuss = new int[rowCount];
		tooltips = new String[rowCount];
		String sql = "select id,desc,pindex,portnum,porttype,mac,speed from ports where (porttype='6' or porttype='117')  and id='"
				+ this.entityobj.getSvid() + "'";
		ResultSet rs = ConfigDB.query(sql, conn);
		int i = 0;
		try {
			while (rs.next()) {
				String id = rs.getString("id");
				String desc = rs.getString("desc");
				String pindex = rs.getString("pindex");
				String portnum = rs.getString("portnum");
				String porttype = rs.getString("porttype");
				String mac = rs.getString("mac");
				String speed = rs.getString("speed");
				long lspeed = 0;
				try {
					lspeed = Long.parseLong(speed);
				} catch (Exception dx) {
				}

				FlowData fdata = null;
				Map<String, FlowData> ports = EquipmentQueuer
						.getEquipmentData(this.entityobj.getLocalip()
								+ this.entityobj.getProperys().get("Community"));
				if (ports != null)
					fdata = ports.get(pindex);
				if (fdata != null) {
					if (fdata.getPortWorkStuatus() == 1) {
						statuss[i] = 1;
					} else if (fdata.getPortWorkStuatus() == 2
							&& fdata.getPortAdminStatus() == 1) {
						statuss[i] = 0;
					} else if (fdata.getPortAdminStatus() == 2) {
						statuss[i] = 3;
					}
					float tempvv = fdata.getPortSpeed();
					if(tempvv>1000000000)
						tempvv=10000000000f;
					String speedd = DevicePanelUtils.getSpeed1(tempvv);
					String inflow ="0";
					String outflow = "0";
					if (fdata.getPortSpeed() >= 1000000000) {
						inflow = DevicePanelUtils.getSpeed(fdata.getHcInSpeed()*1000);
						outflow = DevicePanelUtils.getSpeed(fdata.getHcOutSpeed()*1000);

					} else {
						inflow = DevicePanelUtils.getSpeed(fdata.getInFlow()) ;
						outflow = DevicePanelUtils.getSpeed(fdata.getOutFlow());


					}
					tooltips[i] = PortManage.portTypenum2TypeDesc.get(porttype)
							+ ":"
							+ pindex
							+ ":"
							+ desc
							+ ":"
							+ pindex
							+ ":"
							+ PortManage.portWorkStatusnum2Desc.get(fdata
									.getPortWorkStuatus() + "")
							+ ":"
							+ PortManage.adminStatus.get(fdata
									.getPortAdminStatus() + "") + ":"
							+ inflow + ":"
							+ outflow + ":" + speedd;
				} else {
					String speedd = DevicePanelUtils.getSpeed(lspeed);
					statuss[i] = 0;
					tooltips[i] = PortManage.portTypenum2TypeDesc.get(porttype)
							+ ":" + pindex + ":" + desc + ":" + pindex + ":-"
							+ ":-" + ":-" + ":-" + ":" + speedd;
				}
				i++;

			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception ex) {
		}
		ConfigDB.close(conn);
	}
	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
	}
	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(width, height);
	}

}
