package com.siteview.NNM.dialogs;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.siteview.snmpinterface.EquipmentQueuer;
import com.siteview.snmpinterface.entities.FlowData;
import com.siteview.svchart.svgChart;
import com.siteview.topology.model.SnmpPara;
import com.siteview.topology.snmp.snmpTools;

public class PortDialog extends Dialog {

	private String portindex;
	private String portkey;
	private String portip;
	private String comm = "public";
	String inflow = "0 Kbps";
	String outflow = "0 Kbps";
	int infow1 = 0;
	int outflow1 = 0;
	int vvv = 0;
	long preinflow = 0;
	long preoutflow = 0;
	long pretime = 0;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public PortDialog(Shell parentShell, String portindex, String portkey,
			String portip, String comm) {
		super(parentShell);
		this.portindex = portindex;
		this.portkey = portkey;
		this.portip = portip;
		this.comm = comm;
	}

	protected void configureShell(Shell newShell) {

		newShell.setText(portip + "端口流量(5秒) - 端口" + portindex);
		super.configureShell(newShell);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(final Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(300, 120));
		
		final svgChart chart1 = new svgChart(container, SWT.CALENDAR);
		chart1.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.getDisplay().timerExec(3000, new Runnable() {
			public void run() {
				//getport();
				getaport();
				chart1.setVv(infow1);
				//System.out.println("infow1:" + infow1);
				chart1.setLabel("入流量：" + inflow);
				parent.getDisplay().timerExec(5000, this);
			}
		});
		final svgChart chart2 = new svgChart(container, SWT.CALENDAR);
		chart2.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.getDisplay().timerExec(3000, new Runnable() {
			public void run() {
				chart2.setVv(outflow1);
				//System.out.println("outflow1:" + outflow1);
				chart2.setLabel("出流量：" + outflow);
				parent.getDisplay().timerExec(2000, this);
			}
		});
		return container;
	}

	public void getaport() {
		try{
		SnmpPara para = new SnmpPara(portip.trim(), comm, 2000, 1, 2);
		String portspeed = snmpTools.getValue(para, "1.3.6.1.2.1.2.2.1.5."+portindex);
		if (portspeed != null) {
			String inff = "0";
			String outff = "0";
			//System.out.println(portspeed);
			long portSpeed = Long.parseLong(portspeed);
			if (portSpeed >= 1000000000) {
				inff = snmpTools.getValue(para, "1.3.6.1.2.1.31.1.1.1.6."+portindex);
				
				long currtime=System.currentTimeMillis();
				long usetime =currtime - pretime;
				if (inff != null) {
					if (pretime > 0) {
						long currv= Long.parseLong(inff);
						double HcInSpeed= getHcInOctetSpeed(currv,preinflow,usetime);
						inflow = getSpeed(HcInSpeed * 1000);
						infow1 = vvv;
						preinflow = Long.parseLong(inff);
					} else {
						preinflow = Long.parseLong(inff);
					}
				}
				outff = snmpTools.getValue(para, "1.3.6.1.2.1.31.1.1.1.10."+portindex);
				currtime=System.currentTimeMillis();
				usetime =currtime - pretime;
				if (outff != null) {
					if (pretime > 0) {
						long currv= Long.parseLong(outff);
						double HcOutSpeed= getHcInOctetSpeed(currv,preoutflow,usetime);
						outflow = getSpeed(HcOutSpeed * 1000);
						outflow1 = vvv;
						pretime = System.currentTimeMillis();
						preoutflow = Long.parseLong(outff);
					} else {
						pretime = System.currentTimeMillis();
						preoutflow = Long.parseLong(outff);
					}
				}

			} else {
				inff = snmpTools.getValue(para, "1.3.6.1.2.1.2.2.1.10."+portindex);
				
				long currtime=System.currentTimeMillis();
				long usetime =currtime - pretime;
				if (inff != null) {
					if (pretime > 0) {
						long currv= Long.parseLong(inff);
						float HcInSpeed= getInOctetSpeed(currv,preinflow,usetime);
						inflow = getSpeed(HcInSpeed );
						infow1 = vvv;
						preinflow = Long.parseLong(inff);
					} else {
						preinflow = Long.parseLong(inff);
					}
				}
				outff = snmpTools.getValue(para, "1.3.6.1.2.1.2.2.1.16."+portindex);
				if (outff != null) {
					if (pretime > 0) {
						long currv= Long.parseLong(outff);
						float HcOutSpeed= getInOctetSpeed(currv,preoutflow,usetime);
						outflow = getSpeed(HcOutSpeed );
						outflow1 = vvv;
						pretime = System.currentTimeMillis();
						preoutflow = Long.parseLong(outff);
					} else {
						pretime = System.currentTimeMillis();
						preoutflow = Long.parseLong(outff);
					}
				}
			}
		}
		}catch(Exception ex){
			
		}
	}
	public static final DecimalFormat format=new java.text.DecimalFormat("#.00");
	
	public static float getInOctetSpeed(long nowInFlow, long preInFlow,long mse) {
	float ss=mse/1000f;
	long speedtemp= getDiff(preInFlow,  nowInFlow);
	float speed = speedtemp * 8 /mse;
	//System.err.println("speed "+preInFlow+"-"+nowInFlow+"-"+speed+"-"+speedtemp+"-"+ss);
//	BigDecimal bg = new BigDecimal(speed);
//	return bg.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
		return Float.parseFloat(format.format(speed));
}
	public static long getDiff(long lastOctet, long octet) {
		if (lastOctet == 0) {
			return 0;
		}
		long l3=0;
		if (octet < lastOctet) {
			l3 = (octet + (4294967295L - lastOctet));
		} else {
			l3 = octet - lastOctet;
		}
		return l3;
	}
	//千兆接口速率 M
	public static double getHcInOctetSpeed(long nowInFlow, long preInFlow, long mse){
		double speedtemp = getDiffHc(preInFlow, nowInFlow);
		double speed = speedtemp * 8 / mse / 1000;
//		BigDecimal bg = new BigDecimal(speed);
//		return bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return Double.parseDouble(format.format(speed));
	}
	public static long getDiffHc(long lastOctet, long octet) {
		if (lastOctet == 0) {
			return 0l;
		}
//		long l=18446744073709551615L;
		long l3 = 0;
		BigDecimal last = new BigDecimal(lastOctet);
		BigDecimal now = new BigDecimal(octet);
		BigDecimal max = new BigDecimal("18446744073709551616");
		
		if (last.compareTo(now)>0) {
//			l3 = (octet + (18446744073709551616f - lastOctet));
			l3 =  now.add(max.subtract(last)).longValue();
		} else {
//			l3 = octet - lastOctet;
			l3 = now.subtract(last).longValue();
		}
		return l3;
	}
	public void getport() {

		Map<String, FlowData> ports = EquipmentQueuer.getEquipmentData(portkey);
		FlowData fdata = ports.get(portindex);
		if (fdata == null) {
			return;
		}

		if (fdata.getPortSpeed() >= 1000000000) {
			inflow = getSpeed(fdata.getHcInSpeed() * 1000);
			infow1 = vvv;
			outflow = getSpeed(fdata.getHcOutSpeed() * 1000);
			outflow1 = vvv;

		} else {
			inflow = getSpeed(fdata.getInFlow());
			infow1 = vvv;
			outflow = getSpeed(fdata.getOutFlow());
			outflow1 = vvv;

		}
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
		// , false);
	}

	private String getSpeed(double v1) {
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
		// double vv1= vv ;
		// if( vv1 > 10 ){
		// vv1 = (vv1 /10);
		// if(vv1 > 10){
		// vv1 = (vv1 /10);
		// if( vv1 >10){
		// vv1 = (vv1 /10);
		// if (vv1 >10)
		// vv1 = (vv1 /10);
		// }
		// }
		//
		//
		// }
		vvv = new BigDecimal(vv).setScale(1, BigDecimal.ROUND_HALF_UP)
				.intValue();
		BigDecimal bg = new BigDecimal(vv);
		return bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + dww;
	}

	private String getSpeed(float v1) {
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

		// float vv1= vv ;
		// if( vv1 > 10 ){
		// vv1 = (vv1 /10);
		// if(vv1 > 10){
		// vv1 = (vv1 /10);
		// if( vv1 >10){
		// vv1 = (vv1 /10);
		// if (vv1 >10)
		// vv1 = (vv1 /10);
		// }
		// }
		//
		//
		// }
		vvv = new BigDecimal(vv).setScale(1, BigDecimal.ROUND_HALF_UP)
				.intValue();
		BigDecimal bg = new BigDecimal(vv);
		return bg.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue() + dww;
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(300, 190);
	}

}
