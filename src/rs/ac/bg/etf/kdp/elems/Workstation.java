package rs.ac.bg.etf.kdp.elems;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import javax.swing.JFrame;

import rs.ac.bg.etf.kdp.CWLocalLinda;
import rs.ac.bg.etf.kdp.RemoteCommunication;
import rs.ac.bg.etf.kdp.RemoteCommunicationTask;
import rs.ac.bg.etf.kdp.RemoteCommunicationWorkstation;
import rs.ac.bg.etf.kdp.RemoteCommunicationWorkstationImpl;
import rs.ac.bg.etf.kdp.gui.WorkstationGUI;

public class Workstation {
	RemoteCommunication remotecomm;
	public static String ip;
	int fileid;
	int batid;
	public static int myport;
	static Worker[] worker;
	volatile int workcnt;
	WorkstationGUI GUI;
	static String file="workstationdata.txt";
	static String server="server.txt";
	public Semaphore sem = new Semaphore(0);
	RemoteCommunicationWorkstation remotecommwork;
	ArrayList<Object[]> Jobs = new ArrayList<Object[]>();
	private RemoteCommunicationTask remotecommtask;
	private ArrayList<String[]> Jobsstr1 = new ArrayList<String[]>();
	private ArrayList<String[]> Jobsstr2 = new ArrayList<String[]>();
	public Workstation(RemoteCommunication stub, String ip, int size, int port) {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		worker = new Worker[size];
		remotecommwork = new RemoteCommunicationWorkstationImpl(this.worker,this,size);
		myport = port;
		try {
			  String str = ""+ip+" "+myport;
			    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			    writer.write(str);
			    writer.close();
		}catch (Exception e) {	e.printStackTrace();}
		try {
			RemoteCommunicationWorkstation mystub = (RemoteCommunicationWorkstation) UnicastRemoteObject.exportObject(remotecommwork,
					0);
			Registry registry;
			while (true) {
				try {
					registry = LocateRegistry.createRegistry(this.myport);
					break;
				} catch (Exception e) {
					this.myport++;
				}
			}
			registry.rebind("/remoteclient", mystub);
			this.remotecomm = stub;
			this.ip = ip;
			remotecomm.RegisterWorkStation(new String[] { "" + size, ip,""+myport });
			GUI = new WorkstationGUI(stub, ip, size,myport);
			for (int i = 0; i < size; i++) {
				worker[i] = new Worker(this,GUI.getProgreesBar(i),GUI.getJLabel(i));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		CWLocalLinda buffer=null;
		try {
			buffer = new CWLocalLinda(args[3], Integer.parseInt(args[1]));
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			  String str = ""+args[0]+" "+Integer.parseInt(args[1]);
			    BufferedWriter writer = new BufferedWriter(new FileWriter(server));
			    writer.write(str);
			    writer.close();
		}catch (Exception e) {	e.printStackTrace();}
		new Workstation(buffer.stub, args[3], Integer.parseInt(args[2]), Integer.parseInt(args[1]));
	}

	public void incworkcnt() {
		workcnt++;
	}

	public synchronized int getworkcnt() {
		return workcnt;
	}
	public synchronized ArrayList<Object[]> getJobs(){
		return Jobs;
	}

	public synchronized void addJob(Object[] objects) {
		Jobs.add(objects);
	}

	public synchronized Object[] getNextTask() {
		return Jobs.remove(0);
	}

	public synchronized int getnextfileid() {
		return fileid++;
	}
	public synchronized int getnextbatid() {
		return batid++;
	}

	public Object getRemoteCommTask() {
		return remotecommtask;
	}

	public String[] getNextTaskstr1() {
		return Jobsstr1.remove(0);
	}
	public String[] getNextTaskstr2() {
		return Jobsstr2.remove(0);
	}

	public void addJobstr2(String[] arguments) {
		Jobsstr2.add(arguments);
	}
	public void addJobstr1(String[] arguments) {
		Jobsstr1.add(arguments);
	}
	public Worker[] getWorkers() {
		return worker;
	}
}
