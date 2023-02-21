/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.externalmessage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.surveillancereport.ReportingType;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageType;
import de.symeda.sormas.ui.AbstractBeanTest;
import de.symeda.sormas.ui.TestDataCreator;

public class ExternalMessageControllerTest extends AbstractBeanTest {

	@Test
	public void testCreateSurveillanceReport() {
		ExternalMessageController cut = new ExternalMessageController();
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		CaseDataDto caze = creator.createCase(rdcf);
		Date messageDateTime = new Date();
		ExternalMessageDto externalMessage = creator.createExternalMessage(m -> {
			m.setType(ExternalMessageType.PHYSICIANS_REPORT);
			m.setReportMessageId("1234-abcd");
			m.setMessageDateTime(messageDateTime);
		});
		SurveillanceReportDto surveillanceReport = cut.createSurveillanceReport(externalMessage, caze.toReference());
		assertEquals(ReportingType.DOCTOR, surveillanceReport.getReportingType());

		assertEquals(messageDateTime, surveillanceReport.getReportDate());
		assertEquals(caze.getUuid(), surveillanceReport.getCaze().getUuid());
		assertEquals("1234-abcd", surveillanceReport.getExternalId());

		externalMessage.setType(ExternalMessageType.LAB_MESSAGE);
		surveillanceReport = cut.createSurveillanceReport(externalMessage, caze.toReference());
		assertEquals(ReportingType.LABORATORY, surveillanceReport.getReportingType());
	}
}
