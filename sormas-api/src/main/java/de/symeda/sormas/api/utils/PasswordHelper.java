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

import org.apache.commons.codec.binary.Hex;

import javax.validation.ValidationException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public final class PasswordHelper {

    private PasswordHelper() {
        // Hide Utility Class Constructor
    }

    private static final char[] PASSWORD_CHARS = new char[26 - 2 + 26 - 3 + 8];

    static {
        int i = 0;
        for (char ch = 'a'; ch <= 'z'; ch++) {
            switch (ch) {
                case 'l':
                    continue;
                case 'v':
                    continue;
                default:
                    PASSWORD_CHARS[i++] = ch;
            }
        }
        for (char ch = 'A'; ch <= 'Z'; ch++) {
            switch (ch) {
                case 'I':
                    continue;
                case 'O':
                    continue;
                case 'V':
                    continue;
                default:
                    PASSWORD_CHARS[i++] = ch;
            }
        }
        for (char ch = '2'; ch <= '9'; ch++) {
            PASSWORD_CHARS[i++] = ch;
        }

        if (i != PASSWORD_CHARS.length) {
            throw new ValidationException("Size of password char array does not match defined values.");
        }
    }

    public static String generatePasswordWithSpecialChars(int length) {
        // Combine the existing character set with special characters
        char[] combinedChars = Arrays.copyOf(PASSWORD_CHARS, PASSWORD_CHARS.length + 10);
        combinedChars[PASSWORD_CHARS.length] = '!';
        combinedChars[PASSWORD_CHARS.length + 1] = '@';
        combinedChars[PASSWORD_CHARS.length + 2] = '#';
        combinedChars[PASSWORD_CHARS.length + 3] = '$';
        combinedChars[PASSWORD_CHARS.length + 4] = '%';
        combinedChars[PASSWORD_CHARS.length + 5] = '^';
        combinedChars[PASSWORD_CHARS.length + 6] = '&';
        combinedChars[PASSWORD_CHARS.length + 7] = '*';
        combinedChars[PASSWORD_CHARS.length + 8] = '(';
        combinedChars[PASSWORD_CHARS.length + 9] = ')';

        SecureRandom rnd = new SecureRandom();
        char[] chs = new char[length];

        // Ensure at least one digit
        chs[0] = (char) ('2' + rnd.nextInt(8)); // 2-9

        // Ensure at least one special character
        chs[1] = combinedChars[PASSWORD_CHARS.length + rnd.nextInt(10)];

        // Ensure at least one uppercase letter
        chs[2] = (char) ('A' + rnd.nextInt(26));
        while (chs[2] == 'I' || chs[2] == 'O' || chs[2] == 'V') {
            chs[2] = (char) ('A' + rnd.nextInt(26));
        }

        // Ensure at least one lowercase letter
        chs[3] = (char) ('a' + rnd.nextInt(26));
        while (chs[3] == 'l' || chs[3] == 'v') {
            chs[3] = (char) ('a' + rnd.nextInt(26));
        }

        // Fill the rest of the password
        for (int i = 4; i < length; i++) {
            chs[i] = combinedChars[rnd.nextInt(combinedChars.length)];
        }

        // Shuffle the password array to avoid predictable patterns
        for (int i = 0; i < chs.length; i++) {
            int randomIndex = rnd.nextInt(chs.length);
            char temp = chs[i];
            chs[i] = chs[randomIndex];
            chs[randomIndex] = temp;
        }

        return new String(chs);
    }


    public static String createPass(final int length) {

        SecureRandom rnd = new SecureRandom();

        char[] chs = new char[length];
        for (int i = 0; i < length; i++)
            chs[i] = PASSWORD_CHARS[rnd.nextInt(PASSWORD_CHARS.length)];
        final String val = new String(chs);

        return val;
    }

    public static String encodePassword(String password, String seed) {

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            digest.reset();
            byte[] digested = digest.digest((password + seed).getBytes(StandardCharsets.UTF_8));
            String encoded = Hex.encodeHexString(digested);
            return encoded;

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
