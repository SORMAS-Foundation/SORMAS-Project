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
package de.symeda.sormas.api.facility;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Remote;

import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;

@Remote
public interface FacilityFacade {

	List<FacilityDto> getIndexList(FacilityCriteria facilityCriteria, Integer first, Integer max, List<SortProperty> sortProperties);

	long count(FacilityCriteria criteria);

	List<FacilityReferenceDto> getActiveHealthFacilitiesByCommunity(CommunityReferenceDto community, boolean includeStaticFacilities);

	List<FacilityReferenceDto> getActiveHealthFacilitiesByDistrict(DistrictReferenceDto district, boolean includeStaticFacilities);

	List<FacilityReferenceDto> getAllActiveLaboratories(boolean includeOtherLaboratory);

	List<FacilityDto> getAllByRegionAfter(String regionUuid, Date date);

	List<FacilityDto> getAllWithoutRegionAfter(Date date);

	FacilityReferenceDto getFacilityReferenceByUuid(String uuid);

	FacilityReferenceDto getFacilityReferenceById(long id);

	FacilityDto getByUuid(String uuid);

	List<FacilityDto> getByUuids(List<String> uuids);

	List<String> getAllUuids();

	void saveFacility(FacilityDto value) throws ValidationRuntimeException;

	List<FacilityReferenceDto> getByName(
		String name,
		DistrictReferenceDto districtRef,
		CommunityReferenceDto communityRef,
		boolean includeArchivedEntities);

	List<FacilityReferenceDto> getLaboratoriesByName(String name, boolean includeArchivedEntities);

	void archive(String facilityUuid);

	void dearchive(String facilityUuid);

	boolean hasArchivedParentInfrastructure(Collection<String> facilityUuids);

	Map<String, String> getDistrictUuidsForFacilities(List<FacilityReferenceDto> facilities);

	Map<String, String> getCommunityUuidsForFacilities(List<FacilityReferenceDto> facilities);
}
