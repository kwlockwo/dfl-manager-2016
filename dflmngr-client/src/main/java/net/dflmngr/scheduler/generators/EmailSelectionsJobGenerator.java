package net.dflmngr.scheduler.generators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import net.dflmngr.webservice.CallDflmngrWebservices;

public class EmailSelectionsJobGenerator {
	
	private static Logger logger;
	
	private static String jobName = "EmailSelections";
	private static String jobGroup = "Ongoing";
	private static String jobClass = "net.dflmngr.scheduler.jobs.EmailSelectionsJob";
	
	public EmailSelectionsJobGenerator() {
		MDC.put("batch.name", "EmailSelectionsJobGenerator");
		logger = LoggerFactory.getLogger("batch-logger");
		logger.info("EmailSelectionsJobGenerator Starting ...");
	}
	
	public void execute() {
		
		logger.info("EmailSelectionsJobGenerator Executing ...");
		
		try {
			CallDflmngrWebservices.scheduleJob(jobName, jobGroup, jobClass, null, "0 0/15 * 1/1 * ? *", false, "batch");
		} catch (Exception ex) {
			logger.error("Error in ...", ex);
		}
		
		logger.info("EmailSelectionsJobGenerator Completed");
		MDC.remove("batch.name");
	}
	
	// For internal testing
	public static void main(String[] args) {
		EmailSelectionsJobGenerator testing = new EmailSelectionsJobGenerator();
		testing.execute();
	}


}
