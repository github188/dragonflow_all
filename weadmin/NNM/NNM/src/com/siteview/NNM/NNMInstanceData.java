package com.siteview.NNM;

import org.eclipse.rap.rwt.RWT;

/**
 * 存放缓存数据
 * @author Administrator
 *
 */
public class NNMInstanceData {
	public static Object getNNMData(String s) {
		// TODO Auto-generated method stub
		try{
			return RWT.getUISession().getAttribute(s);
		}catch (Exception e) {
			return null;
		}                           
	}
	public static void setNNMData(String s,Object value){
		if(RWT.getUISession().getAttribute(s)!=null)
			RWT.getUISession().removeAttribute(s);
		RWT.getUISession().setAttribute(s, value);
	}
}
