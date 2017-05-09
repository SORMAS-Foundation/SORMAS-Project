package de.symeda.sormas.app.backend.person;

import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.facility.FacilityDtoHelper;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.location.LocationDtoHelper;

public class PersonDtoHelper extends AdoDtoHelper<Person, PersonDto> {

    private LocationDtoHelper locationHelper = new LocationDtoHelper();

    @Override
    public Person create() {
        return new Person();
    }

    @Override
    public PersonDto createDto() {
        return new PersonDto();
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
    }

    @Override
    public void fillInnerFromAdo(PersonDto dto, Person ado) {

        dto.setFirstName(ado.getFirstName());
        dto.setLastName(ado.getLastName());
        dto.setNickname(ado.getNickname());
        dto.setMothersMaidenName(ado.getMothersMaidenName());
        dto.setSex(ado.getSex());
        dto.setBirthdateDD(ado.getBirthdateDD());
        dto.setBirthdateMM(ado.getBirthdateMM());
        dto.setBirthdateYYYY(ado.getBirthdateYYYY());
        dto.setApproximateAge(ado.getApproximateAge());
        dto.setApproximateAgeType(ado.getApproximateAgeType());
        dto.setPhone(ado.getPhone());
        dto.setPhoneOwner(ado.getPhoneOwner());
        dto.setPresentCondition(ado.getPresentCondition());
        dto.setDeathDate(ado.getDeathDate());

        if (ado.getAddress() != null) {
            Location location = DatabaseHelper.getLocationDao().queryForId(ado.getAddress().getId());
            dto.setAddress(locationHelper.adoToDto(location));
        }

        dto.setOccupationType(ado.getOccupationType());
        dto.setOccupationDetails(ado.getOccupationDetails());
        if (ado.getOccupationFacility() != null) {
            Facility facility = DatabaseHelper.getFacilityDao().queryForId(ado.getOccupationFacility().getId());
            dto.setOccupationFacility(FacilityDtoHelper.toReferenceDto(facility));
        } else {
            dto.setOccupationFacility(null);
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
