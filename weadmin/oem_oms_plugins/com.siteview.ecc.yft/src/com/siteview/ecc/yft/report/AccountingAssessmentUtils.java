package com.siteview.ecc.yft.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Siteview.DataRow;
import Siteview.DataTable;
import Siteview.Api.ISiteviewApi;

import com.siteview.ecc.elasticsearchutil.ElasticSearchIndexer;
import com.siteview.ecc.elasticsearchutil.ElasticSearchIndexerImpl;
import com.siteview.ecc.yft.bean.AccountingAssessment;
import com.siteview.ecc.yft.es.AccountingAssessmentReport;
import com.siteview.utils.date.DateUtils;
import com.siteview.utils.db.DBUtils;
import com.siteview.utils.html.StringUtils;

/**
 * 计费考核获取数据方法
 * 
 * @author Administrator
 *
 */
public class AccountingAssessmentUtils {

	/**
	 * 获取计费考核数据
	 * @param api
	 * @param startTime
	 * @param eDate
	 * @return
	 */
	public static List<AccountingAssessment> getAccounttingAssessments(ISiteviewApi api,
			String startTime, Date eDate) {
		List<AccountingAssessment> list = new ArrayList<AccountingAssessment>();
		String sql = "SELECT wc.CreatedDateTime,wc.WorkOrderNumber,wc.SLAId,wc.TimeOutNumber,wc.ParentId,sla.TotalCount,(wc.TimeOutNumber*sla.TotalCount)cost,sla.ServiceContent"
				+ " FROM WorkOrderCommon wc,ServiceLevelAgreement sla "
				+ " WHERE wc.SLAId != '' AND wc.TimeOutNumber > 0 AND wc.SLAId=sla.RecId "
				+ " AND wc.LastModDateTime >= '%s' AND wc.LastModDateTime <= '%s'";
		DataTable dataTable = DBUtils.select(
				String.format(sql, startTime, DateUtils.formatDefaultDate(eDate)), api);
		for (DataRow row : dataTable.get_Rows()) {
			AccountingAssessment assessment = new AccountingAssessment();
			assessment.setCreatedate(StringUtils.removeLastPoint(row.get("CreatedDateTime").toString()));
			assessment.setWorkordernum(StringUtils.getNotNullStr(row.get("WorkOrderNumber")));
			assessment.setCostdeduction(Double.parseDouble(row.get("cost").toString())); // 所扣费用
			assessment.setLongdelay(Double.parseDouble(row.get("TimeOutNumber").toString()));
			assessment.setIsp(StringUtils.getNotNullStr(row.get("ServiceContent")));
			String parentId = row.get("ParentId").toString();
			String organization = "";
			if (!"".equals(parentId)) {
				String oSql = "SELECT o.* FROM OrganizeAndSafeGroupRel os,Organization o WHERE os.safegroup_id='%s' AND os.organize_id=o.RecId";
				DataRow oRow = DBUtils.getDataRow(api, oSql);
				if (oRow != null)
					organization = oRow.get("o_name").toString();
			}
			assessment.setOrganization(organization);
			list.add(assessment);
		}
		return list;
	}

	/**
	 * 保存计费考核报表
	 * @param api
	 * @param index
	 * @param type
	 * @param startTime
	 * @param date
	 */
	public static void saveAccountingAssessmentReport(ISiteviewApi api, String index, String type,
			String startTime, Date date) {
		ElasticSearchIndexer indexer = ElasticSearchIndexerImpl.getiElasticSearchIndexerImpl();
		AccountingAssessmentReport schema = new AccountingAssessmentReport();
		schema.setIndex(index);
		schema.setType(type);
		List<AccountingAssessment> list = getAccounttingAssessments(api, startTime, date);
		for (AccountingAssessment assessment : list) {
			Map<String, Object> mapping = new HashMap<String, Object>();
			mapping.put("organization", assessment.getOrganization());
			mapping.put("createdate", assessment.getCreatedate());
			mapping.put("workordernum", assessment.getWorkordernum());
			mapping.put("longdelay", assessment.getLongdelay());
			mapping.put("costdeduction", assessment.getCostdeduction());
			mapping.put("isp", assessment.getIsp());
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.HOUR_OF_DAY, -2);
			mapping.put("date", calendar.getTime());
			try {
				indexer.index(schema, mapping);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
