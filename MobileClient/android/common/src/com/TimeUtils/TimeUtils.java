//package com.TimeUtils;
//
//import java.util.Calendar;
//import java.util.Date;
//
//import org.joda.time.DateTime;
//import org.joda.time.format.DateTimeFormat;
//import org.joda.time.format.DateTimeFormatter;
//import org.joda.time.format.ISODateTimeFormat;
//
// 
//
//public class TimeUtils {
//
//	
//	/**
//	 * Get a diff image date and current date
//	 * 
//	 * @param date1
//	 *            the oldest date
//	 * @param date2s
//	 *            the newest date
//	 * @return the diff value, fuzzy timestamps (e.g. "4 minutes ago" or "about 1 day ago").
//	 */
//	public static String getDateDiff(String date) {
//		TimeAgo ago = new TimeAgo();
//
//		String fixDateString = date.trim().replace("Z", ".207Z");
//		DateTimeFormatter parser = ISODateTimeFormat.dateTime();
//		DateTime dt = parser.parseDateTime(fixDateString);
//
//		DateTimeFormatter formatter = DateTimeFormat.mediumDateTime();
//
//		Date imageDate = new Date(formatter.print(dt));
//
//		return ago.timeAgo(imageDate);
//
//	}
//	
//	public static long getCurrentTimeStamp() {
//		Calendar cal = Calendar.getInstance();
//		// to store the number of seconds
//		//int epoch_time = (int) cal.getTimeInMillis()/1000; 
//		// for the milliseconds
//		long epoch_time = cal.getTimeInMillis();
//		return epoch_time;
//	}
//}
