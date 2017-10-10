package com.ledble.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ledble.R;
import com.ledble.bean.AdapterBean;

public class ModelAdapter extends BaseAdapter {

	private ArrayList<AdapterBean> list;
	private Context context;
	private int index = -1;

	public ModelAdapter(Context context, ArrayList<AdapterBean> list) {
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return Integer.parseInt(list.get(position).getValue().trim());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (null == convertView) {
			vh = new ViewHolder();
			convertView = View.inflate(context, R.layout.item_model, null);
			vh.textView = (TextView) convertView.findViewById(R.id.textViewLabelId);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		vh.textView.setText(list.get(position).getLabel());
		
		if (index == position) {
			vh.textView.setTextColor(context.getResources().getColor(R.color.blue_text_color));
		} else {
			vh.textView.setTextColor(context.getResources().getColor(android.R.color.white));
		}
		return convertView;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	class ViewHolder {
		TextView textView;
	}
}
