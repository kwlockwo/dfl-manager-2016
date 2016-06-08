package net.dflmngr.handlers;

import java.util.List;
import java.util.Map;

import net.dflmngr.logging.LoggingUtils;
import net.dflmngr.model.entity.DflPlayerScores;
import net.dflmngr.model.service.DflPlayerScoresService;
import net.dflmngr.model.service.impl.DflPlayerScoresServiceImpl;

public class Best22Handler {
	private LoggingUtils loggerUtils;
	
	boolean isExecutable;
	
	String defaultMdcKey = "batch.name";
	String defaultLoggerName = "batch-logger";
	String defaultLogfile = "Best22Handler";
	
	String mdcKey;
	String loggerName;
	String logfile;
	
	DflPlayerScoresService dflPlayerScoresService;
	
	public Best22Handler() {
		dflPlayerScoresService = new DflPlayerScoresServiceImpl();
	}
	
	public void configureLogging(String mdcKey, String loggerName, String logfile) {
		loggerUtils = new LoggingUtils(loggerName, mdcKey, logfile);
		this.mdcKey = mdcKey;
		this.loggerName = loggerName;
		this.logfile = logfile;
		isExecutable = true;
	}
	
	public void execute(int round) {
		
		try{
			if(!isExecutable) {
				configureLogging(defaultMdcKey, defaultLoggerName, defaultLogfile);
				loggerUtils.log("info", "Default logging configured");
			}
			
			loggerUtils.log("info", "Best22Handler excuting, rount={} ....", round);
			
			calculateBest22(round);
			
			dflPlayerScoresService.close();
			
			loggerUtils.log("info", "Best22Handler complete");
			
		} catch (Exception ex) {
			loggerUtils.log("error", "Error in ... ", ex);
		}
	}
	
	private void calculateBest22(int round) {
		
		Map<Integer, List<DflPlayerScores>> allPlayerScores = dflPlayerScoresService.getUptoRoundWithKey(round+1);
		
		
		
	}
}
