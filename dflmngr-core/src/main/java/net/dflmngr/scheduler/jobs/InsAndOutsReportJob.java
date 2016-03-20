package net.dflmngr.scheduler.jobs;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import net.dflmngr.reports.InsAndOutsReport;

@PersistJobDataAfterExecution   
@DisallowConcurrentExecution
public class InsAndOutsReportJob implements Job {
	private Logger logger;
	
	public static String ROUND = "ROUND";
	public static String REPORT_TYPE = "REPORT_TYPE";

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		MDC.put("online.name", "Scheduler");
		
		try {
			logger = LoggerFactory.getLogger("online-logger");
		
			logger.info("InsAndOutsReportJob starting ...");
			
			JobDataMap data = context.getJobDetail().getJobDataMap(); 
			
			int round = data.getInt(ROUND);
			String reportType = data.getString(REPORT_TYPE);
			
			InsAndOutsReport insAndOutsReport = new InsAndOutsReport();

			logger.info("Running insAndOutsReport: round={}; reportType={};", round, reportType);
			insAndOutsReport.execute(round, reportType);
			logger.info("InsAndOutsReportJob completed");
		} catch (Exception ex) {
			logger.error("Error in ... ", ex);
		} finally {
			MDC.remove("online.name");
		}
	}
}
