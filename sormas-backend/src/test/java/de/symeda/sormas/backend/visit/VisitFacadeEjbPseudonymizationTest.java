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

package de.symeda.sormas.backend.visit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.nullValue;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.VisitOrigin;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.visit.VisitCriteria;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitExportDto;
import de.symeda.sormas.api.visit.VisitExportType;
import de.symeda.sormas.api.visit.VisitIndexDto;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.symptoms.Symptoms;

public class VisitFacadeEjbPseudonymizationTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf1;
	private TestDataCreator.RDCF rdcf2;
	private UserDto user1;
	private UserDto user2;
	private UserDto nationalVisitUser;
	private PersonDto person;

	@Override
	public void init() {
		super.init();

		rdcf1 = creator.createRDCF("Region 1", "District 1", "Community 1", "Facility 1", "Point of entry 1");
		user1 = creator.createUser(
			rdcf1.region.getUuid(),
			rdcf1.district.getUuid(),
			rdcf1.facility.getUuid(),
			"Surv",
			"Off1",
			creator.getUserRoleReference(DefaultUserRole.CONTACT_OFFICER),
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER));

		rdcf2 = creator.createRDCF("Region 2", "District 2", "Community 2", "Facility 2", "Point of entry 2");
		user2 = creator.createUser(
			rdcf2.region.getUuid(),
			rdcf2.district.getUuid(),
			rdcf2.facility.getUuid(),
			"Surv",
			"Off2",
			creator.getUserRoleReference(DefaultUserRole.CONTACT_OFFICER),
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER));

		nationalVisitUser =
			creator.createUser(null, null, null, "National", "Visit User", "National Visit User", JurisdictionLevel.NATION, UserRight.VISIT_EDIT);

		loginWith(user2);

		person = creator.createPerson("John", "Smith");
	}

	@Test
	public void testGetVisitInJurisdiction() {
		VisitDto visit = createVisit(user2, person).visit;

		assertNotPseudonymized(getVisitFacade().getByUuid(visit.getUuid()));
	}

	@Test
	public void testGetVisitOutsideJurisdiction() {
		VisitDto visit = createVisit(user1, person).visit;

		assertPseudonymized(getVisitFacade().getByUuid(visit.getUuid()));
	}

	@Test
	public void testPseudonymizeIndexList() {
		PersonDto newPerson = creator.createPerson("First", "Last");
		ContactVisit contactVisit1 = createVisit(user1, newPerson);
		ContactVisit contactVisit2 = createVisit(user2, person);

		List<VisitIndexDto> indexList1 =
			getVisitFacade().getIndexList(new VisitCriteria().contact(contactVisit1.contact.toReference()), 0, 100, null);
		VisitIndexDto index1 = indexList1.get(0);
		assertThat(index1.getVisitRemarks(), is("Confidential"));

		List<VisitIndexDto> indexList2 =
			getVisitFacade().getIndexList(new VisitCriteria().contact(contactVisit2.contact.toReference()), 0, 100, null);
		VisitIndexDto index2 = indexList2.get(0);
		assertThat(index2.getVisitRemarks(), is("Test remarks"));
	}

	@Test
	public void testPseudonymizeExportList() {
		PersonDto newPerson = creator.createPerson("First", "Last");
		VisitDto visit1 = createVisit(user1, newPerson).visit;
		VisitDto visit2 = createVisit(user2, person).visit;

		List<VisitExportDto> exportList =
			getVisitFacade().getVisitsExportList(new VisitCriteria(), Collections.emptySet(), VisitExportType.CONTACT_VISITS, 0, 100, null);

		VisitExportDto export1 = exportList.stream().filter(v -> v.getUuid().equals(visit1.getUuid())).findFirst().get();
		assertThat(export1.getFirstName(), isEmptyString());
		assertThat(export1.getLastName(), isEmptyString());

		//sensitive data
		assertThat(export1.getReportLat(), is(nullValue()));
		assertThat(export1.getReportLon(), is(nullValue()));

		VisitExportDto export2 = exportList.stream().filter(v -> v.getUuid().equals(visit2.getUuid())).findFirst().get();
		assertThat(export2.getFirstName(), is("John"));
		assertThat(export2.getLastName(), is("Smith"));

		//sensitive data
		assertThat(export2.getReportLat(), is(46.432));
		assertThat(export2.getReportLon(), is(23.234));
	}

	@Test
	public void testUpdatePseudonymized() {
		VisitDto visit = createVisit(user2, person).visit;

		loginWith(nationalVisitUser);

		visit.setReportLat(null);
		visit.setReportLon(null);
		visit.setReportLatLonAccuracy(20F);
		visit.getSymptoms().setPatientIllLocation(null);
		visit.getSymptoms().setOtherHemorrhagicSymptomsText(null);

		getVisitFacade().save(visit);

		Visit updated = getVisitService().getByUuid(visit.getUuid());

		assertThat(updated.getReportLat(), is(46.432));
		assertThat(updated.getReportLon(), is(23.234));
		assertThat(updated.getReportLatLonAccuracy(), is(20f));

		Symptoms symptoms = getSymptomsService().getByUuid(visit.getSymptoms().getUuid());
		assertThat(symptoms.getPatientIllLocation(), is("Test ill location"));
		assertThat(symptoms.getOtherHemorrhagicSymptomsText(), is("OtherHemorrhagic"));
	}

	@Test
	public void testUpdateWithPseudonymizedDto() {
		VisitDto visit = createVisit(user2, person).visit;

		visit.setPseudonymized(true);
		visit.setReportLat(null);
		visit.setReportLon(null);
		visit.setReportLatLonAccuracy(20F);

		getVisitFacade().save(visit);

		Visit updated = getVisitService().getByUuid(visit.getUuid());

		assertThat(updated.getReportLat(), is(46.432));
		assertThat(updated.getReportLon(), is(23.234));
		assertThat(updated.getReportLatLonAccuracy(), is(20f));
	}

	@Test
	public void testGetVisitOnCaseWithSpecialAccess() {
		CaseDataDto caze = creator.createCase(user1.toReference(), person.toReference(), rdcf1);
		VisitDto visit = creator.createVisit(caze.getDisease(), person.toReference());
		creator.createSpecialCaseAccess(caze.toReference(), user1.toReference(), user2.toReference(), DateHelper.addDays(new Date(), 1));

		assertThat(getVisitFacade().getByUuid(visit.getUuid()).isPseudonymized(), is(false));
		assertThat(getVisitFacade().getByUuids(Collections.singletonList(visit.getUuid())).get(0).isPseudonymized(), is(false));
		assertThat(getVisitFacade().getIndexList(new VisitCriteria().caze(caze.toReference()), null, null, null).get(0).isPseudonymized(), is(false));
		assertThat(
			getVisitFacade().getVisitsExportList(new VisitCriteria().caze(caze.toReference()), null, null, 0, Integer.MAX_VALUE, null)
				.get(0)
				.getFirstName(),
			is(person.getFirstName()));
		// user filter for case visits is not implemented
		assertThat(getVisitFacade().getAllActiveVisitsAfter(new Date(0)), hasSize(0));
		assertThat(getVisitFacade().getAllAfter(new Date(0)), hasSize(0));
	}

	private ContactVisit createVisit(UserDto visitUser, PersonDto person) {
		ContactDto contact = creator.createContact(visitUser.toReference(), person.toReference(), Disease.CORONAVIRUS, c -> {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MONTH, 1);

			c.setFollowUpUntil(calendar.getTime());
		});

		VisitDto visit =
			creator.createVisit(Disease.CORONAVIRUS, person.toReference(), new Date(), VisitStatus.COOPERATIVE, VisitOrigin.USER, (v) -> {
				v.setVisitUser(visitUser.toReference());
				v.setReportLat(46.432);
				v.setReportLon(23.234);
				v.setReportLatLonAccuracy(10f);

				v.getSymptoms().setPatientIllLocation("Test ill location");
				v.getSymptoms().setOtherHemorrhagicSymptoms(SymptomState.YES);
				v.getSymptoms().setOtherHemorrhagicSymptomsText("OtherHemorrhagic");

				v.setVisitRemarks("Test remarks");
			});

		return new ContactVisit(contact, visit);
	}

	private static final class ContactVisit {

		final ContactDto contact;

		final VisitDto visit;

		public ContactVisit(ContactDto contact, VisitDto visit) {
			this.contact = contact;
			this.visit = visit;
		}
	}

	private void assertNotPseudonymized(VisitDto visit) {
		assertThat(visit.getPerson().getFirstName(), is("John"));
		assertThat(visit.getPerson().getLastName(), is("Smith"));

		// sensitive data
		assertThat(visit.getVisitUser(), is(user2));
		assertThat(visit.getVisitRemarks(), is("Test remarks"));
		assertThat(visit.getReportLat(), is(46.432));
		assertThat(visit.getReportLon(), is(23.234));
		assertThat(visit.getReportLatLonAccuracy(), is(10F));
	}

	private void assertPseudonymized(VisitDto visit) {
		assertThat(visit.isPseudonymized(), is(true));
		assertThat(visit.getPerson().getFirstName(), isEmptyString());
		assertThat(visit.getPerson().getLastName(), isEmptyString());

		// sensitive data
		assertThat(visit.getVisitUser(), is(nullValue()));
		assertThat(visit.getVisitRemarks(), isEmptyString());
		assertThat(visit.getReportLat(), is(nullValue()));
		assertThat(visit.getReportLon(), is(nullValue()));

		assertThat(visit.getReportLatLonAccuracy(), is(10F));
	}
}
