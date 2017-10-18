package com.ledble.net;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Handler;

import com.common.uitl.ListUtiles;
import com.common.uitl.LogUtil;
import com.common.uitl.NumberHelper;
import com.common.uitl.StringUtils;
import com.common.uitl.Tool;
import com.ledble.base.LedBleApplication;
import com.ledble.bean.MyColor;
import com.ledble.db.GroupDevice;
import com.ledble.db.GroupDeviceDao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

@SuppressLint("NewApi")
public class NetConnectBle {

	private NetExceptionInterface exceptionCallBack;

	//发布特征值
	public static final String serviceid32 = "0000ffe5-0000-1000-8000-00805f9b34fb";
	public static final String characid32 = "0000ffe9-0000-1000-8000-00805f9b34fb";
	
	public static final String serviceid33 = "0000ffe0-0000-1000-8000-00805f9b34fb";
	public static final String characid33 = "0000ffe1-0000-1000-8000-00805f9b34fb";
	
	public static final String serviceid34 = "0000fff0-0000-1000-8000-00805f9b34fb";
	public static final String characid34 = "0000fff3-0000-1000-8000-00805f9b34fb";
	
	private String groupName;

	private ArrayList<GroupDevice> groupDevices;
	private Set<String> setAddress;
	private static NetConnectBle netConnect;

	public NetConnectBle() {
		setAddress = new HashSet<String>();
	}

	public static NetConnectBle getInstance() {
		if (netConnect == null) {
			netConnect = new NetConnectBle();
		}
		return netConnect;
	}

	public static NetConnectBle getInstanceByGroup(String group) {
		if (netConnect == null) {
			netConnect = new NetConnectBle();
		}
		netConnect.setGroupName(group);
		return netConnect;
	}

	/**
	 * 发送数据
	 * 
	 * @param data
	 */
	public void sendCharacteristic(byte[] data) {

		HashMap<String, BluetoothGatt> gattMap = LedBleApplication.getInstance().getBleGattMap();
		if (null != gattMap && !gattMap.isEmpty() && (!ListUtiles.isEmpty(groupDevices) || StringUtils.isEmpty(groupName))) {

			for (Entry<String, BluetoothGatt> gats : gattMap.entrySet()) {
				try {
					BluetoothGatt gat = gats.getValue();
					List<BluetoothGattService> sList = new ArrayList<BluetoothGattService>();
					List<BluetoothGattCharacteristic> cList = new ArrayList<BluetoothGattCharacteristic>();
					if (gat.getService(UUID.fromString(serviceid32)) != null) {
						BluetoothGattService service = gat.getService(UUID.fromString(serviceid32));
						BluetoothGattCharacteristic charater = service.getCharacteristic(UUID.fromString(characid32));
						sList.add(service);
						cList.add(charater);
					}
					if (gat.getService(UUID.fromString(serviceid33)) != null) {
						BluetoothGattService service = gat.getService(UUID.fromString(serviceid33));
						BluetoothGattCharacteristic charater = service.getCharacteristic(UUID.fromString(characid33));
						sList.add(service);
						cList.add(charater);
					} 
					if (gat.getService(UUID.fromString(serviceid34)) != null) {
						BluetoothGattService service = gat.getService(UUID.fromString(serviceid34));
						BluetoothGattCharacteristic charater = service.getCharacteristic(UUID.fromString(characid34));
						sList.add(service);
						cList.add(charater);
					} 
					for (int i = 0; i < sList.size(); i++) {
						BluetoothGattService service = sList.get(i);
						BluetoothGattCharacteristic charater = cList.get(i);
						if (service != null && setAddress.contains(gat.getDevice().getAddress()) && charater != null) {
							charater.setValue(data);
							gat.writeCharacteristic(charater);// 发送数据
							LogUtil.i(LedBleApplication.tag, "send data to:" + gat.getDevice().getAddress());
						} else {
//							LogUtil.i(LedBleApplication.tag, " not send data to:" + gat.getDevice().getAddress());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}

	public String getGroupName() {
		return groupName;
	}
	
	public ArrayList<GroupDevice> getDevicesByGroup() {
		return groupDevices;
	}

	/**
	 * 该方法必须在发送数据之前调用，作用是明确需要发送数据的设备
	 * 
	 * @param groupName
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
		this.setAddress.clear();// 清空所有的设备
		// ====查找同组的所有设备=======
		GroupDeviceDao gDao = new GroupDeviceDao(LedBleApplication.getInstance());
		if (StringUtils.isEmpty(groupName)) {
			List<BluetoothDevice> bleDecies = LedBleApplication.getInstance().getBleDevices();
			for (int i = 0, isize = bleDecies.size(); i < isize; i++) {
				this.setAddress.add(bleDecies.get(i).getAddress());
			}
		} else {

			// 如果组名为空，则控制所有设备
			if (StringUtils.isEmpty(groupName) || "null".equalsIgnoreCase(groupName)) {
				groupDevices = gDao.getAllGroupDevices();
			} else {
				groupDevices = gDao.getDevicesByGroup(groupName);
			}

			for (int i = 0; ListUtiles.getListSize(groupDevices) > 0 && i < groupDevices.size(); i++) {
				this.setAddress.add(groupDevices.get(i).getAddress());
			}
			LogUtil.i(LedBleApplication.tag, "find " + this.setAddress.size() + " devices in the same group");
		}
	}

	public NetConnectBle(NetExceptionInterface netInterface) {
		this.exceptionCallBack = netInterface;
	}

	public NetExceptionInterface getExceptionCallBack() {
		return exceptionCallBack;
	}

	public void setExceptionCallBack(NetExceptionInterface exceptionCallBack) {
		this.exceptionCallBack = exceptionCallBack;
	}

	// 这个不加入异常处理
	public void turnOn() {
		try {
			int code[] = new int[] { 0x7e, 0x04, 0x04, 0x01, 0xff, 0xff, 0xff, 0x00, 0xef };
			sendData(code);
		} catch (Exception e) {
			if (null != exceptionCallBack) {
				exceptionCallBack.onException(e);
			}
		}
	}

	public void turnOff() {
		try {
			int code[] = new int[] { 0x7e, 0x04, 0x04, 0x00, 0xff, 0xff, 0xff, 0x00, 0xef };
			sendData(code);
		} catch (Exception e) {
			if (null != exceptionCallBack) {
				exceptionCallBack.onException(e);
			}
		}

	}

	// -----------------------定时相关设置

	private static int computeTime(int hour, int minute) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		Calendar ca = Calendar.getInstance();
		Date nowDate = ca.getTime();
		String ymd = sdf2.format(nowDate);

		String then = ymd + " " + NumberHelper.LeftPad_Tow_Zero(hour) + ":" + NumberHelper.LeftPad_Tow_Zero(minute);
		Date thenDate = sdf.parse(then);
		int second = (int) ((thenDate.getTime() - nowDate.getTime()) / 1000);
		if (second < 0) {
			second = second + 24 * 60 * 60;
		}
		return second;
	}

	/**
	 * 打开/关闭 －>定时开灯 功能 onOrOff为1为开，为0为关
	 * 
	 * @param onOrOff
	 */
	public void turnOnOffTimerOn(int onOrOff) {
		try {
			// int code[] = new int[] { 0x7e, 0x01, 0x0d, minuteH, minuteL,
			// 0x01, model, distanceSecond, 0xef };
			int code[] = new int[] { 0x7e, onOrOff, 0x0d, 0xff, 0xff, 0x01, 0xff, 0xff, 0xef };
			sendData(code);
		} catch (Exception e) {
			if (null != exceptionCallBack) {
				exceptionCallBack.onException(e);
			}
		}

	}

	/**
	 * 定时开灯
	 * 
	 * @param hour
	 * @param minute
	 * @param model
	 */
	public void timerOn(int hour, int minute, int model) {
		try {
			int second = computeTime(hour, minute);
			int distanceMinute = second / 60;
			int distanceSecond = second % 60;
			byte minuteH = (byte) (distanceMinute >> 8);
			byte minuteL = (byte) (distanceMinute);
			int code[] = new int[] { 0x7e, 0x01, 0x0d, minuteH, minuteL, 0x01, model, distanceSecond, 0xef };
			sendData(code);
		} catch (Exception e) {
			if (null != exceptionCallBack) {
				exceptionCallBack.onException(e);
			}
		}
	}

	/**
	 * 定时关灯
	 * 
	 * @param hour
	 * @param minute
	 */
	public void timerOff(int hour, int minute) {
		try {
			int second = computeTime(hour, minute);
			int distanceMinute = second / 60;
			int distanceSecond = second % 60;
			byte minuteH = (byte) (distanceMinute >> 8);
			byte minuteL = (byte) (distanceMinute);
			int code[] = new int[] { 0x7e, 0x01, 0x0d, minuteH, minuteL, 0x00, 0xff, distanceSecond, 0xef };
			sendData(code);
		} catch (Exception e) {
			if (null != exceptionCallBack) {
				exceptionCallBack.onException(e);
			}
		}
	}

	/**
	 * 打开/关闭－>定时关灯 功能
	 * 
	 * @param onOrOff
	 */
	public void turnOnOrOffTimerOff(int onOrOff) {
		try {
			int code[] = new int[] { 0x7e, onOrOff, 0x0d, 0xff, 0xff, 0x00, 0xff, 0xff, 0xef };
			sendData(code);
		} catch (Exception e) {
			if (null != exceptionCallBack) {
				exceptionCallBack.onException(e);
			}
		}
	}

	// -------------------

	public void setRgb(int r, int g, int b) {
		try {
			if (0 == r && 0 == g && 0 == b) {
				return;
			}
			int code[] = new int[] { 0x7e, 0x07, 0x05, 0x03, r, g, b, 0x00, 0xef };
			sendData(code);
		} catch (Exception e) {
			if (null != exceptionCallBack) {
				exceptionCallBack.onException(e);
			}
		}

	}

	public void setRgbMode(int model) {
		try {
			int code[] = new int[] { 0x7e, 0x05, 0x03, model, 0x03, 0xff, 0xff, 0x00, 0xef };
			sendData(code);
		} catch (Exception e) {
			if (null != exceptionCallBack) {
				exceptionCallBack.onException(e);
			}
		}

	}

	public void setSpeed(int speed) {
		try {
			int code[] = new int[] { 0x7e, 0x04, 0x02, speed, 0xff, 0xff, 0xff, 0x00, 0xef };
			sendData(code);
		} catch (Exception e) {
			if (null != exceptionCallBack) {
				exceptionCallBack.onException(e);
			}
		}

	}
	
	public void setDiy(ArrayList<MyColor> colors, int style){
		try {
			int code1[] = new int[] { 0x7e, 0x05, 0x0e, style, 0x03, 0xff, 0xff, 0x00, 0xef };
			sendData(code1);
			for(int i=0;i<colors.size();i++){
				int r = colors.get(i).r;
				int g = colors.get(i).g;
				int b = colors.get(i).b;
				LogUtil.i(LedBleApplication.tag, "r:" + r + " g:" + g + " b:" + b);
				final int code2[] = new int[] { 0x7e, 0x07, 0x10, 0x03, r, g, b, 0x00, 0xef };
//				new Handler().postDelayed(new Runnable() {
//					
//					@Override
//					public void run() {
//						try {
//							sendData(code2);
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//					}
//				}, /*30*/300);
				
				Thread.sleep(100);
				sendData(code2);
			}
			final int code3[] = new int[] { 0x7e, 0x05, 0x0f, style, 0x03, 0xff, 0xff, 0x00, 0xef };
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					try {
						sendData(code3);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}, 350);
		} catch (Exception e) {
			if (null != exceptionCallBack) {
				exceptionCallBack.onException(e);
			}
		}
	}

	public void setBrightness(int brightness) {
		try {
			if (brightness > 100) {
				brightness = 100;
			}else if (brightness <= 0) {
				brightness = 0;
			}
			int code[] = new int[] { 0x7e, 0x04, 0x01, brightness, 0xff, 0xff, 0xff, 0x00, 0xef };
			sendData(code);
			int tempBrightness = brightness;
		} catch (Exception e) {
			if (null != exceptionCallBack) {
				exceptionCallBack.onException(e);
			}
		}

	}

	// 0x7e 0x05 0x03 0x0 0x02 0xff 0xff 0x0 0xef
	public void setColorWarmModel(int model) {
		try {
			int code[] = new int[] { 0x7e, 0x05, 0x03, model, 0x02, 0xff, 0xff, 0x0, 0xef };
			sendData(code);
		} catch (Exception e) {
			if (null != exceptionCallBack) {
				exceptionCallBack.onException(e);
			}
		}

	}

	// 0x7e 0x05 0x03 0x0 0x01 0xff 0xff 0x00 0xef
	public void setDimModel(int model) {
		try {
			int code[] = new int[] { 0x7e, 0x05, 0x03, model, 0x01, 0xff, 0xff, 0x0, 0xef };
			sendData(code);
		} catch (Exception e) {
			if (null != exceptionCallBack) {
				exceptionCallBack.onException(e);
			}
		}

	}

	public void setDim(int dim) {
		try {
			int code[] = new int[] { 0x7e, 0x05, 0x05, 0x01, dim, 0xff, 0xff, 0x08, 0xef };
			sendData(code);
		} catch (Exception e) {
			if (null != exceptionCallBack) {
				exceptionCallBack.onException(e);
			}
		}

	}

	// 0x7e 0x07 0x06 0x00 0x00 0x00 0x00 0x0 0xef
	public void setMusic(int brightness, int r, int g, int b) {
		try {
			int code[] = new int[] { 0x7e, 0x07, 0x06, brightness, 0x00, 0x00, 0x00, 0x00, 0xef };
			sendData(code);
		} catch (Exception e) {
			if (null != exceptionCallBack) {
				exceptionCallBack.onException(e);
			}
		}

	}

	// 0x7e 0x06 0x05 0x02 0x00 0x00 0xff 0x08 0xef
	public void setColorWarm(int warm, int cool) {
		try {
			int code[] = new int[] { 0x7e, 0x06, 0x05, 0x02, warm, cool, 0xff, 0x08, 0xef };
			sendData(code);
		} catch (Exception e) {
			if (null != exceptionCallBack) {
				exceptionCallBack.onException(e);
			}
		}

	}
	
	// 律动 模式
	public void setDynamicModel(int model) {
		try {
			int code[] = new int[] { 0x7e, 0x05, 0x03, model, 0x04, 0xff, 0xff, 0x00, 0xef };
			sendData(code);
		} catch (Exception e) {
			if (null != exceptionCallBack) {
				exceptionCallBack.onException(e);
			}
		}
	}
	
	//灵敏度
	public void setensitivity(int speed) {
		try {
			int code[] = new int[] { 0x7e, 0x04, 0x05, speed, 0xff, 0xff, 0xff, 0x00, 0xef };
			sendData(code);
		} catch (Exception e) {
			if (null != exceptionCallBack) {
				exceptionCallBack.onException(e);
			}
		}

	}
	
	// 寰嬪姩 DIY
	public void setDynamicDiy(ArrayList<MyColor> colors, int style) {
		try {
			int code1[] = new int[] { 0x7e, 0x05, 0x0a, style, 0x03, 0xff, 0xff, 0x00, 0xef };
			sendData(code1);
			for (int i = 0; i < colors.size(); i++) {
				int r = colors.get(i).r;
				int g = colors.get(i).g;
				int b = colors.get(i).b;
				LogUtil.i(LedBleApplication.tag, "r:" + r + " g:" + g + " b:" + b);
				final int code2[] = new int[] { 0x7e, 0x07, 0x0b, 0x03, r, g, b, 0x00, 0xef };

				Thread.sleep(100);
				sendData(code2);

			}
			final int code3[] = new int[] { 0x7e, 0x05, 0x0c, style, 0x03, 0xff, 0xff, 0x00, 0xef };
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					try {
						sendData(code3);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}, 350);
		} catch (Exception e) {
			if (null != exceptionCallBack) {
				exceptionCallBack.onException(e);
			}
		}
	}



	public void setSPIBrightness(int brightness) {
		try {
			int code[] = new int[] { 0x7B, 0x04, 0x01, brightness, 0xff, 0xff, 0xff, 0x00, 0xBf };
			sendData(code);
		} catch (Exception e) {
			if (null != exceptionCallBack) {
				exceptionCallBack.onException(e);
			}
		}

	}

	// 设置SPI速度
	public void setSPISpeed(int speed) {
		try {
			int code[] = new int[] { 0x7B, 0x04, 0x02, speed, 0xff, 0xff, 0xff, 0x00, 0xBf };
			sendData(code);
		} catch (Exception e) {
			if (null != exceptionCallBack) {
				exceptionCallBack.onException(e);
			}
		}

	}

	public void setSPIModel(int model) {
		try {
			int code[] = new int[] { 0x7B, 0x05, 0x03, model, 0x03, 0xff, 0xff, 0x00, 0xBf };
			sendData(code);
		} catch (Exception e) {
			if (null != exceptionCallBack) {
				exceptionCallBack.onException(e);
			}
		}
	}

	// 设置 SPI开关
	public void turnOnSPI(int off_on) {
		try {
			int code[] = new int[] { 0x7B, 0x04, 0x04, off_on, 0xff, 0xff, 0xff, 0x00, 0xBf };
			sendData(code);
		} catch (Exception e) {
			if (null != exceptionCallBack) {
				exceptionCallBack.onException(e);
			}
		}

	}

	// 配置SPI
	public void configSPI(int bannerType, byte lengthH, byte lengthL, int bannerSort) {
		try {
			int code[] = new int[] { 0x7B, 0x04, 0x05, bannerType, lengthH, lengthL, bannerSort, 0x00, 0xBF };
			sendData(code);
		} catch (Exception e) {
			if (null != exceptionCallBack) {
				exceptionCallBack.onException(e);
			}
		}

	}

	// 暂停
	public void pauseSPI(int pauseBit) {
		try {
			int code[] = new int[] { 0x7B, 0x04, 0x06, pauseBit, 0xff, 0xff, 0xff, 0x00, 0xBF };
			sendData(code);
		} catch (Exception e) {
			if (null != exceptionCallBack) {
				exceptionCallBack.onException(e);
			}
		}
	}

	public void sendData(int[] data) throws IOException {
		ByteArrayOutputStream byo = new ByteArrayOutputStream();
		for (int i = 0; i < data.length; i++) {
			byo.write(Tool.int2bytearray(data[i]));
		}
		byte[] byteArray = byo.toByteArray();
		sendCharacteristic(byteArray);
	}

	public void closeBle() {
		HashMap<String, BluetoothGatt> gattMap = LedBleApplication.getInstance().getBleGattMap();
		if (null != gattMap && !gattMap.isEmpty()) {
			for (Entry<String, BluetoothGatt> entry : gattMap.entrySet()) {
				entry.getValue().close();
				// entry.getValue().disconnect();
			}
		}
	}

}
