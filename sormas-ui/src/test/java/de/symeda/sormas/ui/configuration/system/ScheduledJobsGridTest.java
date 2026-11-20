/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.configuration.system;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;

import org.junit.jupiter.api.Test;

public class ScheduledJobsGridTest {

	@Test
	public void blankSearchMatchesEverything() {
		assertTrue(ScheduledJobsGrid.matchesSearch("ARCHIVE_CASES", "x", ""));
		assertTrue(ScheduledJobsGrid.matchesSearch("ARCHIVE_CASES", "x", null));
	}

	@Test
	public void searchMatchesJobNameOrDescription() {
		assertTrue(ScheduledJobsGrid.matchesSearch("ARCHIVE_CASES", "", "cases"));
		assertTrue(ScheduledJobsGrid.matchesSearch("CLEAN_UP_TEMPORARY_FILES", "Removes temporary files", "temporary"));
		assertTrue(ScheduledJobsGrid.matchesSearch("OTHER_JOB", "Removes temporary files", "temporary"));
	}

	@Test
	public void searchIgnoresCaseAndSurroundingSpaces() {
		assertTrue(ScheduledJobsGrid.matchesSearch("ARCHIVE_CASES", "", "  Archive "));
	}

	@Test
	public void nonMatchingSearchIsFalse() {
		assertFalse(ScheduledJobsGrid.matchesSearch("ARCHIVE_CASES", "Archives old cases", "immunization"));
	}

	@Test
	public void nullDescriptionIsTreatedAsEmpty() {
		assertTrue(ScheduledJobsGrid.matchesSearch("ARCHIVE_CASES", null, "archive"));
	}

	@Test
	public void searchIgnoresCaseUnderTurkishDefaultLocale() {
		Locale previous = Locale.getDefault();
		Locale.setDefault(new Locale("tr", "TR"));
		try {
			assertTrue(ScheduledJobsGrid.matchesSearch("ARCHIVE_CASES", "", "archive"));
		} finally {
			Locale.setDefault(previous);
		}
	}
}
