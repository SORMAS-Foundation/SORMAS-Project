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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class EventServiceTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf;
	private UserDto nationalUser;

	@Override
	public void init() {

		super.init();
		rdcf = creator.createRDCF("Region", "District", "Community", "Facility", "Point of entry");
		nationalUser = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.community.getUuid(),
			rdcf.facility.getUuid(),
			"Nat",
			"User",
			creator.getUserRoleDtoMap().get(DefaultUserRole.NATIONAL_USER));
	}

	@Test
	public void testHasRegionAndDistrict() {

		EventDto event1 = creator.createEvent(nationalUser.toReference());
		assertFalse(getEventService().hasRegionAndDistrict(event1.getUuid()));

		EventDto event2 = creator.createEvent(nationalUser.toReference(), Disease.EVD, e -> {
			e.getEventLocation().setRegion(rdcf.region);
			e.getEventLocation().setDistrict(rdcf.district);
		});
		assertTrue(getEventService().hasRegionAndDistrict(event2.getUuid()));
	}

	@Test
	public void testHasAnyEventParticipantWithoutJurisdiction() {

		EventDto event = creator.createEvent(nationalUser.toReference());
		PersonDto person1 = creator.createPerson();
		creator.createEventParticipant(event.toReference(), person1, "", nationalUser.toReference(), e -> {
			e.setRegion(rdcf.region);
			e.setDistrict(rdcf.district);
		}, null);
		assertFalse(getEventService().hasAnyEventParticipantWithoutJurisdiction(event.getUuid()));

		PersonDto person2 = creator.createPerson();
		creator.createEventParticipant(event.toReference(), person2, nationalUser.toReference());
		assertTrue(getEventService().hasAnyEventParticipantWithoutJurisdiction(event.getUuid()));
	}
}
