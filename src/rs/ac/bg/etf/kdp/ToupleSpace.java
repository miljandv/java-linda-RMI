package rs.ac.bg.etf.kdp;

import java.util.ArrayList;
import java.util.HashMap;

public class ToupleSpace{
	static HashMap<String, LocalLinda> Linda = new HashMap<String, LocalLinda>();
	static HashMap<String, ArrayList<String[]>> Delayed = new HashMap<String,ArrayList<String[]>>();
	public static LocalLinda getLinda() {
		if(!Linda.containsKey(""+ProcessHandle.current().pid())) {
			Linda.put(""+ProcessHandle.current().pid(), new LocalLinda());
			Delayed.put(""+ProcessHandle.current().pid(),new ArrayList<String[]>());
		}
		return Linda.get(""+ProcessHandle.current().pid());
	}
	public static LocalLinda getLinda(String id) {
		if(!Linda.containsKey(id))Linda.put(id,new LocalLinda());
		return Linda.get(id);
	}
	public static void addDelayed(String key,String[] touple) {
		Delayed.get(key).add(touple);
	}
	public static ArrayList<String[]> getDelayed() {
		return Delayed.get(""+ProcessHandle.current().pid());
	}
}


