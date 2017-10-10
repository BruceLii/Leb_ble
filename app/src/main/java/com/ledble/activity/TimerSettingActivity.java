package com.ledble.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;

import com.common.BaseActivity;
import com.common.uitl.NumberHelper;
import com.ledble.R;
import com.ledble.view.wheel.OnWheelChangedListener;
import com.ledble.view.wheel.WheelModelAdapter;
import com.ledble.view.wheel.WheelView;

public class TimerSettingActivity extends BaseActivity {

	private WheelView listViewH;
	private WheelView listViewM;
	private WheelView listViewModel;

	private WheelModelAdapter wheelAdapterH;
	private WheelModelAdapter wheelAdapterM;
	private WheelModelAdapter wheelAdapterModel;

	private String tag = "";
	private View linearLayoutContainer;

	private int hour /*= 12*/;
	private int minite /*= 30*/;
	private int model /*= 10*/;
	
	
	private String modelText = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		initView();
	}

	@Override
	public void initView() {
		setContentView(R.layout.activity_timer_setting);
		if (null != getIntent()) {
			tag = getIntent().getStringExtra("tag");
		} else {
			finish();
		}
		
		Time t=new Time(); // or Time t=new Time("GMT+8"); 鍔犱笂Time Zone璧勬枡  
		t.setToNow(); // 鍙栧緱绯荤粺鏃堕棿銆? 
//		int year = t.year;  
//		int month = t.month;  
//		int date = t.monthDay;  
		hour = t.hour;    // 0-23
		minite = t.minute;    // 0-23
		
		this.listViewH = (WheelView) findViewById(R.id.listViewH);
		this.listViewM = (WheelView) findViewById(R.id.listViewM);
		this.listViewModel = (WheelView) findViewById(R.id.listViewModel);
		this.linearLayoutContainer = findViewById(R.id.linearLayoutContainer);
		if ("off".equalsIgnoreCase(tag)) {
			this.linearLayoutContainer.setVisibility(View.INVISIBLE);
		}
		// ================
		String[] modelH = new String[24];
		for (int i = 0; i < 24; i++) {
			modelH[i] = NumberHelper.LeftPad_Tow_Zero(i);
		}
		wheelAdapterH = new WheelModelAdapter(this, modelH);
		this.listViewH.setViewAdapter(wheelAdapterH);
		this.listViewH.setCurrentItem(hour);
		this.listViewH.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				hour = newValue;
			}
		});

		String[] modelM = new String[60];
		for (int i = 0; i < 60; i++) {
			modelM[i] = NumberHelper.LeftPad_Tow_Zero(i);
		}
		wheelAdapterM = new WheelModelAdapter(this, modelM);
		this.listViewM.setViewAdapter(wheelAdapterM);
		this.listViewM.setCurrentItem(minite);
		// this.listViewModel.setCyclic(true);
		this.listViewM.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				minite = newValue;
			}
		});

		final String[] timerModel = getResources().getStringArray(R.array.timer_model);
		final String[] modelStrings = new String[timerModel.length];
		for (int i = 0; i < timerModel.length; i++) {
			String[] datas = timerModel[i].split(",");
			modelStrings[i] = datas[0];
		}
		
		wheelAdapterModel = new WheelModelAdapter(this, modelStrings);
		this.listViewModel.setViewAdapter(wheelAdapterModel);
		this.listViewModel.setCurrentItem(modelStrings.length / 2);
		this.modelText=modelStrings[modelStrings.length / 2];
		
		this.listViewModel.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				String[] datas = timerModel[newValue].split(",");
				model = Integer.parseInt(datas[1]);
				modelText = datas[0];
			}
		});

		View.OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.buttonCancell: {
					finish();
				}
					break;
				case R.id.buttonSave: {
					putDataback();
				}
					break;
				}
			}
		};

		findButtonById(R.id.buttonCancell).setOnClickListener(clickListener);
		findButtonById(R.id.buttonSave).setOnClickListener(clickListener);
	}

	private void putDataback() {

		Intent intent = new Intent();
		intent.putExtra("hour", hour);
		intent.putExtra("minite", minite);
		intent.putExtra("model", model);
		intent.putExtra("modelText", modelText);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}
}
