package com.siteview.leveltop.draw2d.pojo;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class NodeStyle {
	
	//shape=mxgraph.cisco.hubs_and_gateways.small_hub;html=1;dashed=0;fillColor=#036897;strokeColor=#ffffff;strokeWidth=2;verticalLabelPosition=bottom;verticalAlign=top
	
	private String shape;
	
	private String html;
	
	private String dashed;
	
	private String fillColor;
	
	private String strokeColor;
	
	private String strokeWidth;
	
	private String verticalLabelPosition;
	
	private String verticalAlign;
	
	public NodeStyle(){
		
	}
	
	public NodeStyle(String shape,String html,String dashed,String fillColor,String strokeColor,String strokeWidth,String verticalLabelPosition,String verticalAlign){
		
	}

	public String getShape() {
		return shape;
	}

	public void setShape(String shape) {
		this.shape = shape;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public String getDashed() {
		return dashed;
	}

	public void setDashed(String dashed) {
		this.dashed = dashed;
	}

	public String getFillColor() {
		return fillColor;
	}

	public void setFillColor(String fillColor) {
		this.fillColor = fillColor;
	}

	public String getStrokeColor() {
		return strokeColor;
	}

	public void setStrokeColor(String strokeColor) {
		this.strokeColor = strokeColor;
	}

	public String getStrokeWidth() {
		return strokeWidth;
	}

	public void setStrokeWidth(String strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

	public String getVerticalLabelPosition() {
		return verticalLabelPosition;
	}

	public void setVerticalLabelPosition(String verticalLabelPosition) {
		this.verticalLabelPosition = verticalLabelPosition;
	}

	public String getVerticalAlign() {
		return verticalAlign;
	}

	public void setVerticalAlign(String verticalAlign) {
		this.verticalAlign = verticalAlign;
	}
	
	public void fromStyleString(String style){
		try{
			if(style!=null&&style.trim().length()>0){
				String[] arrays = style.split(";");
				Map<String,String> map = new HashMap<String,String>();
				for(String array:arrays){
					String[] keyValues = array.split("=");
					map.put(keyValues[0],keyValues[1]!=null?keyValues[1]:"");
				}
				Field[] fields = this.getClass().getDeclaredFields();
				for(Field field:fields){
					field.setAccessible(true);
					field.set(this,map.get(field.getName()));
				}
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public String toStyleString(){
		StringBuffer sb = new StringBuffer();
		try{
			Field[] fields = this.getClass().getDeclaredFields();
			for(Field field:fields){
				if(sb.length()>0){
					sb.append(";");
				}
				Object value = field.get(this);
				sb.append(field.getName()).append("=").append(value!=null?value:"");
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		return sb.toString();
	}
}
