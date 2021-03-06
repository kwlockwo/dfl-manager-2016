package net.dflmngr.reports;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import net.dflmngr.jndi.JndiProvider;
import net.dflmngr.logging.LoggingUtils;
import net.dflmngr.model.entity.DflFixture;
import net.dflmngr.model.entity.DflLadder;
import net.dflmngr.model.entity.DflPlayer;
import net.dflmngr.model.entity.DflPlayerPredictedScores;
import net.dflmngr.model.entity.DflPlayerScores;
import net.dflmngr.model.entity.DflSelectedPlayer;
import net.dflmngr.model.entity.DflTeam;
import net.dflmngr.model.entity.DflTeamPredictedScores;
import net.dflmngr.model.entity.DflTeamScores;
import net.dflmngr.model.entity.RawPlayerStats;
import net.dflmngr.model.service.AflFixtureService;
import net.dflmngr.model.service.DflFixtureService;
import net.dflmngr.model.service.DflLadderService;
import net.dflmngr.model.service.DflPlayerPredictedScoresService;
import net.dflmngr.model.service.DflPlayerScoresService;
import net.dflmngr.model.service.DflPlayerService;
import net.dflmngr.model.service.DflSelectedTeamService;
import net.dflmngr.model.service.DflTeamPredictedScoresService;
import net.dflmngr.model.service.DflTeamScoresService;
import net.dflmngr.model.service.DflTeamService;
import net.dflmngr.model.service.GlobalsService;
import net.dflmngr.model.service.RawPlayerStatsService;
import net.dflmngr.model.service.impl.AflFixtureServiceImpl;
import net.dflmngr.model.service.impl.DflFixtureServiceImpl;
import net.dflmngr.model.service.impl.DflLadderServiceImpl;
import net.dflmngr.model.service.impl.DflPlayerPredictedScoresServiceImpl;
import net.dflmngr.model.service.impl.DflPlayerScoresServiceImpl;
import net.dflmngr.model.service.impl.DflPlayerServiceImpl;
import net.dflmngr.model.service.impl.DflSelectedTeamServiceImpl;
import net.dflmngr.model.service.impl.DflTeamPredictedScoresServiceImpl;
import net.dflmngr.model.service.impl.DflTeamScoresServiceImpl;
import net.dflmngr.model.service.impl.DflTeamServiceImpl;
import net.dflmngr.model.service.impl.GlobalsServiceImpl;
import net.dflmngr.model.service.impl.RawPlayerStatsServiceImpl;
import net.dflmngr.reports.struct.ResultsFixtureTabTeamStruct;
import net.dflmngr.utils.DflmngrUtils;
import net.dflmngr.utils.EmailUtils;

public class ResultsReport {
	private LoggingUtils loggerUtils;
	
	boolean isExecutable;
	
	String defaultMdcKey = "batch.name";
	String defaultLoggerName = "batch-logger";
	String defaultLogfile = "RoundProgress";
	
	String mdcKey;
	String loggerName;
	String logfile;
	
	RawPlayerStatsService rawPlayerStatsService;
	DflPlayerScoresService dflPlayerScoresService;
	DflTeamScoresService dflTeamScoresService;
	DflFixtureService dflFixtureService;
	DflSelectedTeamService dflSelectedTeamService;
	GlobalsService globalsService;
	DflTeamService dflTeamService;
	DflPlayerService dflPlayerService;
	AflFixtureService aflFixtureService;
	DflLadderService dflLadderService;
	DflPlayerPredictedScoresService dflPlayerPredictedScoresService;
	DflTeamPredictedScoresService dflTeamPredictedScoresService;
	
	String emailOverride;
	
	String[] resultsSpreadsheetHeaders = {"Player", "D", "M", "HO", "FF", "FA", "T", "G"};
	String[] fixtureSheetHeader = {"No.", "Player", "Pos", "K", "H", "D", "M", "HO", "FF", "FA", "T", "G", "B", "Score", "Predicted", "Trend"};
	String[] ladderHeader = {"Team", "W", "L", "D", "For", "Ave", "Agst", "Av", "Pts", "%"};
	String[] liveLadderHeader = {"Teeam", "Pts", "%"};
	
	Map<String, Integer> playersPlayedCount;
	Map<String, Integer> selectedPlayersCount;
	Map<String, Integer> currentPredictedTeamScores;
	
	Map<String, RawPlayerStats> playerStats;
	Map<Integer, DflPlayerScores> playerScores;
	Map<Integer, DflPlayerPredictedScores> playerPredictedScores;
	Map<String, DflTeamScores> teamScores;
	Map<String, DflTeamPredictedScores> teamPredictedScores;
	List<DflFixture> roundFixtures;
	
	public ResultsReport() {
		rawPlayerStatsService = new RawPlayerStatsServiceImpl();
		dflPlayerScoresService = new DflPlayerScoresServiceImpl();
		dflTeamScoresService = new DflTeamScoresServiceImpl();
		dflFixtureService = new DflFixtureServiceImpl();
		dflSelectedTeamService = new DflSelectedTeamServiceImpl();
		globalsService = new GlobalsServiceImpl();
		dflTeamService = new DflTeamServiceImpl();
		dflPlayerService = new DflPlayerServiceImpl();
		aflFixtureService = new AflFixtureServiceImpl();
		dflLadderService = new DflLadderServiceImpl();
		dflPlayerPredictedScoresService = new DflPlayerPredictedScoresServiceImpl();
		dflTeamPredictedScoresService = new DflTeamPredictedScoresServiceImpl();
		
		playersPlayedCount = new HashMap<>();
		selectedPlayersCount = new HashMap<>();
		currentPredictedTeamScores = new HashMap<>();
	}
	
	public void configureLogging(String mdcKey, String loggerName, String logfile) {
		loggerUtils = new LoggingUtils(loggerName, mdcKey, logfile);
		this.mdcKey = mdcKey;
		this.loggerName = loggerName;
		this.logfile = logfile;
		isExecutable = true;
	}
	
	public void execute(int round, boolean isFinal, String emailOverride) {
		
		try{
			if(!isExecutable) {
				configureLogging(defaultMdcKey, defaultLoggerName, defaultLogfile);
				loggerUtils.log("info", "Default logging configured");
			}
			
			loggerUtils.log("info", "Executing ResultsReport for rounds: {}, is final: {}", round, isFinal);
			
			if(emailOverride != null && !emailOverride.equals("")) {
				loggerUtils.log("info", "Overriding email with: {}", emailOverride);
				this.emailOverride = emailOverride;
			}
			
			playerStats = rawPlayerStatsService.getForRoundWithKey(round);
			playerScores = dflPlayerScoresService.getForRoundWithKey(round);
			playerPredictedScores = dflPlayerPredictedScoresService.getForRoundWithKey(round);
			teamScores = dflTeamScoresService.getForRoundWithKey(round);
			teamPredictedScores = dflTeamPredictedScoresService.getForRoundWithKey(round);
			roundFixtures = dflFixtureService.getFixturesForRound(round);
			
			String reportName = writeReport(round, isFinal);
			
			loggerUtils.log("info", "Sending Report");
			emailReport(reportName, round, isFinal);
			
			rawPlayerStatsService.close();
			dflPlayerScoresService.close();
			dflTeamScoresService.close();
			dflFixtureService.close();
			dflSelectedTeamService.close();
			globalsService.close();
			dflTeamService.close();
			dflPlayerService.close();
			aflFixtureService.close();
			dflPlayerPredictedScoresService.close();
			
			loggerUtils.log("info", "ResultsReport Completed");
			
		} catch (Exception ex) {
			loggerUtils.log("error", "Error in ... ", ex);
		}
		
	}
	
	private String writeReport(int round, boolean isFinal) throws Exception {
		
		String reportName = "";
		if(isFinal) {
			reportName = "ResultsReport_Round_" + round + "_FINAL_" + DflmngrUtils.getNowStr() + ".xlsx";
		} else {
			reportName = "ResultsReport_Round_" + round + "_" + DflmngrUtils.getNowStr() + ".xlsx";
		}
		Path reportLocation = Paths.get(globalsService.getAppDir(), globalsService.getReportDir(), "resultsReport", reportName);
		
		loggerUtils.log("info", "Writing Results Report");
		loggerUtils.log("info", "Report name: {}", reportName);
		loggerUtils.log("info", "Report location: {}", reportLocation);
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		
		XSSFSheet sheet = workbook.createSheet("ResultsSpreadsheet");
		writeResultsSpreadsheetHeaders(workbook);
		writeResultsSpreadsheetStats(sheet);
		
		for(DflFixture fixture : roundFixtures) {
			sheet = workbook.createSheet(fixture.getHomeTeam() + " vs " + fixture.getAwayTeam());
			writeFixtureSheetHeaders(sheet, fixture);
			writeFixtureSheetData(sheet, fixture);
		}
		
		for(int i = 0; i < workbook.getNumberOfSheets(); i++) {
			sheet = workbook.getSheetAt(i);
			for(int j = 0; j < sheet.getRow(1).getPhysicalNumberOfCells(); j++) {
				sheet.autoSizeColumn(j);
			}
		}
		
		OutputStream out = Files.newOutputStream(reportLocation);
		workbook.write(out);
		workbook.close();
		out.close();
		
		return reportLocation.toString();
	}
	
	private void writeResultsSpreadsheetHeaders(XSSFWorkbook workbook) {
		
		loggerUtils.log("info", "Writing ResultsSpreadsheet Header Rows");
		
		XSSFSheet sheet = workbook.getSheet("ResultsSpreadsheet");
		XSSFRow row = sheet.createRow(0);
		
		XSSFCellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		style.setFont(font);
				
		for(int i = 0; i < this.resultsSpreadsheetHeaders.length; i++) {
			XSSFCell cell = row.createCell(i);
			cell.setCellValue(this.resultsSpreadsheetHeaders[i]);
			cell.setCellStyle(style);
		}
	}
	
	private void writeResultsSpreadsheetStats(XSSFSheet sheet) {
		
		loggerUtils.log("info", "Writing ResultsSpreadsheet report data");
		
		for(RawPlayerStats stats : playerStats.values()) {
			XSSFRow row = sheet.createRow(sheet.getLastRowNum()+1);
			
			for(int i = 0; i < this.resultsSpreadsheetHeaders.length; i++) {
				XSSFCell cell = row.createCell(i);
				
				switch(i) {
					case 0: cell.setCellValue(stats.getName()); break;
					case 1: cell.setCellValue(stats.getDisposals()); break;
					case 2: cell.setCellValue(stats.getMarks()); break;
					case 3: cell.setCellValue(stats.getHitouts()); break;
					case 4: cell.setCellValue(stats.getFreesFor()); break;
					case 5: cell.setCellValue(stats.getFreesAgainst()); break;
					case 6: cell.setCellValue(stats.getTackles()); break;
					case 7: cell.setCellValue(stats.getGoals()); break;
				}
			}
		}
	}
	
	private void writeFixtureSheetHeaders(XSSFSheet sheet, DflFixture fixture) {
		
		loggerUtils.log("info", "Writing fixture sheet Header Rows");
		
		XSSFRow row = sheet.createRow(0);
		
		XSSFCellStyle style = sheet.getWorkbook().createCellStyle();
		XSSFFont font = sheet.getWorkbook().createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		style.setFont(font);
		
		XSSFCellStyle teamNameStyle = sheet.getWorkbook().createCellStyle();
		teamNameStyle.setFont(font);
		teamNameStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		
		DflTeam team = dflTeamService.get(fixture.getHomeTeam());
		XSSFCell cell = row.createCell(0);
		cell.setCellValue(team.getName());
		cell.setCellStyle(teamNameStyle);
		
		team = dflTeamService.get(fixture.getAwayTeam());
		cell = row.createCell(15);
		cell.setCellValue(team.getName());
		cell.setCellStyle(teamNameStyle);
		
		sheet.addMergedRegion(new CellRangeAddress(0,0,0,(fixtureSheetHeader.length - 1)));
		sheet.addMergedRegion(new CellRangeAddress(0,0,(fixtureSheetHeader.length + 1),(fixtureSheetHeader.length * 2)));
		
		row = sheet.createRow(1);
		
		for(int i = 0; i < fixtureSheetHeader.length; i++) {
			cell = row.createCell(i);
			cell.setCellValue(fixtureSheetHeader[i]);
			cell.setCellStyle(style);
		}
		
		cell = row.createCell(fixtureSheetHeader.length);
		cell.setCellValue("");
		
		for(int i = 0; i < fixtureSheetHeader.length; i++) {
			cell = row.createCell(fixtureSheetHeader.length + 1 + i);
			cell.setCellValue(fixtureSheetHeader[i]);
			cell.setCellStyle(style);
		}
	}
	
	private void writeFixtureSheetData(XSSFSheet sheet, DflFixture fixture) {
		
		loggerUtils.log("info", "Writing fixture sheet data");
		
		XSSFCellStyle style = sheet.getWorkbook().createCellStyle();
		XSSFFont font = sheet.getWorkbook().createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		style.setFont(font);
		
		List<ResultsFixtureTabTeamStruct> homeTeamData = new ArrayList<>();
		List<ResultsFixtureTabTeamStruct> awayTeamData = new ArrayList<>();
		
		List<DflSelectedPlayer> selectedHomeTeam = dflSelectedTeamService.getSelectedTeamForRound(fixture.getRound(), fixture.getHomeTeam());
		List<DflSelectedPlayer> selectedAwayTeam = dflSelectedTeamService.getSelectedTeamForRound(fixture.getRound(), fixture.getAwayTeam());
		
		List<String> playedTeams = aflFixtureService.getAflTeamsPlayedForRound(fixture.getRound());
		
		int playersPlayed = 0;
		int currentPredictedScore = 0;
		int homeTotalTrend = 0;
		
		for(DflSelectedPlayer selectedPlayer : selectedHomeTeam) {
			ResultsFixtureTabTeamStruct playerRec = new ResultsFixtureTabTeamStruct();
			
			DflPlayerScores score = playerScores.get(selectedPlayer.getPlayerId());
			DflPlayerPredictedScores predictedScore = playerPredictedScores.get(selectedPlayer.getPlayerId());
			DflPlayer player = dflPlayerService.get(selectedPlayer.getPlayerId());
			String playerName = player.getFirstName() + " " + player.getLastName();
			
			playerRec.setNo(selectedPlayer.getTeamPlayerId());
			playerRec.setPlayer(playerName);
			playerRec.setPosition(player.getPosition());
			
			if(predictedScore == null) {
				playerRec.setPredicted(25);
			} else {
				playerRec.setPredicted(predictedScore.getPredictedScore());
			}
			
			if(score == null) {
				if(playedTeams.contains(DflmngrUtils.dflAflTeamMap.get(player.getAflClub()))) {
					playerRec.setPlayed(true);
					playerRec.setDnp(true);
					playerRec.setKicks("0");
					playerRec.setHandballs("0");
					playerRec.setDisposals("0");
					playerRec.setMarks("0");
					playerRec.setHitouts("0");
					playerRec.setFreesFor("0");
					playerRec.setFreesAgainst("0");
					playerRec.setTackles("0");
					playerRec.setGoals("0");
					playerRec.setBehinds("0");
					playerRec.setScore("dnp");
					playerRec.setTrend("-" + playerRec.getPredicted());
				} else {
					playerRec.setPlayed(false);
					playerRec.setDnp(false);
					playerRec.setKicks("");
					playerRec.setHandballs("");
					playerRec.setDisposals("");
					playerRec.setMarks("");
					playerRec.setHitouts("");
					playerRec.setFreesFor("");
					playerRec.setFreesAgainst("");
					playerRec.setTackles("");
					playerRec.setGoals("");
					playerRec.setBehinds("");
					playerRec.setScore("");
					playerRec.setTrend("");
				}				
			} else {
				RawPlayerStats stats =  playerStats.get(score.getAflPlayerId());
				
				playerRec.setPlayed(true);
				playerRec.setDnp(false);
				playerRec.setKicksInt(stats.getKicks());
				playerRec.setHandballsInt(stats.getHandballs());
				playerRec.setDisposalsInt(stats.getDisposals());
				playerRec.setMarksInt(stats.getMarks());
				playerRec.setHitoutsInt(stats.getHitouts());
				playerRec.setFreesForInt(stats.getFreesFor());
				playerRec.setFreesAgainstInt(stats.getFreesAgainst());
				playerRec.setTacklesInt(stats.getTackles());
				playerRec.setGoalsInt(stats.getGoals());
				playerRec.setBehindsInt(stats.getBehinds());
				playerRec.setScoreInt(score.getScore());
				playerRec.setTrendInt(playerRec.getScoreInt() - playerRec.getPredicted());
			}
			
			if(playerRec.hasPlayed()) {
				playersPlayed++;
				if(!playerRec.isDnp()) {
					currentPredictedScore = currentPredictedScore + playerRec.getScoreInt();
				}
				homeTotalTrend = homeTotalTrend + playerRec.getTrendInt();
			} else {
				currentPredictedScore = currentPredictedScore + playerRec.getPredicted();
			}
			
			homeTeamData.add(playerRec);
		}
		
		playersPlayedCount.put(fixture.getHomeTeam(), playersPlayed);
		selectedPlayersCount.put(fixture.getHomeTeam(), selectedHomeTeam.size());
		currentPredictedTeamScores.put(fixture.getHomeTeam(), currentPredictedScore);
		playersPlayed = 0;
		currentPredictedScore = 0;
		int awayTotalTrend = 0;
		
		for(DflSelectedPlayer selectedPlayer : selectedAwayTeam) {
			ResultsFixtureTabTeamStruct playerRec = new ResultsFixtureTabTeamStruct();
			
			DflPlayerScores score = playerScores.get(selectedPlayer.getPlayerId());
			DflPlayerPredictedScores predictedScore = playerPredictedScores.get(selectedPlayer.getPlayerId());
			DflPlayer player = dflPlayerService.get(selectedPlayer.getPlayerId());
			String playerName = player.getFirstName() + " " + player.getLastName();
			
			playerRec.setNo(selectedPlayer.getTeamPlayerId());
			playerRec.setPlayer(playerName);
			playerRec.setPosition(player.getPosition());
			
			if(predictedScore == null) {
				playerRec.setPredicted(25);
			} else {
				playerRec.setPredicted(predictedScore.getPredictedScore());
			}
			
			if(score == null) {
				if(playedTeams.contains(DflmngrUtils.dflAflTeamMap.get(player.getAflClub()))) {
					playerRec.setPlayed(true);
					playerRec.setDnp(true);
					playerRec.setKicks("0");
					playerRec.setHandballs("0");
					playerRec.setDisposals("0");
					playerRec.setMarks("0");
					playerRec.setHitouts("0");
					playerRec.setFreesFor("0");
					playerRec.setFreesAgainst("0");
					playerRec.setTackles("0");
					playerRec.setGoals("0");
					playerRec.setBehinds("0");
					playerRec.setScore("dnp");
					playerRec.setTrend("-" + playerRec.getPredicted());
				} else {
					playerRec.setPlayed(false);
					playerRec.setDnp(false);
					playerRec.setKicks("");
					playerRec.setHandballs("");
					playerRec.setDisposals("");
					playerRec.setMarks("");
					playerRec.setHitouts("");
					playerRec.setFreesFor("");
					playerRec.setFreesAgainst("");
					playerRec.setTackles("");
					playerRec.setGoals("");
					playerRec.setBehinds("");
					playerRec.setScore("");
					playerRec.setTrend("");
				}				
			} else {
				RawPlayerStats stats =  playerStats.get(score.getAflPlayerId());
				
				playerRec.setPlayed(true);
				playerRec.setDnp(false);
				playerRec.setKicksInt(stats.getKicks());
				playerRec.setHandballsInt(stats.getHandballs());
				playerRec.setDisposalsInt(stats.getDisposals());
				playerRec.setMarksInt(stats.getMarks());
				playerRec.setHitoutsInt(stats.getHitouts());
				playerRec.setFreesForInt(stats.getFreesFor());
				playerRec.setFreesAgainstInt(stats.getFreesAgainst());
				playerRec.setTacklesInt(stats.getTackles());
				playerRec.setGoalsInt(stats.getGoals());
				playerRec.setBehindsInt(stats.getBehinds());
				playerRec.setScoreInt(score.getScore());
				playerRec.setTrendInt(playerRec.getScoreInt() - playerRec.getPredicted());
			}
			
			if(playerRec.hasPlayed()) {
				playersPlayed++;
				if(!playerRec.isDnp()) {
					currentPredictedScore = currentPredictedScore + playerRec.getScoreInt();
				}
				awayTotalTrend = awayTotalTrend + playerRec.getTrendInt();
			} else {
				currentPredictedScore = currentPredictedScore + playerRec.getPredicted();
			}
			
			awayTeamData.add(playerRec);
		}
		
		playersPlayedCount.put(fixture.getAwayTeam(), playersPlayed);
		selectedPlayersCount.put(fixture.getAwayTeam(), selectedAwayTeam.size());
		currentPredictedTeamScores.put(fixture.getAwayTeam(), currentPredictedScore);
		
		Collections.sort(homeTeamData);
		Collections.sort(awayTeamData);
		
		int currentStatsRow = sheet.getLastRowNum()+1;
		
		for(ResultsFixtureTabTeamStruct rec : homeTeamData) {
			
			XSSFRow row = sheet.createRow(sheet.getLastRowNum()+1);
			
			for(int i = 0; i < this.fixtureSheetHeader.length; i++) {
				XSSFCell cell = row.createCell(i);
				
				if(rec.hasPlayed()) {
					switch(i) {
						case 0: cell.setCellValue(rec.getNo()); break;
						case 1: cell.setCellValue(rec.getPlayer()); break;
						case 2: cell.setCellValue(rec.getPosition()); break;
						case 3: cell.setCellValue(rec.getKicksInt()); break;
						case 4: cell.setCellValue(rec.getHandballsInt()); break;
						case 5: cell.setCellValue(rec.getDisposalsInt()); break;
						case 6: cell.setCellValue(rec.getMarksInt()); break;
						case 7: cell.setCellValue(rec.getHitoutsInt()); break;
						case 8: cell.setCellValue(rec.getFreesForInt()); break;
						case 9: cell.setCellValue(rec.getFreesAgainstInt()); break;
						case 10: cell.setCellValue(rec.getTacklesInt()); break;
						case 11: cell.setCellValue(rec.getGoalsInt()); break;
						case 12: cell.setCellValue(rec.getBehindsInt()); break;
						case 13: if(rec.isDnp()) {
									cell.setCellValue(rec.getScore()); break;
								} else {
									cell.setCellValue(rec.getScoreInt()); break;
								}
						case 14: cell.setCellValue(rec.getPredicted()); break;
						case 15: cell.setCellValue(rec.getTrendInt()); break;
					}
				} else {
					switch(i) {
						case 0: cell.setCellValue(rec.getNo()); break;
						case 1: cell.setCellValue(rec.getPlayer()); break;
						case 2: cell.setCellValue(rec.getPosition()); break;
						case 3: cell.setCellValue(rec.getKicks()); break;
						case 4: cell.setCellValue(rec.getHandballs()); break;
						case 5: cell.setCellValue(rec.getDisposals()); break;
						case 6: cell.setCellValue(rec.getMarks()); break;
						case 7: cell.setCellValue(rec.getHitouts()); break;
						case 8: cell.setCellValue(rec.getFreesFor()); break;
						case 9: cell.setCellValue(rec.getFreesAgainst()); break;
						case 10: cell.setCellValue(rec.getTackles()); break;
						case 11: cell.setCellValue(rec.getGoals()); break;
						case 12: cell.setCellValue(rec.getBehinds()); break;
						case 13: cell.setCellValue(rec.getScore()); break;
						case 14: cell.setCellValue(rec.getPredicted()); break;
						case 15: cell.setCellValue(rec.getTrend()); break;
					}
				}
			}
		}
		
		for(ResultsFixtureTabTeamStruct rec : awayTeamData) {
			XSSFRow row = sheet.getRow(currentStatsRow);
			if(row == null) {
				row = sheet.createRow(currentStatsRow);
			}
			
			for(int i = 0; i < this.fixtureSheetHeader.length; i++) {
				XSSFCell cell = row.createCell(i + fixtureSheetHeader.length + 1);
				
				if(rec.hasPlayed()) {
					switch(i) {
						case 0: cell.setCellValue(rec.getNo()); break;
						case 1: cell.setCellValue(rec.getPlayer()); break;
						case 2: cell.setCellValue(rec.getPosition()); break;
						case 3: cell.setCellValue(rec.getKicksInt()); break;
						case 4: cell.setCellValue(rec.getHandballsInt()); break;
						case 5: cell.setCellValue(rec.getDisposalsInt()); break;
						case 6: cell.setCellValue(rec.getMarksInt()); break;
						case 7: cell.setCellValue(rec.getHitoutsInt()); break;
						case 8: cell.setCellValue(rec.getFreesForInt()); break;
						case 9: cell.setCellValue(rec.getFreesAgainstInt()); break;
						case 10: cell.setCellValue(rec.getTacklesInt()); break;
						case 11: cell.setCellValue(rec.getGoalsInt()); break;
						case 12: cell.setCellValue(rec.getBehindsInt()); break;
						case 13: if(rec.isDnp()) {
									cell.setCellValue(rec.getScore()); break;
								} else {
									cell.setCellValue(rec.getScoreInt()); break;
								}
						case 14: cell.setCellValue(rec.getPredicted()); break;
						case 15: cell.setCellValue(rec.getTrendInt()); break;
					}
				} else {
					switch(i) {
						case 0: cell.setCellValue(rec.getNo()); break;
						case 1: cell.setCellValue(rec.getPlayer()); break;
						case 2: cell.setCellValue(rec.getPosition()); break;
						case 3: cell.setCellValue(rec.getKicks()); break;
						case 4: cell.setCellValue(rec.getHandballs()); break;
						case 5: cell.setCellValue(rec.getDisposals()); break;
						case 6: cell.setCellValue(rec.getMarks()); break;
						case 7: cell.setCellValue(rec.getHitouts()); break;
						case 8: cell.setCellValue(rec.getFreesFor()); break;
						case 9: cell.setCellValue(rec.getFreesAgainst()); break;
						case 10: cell.setCellValue(rec.getTackles()); break;
						case 11: cell.setCellValue(rec.getGoals()); break;
						case 12: cell.setCellValue(rec.getBehinds()); break;
						case 13: cell.setCellValue(rec.getScore()); break;
						case 14: cell.setCellValue(rec.getPredicted()); break;
						case 15: cell.setCellValue(rec.getTrend()); break;
					}
				}
			}
			
			currentStatsRow++;
		}
		
		int totalsCellBase = fixtureSheetHeader.length - 4;
		
		XSSFRow row = sheet.createRow(sheet.getLastRowNum()+1);
		XSSFCell cell = row.createCell(totalsCellBase);
		cell.setCellValue("Total");
		cell.setCellStyle(style);
		
		cell = row.createCell(totalsCellBase + 1);
		cell.setCellValue(teamScores.get(fixture.getHomeTeam()).getScore());
		cell.setCellStyle(style);
		
		if(playersPlayedCount.get(fixture.getHomeTeam()) == selectedPlayersCount.get(fixture.getHomeTeam())) {
			int teamPredictedScore = teamPredictedScores.get(fixture.getHomeTeam()).getPredictedScore();
			cell = row.createCell(totalsCellBase + 2);
			cell.setCellValue(teamPredictedScore);
			cell.setCellStyle(style);
		} else {
			cell = row.createCell(totalsCellBase + 2);
			cell.setCellValue(currentPredictedTeamScores.get(fixture.getHomeTeam()));
			cell.setCellStyle(style);
		}
		
		cell = row.createCell(totalsCellBase + 3);
		cell.setCellValue(homeTotalTrend);
		cell.setCellStyle(style);
		
		totalsCellBase = totalsCellBase + fixtureSheetHeader.length + 1;
		
		cell = row.createCell(totalsCellBase);
		cell.setCellValue("Total");
		cell.setCellStyle(style);
		
		cell = row.createCell(totalsCellBase + 1);
		cell.setCellValue(teamScores.get(fixture.getAwayTeam()).getScore());
		cell.setCellStyle(style);
		
		if(playersPlayedCount.get(fixture.getAwayTeam()) == selectedPlayersCount.get(fixture.getAwayTeam())) {
			int teamPredictedScore = teamPredictedScores.get(fixture.getAwayTeam()).getPredictedScore();
			cell = row.createCell(totalsCellBase + 2);
			cell.setCellValue(teamPredictedScore);
			cell.setCellStyle(style);
		} else {
			cell = row.createCell(totalsCellBase + 2);
			cell.setCellValue(currentPredictedTeamScores.get(fixture.getAwayTeam()));
			cell.setCellStyle(style);
		};
		
		cell = row.createCell(totalsCellBase + 3);
		cell.setCellValue(awayTotalTrend);
		cell.setCellStyle(style);
	}
	
	private void emailReport(String reportName, int round, boolean isFinal) throws Exception {
		
		String dflMngrEmail = globalsService.getEmailConfig().get("dflmngrEmailAddr");
		
		String subject = "";
		String body = "";
		
		if(isFinal) {
			subject = "Results for DFL round " + round + " - FINAL";
			body = createEmailBodyFinal(round);
		} else {
			subject = "Stats for DFL round " + round + " - CURRENT";
			body = createEmailBody(round);
		}
		
		List<String> to = new ArrayList<>();

		if(emailOverride != null && !emailOverride.equals("")) {
			to.add(emailOverride);
		} else {
			List<DflTeam> teams = dflTeamService.findAll();
			for(DflTeam team : teams) {
				to.add(team.getCoachEmail());
			}
		}
		
		List<String> attachments = new ArrayList<>();
		attachments.add(reportName);
		
		loggerUtils.log("info", "Emailing to={}; reportName={}", to, reportName);
		EmailUtils.sendHtmlEmail(to, dflMngrEmail, subject, body, attachments);
	}
	
	private String createEmailBodyFinal(int round) {
		
		String body = "<html>";
		body = body + "<body>\n";
		body = "<p>Results for round " + round + ":</p>\n";
		body = body + "<p><ul type=none>\n";
				
		for(DflFixture fixture : roundFixtures) {
			DflTeam homeTeam = dflTeamService.get(fixture.getHomeTeam());
			int homeTeamScore = teamScores.get(fixture.getHomeTeam()).getScore();
			int homeTeamPredictedScore = teamPredictedScores.get(fixture.getHomeTeam()).getPredictedScore();
			
			DflTeam awayTeam = dflTeamService.get(fixture.getAwayTeam());
			int awayTeamScore = teamScores.get(fixture.getAwayTeam()).getScore();
			int awayTeamPredictedScore = teamPredictedScores.get(fixture.getAwayTeam()).getPredictedScore();
			
			String resultString = "";
			if(homeTeamScore > awayTeamScore) {
				resultString = " defeated ";
			} else {
				resultString = " defeated by ";
			}
			
			body = body + "<li>" + homeTeam.getName() + " " + homeTeamScore + " (" + homeTeamPredictedScore + ") " + 
				   resultString + awayTeam.getName() + " " + awayTeamScore + " (" + awayTeamPredictedScore + ") " + "</li>\n";
		}
		
		body = body + "</ul></p>\n";
		
		List<DflLadder> ladder = dflLadderService.getLadderForRound(round);
		Collections.sort(ladder, Collections.reverseOrder());
								
		body = body + "<p>Ladder:</p>\n";
		body = body + "<p><table border=1 style=\"border-collapse: collapse; border: 1px solid black;\">\n";
		body = body + "<tr>\n";
		body = body + "<th align=left style=\"border: 1px solid black; padding: 1px 5px 1px 5px;\">Team</th><th style=\"border: 1px solid black; padding: 1px 5px 1px 5px;\">W</th>"
				    + "<th style=\"border: 1px solid black; padding: 1px 5px 1px 5px;\">L</th><th style=\"border: 1px solid black; padding: 1px 5px 1px 5px;\">D</th>"
				    + "<th style=\"border: 1px solid black; padding: 1px 5px 1px 5px;\">For</th><th style=\"border: 1px solid black; padding: 1px 5px 1px 5px;\">Av</th>"
				    + "<th style=\"border: 1px solid black; padding: 1px 5px 1px 5px;\">Agst</th><th style=\"border: 1px solid black; padding: 1px 5px 1px 5px;\">Av</th>"
				    + "<th style=\"border: 1px solid black; padding: 1px 5px 1px 5px;\">Pts</th><th style=\"border: 1px solid black; padding: 1px 5px 1px 5px;\">%</th>";
		body = body + "</tr>\n";
		
		String tableFormat = "<td style=\"border: 1px solid black; padding: 1px 5px 1px 5px;\">%s</td><td align=right style=\"border: 1px solid black; padding: 1px 5px 1px 5px;\">%d</td>"
				           + "<td align=right style=\"border: 1px solid black; padding: 1px 5px 1px 5px;\">%d</td><td align=right style=\"border: 1px solid black; padding: 1px 5px 1px 5px;\">%d</td>"
				           + "<td align=right style=\"border: 1px solid black; padding: 1px 5px 1px 5px;\">%d</td><td align=right style=\"border: 1px solid black; padding: 1px 5px 1px 5px;\">%.2f</td>"
				           + "<td align=right style=\"border: 1px solid black; padding: 1px 5px 1px 5px;\">%d</td><td align=right style=\"border: 1px solid black; padding: 1px 5px 1px 5px;\">%.2f</td>"
				           + "<td align=right style=\"border: 1px solid black; padding: 1px 5px 1px 5px;\">%d</td><td align=right style=\"border: 1px solid black; padding: 1px 5px 1px 5px;\">%.2f</td>"
				           + "%n";
		
		for(DflLadder team : ladder) {
			String teamName = dflTeamService.get(team.getTeamCode()).getName();
			
			body = body + "<tr>\n";
			body = body + String.format(tableFormat, teamName, team.getWins(), team.getLosses(), team.getDraws(), team.getPointsFor(), team.getAverageFor(),
					          team.getPointsAgainst(), team.getAverageAgainst(), team.getPts(), team.getPercentage());
			body = body + "</tr>\n";
		}
		
		body = body + "</table></p>\n";
		body = body + "<p>Results attached.</p>\n";
		body = body + "<p>DFL Manager Admin</p>\n";
		body = body + "</div></body></html>";
		
		return body;
	}
	
	private String createEmailBody(int round) {
		
		String body = "<html>";
		body = body + "<body>\n";
		body = "<p>Current scores for round " + round + ":</p>\n";
		body = body + "<p><ul type=none>\n";
		
		for(DflFixture fixture : roundFixtures) {
			DflTeam homeTeam = dflTeamService.get(fixture.getHomeTeam());
			int homeTeamScore = teamScores.get(fixture.getHomeTeam()).getScore();
						
			int homeTeamPredictedScore = 0;
			if(playersPlayedCount.get(fixture.getHomeTeam()) == selectedPlayersCount.get(fixture.getHomeTeam())) {
				homeTeamPredictedScore = teamPredictedScores.get(fixture.getHomeTeam()).getPredictedScore();
			} else {
				homeTeamPredictedScore = currentPredictedTeamScores.get(fixture.getHomeTeam());
			}
			
			int homePlayersPlayed = playersPlayedCount.get(fixture.getHomeTeam());
			int homeSelectedSize = selectedPlayersCount.get(fixture.getHomeTeam());
			
			DflTeam awayTeam = dflTeamService.get(fixture.getAwayTeam());
			int awayTeamScore = teamScores.get(fixture.getAwayTeam()).getScore();
			
			int awayTeamPredictedScore = 0;
			if(playersPlayedCount.get(fixture.getAwayTeam()) == selectedPlayersCount.get(fixture.getAwayTeam())) {
				awayTeamPredictedScore = teamPredictedScores.get(fixture.getAwayTeam()).getPredictedScore();
			} else {
				awayTeamPredictedScore = currentPredictedTeamScores.get(fixture.getAwayTeam());
			}
			
			int awayPlayersPlayed = playersPlayedCount.get(fixture.getAwayTeam());
			int awaySelectedSize = selectedPlayersCount.get(fixture.getAwayTeam());
			
			String resultString = "";
			if(homeTeamScore > awayTeamScore) {
				resultString = " leading ";
			} else {
				resultString = " lead by ";
			}
			
			body = body + "<li>" 
				   + homeTeam.getName() + " " + homeTeamScore + " (" + homeTeamPredictedScore + " - " + homePlayersPlayed + "/" + homeSelectedSize + " played)"
				   + resultString 
				   + awayTeam.getName() + " " + awayTeamScore + " (" + awayTeamPredictedScore + " - " + awayPlayersPlayed + "/" + awaySelectedSize + " played)"
				   + "</li>\n";
		}
		
		body = body + "</ul></p>\n";
		
		List<DflLadder> ladder = dflLadderService.getLadderForRound(round);
		Collections.sort(ladder, Collections.reverseOrder());
								
		body = body + "<p>Live Ladder:</p>\n";
		body = body + "<p><table border=1 style=\"border-collapse: collapse; border: 1px solid black;\">\n";
		body = body + "<tr>\n";
		body = body + "<th align=left style=\"border: 1px solid black; padding: 1px 5px 1px 5px;\">Team</th>"
				    + "<th style=\"border: 1px solid black; padding: 1px 5px 1px 5px;\">Pts</th><th style=\"border: 1px solid black; padding: 1px 5px 1px 5px;\">%</th>";
		body = body + "</tr>\n";
		
		String tableFormat = "<td style=\"border: 1px solid black; padding: 1px 5px 1px 5px;\">%s</td>"
				           + "<td align=right style=\"border: 1px solid black; padding: 1px 5px 1px 5px;\">%d</td><td align=right style=\"border: 1px solid black; padding: 1px 5px 1px 5px;\">%.2f</td>"
				           + "%n";
		
		for(DflLadder team : ladder) {
			String teamName = dflTeamService.get(team.getTeamCode()).getName();
			
			body = body + "<tr>\n";
			body = body + String.format(tableFormat, teamName, team.getPts(), team.getPercentage());
			body = body + "</tr>\n";
		}
		
		body = body + "</table></p>\n";
		body = body + "<p>Results attached.</p>\n";
		body = body + "<p>DFL Manager Admin</p>\n";
		body = body + "</div></body></html>";
		
		return body;
	}
	
	public static void main(String[] args) {
		
		try {
			String email = null;
			int round = 0;
			boolean isFinal = false;
			
			if(args.length > 3 || args.length < 1) {
				System.out.println("usage: RawStatsReport <round> optional [Final <email>]");
			} else {
				
				round = Integer.parseInt(args[0]);
				
				if(args.length == 2) {
					if(args[1].equalsIgnoreCase("Final")) {
						isFinal = true;
					} else {
						email = args[1];
					}
				} else if(args.length == 3) {
					if(args[1].equalsIgnoreCase("Final")) {
						isFinal = true;
						email = args[2];
					} else if(args[2].equalsIgnoreCase("Final")) {
						isFinal = true;
						email = args[1];
					} else {
						System.out.println("usage: RawStatsReport <round> optional [Final <email>]");
					}
				}
				
				JndiProvider.bind();
				
				ResultsReport report = new ResultsReport();
				report.configureLogging("batch.name", "batch-logger", "ResultsReport");
				report.execute(round, isFinal, email);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
