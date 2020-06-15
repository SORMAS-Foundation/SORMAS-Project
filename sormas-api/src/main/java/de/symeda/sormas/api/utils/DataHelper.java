/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import de.symeda.sormas.api.AgeGroup;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.HasUuid;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.Sex;

public final class DataHelper {

	private DataHelper() {
		// Hide Utility Class Constructor
	}

	public static String createUuid() {

		//uuid = java.util.UUID.randomUUID().toString();
		java.util.UUID randomUuid = java.util.UUID.randomUUID();
		byte[] bytes = longToBytes(randomUuid.getLeastSignificantBits(), randomUuid.getMostSignificantBits());
		String uuid = Base32.encode(bytes, 6);
		return uuid;
	}

	public static boolean isSame(HasUuid left, HasUuid right) {

		if (left == null && right == null) {
			return true;
		} else if (left != null && right != null) {
			return DataHelper.equal(left.getUuid(), right.getUuid());
		} else {
			return false;
		}
	}

	/**
	 * @return a equals b, where a and/or b are allowed to be null
	 */
	public static boolean equal(Object a, Object b) {

		boolean equal = a == b || (a != null && a.equals(b));
		if (a instanceof String) {
			equal = equal || (b == null && ((String) a).isEmpty());
		}
		if (b instanceof String) {
			equal = equal || (a == null && ((String) b).isEmpty());
		}

		return equal;
	}

	/**
	 * @return a equals b, where a and/or b are allowed to be null
	 */
	@SuppressWarnings("unchecked")
	public static <T> int compare(Comparable<T> a, Comparable<T> b) {

		if (a == null) {
			if (b == null) {
				return 0;
			} else {
				return -1;
			}
		} else if (b == null) {
			return 1;
		}
		return a.compareTo((T) b);
	}

	/**
	 * @param nullable
	 * @return "" if null
	 */
	public static String toStringNullable(Object nullable) {

		if (nullable == null) {
			return "";
		}
		return nullable.toString();
	}

	public static boolean isNullOrEmpty(String string) {

		if (string == null) {
			return true;
		} else if (string.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @return Type is a "value" type like a primtive, enum, number, date or string
	 */
	public static boolean isValueType(Class<?> type) {

		return (type.isPrimitive() && type != void.class)
			|| type.isEnum()
			|| type == Double.class
			|| type == Float.class
			|| type == Long.class
			|| type == Integer.class
			|| type == Short.class
			|| type == Character.class
			|| type == Byte.class
			|| type == Boolean.class
			|| type == String.class
			|| type == Date.class;
	}

	public static byte[] longToBytes(long x, long y) {

		ByteBuffer buffer = ByteBuffer.allocate(2 * Long.SIZE / 8);
		buffer.putLong(x);
		buffer.putLong(y);
		return buffer.array();
	}

	public static String getShortUuid(EntityDto domainObject) {
		return getShortUuid(domainObject.getUuid());
	}

	public static String getShortUuid(String uuid) {

		if (uuid == null)
			return null;
		return uuid.substring(0, 6).toUpperCase();
	}

	public static class Pair<K, V> implements Serializable {

		private static final long serialVersionUID = 7135988167451005820L;

		private final K element0;
		private final V element1;

		public static <K, V> Pair<K, V> createPair(K element0, V element1) {
			return new Pair<K, V>(element0, element1);
		}

		public Pair(K element0, V element1) {
			this.element0 = element0;
			this.element1 = element1;
		}

		public K getElement0() {
			return element0;
		}

		public V getElement1() {
			return element1;
		}
	}

	public static String convertStreamToString(InputStream is) throws IOException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line).append('\n');
			}
		} finally {
			is.close();
		}
		return sb.toString();
	}

	public static String capitalize(String input) {
		return input.substring(0, 1).toUpperCase() + input.substring(1);
	}

	public static BigDecimal getTruncatedBigDecimal(BigDecimal number) {
		return number.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0 ? number.setScale(0, RoundingMode.HALF_UP) : number;
	}

	public static List<Integer> buildIntegerList(int min, int max) {
		return buildIntegerList(min, max, 1);
	}

	public static List<Integer> buildIntegerList(int min, int max, int step) {
		List<Integer> x = new ArrayList<>();
		for (int i = min; i <= max; i += step) {
			x.add(i);
		}
		return x;
	}

	public static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValues(Map<K, V> map) {

		SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(new Comparator<Map.Entry<K, V>>() {

			@Override
			public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
				int res = e1.getValue().compareTo(e2.getValue());
				return res != 0 ? res : 1;
			}
		});
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}

	/**
	 * Returns a String that prints all numbers from 0 to 12 spelled out. Higher numbers
	 * are simply transformed into a String.
	 */
	public static String parseNumberToString(int number) {

		switch (number) {
		case 1:
			return I18nProperties.getString(Strings.numberOne).toUpperCase();
		case 2:
			return I18nProperties.getString(Strings.numberTwo).toUpperCase();
		case 3:
			return I18nProperties.getString(Strings.numberThree).toUpperCase();
		case 4:
			return I18nProperties.getString(Strings.numberFour).toUpperCase();
		case 5:
			return I18nProperties.getString(Strings.numberFive).toUpperCase();
		case 6:
			return I18nProperties.getString(Strings.numberSix).toUpperCase();
		case 7:
			return I18nProperties.getString(Strings.numberSeven).toUpperCase();
		case 8:
			return I18nProperties.getString(Strings.numberEight).toUpperCase();
		case 9:
			return I18nProperties.getString(Strings.numberNine).toUpperCase();
		case 10:
			return I18nProperties.getString(Strings.numberTen).toUpperCase();
		case 11:
			return I18nProperties.getString(Strings.numberEleven).toUpperCase();
		case 12:
			return I18nProperties.getString(Strings.numberTwelve).toUpperCase();
		default:
			return Integer.toString(number);
		}
	}

	public static String parseBoolean(Boolean value) {

		if (value == null) {
			return "";
		} else if (Boolean.TRUE.equals(value)) {
			return I18nProperties.getString(Strings.yes);
		} else {
			return I18nProperties.getString(Strings.no);
		}
	}

	public static String getSexAndAgeGroupString(AgeGroup ageGroup, Sex sex) {

		if (sex == null) {
			return I18nProperties.getString(Strings.total) + " " + ageGroup.toString();
		} else {
			return sex.toString() + " " + ageGroup.toString();
		}
	}

	public static String getHumanClassName(Class<?> classType) {

		String className = classType.getSimpleName();
		className = className.replaceAll("Dto$", "");
		return className;
	}

	/**
	 * @return null when NumberFormatException is thrown
	 */
	public static Integer tryParseInt(String value) {

		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * @return null when NumberFormatException is thrown
	 */
	public static Long tryParseLong(String value) {

		try {
			return Long.parseLong(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static boolean isParseableInt(String value) {

		try {
			Integer.parseInt(value);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
