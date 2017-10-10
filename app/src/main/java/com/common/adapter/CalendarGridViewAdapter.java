package com.common.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ledble.R;

/**
 * 日历adapter
 * @author:wangzhengyun
 * 2012-8-31
 */
public class CalendarGridViewAdapter extends BaseAdapter {

	private Calendar calStartDate = Calendar.getInstance();// 当前显示的日历
	private Calendar calSelected = Calendar.getInstance(); // 选择的日历

	public void setSelectedDate(Calendar cal) {
		calSelected = cal;
	}

	private Calendar calToday = Calendar.getInstance(); // 今日
	private int iMonthViewCurrentMonth = 0; // 当前视图月

	// 根据改变的日期更新日历
	// 填充日历控件用
	private void UpdateStartDateForMonth() {
		calStartDate.set(Calendar.DATE, 1); // 设置成当月第一天
		iMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);// 得到当前日历显示的月

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

		calStartDate.add(Calendar.DAY_OF_MONTH, -1);// 周日第一位

	}

	ArrayList<java.util.Date> titles;

	private ArrayList<java.util.Date> getDates() {
		UpdateStartDateForMonth();
		ArrayList<java.util.Date> alArrayList = new ArrayList<java.util.Date>();
		for (int i = 1; i <= 42; i++) {
			alArrayList.add(calStartDate.getTime());
			calStartDate.add(Calendar.DAY_OF_MONTH, 1);
		}
		return alArrayList;
	}

	private Activity context;
	Resources resources;

	// construct
	public CalendarGridViewAdapter(Activity a, Calendar cal) {
		calStartDate = cal;
		context = a;
		resources = context.getResources();
		titles = getDates();
	}

	public CalendarGridViewAdapter(Activity a) {
		context = a;
		resources = context.getResources();
	}

	@Override
	public int getCount() {
		return titles.size();
	}

	@Override
	public Object getItem(int position) {
		return titles.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder vl=null;
		vl=new ViewHolder();
		convertView=View.inflate(context, R.layout.item_calendar_grid, null);
		vl.container=(LinearLayout)convertView.findViewById(R.id.linearLayoutDateContainer);
		vl.txtViewDate=(TextView)convertView.findViewById(R.id.textViewDateDay);
		vl.txtViewDayTag=(TextView)convertView.findViewById(R.id.textViewDayTag);
		
//		convertView.setTag(vl);
//		if(null==convertView){
//			vl=new ViewHolder();
//			convertView=View.inflate(context, R.layout.item_calendar_grid, null);
//			vl.container=(LinearLayout)convertView.findViewById(R.id.linearLayoutDateContainer);
//			vl.txtViewDate=(TextView)convertView.findViewById(R.id.textViewDateDay);
//			vl.txtViewDayTag=(TextView)convertView.findViewById(R.id.textViewDayTag);
//			convertView.setTag(vl);
//		}else{
//			vl=(ViewHolder)convertView.getTag();
//		}
		
		LinearLayout dateContainer = vl.container;
		dateContainer.setId(position + 5000);
		dateContainer.setOrientation(1);
		dateContainer.setBackgroundColor(resources.getColor(R.color.white));

		Date myDate = (Date) getItem(position);
		Calendar calCalendar = Calendar.getInstance();
		calCalendar.setTime(myDate);

		final int iMonth = calCalendar.get(Calendar.MONTH);
		final int iDay = calCalendar.get(Calendar.DAY_OF_WEEK);
		// 判断周六周日
		dateContainer.setBackgroundColor(resources.getColor(R.color.white));
		int color_vacation = context.getResources().getColor(R.color.text_color_date_vation);
		if (iDay == 7) {
			// 周六
			dateContainer.setBackgroundColor(color_vacation);
		} else if (iDay == 1) {
			// 周日
			dateContainer.setBackgroundColor(color_vacation);
		} else {

		}
		// 判断周六周日结束

		TextView txtToDay = vl.txtViewDayTag;
		txtToDay.setGravity(Gravity.CENTER_HORIZONTAL);
		txtToDay.setTextSize(9);
		if (equalsDate(calToday.getTime(), myDate)) {
			    dateContainer.setBackgroundColor(resources.getColor(R.color.event_center));
			    txtToDay.setText("今天!");
		}else{
			txtToDay.setText("");
		}

		// 设置背景颜色
		if (equalsDate(calSelected.getTime(), myDate)) {
			// 选择的
			    dateContainer.setBackgroundColor(resources.getColor(R.color.selection));
		} else {
			if (equalsDate(calToday.getTime(), myDate)) {
				// 当前日期
				dateContainer.setBackgroundColor(resources.getColor(R.color.calendar_zhe_day));
			}
		}
		// 设置背景颜色结束

		// 日期开始
		TextView txtDay =vl.txtViewDate;
		txtDay.setGravity(Gravity.CENTER_HORIZONTAL);

		// 判断是否是当前月
		if (iMonth == iMonthViewCurrentMonth) {
			    txtToDay.setTextColor(resources.getColor(R.color.ToDayText));
			    txtDay.setTextColor(resources.getColor(R.color.Text));
		} else {
			    txtDay.setTextColor(resources.getColor(R.color.noMonth));
			    txtToDay.setTextColor(resources.getColor(R.color.noMonth));
		}
		
		// judge before todat
//		if (smallDate(myDate, calToday.getTime())) {
//			 txtDay.setTextColor(resources.getColor(R.color.noMonth));
//			 txtToDay.setTextColor(resources.getColor(R.color.noMonth));
//			 convertView.setEnabled(false);
//			 convertView.setClickable(false);
//			 convertView.setOnClickListener(null);
//		}

		int day = myDate.getDate(); // 日期
		txtDay.setText(String.valueOf(day));
		txtDay.setId(position + 500);
		dateContainer.setTag(myDate);

		return convertView;
	}

	class ViewHolder{
		 LinearLayout container;
		 TextView txtViewDate;//日期
		 TextView txtViewDayTag;//是否是今天标记
	}
	
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	private Boolean equalsDate(Date date1, Date date2) {

		if (date1.getYear() == date2.getYear()
				&& date1.getMonth() == date2.getMonth()
				&& date1.getDate() == date2.getDate()) {
			return true;
		} else {
			return false;
		}

	}
	
	private Boolean smallDate(Date date1, Date date2) {
		
		if (date1.getYear() < date2.getYear()) {
			return true;
		} else if (date1.getYear() > date2.getYear()) {
			return false;
		} else {
			if (date1.getMonth() < date2.getMonth()) {
				return true;
			} else if (date1.getMonth() > date2.getMonth()) {
				return false;
			} else {
				if (date1.getDate() < date2.getDate()) {
					return true;
				} else {
					return false;
				}
			}
		}

	}

}
