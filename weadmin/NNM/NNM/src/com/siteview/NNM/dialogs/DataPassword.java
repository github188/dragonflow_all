package com.siteview.NNM.dialogs;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.siteview.ecc.monitor.nls.EccMessage;
import com.siteview.nnm.data.ConfigDB;

import Siteview.Windows.Forms.MsgBox;


public class DataPassword extends Dialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6292576636631289460L;
	public DataPassword(Shell parentShell) {
		super(parentShell);
		
	}
	Combo texttype;
	Text name;
	Text textname;
	Text textdesc;
	Text textuser;
	Text textpwd;
	Text textprot;
	Text texttimeout;
	Button textprotocol;
	private Map<String,String> db;
	
	@Override
	protected Point getInitialSize() {
		return new Point(400, 400);
	}
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("数据库");
	}
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite com=(Composite) super.createDialogArea(parent);
		GridLayout gridLayout = new GridLayout(2,false);
		gridLayout.horizontalSpacing = 30;
		gridLayout.verticalSpacing = 10;
		com.setLayout(gridLayout);
		
		GridData data = new GridData();
		data.widthHint = 250;
		
		GridData data_2 = new GridData();
		data_2.widthHint = 250;
		data_2.heightHint=40;
		
		Label lable_name=new Label(com, SWT.NONE);
		lable_name.setText("名称");
		name=new Text(com, SWT.BORDER);
		name.setLayoutData(data);
		
		Label labletype=new Label(com, SWT.NONE);
		labletype.setText("数据库类型");
		texttype=new Combo(com, SWT.BORDER|SWT.READ_ONLY);
		texttype.setLayoutData(data);
		texttype.add("Oracle");
		texttype.add("MySql");
		texttype.add("SqlServer");
		texttype.select(0);
		texttype.addModifyListener(new ModifyListener() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 6340870020614691009L;

			@Override
			public void modifyText(ModifyEvent event) {
				String type=texttype.getText();
				if(type.equals("Oracle"))
					textprot.setText("1521");
				if(type.equals("MySql"))
					textprot.setText("3306");
				if(type.equals("SqlServer"))
					textprot.setText("1433");
//				if(type.equals("DB2"))
//					textprot.setText("1521");
			}
		});
		
		Label lablename=new Label(com, SWT.NONE);
		lablename.setText("数据库名");
		textname=new Text(com, SWT.BORDER);
		textname.setLayoutData(data);
		
//		GridData data_1 = new GridData();
//		data_1.heightHint = 80;
//		data_1.widthHint = 250;
		Label labledesc=new Label(com, SWT.NONE);
		labledesc.setText(EccMessage.get().DESCRIPTION);
		textdesc = new Text(com, SWT.BORDER|SWT.WRAP);
		textdesc.setLayoutData(data_2);
		
		Label lableuser=new Label(com, SWT.NONE);
		lableuser.setText("用户名");
		textuser=new Text(com, SWT.BORDER);
		textuser.setLayoutData(data);
		
		Label lablepwd=new Label(com, SWT.NONE);
		lablepwd.setText("密码");
		textpwd=new Text(com, SWT.BORDER|SWT.PASSWORD);
		textpwd.setLayoutData(data);
		
		Label lableprot=new Label(com, SWT.NONE);
		lableprot.setText("端口");
		textprot=new Text(com, SWT.BORDER);
		textprot.setLayoutData(data);
		textprot.setText("1521");
		
		Label labletimeout=new Label(com, SWT.NONE);
		labletimeout.setText("超时");
		texttimeout=new Text(com, SWT.BORDER);
		texttimeout.setLayoutData(data);
		texttimeout.setText("5");
		
		return com;
	}

	
	@Override
	protected void buttonPressed(int buttonId) {
		if(buttonId==Dialog.OK){
			if("".equals(textname.getText().toString())){
				MsgBox.ShowMessage("名称不能为空");
				return;
			}
			if("".equals(textuser.getText().toString())){
				MsgBox.ShowMessage("用户名不能为空");
				return;
			}
			if("".equals(textpwd.getText().toString())){
				MsgBox.ShowMessage("密码不能为空");
				return;
			}
			try {
				Connection conn= ConfigDB.getConn();
				String sql="insert into databasetype(name,description,dbtype,dbname,user,pass,port,timeout) values('"
				+name.getText()+"','"
				+textdesc.getText()+"','"
				+texttype.getText()+"','"
				+textname.getText()+"','"
				+textuser.getText()+"','"
				+textpwd.getText()+"','"
				+textprot.getText()+"','"
				+texttimeout.getText()+"');";
				ConfigDB.excute(sql, conn);
				db=new HashMap<String,String>();
				db.put("name", name.getText());
				db.put("description", textdesc.getText());
				db.put("dbtype", texttype.getText());
				db.put("dbname", textname.getText());
				db.put("user", textuser.getText());
				db.put("pass", textpwd.getText());
				db.put("port", textprot.getText());
				db.put("timeout", texttimeout.getText());
				
				ConfigDB.close(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		super.buttonPressed(buttonId);
	}

	public Map<String,String> getDB(){
		return db;
	} 
	
}
