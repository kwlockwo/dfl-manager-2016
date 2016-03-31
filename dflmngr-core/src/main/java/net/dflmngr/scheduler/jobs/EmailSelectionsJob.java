package net.dflmngr.scheduler.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import net.dflmngr.handlers.EmailSelectionsHandler;
import net.dflmngr.logging.LoggingUtils;

public class EmailSelectionsJob implements Job {
	private LoggingUtils loggerUtils;
	
	String defaultMdcKey = "online.name";
	String defaultLoggerName = "online-logger";
	String defaultLogfile = "Selections";

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		loggerUtils = new LoggingUtils(defaultLoggerName, defaultMdcKey, defaultLogfile);
		
		try {
			loggerUtils.log("info", "EmailSelectionsJob Starting ...");
			EmailSelectionsHandler emailSelectionsHandler = new EmailSelectionsHandler();
			emailSelectionsHandler.configureLogging(defaultMdcKey, defaultLoggerName, defaultLogfile);
			emailSelectionsHandler.execute();
			loggerUtils.log("info", "EmailSelectionsJob completed");
		} catch (Exception ex) {
			loggerUtils.log("error", "Error in ... ", ex);
		}
	}
}
