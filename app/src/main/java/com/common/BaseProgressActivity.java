package com.common;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;

import com.common.BaseActivity;

public class BaseProgressActivity extends BaseActivity {

	private AsyncTask mTaskRunning;
	private boolean needReCreate = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Crashandler.getInstance().init(this, new CrashCallback() {
		// @Override
		// public void OnCrash(Throwable e) {
		// Intent i = new Intent();
		// i.setClass(App.getInstance(), LoginActivity.class);
		// i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// i.putExtra("flag", -1);
		// startActivity(i);
		// }
		// });

		getmProgressDialog().setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				if (null != mTaskRunning && !mTaskRunning.isCancelled()) {
					mTaskRunning.cancel(true);
					// needReCreate=true;
					// LogUtil.e(Constant.TAG_SKYWORTH_AIRPORT, "关闭后台task");
				}
			}
		});
	}

	public void showProgressDialogWithTask(AsyncTask runingTask) {
		mTaskRunning = runingTask;
		showProgressDialog();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public void showProgressDialog() {
		Dialog dlg = getmProgressDialog();
		if (null != dlg) {
			dlg.show();
		} else {
			crateDialog();
			getmProgressDialog().show();
		}
	}

	public void hideProgressDialogWithTask() {
		if (null != mTaskRunning && !mTaskRunning.isCancelled()) {
			mTaskRunning.cancel(true);
			// LogUtil.e(Constant.TAG_SKYWORTH_AIRPORT, "关闭后台task");
		}
		hideProgressDialog();
	}
}
