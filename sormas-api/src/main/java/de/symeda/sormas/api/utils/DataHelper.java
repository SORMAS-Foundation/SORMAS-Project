package de.symeda.sormas.api.utils;

import java.nio.ByteBuffer;

import de.symeda.sormas.api.DataTransferObject;

public final class DataHelper {

	public static String createUuid() {
		//uuid = java.util.UUID.randomUUID().toString();
		java.util.UUID randomUuid = java.util.UUID.randomUUID();
		byte[] bytes = longToBytes(randomUuid.getLeastSignificantBits(), randomUuid.getMostSignificantBits());
		String uuid = Base32.encode(bytes, 6);
		return uuid;
	}
	
	public static byte[] longToBytes(long x, long y) {
	    ByteBuffer buffer = ByteBuffer.allocate(2*Long.BYTES);
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
}
