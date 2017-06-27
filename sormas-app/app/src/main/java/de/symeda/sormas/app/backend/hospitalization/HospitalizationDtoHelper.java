package de.symeda.sormas.app.backend.hospitalization;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import retrofit2.Call;

/**
 * Created by Mate Strysewske on 22.02.2017.
 */

public class HospitalizationDtoHelper extends AdoDtoHelper<Hospitalization, HospitalizationDto> {

    private PreviousHospitalizationDtoHelper previousHospitalizationDtoHelper;

    public  HospitalizationDtoHelper() {
        previousHospitalizationDtoHelper = new PreviousHospitalizationDtoHelper();
    }

    @Override
    protected Class<Hospitalization> getAdoClass() {
        return Hospitalization.class;
    }

    @Override
    protected Class<HospitalizationDto> getDtoClass() {
        return HospitalizationDto.class;
    }

    @Override
    protected Call<List<HospitalizationDto>> pullAllSince(long since) {
        throw new UnsupportedOperationException("Entity is embedded");
    }

    @Override
    protected Call<Integer> pushAll(List<HospitalizationDto> hospitalizationDtos) {
        throw new UnsupportedOperationException("Entity is embedded");
    }

    @Override
    public void fillInnerFromDto(Hospitalization a, HospitalizationDto b) {

        a.setAdmissionDate(b.getAdmissionDate());
        a.setDischargeDate(b.getDischargeDate());
        a.setHospitalizedPreviously(b.getHospitalizedPreviously());
        a.setIsolated(b.getIsolated());
        a.setIsolationDate(b.getIsolationDate());

        // It would be better to merge with the existing hospitalizations
        List<PreviousHospitalization> previousHospitalizations = new ArrayList<>();
        if (!b.getPreviousHospitalizations().isEmpty()) {
            for (PreviousHospitalizationDto prevHospDto : b.getPreviousHospitalizations()) {
                PreviousHospitalization prevHosp = previousHospitalizationDtoHelper.fillOrCreateFromDto(null, prevHospDto);
                prevHosp.setHospitalization(a);
                previousHospitalizations.add(prevHosp);
            }
        }
        a.setPreviousHospitalizations(previousHospitalizations);
    }

    @Override
    public void fillInnerFromAdo(HospitalizationDto a, Hospitalization b) {

        a.setAdmissionDate(b.getAdmissionDate());
        a.setDischargeDate(b.getDischargeDate());
        a.setHospitalizedPreviously(b.getHospitalizedPreviously());
        a.setIsolated(b.getIsolated());
        a.setIsolationDate(b.getIsolationDate());

        List<PreviousHospitalizationDto> previousHospitalizationDtos = new ArrayList<>();
        for (PreviousHospitalization prevHosp : b.getPreviousHospitalizations()) {
            PreviousHospitalizationDto prevHospDto = previousHospitalizationDtoHelper.adoToDto(prevHosp);
            previousHospitalizationDtos.add(prevHospDto);
        }
        a.setPreviousHospitalizations(previousHospitalizationDtos);
    }
}
