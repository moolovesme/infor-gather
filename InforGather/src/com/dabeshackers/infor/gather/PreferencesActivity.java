package com.dabeshackers.infor.gather;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.dabeshackers.infor.gather.R;

public class PreferencesActivity extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);

	}

}
