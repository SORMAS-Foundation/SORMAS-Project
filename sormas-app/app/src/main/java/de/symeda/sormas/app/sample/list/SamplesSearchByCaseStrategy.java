package de.symeda.sormas.app.sample.list;

import java.util.List;

import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
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
        //TODO: Make changes here
        CaseDao caseDao = DatabaseHelper.getCaseDao();
        Case caze = caseDao.queryUuid(caseId);

        return DatabaseHelper.getSampleDao().queryByCase(caze);
    }
}
