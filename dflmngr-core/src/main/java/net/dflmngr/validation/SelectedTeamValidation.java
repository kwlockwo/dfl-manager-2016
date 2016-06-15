package net.dflmngr.validation;

import java.util.List;
import java.util.Map;

public class SelectedTeamValidation {
	
	public boolean earlyGames;
	public boolean playedSelections;
	public boolean selectionFileMissing;
	public boolean lockedOut;
	public boolean roundCompleted;
	
	public boolean ffCheckOk;
	public boolean fwdCheckOk;
	public boolean rckCheckOk;
	public boolean midCheckOk;
	public boolean fbCheckOk;
	public boolean defCheckOk;
	public boolean benchCheckOk;
	
	public boolean teamPlayerCheckOk;
	
	public boolean unknownError;
	
	private int round;
	private String teamCode;
	
	private String from;
	
	private Map<String, List<Integer>> insAndOuts;
	
	public SelectedTeamValidation() {
		earlyGames = false;
		playedSelections = false;
		selectionFileMissing = true;
		lockedOut = true;
		roundCompleted = true;
		
		ffCheckOk = false;
		fwdCheckOk = false;
		rckCheckOk = false;
		midCheckOk = false;
		fbCheckOk = false;
		defCheckOk = false;
		benchCheckOk = false;
		
		teamPlayerCheckOk = false;
		
		unknownError = false;
	}
	
	public boolean isValid() {
		
		boolean valid = false;
		
		if(earlyGames) {
			if(!playedSelections) {
				valid = true;
			}
		} else {
			if(!selectionFileMissing && ffCheckOk && fwdCheckOk && rckCheckOk && midCheckOk && fbCheckOk && defCheckOk && benchCheckOk && teamPlayerCheckOk && !unknownError && !lockedOut && !roundCompleted) {
				valid = true;
			}
		}
		
		return valid;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public String getTeamCode() {
		return teamCode;
	}

	public void setTeamCode(String teamCode) {
		this.teamCode = teamCode;
	}

	public Map<String, List<Integer>> getInsAndOuts() {
		return insAndOuts;
	}

	public void setInsAndOuts(Map<String, List<Integer>> insAndOuts) {
		this.insAndOuts = insAndOuts;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}
}
