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
    protected Call<Integer> pushAll(List<SampleDto> sampleDtos) {
        return RetroProvider.getSampleFacade().pushAll(sampleDtos);
    }

    @Override
    public void fillInnerFromDto(Sample target, SampleDto source) {

        target.setAssociatedCase(DatabaseHelper.getCaseDao().getByReferenceDto(source.getAssociatedCase()));

        target.setReportingUser(DatabaseHelper.getUserDao().getByReferenceDto(source.getReportingUser()));
        target.setReportDateTime(source.getReportDateTime());

        target.setLab(DatabaseHelper.getFacilityDao().getByReferenceDto(source.getLab()));
        target.setOtherLab(DatabaseHelper.getFacilityDao().getByReferenceDto(source.getOtherLab()));

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
        target.setReferredTo(DatabaseHelper.getSampleDao().getByReferenceDto(source.getReferredTo()));
        target.setShipped(source.isShipped());
        target.setReceived(source.isReceived());
    }

    @Override
    public void fillInnerFromAdo(SampleDto dto, Sample ado) {
        if(ado.getAssociatedCase() != null) {
            Case associatedCase = DatabaseHelper.getCaseDao().queryForId(ado.getAssociatedCase().getId());
            dto.setAssociatedCase(CaseDtoHelper.toReferenceDto(associatedCase));
        } else {
            dto.setAssociatedCase(null);
        }

        if(ado.getReportingUser() != null) {
            User user = DatabaseHelper.getUserDao().queryForId(ado.getReportingUser().getId());
            dto.setReportingUser(UserDtoHelper.toReferenceDto(user));
        } else {
            dto.setReportingUser(null);
        }

        if(ado.getLab() != null) {
            Facility lab = DatabaseHelper.getFacilityDao().queryForId(ado.getLab().getId());
            dto.setLab(FacilityDtoHelper.toReferenceDto(lab));
        } else {
            dto.setLab(null);
        }

        if(ado.getOtherLab() != null) {
            Facility otherLab = DatabaseHelper.getFacilityDao().queryForId(ado.getOtherLab().getId());
            dto.setOtherLab(FacilityDtoHelper.toReferenceDto(otherLab));
        } else {
            dto.setOtherLab(null);
        }

        if (ado.getReferredTo() != null) {
            Sample referredSample = DatabaseHelper.getSampleDao().queryForId(ado.getReferredTo().getId());
            dto.setReferredTo(SampleDtoHelper.toReferenceDto(referredSample));
        } else {
            dto.setReferredTo(null);
        }

        dto.setSampleCode(ado.getSampleCode());
        dto.setLabSampleID(ado.getLabSampleID());
        dto.setSampleDateTime(ado.getSampleDateTime());
        dto.setReportDateTime(ado.getReportDateTime());
        dto.setSampleMaterial(ado.getSampleMaterial());
        dto.setSampleMaterialText(ado.getSampleMaterialText());
        dto.setShipmentDate(ado.getShipmentDate());
        dto.setShipmentDetails(ado.getShipmentDetails());
        dto.setReceivedDate(ado.getReceivedDate());
        dto.setSpecimenCondition(ado.getSpecimenCondition());
        dto.setNoTestPossibleReason(ado.getNoTestPossibleReason());
        dto.setComment(ado.getComment());
        dto.setSampleSource(ado.getSampleSource());
        dto.setSuggestedTypeOfTest(ado.getSuggestedTypeOfTest());
        dto.setShipped(ado.isShipped());
        dto.setReceived(ado.isReceived());
    }

    public static SampleReferenceDto toReferenceDto(Sample ado) {
        if (ado == null) {
            return null;
        }
        SampleReferenceDto dto = new SampleReferenceDto();
        fillReferenceDto(dto, ado);

        return dto;
    }

}
