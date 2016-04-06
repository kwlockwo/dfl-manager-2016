package net.dflmngr.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.dflmngr.logging.LoggingUtils;
import net.dflmngr.model.entity.DflFixture;
import net.dflmngr.model.entity.DflLadder;
import net.dflmngr.model.entity.DflTeamScores;
import net.dflmngr.model.service.DflFixtureService;
import net.dflmngr.model.service.DflLadderService;
import net.dflmngr.model.service.DflTeamScoresService;
import net.dflmngr.model.service.impl.DflFixtureServiceImpl;
import net.dflmngr.model.service.impl.DflLadderServiceImpl;
import net.dflmngr.model.service.impl.DflTeamScoresServiceImpl;

public class LadderCalculatorHandler {
	private LoggingUtils loggerUtils;
	
	boolean isExecutable;
	
	String defaultMdcKey = "batch.name";
	String defaultLoggerName = "batch-logger";
	String defaultLogfile = "RoundProgress";
	
	String mdcKey;
	String loggerName;
	String logfile;
	
	DflLadderService dflLadderService;
	DflFixtureService dflFixtureService;
	DflTeamScoresService dflTeamScoresService;
	
	public LadderCalculatorHandler() {
		dflLadderService = new DflLadderServiceImpl();
		dflFixtureService = new DflFixtureServiceImpl();
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
			
			loggerUtils.log("info", "LadderCalculatorHandler executing round={} ...", round);
			
			handleLadder(round);
			
			dflLadderService.close();;
			dflFixtureService.close();;
			dflTeamScoresService.close();;
			
			loggerUtils.log("info", "ScoresCalculator completed");
			
		} catch (Exception ex) {
			loggerUtils.log("error", "Error in ... ", ex);
		}
	}
	
	private void handleLadder(int round) {
		
		List<DflFixture> roundFixtures = dflFixtureService.getFixturesForRound(round);
		Map<String, DflTeamScores> roundTeamScores = dflTeamScoresService.getForRoundWithKey(round);
		Map<String, DflLadder> previousLadder = dflLadderService.getForPeviousRoundWithKey(round);
		
		List<DflLadder> roundLadder = new ArrayList<>();
		
		for(DflFixture fixture : roundFixtures) {
			
			String homeTeamCode = fixture.getHomeTeam();
			String awayTeamCode = fixture.getAwayTeam();
			
			int homeTeamScore = roundTeamScores.get(homeTeamCode).getScore();
			int awayTeamScore = roundTeamScores.get(awayTeamCode).getScore();
			
			DflLadder homeTeamLadder = calculateLadder(round, homeTeamCode, previousLadder.get(homeTeamCode), homeTeamScore, awayTeamScore);
			DflLadder awayTeamLadder = calculateLadder(round, awayTeamCode, previousLadder.get(awayTeamCode), awayTeamScore, homeTeamScore);
			
			roundLadder.add(homeTeamLadder);
			roundLadder.add(awayTeamLadder);
		}
		
		dflLadderService.replaceAllForRound(round, roundLadder);
	}
	
	private DflLadder calculateLadder(int round, String teamCode, DflLadder previousLadder, int teamScore, int oppositionScore) {
		
		DflLadder newLadder = new DflLadder();
		
		int wins = (teamScore > oppositionScore ? 1 : 0);
		int losses = (teamScore < oppositionScore ? 1 : 0);
		int draws = (teamScore == oppositionScore ? 1 : 0);
		int pointsFor = teamScore;
		float averageFor = (float)pointsFor / round;
		int pointsAgainst = oppositionScore;
		float averageAgainst = (float)pointsAgainst / round;
		int pts = (teamScore > oppositionScore ? 4 : (teamScore == oppositionScore ? 2 : 0));
		float percentage = ((float)pointsFor / pointsAgainst) * 100;
		
		if(round > 1) {
			wins = wins + previousLadder.getWins();
			losses = losses + previousLadder.getLosses();
			draws = draws + previousLadder.getDraws();
			pointsFor = pointsFor + previousLadder.getPointsFor();
			averageFor = (float)pointsFor / round;
			pointsAgainst = pointsAgainst + previousLadder.getPointsAgainst();
			averageAgainst = (float)pointsAgainst / round;
			pts = pts + previousLadder.getPts();
			percentage = ((float)pointsFor / pointsAgainst) * 100;
		}

		newLadder.setRound(round);
		newLadder.setTeamCode(teamCode);
		newLadder.setWins(wins);
		newLadder.setLosses(losses);
		newLadder.setDraws(draws);
		newLadder.setPointsFor(pointsFor);
		newLadder.setAverageFor(averageFor);
		newLadder.setPointsAgainst(pointsAgainst);
		newLadder.setAverageAgainst(averageAgainst);
		newLadder.setPts(pts);
		newLadder.setPercentage(percentage);
		
		return newLadder;
	}

}
