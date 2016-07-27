package de.symeda.sormas.app.backend.location;

import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.facility.Facility;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
public class LocationDtoHelper extends AdoDtoHelper<Location, LocationDto> {

    @Override
    public Location create() {
        return new Location();
    }

    @Override
    public void fillInnerFromDto(Location ado, LocationDto dto) {

        ado.setAddress(dto.getAddress());
        ado.setCity(dto.getCity());
        //ado.setCommunity(dto.getCommunity());
        // TODO ...
    }
}
