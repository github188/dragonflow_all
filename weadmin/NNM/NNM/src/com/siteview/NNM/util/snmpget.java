package com.siteview.NNM.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Vector;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import com.siteview.topology.model.SnmpPara;

public class snmpget {

	public static String getNextValues(SnmpPara para, String oidStr) {
		String ip = para.getIp().indexOf("/") < 0 ? para.getIp() + "/161"
				: para.getIp();
		ip = "udp:" + ip;
		Address targetAddress = GenericAddress.parse(ip);
		String result = null;
		// setting up target
		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString(para.getCommunity()));
		target.setAddress(targetAddress);
		target.setRetries(para.getRetry());
		target.setTimeout(para.getTimeout());
		target.setVersion(getversion(para.getVersion()));
		// creating PDU
		// PDU pdu = new PDU();
		// pdu.add(new VariableBinding(new OID(oidStr)));
		// pdu.setType(PDU.GETNEXT);
		TransportMapping transport;
		Snmp snmp = null;
		try {
			transport = new DefaultUdpTransportMapping();
			snmp = new Snmp(transport);
			snmp.listen();
			boolean isOut = false;
			OID itemoid = new OID(oidStr);
			int vv = 0;
			int ncout = 0;
			while (!isOut) {
				PDU pdu = new PDU();
				pdu.add(new VariableBinding(itemoid));
				pdu.setType(PDU.GETNEXT);
				ResponseEvent event = snmp.getNext(pdu, target);
				if (event == null) {
					break;
				}
				PDU response = event.getResponse();
				if (response == null) {
					break;
				}
				Vector<? extends VariableBinding> vbs = response
						.getVariableBindings();
				for (VariableBinding vb : vbs) {
					if (!vb.getOid().toString().startsWith(oidStr.toString())) {
						isOut = true;
						break;
					}
					itemoid = vb.getOid();
					String tempv = vb.getVariable().toString();
					if (!tempv.equals("0")) {
						vv += Integer.parseInt(tempv);
						ncout=ncout+1;
					}
				}
			}
			if (vv > 0){
				float v = (float)vv/ncout;
				BigDecimal dd = new BigDecimal(v);
				dd.setScale(0, BigDecimal.ROUND_HALF_DOWN);
				result =  dd.intValue()+ "";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (snmp != null) {
				try {
					snmp.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	/**
	 * get value of one oid
	 * 
	 * @param para
	 * @param oidStr
	 * @return
	 */
	public static String getValue(SnmpPara para, String oidStr) {

		String result = null;
		String ip = para.getIp().indexOf("/") < 0 ? para.getIp() + "/161"
				: para.getIp();
		UdpAddress address = new UdpAddress(ip);
		OID oid = new OID(oidStr);
		if (!oid.isValid()) {
			return result;
		}
		OID itemOid = oid;
		CommunityTarget target = new CommunityTarget();
		target.setVersion(getversion(para.getVersion()));
		target.setAddress(address);
		target.setCommunity(new OctetString(para.getCommunity()));
		target.setRetries(para.getRetry());
		target.setTimeout(para.getTimeout());
		Snmp snmp = null;
		try {
			snmp = new Snmp(new DefaultUdpTransportMapping());
			snmp.listen();
			PDU pdu = new PDU();
			pdu.add(new VariableBinding(itemOid));
			pdu.setType(PDU.GET);
			ResponseEvent event = snmp.send(pdu, target);
			if (event == null) {
				return null;
			}
			PDU response = event.getResponse();
			if (response == null) {
				return null;
			}
			VariableBinding vb = response.get(0);
			return vb.getVariable().toString();

		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			if (snmp != null) {
				try {
					snmp.close();
				} catch (IOException e) {
					e.printStackTrace();

				}
				snmp = null;
			}
		}
		return result;

	}
	/**
	 * 
	 * @param para
	 * @param oidStr
	 * @return
	 */
	public static String getNextValue(SnmpPara para, String oidStr) {
		String ip = para.getIp().indexOf("/") < 0 ? para.getIp() + "/161"
				: para.getIp();
		ip = "udp:" + ip;
		Address targetAddress = GenericAddress.parse(ip);
		String result = null;
		// setting up target
		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString(para.getCommunity()));
		target.setAddress(targetAddress);
		target.setRetries(para.getRetry());
		target.setTimeout(para.getTimeout());
		target.setVersion(getversion(para.getVersion()));
		// creating PDU
		PDU pdu = new PDU();
		pdu.add(new VariableBinding(new OID(oidStr)));
		pdu.setType(PDU.GETNEXT);
		TransportMapping transport;
		Snmp snmp = null;
		try {
			transport = new DefaultUdpTransportMapping();
			snmp = new Snmp(transport);
			snmp.listen();
			ResponseEvent event = snmp.getNext(pdu, target);
			if (event == null) {
				return result;
			}
			PDU response = event.getResponse();
			if (response == null) {
				return result;
			}
			Vector<? extends VariableBinding> vbs = response
					.getVariableBindings();
			for (VariableBinding vb : vbs) {
				if(vb.getOid().toString().startsWith(oidStr))
				result = vb.getVariable().toString();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (snmp != null) {
				try {
					snmp.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}

	private static int getversion(int vv) {
		int v = SnmpConstants.version2c;
		if (vv == 1) {
			v = SnmpConstants.version1;
		} else if (vv == 2) {
			v = SnmpConstants.version2c;
		} else if (vv == 3) {
			v = SnmpConstants.version3;
		}
		return v;
	}

}
