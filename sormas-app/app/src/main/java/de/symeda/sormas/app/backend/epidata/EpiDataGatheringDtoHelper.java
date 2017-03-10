package de.symeda.sormas.app.backend.epidata;

import de.symeda.sormas.api.epidata.EpiDataGatheringDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.location.LocationDtoHelper;

/**
 * Created by Mate Strysewske on 08.03.2017.
 */

public class EpiDataGatheringDtoHelper extends AdoDtoHelper<EpiDataGathering, EpiDataGatheringDto> {

    private LocationDtoHelper locationHelper;
    private EpiDataDtoHelper epiDataHelper;

    public EpiDataGatheringDtoHelper(EpiDataDtoHelper epiDataHelper) {
        locationHelper = new LocationDtoHelper();
        this.epiDataHelper = epiDataHelper;
    }

    @Override
    public EpiDataGathering create() {
        return new EpiDataGathering();
    }

    @Override
    public EpiDataGatheringDto createDto() {
        return new EpiDataGatheringDto();
    }

    @Override
    public void fillInnerFromDto(EpiDataGathering a, EpiDataGatheringDto b) {
        if (b.getEpiData() != null) {
            a.setEpiData(DatabaseHelper.getEpiDataDao().queryUuid(b.getEpiData().getUuid()));
        } else {
            a.setEpiData(null);
        }

        if (b.getGatheringAddress() != null) {
            a.setGatheringAddress(DatabaseHelper.getLocationDao().queryUuid(b.getGatheringAddress().getUuid()));
        } else {
            a.setGatheringAddress(null);
        }

        a.setDescription(b.getDescription());
        a.setGatheringDate(b.getGatheringDate());
    }

    @Override
    public void fillInnerFromAdo(EpiDataGatheringDto a, EpiDataGathering b) {
        if (b.getEpiData() != null) {
            EpiData epiData = DatabaseHelper.getEpiDataDao().queryForId(b.getEpiData().getId());
            a.setEpiData(epiDataHelper.adoToDto(epiData));
        } else {
            a.setEpiData(null);
        }

        if (b.getGatheringAddress() != null) {
            Location location = DatabaseHelper.getLocationDao().queryForId(b.getGatheringAddress().getId());
            a.setGatheringAddress(locationHelper.adoToDto(location));
        } else {
            a.setGatheringAddress(null);
        }

        a.setDescription(b.getDescription());
        a.setGatheringDate(b.getGatheringDate());
    }
}
