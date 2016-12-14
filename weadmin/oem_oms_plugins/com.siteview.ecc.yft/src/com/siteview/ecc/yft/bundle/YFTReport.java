package com.siteview.ecc.yft.bundle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

import org.eclipse.swt.widgets.Composite;
import org.elasticsearch.action.support.ActionFilter.Simple;

import siteview.IAutoTaskExtension;
import Siteview.Api.ISiteviewApi;

import com.siteview.alarm.bean.DepartmentalReports;
import com.siteview.ecc.yft.Servlet.YFTVideo;
import com.siteview.ecc.yft.bean.JsonValueProcessorImpl;
import com.siteview.ecc.yft.bean.LoseVideorecord;
import com.siteview.ecc.yft.bean.ServerDetail;
import com.siteview.ecc.yft.es.StorageReport;
import com.siteview.ecc.yft.es.VideoDetailsDaily;
import com.siteview.ecc.yft.es.VideoOnlineHoursReport;
import com.siteview.ecc.yft.es.VideoReport;
import com.siteview.ecc.yft.report.AccountingAssessmentUtils;
import com.siteview.ecc.yft.report.InitReport;
import com.siteview.ecc.yft.report.PerDayReport;
import com.siteview.ecc.yft.report.PerHourReport;
import com.siteview.ecc.yft.report.PerWeekReport;
import com.siteview.ecc.yft.report.ServerPerDayReport;
import com.siteview.ecc.yft.report.ServerUtils;
import com.siteview.utils.date.DateUtils;
import com.siteview.utils.path.PathUtils;

public class YFTReport implements IAutoTaskExtension {
/*
 * 市级报表统计
 */
	public YFTReport() {
	}
	List<DepartmentalReports> listdeps=new ArrayList<DepartmentalReports>();
	List<ServerDetail> uploadServers = new ArrayList<ServerDetail>();
	SimpleDateFormat simp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Override
	public String run(Map<String, Object> params) throws Exception {
		ISiteviewApi api = (ISiteviewApi) params.get("_CURAPI_");
		Calendar cal=Calendar.getInstance();
		Date savetime=null;
		Date date=cal.getTime();
		int hour=cal.get(Calendar.HOUR_OF_DAY);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int month = cal.get(Calendar.MONTH) + 1;
		int w = cal.get(Calendar.DAY_OF_WEEK);//1=周天 2=周一..
		double d=PerHourReport.getPerHour(api);//摄像机每小时断线率
		VideoOnlineHoursReport.saveVideoDaily(date, d);
		List<DepartmentalReports> listdep=new ArrayList<DepartmentalReports>();
		List<ServerDetail> uploadServer = new ArrayList<ServerDetail>();
		List<ServerDetail> servers = null;
		if(hour==0){
			SimpleDateFormat simp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try{
				PerDayReport.yftVideo(api, cal,simp);//结算好当前天yft设备掉线数据
			}catch(Exception e){}
			
			String context=simp.format(date)+"--daily:";
//			cal.add(Calendar.DAY_OF_MONTH, -1);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 58);
			cal.set(Calendar.MILLISECOND, 1000);
			savetime=cal.getTime();
			savetime.setHours(2);
			date=cal.getTime();
			date.setHours(11);
			List<LoseVideorecord> lossvideo=PerDayReport.LoseVideorecordresult(api);//录像丢失
			if(lossvideo.size()>0)
				VideoDetailsDaily.saveVideoDetails("yftitoss", "videoLossDetailsDaily", lossvideo, date);
			
			lossvideo=PerDayReport.LoseViewrecordresult(api, "ONLINESTATUS");//断线
			if(lossvideo.size()>0)
				VideoDetailsDaily.saveVideoDetails("yftitoss", "videoOfflineDetailsDaily", lossvideo, date);
			
			lossvideo=PerDayReport.LoseViewrecordresult(api, "VIDEOLOSS");//视频丢失
			if(lossvideo.size()>0)
				VideoDetailsDaily.saveVideoDetails("yftitoss", "videoImageLossDetailsDaily", lossvideo, date);
			
			DepartmentalReports dep=PerDayReport.VideoAssessmentDaily(api, 2*3600);//视频考核
			if(dep!=null){
				VideoReport.saveVideoDaily(dep, "yftitoss", "videoDaily",date);
				dep.setType("videoDaily");
				dep.setDate(savetime);
				listdep.add(dep);//需上传数据
			}	
//			DVRALARMRESULT,NVRALARMRESULT,CODERALARMRESULT,IPSANALARMRESULT
			dep=PerDayReport.StorageServlet(api, "DVRALARMRESULT");//DVR日报
			if(dep!=null){
				StorageReport.saveVideoDaily(dep, "yftitoss", "storageDVRDaily",date);
				dep.setType("storageDVRDaily");
				dep.setDate(savetime);
				listdep.add(dep);//需上传数据
			}
			dep=PerDayReport.StorageServlet(api, "NVRALARMRESULT");//NVR日报
			if(dep!=null){
				StorageReport.saveVideoDaily(dep, "yftitoss", "storageNVRDaily",date);
				dep.setType("storageNVRDaily");
				dep.setDate(date);
				dep.setDate(savetime);
				listdep.add(dep);//需上传数据
			}
			dep=PerDayReport.StorageServlet(api, "CODERALARMRESULT");//编码器日报
			if(dep!=null){
				StorageReport.saveVideoDaily(dep, "yftitoss", "storageCODEDaily",date);
				dep.setType("storageCODEDaily");
				dep.setDate(savetime);
				listdep.add(dep);//需上传数据
			}
			
			//非视频类日报
			Calendar dayStartCalendar = (Calendar) cal.clone();
			dayStartCalendar.set(Calendar.HOUR_OF_DAY, 0);
			dayStartCalendar.set(Calendar.MINUTE, 0);
			dayStartCalendar.set(Calendar.SECOND, 0);
			uploadServer.addAll(ServerPerDayReport.setServerDailyData(api,
					DateUtils.formatDefaultDate(dayStartCalendar.getTime()), date));
			Thread.sleep(1000);
			if(w==2){//周报
				context+="--weekly";
				Calendar cal1=Calendar.getInstance();
				cal1.add(Calendar.DAY_OF_MONTH, -7);
				cal1.set(Calendar.MINUTE, 0);
				cal1.set(Calendar.SECOND, 0);
				Date d1=cal.getTime();
				Date d0=cal1.getTime();
				
				lossvideo=PerWeekReport.VideoLoseVideorecord(api, d0.getTime(), d1.getTime(), "videoLossDetailsDaily");//录像丢失
				if(lossvideo.size()>0)
					VideoDetailsDaily.saveVideoDetails("yftitoss", "videoLossDetailsWeekly", lossvideo, d1);
				
				lossvideo=PerWeekReport.VideoLoseVideorecord(api, d0.getTime(),  d1.getTime(), "videoOfflineDetailsDaily");//断线
				if(lossvideo.size()>0)
					VideoDetailsDaily.saveVideoDetails("yftitoss", "videoOfflineDetailsWeekly", lossvideo, d1);
				
				lossvideo=PerWeekReport.VideoLoseVideorecord(api, d0.getTime(),  d1.getTime(),"videoImageLossDetailsDaily");//视频丢失
				if(lossvideo.size()>0)
					VideoDetailsDaily.saveVideoDetails("yftitoss", "videoImageLossDetailsWeekly", lossvideo, d1);
				
				dep=PerWeekReport.VideoAssessmentWeekly(api, d0.getTime(),  d1.getTime(),"videoDaily");
				if(dep!=null){
					VideoReport.saveVideoDaily(dep, "yftitoss", "videoWeekly",d1);
					dep.setType("videoWeekly");
					dep.setDate(savetime);
					listdep.add(dep);//需上传数据
				}
				//DVR周报
				dep=PerWeekReport.StorageAssessmentWeekly(api,d0.getTime(),  d1.getTime(),"storageDVRDaily");
				if(dep!=null){
					StorageReport.saveVideoDaily(dep, "yftitoss", "storageDVRWeekly",d1);
					dep.setType("storageDVRWeekly");
					dep.setDate(savetime);
					listdep.add(dep);//需上传数据
				}
				//NVR周报
				dep=PerWeekReport.StorageAssessmentWeekly(api,d0.getTime(), d1.getTime(),"storageNVRDaily");
				if(dep!=null){
					StorageReport.saveVideoDaily(dep, "yftitoss", "storageNVRWeekly",d1);
					dep.setType("storageNVRWeekly");
					dep.setDate(savetime);
					listdep.add(dep);//需上传数据
				}	
				//编码周报
				dep=PerWeekReport.StorageAssessmentWeekly(api, d0.getTime(), d1.getTime(),"storageCODEDaily");
				if(dep!=null){
					StorageReport.saveVideoDaily(dep, "yftitoss", "storageCODEWeekly",d1);
					dep.setType("storageCODEWeekly");
					dep.setDate(savetime);
					listdep.add(dep);//需上传数据
				}	
				//ipsan周报
//				dep=PerWeekReport.StorageAssessmentWeekly(api,date.getTime(),  date.getTime(),"storageDVRDaily");
				//非视频类服务器周报
				servers = PerWeekReport.getServerDetailsWeekly(api, d0.getTime(), d1.getTime(),
						"serverDaily");
				uploadServer.addAll(servers);
				ServerUtils.setData("yftitoss", "serverWeekly", servers, d1);
			}
			if(day==1){
				context+="--Monthly";
				Calendar cal1=Calendar.getInstance();
				cal1.add(Calendar.MONTH, -1);
				cal1.set(Calendar.HOUR_OF_DAY, 0);
				cal1.set(Calendar.MINUTE, 0);
				cal1.set(Calendar.SECOND, 0);
				Date date1=cal.getTime();
				lossvideo=PerWeekReport.VideoLoseVideorecord(api, cal1.getTime().getTime(),date1.getTime(), "videoLossDetailsWeekly");//录像丢失
				if(lossvideo.size()>0)
					VideoDetailsDaily.saveVideoDetails("yftitoss", "videoLossDetailsMonthly", lossvideo, date1);
				
				lossvideo=PerWeekReport.VideoLoseVideorecord(api,  cal1.getTime().getTime(),date1.getTime(), "videoOfflineDetailsWeekly");//断线
				if(lossvideo.size()>0)
					VideoDetailsDaily.saveVideoDetails("yftitoss", "videoOfflineDetailsMonthly", lossvideo, date1);
				
				lossvideo=PerWeekReport.VideoLoseVideorecord(api,  cal1.getTime().getTime(),date1.getTime(),"videoImageLossDetailsWeekly");//视频丢失
				if(lossvideo.size()>0)
					VideoDetailsDaily.saveVideoDetails("yftitoss", "videoImageLossDetailsMonthly", lossvideo, date1);
				
				dep=PerWeekReport.VideoAssessmentWeekly(api,  cal1.getTime().getTime(),date1.getTime(),"videoDaily");
				if(dep!=null){
					VideoReport.saveVideoDaily(dep, "yftitoss", "videoMonthly",date1);
					dep.setType("videoMonthly");
					dep.setDate(savetime);
					listdep.add(dep);//需上传数据
				}	
				//DVR月报
				dep=PerWeekReport.StorageAssessmentWeekly(api, cal1.getTime().getTime(),date1.getTime(),"storageDVRDaily");
				if(dep!=null){
					StorageReport.saveVideoDaily(dep, "yftitoss", "storageDVRMonthly",date1);
					dep.setType("storageDVRMonthly");
					dep.setDate(savetime);
					listdep.add(dep);//需上传数据
				}	
				//NVR月报
				dep=PerWeekReport.StorageAssessmentWeekly(api, cal1.getTime().getTime(),date1.getTime(),"storageNVRDaily");
				if(dep!=null){
					StorageReport.saveVideoDaily(dep, "yftitoss", "storageNVRMonthly",date1);
					dep.setType("storageNVRMonthly");
					dep.setDate(savetime);
					listdep.add(dep);//需上传数据
				}	
				//编码月报
				dep=PerWeekReport.StorageAssessmentWeekly(api, cal1.getTime().getTime(),date1.getTime(),"storageCODEDaily");
				if(dep!=null){
					StorageReport.saveVideoDaily(dep, "yftitoss", "storageCODEMonthly",date1);
					dep.setType("storageCODEMonthly");
					dep.setDate(savetime);
					listdep.add(dep);//需上传数据
				}	
				servers = PerWeekReport.getServerDetailsWeekly(api, cal1.getTime().getTime(),
						date1.getTime(), "serverDaily");
				uploadServer.addAll(servers);
				// 非视频类月报
				ServerUtils.setData("yftitoss", "serverMonthly", servers, date1);
				
				//计费考核月报
				AccountingAssessmentUtils.saveAccountingAssessmentReport(api, "yftitoss", "accountingassessmentMonthly",
						DateUtils.formatDefaultDate(cal1.getTime()), date1);
				
				if(month==4||month==7||month==10||month==1){
					cal1.add(Calendar.MONTH, -2);
					context+="--Quarterly Bulletin";
					lossvideo=PerWeekReport.VideoLoseVideorecord(api,cal1.getTimeInMillis(),  date1.getTime(), "videoLossDetailsMonthly");//录像丢失
					if(lossvideo.size()>0)
						VideoDetailsDaily.saveVideoDetails("yftitoss", "videoLossDetailsQuarterly", lossvideo, date1);
					
					lossvideo=PerWeekReport.VideoLoseVideorecord(api,  cal1.getTimeInMillis(),  date1.getTime(), "videoOfflineDetailsMonthly");//断线
					if(lossvideo.size()>0)
						VideoDetailsDaily.saveVideoDetails("yftitoss", "videoOfflineDetailsQuarterly", lossvideo, date1);
					
					lossvideo=PerWeekReport.VideoLoseVideorecord(api,  cal1.getTimeInMillis(),  date1.getTime(),"videoImageLossDetailsMonthly");//视频丢失
					if(lossvideo.size()>0)
						VideoDetailsDaily.saveVideoDetails("yftitoss", "videoImageLossDetailsQuarterly", lossvideo, date1);
					
					dep=PerWeekReport.VideoAssessmentWeekly(api, cal1.getTimeInMillis(),  date1.getTime(),"videoMonthly");
					if(dep!=null){
						VideoReport.saveVideoDaily(dep, "yftitoss", "videoQuarterly",date1);
						dep.setType("videoQuarterly");
						dep.setDate(savetime);
						listdep.add(dep);//需上传数据
					}
					//DVR月报
					dep=PerWeekReport.StorageAssessmentWeekly(api,cal1.getTimeInMillis(),  date1.getTime(),"storageDVRMonthly");
					if(dep!=null){
						StorageReport.saveVideoDaily(dep, "yftitoss", "storageDVRQuarterly",date1);
						dep.setType("storageDVRQuarterly");
						listdep.add(dep);//需上传数据
					}
					//NVR月报
					dep=PerWeekReport.StorageAssessmentWeekly(api,cal1.getTimeInMillis(),  date1.getTime(),"storageNVRMonthly");
					if(dep!=null){
						VideoReport.saveVideoDaily(dep, "yftitoss", "storageNVRQuarterly",date1);
						dep.setType("storageNVRQuarterly");
						dep.setDate(savetime);
						listdep.add(dep);//需上传数据
					}
					//编码月报
					dep=PerWeekReport.StorageAssessmentWeekly(api,cal1.getTimeInMillis(),  date1.getTime(),"storageCODEMonthly");
					if(dep!=null){
						StorageReport.saveVideoDaily(dep, "yftitoss", "storageCODEQuarterly",date1);
						dep.setType("storageCODEQuarterly");
						dep.setDate(savetime);
						listdep.add(dep);//需上传数据
					}
					
				//非视频类季报
					servers = PerWeekReport.getServerDetailsWeekly(api, cal1.getTime().getTime(),
							date1.getTime(), "serverMonthly");
					uploadServer.addAll(servers);
					ServerUtils.setData("yftitoss", "serverQuarterly", servers, date1);
					
					if(month==1){
						context+="--annual report";
						Calendar cal2=Calendar.getInstance();
						cal2.add(Calendar.YEAR, -1);
						cal2.set(Calendar.HOUR_OF_DAY, 0);
						cal2.set(Calendar.MINUTE, 0);
						cal2.set(Calendar.SECOND, 0);
						
						lossvideo=PerWeekReport.VideoLoseVideorecord(api,cal2.getTimeInMillis(),  date1.getTime(), "videoLossDetailsQuarterly");//录像丢失
						if(lossvideo.size()>0)
							VideoDetailsDaily.saveVideoDetails("yftitoss", "videoLossDetailsYearly", lossvideo, date1);
						
						lossvideo=PerWeekReport.VideoLoseVideorecord(api,  cal2.getTimeInMillis(),  date1.getTime(), "videoOfflineDetailsQuarterly");//断线
						if(lossvideo.size()>0)
							VideoDetailsDaily.saveVideoDetails("yftitoss", "videoOfflineDetailsYearly", lossvideo, date1);
						
						lossvideo=PerWeekReport.VideoLoseVideorecord(api,  cal2.getTimeInMillis(),  date1.getTime(),"videoImageLossDetailsQuarterly");//视频丢失
						if(lossvideo.size()>0)
							VideoDetailsDaily.saveVideoDetails("yftitoss", "videoImageLossDetailsYearly", lossvideo, date1);
						
						
						dep=PerWeekReport.VideoAssessmentWeekly(api, cal2.getTimeInMillis(),  date1.getTime(),"videoQuarterly");
						if(dep!=null){
							VideoReport.saveVideoDaily(dep, "yftitoss", "videoYearly",date1);
							dep.setType("videoYearly");
							dep.setDate(savetime);
							listdep.add(dep);//需上传数据
						}
						//DVR年报
						dep=PerWeekReport.StorageAssessmentWeekly(api,cal2.getTimeInMillis(),  date1.getTime(),"storageDVRQuarterly");
						if(dep!=null){
							StorageReport.saveVideoDaily(dep, "yftitoss", "storageDVRYearly",date1);
							dep.setType("storageDVRYearly");
							dep.setDate(savetime);
							listdep.add(dep);//需上传数据
						}
						//NVR年报
						dep=PerWeekReport.StorageAssessmentWeekly(api,cal2.getTimeInMillis(),  date1.getTime(),"storageNVRQuarterly");
						if(dep!=null){
							StorageReport.saveVideoDaily(dep, "yftitoss", "storageNVRYearly",date1);
							dep.setType("storageNVRYearly");
							dep.setDate(savetime);
							listdep.add(dep);//需上传数据
						}
						//编码年报
						dep=PerWeekReport.StorageAssessmentWeekly(api,cal2.getTimeInMillis(),  date1.getTime(),"storageCODEQuarterly");
						if(dep!=null){
							StorageReport.saveVideoDaily(dep, "yftitoss", "storageCODEYearly",date1);
							dep.setType("storageCODEYearly");
							dep.setDate(savetime);
							listdep.add(dep);//需上传数据
						}
						
					//非视频类年报
						servers = PerWeekReport.getServerDetailsWeekly(api, cal1.getTime().getTime(), date1.getTime(), "serverQuarterly");
						uploadServer.addAll(servers);
						ServerUtils.setData("yftitoss", "serverYearly", servers, date1);
					}
				}
			}
			if(listdep.size()>0){
				String sb="";
				try{
					sb=sendReport(listdep,"http://"+InitReport.map.get("the_parent_domain")+"/reportTransmission");
				}catch(Exception ex){}
					writeToTxtByFileWriter(sb+"-video-"+listdep.size()+"----"+simp.format(new Date())+"\n");
					if(!sb.equals("good"))
						listdeps.addAll(listdep);
			}
			if(uploadServer.size() > 0&&InitReport.map.get("the_parent_domain")!=null){
				String sb="";
				try{
					sb=sendReport(uploadServer,"http://"+InitReport.map.get("the_parent_domain")+"/serverReportUpload");
				}catch(Exception ex){}
				writeToTxtByFileWriter(sb+"-server-"+uploadServer.size()+"----"+simp.format(new Date())+"\n");
				if(!sb.equals("good"))
					uploadServers.addAll(uploadServer);
			}
		}else{
			if(hour>7){
				listdeps.clear();
				uploadServers.clear();
			}
			if(listdeps.size()>0){
				String sb="";
				try{
					sb=sendReport(listdep,"http://"+InitReport.map.get("the_parent_domain")+"/reportTransmission");
				}catch(Exception ex){}
					writeToTxtByFileWriter(sb+"-video-"+listdep.size()+"----"+simp.format(new Date())+"\n");
					if(sb.equals("good"))
						listdeps.clear();
			}
			if(uploadServers.size()>0){
				String sb="";
				try{
					sb=sendReport(uploadServer,"http://"+InitReport.map.get("the_parent_domain")+"/serverReportUpload");
				}catch(Exception ex){}
					writeToTxtByFileWriter(sb+"-server-"+uploadServer.size()+"----"+simp.format(new Date())+"\n");
					if(sb.equals("good"))
					uploadServers.clear();
			}
		}
		return null;
	}

	@Override
	public boolean hasCustomUI() {
		return false;
	}

	@Override
	public void creatConfigUI(Composite parent, Map<String, String> params) {
	}

	@Override
	public Map<String, String> getConfig() {
		return null;
	}
	
	
	public static String sendReport(List dep, String urlAddress) throws IOException {
		URL url = new URL(urlAddress);
		HttpURLConnection httpurlconnection = (HttpURLConnection) url.openConnection();//
		httpurlconnection.setRequestMethod("POST");
		httpurlconnection.setDoOutput(true);
		httpurlconnection.setRequestProperty("Accept-Language", "utf-8");
		// 设置 HttpURLConnection的字符编码
		httpurlconnection.setRequestProperty("Accept-Charset", "UTF-8");
		httpurlconnection.setRequestProperty("Accept-Encoding", "UTF-8");
		httpurlconnection.setRequestProperty("Content-Type", "text/html");
		httpurlconnection.connect();
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.registerJsonValueProcessor(java.util.Date.class, new JsonValueProcessorImpl());
		JSONArray jsonObject = JSONArray.fromObject(dep, jsonConfig);
		DataOutputStream outStream = new DataOutputStream(httpurlconnection.getOutputStream());
		outStream.write(jsonObject.toString().getBytes("utf-8"));
		outStream.flush();
		InputStream input = httpurlconnection.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input, "utf-8"));
		String line = null;
		StringBuffer sb = new StringBuffer();
		while ((line = reader.readLine()) != null) {
			sb.append(line);
			// System.out.println(line);
		}
		if (reader != null) {
			outStream.close();
			reader.close();
		}
		if (httpurlconnection != null) {
			httpurlconnection.disconnect();
		}
		return sb.toString();
	} 
	
	public static void main(String[] args) {
//		List<DepartmentalReports> list=new ArrayList<DepartmentalReports>();
//		DepartmentalReports dep=new DepartmentalReports();
//		dep.setAlarmcount(11);
//		dep.setAnomalycount(11);
//		dep.setAnomalyrate(0.13);
//		dep.setIntactcount(32);
//		dep.setType("videoDaily");
//		dep.setDate(new Date());
//		list.add(dep);
//		try {
//			sendReport(list,  "http://192.168.9.81:10080/reportTransmission");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		System.out.println(new Date());
	}
	/*
	 * 文件追加日志(报表生成记录)
	 */
	public static void writeToTxtByFileWriter(String content){
		String path = PathUtils.getPath("com.siteview.ecc.yft","config/report.log");
		File file=new File(path);
		if(!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		BufferedWriter bw = null;
		try {
		FileWriter fw = new FileWriter(file, true);
		bw = new BufferedWriter(fw);
		bw.write(content);
		} catch (IOException e) {
		e.printStackTrace();
		}finally{
		try {
		bw.close();
		} catch (IOException e) {
		e.printStackTrace();
		}
		}
	}
}
