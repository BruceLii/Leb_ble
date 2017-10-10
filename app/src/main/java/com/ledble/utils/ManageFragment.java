package com.ledble.utils;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * 
 * @title 管理Fragment
 * @Decription TODO
 * @author ftl
 * @date 2016年4月26日下午2:40:15
 *
 */
public class ManageFragment {

	/**
	 * 切换fragment
	 * @param fragmentManager
	 * @param fragmentList
	 * @param currentIndex
	 */
	public static void showFragment(FragmentManager fragmentManager, List<Fragment> fragmentList, 
			int currentIndex) {
		FragmentTransaction t = fragmentManager.beginTransaction();
		for (int i = 0; i < fragmentList.size(); i++) {
			Fragment fragment = fragmentList.get(i);
			if (i == currentIndex) {
				t.show(fragment);
			} else {
				t.hide(fragment);
			}
		}
		t.commit();
	}
}
