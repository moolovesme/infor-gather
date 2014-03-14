package com.dabeshackers.infor.gather.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ProgressDialogFragment extends DialogFragment {
	private static String dialogTitle;
	private static String dialogMessage;
	
	public static ProgressDialogFragment newInstance(String title, String message ) {
		dialogTitle = title;
		dialogMessage = message;
		
		ProgressDialogFragment dialog = new ProgressDialogFragment();
		return dialog;
	}
	
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final ProgressDialog dialog = new ProgressDialog(getActivity());
	    dialog.setTitle(dialogTitle);
	    dialog.setMessage(dialogMessage);
	    dialog.setIndeterminate(true);
	    dialog.setCancelable(false);
	    // etc...
	    return dialog;
	}
	
	

}
