package com.siteview.NNM.uijobs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.siteview.monitor.tools.Platform;

public class PingUtil {
	private static int os = 1;
	static{
		initOs();
	}
	private static void initOs() {
		String s = System.getProperty("os.name").toUpperCase();
		if (s.startsWith("WINDOWS")) {
			os = 1;
		} else if (s.equals("IRIX"))
			os = 3;
		else if (s.equals("SOLARIS") || s.equals("SUNOS"))
			os = 2;
		else if (s.equals("HP-UX"))
			os = 5;
		else if (s.equals("LINUX"))
			os = 6;
		else if (s.equals("MAC OS") || s.equals("MACOS"))
			os = 4;
		else if (s.equals("MacOSX"))
			os = 7;
	}
	
	public static String pingCommand(String host, int timeout, int j, int size) {
		String s1 = null;
		switch (Platform.getOs()) {
		case 1: // '\001'
			// int l = j * (timeout / 1000 + 1) + 10;
			// s1 = perfexCommand("",false,machienType,password,user) +
			// " -timeout " + l + " -ping -n " + j
			// + " -w " + timeout + " -l " + size + " " + host;
			s1 = "ping -n " + j + " -w " + timeout + " -l " + size + " " + host;
			break;

		case 6: // '\006'
			s1 = "/bin/ping -c " + j + " -w " + timeout / 1000 + " -s " + size + " " + host;
			break;

		case 2: // '\002'
			s1 = "/usr/sbin/ping -sn " + host + " " + size + " " + j;
			break;

		case 5: // '\005'
			s1 = "/usr/sbin/ping " + host + " " + size + " -n " + j;
			break;

		case 3: // '\003'
			s1 = "/usr/etc/ping -c " + j + " -s " + size + " " + host;
			break;
		}
		return s1;
	}
	
	public static boolean ping(String host, int timeout, int j, int size) {
		String command = pingCommand(host, timeout, j, size);
		java.lang.Process process;
		List<String> array = new ArrayList();
		boolean res = false;// 结果
		try {
			process = java.lang.Runtime.getRuntime().exec(command);
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("TTL")||line.contains("ttl")) {
					res = true;
					break;
				}
			}
			br.close();
			process.destroy();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public static void main(String[] args){
		System.out.println(ping("192.168.9.63", 10, 1, 1));
	}
}
