package com.siteview.NNM.dialogs.table;

import java.io.File;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;






import com.siteview.NNM.Activator;
import com.siteview.utils.path.PathUtils;

public class ExportAssestInfo {
	public static File createExcel(Table table, String fileName) {
		File file=null;
		try {
			TableColumn[] tableColumns = table.getColumns();
			String fileName1 = PathUtils.getPath("NNM")+ "\\" + fileName + System.currentTimeMillis() + ".xls";
			file=	new File(fileName1);
			WritableWorkbook book = Workbook.createWorkbook(file);// D:\wrokspace_rcp\runtime-core.apploader.product\.metadata\.plugins\ecc0001363162567968.xls
			WritableSheet sheet = book.createSheet(fileName, 0);
			sheet.setRowView(0, 500);
//			sheet.setRowView(1, 500);
//			sheet.mergeCells(0, 0, tableColumns.length-1, 0);
//			WritableFont font1 = new WritableFont(WritableFont.TIMES, 16, WritableFont.BOLD);
//			WritableCellFormat format1 = new WritableCellFormat(font1);
//			format1.setAlignment(jxl.format.Alignment.CENTRE);
//			format1.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
//			
//			Label label = new Label(0, 0, fileName, format1);
//			sheet.addCell(label);

			for (int i = 0; i < tableColumns.length; i++) {
				TableColumn column = table.getColumn(i);
				String columsName = column.getText();
				sheet.mergeCells(i, 0, i, 0);
				WritableFont font = new WritableFont(WritableFont.TIMES, 12, WritableFont.NO_BOLD);
				WritableCellFormat format = new WritableCellFormat(font);
				format.setAlignment(jxl.format.Alignment.CENTRE);
				format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
				
				Label label3 = new Label(i, 0, columsName,format);
				sheet.addCell(label3);
			}
			TableItem[] items = table.getItems();
			for (int k = 0; k < items.length; k++) {
				TableItem tableitem = items[k];
//				if(tableitem.getChecked()){
					for (int i = 0; i < table.getColumns().length; i++) {
						String	value=tableitem.getText(i);
						sheet.mergeCells(i, k+1, i, k+1);
						Label label3 = new Label(i  , k+1, value);
						sheet.addCell(label3);
					}
//				}
			}
			book.write();
			book.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}
	public static File createExcel(Control[] ips ,String fileName,int ls,String[] zy,String name) {
		File file=null;
		try {
			String fileName1 =PathUtils.getPath("NNM")+ "\\" + fileName + System.currentTimeMillis() + ".xls";
			file=	new File(fileName1);
			WritableWorkbook book = Workbook.createWorkbook(file);// D:\wrokspace_rcp\runtime-core.apploader.product\.metadata\.plugins\ecc0001363162567968.xls
			WritableSheet sheet = book.createSheet(fileName, 0);
			sheet.setRowView(0, 500);
//			sheet.setRowView(1, 500);
			sheet.mergeCells(1, 0, 54, 0);
			WritableFont font1 = new WritableFont(WritableFont.TIMES, 16, WritableFont.BOLD);
			WritableCellFormat format1 = new WritableCellFormat(font1);
			format1.setAlignment(jxl.format.Alignment.CENTRE);
			format1.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
			String[] ss={"占用ip","非占用ip"};
			WritableFont font = new WritableFont(WritableFont.TIMES, 12, WritableFont.NO_BOLD);
			WritableCellFormat format = new WritableCellFormat(font);
			format.setAlignment(jxl.format.Alignment.CENTRE);
			format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
			
			for (int i = 0; i < 2; i++) {
				String columsName = ss[i];
				sheet.mergeCells(i+i, 1, i+i+2, 0);
				Label label3 = new Label(i+i, 0, columsName,format);
				sheet.addCell(label3);
				
				Label label30 = new Label(i+i, 1, zy[i],format);
				sheet.addCell(label30);
			}
			
			Label label31 = new Label(0, 2, name,format);
			sheet.mergeCells(2, 0, 1, 16);
			sheet.addCell(label31);
			
			for (int k = 0; k < ips.length; k++) {
					for (int i = 0; i < ls-1; i++) {
						int count=i+k;
						if(count>=ips.length)
							break;
						CLabel cl=(CLabel) ips[count];
						String	value=cl.getText();
						sheet.mergeCells(i+i+1, k+3, i+i+1, k/ls+1);
						Label label3 = new Label(i + i , k/ls+3, value);
						sheet.addCell(label3);
					}
					k=k+ls-1;
			}
			book.write();
			book.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}
	
	public static File createExcel(Table intable,Table outtable,Table inerror,Table outerror,Table inusage,Table outusage){
		File file=null;
		try {
			String fileName1 =PathUtils.getPath("NNM")+ "\\topInterface"  + System.currentTimeMillis() + ".xls";
			file=	new File(fileName1);
			WritableWorkbook book = Workbook.createWorkbook(file);// D:\wrokspace
			WritableSheet outerrorsheet = book.createSheet("发送丢包数", 0);
			outerrorsheet.setRowView(0, 500);
			createshellitem(outerrorsheet, outerror);
			WritableSheet inerrorsheet = book.createSheet("接收丢包数", 0);
			inerrorsheet.setRowView(0, 500);
			createshellitem(inerrorsheet, inerror);
			WritableSheet outusagesheet = book.createSheet("发送带宽比", 0);
			outusagesheet.setRowView(0, 500);
			createshellitem(outusagesheet, outusage);
			WritableSheet inusagesheet = book.createSheet("接收带宽比", 0);
			inusagesheet.setRowView(0, 500);
			createshellitem(inusagesheet, inusage);
			WritableSheet outsheet = book.createSheet("发送流量", 0);
			outsheet.setRowView(0, 500);
			createshellitem(outsheet, outtable);
			WritableSheet sheet = book.createSheet("接收流量", 0);
			sheet.setRowView(0, 500);
			createshellitem(sheet, intable);
			book.write();
			book.close();
		}catch(Exception e){
			
		}
		return file;
	}
	
	public static void createshellitem(WritableSheet sheet,Table intable){
		try{
			TableColumn[] tableColumns = intable.getColumns();
			for (int i = 0; i < tableColumns.length; i++) {
				TableColumn column = intable.getColumn(i);
				String columsName = column.getText();
				WritableFont font = new WritableFont(WritableFont.TIMES, 12, WritableFont.NO_BOLD);
				WritableCellFormat format = new WritableCellFormat(font);
				format.setAlignment(jxl.format.Alignment.CENTRE);
				format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
				
				Label label3 = new Label(i, 0, columsName,format);
				sheet.addCell(label3);
			}
			
			TableItem[] items = intable.getItems();
			for (int k = 0; k < items.length; k++) {
				TableItem tableitem = items[k];
					for (int i = 0; i < intable.getColumns().length; i++) {
						String	value=tableitem.getText(i);
						Label label3 = new Label(i  , k+1, value);
						sheet.addCell(label3);
					}
			}
		}catch(Exception e){
			
		}
	}
}
