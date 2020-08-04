package rs.ac.bg.etf.kdp;

import java.rmi.RemoteException;
import java.util.concurrent.Semaphore;

import rs.ac.bg.etf.kdp.elems.ResultHandler;

public class RemoteCommunicationClientImpl implements RemoteCommunicationClient {
	ResultHandler rh;
	int k=1;
	public RemoteCommunicationClientImpl () {
		
	}
	public void ReceiveResult(Object[] result) throws RemoteException {
		rh.addResult(result);
		synchronized (rh) {
			rh.notify();
		}
	}
	public void setRH(ResultHandler rh) throws RemoteException {
		this.rh=rh;
	}

}
