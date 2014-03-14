package com.dabeshackers.infor.gather.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimestampHelper {
	public static String getTimestampString(Date date){
		if(date == null){
			date = new Date();
		}
		return new SimpleDateFormat("yyyyMMdd_HHmm").format(date);
	}

	public static Long getTimestampLong(Date date){
		if(date == null){
			date = new Date();
		}
		return date.getTime();
	}

	



}
