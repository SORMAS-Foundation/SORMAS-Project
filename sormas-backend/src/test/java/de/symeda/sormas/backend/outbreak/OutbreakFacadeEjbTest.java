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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.*;
import java.util.stream.Collectors;

import de.symeda.sormas.backend.infrastructure.district.District;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.outbreak.OutbreakCriteria;
import de.symeda.sormas.api.outbreak.OutbreakDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class OutbreakFacadeEjbTest extends AbstractBeanTest {

	private RDCF rdcf;

	@InjectMocks
	private OutbreakFacadeEjb outbreakFacade;

	@Override
	public void init() {
		super.init();

		rdcf = creator.createRDCF();
		loginWith(creator.createSurveillanceSupervisor(rdcf));
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

	@Test
	public void testGetOutbreakDistrictNameByDisease() {

		Disease disease1 = Disease.EVD;
		Disease disease2 = Disease.ADENOVIRUS;
		Disease disease3 = Disease.C_PNEUMONIAE;

		DistrictReferenceDto district = new DistrictReferenceDto(rdcf.district.getUuid(), null, null);
		getOutbreakFacade().startOutbreak(district, disease1);
		getOutbreakFacade().startOutbreak(district, disease2);
		getOutbreakFacade().startOutbreak(district, disease3);

		OutbreakCriteria outbreakCriteria = new OutbreakCriteria().district(district);

		Map<Disease, District> result =getOutbreakService().getOutbreakDistrictNameByDisease(outbreakCriteria);

		assertEquals(3, result.size());
		Set<Disease> resultDiseases = result.keySet();
		assertTrue(resultDiseases.contains(disease1));
		assertTrue(resultDiseases.contains(disease2));
	}

	@Test
	public void testGetOutbreakDistrictCountByDisease() {

		Disease disease1 = Disease.ADENOVIRUS;
		Disease disease2 = Disease.ANTHRAX;

		DistrictReferenceDto district = new DistrictReferenceDto(rdcf.district.getUuid(), null, null);
		getOutbreakFacade().startOutbreak(district, disease1);
		getOutbreakFacade().startOutbreak(district, disease2);

		Set<Disease> diseases = new HashSet<>();
		OutbreakCriteria outbreakCriteria = new OutbreakCriteria().diseases(diseases);

		Map<Disease, Long> result = getOutbreakFacade().getOutbreakDistrictCountByDisease(outbreakCriteria);

		assertEquals(2, result.size());
	}

	@Test
	public void testGetOutbreakDistrictCount() {

		Disease disease1 = Disease.ADENOVIRUS;

		DistrictReferenceDto district = new DistrictReferenceDto(rdcf.district.getUuid(), null, null);
		getOutbreakFacade().startOutbreak(district, disease1);
		OutbreakCriteria outbreakCriteria= new OutbreakCriteria().district(district);
		Long result = getOutbreakFacade().getOutbreakDistrictCount(outbreakCriteria);

		assertEquals(1, result);
	}

}
