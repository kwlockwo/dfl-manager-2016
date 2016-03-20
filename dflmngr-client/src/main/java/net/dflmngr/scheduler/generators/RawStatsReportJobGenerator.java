package net.dflmngr.scheduler.generators;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

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
	private Logger logger;
	
	private static String jobName = "RawStatsReport";
	private static String jobGroup = "Reports";
	private static String jobClass = "net.dflmngr.scheduler.jobs.RawStatsReportJob";
	
	DflRoundInfoService dflRoundInfoService;
	AflFixtureService aflFixtureService;
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY");
	private static SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
	
	public RawStatsReportJobGenerator() {
		MDC.put("batch.name", "RawStatsReportJobGenerator");
		logger = LoggerFactory.getLogger("batch-logger");
		
		dflRoundInfoService = new DflRoundInfoServiceImpl();
		aflFixtureService = new AflFixtureServiceImpl();
	}
	
	public void execute() {
		
		try {
			
			logger.info("Executing RawStatsReportJobGenerator ....");
			
			List<DflRoundInfo> dflSeason = dflRoundInfoService.findAll();
			
			for(DflRoundInfo roundInfo : dflSeason) {
				List<DflRoundMapping> roundMapping = roundInfo.getRoundMapping();
				
				List<AflFixture> dflAflGames = new ArrayList<>();
				for(DflRoundMapping mapping : roundMapping) {
					logger.info("Finding AFL games for: DFL round={}; AFL round={};", roundInfo.getRound(), mapping.getAflRound());
					if(mapping.getAflGame() == 0) {
						logger.info("No AFL game mapping adding all games");
						dflAflGames.addAll(aflFixtureService.getAflFixturesForRound(mapping.getAflRound()));
					} else {
						logger.info("Bye round adding: AFL round={}; game={};", mapping.getAflRound(), mapping.getAflGame());
						AflFixturePK aflFixturePK = new AflFixturePK();
						aflFixturePK.setRound(mapping.getAflRound());
						aflFixturePK.setGame(mapping.getAflGame());
						dflAflGames.add(aflFixtureService.get(aflFixturePK));
					}
				}
				
				logger.info("Processing fixtures: DFL round={}; fixtures={}", roundInfo.getRound(), dflAflGames);
				processFixtures(roundInfo.getRound(), dflAflGames);
				
				logger.info("RawStatsReportJobGenerator completed");
			}
		} catch (Exception ex) {
			logger.error("Error in ... ", ex);
		} finally {
			MDC.remove("batch.name");
		}
	}
	
	private void processFixtures(int dflRound, List<AflFixture> aflGames) {
		
		Collections.sort(aflGames);
		
		int currentGameDay = 0;
		int previousGameDay = 0;
		
		Calendar startTimeCal = null;
		
		for(AflFixture game : aflGames) {
			
			logger.info("AFL Fixture={}", game);
			
			startTimeCal = Calendar.getInstance();
			startTimeCal.setTime(game.getStart());
			currentGameDay = startTimeCal.get(Calendar.DAY_OF_WEEK);
			
			logger.info("Current Game Day={}; Previous Game Day={};", currentGameDay, previousGameDay);
			
			if(currentGameDay != previousGameDay) {
				if(currentGameDay == Calendar.SUNDAY || currentGameDay == Calendar.SATURDAY) {
					logger.info("Creating weekend run, start time={}", startTimeCal);
					createWeekendSchedule(dflRound, startTimeCal);
				} else {
					logger.info("Creating weekday run, start time={}", startTimeCal);
					createWeekdaySchedule(dflRound, startTimeCal);
				}
				
				previousGameDay = currentGameDay;
			}
			
		}
		
		logger.info("Creating final run, start time={}", startTimeCal);
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
		
		CallDflmngrWebservices.scheduleJob(jobName, jobGroup, jobClass, jobParams, cronExpression.getCronExpression(), false, "batch");	
	}
	
	public static void main(String[] args) {		
		RawStatsReportJobGenerator testing = new RawStatsReportJobGenerator();
		testing.execute();
	}
}
