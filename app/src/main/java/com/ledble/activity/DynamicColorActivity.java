package com.ledble.activity;

import java.util.ArrayList;
import java.util.HashMap;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.common.BaseProgressActivity;
import com.common.adapter.AnimationListenerAdapter;
import com.common.adapter.OnSeekBarChangeListenerAdapter;
import com.common.uitl.ListUtiles;
import com.common.uitl.Tool;
import com.common.view.SegmentedRadioGroup;
import com.ledble.R;
import com.ledble.bean.MyColor;
import com.ledble.constant.Constant;
import com.ledble.net.NetConnectBle;
import com.ledble.view.BlackWiteSelectView;
import com.ledble.view.BlackWiteSelectView.OnSelectColor;
import com.ledble.view.ColorTextView;
import com.ledble.view.MyColorPickerImageView4RGB;

@SuppressLint("NewApi")
public class DynamicColorActivity extends BaseProgressActivity {

	private SegmentedRadioGroup dynamicChangeGroup;
	private Button buttonRunButton;//运行
	private Button buttonBackButton;
	private Button buttonConfirButton;
	private SeekBar seekBarBrightNess2;
	private TextView textViewBrightNess2;
	private TextView textRGB;
	private TextView textViewRingBrightSC;

	private View relativeTabColorCover;
	private int currentSelecColorFromPicker;
	private ColorTextView actionView;
	private ArrayList<ColorTextView> colorTextViews;
	private MyColorPickerImageView4RGB imageViewPicker ;
	private BlackWiteSelectView blackWiteSelectView ;
	private SharedPreferences sp;
	private int style = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		if (Build.VERSION.SDK_INT >= 21) {
		    View decorView = getWindow().getDecorView();
		    int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
		            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
		    decorView.setSystemUiVisibility(option);
		    getWindow().setStatusBarColor(Color.TRANSPARENT); //也可以设置成灰色透明的，比较符合Material Design的风格
		}
		
		initView();
	}

	@Override
	public void initView() {
		setContentView(R.layout.activity_dynamic_color);
		this.sp = getSharedPreferences(Constant.DYNAMIC_DIY, Context.MODE_PRIVATE);
		
		textRGB = (TextView)findViewById(R.id.tvRGB);
		textViewRingBrightSC = (TextView)findViewById(R.id.tvRingBrightnessSC);
		textViewBrightNess2 = (TextView)findTextViewById(R.id.textViewBrightNess2);
		seekBarBrightNess2 = (SeekBar)findViewById(R.id.seekBarBrightNess2);
		this.seekBarBrightNess2.setOnSeekBarChangeListener(new OnSeekBarChangeListenerAdapter() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (progress == 0) {
					seekBar.setProgress(1);
					NetConnectBle.getInstance().setensitivity(1);
					textViewBrightNess2.setText(getResources().getString(R.string.sensitivity_set, 1));
		
				} else {
					NetConnectBle.getInstance().setensitivity(progress);
					textViewBrightNess2.setText(getResources().getString(R.string.sensitivity_set, progress));
				}
			}
		});
		
		
		dynamicChangeGroup = (SegmentedRadioGroup)findViewById(R.id.dynamicChangeButton);
		dynamicChangeGroup.check(R.id.dynamicChangeButtonOne);
		dynamicChangeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (R.id.dynamicChangeButtonOne == checkedId) {
					style = 0;
				} else if((R.id.dynamicChangeButtonTwo == checkedId)) {
					style = 1;
				} else if((R.id.dynamicChangeButtonThree == checkedId)) {
					style = 2;
				} 
				else if((R.id.dynamicChangeButtonFour == checkedId)) {
					style = 3;
				}
			}
		});
		
		buttonRunButton = findButtonById(R.id.button1);
		buttonRunButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				putDataBack(getSelectColor());
			}
		});
		
		relativeTabColorCover = findViewById(R.id.relativeTabColorCover);
		buttonBackButton = findButtonById(R.id.buttonBack);
		buttonBackButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		initColorBlock();
		initColorSelecterView();
	}

	private void putDataBack(ArrayList<MyColor> colors) {

		NetConnectBle.getInstance().setDynamicDiy(colors, style);;
	}

	private ArrayList<MyColor> getSelectColor() {

		ArrayList<MyColor> colorList = new ArrayList<MyColor>();
		if (!ListUtiles.isEmpty(colorTextViews)) {
			for (ColorTextView ctx : colorTextViews) {
				if (-1 != ctx.getColor()) {
					int[] rgb = Tool.getRGB(ctx.getColor());
					colorList.add(new MyColor(rgb[0], rgb[1], rgb[2]));
				}
			}
		}
		return colorList;
	}

	/**
	 * 初始化颜色选择block
	 */
	private void initColorBlock() {
		View blocks = findViewById(R.id.linearLayoutViewBlocks);
		colorTextViews = new ArrayList<ColorTextView>();
		for (int i = 1; i <= 16; i++) {
			final ColorTextView tv = (ColorTextView) blocks.findViewWithTag((String.valueOf("editColor") + i));
			String tag = (String) tv.getTag();
			int color = sp.getInt(tag, -1);
			if(color != -1){
				int radius = 10;
				float[] outerR = new float[] { radius, radius, radius, radius, radius, radius, radius, radius };
				// 构造一个圆角矩形,可以使用其他形状，这样ShapeDrawable
				RoundRectShape roundRectShape = new RoundRectShape(outerR, null, null); 
				// 组合圆角矩形和ShapeDrawable
				ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape); 
				// 设置形状的颜色
				shapeDrawable.getPaint().setColor(color); 
				// 设置绘制方式为填充
				shapeDrawable.getPaint().setStyle(Paint.Style.FILL);
				// 将当前选择得颜色设置到触发颜色编辑器得View上
				tv.setBackgroundDrawable(shapeDrawable);
				tv.setColor(color);
				tv.setText("");
			}
			tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 点击弹出颜色选择框
					v.startAnimation(AnimationUtils.loadAnimation(DynamicColorActivity.this, R.anim.layout_scale));
					int color = tv.getColor();
					if(color == -1) {
						showColorCover((ColorTextView) v);
					}
				}
			});
			tv.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					ColorTextView cv = (ColorTextView) v;
					cv.setColor(-1);
					String tag = (String) tv.getTag();
					sp.edit().putInt(tag, -1).commit();
					// 长按删除颜色
					cv.setBackgroundDrawable(getResources().getDrawable(R.drawable.block_shap_color));
					cv.setText("+");
					return true;
				}
			});
			colorTextViews.add(tv);
		}

	}

	/**
	 * 弹出颜色编辑器
	 * 
	 * @param actionView
	 */
	private void showColorCover(ColorTextView actionView) {
		this.actionView = actionView;
		//数据初始化
		currentSelecColorFromPicker = -1;
		textRGB.setText(getResources().getString(R.string.r_g_b, 0, 0, 0));
		
		Animation slideInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_in);
		slideInAnimation.setAnimationListener(new AnimationListenerAdapter() {
			@Override
			public void onAnimationStart(Animation animation) {
				relativeTabColorCover.setVisibility(View.VISIBLE);
				blackWiteSelectView.setVisibility(View.INVISIBLE);
				textViewRingBrightSC.setVisibility(View.INVISIBLE);
			}
		});
		relativeTabColorCover.startAnimation(slideInAnimation);
	}

	/**
	 * 隐藏
	 */
	private void hideColorCover() {
		Animation slideInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_out);
		slideInAnimation.setAnimationListener(new AnimationListenerAdapter() {
			@Override
			public void onAnimationEnd(Animation animation) {
				relativeTabColorCover.setVisibility(View.GONE);
			}
		});
		relativeTabColorCover.startAnimation(slideInAnimation);
	}

	/**
	 * 初始化颜色编辑器
	 */
	private void initColorSelecterView() {
		imageViewPicker = (MyColorPickerImageView4RGB) findViewById(R.id.imageViewPicker);
		blackWiteSelectView = (BlackWiteSelectView) findViewById(R.id.blackWiteSelectView);
		imageViewPicker.setOnTouchPixListener(new MyColorPickerImageView4RGB.OnTouchPixListener() {
			@Override
			public void onColorSelect(int color, float angle) {
				blackWiteSelectView.setStartColor(color);
				currentSelecColorFromPicker = color;
				
				int[] colors = Tool.getRGB(color);
				textRGB.setText(getResources().getString(R.string.r_g_b, colors[0], colors[1], colors[2]));
			}
		});

		blackWiteSelectView.setOnSelectColor(new OnSelectColor() {
			@Override
			public void onColorSelect(int color,int progress) {
				currentSelecColorFromPicker = color;
				int p = progress;
				if (progress <= 0) {
					p = 1;
				}
				if (progress >= 100) {
					p = 100;
				}
				textViewRingBrightSC.setText(getResources().getString(R.string.brightness_set, p));
			}
		});

		View viewColors = findViewById(R.id.viewColors);
		ArrayList<View> viewCsArrayLis = new ArrayList<View>();
		int[] colors = { 0xffff0000, 0xffffff00, 0xff00ff00, 0xff00ffff, 0xff0000ff, 0xffff00ff };
		final HashMap<Integer, Double> rmap = new HashMap<Integer, Double>();
		rmap.put(colors[0], 0.0);
		rmap.put(colors[1], Math.PI / 3);
		rmap.put(colors[2], Math.PI * 2 / 3);
		rmap.put(colors[3], Math.PI);
		rmap.put(colors[4], Math.PI * 4 / 3);
		rmap.put(colors[5], Math.PI * 5 / 3);
		
		for (int i = 1; i <= 6; i++) {
			View vc = viewColors.findViewWithTag("viewColor" + i);
			vc.setTag(colors[i - 1]);
			vc.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int selectColor = (Integer) v.getTag();
					currentSelecColorFromPicker = selectColor;
					blackWiteSelectView.setStartColor(selectColor);
					imageViewPicker.move2Ege(rmap.get(selectColor));
					
					int[] colors = Tool.getRGB(selectColor);
					textRGB.setText(getResources().getString(R.string.r_g_b, colors[0], colors[1], colors[2]));
				}
			});
			viewCsArrayLis.add(vc);
		}
		buttonConfirButton = findButtonById(R.id.buttonConfirm);
		buttonConfirButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hideColorCover();
				if (currentSelecColorFromPicker == -1)
					return;
				int radius = 10;
				float[] outerR = new float[] { radius, radius, radius, radius, radius, radius, radius, radius };
				RoundRectShape roundRectShape = new RoundRectShape(outerR, null, null); // 构造一个圆角矩形,可以使用其他形状，这样ShapeDrawable
																						// 就会根据形状来绘制。
				ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape); // 组合圆角矩形和ShapeDrawable
				shapeDrawable.getPaint().setColor(currentSelecColorFromPicker); // 设置形状的颜色
				shapeDrawable.getPaint().setStyle(Paint.Style.FILL); // 设置绘制方式为填充

				actionView.setColor(currentSelecColorFromPicker);
				// 将当前选择得颜色设置到触发颜色编辑器得View上
				actionView.setBackgroundDrawable(shapeDrawable);
				// 保存颜色值
				String tag = (String) actionView.getTag();
				sp.edit().putInt(tag, currentSelecColorFromPicker).commit();
				actionView.setText("");
			}
		});
	}

	@Override
	public void onBackPressed() {
		if (relativeTabColorCover.getVisibility() == View.VISIBLE) {
			hideColorCover();
			return;
		}
		super.onBackPressed();
	}

}
