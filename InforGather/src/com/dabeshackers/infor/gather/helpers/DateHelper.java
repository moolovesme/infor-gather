package com.dabeshackers.infor.gather.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {
	public static String toMMDDYYY(long date, String dateSeparator){
		dateSeparator = dateSeparator==null?"/":dateSeparator;
		SimpleDateFormat df = new SimpleDateFormat(String.format("MM" + dateSeparator + "dd" + dateSeparator + "yyyy", dateSeparator));

		return df.format(new Date(date));
	}
	
	public static String toMMDDYYYHHMMS(long date, String dateSeparator, String timeSeparator){
		dateSeparator = dateSeparator==null?"/":dateSeparator;
		timeSeparator = timeSeparator==null?":":timeSeparator;
		SimpleDateFormat df = new SimpleDateFormat("MM" + dateSeparator + "dd" + dateSeparator + "yyyy HH" + timeSeparator + "mm" + timeSeparator);
		
		return df.format(new Date(date));
	}
	
	public static String toMMDDYYYHHMMSSS(long date, String dateSeparator, String timeSeparator){
		dateSeparator = dateSeparator==null?"/":dateSeparator;
		timeSeparator = timeSeparator==null?":":timeSeparator;
		SimpleDateFormat df = new SimpleDateFormat("MM" + dateSeparator + "dd" + dateSeparator + "yyyy HH" + timeSeparator + "mm" + timeSeparator + "ss");
		
		return df.format(new Date(date));
	}
}
