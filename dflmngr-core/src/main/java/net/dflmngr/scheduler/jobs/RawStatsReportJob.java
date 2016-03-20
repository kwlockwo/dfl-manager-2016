package net.dflmngr.scheduler.jobs;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import net.dflmngr.reports.RawStatsReport;

public class RawStatsReportJob implements Job {
	private Logger logger;
	
	public static String ROUND = "ROUND";
	public static String IS_FINAL = "IS_FINAL";

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		MDC.put("online.name", "Scheduler");
		
		try {
			logger = LoggerFactory.getLogger("online-logger");
		
			logger.info("RawStatsReportJob starting ...");
			
			JobDataMap data = context.getJobDetail().getJobDataMap(); 
			
			int round = data.getInt(ROUND);
			boolean isFinal = data.getBoolean(IS_FINAL);
			
			RawStatsReport rawStatsReport = new RawStatsReport();
		
			logger.info("Running rawStatsReport: round={}; isFinal={};", round, isFinal);
			rawStatsReport.execute(round, isFinal);
			logger.info("RawStatsReportJob completed");
		} catch (Exception ex) {
			logger.error("Error in ... ", ex);
		} finally {
			MDC.remove("online.name");
		}
	}
}
