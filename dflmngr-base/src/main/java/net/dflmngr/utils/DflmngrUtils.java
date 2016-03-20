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
	
	public static final Map<String, Integer> weekDaysInt;
	static
	{
	    weekDaysInt = new HashMap<String, Integer>();
	    weekDaysInt.put("Monday", Calendar.MONDAY);
	    weekDaysInt.put("Tuesday", Calendar.TUESDAY);
	    weekDaysInt.put("Wednesday", Calendar.WEDNESDAY);
	    weekDaysInt.put("Thursday", Calendar.THURSDAY);
	    weekDaysInt.put("Friday", Calendar.FRIDAY);
	    weekDaysInt.put("Saturday", Calendar.SATURDAY);
	    weekDaysInt.put("Sunday", Calendar.SUNDAY);
	};
	
	public static final Map<Integer, String> weekDaysString;
	static
	{
	    weekDaysString = new HashMap<Integer, String>();
	    weekDaysString.put(Calendar.MONDAY, "Monday");
	    weekDaysString.put(Calendar.TUESDAY, "Tuesday");
	    weekDaysString.put(Calendar.WEDNESDAY, "Wednesday");
	    weekDaysString.put(Calendar.THURSDAY, "Thursday");
	    weekDaysString.put(Calendar.FRIDAY, "Friday");
	    weekDaysString.put(Calendar.SATURDAY, "Saturday");
	    weekDaysString.put(Calendar.SUNDAY, "Sunday");
	};
	
	public static final Map<String, Integer> AMPM;
	static
	{
	    AMPM = new HashMap<String, Integer>();
	    AMPM.put("AM", Calendar.AM);
	    AMPM.put("PM", Calendar.PM);
	};

}
