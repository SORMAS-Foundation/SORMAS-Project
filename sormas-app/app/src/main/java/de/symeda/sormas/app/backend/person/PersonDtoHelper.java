package de.symeda.sormas.app.backend.person;

import java.util.List;

import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.facility.FacilityDtoHelper;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.location.LocationDtoHelper;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.util.DataUtils;
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
    protected Call<List<PersonDto>> pullAllSince(long since) {
        return RetroProvider.getPersonFacade().pullAllSince(since);
    }

    @Override
    protected Call<Integer> pushAll(List<PersonDto> personDtos) {
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

        target.setPhone(source.getPhone());
        target.setPhoneOwner(source.getPhoneOwner());

        target.setPresentCondition(source.getPresentCondition());
        target.setDeathDate(source.getDeathDate());

        target.setAddress(locationHelper.fillOrCreateFromDto(target.getAddress(), source.getAddress()));
        target.setOccupationType(source.getOccupationType());
        target.setOccupationDetails(source.getOccupationDetails());
        target.setOccupationFacility(DatabaseHelper.getFacilityDao().getByReferenceDto(source.getOccupationFacility()));
        target.setDeathPlaceType(source.getDeathPlaceType());
        target.setDeathPlaceDescription(source.getDeathPlaceDescription());
        target.setBurialDate(source.getBurialDate());
        target.setBurialPlaceDescription(source.getBurialPlaceDescription());
        target.setBurialConductor(source.getBurialConductor());
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
        target.setPhone(source.getPhone());
        target.setPhoneOwner(source.getPhoneOwner());


        Location address = DatabaseHelper.getLocationDao().queryForId(source.getAddress().getId());
        target.setAddress(locationHelper.adoToDto(address));

        target.setOccupationType(source.getOccupationType());
        target.setOccupationDetails(source.getOccupationDetails());
        if (source.getOccupationFacility() != null) {
            Facility facility = DatabaseHelper.getFacilityDao().queryForId(source.getOccupationFacility().getId());
            target.setOccupationFacility(FacilityDtoHelper.toReferenceDto(facility));
        } else {
            target.setOccupationFacility(null);
        }
    }

    public static PersonReferenceDto toReferenceDto(Person ado) {
        if (ado == null) {
            return null;
        }
        PersonReferenceDto dto = new PersonReferenceDto();
        fillReferenceDto(dto, ado);

        dto.setFirstName(ado.getFirstName());
        dto.setLastName(ado.getLastName());

        return dto;
    }
}
