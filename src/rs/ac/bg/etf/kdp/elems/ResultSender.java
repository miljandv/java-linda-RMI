package rs.ac.bg.etf.kdp.elems;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JTextArea;

import rs.ac.bg.etf.kdp.RemoteCommunicationClient;

public class ResultSender extends Thread {
	RequestListener rs;
	static final String finishedFile="C:\\Users\\milja\\eclipse-workspace\\Linda\\log\\finished.txt";
	ArrayList<Object[]> Results = new ArrayList<Object[]>();
	JTextArea jta;
	public ResultSender(RequestListener rs,JTextArea jta) {
		this.rs = rs;
		this.jta=jta;
		start();
	}

	public void run() {
		while (true) {
			try {
				synchronized (this) {
					wait();
				}
				ArrayList<Integer> Successfull = new ArrayList<Integer>();
				for (int i = 0; i < Results.size(); i++) {
					try {
						RemoteCommunicationClient rmc = rs.getClientComFor((String)Results.get(i)[0], Integer.parseInt((String)Results.get(i)[1]));
						if (rmc != null) {
							rmc.ReceiveResult(Results.get(i));
							rs.getFinished().add(Results.get(i));
							Successfull.add(i);
							long yourmilliseconds = System.currentTimeMillis();
							SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss"); 
							Date resultdate = new Date(yourmilliseconds);
							jta.append(sdf.format(resultdate)+" Done["
									+ (String)Results.get(i)[0] + "]: " + Integer.parseInt((String)Results.get(i)[1])
									+ "\n");

							try {
								BufferedWriter output = new BufferedWriter(new FileWriter(Server.log, true));
								output.write(sdf.format(resultdate)+" Done["
										+ (String)Results.get(i)[0] + "]: " + Integer.parseInt((String)Results.get(i)[1])
										+ "\n");
								output.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
							try {
								String[] cc=null;
								for(int id=0;id<rs.getDirectories().size();id++) {
									System.err.println(Results.get(i).length);
									if(rs.getDirectories().get(id)[1].equals((String)Results.get(i)[0]) &&
										Integer.parseInt(rs.getDirectories().get(id)[3])==Integer.parseInt((String)Results.get(i)[1]) 
										/* Integer.parseInt(rs.getDirectories().get(id)[5])==Integer.parseInt((String)Results.get(i)[4]*/) {
										cc = rs.getDirectories().get(id);
									}
								}
								PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(finishedFile, true)));
								for(int id=0;id<7;id++) {
									out.print(cc[id]+"#");
								}
								out.print("\n");
								out.close();
							} catch (Exception e1) {
								//System.out.println("error");
								e1.printStackTrace();
							}
							
						}
					} catch (RemoteException e) {
						throw e;
					}
					for (int j = Successfull.size() - 1; j >= 0; j--) {
						Results.remove(j);
					}
					Successfull.clear();
				}
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}

	}

	public void addResult(Object[] str) {
		Results.add(str);
	}
}
