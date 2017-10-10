package com.common.uitl;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class ScreenUtils {

	/**
	 * 获取第三方截屏
	 */
	public static Bitmap getScreenShotBitmap(Context context) {
		FileInputStream buf = null;
		try {
			String fdir = "/dev/graphics/fb0";
			Runtime runtime = Runtime.getRuntime();
			Process suPro = runtime.exec("su");
			OutputStream psOut = suPro.getOutputStream();
			String chagePwd = "chmod 777 " + fdir;
			psOut.write(chagePwd.getBytes());

			buf = new FileInputStream(new File(fdir));
			DisplayMetrics dm = new DisplayMetrics();
			WindowManager WM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			Display display = WM.getDefaultDisplay();

			display.getMetrics(dm);
			int screenWidth = dm.widthPixels; // 屏幕宽（像素，如：480px）
			int screenHeight = dm.heightPixels; // 屏幕高（像素，如：800p）

			int pixelformat = display.getPixelFormat();
			PixelFormat localPixelFormat1 = new PixelFormat();
			PixelFormat.getPixelFormatInfo(pixelformat, localPixelFormat1);
			int deepth = localPixelFormat1.bytesPerPixel;// 位深
			byte[] piex = new byte[screenHeight * screenWidth * deepth];
			DataInputStream dStream = new DataInputStream(buf);
			dStream.readFully(piex);

			int[] colors = new int[screenHeight * screenWidth];

			for (int m = 0; m < colors.length; m++) {
				int b = (piex[m * 4] & 0xFF);
				int g = (piex[m * 4 + 1] & 0xFF);
				int r = (piex[m * 4 + 2] & 0xFF);
				int a = (piex[m * 4 + 3] & 0xFF);
				colors[m] = (a << 24) + (r << 16) + (g << 8) + b;
			}
			return Bitmap.createBitmap(colors, screenWidth, screenHeight, Bitmap.Config.ARGB_4444);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
