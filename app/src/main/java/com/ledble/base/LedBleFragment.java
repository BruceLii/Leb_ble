package com.ledble.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import butterknife.ButterKnife;

public abstract class LedBleFragment extends Fragment {
	
	public LedBleActivity activity;
	public LedBleApplication baseApp;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		this.activity = (LedBleActivity) activity;
		this.baseApp = this.activity.getBaseApp();
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButterKnife.bind(this, view);
		initData();
		initView();
		initEvent();
	}
	
	/**
	 * 初始化默认数据
	 */
	public abstract void initData();

	/**
	 * 初始化View
	 */
	public abstract void initView();

	/**
	 * 初始化事件（监听）
	 */
	public abstract void initEvent();
	
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
	}

}
