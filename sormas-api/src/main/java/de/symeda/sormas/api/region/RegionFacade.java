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
package de.symeda.sormas.api.region;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface RegionFacade {

    List<RegionReferenceDto> getAllAsReference();

	List<RegionDto> getAllAfter(Date date);
	
	List<RegionIndexDto> getIndexList(RegionCriteria criteria, int first, int max, List<SortProperty> sortProperties);
	
	long count(RegionCriteria criteria);
	
	RegionDto getRegionByUuid(String uuid);
	
	RegionReferenceDto getRegionReferenceByUuid(String uuid);

	RegionReferenceDto getRegionReferenceById(int id);
	
	List<String> getAllUuids(String userUuid);
	
	List<String> getAllUuids();
	
	List<RegionDto> getByUuids(List<String> uuids);
	
	void saveRegion(RegionDto dto);
	
	List<RegionReferenceDto> getByName(String name);

}
