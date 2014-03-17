package com.dabeshackers.infor.gather;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.dabeshackers.infor.gather.helpers.DeviceAudioHelper;
import com.dabeshackers.infor.gather.helpers.ToastHelper;

public class MainActivity extends SherlockActivity {

	Button merchant, customer;
	ToggleButton toggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		toggle = (ToggleButton) findViewById(R.id.toggle);
		toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					// The toggle is enabled
					DeviceAudioHelper.setDeviceAudioToSilent(MainActivity.this, DeviceAudioHelper.SILENT);
					ToastHelper.customToast(getLayoutInflater(), MainActivity.this, "Quiet Time is activated for X hours. Click on settings to change this.", Toast.LENGTH_LONG);

				} else {
					// The toggle is disabled
					DeviceAudioHelper.setDeviceAudioToSilent(MainActivity.this, DeviceAudioHelper.NORMAL);

				}
			}
		});

		if (DeviceAudioHelper.getRingerMode(this) != AudioManager.RINGER_MODE_SILENT) {
			toggle.setChecked(false);
		} else {
			toggle.setChecked(true);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();

			return true;
		case R.id.action_settings:
			Intent intent = new Intent(MainActivity.this, MainContainerActivity.class);
			startActivity(intent);
			return true;
		}

		return true;
	}
	//
	//	@SuppressWarnings({ "unchecked", "rawtypes" })
	//	private void showConfirmationDialog(String message){
	//		final Dialog alert = new Dialog(this);
	//		alert.setContentView(R.layout.dialog_confirm);
	//		alert.setTitle("Activating Quiet Mode");
	//
	//		final TextView messageView = (TextView) alert.findViewById(R.id.message);
	//		messageView.setText(message);
	//
	//		/**
	//		 * *************************************
	//		 * VIEW COMPONENTS EVENT DEFINITIONS
	//		 * *************************************
	//		 */
	//		//Define capture button events
	//		Button btnOK = (Button) alert.findViewById(R.id.btn0);
	//		btnOK.setOnClickListener(new OnClickListener() {
	//			public void onClick(View v) {
	//				DeviceAudioHelper.setDeviceAudioToSilent(MainActivity.this, DeviceAudioHelper.SILENT);
	//			}
	//		});
	//
	//		Button btnSettings = (Button) alert.findViewById(R.id.btn1);
	//		btnSettings.setOnClickListener(new OnClickListener() {
	//			public void onClick(View v) {
	//
	//			}
	//		});
	//
	//
	//
	//
	//		/**
	//		 * *************************************
	//		 * END VIEW COMPONENTS EVENT DEFINITIONS
	//		 * *************************************
	//		 */
	//
	//		alert.show();
	//		Handler handler = null;
	//		handler = new Handler(); 
	//		handler.postDelayed(new Runnable(){ 
	//			public void run(){
	//				DeviceAudioHelper.setDeviceAudioToSilent(MainActivity.this, DeviceAudioHelper.SILENT);
	//				alert.cancel();
	//				alert.dismiss();
	//			}
	//		}, 2000);
	//	}
	//	
	//	

}
