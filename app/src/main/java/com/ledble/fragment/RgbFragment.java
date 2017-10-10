package com.ledble.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import butterknife.Bind;

import com.common.adapter.OnSeekBarChangeListenerAdapter;
import com.common.uitl.ListUtiles;
import com.common.uitl.SharePersistent;
import com.common.uitl.Tool;
import com.common.view.SegmentedRadioGroup;
import com.ledble.R;
import com.ledble.activity.MainActivity;
import com.ledble.adapter.ModelAdapter;
import com.ledble.base.LedBleFragment;
import com.ledble.bean.AdapterBean;
import com.ledble.bean.MyColor;
import com.ledble.constant.Constant;
import com.ledble.view.BlackWiteSelectView;
import com.ledble.view.BlackWiteSelectView.OnSelectColor;
import com.ledble.view.ColorTextView;
import com.ledble.view.MyColorPickerImageView4RGB;

/**
 * 彩色
 * 
 * @author ftl
 *
 */
public class RgbFragment extends LedBleFragment {

	@Bind(R.id.changeButton) SegmentedRadioGroup changeRadioGroup;
	@Bind(R.id.relativeTab1) View relativeTab1;// 色环
	@Bind(R.id.relativeTab2) View relativeTab2;// 模式
	@Bind(R.id.relativeTab3) View relativeTab3;// 自定义
	@Bind(R.id.listViewModel) ListView listViewModel;
	@Bind(R.id.textViewCurretModel) TextView textViewCurretModel;
	@Bind(R.id.imageViewPicker) MyColorPickerImageView4RGB imageViewPicker;
	@Bind(R.id.blackWiteSelectView) BlackWiteSelectView blackWiteSelectView;

	private SegmentedRadioGroup segmentedRadioGroup;
	private ModelAdapter maAdapter;

	@Bind(R.id.seekBarSpeed) SeekBar seekBarSpeedBar;
	@Bind(R.id.textViewSpeed) TextView textViewSpeed;
	@Bind(R.id.seekBarBrightNess2) SeekBar seekBarBrightNess2;
	@Bind(R.id.textViewBrightNess2) TextView textViewBrightNess2;
	@Bind(R.id.seekBarBrightNess) SeekBar seekBarBrightness;
	@Bind(R.id.textViewBrightNess) TextView textViewBrightness;
	@Bind(R.id.toggleButton1) ToggleButton tgbtn;
	@Bind(R.id.tvBrightness) TextView tvBrightness;

	// relativeTab3
	@Bind(R.id.button1) Button buttonRunButton;// 运行
	

	private int style = 0;
	private int currentTab = 1;// 1：色环，2：模式
	private int currentSelecColorFromPicker;
	private String diyViewTag;
	private View mContentView;
	private ColorTextView actionView;
	private ArrayList<ColorTextView> colorTextViews;
	private MainActivity mActivity;
	private SharedPreferences sp;
	private PopupWindow mPopupWindow;
	private View menuView;

	// 隐藏部分 选色部分View
	private SegmentedRadioGroup srgCover;
	private TextView textRGB;
	private LinearLayout llRing;
	private LinearLayout llCover;
	private TextView tvCoverModel;
	private ListView lvCover;
	private MyColorPickerImageView4RGB imageViewPicker2;
	private BlackWiteSelectView blackWiteSelectView2;
	private TextView textViewRingBrightSC;
	private SeekBar seekBarSpeedBarSC;
	private TextView textViewSpeedSC;
	private SeekBar seekBarBrightBarSC;
	private TextView textViewBrightSC;
	private Button buttonSelectColorConfirm;// 确认

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.fragment_rgb, container, false); 
		menuView = inflater.inflate(R.layout.activity_select_color, container, false);
		return mContentView;
	}

	@Override
	public void initData() {
		mActivity = (MainActivity) getActivity();
		
		segmentedRadioGroup = mActivity.getSegmentRgb();
		sp = mActivity.getSharedPreferences(Constant.SPF_DIY, Context.MODE_PRIVATE);

		buttonSelectColorConfirm = (Button) menuView.findViewById(R.id.buttonSelectColorConfirm); // 通过另外一个布局对象的findViewById获取其中的控件
		textRGB = (TextView) menuView.findViewById(R.id.tvRGB);
		srgCover = (SegmentedRadioGroup) menuView.findViewById(R.id.srgCover);
		llRing = (LinearLayout) menuView.findViewById(R.id.llRing);
		llCover = (LinearLayout) menuView.findViewById(R.id.llCover);
		tvCoverModel = (TextView) menuView.findViewById(R.id.tvCoverModel);
		lvCover = (ListView) menuView.findViewById(R.id.lvCover);
		imageViewPicker2 = (MyColorPickerImageView4RGB) menuView.findViewById(R.id.imageViewPicker2);
		blackWiteSelectView2 = (BlackWiteSelectView) menuView.findViewById(R.id.blackWiteSelectView2);
		seekBarSpeedBarSC = (SeekBar) menuView.findViewById(R.id.seekBarSpeed);
		textViewSpeedSC = (TextView) menuView.findViewById(R.id.textViewSpeed);
		seekBarBrightBarSC = (SeekBar) menuView.findViewById(R.id.seekBarBrightNess);
		textViewBrightSC = (TextView) menuView.findViewById(R.id.textViewBrightNess);
		textViewRingBrightSC = (TextView) menuView.findViewById(R.id.tvRingBrightnessSC);

		this.seekBarSpeedBar.setOnSeekBarChangeListener(new OnSeekBarChangeListenerAdapter() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (progress == 0) {
					seekBar.setProgress(1);
					mActivity.setSpeed(1);
					textViewSpeed.setText(mActivity.getResources().getString(R.string.speed_set, 1));

				} else {
					mActivity.setSpeed(progress);
					textViewSpeed.setText(mActivity.getResources().getString(R.string.speed_set, progress));

				}
			}
		});
		
		seekBarSpeedBarSC.setOnSeekBarChangeListener(new OnSeekBarChangeListenerAdapter() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (progress == 0) {
					seekBar.setProgress(1);
					mActivity.setSpeed(1);
					textViewSpeedSC.setText(mActivity.getResources().getString(R.string.speed_set, 1));
					SharePersistent.saveBrightData(mActivity, diyViewTag+"speed", diyViewTag+"speed", 1);

				} else {
					mActivity.setSpeed(progress);
					textViewSpeedSC.setText(mActivity.getResources().getString(R.string.speed_set, progress));
					SharePersistent.saveBrightData(mActivity, diyViewTag+"speed", diyViewTag+"speed", progress);
				}
			}
		});

		this.seekBarBrightNess2.setOnSeekBarChangeListener(new OnSeekBarChangeListenerAdapter() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (progress == 0) {
					seekBar.setProgress(1);
					mActivity.setSpeed(1);
					textViewBrightNess2.setText(mActivity.getResources().getString(R.string.speed_set, 1));

				} else {
					mActivity.setSpeed(progress);
					textViewBrightNess2.setText(mActivity.getResources().getString(R.string.speed_set, progress));

				}
			}
		});

		this.seekBarBrightness.setOnSeekBarChangeListener(new OnSeekBarChangeListenerAdapter() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (0 == progress) {
					seekBar.setProgress(1);
					textViewBrightness.setText(mActivity.getResources().getString(R.string.brightness_set, 1));
					mActivity.setBrightNess(1);

				} else {
					textViewBrightness.setText(mActivity.getResources().getString(R.string.brightness_set, progress));
					mActivity.setBrightNess(progress);

				}
			}
		});
		
		seekBarBrightBarSC.setOnSeekBarChangeListener(new OnSeekBarChangeListenerAdapter() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (0 == progress) {
					seekBar.setProgress(1);
					mActivity.setBrightNess(1);
					textViewBrightSC.setText(mActivity.getResources().getString(R.string.brightness_set, 1));
					SharePersistent.saveBrightData(mActivity, diyViewTag+"bright", diyViewTag+"bright", 1);
				} else {
					mActivity.setBrightNess(progress);
					textViewBrightSC.setText(mActivity.getResources().getString(R.string.brightness_set, progress));
					SharePersistent.saveBrightData(mActivity, diyViewTag+"bright", diyViewTag+"bright", progress);
				}
			}
		});

		this.blackWiteSelectView.setOnSelectColor(new OnSelectColor() {
			@Override
			public void onColorSelect(int color, int progress) {
				int p = progress;
				if (progress <= 0) {
					p = 1;
				} else if (progress >= 100) {
					p = 100;
				}
				mActivity.setBrightNess(p);
				tvBrightness.setText(mActivity.getResources().getString(R.string.brightness_set, p));

			}
		});
		this.imageViewPicker.setOnTouchPixListener(new MyColorPickerImageView4RGB.OnTouchPixListener() {
			@Override
			public void onColorSelect(int color, float angle) {
				int[] colors = Tool.getRGB(color);
				blackWiteSelectView.setStartColor(color);
				updateRgbText(colors);
			}
		});
		this.listViewModel.setAdapter(buildModel());
		this.listViewModel.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				maAdapter.setIndex(position);
				maAdapter.notifyDataSetChanged();
				AdapterBean abean = ((AdapterBean) maAdapter.getItem(position));
				String value = abean.getValue();
				textViewCurretModel
						.setText(mActivity.getResources().getString(R.string.current_mode_format, abean.getLabel()));
				textViewCurretModel.setTag(value);
				mActivity.setRegMode(Integer.parseInt(value));

			}
		});

		segmentedRadioGroup.check(R.id.rbRgbOne);
		segmentedRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (R.id.rbRgbOne == checkedId) {
					relativeTab1.setVisibility(View.VISIBLE);
					relativeTab2.setVisibility(View.GONE);
					relativeTab3.setVisibility(View.GONE);
				} else if ((R.id.rbRgbTwo == checkedId)) {
					relativeTab2.setVisibility(View.VISIBLE);
					relativeTab1.setVisibility(View.GONE);
					relativeTab3.setVisibility(View.GONE);
				} else if ((R.id.rbRgbThree == checkedId)) {
					relativeTab3.setVisibility(View.VISIBLE);
					relativeTab1.setVisibility(View.GONE);
					relativeTab2.setVisibility(View.GONE);
				}
			}
		});

		changeRadioGroup.check(R.id.changeButtonOne);
		changeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (R.id.changeButtonOne == checkedId) {
					style = 0;
				} else if ((R.id.changeButtonTwo == checkedId)) {
					style = 1;
				} else if ((R.id.changeButtonThree == checkedId)) {
					style = 2;
				} else if ((R.id.changeButtonFour == checkedId)) {
					style = 3;
				}
			}
		});

		tgbtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mActivity.isLightOpen = isChecked;
				if (isChecked) {
					mActivity.open();
				} else {
					mActivity.close();
				}

			}
		});

		buttonRunButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				putDataBack(getSelectColor());
			}
		});

		srgCover.check(R.id.rbRing);
		srgCover.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (R.id.rbRing == checkedId) {
					currentTab = 1;
					llRing.setVisibility(View.VISIBLE);
					llCover.setVisibility(View.GONE);
				} else if ((R.id.rbModle == checkedId)) {
					currentTab = 2;
					llCover.setVisibility(View.VISIBLE);
					llRing.setVisibility(View.GONE);
					llCover.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);  
					
				}
			}
		});

		lvCover.setAdapter(buildModel());
		lvCover.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				maAdapter.setIndex(position);
				maAdapter.notifyDataSetChanged();
				AdapterBean abean = ((AdapterBean) maAdapter.getItem(position));
				String value = abean.getValue();
				tvCoverModel.setText(mActivity.getResources().getString(R.string.current_mode_format, abean.getLabel()));
				tvCoverModel.setTag(value);
				currentSelecColorFromPicker = Integer.parseInt(abean.getValue());
			}
		});

		initSingColorView();
		initColorBlock();
		initColorSelecterView();
	}

	@Override
	public void initView() {
		tgbtn.setChecked(mActivity.isLightOpen);
		seekBarBrightness.setProgress(mActivity.brightness);// 设置亮度
		seekBarSpeedBar.setProgress(mActivity.speed);
	}

	@Override
	public void initEvent() {

	}

	// private void pauseMusicAndVolum() {
	// musicFragment.pauseMusic();
	// musicFragment.pauseVolum();
	// }

	private void putDataBack(ArrayList<MyColor> colors) {
		mActivity.setDiy(colors, style);

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
	 * 初始化单色View的点击事件
	 */
	private void initSingColorView() {
		final HashMap<Integer, Double> rmap = new HashMap<Integer, Double>();
		int[] colors = { 0xffff0000, 0xffffff00, 0xff00ff00, 0xff00ffff, 0xff0000ff, 0xffff00ff };
		rmap.put(colors[0], 0.0);
		rmap.put(colors[1], Math.PI / 3);
		rmap.put(colors[2], Math.PI * 2 / 3);
		rmap.put(colors[3], Math.PI);
		rmap.put(colors[4], Math.PI * 4 / 3);
		rmap.put(colors[5], Math.PI * 5 / 3);

		String tagStart = "viewColor";
		View.OnClickListener click = new OnClickListener() {
			@Override
			public void onClick(View v) {
				v.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.layout_scale));
				int color = (Integer) v.getTag();
				updateRgbText(Tool.getRGB(color));
				blackWiteSelectView.setStartColor(color);
				imageViewPicker.move2Ege(rmap.get(color));
			}
		};
		ArrayList<View> views = new ArrayList<View>();
		for (int i = 1; i <= 6; i++) {
			View view = mContentView.findViewWithTag(tagStart + i);
			view.setOnClickListener(click);
			view.setTag(colors[i - 1]);
			views.add(view);
		}

	}

	/**
	 * 初始化颜色选择block
	 */
	private void initColorBlock() {
		View blocks = mContentView.findViewById(R.id.linearLayoutViewBlocks);
		// 16个自定义view
		colorTextViews = new ArrayList<ColorTextView>();
		for (int i = 1; i <= 16; i++) {
			final ColorTextView tv = (ColorTextView) blocks.findViewWithTag((String.valueOf("labelColor") + i));
			String tag = (String) tv.getTag();
			int color = sp.getInt(tag, -1);
			if (color != -1) {
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
				public void onClick(View v) {// 点击弹出颜色选择框
					v.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.layout_scale));
					int color = tv.getColor();
					if (color == -1) {
						showColorCover((ColorTextView) v, false);
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
					cv.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.block_shap_color));
					cv.setText("+");
					return true;
				}
			});
			colorTextViews.add(tv);
		}

		View diy = mContentView.findViewById(R.id.linarLayoutColorCile);
		for (int i = 1; i <= 6; i++) {
			final ColorTextView tv = (ColorTextView) diy.findViewWithTag((String.valueOf("diyColor") + i));
			String tag = (String) tv.getTag();
			int color = sp.getInt(tag, -1);

			if (color != -1) {
				if (color < 128) {
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
				} else {
					Drawable image = getImage(color + "");
					tv.setBackgroundDrawable(image);
					tv.setColor(color);
				}
				tv.setText("");
			}

			tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {// 点击弹出颜色选择框
					v.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.layout_scale));
					int color = tv.getColor();
					
					diyViewTag = (String) tv.getTag();
					
					if (color != -1) {
						if (color < 128) {
							
							updateRgbText(Tool.getRGB(color));
//							int  bright = SharePersistent.getBrightData(mActivity, Constant.BRIGHT_TYPE, Constant.BRIGHT_KEY);
							int  bright = SharePersistent.getBrightData(mActivity, diyViewTag+"bright", diyViewTag+"bright");
							if (0 == bright) {
								mActivity.setBrightNess(100); //默认 100
							}else {
								mActivity.setBrightNess(bright);  //取出亮度，并发送
							}
							
						} else {
							mActivity.setRegMode(color);
							
							int  bright = SharePersistent.getBrightData(mActivity, diyViewTag+"bright", diyViewTag+"bright");
							int  speed = SharePersistent.getBrightData(mActivity, diyViewTag+"speed", diyViewTag+"speed");
							if (0 == bright) {
								mActivity.setBrightNess(100);
							}else {
								mActivity.setBrightNess(bright);
							}
							
							if (0 == speed) {
								mActivity.setSpeed(85);
							}else {
								mActivity.setSpeed(speed);
							}
						}
					} else {
						showColorCover((ColorTextView) v, true);
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
					cv.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.block_shap_color));
					cv.setText("+");
					return true;
				}
			});
		}
	}

	/**
	 * 弹出颜色编辑器
	 * 
	 * @param actionView
	 * @param hasModel
	 */
	public void showColorCover(ColorTextView actionView, final boolean hasBrightNess) {
		// 数据初始化
		this.actionView = actionView;
		currentSelecColorFromPicker = -1;
		srgCover.check(R.id.rbRing);
		tvCoverModel.setText(mActivity.getResources().getString(R.string.current_mode));
		maAdapter.setIndex(-1);
		maAdapter.notifyDataSetChanged();
		textRGB.setText(mActivity.getString(R.string.r_g_b, 0, 0, 0));

		if (hasBrightNess) {
			srgCover.setVisibility(View.VISIBLE);
			llRing.setVisibility(View.VISIBLE);
			llCover.setVisibility(View.GONE);
			blackWiteSelectView2.setVisibility(View.VISIBLE);
		} else {
			srgCover.setVisibility(View.INVISIBLE);
			llRing.setVisibility(View.VISIBLE);
			llCover.setVisibility(View.GONE);
			blackWiteSelectView2.setVisibility(View.GONE);
			textViewRingBrightSC.setVisibility(View.GONE);
		}


		mPopupWindow = new PopupWindow(menuView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true);
		mPopupWindow.showAtLocation(mContentView, Gravity.BOTTOM, 0, 0);
//		mPopupWindow.setAnimationStyle(R.style.animTranslate);

	}

	/**
	 * 隐藏
	 */
	private void hideColorCover() {
		mPopupWindow.dismiss(); // 隐藏
	}

	/**
	 * 初始化颜色编辑器
	 */
	private void initColorSelecterView() {

		imageViewPicker2.setOnTouchPixListener(new MyColorPickerImageView4RGB.OnTouchPixListener() {
			@Override
			public void onColorSelect(int color, float angle) {
				blackWiteSelectView2.setStartColor(color);
				currentSelecColorFromPicker = color;

				int[] colors = Tool.getRGB(color);
				updateRgbText(colors);
				textRGB.setText(mActivity.getString(R.string.r_g_b, colors[0], colors[1], colors[2]));
				SharePersistent.saveBrightData(mActivity, diyViewTag+"bright", diyViewTag+"bright", 100);
			}
		});

		blackWiteSelectView2.setOnSelectColor(new OnSelectColor() {
			@Override
			public void onColorSelect(int color, int progress) {
				currentSelecColorFromPicker = color;

				int p = progress;
				if (progress <= 0) {
					p = 1;
				}
				if (progress >= 100) {
					p = 100;
				}
				textViewRingBrightSC.setText(mActivity.getResources().getString(R.string.brightness_set, p));
				SharePersistent.saveBrightData(mActivity, diyViewTag+"bright", diyViewTag+"bright", p);
//				SharePersistent.saveBrightData(mActivity, Constant.BRIGHT_TYPE, Constant.BRIGHT_KEY, p); 
				mActivity.setBrightNess(p);
			}
		});

		View viewColors = menuView.findViewById(R.id.viewColors);
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
					blackWiteSelectView2.setStartColor(selectColor);
					imageViewPicker2.move2Ege(rmap.get(selectColor));
					updateRgbText(Tool.getRGB(selectColor));
					
					int[] colors = Tool.getRGB(selectColor);
					textRGB.setText(mActivity.getString(R.string.r_g_b, colors[0], colors[1], colors[2]));
				}
			});
			viewCsArrayLis.add(vc);
		}

		buttonSelectColorConfirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (currentSelecColorFromPicker != -1) {
					if (currentTab == 1) {
						int radius = 10;
						float[] outerR = new float[] { radius, radius, radius, radius, radius, radius, radius, radius };
						// 构造一个圆角矩形,可以使用其他形状，这样ShapeDrawable
						// 就会根据形状来绘制。
						RoundRectShape roundRectShape = new RoundRectShape(outerR, null, null);
						// 组合圆角矩形和ShapeDrawable
						ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape);
						// 设置形状的颜色
						shapeDrawable.getPaint().setColor(currentSelecColorFromPicker);
						// 设置绘制方式为填充
						shapeDrawable.getPaint().setStyle(Paint.Style.FILL);
						actionView.setColor(currentSelecColorFromPicker);
						// 保存颜色值
						String tag = (String) actionView.getTag();
						sp.edit().putInt(tag, currentSelecColorFromPicker).commit();
						// 将当前选择得颜色设置到触发颜色编辑器得View上
						actionView.setBackgroundDrawable(shapeDrawable);
					} else {
						actionView.setColor(currentSelecColorFromPicker);
						// 保存颜色值
						String tag = (String) actionView.getTag();
						if (null != tvCoverModel) {
							if (null != tvCoverModel.getTag().toString()) {
								String value = tvCoverModel.getTag().toString();
								sp.edit().putInt(tag, Integer.parseInt(value)).commit();
								Drawable image = getImage(value);
								actionView.setBackgroundDrawable(image);
							}
						}
					}
					actionView.setText("");
				}
				
				hideColorCover();
			}
		});
	}

	public Drawable getImage(String value) {
		int resID = mActivity.getResources().getIdentifier("img_" + value, "drawable", "com.ledble");
		return mActivity.getResources().getDrawable(resID);
	}

	public void updateRgbText(int rgb[]) {
		((TextView) mContentView.findViewById(R.id.textViewR)).setText("R:" + (rgb[0]));
		((TextView) mContentView.findViewById(R.id.textViewG)).setText("G:" + (rgb[1]));
		((TextView) mContentView.findViewById(R.id.textViewB)).setText("B:" + (rgb[2]));
		try {
			mActivity.setRgb(rgb[0], rgb[1], rgb[2]);
		} catch (Exception e) {
			e.printStackTrace();
			Tool.ToastShow(mActivity, "错误。。。");
		}
	}

	private ModelAdapter buildModel() {
		String[] ary = mActivity.getResources().getStringArray(R.array.rgb_mode);
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
