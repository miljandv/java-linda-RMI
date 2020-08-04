package rs.ac.bg.etf.kdp.elems;

public class ThreadWrapper extends Thread {
	static int port;
	static String ip;
	public static int getPort() {
		return port;
	}
	public static void setPort(int port) {
		ThreadWrapper.port = port;
	}
	public static String getIp() {
		return ip;
	}
	public static void setIp(String ip) {
		ThreadWrapper.ip = ip;
	}
}
