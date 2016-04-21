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
import net.dflmngr.model.entity.DflAdamGoodes;
import net.dflmngr.model.entity.DflPlayer;
import net.dflmngr.model.entity.DflPlayerScores;
import net.dflmngr.model.entity.DflSelectedPlayer;
import net.dflmngr.model.service.DflPlayerScoresService;
import net.dflmngr.model.service.DflPlayerService;
import net.dflmngr.model.service.DflSelectedTeamService;
import net.dflmngr.model.service.impl.DflPlayerScoresServiceImpl;
import net.dflmngr.model.service.impl.DflPlayerServiceImpl;
import net.dflmngr.model.service.impl.DflSelectedTeamServiceImpl;

public class AdamGoodesHandler {
	private LoggingUtils loggerUtils;
	
	boolean isExecutable;
	
	String defaultMdcKey = "batch.name";
	String defaultLoggerName = "batch-logger";
	String defaultLogfile = "AdamGoodesHandler";
	
	String mdcKey;
	String loggerName;
	String logfile;
	
	String emailOverride;
	
	List<DflAdamGoodes> medalStandings;
	List<DflAdamGoodes> topFirstYears;
	
	DflPlayerService dflPlayerService;
	DflPlayerScoresService dflPlayerScoresService;
	DflSelectedTeamService dflSelectedTeamService;
	
	public AdamGoodesHandler() {
		medalStandings = new ArrayList<>();
		topFirstYears = new ArrayList<>();
		
		dflPlayerService = new DflPlayerServiceImpl();
		dflPlayerScoresService = new DflPlayerScoresServiceImpl();
		dflSelectedTeamService = new DflSelectedTeamServiceImpl();
	}
	
	public void configureLogging(String mdcKey, String loggerName, String logfile) {
		loggerUtils = new LoggingUtils(loggerName, mdcKey, logfile);
		this.mdcKey = mdcKey;
		this.loggerName = loggerName;
		this.logfile = logfile;
		isExecutable = true;
		emailOverride = null;
	}
	
	public void execute(int round) {
		
		try{
			if(!isExecutable) {
				configureLogging(defaultMdcKey, defaultLoggerName, defaultLogfile);
				loggerUtils.log("info", "Default logging configured");
			}
			
			List<DflPlayer> adamGoodesEligible = dflPlayerService.getAdamGoodesEligible();
			
			List<Map<Integer, DflPlayerScores>> playerScores = new ArrayList<>();
			
			for(int i = 1; i <= round; i++) {
				playerScores.add(dflPlayerScoresService.getForRoundWithKey(i));
			}
			
			calculateStandings(round, adamGoodesEligible, playerScores);
			
			dflPlayerService.close();
			dflPlayerScoresService.close();
			dflSelectedTeamService.close();
	
		} catch (Exception ex) {
			loggerUtils.log("error", "Error in ... ", ex);
		}
	}
	
	public List<DflAdamGoodes> getMedalStandings() {
		return medalStandings;
	}
	
	public List<DflAdamGoodes> getTopFirstYears() {
		return topFirstYears;
	}
	
	private void calculateStandings(int round, List<DflPlayer> adamGoodesEligible, List<Map<Integer, DflPlayerScores>> playerScores) {
		
		for(DflPlayer player : adamGoodesEligible) {
			int adamGoodesTotal = 0;
			int scoreTotal = 0;
			String teamCode = "";
			int teamPlayerId = 0;
			for(Map<Integer, DflPlayerScores> roundScores : playerScores) {
				if(roundScores.containsKey(player.getPlayerId())) {
					DflPlayerScores playerScore = roundScores.get(player.getPlayerId());
					Map<Integer, DflSelectedPlayer> dflSelectedPlayers = dflSelectedTeamService.getForRoundWithKey(playerScore.getRound());
					
					if(dflSelectedPlayers.containsKey(player.getPlayerId())) {
						adamGoodesTotal = adamGoodesTotal + playerScore.getScore();
					}
					
					scoreTotal = scoreTotal + playerScore.getScore();
					teamCode = playerScore.getTeamCode();
					teamPlayerId = playerScore.getTeamPlayerId();
				}
			}
			
			if(scoreTotal > 0) {
				DflAdamGoodes topFirstYearsTotal = new DflAdamGoodes();
				topFirstYearsTotal.setRound(round);
				topFirstYearsTotal.setPlayerId(player.getPlayerId());
				topFirstYearsTotal.setTeamCode(teamCode);
				topFirstYearsTotal.setTeamPlayerId(teamPlayerId);
				topFirstYearsTotal.setTotalScore(scoreTotal);
				
				topFirstYears.add(topFirstYearsTotal);
			}
				
			if(adamGoodesTotal > 0) {
				DflAdamGoodes adamGoodes = new DflAdamGoodes();
				adamGoodes.setRound(round);
				adamGoodes.setPlayerId(player.getPlayerId());
				adamGoodes.setTeamCode(teamCode);
				adamGoodes.setTeamPlayerId(teamPlayerId);
				adamGoodes.setTotalScore(adamGoodesTotal);
				
				medalStandings.add(adamGoodes);
			}
		}
		
		Collections.sort(medalStandings, Collections.reverseOrder());
		Collections.sort(topFirstYears, Collections.reverseOrder());
	}
	
	public static void main(String[] args) {
		
		Options options = new Options();
		
		Option roundOpt  = Option.builder("r").argName("round").hasArg().desc("round to run to").type(Number.class).required().build();
		
		options.addOption(roundOpt);
		
		try {
			int round = 0;
						
			CommandLineParser parser = new DefaultParser();
			CommandLine cli = parser.parse(options, args);
			
			round = ((Number)cli.getParsedOptionValue("r")).intValue();
							
			JndiProvider.bind();
				
			AdamGoodesHandler adamGoodesHandler = new AdamGoodesHandler();
			adamGoodesHandler.configureLogging("batch.name", "batch-logger", ("AdamGoodesHandler_R" + round));
			adamGoodesHandler.execute(round);
			
			List<DflAdamGoodes> standings = adamGoodesHandler.getMedalStandings();
			List<DflAdamGoodes> topFirstYears = adamGoodesHandler.getTopFirstYears();
			
			System.out.println("Adam Goodes top 5 for round " + round);
			for(int i = 0; i < 5; i++) {
				if(i < standings.size()) {
					DflAdamGoodes standing = standings.get(i);
					String out = i+1 + ". " + standing.getPlayerId() + ", " + standing.getTeamCode() + ", " + standing.getTeamPlayerId() + " - " + standing.getTotalScore();
					System.out.println(out);
				} else {
					break;
				}
			}
			
			System.out.println("\nTop 5 first year players for round " + round);
			for(int i = 0; i < 5; i++) {
				if(i < topFirstYears.size()) {
					DflAdamGoodes topFirstYear = topFirstYears.get(i);
					String out = i+1 + ". " + topFirstYear.getPlayerId() + ", " + topFirstYear.getTeamCode() + ", " + topFirstYear.getTeamPlayerId() + " - " + topFirstYear.getTotalScore();
					System.out.println(out);
				} else {
					break;
				}
			}
			
		} catch (ParseException ex) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "adamGoodesHandler", options);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
