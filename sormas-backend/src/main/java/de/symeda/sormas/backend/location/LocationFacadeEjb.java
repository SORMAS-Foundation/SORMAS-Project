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

import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.location.LocationFacade;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.region.CommunityFacadeEjb;
import de.symeda.sormas.backend.region.CommunityService;
import de.symeda.sormas.backend.region.ContinentFacadeEjb;
import de.symeda.sormas.backend.region.ContinentService;
import de.symeda.sormas.backend.region.CountryFacadeEjb;
import de.symeda.sormas.backend.region.CountryService;
import de.symeda.sormas.backend.region.DistrictFacadeEjb;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.RegionFacadeEjb;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.region.SubcontinentFacadeEjb;
import de.symeda.sormas.backend.region.SubcontinentService;
import de.symeda.sormas.backend.util.DtoHelper;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless(name = "LocationFacade")
public class LocationFacadeEjb implements LocationFacade {

	@EJB
	private LocationService locationService;
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

	public Location fromDto(LocationDto source, boolean checkChangeDate) {

		if (source == null) {
			return null;
		}

		Location target = DtoHelper.fillOrBuildEntity(source, locationService.getByUuid(source.getUuid()), Location::new, checkChangeDate);

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

	@LocalBean
	@Stateless
	public static class LocationFacadeEjbLocal extends LocationFacadeEjb {

	}
}
