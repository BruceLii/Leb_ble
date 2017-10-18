package com.ledble.base;

import java.util.List;

import android.content.Intent;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import butterknife.ButterKnife;

public class LedBleActivity extends AppCompatActivity {
	
	private LedBleApplication baseApp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		baseApp = (LedBleApplication) getApplication();
	}
	
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		ButterKnife.bind(this);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		List<Fragment> fragments = getSupportFragmentManager().getFragments();
		if (fragments != null && fragments.size() > 0) {
			for (Fragment fragment : fragments) {
				fragment.onActivityResult(requestCode, resultCode, data);
			}
		}
	}

	public LedBleApplication getBaseApp() {
		return baseApp;
	}
	
	//instance 进入后台保持运行状态
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
			moveTaskToBack(true); //ture 在任何Activity中按下返回键都退出并进入后台运行， false 只有在根Activity中按下返回键才会退向后台运行。
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		moveTaskToBack(true);
	}
	
	protected void onDestroy() {
		super.onDestroy();
		ButterKnife.unbind(this);
	}

	public void onSensorChanged(SensorEvent sensorEvent) {
		// TODO Auto-generated method stub
		
	}
}
