package com.siteview.svgmap;

import java.util.Arrays;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.swt.widgets.Composite;

public class VisioMap extends SvgMap {

	private static final String REMOTE_TYPE = "d3svgmap.VisioMap";
	 private int barWidth = 25;
	 private int spacing = 2;
	 private int[] statuss;
	 private String tooltipdesc;
	 private String menudesc;
	 private String[] tooltipdata;
	


	public VisioMap(Composite parent, int style) {
		super(parent,style, REMOTE_TYPE);
		// TODO Auto-generated constructor stub
		this.setTooltipdesc("端口信息\n端口类型：p1\n端口索引：p2\n端口描述：p3\n接口索引：p4\n端口状态：p5\n管理状态：p6\n接收流量：p7\n发送流量：p8\n速率   ：p9");
		this.setMenudesc("打开端口:关闭端口:当前端口连接设备");
	}

	public String getMenudesc() {
		checkWidget();
		return menudesc;
	}

	public void setMenudesc(String menudesc) {
		checkWidget();
		if(this.menudesc != menudesc)
		{
		this.menudesc = menudesc;
		remoteObject.set( "menudesc", menudesc );
		}
	}
	public String[] getTooltipdata() {
		checkWidget();
		return tooltipdata == null ? null : tooltipdata.clone();
	}

	public void setTooltipdata(String... tooltipdata) {
		//checkWidget();
		if (!Arrays.equals(this.tooltipdata, tooltipdata)) {
			this.tooltipdata = tooltipdata.clone();
			remoteObject.set("tooltipdata", jsonArray(tooltipdata));
		}
	}
	public int[] getStatuss() {
		checkWidget();
		return statuss == null ? null : statuss.clone();
	}

	public void setStatuss(int... statuss) {
		//checkWidget();
		if (!Arrays.equals(this.statuss, statuss)) {
			this.statuss = statuss.clone();
			remoteObject.set("statuss", jsonArray(statuss));
		}
	}
	public String getTooltipdesc() {
		checkWidget();
		return tooltipdesc;
	}

	public void setTooltipdesc(String tooltipdesc) {
		checkWidget();
		if(this.tooltipdesc!=tooltipdesc)
		{
		this.tooltipdesc = tooltipdesc;
		remoteObject.set( "tooltipdesc", tooltipdesc );
		}
	}
	public int getBarWidth() {
	    checkWidget();
	    return barWidth;
	  }

	  public void setBarWidth( int width ) {
	    checkWidget();
	    if( width != barWidth ) {
	      barWidth = width;
	      remoteObject.set( "barWidth", width );
	    }
	  }

	  public int getSpacing() {
	    checkWidget();
	    return spacing;
	  }

	  public void setSpacing( int width ) {
	    checkWidget();
	    if( width != spacing ) {
	      spacing = width;
	      remoteObject.set( "spacing", width );
	    }
	  }
		private static JsonArray jsonArray(int[] values) {
		// TODO use array.addAll in future versions
		JsonArray array = new JsonArray();
		for (int i = 0; i < values.length; i++) {
			array.add(values[i]);
		}
		return array;
	}
		private static JsonArray jsonArray(String[] values) {
			// TODO use array.addAll in future versions
			JsonArray array = new JsonArray();
			for (int i = 0; i < values.length; i++) {
				array.add(values[i]);
			}
			return array;
		}

}
