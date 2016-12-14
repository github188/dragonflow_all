package com.siteview.NNM.modles;

import com.siteview.ecc.authorization.ItossAuthorizeServiceImpl;
import com.siteview.ecc.authorization.PermissionFactory;
import com.siteview.ecc.constants.Operation;
import com.siteview.ecc.constants.Resource;

public class IpManagermentModle {
	public  final String name="IP管理";
	private SubnetModle sub;
	private IpMacChangeModle ipmac;
	public IpManagermentModle(){
		boolean flag=ItossAuthorizeServiceImpl.getInstance().isPermitted
				(PermissionFactory.createPermission(
						Resource.IP_MANAGEMENT_CHILD_NETWORK.getType(), Operation.IP_MANAGEMENT_CHILD_NETWORK.getOperationString(), "*"));
		if(flag)
			sub=new SubnetModle();
		flag=ItossAuthorizeServiceImpl.getInstance().isPermitted
				(PermissionFactory.createPermission(
						Resource.IP_MANAGEMENT_MAC_CHANGE.getType(), Operation.IP_MANAGEMENT_MAC_CHANGE.getOperationString(), "*"));
		if(flag)
			ipmac=new IpMacChangeModle();
	}
	public String getName() {
		return name;
	}
	public SubnetModle getSub() {
		return sub;
	}
	public IpMacChangeModle getIpmac() {
		return ipmac;
	}
	
}
