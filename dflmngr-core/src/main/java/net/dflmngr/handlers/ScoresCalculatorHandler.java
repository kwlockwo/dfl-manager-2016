package net.dflmngr.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.dflmngr.logging.LoggingUtils;
import net.dflmngr.model.entity.DflPlayer;
import net.dflmngr.model.entity.DflPlayerScores;
import net.dflmngr.model.entity.DflSelectedPlayer;
import net.dflmngr.model.entity.DflTeam;
import net.dflmngr.model.entity.DflTeamPlayer;
import net.dflmngr.model.entity.DflTeamScores;
import net.dflmngr.model.entity.RawPlayerStats;
import net.dflmngr.model.entity.keys.DflPlayerScoresPK;
import net.dflmngr.model.service.DflPlayerScoresService;
import net.dflmngr.model.service.DflPlayerService;
import net.dflmngr.model.service.DflSelectedTeamService;
import net.dflmngr.model.service.DflTeamPlayerService;
import net.dflmngr.model.service.DflTeamScoresService;
import net.dflmngr.model.service.DflTeamService;
import net.dflmngr.model.service.RawPlayerStatsService;
import net.dflmngr.model.service.impl.DflPlayerScoresServiceImpl;
import net.dflmngr.model.service.impl.DflPlayerServiceImpl;
import net.dflmngr.model.service.impl.DflSelectedTeamServiceImpl;
import net.dflmngr.model.service.impl.DflTeamPlayerServiceImpl;
import net.dflmngr.model.service.impl.DflTeamScoresServiceImpl;
import net.dflmngr.model.service.impl.DflTeamServiceImpl;
import net.dflmngr.model.service.impl.RawPlayerStatsServiceImpl;

public class ScoresCalculatorHandler {
	private LoggingUtils loggerUtils;
	
	boolean isExecutable;
	
	String defaultMdcKey = "batch.name";
	String defaultLoggerName = "batch-logger";
	String defaultLogfile = "RoundProgress";
	
	String mdcKey;
	String loggerName;
	String logfile;
	
	RawPlayerStatsService rawPlayerStatsService;
	DflPlayerService dflPlayerService;
	DflTeamPlayerService dflTeamPlayerService;
	DflPlayerScoresService dflPlayerScoresService;
	DflSelectedTeamService dflSelectedTeamService;
	DflTeamService dflTeamService;
	DflTeamScoresService dflTeamScoresService;
	
	public ScoresCalculatorHandler() {
		rawPlayerStatsService = new RawPlayerStatsServiceImpl();
		dflPlayerService = new DflPlayerServiceImpl();
		dflTeamPlayerService = new DflTeamPlayerServiceImpl();
		dflPlayerScoresService = new DflPlayerScoresServiceImpl();
		dflSelectedTeamService = new DflSelectedTeamServiceImpl();
		dflTeamService = new DflTeamServiceImpl();
		dflTeamScoresService = new DflTeamScoresServiceImpl();
	}
	
	public void configureLogging(String mdcKey, String loggerName, String logfile) {
		loggerUtils = new LoggingUtils(loggerName, mdcKey, logfile);
		this.mdcKey = mdcKey;
		this.loggerName = loggerName;
		this.logfile = logfile;
		isExecutable = true;
	}
	
	public void execute(int round) {
		
		try{
			if(!isExecutable) {
				configureLogging(defaultMdcKey, defaultLoggerName, defaultLogfile);
				loggerUtils.log("info", "Default logging configured");
			}
			
			loggerUtils.log("info", "ScoresCalculator executing round={} ...", round);
			loggerUtils.log("info", "Handling player scores");
			handlePlayerScores(round);
			
			loggerUtils.log("info", "Handling team scores");
			handleTeamScores(round);
			
			rawPlayerStatsService.close();
			dflPlayerService.close();
			dflTeamPlayerService.close();
			dflPlayerScoresService.close();
			dflSelectedTeamService.close();
			dflTeamService.close();
			dflTeamScoresService.close();
			
			loggerUtils.log("info", "ScoresCalculator completed");
			
		} catch (Exception ex) {
			loggerUtils.log("error", "Error in ... ", ex);
		}
	}
	
	private void handlePlayerScores(int round) {
		
		Map<String, RawPlayerStats> stats = rawPlayerStatsService.getForRoundWithKey(round);
		List<DflPlayerScores> scores = new ArrayList<>();
		
		for (Map.Entry<String, RawPlayerStats> entry : stats.entrySet()) {

			DflPlayerScores playerScores = new DflPlayerScores();
			String aflPlayerId = entry.getKey();
			RawPlayerStats playerStats = entry.getValue();
			
			int score = calculatePlayerScore(playerStats);
			
			DflPlayer dflPlayer = dflPlayerService.getByAflPlayerId(aflPlayerId);
			
			if(dflPlayer == null) {
				loggerUtils.log("Missing afl dfl player mapping: aflPlayerId={};", aflPlayerId);
			} else {
				DflTeamPlayer dflTeamPlayer = dflTeamPlayerService.get(dflPlayer.getPlayerId());
					
				playerScores.setPlayerId(dflPlayer.getPlayerId());
				playerScores.setRound(round);
				playerScores.setAflPlayerId(aflPlayerId);
				
				if(dflTeamPlayer != null) {
					playerScores.setTeamCode(dflTeamPlayer.getTeamCode());
					playerScores.setTeamPlayerId(dflTeamPlayer.getTeamPlayerId());
				}
				
				playerScores.setScore(score);
				
				loggerUtils.log("info", "Player score={}", playerScores);
				scores.add(playerScores);
			}
		}
		
		dflPlayerScoresService.replaceAllForRound(round, scores);
	}
	
	private int calculatePlayerScore(RawPlayerStats playerStats) {
		
		int score = 0;
		
		int disposals = playerStats.getDisposals();
		int marks = playerStats.getMarks();
		int hitOuts = playerStats.getHitouts();
		int freesFor = playerStats.getFreesFor();
		int fressAgainst = playerStats.getFreesAgainst();
		int tackles = playerStats.getTackles();
		int goals = playerStats.getGoals();
		
		score = disposals + marks + hitOuts + freesFor + (-fressAgainst) + tackles + (goals * 3);
		
		return score;
	}
	
	private void handleTeamScores(int round) {
		
		List<DflTeam> teams = dflTeamService.findAll();
		List<DflTeamScores> scores = new ArrayList<>();
		
		for(DflTeam team : teams) {
			
			List<DflSelectedPlayer> selectedTeam = dflSelectedTeamService.getSelectedTeamForRound(round, team.getTeamCode());
			DflTeamScores teamScore = new DflTeamScores();
			
			int score = calculateTeamScore(selectedTeam);
			
			teamScore.setTeamCode(team.getTeamCode());
			teamScore.setRound(round);
			teamScore.setScore(score);
			
			loggerUtils.log("info", "Team score={}", teamScore);
			scores.add(teamScore);
		}
		
		dflTeamScoresService.replaceAllForRound(round, scores);
	}
	
	private int calculateTeamScore(List<DflSelectedPlayer> selectedTeam) {
		
		int teamScore = 0;
		
		for(DflSelectedPlayer player : selectedTeam) {
			DflPlayerScoresPK pk = new DflPlayerScoresPK();
			pk.setPlayerId(player.getPlayerId());
			pk.setRound(player.getRound());
			DflPlayerScores playerScore = dflPlayerScoresService.get(pk);
			
			if(playerScore == null) {
				loggerUtils.log("info", "DNP: teamCode={}; playerId={}; teamPlayerId={}", player.getTeamCode(), player.getPlayerId(), player.getTeamPlayerId());
			} else {
				teamScore = teamScore + playerScore.getScore();
			}
		}
		
		return teamScore;
	}
}
