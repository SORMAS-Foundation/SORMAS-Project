package de.symeda.sormas.app.backend.caze;

import de.symeda.sormas.api.caze.HospitalizationDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.facility.FacilityDtoHelper;

/**
 * Created by Mate Strysewske on 22.02.2017.
 */

public class HospitalizationDtoHelper extends AdoDtoHelper<Hospitalization, HospitalizationDto> {

    @Override
    public Hospitalization create() {
        return new Hospitalization();
    }

    @Override
    public HospitalizationDto createDto() {
        return new HospitalizationDto();
    }

    @Override
    public void fillInnerFromDto(Hospitalization a, HospitalizationDto b) {

        a.setAdmissionDate(b.getAdmissionDate());
        a.setDischargeDate(b.getDischargeDate());
        a.setHospitalizedPreviously(b.getHospitalizedPreviously());
        a.setIsolated(b.getIsolated());
        a.setIsolationDate(b.getIsolationDate());
    }

    @Override
    public void fillInnerFromAdo(HospitalizationDto a, Hospitalization b) {

        a.setAdmissionDate(b.getAdmissionDate());
        a.setDischargeDate(b.getDischargeDate());
        a.setHospitalizedPreviously(b.getHospitalizedPreviously());
        a.setIsolated(b.getIsolated());
        a.setIsolationDate(b.getIsolationDate());
    }
}
