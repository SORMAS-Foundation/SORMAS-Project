package de.symeda.sormas.app.backend.caze;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.symptoms.SymptomsDtoHelper;
import de.symeda.sormas.app.backend.user.User;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
public class CaseDtoHelper extends AdoDtoHelper<Case, CaseDataDto> {

    private SymptomsDtoHelper symptomsDtoHelper;

    public CaseDtoHelper() {
        symptomsDtoHelper = new SymptomsDtoHelper();
    }

    @Override
    public Case create() {
        return new Case();
    }

    @Override
    public CaseDataDto createDto() {
        return new CaseDataDto();
    }

    @Override
    public void fillInnerFromDto(Case ado, CaseDataDto dto) {

        ado.setCaseStatus(dto.getCaseStatus());
        ado.setDisease(dto.getDisease());
        if (dto.getHealthFacility() != null) {
            ado.setHealthFacility(DatabaseHelper.getFacilityDao().queryUuid(dto.getHealthFacility().getUuid()));
        } else {
            ado.setHealthFacility(null);
        }
        if (dto.getPerson() != null) {
            ado.setPerson(DatabaseHelper.getPersonDao().queryUuid(dto.getPerson().getUuid()));
        } else {
            ado.setPerson(null);
        }
        ado.setInvestigatedDate(dto.getInvestigatedDate());
        ado.setReportDate(dto.getReportDate());
        if (dto.getReportingUser() != null) {
            ado.setReportingUser(DatabaseHelper.getUserDao().queryUuid(dto.getReportingUser().getUuid()));
        } else {
            ado.setReportingUser(null);
        }

        if (dto.getSymptoms() != null) {
            Symptoms symptoms = DatabaseHelper.getSymptomsDao().queryUuid(dto.getSymptoms().getUuid());
            if(symptoms!=null) {
                symptoms = symptomsDtoHelper.create();
                symptomsDtoHelper.fillInnerFromDto(symptoms, dto.getSymptoms());
            }
            ado.setSymptoms(symptoms);
        } else {
            ado.setSymptoms(null);
        }

        if (dto.getSurveillanceOfficer() != null) {
            ado.setSurveillanceOfficer(DatabaseHelper.getUserDao().queryUuid(dto.getSurveillanceOfficer().getUuid()));
        } else {
            ado.setSurveillanceOfficer(null);
        }
        if (dto.getSurveillanceSupervisor() != null) {
            ado.setSurveillanceSupervisor(DatabaseHelper.getUserDao().queryUuid(dto.getSurveillanceSupervisor().getUuid()));
        } else {
            ado.setSurveillanceSupervisor(null);
        }
        // TODO user
    }

    @Override
    public void fillInnerFromAdo(CaseDataDto dto, Case ado) {

        dto.setCaseStatus(ado.getCaseStatus());
        dto.setDisease(ado.getDisease());
        if (ado.getHealthFacility() != null) {
            Facility facility = DatabaseHelper.getFacilityDao().queryForId(ado.getHealthFacility().getId());
            dto.setHealthFacility(AdoDtoHelper.toReferenceDto(facility));
        } else {
            dto.setHealthFacility(null);
        }

        if (ado.getPerson() != null) {
            Person person = DatabaseHelper.getPersonDao().queryForId(ado.getPerson().getId());
            dto.setPerson(AdoDtoHelper.toReferenceDto(person));
        }
        dto.setInvestigatedDate(dto.getInvestigatedDate());
        dto.setReportDate(dto.getReportDate());

        if (ado.getReportingUser() != null) {
            User user = DatabaseHelper.getUserDao().queryForId(ado.getReportingUser().getId());
            dto.setReportingUser(AdoDtoHelper.toReferenceDto(user));
        } else {
            dto.setReportingUser(null);
        }

        if (ado.getSymptoms() != null) {
            Symptoms symptoms = DatabaseHelper.getSymptomsDao().queryForId(ado.getSymptoms().getId());
            SymptomsDto symptomsDto = symptomsDtoHelper.createDto();
            symptomsDtoHelper.fillInnerFromAdo(symptomsDto, symptoms);
            dto.setSymptoms(symptomsDto);
        } else {
            ado.setSymptoms(null);
        }

        if (ado.getSurveillanceOfficer() != null) {
            User user = DatabaseHelper.getUserDao().queryForId(ado.getSurveillanceOfficer().getId());
            dto.setSurveillanceOfficer(AdoDtoHelper.toReferenceDto(user));
        } else {
            dto.setSurveillanceOfficer(null);
        }
        if (ado.getSurveillanceSupervisor() != null) {
            User user = DatabaseHelper.getUserDao().queryForId(ado.getSurveillanceSupervisor().getId());
            dto.setSurveillanceSupervisor(AdoDtoHelper.toReferenceDto(user));
        } else {
            dto.setSurveillanceSupervisor(null);
        }
        // TODO user
    }
}
