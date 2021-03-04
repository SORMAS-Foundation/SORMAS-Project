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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.regex.Pattern;

import org.apache.commons.io.input.BOMInputStream;

// Charset detection according to UTF-8 Encoding Debugging Chart
// https://www.i18nqa.com/debug/utf8-debug.html
public class CharsetHelper {

	private static final Pattern UTF_8_SEQUENCES = Pattern.compile(
		"\u00C2(\u00A1|\u00A2|\u00A3|\u00A4|\u00A5|\u00A6|\u00A7|\u00A8|\u00A9|\u00AA|\u00AB|\u00AC|\u00AD|\u00AE|\u00AF|\u00B0|\u00B1|\u00B2|\u00B3|\u00B4|\u00B5|\u00B6|\u00B7|\u00B8|\u00B9|\u00BA|\u00BB|\u00BC|\u00BD|\u00BE|\u00BF)|"
			+ "\u00C3(\u0080|\u0081|\u0082|\u0083|\u0084|\u0085|\u0086|\u0087|\u0088|\u0089|\u008A|\u008B|\u008C|\u008D|\u008E|\u008F|\u0090|\u0091|\u0092|\u0093|\u0094|\u0095|\u0096|\u0097|\u0098|\u0099|\u009A|\u009B|\u009C|\u009D|\u009E|\u009F|\u00A0|\u00A1|\u00A2|\u00A3|\u00A4|\u00A5|\u00A6|\u00A7|\u00A8|\u00A9|\u00AA|\u00AB|\u00AC|\u00AD|\u00AE|\u00AF|\u00B0|\u00B1|\u00B2|\u00B3|\u00B4|\u00B5|\u00B6|\u00B7|\u00B8|\u00B9|\u00BA|\u00BB|\u00BC|\u00BD|\u00BE|\u00BF)|"
			+ "\u00C5(\u0092|\u0093|\u00A0|\u00A1|\u00B8|\u00BD|\u00BE)|\u00C6\u0092|\u00CB(\u0086|\u009C)|"
			+ "\u00E2(\u0080(\u0093|\u0094|\u0098|\u0099|\u009A|\u009C|\u009D|\u009E|\u00A0|\u00A1|\u00A2|\u00A6|\u00B0|\u00B9|\u00BA)|\u0082\u00AC|\u0084\u00A2)");

	public static Charset detectCharset(File inputFile) {
		if (isCharsetUTF8(inputFile)) {
			return StandardCharsets.UTF_8;
		}
		if (isCharsetIso8859_1(inputFile)) {
			return StandardCharsets.ISO_8859_1;
		}
		return StandardCharsets.UTF_8;
	}

	public static CharsetDecoder getDecoder(File inputFile) {
		Charset charset = detectCharset(inputFile);
		return charset != null ? charset.newDecoder() : StandardCharsets.UTF_8.newDecoder();
	}

	public static boolean isCharsetUTF8(File inputFile) {
		try (InputStream inputStream = Files.newInputStream(inputFile.toPath())) {
			BOMInputStream bomInputStream = new BOMInputStream(inputStream);
			if (bomInputStream.hasBOM()) {
				return true;
			}
			Reader reader = new InputStreamReader(bomInputStream, StandardCharsets.UTF_8.newDecoder());
			BufferedReader bufferedReader = new BufferedReader(reader);
			while (bufferedReader.readLine() != null);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public static boolean isCharsetIso8859_1(File inputFile) {
		try (InputStream inputStream = Files.newInputStream(inputFile.toPath())) {
			Reader reader = new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1.newDecoder());
			BufferedReader bufferedReader = new BufferedReader(reader);
			while (true) {
				String line = bufferedReader.readLine();
				if (line != null) {
					if (isMisreadUtf8Line(line)) {
						return false;
					}
				} else {
					break;
				}
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public static boolean isMisreadUtf8Line(String line) {
		return UTF_8_SEQUENCES.matcher(line).find();
	}
}
