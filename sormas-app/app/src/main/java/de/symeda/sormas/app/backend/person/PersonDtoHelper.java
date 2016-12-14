package de.symeda.sormas.app.backend.person;

import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.facility.FacilityDtoHelper;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.location.LocationDtoHelper;
import de.symeda.sormas.app.backend.user.User;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
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
    public void fillInnerFromDto(Person ado, PersonDto dto) {

        ado.setFirstName(dto.getFirstName());
        ado.setLastName(dto.getLastName());
        ado.setSex(dto.getSex());
        ado.setBirthdateDD(dto.getBirthdateDD());
        ado.setBirthdateMM(dto.getBirthdateMM());
        ado.setBirthdateYYYY(dto.getBirthdateYYYY());
        ado.setApproximateAge(dto.getApproximateAge());
        ado.setApproximateAgeType(dto.getApproximateAgeType());
        ado.setPhone(dto.getPhone());
        ado.setPhoneOwner(dto.getPhoneOwner());
        ado.setPresentCondition(dto.getPresentCondition());
        ado.setDeathDate(dto.getDeathDate());
        ado.setAddress(locationHelper.fillOrCreateFromDto(ado.getAddress(), dto.getAddress()));
        ado.setOccupationType(dto.getOccupationType());
        ado.setOccupationDetails(dto.getOccupationDetails());
        if (dto.getOccupationFacility() != null) {
            ado.setOccupationFacility(DatabaseHelper.getFacilityDao().queryUuid(dto.getOccupationFacility().getUuid()));
        } else {
            ado.setOccupationFacility(null);
        }

        // TODO
    }

    @Override
    public void fillInnerFromAdo(PersonDto dto, Person ado) {

        dto.setFirstName(ado.getFirstName());
        dto.setLastName(ado.getLastName());
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

        // TODO
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
