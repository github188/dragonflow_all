package com.siteview.NNM.uijobs;

import java.util.TimerTask;

import org.eclipse.swt.widgets.Display;

import com.siteview.NNM.Editors.TopNetWorkEditor;

public class MyTimer extends TimerTask{
	TopNetWorkEditor top;
	Display display;
	JobUtils job;
	public MyTimer(TopNetWorkEditor top,Display display) {
		this.top=top;
		this.display=display;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(job==null)
			job=new JobUtils();
		job.refreshtop(top, display);
	}
	public TopNetWorkEditor getTop() {
		return top;
	}
}
