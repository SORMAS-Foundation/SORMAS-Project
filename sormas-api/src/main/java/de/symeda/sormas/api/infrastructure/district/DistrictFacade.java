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
package de.symeda.sormas.api.infrastructure.district;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.ejb.Remote;

import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.infrastructure.GeoLocationFacade;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface DistrictFacade extends GeoLocationFacade<DistrictDto, DistrictIndexDto, DistrictReferenceDto, DistrictCriteria> {

	List<DistrictReferenceDto> getAllActiveByArea(String areaUuid);

	List<DistrictReferenceDto> getAllActiveByRegion(String regionUuid);

	int getCountByRegion(String regionUuid);

	Page<DistrictIndexDto> getIndexPage(DistrictCriteria districtCriteria, Integer offset, Integer size, List<SortProperty> sortProperties);

	DistrictReferenceDto getDistrictReferenceById(long id);

	List<DistrictReferenceDto> getAllActiveAsReference();

	List<DistrictReferenceDto> getByName(String name, RegionReferenceDto regionRef, boolean includeArchivedEntities);

	String getFullEpidCodeForDistrict(String districtUuid);

	boolean isUsedInOtherInfrastructureData(Collection<String> districtUuids);

	boolean hasArchivedParentInfrastructure(Collection<String> districtUuids);

	Map<String, String> getRegionUuidsForDistricts(List<DistrictReferenceDto> districts);
}
