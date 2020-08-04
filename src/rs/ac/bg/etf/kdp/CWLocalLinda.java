package rs.ac.bg.etf.kdp;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class CWLocalLinda{
	public RemoteCommunication stub;

	public CWLocalLinda(String host, int port) throws Exception {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		try {
			Registry r = LocateRegistry.getRegistry(host, port);
			stub = (RemoteCommunication) r.lookup("/touplespace");
		} catch (RemoteException | NotBoundException e) {
			System.out.println("fail");
			throw e;
		}
	}

}
