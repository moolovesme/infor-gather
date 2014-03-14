package com.dabeshackers.infor.gather.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class SimpleDialogFragment {
	private static String dialogTitle;
	private static String dialogMessage;
	
	public static AlertDialog newMessageDialogInstance(Context cxt, String title, String message, DialogInterface.OnClickListener listener) {
		dialogTitle = title;
		dialogMessage = message;
		
		final AlertDialog.Builder builder = new AlertDialog.Builder(cxt);
		builder.setTitle(dialogTitle);
	    builder.setMessage(dialogMessage);
	    builder.setCancelable(false);
	    builder.setPositiveButton("OK", listener);
	    return builder.create();
		
	}
	

}
