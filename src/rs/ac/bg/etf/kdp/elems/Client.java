package rs.ac.bg.etf.kdp.elems;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import rs.ac.bg.etf.kdp.CWLocalLinda;
import rs.ac.bg.etf.kdp.RemoteCommunication;
import rs.ac.bg.etf.kdp.RemoteCommunicationClient;
import rs.ac.bg.etf.kdp.RemoteCommunicationClientImpl;
import rs.ac.bg.etf.kdp.gui.ClientGUI;

public class Client {
	RemoteCommunicationClient remotecomm;
	RemoteCommunication linda;
	String ip;
	int serverPort;
	int myport;
	boolean reached = false;
	private ClientGUI GUI;
	private String serverIp;
	static ResultHandler rh;

	public Client(RemoteCommunication stub, String ip, int serverPort, String serverIp) {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		remotecomm = new RemoteCommunicationClientImpl();
		this.serverPort = serverPort;
		this.serverIp = serverIp;
		this.myport = serverPort + 1;
		try {
			RemoteCommunicationClient mystub = (RemoteCommunicationClient) UnicastRemoteObject.exportObject(remotecomm,
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
			this.linda = stub;
			this.ip = ip;
			GUI = new ClientGUI(stub, ip, myport, this);
			rh = new ResultHandler(GUI.getrjta());
			remotecomm.setRH(rh);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		CWLocalLinda buffer = null;
		try {
			buffer = new CWLocalLinda(args[0], Integer.parseInt(args[1]));
		} catch (Exception e) {
		}
		RemoteCommunication rc = (buffer==null)?null:buffer.stub;
		new Client(rc, args[2], Integer.parseInt(args[1]), args[0]);
	}

	public void setServer(RemoteCommunication stub) {
		// try {
		linda = stub;
		// }catch (Exception e) {
		// }
	}

	public String getServerIp() {
		return serverIp;
	}

	public int getServerPort() {
		return serverPort;
	}
}
