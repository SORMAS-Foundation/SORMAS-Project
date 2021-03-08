/*
 * ******************************************************************************
 * * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * *
 * * This program is free software: you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation, either version 3 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program. If not, see <https://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.symeda.sormas.backend.event;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.hasSize;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventInvestigationStatus;
import de.symeda.sormas.api.event.EventParticipantCriteria;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantExportDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.TestDataCreator.RDCFEntities;

public class EventParticipantFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetExportList() {

		TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		EventDto event = creator.createEvent(
			EventStatus.SIGNAL,
			EventInvestigationStatus.PENDING,
			"Title",
			"Description",
			"First",
			"Name",
			"12345",
			TypeOfPlace.PUBLIC_PLACE,
			DateHelper.subtractDays(new Date(), 1),
			new Date(),
			user.toReference(),
			user.toReference(),
			Disease.EVD,
			rdcf.district);
		PersonDto eventPerson = creator.createPerson("Event", "Organizer");
		creator.createEventParticipant(event.toReference(), eventPerson, "event Director", user.toReference());

		PersonDto eventPerson1 = creator.createPerson("Event", "Participant");
		creator.createEventParticipant(event.toReference(), eventPerson1, "event fan", user.toReference());

		EventParticipantCriteria eventParticipantCriteria = new EventParticipantCriteria();
		eventParticipantCriteria.event(event.toReference());

		List<EventParticipantExportDto> results = getEventParticipantFacade().getExportList(eventParticipantCriteria, Collections.emptySet(), 0, 100, Language.EN);

		// List should have two entries
		assertThat(results, Matchers.hasSize(2));
	}

	@Test
	public void testCreateWithoutUuid() {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER);
		EventParticipantDto eventParticipant = new EventParticipantDto();
		eventParticipant.setEvent(creator.createEvent(user.toReference()).toReference());
		eventParticipant.setPerson(creator.createPerson());

		EventParticipantDto savedEventParticipant = getEventParticipantFacade().saveEventParticipant(eventParticipant);

		MatcherAssert.assertThat(savedEventParticipant.getUuid(), not(isEmptyOrNullString()));
	}

	@Test
	public void testGetByEventUuids() {

		RDCFEntities rdcf = creator.createRDCFEntities();
		UserDto user = creator.createUser(rdcf, UserRole.SURVEILLANCE_SUPERVISOR);

		EventDto event1 = creator.createEvent(user.toReference());
		EventDto event2 = creator.createEvent(user.toReference());

		PersonDto person1 = creator.createPerson();
		PersonDto person2 = creator.createPerson();
		creator.createEventParticipant(event1.toReference(), person1, user.toReference());
		creator.createEventParticipant(event1.toReference(), person2, user.toReference());
		creator.createEventParticipant(event2.toReference(), person1, user.toReference());

		List<EventParticipantDto> eps = getEventParticipantFacade().getByEventUuids(Collections.singletonList(event1.getUuid()));
		assertThat(eps, hasSize(2));
		eps = getEventParticipantFacade().getByEventUuids(Arrays.asList(event1.getUuid(), event2.getUuid()));
		assertThat(eps, hasSize(3));
	}

	@Test
	public void testGetByPersonUuids() {

		RDCFEntities rdcf = creator.createRDCFEntities();
		UserDto user = creator.createUser(rdcf, UserRole.SURVEILLANCE_SUPERVISOR);

		EventDto event1 = creator.createEvent(user.toReference());
		EventDto event2 = creator.createEvent(user.toReference());

		PersonDto person1 = creator.createPerson();
		PersonDto person2 = creator.createPerson();
		creator.createEventParticipant(event1.toReference(), person1, user.toReference());
		creator.createEventParticipant(event1.toReference(), person2, user.toReference());
		creator.createEventParticipant(event2.toReference(), person1, user.toReference());

		List<EventParticipantDto> eps = getEventParticipantFacade().getByPersonUuids(Collections.singletonList(person1.getUuid()));
		assertThat(eps, hasSize(2));
		eps = getEventParticipantFacade().getByPersonUuids(Arrays.asList(person1.getUuid(), person2.getUuid()));
		assertThat(eps, hasSize(3));
	}
}
