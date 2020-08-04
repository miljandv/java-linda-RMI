package rs.ac.bg.etf.kdp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.Semaphore;

public class LocalLinda implements Linda {
	private static final long serialVersionUID = 1L;
	ArrayList<String[]> tupleSpace = new ArrayList<String[]>();
	static String file = "linda.txt";
	String server = "C:\\Users\\milja\\eclipse-workspace\\Linda\\server.txt";
	private String last = "C:\\Users\\milja\\eclipse-workspace\\Linda\\last.txt";
	private String last1 = "C:\\Users\\milja\\eclipse-workspace\\Linda\\last1.txt";
	String ip;
	int port;
	String lastip;
	int lastport;
	static Semaphore mutex = new Semaphore(1);
	int lastcounter;
	static String newf = "C:\\Users\\milja\\eclipse-workspace\\Linda\\newf.txt";
	static String newf1 = "C:\\Users\\milja\\eclipse-workspace\\Linda\\newf1.txt";
	RemoteCommunicationTask stub;
	RemoteWait remotewait;
	Semaphore sem = new Semaphore(0);
	boolean skip = false;

	public LocalLinda() {
		try {
			FileInputStream fstream = new FileInputStream(server);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String strLine = br.readLine();
			String[] params = strLine.split(" ");
			fstream.close();

			FileInputStream fstream1 = new FileInputStream(last);
			BufferedReader br1 = new BufferedReader(new InputStreamReader(fstream1));
			String strLine1 = br1.readLine();
			String[] params1 = null;
			if (strLine1 != null) {
				params1 = strLine1.split(" ");
				fstream1.close();
			} else {
				fstream1 = new FileInputStream(last1);
				br1 = new BufferedReader(new InputStreamReader(fstream1));
				strLine1 = br1.readLine();
				params1 = strLine1.split(" ");
				fstream1.close();
			}
			if (System.getSecurityManager() == null) {
				System.setSecurityManager(new SecurityManager());
			}
			ip = params[0];
			port = Integer.parseInt(params[1]);
			Registry r = LocateRegistry.getRegistry(params[0], Integer.parseInt(params[1]));
			stub = (RemoteCommunicationTask) r.lookup("/linda");
			// String tname = ProcessHandle.current().pid();
			// System.out.println("tname=" + tname);
			// String[] split11 = tname.split(" ");
			skip = true;
			lastip = params1[0];
			lastport = Integer.parseInt(params1[1]);
			// LocalLinda.debug1(lastip + " " + lastport);

			remotewait = new RemoteWaitImpl();
			RemoteWait stubq = (RemoteWait) UnicastRemoteObject.exportObject(remotewait, 0);
			Registry registry;

			lastcounter = Integer.parseInt(params1[2]);
			mutex.acquire();
			kk: while (true) {
				try {

					// LocalLinda.debug1("port == " + lastport);
					registry = LocateRegistry.createRegistry(lastport);
					registry.rebind("/wait", stubq);
					// LocalLinda.debug1("successfull rebind "+lastport);
					break kk;
				} catch (Exception e) {
					lastport++;
				}
			}
			mutex.release();
		} catch (IOException | NotBoundException | InterruptedException e) {
			// LocalLinda.debug1("error stub");
			e.printStackTrace();
		}
	}

	public synchronized void eval(String name, Runnable thread) {

		try {
			stub.eval(name, thread, lastip, lastport, lastcounter);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void eval(final String className, final Object[] initargs, final String methodName,
			final Object[] arguments) {
		try {
			String[] finalinitargs = new String[initargs.length];
			/*
			 * for (int i = 0; i < finalinitargs.length; i++) { try { finalinitargs[i] =
			 * initargs[i].toString(); LocalLinda.debug1("final  " + finalinitargs[i]); }
			 * catch (NullPointerException ex) { } }
			 */
			String[] finalarguments = new String[arguments.length];
			for (int i = 0; i < finalarguments.length; i++) {
				try {
					finalarguments[i] = arguments[i].toString();
				} catch (NullPointerException ex) {
				}
			}
			stub.eval(className, finalinitargs, methodName, finalarguments, lastip, lastport, lastcounter);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void in(String[] tuple) {
		// debug1("in " + tuple[0] + lastip + " " + lastport + " " +
		// ProcessHandle.current().pid());
		try {
			int ii = -1;
			boolean found = false;
			go: for (int i = 0; i < ToupleSpace.getDelayed().size(); i++) {
				if (equals(tuple, ToupleSpace.getDelayed().get(i))) {
					fill(tuple, ToupleSpace.getDelayed().get(i));
					found = true;
					ii = i;
					break go;
				}
			}
			if (found) {
				ToupleSpace.getDelayed().remove(ii);
				// LocalLinda.debug1("in LOCAL-----------");
				return;
			}
		} catch (Exception e) {

		}
		try {
			stub.in(lastip, lastport, tuple, "" + ProcessHandle.current().pid(), lastcounter);
			// debug1("in wait" + lastip + " " + lastport + " " + tuple[0]);
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
		while (true) {
			try {
				// LocalLinda.debug1("PRE sem IN wwwwwwwwwwwwwwwwwwwwwwwwww");

				sem.acquire();
				// debug1("semmmm" + ToupleSpace.getDelayed().size() + " " + lastip + " " +
				// lastport
				// + ToupleSpace.getDelayed().get(0)[0]);
				boolean flag = false;
				int rem = 0;
				// LocalLinda.debug1("sem++++++");
				ll: for (int i = 0; i < ToupleSpace.getDelayed().size(); i++) {
					// debug1(tuple[0] + "debug " + ToupleSpace.getDelayed().get(i)[0] + " "
					// + ToupleSpace.getDelayed().get(i)[1]);
					if (equals(tuple, ToupleSpace.getDelayed().get(i))) {
						fill(tuple, ToupleSpace.getDelayed().get(i));
						// debug1("WOW" + " " + lastip + " " + lastport + " " + tuple[0]);
						flag = true;
						rem = i;
						break ll;
					}
				}
				if (flag) {
					ToupleSpace.getDelayed().remove(rem);
					return;
				}
				sem.release();
				Thread.sleep(15);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean inp(String[] tuple) {
		try {
			try {
				int ii = -1;
				boolean found = false;
				go: for (int i = 0; i < ToupleSpace.getDelayed().size(); i++) {
					if (equals(tuple, ToupleSpace.getDelayed().get(i))) {
						fill(tuple, ToupleSpace.getDelayed().get(i));
						found = true;
						ii = i;
						break go;
					}
				}
				if (found) {
					ToupleSpace.getDelayed().remove(ii);
					// LocalLinda.debug1("in LOCAL-----------");
					return true;
				}
			} catch (Exception e) {

			}
			return stub.inp(lastip, lastport, tuple, "" + ProcessHandle.current().pid(), lastcounter);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void out(String[] tuple) {
		// debug1("out " + tuple[0]);
		try {
			stub.out(tuple, "" + ProcessHandle.current().pid(), lastcounter);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void rd(String[] tuple) {
		try {
			int ii = -1;
			boolean found = false;
			go: for (int i = 0; i < ToupleSpace.getDelayed().size(); i++) {
				if (equals(tuple, ToupleSpace.getDelayed().get(i))) {
					fill(tuple, ToupleSpace.getDelayed().get(i));
					found = true;
					ii = i;
					break go;
				}
			}
			if (found) {
				ToupleSpace.getDelayed().remove(ii);
				// LocalLinda.debug1("in LOCAL-----------");
				return;
			}
		} catch (Exception e) {

		}
		try {
			stub.rd(lastip, lastport, tuple, "" + ProcessHandle.current().pid(), lastcounter);
			// debug1("in wait" + lastip + " " + lastport + " " + tuple[0]);
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
		while (true) {
			try {
				// LocalLinda.debug1("PRE sem IN wwwwwwwwwwwwwwwwwwwwwwwwww");

				sem.acquire();
				// debug1("semmmm" + ToupleSpace.getDelayed().size() + " " + lastip + " " +
				// lastport
				// + ToupleSpace.getDelayed().get(0)[0]);
				boolean flag = false;
				int rem = 0;
				// LocalLinda.debug1("sem++++++");
				ll: for (int i = 0; i < ToupleSpace.getDelayed().size(); i++) {
					// debug1(tuple[0] + "debug " + ToupleSpace.getDelayed().get(i)[0] + " "
					// + ToupleSpace.getDelayed().get(i)[1]);
					if (equals(tuple, ToupleSpace.getDelayed().get(i))) {
						fill(tuple, ToupleSpace.getDelayed().get(i));
						// debug1("WOW" + " " + lastip + " " + lastport + " " + tuple[0]);
						flag = true;
						rem = i;
						break ll;
					}
				}
				if (flag) {
					ToupleSpace.getDelayed().remove(rem);
					return;
				}
				sem.release();
				Thread.sleep(15);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean rdp(String[] tuple) {
		try {
			try {
				int ii = -1;
				boolean found = false;
				go: for (int i = 0; i < ToupleSpace.getDelayed().size(); i++) {
					if (equals(tuple, ToupleSpace.getDelayed().get(i))) {
						fill(tuple, ToupleSpace.getDelayed().get(i));
						found = true;
						ii = i;
						break go;
					}
				}
				if (found) {
					return true;
				}
			} catch (Exception e) {

			}
			return stub.rdp(lastip, lastport, tuple, "" + ProcessHandle.current().pid(), lastcounter);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return false;
	}

	/*
	 * public static void debug(String str) { BufferedWriter writesr; try {
	 * PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(newf,
	 * true))); out.println(str); out.close(); } catch (Exception e1) { } }
	 */
	public static void debug1(String string) {
		BufferedWriter writesr;
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(newf1, true)));
			out.println(string);
			out.close();
		} catch (Exception e1) {
		}
	}

	private boolean equals(String[] a, String[] b) {
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

	private void fill(String a[], String b[]) {
		for (int i = 0; i < a.length; i++) {
			if (a[i] == null) {
				a[i] = new String(b[i]);
			}
		}
	}

}
