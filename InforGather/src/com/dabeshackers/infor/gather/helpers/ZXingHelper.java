package com.dabeshackers.infor.gather.helpers;

import java.util.EnumMap;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class ZXingHelper {


	public static Bitmap generateQRCode(String uniqueID, int width, int height) {

		QRCodeWriter writer = new QRCodeWriter();
		try
		{
			EnumMap<EncodeHintType, Object> hint = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
			hint.put(EncodeHintType.CHARACTER_SET, "UTF-8");
			BitMatrix bitMatrix = writer.encode(uniqueID, BarcodeFormat.QR_CODE, width, height, hint);
			int mxWidth = bitMatrix.getWidth();
			int mxHeight = bitMatrix.getHeight();
			int[] pixels = new int[mxWidth * mxHeight];
			for (int y = 0; y < mxHeight; y++)
			{
				int offset = y * mxWidth;
				for (int x = 0; x < mxWidth; x++)
				{

					pixels[offset + x] = bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
				}
			}

			Bitmap bitmap = Bitmap.createBitmap(mxWidth, mxHeight, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, mxWidth, 0, 0, mxWidth, mxHeight);
			
			return bitmap;
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static Bitmap generateEAN13BarCode(String uniqueID, int width, int height) {

		QRCodeWriter writer = new QRCodeWriter();
		try
		{
			EnumMap<EncodeHintType, Object> hint = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
			hint.put(EncodeHintType.CHARACTER_SET, "UTF-8");
			BitMatrix bitMatrix = writer.encode(uniqueID, BarcodeFormat.EAN_13, width, height, hint);
			int mxWidth = bitMatrix.getWidth();
			int mxHeight = bitMatrix.getHeight();
			int[] pixels = new int[mxWidth * mxHeight];
			for (int y = 0; y < mxHeight; y++)
			{
				int offset = y * mxWidth;
				for (int x = 0; x < mxWidth; x++)
				{

					pixels[offset + x] = bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
				}
			}

			Bitmap bitmap = Bitmap.createBitmap(mxWidth, mxHeight, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, mxWidth, 0, 0, mxWidth, mxHeight);
			
			return bitmap;
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}
