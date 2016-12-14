package com.siteview.emc;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class EMC {
	/**
	 * check state of the storage processors
	 * 
	 * @param opt_user
	 * @param opt_password
	 * @param opt_host
	 * @return
	 */
	public List<String> EnumSP(String opt_user, String opt_password,
			String opt_host) {
		List<String> rr = new ArrayList<String>();
		String command = "NAVISECCLI -User " + opt_user + " -Password "
				+ opt_password + " -Scope 0 -h " + opt_host + " getcrus";
		try {
			String cmdr = Util.execCommand(command);
			String[] arraycmd = cmdr.split("\\r\\n");
			for (String key : arraycmd) {
				if (key.indexOf("State") > -1) {
					if (key.indexOf(":") > -1) {
						String temp = key.split("\\:")[0].trim();
						if (!rr.contains(temp))
							rr.add(temp);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rr;

	}

	/**
	 * check state of the storage processors
	 * 
	 * @param opt_user
	 * @param opt_password
	 * @param opt_host
	 * @return
	 */
	public Map<String, String> SPState(String opt_user, String opt_password,
			String opt_host, String param) {
		Map<String, String> rrr = new HashMap<String, String>();
		rrr.put("Status", "failed");
		String command = "NAVISECCLI -User " + opt_user + " -Password "
				+ opt_password + " -Scope 0 -h " + opt_host + " getcrus";
		try {
			String cmdr = Util.execCommand(command);
			String[] arraycmd = cmdr.split("\\r\\n");
			for (String key : arraycmd) {
				if (key.indexOf(param) > -1) {
					if (key.indexOf(":") > -1) {
						String temp = key.split("\\:")[1].trim();
						if (temp.equals("Present") || temp.equals("Valid"))
							rrr.put("Status", "OK");
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rrr;
	}

	/**
	 * check state of the read and write cache
	 * 
	 * @param opt_user
	 * @param opt_password
	 * @param opt_host
	 * @return
	 */
	public List<String> EnumCache(String opt_user, String opt_password,
			String opt_host) {
		List<String> rr = new ArrayList<String>();
		String command = "NAVISECCLI -User " + opt_user + " -Password "
				+ opt_password + " -Scope 0 -h " + opt_host + " getcache";
		try {
			String cmdr = Util.execCommand(command);
			String[] arraycmd = cmdr.split("\\r\\n");
			for (String key : arraycmd) {
				if (key.indexOf("State") > -1) {
					String temp = key.substring(0, key.indexOf("State") + 4);
					if (!rr.contains(temp))
						rr.add(temp);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rr;

	}

	/**
	 * check state of the read and write cache
	 * 
	 * @param opt_user
	 * @param opt_password
	 * @param opt_host
	 * @param param
	 * @return
	 */
	public Map<String, String> CacheState(String opt_user, String opt_password,
			String opt_host, String param) {
		Map<String, String> rrr = new HashMap<String, String>();
		rrr.put("Status", "failed");
		String command = "NAVISECCLI -User " + opt_user + " -Password "
				+ opt_password + " -Scope 0 -h " + opt_host + " getcache";
		try {
			String cmdr = Util.execCommand(command);
			String[] arraycmd = cmdr.split("\\r\\n");
			for (String key : arraycmd) {
				if (key.indexOf(param) > -1) {
					if (key.indexOf("Enabled") > -1) {
						rrr.put("Status", "OK");
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rrr;
	}

	/**
	 * check state of the different faults
	 * 
	 * @param opt_user
	 * @param opt_password
	 * @param opt_host
	 * @return
	 */
	public Map<String, String> FaultsState(String opt_user,
			String opt_password, String opt_host) {
		Map<String, String> rrr = new HashMap<String, String>();
		rrr.put("Status", "failed");
		String command = "NAVISECCLI -User " + opt_user + " -Password "
				+ opt_password + " -Scope 0 -h " + opt_host + " Faults -list";
		try {
			String cmdr = Util.execCommand(command);
			String[] arraycmd = cmdr.split("\\r\\n");
			for (String key : arraycmd) {
				if (key.indexOf("operating") > -1) {
					if (key.indexOf("normally") > -1) {
						rrr.put("Status", "OK");
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rrr;
	}

	/**
	 * check state of the disks
	 * 
	 * @param opt_user
	 * @param opt_password
	 * @param opt_host
	 * @return
	 */
	public List<String> EnumDisk(String opt_user, String opt_password,
			String opt_host) {
		List<String> rr = new ArrayList<String>();
		String command = "NAVISECCLI -User " + opt_user + " -Password "
				+ opt_password + " -Scope 0 -h " + opt_host + " getdisk -state";
		try {
			String cmdr = Util.execCommand(command);
			String[] arraycmd = cmdr.split("\\r\\n");
			for (String key : arraycmd) {
				if (key.indexOf("Bus") > -1 && key.indexOf("Enclosure") > -1
						&& key.indexOf("Disk") > -1) {
					String temp = key.trim();
					if (!rr.contains(temp))
						rr.add(temp);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rr;

	}

	/**
	 * check state of the disks
	 * 
	 * @param opt_user
	 * @param opt_password
	 * @param opt_host
	 * @param param
	 * @return
	 */
	public Map<String, String> DiskState(String opt_user, String opt_password,
			String opt_host, String param) {
		Map<String, String> rrr = new HashMap<String, String>();
		rrr.put("Status", "failed");
		rrr.put("Capacity", "0");
		rrr.put("UserCapacity", "0");
		rrr.put("HardReadErrors", "0");
		rrr.put("HardWriteErrors", "0");
		rrr.put("SoftReadErrors", "0");
		rrr.put("SoftWriteErrors", "0");
		String command = "NAVISECCLI -User " + opt_user + " -Password "
				+ opt_password + " -Scope 0 -h " + opt_host
				+ " getdisk -state -capacity -usercapacity -hr -hw -sr -sw";
		try {
			String cmdr = Util.execCommand(command);
			String[] arraycmd = cmdr.split("\\r\\n");
			int i = 0;
			int icount = 0;
			for (String key : arraycmd) {
				icount = 0;
				if (key.indexOf(param) > -1) {
					icount = i + 1;
					if (icount < arraycmd.length ) {
						String s1 = arraycmd[icount];
						if (s1.indexOf("State") > -1) {
							if (s1.indexOf("Hot Spare Ready") > -1
									|| s1.indexOf("Binding") > -1
									|| s1.indexOf("Empty") > -1
									|| s1.indexOf("Enabled") > -1
									|| s1.indexOf("Expanding") > -1
									|| s1.indexOf("Unbound") > -1
									|| s1.indexOf("Powering Up") > -1
									|| s1.indexOf("Ready") > -1) {

								rrr.put("Status", "OK");

							} else if (s1.indexOf("Equalizing") > -1
									|| s1.indexOf("Rebuilding") > -1) {
								rrr.put("Status", "Warn");
							}
						}
					}
					icount = i + 2;
					if (icount < arraycmd.length ) {
						String s1 = arraycmd[icount];
						if (s1.indexOf("Capacity") > -1) {
							String temvalue = s1.substring(s1.indexOf(":") + 1)
									.trim();
							rrr.put("Capacity", temvalue);
						}
					}
					icount = i + 3;
					if (icount < arraycmd.length ) {
						String s1 = arraycmd[icount];
						if (s1.indexOf("Capacity") > -1) {
							String temvalue = s1.substring(s1.indexOf(":") + 1)
									.trim();
							rrr.put("Capacity", temvalue);
						}
					}
					icount = i + 4;
					if (icount < arraycmd.length ) {
						String s1 = arraycmd[icount];
						if (s1.indexOf("Hard Read Errors") > -1) {
							String temvalue = s1.substring(s1.indexOf(":") + 1)
									.trim();
							rrr.put("HardReadErrors", temvalue);
						}
					}
					icount = i + 5;
					if (icount < arraycmd.length ) {
						String s1 = arraycmd[icount];
						if (s1.indexOf("Hard Write Errors") > -1) {
							String temvalue = s1.substring(s1.indexOf(":") + 1)
									.trim();
							rrr.put("HardWriteErrors", temvalue);
						}
					}
					icount = i + 6;
					if (icount < arraycmd.length ) {
						String s1 = arraycmd[icount];
						if (s1.indexOf("Soft Read Errors") > -1) {
							String temvalue = s1.substring(s1.indexOf(":") + 1)
									.trim();
							rrr.put("SoftReadErrors", temvalue);
						}
					}
					icount = i + 7;
					if (icount < arraycmd.length ) {
						String s1 = arraycmd[icount];
						if (s1.indexOf("Soft Write Errors") > -1) {
							String temvalue = s1.substring(s1.indexOf(":") + 1)
									.trim();
							rrr.put("SoftWriteErrors", temvalue);
						}
					}
				}
				i = i + 1;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rrr;
	}

	public static void main(String[] args) {
		// List<String> rr = new ArrayList<String>();
		// try {
		// String cmdr = Util.readTxt("D:/google/11.txt");
		// String[] arraycmd = cmdr.split("\\r\\n");
		// for (String key : arraycmd) {
		// if (key.indexOf("State") > -1) {
		// if (key.indexOf(":") > -1) {
		// String temp = key.split("\\:")[0].trim();
		// if (!rr.contains(temp))
		// rr.add(temp);
		// }
		// }
		// }
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// Map<String, String> rrr = new HashMap<String, String>();
		// rrr.put("Status", "failed");
		//
		// try {
		// String cmdr = Util.readTxt("D:/google/11.txt");
		// String[] arraycmd = cmdr.split("\\r\\n");
		// for (String key : arraycmd) {
		// if (key.indexOf("Enclosure SPE Power A0 State") > -1) {
		// if (key.indexOf(":") > -1) {
		// String temp = key.split("\\:")[1].trim();
		// if (temp.equals("Present") || temp.equals("Valid"))
		// rrr.put("Status", "OK");
		// }
		// }
		// }
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// List<String> rr = new ArrayList<String>();
		// try {
		// String cmdr = Util.readTxt("D:/google/11.txt");
		// String[] arraycmd = cmdr.split("\\r\\n");
		// for (String key : arraycmd) {
		// if (key.indexOf("Bus") > -1 && key.indexOf("Enclosure") > -1
		// && key.indexOf("Disk") > -1) {
		// String temp = key.trim();
		// if (!rr.contains(temp))
		// rr.add(temp);
		// }
		// }
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		Map<String, String> rrr = new HashMap<String, String>();
		rrr.put("Status", "failed");
		rrr.put("Capacity", "0");
		rrr.put("UserCapacity", "0");
		rrr.put("HardReadErrors", "0");
		rrr.put("HardWriteErrors", "0");
		rrr.put("SoftReadErrors", "0");
		rrr.put("SoftWriteErrors", "0");

		try {
			String cmdr = Util.readTxt("D:/google/11.txt");
			String[] arraycmd = cmdr.split("\\r\\n");
			int i = 0;
			int icount = 0;
			for (String key : arraycmd) {
				icount = 0;
				if (key.indexOf("Bus 0 Enclosure 0  Disk 10") > -1) {
					icount = i + 1;
					if (icount < arraycmd.length ) {
						String s1 = arraycmd[icount];
						if (s1.indexOf("State") > -1) {
							if (s1.indexOf("Hot Spare Ready") > -1
									|| s1.indexOf("Binding") > -1
									|| s1.indexOf("Empty") > -1
									|| s1.indexOf("Enabled") > -1
									|| s1.indexOf("Expanding") > -1
									|| s1.indexOf("Unbound") > -1
									|| s1.indexOf("Powering Up") > -1
									|| s1.indexOf("Ready") > -1) {

								rrr.put("Status", "OK");

							} else if (s1.indexOf("Equalizing") > -1
									|| s1.indexOf("Rebuilding") > -1) {
								rrr.put("Status", "Warn");
							}
						}
					}
					icount = i + 2;
					if (icount < arraycmd.length ) {
						String s1 = arraycmd[icount];
						if (s1.indexOf("Capacity") > -1) {
							String temvalue = s1.substring(s1.indexOf(":") + 1)
									.trim();
							rrr.put("Capacity", temvalue);
						}
					}
					icount = i + 3;
					if (icount < arraycmd.length ) {
						String s1 = arraycmd[icount];
						if (s1.indexOf("Capacity") > -1) {
							String temvalue = s1.substring(s1.indexOf(":") + 1)
									.trim();
							rrr.put("Capacity", temvalue);
						}
					}
					icount = i + 4;
					if (icount < arraycmd.length ) {
						String s1 = arraycmd[icount];
						if (s1.indexOf("Hard Read Errors") > -1) {
							String temvalue = s1.substring(s1.indexOf(":") + 1)
									.trim();
							rrr.put("HardReadErrors", temvalue);
						}
					}
					icount = i + 5;
					if (icount < arraycmd.length ) {
						String s1 = arraycmd[icount];
						if (s1.indexOf("Hard Write Errors") > -1) {
							String temvalue = s1.substring(s1.indexOf(":") + 1)
									.trim();
							rrr.put("HardWriteErrors", temvalue);
						}
					}
					icount = i + 6;
					if (icount < arraycmd.length ) {
						String s1 = arraycmd[icount];
						if (s1.indexOf("Soft Read Errors") > -1) {
							String temvalue = s1.substring(s1.indexOf(":") + 1)
									.trim();
							rrr.put("SoftReadErrors", temvalue);
						}
					}
					icount = i + 7;
					if (icount < arraycmd.length ) {
						String s1 = arraycmd[icount];
						if (s1.indexOf("Soft Write Errors") > -1) {
							String temvalue = s1.substring(s1.indexOf(":") + 1)
									.trim();
							rrr.put("SoftWriteErrors", temvalue);
						}
					}
				}
				i = i + 1;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
