package rs.ac.bg.etf.kdp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

public class Collector extends Thread {
	Linda linda;
	int n;
	static String newfc="C:\\Users\\milja\\eclipse-workspace\\Linda\\newfc.txt";
	
	public static void debug1(String string) {
		BufferedWriter writesr;
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(newfc, true)));
			out.println(string);
			out.close();
		}catch (Exception e1) {
		}
	}
	public Collector() {
		this.linda = ToupleSpace.getLinda();
		String[] parameters = { "collectorParameters", null };
		linda.in(parameters);
		this.n = Integer.parseInt(parameters[1]);
	}

	public void run() {
		debug1("collector start");
		double integral, data;
		integral = 0;
		for (int i = 0; i < n; i++) {
			String[] responce = { "responce", null };
			linda.in(responce);
			data = Double.parseDouble(responce[1]);
			integral = integral + data;
		}
		String[] stg = new String[] {"bagend",null};
		linda.in(stg);
		String[] result = { "result", "" + integral };
		linda.out(result);
		debug1("collector end");
	}
	public static void main(String[] args) {
		debug1("Collector PID: "+ProcessHandle.current().pid());
		Collector col = new Collector();
		col.run();
		System.out.println("Collector done");
		System.exit(0);
	}
}
