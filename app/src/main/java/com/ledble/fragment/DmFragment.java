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
import android.widget.TextView;
import android.widget.ToggleButton;

import com.common.view.SegmentedRadioGroup;
import com.ledble.R;
import com.ledble.activity.MainActivity;
import com.ledble.adapter.ModelAdapter;
import com.ledble.base.LedBleFragment;
import com.ledble.bean.AdapterBean;
import com.ledble.view.MyColorPickerImageView;
import com.ledble.view.MyColorPickerImageView.OnTouchPixListener;
/**
 * 单色
 * @author ftl
 *
 */
public class DmFragment extends LedBleFragment {
	
	private SegmentedRadioGroup segmentButton;
	private View relativeLayoutTab1;
	private View relativeLayoutTab2;
	private ModelAdapter maAdapter;
	private ListView listViewModel;
	private TextView textViewCurretModel;
	private TextView textViewBrightNess;
	private MyColorPickerImageView pikerImageView;
	private ToggleButton tgbtn ;
	
	private View mContentView;
	private MainActivity mActivity;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,  Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.fragment_dm, container, false);
		return mContentView;
	}

	@Override
	public void initData() {
		mActivity = (MainActivity) getActivity();
		segmentButton = mActivity.getSegmentDm();
	}

	@Override
	public void initView() {
		this.pikerImageView = (MyColorPickerImageView) mContentView.findViewById(R.id.pikerImageView);
		this.textViewBrightNess = (TextView) mContentView.findViewById(R.id.textViewBrightNess);
		this.pikerImageView.setInnerCircle(0.25f);
		this.pikerImageView.setOnTouchPixListener(new OnTouchPixListener() {
			@Override
			public void onColorSelect(int color, float angle) {
				int cool = (int) ((angle / 360) * 100);
				String percentColor = cool + "%";
				textViewBrightNess.setText(mActivity.getResources().getString(R.string.brightness) + ":" + percentColor);
				setDimBrighNess(cool);
			}
		});
		this.textViewCurretModel = (TextView) mContentView.findViewById(R.id.textViewCurretModel);
		this.relativeLayoutTab1 = mContentView.findViewById(R.id.relativeLayoutTab1);
		this.relativeLayoutTab2 = mContentView.findViewById(R.id.relativeLayoutTab2);
		this.listViewModel = (ListView) mContentView.findViewById(R.id.listViewModel);
		this.listViewModel.setAdapter(buildModel());
		this.listViewModel.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				maAdapter.setIndex(position);
				maAdapter.notifyDataSetChanged();
				AdapterBean abean = ((AdapterBean) maAdapter.getItem(position));
				String label = mActivity.getResources().getString(R.string.current_mode_format, abean.getLabel());
				String value = abean.getValue();
				textViewCurretModel.setText(label);
				setDimMode(Integer.parseInt(value));
			}

		});
		this.segmentButton.check(R.id.rbDmOne);
		this.segmentButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (R.id.rbDmOne == checkedId) {
					relativeLayoutTab1.setVisibility(View.VISIBLE);
					relativeLayoutTab2.setVisibility(View.GONE);
				} else {
					relativeLayoutTab1.setVisibility(View.GONE);
					relativeLayoutTab2.setVisibility(View.VISIBLE);
				}
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
		tgbtn.setChecked(mActivity.isLightOpen);
	}
	
	private void setDimBrighNess(int dimPercent) {
		mActivity.setDim(dimPercent);
	}

	private void setDimMode(int value) {
		mActivity.setDimModel(value);
	}

	private ModelAdapter buildModel() {
		String[] ary = mActivity.getResources().getStringArray(R.array.dm_mode);
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
