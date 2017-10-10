package com.ledble.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.common.BaseActivity;
import com.ledble.R;


public class OprationManualActivity extends BaseActivity {

	private ViewPager mViewPaper;
	private List<ImageView> images;
	private List<View> dots;
//	private int currentItem;
	// 记录上一次点的位置
	private int oldPosition = 0;
	// 存放图片的id
	private int[] imageIds = new int[] { R.drawable.instruction_image_000, R.drawable.instruction_image_001, R.drawable.instruction_image_002, R.drawable.instruction_image_003, R.drawable.instruction_image_004};
	// 存放图片的标题
//	private String[] titles = new String[] { "OK", "OK"};
	private TextView okBtn;
	private ViewPagerAdapter adapter;
//	private ScheduledExecutorService scheduledExecutorService;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		if (Build.VERSION.SDK_INT >= 21) {
			View decorView = getWindow().getDecorView();
			int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
			decorView.setSystemUiVisibility(option);
			getWindow().setStatusBarColor(Color.TRANSPARENT); //透明 状态栏
		}
		
		initView();
	}

	@Override
	public void initView() {
		super.initView();
		setContentView(R.layout.activity_opration_manual);
		mViewPaper = (ViewPager) findViewById(R.id.vp);

		inintent();
	}

	private void inintent() {
		// 显示的图片
		images = new ArrayList<ImageView>();
		for (int i = 0; i < imageIds.length; i++) {
			ImageView imageView = new ImageView(this);
			imageView.setBackgroundResource(imageIds[i]);
			images.add(imageView);
		}
		// 显示的小点
		dots = new ArrayList<View>();
		dots.add(findViewById(R.id.dot_0));
		dots.add(findViewById(R.id.dot_1));
		dots.add(findViewById(R.id.dot_2));
		dots.add(findViewById(R.id.dot_3));
		dots.add(findViewById(R.id.dot_4));

		okBtn = (TextView) findViewById(R.id.title);
		okBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
//		title.setText(titles[0]);

		adapter = new ViewPagerAdapter();
		mViewPaper.setAdapter(adapter);

		mViewPaper.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
//				title.setText(titles[position]);
				dots.get(position).setBackgroundResource(R.drawable.dot_focused);
				dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);

				oldPosition = position;
//				currentItem = position;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	/**
	 * 自定义Adapter
	 * 
	 * @author liuyazhuang
	 *
	 */
	private class ViewPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return images.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup view, int position, Object object) {
			// TODO Auto-generated method stub
			// super.destroyItem(container, position, object);
			// view.removeView(view.getChildAt(position));
			// view.removeViewAt(position);
			view.removeView(images.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			// TODO Auto-generated method stub
			view.addView(images.get(position));
			return images.get(position);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
