package com.ledble.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.common.uitl.ListUtiles;
import com.common.uitl.LogUtil;
import com.common.uitl.SharePersistent;
import com.common.uitl.StringUtils;
import com.common.uitl.Tool;
//import com.feicanled.ble.App;
import com.ledble.R;
import com.ledble.activity.DeviceListActivity;
import com.ledble.activity.MainActivity;
import com.ledble.base.LedBleApplication;
import com.ledble.base.LedBleFragment;
import com.ledble.db.Group;
import com.ledble.db.GroupDevice;
import com.ledble.db.GroupDeviceDao;
import com.ledble.net.NetConnectBle;
import com.ledble.service.BluetoothLeServiceSingle;
import com.ledble.view.GroupView;
import com.ledble.view.SlideSwitch;
import com.ledble.view.SlideSwitch.SlideListener;
/**
 * 左侧菜单
 * @author ftl
 *
 */
public class LeftMenuFragment extends LedBleFragment {
	
//	@Bind(R.id.llExit) LinearLayout llExit;
	
	private Button buttonAddGroup;
	private LinearLayout linearGroups;
	private Button buttonAllOn, buttonAllOff;
	private ArrayList<GroupView> arrayListGroupViews;
	private ToggleButton toggleButtonDefault;

	private BluetoothLeServiceSingle mBluetoothLeService;
//  private BleDeviceAdapter mLeDeviceListAdapter;
	private BluetoothAdapter mBluetoothAdapter;
//	private boolean mScanning;
	private Handler mHandler;

	private static final int REQUEST_ENABLE_BT = 1;
	private static final long SCAN_PERIOD = 10000;
	private BluetoothManager bluetoothManager;
	private boolean isInitGroup = false;
	private int INT_GO_LIST = 111;

	private final int MSG_START_CONNECT = 10000;// 开始连接
	private ArrayList<String> macAddr /*= new List<string>()*/;


	private volatile HashMap<String, Boolean> hashMapLock;// service连接锁
	private volatile HashMap<String, Boolean> hashMapConnect;

	private TextView textViewAllDeviceIndicater;// 显示所有设备
	private ImageView imageViewControll;
	private String allOnStringKey = "isAllon";
	private boolean isAllOn = true;
	private boolean isStop = true;
	private boolean isFirst = true;

	private View mContentView;
	private SlideSwitch slideSwitch;
	private Map<String, SlideSwitch> map = new HashMap<>();
	private MainActivity mActivity;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,  Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.fragment_left_menu, container, false);
		return mContentView;
	}

	@Override
	public void initData() {
		mActivity = (MainActivity) getActivity();
		activity.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		// 是否支持蓝牙
		if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(activity, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
			Tool.exitApp();
		}

		mHandler = new Handler();
		arrayListGroupViews = new ArrayList<GroupView>();
		macAddr = new ArrayList<String>();


		hashMapLock = new HashMap<String, Boolean>();
		hashMapConnect = new HashMap<String, Boolean>();

		this.imageViewControll = (ImageView) mContentView.findViewById(R.id.imageViewControll);
		this.textViewAllDeviceIndicater = (TextView) mContentView.findViewById(R.id.textViewAllDeviceIndicater);
		this.linearGroups = (LinearLayout) mContentView.findViewById(R.id.linearLayoutDefineGroups);

		this.toggleButtonDefault = (ToggleButton) mContentView.findViewById(R.id.toggleButtonDefault);
//		this.isAllOn = SharePersistent.getBoolean(activity, allOnStringKey);
		this.toggleButtonDefault.setChecked(this.isAllOn);
		this.imageViewControll.setVisibility((isAllOn ? View.VISIBLE : View.INVISIBLE));// 控制是否可以点击进入控制

		this.toggleButtonDefault.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				isAllOn = isChecked;
				SharePersistent.saveBoolean(activity, allOnStringKey, isAllOn);// 保存开关
				if (isChecked) {
					allOn();
				} else {
					allOff();
				}
				imageViewControll.setVisibility((isAllOn ? View.VISIBLE : View.INVISIBLE));
			}
		});

		this.buttonAllOff = (Button) mContentView.findViewById(R.id.buttonAllOff);
		this.buttonAllOff.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				allOff();
			}
		});
		this.buttonAllOn = (Button) mContentView.findViewById(R.id.buttonAllOn);
		this.buttonAllOn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				allOn();
			}
		});

		buttonAddGroup = (Button) mContentView.findViewById(R.id.buttonAddGroup);
		buttonAddGroup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addGroupMessage();
			}
		});

		// 刷新
		mContentView.findViewById(R.id.ivRefresh).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				refreshDevices();
			}
		});

//		findButtonById(R.id.buttonBack).setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Tool.startActivity(SettingActivity.this, HelpActivity.class);
//			}
//		});

		bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();

		// 不能获得蓝牙设备支持
		if (mBluetoothAdapter == null) {
			Tool.ToastShow(activity, R.string.ble_not_supported);
			Tool.exitApp();
		}
		slideSwitch = (SlideSwitch) mContentView.findViewById(R.id.slideSwitch);
		map.put("", slideSwitch);
		slideSwitch.setStateNoListener(true);
		slideSwitch.setSlideListener(new SlideListener() {
			
			@Override
			public void open() {
				if (arrayListGroupViews.size() > 0) {
					changeStatus("");
				}
			}
			
			@Override
			public void close() {
//				if (arrayListGroupViews.size() == 0) {
					slideSwitch.setStateNoListener(true);
//				} 
			}
		});

		initBleScanList(isAllOn);
	}

	@Override
	public void initView() {
		
	}

	@Override
	public void initEvent() {
		
	}
	
	@Override
	public void onDestroy() {
		activity.unregisterReceiver(mGattUpdateReceiver);
		hashMapLock = null;
		hashMapConnect = null;
		super.onDestroy();
	}
	
//    @OnClick(value = {R.id.llExit})
//    public void onClick (View v) {
//    	switch (v.getId()) {
//    	case R.id.llExit:
//    		baseApp.exit();
//    		break;
//    	}
//    }
	
	protected void refreshDevices() {
		Tool.ToastShow(activity, R.string.refresh);
		
//		disConnectAll();
//		LedBleApplication.getApp().getBleDevices().clear();
//		LedBleApplication.getApp().getBleGattMap().clear();
//		hashMapConnect.clear();
//		hashMapLock.clear();
		
//		mBluetoothAdapter.startLeScan(mLeScanCallback);
		initBleScanList(true);
		mBluetoothAdapter.startLeScan(mLeScanCallback);
		
//		updateDevieConnect();
//		if (isStop) {
//			scanLeDevice(true);
//		}
	}
	
	private BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			System.out.println("接收廣播action-->" + action);
			if (BluetoothLeServiceSingle.ACTION_GATT_CONNECTED.equals(action)) {// 连接到设备
				String address = intent.getStringExtra("address");
				hashMapConnect.put(address, true);
				updateDevieConnect();
				LogUtil.i(LedBleApplication.tag, "connect:" + address);

			} else if (BluetoothLeServiceSingle.ACTION_GATT_DISCONNECTED.equals(action)) {// 断开设备
				
				isFirst = false;

				String address = intent.getStringExtra("address");
				// hashMapConnect.put(address,false);//删除连接计数

				hashMapConnect.remove(address);
				LedBleApplication.getApp().removeDisconnectDevice(address);// 删除断开的device
				LedBleApplication.getApp().getBleGattMap().remove(address);// 删除断开的blgatt
//				hashMapLock.remove(address);// 删除锁
				updateDevieConnect();
				LogUtil.i(LedBleApplication.tag, "disconnect:" + address + " connected devices:" + LedBleApplication.getApp().getBleDevices().size());
				
				
				mBluetoothAdapter.startLeScan(mLeScanCallback); //开始搜索 


			} else if (BluetoothLeServiceSingle.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {// 发现service,就可以获取Characteristic
				// 发现之后依次连接
				String address = intent.getStringExtra("address");
				BluetoothGatt blgat = mBluetoothLeService.getBluetoothGatt();
				// 控制连接要求的设备
//				BluetoothGattService service32 = blgat.getService(UUID.fromString(NetConnectBle.serviceid32));
//				BluetoothGattService service33 = blgat.getService(UUID.fromString(NetConnectBle.serviceid33));
//				BluetoothGattService service34 = blgat.getService(UUID.fromString(NetConnectBle.serviceid34));
//				if (service32 != null || service33 != null || service34 != null) {
					LedBleApplication.getApp().getBleGattMap().put(address, blgat);
					hashMapLock.put(address, true);// 解锁
					LogUtil.i(LedBleApplication.tag, "发现service" + intent.getStringExtra("address"));
//				}
			} else if (BluetoothLeServiceSingle.ACTION_DATA_AVAILABLE.equals(action)) {// 读取到数据，不做处理，本应用不需要读取数据
				// displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
			}
		}
	};

	/**
	 * 首次初始化组视图
	 */
	private void initGroup(boolean isAllOn) {
		GroupDeviceDao gDao = new GroupDeviceDao(activity);
		ArrayList<Group> groups = gDao.getAllgroup();
		for (Group group : groups) {
			addGroupViewFromInit(group.getGroupName(), group.getIsOn(), isAllOn);
		}
	}

	/**
	 * 全开
	 */
	private void allOn() {
//		for (GroupView groupView : arrayListGroupViews) {
//			groupView.getToggleButton().setEnabled(true);
//			groupView.turnOn();
//		}
//		toggleButtonDefault.setChecked(true);
		NetConnectBle.getInstanceByGroup("").turnOn();
	}

	/**
	 * 全关
	 */
	private void allOff() {
//		for (GroupView groupView : arrayListGroupViews) {
//			groupView.turnOff();
//			groupView.getToggleButton().setEnabled(false);
//		}
//		toggleButtonDefault.setChecked(false);
		NetConnectBle.getInstanceByGroup("").turnOff();
	}

	private void addGroupMessage() {

		final EditText editText = new EditText(activity);
		new AlertDialog.Builder(activity).setTitle(R.string.please_input).setIcon(android.R.drawable.ic_dialog_info)
				.setView(editText).setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						String gnameString = editText.getText().toString();
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
			GroupDeviceDao groupDeviceDao = new GroupDeviceDao(activity);
			groupDeviceDao.addGroup(groupName);
			addGroupView(groupName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addGroupView(final String groupName) {
		// 添加组视图
		final GroupView groupView = new GroupView(activity, groupName, isAllOn);
		final SlideSwitch slideSwitch = groupView.getSlideSwitch();
		linearGroups.addView(groupView.getGroupView());
		map.put(groupName, slideSwitch);
		slideSwitch.setStateNoListener(false);
		slideSwitch.setSlideListener(new SlideListener() {
			
			@Override
			public void open() {
				changeStatus(groupName);
			}
			
			@Override
			public void close() {
				slideSwitch.setStateNoListener(true);
			}
		});
		groupView.getGroupView().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (groupView.isTurnOn()) {
					if (groupView.getConnect() > 0) {
						//gotoMain(groupName, groupView);
					} else {
						Tool.ToastShow(activity, R.string.edit_group_please);
						gotoEdit(groupName);
					}
				} else {
					gotoEdit(groupName);
				}
			}
		});
		groupView.getGroupView().setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				showDeleteDialog(groupName);
				return true;
			}
		});

		arrayListGroupViews.add(groupView);
	}
	
	private void changeStatus(String groupName) {
		mActivity.setGroupName(groupName);
		for (Iterator<String> i = map.keySet().iterator(); i.hasNext(); ) {  
            String key = i.next();   
            if (!groupName.equals(key)) {
            	map.get(key).setStateNoListener(false);
            }
        }  
	}

	private void addGroupViewFromInit(final String groupName, final String ison, final boolean isAllOn) {
		// 添加组视图
		final GroupView groupView = new GroupView(activity, groupName, isAllOn);

		GroupDeviceDao groupDeviceDao = new GroupDeviceDao(activity);
		final ArrayList<GroupDevice> groupDevices = groupDeviceDao.getDevicesByGroup(groupName);// 相同组的所有设备
		if (!ListUtiles.isEmpty(groupDevices)) {
			groupView.setGroupDevices(groupDevices);
		}
		
		final SlideSwitch slideSwitch = groupView.getSlideSwitch();
		map.put(groupName, slideSwitch);
		slideSwitch.setStateNoListener(false);
		slideSwitch.setSlideListener(new SlideListener() {
			
			@Override
			public void open() {
				changeStatus(groupName);
			}
			
			@Override
			public void close() {
				slideSwitch.setStateNoListener(true);
			}
		});

		groupView.getGroupView().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (groupView.isTurnOn()) {
					if (groupView.getConnect() > 0) {
						//gotoMain(groupName, groupView);
					} else {
						Tool.ToastShow(activity, R.string.edit_group_please);
						gotoEdit(groupName);
					}
				} else {
					gotoEdit(groupName);
				}
			}
		});

		linearGroups.addView(groupView.getGroupView());
		groupView.getGroupView().setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				showDeleteDialog(groupName);
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

	/**
	 * 编辑组
	 * 
	 * @param groupName
	 */
	private void gotoEdit(final String groupName) {
		Intent intent = new Intent(activity, DeviceListActivity.class);
		intent.putExtra("group", groupName);
		GroupDeviceDao groupDeviceDao = new GroupDeviceDao(activity);
		ArrayList<GroupDevice> devices = groupDeviceDao.getDevicesByGroup(groupName);
		if (!ListUtiles.isEmpty(devices)) {
			intent.putExtra("devices", devices);
		}
		startActivityForResult(intent, INT_GO_LIST);
	}

	/**
	 * enable 暂时无用途
	 * 
	 * @param enable
	 */
	private void scanLeDevice(final boolean enable) {
		Tool.ToastShow(activity, R.string.refresh);
		if (enable) {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					isStop = true;
//					mScanning = false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
				}
			}, SCAN_PERIOD);
			isStop = false;
//			mScanning = true;
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			isStop = true;
//			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
	}

	/**
	 * 更新发现新的设备
	 */
	private void updateNewFindDevice() {
		String connected = getResources().getString(R.string.conenct_device, LedBleApplication.getApp().getBleDevices().size(), hashMapConnect.size());
		textViewAllDeviceIndicater.setText(connected);
		updateDevieConnect();
	}

	/**
	 * 更新连接设备数
	 */
	private void updateDevieConnect() {

		final String connected = getResources().getString(R.string.conenct_device, LedBleApplication.getApp().getBleDevices().size(), hashMapConnect.size());
		if (hashMapConnect.size() > 0) {
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					textViewAllDeviceIndicater.setText(connected);
				}
			}, 3000);
		} else {
			textViewAllDeviceIndicater.setText(connected);
		}

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

	}

	/**
	 * 成功扫描设备
	 */
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {					
					if (device != null) {
						if (!LedBleApplication.getApp().getBleDevices().contains(device) && device.getName() != null) {
							String name = device.getName();
							
							
//							if (isFirst) {
//								if (macAddr.size() < 1) {
//									macAddr.add(device.getAddress());
//								}else {
//									if (!macAddr.contains(device.getAddress())) {
//										macAddr.add(device.getAddress());
//									}
//								}
//							}
											
							
//							if (macAddr.contains(device.getAddress())) {
//								if (name.startsWith(BluetoothLeServiceSingle.NAME_START_ELK) 
//										|| name.startsWith(BluetoothLeServiceSingle.NAME_START_LED)
//										|| name.startsWith(BluetoothLeServiceSingle.NAME_START_TV)) {
//									LedBleApplication.getApp().getBleDevices().add(device);
//									updateNewFindDevice();
//									LogUtil.i(LedBleApplication.tag, "++++++++++++++++++++++++++++++++++++++++++++++++");
//									LogUtil.i(LedBleApplication.tag, "发现新设备：" + device.getAddress() + " total:" + LedBleApplication.getApp().getBleDevices().size());
//									conectHandler.sendEmptyMessage(MSG_START_CONNECT);// 可以开始连接设备了
//								}
//							}

							
							
							if (name.startsWith(BluetoothLeServiceSingle.NAME_START_ELK) 
									|| name.startsWith(BluetoothLeServiceSingle.NAME_START_LED)
									|| name.startsWith(BluetoothLeServiceSingle.NAME_START_TV)) {
								LedBleApplication.getApp().getBleDevices().add(device);
								updateNewFindDevice();
								LogUtil.i(LedBleApplication.tag, "发现新设备：" + device.getAddress() + " total:" + LedBleApplication.getApp().getBleDevices().size());
								conectHandler.sendEmptyMessage(MSG_START_CONNECT);// 可以开始连接设备了
							}
							
						}
					}
				}
			});

		}
	};

	private boolean booleanCanStart = false;
	private Handler conectHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case MSG_START_CONNECT:// 可以开始连接了
				if (booleanCanStart == false) {
					booleanCanStart = true;
					startConnectDevices();
				}
				break;

			default:
				break;
			}
		};
	};

	/**
	 * 开始连接设备，设备的连接是异步的，必须等到一个设备连接成功后(发现service)才能连接新的设备
	 */
	private void startConnectDevices() {
		System.out.println("mBluetoothLeService:" + mBluetoothLeService);
		final int delayTime = /*50*/500;
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true && null != mBluetoothLeService) {
					ArrayList<BluetoothDevice> bldevices = LedBleApplication.getApp().getBleDevices();
					try {
						for (BluetoothDevice bluetoothDevice : bldevices) {
							final String address = bluetoothDevice.getAddress();
							final String name = bluetoothDevice.getName();
							if (!LedBleApplication.getApp().getBleGattMap().containsKey(address) && null != mBluetoothLeService) {// 如果不存在，可以连接设备了
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
					}
					Tool.delay(delayTime);
				}
			}
		}).start();

	}

	/**
	 * 扫描设备
	 */
	public void initBleScanList(boolean isAllon) {
		if (!mBluetoothAdapter.isEnabled()) {
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}

		if (isInitGroup == false) {
			initGroup(isAllon);
			isInitGroup = true;
		}
		scanLeDevice(true);
	}

	/**
	 * 保存设备到某个组
	 * 
	 * @param groupName
	 * @param selectSet
	 * @throws Exception
	 */
	private void save2GroupByGroupName(String groupName, Set<BluetoothDevice> selectSet) throws Exception {
		GroupDeviceDao groupDeviceDao = new GroupDeviceDao(activity);
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

	private void showDeleteDialog(final String groupName) {

		Dialog alertDialog = new AlertDialog.Builder(activity).setTitle(getResources().getString(R.string.tips))
				.setMessage(getResources().getString(R.string.delete_group, groupName))
				.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						try {
							GroupDeviceDao gDao = new GroupDeviceDao(activity);
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK && requestCode == INT_GO_LIST) {
			try {
				String grop = data.getStringExtra("group");
				save2GroupByGroupName(grop, LedBleApplication.getApp().getTempDevices());// 保存新的组到数据库
				// ==========
				GroupDeviceDao groupDeviceDao = new GroupDeviceDao(activity);
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
				Tool.ToastShow(activity, "保存失败！");
			}
		}

	}

	public void setService(BluetoothLeServiceSingle service) {
		this.mBluetoothLeService = service;
	}
	
	private void disConnectAll() {
		String connected = getResources().getString(R.string.conenct_device, 0, 0);
		textViewAllDeviceIndicater.setText(connected);
	} 

}
