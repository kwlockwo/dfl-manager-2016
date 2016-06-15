package net.dflmngr.scheduler.jobs;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import net.dflmngr.handlers.StartRoundHandler;
import net.dflmngr.logging.LoggingUtils;

public class StartRoundJob implements Job {
	private LoggingUtils loggerUtils;
	
	public static String ROUND = "ROUND";
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		loggerUtils = new LoggingUtils("online-logger", "online.name", "Scheduler");
		
		try {
			loggerUtils.log("info", "StartRoundJob starting ...");
			
			JobDataMap data = context.getJobDetail().getJobDataMap(); 
			
			int round = data.getInt(ROUND);
			
			StartRoundHandler startRound = new StartRoundHandler();
			startRound.configureLogging("online.name", "online-logger", ("StartRound_R"+round));

			loggerUtils.log("info", "Running StartRound: round={};", round);
			startRound.execute(round, null);
			loggerUtils.log("info", "StartRoundJob completed");
		} catch (Exception ex) {
			loggerUtils.log("error", "Error in ... ", ex);
		}
	}
}
