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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventGroupCriteria;
import de.symeda.sormas.api.event.EventGroupDto;
import de.symeda.sormas.api.event.EventGroupFacade;
import de.symeda.sormas.api.event.EventGroupIndexDto;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;
import de.symeda.sormas.backend.event.EventGroupFacadeEjb.EventGroupFacadeEjbLocal;

public class EventGroupFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testDeleteEventGroup() {

		EventGroupFacade cut = getEventGroupFacade();

		RDCF rdcf = creator.createRDCF();
		UserReferenceDto user = creator.createUser(rdcf).toReference();

		EventDto event = creator.createEvent(user);

		EventGroupDto group = createEventGroup();
		cut.linkEventsToGroup(Collections.singletonList(event.toReference()), group.toReference());

		assertThat(cut.getEventGroupByUuid(group.getUuid()), equalTo(group));

		cut.deleteEventGroup(group.getUuid());
		assertNull(cut.getEventGroupByUuid(group.getUuid()));
	}

	@Test
	public void testEditEventGroupWithEventsOutsideJurisdiction() {
		EventGroupFacadeEjb eventGroupFacade = getBean(EventGroupFacadeEjbLocal.class);
		RDCF rdcf = creator.createRDCF();
		UserReferenceDto user = creator.createUser(rdcf).toReference();

		EventDto event = creator.createEvent(user);

		EventGroupDto eventGroup = createEventGroup();

		eventGroupFacade.linkEventToGroup(event.toReference(), eventGroup.toReference());

		RDCF rdcf2 = creator.createRDCF();
		UserDto survOff = creator.createUser(rdcf2, DefaultUserRole.SURVEILLANCE_OFFICER);

		loginWith(survOff);

		EventDto survOffEvent = creator.createEvent(survOff.toReference());
		eventGroupFacade.linkEventToGroup(survOffEvent.toReference(), eventGroup.toReference());

		eventGroup.setName("NewName");
		assertThrows(AccessDeniedException.class, () -> eventGroupFacade.saveEventGroup(eventGroup));

		assertThrows(AccessDeniedException.class, () -> eventGroupFacade.unlinkEventGroup(event.toReference(), eventGroup.toReference()));

		eventGroupFacade.unlinkEventGroup(survOffEvent.toReference(), eventGroup.toReference());

		EventCriteria eventsByGroupCriteria = new EventCriteria().eventGroup(eventGroup.toReference());
		eventsByGroupCriteria.setUserFilterIncluded(false);
		List<EventIndexDto> eventsByGroup = getEventFacade().getIndexList(eventsByGroupCriteria, 0, 10, null);

		assertThat(eventsByGroup, hasSize(1));
		assertThat(eventsByGroup.get(0).getUuid(), Matchers.is(event.getUuid()));
	}

	@Test
	public void testGetIndexListWithNationalUserLimitedToCovid() {
		EventGroupFacadeEjb eventGroupFacade = getBean(EventGroupFacadeEjbLocal.class);
		RDCF rdcf = creator.createRDCF();
		UserReferenceDto user = creator.createUser(rdcf).toReference();

		// create a group with a cholera event
		EventDto event = creator.createEvent(user, Disease.CHOLERA);
		EventGroupDto eventGroup = createEventGroup();
		eventGroupFacade.linkEventToGroup(event.toReference(), eventGroup.toReference());

		//create a group with covid event
		EventDto event2 = creator.createEvent(user, Disease.CORONAVIRUS);
		EventGroupDto eventGroup2 = createEventGroup();
		eventGroupFacade.linkEventToGroup(event2.toReference(), eventGroup2.toReference());

		RDCF rdcf2 = creator.createRDCF();
		UserDto natUser = creator.createUser(rdcf2, creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER), u -> {
			u.setLimitedDiseases(Set.of(Disease.CORONAVIRUS));
		});
		loginWith(natUser);

		List<EventGroupIndexDto> indexList = eventGroupFacade.getIndexList(new EventGroupCriteria(), 0, 10, null);

		assertThat(indexList, hasSize(1));
		assertThat(indexList.get(0).getUuid(), Matchers.is(eventGroup2.getUuid()));
	}

	private EventGroupDto createEventGroup() {
		EventGroupDto group = new EventGroupDto();
		group.setName("GroupA");

		return getEventGroupFacade().saveEventGroup(group);
	}

	private EventGroupFacade getEventGroupFacade() {
		return getBean(EventGroupFacadeEjbLocal.class);
	}
}
