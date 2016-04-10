package net.dflmngr.handlers;

import java.util.ArrayList;
import java.util.Collections;
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
import net.dflmngr.model.entity.DflFixture;
import net.dflmngr.model.entity.DflMatthewAllen;
import net.dflmngr.model.entity.DflPlayer;
import net.dflmngr.model.entity.DflPlayerScores;
import net.dflmngr.model.entity.DflSelectedPlayer;
import net.dflmngr.model.service.DflFixtureService;
import net.dflmngr.model.service.DflMatthewAllenService;
import net.dflmngr.model.service.DflPlayerScoresService;
import net.dflmngr.model.service.DflPlayerService;
import net.dflmngr.model.service.DflSelectedTeamService;
import net.dflmngr.model.service.impl.DflFixtureServiceImpl;
import net.dflmngr.model.service.impl.DflMatthewAllenServiceImpl;
import net.dflmngr.model.service.impl.DflPlayerScoresServiceImpl;
import net.dflmngr.model.service.impl.DflPlayerServiceImpl;
import net.dflmngr.model.service.impl.DflSelectedTeamServiceImpl;

public class MatthewAllenHandler {
	private LoggingUtils loggerUtils;
	
	boolean isExecutable;
	
	String defaultMdcKey = "batch.name";
	String defaultLoggerName = "batch-logger";
	String defaultLogfile = "RoundProgress";
	
	String mdcKey;
	String loggerName;
	String logfile;
	
	DflFixtureService dflFixtureService;
	DflPlayerScoresService dflPlayerScoresService; 
	DflSelectedTeamService dflSelectedTeamService;
	DflMatthewAllenService dflMatthewAllenService;
	DflPlayerService dflPlayerService;
	
	public MatthewAllenHandler() {
		dflFixtureService = new DflFixtureServiceImpl();
		dflPlayerScoresService = new DflPlayerScoresServiceImpl();
		dflSelectedTeamService = new DflSelectedTeamServiceImpl();
		dflMatthewAllenService = new DflMatthewAllenServiceImpl();
		dflPlayerService = new DflPlayerServiceImpl();
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
			
			loggerUtils.log("info", "MatthewAllenHandler excuting, rount={} ....", round);
			
			List<DflFixture> roundFixtures = dflFixtureService.getFixturesForRound(round);
			
			loggerUtils.log("info", "Round {} ....", round);
			for(DflFixture game : roundFixtures) {
				loggerUtils.log("info", "{}: {} vs {} ....", game.getGame(), game.getHomeTeam(), game.getAwayTeam());
				calculateVotes(round, game.getGame(), game.getHomeTeam(), game.getAwayTeam());
			}
			
			loggerUtils.log("info", "MatthewAllenHandler complete");
			
		} catch (Exception ex) {
			loggerUtils.log("error", "Error in ... ", ex);
		}
	}
	
	private void calculateVotes(int round, int game, String homeTeam, String awayTeam) {
		
		Map<Integer, DflPlayerScores> playerScores = dflPlayerScoresService.getForRoundAndTeamWithKey(round, homeTeam);
		playerScores.putAll(dflPlayerScoresService.getForRoundAndTeamWithKey(round, awayTeam));
		
		List<DflPlayerScores> selectedPlayerScores = new ArrayList<>();
		
		List<DflSelectedPlayer> selectedTeam = dflSelectedTeamService.getSelectedTeamForRound(round, homeTeam);
		
		for(DflSelectedPlayer player : selectedTeam) {
			DflPlayerScores playerScore = playerScores.get(player.getPlayerId());
			if(playerScore != null) {
				selectedPlayerScores.add(playerScore);
			}
		}
		
		selectedTeam = dflSelectedTeamService.getSelectedTeamForRound(round, awayTeam);
		
		for(DflSelectedPlayer player : selectedTeam) {
			DflPlayerScores playerScore = playerScores.get(player.getPlayerId());
			if(playerScore != null) {
				selectedPlayerScores.add(playerScore);
			}
		}
		
		Collections.sort(selectedPlayerScores, Collections.reverseOrder());
		
		List<DflMatthewAllen> votes = new ArrayList<>();
		DflPlayerScores lastVoteGetter = null;
		int voteValue = 3;
		
		for(DflPlayerScores playerScore : selectedPlayerScores) {
			DflPlayer player = dflPlayerService.get(playerScore.getPlayerId());
			DflMatthewAllen vote = new DflMatthewAllen();
			if(voteValue == 3) {
				loggerUtils.log("info", "3 votes .... {}-{}: {} {}", player.getPlayerId(), playerScore.getTeamCode(), player.getFirstName(), player.getLastName());
				lastVoteGetter = playerScore;
				vote.setRound(round);
				vote.setGame(game);
				vote.setPlayderId(playerScore.getPlayerId());
				vote.setVotes(3);
				voteValue--;
			} else if(voteValue == 2) {
				if(lastVoteGetter.getScore() == playerScore.getScore()) {
					loggerUtils.log("info", "3 votes .... {}-{}: {} {}", player.getPlayerId(), playerScore.getTeamCode(), player.getFirstName(), player.getLastName());
					lastVoteGetter = playerScore;
					vote.setRound(round);
					vote.setGame(game);
					vote.setPlayderId(playerScore.getPlayerId());
					vote.setVotes(3);
				} else {
					loggerUtils.log("info", "2 votes .... {}-{}: {} {}", player.getPlayerId(), playerScore.getTeamCode(), player.getFirstName(), player.getLastName());
					lastVoteGetter = playerScore;
					vote.setRound(round);
					vote.setGame(game);
					vote.setPlayderId(playerScore.getPlayerId());
					vote.setVotes(2);
					voteValue--;
				}
			} else if(voteValue == 1) {
				if(lastVoteGetter.getScore() == playerScore.getScore()) {
					loggerUtils.log("info", "2 votes .... {}-{}: {} {}", player.getPlayerId(), playerScore.getTeamCode(), player.getFirstName(), player.getLastName());
					lastVoteGetter = playerScore;
					vote.setRound(round);
					vote.setGame(game);
					vote.setPlayderId(playerScore.getPlayerId());
					vote.setVotes(2);
				} else {
					loggerUtils.log("info", "1 vote .... {}-{}: {} {}", player.getPlayerId(), playerScore.getTeamCode(), player.getFirstName(), player.getLastName());
					lastVoteGetter = playerScore;
					vote.setRound(round);
					vote.setGame(game);
					vote.setPlayderId(playerScore.getPlayerId());
					vote.setVotes(1);
					voteValue--;
				}
			} else if(voteValue == 0) {
				if(lastVoteGetter.getScore() == playerScore.getScore()) {
					loggerUtils.log("info", "1 vote .... {}-{}: {} {}", player.getPlayerId(), playerScore.getTeamCode(), player.getFirstName(), player.getLastName());
					lastVoteGetter = playerScore;
					vote.setRound(round);
					vote.setGame(game);
					vote.setPlayderId(playerScore.getPlayerId());
					vote.setVotes(1);
				} else {
					voteValue--;
				}
			}
			
			if(voteValue == -1) {
				break;
			}
			votes.add(vote);
		}
		
		dflMatthewAllenService.insertAll(votes, false);
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
			
			MatthewAllenHandler matthewAllenHandler = new MatthewAllenHandler();
			matthewAllenHandler.configureLogging("batch.name", "batch-logger", ("MathenAllenMedal_R" + round));
			matthewAllenHandler.execute(round);
		} catch (ParseException ex) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "MatthewAllenHandler", options );
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
