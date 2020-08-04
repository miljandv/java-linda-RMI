package rs.ac.bg.etf.kdp;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.util.ArrayList;

public  interface RemoteCommunication extends Remote{	
	public  void RegisterWorkStation(String[] str) throws RemoteException;
	public  void sendBinary(String test1,byte[] test,String ip, int myport,String classname)throws RemoteException;
	public  void incfree(String ip, int myport) throws RemoteException;
	public  void sendWorkResult(Object[] objects)throws RemoteException;
	public  String waitsem()throws RemoteException;
	public boolean isup()throws RemoteException;
}









