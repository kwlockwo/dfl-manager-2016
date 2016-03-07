package net.dflmngr.scheduler.jobs;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dflmngr.reports.RawStatsReport;

public class RawStatsReportJob implements Job {
	private static final Logger logger = LoggerFactory.getLogger("databaseLogger");
	
	public static String ROUND = "ROUND";
	public static String IS_FINAL = "IS_FINAL";

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("RawStatsReportJob starting ...");
		
		JobDataMap data = context.getJobDetail().getJobDataMap(); 
		
		int round = data.getInt(ROUND);
		boolean isFinal = data.getBoolean(IS_FINAL);
		
		RawStatsReport rawStatsReport = new RawStatsReport();
		try {
			logger.info("Running rawStatsReport: round={}; isFinal={};", round, isFinal);
			rawStatsReport.execute(round, isFinal);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		logger.info("RawStatsReportJob completed");
	}
}
