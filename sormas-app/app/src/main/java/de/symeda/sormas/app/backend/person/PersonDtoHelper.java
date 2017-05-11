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
        target.setDeathLocation(locationHelper.fillOrCreateFromDto(target.getDeathLocation(), source.getDeathLocation()));
        target.setBurialDate(source.getBurialDate());
        target.setBurialConductor(source.getBurialConductor());
        target.setBurialLocation(locationHelper.fillOrCreateFromDto(target.getBurialLocation(), source.getBurialLocation()));

        target.setAddress(locationHelper.fillOrCreateFromDto(target.getAddress(), source.getAddress()));
        target.setOccupationType(source.getOccupationType());
        target.setOccupationDetails(source.getOccupationDetails());
        target.setOccupationFacility(DatabaseHelper.getFacilityDao().getByReferenceDto(source.getOccupationFacility()));
    }

    @Override
    public void fillInnerFromAdo(PersonDto target, Person source) {

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
        if (source.getDeathLocation() != null) {
            Location location = DatabaseHelper.getLocationDao().queryForId(source.getDeathLocation().getId());
            target.setDeathLocation(locationHelper.adoToDto(location));
        }
        target.setBurialDate(source.getBurialDate());
        target.setBurialConductor(source.getBurialConductor());
        if (source.getBurialLocation() != null) {
            Location location = DatabaseHelper.getLocationDao().queryForId(source.getBurialLocation().getId());
            target.setBurialLocation(locationHelper.adoToDto(location));
        }

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
