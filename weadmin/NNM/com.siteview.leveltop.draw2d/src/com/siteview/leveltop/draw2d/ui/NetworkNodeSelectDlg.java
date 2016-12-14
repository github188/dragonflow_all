package com.siteview.leveltop.draw2d.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.SiteviewException;
import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ConnectionBroker;
import Siteview.Windows.Forms.MsgBox;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;

public class NetworkNodeSelectDlg extends Dialog{

	private static final long serialVersionUID = 1L;
	
	private Combo comboType;
	private Combo comboRelationType;
	private Combo comboRelationNode;
	
	private String nodeId;
	private String nodeName;
	private String equipmentId;
	private String equipmentTypeName;
	
	private ISiteviewApi siteviewApi;

	private Text txtName;
	
	private String newNodeId;
	private String selectReleationId;
	
	private boolean isBindRelation;
	private boolean isLoadRelation;
	
	private List<String> typeRelationList = new ArrayList<String>();

	private Composite relationComposite;

	protected NetworkNodeSelectDlg(Shell parentShell) {
		super(parentShell);
		siteviewApi = ConnectionBroker.get_SiteviewApi();
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("拓扑图节点");
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(350,300);
	}
	
	public void setNodeId(String nodeId){
		this.nodeId = nodeId;
	}
	
	public void setNodeName(String nodeName){
		this.nodeName = nodeName;
	}
	
	public void setEquipmentId(String equipmentId){
		this.equipmentId = equipmentId;
	}	
	
	public void setEquipmentTypeName(String eqTypeName){
		this.equipmentTypeName = eqTypeName;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(null);
		
		Label lblName = new Label(composite, SWT.RIGHT);
		lblName.setBounds(10, 10, 80, 22);
		lblName.setText("节点名称:");
		
		txtName = new Text(composite, SWT.BORDER);
		txtName.setBounds(100, 8, 200, 22);
		
		Label lblType = new Label(composite, SWT.RIGHT);
		lblType.setBounds(10, 40, 80, 22);
		lblType.setText("节点类型:");
		
		comboType = new Combo(composite, SWT.READ_ONLY);
		comboType.setBounds(100, 38, 200, 22);
		
		comboType.addSelectionListener(new SelectionAdapter() {

			private static final long serialVersionUID = 1L;
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				comboTypeEvent();
			}
		});
		
		Button isRelationBtn = new Button(composite, SWT.CHECK);
		isRelationBtn.setBounds(100, 75, 192, 17);
		isRelationBtn.setText("\u662F\u5426\u624B\u52A8\u6307\u5B9A\u5173\u7CFB\u8282\u70B9");
		
		isRelationBtn.addSelectionListener(new SelectionAdapter() {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				isBindRelation = ((Button)e.getSource()).getSelection();
				relationComposite.setEnabled(isBindRelation);
				if(isBindRelation&&!isLoadRelation){
					try {
						loadRelationInfo();
						comboTypeEvent();
					} catch (SiteviewException e1) {
						e1.printStackTrace();
					}
				}
			}
			
		});
		
		relationComposite = new Composite(composite, SWT.NONE);
		relationComposite.setBounds(10, 100, 303, 84);
		relationComposite.setEnabled(false);
		
		Label lblRelation = new Label(relationComposite, SWT.RIGHT);
		lblRelation.setBounds(0, 16, 80, 22);
		lblRelation.setText("网络关系:");
		
		comboRelationType = new Combo(relationComposite, SWT.READ_ONLY);
		comboRelationType.setBounds(90, 12, 200, 25);
		
		Label lblRelationNode = new Label(relationComposite, SWT.RIGHT);
		lblRelationNode.setBounds(0, 46, 80, 22);
		lblRelationNode.setText("关系节点:");
		
		comboRelationNode = new Combo(relationComposite, SWT.READ_ONLY);
		comboRelationNode.setBounds(90, 45, 200, 25);
		
		comboRelationType.addSelectionListener(new SelectionAdapter() {
		
			private static final long serialVersionUID = 1L;
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				comboRelationTypeEvent();
			}
		});
		
//		loadNodeInfo();
		
		return composite;
	}
	
	public void loadNodeInfo(){
		try{
			
			txtName.setText(nodeName);
			
			DataTable dt = siteviewApi.get_NativeSQLSupportService().ExecuteNativeSQLQuery("select * from NetWorkNodeType where TopType='0'",null);
			if(dt.get_Rows().size()>0){
				for(DataRow dr:dt.get_Rows()){
					comboType.add(dr.get_Item("TypeName").toString());
					comboType.setData(dr.get_Item("TypeName").toString(),dr.get_Item("RecId").toString());
				}
				dt = siteviewApi.get_NativeSQLSupportService().ExecuteNativeSQLQuery("SELECT TypeAlias FROM EquipmentTypeRel WHERE RecId IN (SELECT ParentId FROM EquipmentTypeRel WHERE EqName='"+equipmentTypeName+"')",null);
				if(dt.get_Rows().size()>0){
					String typeAlias = (String) dt.get_Rows().get(0).get("TypeAlias");
					if(!typeAlias.equals("服务器")&&!typeAlias.equals("网络设备")){
						typeAlias = "应用";
					}
					int sel = -1;
					for(int i=0;i<comboType.getItemCount();i++){
						String item = comboType.getItem(i);
						if(item.indexOf(typeAlias)>-1){
							sel = i;
							break;
						}
					}
					if(sel!=-1){
						comboType.select(sel);
					}
				}
				if(comboType.getSelectionIndex()==-1){
					comboType.select(0);
				}
			}
			
			comboTypeEvent();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void loadRelationInfo() throws SiteviewException{
		DataTable dt = siteviewApi.get_NativeSQLSupportService().ExecuteNativeSQLQuery("select nwr.*,n1.TypeName tname,n1.RecId tid from (select nr.*,n.TypeName fname,n.RecId fid " + "from NetWorkNodeTypeRelation nr,NetWorkNodeType " + "n where nr.FromType=n.RecId) nwr,NetWorkNodeType n1 where nwr.ToType=n1.RecId", null);
		for (DataRow dr : dt.get_Rows()) {
			String fname = dr.get("fname") == null ? "" : dr.get("fname").toString();
			String tname = dr.get("tname") == null ? "" : dr.get("tname").toString();
			boolean isSame = fname.trim().toLowerCase().equals(tname.trim().toLowerCase());
			String prefix = isSame?"平面":"立体";
			String fullname = prefix+"："+(isSame?fname:(fname + "-" + tname));
			typeRelationList.add(fullname);
			comboRelationType.setData(fullname,dr.get_Item("RecId").toString());
		}
		Collections.sort(typeRelationList,new Comparator<String>() {
			@Override
			public int compare(String arg0, String arg1) {
				return arg1.compareTo(arg0);   
			}
		});
		
		isLoadRelation = true;
	}
	
	public void comboTypeEvent(){
		comboRelationType.removeAll();
		for(String relationType:typeRelationList){
			if(relationType.contains(comboType.getText())){
				comboRelationType.add(relationType);
			}
		}
		comboRelationType.select(0);
		comboRelationTypeEvent();
	}
	
	public void comboRelationTypeEvent(){
		comboRelationNode.removeAll();
		String content = comboRelationType.getText();
		int index1 = content.indexOf("立体：");
		int index2 = content.indexOf("平面：");
		String target = null;
		if(index1!=-1){
			target = content.replace("立体：", "").replaceAll(comboType.getText(),"").replaceAll("-","");
		}
		else if(index2!=-1){
			target = content.replace("平面：", "");
		}
		if(target!=null){
			try {
				DataTable dt = siteviewApi.get_NativeSQLSupportService().ExecuteNativeSQLQuery("SELECT RecId,NodeName,NodeId FROM NetWorkNode WHERE NodeType IN (SELECT RecId FROM NetWorkNodeType WHERE TypeName='"+target+"')",null);
				for(DataRow dr:dt.get_Rows()){
					comboRelationNode.add(dr.get_Item("NodeName").toString());
					comboRelationNode.setData(dr.get_Item("NodeName").toString(),dr.get_Item("RecId").toString());
				}
				if(comboRelationNode.getItemCount()>0){
					comboRelationNode.select(0);
				}
			} catch (SiteviewException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void save(){
		try{
			BusinessObject bo_node = siteviewApi.get_BusObService().Create("NetWorkNode");
			bo_node.GetField("NodeType").SetValue(new SiteviewValue(comboType.getData(comboType.getText())));
			bo_node.GetField("NodeId").SetValue(new SiteviewValue(nodeId));
			bo_node.GetField("NodeName").SetValue(new SiteviewValue(nodeName));
			bo_node.GetField("NodeRelationMonitor").SetValue(new SiteviewValue(equipmentId));
			
			bo_node.SaveObject(siteviewApi, false,true);
			
			if(comboRelationNode.getText().trim().length()>0){
				BusinessObject bo_nodeRelation = siteviewApi.get_BusObService().Create("NetworkRelation");
				bo_nodeRelation.GetField("RelationLevel").SetValue(new SiteviewValue(comboRelationType.getData(comboRelationType.getText())));
				bo_nodeRelation.GetField("FromNode").SetValue(new SiteviewValue(bo_node.get_RecId()));
				bo_nodeRelation.GetField("ToNode").SetValue(new SiteviewValue(comboRelationNode.getData(comboRelationNode.getText())));
				bo_nodeRelation.SaveObject(siteviewApi, false,true);
				selectReleationId = (String) comboRelationNode.getData(comboRelationNode.getText());
			}
			
			newNodeId = bo_node.get_RecId();
			
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public boolean validate(String[] values,String[] errorInfo){
		if(values.length>0){
			for(int i=0;i<values.length;i++){
				if(values[i]==null||values[i].trim().length()==0){
					MsgBox.ShowWarning(errorInfo[i]);
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		if(IDialogConstants.OK_ID == buttonId){
//			if(validate(new String[]{comboType.getText(),comboRelationType.getText(),comboRelationNode.getText()},new String[]{"节点类型","节点关系","关系节点"})){
			if(validate(new String[]{comboType.getText()},isBindRelation?new String[]{"节点类型"}:new String[]{"节点类型","节点关系","关系节点"})){
				save();
			}
			else{
				return;
			}
		}
		super.buttonPressed(buttonId);
	}
	
	public String getSelectRelationNodeId(){
		return selectReleationId;
	}
	
	public String getNodeId(){
		return newNodeId;
	}
}
