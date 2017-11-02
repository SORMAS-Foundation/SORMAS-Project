package de.symeda.sormas.api.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.Date;

import de.symeda.sormas.api.DataTransferObject;

public final class DataHelper {

	public static String createUuid() {
		//uuid = java.util.UUID.randomUUID().toString();
		java.util.UUID randomUuid = java.util.UUID.randomUUID();
		byte[] bytes = longToBytes(randomUuid.getLeastSignificantBits(), randomUuid.getMostSignificantBits());
		String uuid = Base32.encode(bytes, 6);
		return uuid;
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
	
	public static final String getShortUuid(DataTransferObject domainObject) {
		return getShortUuid(domainObject.getUuid());
	}
	
	public static final String getShortUuid(String uuid) {
		if (uuid == null)
			return null;
		return uuid.substring(0, 6).toUpperCase();
	}
	
	public static class Pair<K, V> {

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
	
}
