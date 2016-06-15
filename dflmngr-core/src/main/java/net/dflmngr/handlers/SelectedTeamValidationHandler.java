package net.dflmngr.handlers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.dflmngr.logging.LoggingUtils;
import net.dflmngr.model.entity.AflFixture;
import net.dflmngr.model.entity.DflEarlyInsAndOuts;
import net.dflmngr.model.entity.DflPlayer;
import net.dflmngr.model.entity.DflRoundEarlyGames;
import net.dflmngr.model.entity.DflRoundInfo;
import net.dflmngr.model.entity.DflSelectedPlayer;
import net.dflmngr.model.entity.DflTeamPlayer;
import net.dflmngr.model.entity.keys.AflFixturePK;
import net.dflmngr.model.service.AflFixtureService;
import net.dflmngr.model.service.DflEarlyInsAndOutsService;
import net.dflmngr.model.service.DflPlayerService;
import net.dflmngr.model.service.DflRoundInfoService;
import net.dflmngr.model.service.DflSelectedTeamService;
import net.dflmngr.model.service.DflTeamPlayerService;
import net.dflmngr.model.service.GlobalsService;
import net.dflmngr.model.service.impl.AflFixtureServiceImpl;
import net.dflmngr.model.service.impl.DflEarlyInsAndOutsServiceImpl;
import net.dflmngr.model.service.impl.DflPlayerServiceImpl;
import net.dflmngr.model.service.impl.DflRoundInfoServiceImpl;
import net.dflmngr.model.service.impl.DflSelectedTeamServiceImpl;
import net.dflmngr.model.service.impl.DflTeamPlayerServiceImpl;
import net.dflmngr.model.service.impl.GlobalsServiceImpl;
import net.dflmngr.utils.DflmngrUtils;
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
	private GlobalsService globalsService;
	private DflRoundInfoService dflRoundInfoService;
	private DflEarlyInsAndOutsService dflEarlyInsAndOutsService;
	private AflFixtureService aflFixtureService;
			
	public SelectedTeamValidationHandler() {		
		dflSelectedTeamService = new DflSelectedTeamServiceImpl();
		dflTeamPlayerService = new DflTeamPlayerServiceImpl();
		dflPlayerService = new DflPlayerServiceImpl();
		globalsService = new GlobalsServiceImpl();
		dflRoundInfoService = new DflRoundInfoServiceImpl();
		dflEarlyInsAndOutsService = new DflEarlyInsAndOutsServiceImpl();
		aflFixtureService = new AflFixtureServiceImpl();
		isExecutable = false;
	}
	
	public void configureLogging(String mdcKey, String loggerName, String logfile) {
		loggerUtils = new LoggingUtils(loggerName, mdcKey, logfile);
		isExecutable = true;
	}
	
	public SelectedTeamValidation execute(int round, String teamCode, Map<String, List<Integer>> insAndOuts, Date receivedDate, boolean skipEarlyGames) {
		
		SelectedTeamValidation validationResult = null;
		
		try {
			if(!isExecutable) {
				configureLogging(defaultMdcKey, defaultLoggerName, defaultLogfile);
				loggerUtils.log("info", "Default logging configured");
			}
			
			loggerUtils.log("info", "Validating selectionds for teamCode={}; round={};", teamCode, round);
			
			int currentRound = Integer.parseInt(globalsService.getCurrentRound());
			DflRoundInfo roundInfo = dflRoundInfoService.get(round);
			Date lockoutTime = roundInfo.getHardLockoutTime();
			
			boolean earlyGamesCompleted = false;
			boolean playedSelections = false;
			
			if(!skipEarlyGames && (roundInfo.getEarlyGames() != null && roundInfo.getEarlyGames().size() > 0)) {
				loggerUtils.log("info", "Round has early games, doing early game validation");
				List<DflRoundEarlyGames> earlyGames = roundInfo.getEarlyGames();
				int completedCount = 0;
				for(DflRoundEarlyGames earlyGame : earlyGames) {
					if(receivedDate.after(earlyGame.getStartTime())) {
						completedCount++;
					}
				}
				if(completedCount == earlyGames.size()) {
					earlyGamesCompleted = true;
				}
				
				List<DflEarlyInsAndOuts> earlyInsAndOuts = dflEarlyInsAndOutsService.getByTeamAndRound(round, teamCode);
				
				playedSelections = checkForPlayedSelections(teamCode, receivedDate, roundInfo, insAndOuts, earlyInsAndOuts);
				
				if(playedSelections) {
					loggerUtils.log("info", "Early game validation failed");
					validationResult = new SelectedTeamValidation();
					validationResult.earlyGames = true;
					validationResult.playedSelections = true;
				} else {
					validationResult = standardValidation(round, currentRound, teamCode, insAndOuts, receivedDate, lockoutTime);
					
					if(!earlyGamesCompleted) {
						loggerUtils.log("info", "Early games not completed, all validation errors will only be warnings.");
						validationResult.earlyGames = true;
						validationResult.playedSelections = false;
					}
				}
			} else {
				validationResult = standardValidation(round, currentRound, teamCode, insAndOuts, receivedDate, lockoutTime);
			}
						
			validationResult.setRound(round);
			validationResult.setTeamCode(teamCode);
			validationResult.setInsAndOuts(insAndOuts);
			
			loggerUtils.log("info", "Validation result={}", validationResult);
			
			dflSelectedTeamService.close();
			dflTeamPlayerService.close();
			dflPlayerService.close();
			globalsService.close();
			dflRoundInfoService.close();
			dflEarlyInsAndOutsService.close();
			aflFixtureService.close();
			
		} catch (Exception ex) {
			loggerUtils.log("error", "Error in ... ", ex);
		}
		
		return validationResult;
	}
	
	private boolean checkForPlayedSelections(String teamCode, Date receivedDate, DflRoundInfo roundInfo, Map<String, List<Integer>> insAndOuts, List<DflEarlyInsAndOuts> earlyInsAndOuts) {
		boolean playedSelections = false;
		
		List<DflRoundEarlyGames> earlyGames = roundInfo.getEarlyGames();
		
		for(DflRoundEarlyGames earlyGame : earlyGames) {
			if(receivedDate.after(earlyGame.getStartTime())) {
				List<Integer> ins = insAndOuts.get("in");
				List<Integer> outs = insAndOuts.get("out");
				
				int aflRound = earlyGame.getAflRound();
				int aflGame = earlyGame.getAflGame();
				
				AflFixturePK aflFixturePK = new AflFixturePK();
				aflFixturePK.setRound(aflRound);
				aflFixturePK.setGame(aflGame);
				AflFixture aflFixture = aflFixtureService.get(aflFixturePK);
				
				for(int in : ins) {
					DflTeamPlayer teamPlayer = dflTeamPlayerService.getTeamPlayerForTeam(teamCode, in);
					DflPlayer player = dflPlayerService.get(teamPlayer.getPlayerId());
					
					String mappedTeam = DflmngrUtils.dflAflTeamMap.get(player.getAflClub());
					
					if(mappedTeam.equals(aflFixture.getHomeTeam()) || mappedTeam.equals(aflFixture.getAwayTeam())) {
						boolean found = false;
						for(DflEarlyInsAndOuts earlyInOrOut : earlyInsAndOuts) {
							if(earlyInOrOut.getTeamPlayerId() == in && earlyInOrOut.getInOrOut().equals("I")) {
								found = true;
								break;
							}
						}
						if(!found) {
							loggerUtils.log("info", "Player selected has already played, teamPlayerId={}; teamCode={};", in, teamCode);
							playedSelections = true;
							break;
						}
					}
				}
				
				if(!playedSelections) {
					for(int out : outs) {
						DflTeamPlayer teamPlayer = dflTeamPlayerService.getTeamPlayerForTeam(teamCode, out);
						DflPlayer player = dflPlayerService.get(teamPlayer.getPlayerId());
						
						String mappedTeam = DflmngrUtils.dflAflTeamMap.get(player.getAflClub()); 
						
						if(mappedTeam.equals(aflFixture.getHomeTeam()) || mappedTeam.equals(aflFixture.getAwayTeam())) {
							boolean found = false;
							for(DflEarlyInsAndOuts earlyInOrOut : earlyInsAndOuts) {
								if(earlyInOrOut.getTeamPlayerId() == out && earlyInOrOut.getInOrOut().equals("I")) {
									found = true;
									break;
								}
							}
							if(!found) {
								loggerUtils.log("info", "Player dropped has already played, teamPlayerId={}; teamCode={};", out, teamCode);
								playedSelections = true;
								break;
							}
						}
					}
				}
			}
		}
		
		return playedSelections;
	}
	
	private SelectedTeamValidation standardValidation(int round, int currentRound, String teamCode, Map<String, List<Integer>> insAndOuts, Date receivedDate, Date lockoutTime) {
		
		SelectedTeamValidation validationResult = null;
		
		loggerUtils.log("info", "DFL round={}; Lockout time={};", currentRound, lockoutTime);
		
		List<DflSelectedPlayer> selectedTeam = null;
		
		if(round < currentRound) {
			validationResult = new SelectedTeamValidation();
			validationResult.selectionFileMissing = false;
			validationResult.roundCompleted = true;
			loggerUtils.log("info", "Team invalid round is completed");
		} else if(receivedDate.after(lockoutTime)) {
			validationResult = new SelectedTeamValidation();
			validationResult.selectionFileMissing = false;
			validationResult.roundCompleted = false;
			validationResult.lockedOut = true;
			loggerUtils.log("info", "Team invalid email recived after lockout, recived date={}", receivedDate);
		} else {
			
			if(round == 1) {
				loggerUtils.log("info", "Round 1 only ins.");
				List<Integer> ins = insAndOuts.get("in");
				
				selectedTeam = new ArrayList<>();
				
				for(int in : ins) {
					if(in < 1 || in > 45) {
						validationResult = new SelectedTeamValidation();
						validationResult.selectionFileMissing = false;
						validationResult.roundCompleted = false;
						validationResult.lockedOut = false;
						loggerUtils.log("info", "Selected player outside player range, teamPlayerId={}.", in);
						break;
					} else {
						DflSelectedPlayer selectedPlayer = new DflSelectedPlayer();
						
						selectedPlayer.setRound(round);
						selectedPlayer.setTeamCode(teamCode);
						selectedPlayer.setTeamPlayerId(in);
						
						selectedTeam.add(selectedPlayer);
						loggerUtils.log("info", "Added selectedPlayer={}.", selectedPlayer);
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
						loggerUtils.log("info", "Selected player outside player range, teamPlayerId={}.", in);
						break;
					} else {
						DflSelectedPlayer selectedPlayer = new DflSelectedPlayer();
						
						selectedPlayer.setRound(round);
						selectedPlayer.setTeamCode(teamCode);
						selectedPlayer.setTeamPlayerId(in);
						
						selectedTeam.add(selectedPlayer);
						loggerUtils.log("info", "Added selectedPlayer={}.", selectedPlayer);
					}
				}
				
				List<DflSelectedPlayer> playersToRemove = new ArrayList<>();
				
				for(int out : outs) {
					if(out < 1 || out > 45) {
						validationResult = new SelectedTeamValidation();
						validationResult.selectionFileMissing = false;
						loggerUtils.log("info", "Dropped player outside player range, teamPlayerId={}.", out);
						break;
					} else {
						for(DflSelectedPlayer selectedPlayer : selectedTeam) {
							if(selectedPlayer.getTeamPlayerId() == out) {
								playersToRemove.add(selectedPlayer);
								loggerUtils.log("info", "Removing selectedPlayer={}.", selectedPlayer);
							}
						}
					}
				}
				
				selectedTeam.removeAll(playersToRemove);
			}
		}
		
		if(validationResult == null) {
			loggerUtils.log("info", "Pre checks PASSED, validating selected team");
			validationResult = validateTeam(teamCode, selectedTeam);
		}
				
		return validationResult;
	}
	
	private SelectedTeamValidation validateTeam(String teamCode, List<DflSelectedPlayer> selectedTeam) {
		
		SelectedTeamValidation validationResult = new SelectedTeamValidation();
		
		validationResult.selectionFileMissing = false;
		validationResult.roundCompleted = false;
		validationResult.lockedOut = false;
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
		
		loggerUtils.log("info", "Position counts: ffCount={}; fwdCount={}; midCount={}; defCount={}; fbCount={}; rckCount={};",
						ffCount, fwdCount, midCount, defCount, fbCount, rckCount);
			
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
		
		loggerUtils.log("info", "Bench count={};", benchCount);
		
		if(benchCount <= 4) {
			validationResult.benchCheckOk = true;
		}
		
		return validationResult;
	}
}
