package net.dflmngr.clients.testing;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.dflmngr.utils.CronExpressionCreator;
import net.dflmngr.webservice.CallDflmngrWebservices;

public class TestingClient {

	public static void main(String[] args) {
		
		Map<String, Object> jobParams = new HashMap<>();
		jobParams.put("ROUND", 1);
		jobParams.put("REPORT_TYPE","Full");
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY");
		SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
		
		Date now = new Date();
		Calendar nowCal = Calendar.getInstance();
		nowCal.setTime(now);
		nowCal.add(Calendar.MINUTE, 5);

		CronExpressionCreator cronExpression = new CronExpressionCreator();
		cronExpression.setTime(timeFormat.format(nowCal.getTime()));
		cronExpression.setStartDate(dateFormat.format(nowCal.getTime()));
		
		CallDflmngrWebservices.schedualJob("InsOutsReport", "Reports", "net.dflmngr.scheduler.jobs.InsAndOutsReportJob", jobParams, cronExpression.getCronExpression(), false);
	}
}
