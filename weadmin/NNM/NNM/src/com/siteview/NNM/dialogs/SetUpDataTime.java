package com.siteview.NNM.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class SetUpDataTime extends Dialog{
	DateTime stdt;
	DateTime stt;
	DateTime eddt;
	DateTime edt;
	String startime="";
	String endtime="";
	public SetUpDataTime(Shell parentShell) {
		super(parentShell);
	}
	protected void configureShell(Shell newShell) {
		newShell.setSize(300, 150);
		newShell.setLocation(400, 175);
		newShell.setText("自定义时间");
		super.configureShell(newShell);
	}
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout taw=new GridLayout();
		taw.numColumns=3;
		container.setLayout(taw);
		Label stlb=new Label(container, SWT.NONE);
		stlb.setText("开始时间:");
		
		stdt=new DateTime(container, SWT.DATE);
		
		stt=new DateTime(container, SWT.TIME);
		
		Label edlb=new Label(container, SWT.NONE);
		edlb.setText("结束时间:");
		
		eddt=new DateTime(container, SWT.DATE);
		
		edt=new DateTime(container, SWT.TIME);
		startime=stdt.getYear()+"-"+(stdt.getMonth()+1)+"-"
				+stdt.getDay()+" "+stt.getHours()+":"+stt.getMinutes()+":"+stt.getSeconds();
		endtime=eddt.getYear()+"-"+(eddt.getMonth()+1)+"-"
				+eddt.getDay()+" "+edt.getHours()+":"+edt.getMinutes()+":"+edt.getSeconds();
		return container;
	}
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button button = createButton(parent, IDialogConstants.OK_ID, "确定",
				true);
	}
	protected void buttonPressed(int buttonId) {
		try{
			if(buttonId==IDialogConstants.OK_ID){
				startime=stdt.getYear()+"-"+(stdt.getMonth()+1)+"-"
						+stdt.getDay()+" "+stt.getHours()+":"+stt.getMinutes()+":"+stt.getSeconds();
				endtime=eddt.getYear()+"-"+(eddt.getMonth()+1)+"-"
						+eddt.getDay()+" "+edt.getHours()+":"+edt.getMinutes()+":"+edt.getSeconds();
			}
			this.close();
		}catch (Exception e) {
		}
	}
	public String getStartime() {
		return startime;
	}
	public String getEndtime() {
		return endtime;
	}
	
}
