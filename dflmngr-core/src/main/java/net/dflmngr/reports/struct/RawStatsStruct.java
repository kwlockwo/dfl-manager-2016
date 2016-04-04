package net.dflmngr.reports.struct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RawStatsStruct {
	
	private String[] headers = {"Player", "D", "M", "HO", "FF", "FA", "T", "G"};
	
	public static String PLAYER = "player";
	public static String DISPOSALS = "disposals";
	public static String MARKS = "marks";
	public static String HIT_OUTS = "hitouts";
	public static String FREES_FOR = "freesFor";
	public static String FREES_AGAINST = "freesAgainst";
	public static String TACKLES = "tackles";
	public static String GOALS = "goals";
	
	private List<List<String>> reportData;
	
	public void addData(Map<String, String> data) {
		
		List<String> record = new ArrayList<>();
		
		String player = data.get(PLAYER);
		String disposals = data.get(DISPOSALS);
		String marks = data.get(MARKS);
		String hitouts = data.get(HIT_OUTS);
		String freesFor = data.get(FREES_FOR);
		String freesAgainst = data.get(FREES_AGAINST);
		String tackles = data.get(TACKLES);
		String goals = data.get(GOALS);
		
		record.add(player);
		record.add(disposals);
		record.add(marks);
		record.add(hitouts);
		record.add(freesFor);
		record.add(freesAgainst);
		record.add(tackles);
		record.add(goals);
		
		reportData.add(record);
	}
	
	public List<String> getHeaders() {
		List<String> headerData = new ArrayList<>();
		
		for(int i = 0; i < headers.length; i++) {
			headerData.add(headers[i]);
		}
		
		return headerData;
	}
	
	public List<List<String>> getReportData() {
		return reportData;
	}
	
	
	

}
