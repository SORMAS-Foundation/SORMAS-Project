/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.travelentry;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.Collections;
import java.util.Date;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.QuarantineType;
import de.symeda.sormas.api.travelentry.TravelEntryCriteria;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRoleReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class TravelEntryFacadeEjbPseudonymizationTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf1;
	private TestDataCreator.RDCF rdcf2;
	private UserDto user1;
	private UserDto user2;

	@Override
	public void init() {

		super.init();

		UserRoleReferenceDto newUserRole = creator.createUserRole(
			"NoEventNoCaseView",
			JurisdictionLevel.DISTRICT,
			UserRight.CASE_CLINICIAN_VIEW,
			UserRight.CASE_VIEW,
			UserRight.PERSON_VIEW);

		rdcf1 = creator.createRDCF("Region 1", "District 1", "Community 1", "Facility 1", "Point of entry 1");
		user1 = creator.createUser(
			rdcf1.region.getUuid(),
			rdcf1.district.getUuid(),
			rdcf1.facility.getUuid(),
			"Surv",
			"Off1",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER),
			newUserRole);

		rdcf2 = creator.createRDCF("Region 2", "District 2", "Community 2", "Facility 2", "Point of entry 2");
		user2 = creator.createUser(
			rdcf2.region.getUuid(),
			rdcf2.district.getUuid(),
			rdcf2.facility.getUuid(),
			"Surv",
			"Off2",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER),
			newUserRole);

		loginWith(user2);
	}

	@Test
	public void testGetTravelEntryOutsideJurisdiction() {
		TravelEntryDto travelEntry = createTravelEntry(user1, rdcf1, null);

		assertPseudonymized(getTravelEntryFacade().getByUuid(travelEntry.getUuid()), rdcf1);
		assertPseudonymized(getTravelEntryFacade().getByUuids(Collections.singletonList(travelEntry.getUuid())).get(0), rdcf1);
		assertThat(getTravelEntryFacade().getAllAfter(new Date(0)), hasSize(0));
		assertThat(getTravelEntryFacade().getIndexList(new TravelEntryCriteria(), null, null, null), hasSize(0));
	}

	@Test
	public void testGetReportOfCaseWithSpecialAccess() {
		CaseDataDto caze = creator.createCase(user1.toReference(), creator.createPerson().toReference(), rdcf1);
		TravelEntryDto travelEntry = createTravelEntry(user1, rdcf1, caze);
		creator.createSpecialCaseAccess(caze.toReference(), user1.toReference(), user2.toReference(), DateHelper.addDays(new Date(), 1));

		assertNotPseudonymized(getTravelEntryFacade().getByUuid(travelEntry.getUuid()), user1, rdcf1);
		assertNotPseudonymized(getTravelEntryFacade().getByUuids(Collections.singletonList(travelEntry.getUuid())).get(0), user1, rdcf1);
		assertNotPseudonymized(getTravelEntryFacade().getAllAfter(new Date(0)).get(0), user1, rdcf1);
		assertThat(getTravelEntryFacade().getIndexList(new TravelEntryCriteria(), null, null, null).get(0).isPseudonymized(), is(false));
	}

	private void assertPseudonymized(TravelEntryDto travelEntry, TestDataCreator.RDCF rdcf) {
		assertThat(travelEntry.isPseudonymized(), is(true));
		assertThat(travelEntry.getReportingUser(), is(nullValue()));
		assertThat(travelEntry.getDisease(), is(Disease.CORONAVIRUS));
		assertThat(travelEntry.getResponsibleRegion(), is(rdcf.region));
		assertThat(travelEntry.getResponsibleDistrict(), is(rdcf.district));
		assertThat(travelEntry.getResponsibleCommunity(), is(nullValue()));
		assertThat(travelEntry.getPointOfEntry(), is(nullValue()));
		assertThat(travelEntry.getPointOfEntryDetails(), is(""));
		assertThat(travelEntry.getQuarantine(), is(QuarantineType.OTHER));
		assertThat(travelEntry.getQuarantineTypeDetails(), is(""));
		assertThat(travelEntry.getQuarantineHelpNeeded(), is(""));
	}

	private void assertNotPseudonymized(TravelEntryDto travelEntry, UserDto user, TestDataCreator.RDCF rdcf) {
		assertThat(travelEntry.isPseudonymized(), is(false));
		assertThat(travelEntry.getReportingUser(), is(user.toReference()));
		assertThat(travelEntry.getDisease(), is(Disease.CORONAVIRUS));
		assertThat(travelEntry.getResponsibleRegion(), is(rdcf.region));
		assertThat(travelEntry.getResponsibleDistrict(), is(rdcf.district));
		assertThat(travelEntry.getResponsibleCommunity(), is(rdcf.community));
		assertThat(travelEntry.getPointOfEntry(), is(rdcf.pointOfEntry));
		assertThat(travelEntry.getPointOfEntryDetails(), is("Test point of entry details"));
		assertThat(travelEntry.getQuarantine(), is(QuarantineType.OTHER));
		assertThat(travelEntry.getQuarantineTypeDetails(), is("Test quarantine type details"));
		assertThat(travelEntry.getQuarantineHelpNeeded(), is("Test quarantine help needed"));
	}

	private TravelEntryDto createTravelEntry(UserDto user, TestDataCreator.RDCF rdcf, CaseDataDto caze) {
		TravelEntryDto travelEntry = TravelEntryDto.build(caze == null ? creator.createPerson().toReference() : caze.getPerson());
		travelEntry.setReportDate(new Date());
		travelEntry.setDateOfArrival(new Date());
		travelEntry.setReportingUser(user.toReference());
		travelEntry.setDisease(Disease.CORONAVIRUS);
		travelEntry.setResponsibleRegion(rdcf.region);
		travelEntry.setResponsibleDistrict(rdcf.district);
		travelEntry.setResponsibleCommunity(rdcf.community);
		travelEntry.setPointOfEntry(rdcf.pointOfEntry);
		travelEntry.setPointOfEntryDetails("Test point of entry details");
		travelEntry.setQuarantine(QuarantineType.OTHER);
		travelEntry.setQuarantineTypeDetails("Test quarantine type details");
		travelEntry.setQuarantineHelpNeeded("Test quarantine help needed");

		return getTravelEntryFacade().save(travelEntry);
	}
}
