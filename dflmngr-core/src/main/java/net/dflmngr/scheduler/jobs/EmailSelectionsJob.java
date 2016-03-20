package net.dflmngr.scheduler.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import net.dflmngr.handlers.EmailSelectionsHandler;

public class EmailSelectionsJob implements Job {
	private Logger logger;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		MDC.put("online.name", "Scheduler");
		
		try {
			logger = LoggerFactory.getLogger("online-logger");
			
			logger.info("EmailSelectionsJob Starting ...");
			EmailSelectionsHandler emailSelectionsHandler = new EmailSelectionsHandler();
			emailSelectionsHandler.execute();
			logger.info("EmailSelectionsJob completed");
		} catch (Exception ex) {
			logger.error("Error in ... ", ex);
		} finally {
			MDC.remove("online.name");
		}
	}
}
