package com.siteview.ibmv7000;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.cim.CIMInstance;
import javax.cim.CIMObjectPath;
import javax.cim.CIMProperty;
import javax.cim.UnsignedInteger16;
import javax.security.auth.Subject;
import javax.wbem.CloseableIterator;
import javax.wbem.WBEMException;
import javax.wbem.client.PasswordCredential;
import javax.wbem.client.UserPrincipal;
import javax.wbem.client.WBEMClient;
import javax.wbem.client.WBEMClientFactory;

import com.dragonflow.siteview.san.array.MMapFCPorts;
import com.dragonflow.siteview.san.array.MMapVolumes;
import com.dragonflow.siteview.san.util.CIM_DataTypes;
import com.dragonflow.siteview.san.util.CIM_Qualifiers;
import com.dragonflow.siteview.san.util.PerformanceMetrics;
import com.dragonflow.siteview.san.beans.*;
public class IBMV7000 {
	private CloseableIterator<?> instanceEnum = null;
	private WBEMClient cc = null;
	private String enumerationNameKey = null;
    private Integer computerSystemIDFinal;
	public String status="nodata";
	//public String ftag="v7000";
	public String errorq="";
	CIM_ComputerSystem cs;
	CIM_SoftwareIdentity csi;
	private CIM_DataTypes cim_DT;
	private CIM_Qualifiers cim_Q = new CIM_Qualifiers();
	
    public	MMapFCPorts mapFCP;
	public  MMapVolumes mapV;
	/**
	 * 
	 * @param ipAddress  
	 * @param username
	 * @param password
	 * @param port
	 * @param protocol
	 * @return
	 */
	public Map<String,String> getserverstatus(String ipAddress, String username,
			String password,  String port, String protocol)
	{

        this.status="OFFLINE";
		String unsecureClientNameSpace = protocol + "://" + ipAddress + ":"
				+ port + "/root/ibm";
		Map<String,String> rr=new HashMap<String,String>();
		rr.put("totalManagedSpace", "0");
		rr.put("allocatedManagedSpace", "0");
		
		
		try {
			CIMObjectPath cns = new CIMObjectPath(unsecureClientNameSpace);
			UserPrincipal up = new UserPrincipal(username);
			PasswordCredential pc = new PasswordCredential(password);

			Subject s = new Subject();
			s.getPrincipals().add(up);
			s.getPrivateCredentials().add(pc);
			this.cc = WBEMClientFactory.getClient("CIM-XML");
			Locale[] l = { Locale.ENGLISH,Locale.CHINESE };
			this.cc.initialize(cns, s, l);
			try {
				this.instanceEnum = this.cc.enumerateInstances(
						new CIMObjectPath("CIM_System", "root/ibm"), true,
						false, true, null);
			} catch (WBEMException ce) {
				
				//System.out.println("WBEMException");
				this.instanceEnum = null;
			}
			if (this.instanceEnum == null) {
				this.status="OFFLINE";
				//System.out.println("instanceEnum == null");
				// this.ucs.updateCimom(Cimom.STATUS_OFFLINE, cimomId);
			} else if(instanceEnum != null){
				this.status="ONLINE";
				
				   getV7000("root/ibm");
				   Map<String,String> tt= updateV700();
				   rr.put("totalManagedSpace", tt.get("totalManagedSpace"));
				   rr.put("allocatedManagedSpace", tt.get("allocatedManagedSpace"));
				  
				// this.ucs.updateCimom(Cimom.STATUS_ONLINE, cimomId);
			}

		} catch (WBEMException ce) {
			System.out.println("WBEMException");
			// ucs.updateCimom(Cimom.STATUS_OFFLINE, cimomId);
		}
		//return this.cc;SANRunStatus="+status "SANRunStatus="+this.status+"$"+
		rr.put("SANRunStatus",this.status);
		return rr;
		
	}
	/**
	 * 
	 * @param ipAddress
	 * @param username
	 * @param password
	 * @param port
	 * @param protocol
	 * @return
	 */
	public List<String> EnumFibre(String ipAddress, String username,
			String password,  String port, String protocol){

        this.status="OFFLINE";
     
		String unsecureClientNameSpace = protocol + "://" + ipAddress + ":"
				+ port + "/" + "root/ibm";
		List<String> rr=new ArrayList<String>();
		
		try {
			CIMObjectPath cns = new CIMObjectPath(unsecureClientNameSpace);
			UserPrincipal up = new UserPrincipal(username);
			PasswordCredential pc = new PasswordCredential(password);

			Subject s = new Subject();
			s.getPrincipals().add(up);
			s.getPrivateCredentials().add(pc);
			this.cc = WBEMClientFactory.getClient("CIM-XML");
			Locale[] l = { Locale.ENGLISH,Locale.CHINESE };
			this.cc.initialize(cns, s, l);
			try {
				this.instanceEnum = this.cc.enumerateInstances(
						new CIMObjectPath("CIM_System", "root/ibm"), true,
						false, true, null);
			} catch (WBEMException ce) {
				
				//System.out.println("WBEMException");
				this.instanceEnum = null;
			}
			if (this.instanceEnum == null) {
				this.status="OFFLINE";
				//System.out.println("instanceEnum == null");
				// this.ucs.updateCimom(Cimom.STATUS_OFFLINE, cimomId);
			} else if(instanceEnum != null){
				this.status="ONLINE";
				
				   getV7000("root/ibm");
				   if(mapFCP!=null)
				       	for (CIM_FCPort fcport : mapFCP.FCPortlist) {
				       		String tmp=fcport.getSystemName()+"/"+fcport.getElementName();
				       		if(!rr.contains(tmp))
				       			rr.add(tmp);
							
						}	
				  
				// this.ucs.updateCimom(Cimom.STATUS_ONLINE, cimomId);
			}

		} catch (WBEMException ce) {
			System.out.println("WBEMException");
			// ucs.updateCimom(Cimom.STATUS_OFFLINE, cimomId);
		}
		return rr;
	}
	/**
	 * 
	 * @param ipAddress
	 * @param username
	 * @param password
	 * @param port
	 * @param protocol
	 * @return
	 */
	public List<String> EnumVolume(String ipAddress, String username,
			String password,  String port, String protocol){

        this.status="OFFLINE";
        
		String unsecureClientNameSpace = protocol + "://" + ipAddress + ":"
				+ port + "/" + "root/ibm";
		List<String> rr=new ArrayList<String>();
		
		try {
			CIMObjectPath cns = new CIMObjectPath(unsecureClientNameSpace);
			UserPrincipal up = new UserPrincipal(username);
			PasswordCredential pc = new PasswordCredential(password);

			Subject s = new Subject();
			s.getPrincipals().add(up);
			s.getPrivateCredentials().add(pc);
			this.cc = WBEMClientFactory.getClient("CIM-XML");
			Locale[] l = { Locale.ENGLISH,Locale.CHINESE };
			this.cc.initialize(cns, s, l);
			try {
				this.instanceEnum = this.cc.enumerateInstances(
						new CIMObjectPath("CIM_System", "root/ibm"), true,
						false, true, null);
			} catch (WBEMException ce) {
				
				//System.out.println("WBEMException");
				this.instanceEnum = null;
			}
			if (this.instanceEnum == null) {
				this.status="OFFLINE";
				//System.out.println("instanceEnum == null");
				// this.ucs.updateCimom(Cimom.STATUS_OFFLINE, cimomId);
			} else if(instanceEnum != null){
				this.status="ONLINE";
				
				   getV7000("root/ibm");
				   if(mapFCP!=null)
					   for (CIM_StorageVolume StorageVolume : mapV.v7000StorageVolumes) {
				       		String tmp=StorageVolume.getPoolName()+"/"+StorageVolume.getElementName();
				       		if(!rr.contains(tmp))
				       			rr.add(tmp);
				       		//rr+=tmp+"="+tmp+"$";	
							
						}	
				  
				// this.ucs.updateCimom(Cimom.STATUS_ONLINE, cimomId);
			}

		} catch (WBEMException ce) {
			System.out.println("WBEMException");
			// ucs.updateCimom(Cimom.STATUS_OFFLINE, cimomId);
		}
		return rr;
	}
	/**
	 * 
	 * @param ipAddress
	 * @param username
	 * @param password
	 * @param port
	 * @param protocol
	 * @param tagid
	 * @return
	 */
	public Map<String,String> VolumeStatus(String ipAddress, String username,
			String password,  String port, String protocol,String tagid){

        this.status="OFFLINE";
    
		String unsecureClientNameSpace = protocol + "://" + ipAddress + ":"
				+ port + "/" + "root/ibm";
		Map<String,String> rr=new HashMap<String,String>();
		rr.put("RunStatus", "Stopped");
		rr.put("LunSize", "0");
		
		try {
			CIMObjectPath cns = new CIMObjectPath(unsecureClientNameSpace);
			UserPrincipal up = new UserPrincipal(username);
			PasswordCredential pc = new PasswordCredential(password);

			Subject s = new Subject();
			s.getPrincipals().add(up);
			s.getPrivateCredentials().add(pc);
			this.cc = WBEMClientFactory.getClient("CIM-XML");
			Locale[] l = { Locale.ENGLISH,Locale.CHINESE };
			this.cc.initialize(cns, s, l);
			try {
				this.instanceEnum = this.cc.enumerateInstances(
						new CIMObjectPath("CIM_System", "root/ibm"), true,
						false, true, null);
			} catch (WBEMException ce) {
				
				//System.out.println("WBEMException");
				this.instanceEnum = null;
			}
			if (this.instanceEnum == null) {
				this.status="OFFLINE";
				//System.out.println("instanceEnum == null");
				// this.ucs.updateCimom(Cimom.STATUS_OFFLINE, cimomId);
			} else if(instanceEnum != null){
				this.status="ONLINE";
				   getV7000("root/ibm");
				   double lunsize=0;
				   double temlunsize=0;
				   if(mapFCP!=null)
					   for (CIM_StorageVolume StorageVolume : mapV.v7000StorageVolumes) {
				       		String tmp=StorageVolume.getPoolName()+"/"+StorageVolume.getElementName();
				       		if(tmp.equals(tagid)){
				       			temlunsize=StorageVolume.getBlockSize()*StorageVolume.getNumberOfBlocks();
				       			lunsize=temlunsize/1024/1024/1024/1024;
				    	    	BigDecimal bd=new BigDecimal(lunsize);
				    		    bd=bd.setScale(2, BigDecimal.ROUND_HALF_UP);
				    		    lunsize=bd.doubleValue();
				    		    rr.put("RunStatus", StorageVolume.getOperationalStatus());
				    		    rr.put("LunSize", lunsize+"");
				       			
				       		}
							
						}	
				  
				
				// this.ucs.updateCimom(Cimom.STATUS_ONLINE, cimomId);
			}

		} catch (WBEMException ce) {
			//System.out.println("WBEMException");
			// ucs.updateCimom(Cimom.STATUS_OFFLINE, cimomId);
		}
		//if(rr.isEmpty()) rr="RunStatus=Stopped$LunSize=0$";
		return rr;
	}
	/**
	 * 
	 * @param ipAddress
	 * @param username
	 * @param password
	 * @param port
	 * @param protocol
	 * @param tagid
	 * @return
	 */
	public Map<String,String> Fibre(String ipAddress, String username,
			String password,  String port, String protocol,String tagid){

        this.status="OFFLINE";
		String unsecureClientNameSpace = protocol + "://" + ipAddress + ":"
				+ port + "/" + "root/ibm";
		Map<String,String> rr=new HashMap<String,String>();
		rr.put("RunStatus", "Stopped");
		
		try {
			CIMObjectPath cns = new CIMObjectPath(unsecureClientNameSpace);
			UserPrincipal up = new UserPrincipal(username);
			PasswordCredential pc = new PasswordCredential(password);

			Subject s = new Subject();
			s.getPrincipals().add(up);
			s.getPrivateCredentials().add(pc);
			this.cc = WBEMClientFactory.getClient("CIM-XML");
			Locale[] l = { Locale.ENGLISH,Locale.CHINESE };
			this.cc.initialize(cns, s, l);
			try {
				this.instanceEnum = this.cc.enumerateInstances(
						new CIMObjectPath("CIM_System", "root/ibm"), true,
						false, true, null);
			} catch (WBEMException ce) {
				
				//System.out.println("WBEMException");
				this.instanceEnum = null;
			}
			if (this.instanceEnum == null) {
				this.status="OFFLINE";
				//System.out.println("instanceEnum == null");
				// this.ucs.updateCimom(Cimom.STATUS_OFFLINE, cimomId);
			} else if(instanceEnum != null){
				this.status="ONLINE";
				
				   getV7000("root/ibm");
				   if(mapFCP!=null)
				       	for (CIM_FCPort fcport : mapFCP.FCPortlist) {
				       		String tmp=fcport.getSystemName()+"/"+fcport.getElementName();
				       		if(tmp.equals(tagid)){
				       			rr.put("RunStatus", fcport.getOperationalStatus());
				       			//rr="RunStatus="+fcport.getOperationalStatus()+"$";
				       		}
							
						}	
				// this.ucs.updateCimom(Cimom.STATUS_ONLINE, cimomId);
			}

		} catch (WBEMException ce) {
			//System.out.println("WBEMException");
			// ucs.updateCimom(Cimom.STATUS_OFFLINE, cimomId);
		}
		//if(rr.isEmpty()) rr="RunStatus=Stopped$";
		return rr;
	}
	private Map<String,String>  updateV700()
	{
		double TotalManagedSpace=0;
		double RemainingManagedSpace=0;
		double assignedManagedSpace=0;
		Map<String,String> rr=new HashMap<String,String>();
		if((this.mapV!=null)&&this.mapV.v7000StoragePools!=null)
		{
		for (CIM_StoragePool  pool: this.mapV.v7000StoragePools) {
			TotalManagedSpace=TotalManagedSpace+pool.getTotalManagedSpace();
			RemainingManagedSpace=RemainingManagedSpace+pool.getRemainingManagedSpace();
			
		}
		TotalManagedSpace=TotalManagedSpace/1024/1024/1024/1024;
		BigDecimal bd=new BigDecimal(TotalManagedSpace);
	    bd=bd.setScale(1, BigDecimal.ROUND_HALF_UP);
	    TotalManagedSpace=bd.doubleValue();
		RemainingManagedSpace=RemainingManagedSpace/1024/1024/1024/1024;
		bd=new BigDecimal(RemainingManagedSpace);
	    bd=bd.setScale(1, BigDecimal.ROUND_HALF_UP);
	    RemainingManagedSpace=bd.doubleValue();
	    if(TotalManagedSpace>0)
	    	assignedManagedSpace=TotalManagedSpace-RemainingManagedSpace;
		bd=new BigDecimal(assignedManagedSpace);
	    bd=bd.setScale(1, BigDecimal.ROUND_HALF_UP);
	    assignedManagedSpace=bd.doubleValue();
	    rr.put("totalManagedSpace", TotalManagedSpace+"");
	    rr.put("allocatedManagedSpace", assignedManagedSpace+"");
	   
		//inf.getUpdate();
	   // cmap.put("totalManagedSpace", TotalManagedSpace+"");
	   // cmap.put("remainingManagedSpace", RemainingManagedSpace+"");
	   // cmap.put("AllocatedCapacityRate", assignedrate+"");
		}else
		{
			rr.put("totalManagedSpace","0");
		    rr.put("allocatedManagedSpace","0");
			 	
		}
		return rr;
	}
    
	@SuppressWarnings("unchecked")
	private void getV7000(String namespace)
	{
		if (cc != null) {
			try {
				
				this.cim_DT = new CIM_DataTypes();
				CIMObjectPath cop = new CIMObjectPath("CIM_ComputerSystem", namespace);
			    PerformanceMetrics pm = new PerformanceMetrics();
			    long statInstanceMean = pm.enumerationTime(cc, cop);
			    CloseableIterator computerSystemEnum = cc.enumerateInstances(cop, true, false, false, null);
				while (computerSystemEnum.hasNext()) {      
				this.cs = new CIM_ComputerSystem();
		        Calendar statCalBefore = Calendar.getInstance();
		        long msBeforeforTotalDisc = statCalBefore.getTimeInMillis();
		        this.cs.setInstanceTimeMean(Long.valueOf(statInstanceMean));
		        CIMInstance ci = (CIMInstance)computerSystemEnum.next();
		        int instancePropertySize = ci.getPropertyCount();
		        this.cs.setInstancePropertySize(instancePropertySize);
		        CIMObjectPath instanceCOP = ci.getObjectPath();
		        CIMProperty cp = instanceCOP.getKey("Name");
		        this.enumerationNameKey = cp.getValue().toString();

		       // this.logger.info("Name = " + this.enumerationNameKey);
		        //this.logger.debug(this.CN + " computerSystemName " + this.enumerationNameKey);

		        String enumerationNameKey = null;
		        String enumerationCreationClassNameKey = null;
		        String elementName = "Not Available";
		        String operationalStatus = null;
		        String statusDescriptions = null;
		        String description = null;
		        int enabledState = 0;

		        enumerationNameKey = cp.getValue().toString();
		        cp = instanceCOP.getKey("CreationClassName");
		        enumerationCreationClassNameKey = cp.getValue().toString();
		        //this.logger.info("CreationClassName = " + enumerationCreationClassNameKey);

		        String caption = "";
		        try {
		          caption = this.cim_DT.getCIMInstancePropertyValueString(ci, "Caption");
		        } catch (Exception e) {
		          caption = "Not Available";
		        }
		        //this.logger.info("Caption = " + caption);

		        String description1 = "";
		        try {
		          description1 = this.cim_DT.getCIMInstancePropertyValueString(ci, "Description");
		        } catch (Exception e) {
		          description1 = "Not Available";
		        }
		        //this.logger.info("Description = " + description1);
		        try
		        {
		          elementName = this.cim_DT.getCIMInstancePropertyValueString(ci, "ElementName");
		        } catch (Exception e) {
		          elementName = "Not Available";
		        }
		       // this.logger.info("ElementName = " + elementName);

		        int requestedState = 0;
		        try {
		          requestedState = this.cim_DT.getCIMInstancePropertyUnsignedInt16Value(ci, "RequestedState").intValue();
		        }
		        catch (Exception localException1) {
		        }
		        int enabledDefault = 0;
		        try {
		          enabledDefault = this.cim_DT.getCIMInstancePropertyUnsignedInt16Value(ci, "EnabledDefault").intValue();
		        }
		        catch (Exception localException2) {
		        }
		        String nameFormat = null;
		        try {
		          nameFormat = this.cim_DT.getCIMInstancePropertyValueString(ci, "NameFormat");
		        } catch (Exception e) {
		         // this.logger.error(this.CN, e);
		        }

		        try
		        {
		          UnsignedInteger16[] operationalStatusArray = this.cim_DT.getUint16ArrayPropertyValue(ci, "OperationalStatus");
		          int operationalStatusSize = 0;
		          if (operationalStatusArray != null) {
		            operationalStatusSize = operationalStatusArray.length;
		          }
		          //this.logger.debug("operationalStatusSize = " + operationalStatusSize);
		          Vector operationalStatusString = new Vector();
		          for (int x = 0; x < operationalStatusSize; ++x) {
		            UnsignedInteger16 opstsint = operationalStatusArray[x];

		            int operationalStatusInt = Integer.parseInt(opstsint.toString());

		            String operationalStatusValue = this.cim_Q.operationalStatus(operationalStatusInt);

		            operationalStatusString.add(operationalStatusValue);
		          }
		          String operationalStatusFinal = this.cim_Q.buildStringFromVector(operationalStatusString, ",");
		          this.cs.setOperationalStatus(operationalStatusFinal);
		        } catch (Exception e) {
		          //this.logger.error("OperationalStatus", e);
		          this.cs.setOperationalStatus("Unknown");
		        }

		        String statusDescriptionsFinal = null;
		        try
		        {
		          String[] statusDescriptionsArray = this.cim_DT.getStringArrayPropertyValue(ci, "StatusDescriptions");
		          int statusDescriptionsSize = 0;
		          if (statusDescriptionsArray != null) {
		            statusDescriptionsSize = statusDescriptionsArray.length;
		          }
		          //this.logger.debug("statusDescriptionsSize = " + statusDescriptionsSize);
		          Vector statusDescriptionsString = new Vector();
		          for (int y = 0; y < statusDescriptionsSize; ++y)
		          {
		            String statusDescriptionsValue = statusDescriptionsArray[y].toString();

		            statusDescriptionsString.add(statusDescriptionsValue);
		          }
		          statusDescriptionsFinal = this.cim_Q.buildStringFromVector(statusDescriptionsString, ",");
		        } catch (Exception e) {
		         // this.logger.error("StatusDescriptions", e);
		        }

		        String otherIdentifyingInfoFinal = null;
		        try
		        {
		          String[] otherIdentifyingInfoArray = this.cim_DT.getStringArrayPropertyValue(ci, "OtherIdentifyingInfo");
		          int otherIdentifyingInfoSize = 0;
		          if (otherIdentifyingInfoArray != null) {
		            otherIdentifyingInfoSize = otherIdentifyingInfoArray.length;
		          }
		         // this.logger.debug("otherIdentifyingInfoSize = " + otherIdentifyingInfoSize);
		          Vector otherIdentifyingInfoString = new Vector();
		          for (int y = 0; y < otherIdentifyingInfoSize; ++y)
		          {
		            String otherIdentifyingInfoValue = otherIdentifyingInfoArray[y].toString();

		            otherIdentifyingInfoString.add(otherIdentifyingInfoValue);
		          }
		          otherIdentifyingInfoFinal = this.cim_Q.buildStringFromVector(otherIdentifyingInfoString, ",");
		          //this.logger.info("OtherIdentifyingInfo = " + otherIdentifyingInfoFinal);
		        } catch (Exception e) {
		          //this.logger.error("OtherIdentifyingInfo", e);
		        }

		        String identifyingDescriptionsFinal = null;
		        try
		        {
		          String[] identifyingDescriptionsArray = this.cim_DT.getStringArrayPropertyValue(ci, "IdentifyingDescriptions");
		          int identifyingDescriptionsSize = 0;
		          if (identifyingDescriptionsArray != null) {
		            identifyingDescriptionsSize = identifyingDescriptionsArray.length;
		          }
		          //this.logger.debug("identifyingDescriptinsSize = " + identifyingDescriptionsSize);
		          Vector identifyingDescriptionsString = new Vector();
		          for (int y = 0; y < identifyingDescriptionsSize; ++y)
		          {
		            String identfyingDescriptionsValue = identifyingDescriptionsArray[y].toString();

		            identifyingDescriptionsString.add(identfyingDescriptionsValue);
		          }
		          identifyingDescriptionsFinal = this.cim_Q.buildStringFromVector(identifyingDescriptionsString, ",");
		          //this.logger.info("IdentifyingDescriptions = " + identifyingDescriptionsFinal);
		        } catch (Exception e) {
		         // this.logger.error("IdentifyingDescriptions", e);
		        }
		        try
		        {
		          enabledState = Integer.parseInt(this.cim_DT.getCIMInstancePropertyUnsignedInt16Value(ci, "enabledState").toString());
		        } catch (Exception e) {
		          //this.logger.warn("enabledState does not exist for CIMOM = " + enumerationNameKey);
		        }

//		        this.logger.debug("description = " + description + 
//		          " operationalStatus = " + operationalStatus + " statusDescriptions = " + statusDescriptions + 
//		          " enabledState = " + enabledState);

		        this.cs.setName(enumerationNameKey);
		        this.cs.setCreationClassName(enumerationCreationClassNameKey);
		        this.cs.setCaption(caption);
		        this.cs.setDescription(description1);
		        this.cs.setRequestedState(requestedState);
		        this.cs.setElementName(elementName);
		        this.cs.setStatusDescriptions(statusDescriptionsFinal);
		        this.cs.setEnabledDefault(enabledDefault);
		        this.cs.setNameFormat(nameFormat);
		        this.cs.setEnabledState(enabledState);

		        Calendar cal = Calendar.getInstance();
		        Date now = new Date();

		        this.cs.setTimeOfCreation(cal);
		        this.cs.setTimeOfCreationString(DateFormat.getDateTimeInstance(3, 3).format(now).toString());
//		        Integer cimom_Id_Integer = new Integer(cimom_Id);
//		        this.cs.setCimomID(cimom_Id_Integer);

		        int dedicatedSize = 0;
		        UnsignedInteger16 dedicatedFill = new UnsignedInteger16("555");
		        UnsignedInteger16[] dedicated = { dedicatedFill };
		        try
		        {
		          dedicated = this.cim_DT.getUint16ArrayPropertyValue(ci, "Dedicated");

		          if (dedicated != null)
		            dedicatedSize = dedicated.length;
		        }
		        catch (Exception e) {
		         // this.logger.error(this.CN, e);
		        }
		        //this.logger.info("dedicatedSize = " + dedicatedSize);

		        int intDedicated1 = 0;

		        int intDedicated2 = 0;

		        int intDedicated3 = 0;

		        int intDedicated4 = 0;

		        int intDedicated5 = 0;
		        this.cs.setDedicated("555");
		        String dedicated1;
		        if (dedicatedSize == 1) {
		          dedicated1 = dedicated[0].toString();
		          intDedicated1 = Integer.parseInt(dedicated1);
		          this.cs.setDedicated(dedicated1);
		         // this.logger.info("DedicateFilter1 = " + intDedicated1);
		        }
		        else
		        {
		          String dedicated2;
		          if (dedicatedSize == 2) {
		            dedicated1 = dedicated[0].toString();
		            intDedicated1 = Integer.parseInt(dedicated1);
		            dedicated2 = dedicated[1].toString();
		            intDedicated2 = Integer.parseInt(dedicated2);
		            this.cs.setDedicated(dedicated1 + "," + dedicated2);
		            //this.logger.info("DedicateFilter1 = " + intDedicated1 + " DedicatedFilter2 = " + intDedicated2);
		          }
		          else
		          {
		            String dedicated3;
		            if (dedicatedSize == 3) {
		              dedicated1 = dedicated[0].toString();
		              intDedicated1 = Integer.parseInt(dedicated1);
		              dedicated2 = dedicated[1].toString();
		              intDedicated2 = Integer.parseInt(dedicated2);
		              dedicated3 = dedicated[2].toString();
		              intDedicated3 = Integer.parseInt(dedicated3);
		              this.cs.setDedicated(dedicated1 + "," + dedicated2 + "," + dedicated3);
		              //this.logger.info("DedicateFilter1 = " + intDedicated1 + " DedicatedFilter2 = " + intDedicated2 + " DedicatedFilter3 = " + intDedicated3);
		            }
		            else
		            {
		              String dedicated4;
		              if (dedicatedSize == 4) {
		                dedicated1 = dedicated[0].toString();
		                intDedicated1 = Integer.parseInt(dedicated1);
		                dedicated2 = dedicated[1].toString();
		                intDedicated2 = Integer.parseInt(dedicated2);
		                dedicated3 = dedicated[2].toString();
		                intDedicated3 = Integer.parseInt(dedicated3);
		                dedicated4 = dedicated[3].toString();
		                intDedicated4 = Integer.parseInt(dedicated4);
		                this.cs.setDedicated(dedicated1 + "," + dedicated2 + "," + dedicated3 + "," + dedicated4);
		                //this.logger.info("DedicateFilter1 = " + intDedicated1 + " DedicatedFilter2 = " + intDedicated2 + " DedicateFilter3 = " + intDedicated3 + " DedicatedFilter4 = " + intDedicated4);
		              }
		              else if (dedicatedSize == 5) {
		                dedicated1 = dedicated[0].toString();
		                intDedicated1 = Integer.parseInt(dedicated1);
		                dedicated2 = dedicated[1].toString();
		                intDedicated2 = Integer.parseInt(dedicated2);
		                dedicated3 = dedicated[2].toString();
		                intDedicated3 = Integer.parseInt(dedicated3);
		                dedicated4 = dedicated[3].toString();
		                intDedicated4 = Integer.parseInt(dedicated4);
		                String dedicated5 = dedicated[4].toString();
		                intDedicated5 = Integer.parseInt(dedicated5);
		                this.cs.setDedicated(dedicated1 + "," + dedicated2 + "," + dedicated3 + "," + dedicated4 + "," + dedicated5);
		                //this.logger.info("DedicateFilter1 = " + intDedicated1 + " DedicatedFilter2 = " + intDedicated2 + " DedicateFilter3 = " + intDedicated3 + " DedicatedFilter4 = " + intDedicated4 + " DedicatedFilter5 = " + intDedicated5); 
		                } } }
		        }
		        //this.logger.info("Dedicated = " + this.cs.getDedicated());
		        //this.logger.info("------------------------------------------COMPUTER SYSTEM END------------------------------------------");

		        if ((!(this.cs.getDedicated().equals("555"))) && 
		          (!(this.cs.getCreationClassName().equals("LSISSI_StorageProcessorSystem"))) && 
		          (!(this.cs.getCreationClassName().equals("HPEVA_StorageProcessorSystem"))) && 
		          (!(this.cs.getCreationClassName().equals("HITACHI_StorageProcessorSystem"))) && 
		          (!(this.cs.getCreationClassName().equals("Brocade_PhysicalComputerSystem"))) && 
		          (!(this.cs.getCreationClassName().equals("CISCO_LogicalComputerSystem"))) && 
		          (!(this.cs.getCreationClassName().equals("SunStorEdge_DSPStorageProcessorSystem"))) && 
		          (!(this.cs.getCreationClassName().equals("OpenWBEM_UnitaryComputerSystem"))) && 
		          (!(this.cs.getCreationClassName().equals("IBMTSSVC_IOGroup"))) && 
		          (!(this.cs.getCreationClassName().equals("HPMSA_ArrayController"))) && ((
		          (this.cs.getDedicated().equals("3,15")) || 
		          (this.cs.getDedicated().equals("15,3")) || 
		          (this.cs.getDedicated().equals("3")) || 
		          (this.cs.getDedicated().equals("0")) || 
		          (this.cs.getDedicated().equals("3,15,16,25")) || 
		          (this.cs.getDedicated().equals("3,15,16,21,25")) || 
		          (this.cs.getDedicated().equals("3,15,25")) || 
		          (this.cs.getDedicated().equals("15")) || 
		          (this.cs.getDedicated().equals("5")) || 
		          (this.cs.getDedicated().equals("3,22")) || 
		          (this.cs.getDedicated().equals("3,15,21")) || 
		          (this.cs.getDedicated().equals("15,21")))))
		        {
		          if ((enumerationNameKey != null)  && (dedicated != null)) {
		          
		        
		          if ((intDedicated1 == 15) && (intDedicated2 == 21))
		            {
		              this.mapFCP = new MMapFCPorts(cc,instanceCOP,this.cs,"v7000");
		              this.mapV = new MMapVolumes(cc, instanceCOP,this.cs,"v7000");
		            } else {
		              //this.logger.info("NOTHING");
		            }
		           // this.logger.info("Finished Discovery For " + this.cs.getCreationClassName());
		          }

		        }

		  
		        }
				
				//session.getTransaction().commit();
				//session.close();
				//session.disconnect();
				//session.flush();
			
				// close session.
				 if (cc != null) 
				       cc.close();
				
				// Call the garbage collection to run
				//System.gc();
				
			} catch (WBEMException ce) {
				//ce.printStackTrace();
				//logger.warn("Unable to login at this time");
			} catch (Exception e) {
				//e.printStackTrace();
				e.getCause();
			} finally {
				//session.close();
			}
        } else {
        	//logger.info("Unable to login at this time");
        }
	
	}
}
