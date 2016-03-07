package net.dflmngr.model.service;

import java.util.List;
import java.util.Map;

import net.dflmngr.model.entity.Globals;
import net.dflmngr.model.entity.keys.GlobalsPK;

public interface GlobalsService extends GenericService<Globals, GlobalsPK> {
	
	public Map<String, String> getValueAndParams(String code, String group);
	public String getValue(String group, String code);
	public String getCurrentYear();
	public List<String> getAflFixtureUrl(); 
	public String getGroundTimeZone(String ground);
	public List<String> getTeamCodes();
	public String getAppDir();
	public String getReportDir();
	public String getStandardLockoutTime();
	public String getNonStandardLockout(int round);
	public String getAflRoundsMax();
	public Map<String, String> getEmailConfig();
	public String getAflStatsUrl();
	public String getEmailerRoot();
}
