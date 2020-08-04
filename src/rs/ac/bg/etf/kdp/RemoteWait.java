package rs.ac.bg.etf.kdp;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public interface RemoteWait extends Remote{
	public void addTouple(String[] newt,String id)throws RemoteException;
}
