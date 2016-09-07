package cn.newphy.commons.consistency.util;

public class EnumUtils {

	/**
	 * 根据value值获取枚举值
	 * 
	 * @param value
	 *            键
	 * @param enumType
	 *            枚举类型
	 * @return
	 */
	public static <T extends IEnum> T getEnum(int value, Class<T> enumType) {
		if (enumType == null) {
			return null;
		}
		T[] enums = enumType.getEnumConstants();
		for (T e : enums) {
			T temp = (T) e;
			if (temp.getValue() == value) {
				return temp;
			}
		}
		return null;
	}
}
