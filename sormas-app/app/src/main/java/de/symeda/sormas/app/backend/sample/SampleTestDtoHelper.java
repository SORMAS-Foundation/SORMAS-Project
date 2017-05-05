package de.symeda.sormas.app.backend.sample;

import de.symeda.sormas.api.sample.SampleTestDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;

/**
 * Created by Mate Strysewske on 09.02.2017.
 */

public class SampleTestDtoHelper extends AdoDtoHelper<SampleTest, SampleTestDto> {

    public SampleTestDtoHelper() {

    }

    @Override
    protected SampleTest create() {
        return new SampleTest();
    }

    @Override
    protected SampleTestDto createDto() {
        return new SampleTestDto();
    }

    @Override
    protected void fillInnerFromDto(SampleTest ado, SampleTestDto dto) {
        try {
            if (dto.getSample() != null) {
                ado.setSample(DatabaseHelper.getSampleDao().queryUuid(dto.getSample().getUuid()));
            } else {
                ado.setSample(null);
            }

            ado.setTestDateTime(dto.getTestDateTime());
            ado.setTestResult(dto.getTestResult());
            ado.setTestType(dto.getTestType());
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void fillInnerFromAdo(SampleTestDto dto, SampleTest ado) {
        if(ado.getSample() != null) {
            Sample sample = DatabaseHelper.getSampleDao().queryForId(ado.getSample().getId());
            dto.setSample(SampleDtoHelper.toReferenceDto(sample));
        } else {
            dto.setSample(null);
        }

        dto.setTestDateTime(ado.getTestDateTime());
        dto.setTestResult(ado.getTestResult());
        dto.setTestType(ado.getTestType());
    }
}
