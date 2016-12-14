package com.siteview.nnm.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.rap.json.JsonObject;
import org.snmp4j.mp.SnmpConstants;

import com.siteview.nnm.data.model.EdgeFlow;
import com.siteview.nnm.data.model.TopoChart;
import com.siteview.nnm.data.model.entity;
import com.siteview.nnm.data.model.svEdge;
import com.siteview.nnm.data.model.svNode;
import com.siteview.snmpinterface.EquipmentQueueManager;
import com.siteview.snmpinterface.EquipmentQueuer;
import com.siteview.snmpinterface.entities.FlowData;

public class FlowDataManager {

	static byte[] lock = new byte[0];
	/**
	 * {lineid,{}}
	 */
	public static Map<String, EdgeFlow> EdgeFlowMap = new HashMap<String, EdgeFlow>();
	public static Map<String, String> portid2line = new HashMap<String, String>();

	/**
	 * 
	 */
	public static void AddEdgeFlowTasks() {
		synchronized (lock) {
			EdgeFlowMap.clear();
			portid2line.clear();
			for (TopoChart topochart : DBManage.Topos.values()) {
				for (svEdge edg : topochart.getEdges().values()) {
					if (!EdgeFlowMap.containsKey(edg.getLid())) {
						EdgeFlow edgeFlow = new EdgeFlow();
						edgeFlow.leftid = edg.getLsource();
						edgeFlow.rightid = edg.getLtarget();
						edgeFlow.leftport = edg.getSinterface();
						edgeFlow.rightport = edg.getTinterface();
						if (edg.getFlowfrom().equals("left")) {
							edgeFlow.isleft = true;
							edgeFlow.portId = edg.getLsource() + ":"
									+ edg.getSinterface();
						} else {
							edgeFlow.portId = edg.getLtarget() + ":"
									+ edg.getTinterface();
							edgeFlow.isleft = false;
						}
						portid2line.put(edg.getLid(), edgeFlow.portId);
						EdgeFlowMap.put(edg.getLid(), edgeFlow);
					}
				}

			}
		}
	}

	/**
	 * 
	 * @param fs
	 */
	public static void AddEdgeFlowTask(String edgeString, EdgeFlow edgeFlow) {
		synchronized (lock) {
			portid2line.put(edgeString, edgeFlow.portId);
			if (!EdgeFlowMap.containsKey(edgeString))
				EdgeFlowMap.put(edgeString, edgeFlow);
		}
	}

	public static void DeleteEdgeFlowTask(String edgeString) {
		synchronized (lock) {
			if (EdgeFlowMap.containsKey(edgeString)) {
				EdgeFlowMap.remove(edgeString);
				portid2line.remove(edgeString);
			}
		}
	}
	
  private static void writelog(String vv){
//	  if(IoUtils.isave()){
//  		List<String> snmpinters= IoUtils.ReadSnmpExceptionflow();
//  		snmpinters.add(vv);
//  		if(snmpinters.size()<10)
//  		IoUtils.saveSnmpExceptionflow(snmpinters);	
//
// 
//  	}else
//  	{
//  		List<String> snmpinters=new ArrayList<String>();
//  		snmpinters.add(vv);
//  		IoUtils.saveSnmpExceptionflow(snmpinters);	
//  		
//  	}
  }

	public static JsonObject[] buildFlowdata() {
		JsonObject[] flowdata = new JsonObject[EdgeFlowMap.size()];
		JsonObject jo = null;
		int i = 0;
		Map<String, Map<String, FlowData>> flowmap= EquipmentQueuer.map;
		synchronized (lock) {
			for (String lineid : EdgeFlowMap.keySet()) {
				EdgeFlow edgeflow = EdgeFlowMap.get(lineid);
				String devid = edgeflow.leftid;
				String devport = edgeflow.leftport;
				edgeflow.flowRate = 0;
				edgeflow.flowPercent = 0;
				edgeflow.pktsRate = 0;
				edgeflow.broadcastRate = 0;
				edgeflow.avgPktLen = 0;
				edgeflow.portstate=0;
				edgeflow.inflow=0;
				edgeflow.outflow=0;
				if (!edgeflow.isleft) {
					devid = edgeflow.rightid;
					devport = edgeflow.rightport;
				}
				if (EntityManager.allEntity.containsKey(devid)) {
					entity tempdev = EntityManager.allEntity.get(devid);
					if (tempdev instanceof svNode) {
						svNode snmpdev = (svNode) tempdev;
						String ip = snmpdev.getLocalip();
						String comm = snmpdev.getProperys().get("Community");
//						Map<String, FlowData> ports = EquipmentQueuer
//								.getEquipmentData(ip + comm);
						String keyip=ip+comm;
						Map<String, FlowData> ports = flowmap.get(keyip);
						if (ports != null) {
							if (ports.containsKey(devport)) {
								FlowData tempflowdata = ports.get(devport);
								if(tempflowdata.getPortSpeed()>=0){
									float tempvv=tempflowdata.getPortSpeed();
									if(tempvv>1000000000)
										tempvv=10000000000f;
									edgeflow.speed=tempvv/1000/1000;
								}
							    if(tempflowdata.getPortWorkStuatus()>=0){
							    	if(tempflowdata.getPortWorkStuatus()==1){
							    		edgeflow.portstate=1;
							    	}
							    }
								if (tempflowdata.getPortSpeed() >= 1000000000) {
									//
									 double hcsinpeed = tempflowdata.getHcInSpeed();
									 double hcsoutpeed = tempflowdata.getHcOutSpeed();
									 boolean isnook=false;
									 if(Double.isNaN(hcsinpeed) || Double.isInfinite(hcsinpeed)){
										 writelog(hcsinpeed+"");
										 isnook=true;
									 }
									 if(Double.isNaN(hcsoutpeed) || Double.isInfinite(hcsoutpeed)){
										 writelog(hcsoutpeed+"");
										 isnook=true;
									 }
									 if(hcsinpeed>1000*10){
										 writelog(hcsinpeed+"");
										 isnook=true;
									 }
									 if(hcsoutpeed>1000*10){
										 writelog(hcsoutpeed+"");
										 isnook=true;
									 }
									 if(isnook){
										 edgeflow.inflow =0;
										 edgeflow.outflow =0;
										 edgeflow.flowRate=0;
									 }else{
										 if (tempflowdata.getHcInSpeed() >= 0) {
												edgeflow.inflow = tempflowdata
														.getHcInSpeed()*1000;
											}
											if (tempflowdata.getHcOutSpeed() >= 0) {
												edgeflow.outflow = tempflowdata
														.getHcOutSpeed()*1000;
											}
											if (tempflowdata.getHcInSpeed() >= 0) {
												edgeflow.flowRate += tempflowdata
														.getHcInSpeed()*1000;
											}
											if (tempflowdata.getHcOutSpeed() >= 0) {
												edgeflow.flowRate += tempflowdata
														.getHcOutSpeed()*1000;
											}
									 }
									
								} else {
									if (tempflowdata.getInFlow() >= 0) {
										edgeflow.inflow = tempflowdata
												.getInFlow();
									}
									if (tempflowdata.getOutFlow() >= 0) {
										edgeflow.outflow = tempflowdata
												.getOutFlow();
									}
									if (tempflowdata.getInFlow() >= 0) {
										edgeflow.flowRate += tempflowdata
												.getInFlow();
									}
									if (tempflowdata.getOutFlow() >= 0) {
										edgeflow.flowRate += tempflowdata
												.getOutFlow();
									}
								}
								if (tempflowdata.getInBroadcast() >= 0) {
									edgeflow.broadcastRate += tempflowdata
											.getInBroadcast();
								}
								if (tempflowdata.getOutBroadcast() >= 0) {
									edgeflow.broadcastRate += tempflowdata
											.getOutBroadcast();
								}
								if (tempflowdata.getInPkts() >= 0) {
									edgeflow.pktsRate += tempflowdata
											.getInPkts();
								}
								if (tempflowdata.getOutPkts() >= 0) {
									edgeflow.pktsRate += tempflowdata
											.getOutPkts();
								}
								if (tempflowdata.getPortSpeed() > 0) {
									float tempvv=tempflowdata.getPortSpeed();
									if(tempvv>1000000000)
										tempvv=10000000000f;
									edgeflow.flowPercent = (edgeflow.flowRate * 1000 * 100)
											/ (tempvv*2);
								} else {
									edgeflow.flowPercent = 0;
								}
								if (edgeflow.pktsRate > 0) {
									float tempv = edgeflow.flowRate;
									tempv = tempv * 128 / edgeflow.pktsRate;
									if (tempv > 1500)
										tempv = 1500;
									else if (tempv < 1)
										tempv = 1;
									edgeflow.avgPktLen = tempv;
								} else {
									edgeflow.avgPktLen = 0;
								}
								if(tempflowdata.getAlias()!=null && !tempflowdata.getAlias().isEmpty()){
									edgeflow.alias=tempflowdata.getAlias();
								}
								//端口shutdown
								if(tempflowdata.getPortWorkStuatus()==2){
									 edgeflow.flowRate =0;
									 edgeflow.outflow =0;
									 edgeflow.inflow =0;
									 edgeflow.pktsRate=0;
									 edgeflow.broadcastRate =0;
									 edgeflow.flowPercent =0;
									 edgeflow.avgPktLen =0;
									 
								}
							}
						}
					}
				}

				// int j = (int) (Math.random() * 70);
				// if (j == 0)
				// j = 1;
				// edgeflow.flowRate=j;
				jo = new JsonObject().add("lid", lineid)
						.add("flow", edgeflow.flowRate)
						.add("outflow", edgeflow.outflow)
						.add("inflow", edgeflow.inflow)
						.add("speed", edgeflow.speed)
						.add("pkts", edgeflow.pktsRate)
						.add("broadcast", edgeflow.broadcastRate)
						.add("bwusage", edgeflow.flowPercent)
						.add("alias", edgeflow.alias)
						.add("portstate", edgeflow.portstate)
						.add("avgframeLen", edgeflow.avgPktLen);
				flowdata[i] = jo;
				i++;
			}
		}
		return flowdata;
	}
}
