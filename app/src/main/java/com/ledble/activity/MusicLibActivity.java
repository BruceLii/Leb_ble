package com.ledble.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.common.BaseProgressActivity;
import com.common.net.NetResult;
import com.common.task.BaseTask;
import com.common.task.NetCallBack;
import com.common.uitl.ListUtiles;
import com.common.uitl.Tool;
import com.ledble.R;
import com.ledble.adapter.Mp3Adapter;
import com.ledble.adapter.Mp3Adapter.OnSelectListener;
import com.ledble.base.LedBleApplication;
import com.ledble.bean.Mp3;

public class MusicLibActivity extends BaseProgressActivity {

	private ListView listViewMuiscs;
	private BaseTask baseTask;
	private static ArrayList<Mp3> mp3s = new ArrayList<Mp3>();
	private Mp3Adapter mp3Adapter;
	private Button buttonAdd;
	private Button buttonAddAll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		initView();
	}

	@Override
	public void initView() {
		setContentView(R.layout.activity_music_lib);
		listViewMuiscs = (ListView) findViewById(R.id.listViewMuiscsList);
		if (ListUtiles.isEmpty(mp3s)) {
			scanMp3();
		} else {
			buildmp3Adapter(mp3s, true);
		}
		buttonAdd = findButtonById(R.id.buttonConfirm);
		buttonAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null != mp3Adapter && !mp3Adapter.getSelectSet().isEmpty()) {
					goBackWithData(mp3Adapter);
				} else {
					Tool.ToastShow(MusicLibActivity.this, R.string.select_play_list);
				}
			}
		});
		buttonAddAll = findButtonById(R.id.buttonAddAll);
		buttonAddAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null != mp3Adapter) {
					mp3Adapter.selectAll();
					goBackWithData(mp3Adapter);
				}
			}
		});

	}
	
	private void goBackWithData(Mp3Adapter mp3Adapter){
		ArrayList<Mp3> mprMp3s = new ArrayList<Mp3>(mp3Adapter.getSelectSet());
		LedBleApplication.getApp().setMp3s(mprMp3s);
		putDataBack(null);
	}
	
	private Mp3Adapter buildmp3Adapter(ArrayList<Mp3> mp3list, boolean isFromInit) {
		if (!ListUtiles.isEmpty(mp3list)) {
			if (!isFromInit) {
				mp3s.clear();
				mp3s.addAll(mp3list);
			}
			mp3Adapter = new Mp3Adapter(this, mp3s);
			if (!ListUtiles.isEmpty(LedBleApplication.getApp().getMp3s())) {
				HashSet<Mp3> set = new HashSet<Mp3>(LedBleApplication.getApp().getMp3s());
				mp3Adapter.setSelectSet(set);
			}
			mp3Adapter.setOnSelectListener(new OnSelectListener() {
				@Override
				public void onSelect(int index, Mp3 mp3, HashSet<Mp3> mp3s, boolean isCheck, BaseAdapter adapter) {
					if (mp3s.contains(mp3)) {
						mp3s.remove(mp3);
					} else {
						mp3s.add(mp3);
					}
					adapter.notifyDataSetChanged();
				}
			});
			listViewMuiscs.setAdapter(mp3Adapter);
			listViewMuiscs.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					try {
						Mp3 mp3 = (Mp3) parent.getAdapter().getItem(position);
						putDataBack(mp3);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			});
			return mp3Adapter;
		}
		return null;
	}

	private void putDataBack(Mp3 mp3) {
		Intent intent = new Intent();
		intent.putExtra("mp3", mp3);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	private void scanMp3() {
		if (null != baseTask && !baseTask.cancel(true)) {
			baseTask.cancel(true);
		}
		baseTask = new BaseTask(new NetCallBack() {

			@Override
			public void onPreCall() {
				showProgressDialogWithTask(baseTask);
			}

			@Override
			public void onFinish(NetResult result) {
				hideProgressDialogWithTask();
				if (null != result) {
					ArrayList<Mp3> mp3list = (ArrayList<Mp3>) result.getTag();
					buildmp3Adapter(mp3list, false);
				} else {
					
				}
			}

			@Override
			public NetResult onDoInBack(HashMap<String, String> paramMap) {
				NetResult netResult = null;
				try {
					ArrayList<Mp3> list = getMp3Files();
					netResult = new NetResult();
					netResult.setTag(list);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return netResult;
			}
		});
		baseTask.execute(new HashMap<String, String>());
	}

	public ArrayList<Mp3> getMp3Files() {
		// 生成动态数组，并且转载数据
		ArrayList<Mp3> mylist = new ArrayList<Mp3>();
		// 查询媒体数据库
		Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		// 遍历媒体数据库
		int i = 0;
		while (cursor.moveToNext()) {
			Mp3 mp3 = new Mp3();
			// 歌曲编号
			int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
			mp3.setId(id);
			// 歌曲标题
			String tilte = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
			mp3.setTitle(tilte);
			// 歌曲的专辑名：MediaStore.Audio.Media.ALBUM
			String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
			mp3.setAlbum(album);
			// 歌曲的歌手名： MediaStore.Audio.Media.ARTIST
			String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
			mp3.setArtist(artist);
			// 歌曲文件的路径 ：MediaStore.Audio.Media.DATA
			String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
			mp3.setUrl(url);
			// 歌曲的总播放时长 ：MediaStore.Audio.Media.DURATION
			int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
			mp3.setDuration(duration);
			// 歌曲文件的大小 ：MediaStore.Audio.Media.SIZE
			Long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
			mp3.setSize(size);
			if (size > 1024 * 800 && url.endsWith(".mp3")) {// 大于800K
				mylist.add(mp3);
			}
		}
		return mylist;
	}

}
