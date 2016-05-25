package net.dflmngr.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import net.dflmngr.jndi.JndiProvider;
import net.dflmngr.logging.LoggingUtils;
import net.dflmngr.model.entity.DflPlayerPredictedScores;
import net.dflmngr.model.entity.DflPlayerScores;
import net.dflmngr.model.entity.DflSelectedPlayer;
import net.dflmngr.model.entity.DflTeam;
import net.dflmngr.model.entity.DflTeamPredictedScores;
import net.dflmngr.model.entity.keys.DflPlayerPredictedScoresPK;
import net.dflmngr.model.service.DflPlayerPredictedScoresService;
import net.dflmngr.model.service.DflPlayerScoresService;
import net.dflmngr.model.service.DflSelectedTeamService;
import net.dflmngr.model.service.DflTeamPredictedScoresService;
import net.dflmngr.model.service.DflTeamService;
import net.dflmngr.model.service.impl.DflPlayerPredictedScoresServiceImpl;
import net.dflmngr.model.service.impl.DflPlayerScoresServiceImpl;
import net.dflmngr.model.service.impl.DflSelectedTeamServiceImpl;
import net.dflmngr.model.service.impl.DflTeamPredictedScoresServiceImpl;
import net.dflmngr.model.service.impl.DflTeamServiceImpl;

public class PredictionHandler {
	private LoggingUtils loggerUtils;
	
	DflPlayerPredictedScoresService dflPlayerPredictedScoresService;
	DflTeamPredictedScoresService dflTeamPredictedScoresService;
	DflPlayerScoresService dflPlayerScoresService;
	DflTeamService dflTeamService;
	DflSelectedTeamService dflSelectedTeamService;
	
	boolean isExecutable;
	
	String defaultMdcKey = "batch.name";
	String defaultLoggerName = "batch-logger";
	String defaultLogfile = "Predictions";
	
	String mdcKey;
	String loggerName;
	String logfile;
	
	public PredictionHandler() {
		dflPlayerPredictedScoresService = new DflPlayerPredictedScoresServiceImpl();
		dflTeamPredictedScoresService = new DflTeamPredictedScoresServiceImpl();
		dflPlayerScoresService = new DflPlayerScoresServiceImpl();
		dflTeamService = new DflTeamServiceImpl();
		dflSelectedTeamService = new DflSelectedTeamServiceImpl();
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
			
			loggerUtils.log("info", "PredictionHandler excuting, rount={} ....", round);
			
			calculatePlayerPredictions(round);
			calculateTeamPredictions(round);
			
			loggerUtils.log("info", "PredictionHandler completed");
			
			dflPlayerPredictedScoresService.close();
			dflTeamPredictedScoresService.close();
			dflPlayerScoresService.close();
			dflTeamService.close();
			dflSelectedTeamService.close();
		
		} catch (Exception ex) {
			loggerUtils.log("error", "Error in ... ", ex);
		}
	}
	
	private void calculatePlayerPredictions(int round) {
	
		loggerUtils.log("info", "Calculating predicted player scores ....");
		
		List<DflPlayerPredictedScores> predictedPlayerScores = new ArrayList<>();
		
		Map<Integer, List<DflPlayerScores>> allPlayerScores = dflPlayerScoresService.getUptoRoundWithKey(round);
		
		for (Map.Entry<Integer, List<DflPlayerScores>> playerScores : allPlayerScores.entrySet()) {
			DflPlayerPredictedScores predictedPlayerScore = new DflPlayerPredictedScores();
			
		    int playerId = playerScores.getKey();
		    List<DflPlayerScores> scores = playerScores.getValue();
		    
		    int totalScore = 0;
		    for(DflPlayerScores playerScore : scores) {
		    	totalScore = totalScore + playerScore.getScore();
		    }
		    
		    int predictedScore = totalScore / scores.size();
		    
		    predictedPlayerScore.setPlayerId(playerId);
		    predictedPlayerScore.setRound(round);
		    predictedPlayerScore.setAflPlayerId(scores.get(0).getAflPlayerId());
		    predictedPlayerScore.setTeamCode(scores.get(0).getTeamCode());
		    predictedPlayerScore.setTeamPlayerId(scores.get(0).getTeamPlayerId());
		    predictedPlayerScore.setPredictedScore(predictedScore);
		    
		    loggerUtils.log("info", "Predicted score for: playerId={}; round={}; teamCode={}; teamPlayerId={} predictedScore={};", 
		    				playerId, round, predictedPlayerScore.getTeamCode(), predictedPlayerScore.getTeamPlayerId(), predictedPlayerScore.getPredictedScore());
		    
		    predictedPlayerScores.add(predictedPlayerScore);
		}
		
		dflPlayerPredictedScoresService.replaceAllForRound(round, predictedPlayerScores);
	}
	
	private void calculateTeamPredictions(int round) {
		
		loggerUtils.log("info", "Calculating predicted team scores ....");
		
		List<DflTeamPredictedScores> predictedTeamScores = new ArrayList<>();
		
		List<DflTeam> teams = dflTeamService.findAll();
		
		for(DflTeam team : teams) {
			DflTeamPredictedScores predictedTeamScore = new DflTeamPredictedScores();
			List<DflSelectedPlayer> selectedTeam = dflSelectedTeamService.getSelectedTeamForRound(round, team.getTeamCode());
			
			int predictedScore = 0;
			for(DflSelectedPlayer selectedPlayer : selectedTeam) {
				DflPlayerPredictedScoresPK dflPlayerPredictedScoresPK = new DflPlayerPredictedScoresPK();
				dflPlayerPredictedScoresPK.setPlayerId(selectedPlayer.getPlayerId());
				dflPlayerPredictedScoresPK.setRound(round);
				
				DflPlayerPredictedScores predictedPlayerScore = dflPlayerPredictedScoresService.get(dflPlayerPredictedScoresPK);
				if(predictedPlayerScore != null) {
					predictedScore = predictedScore + predictedPlayerScore.getPredictedScore();
				}
			}
			
			predictedTeamScore.setTeamCode(team.getTeamCode());
			predictedTeamScore.setRound(round);
			predictedTeamScore.setPredictedScore(predictedScore);
			
		    loggerUtils.log("info", "Predicted score for: teamCode={}; round={}; predictedScore={};", 
		    				predictedTeamScore.getTeamCode(), round, predictedTeamScore.getPredictedScore());
			
			predictedTeamScores.add(predictedTeamScore);
		}
		
		dflTeamPredictedScoresService.replaceAllForRound(round, predictedTeamScores);
	}
	
	public static void main(String[] args) {
		
		Options options = new Options();
		
		Option roundOpt  = Option.builder("r").argName("round").hasArg().desc("round to run on").type(Number.class).required().build();
		
		options.addOption(roundOpt);
		
		try {
			int round = 0;
						
			CommandLineParser parser = new DefaultParser();
			CommandLine cli = parser.parse(options, args);
			
			round = ((Number)cli.getParsedOptionValue("r")).intValue();
			
			JndiProvider.bind();
			
			PredictionHandler predictions = new PredictionHandler();
			predictions.configureLogging("batch.name", "batch-logger", "PredictionsHandler_R" + round);
			predictions.execute(round);
		
		} catch (ParseException ex) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "PredictionHandler", options );
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
