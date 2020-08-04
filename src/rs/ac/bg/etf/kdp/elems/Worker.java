package rs.ac.bg.etf.kdp.elems;

import java.awt.JobAttributes;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.UUID;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class Worker extends Thread {
	private Workstation workstation;
	private JProgressBar jpb;
	private static String jarname;
	private static String last = "last.txt";
	private String lastip;
	private int lastport;
	private int lastcnt;
	private String myKey;
	int flipper = -1;
	Process pr;
	Thread t;
	JLabel jlb;

	public Worker(Workstation wst, JProgressBar jpb, JLabel jlb) {
		this.workstation = wst;
		this.jpb = jpb;
		this.jlb = jlb;

		start();
	}

	public String getKey() {
		return myKey;
	}

	public void run() {
		while (true) {
			while (workstation.getworkcnt() == 0) {
				try {
					workstation.sem.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			synchronized (workstation) {
				if (workstation.workcnt != 0)
					workstation.workcnt--;
			}
			Object[] task = workstation.getNextTask(); //
			try {
				lastip = workstation.ip;
				lastport = Integer.parseInt((String) task[3]);
				lastcnt = Integer.parseInt((String) task[4]);
				myKey = lastip + " " + lastport + " " + lastcnt;
				try {
					String str = "" + lastip + " " + lastport + " " + lastcnt;
					BufferedWriter writer = new BufferedWriter(new FileWriter(last));
					writer.write(str);
					writer.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (task[0].equals("init")) {
					flipper = 0;
					jlb.setText("" + task[5]);
					// LocalLinda.debug("FROM 1 " + task[0]+" "+task[1]+" "+task[2]+" "+
					// task[3]+" "+task[4]+" "+task[5]);
					jpb.setValue(10); // init/reg/ireg,test,rs.getDirectories().get(cnt)[1],Integer.parseInt(rs.getDirectories().get(cnt)[2])
					String cmd = "C:\\Users\\milja\\OneDrive\\Desktop\\WORKSTATION_FILES\\test[" + (workstation.ip)
							+ "-" + (workstation.myport) + "]" + (workstation.getnextbatid()) + ".bat";
					String str = "\"C:\\Program Files\\Java\\jre-10.0.2\\bin\\java.exe\" -Xms256m -Xmx4g -XX:+HeapDumpOnOutOfMemoryError -Djava.security.policy=\"C:\\Users\\milja\\eclipse-workspace\\KDPLab4\\fajl sa polisama\" -cp \"C:\\Users\\milja\\eclipse-workspace\\Linda\\jar\\LocalLinda.jar\";"
							+ "\"" + task[1] + "\"" + " " + task[5] + "\n" + "exit";
					BufferedWriter writer = new BufferedWriter(new FileWriter(cmd));
					writer.write(str);
					writer.close();// -Djava.security.policy=\"C:\\Users\\milja\\eclipse-workspace\\KDPLab4\\fajl
									// sa polisama\"
					jpb.setValue(20);
					Runtime r = Runtime.getRuntime();
					jpb.setValue(30);
					pr = r.exec(cmd);
					// pr.waitFor();
					BufferedReader brStdOut = new BufferedReader(new InputStreamReader(pr.getInputStream()));
					jpb.setValue(50);
					String s;
					jpb.setValue(60);
					String ip = (String) task[2];
					String port = ((String) task[3]);
					ArrayList<String> res = new ArrayList<String>();
					res.add(ip);
					res.add(port);
					jpb.setValue(70);
					System.out.println("WTF");
					int cntt = 0;
						while (cntt<3 && (s = brStdOut.readLine()) != null) {
							System.out.println("push " + s);
							if (!s.equals(""))
								res.add(s);
							cntt++;
						}
					brStdOut.close();
					// pr.waitFor();
					/*
					 * System.out.println("DONE"); for (int i = 0; i < res.size(); i++) {
					 * System.out.println(res.get(i)); }
					 */
					//pr.waitFor();
					pr.waitFor();
					pr.destroy();
					//pr.destroyForcibly();
					jpb.setValue(80);
					res.add("" + lastcnt);
					jpb.setValue(90);
					// LocalLinda.debug1("Result-integral: ");
					/*
					 * for (int i = 0; i < res.size(); i++) { LocalLinda.debug1(res.get(i)); }
					 */
					// LocalLinda.debug1("res size="+res.size()+res.get(0)+" "+res.get(1)+"
					// "+res.get(2)+" "+res.get(3));
					workstation.remotecomm.sendWorkResult(res.toArray());
					jpb.setValue(100);
					jlb.setText("No Job");
					workstation.remotecomm.incfree(workstation.ip, workstation.myport);
				} else if (task[0].equals("noninitreg")) {
					flipper = 1;
					// LocalLinda.debug("wait----------");
					/*
					 * bs:while(true) { if(workstation.remotecomm.waitsem().equals("true"))break bs;
					 * sleep(10); }
					 */
					jlb.setText((String) task[5]);
					String[] taskstr1 = workstation.getNextTaskstr1();
					String[] taskstr2 = workstation.getNextTaskstr2();
					jpb.setValue(10);
					/*
					 * LocalLinda.debug("FROM 2   " + task[0]+" "+task[1]+" "+task[2]+" "+
					 * task[3]+" "+task[4]+" "+task[5]+" "+ task[6]+" "+task[7]+" "+task[8]+" ");
					 */
					try {
						jpb.setValue(20);
						String className = (String) task[5];
						String[] initargs1 = new String[] {};
						if (taskstr1.length != 0)
							initargs1 = taskstr1;
						final Object[] initargs = initargs1;
						String methodName = (String) task[7];
						String[] arguments1 = new String[] {};
						final Object[] arguments = arguments1;
						if (taskstr2.length != 0)
							arguments1 = taskstr2;
						jpb.setValue(30);
						/*
						 * t = new Thread(""+UUID.randomUUID()) { public void run() {
						 * 
						 * try { Class threadClass = Class.forName(className); Class[] parameterTypes =
						 * new Class[initargs.length]; for (int i = 0; i < initargs.length; i++) {
						 * parameterTypes[i] = initargs[i].getClass(); } Constructor[] constructors =
						 * threadClass.getConstructors(); Constructor constructor =
						 * threadClass.getConstructor(parameterTypes); Object runningThread =
						 * constructor.newInstance(initargs); parameterTypes = new
						 * Class[arguments.length]; for (int i = 0; i < arguments.length; i++) {
						 * parameterTypes[i] = arguments[i].getClass(); } Method method =
						 * threadClass.getMethod(methodName, parameterTypes);
						 * method.invoke(runningThread, arguments);
						 * 
						 * } catch (Exception e) { e.printStackTrace(); } } }; t.start(); t.join();
						 */
						String cmd = "C:\\Users\\milja\\OneDrive\\Desktop\\WORKSTATION_FILES\\test[" + (workstation.ip)
								+ "-" + (workstation.myport) + "]" + (workstation.getnextbatid()) + ".bat";
						String str = "\"C:\\Program Files\\Java\\jre-10.0.2\\bin\\java.exe\" -Xms256m -Xmx4g -XX:+HeapDumpOnOutOfMemoryError -Djava.security.policy=\"C:\\Users\\milja\\eclipse-workspace\\KDPLab4\\fajl sa polisama\" -cp \"C:\\Users\\milja\\eclipse-workspace\\Linda\\jar\\LocalLinda.jar\";"
								+ "\"" + task[1] + "\"" + " " + className + "\n" + "exit";
						BufferedWriter writer = new BufferedWriter(new FileWriter(cmd));
						writer.write(str);
						writer.close();
						Runtime r = Runtime.getRuntime();
						jpb.setValue(40);
						pr = r.exec(cmd);
						jpb.setValue(50);
						jpb.setValue(50);
						String s;
						jpb.setValue(60);
						String ip = (String) task[2];
						String port = ((String) task[3]);
						ArrayList<String> res = new ArrayList<String>();
						res.add(ip);
						res.add(port);
						jpb.setValue(70);
						// System.out.println("WTF");
						int cntt = 0;
						pr.getOutputStream().close();
						pr.getErrorStream().close();
						BufferedReader stdInput = new BufferedReader(new InputStreamReader(pr.getInputStream()));
						while (cntt<3 && (s = stdInput.readLine()) != null) {
							System.out.println(s);
							cntt++;
						}
						//stdInput.close();
						//pr.destroyForcibly();
						pr.waitFor();
						pr.destroy();
						jpb.setValue(70);
						workstation.remotecomm.incfree(workstation.ip, workstation.myport);
						jpb.setValue(100);
					} catch (Exception e) {
						e.printStackTrace();
					}
					jlb.setText("No Job");
				} else if (task[0].equals("noninitireg")) {
					flipper = 1;
					// LocalLinda.debug("wait----------");
					/*
					 * while(true) { if(workstation.remotecomm.waitsem())break; else sleep(5); }
					 */
					jlb.setText((String) task[5]);
					jpb.setValue(20);
					// LocalLinda.debug("FROM 3 " + task[0]+" "+task[1]+" "+task[2]+" "+
					// task[3]+" "+task[4]+" "+task[5]+" "+
					// task[6]+" "+task[7]+" "+task[8]+" ");
					String thread = (String) task[5];
					Runnable name = (Runnable) task[6];
					jpb.setValue(40);
					Thread t = new Thread(name, thread);
					t.start();
					t.join();
					jpb.setValue(80);
					workstation.remotecomm.incfree(workstation.ip, workstation.myport);
					jpb.setValue(100);
					jlb.setText("No Job");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static String getjarname() {
		return jarname;
	}

	@SuppressWarnings("removal")
	public void kill() {
		if (flipper == 0) {
			pr.destroy();
		} else {
			t.destroy();
		}
	}
}
