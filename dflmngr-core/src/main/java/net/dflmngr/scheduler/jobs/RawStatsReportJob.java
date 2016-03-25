package net.dflmngr.scheduler.jobs;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import net.dflmngr.logging.LoggingUtils;
import net.dflmngr.reports.RawStatsReport;

public class RawStatsReportJob implements Job {
	private LoggingUtils loggerUtils;
	
	public static String ROUND = "ROUND";
	public static String IS_FINAL = "IS_FINAL";

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		loggerUtils = new LoggingUtils("online-logger", "online.name", "Scheduler");
		
		try {
			loggerUtils.log("info", "RawStatsReportJob starting ...");
			
			JobDataMap data = context.getJobDetail().getJobDataMap(); 
			
			int round = data.getInt(ROUND);
			boolean isFinal = data.getBoolean(IS_FINAL);
			
			RawStatsReport rawStatsReport = new RawStatsReport();
		
			loggerUtils.log("info", "Running rawStatsReport: round={}; isFinal={};", round, isFinal);
			rawStatsReport.execute(round, isFinal, null);
			loggerUtils.log("info", "RawStatsReportJob completed");
		} catch (Exception ex) {
			loggerUtils.log("error", "Error in ... ", ex);
		}
	}
}
