package rs.ac.bg.etf.kdp;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class RemoteWaitImpl implements RemoteWait {
	public synchronized void addTouple(String[] newt,String id) throws RemoteException {
		ToupleSpace.addDelayed(id,newt);
	//	LocalLinda.debug1("delay "+newt[0]+" "+newt[1]+" "+id);
		ToupleSpace.getLinda(id).sem.release();
	} 
}
