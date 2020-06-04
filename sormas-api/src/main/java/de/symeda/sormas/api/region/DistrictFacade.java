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

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Remote;

import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;

@Remote
public interface DistrictFacade {

    List<DistrictReferenceDto> getAllActiveByRegion(String regionUuid);
	
	int getCountByRegion(String regionUuid);

	List<DistrictDto> getAllAfter(Date date);
	
	List<DistrictIndexDto> getIndexList(DistrictCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties);
	
	long count(DistrictCriteria criteria);
	
	DistrictDto getDistrictByUuid(String uuid);
	
	DistrictReferenceDto getDistrictReferenceByUuid(String uuid);
	
	DistrictReferenceDto getDistrictReferenceById(long id);

	List<DistrictReferenceDto> getAllActiveAsReference();

	List<String> getAllUuids();
	
	List<DistrictDto> getByUuids(List<String> uuids);
	
	void saveDistrict(DistrictDto dto) throws ValidationRuntimeException;
	
	List<DistrictReferenceDto> getByName(String name, RegionReferenceDto regionRef, boolean includeArchivedEntities);
	
	List<String> getNamesByIds(List<Long> districtIds);

	String getFullEpidCodeForDistrict(String districtUuid);
	
	void archive(String districtUuid);
	
	void dearchive(String districtUuid);
	
	boolean isUsedInOtherInfrastructureData(Collection<String> districtUuids);
	
	boolean hasArchivedParentInfrastructure(Collection<String> districtUuids);

	Map<String, String> getRegionUuidsForDistricts(List<DistrictReferenceDto> districts);
	
}
