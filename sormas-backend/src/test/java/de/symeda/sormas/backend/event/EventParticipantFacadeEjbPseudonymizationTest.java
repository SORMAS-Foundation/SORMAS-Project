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

package de.symeda.sormas.backend.event;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;

@RunWith(MockitoJUnitRunner.class)
public class EventParticipantFacadeEjbPseudonymizationTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf1;
	private TestDataCreator.RDCF rdcf2;
	private UserDto user1;
	private UserDto user2;

	@Override
	public void init() {

		super.init();

		rdcf1 = creator.createRDCF("Region 1", "District 1", "Community 1", "Facility 1", "Point of entry 1");
		user1 = creator
			.createUser(rdcf1.region.getUuid(), rdcf1.district.getUuid(), rdcf1.facility.getUuid(), "Surv", "Off1", UserRole.SURVEILLANCE_OFFICER);

		rdcf2 = creator.createRDCF("Region 2", "District 2", "Community 2", "Facility 2", "Point of entry 2");
		user2 = creator
			.createUser(rdcf2.region.getUuid(), rdcf2.district.getUuid(), rdcf2.facility.getUuid(), "Surv", "Off2", UserRole.SURVEILLANCE_OFFICER);

		when(MockProducer.getPrincipal().getName()).thenReturn("SurvOff2");
	}

	@Test
	public void testEventInJurisdiction() {
		EventParticipantDto eventParticipant = createEventParticipant(user2, rdcf2);

		assertNotPseudonymized(getEventParticipantFacade().getEventParticipantByUuid(eventParticipant.getUuid()));
	}

	@Test
	public void testEventOutsideJurisdiction() {
		EventParticipantDto eventParticipant = createEventParticipant(user1, rdcf1);

		assertPseudonymized(getEventParticipantFacade().getEventParticipantByUuid(eventParticipant.getUuid()));
	}

	@Test
	public void testPseudonymizeGetByUuids() {
		EventParticipantDto eventParticipant1 = createEventParticipant(user2, rdcf2);
		EventParticipantDto eventParticipant2 = createEventParticipant(user1, rdcf1);

		List<EventParticipantDto> participants =
			getEventParticipantFacade().getByUuids(Arrays.asList(eventParticipant1.getUuid(), eventParticipant2.getUuid()));

		assertNotPseudonymized(participants.stream().filter(p -> p.getUuid().equals(eventParticipant1.getUuid())).findFirst().get());
		assertPseudonymized(participants.stream().filter(p -> p.getUuid().equals(eventParticipant2.getUuid())).findFirst().get());
	}

	@Test
	public void testUpdateOutsideJurisdiction() {
		EventParticipantDto participant = createEventParticipant(user1, rdcf1);

		participant.setInvolvementDescription(null);
		participant.getPerson().setFirstName(null);
		participant.getPerson().setLastName(null);
		participant.getPerson().getAddress().setAddress(null);
		participant.getPerson().getAddress().setCity(null);

		getEventParticipantFacade().saveEventParticipant(participant);

		EventParticipant saved = getEventParticipantService().getByUuid(participant.getUuid());

		assertThat(saved.getInvolvementDescription(), is("Test involvement descr"));
		assertThat(saved.getPerson().getFirstName(), is("John"));
		assertThat(saved.getPerson().getLastName(), is("Smith"));
		assertThat(saved.getPerson().getAddress().getAddress(), is("Test Address"));
		assertThat(saved.getPerson().getAddress().getCity(), is("Test City"));
	}

	@Test
	public void testUpdateWithPseudonymizedDto() {
		EventParticipantDto participant = createEventParticipant(user1, rdcf1);

		participant.setPseudonymized(true);
		participant.setInvolvementDescription(null);
		participant.getPerson().setFirstName(null);
		participant.getPerson().setLastName(null);
		participant.getPerson().getAddress().setAddress(null);
		participant.getPerson().getAddress().setCity(null);

		getEventParticipantFacade().saveEventParticipant(participant);

		EventParticipant saved = getEventParticipantService().getByUuid(participant.getUuid());

		assertThat(saved.getInvolvementDescription(), is("Test involvement descr"));
		assertThat(saved.getPerson().getFirstName(), is("John"));
		assertThat(saved.getPerson().getLastName(), is("Smith"));
		assertThat(saved.getPerson().getAddress().getAddress(), is("Test Address"));
		assertThat(saved.getPerson().getAddress().getCity(), is("Test City"));
	}

	private EventParticipantDto createEventParticipant(UserDto user, TestDataCreator.RDCF rdcf) {
		EventDto event = creator.createEvent(EventStatus.POSSIBLE, "", user.toReference(), e -> {
			e.getEventLocation().setRegion(rdcf.region);
			e.getEventLocation().setDistrict(rdcf.district);
			e.getEventLocation().setCommunity(rdcf.community);
		});

		PersonDto person = creator.createPerson("John", "Smith", Sex.MALE, null, null, 23, p -> {
			p.getAddress().setAddress("Test Address");
			p.getAddress().setCity("Test City");
		});

		return creator.createEventParticipant(event.toReference(), person, "Test involvement descr");
	}

	private void assertNotPseudonymized(EventParticipantDto eventParticipant) {
		assertThat(eventParticipant.getInvolvementDescription(), is("Test involvement descr"));
		assertThat(eventParticipant.getPerson().getFirstName(), is("John"));
		assertThat(eventParticipant.getPerson().getLastName(), is("Smith"));
		assertThat(eventParticipant.getPerson().getAddress().getAddress(), is("Test Address"));
		assertThat(eventParticipant.getPerson().getAddress().getCity(), is("Test City"));
	}

	private void assertPseudonymized(EventParticipantDto eventParticipant) {
		assertThat(eventParticipant.getInvolvementDescription(), isEmptyString());
		assertThat(eventParticipant.getPerson().getFirstName(), isEmptyString());
		assertThat(eventParticipant.getPerson().getLastName(), isEmptyString());
		assertThat(eventParticipant.getPerson().getAddress().getAddress(), isEmptyString());
		assertThat(eventParticipant.getPerson().getAddress().getCity(), isEmptyString());

	}
}
