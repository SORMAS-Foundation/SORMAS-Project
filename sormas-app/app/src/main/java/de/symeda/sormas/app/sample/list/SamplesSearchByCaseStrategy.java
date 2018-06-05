package de.symeda.sormas.app.sample.list;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.Sample;

/**
 * Created by Orson on 10/12/2017.
 */

public class SamplesSearchByCaseStrategy implements ISamplesSearchStrategy {

    private String caseId;


    public SamplesSearchByCaseStrategy(String caseId) {
        this.caseId = caseId;
    }

    @Override
    public List<Sample> search() {
        List<Sample> list = new ArrayList<>();

        if (caseId == null || caseId.isEmpty())
            return list;

        final Case caze = DatabaseHelper.getCaseDao().queryUuidReference(caseId);
        if (caze != null) {
            list = DatabaseHelper.getSampleDao().queryByCase(caze);
        }

        return list;
    }
}
