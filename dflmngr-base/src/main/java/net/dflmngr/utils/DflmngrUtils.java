package net.dflmngr.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DflmngrUtils {
	
	//public static String nowStr = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	public static String getNowStr() {
		
		return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		
	}
	
	public static final Map<String, Integer> weekDays;
	static
	{
	    weekDays = new HashMap<String, Integer>();
	    weekDays.put("Monday", Calendar.MONDAY);
	    weekDays.put("Tuesday", Calendar.TUESDAY);
	    weekDays.put("Wednesday", Calendar.WEDNESDAY);
	    weekDays.put("Thursday", Calendar.THURSDAY);
	    weekDays.put("Friday", Calendar.FRIDAY);
	    weekDays.put("Saturday", Calendar.SATURDAY);
	    weekDays.put("Sunday", Calendar.SUNDAY);
	};
	
	public static final Map<String, Integer> AMPM;
	static
	{
	    AMPM = new HashMap<String, Integer>();
	    AMPM.put("AM", Calendar.AM);
	    AMPM.put("PM", Calendar.PM);
	};

}
