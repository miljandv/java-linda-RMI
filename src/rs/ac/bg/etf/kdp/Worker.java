package rs.ac.bg.etf.kdp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

public class Worker extends Thread{
	private static final double PRECISION = 0.000001;
	Linda linda;
	int id;
	static String newf1="C:\\Users\\milja\\eclipse-workspace\\Linda\\newf1.txt";
	
	public static void debug1(String string) {
		BufferedWriter writesr;
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(newf1, true)));
			out.println(string);
			out.close();
		}catch (Exception e1) {
		}
	}
	public Worker() {
		this.linda = ToupleSpace.getLinda();
		String[] workerParameters = { "workerParameters", null };
		linda.in(workerParameters);
		this.id = Integer.parseInt(workerParameters[1]);
		String[] workerOutParameters = { "workerParameters", "" + (id + 1) };
		linda.out(workerOutParameters);
	}

	public void run() {
		debug1("worker start"+id);
		boolean end = false;
		while (!end) {
			String[] getTask = { "getTask", "" + id };
			linda.out(getTask);

			String[] request = { "request", "" + id, null, null };
			debug1("worker req"+id);
			linda.in(request);
			debug1("worker req end"+id);
			double left = Double.parseDouble(request[2]);
			double right = Double.parseDouble(request[3]);
			if (left > right) {
				end = true;
				break;
			}
			double data = calcIntegral(left, right);
			String[] responce = { "responce", "" + data };
			linda.out(responce);
			//LocalLinda.debug1("end worker");
		}
	}

	private double calcIntegral(double left, double right) {
		double data = 0;
		if (left < right) {
			if ((right - left) < PRECISION) {
				data = (function(left) + function(right)) / 2;
				data = data * (right - left);
			} else {
				double midle = (left + right) / 2;
				data = calcIntegral(left, midle) + calcIntegral(midle, right);
			}
		}
		return data;
	}

	private double function(double x) {
		if (x > PRECISION) {
			return Math.exp(x) / x;
		} else
			return 1;
	}
	public static void main(String[] args) {
		debug1("Worker PID: "+ProcessHandle.current().pid());
		Worker worker = new Worker();
		worker.run();
		System.out.println("worker done");
		System.exit(0);
	}
}
