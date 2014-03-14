package com.dabeshackers.infor.gather.helpers;

import com.dabeshackers.infor.gather.R;
import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.util.Log;

public class FtpHelper {

	public static void uploadFile(Context context, File file, String FTPDirectory, FTPDataTransferListener ftpTransferListener) throws Exception {
		FTPClient f = new FTPClient();
		try {
			String host = context.getResources().getString(R.string.ftp_host);
			String user = context.getResources().getString(R.string.ftp_user);
			String password = context.getResources().getString(R.string.ftp_pass);
			int port = Integer.parseInt(context.getResources().getString(R.string.ftp_port));

			//File edited uploading to FTP
			f.setPassive(true);

			f.connect(host, port);

			f.login(user, password);

			try {
				f.createDirectory(FTPDirectory);
			} catch (FTPException e) {
				//Print exception except 550 -> Directory exists
				if (e.getCode() != 550) {
					e.printStackTrace();
				}
			}

			f.changeDirectory(FTPDirectory);

			Log.v("FTP Upload", FTPDirectory + file.getName());
			f.setType(FTPClient.TYPE_BINARY);

			try {
				f.deleteFile(file.getName());
			} catch (Exception e) {
			}

			f.upload(file, ftpTransferListener);

		} catch (IllegalStateException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} catch (FTPIllegalReplyException e) {
			e.printStackTrace();
			throw e;
		} catch (FTPException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (f.isConnected()) {
				f.disconnect(true);
			}
		}

	}

	public static void uploadFiles(Context context, List<File> files, String FTPDirectory) throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException {
		FTPClient f = new FTPClient();
		String host = context.getResources().getString(R.string.ftp_host);
		String user = context.getResources().getString(R.string.ftp_user);
		String password = context.getResources().getString(R.string.ftp_pass);
		int port = Integer.parseInt(context.getResources().getString(R.string.ftp_port));

		//File edited uploading to FTP
		f.setPassive(true);

		f.connect(host, port);

		f.login(user, password);

		try {
			f.createDirectory(FTPDirectory);
		} catch (FTPException e) {
			//Print exception except 550 -> Directory exists
			if (e.getCode() != 550) {
				e.printStackTrace();
			}
		}

		f.changeDirectory(FTPDirectory);

		for (File file : files) {
			Log.v("FTP Upload", FTPDirectory + file.getName());
			f.setType(FTPClient.TYPE_BINARY);

			try {
				f.deleteFile(file.getName());
			} catch (Exception e) {
			}

			f.upload(file);

		}

		if (f.isConnected()) {
			f.disconnect(true);
		}

	}

	public static void deleteDirectory(Context context, String FTPDirectory) throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException {
		FTPClient f = new FTPClient();
		String host = context.getResources().getString(R.string.ftp_host);
		String user = context.getResources().getString(R.string.ftp_user);
		String password = context.getResources().getString(R.string.ftp_pass);
		int port = Integer.parseInt(context.getResources().getString(R.string.ftp_port));

		//File edited uploading to FTP
		f.setPassive(true);

		f.connect(host, port);

		f.login(user, password);

		try {
			f.createDirectory(FTPDirectory);
		} catch (FTPException e) {
			//Print exception except 550 -> Directory exists
			if (e.getCode() != 550) {
				e.printStackTrace();
			}
		}

		f.changeDirectory(FTPDirectory);

		try {
			FTPFile[] files = f.list();
			for (int i = 0; i < files.length; i++) {
				f.deleteFile(files[i].getName());
			}
		} catch (FTPListParseException e) {
			e.printStackTrace();
		}
		f.deleteDirectory(FTPDirectory);

		if (f.isConnected()) {
			f.disconnect(true);
		}

	}
}
