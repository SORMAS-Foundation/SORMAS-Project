/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.utils;

import static java.util.stream.Collectors.joining;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.CharMatcher;

import de.symeda.sormas.api.AgeGroup;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.caze.AgeAndBirthDateDto;
import de.symeda.sormas.api.caze.BirthDateDto;
import de.symeda.sormas.api.caze.BurialInfoDto;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.environment.environmentsample.Pathogen;
import de.symeda.sormas.api.event.SpecificRisk;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.uuid.HasUuid;

public final class DataHelper {

	public static final String VALID_EMAIL_REGEX = "^([a-zA-Z0-9_\\.\\-+])+@[a-zA-Z0-9-.]+\\.[a-zA-Z0-9-]{2,}$";
	public static final String NOT_A_VALID_PHONE_NUMBER_REGEX = ".*[a-zA-Z].*";

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

	public static String createConstantUuid(int seed) {
		return new UUID(0, seed).toString();
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

		if (a instanceof Timestamp && b instanceof Date) {
			equal = equal || a.equals(new Timestamp(((Date) b).getTime()));
		} else if (a instanceof Date && b instanceof Timestamp) {
			equal = equal || new Timestamp(((Date) a).getTime()).equals(b);
		}

		return equal;
	}

	/**
	 * Compare content of collections, ignoring the order
	 */
	public static boolean equalContains(Collection a, Collection b) {
		if (equal(a, b)) {
			return true;
		}
		if (a == null || b == null) {
			return false;
		}
		return a.size() == b.size() && a.containsAll(b);
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

		if (HasCaption.class.isAssignableFrom(nullable.getClass())) {
			return ((HasCaption) nullable).buildCaption();
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
			|| type == Date.class
			|| type.isAssignableFrom(DiseaseVariant.class)
			|| type.isAssignableFrom(OccupationType.class)
			|| type.isAssignableFrom(SpecificRisk.class);
	}

	public static byte[] longToBytes(long x, long y) {

		ByteBuffer buffer = ByteBuffer.allocate(2 * Long.SIZE / 8);
		buffer.putLong(x);
		buffer.putLong(y);
		return buffer.array();
	}

	public static String getShortUuid(HasUuid domainObject) {
		return getShortUuid(domainObject.getUuid());
	}

	public static String getShortUuid(String uuid) {

		if (uuid == null)
			return null;
		return uuid.substring(0, 6).toUpperCase();
	}

	@AuditedClass(includeAllFields = true)
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

	public static String capitalize(String input) {
		return input.substring(0, 1).toUpperCase() + input.substring(1);
	}

	public static String lowercaseFirst(String input) {
		return input.substring(0, 1).toLowerCase() + input.substring(1);
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

	public static String stringifyBoolean(Boolean value) {

		if (value == null) {
			return "";
		} else if (Boolean.TRUE.equals(value)) {
			return I18nProperties.getString(Strings.yes);
		} else {
			return I18nProperties.getString(Strings.no);
		}
	}

	public static Boolean parseBoolean(String value) {

		if (value == null) {
			return null;
		}

		if (I18nProperties.getString(Strings.yes).equalsIgnoreCase(value)) {
			return true;
		} else if (I18nProperties.getString(Strings.no).equalsIgnoreCase(value)) {
			return false;
		}

		return Boolean.parseBoolean(value);
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

	public static String getHumanClassCaption(Class<?> classType) {

		String className = classType.getSimpleName();
		className = className.replaceAll("Dto$", "");
		className = className.replaceAll("Reference$", "");
		I18nProperties.getCaption(DataHelper.lowercaseFirst(className), className);
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

	public static String valueToString(Object value) {
		Language userLanguage = I18nProperties.getUserLanguage();
		if (value == null) {
			return "";
		} else if (value instanceof Date) {
			return DateFormatHelper.formatDate((Date) value);
		} else if (value.getClass().equals(Boolean.class)) {
			return DataHelper.stringifyBoolean((Boolean) value);
		} else if (value instanceof Set) {
			StringBuilder sb = new StringBuilder();
			for (Object o : (Set<?>) value) {
				if (sb.length() != 0) {
					sb.append(", ");
				}
				sb.append(o);
			}
			return sb.toString();
		} else if (value instanceof BurialInfoDto) {
			return PersonHelper.buildBurialInfoString((BurialInfoDto) value, userLanguage);
		} else if (value instanceof AgeAndBirthDateDto) {
			AgeAndBirthDateDto ageAndBirthDate = (AgeAndBirthDateDto) value;
			return PersonHelper.getAgeAndBirthdateString(
				ageAndBirthDate.getAge(),
				ageAndBirthDate.getAgeType(),
				ageAndBirthDate.getDateOfBirthDD(),
				ageAndBirthDate.getDateOfBirthMM(),
				ageAndBirthDate.getDateOfBirthYYYY());
		} else if (value instanceof BirthDateDto) {
			BirthDateDto birthDate = (BirthDateDto) value;
			return DateFormatHelper.formatDate(birthDate.getDateOfBirthDD(), birthDate.getDateOfBirthMM(), birthDate.getDateOfBirthYYYY());
		} else if (value instanceof HasCaption) {
			return ((HasCaption) value).buildCaption();
		} else {
			return value.toString();
		}
	}

	public static String sanitizeFileName(String fileName) {
		return fileName.replaceAll("[^a-zA-Z0-9._-]", "");
	}

	public static String cleanStringForFileName(String name) {
		String nameWithoutSpecialCharacters = CharMatcher.javaLetter().or(CharMatcher.is(' ')).retainFrom(name);
		return nameWithoutSpecialCharacters.replace(' ', '_').toLowerCase();
	}

	public static <T> List<T> asListNullable(@Nullable T object) {
		if (object == null) {
			return null;
		}

		return Collections.singletonList(object);
	}

	public static String joinStrings(String separator, String... strings) {
		List<String> notEmptyValues = new ArrayList<>();
		for (String string : strings) {
			if (!StringUtils.isBlank(string)) {
				notEmptyValues.add(string);
			}
		}

		return StringUtils.join(notEmptyValues, separator);
	}

	public static boolean isValidPhoneNumber(String phoneNumber) {
		return StringUtils.isBlank(phoneNumber) || !phoneNumber.matches(NOT_A_VALID_PHONE_NUMBER_REGEX);
	}

	public static boolean isValidEmailAddress(String emailAddress) {
		return StringUtils.isBlank(emailAddress) || emailAddress.matches(VALID_EMAIL_REGEX);
	}

	public static String buildStringFromTrueValues(Map<? extends Enum<?>, Boolean> map) {
		if (map != null) {
			return map.keySet().stream().filter(map::get).map(I18nProperties::getEnumCaption).collect(joining(", "));
		} else {
			return "";
		}
	}

	/**
	 * This method will remove the time from a date and set it to midnight. For example date 22.09.2022 15:32:54.123 will become
	 * 22.09.2022 0:00:0 .
	 */
	public static Date removeTime(Date date) {
		Date shortDate = date;
		if (date != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			shortDate = calendar.getTime();
		}
		return shortDate;
	}

	public static String getPathogenString(Pathogen pathogen, String pathogenDetails) {
		return pathogen != null ? pathogen.getCaption() + (StringUtils.isNotBlank(pathogenDetails) ? " (" + pathogenDetails + ")" : "") : "";
	}
}
