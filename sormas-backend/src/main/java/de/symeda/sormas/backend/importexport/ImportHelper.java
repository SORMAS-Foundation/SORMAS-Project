/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.importexport;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseLogic;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.utils.DataHelper;

public class ImportHelper {

	public static RegionReferenceDto getRegionBasedOnDistrict(
		String propertyName,
		CaseDataDto caze,
		ContactDto contact,
		TravelEntryDto travelEntry,
		PersonDto person,
		Object currentElement) {

		if (currentElement instanceof CaseDataDto) {
			return getCaseRegion(propertyName, caze);
		} else if (currentElement instanceof ContactDto) {
			return contact.getRegion();
		} else if (currentElement instanceof TravelEntryDto) {
			return getTravelEntryRegion(propertyName, travelEntry);
		} else {
			return getPersonRegion(propertyName, person);
		}
	}

	public static DistrictReferenceDto getDistrictBasedOnCommunity(String propertyName, CaseDataDto caze, PersonDto person, Object currentElement) {
		if (currentElement instanceof CaseDataDto) {
			return getCaseDistrict(propertyName, caze);
		} else {
			return getPersonDistrict(propertyName, person);
		}
	}

	public static DataHelper.Pair<DistrictReferenceDto, CommunityReferenceDto> getDistrictAndCommunityBasedOnFacility(
		String propertyName,
		CaseDataDto caze,
		PersonDto person,
		Object currentElement) {

		if (currentElement instanceof CaseDataDto) {
			return DataHelper.Pair.createPair(getCaseDistrict(propertyName, caze), getCaseCommunity(propertyName, caze));
		} else {
			return getPersonDistrictAndCommunity(propertyName, person);
		}
	}

	public static RegionReferenceDto getRegionBasedOnDistrict(String propertyName, LocationDto associatedLocation, Object currentElement) {
		if (!(currentElement instanceof LocationDto)) {
			throw new IllegalArgumentException("currentElement is not a LocationDto: " + currentElement.getClass());
		}
		return associatedLocation.getRegion();
	}

	public static RegionReferenceDto getRegionBasedOnDistrict(
		String propertyName,
		EventParticipantDto eventParticipant,
		PersonDto person,
		Object currentElement) {

		if (currentElement instanceof EventParticipantDto) {
			return eventParticipant.getRegion();
		} else {
			return getPersonRegion(propertyName, person);
		}
	}

	public static RegionReferenceDto getCaseRegion(String propertyName, CaseDataDto caze) {
		switch (propertyName) {
		case CaseDataDto.RESPONSIBLE_DISTRICT:
			return caze.getResponsibleRegion();
		case LocationDto.DISTRICT:
			return caze.getRegion();
		default:
			throw new IllegalArgumentException(propertyName);
		}
	}

	public static RegionReferenceDto getPersonRegion(String propertyName, PersonDto person) {
		switch (propertyName) {
		case PersonDto.PLACE_OF_BIRTH_DISTRICT:
			return person.getPlaceOfBirthRegion();
		case LocationDto.DISTRICT:
			return person.getAddress().getRegion();
		default:
			throw new IllegalArgumentException(propertyName);
		}
	}

	public static RegionReferenceDto getTravelEntryRegion(String propertyName, TravelEntryDto travelEntry) {
		switch (propertyName) {
		case TravelEntryDto.RESPONSIBLE_DISTRICT:
			return travelEntry.getResponsibleRegion();
		case TravelEntryDto.POINT_OF_ENTRY_DISTRICT:
			return travelEntry.getPointOfEntryRegion();
		default:
			throw new IllegalArgumentException(propertyName);
		}
	}

	public static DistrictReferenceDto getDistrictBasedOnCommunity(String propertyName, LocationDto associatedLocation, Object currentElement) {
		if (!(currentElement instanceof LocationDto)) {
			throw new IllegalArgumentException("currentElement is not a LocationDto: " + currentElement.getClass());
		}
		return associatedLocation.getDistrict();
	}

	public static DistrictReferenceDto getDistrictBasedOnCommunity(
		String propertyName,
		EventParticipantDto eventParticipant,
		PersonDto person,
		Object currentElement) {
		if (currentElement instanceof EventParticipantDto) {
			return eventParticipant.getDistrict();
		} else {
			return getPersonDistrict(propertyName, person);
		}
	}

	public static DistrictReferenceDto getCaseDistrict(String propertyName, CaseDataDto caze) {
		switch (propertyName) {
		case CaseDataDto.RESPONSIBLE_COMMUNITY:
			return caze.getResponsibleDistrict();
		case LocationDto.COMMUNITY:
			return caze.getDistrict();
		case CaseDataDto.HEALTH_FACILITY:
			return CaseLogic.getDistrictWithFallback(caze);
		default:
			throw new IllegalArgumentException(propertyName);
		}
	}

	public static CommunityReferenceDto getCaseCommunity(String propertyName, CaseDataDto caze) {
		switch (propertyName) {
		case CaseDataDto.RESPONSIBLE_COMMUNITY:
			return caze.getResponsibleCommunity();
		case LocationDto.COMMUNITY:
			return caze.getCommunity();
		case CaseDataDto.HEALTH_FACILITY:
			return CaseLogic.getCommunityWithFallback(caze);
		default:
			throw new IllegalArgumentException(propertyName);
		}
	}

	public static DistrictReferenceDto getPersonDistrict(String propertyName, PersonDto person) {
		switch (propertyName) {
		case PersonDto.PLACE_OF_BIRTH_COMMUNITY:
			return person.getPlaceOfBirthDistrict();
		case LocationDto.COMMUNITY:
			return person.getAddress().getDistrict();
		default:
			throw new IllegalArgumentException(propertyName);
		}
	}

	public static DataHelper.Pair<DistrictReferenceDto, CommunityReferenceDto> getDistrictAndCommunityBasedOnFacility(
		String propertyName,
		LocationDto associatedLocation,
		Object currentElement) {
		if (!(currentElement instanceof LocationDto)) {
			throw new IllegalArgumentException("currentElement is not a LocationDto: " + currentElement.getClass());
		}

		return DataHelper.Pair.createPair(associatedLocation.getDistrict(), associatedLocation.getCommunity());
	}

	public static DataHelper.Pair<DistrictReferenceDto, CommunityReferenceDto> getDistrictAndCommunityBasedOnFacility(
		String propertyName,
		EventParticipantDto eventParticipant,
		PersonDto person,
		Object currentElement) {

		if (currentElement instanceof EventParticipantDto) {
			return DataHelper.Pair.createPair(eventParticipant.getDistrict(), null);
		} else {
			return getPersonDistrictAndCommunity(propertyName, person);
		}
	}

	public static DataHelper.Pair<DistrictReferenceDto, CommunityReferenceDto> getPersonDistrictAndCommunity(String propertyName, PersonDto person) {
		DistrictReferenceDto district;
		CommunityReferenceDto community;

		switch (propertyName) {
		case PersonDto.PLACE_OF_BIRTH_FACILITY:
			district = person.getPlaceOfBirthDistrict();
			community = person.getPlaceOfBirthCommunity();
			break;
		case LocationDto.FACILITY:
			district = person.getAddress().getDistrict();
			community = person.getAddress().getCommunity();
			break;
		default:
			throw new IllegalArgumentException(propertyName);
		}

		return DataHelper.Pair.createPair(district, community);
	}
}
