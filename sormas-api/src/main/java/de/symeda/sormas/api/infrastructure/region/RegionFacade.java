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
package de.symeda.sormas.api.infrastructure.region;

import java.util.Collection;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.infrastructure.GeoLocationFacade;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface RegionFacade extends GeoLocationFacade<RegionDto, RegionIndexDto, RegionReferenceDto, RegionCriteria> {

	List<RegionReferenceDto> getAllActiveByServerCountry();

	List<RegionReferenceDto> getAllActiveByCountry(String countryUuid);

	List<RegionReferenceDto> getAllActiveByArea(String areaUuid);

	List<RegionReferenceDto> getAllActiveAsReference();

	Page<RegionIndexDto> getIndexPage(RegionCriteria regionCriteria, Integer offset, Integer size, List<SortProperty> sortProperties);


	RegionReferenceDto getRegionReferenceById(int id);

	List<RegionDto> getByName(String name, boolean includeArchivedEntities);

	List<String> getNamesByIds(List<Long> regionIds);

	boolean isUsedInOtherInfrastructureData(Collection<String> regionUuids);

	List<RegionDto> getAllRegion();

}
