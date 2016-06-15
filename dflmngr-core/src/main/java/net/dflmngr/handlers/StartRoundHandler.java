package net.dflmngr.handlers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dflmngr.jndi.JndiProvider;
import net.dflmngr.logging.LoggingUtils;
import net.dflmngr.model.DomainDecodes;
import net.dflmngr.model.entity.DflEarlyInsAndOuts;
import net.dflmngr.model.entity.DflRoundEarlyGames;
import net.dflmngr.model.entity.DflRoundInfo;
import net.dflmngr.model.entity.DflSelectedPlayer;
import net.dflmngr.model.entity.DflTeam;
import net.dflmngr.model.entity.DflTeamPlayer;
import net.dflmngr.model.entity.InsAndOuts;
import net.dflmngr.model.service.DflEarlyInsAndOutsService;
import net.dflmngr.model.service.DflRoundInfoService;
import net.dflmngr.model.service.DflSelectedTeamService;
import net.dflmngr.model.service.DflTeamPlayerService;
import net.dflmngr.model.service.DflTeamService;
import net.dflmngr.model.service.GlobalsService;
import net.dflmngr.model.service.InsAndOutsService;
import net.dflmngr.model.service.impl.DflEarlyInsAndOutsServiceImpl;
import net.dflmngr.model.service.impl.DflRoundInfoServiceImpl;
import net.dflmngr.model.service.impl.DflSelectedTeamServiceImpl;
import net.dflmngr.model.service.impl.DflTeamPlayerServiceImpl;
import net.dflmngr.model.service.impl.DflTeamServiceImpl;
import net.dflmngr.model.service.impl.GlobalsServiceImpl;
import net.dflmngr.model.service.impl.InsAndOutsServiceImpl;
import net.dflmngr.reports.InsAndOutsReport;
import net.dflmngr.utils.EmailUtils;
import net.dflmngr.validation.SelectedTeamValidation;

public class StartRoundHandler {
	private LoggingUtils loggerUtils;
	
	boolean isExecutable;
	
	String defaultMdcKey = "batch.name";
	String defaultLoggerName = "batch-logger";
	String defaultLogfile = "StartRound";
	
	String mdcKey;
	String loggerName;
	String logfile;
	
	DflTeamService dflTeamService;
	DflSelectedTeamService dflSelectedTeamService;
	InsAndOutsService insAndOutsService;
	DflTeamPlayerService dflTeamPlayerService;
	DflEarlyInsAndOutsService dflEarlyInsAndOutsService;
	DflRoundInfoService dflRoundInfoService;
	GlobalsService globalsService;
	
	public StartRoundHandler() {
		dflTeamService = new DflTeamServiceImpl();
		dflSelectedTeamService = new DflSelectedTeamServiceImpl();
		insAndOutsService = new InsAndOutsServiceImpl();
		dflTeamPlayerService = new DflTeamPlayerServiceImpl();
		dflEarlyInsAndOutsService = new DflEarlyInsAndOutsServiceImpl();
		dflRoundInfoService = new DflRoundInfoServiceImpl();
		globalsService = new GlobalsServiceImpl();
	}
	
	public void configureLogging(String mdcKey, String loggerName, String logfile) {
		loggerUtils = new LoggingUtils(loggerName, mdcKey, logfile);
		this.mdcKey = mdcKey;
		this.loggerName = loggerName;
		this.logfile = logfile;
		isExecutable = true;
	}
	
	public void execute(int round, String emailOveride) {
		
		try{
			if(!isExecutable) {
				configureLogging(defaultMdcKey, defaultLoggerName, defaultLogfile);
				loggerUtils.log("info", "Default logging configured");
			}

			boolean earlyGamesExist = false;
			boolean earlyGamesCompleted = false;
			
			DflRoundInfo dflRoundInfo = dflRoundInfoService.get(round);
			Date dummyReceivedDate = new Date(dflRoundInfo.getHardLockoutTime().getTime() - 600000);
			
			Date now = new Date();
			Calendar nowCal = Calendar.getInstance();
			nowCal.setTime(now);
			
			List<DflRoundEarlyGames> earlyGames = dflRoundInfo.getEarlyGames();
			
			if(earlyGames != null && dflRoundInfo.getEarlyGames().size() > 0) {
				loggerUtils.log("info", "Early Games exist, checking if completed");
				earlyGamesExist = true;
				int completedCount = 0;
				for(DflRoundEarlyGames earlyGame : earlyGames) {
					Calendar startCal = Calendar.getInstance();
					startCal.setTime(earlyGame.getStartTime());
					startCal.add(Calendar.HOUR_OF_DAY, 3);
					
					if(nowCal.after(startCal)) {
						completedCount++;
					}
					
				}
				
				if(completedCount == earlyGames.size()) {
					earlyGamesCompleted = true;
				}
			}
			
			if(earlyGamesExist && !earlyGamesCompleted) {
				loggerUtils.log("info", "Executing start round for round={} with earlyGameStart",  round);
			} else {
				loggerUtils.log("info", "Executing start round for round={}",  round);
			}
			
			createTeamSelections(round, earlyGamesExist, earlyGamesCompleted, dummyReceivedDate);
			
			loggerUtils.log("info", "Creating predictions");
			PredictionHandler predictions = new PredictionHandler();
			predictions.configureLogging(mdcKey, loggerName, logfile);
			predictions.execute(round);
			
			if(!earlyGamesExist || earlyGamesCompleted) {
				loggerUtils.log("info", "Creating insAndOuts Report");
				
				InsAndOutsReport insAndOutsReport = new InsAndOutsReport();
				insAndOutsReport.configureLogging(mdcKey, loggerName, logfile);
				insAndOutsReport.execute(round, "Full", emailOveride);
			}
			
			if(earlyGamesExist && !earlyGamesCompleted) {
				loggerUtils.log("info", "Early game start round completed");
			} else {
				loggerUtils.log("info", "Start round completed");
			}
			
			dflTeamService.close();
			dflSelectedTeamService.close();
			insAndOutsService.close();
			dflTeamPlayerService.close();
			dflEarlyInsAndOutsService.close();
			dflRoundInfoService.close();
			globalsService.close();
		
		} catch (Exception ex) {
			loggerUtils.log("error", "Error in ... ", ex);
		}
		
	}
	
	private void createTeamSelections(int round, boolean earlyGamesExist, boolean earlyGamesCompleted, Date dummyReceivedDate) throws Exception {

		loggerUtils.log("info", "Creating team selections");
		
		List<DflTeam> teams = dflTeamService.findAll();
		
		for(DflTeam team : teams) {
			loggerUtils.log("info", "Working with team={}", team.getTeamCode());
			
			List<DflSelectedPlayer> tmpSelectedTeam = null;
			
			if(round == 1) {
				tmpSelectedTeam = new ArrayList<>();
				loggerUtils.log("info", "Round 1: no previous team");
			} else {
				tmpSelectedTeam = dflSelectedTeamService.getSelectedTeamForRound(round-1, team.getTeamCode());
				loggerUtils.log("info", "Not round 1: previous team: {}", tmpSelectedTeam);
			}
						
			List<InsAndOuts> insAndOuts = insAndOutsService.getByTeamAndRound(round, team.getTeamCode());
			
			if((earlyGamesExist && earlyGamesCompleted) && (insAndOuts == null || insAndOuts.size() == 0)) {
				List<DflEarlyInsAndOuts> earlyInsAndOuts = dflEarlyInsAndOutsService.getByTeamAndRound(round, team.getTeamCode());
				if(earlyInsAndOuts != null && earlyInsAndOuts.size() > 0) {
					loggerUtils.log("info", "Early Ins and Outs, starting to validate");
					
					Map<String, List<Integer>> validationInsAndOuts = new HashMap<>();
					List<Integer> ins = new ArrayList<>();
					List<Integer> outs = new ArrayList<>();
					for(DflEarlyInsAndOuts inOrOut : earlyInsAndOuts) {
						if(inOrOut.getInOrOut().equals("I")) {
							ins.add(inOrOut.getTeamPlayerId());
						} else {
							outs.add(inOrOut.getTeamPlayerId());
						}
					}
					validationInsAndOuts.put("in",ins);
					validationInsAndOuts.put("out",outs);
					
					SelectedTeamValidationHandler validationHandler = new SelectedTeamValidationHandler();
					validationHandler.configureLogging(mdcKey, loggerName, logfile);
					SelectedTeamValidation validationResult = validationHandler.execute(round, team.getTeamCode(), validationInsAndOuts, dummyReceivedDate, true);
					
					if(validationResult.isValid()) {
						loggerUtils.log("info", "Early Ins and Outs are valid and are being used");
						
						for(DflEarlyInsAndOuts inOrOut : earlyInsAndOuts) {
							if(inOrOut.getInOrOut().equals(DomainDecodes.INS_AND_OUTS.IN_OR_OUT.IN)) {
								DflTeamPlayer teamPlayer = dflTeamPlayerService.getTeamPlayerForTeam(team.getTeamCode(), inOrOut.getTeamPlayerId());
								
								DflSelectedPlayer selectedPlayer = new DflSelectedPlayer();
								selectedPlayer.setPlayerId(teamPlayer.getPlayerId());
								selectedPlayer.setRound(round);
								selectedPlayer.setTeamCode(team.getTeamCode());
								selectedPlayer.setTeamPlayerId(inOrOut.getTeamPlayerId());
								
								loggerUtils.log("info", "Adding player to selected team: player={}", selectedPlayer);
								tmpSelectedTeam.add(selectedPlayer);
							} else {
								DflSelectedPlayer droppedPlayer = null;
								for(DflSelectedPlayer selectedPlayer : tmpSelectedTeam) {
									if(inOrOut.getTeamPlayerId() == selectedPlayer.getTeamPlayerId()) {
										droppedPlayer = selectedPlayer;
										loggerUtils.log("info", "Dropping player from selected team: player={}", droppedPlayer);
										break;
									}
								}
								tmpSelectedTeam.remove(droppedPlayer);
							}
						}
					} else {
						loggerUtils.log("info", "Early Ins and Outs are invalid");
						emailValidationError(round, team, validationResult);
					}
				}
			} else {
				if(insAndOuts != null && insAndOuts.size() > 0) {
					for(InsAndOuts inOrOut : insAndOuts) {
						if(inOrOut.getInOrOut().equals(DomainDecodes.INS_AND_OUTS.IN_OR_OUT.IN)) {
							DflTeamPlayer teamPlayer = dflTeamPlayerService.getTeamPlayerForTeam(team.getTeamCode(), inOrOut.getTeamPlayerId());
							
							DflSelectedPlayer selectedPlayer = new DflSelectedPlayer();
							selectedPlayer.setPlayerId(teamPlayer.getPlayerId());
							selectedPlayer.setRound(round);
							selectedPlayer.setTeamCode(team.getTeamCode());
							selectedPlayer.setTeamPlayerId(inOrOut.getTeamPlayerId());
							
							loggerUtils.log("info", "Adding player to selected team: player={}", selectedPlayer);
							tmpSelectedTeam.add(selectedPlayer);
						} else {
							DflSelectedPlayer droppedPlayer = null;
							for(DflSelectedPlayer selectedPlayer : tmpSelectedTeam) {
								if(inOrOut.getTeamPlayerId() == selectedPlayer.getTeamPlayerId()) {
									droppedPlayer = selectedPlayer;
									loggerUtils.log("info", "Dropping player from selected team: player={}", droppedPlayer);
									break;
								}
							}
							tmpSelectedTeam.remove(droppedPlayer);
						}
					}
				}
			}
		
			List<DflSelectedPlayer> selectedTeam = new ArrayList<>();
			
			if(round > 1) {
				for(DflSelectedPlayer tmpSelectedPlayer : tmpSelectedTeam) {
					DflSelectedPlayer selectedPlayer = new DflSelectedPlayer();
					selectedPlayer.setPlayerId(tmpSelectedPlayer.getPlayerId());
					selectedPlayer.setRound(round);
					selectedPlayer.setTeamCode(team.getTeamCode());
					selectedPlayer.setTeamPlayerId(tmpSelectedPlayer.getTeamPlayerId());
					
					selectedTeam.add(selectedPlayer);
				}
			}
									
			loggerUtils.log("info", "Saving selected to DB: selected tema={}", selectedTeam);
			dflSelectedTeamService.replaceTeamForRound(round, team.getTeamCode(), selectedTeam);
		}
	}
	
	/*
	private void createTeamSelections(int round, boolean earlyGameStart) throws Exception {
		
		loggerUtils.log("info", "Creating team selections");
		
		List<DflTeam> teams = dflTeamService.findAll();
		DflRoundInfo roundInfo = dflRoundInfoService.get(round);
		
		for(DflTeam team : teams) {
			loggerUtils.log("info", "Working with team={}", team.getTeamCode());
			
			List<DflSelectedPlayer> selectedTeam = new ArrayList<>();
			
			List<InsAndOuts> insAndOuts = insAndOutsService.getByTeamAndRound(round, team.getTeamCode());
			
			if(round == 1) {
				loggerUtils.log("info", "Round 1, only ins");
				
				if(insAndOuts != null && insAndOuts.size() > 0) {
					for(InsAndOuts in : insAndOuts) {
						
						DflTeamPlayer teamPlayer = dflTeamPlayerService.getTeamPlayerForTeam(team.getTeamCode(), in.getTeamPlayerId());
						
						DflSelectedPlayer selectedPlayer = new DflSelectedPlayer();
						selectedPlayer.setPlayerId(teamPlayer.getPlayerId());
						selectedPlayer.setRound(round);
						selectedPlayer.setTeamCode(team.getTeamCode());
						selectedPlayer.setTeamPlayerId(in.getTeamPlayerId());
						
						loggerUtils.log("info", "Adding player to selected team: player={}", selectedPlayer);
						selectedTeam.add(selectedPlayer);
					}
				} else {
					if(!earlyGameStart) {
						List<DflEarlyInsAndOuts> earlyInsAndOuts = dflEarlyInsAndOutsService.getByTeamAndRound(round, team.getTeamCode());
						if(earlyInsAndOuts != null && earlyInsAndOuts.size() > 0) {
							loggerUtils.log("info", "Early Ins and Outs, starting to validate");
							
							Map<String, List<Integer>> validationInsAndOuts = new HashMap<>();
							List<Integer> ins = new ArrayList<>();
							List<Integer> outs = new ArrayList<>();
							for(DflEarlyInsAndOuts inOrOut : earlyInsAndOuts) {
								if(inOrOut.getInOrOut().equals("I")) {
									ins.add(inOrOut.getTeamPlayerId());
								} else {
									outs.add(inOrOut.getTeamPlayerId());
								}
							}
							validationInsAndOuts.put("in",ins);
							validationInsAndOuts.put("out",outs);
							
							Date dummyReceivedDate = new Date(roundInfo.getHardLockoutTime().getTime() - 600000);
							
							SelectedTeamValidationHandler validationHandler = new SelectedTeamValidationHandler();
							validationHandler.configureLogging(mdcKey, loggerName, logfile);
							SelectedTeamValidation validationResult = validationHandler.execute(round, team.getTeamCode(), validationInsAndOuts, dummyReceivedDate, true);
							
							if(validationResult.isValid()) {
								loggerUtils.log("info", "Early Ins and Outs are valid and are being used");
								
								for(DflEarlyInsAndOuts in : earlyInsAndOuts) {
									
									DflTeamPlayer teamPlayer = dflTeamPlayerService.getTeamPlayerForTeam(team.getTeamCode(), in.getTeamPlayerId());
									
									DflSelectedPlayer selectedPlayer = new DflSelectedPlayer();
									selectedPlayer.setPlayerId(teamPlayer.getPlayerId());
									selectedPlayer.setRound(round);
									selectedPlayer.setTeamCode(team.getTeamCode());
									selectedPlayer.setTeamPlayerId(in.getTeamPlayerId());
									
									loggerUtils.log("info", "Adding player to selected team: player={}", selectedPlayer);
									selectedTeam.add(selectedPlayer);
								}
							} else {
								loggerUtils.log("info", "Early Ins and Outs are invalid");
								emailValidationError(round, team, validationResult);
							}
						}
					}
				}
			} else {
				List<DflSelectedPlayer> prevSelectedTeam = dflSelectedTeamService.getSelectedTeamForRound(round-1, team.getTeamCode());
				loggerUtils.log("info", "Not round 1: previous team: {}", prevSelectedTeam);
				
				if(insAndOuts != null && insAndOuts.size() > 0) {
					for(InsAndOuts inOrOut : insAndOuts) {
						if(inOrOut.getInOrOut().equals(DomainDecodes.INS_AND_OUTS.IN_OR_OUT.IN)) {
							DflTeamPlayer teamPlayer = dflTeamPlayerService.getTeamPlayerForTeam(team.getTeamCode(), inOrOut.getTeamPlayerId());
							
							DflSelectedPlayer selectedPlayer = new DflSelectedPlayer();
							selectedPlayer.setPlayerId(teamPlayer.getPlayerId());
							selectedPlayer.setRound(round);
							selectedPlayer.setTeamCode(team.getTeamCode());
							selectedPlayer.setTeamPlayerId(inOrOut.getTeamPlayerId());
							
							loggerUtils.log("info", "Adding player to selected team: player={}", selectedPlayer);
							prevSelectedTeam.add(selectedPlayer);
						} else {
							DflSelectedPlayer droppedPlayer = null;
							for(DflSelectedPlayer selectedPlayer : prevSelectedTeam) {
								if(inOrOut.getTeamPlayerId() == selectedPlayer.getTeamPlayerId()) {
									droppedPlayer = selectedPlayer;
									loggerUtils.log("info", "Dropping player from selected team: player={}", droppedPlayer);
									break;
								}
							}
							prevSelectedTeam.remove(droppedPlayer);
						}
					}
					
					for(DflSelectedPlayer prevSelectedPlayer : prevSelectedTeam) {
						DflSelectedPlayer selectedPlayer = new DflSelectedPlayer();
						selectedPlayer.setPlayerId(prevSelectedPlayer.getPlayerId());
						selectedPlayer.setRound(round);
						selectedPlayer.setTeamCode(team.getTeamCode());
						selectedPlayer.setTeamPlayerId(prevSelectedPlayer.getTeamPlayerId());
						
						selectedTeam.add(selectedPlayer);
					}
				} else {
					if(!earlyGameStart) {
						List<DflEarlyInsAndOuts> earlyInsAndOuts = dflEarlyInsAndOutsService.getByTeamAndRound(round, team.getTeamCode());
						if(earlyInsAndOuts != null && earlyInsAndOuts.size() > 0) {
							loggerUtils.log("info", "Early Ins and Outs, starting to validate");
							
							Map<String, List<Integer>> validationInsAndOuts = new HashMap<>();
							List<Integer> ins = new ArrayList<>();
							List<Integer> outs = new ArrayList<>();
							for(DflEarlyInsAndOuts inOrOut : earlyInsAndOuts) {
								if(inOrOut.getInOrOut().equals("I")) {
									ins.add(inOrOut.getTeamPlayerId());
								} else {
									outs.add(inOrOut.getTeamPlayerId());
								}
							}
							validationInsAndOuts.put("in",ins);
							validationInsAndOuts.put("out",outs);
							
							Date dummyReceivedDate = new Date(roundInfo.getHardLockoutTime().getTime() - 600000);
							
							SelectedTeamValidationHandler validationHandler = new SelectedTeamValidationHandler();
							validationHandler.configureLogging(mdcKey, loggerName, logfile);
							SelectedTeamValidation validationResult = validationHandler.execute(round, team.getTeamCode(), validationInsAndOuts, dummyReceivedDate, true);
							
							if(validationResult.isValid()) {
								loggerUtils.log("info", "Early Ins and Outs are valid and are being used");
								
								for(DflEarlyInsAndOuts in : earlyInsAndOuts) {
									
									DflTeamPlayer teamPlayer = dflTeamPlayerService.getTeamPlayerForTeam(team.getTeamCode(), in.getTeamPlayerId());
									
									DflSelectedPlayer selectedPlayer = new DflSelectedPlayer();
									selectedPlayer.setPlayerId(teamPlayer.getPlayerId());
									selectedPlayer.setRound(round);
									selectedPlayer.setTeamCode(team.getTeamCode());
									selectedPlayer.setTeamPlayerId(in.getTeamPlayerId());
									
									loggerUtils.log("info", "Adding player to selected team: player={}", selectedPlayer);
									selectedTeam.add(selectedPlayer);
								}
							} else {
								loggerUtils.log("info", "Early Ins and Outs are invalid");
								emailValidationError(round, team, validationResult);
							}
						}
					}
				}
			}
		
			loggerUtils.log("info", "Saving selected to DB: selected tema={}", selectedTeam);
			dflSelectedTeamService.replaceTeamForRound(round, team.getTeamCode(), selectedTeam);			
		}	
	}
	*/
	
	private void emailValidationError(int round, DflTeam team, SelectedTeamValidation validationResult) throws Exception {
		
		String dflMngrEmail = globalsService.getEmailConfig().get("dflmngrEmailAddr");
		
		String subject = "Team Validation FAILURE";
		
		String messageBody = "Coach,\n\n" +
				 		     "Your selections were entered before some early games and were not fully validated.  The full validation has failed .... The reasons for this are:\n";

		if(validationResult.selectionFileMissing) {
			messageBody = messageBody + "\t- You sent the email with no selections.txt\n";
		} else if(validationResult.roundCompleted) {
			messageBody = messageBody + "\t- The round you have in your selections.txt has past\n";
		} else if(validationResult.lockedOut) {
			messageBody = messageBody + "\t- The round you have in your selections.txt is in progress and doesn't allow more selections\n";
		} else if(validationResult.unknownError) {
			messageBody = messageBody + "\t- Some exception occured follow up with email to xdfl google group.\n";
		} else if(!validationResult.teamPlayerCheckOk) {
			messageBody = messageBody + "\t- The ins and/or outs numbers sent are not correct\n";
		} else {
			if(!validationResult.ffCheckOk) {
				messageBody = messageBody + "\t- You have too many Full Forwards\n";
			}
			if(!validationResult.fwdCheckOk) {
				messageBody = messageBody + "\t- You have too many Forwards\n";
			}
			if(!validationResult.rckCheckOk) {
				messageBody = messageBody + "\t- You have too many Rucks\n";
			}
			if(!validationResult.midCheckOk) {
				messageBody = messageBody + "\t- You have too many Midfielders\n";
			}
			if(!validationResult.fbCheckOk) {
				messageBody = messageBody + "\t- You have too many Full Backs\n";
			}
			if(!validationResult.defCheckOk) {
				messageBody = messageBody + "\t- You have too many Defenders\n";
			}
			if(!validationResult.benchCheckOk) {
				messageBody = messageBody + "\t- You have too many on the bench.\n";
			}
		}
		
		messageBody = messageBody + "\nThere is nothing you can do from here, but if you belive this is an error please send an email to the google group.\n\n" +
				      "DFL Manager Admin";
		
		List<String> to = new ArrayList<>();
		to.add(team.getCoachEmail());
				
		loggerUtils.log("info", "Emailing validation errors to={}; validationResult={}", to, validationResult);
		EmailUtils.sendTextEmail(to, dflMngrEmail, subject, messageBody, null);
	}
	
	public static void main(String[] args) {
		
		try {
			String email = null;
			int round = 0;
			
			if(args.length > 2 || args.length < 1) {
				System.out.println("usage: RawStatsReport <round> optional [<email>]");
			} else {
				
				round = Integer.parseInt(args[0]);
				
				if(args.length == 2) {
					email = args[1];
				}
				
				JndiProvider.bind();
				
				StartRoundHandler startRound = new StartRoundHandler();
				startRound.configureLogging("batch.name", "batch-logger", "StartRound");
				startRound.execute(round, email);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
