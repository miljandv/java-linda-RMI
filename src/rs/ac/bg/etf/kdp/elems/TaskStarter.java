package rs.ac.bg.etf.kdp.elems;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.swing.JTextArea;


public class TaskStarter extends Thread {
	private JTextArea jta;
	private int cnt;
	private RequestListener rs;
	private WorkerCounter wc;
	private ArrayList<Object[]> Tasks = new ArrayList<Object[]>(); 
	private ArrayList<String[]> ToupleSpace = new ArrayList<String[]>();
	public TaskStarter(JTextArea jta) {
		this.jta = jta;
	}

	public void startW() {
		start();
	}

	public void run() {
		while (true) {
			while (cnt == Tasks.size()) {
				synchronized (this) {
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			System.out.println("tasker");
			while (cnt != Tasks.size()) {
				while (wc.getNumFree() == 0) {
					synchronized (this) {
						try {
							wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				label: for (int i = 0; i < wc.getWorkstations().size(); i++) {
					if (wc.getFree().get(i) != 0) {
						wc.setNotFreeAtIndex(i);
						try {
							String ip = "";
							int port = 0;
							int counter = 0;
							if(Tasks.get(cnt)[0].equals("reg")) {
								ip = Tasks.get(cnt)[5].toString();
								port = Integer.parseInt(Tasks.get(cnt)[6].toString());
								counter = (int) Tasks.get(cnt)[7];
							}
							else if(Tasks.get(cnt)[0].equals("ireg")) {
								ip = (String) Tasks.get(cnt)[2];
								port = (int) Tasks.get(cnt)[3];
								counter = (int) Tasks.get(cnt)[4];
							}
							String ss= "";
							for (int j = 0; j < rs.getDirectories().size(); j++) {
								if(rs.getDirectories().get(i)[1].equals(ip) &&
										Integer.parseInt(rs.getDirectories().get(i)[2]) == port &&
										Integer.parseInt(rs.getDirectories().get(i)[4]) == counter) {
									ss = rs.getDirectories().get(i)[0];
								}
							}
							File ft = new File(ss);
							FileInputStream fis;
							byte[] test = null;
							try {
								fis = new FileInputStream (ft);
								test = new byte[(int) ft.length ()];
								fis.read (test, 0, (int) ft.length () );
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
							if(Tasks.get(cnt)[0].equals("reg")) {
							wc.GetRemoteCommunicationWorkstation().get(i).startTask(true,test, ip, port,counter, (String)Tasks.get(cnt)[1], 
									(Object[])Tasks.get(cnt)[2], (String)Tasks.get(cnt)[3],(Object[])Tasks.get(cnt)[4],null);
							}else if (Tasks.get(cnt)[0].equals("ireg")) {
								wc.GetRemoteCommunicationWorkstation().get(i).startTask(false,test, ip, port,counter, (String)Tasks.get(cnt)[1], 
										null, "",null,(Thread)Tasks.get(cnt)[2]);
							}
						} catch (RemoteException e) {
							e.printStackTrace();
						}
						break label;
					}
				}
				wc.decrementNumFree();
				cnt++;
			}
		}
	}

	public void setRequestListener(RequestListener rs) {
		this.rs = rs;
	}

	public void setWorkerCounter(WorkerCounter wc) {
		this.wc = wc;
	}
	public void addTask(String className,Object[] newtask, String methodName, Object[] arguments,String ip,int port,int counter) {
		Tasks.add(new Object[] {"reg",className,newtask,methodName,arguments,ip,port,counter});
	}

	public void addTask(String name, Runnable thread,String ip,int port,int counter) {
		Tasks.add(new Object[] {"ireg",name,thread,ip,port,counter});
	}

	public ArrayList<String[]> getToupleSpace() {
		return ToupleSpace;
	}

}
