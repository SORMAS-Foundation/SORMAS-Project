package de.symeda.sormas.app.backend.person;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.CasePersonDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.location.LocationDtoHelper;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
public class PersonDtoHelper extends AdoDtoHelper<Person, CasePersonDto> {

    private LocationDtoHelper locationHelper = new LocationDtoHelper();

    @Override
    public Person create() {
        return new Person();
    }

    @Override
    public CasePersonDto createDto() {
        return new CasePersonDto();
    }

    @Override
    public void fillInnerFromDto(Person ado, CasePersonDto dto) {

        ado.setCaseUuid(dto.getCaseUuid());
        ado.setFirstName(dto.getFirstName());
        ado.setLastName(dto.getLastName());
        ado.setSex(dto.getSex());
        ado.setBirthDate(dto.getBirthDate());
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
    public void fillInnerFromAdo(CasePersonDto dto, Person ado) {

        dto.setCaseUuid(ado.getCaseUuid());
        dto.setFirstName(ado.getFirstName());
        dto.setLastName(ado.getLastName());
        dto.setSex(ado.getSex());
        dto.setBirthDate(ado.getBirthDate());
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
            dto.setOccupationFacility(AdoDtoHelper.toReferenceDto(facility));
        } else {
            dto.setOccupationFacility(null);
        }

        // TODO
    }
}
