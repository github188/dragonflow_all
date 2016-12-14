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
 *
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
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ConfigDB.close(connn);
		return rowCount;
	}
	/**
	 * 根据xh获取svid
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
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return svid;
	}

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
