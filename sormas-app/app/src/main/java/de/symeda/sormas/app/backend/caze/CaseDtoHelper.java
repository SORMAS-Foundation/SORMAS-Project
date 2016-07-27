package de.symeda.sormas.app.backend.caze;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
public class CaseDtoHelper extends AdoDtoHelper<Case, CaseDataDto> {

    @Override
    public Case create() {
        return new Case();
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
}
