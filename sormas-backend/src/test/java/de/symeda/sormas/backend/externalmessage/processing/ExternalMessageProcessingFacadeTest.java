/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.externalmessage.processing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingFacade;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityFacade;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.backend.AbstractBeanTest;

public class ExternalMessageProcessingFacadeTest extends AbstractBeanTest {

	private ExternalMessageProcessingFacade processingFacade;

	@Override
	public void init() {
		super.init();
		processingFacade = getExternalMessageProcessingFacade();
	}

	@Test
	public void testGetLabReference() {
		var rdcf = creator.createRDCF();
		FacilityFacade facilityFacade = getFacilityFacade();
		final FacilityReferenceDto otherFacilityRef = facilityFacade.getReferenceByUuid(FacilityDto.OTHER_FACILITY_UUID);

		assertEquals(otherFacilityRef, processingFacade.getFacilityReference(Collections.emptyList()));
		assertEquals(otherFacilityRef, processingFacade.getFacilityReference(Collections.singletonList("unknown")));

		FacilityDto one = creator.createFacility("One", rdcf.region, rdcf.district, rdcf.community, FacilityType.LABORATORY);
		one.setExternalID("oneExternal");
		one.setChangeDate(new Date());
		facilityFacade.save(one);

		FacilityDto two = creator.createFacility("Two", rdcf.region, rdcf.district, rdcf.community, FacilityType.LABORATORY);
		two.setExternalID("twoExternal");
		two.setChangeDate(new Date());
		facilityFacade.save(two);

		FacilityReferenceDto oneExternal = processingFacade.getFacilityReference(Collections.singletonList("oneExternal"));
		assertEquals(one.toReference(), oneExternal);

		FacilityReferenceDto twoExternal = processingFacade.getFacilityReference(Collections.singletonList("twoExternal"));
		assertEquals(two.toReference(), twoExternal);

		assertNull(processingFacade.getFacilityReference(Arrays.asList("oneExternal", "twoExternal")));
	}
}
