/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.immunization;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static de.symeda.sormas.app.backend.immunization.ImmunizationDaoHelper.overlappingDateRangeImmunizations;

import de.symeda.sormas.api.utils.DateHelper;

public class ImmunizationDaoHelperTest {

	@Test
	public void testOverlappingDateRangeImmunizationsForNonExistingRange() {
		final Date now = new Date();
		final List<Immunization> immunizations = getImmunizationListOfOneEntryWithDateRange(null, null);

		Assert.assertEquals(1, overlappingDateRangeImmunizations(immunizations, now, DateHelper.addDays(now, 5)).size());
		Assert.assertEquals(
			1,
			overlappingDateRangeImmunizations(immunizations, DateHelper.subtractDays(now, 10), DateHelper.subtractDays(now, 5)).size());
		Assert.assertEquals(1, overlappingDateRangeImmunizations(immunizations, DateHelper.subtractDays(now, 10), null).size());
		Assert.assertEquals(1, overlappingDateRangeImmunizations(immunizations, DateHelper.subtractDays(now, 30), null).size());
		Assert.assertEquals(1, overlappingDateRangeImmunizations(immunizations, null, DateHelper.subtractDays(now, 10)).size());
		Assert.assertEquals(1, overlappingDateRangeImmunizations(immunizations, null, DateHelper.subtractDays(now, 30)).size());
		Assert.assertEquals(1, overlappingDateRangeImmunizations(immunizations, DateHelper.addDays(now, 1), null).size());
		Assert.assertEquals(
			1,
			overlappingDateRangeImmunizations(immunizations, DateHelper.addDays(now, 1), DateHelper.addDays(now, 100)).size());
	}

	@Test
	public void testOverlappingDateRangeImmunizationsForExistingRange() {
		final Date now = new Date();
		final List<Immunization> immunizations = getImmunizationListOfOneEntryWithDateRange(DateHelper.subtractDays(now, 20), now);

		Assert.assertEquals(1, overlappingDateRangeImmunizations(immunizations, now, DateHelper.addDays(now, 5)).size());
		Assert.assertEquals(
				1,
				overlappingDateRangeImmunizations(immunizations, DateHelper.subtractDays(now, 10), DateHelper.subtractDays(now, 5)).size());
		Assert.assertEquals(1, overlappingDateRangeImmunizations(immunizations, DateHelper.subtractDays(now, 10), null).size());
		Assert.assertEquals(1, overlappingDateRangeImmunizations(immunizations, DateHelper.subtractDays(now, 30), null).size());
		Assert.assertEquals(1, overlappingDateRangeImmunizations(immunizations, null, DateHelper.subtractDays(now, 10)).size());
		Assert.assertEquals(0, overlappingDateRangeImmunizations(immunizations, null, DateHelper.subtractDays(now, 30)).size());
		Assert.assertEquals(0, overlappingDateRangeImmunizations(immunizations, DateHelper.addDays(now, 1), null).size());
		Assert.assertEquals(
				0,
				overlappingDateRangeImmunizations(immunizations, DateHelper.addDays(now, 1), DateHelper.addDays(now, 100)).size());
	}

	@Test
	public void testOverlappingDateRangeImmunizationsForExistingStartDate() {
		final Date now = new Date();
		final List<Immunization> immunizations = getImmunizationListOfOneEntryWithDateRange(DateHelper.subtractDays(now, 20), null);

		Assert.assertEquals(1, overlappingDateRangeImmunizations(immunizations, now, DateHelper.addDays(now, 5)).size());
		Assert.assertEquals(
				1,
				overlappingDateRangeImmunizations(immunizations, DateHelper.subtractDays(now, 10), DateHelper.subtractDays(now, 5)).size());
		Assert.assertEquals(1, overlappingDateRangeImmunizations(immunizations, DateHelper.subtractDays(now, 10), null).size());
		Assert.assertEquals(1, overlappingDateRangeImmunizations(immunizations, DateHelper.subtractDays(now, 30), null).size());
		Assert.assertEquals(1, overlappingDateRangeImmunizations(immunizations, null, DateHelper.subtractDays(now, 10)).size());
		Assert.assertEquals(0, overlappingDateRangeImmunizations(immunizations, null, DateHelper.subtractDays(now, 30)).size());
		Assert.assertEquals(1, overlappingDateRangeImmunizations(immunizations, DateHelper.addDays(now, 1), null).size());
		Assert.assertEquals(
				1,
				overlappingDateRangeImmunizations(immunizations, DateHelper.addDays(now, 1), DateHelper.addDays(now, 100)).size());
	}

	@Test
	public void testOverlappingDateRangeImmunizationsForExistingEndDate() {
		final Date now = new Date();
		final List<Immunization> immunizations = getImmunizationListOfOneEntryWithDateRange(null, now);

		Assert.assertEquals(1, overlappingDateRangeImmunizations(immunizations, now, DateHelper.addDays(now, 5)).size());
		Assert.assertEquals(
				1,
				overlappingDateRangeImmunizations(immunizations, DateHelper.subtractDays(now, 10), DateHelper.subtractDays(now, 5)).size());
		Assert.assertEquals(1, overlappingDateRangeImmunizations(immunizations, DateHelper.subtractDays(now, 10), null).size());
		Assert.assertEquals(1, overlappingDateRangeImmunizations(immunizations, null, DateHelper.subtractDays(now, 10)).size());
		Assert.assertEquals(1, overlappingDateRangeImmunizations(immunizations, null, DateHelper.subtractDays(now, 30)).size());
		Assert.assertEquals(0, overlappingDateRangeImmunizations(immunizations, DateHelper.addDays(now, 1), null).size());
		Assert.assertEquals(
				0,
				overlappingDateRangeImmunizations(immunizations, DateHelper.addDays(now, 1), DateHelper.addDays(now, 100)).size());
	}

	private List<Immunization> getImmunizationListOfOneEntryWithDateRange(Date startDate, Date endDate) {
		final List<Immunization> immunizations = new ArrayList<>();
		final Immunization immunization = new Immunization();
		immunization.setStartDate(startDate);
		immunization.setEndDate(endDate);
		immunizations.add(immunization);
		return immunizations;
	}

}
