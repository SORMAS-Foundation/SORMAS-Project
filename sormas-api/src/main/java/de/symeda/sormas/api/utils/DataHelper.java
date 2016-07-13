package de.symeda.sormas.api.utils;

import java.nio.ByteBuffer;

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
}
