package net.dflmngr.scheduler.generators;

import net.dflmngr.logging.LoggingUtils;
import net.dflmngr.webservice.CallDflmngrWebservices;

public class EmailSelectionsJobGenerator {
	
	private LoggingUtils loggerUtils;
	
	private static String jobName = "EmailSelections";
	private static String jobGroup = "Ongoing";
	private static String jobClass = "net.dflmngr.scheduler.jobs.EmailSelectionsJob";
	
	public EmailSelectionsJobGenerator() {
		loggerUtils = new LoggingUtils("batch-logger", "batch.name", "EmailSelectionsJobGenerator");
		loggerUtils.log("info", "EmailSelectionsJobGenerator Starting ...");
	}
	
	public void execute() {
		
		loggerUtils.log("info", "EmailSelectionsJobGenerator Executing ...");
		
		try {
			CallDflmngrWebservices.scheduleJob(jobName, jobGroup, jobClass, null, "0 0/15 * 1/1 * ? *", false, loggerUtils);
		} catch (Exception ex) {
			loggerUtils.log("error", "Error in ...", ex);
		}
		
		loggerUtils.log("info", "EmailSelectionsJobGenerator Completed");
	}
	
	// For internal testing
	public static void main(String[] args) {
		EmailSelectionsJobGenerator testing = new EmailSelectionsJobGenerator();
		testing.execute();
	}


}
