package com.siteview.workorder.odata.yft;

import java.util.ArrayList;
import java.util.List;

import com.siteview.workorder.odata.yft.entities.CountObject;

import Siteview.DataRow;
import Siteview.DataTable;

public class CommonUtils {
	/**
	 * 获取统计数据的公共方法
	 * 
	 * @param dataTable
	 *          查询的结果集
	 * @param filed
	 *          要查询的字段名称
	 * @return
	 */
	public static List<CountObject> getCountObjects(DataTable dataTable, String filed) {
		List<CountObject> objects = new ArrayList<CountObject>();
		for (DataRow row : dataTable.get_Rows()) {
			CountObject object = new CountObject();
			if (row.get(filed) != null) {
				object.setName(row.get(filed).toString());
				object.setCount(row.get("c").toString());
				objects.add(object);
			}
		}
		return objects;
	}
}
