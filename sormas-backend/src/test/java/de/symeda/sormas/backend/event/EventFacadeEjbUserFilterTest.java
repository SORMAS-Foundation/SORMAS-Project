/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.event.EventInvestigationStatus;
import de.symeda.sormas.api.event.EventParticipantCriteria;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class EventFacadeEjbUserFilterTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf1;
	private TestDataCreator.RDCF rdcf2;

	private UserDto districtUser1;
	private UserDto districtUser2;
	private UserDto nationalUser;

	@Override
	public void init() {
		super.init();
		rdcf1 = creator.createRDCF("Region 1", "District 1", "Community 1", "Facility 1", "Point of entry 1");
		rdcf2 = creator.createRDCF("Region 2", "District 2", "Community 2", "Facility 2", "Point of entry 2");
		nationalUser = creator.createUser(
			rdcf1.region.getUuid(),
			rdcf1.district.getUuid(),
			rdcf1.community.getUuid(),
			rdcf1.facility.getUuid(),
			"Nat",
			"User",
			creator.getUserRoleDtoMap().get(DefaultUserRole.NATIONAL_USER));

		districtUser1 = creator.createUser(
			rdcf1.region.getUuid(),
			rdcf1.district.getUuid(),
			rdcf1.facility.getUuid(),
			"Surv",
			"Off1",
			creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER));

		districtUser2 = creator.createUser(
			rdcf2.region.getUuid(),
			rdcf2.district.getUuid(),
			rdcf2.facility.getUuid(),
			"Surv",
			"Off2",
			creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER));
	}

	@Test
	public void testIndexListWhenParticipantIsInJurisdictionButEventIsNot() {

		loginWith(nationalUser);

		EventDto event1 = creator.createEvent(
			EventStatus.SIGNAL,
			EventInvestigationStatus.PENDING,
			"TitleEv1",
			"DescriptionEv1",
			"John1",
			"Doe1",
			"12345",
			TypeOfPlace.PUBLIC_PLACE,
			DateHelper.subtractDays(new Date(), 1),
			new Date(),
			nationalUser.toReference(),
			nationalUser.toReference(),
			Disease.EVD,
			rdcf1.district);

		EventDto event2 = creator.createEvent(
			EventStatus.EVENT,
			EventInvestigationStatus.PENDING,
			"TitleEv2",
			"DescriptionEv2",
			"John2",
			"Doe2",
			"12345",
			TypeOfPlace.FACILITY,
			DateHelper.subtractDays(new Date(), 1),
			new Date(),
			nationalUser.toReference(),
			nationalUser.toReference(),
			Disease.EVD,
			rdcf2.district);

		loginWith(districtUser1);
		EventCriteria eventCriteria = new EventCriteria();
		assertEquals(1, getEventFacade().getIndexList(eventCriteria, 0, 100, null).size());

		PersonDto event2Person1 = creator.createPerson("Event2", "Part1");
		creator.createEventParticipant(event2.toReference(), event2Person1, "event2 participant 1", nationalUser.toReference(), rdcf1);
		PersonDto event2Person2 = creator.createPerson("Event2", "Part2");
		creator.createEventParticipant(event2.toReference(), event2Person2, "event2 participant 2", nationalUser.toReference(), rdcf2);

		List<EventIndexDto> indexList = getEventFacade().getIndexList(eventCriteria, 0, 100, Arrays.asList(new SortProperty(Event.EVENT_TITLE)));
		assertEquals(2, indexList.size());
		// not checking here for pseudonymized because there is no field yet on event index that is sensitive/personal
		Assert.assertTrue(indexList.get(0).getInJurisdictionOrOwned());
		Assert.assertFalse(indexList.get(1).getInJurisdictionOrOwned());

		assertEquals(
			2,
			getEventParticipantFacade().getIndexList(new EventParticipantCriteria().withEvent(event2.toReference()), 0, 100, null).size());

		loginWith(districtUser2);
		assertEquals(1, getEventFacade().getIndexList(eventCriteria, 0, 100, null).size());
	}
}
