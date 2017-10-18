package com.ledble.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.common.uitl.ListUtiles;
import com.common.uitl.LogUtil;
import com.common.uitl.SharePersistent;
import com.common.uitl.StringUtils;
import com.common.uitl.Tool;
import com.common.view.SegmentedRadioGroup;
import com.ledble.R;
import com.ledble.base.LedBleActivity;
import com.ledble.base.LedBleApplication;
import com.ledble.bean.MyColor;
import com.ledble.constant.Constant;
import com.ledble.db.Group;
import com.ledble.db.GroupDevice;
import com.ledble.db.GroupDeviceDao;
import com.ledble.fragment.CtFragment;
import com.ledble.fragment.DmFragment;
import com.ledble.fragment.MusicFragment;
import com.ledble.fragment.RgbFragment;
import com.ledble.fragment.TimerFragment;
import com.ledble.net.NetConnectBle;
import com.ledble.net.NetExceptionInterface;
import com.ledble.service.BluetoothLeServiceSingle;
import com.ledble.service.MyServiceConenction;
import com.ledble.service.MyServiceConenction.ServiceConnectListener;
import com.ledble.utils.ManageFragment;
import com.ledble.view.ActionSheet;
import com.ledble.view.ActionSheet.ActionSheetListener;
import com.ledble.view.ActionSheet.Item;
import com.ledble.view.GroupView;
import com.ledble.view.SlideSwitch;
import com.ledble.view.SlideSwitch.SlideListener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import butterknife.Bind;

public class MainActivity extends LedBleActivity implements NetExceptionInterface, SensorEventListener, ActionSheetListener {

    private static final int TAKE_PICTURE = 0; //
    private static final int CHOOSE_PICTURE = 1;
    private static final long SCAN_PERIOD = 3000;
    private static final int REQUEST_ENABLE_BT = 1;
    private static MainActivity mActivity;
    private static Bitmap bm;
    private final int MSG_START_CONNECT = 10000;// 开始连接
    public boolean isLightOpen = true;
    public int speed = 1;
    public int brightness = 1;
    @Bind(R.id.segmentDm)
    SegmentedRadioGroup segmentDm;
    @Bind(R.id.segmentCt)
    SegmentedRadioGroup segmentCt;
    @Bind(R.id.segmentRgb)
    SegmentedRadioGroup segmentRgb;
    @Bind(R.id.segmentMusic)
    SegmentedRadioGroup segmentMusic;
    @Bind(R.id.ivLeftMenu)
    ImageView ivLeftMenu;
    @Bind(R.id.textViewConnectCount)
    TextView textViewConnectCount;
    @Bind(R.id.ivRightMenu)
    ImageView ivRightMenu;
    @Bind(R.id.ivType)
    ImageView ivType;
    @Bind(R.id.rgBottom)
    RadioGroup rgBottom;
    @Bind(R.id.rbFirst)
    RadioButton rbFirst;
    @Bind(R.id.rbSecond)
    RadioButton rbSecond;
    @Bind(R.id.rbThrid)
    RadioButton rbThrid;
    @Bind(R.id.rbFourth)
    RadioButton rbFourth;
    @Bind(R.id.rbFifth)
    RadioButton rbFifth;
    @Bind(R.id.llMenu)
    LinearLayout avtivity_main;
    @Bind(R.id.menu_content_layout)
    LinearLayout left_menu;
    @Bind(R.id.right_menu_frame)
    ScrollView right_menu;
    @Bind(R.id.linearLayoutTopItem)
    LinearLayout TopItem;
    private int currentIndex;
    private FragmentManager fragmentManager;
    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    private DrawerLayout mDrawerLayout;
    private String speedKey = "speedkey";
    private String brightnessKey = "brightnesskey";
    private String groupName = "";
    private SharedPreferences sp;
    private Editor editor;
    private MusicFragment musicFragment;
    private BluetoothLeServiceSingle mBluetoothLeService;
    private MyServiceConenction myServiceConenction;
    private LinearLayout linearGroups;
    // left_menu
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private View refreshView;
    private Button buttonAddGroup, buttonAllOn, buttonAllOff;
    // right_menu
    private TextView operationguideTV, resetTV, changePicTV, dynamicTV, shakeTV;
    private ImageView gradientIV, breathIV, jumpIV, strobeIV;
    private ImageView shakeColorIV, shakeNoneIV, shakeModelIV;
    private Random random = new Random();
    private SensorManager sensorManager;
    private SoundPool soundPool;
    private int soundID;
    private int shakeStyle = 1;
    private ImageView imageView = null;
    private TextView textViewAllDeviceIndicater;// 显示所有设备
    private String allOnStringKey = "isAllon";
    private int INT_GO_LIST = 111;
    private boolean isInitGroup = false;
    private boolean isAllOn = true;
    private boolean isStop = true;
    private boolean isFirstOnApp = true;

    private volatile HashMap<String, Boolean> hashMapLock;// service连接锁
    private volatile HashMap<String, Boolean> hashMapConnect;
    private Map<String, SlideSwitch> map = new HashMap<>();
    private ArrayList<GroupView> arrayListGroupViews;
    private SlideSwitch slideSwitch;
    private boolean booleanCanStart = false;
    private Handler conectHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case MSG_START_CONNECT:// 可以开始连接了
                    if (booleanCanStart == false) {
                        booleanCanStart = true;
                        // updateNewFindDevice();
                        BluetoothDevice device = (BluetoothDevice) msg.obj;
                        startConnectDevices(device);//单次发现，单次连接
                    }
                    break;

                default:
                    break;
            }
        }

        ;
    };
    /**
     * 成功扫描设备
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (device != null) {
                        if (!LedBleApplication.getApp().getBleDevices().contains(device) && device.getName() != null) {
                            String name = device.getName();

                            if (name.startsWith(BluetoothLeServiceSingle.NAME_START_ELK)
                                    || name.startsWith(BluetoothLeServiceSingle.NAME_START_LED)
                                    || name.startsWith(BluetoothLeServiceSingle.NAME_START_TV)
                                    || name.startsWith(BluetoothLeServiceSingle.NAME_START_HEI)) {
                                LedBleApplication.getApp().getBleDevices().add(device);
                                LogUtil.i(LedBleApplication.tag, "发现新设备：" + device.getAddress() + " total:" + LedBleApplication.getApp().getBleDevices().size());
//                                conectHandler.sendEmptyMessage(MSG_START_CONNECT);// 可以开始连接设备了 发现单个设备
                                Message m = Message.obtain();
                                m.what = MSG_START_CONNECT;
                                m.obj = device;
                                conectHandler.sendMessage(m);
                            }
                        }
                    }
                }
            });
        }
    };
    /**
     * 广播监听回调
     */
    private BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            System.out.println("接收廣播action-->" + action);
            String address = intent.getStringExtra("address");
            if (BluetoothLeServiceSingle.ACTION_GATT_CONNECTED.equals(action)) {// 连接到设备

                hashMapConnect.put(address, true);
                updateDevieConnect();
                LogUtil.i(LedBleApplication.tag, "connect:" + address);

                mBluetoothAdapter.stopLeScan(mLeScanCallback);

            } else if (BluetoothLeServiceSingle.ACTION_GATT_DISCONNECTED.equals(action)) {// 设断开备
                hashMapConnect.remove(address);
                LedBleApplication.getApp().removeDisconnectDevice(address);// 删除断开的device
                LedBleApplication.getApp().getBleGattMap().remove(address);// 删除断开的blgatt
                hashMapLock.remove(address);// 删除锁
                updateDevieConnect();
                LogUtil.i(LedBleApplication.tag, "disconnect:" + address + " connected devices:"
                        + LedBleApplication.getApp().getBleDevices().size());

                mBluetoothAdapter.startLeScan(mLeScanCallback);

            } else if (BluetoothLeServiceSingle.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {// 发现service,就可以获取Characteristic
                // 发现之后依次连接
                BluetoothGatt blgat = mBluetoothLeService.getBluetoothGatt();
                LedBleApplication.getApp().getBleGattMap().put(address, blgat);
                hashMapLock.put(address, true);// 解锁
                LogUtil.i(LedBleApplication.tag, "发现service" + intent.getStringExtra("address"));
            } else if (BluetoothLeServiceSingle.ACTION_DATA_AVAILABLE.equals(action)) {// 读取到数据，不做处理，本应用不需要读取数据
            }
        }
    };

    /**
     * 根据图片的Uri获取图片的绝对路径(已经适配多种API)
     *
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    public static String getRealPathFromUri(Context context, Uri uri) {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion < 11) {
            // SDK < Api11
            return getRealPathFromUri_BelowApi11(context, uri);
        }
        if (sdkVersion < 19) {
            // SDK > 11 && SDK < 19
            return getRealPathFromUri_Api11To18(context, uri);
        }
        // SDK > 19
        return getRealPathFromUri_AboveApi19(context, uri);
    }

    /**
     * 适配api19以上,根据uri获取图片的绝对路径
     */
    private static String getRealPathFromUri_AboveApi19(Context context, Uri uri) {
        String filePath = null;
        String wholeID = DocumentsContract.getDocumentId(uri);

        // 使用':'分割
        String id = wholeID.split(":")[1];

        String[] projection = {MediaStore.Images.Media.DATA};
        String selection = MediaStore.Images.Media._ID + "=?";
        String[] selectionArgs = {id};

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                selection, selectionArgs, null);
        int columnIndex = cursor.getColumnIndex(projection[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    /**
     * 适配api11-api18,根据uri获取图片的绝对路径
     */
    private static String getRealPathFromUri_Api11To18(Context context, Uri uri) {
        String filePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};

        CursorLoader loader = new CursorLoader(context, uri, projection, null,
                null, null);
        Cursor cursor = loader.loadInBackground();

        if (cursor != null) {
            cursor.moveToFirst();
            filePath = cursor.getString(cursor.getColumnIndex(projection[0]));
            cursor.close();
        }
        return filePath;
    }

    /**
     * 适配api11以下(不包括api11),根据uri获取图片的绝对路径
     */
    private static String getRealPathFromUri_BelowApi11(Context context, Uri uri) {
        String filePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection,
                null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            filePath = cursor.getString(cursor.getColumnIndex(projection[0]));
            cursor.close();
        }
        return filePath;
    }

    /**
     * 创建广播过滤器
     *
     * @return
     */
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeServiceSingle.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeServiceSingle.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeServiceSingle.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeServiceSingle.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    /**
     * 判断app是否进入后台
     */
    public static boolean isBackground(Context context) {

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {

                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        mActivity = this;

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT); //透明 状态栏
        }

        initFragment();
        initSlidingMenu();
        initView();

        if (getImagePath() != "") { // 显示保存的皮肤
            showImage(getImagePath());
        }
    }

    // =============================================行为事件=====================================================//

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // super.onSaveInstanceState(outState);
    }

    @SuppressLint("NewApi")
    private void initView() {

        mActivity.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        mActivity.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        // 是否支持蓝牙
        if (!mActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(mActivity, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            Tool.exitApp();
        }

        bluetoothManager = (BluetoothManager) mActivity.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        // 不能获得蓝牙设备支持
        if (mBluetoothAdapter == null) {
            Tool.ToastShow(mActivity, R.string.ble_not_supported);
            Tool.exitApp();
        }

        mHandler = new Handler();
        hashMapLock = new HashMap<String, Boolean>();
        hashMapConnect = new HashMap<String, Boolean>();
        arrayListGroupViews = new ArrayList<GroupView>();

        //右菜单  各个标签项
        changePicTV = (TextView) mActivity.findViewById(R.id.change_under_pic_tv);
        changePicTV.setOnClickListener(new MyOnClickListener());
        resetTV = (TextView) mActivity.findViewById(R.id.reset_tv);
        resetTV.setOnClickListener(new MyOnClickListener());
        shakeTV = (TextView) mActivity.findViewById(R.id.shake_tv);
        shakeTV.setOnClickListener(new MyOnClickListener());
        dynamicTV = (TextView) mActivity.findViewById(R.id.dynamic_tv);
        dynamicTV.setOnClickListener(new MyOnClickListener());

        //右菜单   律动各项
        gradientIV = (ImageView) mActivity.findViewById(R.id.dynamic_gradient_iv);
        gradientIV.setOnClickListener(new MyOnClickListener());
        breathIV = (ImageView) mActivity.findViewById(R.id.dynamic_breath_iv);
        breathIV.setOnClickListener(new MyOnClickListener());
        jumpIV = (ImageView) mActivity.findViewById(R.id.dynamic_jump_iv);
        jumpIV.setOnClickListener(new MyOnClickListener());
        strobeIV = (ImageView) mActivity.findViewById(R.id.dynamic_strobe_iv);
        strobeIV.setOnClickListener(new MyOnClickListener());
        operationguideTV = (TextView) mActivity.findViewById(R.id.operation_guide_tv);
        operationguideTV.setOnClickListener(new MyOnClickListener());

        //右菜单  摇一摇
        shakeColorIV = (ImageView) mActivity.findViewById(R.id.shake_one_iv);
        shakeColorIV.setOnClickListener(new MyOnClickListener());
        shakeNoneIV = (ImageView) mActivity.findViewById(R.id.shake_two_iv);
        shakeNoneIV.setOnClickListener(new MyOnClickListener());
        shakeModelIV = (ImageView) mActivity.findViewById(R.id.shake_three_iv);
        shakeModelIV.setOnClickListener(new MyOnClickListener());


        //左侧   菜单
        imageView = (ImageView) mActivity.findViewById(R.id.activity_main_imageview);
        linearGroups = (LinearLayout) mActivity.findViewById(R.id.linearLayoutDefineGroups);
        textViewAllDeviceIndicater = (TextView) mActivity.findViewById(R.id.textViewAllDeviceIndicater);
        TopItem.setOnClickListener(new MyOnClickListener());

        buttonAllOff = (Button) mActivity.findViewById(R.id.buttonAllOff);
        buttonAllOff.setOnClickListener(new MyOnClickListener());

        buttonAllOn = (Button) mActivity.findViewById(R.id.buttonAllOn);
        buttonAllOn.setOnClickListener(new MyOnClickListener());

        buttonAddGroup = (Button) mActivity.findViewById(R.id.buttonAddGroup);
        buttonAddGroup.setOnClickListener(new MyOnClickListener());

        refreshView = (View) mActivity.findViewById(R.id.ivRefresh);
        refreshView.setOnClickListener(new MyOnClickListener());


        // 摇一摇
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { /* 新版 */
            AudioAttributes attributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
            soundPool = new SoundPool.Builder().setAudioAttributes(attributes).build();
        } else {
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
        }
        soundID = soundPool.load(this, R.raw.dang, 1);

        // 初始tab项
        sp = getSharedPreferences(Constant.MODLE_TYPE, Context.MODE_PRIVATE);
        editor = sp.edit();
        currentIndex = sp.getInt(Constant.MODLE_VALUE, 2);

        // ====连接后台service
        myServiceConenction = new MyServiceConenction();
        myServiceConenction.setServiceConnectListener(new ServiceConnectListener() {
            @Override
            public void onConnect(ComponentName name, IBinder service, BluetoothLeServiceSingle bLeService) {
                mBluetoothLeService = bLeService;// 获取连接实例
                // leftMenuFragment.setService(mBluetoothLeService);
                if (!mBluetoothLeService.initialize()) {
                    Log.e(LedBleApplication.tag, "Unable to initialize Bluetooth");
                } else {
                    Log.e(LedBleApplication.tag, "Initialize Bluetooth");
                }
            }

            @Override
            public void onDisConnect(ComponentName name) {

            }
        });

        Intent gattServiceIntent = new Intent(this, BluetoothLeServiceSingle.class);
        bindService(gattServiceIntent, myServiceConenction, Activity.BIND_AUTO_CREATE);

        ivType.setImageResource(R.drawable.tab_dim_check);
        ivType.setOnClickListener(new MyOnClickListener());
        ivLeftMenu.setOnClickListener(new MyOnClickListener());
        ivRightMenu.setOnClickListener(new MyOnClickListener());

        // buttonAllOff.setOnClickListener(new MyOnClickListener());
        // buttonAllOn.setOnClickListener(new MyOnClickListener());
        // buttonAddGroup.setOnClickListener(new MyOnClickListener());

        rgBottom.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbFirst:
                        currentIndex = 0;
                        segmentDm.setVisibility(View.VISIBLE);
                        segmentCt.setVisibility(View.GONE);
                        segmentRgb.setVisibility(View.GONE);
                        segmentMusic.setVisibility(View.GONE);
                        ivType.setVisibility(View.VISIBLE);
                        rbFirst.setVisibility(View.VISIBLE);
                        rbSecond.setVisibility(View.GONE);
                        rbThrid.setVisibility(View.GONE);
                        editor.putInt(Constant.MODLE_VALUE, currentIndex);
                        editor.commit();
                        pauseMusicAndVolum();
                        break;
                    case R.id.rbSecond:
                        currentIndex = 1;
                        segmentDm.setVisibility(View.GONE);
                        segmentCt.setVisibility(View.VISIBLE);
                        segmentRgb.setVisibility(View.GONE);
                        segmentMusic.setVisibility(View.GONE);
                        ivType.setVisibility(View.VISIBLE);
                        rbFirst.setVisibility(View.GONE);
                        rbSecond.setVisibility(View.VISIBLE);
                        rbThrid.setVisibility(View.GONE);
                        editor.putInt(Constant.MODLE_VALUE, currentIndex);
                        editor.commit();
                        pauseMusicAndVolum();
                        break;
                    case R.id.rbThrid:
                        currentIndex = 2;
                        segmentDm.setVisibility(View.GONE);
                        segmentCt.setVisibility(View.GONE);
                        segmentRgb.setVisibility(View.VISIBLE);
                        segmentMusic.setVisibility(View.GONE);
                        ivType.setVisibility(View.VISIBLE);
                        rbFirst.setVisibility(View.GONE);
                        rbSecond.setVisibility(View.GONE);
                        rbThrid.setVisibility(View.VISIBLE);
                        editor.putInt(Constant.MODLE_VALUE, currentIndex);
                        editor.commit();
                        pauseMusicAndVolum();
                        break;
                    case R.id.rbFourth:
                        currentIndex = 3;
                        segmentDm.setVisibility(View.GONE);
                        segmentCt.setVisibility(View.GONE);
                        segmentRgb.setVisibility(View.GONE);
                        segmentMusic.setVisibility(View.VISIBLE);
                        ivType.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.rbFifth:
                        currentIndex = 4;
                        segmentDm.setVisibility(View.GONE);
                        segmentCt.setVisibility(View.GONE);
                        segmentRgb.setVisibility(View.GONE);
                        segmentMusic.setVisibility(View.GONE);
                        ivType.setVisibility(View.INVISIBLE);
                        pauseMusicAndVolum();
                        break;
                }
                ManageFragment.showFragment(fragmentManager, fragmentList, currentIndex);
            }
        });

        if (currentIndex == 0) {
            ivType.setImageResource(R.drawable.tab_dim_check);
            rgBottom.check(R.id.rbFirst);
        } else if (currentIndex == 1) {
            ivType.setImageResource(R.drawable.tab_ct_check);
            rgBottom.check(R.id.rbSecond);
        } else if (currentIndex == 2) {
            ivType.setImageResource(R.drawable.tab_reg_check);
            rgBottom.check(R.id.rbThrid);
        }

        brightness = SharePersistent.getInt(this, brightnessKey);
        speed = SharePersistent.getInt(this, speedKey);


        initBleScanList(isAllOn);
    }

    /**
     * 初始化滑动菜单
     */
    private void initSlidingMenu() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        // mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
        // GravityCompat.START);
    }

    /**
     * 初始化fragment
     */
    public void initFragment() {
        fragmentList.add(new DmFragment());
        fragmentList.add(new CtFragment());
        fragmentList.add(new RgbFragment());
        musicFragment = new MusicFragment();
        fragmentList.add(musicFragment);
        fragmentList.add(new TimerFragment());
        // 添加Fragment
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        for (int i = 0; i < fragmentList.size(); i++) {
            transaction.add(R.id.flContent, fragmentList.get(i), fragmentList.get(i).getClass().getSimpleName());
        }
        transaction.commit();
        ManageFragment.showFragment(fragmentManager, fragmentList, currentIndex);
    }

    private void pauseMusicAndVolum() {
        musicFragment.pauseMusic();
        musicFragment.pauseVolum();
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean open() {
        NetConnectBle.getInstanceByGroup(groupName).turnOn();
        return false;
    }

    // =============================================更换皮肤=====================================================//

    public boolean close() {
        NetConnectBle.getInstanceByGroup(groupName).turnOff();
        return false;
    }

    /**
     * 全开
     */
    private void allOn() {
        NetConnectBle.getInstanceByGroup("").turnOn();
    }

    /**
     * 全关
     */
    private void allOff() {
        NetConnectBle.getInstanceByGroup("").turnOff();
    }

    /**
     * 刷新
     */
    protected void refreshDevices() {
        Tool.ToastShow(mActivity, R.string.refresh);
        disConnectAll();

//		mHandler.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				isStop = true;
//				// mScanning = false;
//				mBluetoothAdapter.stopLeScan(mLeScanCallback);
//			}
//		}, SCAN_PERIOD);
//		isStop = false;
        // mScanning = true;
        mBluetoothAdapter.startLeScan(mLeScanCallback);

        // disConnectAll();
//		 LedBleApplication.getApp().getBleDevices().clear();
//		 LedBleApplication.getApp().getBleGattMap().clear();
//		 hashMapConnect.clear();
//		 hashMapLock.clear();

        // mBluetoothAdapter.startLeScan(mLeScanCallback);
        // initBleScanList(true);

        // updateDevieConnect();
        // if (isStop) {
        // scanLeDevice(true);
        // }
    }

    /**
     * 添加组
     */
    private void addGroupMessage() {

        final EditText editText = new EditText(mActivity);
        new AlertDialog.Builder(mActivity).setTitle(R.string.please_input).setIcon(android.R.drawable.ic_dialog_info)
                .setView(editText).setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String gnameString = editText.getText().toString();

                GroupDeviceDao gDao = new GroupDeviceDao(mActivity);
                ArrayList<Group> groups = gDao.getAllgroup();
                for (Group group : groups) {
                    if (group.getGroupName().equalsIgnoreCase(gnameString)) { //不能添加相同组名的组
                        Tool.ToastShow(mActivity, R.string.groupname_cannot_same);
                        return;
                    }
                }

                if (!StringUtils.isEmpty(gnameString)) {
                    addGroupByName(gnameString);
                }
            }
        }).setNegativeButton(R.string.cancell_dialog, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    private void addGroupByName(String groupName) {
        try {
            // 添加组数据库
            GroupDeviceDao groupDeviceDao = new GroupDeviceDao(mActivity);
            groupDeviceDao.addGroup(groupName);
            addGroupView(groupName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addGroupView(final String groupname) {
        // 添加组视图
        final GroupView groupView = new GroupView(mActivity, groupname, isAllOn);
        final SlideSwitch slideSwitch = groupView.getSlideSwitch();
        linearGroups.addView(groupView.getGroupView());
        map.put(groupname, slideSwitch);
        slideSwitch.setStateNoListener(false);
        slideSwitch.setSlideListener(new SlideListener() {

            @Override
            public void open() {
                changeStatus(groupname);
            }

            @Override
            public void close() {
                slideSwitch.setStateNoListener(true);
            }
        });
        groupView.getGroupView().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//				groupName = groupname;
                if (groupView.isTurnOn()) {
                    if (groupView.getConnect() > 0) {
                        // gotoMain(groupName, groupView);
                    } else {
                        Tool.ToastShow(mActivity, R.string.edit_group_please);
//						gotoEdit(groupName);
                        showActionSheet(groupname);
                    }
                } else {
//					gotoEdit(groupName);
                    showActionSheet(groupname);
                }
            }
        });
        groupView.getGroupView().setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDeleteDialog(groupname);
                return true;
            }
        });

        arrayListGroupViews.add(groupView);
    }

    private void changeStatus(String groupName) {
        setGroupName(groupName);
        for (Iterator<String> i = map.keySet().iterator(); i.hasNext(); ) {
            String key = i.next();
            if (!groupName.equals(key)) {
                map.get(key).setStateNoListener(false);
            }
        }
    }


    // =============================================蓝牙操作=====================================================//

    /**
     * 编辑组
     *
     * @param groupName
     */
    private void gotoEdit(final String groupName) {
        Intent intent = new Intent(mActivity, DeviceListActivity.class);
        intent.putExtra("group", groupName);
        GroupDeviceDao groupDeviceDao = new GroupDeviceDao(mActivity);
        ArrayList<GroupDevice> devices = groupDeviceDao.getDevicesByGroup(groupName);
        if (!ListUtiles.isEmpty(devices)) {
            intent.putExtra("devices", devices);
        }
        startActivityForResult(intent, INT_GO_LIST);
    }

    /**
     * 更换皮肤相关 : 保存图片地址
     */
    private void saveImagePathToSharedPreferences(String imagePath) {

        SharedPreferences sharedPreferences = getSharedPreferences(Constant.IMAGE_VALUE, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString(Constant.IMAGE_VALUE, imagePath);
        editor.commit();
    }

    /**
     * 更换皮肤相关 : 打开相册
     */
    public void showPicturePicker(Context context) {

        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
        openAlbumIntent.setType("image/*");
        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
    }

    /**
     * 更换皮肤相关 : 获取保存过的图片路径
     */
    private String getImagePath() {
        sp = getSharedPreferences(Constant.IMAGE_VALUE, Context.MODE_PRIVATE);
        editor = sp.edit();
        String imagePath = sp.getString(Constant.IMAGE_VALUE, "");

        return imagePath;
    }

    /**
     * 更换皮肤相关 : 加载图片
     */
    private void showImage(String imaePath) {

        if (null != bm && !bm.isRecycled()) {
            bm.recycle();
            bm = null;
            System.gc();
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;//图片高宽度都为原来的二分之一，即图片大小为原来的大小的四分之一  
        options.inTempStorage = new byte[5 * 1024]; //设置16MB的临时存储空间（不过作用还没看出来，待验证
        Bitmap bitMap = BitmapFactory.decodeFile(imaePath, options);
        imageView.setImageBitmap(bitMap);

    }

    /**
     * 扫描设备
     */
    public void initBleScanList(boolean isAllon) {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        if (isInitGroup == false) {
            initGroup(isAllon);
            isInitGroup = true;
        }
        scanLeDevice(true);
    }

    /**
     * enable 暂时无用途
     *
     * @param enable
     */
    private void scanLeDevice(final boolean enable) {
        Tool.ToastShow(mActivity, R.string.refresh);
        if (enable) {
            refreshDevices();
        } else {
            isStop = true;
            // mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    private void disConnectAll() {
        String connected = getResources().getString(R.string.conenct_device, 0, 0);
        textViewAllDeviceIndicater.setText(connected);

        final Handler handler = new Handler(); // 开启 音乐  渐变  定时器
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                // 在此处添加执行的代码
                final String connect = getResources().getString(R.string.conenct_device, LedBleApplication.getApp().getBleDevices().size(), hashMapConnect.size());
                textViewAllDeviceIndicater.setText(connect);

                handler.removeCallbacks(this);// 关闭定时器处理
            }
        };
        handler.postDelayed(runnable, 2000);// 打开定时器，2秒后执行runnable
    }

    /**
     * 更新连接设备数
     */
    private void updateDevieConnect() {

        final String connected = getResources().getString(R.string.conenct_device, LedBleApplication.getApp().getBleDevices().size(), hashMapConnect.size());

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                textViewAllDeviceIndicater.setText(connected);
            }
        }, 2000);
        // } else {
        textViewAllDeviceIndicater.setText(connected);
        // }

        for (int i = 0, isize = arrayListGroupViews.size(); i < isize; i++) {
            GroupView gv = arrayListGroupViews.get(i);
            ArrayList<GroupDevice> gdes = gv.getGroupDevices();
            if (!ListUtiles.isEmpty(gdes)) {
                int connectCount = 0;
                for (GroupDevice groupDevice : gdes) {
                    String address = groupDevice.getAddress();
                    if (hashMapConnect.containsKey(address) && hashMapConnect.get(address)) {
                        connectCount++;
                    }
                }
                gv.setConnectCount(connectCount);
            }
        }
//        if (groupName.equalsIgnoreCase("")) {
//            textViewConnectCount.setText(Integer.toString(hashMapConnect.size()));
//        }
        textViewConnectCount.setText(Integer.toString(hashMapConnect.size()));
    }

    /**
     * 开始连接设备，设备的连接是异步的，必须等到一个设备连接成功后(发现service)才能连接新的设备
     */
    private void startConnectDevices(final BluetoothDevice device) {
        System.out.println("mBluetoothLeService:" + mBluetoothLeService);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String address = device.getAddress();
                final String name = device.getName();
                if (!LedBleApplication.getApp().getBleGattMap().containsKey(address) && null != mBluetoothLeService) {// 如果不存在，可以连接设备了
                    mBluetoothLeService.connect(address, name);
                    hashMapLock.put(address, false);// 上锁
                }
            }
        }).start();

    }

    /**
     * 开始连接设备，设备的连接是异步的，必须等到一个设备连接成功后(发现service)才能连接新的设备
     */
    private void startConnectDevices() {
        System.out.println("mBluetoothLeService:" + mBluetoothLeService);
        final int delayTime = 50 /* 500*/;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true && null != mBluetoothLeService) {
                    List<BluetoothDevice> bldevices = LedBleApplication.getApp().getBleDevices();
                    try {
                        for (BluetoothDevice bluetoothDevice : bldevices) {
                            final String address = bluetoothDevice.getAddress();
                            final String name = bluetoothDevice.getName();
                            if (!LedBleApplication.getApp().getBleGattMap().containsKey(address)
                                    && null != mBluetoothLeService) {// 如果不存在，可以连接设备了

                                mBluetoothLeService.connect(address, name);
                                hashMapLock.put(address, false);// 上锁

                                while (true) {// 如果已经解锁那就可以进行下一次连接了
                                    Tool.delay(delayTime);
                                    if (hashMapLock.get(address)) {
                                        break;
                                    }
                                }
                            }
                            Tool.delay(delayTime);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();// 防止出现并发修改异常
                        Log.e("fatal error ", e.toString());
                    }
                    Tool.delay(delayTime);
                }
            }
        }).start();

    }

    /**
     * 首次初始化组视图
     */
    private void initGroup(boolean isAllOn) {
        GroupDeviceDao gDao = new GroupDeviceDao(mActivity);
        ArrayList<Group> groups = gDao.getAllgroup();
        for (Group group : groups) {
            addGroupViewFromInit(group.getGroupName(), group.getIsOn(), isAllOn);
        }
    }

    private void addGroupViewFromInit(final String groupname, final String ison, final boolean isAllOn) {
        // 添加组视图
        final GroupView groupView = new GroupView(mActivity, groupname, isAllOn);

        GroupDeviceDao groupDeviceDao = new GroupDeviceDao(mActivity);
        final ArrayList<GroupDevice> groupDevices = groupDeviceDao.getDevicesByGroup(groupname);// 相同组的所有设备
        if (!ListUtiles.isEmpty(groupDevices)) {
            groupView.setGroupDevices(groupDevices);
        }

        final SlideSwitch slideSwitch = groupView.getSlideSwitch();
        map.put(groupname, slideSwitch);
        slideSwitch.setStateNoListener(false);
        slideSwitch.setSlideListener(new SlideListener() {

            @Override
            public void open() {
                changeStatus(groupname);
            }

            @Override
            public void close() {
                slideSwitch.setStateNoListener(true);
            }
        });

        groupView.getGroupView().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//				groupName = groupname;
                if (groupView.isTurnOn()) {
                    if (groupView.getConnect() > 0) {
                        // gotoMain(groupName, groupView);
                    } else {
                        Tool.ToastShow(mActivity, R.string.edit_group_please);
//						gotoEdit(groupName);
                        showActionSheet(groupname);
                    }
                } else {
//					gotoEdit(groupName);
                    showActionSheet(groupname);
                }
            }
        });

        linearGroups.addView(groupView.getGroupView());
        groupView.getGroupView().setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDeleteDialog(groupname);
                return true;
            }
        });

        if ("y".equalsIgnoreCase(ison) && isAllOn) {
            groupView.turnOn();
        } else {
            groupView.turnOff();
        }
        arrayListGroupViews.add(groupView);
    }

    private void showDeleteDialog(final String groupName) {

        Dialog alertDialog = new AlertDialog.Builder(mActivity).setTitle(getResources().getString(R.string.tips))
                .setMessage(getResources().getString(R.string.delete_group, groupName))
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        try {
                            GroupDeviceDao gDao = new GroupDeviceDao(mActivity);
                            gDao.deleteGroup(groupName);
                            gDao.delteByGroup(groupName);
                            linearGroups.removeView(linearGroups.findViewWithTag(groupName));
                            map.remove(groupName);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).setNegativeButton(R.string.cancell_dialog, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                }).create();

        alertDialog.show();

    }

    /**
     * 保存设备到某个组
     *
     * @param groupName
     * @param selectSet
     * @throws Exception
     */
    private void save2GroupByGroupName(String groupName, Set<BluetoothDevice> selectSet) throws Exception {
        GroupDeviceDao groupDeviceDao = new GroupDeviceDao(mActivity);
        // 删除原来组的设备，重新添加新的设备
        groupDeviceDao.delteByGroup(groupName);// 删除原有的设备
        ArrayList<GroupDevice> groupDevices = new ArrayList<GroupDevice>();
        for (BluetoothDevice bluetoothDevice : selectSet) {
            GroupDevice object = new GroupDevice();
            object.setAddress(bluetoothDevice.getAddress());
            object.setGroupName(groupName);
            groupDevices.add(object);
        }
        groupDeviceDao.save2Group(groupDevices);
    }

    /**
     * 计算组中已经连接的设备
     *
     * @param groupDevices
     * @return
     */
    private int findConnectCount(ArrayList<GroupDevice> groupDevices) {
        if (ListUtiles.isEmpty(groupDevices)) {
            return 0;
        }
        int connectCount = 0;
        for (GroupDevice groupDevice : groupDevices) {
            if (hashMapConnect.containsKey(groupDevice.getAddress()) && hashMapConnect.get(groupDevice.getAddress())) {
                connectCount++;
            }
        }
        return connectCount;
    }

    public void showActionSheet(String groupname) {

        NetConnectBle.getInstanceByGroup(groupname);

        if (groupname.equalsIgnoreCase("")) {
            Item item1 = new Item(R.color.white, R.color.white, R.drawable.tab_ct, R.drawable.tab_ct, R.color.colorPrimary, R.color.white, getResources().getString(R.string.control));
            Item cancelItem = new Item(R.color.white, R.color.white, 0, 0, R.color.colorPrimary, R.color.white, getResources().getString(R.string.text_cancel));

            ActionSheet.createBuilder(this, this.getFragmentManager()).setCancelItem(cancelItem)
                    .setmOtherItems(item1).setGroupName(groupname).setCancelableOnTouchOutside(true).setListener(this).show();
        } else {
            Item item1 = new Item(R.color.white, R.color.white, R.drawable.tab_ct, R.drawable.tab_ct, R.color.colorPrimary, R.color.white, getResources().getString(R.string.control));
            Item item2 = new Item(R.color.white, R.color.white, R.drawable.tab_ct, R.drawable.tab_ct, R.color.colorPrimary, R.color.white, getResources().getString(R.string.add_device));
            Item cancelItem = new Item(R.color.white, R.color.white, 0, 0, R.color.colorPrimary, R.color.white, getResources().getString(R.string.text_cancel));

            ActionSheet.createBuilder(this, this.getFragmentManager()).setCancelItem(cancelItem)
                    .setmOtherItems(item1, item2).setGroupName(groupname).setCancelableOnTouchOutside(true).setListener(this).show();
        }
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
        // TODO Auto-generated method stub
//		Toast.makeText(getApplicationContext(), "dismissed isCancle = " + isCancel, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index, String groupName) {
        // TODO Auto-generated method stub
        switch (index) {
            case 0:
//			Toast.makeText(getApplicationContext(), "控制的组 = "+groupName, Toast.LENGTH_SHORT).show();
                if (hashMapConnect.size() > 0) {
                    this.groupName = groupName;
                    if (groupName.equalsIgnoreCase("")) {
                        textViewConnectCount.setText(Integer.toString(hashMapConnect.size()));
                    } else {
                        textViewConnectCount.setText(Integer.toString(NetConnectBle.getInstance().getDevicesByGroup().size()));
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_device_found, Toast.LENGTH_SHORT).show();
                }

                break;
            case 1:
                gotoEdit(groupName);
                break;

            default:
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == INT_GO_LIST) {
            try {
                String grop = data.getStringExtra("group");
                save2GroupByGroupName(grop, LedBleApplication.getApp().getTempDevices());// 保存新的组到数据库
                // ==========
                GroupDeviceDao groupDeviceDao = new GroupDeviceDao(mActivity);
                ArrayList<GroupDevice> list = groupDeviceDao.getDevicesByGroup(grop);

                for (GroupView groupView : arrayListGroupViews) {
                    if (grop.equals(groupView.getGroupName())) {
                        // 设置已经连接的设备数量
                        groupView.setGroupDevices(list);
                        int count = findConnectCount(list);
                        LogUtil.i(LedBleApplication.tag, "count:" + count);
                        groupView.setConnectCount(count);
                        break;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                Tool.ToastShow(mActivity, "保存失败！");
            }
        } else {
            if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case TAKE_PICTURE:
                        break;
                    case CHOOSE_PICTURE: // 更换皮肤
                        ContentResolver resolver = getContentResolver();
                        // 照片的原始资源地址
                        if (null != data) {
                            Uri originalUri = data.getData();
                            if (null != originalUri) {
                                try {

                                    if (null != bm && !bm.isRecycled()) { //回收图片资源
                                        bm.recycle();
                                        bm = null;
                                        System.gc();
                                    }

                                    // 使用ContentProvider通过URI获取原始图片
                                    bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                                    if (bm != null) {

                                        String img_path = getRealPathFromUri(mActivity, originalUri);// 这是本机的图片路径
                                        showImage(img_path);

                                        saveImagePathToSharedPreferences(img_path);// 保存图片
                                    }
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }


                        break;

                    default:
                        break;
                }
            }

        }

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int sensorType = sensorEvent.sensor.getType();
        float[] values = sensorEvent.values;

        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            if ((Math.abs(values[0]) > 19) || Math.abs(values[1]) > 19 || Math.abs(values[2]) > 19) {

                switch (shakeStyle) {
                    case 0:
                        soundPool.play(soundID, 1, 1, 0, 0, 1);
                        setRgb(random.nextInt(255) + 1, random.nextInt(255) + 1, random.nextInt(255) + 1);
                        break;
                    case 1:

                        break;
                    case 2:
                        soundPool.play(soundID, 1, 1, 0, 0, 1);
                        setRegMode(random.nextInt(156 - 135 + 1) + 135);
                        break;

                    default:
                        break;
                }
            }
        }
    }

    public void setService(BluetoothLeServiceSingle service) {
        this.mBluetoothLeService = service;
    }

    // ============业务模式===================

    public void turnOnOffTimerOn(int onOrOff) {
        // if (isLightOpen == false) {
        // return;
        // }
        NetConnectBle.getInstanceByGroup(groupName).turnOnOffTimerOn(onOrOff);
    }

    public void timerOn(int hour, int minute, int model) {
        // if (isLightOpen == false) {
        // return;
        // }
        NetConnectBle.getInstanceByGroup(groupName).timerOn(hour, minute, model);
    }

    public void turnOnOffTimerOff(int onOrOff) {
        // if (isLightOpen == false) {
        // return;
        // }
        NetConnectBle.getInstanceByGroup(groupName).turnOnOrOffTimerOff(onOrOff);
    }

    public void timerOff(int hour, int minute) {
        // if (isLightOpen == false) {
        // return;
        // }
        NetConnectBle.getInstanceByGroup(groupName).timerOff(hour, minute);
    }

    public void setRgb(int r, int g, int b) {
        // if (isLightOpen == false) {
        // return;
        // }
        NetConnectBle.getInstanceByGroup(groupName).setRgb(r, g, b);
    }

    public void setDim(int dim) {
        // if (isLightOpen == false) {
        // return;
        // }
        NetConnectBle.getInstanceByGroup(groupName).setDim(dim);
    }

    public void setDimModel(int dimModel) {
        // if (isLightOpen == false) {
        // return;
        // }
        NetConnectBle.getInstanceByGroup(groupName).setDimModel(dimModel);
    }

    public void setRegMode(int model) {
        // if (isLightOpen == false) {
        // return;
        // }
        NetConnectBle.getInstanceByGroup(groupName).setRgbMode(model);
    }

    public void setSpeed(int speed) {
        // if (isLightOpen == false) {
        // return;
        // }
        this.speed = speed;
        SharePersistent.savePerference(this, speedKey, this.speed);
        NetConnectBle.getInstanceByGroup(groupName).setSpeed(speed);
    }

    public void setDiy(ArrayList<MyColor> colors, int style) {
        // if (isLightOpen == false) {
        // return;
        // }
        NetConnectBle.getInstanceByGroup(groupName).setDiy(colors, style);
    }

    public void setBrightNess(int brightness) {
        // if (isLightOpen == false) {
        // return;
        // }
        this.brightness = brightness;// 设置亮度参数
        SharePersistent.savePerference(this, brightnessKey, this.brightness);
        NetConnectBle.getInstanceByGroup(groupName).setBrightness(brightness);

    }

    public void setCT(int warm, int cool) {
        // if (isLightOpen == false) {
        // return;
        // }
        NetConnectBle.getInstanceByGroup(groupName).setColorWarm(warm, cool);
    }

    public void setCTModel(int model) {
        // if (isLightOpen == false) {
        // return;
        // }
        NetConnectBle.getInstanceByGroup(groupName).setColorWarmModel(model);
    }

    // 律动 模式
    public void setDynamicModel(int model) {
        // if (isLightOpen == false) {
        // return;
        // }
        NetConnectBle.getInstanceByGroup(groupName).setDynamicModel(model);
    }

    @Override
    public void onException(Exception e) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unbindService(myServiceConenction);
        unregisterReceiver(mGattUpdateReceiver);
//		mActivity.unregisterReceiver(mGattUpdateReceiver);
        hashMapConnect = null;
        hashMapLock = null;
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (isBackground(mActivity)) { //app进入后台停止扫描
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
//			Toast.makeText(mActivity, "当前为后台", Toast.LENGTH_SHORT).show();
        } else {

//			Toast.makeText(mActivity, "当前为前台", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//		MobclickAgent.onResume(mActivity);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mBluetoothAdapter.startLeScan(mLeScanCallback);
//		Toast.makeText(mActivity, "从后台切换回前台", Toast.LENGTH_SHORT).show();
    }

    public SegmentedRadioGroup getSegmentDm() {
        return segmentDm;
    }

    public SegmentedRadioGroup getSegmentCt() {
        return segmentCt;
    }

    public SegmentedRadioGroup getSegmentRgb() {
        return segmentRgb;
    }

    public SegmentedRadioGroup getSegmentMusic() {
        return segmentMusic;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    public class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ivLeftMenu:
                    mDrawerLayout.openDrawer(left_menu);
                    break;
                case R.id.ivRightMenu:
                    mDrawerLayout.openDrawer(right_menu);
                    break;
                case R.id.linearLayoutTopItem:
                    showActionSheet("");
                    break;
                case R.id.ivType:
                    if (currentIndex == 0) {
                        ivType.setImageResource(R.drawable.tab_ct_check);
                        rgBottom.check(R.id.rbSecond);
                    } else if (currentIndex == 1) {
                        ivType.setImageResource(R.drawable.tab_reg_check);
                        rgBottom.check(R.id.rbThrid);
                    } else if (currentIndex == 2) {
                        ivType.setImageResource(R.drawable.tab_dim_check);
                        rgBottom.check(R.id.rbFirst);
                    }
                    break;

                case R.id.buttonAllOff:
                    // Toast.makeText(mActivity, "全关", Toast.LENGTH_SHORT).show();
                    allOff();
                    break;
                case R.id.buttonAllOn:
                    allOn();
                    break;
                case R.id.buttonAddGroup:
                    addGroupMessage();
                    break;
                case R.id.ivRefresh:// 刷新
                    refreshDevices();
                    break;

                case R.id.dynamic_tv:// 律动
                    startActivity(new Intent(mActivity, DynamicColorActivity.class));
                    break;
                case R.id.dynamic_gradient_iv: // 律动  渐变
                    setDynamicModel(128);
                    Toast.makeText(mActivity, R.string.gradient, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.dynamic_breath_iv: // 渐变	呼吸
                    setDynamicModel(129);
                    Toast.makeText(mActivity, R.string.breathe, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.dynamic_jump_iv: // 律动	跳变
                    setDynamicModel(130);
                    Toast.makeText(mActivity, R.string.jump, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.dynamic_strobe_iv: // 律动 	频闪
                    setDynamicModel(131);
                    Toast.makeText(mActivity, R.string.flash, Toast.LENGTH_SHORT).show();
                    break;


                case R.id.shake_one_iv:// 摇一摇	颜色
                    Toast.makeText(mActivity, R.string.shake_random_color, Toast.LENGTH_SHORT).show();
                    shakeStyle = 0;
                    break;
                case R.id.shake_two_iv:// 摇一摇	NONE
                    Toast.makeText(mActivity, R.string.shake_random_none, Toast.LENGTH_SHORT).show();
                    shakeStyle = 1;
                    break;
                case R.id.shake_three_iv:// 摇一摇		模式
                    Toast.makeText(mActivity, R.string.shake_random_mode, Toast.LENGTH_SHORT).show();
                    shakeStyle = 2;
                    break;


                case R.id.operation_guide_tv: // 操作指南
                    Intent intent = new Intent(mActivity, OprationManualActivity.class);
                    startActivity(intent);
                    break;
                case R.id.change_under_pic_tv: // 更换皮肤
                    showPicturePicker(MainActivity.this);
                    break;
                case R.id.reset_tv: // 一键还原
                    Drawable drawable = getResources().getDrawable(R.drawable.bg_all);
                    imageView.setImageDrawable(drawable);

                    String imagePath = getImagePath();
                    imagePath = "";
                    saveImagePathToSharedPreferences(imagePath);// 保存图片

                    break;
            }
        }
    }

}
