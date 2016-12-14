package com.siteview.NNM.uijobs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;

import com.google.common.net.InetAddresses;


public class Ipend {
	public static long ipToLong(String strIp) { // ip地址转化为Long型
		long[] ip = new long[4];
		// 先找到IP地址字符串中.的位置
		int position1 = strIp.indexOf(".");
		int position2 = strIp.indexOf(".", position1 + 1);
		int position3 = strIp.indexOf(".", position2 + 1);
		// 将每个.之间的字符串转换成整型
		ip[0] = Long.parseLong(strIp.substring(0, position1));
		ip[1] = Long.parseLong(strIp.substring(position1 + 1, position2));
		ip[2] = Long.parseLong(strIp.substring(position2 + 1, position3));
		ip[3] = Long.parseLong(strIp.substring(position3 + 1));
		return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
	}

	public static String binToip(String strIp, int strlength) { // 二进制转化位IP
		int[] ip = new int[4];

		// 将每个.之间的字符串转换成整型
		ip[0] = Integer.parseInt(strIp.substring(0, strlength - 24), 2);
		ip[1] = Integer.parseInt(
				strIp.substring(strlength - 24, strlength - 16), 2);
		ip[2] = Integer.parseInt(
				strIp.substring(strlength - 16, strlength - 8), 2);
		ip[3] = Integer.parseInt(strIp.substring(strlength - 8, strlength), 2);

		StringBuffer sb = new StringBuffer("");
		sb.append(String.valueOf(ip[0]));
		sb.append(".");
		sb.append(String.valueOf(ip[1]));
		sb.append(".");
		sb.append(String.valueOf(ip[2]));
		sb.append(".");
		sb.append(String.valueOf(ip[3]));
		return sb.toString();
	}

	public static String longToIP(long longIP) { // 将10进制整数形式转换成127.0.0.1形式的IP地址
		StringBuffer sb = new StringBuffer("");
		// 直接右移24位
		sb.append(String.valueOf(longIP >>> 24));
		sb.append(".");
		// 将高8位置0，然后右移16位
		sb.append(String.valueOf((longIP & 0x00FFFFFF) >>> 16));
		sb.append(".");
		sb.append(String.valueOf((longIP & 0x0000FFFF) >>> 8));
		sb.append(".");
		sb.append(String.valueOf(longIP & 0x000000FF));
		return sb.toString();
	}

	public static String endip(String start, String msk) { // 根据起始IP地址和子网掩码计算结束IP地址
		String yi = new String("1");
		long longstartIp = Ipend.ipToLong(start);
		int mskint = Integer.parseInt(msk);
		String startbin = Long.toBinaryString(longstartIp);
		// ip地址转化成二进制形式输出
		int slength = startbin.length();
		if((mskint - 32 + slength)<=0)
			return "";
		String endbin = startbin.substring(0, mskint - 32 + slength);
		// System.out.println(endbin);
		for (int i = 0; i < 32 - mskint; i++) {
			endbin = endbin.concat(yi);
		}
		String endIp = Ipend.binToip(endbin, slength);
		return endIp;
	}
	public static void main(String[] args){
//		System.out.println(endip("172.17.0.0", "16"));
		System.out.println(endip("101.81.77.219", "0"));
	}
	
	public static boolean pingIp(String ip) {
		Runtime runtime = Runtime.getRuntime(); // 获取当前程序的运行进对象
		Process process = null; // 声明处理类对象
		String line = null; // 返回行信息
		InputStream is = null; // 输入流
		InputStreamReader isr = null; // 字节流
		BufferedReader br = null;
		boolean res = false;// 结果
		try {
			process = runtime.exec("ping " + ip+" -w 30"); // PING
			is = process.getInputStream(); // 实例化输入流
			isr = new InputStreamReader(is);// 把输入流转换成字节流
			br = new BufferedReader(isr);// 从字节中读取文本
			while ((line = br.readLine()) != null) {
				if (line.contains("TTL")) {
					res = true;
					break;
				}
			}
			is.close();
			isr.close();
			br.close();
			process.destroy();
			if (res) {
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			System.out.println(e);
			runtime.exit(1);
		}
		return false;
	}
}
