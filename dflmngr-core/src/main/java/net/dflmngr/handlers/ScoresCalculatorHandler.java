package net.dflmngr.handlers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.dflmngr.logging.LoggingUtils;
import net.dflmngr.model.entity.AflFixture;
import net.dflmngr.model.entity.DflEarlyInsAndOuts;
import net.dflmngr.model.entity.DflPlayer;
import net.dflmngr.model.entity.DflPlayerPredictedScores;
import net.dflmngr.model.entity.DflPlayerScores;
import net.dflmngr.model.entity.DflRoundEarlyGames;
import net.dflmngr.model.entity.DflRoundInfo;
import net.dflmngr.model.entity.DflSelectedPlayer;
import net.dflmngr.model.entity.DflTeam;
import net.dflmngr.model.entity.DflTeamPlayer;
import net.dflmngr.model.entity.DflTeamScores;
import net.dflmngr.model.entity.InsAndOuts;
import net.dflmngr.model.entity.RawPlayerStats;
import net.dflmngr.model.entity.keys.AflFixturePK;
import net.dflmngr.model.entity.keys.DflPlayerScoresPK;
import net.dflmngr.model.service.AflFixtureService;
import net.dflmngr.model.service.DflEarlyInsAndOutsService;
import net.dflmngr.model.service.DflPlayerPredictedScoresService;
import net.dflmngr.model.service.DflPlayerScoresService;
import net.dflmngr.model.service.DflPlayerService;
import net.dflmngr.model.service.DflRoundInfoService;
import net.dflmngr.model.service.DflSelectedTeamService;
import net.dflmngr.model.service.DflTeamPlayerService;
import net.dflmngr.model.service.DflTeamScoresService;
import net.dflmngr.model.service.DflTeamService;
import net.dflmngr.model.service.InsAndOutsService;
import net.dflmngr.model.service.RawPlayerStatsService;
import net.dflmngr.model.service.impl.AflFixtureServiceImpl;
import net.dflmngr.model.service.impl.DflEarlyInsAndOutsServiceImpl;
import net.dflmngr.model.service.impl.DflPlayerPredictedScoresServiceImpl;
import net.dflmngr.model.service.impl.DflPlayerScoresServiceImpl;
import net.dflmngr.model.service.impl.DflPlayerServiceImpl;
import net.dflmngr.model.service.impl.DflRoundInfoServiceImpl;
import net.dflmngr.model.service.impl.DflSelectedTeamServiceImpl;
import net.dflmngr.model.service.impl.DflTeamPlayerServiceImpl;
import net.dflmngr.model.service.impl.DflTeamScoresServiceImpl;
import net.dflmngr.model.service.impl.DflTeamServiceImpl;
import net.dflmngr.model.service.impl.InsAndOutsServiceImpl;
import net.dflmngr.model.service.impl.RawPlayerStatsServiceImpl;
import net.dflmngr.structs.DflPlayerAverage;

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
	DflRoundInfoService dflRoundInfoService;
	DflEarlyInsAndOutsService dflEarlyInsAndOutsService;
	AflFixtureService aflFixtureService;
	DflPlayerPredictedScoresService dflPlayerPredictedScoresService;
	InsAndOutsService insAndOutsService;
	
	public ScoresCalculatorHandler() {
		rawPlayerStatsService = new RawPlayerStatsServiceImpl();
		dflPlayerService = new DflPlayerServiceImpl();
		dflTeamPlayerService = new DflTeamPlayerServiceImpl();
		dflPlayerScoresService = new DflPlayerScoresServiceImpl();
		dflSelectedTeamService = new DflSelectedTeamServiceImpl();
		dflTeamService = new DflTeamServiceImpl();
		dflTeamScoresService = new DflTeamScoresServiceImpl();
		dflRoundInfoService = new DflRoundInfoServiceImpl();
		dflEarlyInsAndOutsService = new DflEarlyInsAndOutsServiceImpl();
		aflFixtureService = new AflFixtureServiceImpl();
		dflPlayerPredictedScoresService = new DflPlayerPredictedScoresServiceImpl();
		insAndOutsService = new InsAndOutsServiceImpl();
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
		
			
			loggerUtils.log("info", "Checking Early Games");
			DflRoundInfo dflRoundInfo = dflRoundInfoService.get(round);
			
			Date now = new Date();
			Calendar nowCal = Calendar.getInstance();
			nowCal.setTime(now);
			
			boolean earlyGamesCompleted = false;
			List<String> earlyGameTeams = new ArrayList<>();
			
			List<DflRoundEarlyGames> earlyGames = dflRoundInfo.getEarlyGames();
			
			if(earlyGames != null && dflRoundInfo.getEarlyGames().size() > 0) {
				loggerUtils.log("info", "Early Games exist, checking if completed");
				int completedCount = 0;
				for(DflRoundEarlyGames earlyGame : earlyGames) {
					Calendar startCal = Calendar.getInstance();
					startCal.setTime(earlyGame.getStartTime());
					startCal.add(Calendar.HOUR_OF_DAY, 3);
					
					if(nowCal.after(startCal)) {
						completedCount++;
					}
					
					AflFixturePK aflFixturePK = new AflFixturePK();
					aflFixturePK.setRound(earlyGame.getAflRound());
					aflFixturePK.setGame(earlyGame.getAflGame());
					
					AflFixture aflFixture = aflFixtureService.get(aflFixturePK);
					earlyGameTeams.add(aflFixture.getHomeTeam());
					earlyGameTeams.add(aflFixture.getAwayTeam());
				}
				
				if(completedCount == earlyGames.size()) {
					earlyGamesCompleted = true;
				}
			}
			
			if(!earlyGamesCompleted) {
				loggerUtils.log("info", "Early games still in progress, handling team selections");
				handleEarlyGameSelections(round, earlyGameTeams);
				StartRoundHandler startRound = new StartRoundHandler();
				startRound.configureLogging(mdcKey, loggerName, logfile);
				startRound.execute(round, null);
				
				insAndOutsService.removeForRound(round);
			}
			 		
			loggerUtils.log("info", "Handling team scores");
			handleTeamScores(round);
			
			rawPlayerStatsService.close();
			dflPlayerService.close();
			dflTeamPlayerService.close();
			dflPlayerScoresService.close();
			dflSelectedTeamService.close();
			dflTeamService.close();
			dflTeamScoresService.close();
			dflRoundInfoService.close();
			dflEarlyInsAndOutsService.close();
			aflFixtureService.close();
			dflPlayerPredictedScoresService.close();
			insAndOutsService.close();
			
			loggerUtils.log("info", "ScoresCalculator completed");
			
		} catch (Exception ex) {
			loggerUtils.log("error", "Error in ... ", ex);
		}
	}
	
	private void handleEarlyGameSelections(int round, List<String> earlyGameTeams) {
		
		List<DflTeam> teams = dflTeamService.findAll();
		
		for(DflTeam team : teams) {
			loggerUtils.log("info", "Handling early game selections for team={}", team.getTeamCode());
			List<DflEarlyInsAndOuts> earlyInsAndOuts = dflEarlyInsAndOutsService.getByTeamAndRound(round, team.getTeamCode());
			
			int inCount = 0;
			int outCount = 0;
			
			List<InsAndOuts> insAndOuts = new ArrayList<>();
			
			for(DflEarlyInsAndOuts inOrOut : earlyInsAndOuts) {
				if(inOrOut.getInOrOut().equals("I")) {
					inCount++;
				} else {
					outCount++;
				}
				
				InsAndOuts selection = new InsAndOuts();
				selection.setRound(round);
				selection.setTeamCode(inOrOut.getTeamCode());
				selection.setTeamPlayerId(inOrOut.getTeamPlayerId());
				selection.setInOrOut(inOrOut.getInOrOut());
				
				insAndOuts.add(selection);
			}
			
			loggerUtils.log("info", "In count={}; Out count={}", inCount, outCount);
			
			if(inCount > outCount) {
				int removeCount = inCount - outCount;
				
				loggerUtils.log("info", "Players to remove={}", removeCount);
				
				List<DflSelectedPlayer> selectedTeam = dflSelectedTeamService.getSelectedTeamForRound(round - 1, team.getTeamCode());
				
				List<DflSelectedPlayer> removePlayers = new ArrayList<>();
				
				for(DflSelectedPlayer selectedPlayer : selectedTeam) {
					DflPlayer player = dflPlayerService.get(selectedPlayer.getPlayerId());
					if(earlyGameTeams.contains(player.getAflClub())) {
						removePlayers.add(selectedPlayer);
					}
				}
				
				selectedTeam.removeAll(removePlayers);
				
				Map<Integer, DflPlayerPredictedScores> predictedScores = dflPlayerPredictedScoresService.getForRoundWithKey(round-1);
				List<DflPlayerAverage> playerAverages = new ArrayList<>();
				
				for(DflSelectedPlayer selectedPlayer : selectedTeam) {
					DflPlayerAverage playerAverage = new DflPlayerAverage();
					playerAverage.setPlayerId(selectedPlayer.getPlayerId());
					playerAverage.setTeamPlayerId(selectedPlayer.getTeamPlayerId());
					playerAverage.setTeamCode(selectedPlayer.getTeamCode());
					
					DflPlayerPredictedScores playerPrediction = predictedScores.get(selectedPlayer.getPlayerId());
					
					int average = 0;
					if(playerPrediction != null) {
						average = predictedScores.get(selectedPlayer.getPlayerId()).getPredictedScore();
					}
					playerAverage.setAverage(average);
					
					playerAverages.add(playerAverage);
				}
				
				Collections.reverse(playerAverages);
				
				for(int i = 0; i < removeCount; i++) {
					DflPlayerAverage playerAverage = playerAverages.get(i);
					
					InsAndOuts out = new InsAndOuts();
					out.setRound(round);
					out.setTeamCode(playerAverage.getTeamCode());
					out.setTeamPlayerId(playerAverage.getTeamPlayerId());
					out.setInOrOut("O");
					
					loggerUtils.log("info", "Outing={} due to early game balance", out);
					insAndOuts.add(out);
				}
			}
			
			if(insAndOuts.size() > 0) {
				loggerUtils.log("info", "Early game ins and outs for team={}, insAndOuts={}", team.getTeamCode(), insAndOuts);
				insAndOutsService.saveTeamInsAndOuts(insAndOuts);
			} else {
				loggerUtils.log("info", "No early game ins and outs for team={}", team.getTeamCode());
			}
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
