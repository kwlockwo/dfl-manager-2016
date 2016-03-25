package net.dflmngr.scheduler.jobs;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import net.dflmngr.logging.LoggingUtils;
import net.dflmngr.reports.InsAndOutsReport;

@PersistJobDataAfterExecution   
@DisallowConcurrentExecution
public class InsAndOutsReportJob implements Job {
	private LoggingUtils loggerUtils;
	
	public static String ROUND = "ROUND";
	public static String REPORT_TYPE = "REPORT_TYPE";

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		loggerUtils = new LoggingUtils("online-logger", "online.name", "Scheduler");
		
		try {
			loggerUtils.log("info", "InsAndOutsReportJob starting ...");
			
			JobDataMap data = context.getJobDetail().getJobDataMap(); 
			
			int round = data.getInt(ROUND);
			String reportType = data.getString(REPORT_TYPE);
			
			InsAndOutsReport insAndOutsReport = new InsAndOutsReport();

			loggerUtils.log("info", "Running insAndOutsReport: round={}; reportType={};", round, reportType);
			insAndOutsReport.execute(round, reportType, null);
			loggerUtils.log("info", "InsAndOutsReportJob completed");
		} catch (Exception ex) {
			loggerUtils.log("error", "Error in ... ", ex);
		}
	}
}
