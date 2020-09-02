/*
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
 */

package de.symeda.sormas.app.backend.person;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.facility.FacilityDtoHelper;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.location.LocationDtoHelper;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.CommunityDtoHelper;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.DistrictDtoHelper;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.region.RegionDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class PersonDtoHelper extends AdoDtoHelper<Person, PersonDto> {

    private LocationDtoHelper locationHelper = new LocationDtoHelper();

    @Override
    protected Class<Person> getAdoClass() {
        return Person.class;
    }

    @Override
    protected Class<PersonDto> getDtoClass() {
        return PersonDto.class;
    }

    @Override
    protected Call<List<PersonDto>> pullAllSince(long since) throws NoConnectionException {
        return RetroProvider.getPersonFacade().pullAllSince(since);
    }

    @Override
    protected Call<List<PersonDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
        return RetroProvider.getPersonFacade().pullByUuids(uuids);
    }

    @Override
    protected Call<List<PushResult>> pushAll(List<PersonDto> personDtos) throws NoConnectionException {
        return RetroProvider.getPersonFacade().pushAll(personDtos);
    }

    @Override
    public void fillInnerFromDto(Person target, PersonDto source) {

        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setNickname(source.getNickname());
        target.setMothersMaidenName(source.getMothersMaidenName());
        target.setSex(source.getSex());

        target.setBirthdateDD(source.getBirthdateDD());
        target.setBirthdateMM(source.getBirthdateMM());
        target.setBirthdateYYYY(source.getBirthdateYYYY());
        target.setApproximateAge(source.getApproximateAge());
        target.setApproximateAgeType(source.getApproximateAgeType());
        target.setApproximateAgeReferenceDate(source.getApproximateAgeReferenceDate());

        target.setPhone(source.getPhone());
        target.setPhoneOwner(source.getPhoneOwner());

        target.setPresentCondition(source.getPresentCondition());
        target.setDeathDate(source.getDeathDate());

        target.setAddress(locationHelper.fillOrCreateFromDto(target.getAddress(), source.getAddress()));

        target.setEducationType(source.getEducationType());
        target.setEducationDetails(source.getEducationDetails());

        target.setOccupationType(source.getOccupationType());
        target.setOccupationDetails(source.getOccupationDetails());
        target.setOccupationRegion(DatabaseHelper.getRegionDao().getByReferenceDto(source.getOccupationRegion()));
        target.setOccupationDistrict(DatabaseHelper.getDistrictDao().getByReferenceDto(source.getOccupationDistrict()));
        target.setOccupationCommunity(DatabaseHelper.getCommunityDao().getByReferenceDto(source.getOccupationCommunity()));
        target.setOccupationFacility(DatabaseHelper.getFacilityDao().getByReferenceDto(source.getOccupationFacility()));
        target.setOccupationFacilityDetails(source.getOccupationFacilityDetails());
        target.setDeathPlaceType(source.getDeathPlaceType());
        target.setDeathPlaceDescription(source.getDeathPlaceDescription());
        target.setBurialDate(source.getBurialDate());
        target.setBurialPlaceDescription(source.getBurialPlaceDescription());
        target.setBurialConductor(source.getBurialConductor());
        target.setCauseOfDeath(source.getCauseOfDeath());
        target.setCauseOfDeathDisease(source.getCauseOfDeathDisease());
        target.setCauseOfDeathDetails(source.getCauseOfDeathDetails());
        target.setMothersName(source.getMothersName());
        target.setFathersName(source.getFathersName());
        target.setPlaceOfBirthRegion(DatabaseHelper.getRegionDao().getByReferenceDto(source.getPlaceOfBirthRegion()));
        target.setPlaceOfBirthDistrict(DatabaseHelper.getDistrictDao().getByReferenceDto(source.getPlaceOfBirthDistrict()));
        target.setPlaceOfBirthCommunity(DatabaseHelper.getCommunityDao().getByReferenceDto(source.getPlaceOfBirthCommunity()));
        target.setPlaceOfBirthFacility(DatabaseHelper.getFacilityDao().getByReferenceDto(source.getPlaceOfBirthFacility()));
        target.setPlaceOfBirthFacilityDetails(source.getPlaceOfBirthFacilityDetails());
        target.setGestationAgeAtBirth(source.getGestationAgeAtBirth());
        target.setBirthWeight(source.getBirthWeight());

        target.setGeneralPractitionerDetails(source.getGeneralPractitionerDetails());
        target.setEmailAddress(source.getEmailAddress());
        target.setPassportNumber(source.getPassportNumber());
        target.setNationalHealthId(source.getNationalHealthId());

        target.setPseudonymized(source.isPseudonymized());
    }

    @Override
    public void fillInnerFromAdo(PersonDto target, Person source) {

        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setNickname(source.getNickname());
        target.setMothersMaidenName(source.getMothersMaidenName());
        target.setSex(source.getSex());
        target.setPresentCondition(source.getPresentCondition());
        target.setDeathDate(source.getDeathDate());
        target.setDeathPlaceType(source.getDeathPlaceType());
        target.setDeathPlaceDescription(source.getDeathPlaceDescription());
        target.setBurialDate(source.getBurialDate());
        target.setBurialPlaceDescription(source.getBurialPlaceDescription());
        target.setBurialConductor(source.getBurialConductor());

        target.setBirthdateDD(source.getBirthdateDD());
        target.setBirthdateMM(source.getBirthdateMM());
        target.setBirthdateYYYY(source.getBirthdateYYYY());
        target.setApproximateAge(source.getApproximateAge());
        target.setApproximateAgeType(source.getApproximateAgeType());
        target.setApproximateAgeReferenceDate(source.getApproximateAgeReferenceDate());
        target.setPhone(source.getPhone());
        target.setPhoneOwner(source.getPhoneOwner());

        target.setCauseOfDeath(source.getCauseOfDeath());
        target.setCauseOfDeathDisease(source.getCauseOfDeathDisease());
        target.setCauseOfDeathDetails(source.getCauseOfDeathDetails());

        Location address = DatabaseHelper.getLocationDao().queryForId(source.getAddress().getId());
        target.setAddress(locationHelper.adoToDto(address));

        target.setEducationType(source.getEducationType());
        target.setEducationDetails(source.getEducationDetails());

        target.setOccupationType(source.getOccupationType());
        target.setOccupationDetails(source.getOccupationDetails());
        if (source.getOccupationRegion() != null) {
            Region region = DatabaseHelper.getRegionDao().queryForId(source.getOccupationRegion().getId());
            target.setOccupationRegion(RegionDtoHelper.toReferenceDto(region));
        } else {
            target.setOccupationRegion(null);
        }
        if (source.getOccupationDistrict() != null) {
            District district = DatabaseHelper.getDistrictDao().queryForId(source.getOccupationDistrict().getId());
            target.setOccupationDistrict(DistrictDtoHelper.toReferenceDto(district));
        } else {
            target.setOccupationDistrict(null);
        }
        if (source.getOccupationCommunity() != null) {
            Community community = DatabaseHelper.getCommunityDao().queryForId(source.getOccupationCommunity().getId());
            target.setOccupationCommunity(CommunityDtoHelper.toReferenceDto(community));
        } else {
            target.setOccupationCommunity(null);
        }
        if (source.getOccupationFacility() != null) {
            Facility facility = DatabaseHelper.getFacilityDao().queryForId(source.getOccupationFacility().getId());
            target.setOccupationFacility(FacilityDtoHelper.toReferenceDto(facility));
        } else {
            target.setOccupationFacility(null);
        }

        target.setOccupationFacilityDetails(source.getOccupationFacilityDetails());

        target.setMothersName(source.getMothersName());
        target.setFathersName(source.getFathersName());

        if (source.getPlaceOfBirthRegion() != null) {
            target.setPlaceOfBirthRegion(RegionDtoHelper.toReferenceDto(DatabaseHelper.getRegionDao().queryForId(source.getPlaceOfBirthRegion().getId())));
        } else {
            target.setPlaceOfBirthRegion(null);
        }
        if (source.getPlaceOfBirthDistrict() != null) {
            target.setPlaceOfBirthDistrict(DistrictDtoHelper.toReferenceDto(DatabaseHelper.getDistrictDao().queryForId(source.getPlaceOfBirthDistrict().getId())));
        } else {
            target.setPlaceOfBirthDistrict(null);
        }
        if (source.getPlaceOfBirthCommunity() != null) {
            target.setPlaceOfBirthCommunity(CommunityDtoHelper.toReferenceDto(DatabaseHelper.getCommunityDao().queryForId(source.getPlaceOfBirthCommunity().getId())));
        } else {
            target.setPlaceOfBirthCommunity(null);
        }
        if (source.getPlaceOfBirthFacility() != null) {
            target.setPlaceOfBirthFacility(FacilityDtoHelper.toReferenceDto(DatabaseHelper.getFacilityDao().queryForId(source.getPlaceOfBirthFacility().getId())));
        } else {
            target.setPlaceOfBirthFacility(null);
        }

        target.setPlaceOfBirthFacilityDetails(source.getPlaceOfBirthFacilityDetails());
        target.setGestationAgeAtBirth(source.getGestationAgeAtBirth());
        target.setBirthWeight(source.getBirthWeight());

        target.setGeneralPractitionerDetails(source.getGeneralPractitionerDetails());
        target.setEmailAddress(source.getEmailAddress());
        target.setPassportNumber(source.getPassportNumber());
        target.setNationalHealthId(source.getNationalHealthId());

        target.setPseudonymized(source.isPseudonymized());
    }

    public static PersonReferenceDto toReferenceDto(Person ado) {
        if (ado == null) {
            return null;
        }
        PersonReferenceDto dto = new PersonReferenceDto(ado.getUuid());

        return dto;
    }
}
