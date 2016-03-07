package net.dflmngr.scheduler.generators;

import net.dflmngr.webservice.CallDflmngrWebservices;

public class EmailSelectionsJobGenerator {
	
	private static String jobName = "EmailSelections";
	private static String jobGroup = "Ongoing";
	private static String jobClass = "net.dflmngr.scheduler.jobs.EmailSelectionsJob";
	
	public void execute() {
		
		CallDflmngrWebservices.schedualJob(jobName, jobGroup, jobClass, null, "0 0/15 * 1/1 * ? *", false);
	}
	
	// For internal testing
	public static void main(String[] args) {
		
		EmailSelectionsJobGenerator testing = new EmailSelectionsJobGenerator();

		try {
			testing.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
