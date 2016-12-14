package com.siteview.ecc.yft.bundle;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;

import com.siteview.ecc.foundation.SecurityGroupManager;
import com.siteview.ecc.yft.report.InitReport;
import com.siteview.utils.db.DBQueryUtils;

import Siteview.ISecurityGroup;
import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ConnectionBroker;
import siteview.IAutoTaskExtension;

public class ImportUnit implements IAutoTaskExtension {

	public ImportUnit() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public String run(Map<String, Object> params) throws Exception {
		BusinessObject bo = (BusinessObject) params.get("_CUROBJ_");
		ISiteviewApi api = (ISiteviewApi) params.get("_CURAPI_");
		String name=bo.GetField("o_name").get_NativeValue().toString();
		String code=bo.GetField("o_code").get_NativeValue().toString();
		String pcode=bo.GetField("parentId").get_NativeValue().toString();
		SecurityGroupManager groupManager = new SecurityGroupManager(api);
		if(name==null||name.equals("")){
			bo.DeleteObject(api);
			return null;
		}
		Collection<BusinessObject> col=DBQueryUtils.getBussCollection("o_name", name, "Organization", api);
		if(col.size()>1){
			Iterator<BusinessObject> ite=col.iterator();
			while(ite.hasNext()){
				BusinessObject bo1=ite.next();
				if(!bo1.get_RecId().equals(bo.get_RecId())){
					bo1.GetField("o_code").SetValue(new SiteviewValue(code));
					bo1.SaveObject(api, true, true);
					bo.DeleteObject(api);
				}
			}
			return null;
		}
		
		col=DBQueryUtils.getBussCollection("o_code", code, "Organization", api);
		if(col.size()>1){
			Iterator<BusinessObject> ite=col.iterator();
			while(ite.hasNext()){
				BusinessObject bo1=ite.next();
				if(!bo1.get_RecId().equals(bo.get_RecId())){
					String id=bo1.get_RecId();
					DBQueryUtils.UpdateorDelete("delete from OrganizeAndSafeGroupRel where organize_id='"+id+"'", api);
					String name1=bo1.GetField("o_name").get_NativeValue().toString();
					ISecurityGroup group=null;
					try{
						group=groupManager.getSecurityGroup(name1);
					}catch(Exception e){
					}
					if(group!=null){
						group.ClearRoles();
						groupManager.deleteSecurityGroup(group);
					}
					bo1.DeleteObject(api);
				}
			}
			return null;
		}
		
		String des=bo.GetField("o_desc").get_NativeValue().toString();
		ISecurityGroup group =null;
		try{
			group=groupManager.getSecurityGroup(name);
		}catch(Exception e){
		}	
		if(group==null)
			group=groupManager.createSecurityGroup(name, des);
		group.AddRole("管理员");
		groupManager.saveSecurityGroup(group);
		BusinessObject borel=api.get_BusObService().Create("OrganizeAndSafeGroupRel");
		borel.GetField("safegroup_id").SetValue(new SiteviewValue(group.get_Id()));
		borel.GetField("organize_id").SetValue(new SiteviewValue(bo.get_RecId()));
		borel.GetField("organize_table").SetValue(new SiteviewValue("Organization"));
		borel.SaveObject(api, true, true);
		if(InitReport.map!=null&&InitReport.map.get("the_parent_domain")!=null){
			BusinessObject g=DBQueryUtils.queryOnlyBo("GroupCode", code, "EccGroup", api);
			if(g==null)
				g=api.get_BusObService().Create("EccGroup");
			g.GetField("GroupName").SetValue(new SiteviewValue(name));
			g.GetField("GroupCode").SetValue(new SiteviewValue(code));
			g.GetField("ParentGroupId").SetValue(new SiteviewValue(pcode));
			g.GetField("Status").SetValue(new SiteviewValue("good"));
			g.GetField("Sorter").SetValue(new SiteviewValue(1000));
			g.SaveObject(api, false, false,false,false,false);
		}
		return null;
	}

	@Override
	public boolean hasCustomUI() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void creatConfigUI(Composite parent, Map<String, String> params) {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, String> getConfig() {
		// TODO Auto-generated method stub
		return null;
	}

}
