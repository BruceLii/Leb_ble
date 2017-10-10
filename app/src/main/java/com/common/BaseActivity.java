package com.common;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ledble.R;

/**
 * 基础activity
 * 
 * @author:wangzhengyun 2012-8-31
 */
public class BaseActivity extends FragmentActivity implements OnProgressDialogCallback {

	public Dialog mProgressDialog = null;
	private OnProgressDialogCallback mProgressDialogCallback;
	private ProgressBar mProgressBar = null;
	private TextView tvMessage = null;

	// private ImageView imageView;
	// private Animation rotateAnimation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		crateDialog();
	}

	public void crateDialog() {
		mProgressDialog = new Dialog(this, R.style.dialog);
		View dialogView = View.inflate(this, R.layout.dialog_progress, null);
		mProgressBar = (ProgressBar) dialogView.findViewById(R.id.progressBarMore);
		tvMessage = (TextView)dialogView.findViewById(R.id.tvMessage);
		mProgressDialog.setContentView(dialogView);
		mProgressDialog.setCancelable(true);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				if (null != mProgressDialogCallback) {
					mProgressDialogCallback.onProgressDialogcancel();
				}
			}
		});
	}
	
	public Dialog getmProgressDialog() {
		return mProgressDialog;
	}
	public Dialog getmProgressDialog(String msg) {
		tvMessage.setText(msg+"...");
		return mProgressDialog;
	}
	
	public void setMessage(String msg){
		tvMessage.setText(msg+"...");
	}

	public void setmProgressDialog(Dialog mProgressDialog) {
		this.mProgressDialog = mProgressDialog;
	}


	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// hideProgressDialog();
		super.onNewIntent(intent);
	}

	public void setDialogCancellAable(boolean isCancellAable) {
		mProgressDialog.setCancelable(isCancellAable);
	}

	/**
	 * 初始化视图
	 */
	public void initView() {

	}

	/**
	 * 丛可输入的组件中获取输入
	 * 
	 * @param id
	 * @return
	 */
	public String getInputFromId(int id) {
		EditText edit = findEditTextById(id);
		String input = edit.getText().toString();
		return input;
	}

	public String getInput(EditText input) {
		return input.getText().toString();
	}

	public TextView findTextViewById(int id) {
		return (TextView) findViewById(id);
	}

	public EditText findEditTextById(int id) {
		return (EditText) findViewById(id);
	}

	public Button findButtonById(int id) {
		return (Button) findViewById(id);
	}

	public ImageView findImageViewById(int id) {
		return (ImageView) findViewById(id);
	}

	public ImageButton findImageButtonById(int id) {
		return (ImageButton) findViewById(id);
	}

	public ListView findListViewById(int id) {
		return (ListView) findViewById(id);
	}

	public RelativeLayout findRelativeLayout(int id) {
		return (RelativeLayout) findViewById(id);
	}

	public LinearLayout findLinearLayout(int id) {
		return (LinearLayout) findViewById(id);
	}

	public AutoCompleteTextView findAutoCompleteTextById(int id) {
		return (AutoCompleteTextView) findViewById(id);
	}

	@Override
	public void onBackPressed() {
		backFinish();
		return;
	}

	// public void showProgressDialog(String title, String message,
	// boolean cancelable) {
	//
	// }

	public void showProgressDialog(boolean isCancellAble) {
		if (!isFinishing()) {
			if (isCancellAble) {
				mProgressDialog.setCancelable(true);
			} else {
				mProgressDialog.setCancelable(false);
			}
			mProgressDialog.show();
		}
	}

	public void showProgressDialog() {
		if (!isFinishing()) {
			showProgressDialog(true);
		}
	}

	public void hideProgressDialog() {
		mProgressDialog.hide();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mProgressDialog.dismiss();
	}

	public void backFinish() {
		finish();
	}

	@Override
	public void onProgressDialogcancel() {

	}

	public void setCancelAble(boolean isAble) {
		mProgressDialog.setCancelable(isAble);
	}

}

/**
 * 进度条回调
 * 
 * @author:wangzhengyun 2012-8-13
 */
interface OnProgressDialogCallback {
	public void onProgressDialogcancel();

}
