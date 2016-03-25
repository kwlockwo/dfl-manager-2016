package net.dflmngr.scheduler.generators;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.dflmngr.jndi.JndiProvider;
import net.dflmngr.logging.LoggingUtils;
import net.dflmngr.model.entity.DflRoundEarlyGames;
import net.dflmngr.model.entity.DflRoundInfo;
import net.dflmngr.model.service.DflRoundInfoService;
import net.dflmngr.model.service.GlobalsService;
import net.dflmngr.model.service.impl.DflRoundInfoServiceImpl;
import net.dflmngr.model.service.impl.GlobalsServiceImpl;
import net.dflmngr.utils.CronExpressionCreator;
import net.dflmngr.utils.DflmngrUtils;
import net.dflmngr.webservice.CallDflmngrWebservices;

public class InsAndOutsReportJobGenerator {
	private LoggingUtils loggerUtils;
	
	DflRoundInfoService dflRoundInfoService;
	GlobalsService globalsService;
	
	private static String jobName = "InsOutsReport";
	private static String jobGroup = "Reports";
	private static String jobClass = "net.dflmngr.scheduler.jobs.InsAndOutsReportJob";
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY");
	private static SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
	
	
	public InsAndOutsReportJobGenerator() {
		
		loggerUtils = new LoggingUtils("batch-logger", "batch.name", "InsAndOutsReportJobGenerator");
		
		try {
			JndiProvider.bind();
			
			dflRoundInfoService = new DflRoundInfoServiceImpl();
			globalsService = new GlobalsServiceImpl();
		} catch (Exception ex) {
			loggerUtils.log("error", "Error in ... ", ex);
		}
	}
	
	
	public void execute() {
		
		try {
			
			loggerUtils.log("infp", "Executing InsAndOutsReportJobGenerator ....");
			
			List<DflRoundInfo> dflRounds = dflRoundInfoService.findAll();
			
			for(DflRoundInfo dflRound : dflRounds) {
				loggerUtils.log("info", "Creating full report job entry for round={}, lockout={}", dflRound.getRound(), dflRound.getHardLockoutTime());
				createReportJobEntryForFull(dflRound.getRound(), dflRound.getHardLockoutTime());
				
				Set<Date> earlyGameDates = new HashSet<>();
				for(DflRoundEarlyGames earlyGame : dflRound.getEarlyGames()) {
					loggerUtils.log("info", "Adding early games round={}, start time={}", dflRound.getRound(), earlyGame.getStartTime());
					earlyGameDates.add(earlyGame.getStartTime());
				}
				
				loggerUtils.log("info", "Creating partial report job entry for round={}", dflRound.getRound());
				createReportJobEntryForPartial(dflRound.getRound(), earlyGameDates);
				
				loggerUtils.log("info", "InsAndOutsReportJobGenerator completed");
			}
		} catch (Exception ex) {
			loggerUtils.log("error", "Error in ... ", ex);
		}
	}
	
	private void createReportJobEntryForFull(int round, Date time) {
		
		CronExpressionCreator cronExpression = new CronExpressionCreator();
		
		Calendar timeCal = Calendar.getInstance();
		timeCal.setTime(time);
		
		int mins = timeCal.get(Calendar.MINUTE) + 10;
		if(mins >= 60) {
			timeCal.set(Calendar.MINUTE, (mins - 60));
			timeCal.set(Calendar.HOUR, (timeCal.get(Calendar.HOUR) + 1));
		} else {
			timeCal.set(Calendar.MINUTE, mins);
		}
		
		cronExpression.setTime(timeFormat.format(timeCal.getTime()));
		cronExpression.setStartDate(dateFormat.format(timeCal.getTime()));
        
		Map<String, Object> jobParams = new HashMap<>();
		jobParams.put("ROUND", round);
		jobParams.put("REPORT_TYPE","Full");
		
		CallDflmngrWebservices.scheduleJob(jobName, jobGroup, jobClass, jobParams, cronExpression.getCronExpression(), false, loggerUtils);
	}
	
	private void createReportJobEntryForPartial(int round, Set<Date> times) throws Exception {
		
		Set<String> runDates = new HashSet<>();
				
		for(Date time : times) {
			String timeStr = dateFormat.format(time);
			if(!runDates.contains(timeStr)) {
				runDates.add(timeStr);
			}
		}
		
		loggerUtils.log("info", "Partial report will run at the following times: {}", runDates);
		
		String standardLockout = globalsService.getStandardLockoutTime();
		int standardLockoutHour = Integer.parseInt((standardLockout.split(";"))[1]);
		int standardLockoutMinute = Integer.parseInt((standardLockout.split(";"))[2]);
		String standardLockoutHourAMPM = (standardLockout.split(";"))[3];
		
		for(String runDate : runDates) {
			Calendar timeCal = Calendar.getInstance();
			timeCal.setTime(dateFormat.parse(runDate));
			
			timeCal.set(Calendar.HOUR, standardLockoutHour);
			timeCal.set(Calendar.MINUTE, standardLockoutMinute + 10);
			timeCal.set(Calendar.AM_PM, DflmngrUtils.AMPM.get(standardLockoutHourAMPM));
			
			CronExpressionCreator cronExpression = new CronExpressionCreator();
			cronExpression.setTime(timeFormat.format(timeCal.getTime()));
			cronExpression.setStartDate(dateFormat.format(timeCal.getTime()));
	        
			Map<String, Object> jobParams = new HashMap<>();
			jobParams.put("ROUND", round);
			jobParams.put("REPORT_TYPE","Partial");
			
			CallDflmngrWebservices.scheduleJob(jobName, jobGroup, jobClass, jobParams, cronExpression.getCronExpression(), false, loggerUtils);
		}
	}
	
	public static void main(String[] args) {
		InsAndOutsReportJobGenerator testing = new InsAndOutsReportJobGenerator();
		testing.execute();
	}
}
