package de.symeda.sormas.api.utils;

import java.nio.ByteBuffer;

import de.symeda.sormas.api.DataTransferObject;
import de.symeda.sormas.api.ReferenceDto;

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
	
	public static ReferenceDto toReferenceDto(DataTransferObject sourceDto) {
		if (sourceDto == null) {
			return null;
		}
		ReferenceDto dto = new ReferenceDto();
		dto.setCreationDate(sourceDto.getCreationDate());
		dto.setChangeDate(sourceDto.getChangeDate());
		dto.setUuid(sourceDto.getUuid());
		dto.setCaption(sourceDto.toString());
		return dto;
	}
}
