package net.dflmngr.handlers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import net.dflmngr.model.DomainDecodes;
import net.dflmngr.model.entity.AflFixture;
import net.dflmngr.model.entity.DflRoundEarlyGames;
import net.dflmngr.model.entity.DflRoundInfo;
import net.dflmngr.model.entity.DflRoundMapping;
import net.dflmngr.model.service.AflFixtureService;
import net.dflmngr.model.service.DflRoundInfoService;
import net.dflmngr.model.service.GlobalsService;
import net.dflmngr.model.service.impl.AflFixtureServiceImpl;
import net.dflmngr.model.service.impl.DflRoundInfoServiceImpl;
import net.dflmngr.model.service.impl.GlobalsServiceImpl;
import net.dflmngr.utils.DflmngrUtils;

public class DflRoundInfoCalculatorHandler {
	private Logger logger;
	
	SimpleDateFormat lockoutFormat = new SimpleDateFormat("dd/MM/yyyy h:mm a");
	
	GlobalsService globalsService;
	AflFixtureService aflFixtrureService;
	DflRoundInfoService dflRoundInfoService;
	
	
	String standardLockout;
	
	public DflRoundInfoCalculatorHandler() {
		
		MDC.put("batch.name", "DflRoundInfoCalculator");
		logger = LoggerFactory.getLogger("batch-logger");
		
		globalsService = new GlobalsServiceImpl();
		aflFixtrureService = new AflFixtureServiceImpl();
		dflRoundInfoService = new DflRoundInfoServiceImpl();
	}
	
	public void execute() {
		
		try {
			standardLockout = globalsService.getStandardLockoutTime();
			int aflRoundsMax = Integer.parseInt(globalsService.getAflRoundsMax());
			
			logger.info("Executing DflRoundInfoCalculator, max AFL rounds: {}", aflRoundsMax);
			logger.info("Stnadard round lockout time: {}", standardLockout);
			
			List<DflRoundInfo> allRoundInfo = new ArrayList<>();
			
			Map<Integer, List<AflFixture>> aflFixture = aflFixtrureService.getAflFixturneRoundBlocks();
					
			int dflRound = 1;
			
			for(int i = 1; i <= aflRoundsMax; i++) {
				
				logger.info("Handling round: {}", i);
				
				List<AflFixture> aflRoundFixtures = aflFixture.get(i);
				DflRoundInfo dflRoundInfo = new DflRoundInfo();
				
				if(aflRoundFixtures.size() == 9) {
					logger.info("AFL full round");
					
					dflRoundInfo.setRound(dflRound);
					dflRoundInfo.setSplitRound(DomainDecodes.DFL_ROUND_INFO.SPLIT_ROUND.NO);
					
					Map<Integer, Date> gameStartTimes = new HashMap<>();
					
					for(AflFixture fixture : aflRoundFixtures) {
						gameStartTimes.put(fixture.getGame(), fixture.getStart());
					}
					
					logger.info("AFL game start times: {}", gameStartTimes);
					
					logger.info("Calculating hard lockout time");
					Date hardLockout = calculateHardLockout(dflRound, gameStartTimes);
					dflRoundInfo.setHardLockoutTime(hardLockout);
					
					logger.info("Creating round mapping: DFL rouund={}; AFL round={};", dflRound, i);
					List<DflRoundMapping> roundMappingList = new ArrayList<>();
					DflRoundMapping roundMapping = new DflRoundMapping();
					roundMapping.setRound(dflRound);
					roundMapping.setAflRound(i);
					roundMappingList.add(roundMapping);
					dflRoundInfo.setRoundMapping(roundMappingList);
					
					logger.info("Calculating early games");
					List<DflRoundEarlyGames> earlyGames = calculateEarlyGames(hardLockout, aflRoundFixtures, dflRound);
					dflRoundInfo.setEarlyGames(earlyGames);
					
					allRoundInfo.add(dflRoundInfo);
				} else {
					logger.info("AFL split/bye round");
					
					int gamesCount = aflRoundFixtures.size();
					logger.info("Games in AFL round: {}", gamesCount);
					
					Map<Integer, List<AflFixture>> gamesInsSplitRound = new HashMap<>();
					gamesInsSplitRound.put(i, aflRoundFixtures);
					
					for(i++; i <= aflRoundsMax; i++) {
						List<AflFixture> aflNextRoundFixtures = aflFixture.get(i);
						gamesInsSplitRound.put(i, aflNextRoundFixtures);
						
						logger.info("Games in next AFL round: {}", aflNextRoundFixtures.size());
						gamesCount = gamesCount + aflNextRoundFixtures.size();
						
						if(gamesCount % 9 == 0)  {
							logger.info("Found enough games to make DFL rounds ... calculation split round");
							allRoundInfo.addAll(calculateSplitRound(dflRound, gamesInsSplitRound));
							for(DflRoundInfo roundInfo : allRoundInfo) {
								if(roundInfo.getRound() > dflRound) {
									dflRound = roundInfo.getRound();
								}
							}
							break;
						} 
					}	
				}
				dflRound++;
			}
			
			dflRoundInfoService.insertAll(allRoundInfo, false);
			
			logger.info("DflRoundInfoCalculator Complete");
		} catch (Exception ex) {
			logger.error("Error in ... ", ex);
		} finally {
			MDC.remove("batch.name");
		}
	}
	
	private List<DflRoundInfo> calculateSplitRound(int dflRound, Map<Integer, List<AflFixture>> gamesInsSplitRound) throws Exception {
		
		List<DflRoundInfo> splitRoundInfo = new ArrayList<>();
		
		Set<Integer> aflSplitRounds = gamesInsSplitRound.keySet();
		int aflFirstSplitRound = Collections.min(aflSplitRounds);
		
		logger.info("AFL rounds used in split round: {}", aflSplitRounds);
		
		Set<String> remainderTeams = new HashSet<>();
		Set<AflFixture> remainderGames = new HashSet<>();
		
		for(int i = 0; i < aflSplitRounds.size(); i++) {
			
			int currentSplitRound = aflFirstSplitRound + i;
			
			logger.info("Working with AFL round: {}", currentSplitRound);
			
			List<AflFixture> gamesInSplitRound = new ArrayList<>();
			
			DflRoundInfo dflRoundInfo = new DflRoundInfo();
			dflRoundInfo.setRound(dflRound);
			dflRoundInfo.setSplitRound(DomainDecodes.DFL_ROUND_INFO.SPLIT_ROUND.YES);
			
			if(remainderGames.isEmpty()) {
				logger.info("No remainder games");
								
				List<AflFixture> splitRoundGames = gamesInsSplitRound.get(currentSplitRound);
				int gamesInRound = splitRoundGames.size();
				int teamsToMakeFull = (9 - gamesInRound) * 2;
				
				logger.info("Games in current AFL round: {}; Teams required for full DFL round: {}", gamesInRound, teamsToMakeFull);
				
				Set<String> teamsInRound = new HashSet<>();
				for(AflFixture game : splitRoundGames) {
					gamesInSplitRound.add(game);
					teamsInRound.add(game.getHomeTeam());
					teamsInRound.add(game.getAwayTeam());
				}
				
				logger.info("Teams used for current DFL round {}", teamsInRound);
				
				List<AflFixture> nextSplitRoundGames = gamesInsSplitRound.get(currentSplitRound+1);
				Collections.sort(nextSplitRoundGames);
				
				List<DflRoundMapping> roundMappings = new ArrayList<>();
				
				logger.info("Creating round mapping: DFL rouund={}; AFL round={};", dflRound, currentSplitRound);
				
				DflRoundMapping roundMapping = new DflRoundMapping();
				roundMapping.setRound(dflRound);
				roundMapping.setAflRound(currentSplitRound);
				roundMappings.add(roundMapping);
				
				logger.info("Find teams to fill out round");
				
				for(AflFixture nextSplitRoundGame : nextSplitRoundGames) {
					
					if(teamsToMakeFull > 0) {
						roundMapping = new DflRoundMapping();
						roundMapping.setRound(dflRound);
						
						boolean homeTeamAdded = false;
						boolean awayTeamAdded = false;
						
						if(teamsInRound.contains(nextSplitRoundGame.getHomeTeam())) {
							logger.info("Home team={}; alredy in DFL round={}", nextSplitRoundGame.getHomeTeam(), dflRound);
							remainderTeams.add(nextSplitRoundGame.getHomeTeam());
							if(!remainderGames.contains(nextSplitRoundGame)) {
								remainderGames.add(nextSplitRoundGame);
							}
						} else {
							logger.info("Home team={}; not in DFL round={}; team will be added to round", nextSplitRoundGame.getHomeTeam(), dflRound);
							if(!gamesInSplitRound.contains(nextSplitRoundGame)) {
								gamesInSplitRound.add(nextSplitRoundGame);
							}
							homeTeamAdded = true;
							teamsToMakeFull--;
						}
						
						if(teamsInRound.contains(nextSplitRoundGame.getAwayTeam())) {
							logger.info("Away team={}; alredy in DFL round={}", nextSplitRoundGame.getHomeTeam(), dflRound);
							remainderTeams.add(nextSplitRoundGame.getAwayTeam());
							if(!remainderGames.contains(nextSplitRoundGame)) {
								remainderGames.add(nextSplitRoundGame);
							}
						} else {
							logger.info("Away team={}; not in DFL round={}; team will be added to round", nextSplitRoundGame.getHomeTeam(), dflRound);
							if(!gamesInSplitRound.contains(nextSplitRoundGame)) {
								gamesInSplitRound.add(nextSplitRoundGame);
							}
							awayTeamAdded = true;
							teamsToMakeFull--;
						}
						
						if(homeTeamAdded && awayTeamAdded) {
							roundMapping.setAflRound(nextSplitRoundGame.getRound());
							roundMapping.setAflGame(nextSplitRoundGame.getGame());
						} else if(homeTeamAdded) {
							roundMapping.setAflRound(nextSplitRoundGame.getRound());
							roundMapping.setAflGame(nextSplitRoundGame.getGame());
							roundMapping.setAflTeam(nextSplitRoundGame.getHomeTeam());
						} else if(awayTeamAdded) {
							roundMapping.setAflRound(nextSplitRoundGame.getRound());
							roundMapping.setAflGame(nextSplitRoundGame.getGame());
							roundMapping.setAflTeam(nextSplitRoundGame.getAwayTeam());
						}
						
						logger.info("Split round mapping created: {}", roundMapping);
						roundMappings.add(roundMapping);
					} else {
						logger.info("DFL round is filled out, adding to remainder: {}", nextSplitRoundGame);
						remainderTeams.add(nextSplitRoundGame.getHomeTeam());
						remainderTeams.add(nextSplitRoundGame.getAwayTeam());
						remainderGames.add(nextSplitRoundGame);
					}
				}
				
				dflRoundInfo.setRoundMapping(roundMappings);
					
				Collections.sort(gamesInSplitRound);
				Map<Integer, Date> gameStartTimes = new HashMap<>();
				
				for(AflFixture gameInSplitRound : gamesInSplitRound) {
					Integer key = Integer.parseInt(Integer.toString(gameInSplitRound.getRound()) + Integer.toString(gameInSplitRound.getGame()));
					gameStartTimes.put(key, gameInSplitRound.getStart());
				}
				
				logger.info("Calculating hard lockout time");
				Date hardLockout = calculateHardLockout(dflRound, gameStartTimes);
				dflRoundInfo.setHardLockoutTime(hardLockout);
				
				logger.info("Calculating early games");
				List<DflRoundEarlyGames> earlyGames = calculateEarlyGames(hardLockout, gamesInSplitRound, dflRound);
				dflRoundInfo.setEarlyGames(earlyGames);
				
				splitRoundInfo.add(dflRoundInfo);
			} else {
				logger.info("Remainder games exist");
				
				List<AflFixture> nextSplitRoundGames = gamesInsSplitRound.get(currentSplitRound+1);
				
				int gamesInRound = nextSplitRoundGames.size();
				int teamsToMakeFull = (gamesInRound * 2) + remainderTeams.size();
				
				logger.info("Games in current AFL round: {}; Teams required for full DFL round: {}", gamesInRound, teamsToMakeFull);
				
				if(teamsToMakeFull == 18) {
					
					for(AflFixture game : nextSplitRoundGames) {
						gamesInSplitRound.add(game);
					}
					
					
					List<DflRoundMapping> roundMappings = new ArrayList<>();
					
					logger.info("Creating round mapping: DFL rouund={}; AFL round={};", dflRound, currentSplitRound+1);
					
					DflRoundMapping roundMapping = new DflRoundMapping();
					roundMapping.setRound(dflRound);
					roundMapping.setAflRound(currentSplitRound+1);
					roundMappings.add(roundMapping);
					
					Set<AflFixture> removeGames = new HashSet<>();
					
					for(AflFixture remainderGame : remainderGames) {
						
						roundMapping = new DflRoundMapping();
						
						boolean homeTeamAdded = false;
						boolean awayTeamAdded = false;
						
						if(remainderTeams.contains(remainderGame.getHomeTeam())) {
							logger.info("Home team={}; in remainder for DFL round={}; team will be added to round", remainderGame.getHomeTeam(), dflRound);
							remainderTeams.remove(remainderGame.getHomeTeam());
							homeTeamAdded = true;
						}
						
						if(remainderTeams.contains(remainderGame.getAwayTeam())) {
							logger.info("Away team={}; in remainder for DFL round={}; team will be added to round", remainderGame.getHomeTeam(), dflRound);
							remainderTeams.remove(remainderGame.getAwayTeam());
							awayTeamAdded = true;
						}
						
						if(homeTeamAdded && awayTeamAdded) {
							roundMapping.setAflRound(remainderGame.getRound());
							roundMapping.setAflGame(remainderGame.getGame());
						} else if(homeTeamAdded) {
							roundMapping.setAflRound(remainderGame.getRound());
							roundMapping.setAflGame(remainderGame.getGame());
							roundMapping.setAflTeam(remainderGame.getHomeTeam());
						} else if(awayTeamAdded) {
							roundMapping.setAflRound(remainderGame.getRound());
							roundMapping.setAflGame(remainderGame.getGame());
							roundMapping.setAflTeam(remainderGame.getAwayTeam());
						}
						
						logger.info("Mark game to be removed from remainder: {}", remainderGame);
						if(!removeGames.contains(remainderGame)) {
							removeGames.add(remainderGame);
						}
						gamesInSplitRound.add(remainderGame);
						
						logger.info("Split round mapping created: {}", roundMapping);
						roundMappings.add(roundMapping);
					}
					
					remainderGames.removeAll(removeGames);
					
					dflRoundInfo.setRoundMapping(roundMappings);
					
					Collections.sort(gamesInSplitRound);
					Map<Integer, Date> gameStartTimes = new HashMap<>();
					
					for(AflFixture gameInSplitRound : gamesInSplitRound) {
						Integer key = Integer.parseInt(Integer.toString(gameInSplitRound.getGame()) + Integer.toString(gameInSplitRound.getGame()));
						gameStartTimes.put(key, gameInSplitRound.getStart());
					}
					
					logger.info("Calculating hard lockout time");
					Date hardLockout = calculateHardLockout(dflRound, gameStartTimes);
					dflRoundInfo.setHardLockoutTime(hardLockout);
					
					logger.info("Calculating early games");
					List<DflRoundEarlyGames> earlyGames = calculateEarlyGames(hardLockout, gamesInSplitRound, dflRound);
					dflRoundInfo.setEarlyGames(earlyGames);
					
					splitRoundInfo.add(dflRoundInfo);
					
					i++;
				} else {
					throw new Exception();
				}
			}
			dflRound++;
		}
		
		return splitRoundInfo;
	}
	
	private Date calculateHardLockout(int dflRound, Map<Integer, Date> gameStartTimes) throws Exception {
		
		Date hardLockoutTime = null;
		
		logger.info("Calculating hard lockout for round={}; from start times: {};", dflRound, gameStartTimes);
		
		String standardLockoutDay = (standardLockout.split(";"))[0];
		int standardLockoutHour = Integer.parseInt((standardLockout.split(";"))[1]);
		int standardLockoutMinute = Integer.parseInt((standardLockout.split(";"))[2]);
		String standardLockoutHourAMPM = (standardLockout.split(";"))[3];
		
		logger.info("Standard lockout details: day={}; hour={}; min={}; AMPM={};", standardLockoutDay, standardLockoutHour, standardLockoutMinute, standardLockoutHourAMPM);
		
		int lastGame = Collections.max(gameStartTimes.keySet());
		Date lastGameTime = gameStartTimes.get(lastGame);
		
		Calendar startTimeCal = Calendar.getInstance();
		startTimeCal.setTime(lastGameTime);
		int lastGameDay = startTimeCal.get(Calendar.DAY_OF_WEEK);
		
		logger.info("Last day for round: {}", DflmngrUtils.weekDaysString.get(lastGameDay));
		
		logger.info("Rebase to DFL week");
		int lastGameDayBaseWed = (lastGameDay + Calendar.WEDNESDAY) % 7;
		int standardLockoutDayBaseWed = (DflmngrUtils.weekDaysInt.get(standardLockoutDay) + Calendar.WEDNESDAY) % 7;
		
		if((lastGameDayBaseWed < standardLockoutDayBaseWed) || (lastGameDay == Calendar.TUESDAY) || (lastGameDay == Calendar.WEDNESDAY)) {
			logger.info("Last game of round is late in week, get custome lockout time");
			
			String nonStandardLockoutStr = globalsService.getNonStandardLockout(dflRound);
			
			if(nonStandardLockoutStr != null && nonStandardLockoutStr.equals("")) {
				hardLockoutTime = lockoutFormat.parse(nonStandardLockoutStr);
				logger.info("Custom lockout time: {}", hardLockoutTime);
			} else {
				throw new Exception();
			}
		} else {
		
			int lockoutOffset = standardLockoutDayBaseWed - lastGameDayBaseWed;
			
			startTimeCal.add(Calendar.DAY_OF_MONTH, lockoutOffset);
			startTimeCal.set(Calendar.HOUR, standardLockoutHour);
			startTimeCal.set(Calendar.MINUTE, standardLockoutMinute);
			startTimeCal.set(Calendar.AM_PM, DflmngrUtils.AMPM.get(standardLockoutHourAMPM));
			
			hardLockoutTime = startTimeCal.getTime();
			
			logger.info("Lockout time: {}", hardLockoutTime);
		}
			
		return hardLockoutTime;
	}
	
	private List<DflRoundEarlyGames> calculateEarlyGames(Date hardLockout, List<AflFixture> gamesInRound, int dflRound) {
		
		List<DflRoundEarlyGames> earlyGames = new ArrayList<>();
		
		for(AflFixture game : gamesInRound) {
			logger.info("DFL round={}; Hard lockout={}; Game start={};", dflRound, hardLockout, game.getStart());
			
			if(game.getStart().before(hardLockout)) {
				logger.info("Adding early game");
				
				DflRoundEarlyGames earlyGame = new DflRoundEarlyGames();
				earlyGame.setRound(dflRound);
				earlyGame.setAflRound(game.getRound());
				earlyGame.setAflGame(game.getGame());
				earlyGame.setStartTime(game.getStart());
				
				earlyGames.add(earlyGame);
			}
		}
		
		return earlyGames;
	}
	
	// For internal testing
	public static void main(String[] args) {		
		DflRoundInfoCalculatorHandler testing = new DflRoundInfoCalculatorHandler();
		testing.execute();
	}
}
