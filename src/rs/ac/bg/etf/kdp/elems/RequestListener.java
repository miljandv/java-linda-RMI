package rs.ac.bg.etf.kdp.elems;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JTextArea;

import rs.ac.bg.etf.kdp.LocalLinda;
import rs.ac.bg.etf.kdp.RemoteCommunicationClient;
import rs.ac.bg.etf.kdp.RemoteWait;

public class RequestListener extends Thread {
	JTextArea jta;
	Date date;
	public Integer mtx = 1;
	int counter;
	ResultSender res;
	private ArrayList<String> IDS = new ArrayList<String>();
	private ArrayList<String[]> RequestedTouples = new ArrayList<String[]>();
	private ArrayList<RemoteWait> RemoteWaits = new ArrayList<RemoteWait>();
	private ArrayList<Boolean> ISIN = new ArrayList<Boolean>();
	private ArrayList<String> delayedCnt = new ArrayList<String>();

	private ArrayList<String> Cnt = new ArrayList<String>();
	private ArrayList<String[]> Directories = new ArrayList<String[]>(); // "C:\\Users\\milja\\OneDrive\\Desktop\\SERVER_FILES\\test"+(fileid-1)+".jar",ip,""+port,sdf.format(resultdate),counter++}
	private ArrayList<String[]> rmclipport = new ArrayList<String[]>();
	private ArrayList<String[]> args1 = new ArrayList<String[]>();
	private ArrayList<String[]> args2 = new ArrayList<String[]>();
	private ArrayList<String[]> toupleSpace = new ArrayList<String[]>();
	private ArrayList<Object[]> finished = new ArrayList<Object[]>();
	private int cnt;
	private WorkStarter ws;

	public ArrayList<Object[]> getFinished() {
		return finished;
	}

	public ArrayList<String[]> getRmclipport() {
		return rmclipport;
	}

	RequestListener(JTextArea jta, WorkStarter ws) {
		this.jta = jta;
		this.ws = ws;
		start();
	}

	@Override
	public void run() {
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
		Font prev = jta.getFont();
		jta.setFont(prev.deriveFont(Font.BOLD));
		ArrayList<String> acc = new ArrayList<String>();
		ArrayList<String> fin = new ArrayList<String>();
		BufferedReader reader;
		try {
			reader = new BufferedReader(
					new FileReader("C:\\Users\\milja\\eclipse-workspace\\Linda\\log\\accepted.txt"));
			String line = reader.readLine();
			while (line != null) {
				System.out.println(line);
				acc.add(line);
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedReader reader1;
		try {
			reader1 = new BufferedReader(
					new FileReader("C:\\Users\\milja\\eclipse-workspace\\Linda\\log\\finished.txt"));
			String line1 = reader1.readLine();
			while (line1 != null) {
				System.out.println(line1);
				fin.add(line1);
				line1 = reader1.readLine();
			}
			reader1.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < fin.size(); i++) {
			acc.remove(fin.get(i));
		}
		boolean found1 = false;
		for (int i = 0; i < acc.size(); i++) {
			Directories.add(acc.get(i).split("#"));
			addArgs1(new String[] { "" });
			addArgs2(new String[] { "" });
			// found1=true;
		}
		while (true) {
			if (!found1) {
				try {
					synchronized (this) {
						wait();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				found1 = false;
			}
			while (cnt != Directories.size()) {
				if (Directories.get(cnt)[6].equals("init")) {
					jta.append(Directories.get(cnt)[5] + ") " + Directories.get(cnt)[4] + " Ready["
							+ Directories.get(cnt)[1] + "]: " + Directories.get(cnt)[3] + " " + Directories.get(cnt)[0]
							+ " " + Directories.get(cnt)[2] + "\n");

					try {
						BufferedWriter output = new BufferedWriter(new FileWriter(Server.log, true));
						output.write(Directories.get(cnt)[5] + ") " + Directories.get(cnt)[4] + " Ready["
								+ Directories.get(cnt)[1] + "]: " + Directories.get(cnt)[3] + " "
								+ Directories.get(cnt)[0] + " " + Directories.get(cnt)[2] + "\n");
						output.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					boolean found = false;
					String host = Directories.get(cnt)[1];
					int port = Integer.parseInt(Directories.get(cnt)[3]);
					lab: for (int i = 0; i < rmclipport.size(); i++) {
						if (rmclipport.get(i)[0].equals(Directories.get(cnt)[1]) && Integer
								.parseInt(rmclipport.get(i)[1]) == Integer.parseInt(Directories.get(cnt)[3])) {
							found = true;
							break lab;
						}
					}
					synchronized (res) {
						res.notify();
					}
					if (!found) {
						rmclipport.add(new String[] { host, "" + port });
					}
				} else {
					rmclipport.add(new String[] { "", "" });
				}
				synchronized (ws) {
					ws.notify();
				}
				cnt++;

			}
		}
	}

	public synchronized void addDirectory(String[] strings) {
		Directories.add(strings);
	}

	public synchronized void addArgs2(String[] strings) {
		args2.add(strings);
	}

	public synchronized void addArgs1(String[] strings) {
		args1.add(strings);
	}

	public synchronized ArrayList<String[]> getDirectories() {
		return Directories;
	}

	public synchronized ArrayList<String[]> getArgs1() {
		return args1;
	}

	public synchronized ArrayList<String[]> getArgs2() {
		return args2;
	}

	public synchronized RemoteCommunicationClient getClientComFor(String oip, int oport) {
		for (int i = 0; i < rmclipport.size(); i++) {
			if (((String) rmclipport.get(i)[0]).equals(oip) && (Integer.parseInt(rmclipport.get(i)[1])) == oport) {
				if (System.getSecurityManager() == null) {
					System.setSecurityManager(new SecurityManager());
				}
				String host = (String) rmclipport.get(i)[0];
				int port = Integer.parseInt(rmclipport.get(i)[1]);
				try {
					RemoteCommunicationClient stub;
					Registry r = LocateRegistry.getRegistry(host, port);
					stub = (RemoteCommunicationClient) r.lookup("/remoteclient");
					return stub;
				} catch (RemoteException | NotBoundException e) {
					// e.printStackTrace();
					return null;
				}
			}
		}
		return null;
	}

	public synchronized void setResultSender(ResultSender res) {
		this.res = res;
	}

	public synchronized int getnextcounter() {
		return counter++;
	}

	/*
	 * public synchronized ArrayList<String[]> getToupleSpace() { return
	 * toupleSpace; }
	 */

	public synchronized void addDelayed(String ip, int port, String[] touple, String id, boolean isin, int lastcnt) {
		try {
			checkall();
			RemoteWait stubg = null;
			if (System.getSecurityManager() == null) {
				System.setSecurityManager(new SecurityManager());
			}

			try {

				Registry r = LocateRegistry.getRegistry(ip, port);
				stubg = (RemoteWait) r.lookup("/wait");

			} catch (RemoteException | NotBoundException e) {
				// e.printStackTrace();
			}
			// LocalLinda.debug1("delayed1 - -" + toupleSpace.size());
			/*
			 * for (int i = 0; i < touple.length; i++) { LocalLinda.debug1("t:" + i + ": " +
			 * touple[i]); } for (int i = 0; i < toupleSpace.size(); i++) { for (int j = 0;
			 * j < toupleSpace.get(i).length; j++) { LocalLinda.debug1(i + "-" + j + ": " +
			 * toupleSpace.get(i)[j]); } }
			 */
			// System.err.println(touple[0]);
			String comp = "" + lastcnt;
			for (int i = 0; i < toupleSpace.size(); i++) {
				if (equals(touple, toupleSpace.get(i)) && comp.equals(Cnt.get(i))) {
					// LocalLinda.debug1("delayedfound");
					try {
						LocalLinda.debug1("BINGO " + touple[0] + " " + id);
						String[] ts;
						if (isin) {
							ts = toupleSpace.remove(i);
							Cnt.remove(i);
						} else
							ts = toupleSpace.get(i);
						/*
						 * for (int j = 0; j < ts.length; j++) { System.out.println(ts[j]); }
						 */
						stubg.addTouple(ts, id);
						// System.err.println("gone"+isin);
					} catch (RemoteException e) {
						// e.printStackTrace();
					}
					return;
				}
			}
			// LocalLinda.debug1("delayednf");
			/*
			 * for (int i = 0; i < touple.length; i++) { LocalLinda.debug1("delayednf " + i
			 * System.err.println(touple[0]);
			 * 
			 * + ": " + touple[i]); }
			 */
			RequestedTouples.add(touple);
			delayedCnt.add("" + lastcnt);
			RemoteWaits.add(stubg);
			IDS.add(id);
			ISIN.add(isin);
		} catch (Exception e) {
		}
	}

	public synchronized void addTouple(String[] touple, String id, int lastcnt) throws RemoteException {
		try {
			// toupleSpace.add(touple);
			// LocalLinda.debug1("outed");
			/*
			 * for (int i = 0; i < touple.length; i++) { LocalLinda.debug1("outed " + i +
			 * ": " + touple[i]); }
			 */
			/*
			 * for (int i = 0; i < toupleSpace.size(); i++) { for (int j = 0; j <
			 * toupleSpace.get(i).length; j++) { LocalLinda.debug1(i + "-" + j + ": " +
			 * toupleSpace.get(i)[j]); } }
			 */
			int ii = 0;
			// System.err.println(touple[0]);
			boolean found = false;
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < RequestedTouples.size(); i++) {
				for (int j = 0; j < RequestedTouples.get(i).length; j++) {
					sb.append(RequestedTouples.get(i)[j] + "-");
				}
				sb.append(" ");
			}
			sb.append(" ||| ");
			for (int i = 0; i < toupleSpace.size(); i++) {
				for (int j = 0; j < toupleSpace.get(i).length; j++) {
					sb.append(toupleSpace.get(i)[j] + "-");
				}
				sb.append(" ");
			}
			sb.append("----------");
			for (int i = 0; i < touple.length; i++) {
				sb.append(touple[i]);
			}
			LocalLinda.debug1(sb.toString());
			String cmp = "" + lastcnt;
			ArrayList<Integer> tosend = new ArrayList<Integer>();
			out: for (int i = 0; i < RequestedTouples.size(); i++) {
				if (equals(RequestedTouples.get(i), touple) && delayedCnt.get(i).equals(cmp)) {
					found = true;
					LocalLinda.debug1("SPECTACULAR" + touple[0]);
					ii = i;
					break out;
				}
			}
			if (found) {
				RemoteWait rw;
				String ids;
				boolean rd = ISIN.get(ii);
				if (rd) {
					rw = RemoteWaits.remove(ii);
					RequestedTouples.remove(ii);
					delayedCnt.remove(ii);
					ids = IDS.remove(ii);
					ISIN.remove(ii);
				} else {
					rw = RemoteWaits.get(ii);
					ids = IDS.get(ii);
				}
				rw.addTouple(touple, ids);
			} else {
				LocalLinda.debug1("TOUPLE ADD " + toupleSpace.size());
				toupleSpace.add(touple);
				Cnt.add("" + lastcnt);
			}
			checkall();
		} catch (Exception e) {
		}
	}

	private synchronized boolean equals(String[] a, String[] b) {
		if ((a == null) || (b == null) || (a.length != b.length)) {
			return false;
		}
		boolean match = true;
		for (int i = 0; i < a.length; i++) {
			if (a[i] != null) {
				match = match && a[i].equals(b[i]);
			}
		}
		return match;
	}

	public synchronized boolean existsDirectory(String ip, int port, String classname) {
		boolean exists = false;
		for (int i = 0; i < Directories.size(); i++) {
			if (Directories.get(i)[1].equals(ip) && Integer.parseInt(Directories.get(i)[3]) == port
					&& Directories.get(i)[2].equals(classname)) {
				exists = true;
			}
		}
		return exists;
	}

	synchronized void checkall() {
		boolean more = true;
		while (more) {
			boolean foundone = false;
			out: for (int i = 0; i < RequestedTouples.size(); i++) {
				for (int j = 0; j < toupleSpace.size(); j++) {
					if (equals(RequestedTouples.get(i),
							toupleSpace.get(j))/* && Cnt.get(j).equals(delayedCnt.get(i)) */) {
						RemoteWait rw;
						String ids;
						String[] ts;
						foundone = true;
						boolean rd = ISIN.get(i);
						if (rd) {
							rw = RemoteWaits.remove(i);
							RequestedTouples.remove(i);
							delayedCnt.remove(i);
							ids = IDS.remove(i);
							ISIN.remove(i);
							ts = toupleSpace.remove(j);
							Cnt.remove(j);
						} else {
							rw = RemoteWaits.get(i);
							ids = IDS.get(i);
							ts = toupleSpace.get(j);
						}
						try {
							rw.addTouple(ts, ids);
							break out;
						} catch (RemoteException e) {
							e.printStackTrace();
						}
						break out;
					}
				}
			}
			if (!foundone)
				return;
		}
	}

	public boolean addDelayedP(String workstation_ip, int workstation_port, String[] tuple, String id, boolean isin,
			int lastcnt) {
		try {
			checkall();
			RemoteWait stubg = null;
			if (System.getSecurityManager() == null) {
				System.setSecurityManager(new SecurityManager());
			}

			try {

				Registry r = LocateRegistry.getRegistry(workstation_ip, workstation_port);
				stubg = (RemoteWait) r.lookup("/wait");

			} catch (RemoteException | NotBoundException e) {
				// e.printStackTrace();
			}
			// LocalLinda.debug1("delayed1 - -" + toupleSpace.size());
			/*
			 * for (int i = 0; i < touple.length; i++) { LocalLinda.debug1("t:" + i + ": " +
			 * touple[i]); } for (int i = 0; i < toupleSpace.size(); i++) { for (int j = 0;
			 * j < toupleSpace.get(i).length; j++) { LocalLinda.debug1(i + "-" + j + ": " +
			 * toupleSpace.get(i)[j]); } }
			 */
			// System.err.println(touple[0]);
			String comp = "" + lastcnt;
			for (int i = 0; i < toupleSpace.size(); i++) {
				if (equals(tuple, toupleSpace.get(i)) && comp.equals(Cnt.get(i))) {
					// LocalLinda.debug1("delayedfound");
					try {
						LocalLinda.debug1("BINGO " + tuple[0] + " " + id);
						String[] ts;
						if (isin) {
							ts = toupleSpace.remove(i);
							Cnt.remove(i);
						} else
							ts = toupleSpace.get(i);
						/*
						 * for (int j = 0; j < ts.length; j++) { System.out.println(ts[j]); }
						 */
						stubg.addTouple(ts, id);
						return true;
						// System.err.println("gone"+isin);
					} catch (RemoteException e) {
						// e.printStackTrace();
					}
				}
			}
			// LocalLinda.debug1("delayednf");
			/*
			 * for (int i = 0; i < touple.length; i++) { LocalLinda.debug1("delayednf " + i
			 * System.err.println(touple[0]);
			 * 
			 * + ": " + touple[i]); }
			 */
		} catch (Exception e) {
		}
		return false;
	}
}






