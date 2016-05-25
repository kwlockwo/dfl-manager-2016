package net.dflmngr.handlers;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import net.dflmngr.jndi.JndiProvider;
import net.dflmngr.logging.LoggingUtils;
import net.dflmngr.reports.ResultsReport;

public class ResultsHandler {
	private LoggingUtils loggerUtils;
	
	boolean isExecutable;
	
	String defaultMdcKey = "batch.name";
	String defaultLoggerName = "batch-logger";
	String defaultLogfile = "RoundProgress";
	
	String mdcKey;
	String loggerName;
	String logfile;
	
	String emailOverride;
	
	public ResultsHandler() {}
	
	public void configureLogging(String mdcKey, String loggerName, String logfile) {
		loggerUtils = new LoggingUtils(loggerName, mdcKey, logfile);
		this.mdcKey = mdcKey;
		this.loggerName = loggerName;
		this.logfile = logfile;
		isExecutable = true;
		emailOverride = null;
	}
	
	public void execute(int round, boolean isFinal, String emailOverride, boolean skipStats) {
		
		try{
			if(!isExecutable) {
				configureLogging(defaultMdcKey, defaultLoggerName, defaultLogfile);
				loggerUtils.log("info", "Default logging configured");
			}
			
			loggerUtils.log("info", "ResultsHandler excuting, round={} ....", round);
			
			if(emailOverride != null && !emailOverride.equals("")) {
				loggerUtils.log("info", "Overriding email with: {}", emailOverride);
				this.emailOverride = emailOverride;
			}
			
			boolean statsLoaded = false;
			
			if(!skipStats) {
				loggerUtils.log("info", "Getting stats");
				RawPlayerStatsHandler statsHandler = new RawPlayerStatsHandler();
				statsHandler.configureLogging(mdcKey, loggerName, logfile);
				
				int tries = 1;
				
				while(!statsLoaded) {
					loggerUtils.log("info", "Attempt: " + tries);
					statsLoaded = statsHandler.execute(round);
					if(tries > 5) {
						break;
					} else {
						tries++;
					}
				}
				if(statsLoaded) {
					loggerUtils.log("info", "Stats Loaded");
				}
			} else {
				statsLoaded = true;
			}
			
			if(statsLoaded) {
				loggerUtils.log("info", "Calculating scores");
				ScoresCalculatorHandler scoresCalculator = new ScoresCalculatorHandler();
				scoresCalculator.configureLogging(mdcKey, loggerName, logfile);
				scoresCalculator.execute(round);
				
				loggerUtils.log("info", "Calculating Ladder");
				LadderCalculatorHandler ladderCalculator = new LadderCalculatorHandler();
				ladderCalculator.configureLogging(mdcKey, loggerName, logfile);
				ladderCalculator.execute(round);
				
				loggerUtils.log("info", "Writing report");
				ResultsReport resultsReport = new ResultsReport();
				resultsReport.configureLogging(mdcKey, loggerName, logfile);
				resultsReport.execute(round, isFinal, emailOverride);
				
				loggerUtils.log("info", "ResultsHandler complete");
			} else {
				loggerUtils.log("info", "No stats loaded nothing to do.");
			}
		} catch (Exception ex) {
			loggerUtils.log("error", "Error in ... ", ex);
		}
	}
	
	public static void main(String[] args) {
		
		Options options = new Options();
		
		Option roundOpt  = Option.builder("r").argName("round").hasArg().desc("round to run on").type(Number.class).required().build();
		Option emailOPt = Option.builder("e").argName("email").hasArg().desc("override email distribution").build();
		Option finalOpt = new Option("f", "final run");
		Option skipStatsOpt = new Option("ss", "skip stats download");
		
		options.addOption(roundOpt);
		options.addOption(emailOPt);
		options.addOption(finalOpt);
		options.addOption(skipStatsOpt);
		
		try {
			String email = null;
			int round = 0;
			boolean isFinal = false;
			boolean skipStats = false;
						
			CommandLineParser parser = new DefaultParser();
			CommandLine cli = parser.parse(options, args);
			
			round = ((Number)cli.getParsedOptionValue("r")).intValue();
			
			if(cli.hasOption("e")) {
				email = cli.getOptionValue("e");
			}
			if(cli.hasOption("f")) {
				isFinal = true;
			}
			if(cli.hasOption("ss")) {
				skipStats=true;
			}
			

			JndiProvider.bind();
			
			ResultsHandler resultsHandler = new ResultsHandler();
			resultsHandler.configureLogging("batch.name", "batch-logger", ("ResultsHandler_R" + round));
			resultsHandler.execute(round, isFinal, email, skipStats);

		} catch (ParseException ex) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "RawStatsReport", options );
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
