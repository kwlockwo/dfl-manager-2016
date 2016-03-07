package net.dflmngr.logging;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class JDBCLogHandler extends Handler {

	String driverString;
	String connectionString;
	Connection connection;

	protected final static String insertSQL = "insert into log (level,logger,message,sequence,sourceClass,sourceMethod,threadID,timeEntered)" + "values(?,?,?,?,?,?,?,?)";
	protected final static String clearSQL = "delete from log;";

	protected PreparedStatement prepInsert;
	protected PreparedStatement prepClear;

	public JDBCLogHandler(String driverString, String connectionString) {
		try {
			this.driverString = driverString;
			this.connectionString = connectionString;

			Class.forName(driverString);
			connection = DriverManager.getConnection(connectionString);
			prepInsert = connection.prepareStatement(insertSQL);
			prepClear = connection.prepareStatement(clearSQL);
		} catch (ClassNotFoundException e) {
			System.err.println("Error on open: " + e);
		} catch (SQLException e) {
			System.err.println("Error on open: " + e);
		}
	}

	static public String truncate(String str, int length) {
		if (str.length() < length)
			return str;
		return (str.substring(0, length));
	}

	public void publish(LogRecord record) {
		
		if (getFilter() != null) {
			if (!getFilter().isLoggable(record))
				return;
		}

		try {
			prepInsert.setInt(1, record.getLevel().intValue());
			prepInsert.setString(2, truncate(record.getLoggerName(), 63));
			prepInsert.setString(3, truncate(record.getMessage(), 255));
			prepInsert.setLong(4, record.getSequenceNumber());
			prepInsert.setString(5, truncate(record.getSourceClassName(), 63));
			prepInsert.setString(6, truncate(record.getSourceMethodName(), 31));
			prepInsert.setInt(7, record.getThreadID());
			prepInsert.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
			prepInsert.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Error on open: " + e);
		}

	}

	public void close() {
		try {
			if (connection != null)
				connection.close();
		} catch (SQLException e) {
			System.err.println("Error on close: " + e);
		}
	}

	public void clear() {
		try {
			prepClear.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Error on clear: " + e);
		}
	}

	public void flush() {}
}
