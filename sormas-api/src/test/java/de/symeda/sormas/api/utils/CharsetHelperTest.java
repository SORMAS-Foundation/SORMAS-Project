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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class CharsetHelperTest {

	@Test
	public void recognizesUTF8File() throws URISyntaxException {
		File file = new File(getClass().getClassLoader().getResource("Some-UTF-8-File.txt").toURI());
		assertEquals(StandardCharsets.UTF_8, CharsetHelper.detectCharset(file));
	}

	@Test
	public void recognizesISO_8859_1File() throws URISyntaxException {
		File file = new File(getClass().getClassLoader().getResource("Some-ISO-8859-1-File.txt").toURI());
		assertEquals(StandardCharsets.ISO_8859_1, CharsetHelper.detectCharset(file));
	}

	@Test
	public void recognizesASCIIFilesAsUTF8() throws URISyntaxException {
		File file = new File(getClass().getClassLoader().getResource("Some-ASCII-File.txt").toURI());
		assertEquals(StandardCharsets.UTF_8, CharsetHelper.detectCharset(file));
	}

	@Test
	public void recognizesIsNoISO_8859_1File() throws URISyntaxException {
		File file = new File(getClass().getClassLoader().getResource("Some-UTF-8-File.txt").toURI());
		assertFalse(CharsetHelper.isCharsetIso8859_1(file));
	}

	@Test
	public void recognizesIsNoUTF8File() throws URISyntaxException {
		File file = new File(getClass().getClassLoader().getResource("Some-ISO-8859-1-File.txt").toURI());
		assertFalse(CharsetHelper.isCharsetUTF8(file));
	}

	@Test
	public void misreadUTFSequencesAreRecognized() throws IOException, URISyntaxException {
		File file = new File(getClass().getClassLoader().getResource("utf8sequences.txt").toURI());
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.ISO_8859_1))) {
			String line;
			while ((line = br.readLine()) != null) {
				assertTrue(CharsetHelper.isMisreadUtf8Line("Some text with " + line + "in it."));
			}
		}
		assertFalse(CharsetHelper.isMisreadUtf8Line("Some text without any of those sequences"));
		assertFalse(CharsetHelper.isMisreadUtf8Line("Some text with special characters äöüß"));
	}
}
