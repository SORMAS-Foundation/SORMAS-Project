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
package de.symeda.sormas.backend.util;

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
}
