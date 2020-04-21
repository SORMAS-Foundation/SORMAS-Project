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

import javax.ejb.Remote;

import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;

@Remote
public interface CommunityFacade {
	
    List<CommunityReferenceDto> getAllActiveByDistrict(String districtUuid);

	List<CommunityDto> getAllAfter(Date date);
	
	List<CommunityDto> getIndexList(CommunityCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties);

	long count(CommunityCriteria criteria);
	
	CommunityDto getByUuid(String uuid);

	List<String> getAllUuids();

	CommunityReferenceDto getCommunityReferenceByUuid(String uuid);
	
	List<CommunityDto> getByUuids(List<String> uuids);
	
	void saveCommunity(CommunityDto dto) throws ValidationRuntimeException;
	
	List<CommunityReferenceDto> getByName(String name, DistrictReferenceDto districtRef, boolean includeArchivedEntities);
	
	void archive(String communityUuid);
	
	void dearchive(String communityUuid);
	
	boolean isUsedInOtherInfrastructureData(Collection<String> communityUuids);
	
	boolean hasArchivedParentInfrastructure(Collection<String> communityUuids);
	
}