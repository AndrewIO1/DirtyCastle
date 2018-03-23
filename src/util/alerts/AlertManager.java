package util.alerts;

import java.util.ArrayList;

public class AlertManager {
	private static volatile AlertManager managerSingleton;

	public static AlertManager getInstance(){
		if(managerSingleton == null) {
			synchronized(AlertManager.class) {
				if(managerSingleton == null) {
					managerSingleton = new AlertManager();
				}
			}
		}
		return managerSingleton;
	}
	
	private ArrayList<String> log;
	private final int logSize = 10;
	private boolean showLog = true;
	
	private AlertManager() {
		log = new ArrayList<String>(logSize);
	}
	
	public void toggleShow() {
		showLog = !showLog;
	}
	
	public boolean showingLog() {
		return showLog;
	}
	
	public void log(String message) {
		if(log.size() == logSize) {
			log.remove(0);
		}
		log.add(message);
	}
	
	public ArrayList<String> getLog(){
		return log;
	}
}
