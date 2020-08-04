package rs.ac.bg.etf.kdp;

public class Integral {
	public static void main(String[] args) {
		try {
			LocalLinda.debug1("Integral PID: "+ProcessHandle.current().pid());
			// String className = args[0];
			// Linda linda = (Linda) Class.forName(className).newInstance();
			// ToupleSpace.setLinda(linda);
			LocalLinda.debug1("integral start");
			Linda linda = ToupleSpace.getLinda();
			LocalLinda.debug1("gotlinda");
			int num = 10;
			int n = 100;
			double xmin = 0;
			double xmax = 50;
			String[] bagParameters = { "bagParameters", "" + xmin, "" + xmax,
					"" + n };
			LocalLinda.debug1("integral bag");
			linda.out(bagParameters);
			LocalLinda.debug1("integral bag out");
			String[] numNode = { "numNode", "" + num };
			linda.out(numNode);
			Object[] construct = {};
			Object[] arguments = {};
			linda.eval("rs.ac.bg.etf.kdp.Bag", construct, "run", arguments);
			String[] collectorParameters = { "collectorParameters", "" + n };
			linda.out(collectorParameters);
			linda.eval("rs.ac.bg.etf.kdp.Collector", construct, "run",
					arguments);

			String[] workerParameters = { "workerParameters", "0" };
			linda.out(workerParameters);
			for (int i = 0; i < num; i++) {
				linda.eval("rs.ac.bg.etf.kdp.Worker", construct, "run",
						arguments);
			}
			String[] result = { "result", null };
		//	LocalLinda.debug1("integral result block");
			linda.in(result);
			double integral = Double.parseDouble(result[1]);
			System.out.println("[" + xmin + ", " + xmax + ", " + n + "] = "
					+ integral);
			System.exit(0);
			//LocalLinda.debug1("integral finish");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

}
