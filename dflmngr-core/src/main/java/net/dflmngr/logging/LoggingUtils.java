package net.dflmngr.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class LoggingUtils {
	
	private Logger logger;
	
	private String loggerName;
	private String loggerKey;
	private String logFileBase;

	public LoggingUtils(String loggerName, String loggerKey, String logFileBase) {
		this.loggerName = loggerName;
		this.loggerKey = loggerKey;
		this.logFileBase = logFileBase;
		
		logger = LoggerFactory.getLogger(this.loggerName);
	}
	
	public void log(String level, String msg, Object...arguments) {
		
		MDC.put(loggerKey, logFileBase);
		
		try {
			switch (level) {
				case "info" : logger.info(msg, arguments); break;
				case "error" : logger.error(msg, arguments);
			}
		} catch (Exception ex) {
			logger.error("Error in ... ", ex);
		} finally {
			MDC.remove(loggerKey);
		}
	}
}
