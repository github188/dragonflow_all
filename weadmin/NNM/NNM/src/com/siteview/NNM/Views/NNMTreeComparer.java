package com.siteview.NNM.Views;

import java.text.Collator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import com.siteview.NNM.adapts.NNMInstance;
import com.siteview.NNM.modles.InfoMibModle;
import com.siteview.NNM.modles.IpManagermentModle;
import com.siteview.NNM.modles.RootNNM;
import com.siteview.NNM.modles.SvgManagementModle;
import com.siteview.NNM.modles.SvgModle;
import com.siteview.NNM.modles.SvgTypeModle;
import com.siteview.NNM.modles.TableMibModle;
import com.siteview.NNM.modles.TopoModle;
import com.siteview.mib.model.FileMibModel;
import com.siteview.mib.model.MibModel;
import com.siteview.utils.entities.TreeEquipmentObject;
import com.siteview.utils.entities.TreeGroupObject;
import com.siteview.utils.entities.TreeMonitorObject;
import com.siteview.utils.entities.TreeObject;

public class NNMTreeComparer extends ViewerSorter {
	
	private static final long serialVersionUID = 1L;
	
	private Collator cmp = Collator.getInstance(java.util.Locale.CHINA);

	public NNMTreeComparer() {
		// TODO Auto-generated constructor stub
	}

	public NNMTreeComparer(Collator collator) {
		super(collator);
	}
	@Override
	public int compare(Viewer viewer, Object a, Object b) {
		// TODO Auto-generated method stub
		if(a!=null&&b!=null){
			if((a instanceof TreeObject)&&(b instanceof TreeObject))
			{
				int s1=((TreeObject)a).getSorter();
				int s2=((TreeObject)b).getSorter();
				String sort1=((TreeObject)a).getName();
				String sort2=((TreeObject)b).getName();
				if(a instanceof TreeGroupObject && b instanceof TreeEquipmentObject)
					return 1;
				else if(a instanceof TreeEquipmentObject && b instanceof TreeGroupObject)
					return -1;
				else if(a instanceof TreeMonitorObject && b instanceof TreeMonitorObject){
					return sort1.compareTo(sort2);
				}
				if(s1!=s2)
					return s1-s2;
//				return sor1.compareTo(sor2);
				int result = cmp.compare(sort1,sort2);
				if (a instanceof TreeEquipmentObject && b instanceof TreeEquipmentObject) {
					if (result == 0) { //设备ip相等,比较名称
						return cmp.compare(((TreeEquipmentObject) a).getTitle(), ((TreeEquipmentObject) b).getTitle());
					} else {
						return result;
					}
				} else {
					return result;
				}
				
//		        return 0;  
			}
			int name0=0;
			int name1=0;
			if(a instanceof SvgManagementModle)
				name0=3;
			else if(a instanceof IpManagermentModle)
				name0=4;
			else if(a instanceof TopoModle)
				name0=2;
			else if(a instanceof SvgModle)
				name0=((SvgModle) a).getId()+6;
			else if(a instanceof NNMInstance)
				name0=0;
			else if(a instanceof RootNNM)
				name0=1;
			else if(a instanceof NNMInstance)
				name0=0;
			else if(a instanceof SvgTypeModle)
				name0=5;
			else if(a instanceof MibModel)
				name0=6;
			else if(a instanceof FileMibModel){
				String name=((FileMibModel) a).getEndnum();
//				name=name.substring(name.indexOf("(")+1, name.indexOf(")"));
				name0=Integer.parseInt(name);
			}if(a instanceof TableMibModle){
				String name=((FileMibModel) a).getEndnum();
//				name=name.substring(name.indexOf("(")+1, name.indexOf(")"));
				name0=Integer.parseInt(name);
			}if(a instanceof InfoMibModle){
				String name=((FileMibModel) a).getEndnum();
//				name=name.substring(name.indexOf("(")+1, name.indexOf(")"));
				name0=Integer.parseInt(name);
			}
			
			if(b instanceof SvgManagementModle)
				name1=3;
			else if(b instanceof IpManagermentModle)
				name1=4;
			else if(b instanceof TopoModle)
				name1=2;
			else if(b instanceof SvgModle)
				name1=((SvgModle) b).getId()+6;
			else if(b instanceof NNMInstance)
				name1=0;
			else if(b instanceof RootNNM)
				name1=1;
			else if(b instanceof NNMInstance)
				name1=0;
			else if(b instanceof SvgTypeModle)
				name1=5;
			else if(b instanceof MibModel)
				name1=6;
			else if(b instanceof FileMibModel){
				String name=((FileMibModel) b).getEndnum();
//				name=name.substring(name.indexOf("(")+1, name.indexOf(")"));
				name1=Integer.parseInt(name);
			}else if(b instanceof TableMibModle){
				String name=((FileMibModel) b).getEndnum();
//				name=name.substring(name.indexOf("(")+1, name.indexOf(")"));
				name1=Integer.parseInt(name);
			}
			else if(b instanceof InfoMibModle){
				String name=((FileMibModel) b).getEndnum();
//				name=name.substring(name.indexOf("(")+1, name.indexOf(")"));
				name1=Integer.parseInt(name);
			}
			return name0-name1;
		}
		return 1;
	}
}
