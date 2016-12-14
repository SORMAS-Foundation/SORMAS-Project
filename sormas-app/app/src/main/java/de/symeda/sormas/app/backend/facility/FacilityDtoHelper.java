package de.symeda.sormas.app.backend.facility;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.location.LocationDtoHelper;
import de.symeda.sormas.app.backend.person.Person;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
public class FacilityDtoHelper extends AdoDtoHelper<Facility, FacilityDto> {

    private LocationDtoHelper locationHelper = new LocationDtoHelper();

    @Override
    public Facility create() {
        return new Facility();
    }

    @Override
    public FacilityDto createDto() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fillInnerFromDto(Facility ado, FacilityDto dto) {

        ado.setLocation(locationHelper.fillOrCreateFromDto(ado.getLocation(), dto.getLocation()));
        ado.setName(dto.getName());
        ado.setPublicOwnership(dto.isPublicOwnership());
        ado.setType(dto.getType());
    }

    @Override
    public void fillInnerFromAdo(FacilityDto facilityDto, Facility facility) {
        throw new UnsupportedOperationException();
    }

    public static FacilityReferenceDto toReferenceDto(Facility ado) {
        if (ado == null) {
            return null;
        }
        FacilityReferenceDto dto = new FacilityReferenceDto();
        fillReferenceDto(dto, ado);

        return dto;
    }
}
