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
package de.symeda.sormas.backend.facility;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;

public class FacilityServiceTest extends AbstractBeanTest {

	@Override
	public void init() {
		getFacilityService().createConstantFacilities();
	}

	@Test
	public void testSpecialFacilitiesExist() {

		FacilityService facilityService = getBean(FacilityService.class);
		Facility otherFacility = facilityService.getByUuid(FacilityDto.OTHER_FACILITY_UUID);
		assertNotNull(otherFacility);
		Facility noneFacility = facilityService.getByUuid(FacilityDto.NONE_FACILITY_UUID);
		assertNotNull(noneFacility);
		Facility otherLaboratory = facilityService.getByUuid(FacilityDto.OTHER_LABORATORY_UUID);
		assertNotNull(otherLaboratory);
	}

	@Test
	public void testGetHealthFacilitiesByName() {

		Region region = creator.createRegion("Region");
		District district = creator.createDistrict("District", region);
		District otherDistrict = creator.createDistrict("Other District", region);
		Community community = creator.createCommunity("Community", district);
		Community otherCommunity = creator.createCommunity("Other Community", otherDistrict);
		creator.createFacility("Facility", region, district, community);

		assertThat(getFacilityService().getHealthFacilitiesByName("Facility", district, community, true), hasSize(1));
		assertThat(getFacilityService().getHealthFacilitiesByName(" Facility ", district, community, true), hasSize(1));
		assertThat(getFacilityService().getHealthFacilitiesByName("facility", district, null, true), hasSize(1));
		assertThat(getFacilityService().getHealthFacilitiesByName("FACILITY", district, null, true), hasSize(1));
		assertThat(getFacilityService().getHealthFacilitiesByName("Facility", otherDistrict, otherCommunity, true), empty());
		assertThat(getFacilityService().getHealthFacilitiesByName("Redcliffe Church", district, community, true), empty());
	}

	@Test
	public void testGetLaboratoriesByName() {

		Region region = creator.createRegion("Region");
		District district = creator.createDistrict("District", region);
		Community community = creator.createCommunity("Community", district);
		creator.createFacility("Laboratory", FacilityType.LABORATORY, region, district, community);

		assertThat(getFacilityService().getLaboratoriesByName("Laboratory", true), hasSize(1));
		assertThat(getFacilityService().getLaboratoriesByName(" Laboratory ", true), hasSize(1));
		assertThat(getFacilityService().getLaboratoriesByName("laboratory", true), hasSize(1));
		assertThat(getFacilityService().getLaboratoriesByName("LABORATORY", true), hasSize(1));
		assertThat(getFacilityService().getLaboratoriesByName("Jowan's Chamber", true), empty());
	}
}
