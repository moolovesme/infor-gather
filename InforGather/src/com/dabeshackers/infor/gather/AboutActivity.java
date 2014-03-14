package com.dabeshackers.infor.gather;


import java.io.File;
import java.io.FileInputStream;
import java.nio.channels.FileChannel;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.dabeshackers.infor.gather.R;
import com.dabeshackers.infor.gather.application.ApplicationUtils;

public class AboutActivity extends Activity {

	private static final String TAG = AboutActivity.class.getSimpleName();
	TextView version;
	ImageView imgLogo;

	//	EditText version;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.about);

		final PackageManager pm = getApplicationContext().getPackageManager();
		PackageInfo pInfo;
		try {
			pInfo = pm.getPackageInfo(getPackageName(), 0);
			ApplicationInfo aInfo = pm.getApplicationInfo(this.getPackageName(), 0);

			String appVersion = pInfo.versionName;
			String appName = (String) (aInfo != null ? getPackageManager().getApplicationLabel(aInfo) : "(unknown)");

			version = (TextView) findViewById(R.id.version);
			version.setText(appName + " ver. " + appVersion);

			imgLogo = (ImageView) findViewById(R.id.imgLogo);
			imgLogo.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					backupDatabase();
				}
			});
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

	}

	public void backupDatabase() {
		try {
			File sd = new File(ApplicationUtils.Paths.getLocalAppFilesFolder(AboutActivity.this));

			if (sd.canWrite()) {
				String backupDBPath = "app_db.sqlite";
				File currentDB = getDatabasePath("app_db.sqlite");
				File backupDB = new File(sd, backupDBPath);

				if (currentDB.exists()) {
					FileInputStream srcIS = new FileInputStream(currentDB);
					FileInputStream dstIS = new FileInputStream(backupDB);
					FileChannel src = srcIS.getChannel();
					FileChannel dst = dstIS.getChannel();
					dst.transferFrom(src, 0, src.size());
					srcIS.close();
					dstIS.close();
					src.close();
					dst.close();

					Log.d(TAG, "Database Backup Complete.");
				}

			}
		} catch (Exception e) {
			Log.w("Database Backup failed", e);
		}
	}

}
