package com.common.uitl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

/**
 * 工具集
 * @author:wangzhengyun
 * 2012-9-11
 */
public class GlobalTool {

	/**
	 * 启动一个新的Activity
	 * 
	 * @param ctx
	 * @param clasz
	 */
	public static void startOtherActivity(Activity ctx, Class clasz) {
		Intent intent = new Intent();
		intent.setClass(ctx, clasz);
		ctx.startActivity(intent);
	}

	/**
	 * 启动一个新的Activity
	 * 
	 * @param ctx
	 * @param clasz
	 */
	public static void startOtherActivity(Activity ctx, Class clasz,
			Serializable data) {
		Intent intent = new Intent();
		intent.putExtra("data", data);
		intent.setClass(ctx, clasz);
		ctx.startActivity(intent);
	}

	/**
	 * 获取屏幕高度宽度
	 * 
	 * @param ctx
	 * @return
	 */
	public static Point getDisplayMetrics(Context ctx) {
		DisplayMetrics metrcis = ctx.getResources().getDisplayMetrics();
		Point metricsPoint = new Point();
		metricsPoint.x = metrcis.widthPixels;
		metricsPoint.y = metrcis.heightPixels;
		return metricsPoint;
	}

	/**
	 * 参数必须为：getDisplayMetrics返回值
	 * 
	 * @param point
	 * @return
	 */
	public static Point getRandomPoint(Point point) {
		Random rand = new Random();
		int rx = rand.nextInt(point.x);
		int ry = rand.nextInt(point.y);
		return new Point(rx, ry);
	}

	/**
	 * 根据设定后的宽度和高度设置按钮的出事随机值
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	public static Point getDimensionsByDimens(int width, int height) {
		Random rand = new Random();
		int rx = rand.nextInt(width);
		int ry = rand.nextInt(height);
		return new Point(rx, ry);
	}

	/**
	 * 创建一个dlgbuilder
	 * 
	 * @param context
	 * @param view
	 * @return
	 */
	public static Builder createADialig(Context context, View view) {
		Builder alertDialog = new AlertDialog.Builder(context);
		alertDialog.setView(view);
		return alertDialog;
	}

	/**
	 * 发送广播
	 * 
	 * @param context
	 * @param intent
	 */
	public static void sendBoardCast(Context context, Intent intent) {
		context.sendBroadcast(intent);
	}

	/**
	 * 请求url
	 * 
	 * @param context
	 * @param url
	 */
	public static void startUrl(Context context, String url) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		context.startActivity(i);
	}

	/**
	 * 获取状态栏和标题栏的高度和宽度
	 * 
	 * @param context
	 * @return
	 */
	public static int getAppRect(Activity context) {
		/**
		 * decorView是window中的最顶层view，可以从window中获取到decorView，
		 * 然后decorView有个getWindowVisibleDisplayFrame方法可以获取到程序显示的区域，包括标题栏，但不包括状态栏
		 */
		Rect frame = new Rect();
		context.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;// 状态栏

		/**
		 * getWindow().findViewById(Window.ID_ANDROID_CONTENT)
		 * 这个方法获取到的view就是程序不包括标题栏的部分，然后就可以知道标题栏的高度了。
		 */
		int contentTop = context.getWindow()
				.findViewById(Window.ID_ANDROID_CONTENT).getTop();
		// statusBarHeight是上面所求的状态栏的高度
		int titleBarHeight = contentTop - statusBarHeight;// 标题栏
		return titleBarHeight;
	}

	/**
	 * 获取状态栏高度
	 * 
	 * @param activity
	 * @return
	 */
	public static int geteAppUnVisibleHeight(Activity activity) {
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;// 状态栏

		int contentTop = activity.getWindow()
				.findViewById(Window.ID_ANDROID_CONTENT).getTop();
		// statusBarHeight是上面所求的状态栏的高度
		int titleBarHeight = contentTop - statusBarHeight;// 标题栏
		return statusBarHeight + titleBarHeight;
	}

	/**
	 * 创建目录
	 * 
	 * @param dir
	 */
	public static void mkdir(String dir) {
		File file = new File(dir);
		file.mkdir();
	}

	/**
	 * 把asset下面的资源拷贝到sdcard下面
	 * 
	 * @param context
	 * @param destFile
	 * @throws IOException
	 */
	public static void copyAssetFile2Sdcard(Context context, String fname,
			String destFile) throws IOException {
		AssetManager asm = context.getAssets();
		File df = new File(destFile);
		if (df.exists() == false) {
			df.mkdir();
		} else {
			InputStream os = asm.open(fname);
			byte buf[] = new byte[256];
			int b = -1;
			FileOutputStream fout = new FileOutputStream(destFile + fname);
			while ((b = os.read(buf)) != -1) {
				fout.write(buf, 0, buf.length);
			}
			fout.flush();
			os.close();
			fout.close();
		}

	}

	/**
	 * 线程休眠
	 * 
	 * @param times
	 */
	public static void delay(long times) {
		try {
			Thread.sleep(times);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static String getLogPrffix() {
		StackTraceElement[] stacks = (new Throwable()).getStackTrace();
		String logPreffix = null;
		if (stacks.length > 1) {
			String lsname = stacks[1].getClassName();
			String name = lsname.substring(lsname.lastIndexOf(".") + 1);
			String mehod = stacks[1].getMethodName();
			logPreffix = new String("[(" + name + ":" + mehod + ") line:"
					+ stacks[1].getLineNumber() + " ]");
		}
		return logPreffix;
	}
	
	
	public static LayoutInflater getInflater(Context context){
		LayoutInflater inf = LayoutInflater.from(context);
		return inf;
	}
}
