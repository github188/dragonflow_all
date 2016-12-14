package com.siteview.ecc.yft.util;

import Siteview.Api.ISiteviewApi;

import com.siteview.ecc.foundation.AbsOrganization4MySQLService;

public class Organization4MySQLService extends AbsOrganization4MySQLService {

	public Organization4MySQLService(ISiteviewApi siteviewApi) {
		super(siteviewApi);
	}

	@Override
	public String getTreeNodeTableName() {
		return "Organization";
	}

	@Override
	public String getIdNameInField() {
		return "o_code";
	}

	@Override
	public String getParentIdNameInField() {
		return "parentId";
	}
	
}
