/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.outbreak;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.outbreak.OutbreakDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;

public class OutbreakFacadeEjbTest extends AbstractBeanTest {
	
	@Test
	public void testOutbreakCreationAndDeletion() {
		
		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		OutbreakDto outbreak = creator.createOutbreak(rdcf, Disease.EVD, user.toReference());
		
		// Database should contain one outbreak
		assertEquals(1, getOutbreakFacade().getAllAfter(null).size());
		
		getOutbreakFacade().deleteOutbreak(outbreak);
		
		// Database should contain no outbreak
		assertEquals(0, getOutbreakFacade().getAllAfter(null).size());
	}
}
