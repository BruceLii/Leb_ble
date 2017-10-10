package com.ledble.fragment;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;

import com.common.adapter.OnSeekBarChangeListenerAdapter;
import com.common.uitl.DrawTool;
import com.common.uitl.ListUtiles;
import com.common.uitl.LogUtil;
import com.common.uitl.SharePersistent;
import com.common.uitl.StringUtils;
import com.common.uitl.Tool;
import com.common.view.ScrollForeverTextView;
import com.common.view.Segment;
import com.common.view.SegmentedRadioGroup;
import com.ledble.R;
import com.ledble.activity.EditColorActivity;
import com.ledble.activity.MainActivity;
import com.ledble.activity.MusicLibActivity;
import com.ledble.base.LedBleApplication;
import com.ledble.base.LedBleFragment;
import com.ledble.bean.Mp3;
import com.ledble.bean.MyColor;
import com.ledble.constant.Constant;
import com.ledble.view.VolumCircleBar;

/**
 * 音乐
 * 
 * @author ftl
 *
 */
public class MusicFragment extends LedBleFragment implements Runnable {

	@Bind(R.id.segmentStyle) Segment segment;
	@Bind(R.id.imageViewPre) ImageView imageViewPre;
	@Bind(R.id.imageViewPlay) ImageView imageViewPlay;
	@Bind(R.id.imageViewNext) ImageView imageViewNext;
	@Bind(R.id.imageViewEdit) ImageView imageViewEdit;
	@Bind(R.id.textViewAutoAjust) ScrollForeverTextView textViewAutoAjust;
	@Bind(R.id.tvCurrentTime) TextView tvCurrentTime;
	@Bind(R.id.tvTotalTime) TextView tvTotalTime;
	@Bind(R.id.llDecibel) LinearLayout llDecibel;
	@Bind(R.id.seekBarDecibel) SeekBar seekBarDecibel;
	@Bind(R.id.tvDecibelValue) TextView tvDecibelValue;
	@Bind(R.id.llBottom) LinearLayout llBottom;
	@Bind(R.id.seekBarMusic) SeekBar seekBarMusic;
	@Bind(R.id.imageViewRotate) ImageView imageViewRotate;
	@Bind(R.id.imageViewPlayType) ImageView imageViewPlayType;
	@Bind(R.id.buttonMusicLib) Button buttonMusicLib;
	@Bind(R.id.seekBarRhythm) SeekBar seekBarRhythm;
	@Bind(R.id.tvRhythm) TextView tvRhythm;
	@Bind(R.id.tvrhythmValue) TextView tvrhythmValue;
	@Bind(R.id.volumCircleBar) VolumCircleBar volumCircleBar;

	private static final int INT_GO_SELECT_MUSIC = 100;
	private static final int INT_UPDATE_PROGRESS = 101;
	private static final int INT_EDIT_COLOR = 102;
	private static final int INT_UPDATE_RECORD = 103;
	private static int SAMPLE_RATE_IN_HZ = 8000;

	private ObjectAnimator mCircleAnimator;
	private SegmentedRadioGroup segmentedRadioGroup;
	private MainActivity mActivity;
	private View mContentView;
	private Animation animationRotate;
	private Animation animationScale;
	private MediaPlayer mediaPlayer;
	private boolean isMusic = true;
	private boolean isMusicStop = false;
	private boolean isStroboflash = false; //是否 频闪
	private int musicMode = 1;
	private int microMode = 1;
	private int indexMusic = 0;
	private int indexMicro = 0;
	private int musicStyeValue = 0;
	private int microStyeValue = 0;
	private int microDB = 0;
	private boolean isLoopAll = false;
	private boolean isLoopOne = true;
	private boolean isRandom = false;
	private Mp3 currentMp3;
	private Visualizer mVisualizer;
	private ArrayList<MyColor> colors;// 随机选择得颜色
	private volatile int chnnelValue;// 声道值，发送传化为亮度
	private boolean isStartTimer = false;
	private Timer timer;
	private int peridTime = 300;
	private int fy = 0;
	private int decibel = 0;
	private Thread sendMusicThread;
	private Random random = new Random();;
	private AudioRecord ar;
	private AudioTrack mAudioTrack;
	private int bs;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.fragment_music, container, false);
		return mContentView;
	}

	@Override
	public void initData() {
		bs = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_DEFAULT,
				AudioFormat.ENCODING_PCM_16BIT);
		ar = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_DEFAULT,
				AudioFormat.ENCODING_PCM_16BIT, bs);
		//
		// mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
		// SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_OUT_MONO,
		// AudioFormat.ENCODING_PCM_16BIT, bs,
		// AudioTrack.MODE_STREAM);

		mActivity = (MainActivity) getActivity();
		segmentedRadioGroup = mActivity.getSegmentMusic();
		
		mCircleAnimator = ObjectAnimator.ofFloat(imageViewRotate, "rotation", 0.0f, 360.0f);
		mCircleAnimator.setDuration(8000);
		mCircleAnimator.setInterpolator(new LinearInterpolator());
		mCircleAnimator.setRepeatCount(-1);
		mCircleAnimator.setRepeatMode(ObjectAnimator.RESTART);
	}

	@Override
	public void initView() {
		segment.setIndex(0);
		new Thread(this).start();
		
		final Handler handler = new Handler(); // 开启 音乐  渐变  定时器
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				// 在此处添加执行的代码
				handler.postDelayed(this, 3000);// 3秒 是延时时长

				if (chnnelValue > musicStyeValue) { // 渐变
					if (musicMode == 2 && true == isMusic) {
						
						if (null == colors || colors.size() == 0) {
							mActivity.setDiy(getRandomColors(), 1);
						} else {

							ArrayList<MyColor> colorList = new ArrayList<MyColor>();
							colorList.add(colors.get(indexMusic));

							mActivity.setDiy(colorList, 1);
							indexMusic++;
							if (indexMusic == (colors.size())) {
								indexMusic = 0;
							}
						}
					}
				}
			}
		};
		handler.postDelayed(runnable, 50);// 打开定时器，执行操作
		handler.removeCallbacks(runnable, this);// 关闭定时器处理
	}

	@Override
	public void initEvent() {
		this.imageViewPre.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				playAnimationPress(v);
				playPre();
			}
		});
		this.imageViewPlay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				playAnimationPress(v);
				playPause();
			}
		});
		this.imageViewNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				playAnimationPress(v);
				palayNext();
			}
		});
		this.seekBarDecibel.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				decibel = progress;
				SharePersistent.savePerference(activity, "decibel", progress);
				tvDecibelValue.setText(String.valueOf(progress));
			}
		});
		this.seekBarDecibel.setProgress(SharePersistent.getInt(activity, "decibel"));
		this.animationScale = AnimationUtils.loadAnimation(activity, R.anim.layout_scale);
		this.animationRotate = AnimationUtils.loadAnimation(activity, R.anim.rotate_frover);
		this.animationRotate.setInterpolator(new LinearInterpolator());
		this.animationRotate.setRepeatCount(-1);
		this.animationRotate.setAnimationListener(new com.common.adapter.AnimationListenerAdapter() {
		});
		this.imageViewRotate.setOnClickListener(new OnClickListener() { // 中间旋转大图

			@Override
			public void onClick(View v) {
				switch (musicMode) {
				case 0:
					imageViewRotate.setImageResource(R.drawable.music_jump);
					musicMode = 1;
					break;
				case 1:
					imageViewRotate.setImageResource(R.drawable.music_gradualchange);
					musicMode = 2;
					break;
				case 2:
					imageViewRotate.setImageResource(R.drawable.music_stroboflash);
					musicMode = 3;
					break;
				case 3:
					imageViewRotate.setImageResource(R.drawable.music_nooutput);
					musicMode = 0;
					break;

				default:
					break;
				}
			}
		});
		
		this.imageViewPlayType.setOnClickListener(new OnClickListener() { //播放模式（顺序，单曲，随机）

			@Override
			public void onClick(View v) {
				if (isLoopAll) {
					imageViewPlayType.setImageResource(R.drawable.playtype_loopall);
					Toast.makeText(mActivity, R.string.SequentialPlay, Toast.LENGTH_SHORT).show();
					isLoopAll = false;
					isLoopOne = true;
				} else if (isLoopOne) {
					imageViewPlayType.setImageResource(R.drawable.playtype_loopone);
					Toast.makeText(mActivity, R.string.SinglePlay, Toast.LENGTH_SHORT).show();
					isLoopOne = false;
					isRandom = true;
				} else if (isRandom) {
					imageViewPlayType.setImageResource(R.drawable.playtype_random);
					Toast.makeText(mActivity, R.string.RandomPlay, Toast.LENGTH_SHORT).show();
					isRandom = false;
					isLoopAll = true;
				}
//				isSending = !isSending;
			}
		});
		this.buttonMusicLib.setOnClickListener(new OnClickListener() { //乐库
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, MusicLibActivity.class);
				mActivity.startActivityForResult(intent, INT_GO_SELECT_MUSIC);
			}
		});

		imageViewEdit.findViewById(R.id.imageViewEdit).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, EditColorActivity.class);
				mActivity.startActivityForResult(intent, INT_EDIT_COLOR);
			}
		});
		this.seekBarRhythm.setOnSeekBarChangeListener(new OnSeekBarChangeListenerAdapter() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				float fl = (float) progress / 100;
				fy = (int) (100 * fl) * 2;
				
				tvrhythmValue = (TextView) mActivity.findViewById(R.id.tvrhythmValue);
				tvrhythmValue.setText(Integer.toString(progress));
				musicStyeValue = progress;

				SharePersistent.savePerference(mActivity, "rhythm", progress);
			}
		});
		this.seekBarRhythm.setProgress(SharePersistent.getInt(mActivity, "rhythm"));
		this.volumCircleBar.setVisibility(View.GONE);
		this.volumCircleBar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (musicMode!= 3 && null != mediaPlayer && mediaPlayer.isPlaying()) {
					Toast.makeText(mActivity, R.string.please_close_music, Toast.LENGTH_SHORT).show();
					volumCircleBar.setBackgroundResource(R.drawable.micro_stop);
				} else {
					
					switch (microMode) {
					case 0:
						volumCircleBar.toggleRecord(0);
						volumCircleBar.setBackgroundResource(R.drawable.micro_jump);
						microMode = 1;
						break;
					case 1:
						volumCircleBar.setBackgroundResource(R.drawable.micro_gradualchange);
						microMode = 2;
						break;
					case 2:
						volumCircleBar.setBackgroundResource(R.drawable.micro_stroboflash);
						microMode = 3;
						break;
					case 3:
						volumCircleBar.toggleRecord(3);
						volumCircleBar.setBackgroundResource(R.drawable.micro_stop);
						Toast.makeText(mActivity, R.string.pause_microphone, Toast.LENGTH_SHORT).show();
						mActivity.setRgb(255, 255, 255);
						mActivity.setBrightNess(100);
						microMode = 0;
						break;

					default:
						break;
					}
				}
			}
		});

		segmentedRadioGroup.check(R.id.rbMusicOne);
		segmentedRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (R.id.rbMusicOne == checkedId) {
					isMusic = true;
					
					imageViewEdit.setVisibility(View.VISIBLE);
					tvrhythmValue.setVisibility(View.VISIBLE);
					tvRhythm.setVisibility(View.VISIBLE);
					seekBarRhythm.setVisibility(View.VISIBLE);
					imageViewRotate.setVisibility(View.VISIBLE);
					volumCircleBar.setVisibility(View.GONE);
					llDecibel.setVisibility(View.GONE);
					llBottom.setVisibility(View.VISIBLE);
					pauseVolum();
				} else {
					isMusic = false;
					
					imageViewEdit.setVisibility(View.INVISIBLE);
					tvrhythmValue.setVisibility(View.INVISIBLE);
					tvRhythm.setVisibility(View.INVISIBLE);
					seekBarRhythm.setVisibility(View.INVISIBLE);
					segment.setVisibility(View.INVISIBLE);
					imageViewRotate.setVisibility(View.GONE);
					volumCircleBar.setVisibility(View.VISIBLE);
					llDecibel.setVisibility(View.VISIBLE);
					llBottom.setVisibility(View.GONE);

					if (!isHasPermission()) {
						Toast.makeText(mActivity, R.string.microphone_enable, Toast.LENGTH_SHORT).show();
						return;
					}

					
					// 点击了 麦克风 标签，再初始化
					volumCircleBar.toggleRecord(0);
					ar.startRecording();
					volumCircleBar.setBackgroundResource(R.drawable.micro_jump);
					Toast.makeText(mActivity, R.string.microphone_start, Toast.LENGTH_SHORT).show();
					pauseMusic();
					
					final Handler handler = new Handler();
					Runnable runnable = new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							// 在此处添加执行的代码
							handler.postDelayed(this, 3000);// 3秒 是延时时长

							if (microDB > decibel) { // 渐变
								if (microMode == 2 && false == isMusic) {
									
									if (null == colors || colors.size() == 0) {
										mActivity.setDiy(getRandomColors(), 1);
									} else {

										ArrayList<MyColor> colorList = new ArrayList<MyColor>();
										colorList.add(colors.get(indexMicro));

										mActivity.setDiy(colorList, 1);
										indexMicro++;
										if (indexMusic == (colors.size())) {
											indexMicro = 0;
										}
									}
								}
							}
						}
					};
					handler.postDelayed(runnable, 50);// 打开定时器，执行操作
					handler.removeCallbacks(runnable, this);// 关闭定时器处理 
				}
			}
		});
		seekBarMusic.setOnSeekBarChangeListener(new OnSeekBarChangeListenerAdapter() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

			}

			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				if (null == currentMp3) {
					Tool.ToastShow(mActivity, R.string.chose_list);
					seekBar.setProgress(0);
					return;
				}
				if (!ListUtiles.isEmpty(LedBleApplication.getApp().getMp3s())) {
					System.out.println("progress-->" + seekBar.getProgress());
					int duration = currentMp3.getDuration();
					int current = (int) seekBar.getProgress() * duration / 100;
					System.out.println("current-->" + current);
					mediaPlayer.seekTo(current);
				}
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		// 设置音乐
		sendMusicData();
	}

	private void sendMusicData() {
		if (null == sendMusicThread || isStartTimer == false) {
			sendMusicThread = new Thread(new Runnable() {
				@Override
				public void run() {
					isStartTimer = true;
					while (true) {
						try {
							if (chnnelValue > 0 && (/*musicMode!= 3 &&*/ null != mediaPlayer && mediaPlayer.isPlaying())) {
								sendMusicValue(getRandColor(), chnnelValue);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						Tool.delay(peridTime - fy);
						if (mActivity.isFinishing()) {
							break;
						}
					}
				}
			});
			sendMusicThread.start();
		}
	}

	/*
	 * 随机发送颜色
	 */
	private MyColor getRandColor() {
		if (null == colors || colors.size() == 0) {
			return null;
		}

		int r = random.nextInt(colors.size());
		MyColor color = colors.get(r);
		return color;
	}

	private void playAnimationPress(View v) {
		v.startAnimation(animationScale);
	}

	private Handler mhanHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case INT_UPDATE_PROGRESS:
				updatePlayProgress(currentMp3);
				if (mediaPlayer != null) {
					int current = (int) (mediaPlayer.getCurrentPosition());
					int musicTime = (int) current / 1000;
//					String time = "";
					String time = new String("");
					if (musicTime < 60) {
						if (musicTime < 10) {
							time = "0:0" + musicTime;
						} else {
							time = "0:" + musicTime;
						}
					} else {
//						String minute = "";
						String minute = new String("");
						if (musicTime % 60 < 10) {
							minute = "0" + musicTime % 60;
						} else {
							minute = "" + musicTime % 60;
						}
						time = musicTime / 60 + ":" + minute;
					}
					tvCurrentTime.setText(time);
				}
				break;
			case INT_UPDATE_RECORD:
				// int pre = (Integer) msg.obj;
				double pre = (Double) msg.obj;
				int dB = (int) (10 * Math.log10(pre));
				int value = dB;
				microDB = dB;

				float precent = (float) value / 100;
				
				volumCircleBar.updateVolumRate(precent);
				
				if ( microMode == 0) {
					return;
				}
				if (dB < decibel) {
					return;
				} else {
					
					sendVolumValue(getRandColor(), value);
				}
				break;
			}
		}
	};

	private void updatePlayProgress(Mp3 currentMp3) {
		if (null != mediaPlayer && mediaPlayer.isPlaying()) {
			int duration = currentMp3.getDuration();
			int current = (int) (mediaPlayer.getCurrentPosition());
			if (duration != 0 && (current < duration)) {
				int percent = (int) (current * 100 / duration);
				seekBarMusic.setProgress(percent);
			}
		}
	}

	public void palayNext() {
		if (null == currentMp3) {
			if (!ListUtiles.isEmpty(LedBleApplication.getApp().getMp3s())) {
				startPlay(0);
			} else {
				Tool.ToastShow(mActivity, R.string.chose_list);
			}
		} else {
			if (!ListUtiles.isEmpty(LedBleApplication.getApp().getMp3s())) {
				int currentIndex = findCurrentIndex(currentMp3);
				if (currentIndex != -1) {
					if (currentIndex < (LedBleApplication.getApp().getMp3s().size() - 1)) {
						startPlay(++currentIndex);
					} else if (currentIndex == (LedBleApplication.getApp().getMp3s().size() - 1)) {
						startPlay(0);
					}
				} else {
					startPlay(0);
				}
			} else {
				Tool.ToastShow(mActivity, R.string.chose_list);
			}
		}
	}

	public void playPre() {
		if (null == currentMp3) {
			if (!ListUtiles.isEmpty(LedBleApplication.getApp().getMp3s())) {
				startPlay(0);
			} else {
				Tool.ToastShow(mActivity, R.string.chose_list);
			}
		} else {
			if (!ListUtiles.isEmpty(LedBleApplication.getApp().getMp3s())) {
				int currentIndex = findCurrentIndex(currentMp3);
				if (currentIndex != -1) {
					if (currentIndex > 0) {
						startPlay(--currentIndex);
					} else if (currentIndex == 0) {
						startPlay(LedBleApplication.getApp().getMp3s().size() - 1);
					}
				} else {
					startPlay(0);
				}
			} else {
				Tool.ToastShow(mActivity, R.string.chose_list);
			}
		}
	}

	/**
	 * 停止播放音乐
	 */
	public void pauseMusic() {
//		if (musicMode!= 3) {
			if (mediaPlayer != null && mediaPlayer.isPlaying()) {
				// 暂停
				mediaPlayer.pause();
				imageViewPlay.setImageResource(R.drawable.bg_play);
				mCircleAnimator.pause();
			}
//		}
	}
	
	/**
	 * 开始播放音乐
	 */
	public void startMusic() {
		if (musicMode!= 3 && isMusic == true ) {
			
			if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
				
				if (isMusicStop) {
					return;
				}else {
					mediaPlayer.start();
					imageViewPlay.setImageResource(R.drawable.bg_play_pause);
					startRotate(); // 开始旋转动画
				}
			}
		}
	}

	/**
	 * 停止麦克风
	 */
	public void pauseVolum() {
		if (volumCircleBar != null && volumCircleBar.recordMode() != 3) {
			volumCircleBar.toggleRecord(3);
		}
	}
	
	/**
	 * 开始麦克风
	 */
	public void startVolum() {
		if (microMode != 0 && isMusic == false) {
			if (volumCircleBar != null) {
				volumCircleBar.toggleRecord(0);
			}		
		}
	}

	public void playPause() {

		if (ListUtiles.isEmpty(LedBleApplication.getApp().getMp3s())) {
			Tool.ToastShow(mActivity, R.string.chose_list);
			return;
		}

		if (null == mediaPlayer) {
			startPlay(0);
		} else {
			if (mediaPlayer.isPlaying()) {
				// 暂停
				mediaPlayer.pause();
				imageViewPlay.setImageResource(R.drawable.bg_play);
				stopRotate(); // 暂停旋转动画
				isMusicStop = true;
			} else {
				// 播放
				if (null != currentMp3) {
					mediaPlayer.start(); 
					resumeRotate();  // 继续旋转动画
					isMusicStop = false;
				} else {
					if (!ListUtiles.isEmpty(LedBleApplication.getApp().getMp3s())) {
						startPlay(0);
					} else {
						Tool.ToastShow(mActivity, R.string.chose_list);
						return;
					}
				}
				imageViewPlay.setImageResource(R.drawable.bg_play_pause);
			}

		}

	}

	boolean isSettingBoolean = false;

	public void playMp3(final Mp3 mp3) {
		try {
			currentMp3 = mp3;
			setTitles(mp3);
			setAbulmImage(imageViewRotate, mp3);
			if (null == mediaPlayer) {
				mediaPlayer = new MediaPlayer();
			}
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
			}
			mediaPlayer.reset();
			mediaPlayer.setDataSource(mp3.getUrl());
			if (!isSettingBoolean) {
				isSettingBoolean = true;
				mVisualizer = new Visualizer(mediaPlayer.getAudioSessionId());
				mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[0]);
				// mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
			}
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					if (mVisualizer != null) {
						mVisualizer.setEnabled(false);
					}
					try {
						ArrayList<Mp3> list = LedBleApplication.getApp().getMp3s();
						int index = findCurrentIndex(mp3);
						
						if (!isLoopOne && isRandom) { //单曲循环
							mediaPlayer.start();  
							mediaPlayer.setLooping(true);
//							playMp3(list.get(index));
							return;
						}else if (!isRandom && isLoopAll) { //随机播放
							int playIndex = getRandIntPlayIndex(list.size());
							playMp3(list.get(playIndex));
							return;
						}
						
						if (-1 != index && !ListUtiles.isEmpty(list)) {
							if (index == (list.size() - 1)) {
								playMp3(list.get(0));
								return;
							}
							if (index <= (list.size() - 2)) {
								playMp3(list.get(index + 1));
								return;
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			mediaPlayer.prepare();
			mediaPlayer.start();
			startRotate();	//继续旋转动画
			if (mVisualizer != null) {
				mVisualizer.setEnabled(false);
			}

			mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
				public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
					updateVisualizer(bytes);
				}

				public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
					updateVisualizer(fft);
				}
			}, Visualizer.getMaxCaptureRate() / 2, true, false);
			if (mVisualizer != null) {
				mVisualizer.setEnabled(true);
			}
			new Thread(this).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 ** 开启动画    
	 */
	public void startRotate() {
//		Animation operatingAnim = AnimationUtils.loadAnimation(mActivity, R.anim.img_animation);
//		LinearInterpolator lin = new LinearInterpolator();
//		operatingAnim.setInterpolator(lin);
//		if (operatingAnim != null) {
//			imageViewRotate.startAnimation(operatingAnim);
//			
//		}
		
		mCircleAnimator.start();
	}
	
	/**
	 ** 继续动画    
	 */
	public void resumeRotate() {
//		imageViewRotate.clearAnimation();
//		imageViewRotate.onAnimationStart();
		mCircleAnimator.resume();
	}

	/**
	 ** 暂停动画    
	 */
	public void stopRotate() {
//		imageViewRotate.clearAnimation();
//		imageViewRotate.onAnimationStart();
		mCircleAnimator.pause();
	}

	/**
	 * 设置title现实内容
	 * 
	 * @param mp3
	 */
	private void setTitles(Mp3 mp3) {
		String artist = mp3.getArtist();
		if (StringUtils.isEmpty(artist)) {
			textViewAutoAjust.setText(mp3.getTitle());
		} else {
			String title = getActivity().getResources().getString(R.string.ablum_title, "<<" + mp3.getTitle() + ">>",
					mp3.getArtist(), mp3.getAlbum());
			textViewAutoAjust.setText(title);
		}
	}

	/**
	 * 获取并且专辑专辑图片
	 * 
	 * @param imageViewImg
	 * @param mp3
	 */
	@SuppressLint("NewApi")
	private void setAbulmImage(ImageView imageViewImg, Mp3 mp3) {
		try {
			MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
			mediaMetadataRetriever.setDataSource(mp3.getUrl());
			byte[] data = mediaMetadataRetriever.getEmbeddedPicture();
			if (data != null) {
				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
				bitmap = DrawTool.toRoundBitmap(bitmap);
				// imageViewRotate.setImageBitmap(bitmap); // associated cover
				// art in//
			} else {
				imageViewRotate.setImageResource(R.drawable.music_jump);
			}
		} catch (Exception e) {
			e.printStackTrace();
			imageViewRotate.setImageResource(R.drawable.music_jump);
		}
	}

	// @Override
	// public void onDestory() {
	// if (null != mediaPlayer) {
	// mediaPlayer.release();
	// mediaPlayer = null;
	// }
	// if (null != ar) {
	// ar.stop();
	// ar = null;
	// }
	// if (null != timer) {
	// timer.cancel();
	// }
	// }

	int microDegree = 0;
	private void sendVolumValue(MyColor color, int volumValue) {
		
//		if (null != color) {
//			mActivity.setRgb(color.r, color.g, color.b);
//		} else {
//			mActivity.setRgb(getRandIntColor(), getRandIntColor(), getRandIntColor());
//		}
		if (isMusic) {
			return;
		}
		
		int maxCount = 1;
		if (microDegree == maxCount) {
			microDegree = 0;
			
			if (volumValue <= decibel || microMode == 0 || volumValue == 0) {
				return;
			}
			
			if (microMode != 2) {
				if (null != color) {
					mActivity.setRgb(color.r, color.g, color.b);
				} else {
					mActivity.setRgb(getRandIntColor(), getRandIntColor(), getRandIntColor());
				}
			}
			
		} else {

			switch (microMode) {
			case 0:	//无输出
				
				break;
			case 1:	//跳变
				mActivity.setBrightNess(100);
				break;
			case 2:	//呼吸
//				mActivity.setDynamicModel(129);
				break;
			case 3:	//频闪
				if (isStroboflash) {
					mActivity.setBrightNess(0);
					LogUtil.i(LedBleApplication.tag, "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ ");
					isStroboflash = false;
				}else {
					mActivity.setBrightNess(100);
					LogUtil.i(LedBleApplication.tag, "---------------------------------------------------------------------------- ");
					isStroboflash = true;
				}
			
				break;

			default:
				break;
			}
			
			microDegree++;
			if (microDegree > maxCount) {
				microDegree = 0;
			}
		}
	}

	int degree = 0;
	// 每隔一定时效发送
	private void sendMusicValue(MyColor color, int chnnel) {
		// 把亮度和颜色值错开发送，不然会有问题，7*300毫秒发送一次颜色值
		// 1.曲风添加：流行，就按照原来的不改。柔和，在发送亮度的时候判断下，亮度<50? 50:100.摇滚，亮度<50?0:100
		
//		 LogUtil.i(LedBleApplication.tag, "chnnel = ++++++++++++++++++++++++++++++++++++++++++++++++ " + chnnel);

		int maxCount = 1;
		if (degree == maxCount) {
			degree = 0;
			
			if (musicMode == 0) {
				mActivity.setRgb(255, 255, 255);
				mActivity.setBrightNess(100);
				return;
			}
			
			if (chnnel <= musicStyeValue) {
				return;
			}
			
			
			if (musicMode != 2) {
				if (null != color) {
					mActivity.setRgb(color.r, color.g, color.b);
				} else {
					mActivity.setRgb(getRandIntColor(), getRandIntColor(), getRandIntColor());
				}
			}
			
		} else {
			
			switch (musicMode) {
			case 0:	//无输出
//				mActivity.setBrightNess(100);
				break;
			case 1:	//跳变
				mActivity.setBrightNess(100);
				break;
			case 2:	//呼吸
//				mActivity.setDynamicModel(129);
				break;
			case 3:	//频闪
				if (isStroboflash) {
					mActivity.setBrightNess(0);
					LogUtil.i(LedBleApplication.tag, "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ ");
					isStroboflash = false;
				}else {
					mActivity.setBrightNess(100);
					LogUtil.i(LedBleApplication.tag, "---------------------------------------------------------------------------- ");
					isStroboflash = true;
				}
			
				break;

			default:
				break;
			}
			
			degree++;
			if (degree > maxCount) {
				degree = 0;
			}
		}
	}

	Random rand = new Random();

	private int getRandIntColor() {
		int color = rand.nextInt(255);
		return color;
	}
	
	private int getRandIntPlayIndex(int size) {
		int index = rand.nextInt(size);
		return index;
	}

	private void updateVisualizer(byte[] fft) {
		int mSpectrumNum = 48;
		byte[] model = new byte[fft.length / 2 + 1];
		model[0] = (byte) Math.abs(fft[0]);
		for (int i = 2, j = 1; j < mSpectrumNum;) {
			model[j] = (byte) Math.hypot(fft[i], fft[i + 1]);
			i += 2;
			j++;
		}
		StringBuffer sb = new StringBuffer();
		for (Byte b : model) {
			sb.append(b + " ");
		}
		chnnelValue = avgData(model);
		// LogUtil.i(App.tag, "fft:" + fft.length + " model:" + model.length);
		// LogUtil.i(App.tag, "vs:" + sb.toString());
	}

	private int findCurrentIndex(Mp3 mp3) {
		ArrayList<Mp3> list = LedBleApplication.getApp().getMp3s();
		if (ListUtiles.isEmpty(list)) {
			return -1;
		}
		for (int i = 0, lsize = list.size(); i < lsize; i++) {
			if (mp3.equals(list.get(i))) {
				return i;
			}
		}
		return -1;
	}

	public int avgData(byte[] fft) {
		int sum = 0;
		for (int i = 1; i < fft.length; i++) {
			sum += fft[i];
		}
		int ave = sum / (fft.length - 1);
		ave = (int) (Math.abs((float) ave / 127) * 100);
		return ave;
		// LogUtil.i(App.tag, "consult:" + sum / fft.length);
	}

	private void startPlay(int index) {
		if (!ListUtiles.isEmpty(LedBleApplication.getApp().getMp3s())
				&& LedBleApplication.getApp().getMp3s().size() > index) {
			try {
				playMp3(LedBleApplication.getApp().getMp3s().get(index));
				imageViewPlay.setImageResource(R.drawable.bg_play_pause);
				int musicTime = mediaPlayer.getDuration() / 1000;
				String minute = "";
				if (musicTime % 60 < 10) {
					minute = "0" + musicTime % 60;
				} else {
					minute = "" + musicTime % 60;
				}
				String time = musicTime / 60 + ":" + minute;
				tvCurrentTime.setText("0:00");
				tvTotalTime.setText(time);
				// this.currentPlayIndex = index;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == INT_GO_SELECT_MUSIC && resultCode == Activity.RESULT_OK) {
			startPlay(0);
		}
		if (requestCode == INT_EDIT_COLOR && resultCode == Activity.RESULT_OK && null != data) {
			colors = (ArrayList<MyColor>) data.getSerializableExtra("color");
		}
	}

	@Override
	public void run() {
		try {
			// ar.startRecording();
			// 用于读取的 buffer
			short[] buffer = new short[bs];
			while (true) {
				if (musicMode!= 3 && null != mediaPlayer && mediaPlayer.isPlaying()) {
					mhanHandler.sendEmptyMessage(INT_UPDATE_PROGRESS);
				}

				if (volumCircleBar != null && volumCircleBar.recordMode() != 3) {
					int r = ar.read(buffer, 0, bs);
					long v = 0;
					// 将 buffer 内容取出，进行平方和运算
					for (int i = 0; i < buffer.length; i++) {
						// 这里没有做运算的优化，为了更加清晰的展示代码
						v += buffer[i] * buffer[i];
					}

					// int value = (int)(v / r);
					double value = v / (double) r;
					// 平方和除以数据总长度，得到音量大小。可以获取白噪声值，然后对实际采样进行标准化。
					// 如果想利用这个数值进行操作，建议用 sendMessage 将其抛出，在 Handler 里进行处理。
					// double dB = 10*Math.log10(v / (long) r);
					Message msg = new Message();
					msg.what = INT_UPDATE_RECORD;
					msg.obj = value;
					mhanHandler.sendMessage(msg);
				}
				Tool.delay(peridTime - fy);
				if (activity.isFinishing()) {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private ArrayList<MyColor> getRandomColors() {

		ArrayList<MyColor> colorList = new ArrayList<MyColor>();
		colorList.add(new MyColor(getRandIntColor(), getRandIntColor(), getRandIntColor()));

		return colorList;
	}
	
	public static int audioSource = MediaRecorder.AudioSource.MIC;
    public static int sampleRateInHz = 44100;
    public static int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
    public static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    public static int bufferSizeInBytes = 0;
	public static boolean isHasPermission(){
        bufferSizeInBytes = 0;
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
        AudioRecord audioRecord =  new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);
        
        try{
            audioRecord.startRecording();
        }catch (IllegalStateException e){
            e.printStackTrace();
        }
        
        if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
            return false;
        }
        audioRecord.stop();
        audioRecord.release();
        audioRecord = null;

        return true;
    }


}

