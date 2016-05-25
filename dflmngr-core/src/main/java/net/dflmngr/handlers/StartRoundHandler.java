package net.dflmngr.handlers;

import java.util.ArrayList;
import java.util.List;

import net.dflmngr.jndi.JndiProvider;
import net.dflmngr.logging.LoggingUtils;
import net.dflmngr.model.DomainDecodes;
import net.dflmngr.model.entity.DflSelectedPlayer;
import net.dflmngr.model.entity.DflTeam;
import net.dflmngr.model.entity.DflTeamPlayer;
import net.dflmngr.model.entity.InsAndOuts;
import net.dflmngr.model.service.DflSelectedTeamService;
import net.dflmngr.model.service.DflTeamPlayerService;
import net.dflmngr.model.service.DflTeamService;
import net.dflmngr.model.service.InsAndOutsService;
import net.dflmngr.model.service.impl.DflSelectedTeamServiceImpl;
import net.dflmngr.model.service.impl.DflTeamPlayerServiceImpl;
import net.dflmngr.model.service.impl.DflTeamServiceImpl;
import net.dflmngr.model.service.impl.InsAndOutsServiceImpl;
import net.dflmngr.reports.InsAndOutsReport;

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
	
	public StartRoundHandler() {
		dflTeamService = new DflTeamServiceImpl();
		dflSelectedTeamService = new DflSelectedTeamServiceImpl();
		insAndOutsService = new InsAndOutsServiceImpl();
		dflTeamPlayerService = new DflTeamPlayerServiceImpl();
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
			
			loggerUtils.log("info", "Executing start round for round={}",  round);
			
			createTeamSelections(round);
			
			loggerUtils.log("info", "Creating predictions");
			PredictionHandler predictions = new PredictionHandler();
			predictions.configureLogging(mdcKey, loggerName, logfile);
			predictions.execute(round);
			
			loggerUtils.log("info", "Creating insAndOuts Report");
			
			InsAndOutsReport insAndOutsReport = new InsAndOutsReport();
			insAndOutsReport.configureLogging(mdcKey, loggerName, logfile);
			insAndOutsReport.execute(round, "Full", emailOveride);
			
			loggerUtils.log("info", "Start round completed");
		
		} catch (Exception ex) {
			loggerUtils.log("error", "Error in ... ", ex);
		}
		
	}
	
	private void createTeamSelections(int round) {
		
		loggerUtils.log("info", "Creating team selections");
		
		List<DflTeam> teams = dflTeamService.findAll();
		
		for(DflTeam team : teams) {
			loggerUtils.log("info", "Working with team={}", team.getTeamCode());
			
			List<DflSelectedPlayer> selectedTeam = new ArrayList<>();
			
			List<InsAndOuts> insAndOuts = insAndOutsService.getByTeamAndRound(round, team.getTeamCode());
			
			if(round == 1) {
				loggerUtils.log("info", "Round 1, only ins");

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
				List<DflSelectedPlayer> prevSelectedTeam = dflSelectedTeamService.getSelectedTeamForRound(round-1, team.getTeamCode());
				loggerUtils.log("info", "Not round 1: previous team: {}", prevSelectedTeam);
				
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
			}
			
			loggerUtils.log("info", "Saving selected to DB: selected tema={}", selectedTeam);
			dflSelectedTeamService.replaceTeamForRound(round, team.getTeamCode(), selectedTeam);			
		}	
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
