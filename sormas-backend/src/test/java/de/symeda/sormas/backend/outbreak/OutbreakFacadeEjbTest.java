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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.outbreak;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator.RDCFEntities;

public class OutbreakFacadeEjbTest extends AbstractBeanTest {

	private RDCFEntities rdcf;

	@Override
	public void init() {
		super.init();

		rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);

		when(MockProducer.getPrincipal().getName()).thenReturn("SurvSup");
	}

	@Test
	public void testOutbreakCreationAndDeletion() {

		DistrictReferenceDto district = new DistrictReferenceDto(rdcf.district.getUuid());
		Disease disease = Disease.EVD;

		getOutbreakFacade().startOutbreak(district, disease);
		// outbreak should be active
		assertNotNull(getOutbreakFacade().getActiveByDistrictAndDisease(district, disease));

		getOutbreakFacade().endOutbreak(district, disease);
		// Database should contain no outbreak
		assertNull(getOutbreakFacade().getActiveByDistrictAndDisease(district, disease));
	}
}
