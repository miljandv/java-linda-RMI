package rs.ac.bg.etf.kdp.elems;

import java.util.ArrayList;

import javax.swing.JTextArea;

public class ResultHandler extends Thread {
	JTextArea rjta;
	public ResultHandler(JTextArea rjta) {
		this.rjta = rjta;
		start();
	}
	ArrayList<Object[]> results = new ArrayList<Object[]>();
	public void run() {
		while(true) {
			try {
				synchronized (this) {
					wait();
				}
			}catch (Exception e) {e.printStackTrace();}
			while(results.size()!=0) {
				Object[] res = results.remove(0);
				for (int i =2; i < res.length-1; i++) {
					rjta.append(res[i].toString()+"\n");
				}
				rjta.append("\n");
			}
		}
	}
	public void addResult(Object[] result) {
		results.add(result);
	}
}
