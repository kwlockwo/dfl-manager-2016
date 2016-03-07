package net.dflmngr.reports;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dflmngr.handlers.RawPlayerStatsHandler;
import net.dflmngr.model.entity.RawPlayerStats;
import net.dflmngr.model.service.GlobalsService;
import net.dflmngr.model.service.RawPlayerStatsService;
import net.dflmngr.model.service.impl.GlobalsServiceImpl;
import net.dflmngr.model.service.impl.RawPlayerStatsServiceImpl;
import net.dflmngr.utils.DflmngrUtils;
import net.dflmngr.utils.EmailUtils;

public class RawStatsReport {
	private static final Logger logger = LoggerFactory.getLogger("databaseLogger");
	
	RawPlayerStatsService rawPlayerStatsService;
	GlobalsService globalsService;
	
	String[] headers = {"Player", "D", "M", "HO", "FF", "FA", "T", "G"};

	
	public RawStatsReport() {
		rawPlayerStatsService = new RawPlayerStatsServiceImpl();
		globalsService = new GlobalsServiceImpl();
	}
	
	public void execute(int round, boolean isFinal) throws Exception {
		
		logger.info("Executing RawStatsReport for rounds: {}, is final: {}", round, isFinal);
		
		RawPlayerStatsHandler rawPlayerStatsHandler = new RawPlayerStatsHandler();
		rawPlayerStatsHandler.execute(round);
		
		List<RawPlayerStats> playerStats = rawPlayerStatsService.getForRound(round);
		
		String reportName = writeReport(round, isFinal, playerStats);
		
		emailReport(reportName, round, isFinal);
		
		logger.info("RawStatsReport Completed");
	}
	
	private String writeReport(int round, boolean isFinal, List<RawPlayerStats> playerStats) throws Exception {
		
		String reportName = "";
		if(isFinal) {
			reportName = "RawStatsReport_Round_" + round + "_FINAL_" + DflmngrUtils.getNowStr() + ".xlsx";
		} else {
			reportName = "RawStatsReport_Round_" + round + "_" + DflmngrUtils.getNowStr() + ".xlsx";
		}
		Path reportLocation = Paths.get(globalsService.getAppDir(), globalsService.getReportDir(), "insAndOutsReport", reportName);
		
		logger.info("Writing rawStatsReport Report");
		logger.info("Report name: {}", reportName);
		logger.info("Report location: {}", reportLocation);
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Stats");
		
		writeHeaders(workbook);
		writeStats(sheet, playerStats);
		
		OutputStream out = Files.newOutputStream(reportLocation);
		workbook.write(out);
		workbook.close();
		out.close();
		
		return reportLocation.toString();
	}
	
	private void writeHeaders(XSSFWorkbook workbook) {
		
		logger.info("Writing Header Rows");
		
		XSSFSheet sheet = workbook.getSheet("Stats");
		XSSFRow row = sheet.createRow(0);
		
		XSSFCellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		style.setFont(font);
				
		for(int i = 0; i < this.headers.length; i++) {
			XSSFCell cell = row.createCell(i);
			cell.setCellValue(this.headers[i]);
			cell.setCellStyle(style);
		}
	}
	
	private void writeStats(XSSFSheet sheet, List<RawPlayerStats> playerStats) {
		
		logger.info("Writing report data");
		
		for(RawPlayerStats stats : playerStats) {
			XSSFRow row = sheet.createRow(sheet.getLastRowNum()+1);
			
			for(int i = 0; i < this.headers.length; i++) {
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
	
	private void emailReport(String reportName, int round, boolean isFinal) throws Exception {
		
		String dflMngrEmail = globalsService.getEmailConfig().get("dflmngrEmailAddr");
		String dflGroupEmail = globalsService.getEmailConfig().get("dflgroupEmailAddr");
		
		String subject = "";
		String body = "";
		
		if(isFinal) {
			subject = "Stats for DFL round " + round + " - FINAL";
			body = "Please find attached the final stats for round " + round + ".\n\n" +
				   "DFL Manager Admin";
		} else {
			subject = "Stats for DFL round " + round + " - PARTIAL";
			body = "Please find attached the partial stats for round " + round + ". More games are still to be played, further updates will be sent.\n\n" +
				   "DFL Manager Admin";
		}
		
		List<String> to = new ArrayList<>();
		to.add(dflGroupEmail);
		
		List<String> attachments = new ArrayList<>();
		attachments.add(reportName);
		
		EmailUtils.send(to, dflMngrEmail, subject, body, attachments);
	}
	
	public static void main(String[] args) {
		
		try {
			RawStatsReport testing = new RawStatsReport();
			testing.execute(1, false);
			testing = new RawStatsReport();
			testing.execute(1, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
