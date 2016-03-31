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

import net.dflmngr.jndi.JndiProvider;
import net.dflmngr.logging.LoggingUtils;
import net.dflmngr.model.DomainDecodes;
import net.dflmngr.model.entity.DflTeam;
import net.dflmngr.model.entity.InsAndOuts;
import net.dflmngr.model.service.DflTeamService;
import net.dflmngr.model.service.GlobalsService;
import net.dflmngr.model.service.InsAndOutsService;
import net.dflmngr.model.service.impl.DflTeamServiceImpl;
import net.dflmngr.model.service.impl.GlobalsServiceImpl;
import net.dflmngr.model.service.impl.InsAndOutsServiceImpl;
import net.dflmngr.utils.DflmngrUtils;
import net.dflmngr.utils.EmailUtils;

public class InsAndOutsReport {
	private LoggingUtils loggerUtils;
	String defaultMdcKey = "online.name";
	String defaultLoggerName = "online-logger";
	String defaultLogfile = "InsAndOutsReport";
	
	String mdcKey;
	String loggerName;
	String logfile;
	
	GlobalsService globalsService;
	InsAndOutsService insAndOutsService;
	DflTeamService dflTeamService;
	
	String reportType;
	
	String emailOverride;
	boolean isExecutable;
		
	public InsAndOutsReport() {
		globalsService = new GlobalsServiceImpl();
		insAndOutsService = new InsAndOutsServiceImpl();
		dflTeamService = new DflTeamServiceImpl();
		isExecutable = false;
	}
	
	public void configureLogging(String mdcKey, String loggerName, String logfile) {
		this.mdcKey = mdcKey;
		this.loggerName = loggerName;
		this.logfile = logfile;
		
		loggerUtils = new LoggingUtils(loggerName, mdcKey, logfile);
		isExecutable = true;
	}
	
	public void execute(int round, String reportType, String emailOverride) {
		
		try {
			if(!isExecutable) {
				configureLogging(defaultMdcKey, defaultLoggerName, defaultLogfile);
				loggerUtils.log("info", "Default logging configured");
			}
			
			loggerUtils.log("info", "Executing InsAndOutsReport for rounds: {}, report type: {}", round, reportType);
			
			if(emailOverride != null && !emailOverride.equals("")) {
				this.emailOverride = emailOverride;
			}
					
			//List<String> teams = globalsService.getTeamCodes();
			List<DflTeam> teams = dflTeamService.findAll();
			Map<String, List<Integer>> ins = new  HashMap<>();
			Map<String, List<Integer>> outs = new  HashMap<>();
			
			this.reportType = reportType;
			
			loggerUtils.log("info", "Team codes: {}", teams);
			
			for(DflTeam team : teams) {
				List<Integer> teamIns = new ArrayList<>();
				List<Integer> teamOuts = new ArrayList<>();
				
				List<InsAndOuts> teamInsAndOuts = insAndOutsService.getByTeamAndRound(round, team.getTeamCode());
				
				for(InsAndOuts inOrOut : teamInsAndOuts) {
					if(inOrOut.getInOrOut().equals(DomainDecodes.INS_AND_OUTS.IN_OR_OUT.IN)) {
						teamIns.add(inOrOut.getTeamPlayerId());
					} else {
						teamOuts.add(inOrOut.getTeamPlayerId());
					}
				}
				
				loggerUtils.log("info", "{} ins: {}", team.getTeamCode(), teamIns);
				
				if(round == 1) {
					loggerUtils.log("info", "{} no outs round 1", team.getTeamCode());
				} else {
					loggerUtils.log("info", "{} outs: {}", team.getTeamCode(), teamOuts);
				}
				
				ins.put(team.getTeamCode(), teamIns);
				outs.put(team.getTeamCode(), teamOuts);
			}
	
			String report = writeReport(teams, round, ins, outs);
			
			if(reportType.equals("Full")) {
				loggerUtils.log("info", "Sending Full Report");
				emailReport(teams, report, round, true);
			} else {
				loggerUtils.log("info", "Sending Partial Report");
				emailReport(teams, report, round, false);
			}
			
			globalsService.close();
			insAndOutsService.close();
			
		} catch (Exception ex) {
			loggerUtils.log("error", "Error in ... ", ex);
		}
	}
	
	private String writeReport(List<DflTeam> teams, int round, Map<String, List<Integer>> ins, Map<String, List<Integer>> outs) throws Exception {
		
		String reportName = "InsAndOutsReport_" + this.reportType + "_" + DflmngrUtils.getNowStr() + ".xlsx";
		Path reportLocation = Paths.get(globalsService.getAppDir(), globalsService.getReportDir(), "insAndOutsReport", reportName);
		
		loggerUtils.log("info", "Writing insAndOuts Report");
		loggerUtils.log("info", "Report name: {}", reportName);
		loggerUtils.log("info", "Report location: {}", reportLocation);
		
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
	
	private void setupReportRows(XSSFSheet sheet, List<DflTeam> teams) {
		
		loggerUtils.log("info", "Initlizing report rows");
		
		for(int i = 0; i < 25; i++) {
			sheet.createRow(i);
		}

		XSSFRow row = sheet.getRow(0);
		
		int columnIndex = 1;
		for(DflTeam team : teams) {
			XSSFCell cell = row.createCell(columnIndex++);
			cell.setCellValue(team.getTeamCode());
		}
	}
	
	private void addSelectionsToReport(XSSFSheet sheet, List<DflTeam> teams, Map<String, List<Integer>> selections, String selectionType) {
		
		XSSFRow row;
		XSSFCell cell;
		
		loggerUtils.log("info", "Writing report data for: {}", selectionType);
		
		if(selectionType.equals("Ins")) {
			row = sheet.getRow(1);
			cell = row.createCell(0);
			cell.setCellValue("In");
		} else {
			row = sheet.getRow(13);
			cell = row.createCell(0);
			cell.setCellValue("Out");
		}
		
		for(DflTeam team : teams) {
			
			row = sheet.getRow(0);
			Iterator<Cell> cellIterator = row.cellIterator();
			
			int columnIndex = 1;
			while(cellIterator.hasNext()) {
				cell = (XSSFCell) cellIterator.next();
				
				if(cell.getStringCellValue().equals(team.getTeamCode())) {
					break;
				}
				
				columnIndex++;
			}
			
			List<Integer> teamSelections = selections.get(team.getTeamCode());
			
			int rowIndex = 1;
			if(selectionType.equals("Outs")) {
				rowIndex = 13;
			}
			
			for(Integer selection : teamSelections) {
					row = sheet.getRow(rowIndex++);
					if(row == null) {
						row = sheet.createRow(rowIndex-1);
					}
					cell = row.getCell(columnIndex, Row.CREATE_NULL_AS_BLANK);
					cell.setCellValue(selection);
			}		
		}
	}
	
	private void emailReport(List<DflTeam> teams, String reportName, int round, boolean isFinal) throws Exception {
		
		String dflMngrEmail = globalsService.getEmailConfig().get("dflmngrEmailAddr");
		
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

		if(emailOverride != null && !emailOverride.equals("")) {
			to.add(emailOverride);
		} else {
			for(DflTeam team : teams) {
				to.add(team.getCoachEmail());
			}
		}
		
		List<String> attachments = new ArrayList<>();
		attachments.add(reportName);
		
		loggerUtils.log("info", "Emailing to={}; reportName={}", to, reportName);
		EmailUtils.send(to, dflMngrEmail, subject, body, attachments);
	}
	
	// For internal testing
	public static void main(String[] args) {
		
		try {
			String email = "";
			int round = 0;
			String reportType = "";
			
			if(args.length > 3 || args.length < 2) {
				System.out.println("usage: RawStatsReport <round> Full|Partial optional [<email>]");
			} else {
				
				round = Integer.parseInt(args[0]);
				
				if(args.length == 2) {
					reportType = args[1];
				} else if(args.length == 3) {
					reportType = args[1];
					email = args[2];
				}
				
				JndiProvider.bind();
				
				InsAndOutsReport insAndOutsReport = new InsAndOutsReport();
				insAndOutsReport.configureLogging("batch.name", "batch-logger", "InsAndOutsReport");
				insAndOutsReport.execute(round, reportType, email);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
