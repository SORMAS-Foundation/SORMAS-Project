package de.symeda.sormas.app.backend.epidata;

import de.symeda.sormas.api.epidata.EpiDataBurialDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.location.LocationDtoHelper;

/**
 * Created by Mate Strysewske on 08.03.2017.
 */

public class EpiDataBurialDtoHelper extends AdoDtoHelper<EpiDataBurial, EpiDataBurialDto> {

    private LocationDtoHelper locationHelper;
    private EpiDataDtoHelper epiDataHelper;

    public EpiDataBurialDtoHelper(EpiDataDtoHelper epiDataHelper) {
        locationHelper = new LocationDtoHelper();
        this.epiDataHelper = epiDataHelper;
    }

    @Override
    public EpiDataBurial create() {
        return new EpiDataBurial();
    }

    @Override
    public EpiDataBurialDto createDto() {
        return new EpiDataBurialDto();
    }

    @Override
    public void fillInnerFromDto(EpiDataBurial a, EpiDataBurialDto b) {
        try {
            // epi data is set by calling method

            if (b.getBurialAddress() != null) {
                a.setBurialAddress(DatabaseHelper.getLocationDao().queryUuid(b.getBurialAddress().getUuid()));
            } else {
                a.setBurialAddress(null);
            }

            a.setBurialDateFrom(b.getBurialDateFrom());
            a.setBurialDateTo(b.getBurialDateTo());
            a.setBurialPersonname(b.getBurialPersonName());
            a.setBurialRelation(b.getBurialRelation());
            a.setBurialIll(b.getBurialIll());
            a.setBurialTouching(b.getBurialTouching());
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void fillInnerFromAdo(EpiDataBurialDto a, EpiDataBurial b) {

        if (b.getBurialAddress() != null) {
            Location location = DatabaseHelper.getLocationDao().queryForId(b.getBurialAddress().getId());
            a.setBurialAddress(locationHelper.adoToDto(location));
        } else {
            a.setBurialAddress(null);
        }

        a.setBurialDateFrom(b.getBurialDateFrom());
        a.setBurialDateTo(b.getBurialDateTo());
        a.setBurialPersonName(b.getBurialPersonname());
        a.setBurialRelation(b.getBurialRelation());
        a.setBurialIll(b.getBurialIll());
        a.setBurialTouching(b.getBurialTouching());
    }
}
