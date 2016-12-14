package com.siteview.NNM.uijobs;
/**
* 给表格的指定列添加排序器，使用Collator类进行比较
* 当单击某一列表头时，就按照该列进行升序或降序来中心排列当前也现实的数据
*/
import java.text.Collator;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
/**
* @author
* @since 2013.3.11
*/
public class TableColumnSortor {

/**
* 给表格的指定列添加数值排序器，使用Collator类比较两个float类型数值的大小
* 
* @param table
*            记录数据的表对象
* @param column
*            表中的某数值列
*/
public static void addNumberSorter(final Table table, final TableColumn column) {
   column.addListener(SWT.Selection, new Listener() {
    boolean isAscend = true; // 按照升序排序
    public void handleEvent(Event e) {
     int columnIndex = getColumnIndex(table, column);
     TableItem[] items = table.getItems();
     //使用冒泡法进行排序
     for (int i = 1; i < items.length; i++) {
      String strvalue2 = items[i].getText(columnIndex);
      if(strvalue2.equalsIgnoreCase("")){ 
       //当遇到表格中的空项目时，就停止往下检索排序项目
       break;
      }
      for (int j = 0; j < i; j++) {
       String strvalue1 = items[j].getText(columnIndex);
       //将字符串类型数据转化为float类型
       float numbervalue1=Float.valueOf(strvalue1);
       float numbervalue2=Float.valueOf(strvalue2);
      
       boolean isLessThan =false;
       if(numbervalue2<numbervalue1){
        isLessThan =true;
       }     
       if ((isAscend && isLessThan)
         || (!isAscend && !isLessThan)) {
        String[] values = getTableItemText(table, items[i]);
        Image[] image=getTableItemImage(table, items[i]);
        Object obj = items[i].getData();
        Color color=items[i].getBackground();
        items[i].dispose();
        TableItem item = new TableItem(table, SWT.NONE, j);
        item.setText(values);
        item.setImage(image);
        item.setData(obj);
        item.setBackground(color);
        items = table.getItems();
        break;
       }
      }
     }
     table.setSortColumn(column);
     table.setSortDirection((isAscend ? SWT.UP : SWT.DOWN));
     isAscend = !isAscend;
    }
   });
}
/**
* 给表格的指定列添加字符串排序器（其值按照字符串来比较大小），使用Collator类按字典次序比较两个字符串的大小
* 
* @param table
*            记录数据的表对象
* @param column
*            表中的某字符串列
*/
public static void addStringSorter(final Table table, final TableColumn column) {
   column.addListener(SWT.Selection, new Listener() {
    boolean isAscend = true; // 按照升序排序
    Collator comparator = Collator.getInstance(Locale.getDefault());
    public void handleEvent(Event e) {
     int columnIndex = getColumnIndex(table, column);
     TableItem[] items = table.getItems();
    //使用冒泡法进行排序
     for (int i = 1; i < items.length; i++) {
      String str2value = items[i].getText(columnIndex);
//      if(str2value.equalsIgnoreCase("")){ 
//       //当遇到表格中的空项目时，就停止往下检索排序项目
//       break;
//      }
      for (int j = 0; j < i; j++) {
       String str1value = items[j].getText(columnIndex);
       boolean isLessThan = comparator.compare(str2value, str1value) < 0;      
       if ((isAscend && isLessThan)
         || (!isAscend && !isLessThan)) {
        String[] values = getTableItemText(table, items[i]);
        Image[] image=getTableItemImage(table, items[i]);
        Color color=items[i].getBackground();
        Object obj = items[i].getData();
        Color fore=items[i].getForeground();
        items[i].dispose();
        TableItem item = new TableItem(table, SWT.NONE, j);
        item.setText(values);
        item.setImage(image);
        item.setForeground(fore);
        item.setData(obj);
        item.setBackground(color);
        items = table.getItems();
        break;
       }
      }
     }
     table.setSortColumn(column);
     table.setSortDirection((isAscend ? SWT.UP : SWT.DOWN));
     isAscend = !isAscend;
    }
   });
}

/**
* 获取某列对应的索引号
* @param table
*            记录数据的表对象
* @param column
*            表中的某列
*/
public static int getColumnIndex(Table table, TableColumn column) {
   TableColumn[] columns = table.getColumns();
   for (int i = 0; i < columns.length; i++) {
    if (columns[i].equals(column))
     return i;
   }
   return -1;
}
/**
* 获取某列对应的文本
* @param table
*            记录数据的表对象
* @param column
*            表中的某列
*/
public static String[] getTableItemText(Table table, TableItem item) {
   int count = table.getColumnCount();
   String[] strs = new String[count];
   for (int i = 0; i < count; i++) {
    strs[i] = item.getText(i);
   }
   return strs;
}

/**
* 获取某列对应的图片
* @param table
*            记录数据的表对象
* @param column
*            表中的某列
*/

public static Image[] getTableItemImage(Table table, TableItem item) {
	   int count = table.getColumnCount();
	   Image[] strs = new Image[count];
	   for (int i = 0; i < count; i++) {
	    strs[i]=item.getImage(i);
	   }
	   return strs;
	}
}
