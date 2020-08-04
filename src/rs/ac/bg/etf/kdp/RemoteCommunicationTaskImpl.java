package rs.ac.bg.etf.kdp;

import java.rmi.RemoteException;

import rs.ac.bg.etf.kdp.elems.RequestListener;
import rs.ac.bg.etf.kdp.elems.ResultSender;
import rs.ac.bg.etf.kdp.elems.WorkStarter;

public class RemoteCommunicationTaskImpl implements RemoteCommunicationTask {
	RequestListener rs;
	ResultSender res;

	public RemoteCommunicationTaskImpl(RequestListener rs, ResultSender res) {
		this.rs = rs;
		this.res = res;
	}

	@Override
	public synchronized void eval(String name, Runnable thread, String ip, int port, int counter) {
		try {
			rs.addDirectory(
					new String[] { "", ip, "", "" + port, "", "" + counter, "noninit-ireg", name, thread.toString() });
			synchronized (rs) {
				rs.notify();
			}
		} catch (Exception e) {
		}
	}

	@Override
	public synchronized void eval(String className, String[] initargs, String methodName, String[] arguments, String ip,
			int port, int counter) {
		try {
			rs.addDirectory(
					new String[] { "", ip, "", "" + port, "", "" + counter, "noninit-reg", className, methodName });
			rs.addArgs1(initargs);
			rs.addArgs2(arguments);
			synchronized (rs) {
				rs.notify();
			}
		} catch (Exception e) {
		}
	}

	@Override
	public synchronized void in(String workstation_ip, int workstation_port, String[] tuple, String id,int lastcnt) {
		try {
			rs.addDelayed(workstation_ip, workstation_port, tuple, id,true,lastcnt);
		} catch (Exception e) {
		}
	}

	@Override
	public synchronized boolean inp(String workstation_ip, int workstation_port, String[] tuple, String id,int lastcnt) {
		return rs.addDelayedP(workstation_ip, workstation_port, tuple, id, true, lastcnt); 
	}

	@Override
	public synchronized void out(String[] tuple, String id,int lastcnt) {
		try {
			rs.addTouple(tuple, id, lastcnt);
			// LocalLinda.debug1("touplespace size: " + rs.getToupleSpace().size());
		} catch (Exception e) {
		}
	}

	@Override
	public synchronized void rd(String workstation_ip, int workstation_port, String[] tuple, String id,int lastcnt) {
		try {
			System.err.println("read!");
			rs.addDelayed(workstation_ip, workstation_port, tuple, id, false,lastcnt);
		} catch (Exception e) {
		}
	}

	@Override
	public synchronized boolean rdp(String workstation_ip, int workstation_port, String[] tuple, String id,int lastcnt) {
		return rs.addDelayedP(workstation_ip, workstation_port, tuple, id, false, lastcnt); 
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

	public void signalsem() throws RemoteException {
		synchronized (rs.mtx) {
			rs.mtx = 1;
			System.out.println("signal");
		}
	}

	@Override
	public int getMyport(String ip) throws RemoteException {
		for (int i = 0; i < rs.getDirectories().size(); i++) {
			if (rs.getDirectories().get(i)[1].equals(ip)) {
				return Integer.parseInt(rs.getDirectories().get(i)[3]);
			}
		}
		return -1;
	}

}
