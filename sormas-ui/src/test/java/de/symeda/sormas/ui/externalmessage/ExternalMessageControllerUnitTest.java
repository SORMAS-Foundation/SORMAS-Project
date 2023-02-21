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
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.caze.surveillancereport.ReportingType;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageType;

public class ExternalMessageControllerUnitTest {

	@Test
	public void testSetSurvReportingType() {
		ExternalMessageDto message = ExternalMessageDto.build();
		SurveillanceReportDto report = SurveillanceReportDto.build(null, null);
		ExternalMessageController externalMessageController = new ExternalMessageController();

		message.setType(ExternalMessageType.LAB_MESSAGE);
		externalMessageController.setSurvReportingType(report, message);
		assertEquals(ReportingType.LABORATORY, report.getReportingType());

		message.setType(ExternalMessageType.PHYSICIANS_REPORT);
		externalMessageController.setSurvReportingType(report, message);
		assertEquals(ReportingType.DOCTOR, report.getReportingType());

		message.setType(null);
		assertThrows(UnsupportedOperationException.class, () -> externalMessageController.setSurvReportingType(report, message));
	}

}
