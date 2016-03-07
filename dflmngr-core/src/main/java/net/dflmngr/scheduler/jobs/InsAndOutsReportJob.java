package net.dflmngr.scheduler.jobs;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dflmngr.reports.InsAndOutsReport;

@PersistJobDataAfterExecution   
@DisallowConcurrentExecution
public class InsAndOutsReportJob implements Job {
	private static final Logger logger = LoggerFactory.getLogger("databaseLogger");
	
	public static String ROUND = "ROUND";
	public static String REPORT_TYPE = "REPORT_TYPE";

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("InsAndOutsReportJob starting ...");
		
		JobDataMap data = context.getJobDetail().getJobDataMap(); 
		
		int round = data.getInt(ROUND);
		String reportType = data.getString(REPORT_TYPE);
		
		InsAndOutsReport insAndOutsReport = new InsAndOutsReport();
		try {
			logger.info("Running insAndOutsReport: round={}; reportType={};", round, reportType);
			insAndOutsReport.execute(round, reportType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		logger.info("InsAndOutsReportJob completed");
	}

}
