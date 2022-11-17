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

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageType;
import de.symeda.sormas.ui.AbstractBeanTest;
import de.symeda.sormas.ui.TestDataCreator;

public class ExternalMessageControllerTest extends AbstractBeanTest {

	@Test
	public void testCreateSurveillanceReport() {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		TestDataCreator.RDCF caseRdcf = creator.createRDCF();
		CaseDataDto caze = creator.createCase(caseRdcf);
		ExternalMessageDto message = ExternalMessageDto.build();
		creator.createExternalMessage(m -> m.setType(ExternalMessageType.PHYSICIANS_REPORT));
		// TODO continue this test and check for correct creation of the surveillance report 
		//  with all fields for physicians reports and lab messages
	}

}
