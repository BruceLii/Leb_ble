package com.common.uitl;

import java.util.UUID;

public class UUIDGenerator {

	public UUIDGenerator() {

	}

	/**
	 * 获取uuid，并截取
	 * 
	 * @return
	 */
	public static String getUUID(int number) {

		String uuidstr;

		try {
			UUID uuid = UUID.randomUUID();

			uuidstr = uuid.toString();

			uuidstr = uuidstr.substring(0, 8) + uuidstr.substring(9, 13)
					+ uuidstr.substring(14, 18) + uuidstr.substring(19, 23)
					+ uuidstr.substring(24);

			if (uuidstr.length() > number) {
				uuidstr = uuidstr.substring(0, number);
			}
			// 去掉"-"符号

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return uuidstr;

	}

}