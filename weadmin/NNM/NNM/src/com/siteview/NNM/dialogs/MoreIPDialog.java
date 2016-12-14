package com.siteview.NNM.dialogs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.siteview.NNM.dialogs.table.ImageKeys;
import com.siteview.nnm.data.model.entity;
import com.siteview.nnm.data.model.svNode;

public class MoreIPDialog extends Dialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<String, entity> allEntiry = null;
	public String serverAddress;
	private Tree tree;

	public MoreIPDialog(Shell parentShell, Map<String, entity> allEntiry) {
		super(parentShell);
		// TopoChart tochart = com.siteview.nnm.data.DBManage.Topos.get(tag);
		// svNode entityobj = (svNode) tochart.getNodes().get(nodeid);
		// ip = entityobj.getLocalip();
		this.allEntiry = allEntiry;
	}

	protected void configureShell(Shell newShell) {
		newShell.setText("选择设备");
		super.configureShell(newShell);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		// ImageKeys.loadimage();
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout());

		Group group = new Group(container, SWT.NONE);
		GridData gd_group = new GridData(GridData.FILL_BOTH);
		group.setLayoutData(gd_group);

		GridLayout gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = 10;
		group.setLayout(gridLayout);

		tree = new Tree(group, SWT.NONE);
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		tree.setLayoutData(new GridData(GridData.FILL_BOTH));
		TreeItem switches3item = new TreeItem(tree, SWT.NONE);
		switches3item.setText("三层交换机");
		switches3item.setImage(ImageKeys.images.get(ImageKeys.SWITCHROUTER_BLUE));

		TreeItem switches2item = new TreeItem(tree, SWT.NONE);
		switches2item.setText("二层交换机");
		switches2item.setImage(ImageKeys.images.get(ImageKeys.SWITCH_BLUE));

		TreeItem routeritem = new TreeItem(tree, SWT.NONE);
		routeritem.setText("路由器");
		routeritem.setImage(ImageKeys.images.get(ImageKeys.ROUTER_BLUE));

		TreeItem firewallitem = new TreeItem(tree, SWT.NONE);
		firewallitem.setText("防火墙");
		firewallitem.setImage(ImageKeys.images.get(ImageKeys.FIREWALL_BLUE));

		TreeItem serveritem = new TreeItem(tree, SWT.NONE);
		serveritem.setText("服务器");
		serveritem.setImage(ImageKeys.images.get(ImageKeys.SERVER_BLUE));

		TreeItem pcitem = new TreeItem(tree, SWT.NONE);
		pcitem.setText("PC终端");
		pcitem.setImage(ImageKeys.images.get(ImageKeys.PC_BLUE));

		TreeItem otheritem = new TreeItem(tree, SWT.NONE);
		otheritem.setText("其他");
		otheritem.setImage(ImageKeys.images.get(ImageKeys.OTHER_BLUE));
		List<String> list=new ArrayList<String>();
		Iterator<String> it = allEntiry.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			entity entiry = allEntiry.get(key);
			svNode node = (svNode) entiry;
			// String ip = node.getProperys().get("ipList");
			// node.getProperys().get("localip");
			String ip = node.getLocalip();
			if(list.contains(ip))
				continue;
			list.add(ip);
			// String[] ips = null;
			// if (ip.contains(",")) {
			// ips = ip.split(",");
			// } else if (ips == null) {
			// ips = new String[1];
			// ips[0] = ip;
			// }
			// for (int i = 0; i < ips.length; i++) {
			if (entiry.getSvgtype() == 0) {
				TreeItem switches3item_1 = new TreeItem(switches3item, SWT.NONE);
				switches3item_1.setText(ip);
			} else if (entiry.getSvgtype() == 1) {
				TreeItem switches2item_1 = new TreeItem(switches2item, SWT.NONE);
				switches2item_1.setText(ip);
			} else if (entiry.getSvgtype() == 2) {
				TreeItem routeritem_1 = new TreeItem(routeritem, SWT.NONE);
				routeritem_1.setText(ip);
			} else if (entiry.getSvgtype() == 3) {
				TreeItem firewallitem_1 = new TreeItem(firewallitem, SWT.NONE);
				firewallitem_1.setText(ip);
			} else if (entiry.getSvgtype() == 4) {
				TreeItem serveritem_1 = new TreeItem(serveritem, SWT.NONE);
				serveritem_1.setText(ip);
			} else if (entiry.getSvgtype() == 5) {
				TreeItem pcitem_1 = new TreeItem(pcitem, SWT.NONE);
				pcitem_1.setText(ip);
			} else if (entiry.getSvgtype() == 6) {
				TreeItem otheritem_1 = new TreeItem(otheritem, SWT.NONE);
				otheritem_1.setText(ip);
			}
			// }

		}
		return container;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "确定", true);
		createButton(parent, IDialogConstants.CANCEL_ID, "取消", true);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(300, 400);
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			TreeItem[] items = tree.getSelection();
			if(items.length == 0) {
				MessageDialog.openWarning(getShell(), "提示", "请选择一个设备!");
				return;
			}
			setServerAddress(items[0].getText());
		}
		super.buttonPressed(buttonId);
	}
}
