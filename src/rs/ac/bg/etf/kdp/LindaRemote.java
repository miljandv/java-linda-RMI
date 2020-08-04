package rs.ac.bg.etf.kdp;

import java.io.*;
import java.rmi.Remote;

public interface LindaRemote extends Serializable,Remote {
	public void out(String[] tuple);
	public void in(String[] tuple);
	public boolean inp(String[] tuple);
	public void rd(String[] tuple);
	public boolean rdp(String[] tuple);
	public void eval(String name, Runnable thread);
	public void eval(String className, Object[] construct, String methodName,
			Object[] arguments);
}
