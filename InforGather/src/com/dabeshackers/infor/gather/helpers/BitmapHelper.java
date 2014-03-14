package com.dabeshackers.infor.gather.helpers;

import com.dabeshackers.infor.gather.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapHelper {
	public static final int SIZE_500 = 500;
	public static final int SIZE_400 = 400;
	public static final int SIZE_300 = 300;
	public static final int SIZE_200 = 200;
	public static final int SIZE_100 = 100;

	public static boolean createDefaultImage(Resources resources, String fromResourceName, String path) {
		Bitmap b = decodeSampledBitmapFromResource(fromResourceName, SIZE_200, SIZE_200, resources);
		try {
			saveBitmapToSD(b, path);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static boolean resize(String path, int size) {
		Bitmap b = decodeSampledBitmapFromFile(path, size, size);
		try {
			saveBitmapToSD(b, path);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}

	public static void saveBitmapToSD(Bitmap b, String fileName) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		b.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

		//you can create a new file name "test.jpg" in sdcard folder.
		File f = new File(fileName);
		if (!f.exists()) {
			f.createNewFile();
		}

		//write the bytes in file
		FileOutputStream fo = new FileOutputStream(f);
		fo.write(bytes.toByteArray());

		// remember close de FileOutput
		fo.close();
	}

	@SuppressWarnings("rawtypes")
	public static Bitmap decodeSampledBitmapFromResource(String path, int reqWidth, int reqHeight, Resources resources) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		Class cls = R.drawable.class;
		int drawableId = 0;
		Field fieldlist[] = cls.getDeclaredFields();
		for (Field fld : fieldlist) {
			String fieldName = fld.getName();
			if (fieldName.equals(path)) {
				try {
					drawableId = fld.getInt(null);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				// do whatever with the drawable
			}
		}

		BitmapFactory.decodeResource(resources, drawableId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(resources, drawableId, options);
	}

	public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(path, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(path, options);
	}

	public static Bitmap decodeSampledBitmapFromByteArray(byte[] bytes, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		//		BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
	}

	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and width
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}
}
