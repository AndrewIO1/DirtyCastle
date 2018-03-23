package util;

import java.util.HashMap;
import java.util.Map;

public class DataPack {
	Map<String, Integer> ints;
	Map<String, String> strings;
	
	public DataPack() {
		ints = new HashMap<String, Integer>();
		strings = new HashMap<String, String>();
	}
	
	public void putInt(String name, int data) {
		ints.put(name, data);
	}
	
	public int getInt(String name) {
		if(!ints.containsKey(name)) return 0;
		return ints.get(name);
	}
	
	public void putString(String name, String data) {
		strings.put(name, data);
	}
	
	public String getString(String name) {
		return strings.get(name);
	}
}
