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

package de.symeda.sormas.backend.labmessage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import de.symeda.sormas.api.labmessage.LabMessageStatus;
import org.junit.Test;

import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.backend.AbstractBeanTest;

public class LabMessageFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetByReportIdWithCornerCaseInput() {
		String reportId = "123456789";
		creator.createLabMessage((lm) -> lm.setReportId(reportId));

		List<LabMessageDto> list = getLabMessageFacade().getByReportId(null);

		assertNotNull(list);
		assertTrue(list.isEmpty());

		list = getLabMessageFacade().getByReportId("");

		assertNotNull(list);
		assertTrue(list.isEmpty());
	}

	@Test
	public void testGetByReportIdWithOneMessage() {

		String reportId = "123456789";
		creator.createLabMessage((lm) -> lm.setReportId(reportId));

		// create noise
		creator.createLabMessage(null);
		creator.createLabMessage((lm) -> lm.setReportId("some-other-id"));

		List<LabMessageDto> list = getLabMessageFacade().getByReportId(reportId);

		assertNotNull(list);
		assertFalse(list.isEmpty());
		assertEquals(1, list.size());
		assertEquals(reportId, list.get(0).getReportId());
	}

	@Test
	public void testGetByUuid() {

		LabMessageDto labMessage = creator.createLabMessage(null);

		LabMessageDto result = getLabMessageFacade().getByUuid(labMessage.getUuid());
		assertThat(result, equalTo(labMessage));

		getLabMessageFacade().deleteLabMessage(labMessage.getUuid());

		// deleted lab messages shall still be returned
		result = getLabMessageFacade().getByUuid(labMessage.getUuid());
		assertThat(result, equalTo(labMessage));

	}

	@Test
	public void testExistsForwardedLabMessageWith() {

		String reportId = "1234";

		// create noise
		creator.createLabMessage((lm) -> lm.setStatus(LabMessageStatus.FORWARDED));

		assertFalse(getLabMessageFacade().existsForwardedLabMessageWith(reportId));
		assertFalse(getLabMessageFacade().existsForwardedLabMessageWith(null));

		creator.createLabMessage((lm) -> lm.setReportId(reportId));

		assertFalse(getLabMessageFacade().existsForwardedLabMessageWith(reportId));

		LabMessageDto forwardedMessage = creator.createLabMessage((lm) -> {
			lm.setReportId(reportId);
			lm.setStatus(LabMessageStatus.FORWARDED);
		});

		assertTrue(getLabMessageFacade().existsForwardedLabMessageWith(reportId));

		getLabMessageFacade().deleteLabMessage(forwardedMessage.getUuid());

		assertTrue(getLabMessageFacade().existsForwardedLabMessageWith(reportId));

	}
}
