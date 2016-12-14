package com.siteview.NNM.dialogs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;

import com.siteview.NNM.modles.DPort;
import com.siteview.nnm.data.ConfigDB;
import com.siteview.nnm.data.FlowDataManager;
import com.siteview.nnm.data.PortManage;
import com.siteview.nnm.data.model.EdgeFlow;
import com.siteview.nnm.data.model.svEdge;
import com.siteview.topo.TopoMap;

import org.eclipse.swt.widgets.Button;

public class AddLine extends Dialog {
	private Text textip1;
	private Text textport1;
	private Text textip2;
	private Text textport2;
	private int nid1,nid2;
	TopoMap topochart;
	JsonArray data=null;
	Combo combo1;
	Combo combo2;
	String subtopo;
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public AddLine(Shell parentShell,JsonArray data,TopoMap topochart,String subtopo) {
		super(parentShell);
		this.data=data;
		this.topochart=topochart;
		this.subtopo=subtopo;
	}
	protected void configureShell(Shell newShell) {
		//newShell.setSize(450, 320);
		//newShell.setLocation(400, 175);
		newShell.setText("添加连线");
		super.configureShell(newShell);
	}
	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);//(Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		container.setLayout(new GridLayout());
		String ip1="";
		String ip2="";
		String nid1="";
		String nid2="";
		if(data!=null)
		{
			JsonObject jobj1 = data.get(0).asObject();
			ip1=jobj1.get("localip").asString();
			nid1=jobj1.get("sid").asString();
			JsonObject jobj2 = data.get(1).asObject();
			ip2=jobj2.get("localip").asString();
			nid2=jobj2.get("sid").asString();
		}
		Group group = new Group(container, SWT.NONE);
		group.setText("\u8BBE\u59071");
		GridData gd_group = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_group.heightHint = 131;
		gd_group.widthHint = 456;
		group.setLayoutData(gd_group);
		
		Label lblNewLabel = new Label(group, SWT.NONE);
		lblNewLabel.setBounds(10, 76, 91, 15);
		lblNewLabel.setText("\u8BBE\u5907\u7AEF\u53E3\uFF1A");
		
		textip1 = new Text(group, SWT.BORDER);
		textip1.setBounds(107, 29, 122, 21);
		textip1.setText(ip1);
		
		Label label = new Label(group, SWT.NONE);
		label.setText("\u8BBE\u5907IP\u5730\u5740\uFF1A");
		label.setBounds(10, 32, 91, 15);
		
		textport1 = new Text(group, SWT.BORDER);
		textport1.setBounds(107, 70, 122, 21);
		
		combo1 = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo1.setVisibleItemCount(15);
		combo1.setBounds(250, 70, 202, 23);
		if(nid1!=null && !nid1.isEmpty()){
		Connection conn= ConfigDB.getConn();
		String sql="select pindex,desc from ports where id='"+nid1+"'";
		ResultSet rs= ConfigDB.query(sql, conn);
		try {
			while(rs.next()){
				String pindex=rs.getString("pindex");
				String desc=rs.getString("desc");
				combo1.add(pindex+"["+desc+"]");
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(Exception ex){
			
		}
		ConfigDB.close(conn);
		if(combo1.getItemCount()==0){
			textport1.setText("0");
			combo1.add("0[虚拟端口]");
		}
		
		}
		combo1.addSelectionListener(new SelectionAdapter(){
	            @Override
	            public void widgetSelected(SelectionEvent e) {
	            	
	                String key = combo1.getText();
	                
	                String[] kkk= key.split("\\[");
	                String vvv= kkk[0];
	                textport1.setText(vvv);
	            }
	        });
		Button btnNewButton = new Button(group, SWT.NONE);
		btnNewButton.setBounds(235, 29, 33, 25);
		btnNewButton.setText("...");
		btnNewButton.setVisible(false);
		
		Group group_1 = new Group(container, SWT.NONE);
		GridData gd_group_1 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_group_1.heightHint = 141;
		gd_group_1.widthHint = 461;
		group_1.setLayoutData(gd_group_1);
		group_1.setText("\u8BBE\u59072");
		
		Label label_1 = new Label(group_1, SWT.NONE);
		label_1.setText("\u8BBE\u5907IP\u5730\u5740\uFF1A");
		label_1.setBounds(10, 33, 91, 15);
		
		textip2 = new Text(group_1, SWT.BORDER);
		textip2.setBounds(107, 30, 122, 21);
		textip2.setText(ip2);
		
		Label label_2 = new Label(group_1, SWT.NONE);
		label_2.setText("\u8BBE\u5907\u7AEF\u53E3\uFF1A");
		label_2.setBounds(10, 77, 91, 15);
		
		textport2 = new Text(group_1, SWT.BORDER);
		textport2.setBounds(107, 71, 122, 21);
		
		 combo2 = new Combo(group_1, SWT.DROP_DOWN | SWT.READ_ONLY);
		 combo2.setVisibleItemCount(15);
		 combo2.setBounds(250, 71, 202, 23);
		 if(nid2!=null && !nid2.isEmpty()){
				Connection conn= ConfigDB.getConn();
				String sql="select pindex,desc from ports where id='"+nid2+"'";
				ResultSet rs= ConfigDB.query(sql, conn);
				try {
					while(rs.next()){
						String pindex=rs.getString("pindex");
						String desc=rs.getString("desc");
						combo2.add(pindex+"["+desc+"]");
					}
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch(Exception ex){
					
				}
				ConfigDB.close(conn);
				if(combo2.getItemCount()==0){
					textport2.setText("0");
					combo2.add("0[虚拟端口]");
				}
				}
		 combo2.addSelectionListener(new SelectionAdapter(){
	            @Override
	            public void widgetSelected(SelectionEvent e) {
	            	
	                String key = combo2.getText();
	                
	                String[] kkk= key.split("\\[");
	                String vvv= kkk[0];
	                textport2.setText(vvv);
	            }
	        });
		Button button = new Button(group_1, SWT.NONE);
		button.setText("...");
		button.setBounds(235, 28, 33, 25);
		button.setVisible(false);

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "添加",
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				"取消", true);
	}
	protected void buttonPressed(int buttonId) {
		try{
			if(buttonId==IDialogConstants.OK_ID){
				if(textport1.getText().trim().isEmpty()){
					return;
				}
				if(textport2.getText().trim().isEmpty()){
					return;
				}
				if(data!=null)
				{
					JsonObject jobj1 = data.get(0).asObject();
					nid1=Integer.parseInt(jobj1.get("sid").asString().substring(1));
					JsonObject jobj2 = data.get(1).asObject();
					nid2=Integer.parseInt(jobj2.get("sid").asString().substring(1));
				}
				
				JsonObject da= com.siteview.nnm.data.DBManage.createLine(nid1, nid2, textport1.getText().trim(), textport2.getText().trim());
				topochart.setNewlinedata(da);
				EdgeFlow edgeFlow = new EdgeFlow();
				edgeFlow.leftid ="n"+nid1;
				edgeFlow.rightid = "n"+nid2;
				edgeFlow.leftport =  textport1.getText().trim();
				edgeFlow.rightport =  textport2.getText().trim();
				edgeFlow.isleft = true;
				edgeFlow.portId = "n"+nid1 + ":"
						+textport1.getText().trim();
				String edgeString=da.get("lid").asString();
				svEdge svedge=new svEdge();
				svedge.setLid(edgeString);
				svedge.setLsource(edgeFlow.leftid );
				svedge.setLtarget(edgeFlow.rightid );
				svedge.setSinterface(edgeFlow.leftport);
				svedge.setTinterface(edgeFlow.rightport);
				 com.siteview.nnm.data.DBManage.Topos.get(subtopo).getEdges().put(edgeString, svedge);
				FlowDataManager.AddEdgeFlowTask(edgeString, edgeFlow);
			}else
			{
				topochart.setNewlinedata(new JsonObject().add("lid", "nil"));
			}
			super.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(498, 413);
	}
}
