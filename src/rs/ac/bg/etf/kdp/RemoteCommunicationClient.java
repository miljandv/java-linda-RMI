package rs.ac.bg.etf.kdp;

import java.rmi.Remote;
import java.rmi.RemoteException;

import rs.ac.bg.etf.kdp.elems.ResultHandler;

public interface RemoteCommunicationClient extends Remote {
	public void ReceiveResult(Object[] objects) throws RemoteException;

	public void setRH(ResultHandler rh) throws RemoteException;
}
