package com.dabeshackers.infor.gather;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.dabeshackers.infor.gather.helpers.ToastHelper;

public class InventoryFragment extends SherlockFragment {
	private static final String TAG = InventoryFragment.class.getSimpleName();
	public static final String TITLE = "Inventory";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		return inflater.inflate(R.layout.fragment_dashboard, container, false);

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		Button button = (Button) view.findViewById(R.id.test);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ToastHelper.toast(getActivity(), "Hello from Inventory!", Toast.LENGTH_SHORT);
			}
		});
	}
}
