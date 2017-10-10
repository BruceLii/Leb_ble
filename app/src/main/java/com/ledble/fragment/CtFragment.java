package com.ledble.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.common.adapter.OnSeekBarChangeListenerAdapter;
import com.common.view.SegmentedRadioGroup;
import com.ledble.R;
import com.ledble.activity.MainActivity;
import com.ledble.adapter.ModelAdapter;
import com.ledble.base.LedBleFragment;
import com.ledble.bean.AdapterBean;
import com.ledble.view.MyColorPickerImageView;
import com.ledble.view.MyColorPickerImageView.OnTouchPixListener;
/**
 * 色温
 * @author ftl
 *
 */
public class CtFragment extends LedBleFragment {
	
	private MyColorPickerImageView pikerImageView;
	private TextView textViewBrightNess;
	private TextView textViewBrightNess2;
	private TextView textViewWarmCool;
	private SeekBar seekBar;
	private SeekBar seekBar2;
	private SegmentedRadioGroup segementGoup;
	private View relativeLayoutTab1, relativeLayoutTab2;
	private ListView listViewModel;
	private ModelAdapter maAdapter;
	private TextView textViewWarmCoolModel;
	private ToggleButton tgbtn ;
	
	private View mContentView;
	private MainActivity mActivity;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,  Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.fragment_ct, container, false);
		return mContentView;
	}

	@Override
	public void initData() {
		mActivity = (MainActivity) getActivity();
		segementGoup = mActivity.getSegmentCt();
	}

	@Override
	public void initView() {
		this.seekBar = (SeekBar) mContentView.findViewById(R.id.seekBarBrightNess);
		this.seekBar2 = (SeekBar) mContentView.findViewById(R.id.seekBar2);

		this.listViewModel = (ListView) mContentView.findViewById(R.id.listViewModel);
		this.listViewModel.setAdapter(buildModel());
		this.textViewWarmCoolModel = (TextView) mContentView.findViewById(R.id.textViewWarmCoolModel);
		this.listViewModel.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				maAdapter.setIndex(position);
				maAdapter.notifyDataSetChanged();
				AdapterBean abean = ((AdapterBean) maAdapter.getItem(position));
				String label = mActivity.getResources().getString(R.string.current_mode_format, abean.getLabel());
				textViewWarmCoolModel.setText(label);
				setSetModeFromAdapter(Integer.parseInt(abean.getValue()));
			}

		});
		this.relativeLayoutTab1 = mContentView.findViewById(R.id.relativeLayoutTab1);
		this.relativeLayoutTab2 = mContentView.findViewById(R.id.relativeLayoutTab2);

		this.segementGoup.check(R.id.rbCtOne);
		this.segementGoup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (R.id.rbCtOne == checkedId) {
					relativeLayoutTab1.setVisibility(View.VISIBLE);
					relativeLayoutTab2.setVisibility(View.GONE);
				} else {
					relativeLayoutTab1.setVisibility(View.GONE);
					relativeLayoutTab2.setVisibility(View.VISIBLE);
				}
			}
		});
		this.seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListenerAdapter() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if(0==progress){
					String brightNess = mActivity.getResources().getString(R.string.brightness_set, String.valueOf(1));
					textViewBrightNess.setText(brightNess);
					seekBar.setProgress(1);
					setBrightNess(1);
				}else {
					String brightNess = mActivity.getResources().getString(R.string.brightness_set, String.valueOf(progress));
					textViewBrightNess.setText(brightNess);
					setBrightNess(progress);
				}
			}
		});
		this.seekBar2.setOnSeekBarChangeListener(new OnSeekBarChangeListenerAdapter() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if(0==progress){
					String brightNess = mActivity.getResources().getString(R.string.brightness_set, String.valueOf(1));
					textViewBrightNess2.setText(brightNess);
					seekBar.setProgress(1);
					setBrightNess(1);
				}else{
					String brightNess = mActivity.getResources().getString(R.string.brightness_set, String.valueOf(progress));
					textViewBrightNess2.setText(brightNess);
					setBrightNess(progress);
				}
			}
		});

		this.textViewWarmCool = (TextView) mContentView.findViewById(R.id.textViewWarmCool);
		this.textViewBrightNess = (TextView) mContentView.findViewById(R.id.textViewBrightNess);
		this.textViewBrightNess2 = (TextView) mContentView.findViewById(R.id.textViewBrightNess2);

		this.pikerImageView = (MyColorPickerImageView) mContentView.findViewById(R.id.pikerImageView);
		this.pikerImageView.setInnerCircle(0.459f);
		this.pikerImageView.setOnTouchPixListener(new OnTouchPixListener() {
			@Override
			public void onColorSelect(int color, float angle) {
				int cool = (int) ((angle / 360) * 100);
				int warm = 100 - cool;
				String warmText = mActivity.getString(R.string.cool_warm, warm, cool);
				textViewWarmCool.setText(warmText);
				setModelFromPicker(warm);
			}
		});

		
		tgbtn = (ToggleButton) mContentView.findViewById(R.id.toggleButton1);
		tgbtn.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mActivity.isLightOpen=isChecked;
				if (isChecked) {
					mActivity.open();
				} else {
					mActivity.close();
				}
			}

		});
	}

	@Override
	public void initEvent() {
		updateProgressUI();
	}
	
	public void updateProgressUI() {
		tgbtn.setChecked(mActivity.isLightOpen);
		seekBar.setProgress(mActivity.brightness);
		seekBar2.setProgress(mActivity.brightness);
	}
	
	private void setSetModeFromAdapter(int model) {
		mActivity.setCTModel(model);
	}

	private void setModelFromPicker(int warm) {
		mActivity.setCT(warm, 100 - warm);
	}

	private void setBrightNess(int progress) {
		mActivity.setBrightNess(progress);
		updateProgressUI();
	}

	private ModelAdapter buildModel() {
		String[] ary = mActivity.getResources().getStringArray(R.array.ct_mode);
		ArrayList<AdapterBean> abs = new ArrayList<AdapterBean>();
		for (String lable : ary) {
			String label[] = lable.split(",");
			AdapterBean abean = new AdapterBean(label[0], label[1]);
			abs.add(abean);
		}
		maAdapter = new ModelAdapter(mActivity, abs);
		return maAdapter;
	}

}
