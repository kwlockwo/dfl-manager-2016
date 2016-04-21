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
import net.dflmngr.model.entity.DflCallumChambers;
import net.dflmngr.model.entity.DflPlayer;
import net.dflmngr.model.entity.DflPlayerScores;
import net.dflmngr.model.entity.DflTeamPlayer;
import net.dflmngr.model.service.DflPlayerScoresService;
import net.dflmngr.model.service.DflPlayerService;
import net.dflmngr.model.service.DflTeamPlayerService;
import net.dflmngr.model.service.GlobalsService;
import net.dflmngr.model.service.impl.DflPlayerScoresServiceImpl;
import net.dflmngr.model.service.impl.DflPlayerServiceImpl;
import net.dflmngr.model.service.impl.DflTeamPlayerServiceImpl;
import net.dflmngr.model.service.impl.GlobalsServiceImpl;

public class CallumChambersHandler {
	private LoggingUtils loggerUtils;
	
	boolean isExecutable;
	
	String defaultMdcKey = "batch.name";
	String defaultLoggerName = "batch-logger";
	String defaultLogfile = "CallumChambersHandler";
	
	String mdcKey;
	String loggerName;
	String logfile;
	
	String emailOverride;
	
	List<DflCallumChambers> medalStandings;
	
	DflPlayerService dflPlayerService;
	DflPlayerScoresService dflPlayerScoresService;
	DflTeamPlayerService dflTeamPlayerService;
	GlobalsService globalsService;
	
	public CallumChambersHandler() {
		medalStandings = new ArrayList<>();
		
		dflPlayerService = new DflPlayerServiceImpl();
		dflPlayerScoresService = new DflPlayerScoresServiceImpl();
		dflTeamPlayerService = new DflTeamPlayerServiceImpl();
		globalsService = new GlobalsServiceImpl();
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
			
			List<DflPlayer> players = dflPlayerService.findAll();
			List<Map<Integer, DflPlayerScores>> playerScores = new ArrayList<>();
			
			for(int i = 1; i <= round; i++) {
				playerScores.add(dflPlayerScoresService.getForRoundWithKey(i));
			}
			
			calculateStandings(round, players, playerScores);
			
			dflPlayerService.close();
			dflPlayerScoresService.close();
			dflTeamPlayerService.close();
			globalsService.close();
			
		} catch (Exception ex) {
			loggerUtils.log("error", "Error in ... ", ex);
		}
	}
	
	public List<DflCallumChambers> getMedalStandings() {
		return medalStandings;
	}
	
	private void calculateStandings(int round, List<DflPlayer> players, List<Map<Integer, DflPlayerScores>> playerScores) {
		
		Map<String, String> draftOrder = globalsService.getDraftOrder();
		
		for(DflPlayer player : players) {
			int score = 0;
			
			DflTeamPlayer teamPlayer = dflTeamPlayerService.get(player.getPlayerId());
			
			if(teamPlayer != null) {
				String teamCode = teamPlayer.getTeamCode();
				int teamPlayerId = teamPlayer.getTeamPlayerId();
				
				for(Map<Integer, DflPlayerScores> roundScores : playerScores) {
					if(roundScores.containsKey(player.getPlayerId())) {
						score = score + roundScores.get(player.getPlayerId()).getScore();
					}
				}
				
				
				DflCallumChambers callumChambersTotal = new DflCallumChambers();
				callumChambersTotal.setRound(round);
				callumChambersTotal.setPlayerId(player.getPlayerId());
				callumChambersTotal.setTeamCode(teamCode);
				callumChambersTotal.setDraftOrder(Integer.parseInt(draftOrder.get(teamCode)));
				callumChambersTotal.setTeamPlayerId(teamPlayerId);
				callumChambersTotal.setTotalScore(score);
				
				medalStandings.add(callumChambersTotal);
			}
		}
		
		Collections.sort(medalStandings, Collections.reverseOrder());
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
				
			CallumChambersHandler callumChambersHandler = new CallumChambersHandler();
			callumChambersHandler.configureLogging("batch.name", "batch-logger", ("CallumChambersHandler_R" + round));
			callumChambersHandler.execute(round);
			
			List<DflCallumChambers> standings = callumChambersHandler.getMedalStandings();
			
			System.out.println("Callum Chambers top 5 for round " + round);
			for(int i = 0; i < 5; i++) {
				DflCallumChambers standing = standings.get(i);
				String out = i+1 + ". " + standing.getPlayerId() + ", " + standing.getTeamCode() + ", " + standing.getDraftOrder() + ", " + standing.getTeamPlayerId() + " - " + standing.getTotalScore();
				System.out.println(out);
			}
			
		} catch (ParseException ex) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "adamGoodesHandler", options);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
