/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.backend.hospitalization;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import retrofit2.Call;

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
    protected Call<List<HospitalizationDto>> pullAllSince(long since) throws NoConnectionException {
        throw new UnsupportedOperationException("Entity is embedded");
    }

    @Override
    protected Call<List<HospitalizationDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
        throw new UnsupportedOperationException("Entity is embedded");
    }

    @Override
    protected Call<List<PushResult>> pushAll(List<HospitalizationDto> hospitalizationDtos) throws NoConnectionException {
        throw new UnsupportedOperationException("Entity is embedded");
    }

    @Override
    public void fillInnerFromDto(Hospitalization a, HospitalizationDto b) {

        a.setAdmittedToHealthFacility(b.getAdmittedToHealthFacility());
        a.setAdmissionDate(b.getAdmissionDate());
        a.setDischargeDate(b.getDischargeDate());
        a.setIsolated(b.getIsolated());
        a.setIsolationDate(b.getIsolationDate());
        a.setLeftAgainstAdvice(b.getLeftAgainstAdvice());

        a.setHospitalizedPreviously(b.getHospitalizedPreviously());

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

        a.setAdmittedToHealthFacility(b.getAdmittedToHealthFacility());
        a.setAdmissionDate(b.getAdmissionDate());
        a.setDischargeDate(b.getDischargeDate());
        a.setIsolated(b.getIsolated());
        a.setIsolationDate(b.getIsolationDate());
        a.setLeftAgainstAdvice(b.getLeftAgainstAdvice());

        a.setHospitalizedPreviously(b.getHospitalizedPreviously());

        List<PreviousHospitalizationDto> previousHospitalizationDtos = new ArrayList<>();
        for (PreviousHospitalization prevHosp : b.getPreviousHospitalizations()) {
            PreviousHospitalizationDto prevHospDto = previousHospitalizationDtoHelper.adoToDto(prevHosp);
            previousHospitalizationDtos.add(prevHospDto);
        }
        a.setPreviousHospitalizations(previousHospitalizationDtos);
    }
}
