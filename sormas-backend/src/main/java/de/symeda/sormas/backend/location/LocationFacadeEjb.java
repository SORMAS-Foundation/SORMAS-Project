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
package de.symeda.sormas.backend.location;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.location.LocationFacade;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.uuid.HasUuid;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.community.CommunityService;
import de.symeda.sormas.backend.infrastructure.continent.ContinentFacadeEjb;
import de.symeda.sormas.backend.infrastructure.continent.ContinentService;
import de.symeda.sormas.backend.infrastructure.country.CountryFacadeEjb;
import de.symeda.sormas.backend.infrastructure.country.CountryService;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.facility.FacilityService;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.RegionService;
import de.symeda.sormas.backend.infrastructure.subcontinent.SubcontinentFacadeEjb;
import de.symeda.sormas.backend.infrastructure.subcontinent.SubcontinentService;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "LocationFacade")
public class LocationFacadeEjb implements LocationFacade {

	@EJB
	private ContinentService continentService;
	@EJB
	private SubcontinentService subcontinentService;
	@EJB
	private CountryService countryService;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private CommunityService communityService;
	@EJB
	private FacilityService facilityService;

	public Location fillOrBuildEntity(LocationDto source, Location target, boolean checkChangeDate) {
		if (source == null) {
			return null;
		}

		target = DtoHelper.fillOrBuildEntity(source, target, Location::new, checkChangeDate);

		target.setDetails(source.getDetails());
		target.setCity(source.getCity());
		target.setAreaType(source.getAreaType());

		target.setContinent(continentService.getByReferenceDto(source.getContinent()));
		target.setSubcontinent(subcontinentService.getByReferenceDto(source.getSubcontinent()));
		target.setCountry(countryService.getByReferenceDto(source.getCountry()));
		target.setRegion(regionService.getByReferenceDto(source.getRegion()));
		target.setDistrict(districtService.getByReferenceDto(source.getDistrict()));
		target.setCommunity(communityService.getByReferenceDto(source.getCommunity()));

		target.setLatitude(source.getLatitude());
		target.setLongitude(source.getLongitude());
		target.setLatLonAccuracy(source.getLatLonAccuracy());

		target.setPostalCode(source.getPostalCode());
		target.setStreet(source.getStreet());
		target.setHouseNumber(source.getHouseNumber());
		target.setAdditionalInformation(source.getAdditionalInformation());
		target.setAddressType(source.getAddressType());
		target.setAddressTypeDetails(source.getAddressTypeDetails());
		target.setFacility(facilityService.getByReferenceDto(source.getFacility()));
		target.setFacilityDetails(source.getFacilityDetails());
		target.setFacilityType(source.getFacilityType());
		target.setContactPersonFirstName(source.getContactPersonFirstName());
		target.setContactPersonLastName(source.getContactPersonLastName());
		target.setContactPersonPhone(source.getContactPersonPhone());
		target.setContactPersonEmail(source.getContactPersonEmail());

		return target;
	}

	public static LocationDto toDto(Location source) {

		if (source == null) {
			return null;
		}

		LocationDto target = new LocationDto();
		DtoHelper.fillDto(target, source);

		target.setDetails(source.getDetails());
		target.setCity(source.getCity());
		target.setAreaType(source.getAreaType());

		target.setContinent(ContinentFacadeEjb.toReferenceDto(source.getContinent()));
		target.setSubcontinent(SubcontinentFacadeEjb.toReferenceDto(source.getSubcontinent()));
		target.setCountry(CountryFacadeEjb.toReferenceDto(source.getCountry()));
		target.setRegion(RegionFacadeEjb.toReferenceDto(source.getRegion()));
		target.setDistrict(DistrictFacadeEjb.toReferenceDto(source.getDistrict()));
		target.setCommunity(CommunityFacadeEjb.toReferenceDto(source.getCommunity()));

		target.setLatitude(source.getLatitude());
		target.setLongitude(source.getLongitude());
		target.setLatLonAccuracy(source.getLatLonAccuracy());

		target.setPostalCode(source.getPostalCode());
		target.setStreet(source.getStreet());
		target.setHouseNumber(source.getHouseNumber());
		target.setAdditionalInformation(source.getAdditionalInformation());
		target.setAddressType(source.getAddressType());
		target.setAddressTypeDetails(source.getAddressTypeDetails());
		target.setFacility(FacilityFacadeEjb.toReferenceDto(source.getFacility()));
		target.setFacilityDetails(source.getFacilityDetails());
		target.setFacilityType(source.getFacilityType());

		target.setContactPersonFirstName(source.getContactPersonFirstName());
		target.setContactPersonLastName(source.getContactPersonLastName());
		target.setContactPersonPhone(source.getContactPersonPhone());
		target.setContactPersonEmail(source.getContactPersonEmail());

		return target;
	}

	public boolean areDifferentLocation(LocationDto firstAddress, LocationDto secondAddress) {

		java.util.function.Predicate<Function<LocationDto, HasUuid>> differentInfrastructurePredicate = (Function<LocationDto, HasUuid> function) -> {
			return function.apply(firstAddress) != null
				&& function.apply(secondAddress) != null
				&& !DataHelper.equal(function.apply(firstAddress).getUuid(), function.apply(secondAddress).getUuid());

		};

		if (Boolean.TRUE.equals(differentInfrastructurePredicate.test(LocationDto::getCountry))) {
			return true;
		}

		if (Boolean.TRUE.equals(differentInfrastructurePredicate.test(LocationDto::getRegion))) {
			return true;
		}

		if (Boolean.TRUE.equals(differentInfrastructurePredicate.test(LocationDto::getDistrict))) {
			return true;
		}

		if (Boolean.TRUE.equals(differentInfrastructurePredicate.test(LocationDto::getCommunity))) {
			return true;
		}

		if (firstAddress.getFacility() != null) {
			Facility firstAddressFacilityDto = facilityService.getByUuid(firstAddress.getFacility().getUuid());
			if (secondAddress.getCommunity() != null
				&& firstAddressFacilityDto.getCommunity() != null
				&& !firstAddressFacilityDto.getCommunity().getUuid().equals(secondAddress.getCommunity().getUuid())) {
				return true;
			}
		}

		boolean oneMatch = Boolean.FALSE;
		boolean secondLocationHasAddressValues = Boolean.FALSE;
		List<DataHelper.Pair<String, String>> addressValues = new ArrayList<>();
		addressValues.add(new DataHelper.Pair<>(firstAddress.getCity(), secondAddress.getCity()));
		addressValues.add(new DataHelper.Pair<>(firstAddress.getPostalCode(), secondAddress.getPostalCode()));
		addressValues.add(new DataHelper.Pair<>(firstAddress.getStreet(), secondAddress.getStreet()));
		addressValues.add(new DataHelper.Pair<>(firstAddress.getHouseNumber(), secondAddress.getHouseNumber()));

		for (DataHelper.Pair<String, String> addressTypePair : addressValues) {
			if (StringUtils.isNotBlank(addressTypePair.getElement0()) && StringUtils.isNotBlank(addressTypePair.getElement1())) {
				if (!DataHelper.equal(addressTypePair.getElement0(), addressTypePair.getElement1())) {
					return true;
				} else {
					oneMatch = Boolean.TRUE;
				}
			} else if (addressTypePair.getElement1() != null) {
				secondLocationHasAddressValues = Boolean.TRUE;
			}
		}

		if (secondLocationHasAddressValues) {
			return !oneMatch;
		}

		return false;
	}

	@LocalBean
	@Stateless
	public static class LocationFacadeEjbLocal extends LocationFacadeEjb {

	}
}
