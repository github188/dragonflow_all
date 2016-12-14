package com.siteview.leveltop.draw2d.util;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.view.mxGraph;

public class MxGraphUtils {
	
	private static MxGraphUtils instance = new MxGraphUtils();
	
	private MxGraphUtils(){
		
	}
	
	public static MxGraphUtils getInstance(){
		return instance;
	}
	
	/*
	 * 添加节点
	 */
	public mxCell addNode(mxGraph content,String id,int x,int y,int width,int height,String text,String modelStyle){
		return (mxCell) content.insertVertex(content.getDefaultParent(), id, text, x, y, width, height,modelStyle);
	}
	
	/*
	 * 添加边 by 来源 和目标 id
	 */
	public mxCell addEdge(mxGraph content,String id,String sourceId,String descId,String text,String modelStyle){
		mxCell sourceCell = getNodeById(content, sourceId);
		mxCell descCell = getNodeById(content, descId);
		if(sourceCell!=null&&descCell!=null){
			return (mxCell) content.insertEdge(content.getDefaultParent(), id, text, sourceCell, descCell,modelStyle);
		}
		return null;
	}
	
	/*
	 * 添加边 in 组合ID(from + “-” + to)
	 */
	public mxCell addEdge(mxGraph content,String sourceId,String descId,String text,String modelStyle){
		StringBuffer sb = new StringBuffer();
		sb.append(sourceId).append("-").append(descId).toString();
		String customEdgeId = sb.toString();
		return addEdge(content,customEdgeId,sourceId,descId,text,modelStyle);
	}
	
	/*
	 * 查找边 by 组合ID集合
	 */
	public mxCell[] getEdge(mxGraph content,String... id){
		return getNodeById(content,id);
	}
	
	/*
	 * 查找边 by 组合ID
	 */
	public mxCell getEdge(mxGraph content,String id){
		mxCell[] mxcell = getNodeById(content,new String[]{id});
		if(mxcell!=null&&mxcell[0]!=null){
			return mxcell[0];
		}
		return null;
	}
	
	public mxCell setEdge(mxGraph content,String edgeId,String sourceId,String targetId){
		mxCell edgeMxcell = getNodeById(content, edgeId);
		if(edgeMxcell!=null){
			StringBuffer sb = new StringBuffer();
			sb.append(sourceId).append("-").append(targetId).toString();
			String customEdgeId = sb.toString();
			edgeMxcell.setId(customEdgeId);
		}
		return edgeMxcell;
	}
	
	/*
	 * 查找边 by 组合ID(from + “-” + to)
	 */
	public mxCell getEdge(mxGraph content,String sourceId,String descId){
		StringBuffer sb = new StringBuffer();
		sb.append(sourceId).append("-").append(descId).toString();
		String customEdgeId = sb.toString();
		return getEdge(content,customEdgeId);
	}
	
	
	
	/*
	 * 删除边  by ID集合
	 */
	public mxCell[] removeEdge(mxGraph content,String... id){
		return removeNodeById(content,id);
	}
	
	/*
	 * 删除边  by ID
	 */
	public mxCell removeEdge(mxGraph content,String id){
		mxCell[] mxcell = removeNodeById(content,new String[]{id});
		if(mxcell!=null&&mxcell[0]!=null){
			return mxcell[0];
		}
		return null;
	}
	
	/*
	 * 删除边  by 组合ID(from + “-” + to)
	 */
	public mxCell removeEdge(mxGraph content,String sourceId,String descId){
		StringBuffer sb = new StringBuffer();
		sb.append(sourceId).append("-").append(descId).toString();
		String customEdgeId = sb.toString();
		return removeEdge(content,customEdgeId);
	}
	
	/*
	 * 查找节点 by ID集合
	 */
	public mxCell[] getNodeById(mxGraph content,String... id){
		if(id.length>0){
			mxCell[] mxcell = new mxCell[id.length];
			for(int i=0;i<id.length;i++){
				mxcell[i] = (mxCell) ((mxGraphModel)content.getModel()).getCell(id[i]);
			}
			return mxcell;
		}
		return null;
	}
	
	/*
	 * 查找节点 by ID
	 */
	public mxCell getNodeById(mxGraph content,String id){
		mxCell[] mxcell = getNodeById(content,new String[]{id});
		if(mxcell!=null&&mxcell[0]!=null){
			return mxcell[0];
		}
		return null;
	}
	
	/*
	 * 删除节点 by ID集合
	 */
	public mxCell[] removeNodeById(mxGraph content,String... id){
		if(id.length>0){
			mxCell[] mxcell = new mxCell[id.length];
			for(int i=0;i<id.length;i++){
				Object node = getNodeById(content,id[i]);
				if(node!=null){
					mxcell[i] = (mxCell) ((mxGraphModel)content.getModel()).remove(node);
				}
			}
			return mxcell;
		}
		return null;
	}
	
	/*
	 * 删除节点 by ID
	 */
	public mxCell removeNodeById(mxGraph content,String id){
		mxCell[] mxcell = removeNodeById(content,new String[]{id});
		if(mxcell!=null&&mxcell[0]!=null){
			return mxcell[0];
		}
		return null;
	}
}
