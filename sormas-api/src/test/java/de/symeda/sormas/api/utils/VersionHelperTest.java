package de.symeda.sormas.api.utils;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class VersionHelperTest {

	@Test
	public void testExtractVersion() {

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
	}

	@Test
	public void testIsBefore() {
		assertTrue(VersionHelper.isBefore(new int[] { 0, 23, 0 }, new int[] { 1, 0, 0 }));
		assertTrue(VersionHelper.isBefore(new int[] { 1, 23, 0 }, new int[] { 2, 0, 0 }));
		assertTrue(VersionHelper.isBefore(new int[] { 0, 23, 0 }, new int[] { 0, 23, 1 }));

		assertFalse(VersionHelper.isBefore(new int[] { 1, 0, 0 }, new int[] { 0, 23, 0 }));
		assertFalse(VersionHelper.isBefore(new int[] { 2, 0, 0 }, new int[] { 1, 23, 0 }));
		assertFalse(VersionHelper.isBefore(new int[] { 0, 23, 1 }, new int[] { 0, 23, 0 }));
		assertFalse(VersionHelper.isBefore(new int[] { 0, 23, 0 }, new int[] { 0, 23, 0 }));
	}

	@Test
	public void testIsAfter() {
		assertFalse(VersionHelper.isAfter(new int[] { 0, 23, 0 }, new int[] { 1, 0, 0 }));
		assertFalse(VersionHelper.isAfter(new int[] { 1, 23, 0 }, new int[] { 2, 0, 0 }));
		assertFalse(VersionHelper.isAfter(new int[] { 0, 23, 0 }, new int[] { 0, 23, 1 }));
		assertFalse(VersionHelper.isAfter(new int[] { 0, 23, 0 }, new int[] { 0, 23, 0 }));

		assertTrue(VersionHelper.isAfter(new int[] { 1, 0, 0 }, new int[] { 0, 23, 0 }));
		assertTrue(VersionHelper.isAfter(new int[] { 2, 0, 0 }, new int[] { 1, 23, 0 }));
		assertTrue(VersionHelper.isAfter(new int[] { 0, 23, 1 }, new int[] { 0, 23, 0 }));
	}


}
