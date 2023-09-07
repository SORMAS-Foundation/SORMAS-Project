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
package de.symeda.sormas.api.infrastructure.facility;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Remote;

import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.infrastructure.InfrastructureFacade;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface FacilityFacade extends InfrastructureFacade<FacilityDto, FacilityIndexDto, FacilityReferenceDto, FacilityCriteria> {

	List<FacilityReferenceDto> getActiveFacilitiesByCommunityAndType(
		CommunityReferenceDto community,
		FacilityType type,
		boolean includeOtherFacility,
		boolean includeNoneFacility);

	List<FacilityReferenceDto> getActiveFacilitiesByDistrictAndType(
		DistrictReferenceDto district,
		FacilityType type,
		boolean includeOtherFacility,
		boolean includeNoneFacility);

	List<FacilityReferenceDto> getActiveHospitalsByCommunity(CommunityReferenceDto community, boolean includeOtherFacility);

	List<FacilityReferenceDto> getActiveHospitalsByDistrict(DistrictReferenceDto district, boolean includeOtherFacility);

	List<FacilityReferenceDto> getAllActiveLaboratories(boolean includeOtherFacility);

	List<FacilityDto> getAllByRegionAfter(String regionUuid, Date date);

	List<FacilityDto> getAllWithoutRegionAfter(Date date);

	FacilityReferenceDto getFacilityReferenceById(long id);

	List<FacilityReferenceDto> getByNameAndType(
		String name,
		DistrictReferenceDto districtRef,
		CommunityReferenceDto communityRef,
		FacilityType type,
		boolean includeArchivedEntities);

	FacilityReferenceDto getByAddress(String street, String postalCode, String city);

	List<FacilityReferenceDto> getLaboratoriesByName(String name, boolean includeArchivedEntities);

	boolean hasArchivedParentInfrastructure(Collection<String> facilityUuids);

	Map<String, String> getDistrictUuidsForFacilities(List<FacilityReferenceDto> facilities);

	Map<String, String> getCommunityUuidsForFacilities(List<FacilityReferenceDto> facilities);

	List<FacilityReferenceDto> getByExternalIdAndType(String id, FacilityType type, boolean includeArchivedEntities);

	Page<FacilityIndexDto> getIndexPage(FacilityCriteria criteria, Integer offset, Integer size, List<SortProperty> sortProperties);

	List<FacilityExportDto> getExportList(FacilityCriteria facilityCriteria, Collection<String> selectedRows, Integer first, Integer max);
}
