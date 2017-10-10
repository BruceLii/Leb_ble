package com.ledble.adapter;

import java.util.ArrayList;
import java.util.HashSet;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.ledble.R;
import com.ledble.bean.Mp3;

public class Mp3Adapter extends BaseAdapter {

	private ArrayList<Mp3> list;
	private HashSet<Mp3> selectSet;
	private Context context;
	private int index = -1;
	private OnSelectListener onSelectListener;

	public Mp3Adapter(Context context, ArrayList<Mp3> list) {
		this.list = list;
		this.context = context;
		this.selectSet = new HashSet<Mp3>();
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
		return list.get(position).getId();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (null == convertView) {
			vh = new ViewHolder();
			convertView = View.inflate(context, R.layout.item_mp3, null);
			vh.textViewTitle = (TextView) convertView.findViewById(R.id.textViewTitle);
			vh.textViewAblum = (TextView) convertView.findViewById(R.id.textViewAblum);
			vh.textViewSinger = (TextView) convertView.findViewById(R.id.textViewSinger);
			vh.checkBox = (CheckBox) convertView.findViewById(R.id.checkBoxSelect);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		vh = (ViewHolder) convertView.getTag();
		final Mp3 mp3 = list.get(position);
		vh.textViewTitle.setText(mp3.getTitle());
		vh.textViewAblum.setText(mp3.getAlbum());
		if ("<unknown>".equalsIgnoreCase(mp3.getArtist())) {
			vh.textViewSinger.setText(context.getResources().getString(R.string.un_known_artist));
		} else {
			vh.textViewSinger.setText(mp3.getArtist());
		}

		vh.checkBox.setOnCheckedChangeListener(null);
		if (selectSet.contains(mp3)) {
			vh.checkBox.setChecked(true);
		} else {
			vh.checkBox.setChecked(false);
		}

		vh.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (null != onSelectListener) {
					onSelectListener.onSelect(position, mp3, selectSet, isChecked, Mp3Adapter.this);
				}
			}
		});
		return convertView;
	}

	public void selectAll() {
		selectSet.addAll(list);
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public HashSet<Mp3> getSelectSet() {
		return selectSet;
	}

	public void setSelectSet(HashSet<Mp3> selectSet) {
		this.selectSet = selectSet;
	}

	public OnSelectListener getOnSelectListener() {
		return onSelectListener;
	}

	public void setOnSelectListener(OnSelectListener onSelectListener) {
		this.onSelectListener = onSelectListener;
	}

	class ViewHolder {
		TextView textViewTitle;
		TextView textViewAblum;
		TextView textViewSinger;
		CheckBox checkBox;
	}

	public static interface OnSelectListener {
		public void onSelect(int index, Mp3 mp3, HashSet<Mp3> mp3s, boolean isCheck, BaseAdapter adapter);
	}
}
