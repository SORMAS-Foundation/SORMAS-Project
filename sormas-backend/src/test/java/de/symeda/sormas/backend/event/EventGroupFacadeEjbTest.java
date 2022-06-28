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
import static org.junit.Assert.assertNull;

import java.util.Collections;

import org.junit.Test;

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventGroupDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;
import de.symeda.sormas.backend.event.EventGroupFacadeEjb.EventGroupFacadeEjbLocal;

public class EventGroupFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testDeleteEventGroup() {

		EventGroupFacadeEjb cut = getBean(EventGroupFacadeEjbLocal.class);

		RDCF rdcf = creator.createRDCF();
		UserReferenceDto user = creator.createUser(rdcf).toReference();

		EventDto event = creator.createEvent(user);

		EventGroupDto group = new EventGroupDto();
		group.setName("GroupA");
		group = cut.saveEventGroup(group);
		cut.linkEventsToGroup(Collections.singletonList(event.toReference()), group.toReference());

		assertThat(cut.getEventGroupByUuid(group.getUuid()), equalTo(group));

		cut.deleteEventGroup(group.getUuid());
		assertNull(cut.getEventGroupByUuid(group.getUuid()));
	}
}
