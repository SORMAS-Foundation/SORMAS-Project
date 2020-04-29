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

package de.symeda.sormas.app.backend.sample;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDtoHelper;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.facility.FacilityDtoHelper;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.user.UserDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class SampleDtoHelper extends AdoDtoHelper<Sample, SampleDto> {

    @Override
    protected Class<Sample> getAdoClass() {
        return Sample.class;
    }

    @Override
    protected Class<SampleDto> getDtoClass() {
        return SampleDto.class;
    }

    @Override
    protected Call<List<SampleDto>> pullAllSince(long since) throws NoConnectionException {
        return RetroProvider.getSampleFacade().pullAllSince(since);
    }

    @Override
    protected Call<List<SampleDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
        return RetroProvider.getSampleFacade().pullByUuids(uuids);
    }

    @Override
    protected Call<List<PushResult>> pushAll(List<SampleDto> sampleDtos) throws NoConnectionException {
        return RetroProvider.getSampleFacade().pushAll(sampleDtos);
    }

    @Override
    public void fillInnerFromDto(Sample target, SampleDto source) {

        target.setAssociatedCase(DatabaseHelper.getCaseDao().getByReferenceDto(source.getAssociatedCase()));

        target.setReportingUser(DatabaseHelper.getUserDao().getByReferenceDto(source.getReportingUser()));
        target.setReportDateTime(source.getReportDateTime());

        target.setLab(DatabaseHelper.getFacilityDao().getByReferenceDto(source.getLab()));
        target.setLabDetails(source.getLabDetails());

        target.setLabSampleID(source.getLabSampleID());
        target.setFieldSampleID(source.getFieldSampleID());
        target.setSampleDateTime(source.getSampleDateTime());
        target.setSampleMaterial(source.getSampleMaterial());
        target.setSampleMaterialText(source.getSampleMaterialText());
        target.setSamplePurpose(source.getSamplePurpose());
        target.setShipmentDate(source.getShipmentDate());
        target.setShipmentDetails(source.getShipmentDetails());
        target.setReceivedDate(source.getReceivedDate());
        target.setSpecimenCondition(source.getSpecimenCondition());
        target.setNoTestPossibleReason(source.getNoTestPossibleReason());
        target.setComment(source.getComment());
        target.setSampleSource(source.getSampleSource());
        if (source.getReferredTo() != null) {
            target.setReferredToUuid(source.getReferredTo().getUuid());
        } else {
            target.setReferredToUuid(null);
        }
        target.setShipped(source.isShipped());
        target.setReceived(source.isReceived());
        target.setPathogenTestResult(source.getPathogenTestResult());
        target.setPathogenTestingRequested(source.getPathogenTestingRequested());
        target.setAdditionalTestingRequested(source.getAdditionalTestingRequested());
        target.setRequestedPathogenTests(source.getRequestedPathogenTests());
        target.setRequestedAdditionalTests(source.getRequestedAdditionalTests());
        target.setRequestedOtherPathogenTests(source.getRequestedOtherPathogenTests());
        target.setRequestedOtherAdditionalTests(source.getRequestedOtherAdditionalTests());

        target.setReportLat(source.getReportLat());
        target.setReportLon(source.getReportLon());
        target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());
    }

    @Override
    public void fillInnerFromAdo(SampleDto target, Sample source) {
        if(source.getAssociatedCase() != null) {
            Case associatedCase = DatabaseHelper.getCaseDao().queryForId(source.getAssociatedCase().getId());
            target.setAssociatedCase(CaseDtoHelper.toReferenceDto(associatedCase));
        } else {
            target.setAssociatedCase(null);
        }

        if(source.getReportingUser() != null) {
            User user = DatabaseHelper.getUserDao().queryForId(source.getReportingUser().getId());
            target.setReportingUser(UserDtoHelper.toReferenceDto(user));
        } else {
            target.setReportingUser(null);
        }

        if(source.getLab() != null) {
            Facility lab = DatabaseHelper.getFacilityDao().queryForId(source.getLab().getId());
            target.setLab(FacilityDtoHelper.toReferenceDto(lab));
        } else {
            target.setLab(null);
        }

        if (source.getReferredToUuid() != null) {
            target.setReferredTo(new SampleReferenceDto(source.getReferredToUuid()));
        } else {
            target.setReferredTo(null);
        }

        target.setLabDetails(source.getLabDetails());
        target.setLabSampleID(source.getLabSampleID());
        target.setFieldSampleID(source.getFieldSampleID());
        target.setSampleDateTime(source.getSampleDateTime());
        target.setReportDateTime(source.getReportDateTime());
        target.setSampleMaterial(source.getSampleMaterial());
        target.setSampleMaterialText(source.getSampleMaterialText());
        target.setSamplePurpose(source.getSamplePurpose());
        target.setShipmentDate(source.getShipmentDate());
        target.setShipmentDetails(source.getShipmentDetails());
        target.setReceivedDate(source.getReceivedDate());
        target.setSpecimenCondition(source.getSpecimenCondition());
        target.setNoTestPossibleReason(source.getNoTestPossibleReason());
        target.setComment(source.getComment());
        target.setSampleSource(source.getSampleSource());
        target.setShipped(source.isShipped());
        target.setReceived(source.isReceived());
        target.setPathogenTestResult(source.getPathogenTestResult());
        target.setPathogenTestingRequested(source.getPathogenTestingRequested());
        target.setAdditionalTestingRequested(source.getAdditionalTestingRequested());
        target.setRequestedPathogenTests(source.getRequestedPathogenTests());
        target.setRequestedAdditionalTests(source.getRequestedAdditionalTests());
        target.setRequestedOtherPathogenTests(source.getRequestedOtherPathogenTests());
        target.setRequestedOtherAdditionalTests(source.getRequestedOtherAdditionalTests());

        target.setReportLat(source.getReportLat());
        target.setReportLon(source.getReportLon());
        target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());
    }

    public static SampleReferenceDto toReferenceDto(Sample ado) {
        if (ado == null) {
            return null;
        }

        return new SampleReferenceDto(ado.getUuid());
    }

}
