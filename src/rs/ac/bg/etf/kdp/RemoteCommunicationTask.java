package rs.ac.bg.etf.kdp;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;

public interface RemoteCommunicationTask extends Remote {
	public  void eval(String name, Runnable thread,String ip,int port,int counter) throws RemoteException;

	public  void eval(String className, String[] initargs, String methodName, String[] arguments,String ip,int port,int counter)throws RemoteException;

	public  void in(String workstation_ip,int workstation_port,String[] tuple,String id,int lastcnt)throws RemoteException;

	public  boolean inp(String workstation_ip, int workstation_port, String[] tuple, String id,int lastcnt)throws RemoteException;

	public  void out(String[] tuple,String id,int lastcnt)throws RemoteException;

	public  void rd(String workstation_ip, int workstation_port, String[] tuple, String id,int lastcnt)throws RemoteException;

	public  boolean rdp(String workstation_ip, int workstation_port, String[] tuple, String id,int lastcnt)throws RemoteException;

	public  void signalsem()throws RemoteException;

	public  int getMyport(String ip)throws RemoteException;
}
