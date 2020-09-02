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

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

/**
 * Encodes arbitrary byte arrays as case-insensitive base-32 strings.
 * <p/>
 * The implementation is slightly different than in RFC 4648. During encoding,
 * padding is not added, and during decoding the last incomplete chunk is not
 * taken into account. The result is that multiple strings decode to the same
 * byte array, for example, string of sixteen 7s ("7...7") and seventeen 7s both
 * decode to the same byte array.
 * TODO(sarvar): Revisit this encoding and whether this ambiguity needs fixing.
 *
 * @author sweis@google.com (Steve Weis)
 * @author Neal Gafter
 */
public class Base32 {
	// singleton

	private static final int SECRET_SIZE = 10;

	private static final SecureRandom RANDOM = new SecureRandom();

	private static final Base32 INSTANCE = new Base32("ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"); // RFC 4648/3548

	static Base32 getInstance() {
		return INSTANCE;
	}

	// 32 alpha-numeric characters.
	private String ALPHABET;
	private char[] DIGITS;
	private int MASK;
	private int SHIFT;
	private HashMap<Character, Integer> CHAR_MAP;

	static final String SEPARATOR = "-";

	protected Base32(String alphabet) {
		this.ALPHABET = alphabet;
		DIGITS = ALPHABET.toCharArray();
		MASK = DIGITS.length - 1;
		SHIFT = Integer.numberOfTrailingZeros(DIGITS.length);
		CHAR_MAP = new HashMap<Character, Integer>();
		for (int i = 0; i < DIGITS.length; i++) {
			CHAR_MAP.put(DIGITS[i], i);
		}
	}

	public static byte[] decode(String encoded) throws DecodingException {
		return getInstance().decodeInternal(encoded);
	}

	protected byte[] decodeInternal(String encoded) throws DecodingException {
		// Remove whitespace and separators
		encoded = encoded.trim().replaceAll(SEPARATOR, "").replaceAll(" ", "");

		// Remove padding. Note: the padding is used as hint to determine how many
		// bits to decode from the last incomplete chunk (which is commented out
		// below, so this may have been wrong to start with).
		encoded = encoded.replaceFirst("[=]*$", "");

		// Canonicalize to all upper case
		encoded = encoded.toUpperCase(Locale.US);
		if (encoded.length() == 0) {
			return new byte[0];
		}
		int encodedLength = encoded.length();
		int outLength = encodedLength * SHIFT / 8;
		byte[] result = new byte[outLength];
		int buffer = 0;
		int next = 0;
		int bitsLeft = 0;
		for (char c : encoded.toCharArray()) {
			if (!CHAR_MAP.containsKey(c)) {
				throw new DecodingException("Illegal character: " + c);
			}
			buffer <<= SHIFT;
			buffer |= CHAR_MAP.get(c) & MASK;
			bitsLeft += SHIFT;
			if (bitsLeft >= 8) {
				result[next++] = (byte) (buffer >> (bitsLeft - 8));
				bitsLeft -= 8;
			}
		}
		// We'll ignore leftover bits for now.
		//
		// if (next != outLength || bitsLeft >= SHIFT) {
		//  throw new DecodingException("Bits left: " + bitsLeft);
		// }
		return result;
	}

	public static String encode(byte[] data, int separatorBlockSize) {
		return getInstance().encodeInternal(data, separatorBlockSize);
	}

	protected String encodeInternal(byte[] data, int separatorBlockSize) {
		if (data.length == 0) {
			return "";
		}

		// SHIFT is the number of bits per output character, so the length of the
		// output is the length of the input multiplied by 8/SHIFT, rounded up.
		if (data.length >= (1 << 28)) {
			// The computation below will fail, so don't do it.
			throw new IllegalArgumentException();
		}

		int outputLength = (data.length * 8 + SHIFT - 1) / SHIFT;
		if (separatorBlockSize > 0) {
			outputLength += outputLength / separatorBlockSize - 1;
		}
		StringBuilder result = new StringBuilder(outputLength);

		int buffer = data[0];
		int next = 1;
		int bitsLeft = 8;
		while (bitsLeft > 0 || next < data.length) {
			if (bitsLeft < SHIFT) {
				if (next < data.length) {
					buffer <<= 8;
					buffer |= (data[next++] & 0xff);
					bitsLeft += 8;
				} else {
					int pad = SHIFT - bitsLeft;
					buffer <<= pad;
					bitsLeft += pad;
				}
			}
			int index = MASK & (buffer >> (bitsLeft - SHIFT));
			bitsLeft -= SHIFT;
			result.append(DIGITS[index]);
			if (separatorBlockSize > 0
				&& (result.length() + 1) % (separatorBlockSize + 1) == 0
				&& outputLength - result.length() > separatorBlockSize / 2)
				result.append(SEPARATOR);
		}
		return result.toString();
	}

	@SuppressWarnings("serial")
	public static class DecodingException extends Exception {

		public DecodingException(String message) {
			super(message);
		}
	}

	public static String random() {

		// Allocating the buffer
		byte[] buffer = new byte[SECRET_SIZE];

		// Filling the buffer with random numbers.
		RANDOM.nextBytes(buffer);

		// Getting the key and converting it to Base32
		byte[] secretKey = Arrays.copyOf(buffer, SECRET_SIZE);
		return encode(secretKey, 0);
	}
}
