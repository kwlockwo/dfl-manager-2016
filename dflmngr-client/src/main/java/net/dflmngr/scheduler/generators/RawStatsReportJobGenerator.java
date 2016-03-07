package net.dflmngr.scheduler.generators;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dflmngr.model.entity.AflFixture;
import net.dflmngr.model.entity.DflRoundInfo;
import net.dflmngr.model.entity.DflRoundMapping;
import net.dflmngr.model.entity.keys.AflFixturePK;
import net.dflmngr.model.service.AflFixtureService;
import net.dflmngr.model.service.DflRoundInfoService;
import net.dflmngr.model.service.impl.AflFixtureServiceImpl;
import net.dflmngr.model.service.impl.DflRoundInfoServiceImpl;
import net.dflmngr.utils.CronExpressionCreator;
import net.dflmngr.webservice.CallDflmngrWebservices;

public class RawStatsReportJobGenerator {
	
	private static String jobName = "RawStatsReport";
	private static String jobGroup = "Reports";
	private static String jobClass = "net.dflmngr.scheduler.jobs.RawStatsReportJob";
	
	DflRoundInfoService dflRoundInfoService;
	AflFixtureService aflFixtureService;
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY");
	private static SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
	
	public RawStatsReportJobGenerator() {
		dflRoundInfoService = new DflRoundInfoServiceImpl();
		aflFixtureService = new AflFixtureServiceImpl();
	}
	
	public void execute() {
		
		List<DflRoundInfo> dflSeason = dflRoundInfoService.findAll();
		
		for(DflRoundInfo roundInfo : dflSeason) {
			List<DflRoundMapping> roundMapping = roundInfo.getRoundMapping();
			List<AflFixture> dflAflGames = new ArrayList<>();
			for(DflRoundMapping mapping : roundMapping) {
				if(mapping.getAflGame() == 0) {
					dflAflGames.addAll(aflFixtureService.getAflFixturesForRound(mapping.getAflRound()));
				} else {
					AflFixturePK aflFixturePK = new AflFixturePK();
					aflFixturePK.setRound(mapping.getAflRound());
					aflFixturePK.setGame(mapping.getAflGame());
					dflAflGames.add(aflFixtureService.get(aflFixturePK));
				}
			}
			
			processFixtures(roundInfo.getRound(), dflAflGames);
		}
	}
	
	private void processFixtures(int dflRound, List<AflFixture> aflGames) {
		
		Collections.sort(aflGames);
		
		int currentGameDay = 0;
		int previousGameDay = 0;
		
		Calendar startTimeCal = null;
		
		for(AflFixture game : aflGames) {
			
			startTimeCal = Calendar.getInstance();
			startTimeCal.setTime(game.getStart());
			currentGameDay = startTimeCal.get(Calendar.DAY_OF_WEEK);
			
			if(currentGameDay != previousGameDay) {
				if(currentGameDay == Calendar.SUNDAY || currentGameDay == Calendar.SATURDAY) {
					createWeekendSchedule(dflRound, startTimeCal);
				} else {
					createWeekdaySchedule(dflRound, startTimeCal);
				}
				
				previousGameDay = currentGameDay;
			}
			
		}
		
		createFinalRunSchedule(dflRound, startTimeCal);
	}
	
	private void createWeekendSchedule(int dflRound, Calendar time) {
		time.set(Calendar.HOUR_OF_DAY, 19);
		scheduleJob(dflRound, false, time);
			
		time.set(Calendar.HOUR_OF_DAY, 23);
		scheduleJob(dflRound, false, time);	
	}
	
	private void createWeekdaySchedule(int dflRound, Calendar time) {
		time.set(Calendar.HOUR_OF_DAY, 23);
		scheduleJob(dflRound, false, time);	
	}
	
	private void createFinalRunSchedule(int dflRound, Calendar time) {
		time.set(Calendar.DAY_OF_MONTH, time.get(Calendar.DAY_OF_MONTH)+1);
		time.set(Calendar.HOUR_OF_DAY, 9);
		scheduleJob(dflRound, true, time);	
	}
	
	private void scheduleJob(int round, boolean isFinal, Calendar time) {
		CronExpressionCreator cronExpression = new CronExpressionCreator();
		cronExpression.setTime(timeFormat.format(time.getTime()));
		cronExpression.setStartDate(dateFormat.format(time.getTime()));
		
		Map<String, Object> jobParams = new HashMap<>();
		jobParams.put("ROUND", round);
		jobParams.put("IS_FINAL", isFinal);
		
		CallDflmngrWebservices.schedualJob(jobName, jobGroup, jobClass, jobParams, cronExpression.getCronExpression(), false);	
	}
	
	public static void main(String[] args) {
		
		RawStatsReportJobGenerator testing = new RawStatsReportJobGenerator();

		try {
			testing.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
