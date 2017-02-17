package de.symeda.sormas.app.backend.sample;

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

/**
 * Created by Mate Strysewske on 06.02.2017.
 */

public class SampleDtoHelper extends AdoDtoHelper<Sample, SampleDto> {

    public SampleDtoHelper() {

    }

    @Override
    public Sample create() {
        return new Sample();
    }

    @Override
    public SampleDto createDto() {
        return new SampleDto();
    }

    @Override
    public void fillInnerFromDto(Sample ado, SampleDto dto) {
        if(dto.getAssociatedCase() != null) {
            ado.setAssociatedCase(DatabaseHelper.getCaseDao().queryUuid(dto.getAssociatedCase().getUuid()));
        } else {
            ado.setAssociatedCase(null);
        }

        if(dto.getReportingUser() != null) {
            ado.setReportingUser(DatabaseHelper.getUserDao().queryUuid(dto.getReportingUser().getUuid()));
        } else {
            ado.setReportingUser(null);
        }

        if(dto.getLab() != null) {
            ado.setLab(DatabaseHelper.getFacilityDao().queryUuid(dto.getLab().getUuid()));
        } else {
            ado.setLab(null);
        }

        if(dto.getOtherLab() != null) {
            ado.setOtherLab(DatabaseHelper.getFacilityDao().queryUuid(dto.getOtherLab().getUuid()));
        } else {
            ado.setOtherLab(null);
        }

        ado.setSampleCode(dto.getSampleCode());
        ado.setSampleDateTime(dto.getSampleDateTime());
        ado.setReportDateTime(dto.getReportDateTime());
        ado.setSampleMaterial(dto.getSampleMaterial());
        ado.setSampleMaterialText(dto.getSampleMaterialText());
        ado.setShipmentStatus(dto.getShipmentStatus());
        ado.setShipmentDate(dto.getShipmentDate());
        ado.setShipmentDetails(dto.getShipmentDetails());
        ado.setReceivedDate(dto.getReceivedDate());
        ado.setSpecimenCondition(dto.getSpecimenCondition());
        ado.setNoTestPossibleReason(dto.getNoTestPossibleReason());
        ado.setComment(dto.getComment());
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

        dto.setSampleCode(ado.getSampleCode());
        dto.setSampleDateTime(ado.getSampleDateTime());
        dto.setReportDateTime(ado.getReportDateTime());
        dto.setSampleMaterial(ado.getSampleMaterial());
        dto.setSampleMaterialText(ado.getSampleMaterialText());
        dto.setShipmentStatus(ado.getShipmentStatus());
        dto.setShipmentDate(ado.getShipmentDate());
        dto.setShipmentDetails(ado.getShipmentDetails());
        dto.setReceivedDate(ado.getReceivedDate());
        dto.setSpecimenCondition(ado.getSpecimenCondition());
        dto.setNoTestPossibleReason(ado.getNoTestPossibleReason());
        dto.setComment(ado.getComment());
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
