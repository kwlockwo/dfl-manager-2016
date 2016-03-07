package net.dflmngr.scheduler.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dflmngr.handlers.EmailSelectionsHandler;

public class EmailSelectionsJob implements Job {
	private static final Logger logger = LoggerFactory.getLogger("databaseLogger");

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("EmailSelectionsJob starting ...");
		
		EmailSelectionsHandler emailSelectionsHandler = new EmailSelectionsHandler();
		try {
			emailSelectionsHandler.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		logger.info("EmailSelectionsJob completed");
	}
}
