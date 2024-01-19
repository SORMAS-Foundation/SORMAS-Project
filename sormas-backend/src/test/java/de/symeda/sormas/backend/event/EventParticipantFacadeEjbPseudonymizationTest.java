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
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventInvestigationStatus;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class EventParticipantFacadeEjbPseudonymizationTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf1;
	private TestDataCreator.RDCF rdcf2;
	private UserDto user1;
	private UserDto user2;

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
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER));

		rdcf2 = creator.createRDCF("Region 2", "District 2", "Community 2", "Facility 2", "Point of entry 2");
		user2 = creator.createUser(
			rdcf2.region.getUuid(),
			rdcf2.district.getUuid(),
			rdcf2.facility.getUuid(),
			"Surv",
			"Off2",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER));

		loginWith(user2);
	}

	@Test
	public void testEventParticipantInJurisdiction() {
		EventParticipantDto eventParticipant = createEventParticipant(user2, rdcf2);

		assertNotPseudonymized(getEventParticipantFacade().getEventParticipantByUuid(eventParticipant.getUuid()));
	}

	@Test
	public void testEventParticipantOutsideJurisdiction() {
		EventParticipantDto eventParticipant = createEventParticipant(user1, rdcf1);

		assertPseudonymized(getEventParticipantFacade().getEventParticipantByUuid(eventParticipant.getUuid()));
	}

	@Test
	public void testPseudonymizedGetByUuidWithLimitedUser() {
		loginWith(nationalAdmin);

		// event within limited user's jurisdiction
		EventParticipantDto eventParticipant1 = createEventParticipant(user1, rdcf1);
		EventDto eventDto1 = getEventFacade().getEventByUuid(eventParticipant1.getEvent().getUuid(), false);
		eventDto1.setConnectionNumber("123");
		getEventFacade().save(eventDto1);

		// event outside limited user's jurisdiction
		EventParticipantDto eventParticipant2 = createEventParticipant(user2, rdcf2);
		EventDto eventDto2 = getEventFacade().getEventByUuid(eventParticipant2.getEvent().getUuid(), false);
		eventDto2.setConnectionNumber("456");
		getEventFacade().save(eventDto2);

		UserDto surveillanceOfficerWithRestrictedAccessToAssignedEntities =
			creator.createSurveillanceOfficerWithRestrictedAccessToAssignedEntities(rdcf1);

		loginWith(surveillanceOfficerWithRestrictedAccessToAssignedEntities);
		final EventDto testEvent1 = getEventFacade().getEventByUuid(eventDto1.getUuid(), false);
		assertThat(testEvent1.isPseudonymized(), is(true));
		assertThat(testEvent1.getConnectionNumber(), is(emptyString()));
		final EventParticipantDto testEventParticipant1 = getEventParticipantFacade().getEventParticipantByUuid(eventParticipant1.getUuid());
		assertThat(testEventParticipant1.isPseudonymized(), is(true));
		assertThat(testEventParticipant1.getPerson().getFirstName(), is("Confidential"));

		final EventDto testEvent1Second = getEventFacade().getEventByUuid(eventDto2.getUuid(), false);
		assertThat(testEvent1Second.isPseudonymized(), is(true));
		assertThat(testEvent1Second.getConnectionNumber(), is(emptyString()));
		final EventParticipantDto testEventParticipant1Second = getEventParticipantFacade().getEventParticipantByUuid(eventParticipant2.getUuid());
		assertThat(testEventParticipant1Second.isPseudonymized(), is(true));
		assertThat(testEventParticipant1Second.getPerson().getFirstName(), is("Confidential"));

		// event created by limited user in the same jurisdiction
		EventParticipantDto eventParticipant3 = createEventParticipant(surveillanceOfficerWithRestrictedAccessToAssignedEntities, rdcf1);
		EventDto eventDto3 = getEventFacade().getEventByUuid(eventParticipant3.getEvent().getUuid(), false);
		eventDto3.setConnectionNumber("789");
		getEventFacade().save(eventDto3);

		// event created by limited user outside it's jurisdiction
		EventParticipantDto eventParticipant4 = createEventParticipant(surveillanceOfficerWithRestrictedAccessToAssignedEntities, rdcf2);
		EventDto eventDto4 = getEventFacade().getEventByUuid(eventParticipant4.getEvent().getUuid(), false);
		eventDto4.setConnectionNumber("987");
		getEventFacade().save(eventDto4);

		loginWith(nationalAdmin);
		testEvent1.setResponsibleUser(surveillanceOfficerWithRestrictedAccessToAssignedEntities.toReference());
		getEventFacade().save(testEvent1);
		testEvent1Second.setResponsibleUser(surveillanceOfficerWithRestrictedAccessToAssignedEntities.toReference());
		getEventFacade().save(testEvent1Second);

		loginWith(surveillanceOfficerWithRestrictedAccessToAssignedEntities);
		final EventDto testEvent3 = getEventFacade().getEventByUuid(eventDto1.getUuid(), false);
		assertThat(testEvent3.isPseudonymized(), is(false));
		assertThat(testEvent3.getConnectionNumber(), is("123"));
		final EventParticipantDto testEventParticipant3 = getEventParticipantFacade().getEventParticipantByUuid(eventParticipant1.getUuid());
		assertThat(testEventParticipant3.isPseudonymized(), is(false));
		assertThat(testEventParticipant3.getPerson().getFirstName(), is("John"));

		final EventDto testEvent3Second = getEventFacade().getEventByUuid(eventDto2.getUuid(), false);
		assertThat(testEvent3Second.isPseudonymized(), is(false));
		assertThat(testEvent3Second.getConnectionNumber(), is("456"));
		final EventParticipantDto testEventParticipant3Second = getEventParticipantFacade().getEventParticipantByUuid(eventParticipant2.getUuid());
		assertThat(testEventParticipant3Second.isPseudonymized(), is(false));
		assertThat(testEventParticipant3Second.getPerson().getFirstName(), is("John"));

		final EventDto testEvent3Third = getEventFacade().getEventByUuid(eventDto3.getUuid(), false);
		assertThat(testEvent3Third.isPseudonymized(), is(false));
		assertThat(testEvent3Third.getConnectionNumber(), is("789"));
		final EventParticipantDto testEventParticipant3Third = getEventParticipantFacade().getEventParticipantByUuid(eventParticipant3.getUuid());
		assertThat(testEventParticipant3Third.isPseudonymized(), is(false));
		assertThat(testEventParticipant3Third.getPerson().getFirstName(), is("John"));

		final EventDto testEvent3Fourth = getEventFacade().getEventByUuid(eventDto4.getUuid(), false);
		assertThat(testEvent3Fourth.isPseudonymized(), is(false));
		assertThat(testEvent3Fourth.getConnectionNumber(), is("987"));
		final EventParticipantDto testEventParticipant3Fourth = getEventParticipantFacade().getEventParticipantByUuid(eventParticipant4.getUuid());
		assertThat(testEventParticipant3Fourth.isPseudonymized(), is(false));
		assertThat(testEventParticipant3Fourth.getPerson().getFirstName(), is("John"));
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

		participant.setInvolvementDescription("");
		participant.getPerson().setFirstName("James");
		participant.getPerson().setLastName("Doe");
		participant.getPerson().getAddress().setStreet(null);
		participant.getPerson().getAddress().setHouseNumber(null);
		participant.getPerson().getAddress().setAdditionalInformation(null);
		participant.getPerson().getAddress().setCity(null);

		// saving event participant should be done in 2 steps: person and participant

		getPersonFacade().save(participant.getPerson());

		// personal and sensitive data should not be updated
		loginWith(user1);
		PersonDto savedPerson = getPersonFacade().getByUuid(participant.getPerson().getUuid());

		assertThat(savedPerson.getFirstName(), is("John"));
		assertThat(savedPerson.getLastName(), is("Smith"));
		assertThat(savedPerson.getAddress().getStreet(), is("Test Street"));
		assertThat(savedPerson.getAddress().getHouseNumber(), is("Test Number"));
		assertThat(savedPerson.getAddress().getAdditionalInformation(), is("Test Information"));
		assertThat(savedPerson.getAddress().getCity(), is("Test City"));

		loginWith(user2);
		// saving of event participant should not be possible
		assertThrowsWithMessage(
			AccessDeniedException.class,
			"This event participant is not editable any more",
			() -> getEventParticipantFacade().save(participant));
	}

	@Test
	public void testUpdateWithPseudonymizedDto() {
		EventParticipantDto participant = createEventParticipant(user2, rdcf2);

		participant.setPseudonymized(true);
		participant.setReportingUser(null);
		participant.setInvolvementDescription(null);
		participant.getPerson().setFirstName(null);
		participant.getPerson().setLastName(null);
		participant.getPerson().getAddress().setStreet(null);
		participant.getPerson().getAddress().setHouseNumber(null);
		participant.getPerson().getAddress().setAdditionalInformation(null);
		participant.getPerson().getAddress().setCity(null);

		getEventParticipantFacade().save(participant);

		EventParticipant saved = getEventParticipantService().getByUuid(participant.getUuid());

		assertThat(saved.getReportingUser(), is(notNullValue()));
		assertThat(saved.getInvolvementDescription(), is("Test involvement descr"));
		assertThat(saved.getPerson().getFirstName(), is("John"));
		assertThat(saved.getPerson().getLastName(), is("Smith"));
		assertThat(saved.getPerson().getAddress().getStreet(), is("Test Street"));
		assertThat(saved.getPerson().getAddress().getHouseNumber(), is("Test Number"));
		assertThat(saved.getPerson().getAddress().getAdditionalInformation(), is("Test Information"));
		assertThat(saved.getPerson().getAddress().getCity(), is("Test City"));
	}

	private EventParticipantDto createEventParticipant(UserDto user, TestDataCreator.RDCF rdcf) {
		EventDto event = creator.createEvent(EventStatus.SIGNAL, EventInvestigationStatus.PENDING, "", "", user.toReference(), null, e -> {
			e.getEventLocation().setRegion(rdcf.region);
			e.getEventLocation().setDistrict(rdcf.district);
			e.getEventLocation().setCommunity(rdcf.community);
		});

		PersonDto person = creator.createPerson("John", "Smith", Sex.MALE, 1980, 10, 23, p -> {
			p.getAddress().setStreet("Test Street");
			p.getAddress().setHouseNumber("Test Number");
			p.getAddress().setAdditionalInformation("Test Information");
			p.getAddress().setCity("Test City");
		});

		return creator.createEventParticipant(event.toReference(), person, "Test involvement descr", user.toReference(), rdcf);
	}

	private void assertNotPseudonymized(EventParticipantDto eventParticipant) {
		assertThat(eventParticipant.getInvolvementDescription(), is("Test involvement descr"));
		assertThat(eventParticipant.getPerson().getFirstName(), is("John"));
		assertThat(eventParticipant.getPerson().getLastName(), is("Smith"));
		assertThat(eventParticipant.getPerson().getAddress().getStreet(), is("Test Street"));
		assertThat(eventParticipant.getPerson().getAddress().getHouseNumber(), is("Test Number"));
		assertThat(eventParticipant.getPerson().getAddress().getAdditionalInformation(), is("Test Information"));
		assertThat(eventParticipant.getPerson().getAddress().getCity(), is("Test City"));
	}

	private void assertPseudonymized(EventParticipantDto eventParticipant) {
		assertThat(eventParticipant.getInvolvementDescription(), isEmptyString());
		assertThat(eventParticipant.getReportingUser(), is(nullValue()));
		assertThat(eventParticipant.getPerson().getFirstName(), is(I18nProperties.getCaption(Captions.inaccessibleValue)));
		assertThat(eventParticipant.getPerson().getLastName(), is(I18nProperties.getCaption(Captions.inaccessibleValue)));
		assertThat(eventParticipant.getPerson().getAddress().getStreet(), isEmptyString());
		assertThat(eventParticipant.getPerson().getAddress().getHouseNumber(), isEmptyString());
		assertThat(eventParticipant.getPerson().getAddress().getAdditionalInformation(), isEmptyString());
		assertThat(eventParticipant.getPerson().getAddress().getCity(), isEmptyString());

	}
}
