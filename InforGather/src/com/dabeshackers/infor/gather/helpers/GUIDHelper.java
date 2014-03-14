package com.dabeshackers.infor.gather.helpers;

import java.util.Locale;
import java.util.UUID;

public class GUIDHelper {
	public static String createGUID() {
		UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString().toLowerCase(Locale.getDefault());
 
        return "{" + randomUUIDString + "}";
        
    }
}
