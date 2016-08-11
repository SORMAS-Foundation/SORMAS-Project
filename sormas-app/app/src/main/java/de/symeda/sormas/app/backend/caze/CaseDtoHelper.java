package de.symeda.sormas.app.backend.caze;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.person.Person;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
public class CaseDtoHelper extends AdoDtoHelper<Case, CaseDataDto> {

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
        ado.setPerson(DatabaseHelper.getPersonDao().queryUuid(dto.getPerson().getUuid()));
        ado.setInvestigatedDate(dto.getInvestigatedDate());
        ado.setReportDate(dto.getReportDate());
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
    }
}
