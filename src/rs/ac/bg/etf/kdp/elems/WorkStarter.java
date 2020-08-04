package rs.ac.bg.etf.kdp.elems;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JTextArea;

import rs.ac.bg.etf.kdp.RemoteCommunicationWorkstation;

public class WorkStarter extends Thread {
	private JTextArea jta;
	private int cnt;
	private RequestListener rs;
	private WorkerCounter wc;
	File ft;
	boolean failure = false;

	public WorkStarter(JTextArea jta) {
		this.jta = jta;
	}

	public void startW() {
		start();
	}

	/*
	 * private int getNextFree() { for (int i = lastCheck; i < free.size(); i++) {
	 * if(free.get(i)!=0) { lastCheck=i; return i; } } return -1; }
	 */
	public void run() {
		while (true) {
			while (cnt == rs.getDirectories().size()) {
				synchronized (this) {
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			while (cnt != rs.getDirectories().size()) {
				try {
					while (wc.getNumFree() == 0) {
						synchronized (this) {
							try {
								wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
					// System.out.println("free "+wc.getNumFree());
					int i = 0;
					label: for (; i < wc.getWorkstations().size(); i++) {
						if (wc.getFree().get(i) != 0) {
							wc.setNotFreeAtIndex(i);
							try {
								wc.GetRemoteCommunicationWorkstation().get(i).isup();
								if (rs.getDirectories().get(cnt)[6].equals("init")) {
									jta.append(rs.getDirectories().get(cnt)[5] + ") " + rs.getDirectories().get(cnt)[4]
											+ " Scheduled[" + rs.getDirectories().get(cnt)[1] + "]: " +rs.getDirectories().get(cnt)[3]+" "
											+ rs.getDirectories().get(cnt)[0] + " " + rs.getDirectories().get(cnt)[2]
											+ "\n");

									try {
										BufferedWriter output = new BufferedWriter(new FileWriter(Server.log, true));
										output.write(
												rs.getDirectories().get(cnt)[5] + ") " + rs.getDirectories().get(cnt)[4]
														+ " Scheduled[" + rs.getDirectories().get(cnt)[1] + "]: "
																 +rs.getDirectories().get(cnt)[3]+" "
														+ rs.getDirectories().get(cnt)[0] + " "
														+ rs.getDirectories().get(cnt)[2] + "\n");
										output.close();
									} catch (IOException e) {
										e.printStackTrace();
									}
									ft = new File(rs.getDirectories().get(cnt)[0]);
									FileInputStream fis;
									byte[] test = null;
									try {
										fis = new FileInputStream(ft);
										test = new byte[(int) ft.length()];
										fis.read(test, 0, (int) ft.length());
										fis.close();
									} catch (FileNotFoundException e) {
										e.printStackTrace();
									} catch (IOException e) {
										e.printStackTrace();
									}
									String key = rs.getDirectories().get(cnt)[1] + " " + rs.getDirectories().get(cnt)[3]
											+ " " + rs.getDirectories().get(cnt)[5];
									/*
									 * if(!map.containsKey(key)) { map.put(key, new
									 * ArrayList<RemoteCommunicationWorkstation>()); }
									 */
									// map.get(key).add(wc.getrmcs().get(cnt));
									rs.getDirectories().set(cnt, new String[] { rs.getDirectories().get(cnt)[0],
											rs.getDirectories().get(cnt)[1], rs.getDirectories().get(cnt)[2],
											rs.getDirectories().get(cnt)[3], rs.getDirectories().get(cnt)[4],
											rs.getDirectories().get(cnt)[5], rs.getDirectories().get(cnt)[6], "", "",
											wc.getWorkstations().get(i)[1], wc.getWorkstations().get(i)[2] });
									wc.GetRemoteCommunicationWorkstation().get(i).startwork(test,
											rs.getDirectories().get(cnt)[1],
											Integer.parseInt(rs.getDirectories().get(cnt)[3]),
											rs.getDirectories().get(cnt)[2],
											Integer.parseInt(rs.getDirectories().get(cnt)[5]));

									jta.append(rs.getDirectories().get(cnt)[5] + ") " + rs.getDirectories().get(cnt)[4]
											+ " Running[" + rs.getDirectories().get(cnt)[1] + "]: "
													+rs.getDirectories().get(cnt)[3]+" "
											+ rs.getDirectories().get(cnt)[0] + " " + rs.getDirectories().get(cnt)[2]
											+ "\n");

									try {
										BufferedWriter output = new BufferedWriter(new FileWriter(Server.log, true));
										output.write(
												rs.getDirectories().get(cnt)[5] + ") " + rs.getDirectories().get(cnt)[4]
														+ " Running[" + rs.getDirectories().get(cnt)[1] + "]: "
																+rs.getDirectories().get(cnt)[3]+" "
														+ rs.getDirectories().get(cnt)[0] + " "
														+ rs.getDirectories().get(cnt)[2] + "\n");
										output.close();
									} catch (IOException e) {
										e.printStackTrace();
									}
								} else if (rs.getDirectories().get(cnt)[6].equals("noninit-reg")) {
									String ss = "";
									lab: for (int j = 0; j < rs.getDirectories().size(); j++) {
										/*
										 * System.out.println(""+ Integer.parseInt(rs.getDirectories().get(j)[3]) + " "
										 * + Integer.parseInt(rs.getDirectories().get(cnt)[3]) + " " +
										 * rs.getDirectories().get(cnt)[1]+ " " +rs.getDirectories().get(j)[1] + " " +
										 * Integer.parseInt(rs.getDirectories().get(j)[5]) + " " +
										 * Integer.parseInt(rs.getDirectories().get(cnt)[5]) +
										 * rs.getDirectories().get(j)[0] + " "+j+" "+cnt);
										 */
										if ((Integer.parseInt(rs.getDirectories().get(j)[3]) == Integer
												.parseInt(rs.getDirectories().get(cnt)[3]))
												&& rs.getDirectories().get(cnt)[1].equals(rs.getDirectories().get(j)[1])
												&& (Integer.parseInt(rs.getDirectories().get(j)[5]) == Integer
														.parseInt(rs.getDirectories().get(cnt)[5]))) {
											ss = rs.getDirectories().get(cnt)[0];
											// System.out.println("found");
											break lab;
										}
									}
									// if(ss.equals(""))System.out.println("not found");
									FileInputStream fis;
									byte[] test = null;
									try {
										// ft = new File(ss);
										fis = new FileInputStream(ft);
										test = new byte[(int) ft.length()];
										fis.read(test, 0, (int) ft.length());
										fis.close();
									} catch (FileNotFoundException e) {
										e.printStackTrace();
									} catch (IOException e) {
										e.printStackTrace();
									}
									// wc.getsenttasks(i).get(cnt).add(new String[]
									// {rs.getDirectories().get(cnt)[1],rs.getDirectories().get(cnt)[3],rs.getDirectories().get(cnt)[5]});
									rs.getDirectories().set(cnt,
											new String[] { rs.getDirectories().get(cnt)[0],
													rs.getDirectories().get(cnt)[1], rs.getDirectories().get(cnt)[2],
													rs.getDirectories().get(cnt)[3], rs.getDirectories().get(cnt)[4],
													rs.getDirectories().get(cnt)[5], rs.getDirectories().get(cnt)[6],
													rs.getDirectories().get(cnt)[7], rs.getDirectories().get(cnt)[8],
													wc.getWorkstations().get(i)[1], wc.getWorkstations().get(i)[2] });
									wc.GetRemoteCommunicationWorkstation().get(i).startTask(true, test,
											rs.getDirectories().get(cnt)[1],
											Integer.parseInt(rs.getDirectories().get(cnt)[3]),
											rs.getDirectories().get(cnt)[7],
											Integer.parseInt(rs.getDirectories().get(cnt)[5]), rs.getArgs1().get(cnt),
											rs.getDirectories().get(cnt)[8], rs.getArgs2().get(cnt), null);
								} else if (rs.getDirectories().get(cnt)[6].equals("noninit-ireg")) {

								}
							} catch (RemoteException e) {
								throw new RemoteException();
							}
							break label;
						}
					}
					wc.decrementNumFree();
					cnt++;
				} catch (Exception e) {
				}
			}
		}
	}

	public void setRequestListener(RequestListener rs) {
		this.rs = rs;
	}

	public void setWorkerCounter(WorkerCounter wc) {
		this.wc = wc;
	}

}
