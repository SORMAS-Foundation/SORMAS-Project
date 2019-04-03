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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.HasUuid;

public final class DataHelper {

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
		return (type.isPrimitive() && type != void.class) ||
				type.isEnum() ||
				type == Double.class || type == Float.class || type == Long.class ||
				type == Integer.class || type == Short.class || type == Character.class ||
				type == Byte.class || 
				type == Boolean.class || 
				type == String.class ||
				type == Date.class;	        
	}

	public static byte[] longToBytes(long x, long y) {
		ByteBuffer buffer = ByteBuffer.allocate(2*Long.SIZE/8);
		buffer.putLong(x);
		buffer.putLong(y);
		return buffer.array();
	}

	public static final String getShortUuid(EntityDto domainObject) {
		return getShortUuid(domainObject.getUuid());
	}

	public static final String getShortUuid(String uuid) {
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

	public static String getEpidNumberRegexp() {
		return "\\w{3}-\\w{3}-\\w{3}-\\d{2}-[A-Za-z0-9]+";
	}
	
	public static String capitalize(String input) {
		return input.substring(0, 1).toUpperCase() + input.substring(1);
	}
	
	public static BigDecimal getTruncatedBigDecimal(BigDecimal number) {
		return number.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0 ? number.setScale(0,  RoundingMode.HALF_UP) : number;
	}
	
	public static <K,V extends Comparable<? super V>> SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
	    SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
	        new Comparator<Map.Entry<K,V>>() {
	            @Override 
	            public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
	                int res = e1.getValue().compareTo(e2.getValue());
	                return res != 0 ? res : 1;
	            }
	        }
	    );
	    sortedEntries.addAll(map.entrySet());
	    return sortedEntries;
	}
	
}
