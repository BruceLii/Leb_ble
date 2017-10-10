package com.ledble.bean;

import com.common.bean.IBeanInterface;

public class TimerModelBean implements IBeanInterface {

	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;
	public static final String groupNameDefaut = "tagTotal";

	private String groupName;

	private boolean OpenTurnOn;
	private boolean CloseTurnOn;

	private int timerOnHour;
	private int timerOnMinute;
	private int model;

	private int timerOffHour;
	private int timerOffMinute;

	private String modelText;

	public int getModel() {
		return model;
	}

	public void setModel(int model) {
		this.model = model;
	}

	public String getGroupName() {
		return groupName;
	}

	public String getModelText() {
		return modelText;
	}

	public void setModelText(String mdoelText) {
		this.modelText = mdoelText;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public boolean isOpenTurnOn() {
		return OpenTurnOn;
	}

	public void setOpenTurnOn(boolean openTurnOn) {
		OpenTurnOn = openTurnOn;
	}

	public boolean isCloseTurnOn() {
		return CloseTurnOn;
	}

	public void setCloseTurnOn(boolean closeTurnOn) {
		CloseTurnOn = closeTurnOn;
	}

	public int getTimerOnHour() {
		return timerOnHour;
	}

	public void setTimerOnHour(int timerOnHour) {
		this.timerOnHour = timerOnHour;
	}

	public int getTimerOnMinute() {
		return timerOnMinute;
	}

	public void setTimerOnMinute(int timerOnMinute) {
		this.timerOnMinute = timerOnMinute;
	}

	public int getTimerOffHour() {
		return timerOffHour;
	}

	public void setTimerOffHour(int timerOffHour) {
		this.timerOffHour = timerOffHour;
	}

	public int getTimerOffMinute() {
		return timerOffMinute;
	}

	public void setTimerOffMinute(int timerOffMinute) {
		this.timerOffMinute = timerOffMinute;
	}

	@Override
	public String toString() {
		return "" + groupName + ":" + timerOnHour + " :" + timerOnMinute + " " + modelText + " |" + timerOffHour + " " + timerOnMinute;
	}
}
