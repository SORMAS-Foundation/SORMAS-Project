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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class VersionHelperTest {

	@Test
	public void testExtractVersion() {

		//@formatter:off
		assertArrayEquals(new int[] { 0, 23, 0 }, VersionHelper.extractVersion("0.23.0"));
		assertArrayEquals(new int[] { 0, 23, 1 }, VersionHelper.extractVersion("0.23.1"));
		assertArrayEquals(new int[] { 0, 23, 1 }, VersionHelper.extractVersion("0.23.1-SNAPSHOT"));

		assertArrayEquals(new int[] { 1, 0, 0 }, VersionHelper.extractVersion("1.0.0"));
		assertArrayEquals(new int[] { 1, 0, 0 }, VersionHelper.extractVersion("1.0.0-SNAPSHOT"));
		assertArrayEquals(new int[] { 1, 0, 12 }, VersionHelper.extractVersion("1.0.12"));

		assertNull(VersionHelper.extractVersion("1.0."));
		assertNull(VersionHelper.extractVersion("1.0"));
		assertNull(VersionHelper.extractVersion("1.0-SNAPSHOT"));
		assertNull(VersionHelper.extractVersion("1.0-SNAPSHOT.12"));

		assertNull(VersionHelper.extractVersion("https://www.sormas.org/download/sormas-release.apk"));
		assertArrayEquals(new int[] { 1, 0, 0 },
				VersionHelper.extractVersion("https://www.sormas.org/download/sormas-release-1.0.0.apk"));
		assertArrayEquals(new int[] { 1, 0, 0 },
				VersionHelper.extractVersion("https://www.sormas.org/download/sormas-1.0.0-release.apk"));
		assertArrayEquals(new int[] { 1, 0, 0 },
				VersionHelper.extractVersion("https://www.sormas.org/download/sormas-1.0.0-SNAPSHOT-release.apk"));
		assertArrayEquals(new int[] { 11, 13, 15 },
				VersionHelper.extractVersion("https://www.sormas.org/download/sormas-11.13.15-release.apk"));
		//@formatter:on
	}

	@Test
	public void testIsBefore() {

		//@formatter:off
		assertTrue(VersionHelper.isBefore(new int[] { 0, 23, 0 }, new int[] { 1, 0, 0 }));
		assertTrue(VersionHelper.isBefore(new int[] { 1, 23, 0 }, new int[] { 2, 0, 0 }));
		assertTrue(VersionHelper.isBefore(new int[] { 0, 23, 0 }, new int[] { 0, 23, 1 }));

		assertFalse(VersionHelper.isBefore(new int[] { 1, 0, 0 }, new int[] { 0, 23, 0 }));
		assertFalse(VersionHelper.isBefore(new int[] { 2, 0, 0 }, new int[] { 1, 23, 0 }));
		assertFalse(VersionHelper.isBefore(new int[] { 0, 23, 1 }, new int[] { 0, 23, 0 }));
		assertFalse(VersionHelper.isBefore(new int[] { 0, 23, 0 }, new int[] { 0, 23, 0 }));
		//@formatter:on
	}

	@Test
	public void testIsAfter() {

		//@formatter:off
		assertFalse(VersionHelper.isAfter(new int[] { 0, 23, 0 }, new int[] { 1, 0, 0 }));
		assertFalse(VersionHelper.isAfter(new int[] { 1, 23, 0 }, new int[] { 2, 0, 0 }));
		assertFalse(VersionHelper.isAfter(new int[] { 0, 23, 0 }, new int[] { 0, 23, 1 }));
		assertFalse(VersionHelper.isAfter(new int[] { 0, 23, 0 }, new int[] { 0, 23, 0 }));

		assertTrue(VersionHelper.isAfter(new int[] { 1, 0, 0 }, new int[] { 0, 23, 0 }));
		assertTrue(VersionHelper.isAfter(new int[] { 2, 0, 0 }, new int[] { 1, 23, 0 }));
		assertTrue(VersionHelper.isAfter(new int[] { 0, 23, 1 }, new int[] { 0, 23, 0 }));
		//@formatter:on
	}

	@Test
	public void testIsEqual() {

		//@formatter:off
		assertTrue(VersionHelper.isEqual(new int[] { 1, 0, 0 }, new int[] { 1, 0, 0 }));
		assertTrue(VersionHelper.isEqual(new int[] { 0, 23, 0 }, new int[] { 0, 23, 0 }));
		
		assertFalse(VersionHelper.isEqual(new int[] { 0, 23, 0 }, new int[] { 1, 0, 0 }));
		assertFalse(VersionHelper.isEqual(new int[] { 1, 0, 0 }, new int[] { 0, 0, 5 }));
		//@formatter:on
	}
}
