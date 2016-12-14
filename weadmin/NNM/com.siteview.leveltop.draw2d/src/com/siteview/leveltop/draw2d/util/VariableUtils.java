package com.siteview.leveltop.draw2d.util;

import com.mxgraph.model.mxCell;
import com.siteview.leveltop.draw2d.nls.Message;
import com.siteview.leveltop.draw2d.pojo.NodeStyle;

public class VariableUtils {
	
	private static VariableUtils instance = new VariableUtils();
	
	private VariableUtils(){ 
		
	}
	
	public static VariableUtils getInstance(){
		return instance;
	}
	
	public String getLevelTypeStyle(int level){
		switch(level){
			case 1:return "shape=mxgraph.cisco.hubs_and_gateways.small_hub;html=1;dashed=0;fillColor=#036897;strokeColor=#ffffff;strokeWidth=2;verticalLabelPosition=bottom;verticalAlign=top";
			case 2:return "shape=mxgraph.cisco.servers.fileserver;html=1;dashed=0;fillColor=#036897;strokeColor=#ffffff;strokeWidth=2;verticalLabelPosition=bottom;verticalAlign=top";
			case 3:return "shape=mxgraph.veeam.2d.monitoring_console;html=1;dashed=0;fillColor=#33001A;strokeColor=#82b366;strokeWidth=2;verticalLabelPosition=bottom;verticalAlign=top;";
		}
		return null;
	}
	
	public int[] getLevelTypeSize(int level){
		switch(level){
			case 1:return new int[]{60,30};
			case 2:return new int[]{32,40};
			case 3:return new int[]{36,36};
		}
		return null;
	}
	
	public String getLevelTypeName(int level){
		switch(level){
			case 1:return Message.get().RESOURCE_NETWORK_LEVEL;
			case 2:return Message.get().RESOURCE_SERVER_LEVEL;
			case 3:return Message.get().RESOURCE_APPLICATION_LEVEL;
		}
		return "unkown";
	}
	
	public String getNodeStatusName(String status){
		switch(status){
			case "good":return Message.get().RESOURCE_NODE_GOOD;
			case "warning":return Message.get().RESOURCE_NODE_WARNING;
			case "error":return Message.get().RESOURCE_NODE_ERROR;
			case "disabled":return Message.get().RESOURCE_NODE_DISABLED;
			case "disapear":return Message.get().RESOURCE_NODE_DISAPEAR;
		}
		return "unkown";
	}
	
	public String mkToolTipHtml(String nodeId,int level,String status){
		StringBuffer sb = new StringBuffer();
		sb.append("<b>").append(Message.get().RESOURCE_TOP_NODE_IP).append(":").append("</b>").append(" ").append(nodeId).append("<br>");
		sb.append("<b>").append(Message.get().RESOURCE_TOP_NODE_LEVEL).append(":").append("</b>").append(" ").append(getLevelTypeName(level)).append("<br>");
		sb.append("<b>").append(Message.get().RESOURCE_TOP_NODE_STATUS).append(":").append("</b>").append(" ").append(getNodeStatusName(status));
		return sb.toString();
	}
	
	/*
	 * 获取角度
	 */
	public int getAngle(int source_x,int source_y,int target_x,int target_y){
        int x=Math.abs(source_x-target_x);
        int y=Math.abs(source_y-target_y);
        double z=Math.sqrt(x*x+y*y);
        int angle=Math.round((float)(Math.asin(y/z)/Math.PI*180));//最终角度
        return angle;
	}
	
	public NodeStyle getStyle(mxCell mxCell,String status){
		NodeStyle nodeStyle = new NodeStyle();
		nodeStyle.fromStyleString(mxCell.getStyle());
		String strokeColor = "#ffffff";
		String fillColor = "#036897";
		switch(status){
			case "good":strokeColor="white";fillColor="green";
					    break;
			case "warning":strokeColor="white";fillColor="#FFCC00";
						   break;
			case "error":strokeColor="white";fillColor="#CC0033";
				         break;
			case "disabled":strokeColor="white";fillColor="gray";break;
			case "disappear":break;
		}
		nodeStyle.setStrokeColor(strokeColor);
		nodeStyle.setFillColor(fillColor);
		return nodeStyle;
	}
	
}
