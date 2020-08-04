package rs.ac.bg.etf.kdp;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import rs.ac.bg.etf.kdp.elems.RequestListener;
import rs.ac.bg.etf.kdp.elems.ResultSender;
import rs.ac.bg.etf.kdp.elems.WorkStarter;
import rs.ac.bg.etf.kdp.elems.WorkerCounter;

public class RemoteCommunicationImpl implements RemoteCommunication {
	private static final long serialVersionUID = 1L;
	RequestListener rs;
	WorkerCounter wc;
	WorkStarter ws;
	ResultSender res;
	static final String acceptedFile="C:\\Users\\milja\\eclipse-workspace\\Linda\\log\\accepted.txt";
	int fileid;
	public RemoteCommunicationImpl(RequestListener rs,WorkerCounter wc,WorkStarter ws,ResultSender res) {
		this.rs=rs;
		this.wc=wc;
		this.ws=ws;
		this.res=res;
	}
	public void RegisterWorkStation(String[] str) {
		wc.addStation(str[0],str[1],str[2]);
			synchronized (wc) {
				wc.notify();
		}
	}
	public void sendBinary(String test1,byte[] test,String ip,int port,String classname) {
		 FileOutputStream fos;
		try {
			if(rs.existsDirectory(ip,port,classname)) {
				synchronized (res) {
					res.notify();	
				}
			}
			fos = new FileOutputStream("C:\\Users\\milja\\OneDrive\\Desktop\\SERVER_FILES\\test"+(fileid++)+".jar");
			fos.write(test);
			fos.close();
			long yourmilliseconds = System.currentTimeMillis();
			SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");    
			Date resultdate = new Date(yourmilliseconds);
			String[] cc =new String[] {"C:\\Users\\milja\\OneDrive\\Desktop\\SERVER_FILES\\test"+(fileid-1)+".jar",ip,classname,""+port,sdf.format(resultdate),""+rs.getnextcounter(),"init","","","",""};
			rs.addDirectory(cc);
			BufferedWriter writesr;
			try {
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(acceptedFile, true)));
				for(int i=0;i<7;i++) {
					out.print(cc[i]+"#");
				}
				out.print("\n");
				out.close();
			} catch (Exception e1) {
				System.out.println("error");
			}
			rs.addArgs1(new String[] {""});
			rs.addArgs2(new String[] {""});
			synchronized (rs) {
				rs.notify();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void incfree(String ip, int myport) throws RemoteException {
		wc.incfree(ip,myport);
	}
	public void sendWorkResult(Object[] str) {
		res.addResult(str);
		synchronized (res) {
			res.notify();
		}
	}
	@Override
	public String waitsem() throws RemoteException {
		synchronized (rs.mtx) {
			//System.out.println("wait");
		if(rs.mtx==1) {
			rs.mtx=0;
			return "true";
		}
		return "false";
	}
	}
	@Override
	public boolean isup() throws RemoteException {
		return true;
	}
}
