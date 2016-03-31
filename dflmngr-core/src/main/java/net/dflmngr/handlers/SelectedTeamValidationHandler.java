package net.dflmngr.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.dflmngr.logging.LoggingUtils;
import net.dflmngr.model.entity.DflPlayer;
import net.dflmngr.model.entity.DflSelectedPlayer;
import net.dflmngr.model.entity.DflTeamPlayer;
import net.dflmngr.model.service.DflPlayerService;
import net.dflmngr.model.service.DflSelectedTeamService;
import net.dflmngr.model.service.DflTeamPlayerService;
import net.dflmngr.model.service.impl.DflPlayerServiceImpl;
import net.dflmngr.model.service.impl.DflSelectedTeamServiceImpl;
import net.dflmngr.model.service.impl.DflTeamPlayerServiceImpl;
import net.dflmngr.validation.SelectedTeamValidation;

public class SelectedTeamValidationHandler {
	private LoggingUtils loggerUtils;
		
	boolean isExecutable;
		
	String defaultMdcKey = "batch.name";
	String defaultLoggerName = "batch-logger";
	String defaultLogfile = "SelectedTeamValidationHandler";
	
	private DflSelectedTeamService dflSelectedTeamService;
	private DflTeamPlayerService dflTeamPlayerService;
	private DflPlayerService dflPlayerService;
			
	public SelectedTeamValidationHandler() {		
		dflSelectedTeamService = new DflSelectedTeamServiceImpl();
		dflTeamPlayerService = new DflTeamPlayerServiceImpl();
		dflPlayerService = new DflPlayerServiceImpl();
		
		isExecutable = false;
	}
	
	public void configureLogging(String mdcKey, String loggerName, String logfile) {
		loggerUtils = new LoggingUtils(loggerName, mdcKey, logfile);
		isExecutable = true;
	}
	
	public SelectedTeamValidation execute(int round, String teamCode, Map<String, List<Integer>> insAndOuts) {
		
		SelectedTeamValidation validationResult = null;
		
		try {
			if(!isExecutable) {
				configureLogging(defaultMdcKey, defaultLoggerName, defaultLogfile);
				loggerUtils.log("info", "Default logging configured");
			}
			
			List<DflSelectedPlayer> selectedTeam;
			
			if(round == 1) {
				List<Integer> ins = insAndOuts.get("in");
				
				selectedTeam = new ArrayList<>();
				
				for(int in : ins) {
					if(in < 1 || in > 45) {
						validationResult = new SelectedTeamValidation();
						validationResult.selectionFileMissing = false;
						break;
					} else {
						DflSelectedPlayer selectedPlayer = new DflSelectedPlayer();
						
						selectedPlayer.setRound(round);
						selectedPlayer.setTeamCode(teamCode);
						selectedPlayer.setTeamPlayerId(in);
						
						selectedTeam.add(selectedPlayer);
					}
				}
			} else {
				selectedTeam = dflSelectedTeamService.getSelectedTeamForRound(round-1, teamCode);
				
				List<Integer> ins = insAndOuts.get("in");
				List<Integer> outs = insAndOuts.get("out");
				
				for(int in : ins) {
					if(in < 1 || in > 45) {
						validationResult = new SelectedTeamValidation();
						validationResult.selectionFileMissing = false;
						break;
					} else {
						DflSelectedPlayer selectedPlayer = new DflSelectedPlayer();
						
						selectedPlayer.setRound(round);
						selectedPlayer.setTeamCode(teamCode);
						selectedPlayer.setTeamPlayerId(in);
						
						selectedTeam.add(selectedPlayer);
					}
				}
				
				List<DflSelectedPlayer> playersToRemove = new ArrayList<>();
				
				for(int out : outs) {
					if(out < 1 || out > 45) {
						validationResult = new SelectedTeamValidation();
						validationResult.selectionFileMissing = false;
						break;
					} else {
						for(DflSelectedPlayer selectedPlayer : selectedTeam) {
							if(selectedPlayer.getTeamPlayerId() == out) {
								playersToRemove.add(selectedPlayer);
							}
						}
					}
				}
				
				selectedTeam.removeAll(playersToRemove);
			}
			
			if(validationResult == null) {
				validationResult = validateTeam(teamCode, selectedTeam);
			}
			
			validationResult.setRound(round);
			validationResult.setTeamCode(teamCode);
			validationResult.setInsAndOuts(insAndOuts);
			
			dflSelectedTeamService.close();
			dflTeamPlayerService.close();
			dflPlayerService.close();
			
		} catch (Exception ex) {
			loggerUtils.log("error", "Error in ... ", ex);
		}
		
		return validationResult;
	}
	
	private SelectedTeamValidation validateTeam(String teamCode, List<DflSelectedPlayer> selectedTeam) {
		
		SelectedTeamValidation validationResult = new SelectedTeamValidation();
		
		validationResult.selectionFileMissing = false;
		validationResult.teamPlayerCheckOk = true;
		
		int ffCount = 0;
		int fwdCount = 0;
		int midCount = 0;
		int defCount = 0;
		int fbCount = 0;
		int rckCount = 0;
		int benchCount = 0;
		
		for(DflSelectedPlayer selectedPlayer : selectedTeam) {
			DflTeamPlayer teamPlayer = dflTeamPlayerService.getTeamPlayerForTeam(selectedPlayer.getTeamCode(), selectedPlayer.getTeamPlayerId());
			DflPlayer player = dflPlayerService.get(teamPlayer.getPlayerId());		

			String position = player.getPosition().toLowerCase();
			
			switch(position) {
				case "ff" :
					ffCount++;
					break;
				case "fwd" :
					fwdCount++;
					break;
				case "rck" :
					rckCount++;
					break;
				case "mid" :
					midCount++;
					break;
				case "def" :
					defCount++;
					break;
				case "fb" :
					fbCount++;
					break;
			}
		}
			
		if(ffCount <= 2) {
			validationResult.ffCheckOk = true;
		}
		if(fwdCount <= 6) {
			validationResult.fwdCheckOk = true;
		}
		if(midCount <= 6) {
			validationResult.midCheckOk = true;
		}
		if(defCount <= 6) {
			validationResult.defCheckOk = true;
		}
		if(fbCount <= 2) {
			validationResult.fbCheckOk = true;
		}
		if(rckCount <= 2) {
			validationResult.rckCheckOk = true;
		}
		
		if(ffCount == 2) {
			benchCount++;
		}
		if(fwdCount == 6) {
			benchCount++;
		}
		if(midCount == 6) {
			benchCount++;
		}
		if(defCount == 6) {
			benchCount++;;
		}
		if(fbCount == 2) {
			benchCount++;
		}
		if(rckCount == 2) {
			benchCount++;
		}
		
		if(benchCount <= 4) {
			validationResult.benchCheckOk = true;
		}
			
		return validationResult;
	}
}
