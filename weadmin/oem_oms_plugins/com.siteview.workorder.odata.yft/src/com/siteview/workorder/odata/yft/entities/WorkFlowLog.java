package com.siteview.workorder.odata.yft.entities;

/**
 * 流程日志
 * 
 * @author Administrator
 *
 */
public class WorkFlowLog {
	private String id;
	private String handler; // 处理人
	private String handleTime; // 处理时间
	private String flowAction; // 流程操作
	private String content; // 环节描述

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getHandler() {
		return handler;
	}

	public void setHandler(String handler) {
		this.handler = handler;
	}

	public String getHandleTime() {
		return handleTime;
	}

	public void setHandleTime(String handleTime) {
		this.handleTime = handleTime;
	}

	public String getFlowAction() {
		return flowAction;
	}

	public void setFlowAction(String flowAction) {
		this.flowAction = flowAction;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
