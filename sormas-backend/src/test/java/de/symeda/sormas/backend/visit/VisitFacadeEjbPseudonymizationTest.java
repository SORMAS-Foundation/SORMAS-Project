/*
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.visit;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.visit.VisitCriteria;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitExportDto;
import de.symeda.sormas.api.visit.VisitExportType;
import de.symeda.sormas.api.visit.VisitIndexDto;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.symptoms.Symptoms;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VisitFacadeEjbPseudonymizationTest extends AbstractBeanTest {
	private TestDataCreator.RDCF rdcf1;
	private TestDataCreator.RDCF rdcf2;
	private UserDto user1;
	private UserDto user2;
	private PersonDto person;

	@Override
	public void init() {
		super.init();

		rdcf1 = creator.createRDCF("Region 1", "District 1", "Community 1", "Facility 1", "Point of entry 1");
		user1 = creator.createUser(rdcf1.region.getUuid(), rdcf1.district.getUuid(), rdcf1.facility.getUuid(),
				"Surv", "Off1", UserRole.SURVEILLANCE_OFFICER);

		rdcf2 = creator.createRDCF("Region 2", "District 2", "Community 2", "Facility 2", "Point of entry 2");
		user2 = creator.createUser(rdcf2.region.getUuid(), rdcf2.district.getUuid(), rdcf2.facility.getUuid(),
				"Surv", "Off2", UserRole.SURVEILLANCE_OFFICER);
		when(MockProducer.getPrincipal().getName()).thenReturn("SurvOff2");

		person = creator.createPerson("John", "Smith");
	}

	@Test
	public void testGetVisitInJurisdiction(){
		VisitDto visit = createVisit(user2, creator.createContact(user2.toReference(), person.toReference(), Disease.CORONAVIRUS), person);

		assertNotPseudonymized(getVisitFacade().getVisitByUuid(visit.getUuid()));
	}

	@Test
	public void testGetVisitOutsideJurisdiction(){
		VisitDto visit = createVisit(user1, creator.createContact(user1.toReference(), person.toReference(), Disease.CORONAVIRUS), person);

		assertPseudonymized(getVisitFacade().getVisitByUuid(visit.getUuid()));
	}

	@Test
	public void testPseudonymizeIndexList(){
		PersonDto newPerson = creator.createPerson("First", "Last");
		ContactDto contact1 = creator.createContact(user1.toReference(), newPerson.toReference(), Disease.CORONAVIRUS);
		createVisit(user1, contact1, newPerson);
		ContactDto contact2 = creator.createContact(user2.toReference(), person.toReference(), Disease.CORONAVIRUS);
		createVisit(user2, contact2, person);

		List<VisitIndexDto> indexList1 = getVisitFacade().getIndexList(new VisitCriteria().contact(contact1.toReference()), 0, 100, null);
		VisitIndexDto index1 = indexList1.get(0);
		assertThat(index1.getVisitRemarks(), isEmptyString());

		List<VisitIndexDto> indexList2 = getVisitFacade().getIndexList(new VisitCriteria().contact(contact2.toReference()), 0, 100, null);
		VisitIndexDto index2 = indexList2.get(0);
		assertThat(index2.getVisitRemarks(), is("Test remarks"));
	}

	@Test
	public void testPseudonymizeExportList(){
		PersonDto newPerson = creator.createPerson("First", "Last");
		VisitDto visit1 = createVisit(user1, creator.createContact(user1.toReference(), newPerson.toReference(), Disease.CORONAVIRUS), newPerson);
		VisitDto visit2 = createVisit(user2, creator.createContact(user2.toReference(), person.toReference(), Disease.CORONAVIRUS), person);

		List<VisitExportDto> exportList = getVisitFacade().getVisitsExportList(new VisitCriteria(), VisitExportType.CONTACT_VISITS, 0, 100, null);

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
		assertThat(export2.getReportLat(), is(43.532));
		assertThat(export2.getReportLon(), is(23.4332));
	}

	@Test
	public void testUpdatePseudonymized(){
		VisitDto visit = createVisit(user1, creator.createContact(user1.toReference(), person.toReference(), Disease.CORONAVIRUS), person);

		visit.setReportLat(null);
		visit.setReportLon(null);
		visit.setReportLatLonAccuracy(null);
		visit.getSymptoms().setPatientIllLocation(null);
		visit.getSymptoms().setOtherHemorrhagicSymptomsText(null);

		getVisitFacade().saveVisit(visit);

		Visit updated = getVisitService().getByUuid(visit.getUuid());

		assertThat(updated.getReportLat(), is(43.532));
		assertThat(updated.getReportLon(), is(23.4332));
		assertThat(updated.getReportLatLonAccuracy(), is(10f));

		Symptoms symptoms = getSymptomsService().getByUuid(visit.getSymptoms().getUuid());
		assertThat(symptoms.getPatientIllLocation(), is("Test ill location"));
		assertThat(symptoms.getOtherHemorrhagicSymptomsText(), is("OtherHemorrhagic"));
	}

	@Test
	public void testUpdateWithPseudonymizedDto(){
		VisitDto visit = createVisit(user2, creator.createContact(user2.toReference(), person.toReference(), Disease.CORONAVIRUS), person);

		visit.setPseudonymized(true);
		visit.setReportLat(null);
		visit.setReportLon(null);
		visit.setReportLatLonAccuracy(null);

		getVisitFacade().saveVisit(visit);

		Visit updated = getVisitService().getByUuid(visit.getUuid());

		assertThat(updated.getReportLat(), is(43.532));
		assertThat(updated.getReportLon(), is(23.4332));
		assertThat(updated.getReportLatLonAccuracy(), is(10f));
	}

	private VisitDto createVisit(UserDto visitUser, ContactDto contact, PersonDto person) {
		VisitDto visitDto = creator.createVisit(Disease.CORONAVIRUS, person.toReference(), new Date(), VisitStatus.COOPERATIVE, (v) -> {
			v.setVisitUser(visitUser.toReference());
			v.setReportLat(43.532);
			v.setReportLon(23.4332);
			v.setReportLatLonAccuracy(10f);

			v.getSymptoms().setPatientIllLocation("Test ill location");
			v.getSymptoms().setOtherHemorrhagicSymptoms(SymptomState.YES);
			v.getSymptoms().setOtherHemorrhagicSymptomsText("OtherHemorrhagic");

			v.setVisitRemarks("Test remarks");
		});

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, 1);
		contact.setFollowUpUntil(calendar.getTime());

		getContactFacade().saveContact(contact);

		return visitDto;
	}

	private void assertNotPseudonymized(VisitDto visit){
		assertThat(visit.getPerson().getFirstName(), is("John"));
		assertThat(visit.getPerson().getLastName(), is("Smith"));

		// sensitive data
		assertThat(visit.getVisitUser(), is(user2));
		assertThat(visit.getVisitRemarks(), is("Test remarks"));
		assertThat(visit.getReportLat(), is(43.532));
		assertThat(visit.getReportLon(), is(23.4332));
		assertThat(visit.getReportLatLonAccuracy(), is(10F));
	}

	private void assertPseudonymized(VisitDto visit){
		assertThat(visit.isPseudonymized(), is(true));
		assertThat(visit.getPerson().getFirstName(), isEmptyString());
		assertThat(visit.getPerson().getLastName(), isEmptyString());

		// sensitive data
		assertThat(visit.getVisitUser(), is(nullValue()));
		assertThat(visit.getVisitRemarks(), isEmptyString());
		assertThat(visit.getReportLat(), is(nullValue()));
		assertThat(visit.getReportLon(), is(nullValue()));
		assertThat(visit.getReportLatLonAccuracy(), is(nullValue()));
	}
}
