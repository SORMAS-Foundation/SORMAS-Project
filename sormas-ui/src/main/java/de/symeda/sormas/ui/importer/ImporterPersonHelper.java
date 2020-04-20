package de.symeda.sormas.ui.importer;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DataHelper.Pair;

public final class ImporterPersonHelper {

	private ImporterPersonHelper() {
		// Hide Utility Class Constructor
	}

	public static RegionReferenceDto getRegionBasedOnDistrict(String propertyName, CaseDataDto caze, ContactDto contact, PersonDto person, Object currentElement) {
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
	
	public static Pair<DistrictReferenceDto, CommunityReferenceDto> getDistrictAndCommunityBasedOnFacility(String propertyName, CaseDataDto caze, PersonDto person, Object currentElement) {
		if (currentElement instanceof CaseDataDto) {
			return Pair.createPair(caze.getDistrict(), caze.getCommunity());
		} else {
			return getPersonDistrictAndCommunity(propertyName, person);
		}
	}
	
	public static RegionReferenceDto getPersonRegion(String propertyName, PersonDto person) {
		switch (propertyName) {
		case PersonDto.OCCUPATION_DISTRICT:
			return person.getOccupationRegion();
		case PersonDto.PLACE_OF_BIRTH_DISTRICT:
			return person.getPlaceOfBirthRegion();
		case LocationDto.DISTRICT:
			return person.getAddress().getRegion();
		default:
			throw new IllegalArgumentException(propertyName);
		}
	}
	
	public static DistrictReferenceDto getPersonDistrict(String propertyName, PersonDto person) {
		switch (propertyName) {
		case PersonDto.OCCUPATION_COMMUNITY:
			return person.getOccupationDistrict();
		case PersonDto.PLACE_OF_BIRTH_COMMUNITY:
			return person.getPlaceOfBirthDistrict();
		case LocationDto.COMMUNITY:
			return person.getAddress().getDistrict();
		default:
			throw new IllegalArgumentException(propertyName);
		}
	}
	
	public static Pair<DistrictReferenceDto, CommunityReferenceDto> getPersonDistrictAndCommunity(String propertyName, PersonDto person) {
		DistrictReferenceDto district;
		CommunityReferenceDto community;
		
		switch (propertyName) {
		case PersonDto.OCCUPATION_FACILITY:
			district = person.getOccupationDistrict();
			community = person.getOccupationCommunity();
			break;
		case PersonDto.PLACE_OF_BIRTH_FACILITY:
			district = person.getPlaceOfBirthDistrict();
			community = person.getPlaceOfBirthCommunity();
			break;
		default:
			throw new IllegalArgumentException(propertyName);
		}
		
		return Pair.createPair(district, community);
	}

}
