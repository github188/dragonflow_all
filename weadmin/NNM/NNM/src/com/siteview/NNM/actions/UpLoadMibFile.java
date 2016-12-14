package com.siteview.NNM.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.actions.ActionDelegate;
import Siteview.SiteviewException;
import Siteview.Windows.Forms.MsgBox;

public class UpLoadMibFile extends ActionDelegate{

	public UpLoadMibFile() {
		// TODO Auto-generated constructor stub
	}
	public void run(IAction ation){
		try {
//			FileDialog fileDialog=new FileDialog(Display.getDefault().getActiveShell());
//			fileDialog.setFilterNames(new String[]{"MIB文件"});
//			fileDialog.setFilterExtensions(new String[]{""});
//			fileDialog.setFilterPath(LoadMib.mibpath);
//			String path=fileDialog.open();
//			System.out.println(path);
			MsgBox.ShowConfirm("正在火热开发中.敬请期待");
		} catch (Exception e2) {
			MsgBox.ShowException(new SiteviewException(e2.getMessage(),e2));
		}
	}

	 public void selectionChanged(IAction action, ISelection selection) {
		
	 }
}
