package com.dabeshackers.infor.gather.helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MediaHelper {
	public static void saveMediaToSD(byte[] bytes, String fileName) throws IOException{
//		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//		b.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

		//you can create a new file name "test.jpg" in sdcard folder.
		File f = new File(fileName);
		f.createNewFile();
		//write the bytes in file
		FileOutputStream fo = new FileOutputStream(f);
		fo.write(bytes);

		// remember close the FileOutput
		fo.flush();
		fo.close();
	}
	
}
