package com.siteview.NNM.dialogs;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;

import java.math.BigDecimal;

import siteview.windows.forms.MsgBox;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.SiteviewException;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;
import Siteview.Database.IDbDataParameterCollection;
import Siteview.Database.SqlParameterCollection;
import Siteview.Windows.Forms.ConnectionBroker;

import com.siteview.NNM.util.snmpget;
import com.siteview.nnm.data.model.svNode;
import com.siteview.svggauge.svgGauge;
import com.siteview.topology.model.SnmpPara;
import com.siteview.topology.snmp.snmpTools;
import com.siteview.utils.db.DBQueryUtils;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Text;

public class cpuMemdialog extends Dialog {
	private Text text;

	ISiteviewApi svapi;
	svNode entitynode;
	String cpuoid = "";
	String MemFreeOID = "";
	String MemUseOID = "";
	String MemTotalOID = "";
	boolean getbo = true;
	Label lblall;
	Label lbluse;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public cpuMemdialog(Shell parentShell, svNode entitynode) {
		super(parentShell);
		getbo = true;
		this.entitynode = entitynode;

	}

	protected void configureShell(Shell newShell) {

		newShell.setText("CPU&内存实时分析");
		super.configureShell(newShell);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(final Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(2, true));
		container.setLayoutData(new GridData(300, 200));

		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setText("设备地址:");
		svapi = ConnectionBroker.get_SiteviewApi();

		text = new Text(container, SWT.BORDER);
		text.setEditable(false);
		container.addListener(SWT.KeyDown, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (event.keyCode == 48) {
					String cupoid = "cpu:" + cpuoid + " mf:" + MemFreeOID
							+ " mu:" + MemUseOID + " mall:" + MemTotalOID;
					MsgBox.ShowMessage(cupoid);
				}
			}
		});
		text.setText(this.entitynode.getLocalip());
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		Button btn = new Button(container, SWT.BORDER);
		btn.setText("oid 测试");
		btn.addListener(SWT.MouseUp, new Listener() {
			@Override
			public void handleEvent(Event event) {
				CpuMemtest test = new CpuMemtest(null, entitynode.getLocalip(),
						entitynode.getProperys().get("Community"));
				test.open();
			}

		});

		Label lbl2 = new Label(container, SWT.NONE);
		lblall = new Label(container, SWT.NONE);
		lbluse = new Label(container, SWT.NONE);
		final svgGauge Gauge = new svgGauge(container, SWT.NONE);

		Gauge.setLayoutData(new GridData(GridData.FILL_BOTH));
		Gauge.setLabel("CPU");
		if (cpuoid.isEmpty()) {
			Job jobcpu = new Job("cpujob") {
				protected IStatus run(IProgressMonitor monitor) {
					cpuoid = SelectExecute(svapi);

					return Status.OK_STATUS;
				}
			};
			jobcpu.setPriority(Job.SHORT);
			jobcpu.schedule();
		}
		parent.getDisplay().timerExec(3000, new Runnable() {
			public void run() {
				if (!getbo) {
					MsgBox.ShowError("提示", "请在设备信息维护中配置相关设备的CPU和内存 OID!");
					return;
				}
				int cp = getcpu();
				Gauge.setVv(cp);
				parent.getDisplay().timerExec(10000, this);
			}
		});

		final svgGauge GaugeMem = new svgGauge(container, SWT.NONE);
		GaugeMem.setLayoutData(new GridData(GridData.FILL_BOTH));
		GaugeMem.setLabel("内存");
		parent.getDisplay().timerExec(3000, new Runnable() {
			public void run() {
				if (!getbo) {
					return;
				}
				int j = getMeMused();
				GaugeMem.setVv(j);
				parent.getDisplay().timerExec(10000, this);
			}
		});

		return container;
	}

	public String SelectExecute(ISiteviewApi api) {
		String cpuid = "";
		BusinessObject bo = null;
		long starti = System.currentTimeMillis();
		String sysOid = entitynode.getProperys().get("sysObjectID");
		try {
			bo = DBQueryUtils.queryOnlyBo("SysOID", sysOid, "Mib", api);
			if (bo != null) {
				try {
					cpuid = bo.GetField("CpuOID").get_Value().toString();
				} catch (Exception ex) {

				}
				System.err.println(System.currentTimeMillis() - starti);
				if (cpuid == null)
					cpuid = "";
				try {
					MemFreeOID = bo.GetField("MemFreeOID").get_Value()
							.toString();
				} catch (Exception ex) {

				}
				if (MemFreeOID == null)
					MemFreeOID = "";
				try {
					MemUseOID = bo.GetField("MemUseOID").get_Value().toString();
				} catch (Exception ex) {

				}
				if (MemUseOID == null) {
					MemUseOID = "";
				}
				try {
					MemTotalOID = bo.GetField("MemTotalOID").get_Value()
							.toString();
				} catch (Exception ex) {

				}
				if (MemTotalOID == null) {
					MemTotalOID = "";
				}
			} else {
				String tempoid = "";
				if (sysOid.startsWith("1.3.6.1.4.1.9.")) {
					cpuid = "1.3.6.1.4.1.9.9.109.1.1.1.1.4";
					MemFreeOID = "1.3.6.1.4.1.9.9.48.1.1.1.6";
					MemUseOID = "1.3.6.1.4.1.9.9.48.1.1.1.5";
					MemTotalOID = "";
				} else if (sysOid.startsWith("1.3.6.1.4.1.2011.")) {
					cpuid = "1.3.6.1.4.1.2011.6.1.1.1.4";
					MemFreeOID = "1.3.6.1.4.1.2011.6.1.2.1.1.2";
					MemUseOID = "";
					MemTotalOID = "1.3.6.1.4.1.2011.6.1.2.1.1.2";
				}
				// else if (sysOid.startsWith("1.3.6.1.4.1.25506.")) {
				// cpuid = "1.3.6.1.4.1.2011.10.2.6.1.1.1.1.6";
				// MemFreeOID = "";
				// MemUseOID = "1.3.6.1.4.1.25506.2.6.1.1.1.1.8";
				// MemTotalOID = "1.3.6.1.4.1.25506.2.6.1.1.1.1.10";
				// }
				else if (sysOid.startsWith("1.3.6.1.4.1.3902.")) {
					cpuid = "1.3.6.1.4.1.3902.101.4.1.3";
					MemFreeOID = "1.3.6.1.4.1.3902.101.4.1.4";
					MemUseOID = "";
					MemTotalOID = "";
				} else if (sysOid.startsWith("1.3.6.1.4.1.4881.")) {
					cpuid = "1.3.6.1.4.1.4881.1.1.10.2.36.1.1.2";
					MemFreeOID = "";
					MemUseOID = "";
					MemTotalOID = "";
				} else if (sysOid.equals("1.3.6.1.4.1.2011.2.239.5")) {
					cpuid = "1.3.6.1.4.1.2011.5.25.31.1.1.1.1.5.16842753";
					MemFreeOID = "";
					MemUseOID = "1.3.6.1.4.1.2011.5.25.31.1.1.1.1.7.16842753";
					MemTotalOID = "";
				} else if (sysOid.equals("1.3.6.1.4.1.25506.1.604")) {
					cpuid = "1.3.6.1.4.1.25506.2.6.1.1.1.1.6";
					MemFreeOID = "";
					MemUseOID = "1.3.6.1.4.1.25506.2.6.1.1.1.1.8";
					MemTotalOID = "";
				} else {

					getbo = false;
				}

			}
		} catch (SiteviewException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cpuid;
	}

	// private int getMeMused() {
	// int memused = 0;
	// String mused = "";
	// String mfree = "";
	// String mall = "";
	// SnmpPara para = new SnmpPara(entitynode.getLocalip(), entitynode
	// .getProperys().get("Community"), 5000, 3, 2);
	// try {
	// if (MemFreeOID != null && !MemFreeOID.isEmpty()) {
	// if (MemFreeOID.endsWith(".0")) {
	// mfree = snmpTools.getValue(para, MemFreeOID);
	// } else {
	// mfree = snmpTools.getNextValues(para, MemFreeOID);
	// }
	// }
	// if (MemUseOID != null && !MemUseOID.isEmpty()) {
	// if (MemUseOID.endsWith(".0")) {
	// mused = snmpTools.getValue(para, MemUseOID);
	// } else {
	// mused = snmpTools.getNextValues(para, MemUseOID);
	// }
	// }
	// if (MemTotalOID != null && !MemTotalOID.isEmpty()) {
	// if (MemTotalOID.endsWith(".0")) {
	// mall = snmpTools.getValue(para, MemTotalOID);
	// } else {
	// mall = snmpTools.getNextValues(para, MemTotalOID);
	// }
	// }
	// } catch (Exception ex) {
	//
	// }
	// if (mused == null)
	// mused = "";
	// if (mfree == null)
	// mfree = "";
	// if (mall == null)
	// mall = "";
	// if (!mused.isEmpty() && !mfree.isEmpty()) {
	// int mu = Integer.parseInt(mused);
	// int mf = Integer.parseInt(mfree);
	// if (mu <= 100 && mf <= 100) {
	// memused = mu;
	// } else {
	// float v = ((float) mu / (mu + mf)) * 100;
	// BigDecimal dd = new BigDecimal(v);
	// dd.setScale(0, BigDecimal.ROUND_HALF_UP);
	// memused = dd.intValue();
	// }
	// }
	// if (!mused.isEmpty() && !mall.isEmpty()) {
	// int mu = Integer.parseInt(mused);
	// int ma = Integer.parseInt(mall);
	// if (mu > 100) {
	// float v = (float) mu / ma * 100;
	// BigDecimal dd = new BigDecimal(v);
	// dd.setScale(0, BigDecimal.ROUND_HALF_UP);
	// memused = dd.intValue();
	// } else {
	// memused = mu;
	// }
	// }
	// if (!mfree.isEmpty() && !mall.isEmpty()) {
	// int mf = Integer.parseInt(mfree);
	// int ma = Integer.parseInt(mall);
	// if (mf > 100) {
	// float v = (float) (ma - mf) / ma * 100;
	// BigDecimal dd = new BigDecimal(v);
	// dd.setScale(0, BigDecimal.ROUND_HALF_UP);
	// memused = dd.intValue();
	// } else {
	// memused = 100 - mf;
	// }
	// }
	// if (!mused.isEmpty() && mfree.isEmpty() && mall.isEmpty()) {
	// int mu = Integer.parseInt(mused);
	// if (mu <= 100){
	// memused = mu;
	// }else{
	// mused = snmpTools.getNextValue(para, MemUseOID);
	// }
	// }
	// if (mused.isEmpty() && !mfree.isEmpty() && mall.isEmpty()) {
	// int mf = Integer.parseInt(mfree);
	// if (mf <= 100){
	// memused = 100 - mf;
	// }else{
	//
	// }
	// }
	//
	// return memused;
	// }

	private int getMeMused() {
		int memused = 0;
		String mused = "";
		String mfree = "";
		String mall = "";
		SnmpPara para = new SnmpPara(entitynode.getLocalip(), entitynode
				.getProperys().get("Community"), 5000, 3, 2);
		String sysOid1 = entitynode.getProperys().get("sysObjectID");
		if (sysOid1.equals("1.3.6.1.4.1.2011.2.239.5")) {
			try {
				mused = snmpTools.getValue(para,
						"1.3.6.1.4.1.2011.5.25.31.1.1.1.1.7.16842753");
				if (mused != null && !mused.isEmpty()) {
					int mu = Integer.parseInt(mused);
					if (mu <= 100) {
						memused = mu;
					}
				}
			} catch (Exception ex) {

			}
		} else if (sysOid1.equals("1.3.6.1.4.1.25506.1.604")) {
			try {
				mused = snmpget.getNextValues(para,
						"1.3.6.1.4.1.25506.2.6.1.1.1.1.8");
				if (mused != null && !mused.isEmpty()) {
					int mu = Integer.parseInt(mused);
					if (mu <= 100) {
						memused = mu;
					}
				}
			} catch (Exception ex) {

			}

		} else {
			try {
				if (!MemFreeOID.isEmpty() && !MemUseOID.isEmpty()) {
					if (MemFreeOID.endsWith(".0")) {
						mfree = snmpTools.getValue(para, MemFreeOID);
					} else {
						mfree = snmpTools.getNextValues(para, MemFreeOID);
					}
					if (MemUseOID.endsWith(".0")) {
						mused = snmpTools.getValue(para, MemUseOID);
					} else {
						mused = snmpTools.getNextValues(para, MemUseOID);
					}

					if (mfree != null && mused != null && !mfree.isEmpty()
							&& !mused.isEmpty()) {
						int mu = Integer.parseInt(mused);
						int mf = Integer.parseInt(mfree);
						if (mu <= 100 && mf <= 100) {
							memused = mu;
						} else {
							float v = ((float) mu / (mu + mf)) * 100;
							BigDecimal dd = new BigDecimal(v);
							dd.setScale(0, BigDecimal.ROUND_HALF_UP);
							memused = dd.intValue();
						}
					}
				}
				if (!MemUseOID.isEmpty() && !MemTotalOID.isEmpty()) {
					if (MemUseOID.endsWith(".0")) {
						mused = snmpTools.getValue(para, MemUseOID);
					} else {
						mused = snmpTools.getNextValues(para, MemUseOID);
					}
					if (MemTotalOID.endsWith(".0")) {
						mall = snmpTools.getValue(para, MemTotalOID);
					} else {
						mall = snmpTools.getNextValues(para, MemTotalOID);
					}
					if (mall != null && mused != null && !mused.isEmpty()
							&& !mall.isEmpty()) {
						int mu = Integer.parseInt(mused);
						int ma = Integer.parseInt(mall);
						if (mu > 100) {
							float v = (float) mu / ma * 100;
							BigDecimal dd = new BigDecimal(v);
							dd.setScale(0, BigDecimal.ROUND_HALF_UP);
							memused = dd.intValue();
						} else {
							memused = mu;
						}
					}
				}
				if (!MemFreeOID.isEmpty() && !MemTotalOID.isEmpty()) {
					if (MemTotalOID.endsWith(".0")) {
						mall = snmpTools.getValue(para, MemTotalOID);
					} else {
						mall = snmpTools.getNextValues(para, MemTotalOID);
					}
					if (MemFreeOID.endsWith(".0")) {
						mfree = snmpTools.getValue(para, MemFreeOID);
					} else {
						mfree = snmpTools.getNextValues(para, MemFreeOID);
					}
					if (mall != null && mfree != null && !mall.isEmpty()
							&& !mfree.isEmpty()) {
						int mf = Integer.parseInt(mfree);
						int ma = Integer.parseInt(mall);
						if (mf > 100) {
							float v = (float) (ma - mf) / ma * 100;
							BigDecimal dd = new BigDecimal(v);
							dd.setScale(0, BigDecimal.ROUND_HALF_UP);
							memused = dd.intValue();
						} else {
							memused = 100 - mf;
						}
					}
				}

				if (!MemFreeOID.isEmpty() && MemTotalOID.isEmpty()
						&& MemUseOID.isEmpty()) {
					if (MemFreeOID.endsWith(".0")) {
						mfree = snmpTools.getValue(para, MemFreeOID);
					} else {
						mfree = snmpTools.getNextValue(para, MemFreeOID);
					}
					if (mfree != null && !mfree.isEmpty()) {
						int mf = Integer.parseInt(mfree);
						if (mf <= 100) {
							memused = 100 - mf;
						}
					}

				}
				if (MemFreeOID.isEmpty() && MemTotalOID.isEmpty()
						&& !MemUseOID.isEmpty()) {
					if (MemUseOID.endsWith(".0")) {
						mused = snmpTools.getValue(para, MemUseOID);
					} else {
						mused = snmpTools.getNextValue(para, MemUseOID);
					}
					if (mused != null && !mused.isEmpty()) {
						int mu = Integer.parseInt(mused);
						if (mu <= 100) {
							memused = mu;
						}
					}
				}
			} catch (Exception ex) {

			}
		}
		return memused;
	}

	private int getcpu() {
		int cpu = 0;
		if (cpuoid == null) {
			return 0;
		}
		// // CpuOID
		// if (cpuoid.isEmpty()) {
		// Job jobcpu = new Job("cpujob") {
		// protected IStatus run(IProgressMonitor monitor) {
		// cpuoid = SelectExecute(svapi);
		//
		// return Status.OK_STATUS;
		// }
		// };
		// jobcpu.setPriority(Job.SHORT);
		// jobcpu.schedule();
		// }
		if (cpuoid.isEmpty()) {
			return 0;
		}
		SnmpPara para = new SnmpPara(entitynode.getLocalip(), entitynode
				.getProperys().get("Community"), 5000, 3, 2);
		String cpu1 = "0";
		String sysOid1 = entitynode.getProperys().get("sysObjectID");

		if (sysOid1.equals("1.3.6.1.4.1.2011.2.239.5")) {
			try {
				cpuoid = "1.3.6.1.4.1.2011.5.25.31.1.1.1.1.5.16842753";
				cpu1 = snmpTools.getValue(para, cpuoid);
				cpu = Integer.parseInt(cpu1);
			} catch (Exception ex) {

			}
		} else if (sysOid1.equals("1.3.6.1.4.1.25506.1.604")) {
			cpuoid = "1.3.6.1.4.1.25506.2.6.1.1.1.1.6";
			cpu1 = snmpget.getNextValues(para, cpuoid);
			cpu = Integer.parseInt(cpu1);
		} else {

			try {
				if (cpuoid.endsWith(".0")) {
					cpu1 = snmpTools.getValue(para, cpuoid);
				} else {
					cpu1 = snmpTools.getNextValue(para, cpuoid);
				}

				cpu = Integer.parseInt(cpu1);
			} catch (Exception ex) {

			}
		}
		return cpu;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "关闭", true);
		// createButton(parent, IDialogConstants.CANCEL_ID,
		// IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(320, 320);
	}

}
