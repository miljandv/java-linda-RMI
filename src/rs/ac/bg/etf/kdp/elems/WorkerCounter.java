package rs.ac.bg.etf.kdp.elems;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JLabel;

import rs.ac.bg.etf.kdp.RemoteCommunication;
import rs.ac.bg.etf.kdp.RemoteCommunicationWorkstation;

public class WorkerCounter extends Thread {
	private JLabel workst;
	private JLabel worker;
	private int wstcnt;
	private int wcnt;
	int cnt = 0;
	int numfree;
	int lastCheck=0;
	private ArrayList<String[]> workstations = new ArrayList<String[]>(); //size,ip,port
	private ArrayList<Integer> free = new ArrayList<Integer>();
	private ArrayList<RemoteCommunicationWorkstation> rmcs = new ArrayList<RemoteCommunicationWorkstation>();
	private WorkStarter ws;
	public WorkerCounter(JLabel workst, JLabel worker) {
		this.workst = workst;
		this.worker = worker;
		start();
	}
	public void run() {
		while (true) {
			try {
				synchronized (this) {
					wait();
				}
				synchronized (workstations) {
					while (cnt != workstations.size()) {
						wstcnt++;
						wcnt += Integer.parseInt(workstations.get(cnt)[0]);
						try {
							Registry r = LocateRegistry.getRegistry(workstations.get(cnt)[1],
									Integer.parseInt(workstations.get(cnt)[2]));
							rmcs.add((RemoteCommunicationWorkstation) r.lookup("/remoteclient"));
						} catch (RemoteException | NotBoundException e) {
							e.printStackTrace();
						}
						cnt++;
						System.out.println("added station");
						synchronized (ws) {
							ws.notify();
						}
					}
				}
				workst.setText("Workstations: " + wstcnt);
				worker.setText("Workers: " + wcnt);
			} catch (InterruptedException e) {	e.printStackTrace();}
		}
	}

	public void addStation(String string, String string2, String str) {
		workstations.add(new String[] { string, string2,str });  //size,ip,port
		free.add(Integer.parseInt(string));
		numfree+=Integer.parseInt(string);
	}

	public synchronized int getNumFree() {
		return numfree;
	}

	public synchronized void setWorkStarter(WorkStarter ws) {
		this.ws=ws;
	}

	public synchronized void decrementNumFree() {
		if(numfree!=0)numfree--;
	}
	public synchronized void incrementNumFree() {
		numfree++;
	}
	public synchronized ArrayList<String[]> getWorkstations(){
		return workstations;
	}
	public synchronized ArrayList<Integer> getFree(){
		return free;
	}
	public synchronized ArrayList<RemoteCommunicationWorkstation> GetRemoteCommunicationWorkstation(){
		return rmcs;
	}

	public synchronized void setNotFreeAtIndex(int i) {
		free.set(i, free.get(i)-1);
	}

	public synchronized void incfree(String ip, int myport) {
		for (int i = 0; i < workstations.size(); i++) {
			if(workstations.get(i)[1].equals(ip) && workstations.get(i)[2].equals(""+myport)) {
				free.set(i, free.get(i)+1);
				numfree++;
				return;
			}
		}
	}

	public synchronized ArrayList<RemoteCommunicationWorkstation> getrmcs() {
		return rmcs;
	}

	public synchronized RemoteCommunicationWorkstation GetRemoteCommunicationWorkstation(String strings, String strings2) {
		for (int i = 0; i < workstations.size(); i++) {
			if(workstations.get(i)[1].equals(strings) &&
					workstations.get(i)[2].contentEquals(strings2)) {
				return rmcs.get(i);
			}
		}
		return null;
	}

	public synchronized void removeStation(RemoteCommunicationWorkstation remoteCommunicationWorkstation) {
				int index = rmcs.indexOf(remoteCommunicationWorkstation);
				wcnt -= Integer.parseInt(workstations.get(index)[0]);
				String[] data=workstations.remove(index);
				rmcs.remove(index);
				numfree-=free.remove(index);
				cnt--;
				wstcnt--;
				workst.setText("Workstations: " + wstcnt);
				worker.setText("Workers: " + wcnt);	
				return;
		
	}


}
