package net.dflmngr.handlers;

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
	
	public void execute(int round, boolean isFinal, String emailOverride) {
		
		try{
			if(!isExecutable) {
				configureLogging(defaultMdcKey, defaultLoggerName, defaultLogfile);
				loggerUtils.log("info", "Default logging configured");
			}
			
			loggerUtils.log("info", "ResultsHandler excuting, rount={} ....", round);
			
			if(emailOverride != null && !emailOverride.equals("")) {
				loggerUtils.log("info", "Overriding email with: {}", emailOverride);
				this.emailOverride = emailOverride;
			}
			
			loggerUtils.log("info", "Getting stats");
			RawPlayerStatsHandler statsHandler = new RawPlayerStatsHandler();
			statsHandler.configureLogging(mdcKey, loggerName, logfile);
			statsHandler.execute(round);
			
			loggerUtils.log("info", "Calculating scores");
			ScoresCalculatorHandler scoresCalculator = new ScoresCalculatorHandler();
			scoresCalculator.configureLogging(mdcKey, loggerName, logfile);
			scoresCalculator.execute(round);
			
			loggerUtils.log("info", "Writing report");
			ResultsReport resultsReport = new ResultsReport();
			resultsReport.configureLogging(mdcKey, loggerName, logfile);
			resultsReport.execute(round, isFinal, emailOverride);
			
			loggerUtils.log("info", "ResultsHandler complete");
		} catch (Exception ex) {
			loggerUtils.log("error", "Error in ... ", ex);
		}
	}
	
	public static void main(String[] args) {
		
		try {
			String email = null;
			int round = 0;
			boolean isFinal = false;
			
			if(args.length > 3 || args.length < 2) {
				System.out.println("usage: RawStatsReport <round> optional [Final <email>]");
			} else {
				
				round = Integer.parseInt(args[0]);
				
				if(args.length == 2) {
					if(args[1].equalsIgnoreCase("Final")) {
						isFinal = true;
					} else {
						email = args[1];
					}
				} else if(args.length == 3) {
					if(args[1].equalsIgnoreCase("Final")) {
						isFinal = true;
						email = args[2];
					} else if(args[2].equalsIgnoreCase("Final")) {
						isFinal = true;
						email = args[1];
					} else {
						System.out.println("usage: RawStatsReport <round> optional [Final <email>]");
					}
				}
				
				JndiProvider.bind();
				
				ResultsHandler resultsHandler = new ResultsHandler();
				resultsHandler.configureLogging("batch.name", "batch-logger", ("ResultsHandler_R" + round));
				resultsHandler.execute(round, isFinal, email);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
