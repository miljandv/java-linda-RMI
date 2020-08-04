package rs.ac.bg.etf.kdp;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import rs.ac.bg.etf.kdp.elems.Worker;
import rs.ac.bg.etf.kdp.elems.Workstation;


public class RemoteCommunicationWorkstationImpl implements RemoteCommunicationWorkstation {
	Worker[] worker;
	int size;
	private Workstation workstation;
	public RemoteCommunicationWorkstationImpl(rs.ac.bg.etf.kdp.elems.Worker[] worker,Workstation mywst,
			int size){
		this.worker=worker;
		this.workstation = mywst;
		this.size=size;
	}
	public void startwork(byte[] test,String ip,int port,String classname,int counter) throws RemoteException {
		synchronized (workstation) {
			workstation.incworkcnt();
			ArrayList<String> newtask = new ArrayList<String>();
			newtask.add("init");
			FileOutputStream fos;
		try {
			String filename ="C:\\Users\\milja\\OneDrive\\Desktop\\WORKSTATION_FILES\\test["+(workstation.ip)+"-"+(workstation.myport)+"]"+(workstation.getnextfileid())+".jar";
			fos = new FileOutputStream(filename);
			fos.write(test);
			fos.close();
			newtask.add(filename);
			newtask.add(ip);
			newtask.add(""+port);
			newtask.add(""+counter);
			newtask.add(classname);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
			workstation.addJob(newtask.toArray());
		}
		workstation.sem.release();
	}
	public void startTask(boolean reg,byte[] test,String ip,int port,String className,int counter,
			String[] newtask, String methodName, String[] arguments,Thread thr) {
		synchronized (workstation) {
			workstation.incworkcnt();
			ArrayList<Object> newtaskk = new ArrayList<Object>();
			if(reg)newtaskk.add("noninitreg");
			else newtaskk.add("noninitireg");
			FileOutputStream fos;
		try {
			String filename ="C:\\Users\\milja\\OneDrive\\Desktop\\WORKSTATION_FILES\\test["+(workstation.ip)+"-"+(workstation.myport)+"]"+(workstation.getnextfileid())+".jar";
			fos = new FileOutputStream(filename);
			fos.write(test);
			fos.close();
			newtaskk.add(filename);
			newtaskk.add(ip);
			newtaskk.add(""+port);
			newtaskk.add(""+counter);
			if(reg) {
			newtaskk.add(className);
			newtaskk.add(newtask);
			newtaskk.add(methodName);
			newtaskk.add(arguments);
			}else {
			newtaskk.add(className);
			newtaskk.add(thr);	
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
			workstation.addJob(newtaskk.toArray());
			workstation.addJobstr1(newtask);
			workstation.addJobstr2(arguments);
		}
		workstation.sem.release();
	}
	@Override
	public String isup() throws RuntimeException {
		return "up";
	}
	@Override
	public void stop(String ip_port_cnt) throws RuntimeException {
		for (int i = 0; i < worker.length; i++) {
			if(worker[i].getKey().equals(ip_port_cnt)) {
				worker[i].kill();
			}
		}
	}
}
