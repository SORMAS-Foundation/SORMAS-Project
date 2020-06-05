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
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

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
		person = creator.createPerson("John", "Smith");
		when(MockProducer.getPrincipal().getName()).thenReturn("SurvOff2");
	}

	@Test
	public void testGetVisitInJurisdiction(){
		VisitDto visit = createVisit(user2, creator.createContact(user2.toReference(), person.toReference()));

		assertNotPseudonymized(getVisitFacade().getVisitByUuid(visit.getUuid()));
	}

	@Test
	public void testGetVisitOutsideJurisdiction(){
		VisitDto visit = createVisit(user1, creator.createContact(user1.toReference(), person.toReference()));

		assertPseudonymized(getVisitFacade().getVisitByUuid(visit.getUuid()));
	}

	private VisitDto createVisit(UserDto visitUser, ContactDto contact) {
		VisitDto visitDto = creator.createVisit(Disease.CORONAVIRUS, person.toReference(), visitUser.toReference());

		Visit visit = getVisitService().getByUuid(visitDto.getUuid());
		visit.getContacts().add(getContactService().getByUuid(contact.getUuid()));

		getVisitService().ensurePersisted(visit);

		return getVisitFacade().getVisitByUuid(visitDto.getUuid());
	}

	private void assertNotPseudonymized(VisitDto visit){
		assertThat(visit.getPerson().getFirstName(), is("John"));
		assertThat(visit.getPerson().getLastName(), is("Smith"));

		// sensitive data
		assertThat(visit.getVisitUser(), is(user2));
		assertThat(visit.getReportLat(), is(43.532));
		assertThat(visit.getReportLon(), is(23.4332));
		assertThat(visit.getReportLatLonAccuracy(), is(10F));
	}

	private void assertPseudonymized(VisitDto visit){
		assertThat(visit.getPerson().getFirstName(), isEmptyString());
		assertThat(visit.getPerson().getLastName(), isEmptyString());

		// sensitive data
		assertThat(visit.getVisitUser(), is(nullValue()));
		assertThat(visit.getReportLat(), is(nullValue()));
		assertThat(visit.getReportLon(), is(nullValue()));
		assertThat(visit.getReportLatLonAccuracy(), is(nullValue()));
	}
}
