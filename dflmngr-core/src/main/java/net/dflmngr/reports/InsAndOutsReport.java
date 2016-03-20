package net.dflmngr.reports;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import net.dflmngr.model.DomainDecodes;
import net.dflmngr.model.entity.InsAndOuts;
import net.dflmngr.model.service.GlobalsService;
import net.dflmngr.model.service.InsAndOutsService;
import net.dflmngr.model.service.impl.GlobalsServiceImpl;
import net.dflmngr.model.service.impl.InsAndOutsServiceImpl;
import net.dflmngr.utils.DflmngrUtils;
import net.dflmngr.utils.EmailUtils;

public class InsAndOutsReport {
	private Logger logger;
	
	GlobalsService globalsService;
	InsAndOutsService insAndOutsService;
	
	String reportType;
		
	public InsAndOutsReport() {
		
		MDC.put("online.name", "InsAndOutsReport");
		logger = LoggerFactory.getLogger("online-logger");
		
		globalsService = new GlobalsServiceImpl();
		insAndOutsService = new InsAndOutsServiceImpl();
	}
	
	public void execute(int round, String reportType) {
		
		try {
			logger.info("Executing InsAndOutsReport for rounds: {}, report type: {}", round, reportType);
					
			List<String> teams = globalsService.getTeamCodes();
			Map<String, List<Integer>> ins = new  HashMap<>();
			Map<String, List<Integer>> outs = new  HashMap<>();
			
			this.reportType = reportType;
			
			logger.info("Team codes: {}", teams);
			
			for(String teamCode : teams) {
				List<Integer> teamIns = new ArrayList<>();
				List<Integer> teamOuts = new ArrayList<>();
				
				List<InsAndOuts> teamInsAndOuts = insAndOutsService.getByTeamAndRound(round, teamCode);
				
				for(InsAndOuts inOrOut : teamInsAndOuts) {
					if(inOrOut.getInOrOut().equals(DomainDecodes.INS_AND_OUTS.IN_OR_OUT.IN)) {
						teamIns.add(inOrOut.getTeamPlayerId());
					} else {
						teamOuts.add(inOrOut.getTeamPlayerId());
					}
				}
				
				logger.info("{} ins: {}", teamCode, teamIns);
				
				if(round == 1) {
					logger.info("{} no outs round 1", teamCode);
				} else {
					logger.info("{} outs: {}", teamCode, teamOuts);
				}
				
				ins.put(teamCode, teamIns);
				outs.put(teamCode, teamOuts);
			}
	
			String report = writeReport(teams, round, ins, outs);
			
			if(reportType.equals("Full")) {
				emailReport(report, round, true);
			} else {
				emailReport(report, round, false);
			}
			
			globalsService.close();
			insAndOutsService.close();
			
		} catch (Exception ex) {
			logger.error("Error in ... ", ex);
		} finally {
			MDC.remove("online.name");
		}
	}
	
	private String writeReport(List<String> teams, int round, Map<String, List<Integer>> ins, Map<String, List<Integer>> outs) throws Exception {
		
		String reportName = "InsAndOutsReport_" + this.reportType + "_" + DflmngrUtils.getNowStr() + ".xlsx";
		Path reportLocation = Paths.get(globalsService.getAppDir(), globalsService.getReportDir(), "insAndOutsReport", reportName);
		
		logger.info("Writing insAndOuts Report");
		logger.info("Report name: {}", reportName);
		logger.info("Report location: {}", reportLocation);
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Ins And Outs");
		
		setupReportRows(sheet, teams);
		addSelectionsToReport(sheet, teams, ins, "Ins");
		
		if(round > 1) {
			addSelectionsToReport(sheet, teams, outs, "Outs");
		}
		
		OutputStream out = Files.newOutputStream(reportLocation);
		workbook.write(out);
		workbook.close();
		out.close();
		
		return reportLocation.toString();
	}
	
	private void setupReportRows(XSSFSheet sheet, List<String> teams) {
		
		logger.info("Initlizing report rows");
		
		for(int i = 0; i < 25; i++) {
			sheet.createRow(i);
		}

		XSSFRow row = sheet.getRow(0);
		
		int columnIndex = 1;
		for(String team : teams) {
			XSSFCell cell = row.createCell(columnIndex++);
			cell.setCellValue(team);
		}
	}
	
	private void addSelectionsToReport(XSSFSheet sheet, List<String> teams, Map<String, List<Integer>> selections, String selectionType) {
		
		XSSFRow row;
		XSSFCell cell;
		
		logger.info("Writing report data for: {}", selectionType);
		
		if(selectionType.equals("Ins")) {
			row = sheet.getRow(1);
			cell = row.createCell(0);
			cell.setCellValue("In");
		} else {
			row = sheet.getRow(13);
			cell = row.createCell(0);
			cell.setCellValue("Out");
		}
		
		for(String team : teams) {
			
			row = sheet.getRow(0);
			Iterator<Cell> cellIterator = row.cellIterator();
			
			int columnIndex = 1;
			while(cellIterator.hasNext()) {
				cell = (XSSFCell) cellIterator.next();
				
				if(cell.getStringCellValue().equals(team)) {
					break;
				}
				
				columnIndex++;
			}
			
			List<Integer> teamSelections = selections.get(team);
			
			int rowIndex = 1;
			if(selectionType.equals("Outs")) {
				rowIndex = 13;
			}
			
			for(Integer selection : teamSelections) {
				row = sheet.getRow(rowIndex++);
				cell = row.getCell(columnIndex, Row.CREATE_NULL_AS_BLANK);
				cell.setCellValue(selection);
			}		
		}
	}
	
	private void emailReport(String reportName, int round, boolean isFinal) throws Exception {
		
		String dflMngrEmail = globalsService.getEmailConfig().get("dflmngrEmailAddr");
		String dflGroupEmail = globalsService.getEmailConfig().get("dflgroupEmailAddr");
		
		String subject = "";
		String body = "";
		
		if(isFinal) {
			subject = "Ins and Outs for DFL round " + round + " - FULL";
			body = "Please find attached the full ins and outs for round " + round + ".\n\n" +
				   "DFL Manager Admin";
		} else {
			subject = "Ins and Outs for DFL round " + round + " - PARTIAL";
			body = "Please find attached the partial ins and outs for round " + round + ". Team updates can still be made, further updates will be sent.\n\n" +
				   "DFL Manager Admin";
		}
		
		List<String> to = new ArrayList<>();
		to.add(dflGroupEmail);
		
		List<String> attachments = new ArrayList<>();
		attachments.add(reportName);
		
		EmailUtils.send(to, dflMngrEmail, subject, body, attachments);
	}
	
	// For internal testing
	public static void main(String[] args) {
		
		try {
			InsAndOutsReport testing = new InsAndOutsReport();
			testing.execute(1, "Full");
			testing = new InsAndOutsReport();
			testing.execute(1, "Partial");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
