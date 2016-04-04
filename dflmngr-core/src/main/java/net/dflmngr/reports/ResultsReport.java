package net.dflmngr.reports;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
import net.dflmngr.model.entity.AflFixture;
import net.dflmngr.model.entity.DflFixture;
import net.dflmngr.model.entity.DflPlayer;
import net.dflmngr.model.entity.DflPlayerScores;
import net.dflmngr.model.entity.DflSelectedPlayer;
import net.dflmngr.model.entity.DflTeam;
import net.dflmngr.model.entity.DflTeamScores;
import net.dflmngr.model.entity.RawPlayerStats;
import net.dflmngr.model.service.AflFixtureService;
import net.dflmngr.model.service.DflFixtureService;
import net.dflmngr.model.service.DflPlayerScoresService;
import net.dflmngr.model.service.DflPlayerService;
import net.dflmngr.model.service.DflSelectedTeamService;
import net.dflmngr.model.service.DflTeamScoresService;
import net.dflmngr.model.service.DflTeamService;
import net.dflmngr.model.service.GlobalsService;
import net.dflmngr.model.service.RawPlayerStatsService;
import net.dflmngr.model.service.impl.AflFixtureServiceImpl;
import net.dflmngr.model.service.impl.DflFixtureServiceImpl;
import net.dflmngr.model.service.impl.DflPlayerScoresServiceImpl;
import net.dflmngr.model.service.impl.DflPlayerServiceImpl;
import net.dflmngr.model.service.impl.DflSelectedTeamServiceImpl;
import net.dflmngr.model.service.impl.DflTeamScoresServiceImpl;
import net.dflmngr.model.service.impl.DflTeamServiceImpl;
import net.dflmngr.model.service.impl.GlobalsServiceImpl;
import net.dflmngr.model.service.impl.RawPlayerStatsServiceImpl;
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
	
	String emailOverride;
	
	String[] resultsSpreadsheetHeaders = {"Player", "D", "M", "HO", "FF", "FA", "T", "G"};
	String[] fixtureSheetHeader = {"No.", "Player", "Pos", "K", "H", "D", "M", "HO", "FF", "FA", "T", "G", "B", "Score"};
	
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
			
			Map<String, RawPlayerStats> playerStats = rawPlayerStatsService.getForRoundWithKey(round);
			Map<Integer, DflPlayerScores> playerScores = dflPlayerScoresService.getForRoundWithKey(round);
			Map<String, DflTeamScores> teamScores = dflTeamScoresService.getForRoundWithKey(round);
			
			List<DflFixture> roundFixtures = dflFixtureService.getFixturesForROund(round);
			
			String reportName = writeReport(round, isFinal, playerStats, playerScores, teamScores, roundFixtures);
			
			loggerUtils.log("info", "Sending Report");
			emailReport(reportName, round, isFinal, roundFixtures, teamScores);
			
			rawPlayerStatsService.close();
			dflPlayerScoresService.close();
			dflTeamScoresService.close();
			dflFixtureService.close();
			dflSelectedTeamService.close();
			globalsService.close();
			dflTeamService.close();
			dflPlayerService.close();
			aflFixtureService.close();
			
			loggerUtils.log("info", "ResultsReport Completed");
			
		} catch (Exception ex) {
			loggerUtils.log("error", "Error in ... ", ex);
		}
		
	}
	
	
	private String writeReport(int round, boolean isFinal, Map<String, RawPlayerStats> playerStats, Map<Integer, DflPlayerScores> playerScores, Map<String, DflTeamScores> teamScores, List<DflFixture> roundFixtures) throws Exception {
		
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
		writeResultsSpreadsheetStats(sheet, playerStats);
		
		for(DflFixture fixture : roundFixtures) {
			sheet = workbook.createSheet(fixture.getHomeTeam() + " vs " + fixture.getAwayTeam());
			writeFixtureSheetHeaders(sheet, fixture);
			writeFixtureSheetData(sheet, fixture, playerStats, playerScores, teamScores);
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
	
	private void writeResultsSpreadsheetStats(XSSFSheet sheet, Map<String, RawPlayerStats> playerStats) {
		
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
		
		sheet.addMergedRegion(new CellRangeAddress(0,0,0,13));
		sheet.addMergedRegion(new CellRangeAddress(0,0,15,28));
		
		row = sheet.createRow(1);
		
		for(int i = 0; i < this.fixtureSheetHeader.length; i++) {
			cell = row.createCell(i);
			cell.setCellValue(this.fixtureSheetHeader[i]);
			cell.setCellStyle(style);
		}
		
		cell = row.createCell(14);
		cell.setCellValue("");
		
		for(int i = 15; i - 15 < this.fixtureSheetHeader.length; i++) {
			cell = row.createCell(i);
			cell.setCellValue(this.fixtureSheetHeader[i - 15]);
			cell.setCellStyle(style);
		}
	}
	
	private void writeFixtureSheetData(XSSFSheet sheet, DflFixture fixture, Map<String, RawPlayerStats> playerStats, Map<Integer, DflPlayerScores> playerScores, Map<String, DflTeamScores> teamScores) {
		
		loggerUtils.log("info", "Writing fixture sheet data");
		
		XSSFCellStyle style = sheet.getWorkbook().createCellStyle();
		XSSFFont font = sheet.getWorkbook().createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		style.setFont(font);
		
		List<DflSelectedPlayer> selectedHomeTeam = dflSelectedTeamService.getSelectedTeamForRound(fixture.getRound(), fixture.getHomeTeam());
		List<DflSelectedPlayer> selectedAwayTeam = dflSelectedTeamService.getSelectedTeamForRound(fixture.getRound(), fixture.getAwayTeam());
		
		List<AflFixture> playedFixtures = aflFixtureService.getAflFixturesPlayedForRound(fixture.getRound());
		
		int currentStatsRow = sheet.getLastRowNum()+1;
		
		for(DflSelectedPlayer selectedPlayer : selectedHomeTeam) {
			
			DflPlayerScores score = playerScores.get(selectedPlayer.getPlayerId());
			
			XSSFRow row = sheet.createRow(sheet.getLastRowNum()+1);

			DflPlayer player = dflPlayerService.get(selectedPlayer.getPlayerId());
			String playerName = player.getFirstName() + " " + player.getLastName();
			
			if(score == null) {
				
				boolean played = false;
				for(AflFixture playedFixture : playedFixtures) {
					String aflTeam = player.getAflClub();
					if(DflmngrUtils.dflAflTeamMap.get(aflTeam).equals(playedFixture.getHomeTeam())) {
						played = true;
						break;
					}
					if(DflmngrUtils.dflAflTeamMap.get(aflTeam).equals(playedFixture.getAwayTeam())) {
						played = true;
						break;
					}
				}
				
				for(int i = 0; i < this.fixtureSheetHeader.length; i++) {
					XSSFCell cell = row.createCell(i);
					
					switch(i) {
						case 0: cell.setCellValue(selectedPlayer.getTeamPlayerId()); break;
						case 1: cell.setCellValue(playerName); break;
						case 2: cell.setCellValue(player.getPosition()); break;
						case 3: cell.setCellValue(""); break;
						case 4: cell.setCellValue(""); break;
						case 5: cell.setCellValue(""); break;
						case 6: cell.setCellValue(""); break;
						case 7: cell.setCellValue(""); break;
						case 8: cell.setCellValue(""); break;
						case 9: cell.setCellValue(""); break;
						case 10: cell.setCellValue(""); break;
						case 11: cell.setCellValue(""); break;
						case 12: cell.setCellValue(""); break;
						case 13: if(played) {
									cell.setCellValue("dnp");
								} else {
									cell.setCellValue(0);
								}
								break;
					}
				}
			} else {
				
				RawPlayerStats stats =  playerStats.get(score.getAflPlayerId());
				
				for(int i = 0; i < this.fixtureSheetHeader.length; i++) {
					XSSFCell cell = row.createCell(i);
					
					switch(i) {
						case 0: cell.setCellValue(selectedPlayer.getTeamPlayerId()); break;
						case 1: cell.setCellValue(stats.getName()); break;
						case 2: cell.setCellValue(player.getPosition()); break;
						case 3: cell.setCellValue(stats.getKicks()); break;
						case 4: cell.setCellValue(stats.getHandballs()); break;
						case 5: cell.setCellValue(stats.getDisposals()); break;
						case 6: cell.setCellValue(stats.getMarks()); break;
						case 7: cell.setCellValue(stats.getHitouts()); break;
						case 8: cell.setCellValue(stats.getFreesFor()); break;
						case 9: cell.setCellValue(stats.getFreesAgainst()); break;
						case 10: cell.setCellValue(stats.getTackles()); break;
						case 11: cell.setCellValue(stats.getGoals()); break;
						case 12: cell.setCellValue(stats.getBehinds()); break;
						case 13: cell.setCellValue(score.getScore()); break;
					}
				}
			}
		}
		
		for(DflSelectedPlayer selectedPlayer : selectedAwayTeam) {
			//DflPlayer player = dflPlayerService.get(selectedPlayer.getPlayerId());
			DflPlayerScores score = playerScores.get(selectedPlayer.getPlayerId());
			
			XSSFRow row = sheet.getRow(currentStatsRow);
			if(row == null) {
				row = sheet.createRow(currentStatsRow);
			}

			DflPlayer player = dflPlayerService.get(selectedPlayer.getPlayerId());
			String playerName = player.getFirstName() + " " + player.getLastName();
			
			if(score == null) {
				
				boolean played = false;
				for(AflFixture playedFixture : playedFixtures) {
					String aflTeam = player.getAflClub();
					if(DflmngrUtils.dflAflTeamMap.get(aflTeam).equals(playedFixture.getHomeTeam())) {
						played = true;
						break;
					}
					if(DflmngrUtils.dflAflTeamMap.get(aflTeam).equals(playedFixture.getAwayTeam())) {
						played = true;
						break;
					}	
				}
				
				for(int i = 0; i < this.fixtureSheetHeader.length; i++) {
					XSSFCell cell = row.createCell(i+15);
					
					switch(i) {
					case 0: cell.setCellValue(selectedPlayer.getTeamPlayerId()); break;
					case 1: cell.setCellValue(playerName); break;
					case 2: cell.setCellValue(player.getPosition()); break;
					case 3: cell.setCellValue(""); break;
					case 4: cell.setCellValue(""); break;
					case 5: cell.setCellValue(""); break;
					case 6: cell.setCellValue(""); break;
					case 7: cell.setCellValue(""); break;
					case 8: cell.setCellValue(""); break;
					case 9: cell.setCellValue(""); break;
					case 10: cell.setCellValue(""); break;
					case 11: cell.setCellValue(""); break;
					case 12: cell.setCellValue(""); break;
					case 13: if(played) {
									cell.setCellValue("dnp");
								} else {
									cell.setCellValue(0);
								}
								break;
					}
				}
			} else {
				
				RawPlayerStats stats =  playerStats.get(score.getAflPlayerId());
				
				for(int i = 0; i < this.fixtureSheetHeader.length; i++) {
					XSSFCell cell = row.createCell(i+15);
					
					switch(i) {
						case 0: cell.setCellValue(selectedPlayer.getTeamPlayerId()); break;
						case 1: cell.setCellValue(stats.getName()); break;
						case 2: cell.setCellValue(player.getPosition()); break;
						case 3: cell.setCellValue(stats.getKicks()); break;
						case 4: cell.setCellValue(stats.getHandballs()); break;
						case 5: cell.setCellValue(stats.getDisposals()); break;
						case 6: cell.setCellValue(stats.getMarks()); break;
						case 7: cell.setCellValue(stats.getHitouts()); break;
						case 8: cell.setCellValue(stats.getFreesFor()); break;
						case 9: cell.setCellValue(stats.getFreesAgainst()); break;
						case 10: cell.setCellValue(stats.getTackles()); break;
						case 11: cell.setCellValue(stats.getGoals()); break;
						case 12: cell.setCellValue(stats.getBehinds()); break;
						case 13: cell.setCellValue(score.getScore()); break;
					}
				}
			}
			
			currentStatsRow++;
		}
		
		XSSFRow row = sheet.createRow(sheet.getLastRowNum()+1);
		XSSFCell cell = row.createCell(12);
		cell.setCellValue("Total");
		cell.setCellStyle(style);
		
		cell = row.createCell(13);
		cell.setCellValue(teamScores.get(fixture.getHomeTeam()).getScore());
		cell.setCellStyle(style);
		
		cell = row.createCell(27);
		cell.setCellValue("Total");
		cell.setCellStyle(style);
		
		cell = row.createCell(28);
		cell.setCellValue(teamScores.get(fixture.getAwayTeam()).getScore());
		cell.setCellStyle(style);
	}
	
	private void emailReport(String reportName, int round, boolean isFinal, List<DflFixture> roundFixtures, Map<String, DflTeamScores> teamScores) throws Exception {
		
		String dflMngrEmail = globalsService.getEmailConfig().get("dflmngrEmailAddr");
		
		String subject = "";
		String body = "";
		
		if(isFinal) {
			subject = "Results for DFL round " + round + " - FINAL";
			body = createEmailBodyFinal(round, roundFixtures, teamScores);
		} else {
			subject = "Stats for DFL round " + round + " - CURRENT";
			body = createEmailBody(round, roundFixtures, teamScores);
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
		EmailUtils.send(to, dflMngrEmail, subject, body, attachments);
	}
	
	private String createEmailBodyFinal(int round, List<DflFixture> roundFixtures, Map<String, DflTeamScores> teamScores) {
		String body = "Results for round " + round + ":\n\n";
		
		for(DflFixture fixture : roundFixtures) {
			DflTeam homeTeam = dflTeamService.get(fixture.getHomeTeam());
			int homeTeamScore = teamScores.get(fixture.getHomeTeam()).getScore();
			
			DflTeam awayTeam = dflTeamService.get(fixture.getAwayTeam());
			int awayTeamScore = teamScores.get(fixture.getAwayTeam()).getScore();
			
			String resultString = "";
			if(homeTeamScore > awayTeamScore) {
				resultString = " defeated ";
			} else {
				resultString = " defeated by ";
			}
			
			body = body + "\t" + homeTeam.getName() + " " + homeTeamScore + resultString + awayTeam.getName() + " " + awayTeamScore + "\n";
		}
		
		body = body + "\n" + "Results attached.\n\nDfl Manager Admin";
		
		return body;
	}
	
	private String createEmailBody(int round, List<DflFixture> roundFixtures, Map<String, DflTeamScores> teamScores) {
		String body = "Current scores for round " + round + ":\n\n";
		
		for(DflFixture fixture : roundFixtures) {
			DflTeam homeTeam = dflTeamService.get(fixture.getHomeTeam());
			int homeTeamScore = teamScores.get(fixture.getHomeTeam()).getScore();
			
			DflTeam awayTeam = dflTeamService.get(fixture.getAwayTeam());
			int awayTeamScore = teamScores.get(fixture.getAwayTeam()).getScore();
			
			String resultString = "";
			if(homeTeamScore > awayTeamScore) {
				resultString = " leading ";
			} else {
				resultString = " lead by ";
			}
			
			body = body + "\t" + homeTeam.getName() + " " + homeTeamScore + resultString + awayTeam.getName() + " " + awayTeamScore + "\n";
		}
		
		body = body + "\n" + "Current scores attached.\n\nDfl Manager Admin";
		
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
