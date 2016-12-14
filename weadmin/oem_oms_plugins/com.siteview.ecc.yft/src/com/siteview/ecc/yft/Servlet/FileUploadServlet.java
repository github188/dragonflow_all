package com.siteview.ecc.yft.Servlet;

import javax.servlet.http.HttpServlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.siteview.release.resource.ResourcePathMgr;

public class FileUploadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private String uploadPath = ResourcePathMgr.RESOURCE_NAME_DIR_PATH;

	private String tempPath = ResourcePathMgr.RESOURCE_TEMP_PATH;
	

	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		res.setCharacterEncoding("UTF-8");
		res.setContentType("text/html; charset=utf-8");
		req.setCharacterEncoding("utf-8");
		
		// 判断是否是多数据段提交格式
		boolean isMultipart = ServletFileUpload.isMultipartContent(req);
		if (isMultipart) {
			DiskFileItemFactory factory = new DiskFileItemFactory();
			// 设置文件的下限
			factory.setSizeThreshold(4096);
			// 设置中转目录
			factory.setRepository(new File(tempPath));

			ServletFileUpload upload = new ServletFileUpload(factory);
			// 设置上传文件的上限
			upload.setSizeMax(1000000 * 20);
			
			StringBuffer fileNames = new StringBuffer();
			try {
				List<FileItem> fileItems = upload.parseRequest(req);
				
				if (fileItems.size() > 0) {
					String datadir = ResourcePathMgr.getDateFormatDirName("yyyyMMdd");
					for (Iterator<FileItem> iter = fileItems.iterator(); iter.hasNext();) {
						FileItem item = (FileItem) iter.next();
						String fName = item.getName();
						System.out.println("yftupload--->" + fName);
						
						if(fName == null || fName.equals("")){
							continue;
						}
						String fileName = fName;
						long size = item.getSize();
						if ((fileName == null || fileName.equals(""))&& size == 0) {
							continue;
						}
						// 截取字符串 如：C:\WINDOWS\Debug\PASSWD.LOG
						fileName = fileName.substring(fileName.lastIndexOf("\\") + 1,fileName.length());
						
						File dir = new File(uploadPath + "/" + datadir);
						if(!dir.exists() || !dir.isDirectory())
						{
							dir.mkdir();
						}
						
						File saveFile = new File(dir,fileName);
						item.write(saveFile);
						
						if(fileNames.length()>0){
							fileNames.append("@");
						}
						fileNames.append("/resource/"+datadir+"/"+fileName);
					}
					if(fileNames.length()>0){
						PrintWriter out = res.getWriter();
						JSONObject obj = new JSONObject();
						obj.put("error", 0);
						obj.put("url", fileNames.toString());
						out.println(obj.toString());
					}
				}

			} 
			catch (Exception e) {
				PrintWriter out = res.getWriter();
				JSONObject obj = new JSONObject();
				obj.put("error", 0);
				obj.put("url", fileNames.toString());
				out.println(obj.toString());
				e.printStackTrace();
			}
		}
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doPost(req, res);
	}
}