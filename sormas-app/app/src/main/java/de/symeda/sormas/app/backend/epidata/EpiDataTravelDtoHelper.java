package de.symeda.sormas.app.backend.epidata;

import de.symeda.sormas.api.epidata.EpiDataTravelDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.location.LocationDtoHelper;

/**
 * Created by Mate Strysewske on 08.03.2017.
 */

public class EpiDataTravelDtoHelper extends AdoDtoHelper<EpiDataTravel, EpiDataTravelDto> {

    public EpiDataTravelDtoHelper() {
    }

    @Override
    public EpiDataTravel create() {
        return new EpiDataTravel();
    }

    @Override
    public EpiDataTravelDto createDto() {
        return new EpiDataTravelDto();
    }

    @Override
    public void fillInnerFromDto(EpiDataTravel target, EpiDataTravelDto source) {

        // epi data is set by calling method

        target.setTravelType(source.getTravelType());
        target.setTravelDestination(source.getTravelDestination());
        target.setTravelDateFrom(source.getTravelDateFrom());
        target.setTravelDateTo(source.getTravelDateTo());
    }

    @Override
    public void fillInnerFromAdo(EpiDataTravelDto a, EpiDataTravel b) {

        a.setTravelType(b.getTravelType());
        a.setTravelDestination(b.getTravelDestination());
        a.setTravelDateFrom(b.getTravelDateFrom());
        a.setTravelDateTo(b.getTravelDateTo());
    }

}
