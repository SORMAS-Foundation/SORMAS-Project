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
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;

public class ImportHelper {

	public static RegionReferenceDto getRegionBasedOnDistrict(
		String propertyName,
		CaseDataDto caze,
		ContactDto contact,
		PersonDto person,
		Object currentElement) {

		if (currentElement instanceof CaseDataDto) {
			return caze.getRegion();
		} else if (currentElement instanceof ContactDto) {
			return contact.getRegion();
		} else {
			return getPersonRegion(propertyName, person);
		}
	}

	public static DistrictReferenceDto getDistrictBasedOnCommunity(String propertyName, CaseDataDto caze, PersonDto person, Object currentElement) {
		if (currentElement instanceof CaseDataDto) {
			return caze.getDistrict();
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
			return DataHelper.Pair.createPair(caze.getDistrict(), caze.getCommunity());
		} else {
			return getPersonDistrictAndCommunity(propertyName, person);
		}
	}

	public static RegionReferenceDto getRegionBasedOnDistrict(
		String propertyName,
		EventDto event,
		Object currentElement) {
		if (!(currentElement instanceof LocationDto)) {
			throw new IllegalArgumentException("currentElement is not a LocationDto: " + currentElement.getClass());
		}
		return event.getEventLocation().getRegion();
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

	public static DistrictReferenceDto getDistrictBasedOnCommunity(String propertyName, EventDto event, Object currentElement) {
		if (!(currentElement instanceof LocationDto)) {
			throw new IllegalArgumentException("currentElement is not a LocationDto: " + currentElement.getClass());
		}
		return event.getEventLocation().getDistrict();
	}

	public static DistrictReferenceDto getDistrictBasedOnCommunity(String propertyName, EventParticipantDto eventParticipant, PersonDto person, Object currentElement) {
		if (currentElement instanceof EventParticipantDto) {
			return eventParticipant.getDistrict();
		} else {
			return getPersonDistrict(propertyName, person);
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
		EventDto event,
		Object currentElement) {
		return DataHelper.Pair.createPair(event.getEventLocation().getDistrict(), event.getEventLocation().getCommunity());
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
