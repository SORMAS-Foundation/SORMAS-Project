package de.symeda.sormas.app.backend.epidata;

import de.symeda.sormas.api.epidata.EpiDataTravelDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.location.LocationDtoHelper;

/**
 * Created by Mate Strysewske on 08.03.2017.
 */

public class EpiDataTravelDtoHelper extends AdoDtoHelper<EpiDataTravel, EpiDataTravelDto> {

    private LocationDtoHelper locationHelper;
    private EpiDataDtoHelper epiDataHelper;

    public EpiDataTravelDtoHelper(EpiDataDtoHelper epiDataHelper) {
        locationHelper = new LocationDtoHelper();
        this.epiDataHelper = epiDataHelper;
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
    public void fillInnerFromDto(EpiDataTravel a, EpiDataTravelDto b) {

        // epi data is set by calling method

        a.setTravelType(b.getTravelType());
        a.setTravelDestination(b.getTravelDestination());
        a.setTravelDateFrom(b.getTravelDateFrom());
        a.setTravelDateTo(b.getTravelDateTo());
    }

    @Override
    public void fillInnerFromAdo(EpiDataTravelDto a, EpiDataTravel b) {

        a.setTravelType(b.getTravelType());
        a.setTravelDestination(b.getTravelDestination());
        a.setTravelDateFrom(b.getTravelDateFrom());
        a.setTravelDateTo(b.getTravelDateTo());
    }

}
