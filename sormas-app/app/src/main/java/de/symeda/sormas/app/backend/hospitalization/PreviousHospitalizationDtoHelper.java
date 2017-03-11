package de.symeda.sormas.app.backend.hospitalization;

import de.symeda.sormas.api.caze.PreviousHospitalizationDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.facility.FacilityDtoHelper;

/**
 * Created by Mate Strysewske on 22.02.2017.
 */

public class PreviousHospitalizationDtoHelper extends AdoDtoHelper<PreviousHospitalization, PreviousHospitalizationDto> {

//    private HospitalizationDtoHelper hospitalizationDtoHelper;
//
//    public  PreviousHospitalizationDtoHelper() {
//        hospitalizationDtoHelper = new HospitalizationDtoHelper();
//    }

    @Override
    public PreviousHospitalization create() {
        return new PreviousHospitalization();
    }

    @Override
    public PreviousHospitalizationDto createDto() {
        return new PreviousHospitalizationDto();
    }

    @Override
    public void fillInnerFromDto(PreviousHospitalization a, PreviousHospitalizationDto b) {

        // hospitalization is set by calling method

        if (b.getHealthFacility() != null) {
            a.setHealthFacility(DatabaseHelper.getFacilityDao().queryUuid(b.getHealthFacility().getUuid()));
        } else {
            a.setHealthFacility(null);
        }

        a.setIsolated(b.getIsolated());
        a.setAdmissionDate(b.getAdmissionDate());
        a.setDischargeDate(b.getDischargeDate());
        a.setDescription(b.getDescription());
    }

    @Override
    public void fillInnerFromAdo(PreviousHospitalizationDto a, PreviousHospitalization b) {

        if (b.getHealthFacility() != null) {
            Facility facility = DatabaseHelper.getFacilityDao().queryForId(b.getHealthFacility().getId());
            a.setHealthFacility(FacilityDtoHelper.toReferenceDto(facility));
        } else {
            a.setHealthFacility(null);
        }

        a.setIsolated(b.getIsolated());
        a.setAdmissionDate(b.getAdmissionDate());
        a.setDischargeDate(b.getDischargeDate());
        a.setDescription(b.getDescription());
    }

}
