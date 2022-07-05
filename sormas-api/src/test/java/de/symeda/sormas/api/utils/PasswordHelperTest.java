/**
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 */
package de.symeda.sormas.api.utils;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

/**
 * @see PasswordHelper
 */
public class PasswordHelperTest {

	private static final int LENGTH = 20;

	private static final String[] FORBIDDEN = {
		"0",
		"1",
		"O",
		"I",
		"V",
		"l",
		"v" };

	@Test
	public void testCreatePass() {

		for (int i = 0; i < 100; i++) {

			String password = PasswordHelper.createPass(LENGTH);
			assertEquals("Unerwartete Passwortlänge", LENGTH, password.length());
			for (int j = 0; j < FORBIDDEN.length; j++) {
				assertFalse("Unerlaubtes Zeichen " + FORBIDDEN[j] + " enthalten: " + password, password.contains(FORBIDDEN[j]));
			}
		}
	}

	@Test
	public void testEncodePasswordGeneratesConsistentHash() {

		String password;
		String seed;
		String passwordHashExpected;

		password = "QTj]qF90U~-CMLa/";

		seed = "xPQCk3J5psSOkLcp";
		passwordHashExpected = "35472f3b451e136556597ec82c16236c85a77dd1718173f2bedd1b1ab3737277";
		assertThat(PasswordHelper.encodePassword(password, seed), equalTo(passwordHashExpected));

		seed = "eqQ8HaXBkmjqqBgt";
		passwordHashExpected = "0daf0f39b9f32cc81462f8c4a9f12ac96552474769768aa6811f672692945d98";
		assertThat(PasswordHelper.encodePassword(password, seed), equalTo(passwordHashExpected));
	}
}
