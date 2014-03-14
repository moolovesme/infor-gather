package com.dabeshackers.infor.gather.helpers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;

public class ContactsHelper {
	public static String getContactNameFromNumber(Context context, String number) {
        // If name is not found, number will be returned
        String contactDisplayName = number;

        Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        Cursor cursor = context.getContentResolver().query(uri,
                new String[] { PhoneLookup.DISPLAY_NAME }, null, null, null);
        if (cursor.moveToFirst()) {
            contactDisplayName = cursor.getString(cursor
                    .getColumnIndex(PhoneLookup.DISPLAY_NAME));
        }
        cursor.close();
//        Log.d("GetNumber", "Retrived DisplayName for contact number:" + number + " DisplayName:" + contactDisplayName);

        return contactDisplayName;

    }



}
