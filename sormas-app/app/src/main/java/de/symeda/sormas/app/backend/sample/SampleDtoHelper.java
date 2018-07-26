package de.symeda.sormas.app.backend.sample;

import java.util.List;

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
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

/**
 * Created by Mate Strysewske on 06.02.2017.
 */

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
    protected Call<List<SampleDto>> pullAllSince(long since) {
        return RetroProvider.getSampleFacade().pullAllSince(since);
    }

    @Override
    protected Call<List<SampleDto>> pullByUuids(List<String> uuids) {
        return RetroProvider.getSampleFacade().pullByUuids(uuids);
    }

    @Override
    protected Call<Integer> pushAll(List<SampleDto> sampleDtos) {
        return RetroProvider.getSampleFacade().pushAll(sampleDtos);
    }

    @Override
    public void fillInnerFromDto(Sample target, SampleDto source) {

        target.setAssociatedCase(DatabaseHelper.getCaseDao().getByReferenceDto(source.getAssociatedCase()));

        target.setReportingUser(DatabaseHelper.getUserDao().getByReferenceDto(source.getReportingUser()));
        target.setReportDateTime(source.getReportDateTime());

        target.setLab(DatabaseHelper.getFacilityDao().getByReferenceDto(source.getLab()));
        target.setLabDetails(source.getLabDetails());

        target.setSampleCode(source.getSampleCode());
        target.setLabSampleID(source.getLabSampleID());
        target.setSampleDateTime(source.getSampleDateTime());
        target.setSampleMaterial(source.getSampleMaterial());
        target.setSampleMaterialText(source.getSampleMaterialText());
        target.setShipmentDate(source.getShipmentDate());
        target.setShipmentDetails(source.getShipmentDetails());
        target.setReceivedDate(source.getReceivedDate());
        target.setSpecimenCondition(source.getSpecimenCondition());
        target.setNoTestPossibleReason(source.getNoTestPossibleReason());
        target.setComment(source.getComment());
        target.setSampleSource(source.getSampleSource());
        target.setSuggestedTypeOfTest(source.getSuggestedTypeOfTest());
        if (source.getReferredTo() != null) {
            target.setReferredToUuid(source.getReferredTo().getUuid());
        } else {
            target.setReferredToUuid(null);
        }
        target.setShipped(source.isShipped());
        target.setReceived(source.isReceived());

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
        target.setSampleCode(source.getSampleCode());
        target.setLabSampleID(source.getLabSampleID());
        target.setSampleDateTime(source.getSampleDateTime());
        target.setReportDateTime(source.getReportDateTime());
        target.setSampleMaterial(source.getSampleMaterial());
        target.setSampleMaterialText(source.getSampleMaterialText());
        target.setShipmentDate(source.getShipmentDate());
        target.setShipmentDetails(source.getShipmentDetails());
        target.setReceivedDate(source.getReceivedDate());
        target.setSpecimenCondition(source.getSpecimenCondition());
        target.setNoTestPossibleReason(source.getNoTestPossibleReason());
        target.setComment(source.getComment());
        target.setSampleSource(source.getSampleSource());
        target.setSuggestedTypeOfTest(source.getSuggestedTypeOfTest());
        target.setShipped(source.isShipped());
        target.setReceived(source.isReceived());

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
