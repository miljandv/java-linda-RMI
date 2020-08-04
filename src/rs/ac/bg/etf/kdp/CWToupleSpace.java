package rs.ac.bg.etf.kdp;

public class CWToupleSpace {
	static Linda linda;
	
	public CWToupleSpace(String host, int port) {
		linda = new CWLocalLinda(host, port);
	}
	
	public static Linda getLinda() {
		return linda;
	}

	public static void setLinda(Linda l) {
		linda = l;
	}
}
