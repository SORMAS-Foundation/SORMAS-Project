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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.person;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.symeda.sormas.api.Language;
import org.junit.Test;

public class PersonHelperTest {

	@Test
	public void nameSimilarityExceedsThreshold() {
		String firstName = "Thomas Miller";
		String secondName = "Tomas Miller";

		assertTrue(PersonHelper.areNamesSimilar(firstName, secondName));

		firstName = "Thomas Miller";
		secondName = "Miller Thomas";

		assertTrue(PersonHelper.areNamesSimilar(firstName, secondName));

		firstName = "Thomas Jake Miller";
		secondName = "Thomas Miller";

		assertTrue(PersonHelper.areNamesSimilar(firstName, secondName));

		firstName = "Thomas Jake Miller";
		secondName = "Thomas Jacob Miller";

		assertTrue(PersonHelper.areNamesSimilar(firstName, secondName));

		firstName = "Dan Brown";
		secondName = "Dan Browning";

		assertTrue(PersonHelper.areNamesSimilar(firstName, secondName));

		firstName = "Dan Van";
		secondName = "Gan Van";

		assertTrue(PersonHelper.areNamesSimilar(firstName, secondName));
	}

	@Test
	public void nameSimilarityDeceedsThreshold() {
		String firstName = "Thomas Miller";
		String secondName = "Tomislav Millerton";

		assertFalse(PersonHelper.areNamesSimilar(firstName, secondName));

		firstName = "Jonathan Lee Sterling";
		secondName = "John Lee Langston";

		assertFalse(PersonHelper.areNamesSimilar(firstName, secondName));

		firstName = "Gan Zan";
		secondName = "Don Van";

		assertFalse(PersonHelper.areNamesSimilar(firstName, secondName));
	}

	@Test
	public void testFormatBirthdateEN() throws Exception {
		assertEquals("", PersonHelper.formatBirthdate(null, null, null, Language.EN));
		assertEquals("1990", PersonHelper.formatBirthdate(null, null, 1990, Language.EN));
		assertEquals("7//1990", PersonHelper.formatBirthdate(null, 7, 1990, Language.EN));
		assertEquals("7", PersonHelper.formatBirthdate(null, 7, null, Language.EN));
		assertEquals("7/5", PersonHelper.formatBirthdate(5, 7, null, Language.EN));
		assertEquals("5", PersonHelper.formatBirthdate(5, null, null, Language.EN));
		assertEquals("5/1990", PersonHelper.formatBirthdate(5, null, 1990, Language.EN));
		assertEquals("7/5/1990", PersonHelper.formatBirthdate(5, 7, 1990, Language.EN));
	}

	@Test
	public void testFormatBirthdateDE() throws Exception {
		assertEquals("", PersonHelper.formatBirthdate(null, null, null, Language.DE));
		assertEquals("1990", PersonHelper.formatBirthdate(null, null, 1990, Language.DE));
		assertEquals("7.1990", PersonHelper.formatBirthdate(null, 7, 1990, Language.DE));
		assertEquals("7", PersonHelper.formatBirthdate(null, 7, null, Language.DE));
		assertEquals("5.7", PersonHelper.formatBirthdate(5, 7, null, Language.DE));
		assertEquals("5", PersonHelper.formatBirthdate(5, null, null, Language.DE));
		assertEquals("5..1990", PersonHelper.formatBirthdate(5, null, 1990, Language.DE));
		assertEquals("5.7.1990", PersonHelper.formatBirthdate(5, 7, 1990, Language.DE));
	}
}
