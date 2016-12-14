package com.siteview.leveltop.draw2d.nls;

import org.eclipse.rap.rwt.RWT;

public class Message {
	
	private static final String MESSAGE_BUNDLE = "OSGI-INF/I18N/plugin";

	private Message() {

	}

	public static Message get() {
		return (Message) RWT.NLS.getISO8859_1Encoded(MESSAGE_BUNDLE, Message.class);
	}
	
	public String RESOURCE_TOPO;
	public String SAVE_TOPO;
	public String DELETE_SEL_NODE;
	public String LOAD_RESOURCE_TOPO_SELECT_NODE;
	public String LOAD_RESOURCE_TOPO_STATUS;
	public String DELETE_NODE;
	public String DELETE_EDGE;
	public String OPEN_SOURCE;
	public String RESOURCE_TOPO_NOTE;
	public String RESOURCE_TOPO_DIRY;
	public String RESOURCE_TOPO_OUT;
	public String RESOURCE_TOPO_IN;
	public String RESOURCE_TOPO_SYNCH;
	
	public String RESOURCE_NODE_GOOD;
	public String RESOURCE_NODE_GOOD_TIP;
	public String RESOURCE_NODE_WARNING;
	public String RESOURCE_NODE_WARNING_TIP;
	public String RESOURCE_NODE_ERROR;
	public String RESOURCE_NODE_ERROR_TIP;
	public String RESOURCE_NODE_DISABLED;
	public String RESOURCE_NODE_DISABLED_TIP;
	public String RESOURCE_NODE_DISAPEAR;
	public String RESOURCE_NODE_DISAPEAR_TIP;
	
	public String RESOURCE_TOPO_STATUS;
	public String RESOURCE_SERVER_LEVEL;
	public String RESOURCE_APPLICATION_LEVEL;
	public String RESOURCE_NETWORK_LEVEL;
	
	public String RESOURCE_TOP_NODE_IP;
	public String RESOURCE_TOP_NODE_LEVEL;
	public String RESOURCE_TOP_NODE_STATUS;
}
