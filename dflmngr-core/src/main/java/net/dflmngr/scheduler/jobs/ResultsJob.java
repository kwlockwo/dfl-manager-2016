package net.dflmngr.scheduler.jobs;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import net.dflmngr.handlers.ResultsHandler;
import net.dflmngr.logging.LoggingUtils;

public class ResultsJob implements Job {
	private LoggingUtils loggerUtils;
	
	public static String ROUND = "ROUND";
	public static String IS_FINAL = "IS_FINAL";
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		loggerUtils = new LoggingUtils("online-logger", "online.name", "Scheduler");
		
		try {
			loggerUtils.log("info", "RoundProgressJob starting ...");
			
			JobDataMap data = context.getJobDetail().getJobDataMap(); 
			
			int round = data.getInt(ROUND);
			boolean isFinal = data.getBoolean(IS_FINAL);
			
			String logFile = "";
			
			if(isFinal) {
				logFile = "ResultsRound_R" + round;
			} else {
				logFile = "ProgressRound_R" + round;
			}
			
			ResultsHandler resultsHandler = new ResultsHandler();
			resultsHandler.configureLogging("online.name", "online-logger", logFile);

			loggerUtils.log("info", "Running ProgressRound: round={};", round);
			resultsHandler.execute(round, isFinal, null);
			loggerUtils.log("info", "ProgressRoundJob completed");
		} catch (Exception ex) {
			loggerUtils.log("error", "Error in ... ", ex);
		}

	}

}
