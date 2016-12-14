package com.siteview.NNM.uijobs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

public class refreshDevice extends ProgressMonitorDialog {

	

	public refreshDevice(Shell parent) {
		super(parent);
	}

	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setText("刷新设备...");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List mylist;
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
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite countComposite = new Composite(parent, SWT.NONE);
		countComposite.setLayout(new GridLayout(8, true));

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
		

		// text showing current task
		return parent;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	protected void createCancelButton(Composite parent) {
		cancel = createButton(parent, IDialogConstants.CANCEL_ID,
				"取消", true);
		cancel.setCursor(arrowCursor);
		setOperationCancelButtonEnabled(true);
	}
	
	protected void buttonPressed(int buttonId) {
		try {
			
                this.close();
				cancelPressed();
		} catch (Exception e) {
		}
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
		return new Point(420, 300);
	}

	@Override
	protected void cancelPressed() {
		super.cancelPressed();

	}
	
}
