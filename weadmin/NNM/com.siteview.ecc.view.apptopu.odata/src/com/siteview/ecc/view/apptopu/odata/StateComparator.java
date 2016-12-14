package com.siteview.ecc.view.apptopu.odata;

import java.util.Comparator;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;


/**
 *
 *
 *
 */
public class StateComparator implements Comparator<JsonObject> {
	
	private SortType type;

	public StateComparator(SortType type) {
		this.type = type;
	}

	@Override
	public int compare(JsonObject one, JsonObject other) {
		JsonValue value1 = one.get("state");
		JsonValue value2 = other.get("state");
		if(value1!=null&&value2!=null){
			int val1 = value1.asInt();
			int val2 = value2.asInt();
			switch (type) {
			case ASC:
				return val2 - val1;
			case DESC:
				return val1 - val2;
			default:
				return 0;
			}
		}else{
			return 0;
		}
	}
}

enum SortType {
	// 升序
	ASC,
	// 降序
	DESC
}
