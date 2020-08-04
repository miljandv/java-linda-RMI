package rs.ac.bg.etf.kdp.elems;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import rs.ac.bg.etf.kdp.RemoteCommunication;
import rs.ac.bg.etf.kdp.RemoteCommunicationImpl;
import rs.ac.bg.etf.kdp.RemoteCommunicationTask;
import rs.ac.bg.etf.kdp.RemoteCommunicationTaskImpl;
import rs.ac.bg.etf.kdp.gui.ServerGUI;


public class Server {
	static String log= "C:\\Users\\milja\\eclipse-workspace\\Linda\\log\\log.txt";
	static RemoteCommunication remotecomm;
	static RemoteCommunicationTask remotecommtask;
	static WorkerCounter wc;
	static RequestListener rs;
	static WorkStarter ws;
	static PerodicReporter pc;
	static ResultSender res;
	public static void main(String[] args) {
		try {
			int port = Integer.parseInt(args[0]);
			if (System.getSecurityManager() == null) {
				System.setSecurityManager(new SecurityManager());
			}
				ServerGUI sg = new ServerGUI();
				ws = new WorkStarter(sg.getJta());
				rs = new RequestListener(sg.getJta(),ws);
				wc = new WorkerCounter(sg.GetJworkerstcnt(),sg.GetJworkercnt());
				ws.setRequestListener(rs);
				ws.setWorkerCounter(wc);
				wc.setWorkStarter(ws);
				res = new ResultSender(rs,sg.getJta());
				rs.setResultSender(res);
				ws.startW();
				pc = new PerodicReporter(wc, rs, ws,res,sg.getJta());
				remotecomm = new RemoteCommunicationImpl(rs,wc,ws,res);
				remotecommtask = new RemoteCommunicationTaskImpl(rs,res);
				RemoteCommunication stub = (RemoteCommunication) UnicastRemoteObject.exportObject(remotecomm, 0);
				RemoteCommunicationTask stubtask = (RemoteCommunicationTask) UnicastRemoteObject.exportObject(remotecommtask, 0);
				
				Registry registry = LocateRegistry.createRegistry(port);
				registry.rebind("/touplespace", stub);
				registry.rebind("/linda",stubtask);
				
				long yourmilliseconds = System.currentTimeMillis();
				SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss"); 
				Date resultdate = new Date(yourmilliseconds);
				System.out.println(sdf.format(resultdate));
				
				
				
				System.out.println("RMI server started on port " + port);			
		}catch(Exception e) {e.printStackTrace();}
	}
}
