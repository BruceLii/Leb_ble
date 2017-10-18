package com.ledble.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 * 
 * 需要在Application中注册，为了要在程序启动器就监控整个程序。
 */
public class CrashHandler implements UncaughtExceptionHandler {

	public static final String TAG = "CrashHandler";
	// 系统默认的UncaughtException处理类
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	// CrashHandler实例
	private static CrashHandler instance;
	// 程序的Context对象
	private Context mContext;
	// 用来存储设备信息和异常信息
	private Map<String, String> infos = new HashMap<String, String>();

	// 用于格式化日期,作为日志文件名的一部分
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	private static final String CRASH_REPORTER_EXTENSION = ".log";
	private String CRASH_DIR_PATH;
	File crashDirFile;

	/** 保证只有一个CrashHandler实例 */
	private CrashHandler() {
	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static CrashHandler getInstance() {
		if (instance == null)
			synchronized (CrashHandler.class) {
				if (instance == null) {
					instance = new CrashHandler();
				}
			}
		return instance;
	}

	/**
	 * 判断sd卡是否存在
	 * 
	 * @return
	 */
	private boolean isSdCardExist() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

	/**
	 * 初始化
	 */
	public void init(Context context) {
		mContext = context;
		// 获取系统默认的UncaughtException处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		// 设置该CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
		if (isSdCardExist()) {
			CRASH_DIR_PATH = FileUtils.getStorageDerectory("/LedBle")
					.getAbsolutePath();
		} else {
			CRASH_DIR_PATH = mContext.getFilesDir().getAbsolutePath();
		}
		crashDirFile = new File(CRASH_DIR_PATH);
		if (crashDirFile.exists() && crashDirFile.isDirectory()) {
			return;
		} else {
			crashDirFile.mkdirs();
		}
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Log.e(TAG, "error : ", e);
			}
			// 退出程序
			exit();
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false.
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		// 收集设备参数信息
		collectDeviceInfo(mContext);

		// 使用Toast来显示异常信息
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(mContext, "sorry instance is crash!", Toast.LENGTH_SHORT).show();
				Looper.loop();
			}
		}.start();

		// 保存日志文件
		saveCatchInfo2File(ex);
		return true;
	}

	/**
	 * 收集设备参数信息
	 * 
	 * @param ctx
	 */
	private void collectDeviceInfo(Context ctx) {
		try {
			Log.d(TAG, "-----------------------collectDeviceInfo-----------------------");
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null" : pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "collectDeviceInfo Exception:"
					+ "an error occured when collect package info", e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
				Log.d(TAG, field.getName() + " : " + field.get(null));
			} catch (Exception e) {
				Log.e(TAG, "collectDeviceInfo Exception:"
						+ "an error occured when collect crash info", e);
			}
		}
	}

	/**
	 * 保存错误信息到文件中
	 * 
	 * @param ex
	 * @return 返回文件名称,便于将文件传送到服务器
	 */
	private String saveCatchInfo2File(Throwable ex) {

		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		try {
			long timestamp = System.currentTimeMillis();
			String time = formatter.format(new Date());
			String fileName = "crash-" + time + "-" + timestamp
					+ CRASH_REPORTER_EXTENSION;
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				// String path = mContext.getCacheDir() + "/crash/" ;
				File dir = new File(CRASH_DIR_PATH);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(CRASH_DIR_PATH + "/"
						+ fileName);
				fos.write(sb.toString().getBytes());
				fos.close();

				Log.d(TAG,
						"-----------------------saveCatchInfo2File ok-----------------------");
				Log.d(TAG, "fileName:" + CRASH_DIR_PATH + fileName);

				// 发送给开发人员
				// sendCrashLog2PM(CRASH_DIR_PATH + fileName);
			}
			return fileName;
		} catch (Exception e) {
			Log.e(TAG, "saveCatchInfo2File Exception:"
					+ "an error occured while writing file...", e);
		}
		return null;
	}

	// /**
	// * 将捕获的导致崩溃的错误信息发送给开发人员
	// *
	// * 目前只将log日志保存在sdcard 和输出到LogCat中，并未发送给后台。
	// */
	// private void sendCrashLog2PM(String fileName) {
	// if (!new File(fileName).exists()) {
	// // Toast.makeText(mContext, "日志文件不存在！", Toast.LENGTH_SHORT).show();
	// return;
	// }
	//
	// // 邮件发送
	// Log.d(TAG, "-----------------------send email-----------------------");
	// logPathString = fileName;
	// //
	// // FileInputStream fis = null;
	// // BufferedReader reader = null;
	// // String s = null;
	// // Log.d(TAG,
	// // "-----------------------show CrashLog -----------------------");
	//
	// // try {
	// // fis = new FileInputStream(fileName);
	// // reader = new BufferedReader(new InputStreamReader(fis, "GBK"));
	// // while(true){
	// // s = reader.readLine();
	// // if(s == null) break;
	// // //由于目前尚未确定以何种方式发送，所以先打出log日志。
	// // Log.d(TAG, s.toString());
	// // }
	// // } catch (FileNotFoundException e) {
	// // e.printStackTrace();
	// // } catch (IOException e) {
	// // e.printStackTrace();
	// // }finally{ // 关闭流
	// // try {
	// // reader.close();
	// // fis.close();
	// // } catch (IOException e) {
	// // e.printStackTrace();
	// // }
	// // }
	// }
	//
	private void exit() {
		// 杀进程，关闭应用
		android.os.Process.killProcess(android.os.Process.myPid());
		// android.instance.ActivityManager activityMgr =
		// (android.instance.ActivityManager) mContext
		// .getSystemService(Context.ACTIVITY_SERVICE);
		// activityMgr.restartPackage(mContext.getPackageName());
		// System.exit(0);
		// System.gc();
		// Intent intent = new Intent(mContext,MainActivity.class);
		// intent.setAction(Intent.ACTION_MAIN);
		// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
		// intent.addCategory(Intent.CATEGORY_LAUNCHER);
		// mContext.startActivity(intent);
	}
}