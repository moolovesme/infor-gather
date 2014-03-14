package com.dabeshackers.infor.gather.helpers;

import com.dabeshackers.infor.gather.R;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ToastHelper {
	public static void toast(Context context, String msg, int duration) {
		Toast.makeText(context, msg, duration).show();
	}

	@SuppressWarnings("unused")
	public static void customToast(LayoutInflater inflater, Context context, String msg, int duration) {
		View toastView = inflater.inflate(R.layout.custom_toast, null);

		TextView message = (TextView) toastView.findViewById(R.id.message);
		message.setText(msg);

		ImageButton img = (ImageButton) toastView.findViewById(R.id.img);

		Toast toast = new Toast(context);
		toast.setView(toastView);
		toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 40);
		toast.setDuration(duration);
		toast.show();
	}

}
