package com.siteview.NNM.uijobs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressIndicator;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.siteview.NNM.Activator;
import com.siteview.NNM.resource.ImageResource;

import siteview.windows.forms.ImageHelper;

public class ScanWaitdialog extends ProgressMonitorDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Button bnormal;
	public Button bcircal;
	public Button belse;
	public Button bnlook;
	public Label worklabel;
	public Label countlabel;
	public boolean islookup=false;
	
	public Label lbswrt;//三层
	public Label lbsw;//交换机
	public Label lbrt;//路由
	public Label lbsr;//服务器
	public Label lbfr;//防火墙
	public Label lbdb;//数据库
    public Label lbwn;//windows
    public Label lbux;//unix
    private Text text;
	private List mylist;
	// 用于判断用户是否点击了绘制拓扑图按钮
	public boolean drawTopo = true;
	private int sel = 1;
	
	public ScanWaitdialog(Shell parent) {
		super(parent);
	}

	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setText("扫描");
	}

	public void changeValue(final String value, final boolean append) {
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (append) {
					mylist.add("\r\n" + value);
					mylist.setSelection(mylist.getItemCount() - 1);
					mylist.showSelection();
					// text.insert("\r\n" + value);
				} else {
					// text.insert(value);
					mylist.add(value);
				}
			}
		});
		// 记录日志
		// LogUtil.logInfo(Activator._PLUGIN_ID, "\r\n"+value);
		// wp.work();
	}

	public void worksetvalue(final String value) {
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				worklabel.setText(value);
			}
		});
	}

	public void countvalue(final String value) {
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				countlabel.setText(value);
			}
		});
	}

	public void DeviceValues(final String swrt, final String sw, final String rt, final String sr, final String fr) {
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				lbswrt.setText(swrt);
				lbsw.setText(sw);
				lbrt.setText(rt);
				lbsr.setText(sr);
				lbfr.setText(fr);
			}
		});
	}

	public void NDeviceValues(final String db, final String wn, final String ux) {
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				lbdb.setText(db);
				lbwn.setText(wn);
				lbux.setText(ux);
			}
		});
	}

	/**
	 * Set the message in the message label.
	 * 
	 * @param messageString
	 *            The string for the new message.
	 * @param force
	 *            If force is true then always set the message text.
	 */
//	private void setMessage(String messageString, boolean force) {
//		// must not set null text in a label
//		message = messageString == null ? "" : messageString; //$NON-NLS-1$
//		if (messageLabel == null || messageLabel.isDisposed()) {
//			return;
//		}
//		if (force || messageLabel.isVisible()) {
//			messageLabel.setToolTipText(message);
//			messageLabel.setText(shortenText(message, messageLabel));
//		}
//	}

	@Override
	protected Control createDialogArea(Composite parent) {
//		GridLayout layout = (GridLayout) getLayout();
//		layout.verticalSpacing = 1;
//		String DEFAULT_TASKNAME = "扫描拓扑结构";
//		setMessage(DEFAULT_TASKNAME, false);
//		createMessageArea(parent);
//		taskLabel = messageLabel;
		Composite countComposite = new Composite(parent, SWT.NONE);
		countComposite.setLayout(new GridLayout(8, true));
		Label lb1=new Label(countComposite,SWT.None);
		lb1.setToolTipText("三层设备");
		lb1.setImage(ImageHelper.LoadImage(Activator.PLUGIN_ID, ImageResource.SwitchRoute));
		lbswrt=new Label(countComposite,SWT.None);
		Label lb2=new Label(countComposite,SWT.None);
		lb2.setToolTipText("交换机");
		lb2.setImage(ImageHelper.LoadImage(Activator.PLUGIN_ID, ImageResource.Switch));
		lbsw=new Label(countComposite,SWT.None);
		Label lb3=new Label(countComposite,SWT.None);
		lb3.setToolTipText("路由");
		lb3.setImage(ImageHelper.LoadImage(Activator.PLUGIN_ID, ImageResource.Route));
		lbrt=new Label(countComposite,SWT.None);
		Label lb4=new Label(countComposite,SWT.None);
		lb4.setToolTipText("防火墙");
		lb4.setImage(ImageHelper.LoadImage(Activator.PLUGIN_ID, ImageResource.Fire));
		lbfr=new Label(countComposite,SWT.None);
		Label lb5=new Label(countComposite,SWT.None);
		lb5.setToolTipText("服务器");
		lb5.setImage(ImageHelper.LoadImage(Activator.PLUGIN_ID, ImageResource.Server));
		lbsr=new Label(countComposite,SWT.None);
		Label lb6=new Label(countComposite,SWT.None);
		lb6.setToolTipText("数据库");
		lb6.setImage(ImageHelper.LoadImage(Activator.PLUGIN_ID, ImageResource.database));
		lbdb=new Label(countComposite,SWT.None);
		//lbdb.setText("0");
		Label lb7=new Label(countComposite,SWT.None);
		lb7.setToolTipText("Windows");
		lb7.setImage(ImageHelper.LoadImage(Activator.PLUGIN_ID, ImageResource.windows));
		lbwn=new Label(countComposite,SWT.None);
		//lbwn.setText("0");
		Label lb8=new Label(countComposite,SWT.None);
		lb8.setToolTipText("Unix");
		lb8.setImage(ImageHelper.LoadImage(Activator.PLUGIN_ID, ImageResource.unix));
		lbux=new Label(countComposite,SWT.None);
		//lbux.setText("0");
//		countlabel= new Label(parent, SWT.TOP);
//		GridData gt2 = new GridData();
//		gt2.horizontalAlignment = GridData.FILL;
//		gt2.widthHint = 360;
//		gt2.grabExcessHorizontalSpace = true;
//		gt2.horizontalSpan = 2;
//		countlabel.setLayoutData(gt2);
		mylist = new List(parent, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL
				| SWT.WRAP | SWT.BORDER | SWT.READ_ONLY);
		// text = new Text(parent, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL
		// | SWT.WRAP | SWT.BORDER | SWT.READ_ONLY);
		GridData gt = new GridData();
		gt.horizontalAlignment = GridData.FILL;
		gt.heightHint = 200;
		gt.widthHint = 360;
		gt.grabExcessHorizontalSpace = true;
		gt.horizontalSpan = 2;
		mylist.setLayoutData(gt);
		
		Composite workComposite = new Composite(parent, SWT.NONE);
		workComposite.setLayout(new GridLayout(3, false));
		worklabel = new Label(workComposite, SWT.TOP);
		GridData gt1 = new GridData(2);
		gt1.horizontalAlignment = GridData.FILL;
		gt1.widthHint = 230;
		gt1.grabExcessHorizontalSpace = true;
		gt1.horizontalSpan = 2;
		worklabel.setLayoutData(gt1);
		//bnlook=new Button(workComposite,SWT.NONE);
		//bnlook.setText("查看工作任务");
		// progress indicator
		progressIndicator = new ProgressIndicator(parent);
		GridData gd = new GridData();
		gd.heightHint = convertVerticalDLUsToPixels(9);
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalSpan = 2;
		progressIndicator.setLayoutData(gd);
		Composite startComposite = new Composite(parent, SWT.NONE);
		startComposite.setLayout(new GridLayout(3, false));
		bnormal = new Button(startComposite, SWT.RADIO);
		bnormal.setText("普通排版");
		bcircal = new Button(startComposite, SWT.RADIO);
		bcircal.setText("环形排版");
		bcircal.setSelection(true);
		belse = new Button(startComposite, SWT.RADIO);
		belse.setText("直角排版");
		// text showing current task
		return parent;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	protected void createCancelButton(Composite parent) {
		cancel = createButton(parent, IDialogConstants.CANCEL_ID, "取消", true);
		cancel.setCursor(arrowCursor);
		setOperationCancelButtonEnabled(true);
	}

	public void setMsg(String msgStr, boolean force) {
		// must not set null text in a label
		message = msgStr == null ? "" : msgStr; //$NON-NLS-1$
		if (messageLabel == null || messageLabel.isDisposed()) {
			return;
		}
		if (force || messageLabel.isVisible()) {
			messageLabel.setToolTipText(message);
			messageLabel.setText(shortenText(message, messageLabel));
		}
	}

	String tag = "0";

	public String gettag() {
		getShell().getDisplay().syncExec(new Runnable() {
			public void run() {
				if (bnormal.getSelection()) {
					tag = "1";
				} else if (belse.getSelection()) {
					tag = "2";
				}
			}
		});

		return tag;
	}

	/**
	 * Enables the cancel button (asynchronously).
	 * 
	 * @param b
	 *            The state to set the button to.
	 */
	public void setChacelEnabel(final boolean enabel) {
		if (getShell() != null) {
			getShell().getDisplay().asyncExec(new Runnable() {
				public void run() {
					setOperationCancelButtonEnabled(enabel);
				}
			});
		}
	}

	@Override
	protected Point getInitialSize() {
		return new Point(700, 600);
	}

	@Override
	protected void cancelPressed() {
		super.cancelPressed();
	}

}
