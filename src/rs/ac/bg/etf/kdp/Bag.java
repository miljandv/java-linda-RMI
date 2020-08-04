package rs.ac.bg.etf.kdp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

public class Bag extends Thread {
	Linda linda;
	double xmin;
	double xmax;
	int n;
	static String newfbag="C:\\Users\\milja\\eclipse-workspace\\Linda\\newfbag.txt";
	
	public static void debug1(String string) {
		BufferedWriter writesr;
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(newfbag, true)));
			out.println(string);
			out.close();
		}catch (Exception e1) {
		}
	}
	public Bag() {
		debug1("Bag constructor");
		this.linda = ToupleSpace.getLinda();
		debug1("Bag constructor1");
		String[] parameters = { "bagParameters", null, null, null };
		debug1("Bag constructor2");
		linda.in(parameters);
		debug1("Bag constructor3");
		this.xmin = Double.parseDouble(parameters[1]);
		this.xmax = Double.parseDouble(parameters[2]);
		this.n = Integer.parseInt(parameters[3]);
		debug1("Bag created--");
	}

	public Bag(int i) {
	}

	public void run() {
		double dx, x;
		int i;
		x = xmin;
		debug1("Bag started");
		dx = calcDX(xmin, xmax, n);

		for (i = 0; i < n; i++) {
			String[] getTask = { "getTask", null };
			debug1("gettask ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			linda.in(getTask);
			debug1("gettask post ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + getTask[1]);
			String[] request = { "request", "" + getTask[1], "" + x,
					"" + (x + dx) };
			debug1("Bag sending request ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			linda.out(request);
			x = x + dx;
		}
		String[] numNode = { "numNode", null };
		linda.in(numNode);
		int num = Integer.parseInt(numNode[1]);
		for (i = 0; i < num; i++) {
			String[] getTask = { "getTask", null };
			linda.in(getTask);
			
			String[] request = { "request", "" + getTask[1], "0", "-1" };
			linda.out(request);
		}
		String[] stg = new String[] {"bagend","3"};
		linda.out(stg);
		debug1("Bag FINISHED");
	}

	private double calcDX(double min, double max, int N) {
		return (max - min) / N;
	}
	public static void main(String[] args) {
		debug1("Bag PID: "+ProcessHandle.current().pid());
		Bag bag = new Bag();
		bag.run();
		System.out.println("Bag done");
		System.exit(0);
	}
}
