package com.siteview.NNM.uijobs;

import java.util.List;

import com.siteview.NNM.Views.NNMTreeView;
import com.siteview.NNM.modles.IpManagermentModle;
import com.siteview.NNM.modles.RootNNM;
import com.siteview.NNM.modles.SubnetModle;
import com.siteview.NNM.modles.SvgManagementModle;
import com.siteview.NNM.modles.SubChartModle;
import com.siteview.NNM.modles.SvgModle;
import com.siteview.NNM.modles.SvgTypeModle;
import com.siteview.NNM.modles.TopoModle;
import com.siteview.nnm.data.DBManage;
import com.siteview.nnm.data.model.TopoChart;

public class RefreshNNMTree {
	public static void refresh(boolean mainscan){
		RootNNM nnmroot=NNMTreeView.getCNFNNMTreeView().nnmroot;
		List<Object> list=nnmroot.getList();
		for(Object ob:list){
			if(ob instanceof SvgManagementModle){
				refreshSvg(((SvgManagementModle) ob).getSvgtype());
			}else if(ob instanceof IpManagermentModle){
				SubnetModle sub=((IpManagermentModle) ob).getSub();
				sub.getList().clear();
				sub.getSub();
				NNMTreeView.getCNFNNMTreeView().getCommonViewer().refresh();
			}else if((ob instanceof TopoModle)  ){
				for(SubChartModle submodel:((TopoModle) ob).getList()){
					 if( !DBManage.subhosts.contains(submodel.getName())){
						 if(mainscan)
						 ((TopoModle) ob).getList().remove(submodel);
					 }
				}
				
			}
		}
	}
	public static void refreshSvg(SvgTypeModle svgm){
		int[] i=svgm.getcount();
		for(SvgModle svg:svgm.getSvgs()){
			int s=svg.getId();
			if(s==0){
				svg.setName("三层交换机");
			}else if(s==1){
				svg.setName("二层交换机");
			}else if(s==2){
				svg.setName("路由");
			}else if(s==3){
				svg.setName("防火墙");
			}else if(s==4){
				svg.setName("服务器");
			}else if(s==5){
				svg.setName("pc终端");
			}else if(s==6){
				svg.setName("其他");
			}
			if(i[s]>0)
				svg.setName(svg.getName()+"("+i[s]+")");
		}
	}
}
