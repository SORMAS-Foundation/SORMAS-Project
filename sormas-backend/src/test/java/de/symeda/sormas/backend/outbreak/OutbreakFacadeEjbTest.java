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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.outbreak.OutbreakCriteria;
import de.symeda.sormas.api.outbreak.OutbreakDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator.RDCFEntities;

public class OutbreakFacadeEjbTest extends AbstractBeanTest {

	private RDCFEntities rdcf;

	@Override
	public void init() {
		super.init();

		rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Surv",
			"Sup",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));

		when(MockProducer.getPrincipal().getName()).thenReturn("SurvSup");
	}

	@Test
	public void testOutbreakCreationAndDeletion() {

		DistrictReferenceDto district = new DistrictReferenceDto(rdcf.district.getUuid(), null, null);
		Disease disease = Disease.EVD;

		getOutbreakFacade().startOutbreak(district, disease);
		// outbreak should be active
		assertNotNull(getOutbreakFacade().getActiveByDistrictAndDisease(district, disease));

		getOutbreakFacade().endOutbreak(district, disease);
		// Database should contain no outbreak
		assertNull(getOutbreakFacade().getActiveByDistrictAndDisease(district, disease));
	}

	@Test
	public void testGetActiveOutbreaksWhenOneHasPrimaryFalse() {
		DistrictReferenceDto district = new DistrictReferenceDto(rdcf.district.getUuid(), null, null);
		getOutbreakFacade().startOutbreak(district, Disease.AFP);
		getOutbreakFacade().startOutbreak(district, Disease.CHOLERA);

		OutbreakCriteria criteria = new OutbreakCriteria().active(true);
		List<OutbreakDto> outbreakDtos = getOutbreakFacade().getActive(criteria);
		assertEquals(2, outbreakDtos.size());
		List<Disease> outbreakDiseases = outbreakDtos.stream().map(o -> o.getDisease()).collect(Collectors.toList());
		assertTrue(outbreakDiseases.contains(Disease.AFP));
		assertTrue(outbreakDiseases.contains(Disease.CHOLERA));

		creator.updateDiseaseConfiguration(Disease.AFP, null, false, null, null, null);
		outbreakDtos = getOutbreakFacade().getActive(criteria);
		assertEquals(1, outbreakDtos.size());
		outbreakDiseases = outbreakDtos.stream().map(o -> o.getDisease()).collect(Collectors.toList());
		assertFalse(outbreakDiseases.contains(Disease.AFP));
		assertTrue(outbreakDiseases.contains(Disease.CHOLERA));
	}

	@Test
	public void testGetActiveOutbreaksWhenOneHasCaseSurveillanceEnabledFalse() {
		DistrictReferenceDto district = new DistrictReferenceDto(rdcf.district.getUuid(), null, null);
		getOutbreakFacade().startOutbreak(district, Disease.AFP);
		getOutbreakFacade().startOutbreak(district, Disease.CHOLERA);

		OutbreakCriteria criteria = new OutbreakCriteria().active(true);
		List<OutbreakDto> outbreakDtos = getOutbreakFacade().getActive(criteria);
		assertEquals(2, outbreakDtos.size());
		List<Disease> outbreakDiseases = outbreakDtos.stream().map(o -> o.getDisease()).collect(Collectors.toList());
		assertTrue(outbreakDiseases.contains(Disease.AFP));
		assertTrue(outbreakDiseases.contains(Disease.CHOLERA));

		creator.updateDiseaseConfiguration(Disease.AFP, null, null, false, null, null);
		outbreakDtos = getOutbreakFacade().getActive(criteria);
		assertEquals(1, outbreakDtos.size());
		outbreakDiseases = outbreakDtos.stream().map(o -> o.getDisease()).collect(Collectors.toList());
		assertFalse(outbreakDiseases.contains(Disease.AFP));
		assertTrue(outbreakDiseases.contains(Disease.CHOLERA));
	}
}
