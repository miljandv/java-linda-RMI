package rs.ac.bg.etf.kdp;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TS extends Remote{

	public LocalLinda getLinda() throws RemoteException;
	public void setLinda(LocalLinda l) throws RemoteException;
}
