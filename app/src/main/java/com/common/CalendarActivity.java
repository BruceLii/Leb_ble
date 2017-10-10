package com.common;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.common.adapter.CalendarGridViewAdapter;
import com.common.uitl.NumberHelper;
import com.common.view.CalendarGridView;
import com.ledble.R;

/**
 * 日历选择
 * 
 * @author:wangzhengyun 2012-8-31
 */
public class CalendarActivity extends BaseActivity implements OnTouchListener {

	// 判断手势用
	private static final int SWIPE_MIN_DISTANCE = 100;//判断滑动距离阀值，如果超过这个数字，就执行范爷
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	// 动画
	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
	private Animation slideRightOut;
	private ViewFlipper viewFlipper;
	private GestureDetector mGesture = null;
	private LinearLayout mLinearLayoutContainer;
	private Button buttonPreMon;// 上一月
	private Button buttonNextMon;// 下一月
	private TextView textViewMon;// 当前选中的月份

	private ImageButton mImageButtonBack;
	public  static String CALENDAR_DATA_KEY="date";//入住日期
	
	private int INT_DAY4BOOK=30;//最多可预订的参数
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();

	}

	@Override
	public void initView() {
		setContentView(R.layout.layout_calendar);
		mLinearLayoutContainer = (LinearLayout) findViewById(R.id.linearLayoutCalendarContainer);
		textViewMon = (TextView) findViewById(R.id.textViewMonth);
		buttonPreMon = (Button) findViewById(R.id.buttonPreMon);
		buttonNextMon = (Button) findViewById(R.id.buttonNextMon);

		slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
		slideLeftOut = AnimationUtils
				.loadAnimation(this, R.anim.slide_left_out);
		slideRightIn = AnimationUtils
				.loadAnimation(this, R.anim.slide_right_in);
		slideRightOut = AnimationUtils.loadAnimation(this,
				R.anim.slide_right_out);

		slideLeftIn.setAnimationListener(animationListener);
		slideLeftOut.setAnimationListener(animationListener);
		slideRightIn.setAnimationListener(animationListener);
		slideRightOut.setAnimationListener(animationListener);

		mGesture = new GestureDetector(this, new GestureListener());

		View gcontentView = generateContentView();// 生成日历画面
		mLinearLayoutContainer.addView(gcontentView);

		buttonPreMon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 上一个月
				viewFlipper.setInAnimation(slideRightIn);
				viewFlipper.setOutAnimation(slideRightOut);
				viewFlipper.showPrevious();
				setPrevViewItem();
			}
		});

		buttonNextMon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 下一个月
				viewFlipper.setInAnimation(slideLeftIn);
				viewFlipper.setOutAnimation(slideLeftOut);
				viewFlipper.showNext();
				setNextViewItem();
			}
		});

		mImageButtonBack = (ImageButton) findViewById(R.id.imageButtonBack);
		mImageButtonBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		updateStartDateForMonth();
	}

	public void setHeaderView(View v){
	 RelativeLayout rlayout = ((RelativeLayout)findViewById(R.id.realativeLayoutHeader));
	 rlayout.removeAllViews();
	 rlayout.addView(v);
	}
	/**
	 * 回传数据
	 */
	public void putDataBack(Date d) {
		Intent i=new Intent();
		i.putExtra(CALENDAR_DATA_KEY, d);
		setResult(RESULT_OK,i);
		finish();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return mGesture.onTouchEvent(event);
	}

	AnimationListener animationListener = new AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			// 当动画完成后调用
			CreateGirdView();
		}
	};

	class GestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					viewFlipper.setInAnimation(slideLeftIn);
					viewFlipper.setOutAnimation(slideLeftOut);
					viewFlipper.showNext();
					setNextViewItem();
					// CreateGirdView();
					return true;

				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					viewFlipper.setInAnimation(slideRightIn);
					viewFlipper.setOutAnimation(slideRightOut);
					viewFlipper.showPrevious();
					setPrevViewItem();
					// CreateGirdView();
					return true;

				}
			} catch (Exception e) {
				// nothing
			}
			return false;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// ListView lv = getListView();
			// 得到当前选中的是第几个单元格
			int pos = gView2.pointToPosition((int) e.getX(), (int) e.getY());
			LinearLayout txtDay = (LinearLayout) gView2.findViewById(pos + 5000);
			if (txtDay != null) {
				if (txtDay.getTag() != null) {

					Date date = (Date) txtDay.getTag();
//					Date now = new Date();

//					int absdistance = Math.abs((int) ((now.getTime() - date
//							.getTime()) / 1000 / 60 / 60 / 24));

//					if (absdistance < INT_DAY4BOOK-1) {
						putDataBack(date);
						calSelected.setTime(date);
						gAdapter.setSelectedDate(calSelected);

						gAdapter.notifyDataSetChanged();

						gAdapter1.setSelectedDate(calSelected);

						gAdapter1.notifyDataSetChanged();

						gAdapter3.setSelectedDate(calSelected);
						gAdapter3.notifyDataSetChanged();
//					} else {
//						Tool.ToastShow(CalendarActivity.this, "只能预订"
//								+ INT_DAY4BOOK + "天以内!");
//					}

				}
			}

			return false;
		}
	}

	// 基本变量
	private Context mContext = CalendarActivity.this;
//	private GridView title_gView;
	private GridView gView1;// 上一个月
	private GridView gView2;// 当前月
	private GridView gView3;// 下一个月
	// private GridView gView1;
	boolean bIsSelection = false;// 是否是选择事件发生
	private Calendar calStartDate = Calendar.getInstance();// 当前显示的日历
	private Calendar calSelected = Calendar.getInstance(); // 选择的日历
	private Calendar calToday = Calendar.getInstance(); // 今日
	private CalendarGridViewAdapter gAdapter;
	private CalendarGridViewAdapter gAdapter1;
	private CalendarGridViewAdapter gAdapter3;

	private RelativeLayout mainLayout;

	private int iMonthViewCurrentMonth = 0; // 当前视图月
	private int iMonthViewCurrentYear = 0; // 当前视图年
	private int iFirstDayOfWeek = Calendar.MONDAY;

	private static final int mainLayoutID = 88; // 设置主布局ID
	private static final int titleLayoutID = 77; // title布局ID
	private static final int caltitleLayoutID = 66; // title布局ID
	private static final int calLayoutID = 55; // 日历布局ID

	/** 底部菜单文字 **/
	String[] menu_toolbar_name_array;

	AlertDialog.OnKeyListener onKeyListener = new AlertDialog.OnKeyListener() {

		@Override
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				CalendarActivity.this.finish();
			}
			return false;

		}

	};

	// 生成内容视图
	private View generateContentView() {
		// 创建一个垂直的线性布局（整体内容）
		viewFlipper = new ViewFlipper(this);
		viewFlipper.setId(calLayoutID);

		mainLayout = new RelativeLayout(this); // 创建一个垂直的线性布局（整体内容）
		RelativeLayout.LayoutParams params_main = new RelativeLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.FILL_PARENT);
		mainLayout.setLayoutParams(params_main);
		mainLayout.setId(mainLayoutID);
		mainLayout.setGravity(Gravity.CENTER_HORIZONTAL);

		LinearLayout layTopControls = createLayout(LinearLayout.HORIZONTAL); // 生成顶部按钮布局

		RelativeLayout.LayoutParams params_title = new RelativeLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		params_title.topMargin = 5;
		// params_title.addRule(RelativeLayout.ALIGN_PARENT_TOP, 20);
		layTopControls.setId(titleLayoutID);
		mainLayout.addView(layTopControls, params_title);

		calStartDate = getCalendarStartDate();

//		setTitleGirdView();
		RelativeLayout.LayoutParams params_cal_title = new RelativeLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		params_cal_title.addRule(RelativeLayout.BELOW, titleLayoutID);
		// params_cal_title.topMargin = 5;
//		mainLayout.addView(title_gView, params_cal_title);

		CreateGirdView();

		RelativeLayout.LayoutParams params_cal = new RelativeLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		params_cal.addRule(RelativeLayout.BELOW, caltitleLayoutID);

		mainLayout.addView(viewFlipper, params_cal);

		LinearLayout br = new LinearLayout(this);
		RelativeLayout.LayoutParams params_br = new RelativeLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT, 1);
		params_br.addRule(RelativeLayout.BELOW, calLayoutID);
		
		br.setBackgroundColor(getResources().getColor(R.color.calendar_background));
		
		mainLayout.addView(br, params_br);

		return mainLayout;

	}

	// 创建一个线性布局
	// 参数：方向
	private LinearLayout createLayout(int iOrientation) {
		LinearLayout lay = new LinearLayout(this);
		LayoutParams params = new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,// *fill_parent，填满父控件的空白
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		params.topMargin = 10;
		// 设置布局参数
		lay.setLayoutParams(params);// *wrap_content，表示大小刚好足够显示当前控件里的内容
		lay.setOrientation(iOrientation);// 设置方向
		lay.setGravity(Gravity.CENTER);
		return lay;
	}

	/**
	 * 生成星期表头
	 * @return
	 */
	private View createTitleGridView() {

		GridView title_gView = setGirdView();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		
		title_gView.setLayoutParams(params);
		title_gView.setVerticalSpacing(1);// 垂直间隔
		title_gView.setHorizontalSpacing(1);// 水平间隔
		title_gView.setBackgroundColor(0xffe1e4e7);
		TitleGridAdapter titleAdapter = new TitleGridAdapter(this);
		title_gView.setAdapter(titleAdapter);// 设置菜单Adapter
		title_gView.setId(caltitleLayoutID);
		
		LinearLayout ly=new LinearLayout(this);
		ly.setOrientation(LinearLayout.VERTICAL);
		
		View cv=new View(this);
		cv.setBackgroundColor(0xffe1e4e7);
		ly.addView(cv, android.view.ViewGroup.LayoutParams.FILL_PARENT, 1);
		
		ly.addView(title_gView);
		View cvc=new View(this);
		cvc.setBackgroundColor(0xffe1e4e7);
		ly.addView(cvc,android.view.ViewGroup.LayoutParams.FILL_PARENT, 1);
		
		return ly;
		
//		ImageView imv=new ImageView(this);
//		imv.setBackgroundResource(R.drawable.bg_calendar_title);
//		return imv;
	}

	/**
	 * 生成灰色分割线
	 * @return
	 */
	private View createDarkSplit() {
		View spv=new View(this);
		return spv;
	}
	
	private void CreateGirdView() {

		Calendar tempSelected1 = Calendar.getInstance(); // 临时
		Calendar tempSelected2 = Calendar.getInstance(); // 临时
		Calendar tempSelected3 = Calendar.getInstance(); // 临时
		tempSelected1.setTime(calStartDate.getTime());
		tempSelected2.setTime(calStartDate.getTime());
		tempSelected3.setTime(calStartDate.getTime());

		gView1 = new CalendarGridView(mContext);
		tempSelected1.add(Calendar.MONTH, -1);
		gAdapter1 = new CalendarGridViewAdapter(this, tempSelected1);
		gView1.setAdapter(gAdapter1);// 设置菜单Adapter
		gView1.setId(calLayoutID);

		gView2 = new CalendarGridView(mContext);
		gAdapter = new CalendarGridViewAdapter(this, tempSelected2);
		gView2.setAdapter(gAdapter);// 设置菜单Adapter
		gView2.setId(calLayoutID);
		gView2.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

			}
		});

		gView3 = new CalendarGridView(mContext);
		tempSelected3.add(Calendar.MONTH, 1);
		gAdapter3 = new CalendarGridViewAdapter(this, tempSelected3);
		gView3.setAdapter(gAdapter3);// 设置菜单Adapter
		gView3.setId(calLayoutID);

		gView2.setOnTouchListener(this);
		gView1.setOnTouchListener(this);
		gView3.setOnTouchListener(this);

		if (viewFlipper.getChildCount() != 0) {
			viewFlipper.removeAllViews();
		}

		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		
		LinearLayout l2=new LinearLayout(this);
		l2.setOrientation(LinearLayout.VERTICAL);
		l2.addView(createTitleGridView(),params);
		l2.addView(gView2);
		viewFlipper.addView(l2);
		
		LinearLayout l3=new LinearLayout(this);
		l3.setOrientation(LinearLayout.VERTICAL);
		l3.addView(createTitleGridView(),params);
		l3.addView(gView3);
		viewFlipper.addView(l3);
		
		LinearLayout l1=new LinearLayout(this);
		l1.setOrientation(LinearLayout.VERTICAL);
		l1.addView(createTitleGridView(),params);
		l1.addView(gView1);
		viewFlipper.addView(l1);
		String s = calStartDate.get(Calendar.YEAR)
				+ "年"
				+ NumberHelper.LeftPad_Tow_Zero(calStartDate
						.get(Calendar.MONTH) + 1) + "月";

		textViewMon.setText(s);
	}

	private GridView setGirdView() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		GridView gridView = new GridView(this);
		gridView.setLayoutParams(params);
		gridView.setNumColumns(7);// 设置每行列数
		gridView.setGravity(Gravity.CENTER_VERTICAL);// 位置居中
		gridView.setVerticalSpacing(1);// 垂直间隔
		gridView.setHorizontalSpacing(1);// 水平间隔
		gridView.setBackgroundColor(0xffe1e4e7);

		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int i = display.getWidth() / 7;
		int j = display.getWidth() - (i * 7);
		int x = j / 2;
		gridView.setPadding(x, 0, 0, 0);// 居中

		return gridView;
	}

	// 上一个月
	private void setPrevViewItem() {
		iMonthViewCurrentMonth--;// 当前选择月--
		// 如果当前月为负数的话显示上一年
		if (iMonthViewCurrentMonth == -1) {
			iMonthViewCurrentMonth = 11;
			iMonthViewCurrentYear--;
		}
		calStartDate.set(Calendar.DAY_OF_MONTH, 1); // 设置日为当月1日
		calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth); // 设置月
		calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear); // 设置年

	}

	// 下一个月
	private void setNextViewItem() {
		iMonthViewCurrentMonth++;
		if (iMonthViewCurrentMonth == 12) {
			iMonthViewCurrentMonth = 0;
			iMonthViewCurrentYear++;
		}
		calStartDate.set(Calendar.DAY_OF_MONTH, 1);
		calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
		calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);

	}

	/**
	 * 设置当前时间，改变日历
	 */
	private void updateStartDateForMonth() {
		calStartDate.set(Calendar.DATE, 1); // 设置成当月第一天
		iMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);// 得到当前日历显示的月
		iMonthViewCurrentYear = calStartDate.get(Calendar.YEAR);// 得到当前日历显示的年

		String s = calStartDate.get(Calendar.YEAR)
				+ "年"
				+ NumberHelper.LeftPad_Tow_Zero(calStartDate
						.get(Calendar.MONTH) + 1) + "月";

		textViewMon.setText(s);

		// 星期一是2 星期天是1 填充剩余天数
		int iDay = 0;
		int iFirstDayOfWeek = Calendar.MONDAY;
		int iStartDay = iFirstDayOfWeek;
		if (iStartDay == Calendar.MONDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
			if (iDay < 0)
				iDay = 6;
		}
		if (iStartDay == Calendar.SUNDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
			if (iDay < 0)
				iDay = 6;
		}
		calStartDate.add(Calendar.DAY_OF_WEEK, -iDay);

	}

	private Calendar getCalendarStartDate() {
		calToday.setTimeInMillis(System.currentTimeMillis());
		calToday.setFirstDayOfWeek(iFirstDayOfWeek);

		if (calSelected.getTimeInMillis() == 0) {
			calStartDate.setTimeInMillis(System.currentTimeMillis());
			calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
		} else {
			calStartDate.setTimeInMillis(calSelected.getTimeInMillis());
			calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
		}

		return calStartDate;
	}

	/**
	 * 星期排列表头排列
	 * 
	 * @author:wangzhengyun 2012-8-31
	 */
	public class TitleGridAdapter extends BaseAdapter {

		int[] titles = new int[] { R.string.Sun, R.string.Mon, R.string.Tue,
				R.string.Wed, R.string.Thu, R.string.Fri, R.string.Sat };

		private Activity activity;

		// construct
		public TitleGridAdapter(Activity a) {
			activity = a;
		}

		@Override
		public int getCount() {
			return titles.length;
		}

		@Override
		public Object getItem(int position) {
			return titles[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LinearLayout iv = new LinearLayout(activity);
			TextView txtDay = new TextView(activity);
			
			txtDay.setFocusable(false);
			txtDay.setBackgroundColor(Color.TRANSPARENT);
			iv.setOrientation(1);
			
			txtDay.setGravity(Gravity.CENTER);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.FILL_PARENT);
			
			int i = (Integer) getItem(position);
			txtDay.setTextColor(R.color.text_color_date_title);
			txtDay.setTextSize(18);
			
			
			int color_vacation = activity.getResources().getColor(R.color.text_color_date_vation);
			int color_bg=activity.getResources().getColor(R.color.bg_color_title_day);
			if (i == R.string.Sat) {
				// 周六
				iv.setBackgroundColor(color_vacation);
			} else if (i == R.string.Sun) {
				// 周日
				iv.setBackgroundColor(color_vacation);
			} else {
//				iv.setBackgroundColor(color_bg);
				iv.setBackgroundColor(Color.WHITE);
			}
			txtDay.setText((Integer) getItem(position));
			iv.addView(txtDay, lp);
			return iv;
		}
	}

}
	