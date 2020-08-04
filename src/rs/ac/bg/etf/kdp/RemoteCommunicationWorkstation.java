package rs.ac.bg.etf.kdp;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javafx.concurrent.Worker;

public interface RemoteCommunicationWorkstation extends Remote {

	void startwork(byte[] test,String ip,int port,String classname, int counter) throws RemoteException;
	public void startTask(boolean reg,byte[] test,String ip,int port,String className,int counter,
			String[] newtask, String methodName, String[] arguments,Thread thr)throws RemoteException;
	public String isup() throws RemoteException;
	public void stop(String ip_port_cnt)throws RemoteException;
}
