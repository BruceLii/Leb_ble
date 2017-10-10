package com.ledble.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import butterknife.Bind;

import com.common.uitl.NumberHelper;
import com.common.uitl.SharePersistent;
import com.common.uitl.StringUtils;
import com.ledble.R;
import com.ledble.activity.MainActivity;
import com.ledble.activity.TimerSettingActivity;
import com.ledble.base.LedBleFragment;
import com.ledble.bean.TimerModelBean;
/**
 * 定时
 * @author ftl
 *
 */
public class TimerFragment extends LedBleFragment {
	
	@Bind(R.id.linearLayoutTimerOn) LinearLayout linearLayoutTimerOn;
	@Bind(R.id.linearLayoutTimerOff) LinearLayout linearLayoutTimerOff;
	@Bind(R.id.textViewOnTime) TextView textViewOnTime;
	@Bind(R.id.textViewOffTime) TextView textViewOffTime;
	@Bind(R.id.textViewModelText) TextView textViewModelText;
	
	@Bind(R.id.toggleOn) ToggleButton toggleButtonOn;
	@Bind(R.id.toggleOff) ToggleButton toggleButtonOff;
	
	private final int INT_TIMER_ON = 11;
	private final int INT_TIMER_OFF = 12;
	private MainActivity mActivity;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,  Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_timer, container, false);
	}

	@Override
	public void initData() {
		mActivity = (MainActivity) getActivity();
		String ttag = getMyTag();
		TimerModelBean tb = (TimerModelBean) SharePersistent.getObjectValue(getActivity(), ttag);
		if (null != tb) {
			textViewOnTime.setText(NumberHelper.LeftPad_Tow_Zero(tb.getTimerOnHour()) + ":"
					+ NumberHelper.LeftPad_Tow_Zero(tb.getTimerOnMinute()));
			toggleButtonOn.setChecked(tb.isOpenTurnOn());

			textViewOffTime.setText(NumberHelper.LeftPad_Tow_Zero(tb.getTimerOffHour()) + ":"
					+ NumberHelper.LeftPad_Tow_Zero(tb.getTimerOffMinute()));
			toggleButtonOff.setChecked(tb.isCloseTurnOn());
			textViewModelText.setText(getActivity().getResources().getString(R.string.timer_on)
					+ (StringUtils.isEmpty(tb.getModelText()) ? "" : ":" + tb.getModelText()));
			textViewModelText.setTag(tb.getModel());
		}
	}

	@Override
	public void initView() {
		linearLayoutTimerOn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoTimerSettting(v, INT_TIMER_ON);
			}
		});
		linearLayoutTimerOff.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				gotoTimerSettting(v, INT_TIMER_OFF);
			}
		});

		toggleButtonOn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				saveState2Tag(toggleButtonOn.isChecked(), toggleButtonOff.isChecked(), -1, -1, -1, -1, null, -1);
				TimerModelBean tb = (TimerModelBean) SharePersistent.getObjectValue(getActivity(), getTag());
				if (toggleButtonOn.isChecked() && null != tb) {
					mActivity.timerOn(tb.getTimerOnHour(), tb.getTimerOnMinute(), tb.getModel());
				} else {
					mActivity.turnOnOffTimerOn(0);
				}
			}
		});
		toggleButtonOff.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// textViewOffTime //保存定时关的时间
				saveState2Tag(toggleButtonOn.isChecked(), toggleButtonOff.isChecked(), -1, -1, -1, -1, null, -1);
				TimerModelBean tb = (TimerModelBean) SharePersistent.getObjectValue(getActivity(), getTag());
				if (toggleButtonOff.isChecked() && null != tb) {
					mActivity.timerOff(tb.getTimerOffHour(), tb.getTimerOffMinute());
				} else {
					mActivity.turnOnOffTimerOff(0);
				}
			}
		});
	}

	@Override
	public void initEvent() {
		
	}
	
	private void gotoTimerSettting(View v, int id) {
		Intent intent = new Intent(getActivity(), TimerSettingActivity.class);
		intent.putExtra("tag", (String) v.getTag());
		getActivity().startActivityForResult(intent, id);
	}
	
	private String getMyTag() {
		String tag = mActivity.getGroupName();
		if (StringUtils.isEmpty(tag) || "null".equals(tag)) {
			tag = TimerModelBean.groupNameDefaut;
		}
		return tag;
	}
	
	/**
	 * 保存设置参数
	 * 
	 * @param timerOnTurnOn
	 * @param timerOffTurnOn
	 * @param timerOnHour
	 * @param timerOnMinute
	 * @param timerOffHour
	 * @param timerOffMinute
	 */
	private void saveState2Tag(boolean timerOnTurnOn, boolean timerOffTurnOn, int timerOnHour, int timerOnMinute, int timerOffHour,
			int timerOffMinute, String modelText, int model) {
		String tag = getTag();

		TimerModelBean timerModelBean = (TimerModelBean) SharePersistent.getObjectValue(getActivity(), tag);
		if (null == timerModelBean) {
			timerModelBean = new TimerModelBean();
		}
		timerModelBean.setGroupName(getTag());

		timerModelBean.setOpenTurnOn(timerOnTurnOn);
		timerModelBean.setCloseTurnOn(timerOffTurnOn);

		if (-1 != timerOnHour) {
			timerModelBean.setTimerOnHour(timerOnHour);
			timerModelBean.setTimerOnMinute(timerOnMinute);
			timerModelBean.setModelText(modelText);
			timerModelBean.setModel(model);
		}
		if (-1 != timerOffHour) {
			timerModelBean.setTimerOffHour(timerOffHour);
			timerModelBean.setTimerOffMinute(timerOffMinute);
		}
		SharePersistent.setObjectValue(getActivity(), tag, timerModelBean);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == INT_TIMER_ON && resultCode == Activity.RESULT_OK) {
			int hour = data.getIntExtra("hour", -1);
			int minute = data.getIntExtra("minite", -1);
			int model = data.getIntExtra("model", -1);
			String modelText = data.getStringExtra("modelText");

			textViewOnTime.setText(NumberHelper.LeftPad_Tow_Zero(hour) + ":" + NumberHelper.LeftPad_Tow_Zero(minute));
			textViewOnTime.setTag(modelText);
			textViewModelText.setText(getActivity().getResources().getString(R.string.timer_on) + ":" + modelText);
			textViewModelText.setTag(model);
			saveState2Tag(toggleButtonOn.isChecked(), toggleButtonOff.isChecked(), hour, minute, -1, -1, modelText, model);
			if (toggleButtonOn.isChecked()) {
				mActivity.timerOn(hour, minute, model);
			}
			return;
		}
		if (requestCode == INT_TIMER_OFF && resultCode == Activity.RESULT_OK) {
			int hour = data.getIntExtra("hour", -1);
			int minute = data.getIntExtra("minite", -1);
			textViewOffTime.setText(NumberHelper.LeftPad_Tow_Zero(hour) + ":" + NumberHelper.LeftPad_Tow_Zero(minute));
			saveState2Tag(toggleButtonOn.isChecked(), toggleButtonOff.isChecked(), -1, -1, hour, minute, null, -1);
			if (toggleButtonOff.isChecked()) {
				mActivity.timerOff(hour, minute);
			}
			return;
		}
	}

}
