package de.symeda.sormas.app.backend.facility;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.location.LocationDtoHelper;

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
    public void fillInnerFromDto(Facility ado, FacilityDto dto) {

        ado.setLocation(locationHelper.fillOrCreateFromDto(ado.getLocation(), dto.getLocation()));
        ado.setName(dto.getName());
        ado.setPublicOwnership(dto.isPublicOwnership());
        ado.setType(dto.getType());
    }
}
