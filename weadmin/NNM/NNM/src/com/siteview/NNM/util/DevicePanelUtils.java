package com.siteview.NNM.util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siteview.nnm.data.ConfigDB;
import com.siteview.nnm.data.DBManage;
import com.siteview.nnm.data.EntityManager;
import com.siteview.nnm.data.PortManage;
import com.siteview.nnm.data.model.TopoChart;
import com.siteview.nnm.data.model.entity;
import com.siteview.nnm.data.model.svEdge;
import com.siteview.nnm.data.model.svNode;
import com.siteview.topology.model.SnmpPara;
import com.siteview.topology.snmp.snmpTools;
import com.siteview.utils.snmp.SnmpGet;
import com.siteview.utils.snmp.SnmpWalk;

import net.sf.json.JSONArray;

/**
 * 设备面板工具类
 * @author liuyaoao
 */
public class DevicePanelUtils {
	/**
	 * 根据svid获取总共有多少条记录
	 */
	public static int getRowCountBySvid(String svid){
		int rowCount = 0;
		Connection connn = ConfigDB.getConn();
		String sql3 = "select count(*) as rowCount from ports where (porttype='6' or porttype='117')  and  id='"
				+ svid + "'";
		ResultSet rs3 = ConfigDB.query(sql3, connn);
		try {
			if (rs3 != null) {
				rs3.next();
				rowCount = rs3.getInt("rowCount");
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		ConfigDB.close(connn);
		return rowCount;
	}

	 public static String getPindexFromPorts(String nodeid,String portindex){
		 String pindex = "";
		 Connection conn = ConfigDB.getConn();
		 String sql = "select pindex from ports where (porttype='6' or porttype='117') and id='"
				 + nodeid + "'";
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
		 return pindex;
	 }
	/**
	 * 根据xh获取svid in vData.db数据库
	 */
	public static String getSvidByXh(int xh){
		String svid = "";
		Connection conn = ConfigDB.getvConn();
		String sql3 = "select svid from  visiopanel"+xh+" where svid like 'models%' LIMIT 1";
		ResultSet rs3 = ConfigDB.query(sql3, conn);
		try {
			if (rs3 != null) {
				rs3.next();
				svid = rs3.getString("svid");
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		ConfigDB.close(conn);
		return svid;
	}
	/*
	 * 获取svg图形的数据(每个小图形的位置和颜色等)。在vData.db对应的visiopanel+xh的表里。
	*/
	public static List<Map<String,String>> getVisiopanelDataByXh(int xh){
		List<Map<String,String>> ltips=new ArrayList<Map<String,String>>();
		Map<String, String> tipss =null;
		Connection conn = ConfigDB.getvConn();
		String sql = "select svgtype,transform,itemid,svid,linewidth,fillcolor,linecolor,fontname,fontsize,textanchor,cssclass,ecx,ecy,erx,ery,vvalue from visiopanel"
				+ xh;
		ResultSet rs = ConfigDB.query(sql, conn);
		try{
			while (rs.next()) {
				int svgtype = rs.getInt("svgtype");
				String transform = rs.getString("transform");
				if (transform == null)
					transform = "";
				String itemid = rs.getString("itemid");
				if (itemid == null)
					itemid = "";
				String svid = rs.getString("svid");
				if (svid == null)
					svid = "";
				int linewidth = rs.getInt("linewidth");
				String fillcolor = rs.getString("fillcolor");
				String linecolor = rs.getString("linecolor");
				String fontname = rs.getString("fontname");
				if (fontname == null)
					fontname = "";
				String fontsize = rs.getString("fontsize");
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
				String textanchor = rs.getString("textanchor");
				if (textanchor == null)
					textanchor = "";
				String ecx = rs.getString("ecx");
				if (ecx == null)
					ecx = "";
				String ecy = rs.getString("ecy");
				if (ecy == null)
					ecy = "";
				String erx = rs.getString("erx");
				if (erx == null)
					erx = "";
				String ery = rs.getString("ery");
				if (ery == null)
					ery = "";
				String vvalue = rs.getString("vvalue");
				if (vvalue == null)
					vvalue = "";
				String cssclass = rs.getString("cssclass");
				if (cssclass == null) cssclass = "";

				tipss= new HashMap<String,String>();
				tipss.put("fillcolor", fillcolor);
				tipss.put("linecolor", linecolor);
				tipss.put("linewidth", linewidth+"");
				tipss.put("itemid", itemid);
				tipss.put("svgtype", svgtype+"");
				tipss.put("transform", transform);
				tipss.put("svid", svid);
				tipss.put("fontname", fontname);
				tipss.put("fontsize", fontsize);
				tipss.put("textanchor", textanchor);
				tipss.put("cssclass", cssclass);
				tipss.put("ecx", ecx);
				tipss.put("ecy", ecy);
				tipss.put("erx", erx);
				tipss.put("ery", ery);
				tipss.put("vvalue", vvalue);
				ltips.add(tipss);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception ex) {
		}
		ConfigDB.close(conn);
		return ltips;
	}
	/**
	 * 直接从vData.db数据库的visiopanelauto表里读数据。
	 * @return 两层深度的一个json结构。
	 */
	public static Map<String, Map<String, String>> getBaseShapesWithoutSysoidAndVdx(){
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
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception ex) {
		}
		ConfigDB.close(conn);
		return baseshapes;
	}

	public static List<Map<String,String>> getShapeItemDataList(String fact){
		List<Map<String,String>> dataList=new ArrayList<Map<String,String>>();
		Connection conn = ConfigDB.getConn();
		String sql = "select svgtype,transform,itemid,svid,linewidth,fillcolor,linecolor,fontname,fontsize,textanchor,cssclass,ecx,ecy,erx,ery,vvalue from visiopanelauto where factory ='"
				+ fact + "' ";
		Map<String, String> map = null;
		try {
			ResultSet rs1 = ConfigDB.query(sql, conn);
			while (rs1.next()) {
				map = new HashMap<String, String>();
				int svgtype = rs1.getInt("svgtype");
				map.put("svgtype", svgtype+"");
				int linewidth = rs1.getInt("linewidth");
				map.put("linewidth", linewidth+"");

				String transform = rs1.getString("transform");
				map.put("transform", (transform == null) ? "" : transform);

				String itemid = rs1.getString("itemid");
				map.put("itemid", (itemid == null) ? "" : itemid);

				String svid = rs1.getString("svid");
				map.put("svid", (svid == null) ? "" : svid);

				String fillcolor = rs1.getString("fillcolor");
				map.put("fillcolor", (fillcolor == null) ? "" : fillcolor);

				String linecolor = rs1.getString("linecolor");
				map.put("linecolor", (linecolor == null) ? "" : linecolor);

				String fontname = rs1.getString("fontname");
				map.put("fontname", (fontname == null) ? "" : fontname);

				String fontsize = rs1.getString("fontsize");
				map.put("fontsize", (fontsize == null) ? "" : fontsize);

				String textanchor = rs1.getString("textanchor");
				map.put("textanchor", (textanchor == null) ? "" : textanchor);

				String cssclass = rs1.getString("cssclass");
				map.put("cssclass", (cssclass == null) ? "" : cssclass);

				String ecx = rs1.getString("ecx");
				map.put("ecx", (ecx == null) ? "" : ecx);

				String ecy = rs1.getString("ecy");
				map.put("ecy", (ecy == null) ? "" : ecy);

				String erx = rs1.getString("erx");
				map.put("erx", (erx == null) ? "" : erx);

				String ery = rs1.getString("ery");
				map.put("ery", (ery == null) ? "" : ery);

				String vvalue = rs1.getString("vvalue");
				map.put("vvalue", (vvalue == null) ? "" : vvalue);
				dataList.add(map);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception ex) {
		}
		ConfigDB.close(conn);
		return dataList;
	}

	public static String getFactoryNameBySysoid(String sysoid){
		String fact = "huawei1";
		if (sysoid.startsWith("1.3.6.1.4.1.9.")) {
			fact = "cisco";
		} else if (sysoid.startsWith("1.3.6.1.4.1.2011.")) {
			fact = "huawei";
		} else if (sysoid.startsWith("1.3.6.1.4.1.25506.")) {
			fact = "h3c";
		} else if (sysoid.startsWith("1.3.6.1.4.1.3902.")) {
			fact = "zte";
		} else if (sysoid.startsWith("1.3.6.1.4.1.4881.")) {
			fact = "ruijie";
		}
		return fact;
	}

	public static int getGroupSizeByNodeId(String nodeid){
		Connection conn = ConfigDB.getConn();
		String sql = "select count(*) as coun from ports where (porttype='6' or porttype='117')  and id='"
				+ nodeid + "'";
		ResultSet rs = ConfigDB.query(sql, conn);
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
		ConfigDB.close(conn);
		return groupsize;
	}

	public static String getSpeed(double v1) {
		double vv = 0;
		String dww = " Kbps";
		vv = v1;
		if (vv >= 1024) {
			vv = (vv / 1024);
			if (vv >= 1024) {
				vv = (vv / 1024);
				dww = " Gbps";
				if (vv >= 1024) {
					vv = (int) (vv / 1024);
					dww = " Tbps";
				}
			} else {
				dww = " Mbps";

			}
		}
		BigDecimal bg = new BigDecimal(vv);
		return bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + dww;
	}
	public static String getSpeed(float v1) {
		float vv = 0;
		String dww = " Kbps";
		vv = v1;
		if (vv >= 1024) {
			vv = (vv / 1024);
			if (vv >= 1024) {
				vv = (vv / 1024);
				dww = " Gbps";
				if (vv >= 1024) {
					vv = (int) (vv / 1024);
					dww = " Tbps";
				}
			} else {
				dww = " Mbps";

			}
		}
		BigDecimal bg = new BigDecimal(vv);
		return bg.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue() + dww;
	}
	public static String getSpeed1(float v1) {
		long vv = 0;
		String dww = " Kbps";
		vv = (int) (v1 / 1000);
		if (vv >= 1000) {
			vv = (int) (vv / 1000);
			if (vv >= 1000) {
				vv = (int) (vv / 1000);
				dww = " Gbps";
			} else {
				dww = " Mbps";

			}
		}
		return vv + dww;
	}
	/**
	 * @param v1
	 * @return
	 */
	public static String getSpeed(long v1) {
		long vv = 0;
		String dww = " Kbps";
		vv = (int) (v1 / 1000);
		if (vv >= 1000) {
			vv = (int) (vv / 1000);
			if (vv >= 1000) {
				vv = (int) (vv / 1000);
				dww = " Gbps";
			} else {
				dww = " Mbps";

			}
		}
		return vv + dww;
	}

}
