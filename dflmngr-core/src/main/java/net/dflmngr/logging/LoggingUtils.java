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
		
		String callingClass = Thread.currentThread().getStackTrace()[2].getClassName();
		String callingMethod = Thread.currentThread().getStackTrace()[2].getMethodName();
		int lineNo = Thread.currentThread().getStackTrace()[2].getLineNumber();
		
		String loggerMsg = "[" + callingClass + "." +  callingMethod + "(Line:" + lineNo +")] - " + msg;
		
		try {
			switch (level) {
				case "info" : logger.info(loggerMsg, arguments); break;
				case "error" : logger.error(loggerMsg, arguments);
			}
		} catch (Exception ex) {
			logger.error("Error in ... ", ex);
		} finally {
			MDC.remove(loggerKey);
		}
	}
}
