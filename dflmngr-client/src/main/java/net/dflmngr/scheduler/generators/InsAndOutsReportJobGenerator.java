package net.dflmngr.scheduler.generators;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	
	DflRoundInfoService dflRoundInfoService;
	GlobalsService globalsService;
	
	private static String jobName = "InsOutsReport";
	private static String jobGroup = "Reports";
	private static String jobClass = "net.dflmngr.scheduler.jobs.InsAndOutsReportJob";
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY");
	private static SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
	
	
	public InsAndOutsReportJobGenerator() {
		dflRoundInfoService = new DflRoundInfoServiceImpl();
		globalsService = new GlobalsServiceImpl();
	}
	
	
	public void execute() throws Exception {
		
		List<DflRoundInfo> dflRounds = dflRoundInfoService.findAll();
		
		for(DflRoundInfo dflRound : dflRounds) {
			createReportJobEntryForFull(dflRound.getRound(), dflRound.getHardLockoutTime());
			
			Set<Date> earlyGameDates = new HashSet<>();
			for(DflRoundEarlyGames earlyGame : dflRound.getEarlyGames()) {
				earlyGameDates.add(earlyGame.getStartTime());
			}
			
			createReportJobEntryForPartial(dflRound.getRound(), earlyGameDates);
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
		
		
		CallDflmngrWebservices.schedualJob(jobName, jobGroup, jobClass, jobParams, cronExpression.getCronExpression(), false);
	}
	
	private void createReportJobEntryForPartial(int round, Set<Date> times) throws Exception {
		
		Set<String> runDates = new HashSet<>();
				
		for(Date time : times) {
			String timeStr = dateFormat.format(time);
			if(!runDates.contains(timeStr)) {
				runDates.add(timeStr);
			}
		}
		
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
			jobParams.put("REPORT_TYPE","Full");
			
			CallDflmngrWebservices.schedualJob(jobName, jobGroup, jobClass, jobParams, cronExpression.getCronExpression(), false);
		}
	}
	
	public static void main(String[] args) {
		
		InsAndOutsReportJobGenerator testing = new InsAndOutsReportJobGenerator();

		try {
			testing.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
