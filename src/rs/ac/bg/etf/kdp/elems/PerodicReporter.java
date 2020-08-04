package rs.ac.bg.etf.kdp.elems;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JTextArea;

import rs.ac.bg.etf.kdp.RemoteCommunicationWorkstation;

public class PerodicReporter extends Thread {
	WorkerCounter wc;
	RequestListener rs;
	WorkStarter ws;
	ResultSender res;
	JTextArea jta;
	public PerodicReporter(WorkerCounter wc, RequestListener rs, WorkStarter ws, ResultSender res,JTextArea jta) {
		this.wc = wc;
		this.rs = rs;
		this.ws = ws;
		this.res = res;
		this.jta=jta;
		start();
	}

	ArrayList<RemoteCommunicationWorkstation> Cleaner = new ArrayList<RemoteCommunicationWorkstation>();
	long x = 1;

	public void run() {
		int cnt = 1;
		while (true) {
			try {
				sleep(x);
				rs.checkall();
				int ii = -1;
				// System.out.println("upp " + cnt++);
				synchronized (wc) {
					for (int i = 0; i < wc.GetRemoteCommunicationWorkstation().size(); i++) {
						try {
							ii = i;
							// System.out.println("what
							// "+wc.GetRemoteCommunicationWorkstation().get(i).isup().equals("up"));
							if (!wc.GetRemoteCommunicationWorkstation().get(i).isup().equals("up")) {
								throw new RemoteException();
							}
						} catch (RemoteException e) {
							// System.out.println("except");
							String[] current = null;
							cur: for (int j = 0; j < rs.getDirectories().size(); j++) {
								if (rs.getDirectories().get(j)[9].equals(wc.getWorkstations().get(ii)[1])
										&& rs.getDirectories().get(j)[10].equals(wc.getWorkstations().get(ii)[2])) {
									current = rs.getDirectories().get(j);
									break cur;
								}
							}

							if (current != null) {
								// if() {
								res.addResult(new String[] { current[1], current[3], "Workstation down", "" });
								long yourmilliseconds = System.currentTimeMillis();
								SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss"); 
								Date resultdate = new Date(yourmilliseconds);
								jta.append(sdf.format(resultdate)+" Failed["
										+ current[1] + "]: " + current[3]
										+ "\n");

								try {
									BufferedWriter output = new BufferedWriter(new FileWriter(Server.log, true));
									output.write(sdf.format(resultdate)+" Failed["
											+ current[1] + "]: " + current[3]
													+ "\n");
									output.close();
								} catch (IOException e1) {
									e.printStackTrace();
								}
								synchronized (res) {
									res.notify();
									// }
								}
								for (int j = 0; j < rs.getDirectories().size(); j++) {
									if (rs.getDirectories().get(j)[1].equals(current[1])
											&& rs.getDirectories().get(j)[3].equals(current[3])) {
										try {
											wc.GetRemoteCommunicationWorkstation(rs.getDirectories().get(j)[9],
													rs.getDirectories().get(j)[10])
													.stop(rs.getDirectories().get(j)[1] + " "
															+ rs.getDirectories().get(j)[3] + " "
															+ rs.getDirectories().get(j)[5]);
										} catch (Exception e1) {
											// e1.printStackTrace();
										}
									}
								}
							}
							System.out.println("outed");
							ii = -1;
							Cleaner.add(wc.GetRemoteCommunicationWorkstation().get(i));

						}
					}
					for (int i = 0; i < Cleaner.size(); i++) {
						System.out.println("delete");
						wc.removeStation(Cleaner.get(i));
					}
					Cleaner.clear();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
