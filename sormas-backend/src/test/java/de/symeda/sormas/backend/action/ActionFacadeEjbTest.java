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

package de.symeda.sormas.backend.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.action.ActionContext;
import de.symeda.sormas.api.action.ActionDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventInvestigationStatus;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class ActionFacadeEjbTest extends AbstractBeanTest {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	@Test
	public void testGetAllUuidsEmpty() {
		List<String> allUuids = getActionFacade().getAllUuids();
		assertTrue(allUuids.isEmpty());
	}

	@Test
	public void testGetAllUuids() throws ParseException {
		TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");

		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Surv",
			"Sup",
			creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_SUPERVISOR));

		EventDto eventDto = creator.createEvent(
			EventStatus.SIGNAL,
			EventInvestigationStatus.PENDING,
			"Some Event",
			"...where people meet",
			"Sourcy",
			"McSourceFace",
			"12345",
			TypeOfPlace.PUBLIC_PLACE,
			DATE_FORMAT.parse("12/11/2020"),
			DATE_FORMAT.parse("13/11/2020"),
			user.toReference(),
			user.toReference(),
			Disease.CORONAVIRUS,
			rdcf.district);

		String uuid1 = "ABCDE-FGHIJ-KLMNO-PQRST";
		String uuid2 = "BCDEF-GHIJK-LMNOP-QRSTU";

		ActionDto actionDto1 = new ActionDto();
		actionDto1.setUuid(uuid1);
		actionDto1.setTitle("An action");
		actionDto1.setActionContext(ActionContext.EVENT);
		actionDto1.setDate(DATE_FORMAT.parse("16/11/2020"));
		actionDto1.setDescription("Here is what to do.");
		actionDto1.setReply("This is your reply.");
		actionDto1.setEvent(eventDto.toReference());

		getActionFacade().saveAction(actionDto1);

		ActionDto actionDto2 = new ActionDto();
		actionDto2.setUuid(uuid2);
		actionDto2.setTitle("Another action");
		actionDto2.setActionContext(ActionContext.EVENT);
		actionDto2.setDate(DATE_FORMAT.parse("15/11/2020"));
		actionDto2.setDescription("This action hast no reply");
		actionDto2.setEvent(eventDto.toReference());

		getActionFacade().saveAction(actionDto2);

		List<String> allUuids = getActionFacade().getAllUuids();
		assertEquals(2, allUuids.size());
		assertTrue(allUuids.contains(uuid1));
		assertTrue(allUuids.contains(uuid2));
	}
}
