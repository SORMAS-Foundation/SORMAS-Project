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

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static de.symeda.sormas.app.backend.immunization.ImmunizationDaoHelper.overlappingDateRangeImmunizations;

public class ImmunizationDaoHelperTest {

	@Test
	public void testOverlappingDateRangeImmunizationsForNonExistingRange() {
		final Date now = new Date();
		final List<Immunization> immunizations = getImmunizationListOfOneEntryWithDateRange(null, null);

		Assert.assertEquals(1, overlappingDateRangeImmunizations(immunizations, now, new DateTime().plusDays(5).toDate()).size());
		Assert.assertEquals(
			1,
			overlappingDateRangeImmunizations(immunizations, new DateTime().minusDays(10).toDate(), new DateTime().minusDays(5).toDate()).size());
		Assert.assertEquals(1, overlappingDateRangeImmunizations(immunizations, new DateTime().minusDays(10).toDate(), null).size());
		Assert.assertEquals(1, overlappingDateRangeImmunizations(immunizations, new DateTime().minusDays(30).toDate(), null).size());
		Assert.assertEquals(1, overlappingDateRangeImmunizations(immunizations, null, new DateTime().minusDays(10).toDate()).size());
		Assert.assertEquals(1, overlappingDateRangeImmunizations(immunizations, null, new DateTime().minusDays(30).toDate()).size());
		Assert.assertEquals(1, overlappingDateRangeImmunizations(immunizations, new DateTime().plusDays(1).toDate(), null).size());
		Assert.assertEquals(
			1,
			overlappingDateRangeImmunizations(immunizations, new DateTime().plusDays(1).toDate(), new DateTime().plusDays(100).toDate()).size());
	}

	@Test
	public void testOverlappingDateRangeImmunizationsForExistingRange() {
		final Date now = new Date();
		final List<Immunization> immunizations = getImmunizationListOfOneEntryWithDateRange(new DateTime().minusDays(20).toDate(), now);

		Assert.assertEquals(1, overlappingDateRangeImmunizations(immunizations, now, new DateTime().plusDays(5).toDate()).size());
		Assert.assertEquals(
			1,
			overlappingDateRangeImmunizations(immunizations, new DateTime().minusDays(10).toDate(), new DateTime().minusDays(5).toDate()).size());
		Assert.assertEquals(1, overlappingDateRangeImmunizations(immunizations, new DateTime().minusDays(10).toDate(), null).size());
		Assert.assertEquals(1, overlappingDateRangeImmunizations(immunizations, new DateTime().minusDays(30).toDate(), null).size());
		Assert.assertEquals(1, overlappingDateRangeImmunizations(immunizations, null, new DateTime().minusDays(10).toDate()).size());
		Assert.assertEquals(0, overlappingDateRangeImmunizations(immunizations, null, new DateTime().minusDays(30).toDate()).size());
		Assert.assertEquals(0, overlappingDateRangeImmunizations(immunizations, new DateTime().plusDays(1).toDate(), null).size());
		Assert.assertEquals(
			0,
			overlappingDateRangeImmunizations(immunizations, new DateTime().plusDays(1).toDate(), new DateTime().plusDays(100).toDate()).size());
	}

	@Test
	public void testOverlappingDateRangeImmunizationsForExistingStartDate() {
		final Date now = new Date();
		final List<Immunization> immunizations = getImmunizationListOfOneEntryWithDateRange(new DateTime().minusDays(20).toDate(), null);

		Assert.assertEquals(1, overlappingDateRangeImmunizations(immunizations, now, new DateTime().plusDays(5).toDate()).size());
		Assert.assertEquals(
			1,
			overlappingDateRangeImmunizations(immunizations, new DateTime().minusDays(10).toDate(), new DateTime().minusDays(5).toDate()).size());
		Assert.assertEquals(1, overlappingDateRangeImmunizations(immunizations, new DateTime().minusDays(10).toDate(), null).size());
		Assert.assertEquals(1, overlappingDateRangeImmunizations(immunizations, null, new DateTime().minusDays(10).toDate()).size());
		Assert.assertEquals(0, overlappingDateRangeImmunizations(immunizations, null, new DateTime().minusDays(30).toDate()).size());
		Assert.assertEquals(1, overlappingDateRangeImmunizations(immunizations, new DateTime().plusDays(1).toDate(), null).size());
		Assert.assertEquals(
			1,
			overlappingDateRangeImmunizations(immunizations, new DateTime().plusDays(1).toDate(), new DateTime().plusDays(100).toDate()).size());
	}

	@Test
	public void testOverlappingDateRangeImmunizationsForExistingEndDate() {
		final Date now = new Date();
		final List<Immunization> immunizations = getImmunizationListOfOneEntryWithDateRange(null, now);

		Assert.assertEquals(1, overlappingDateRangeImmunizations(immunizations, now, new DateTime().plusDays(5).toDate()).size());
		Assert.assertEquals(
			1,
			overlappingDateRangeImmunizations(immunizations, new DateTime().minusDays(10).toDate(), new DateTime().minusDays(5).toDate()).size());
		Assert.assertEquals(1, overlappingDateRangeImmunizations(immunizations, new DateTime().minusDays(10).toDate(), null).size());
		Assert.assertEquals(1, overlappingDateRangeImmunizations(immunizations, null, new DateTime().minusDays(10).toDate()).size());
		Assert.assertEquals(1, overlappingDateRangeImmunizations(immunizations, null, new DateTime().minusDays(30).toDate()).size());
		Assert.assertEquals(0, overlappingDateRangeImmunizations(immunizations, new DateTime().plusDays(1).toDate(), null).size());
		Assert.assertEquals(
			0,
			overlappingDateRangeImmunizations(immunizations, new DateTime().plusDays(1).toDate(), new DateTime().plusDays(100).toDate()).size());
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
